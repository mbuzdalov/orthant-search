package ru.ifmo.orthant.epsilonIBEA;

public abstract class EpsilonIBEAFitnessAssignment {
    public abstract int getMaximumPoints();
    public abstract int getMaximumDimension();
    public abstract void assignFitness(double[][] points, double[] fitness);
}
