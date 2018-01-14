package ru.ifmo.orthant.impl;

import java.util.Arrays;

import ru.ifmo.orthant.OrthantSearch;
import ru.ifmo.orthant.ValueTypeClass;

public final class NaiveOrthantSearch extends OrthantSearch {
    private final PointWrapper[] wrappers;
    private final int maxDimension;

    public NaiveOrthantSearch(int maxPoints, int maxDimension) {
        wrappers = new PointWrapper[maxPoints];
        for (int i = 0; i < maxPoints; ++i) {
            wrappers[i] = new PointWrapper();
        }
        this.maxDimension = maxDimension;
    }

    @Override
    public int getMaximumPoints() {
        return wrappers.length;
    }

    @Override
    public int getMaximumDimension() {
        return maxDimension;
    }

    @Override
    public int getAdditionalCollectionSize(int nPoints) {
        return 1;
    }

    @Override
    protected <T> void runSearchImpl(
            double[][] points, T collection, int from, int until,
            boolean[] isDataPoint, boolean[] isQueryPoint, T additionalCollection,
            ValueTypeClass<T> typeClass, boolean[] isObjectiveStrict) {
        for (int i = from; i < until; ++i) {
            wrappers[i].point = points[i];
            wrappers[i].index = i;
        }
        Arrays.sort(wrappers, from, until);
        for (int q = from; q < until; ++q) {
            PointWrapper Q = wrappers[q];
            if (isQueryPoint[Q.index]) {
                // additionalCollection[0] will be the raw result of the query.
                typeClass.fillWithZeroes(additionalCollection, 0, 1);
                for (int d = from; d < q; ++d) {
                    PointWrapper D = wrappers[d];
                    if (isDataPoint[D.index] && dominates(D.point, Q.point, isObjectiveStrict)) {
                        typeClass.add(collection, D.index, additionalCollection, 0);
                    }
                }
                typeClass.storeQuery(additionalCollection, 0, collection, Q.index);
            }
        }
        for (int i = from; i < until; ++i) {
            wrappers[i].point = null;
        }
    }

    private static boolean dominates(double[] good, double[] weak, boolean[] isStrict) {
        int d = good.length;
        boolean isEqual = true;
        for (int i = 0; i < d; ++i) {
            double g = good[i], w = weak[i];
            isEqual &= g == w;
            if (isStrict[i] ? g >= w : g > w) {
                return false;
            }
        }
        return !isEqual;
    }

    private static final class PointWrapper implements Comparable<PointWrapper> {
        double[] point;
        int index;

        @Override
        public int compareTo(PointWrapper o) {
            double[] l = point, r = o.point;
            int d = l.length;
            for (int i = 0; i < d; ++i) {
                int cmp = Double.compare(l[i], r[i]);
                if (cmp != 0) {
                    return cmp;
                }
            }
            return 0;
        }
    }
}
