package ru.ifmo.orthant.nds;

import java.util.Arrays;

import ru.ifmo.orthant.OrthantSearch;
import ru.ifmo.orthant.ValueTypeClass;

public final class OrthantImplementation extends NonDominatedSorting {
    private final OrthantSearch orthantSearch;
    private final int[] additionalCollection;
    private final boolean[] allTrueArray;
    private final boolean[] allFalseArray;
    private final int[] queryStore;

    public OrthantImplementation(OrthantSearch orthantSearch) {
        int maxPoints = orthantSearch.getMaximumPoints();
        this.orthantSearch = orthantSearch;
        this.additionalCollection = TYPE_CLASS_INSTANCE.createCollection(orthantSearch.getAdditionalCollectionSize(maxPoints));
        this.queryStore = new int[maxPoints];
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
        int n = points.length;
        if (n == 0) {
            return;
        }
        int dimension = points[0].length;
        Arrays.fill(ranks, 1);
        orthantSearch.runSearch(points, ranks, queryStore,
                0, n, dimension, allTrueArray, allTrueArray,
                additionalCollection, TYPE_CLASS_INSTANCE, allFalseArray);
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
        public boolean targetChangesOnAdd(int[] source, int sourceIndex, int[] target, int targetIndex) {
            return source[sourceIndex] > target[targetIndex];
        }

        @Override
        public void queryToData(int[] source, int sourceIndex, int[] target) {
            target[sourceIndex] += source[sourceIndex];
        }
    }

    private static final RankTypeClass TYPE_CLASS_INSTANCE = new RankTypeClass();
}
