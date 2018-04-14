package ru.ifmo.orthant.nds;

import java.util.function.BiFunction;

import ru.ifmo.orthant.nds.extra.JensenENSHybrid;

public class NDSHybridTests extends CorrectnessTestsBase {
    @Override
    protected BiFunction<Integer, Integer, NonDominatedSorting> getFactory() {
        return JensenENSHybrid::new;
    }
}
