package ru.ifmo.orthant.epsilonIBEA;

import org.junit.Assert;
import org.junit.Test;

public abstract class CorrectnessTestsBase {
    protected abstract EpsilonIBEAFitnessAssignment getAlgorithm(int maxPoints, int maxDimension, double kappa);

    @Test
    public void testOnePoint() {
        EpsilonIBEAFitnessAssignment algorithm = getAlgorithm(1, 4, 1.31);
        double[][] points = {{ 1, 2, 3, 4 }};
        double[] expectedFitness = { 0 };
        double[] foundFitness = new double[points.length];
        algorithm.assignFitness(points, foundFitness);
        Assert.assertArrayEquals(expectedFitness, foundFitness, 1e-9);
    }

    @Test
    public void testTwoEqualPoints() {
        EpsilonIBEAFitnessAssignment algorithm = getAlgorithm(2, 4, 1.31);
        double[][] points = {{ 1, 2, 3, 4 }, { 1, 2, 3, 4 }};
        double[] expectedFitness = { -1, -1 };
        double[] foundFitness = new double[points.length];
        algorithm.assignFitness(points, foundFitness);
        Assert.assertArrayEquals(expectedFitness, foundFitness, 1e-9);
    }

    @Test
    public void testTwoIncomparablePoints() {
        double kappa = 1.31;
        double expectedFitnessValue = -Math.exp(-1 / kappa);
        EpsilonIBEAFitnessAssignment algorithm = getAlgorithm(2, 2, kappa);
        double[][] points = {{ 1, 2 }, { 2, 1 }};
        double[] expectedFitness = { expectedFitnessValue, expectedFitnessValue };
        double[] foundFitness = new double[points.length];
        algorithm.assignFitness(points, foundFitness);
        Assert.assertArrayEquals(expectedFitness, foundFitness, 1e-9);
    }

    @Test
    public void testTwoComparablePoints() {
        double kappa = 1.31;
        EpsilonIBEAFitnessAssignment algorithm = getAlgorithm(2, 2, kappa);
        double[][] points = {{ 1, 1 }, { 2, 2 }};
        double[] expectedFitness = { -Math.exp(-1 / kappa), -Math.exp(1 / kappa) };
        double[] foundFitness = new double[points.length];
        algorithm.assignFitness(points, foundFitness);
        Assert.assertArrayEquals(expectedFitness, foundFitness, 1e-9);
    }

    @Test
    public void generated0() {
        double[][] points = {
                {4.0, 2.0},
                {3.0, 2.0},
                {1.0, 0.0},
                {2.0, 2.0},
        };
        double[] expectedFitness = {
                -7.053540591735669, -6.498378838883184, -0.48378719884939525, -2.8907274519892985,
        };
        double kappa = 1.2345;
        double[] foundFitness = new double[expectedFitness.length];
        EpsilonIBEAFitnessAssignment algorithm = getAlgorithm(points.length, points[0].length, kappa);
        algorithm.assignFitness(points, foundFitness);
        Assert.assertArrayEquals(expectedFitness, foundFitness, 1e-9);
    }
}
