package ru.ifmo.orthant.nds;

import java.util.function.BiFunction;

import ru.ifmo.orthant.NaiveOrthantSearch;

public class NDSOrthantNaiveTests extends CorrectnessTestsBase {
    @Override
    protected BiFunction<Integer, Integer, NonDominatedSorting> getFactory() {
        return (n, d) -> new OrthantImplementation(new NaiveOrthantSearch(n, d));
    }
}
