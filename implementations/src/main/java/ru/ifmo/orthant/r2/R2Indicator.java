package ru.ifmo.orthant.r2;

public abstract class R2Indicator {
    public abstract int getMaximumSetSize();
    public abstract int getMaximumDimension();
    public abstract double evaluate(double[][] referenceVectors, double[] referencePoint,
                                    double[][] population, double power);
}
