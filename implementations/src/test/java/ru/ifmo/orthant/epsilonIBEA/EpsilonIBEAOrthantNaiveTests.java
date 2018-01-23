package ru.ifmo.orthant.epsilonIBEA;

import ru.ifmo.orthant.NaiveOrthantSearch;

public class EpsilonIBEAOrthantNaiveTests extends CorrectnessTestsBase {
    @Override
    protected EpsilonIBEAFitnessAssignment getAlgorithm(int maxPoints, int maxDimension, double kappa) {
        return new OrthantImplementation(new NaiveOrthantSearch(maxPoints, maxDimension - 1), kappa);
    }
}
