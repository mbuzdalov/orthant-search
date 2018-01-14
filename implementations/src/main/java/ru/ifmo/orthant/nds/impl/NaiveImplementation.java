package ru.ifmo.orthant.nds.impl;

import java.util.Arrays;

import ru.ifmo.orthant.nds.NonDominatedSorting;

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

    private boolean dominates(double[] a, double[] b) {
        boolean hasLess = false;
        int dim = a.length;
        for (int i = 0; i < dim; ++i) {
            double aa = a[i], bb = b[i];
            if (aa > bb) {
                return false;
            }
            hasLess |= aa < bb;
        }
        return hasLess;
    }

    @Override
    public void sort(double[][] points, int[] ranks) {
        int n = points.length;
        for (int i = 0; i < n; ++i) {
            wrappers[i].index = i;
            wrappers[i].point = points[i];
            wrappers[i].rank = 0;
        }
        Arrays.sort(wrappers, 0, n);
        for (int i = 0; i < n; ++i) {
            PointWrapper ii = wrappers[i];
            int rr = ii.rank;
            for (int j = i + 1; j < n; ++j) {
                PointWrapper jj = wrappers[j];
                if (rr >= jj.rank && dominates(ii.point, jj.point)) {
                    jj.rank = rr + 1;
                }
            }
        }
        for (int i = 0; i < n; ++i) {
            ranks[wrappers[i].index] = wrappers[i].rank;
            wrappers[i].point = null;
        }
    }

    private static class PointWrapper implements Comparable<PointWrapper> {
        double[] point;
        int index;
        int rank;

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
