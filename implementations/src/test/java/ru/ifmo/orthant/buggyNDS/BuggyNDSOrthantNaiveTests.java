package ru.ifmo.orthant.buggyNDS;

import java.util.function.BiFunction;

import ru.ifmo.orthant.NaiveOrthantSearch;

public class BuggyNDSOrthantNaiveTests extends CorrectnessTestsBase {
    @Override
    protected BiFunction<Integer, Integer, BuggyNonDominatedSorting> getFactory() {
        return (n, d) -> new OrthantImplementation(new NaiveOrthantSearch(n, d));
    }
}
