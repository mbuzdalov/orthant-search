package ru.ifmo.orthant.domRank;

import java.util.function.BiFunction;

import ru.ifmo.orthant.domRank.impl.NaiveImplementation;

public class DomRankNaiveImplementationTests extends CorrectnessTestsBase {
    @Override
    protected BiFunction<Integer, Integer, DominanceRank> getFactory() {
        return NaiveImplementation::new;
    }
}
