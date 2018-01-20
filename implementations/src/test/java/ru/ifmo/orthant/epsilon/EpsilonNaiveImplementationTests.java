package ru.ifmo.orthant.epsilon;

import java.util.function.BiFunction;

public class EpsilonNaiveImplementationTests extends CorrectnessTestsBase {
    @Override
    protected BiFunction<Integer, Integer, AdditiveEpsilonIndicator> getFactory() {
        return NaiveImplementation::new;
    }
}
