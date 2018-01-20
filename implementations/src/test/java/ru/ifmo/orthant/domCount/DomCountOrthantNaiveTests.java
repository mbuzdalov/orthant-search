package ru.ifmo.orthant.domCount;

import java.util.function.BiFunction;

import ru.ifmo.orthant.domCount.impl.OrthantImplementation;
import ru.ifmo.orthant.impl.NaiveOrthantSearch;

public class DomCountOrthantNaiveTests extends CorrectnessTestsBase {
    @Override
    protected BiFunction<Integer, Integer, DominanceCount> getFactory() {
        return (n, d) -> new OrthantImplementation(new NaiveOrthantSearch(n, d));
    }
}
