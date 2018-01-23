package ru.ifmo.orthant.util;

public final class PointWrapper implements Comparable<PointWrapper> {
    public double[] point;
    public int index;
    public int dimension = -1;
    public int value;

    @Override
    public int compareTo(PointWrapper o) {
        if (dimension == -1) {
            throw new IllegalStateException("Dimension is not specified");
        }
        if (dimension != o.dimension) {
            throw new IllegalArgumentException("Cannot compare points of different dimensions");
        }
        double[] l = point, r = o.point;
        for (int i = 0; i < dimension; ++i) {
            int cmp = Double.compare(l[i], r[i]);
            if (cmp != 0) {
                return cmp;
            }
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (dimension == -1) {
            throw new IllegalStateException("Dimension is not specified");
        }
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointWrapper that = (PointWrapper) o;
        if (dimension != that.dimension) {
            return false;
        }
        double[] l = point, r = that.point;
        for (int i = 0; i < dimension; ++i) {
            if (l[i] != r[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int rv = 0;
        for (int i = 0; i < dimension; ++i) {
            double v = point[i];
            // Canonize a little bit
            v = v == -0 ? +0 : v;
            rv = 31 * rv + Double.hashCode(v);
        }
        return rv;
    }
}
