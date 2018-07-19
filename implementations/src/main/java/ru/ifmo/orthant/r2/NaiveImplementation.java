package ru.ifmo.orthant.r2;

public final class NaiveImplementation extends R2Indicator {
    private final int maximumSetSize;
    private final int maximumDimension;

    public NaiveImplementation(int maximumSetSize, int maximumDimension) {
        this.maximumSetSize = maximumSetSize;
        this.maximumDimension = maximumDimension;
    }

    @Override
    public int getMaximumSetSize() {
        return maximumSetSize;
    }

    @Override
    public int getMaximumDimension() {
        return maximumDimension;
    }

    @Override
    public double evaluate(double[][] referenceVectors, double[] referencePoint,
                           double[][] population, double power) {
        int m = referencePoint.length;
        double result = 0;
        for (double[] vector : referenceVectors) {
            double maxTsch = 0;
            for (double[] ind : population) {
                double tsch = Double.POSITIVE_INFINITY;
                for (int i = 0; i < m; ++i) {
                    double inner = (referencePoint[i] - ind[i]) / vector[i];
                    if (tsch > inner) {
                        tsch = inner;
                    }
                }
                if (maxTsch < tsch) {
                    maxTsch = tsch;
                }
            }
            result += Math.pow(maxTsch, power);
        }
        return result / referenceVectors.length;
    }
}
