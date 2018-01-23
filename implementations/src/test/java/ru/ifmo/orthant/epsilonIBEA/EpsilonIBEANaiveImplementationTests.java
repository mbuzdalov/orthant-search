package ru.ifmo.orthant.epsilonIBEA;

public class EpsilonIBEANaiveImplementationTests extends CorrectnessTestsBase {
    @Override
    protected EpsilonIBEAFitnessAssignment getAlgorithm(int maxPoints, int maxDimension, double kappa) {
        return new NaiveImplementation(maxPoints, maxDimension, kappa);
    }
}
