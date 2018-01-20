package ru.ifmo.orthant;

import java.util.function.BiFunction;

import ru.ifmo.orthant.impl.DivideConquerOrthantSearch;

public class OrthantSearchDivideConquerTests extends CorrectnessTestsBase {
    @Override
    protected BiFunction<Integer, Integer, OrthantSearch> getFactory() {
        return (n, d) -> new DivideConquerOrthantSearch(n, d, false);
    }
}
