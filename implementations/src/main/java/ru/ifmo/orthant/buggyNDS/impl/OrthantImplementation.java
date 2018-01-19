package ru.ifmo.orthant.buggyNDS.impl;

import java.util.Arrays;
import java.util.HashMap;

import ru.ifmo.orthant.OrthantSearch;
import ru.ifmo.orthant.ValueTypeClass;
import ru.ifmo.orthant.buggyNDS.BuggyNonDominatedSorting;
import ru.ifmo.orthant.util.PointWrapper;

public final class OrthantImplementation extends BuggyNonDominatedSorting {
    private final OrthantSearch orthantSearch;
    private final RankTypeClass rankTypeClass;
    private final int[] additionalCollection;
    private final boolean[] allTrueArray;
    private final boolean[] allFalseArray;
    private final int[] queryStore;

    private final HashMap<PointWrapper, PointWrapper> existingWrappers;
    private final PointWrapper[] wrappers;
    private final double[][] newPoints;
    private final int[] newRanks;

    public OrthantImplementation(OrthantSearch orthantSearch) {
        int maxPoints = orthantSearch.getMaximumPoints();
        this.orthantSearch = orthantSearch;
        this.rankTypeClass = new RankTypeClass();
        this.additionalCollection = rankTypeClass.createCollection(orthantSearch.getAdditionalCollectionSize(maxPoints));
        this.queryStore = new int[maxPoints];
        this.allTrueArray = new boolean[maxPoints];
        Arrays.fill(allTrueArray, true);
        this.allFalseArray = new boolean[orthantSearch.getMaximumDimension()];

        wrappers = new PointWrapper[maxPoints];
        for (int i = 0; i < maxPoints; ++i) {
            wrappers[i] = new PointWrapper();
        }
        existingWrappers = new HashMap<>(maxPoints);
        newPoints = new double[maxPoints][];
        newRanks = new int[maxPoints];
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
        // First, we need to leave equal points out, and to put references from "old" points to "new" points.
        int newN = 0;
        for (int i = 0; i < points.length; ++i) {
            PointWrapper current = wrappers[i];
            current.point = points[i];
            current.index = newN;
            PointWrapper appearing = existingWrappers.putIfAbsent(current, current);
            if (appearing == null) {
                newRanks[ranks[i] = newN] = 1;
                newPoints[newN++] = points[i];
            } else {
                ++newRanks[ranks[i] = appearing.index];
            }
        }
        existingWrappers.clear();

        // Second, we call the usual non-dominated sorting,
        // paying attention that the existing values of newRanks represent how much to add to the query result,
        // e.g. the number of equal points.
        orthantSearch.runSearch(newPoints, newRanks, queryStore,
                0, newN, allTrueArray, allTrueArray,
                additionalCollection, rankTypeClass, allFalseArray);

        // Third, we restore the "real" ranks using the computed references
        // AND incrementing the rank value each time we reference it.
        for (int i = points.length - 1; i >= 0; --i) {
            int referenceIndex = ranks[i];
            ranks[i] = newRanks[referenceIndex];
            --newRanks[referenceIndex];
            wrappers[i].point = null;
        }
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
}
