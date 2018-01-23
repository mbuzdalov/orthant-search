package ru.ifmo.orthant;

import java.util.Arrays;

import ru.ifmo.orthant.util.DominanceHelper;
import ru.ifmo.orthant.util.PointWrapper;

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
            double[][] points, T dataCollection, T queryCollection, int from, int until, int dimension,
            boolean[] isDataPoint, boolean[] isQueryPoint, T additionalCollection,
            ValueTypeClass<T> typeClass, boolean[] isObjectiveStrict) {
        for (int i = from; i < until; ++i) {
            wrappers[i].point = points[i];
            wrappers[i].index = i;
            wrappers[i].dimension = dimension;
        }
        Arrays.sort(wrappers, from, until);
        for (int q = from; q < until; ++q) {
            PointWrapper Q = wrappers[q];
            int qi = Q.index;
            if (isQueryPoint[qi]) {
                typeClass.fillWithZeroes(queryCollection, qi, qi + 1);
                for (int d = q - 1; d >= 0; --d) {
                    PointWrapper D = wrappers[d];
                    int di = D.index;
                    if (isDataPoint[di]
                            && typeClass.targetChangesOnAdd(dataCollection, di, queryCollection, qi)
                            && DominanceHelper.strictlyDominates(D.point, Q.point, dimension, isObjectiveStrict)) {
                        typeClass.add(dataCollection, di, queryCollection, qi);
                    }
                }
                typeClass.queryToData(queryCollection, qi, dataCollection);
            }
        }
        for (int i = from; i < until; ++i) {
            wrappers[i].point = null;
        }
    }
}
