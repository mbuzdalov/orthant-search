package ru.ifmo.orthant;

import java.util.function.BiFunction;

public class OrthantSearchNaiveTests extends CorrectnessTestsBase {
    @Override
    protected BiFunction<Integer, Integer, OrthantSearch> getFactory() {
        return NaiveOrthantSearch::new;
    }
}
