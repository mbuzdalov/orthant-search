package ru.ifmo.orthant.buggyNDS.impl;

import java.util.Arrays;

import ru.ifmo.orthant.buggyNDS.BuggyNonDominatedSorting;
import ru.ifmo.orthant.util.DominanceHelper;
import ru.ifmo.orthant.util.PointWrapper;

public final class NaiveImplementation extends BuggyNonDominatedSorting {
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
                if (rr >= jj.value && DominanceHelper.weaklyDominates(ii.point, jj.point)) {
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
