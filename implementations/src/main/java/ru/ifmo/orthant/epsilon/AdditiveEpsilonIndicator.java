package ru.ifmo.orthant.epsilon;

public abstract class AdditiveEpsilonIndicator {
    public abstract int getMaximumPoints();
    public abstract int getMaximumDimension();
    public abstract double evaluate(double[][] moving, double[][] fixed);
}
