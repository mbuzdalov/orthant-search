package ru.ifmo.orthant.r2;

import java.util.Arrays;

import ru.ifmo.orthant.OrthantSearch;
import ru.ifmo.orthant.ValueTypeClass;

public final class OrthantImplementation extends R2Indicator {
    private final OrthantSearch orthantSearch;
    private final double[][] internalPoints;
    private final double[] dataValues;
    private final double[] queryValues;
    private final boolean[] allFalse;
    private final boolean[] isDataPoint;
    private final boolean[] isQueryPoint;
    private final double[] resultingMaxima;
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
        this.resultingMaxima = new double[maxPoints];
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
    public double evaluate(double[][] referenceVectors, double[] referencePoint, double[][] population, double power) {
        int nReference = referenceVectors.length;
        int nPopulation = population.length;
        if (nPopulation == 0 || nReference == 0) {
            return 0;
        }
        int dimension = referencePoint.length;
        int total = nReference + nPopulation;
        for (int i = 0; i < total; ++i) {
            isDataPoint[i] = i < nPopulation;
            isQueryPoint[i] = i >= nPopulation;
        }
        Arrays.fill(resultingMaxima, 0, total, 0.0);
        for (int d = 0; d < dimension; ++d) {
            for (int i = 0; i < nPopulation; ++i) {
                encodeIndividual(population[i], internalPoints[i], referencePoint,d);
                dataValues[i] = referencePoint[d] - population[i][d];
                internalPoints[i][dimension - 1] = 0;
            }
            for (int i = 0; i < nReference; ++i) {
                encodeReference(referenceVectors[i], internalPoints[i + nPopulation], d);
                internalPoints[i + nPopulation][dimension - 1] = 1;
            }
            orthantSearch.runSearch(internalPoints, dataValues, queryValues, 0, total, dimension,
                    isDataPoint, isQueryPoint, additionalCollection, TYPE_CLASS_INSTANCE, allFalse);
            for (int i = 0; i < nReference; ++i) {
                double value = queryValues[i + nPopulation] / referenceVectors[i][d];
                if (resultingMaxima[i] < value) {
                    resultingMaxima[i] = value;
                }
            }
        }

        double rv = 0;
        for (int i = 0; i < nReference; ++i) {
            rv += Math.pow(resultingMaxima[i], power);
        }
        return rv / referenceVectors.length;
    }

    private static void encodeReference(double[] source, double[] target, int objective) {
        int d = source.length;
        double xk = -source[objective];
        for (int j = 0; j < objective; ++j) {
            target[j] = source[j] / xk;
        }
        for (int j = objective + 1; j < d; ++j) {
            target[j - 1] = source[j] / xk;
        }
    }

    private static void encodeIndividual(double[] source, double[] target, double[] reference, int objective) {
        int d = source.length;
        // the reverse order is intentional here; see encodeReference for comparison and theoretical analysis for why.
        double xk = reference[objective] - source[objective];
        for (int j = 0; j < objective; ++j) {
            target[j] = (source[j] - reference[j]) / xk;
        }
        for (int j = objective + 1; j < d; ++j) {
            target[j - 1] = (source[j] - reference[j]) / xk;
        }
    }

    private static class R2TypeClass extends ValueTypeClass<double[]> {
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
            double src = source[sourceIndex];
            if (target[targetIndex] < src) {
                target[targetIndex] = src;
            }
        }

        @Override
        public void queryToData(double[] source, int sourceIndex, double[] target) {
            // nothing here
        }
    }

    private static final R2TypeClass TYPE_CLASS_INSTANCE = new R2TypeClass();
}
