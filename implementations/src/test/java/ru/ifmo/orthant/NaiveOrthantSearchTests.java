package ru.ifmo.orthant;

import java.util.function.BiFunction;

import ru.ifmo.orthant.impl.NaiveOrthantSearch;

public class NaiveOrthantSearchTests extends CorrectnessTestsBase {
    @Override
    protected BiFunction<Integer, Integer, OrthantSearch> getFactory() {
        return NaiveOrthantSearch::new;
    }
}
