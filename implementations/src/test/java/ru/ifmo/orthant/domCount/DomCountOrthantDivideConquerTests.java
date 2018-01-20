package ru.ifmo.orthant.domCount;

import java.util.function.BiFunction;

import ru.ifmo.orthant.domCount.impl.OrthantImplementation;
import ru.ifmo.orthant.impl.DivideConquerOrthantSearch;

public class DomCountOrthantDivideConquerTests extends CorrectnessTestsBase {
    @Override
    protected BiFunction<Integer, Integer, DominanceCount> getFactory() {
        return (n, d) -> new OrthantImplementation(new DivideConquerOrthantSearch(n, d, false));
    }
}
