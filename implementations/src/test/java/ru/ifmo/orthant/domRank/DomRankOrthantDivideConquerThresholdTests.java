package ru.ifmo.orthant.domRank;

import java.util.function.BiFunction;

import ru.ifmo.orthant.domRank.impl.OrthantImplementation;
import ru.ifmo.orthant.impl.DivideConquerOrthantSearch;

public class DomRankOrthantDivideConquerThresholdTests extends CorrectnessTestsBase {
    @Override
    protected BiFunction<Integer, Integer, DominanceRank> getFactory() {
        return (n, d) -> new OrthantImplementation(new DivideConquerOrthantSearch(n, d, true));
    }
}
