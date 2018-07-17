package ru.ifmo.orthant.epsilonIBEA;

import java.util.Arrays;
import java.util.HashMap;

import ru.ifmo.orthant.OrthantSearch;
import ru.ifmo.orthant.ValueTypeClass;
import ru.ifmo.orthant.util.PointWrapper;

public final class OrthantImplementation extends EpsilonIBEAFitnessAssignment {
    private final OrthantSearch orthantSearch;
    private final double[][] internalPoints;
    private final double[] dataValues;
    private final double[] compressedDataValues;
    private final double[] queryValues;
    private final double[] additionalCollection;
    private final boolean[] allTrue;
    private final boolean[] isObjectiveStrict;
    private final double kappa;
    private final int[] reindex;

    private final PointWrapper[] wrappers;
    private final HashMap<PointWrapper, PointWrapper> existingWrappers;

    public OrthantImplementation(OrthantSearch orthantSearch, double kappa) {
        this.orthantSearch = orthantSearch;
        this.kappa = kappa;
        int maxPoints = orthantSearch.getMaximumPoints();
        int maxDimension = orthantSearch.getMaximumDimension();
        internalPoints = new double[maxPoints][maxDimension];
        dataValues = new double[maxPoints];
        compressedDataValues = new double[maxPoints];
        queryValues = new double[maxPoints];
        reindex = new int[maxPoints];
        isObjectiveStrict = new boolean[maxDimension];
        additionalCollection = TYPE_CLASS_INSTANCE.createCollection(orthantSearch.getAdditionalCollectionSize(maxPoints));

        allTrue = new boolean[maxPoints];
        Arrays.fill(allTrue, true);

        wrappers = new PointWrapper[maxPoints];
        for (int i = 0; i < maxPoints; ++i) {
            wrappers[i] = new PointWrapper();
        }
        existingWrappers = new HashMap<>(maxPoints);
    }

    @Override
    public int getMaximumPoints() {
        return orthantSearch.getMaximumPoints();
    }

    @Override
    public int getMaximumDimension() {
        return orthantSearch.getMaximumDimension() + 1;
    }

    @Override
    public void assignFitness(double[][] points, double[] fitness) {
        if (points.length == 0) {
            return;
        }
        int n = points.length;
        int dimension = points[0].length;
        Arrays.fill(isObjectiveStrict, false);
        Arrays.fill(fitness, 0);

        // The first run is different since projected-equal points must be compared with each other.
        // We hash the equal points and aggregate their data values.
        // At the end, we correct their fitness values whenever data values and "compressed" data values differ.
        {
            int newN = 0;
            for (int i = 0; i < n; ++i) {
                double[] sourcePoint = points[i];
                encode(sourcePoint, internalPoints[i], 0);
                dataValues[i] = Math.exp(-sourcePoint[0] / kappa);
                PointWrapper currWrapper = wrappers[i];
                currWrapper.point = internalPoints[i];
                currWrapper.index = i;
                currWrapper.dimension = dimension - 1;
                PointWrapper oldWrapper = existingWrappers.putIfAbsent(currWrapper, currWrapper);
                if (oldWrapper == null) {
                    double[] tmp = internalPoints[newN];
                    internalPoints[newN] = internalPoints[i];
                    internalPoints[i] = tmp;
                    compressedDataValues[newN] = dataValues[i];
                    reindex[i] = newN++;
                } else {
                    int oldIndex = oldWrapper.index;
                    compressedDataValues[oldIndex] += dataValues[i];
                    reindex[i] = oldIndex;
                }
            }
            existingWrappers.clear();

            orthantSearch.runSearch(internalPoints, compressedDataValues, queryValues, 0, newN, dimension - 1,
                    allTrue, allTrue, additionalCollection, TYPE_CLASS_INSTANCE, isObjectiveStrict);
            for (int i = 0; i < n; ++i) {
                double myExp = Math.exp(points[i][0] / kappa);
                // the part of the query corresponding to projected-unequal points.
                fitness[i] -= queryValues[reindex[i]] * myExp;
                // the part of the query corresponding to projected-equal points.
                fitness[i] -= (compressedDataValues[reindex[i]] - dataValues[i]) * myExp;
            }
        }

        for (int d = 1; d < dimension; ++d) {
            isObjectiveStrict[d - 1] = true;
            for (int i = 0; i < n; ++i) {
                double[] sourcePoint = points[i];
                encode(sourcePoint, internalPoints[i], d);
                dataValues[i] = Math.exp(-sourcePoint[d] / kappa);
            }
            orthantSearch.runSearch(internalPoints, dataValues, queryValues, 0, n, dimension - 1,
                    allTrue, allTrue, additionalCollection, TYPE_CLASS_INSTANCE, isObjectiveStrict);
            for (int i = 0; i < n; ++i) {
                fitness[i] -= queryValues[i] * Math.exp(points[i][d] / kappa);
            }
        }
    }

    private static void encode(double[] source, double[] target, int objective) {
        int d = source.length;
        double xk = source[objective];
        for (int j = 0; j < objective; ++j) {
            target[j] = source[j] - xk;
        }
        for (int j = objective + 1; j < d; ++j) {
            target[j - 1] = source[j] - xk;
        }
    }

    private static final class IBEATypeClass extends ValueTypeClass<double[]> {
        @Override
        public double[] createCollection(int size) {
            return new double[size];
        }

        @Override
        public int size(double[] collection) {
            return collection.length;
        }

        @Override
        public void fillWithZeroes(double[] collection, int from, int until) {
            Arrays.fill(collection, from, until, 0);
        }

        @Override
        public void add(double[] source, int sourceIndex, double[] target, int targetIndex) {
            target[targetIndex] += source[sourceIndex];
        }

        @Override
        public void queryToData(double[] source, int sourceIndex, double[] target) {
            // do nothing
        }
    }

    private static final IBEATypeClass TYPE_CLASS_INSTANCE = new IBEATypeClass();
}
