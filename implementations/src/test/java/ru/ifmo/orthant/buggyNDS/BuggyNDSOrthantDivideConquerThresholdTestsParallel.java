package ru.ifmo.orthant.buggyNDS;

import java.util.function.BiFunction;

import ru.ifmo.orthant.DivideConquerOrthantSearch;

public class BuggyNDSOrthantDivideConquerThresholdTestsParallel extends CorrectnessTestsBase {
    @Override
    protected BiFunction<Integer, Integer, BuggyNonDominatedSorting> getFactory() {
        return (n, d) -> new OrthantImplementation(new DivideConquerOrthantSearch(n, d, true, -1));
    }
}
