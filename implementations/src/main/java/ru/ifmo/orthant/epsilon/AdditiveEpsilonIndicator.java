package ru.ifmo.orthant.epsilon;

public abstract class AdditiveEpsilonIndicator {
    public abstract int getMaximumSetSize();
    public abstract int getMaximumDimension();
    public abstract double evaluate(double[][] moving, double[][] fixed);
}
