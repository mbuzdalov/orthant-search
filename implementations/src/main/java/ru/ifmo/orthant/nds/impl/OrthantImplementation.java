package ru.ifmo.orthant.nds.impl;

import java.util.Arrays;

import ru.ifmo.orthant.OrthantSearch;
import ru.ifmo.orthant.ValueTypeClass;
import ru.ifmo.orthant.nds.NonDominatedSorting;

public final class OrthantImplementation extends NonDominatedSorting {
    private final OrthantSearch orthantSearch;
    private final RankTypeClass rankTypeClass;
    private final int[] additionalCollection;
    private final boolean[] allTrueArray;
    private final boolean[] allFalseArray;

    public OrthantImplementation(OrthantSearch orthantSearch) {
        int maxPoints = orthantSearch.getMaximumPoints();
        this.orthantSearch = orthantSearch;
        this.rankTypeClass = new RankTypeClass();
        this.additionalCollection = rankTypeClass.createCollection(orthantSearch.getAdditionalCollectionSize(maxPoints));
        this.allTrueArray = new boolean[maxPoints];
        Arrays.fill(allTrueArray, true);
        this.allFalseArray = new boolean[orthantSearch.getMaximumDimension()];
    }

    @Override
    public int getMaximumPoints() {
        return orthantSearch.getMaximumPoints();
    }

    @Override
    public int getMaximumDimension() {
        return orthantSearch.getMaximumDimension();
    }

    @Override
    public void sort(double[][] points, int[] ranks) {
        Arrays.fill(ranks, 1);
        orthantSearch.runSearch(points, ranks,
                0, points.length,
                allTrueArray, allTrueArray,
                additionalCollection, rankTypeClass, allFalseArray);
    }

    /*
     * For non-dominated sorting, the following monoid works:
     * - the neutral element is "-1";
     * - the commutative composition operation is (a, b) -> Math.max(a, b).
     * - the store-query operation is (res, old) -> res + old, and "old" is set to 1 for every query point.
     */

    private static class RankTypeClass extends ValueTypeClass<int[]> {
        @Override
        public int[] createCollection(int size) {
            return new int[size];
        }

        @Override
        public int size(int[] collection) {
            return collection.length;
        }

        @Override
        public void fillWithZeroes(int[] collection, int from, int until) {
            Arrays.fill(collection, from, until, -1);
        }

        @Override
        public void add(int[] source, int sourceIndex, int[] target, int targetIndex) {
            target[targetIndex] = Math.max(target[targetIndex], source[sourceIndex]);
        }

        @Override
        public void storeQuery(int[] source, int sourceIndex, int[] target, int targetIndex) {
            target[targetIndex] += source[sourceIndex];
        }
    }
}
