package ru.ifmo.orthant.nds;

import java.util.function.BiFunction;

import ru.ifmo.orthant.DivideConquerOrthantSearch;

public class NDSOrthantDivideConquerThresholdTests extends CorrectnessTestsBase {
    @Override
    protected BiFunction<Integer, Integer, NonDominatedSorting> getFactory() {
        return (n, d) -> new OrthantImplementation(new DivideConquerOrthantSearch(n, d, true));
    }
}
