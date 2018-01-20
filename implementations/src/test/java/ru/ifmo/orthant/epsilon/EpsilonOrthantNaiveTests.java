package ru.ifmo.orthant.epsilon;

import java.util.function.BiFunction;

import ru.ifmo.orthant.NaiveOrthantSearch;

public class EpsilonOrthantNaiveTests extends CorrectnessTestsBase {
    @Override
    protected BiFunction<Integer, Integer, AdditiveEpsilonIndicator> getFactory() {
        return (n, d) -> new OrthantImplementation(new NaiveOrthantSearch(n * 2, d));
    }
}
