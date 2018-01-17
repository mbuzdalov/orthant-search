package ru.ifmo.orthant;

import java.util.function.BiFunction;

import ru.ifmo.orthant.impl.DivideConquerOrthantSearch;

public class DivideConquerOrthantSearchTests extends CorrectnessTestsBase {
    @Override
    protected BiFunction<Integer, Integer, OrthantSearch> getFactory() {
        return DivideConquerOrthantSearch::new;
    }
}
