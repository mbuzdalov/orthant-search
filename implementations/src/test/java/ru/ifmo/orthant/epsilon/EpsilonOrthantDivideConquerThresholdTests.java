package ru.ifmo.orthant.epsilon;

import java.util.function.BiFunction;

import ru.ifmo.orthant.DivideConquerOrthantSearch;

public class EpsilonOrthantDivideConquerThresholdTests extends CorrectnessTestsBase {
    @Override
    protected BiFunction<Integer, Integer, AdditiveEpsilonIndicator> getFactory() {
        return (n, d) -> new OrthantImplementation(new DivideConquerOrthantSearch(n * 2, d, true));
    }
}
