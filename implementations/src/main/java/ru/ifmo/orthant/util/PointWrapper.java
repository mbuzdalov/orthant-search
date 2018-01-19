package ru.ifmo.orthant.util;

import java.util.Arrays;

public final class PointWrapper implements Comparable<PointWrapper> {
    public double[] point;
    public int index;
    public int value;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointWrapper that = (PointWrapper) o;
        return Arrays.equals(point, that.point);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(point);
    }
}
