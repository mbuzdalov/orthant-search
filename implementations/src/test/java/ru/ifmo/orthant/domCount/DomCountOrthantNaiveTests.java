package ru.ifmo.orthant.domCount;

import java.util.function.BiFunction;

import ru.ifmo.orthant.NaiveOrthantSearch;

public class DomCountOrthantNaiveTests extends CorrectnessTestsBase {
    @Override
    protected BiFunction<Integer, Integer, DominanceCount> getFactory() {
        return (n, d) -> new OrthantImplementation(new NaiveOrthantSearch(n, d));
    }
}
