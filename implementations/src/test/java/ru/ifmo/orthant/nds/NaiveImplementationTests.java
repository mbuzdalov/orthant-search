package ru.ifmo.orthant.nds;

import java.util.function.BiFunction;

import ru.ifmo.orthant.nds.impl.NaiveImplementation;

public class NaiveImplementationTests extends CorrectnessTestsBase {
    @Override
    protected BiFunction<Integer, Integer, NonDominatedSorting> getFactory() {
        return NaiveImplementation::new;
    }
}
