package ru.ifmo.orthant;

import java.util.function.BiFunction;

public class OrthantSearchDivideConquerThresholdTestsParallel extends CorrectnessTestsBase {
    @Override
    protected BiFunction<Integer, Integer, OrthantSearch> getFactory() {
        return (n, d) -> new DivideConquerOrthantSearch(n, d, true, -1);
    }
}
