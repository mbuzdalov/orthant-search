package ru.ifmo.orthant.epsilon;

import java.util.Arrays;

import ru.ifmo.orthant.OrthantSearch;
import ru.ifmo.orthant.ValueTypeClass;

public final class OrthantImplementation extends AdditiveEpsilonIndicator {
    private final OrthantSearch orthantSearch;
    private final double[][] internalPoints;
    private final double[] dataValues;
    private final double[] queryValues;
    private final boolean[] allFalse;
    private final boolean[] isDataPoint;
    private final boolean[] isQueryPoint;
    private final double[] resultingEpsilons;
    private final double[] additionalCollection;

    public OrthantImplementation(OrthantSearch orthantSearch) {
        this.orthantSearch = orthantSearch;
        int maxPoints = orthantSearch.getMaximumPoints();
        int maxDimension = orthantSearch.getMaximumDimension();
        this.internalPoints = new double[maxPoints][maxDimension];
        this.dataValues = new double[maxPoints];
        this.queryValues = new double[maxPoints];
        this.isDataPoint = new boolean[maxPoints];
        this.isQueryPoint = new boolean[maxPoints];
        this.allFalse = new boolean[maxDimension];
        this.resultingEpsilons = new double[maxPoints];
        this.additionalCollection = TYPE_CLASS_INSTANCE.createCollection(orthantSearch.getAdditionalCollectionSize(maxPoints));
    }

    @Override
    public int getMaximumSetSize() {
        return orthantSearch.getMaximumPoints() / 2;
    }

    @Override
    public int getMaximumDimension() {
        return orthantSearch.getMaximumDimension();
    }

    @Override
    public double evaluate(double[][] moving, double[][] fixed) {
        int nMoving = moving.length;
        int nFixed = fixed.length;
        if (nMoving == 0 || nFixed == 0) {
            return Double.NEGATIVE_INFINITY;
        }
        int dimension = moving[0].length;
        for (int i = 0; i < nMoving + nFixed; ++i) {
            isDataPoint[i] = i < nMoving;
            isQueryPoint[i] = i >= nMoving;
        }
        Arrays.fill(resultingEpsilons, 0, nFixed, Double.POSITIVE_INFINITY);
        for (int d = 0; d < dimension; ++d) {
            for (int i = 0; i < nMoving; ++i) {
                encode(moving[i], internalPoints[i], d);
                dataValues[i] = moving[i][d];
                internalPoints[i][dimension - 1] = 0;
            }
            for (int i = 0; i < nFixed; ++i) {
                encode(fixed[i], internalPoints[i + nMoving], d);
                internalPoints[i + nMoving][dimension - 1] = 1;
            }
            orthantSearch.runSearch(internalPoints, dataValues, queryValues, 0, nMoving + nFixed, dimension,
                    isDataPoint, isQueryPoint, additionalCollection, TYPE_CLASS_INSTANCE, allFalse);
            for (int i = 0; i < nFixed; ++i) {
                double currentValue = queryValues[i + nMoving] - fixed[i][d];
                double existingValue = resultingEpsilons[i];
                if (currentValue < existingValue) {
                    resultingEpsilons[i] = currentValue;
                }
            }
        }

        double epsilon = resultingEpsilons[0];
        for (int i = 1; i < nFixed; ++i) {
            epsilon = Math.max(epsilon, resultingEpsilons[i]);
        }
        return epsilon;
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

    private static class EpsilonTypeClass extends ValueTypeClass<double[]> {
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
            Arrays.fill(collection, from, until, Double.POSITIVE_INFINITY);
        }

        @Override
        public void add(double[] source, int sourceIndex, double[] target, int targetIndex) {
            double src = source[sourceIndex];
            if (target[targetIndex] > src) {
                target[targetIndex] = src;
            }
        }

        @Override
        public void queryToData(double[] source, int sourceIndex, double[] target) {
            // nothing here
        }
    }

    private static final EpsilonTypeClass TYPE_CLASS_INSTANCE = new EpsilonTypeClass();
}
