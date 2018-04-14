package ru.ifmo.orthant.nds;

import java.util.function.BiFunction;

import ru.ifmo.orthant.nds.extra.NonDominationTree;

public class NDSNonDominationTreeTests extends CorrectnessTestsBase {
    @Override
    protected BiFunction<Integer, Integer, NonDominatedSorting> getFactory() {
        return NonDominationTree::new;
    }
}
