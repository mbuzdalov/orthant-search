package ru.ifmo.orthant.buggyNDS;

import java.util.function.BiFunction;

public class BuggyNDSNaiveImplementationTests extends CorrectnessTestsBase {
    @Override
    protected BiFunction<Integer, Integer, BuggyNonDominatedSorting> getFactory() {
        return NaiveImplementation::new;
    }
}
