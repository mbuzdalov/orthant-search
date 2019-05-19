package ru.ifmo.orthant.epsilonIBEA;

import ru.ifmo.orthant.DivideConquerOrthantSearch;

public class EpsilonIBEAOrthantDivideConquerThresholdTests extends CorrectnessTestsBase {
    @Override
    protected EpsilonIBEAFitnessAssignment getAlgorithm(int maxPoints, int maxDimension, double kappa) {
        return new OrthantImplementation(new DivideConquerOrthantSearch(maxPoints, maxDimension - 1, true, 1), kappa);
    }
}
