package ru.ifmo.orthant.nds;

import java.util.function.BiFunction;

import ru.ifmo.orthant.impl.DivideConquerOrthantSearch;
import ru.ifmo.orthant.impl.NaiveOrthantSearch;
import ru.ifmo.orthant.nds.impl.OrthantImplementation;

public class NDSOrthantDivideConquerTests extends CorrectnessTestsBase {
    @Override
    protected BiFunction<Integer, Integer, NonDominatedSorting> getFactory() {
        return (n, d) -> new OrthantImplementation(new DivideConquerOrthantSearch(n, d));
    }
}
