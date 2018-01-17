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
        return 0;
    }

    @Override
    protected <T> void runSearchImpl(
            double[][] points, T dataCollection, T queryCollection, int from, int until,
            boolean[] isDataPoint, boolean[] isQueryPoint, T additionalCollection,
            ValueTypeClass<T> typeClass, boolean[] isObjectiveStrict) {
        for (int i = from; i < until; ++i) {
            wrappers[i].point = points[i];
            wrappers[i].index = i;
        }
        Arrays.sort(wrappers, from, until);
        for (int q = from; q < until; ++q) {
            PointWrapper Q = wrappers[q];
            int qi = Q.index;
            if (isQueryPoint[qi]) {
                typeClass.fillWithZeroes(queryCollection, qi, qi + 1);
                for (int d = from; d < q; ++d) {
                    PointWrapper D = wrappers[d];
                    int di = D.index;
                    if (isDataPoint[di]
                            && typeClass.targetChangesOnAdd(dataCollection, di, queryCollection, qi)
                            && dominates(D.point, Q.point, isObjectiveStrict)) {
                        typeClass.add(dataCollection, di, queryCollection, qi);
                    }
                }
                if (isDataPoint[qi]) {
                    typeClass.queryToData(queryCollection, qi, dataCollection, qi);
                }
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
