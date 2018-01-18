package ru.ifmo.orthant.buggyNDS;

import java.util.function.BiFunction;

import ru.ifmo.orthant.buggyNDS.impl.OrthantImplementation;
import ru.ifmo.orthant.impl.DivideConquerOrthantSearch;

public class BuggyNDSOrthantDivideConquerTests extends CorrectnessTestsBase {
    @Override
    protected BiFunction<Integer, Integer, BuggyNonDominatedSorting> getFactory() {
        return (n, d) -> new OrthantImplementation(new DivideConquerOrthantSearch(n, d, false));
    }
}
