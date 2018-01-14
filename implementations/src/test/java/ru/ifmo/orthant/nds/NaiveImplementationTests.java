package ru.ifmo.orthant.nds;

import java.util.function.BiFunction;

public class NaiveImplementationTests extends CorrectnessTestsBase {
    @Override
    protected BiFunction<Integer, Integer, NonDominatedSorting> getFactory() {
        return NaiveImplementation::new;
    }
}
