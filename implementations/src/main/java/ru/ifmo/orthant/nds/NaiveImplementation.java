package ru.ifmo.orthant.nds;

import java.util.Arrays;

import ru.ifmo.orthant.util.DominanceHelper;
import ru.ifmo.orthant.util.PointWrapper;

public final class NaiveImplementation extends NonDominatedSorting {
    private final PointWrapper[] wrappers;
    private final int maxDimension;

    public NaiveImplementation(int maximumPoints, int maximumDimension) {
        wrappers = new PointWrapper[maximumPoints];
        for (int i = 0; i < maximumPoints; ++i) {
            wrappers[i] = new PointWrapper();
        }
        maxDimension = maximumDimension;
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
    public void sort(double[][] points, int[] ranks) {
        int n = points.length;
        if (n == 0) {
            return;
        }
        int dimension = points[0].length;
        for (int i = 0; i < n; ++i) {
            wrappers[i].index = i;
            wrappers[i].point = points[i];
            wrappers[i].value = 0;
        }
        Arrays.sort(wrappers, 0, n);
        for (int i = 0; i < n; ++i) {
            PointWrapper ii = wrappers[i];
            int rr = ii.value;
            for (int j = i + 1; j < n; ++j) {
                PointWrapper jj = wrappers[j];
                if (rr >= jj.value && DominanceHelper.strictlyDominates(ii.point, jj.point, dimension)) {
                    jj.value = rr + 1;
                }
            }
        }
        for (int i = 0; i < n; ++i) {
            ranks[wrappers[i].index] = wrappers[i].value;
            wrappers[i].point = null;
        }
    }
}
