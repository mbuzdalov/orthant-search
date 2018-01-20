package ru.ifmo.orthant.domCount;

import java.util.function.BiFunction;

public class DomCountNaiveImplementationTests extends CorrectnessTestsBase {
    @Override
    protected BiFunction<Integer, Integer, DominanceCount> getFactory() {
        return NaiveImplementation::new;
    }
}
