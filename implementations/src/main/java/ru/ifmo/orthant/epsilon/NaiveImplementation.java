package ru.ifmo.orthant.epsilon;

public final class NaiveImplementation extends AdditiveEpsilonIndicator {
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
    public double evaluate(double[][] moving, double[][] fixed) {
        double result = Double.NEGATIVE_INFINITY;
        for (double[] fixedPoint : fixed) {
            int dimension = fixedPoint.length;
            double minimumForPoint = Double.POSITIVE_INFINITY;
            for (double[] movingPoint : moving) {
                double maximumForPair = Double.NEGATIVE_INFINITY;
                for (int d = 0; d < dimension; ++d) {
                    double diff = movingPoint[d] - fixedPoint[d];
                    if (maximumForPair < diff) {
                        maximumForPair = diff;
                    }
                }
                if (minimumForPoint > maximumForPair) {
                    minimumForPoint = maximumForPair;
                }
            }
            if (result < minimumForPoint) {
                result = minimumForPoint;
            }
        }
        return result;
    }
}
