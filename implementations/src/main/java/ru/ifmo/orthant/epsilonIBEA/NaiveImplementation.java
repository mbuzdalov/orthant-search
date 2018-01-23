package ru.ifmo.orthant.epsilonIBEA;

import java.util.Arrays;

public final class NaiveImplementation extends EpsilonIBEAFitnessAssignment {
    private final int maxPoints;
    private final int maxDimension;
    private final double kappa;

    public NaiveImplementation(int maxPoints, int maxDimension, double kappa) {
        this.maxPoints = maxPoints;
        this.maxDimension = maxDimension;
        this.kappa = kappa;
    }

    @Override
    public int getMaximumPoints() {
        return maxPoints;
    }

    @Override
    public int getMaximumDimension() {
        return maxDimension;
    }

    @Override
    public void assignFitness(double[][] points, double[] fitness) {
        if (points.length == 0) {
            return;
        }
        int d = points[0].length;
        Arrays.fill(fitness, 0);
        for (int i = 0; i < points.length; ++i) {
            double[] pi = points[i];
            for (int j = 0; j < points.length; ++j) {
                if (i == j) {
                    continue;
                }
                double[] pj = points[j];
                double indicator = pj[0] - pi[0];
                for (int k = 1; k < d; ++k) {
                    double valueAtK = pj[k] - pi[k];
                    if (indicator < valueAtK) {
                        indicator = valueAtK;
                    }
                }
                fitness[i] -= Math.exp(-indicator / kappa);
            }
        }
    }
}
