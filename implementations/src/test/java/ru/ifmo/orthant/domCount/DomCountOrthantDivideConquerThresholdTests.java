package ru.ifmo.orthant.domCount;

import java.util.function.BiFunction;

import ru.ifmo.orthant.DivideConquerOrthantSearch;

public class DomCountOrthantDivideConquerThresholdTests extends CorrectnessTestsBase {
    @Override
    protected BiFunction<Integer, Integer, DominanceCount> getFactory() {
        return (n, d) -> new OrthantImplementation(new DivideConquerOrthantSearch(n, d, true));
    }
}
