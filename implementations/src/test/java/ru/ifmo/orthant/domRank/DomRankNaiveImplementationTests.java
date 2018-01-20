package ru.ifmo.orthant.domRank;

import java.util.function.BiFunction;

public class DomRankNaiveImplementationTests extends CorrectnessTestsBase {
    @Override
    protected BiFunction<Integer, Integer, DominanceRank> getFactory() {
        return NaiveImplementation::new;
    }
}
