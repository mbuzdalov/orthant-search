package ru.ifmo.orthant.nds;

public abstract class NonDominatedSorting {
    public abstract int getMaximumPoints();
    public abstract int getMaximumDimension();
    public abstract void sort(double[][] points, int[] ranks);
}
