package ru.ifmo.orthant.domRank;

import java.util.function.BiFunction;

import ru.ifmo.orthant.domRank.impl.OrthantImplementation;
import ru.ifmo.orthant.impl.NaiveOrthantSearch;

public class DomRankOrthantNaiveTests extends CorrectnessTestsBase {
    @Override
    protected BiFunction<Integer, Integer, DominanceRank> getFactory() {
        return (n, d) -> new OrthantImplementation(new NaiveOrthantSearch(n, d));
    }
}
