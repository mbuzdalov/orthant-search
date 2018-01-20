package ru.ifmo.orthant.domRank;

import java.util.Arrays;

import ru.ifmo.orthant.OrthantSearch;
import ru.ifmo.orthant.ValueTypeClass;
import ru.ifmo.orthant.domRank.DominanceRank;

public final class OrthantImplementation extends DominanceRank {
    private final OrthantSearch orthantSearch;
    private final int[] additionalCollection;
    private final int[] allOnesArray;
    private final boolean[] allTrueArray;
    private final boolean[] allFalseArray;

    public OrthantImplementation(OrthantSearch orthantSearch) {
        int maxPoints = orthantSearch.getMaximumPoints();
        this.orthantSearch = orthantSearch;
        this.additionalCollection = TYPE_CLASS_INSTANCE.createCollection(orthantSearch.getAdditionalCollectionSize(maxPoints));
        this.allOnesArray = new int[maxPoints];
        this.allTrueArray = new boolean[maxPoints];
        this.allFalseArray = new boolean[orthantSearch.getMaximumDimension()];
        Arrays.fill(allTrueArray, true);
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
    public void evaluate(double[][] points, int[] dominanceCounts) {
        int n = points.length;
        Arrays.fill(allOnesArray, 0, n, 1);
        orthantSearch.runSearch(points, allOnesArray, dominanceCounts, 0, n,
                allTrueArray, allTrueArray, additionalCollection,
                TYPE_CLASS_INSTANCE, allFalseArray);
    }

    private static final class DominanceCountTypeClass extends ValueTypeClass<int[]> {
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
            Arrays.fill(collection, from, until, 0);
        }

        @Override
        public void add(int[] source, int sourceIndex, int[] target, int targetIndex) {
            target[targetIndex] += source[sourceIndex];
        }

        @Override
        public void queryToData(int[] source, int sourceIndex, int[] target) {
            // nothing here
        }
    }

    private static final DominanceCountTypeClass TYPE_CLASS_INSTANCE = new DominanceCountTypeClass();
}
