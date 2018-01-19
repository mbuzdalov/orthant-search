package ru.ifmo.orthant.domCount;

public abstract class DominanceCount {
    public abstract int getMaximumPoints();
    public abstract int getMaximumDimension();
    public abstract void evaluate(double[][] points, int[] dominanceCounts);
}
