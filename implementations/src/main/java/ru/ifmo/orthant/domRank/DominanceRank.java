package ru.ifmo.orthant.domRank;

public abstract class DominanceRank {
    public abstract int getMaximumPoints();
    public abstract int getMaximumDimension();
    public abstract void evaluate(double[][] points, int[] dominanceRanks);
}
