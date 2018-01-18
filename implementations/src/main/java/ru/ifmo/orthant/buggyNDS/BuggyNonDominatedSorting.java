package ru.ifmo.orthant.buggyNDS;

public abstract class BuggyNonDominatedSorting {
    public abstract int getMaximumPoints();
    public abstract int getMaximumDimension();
    public abstract void sort(double[][] points, int[] ranks);
}
