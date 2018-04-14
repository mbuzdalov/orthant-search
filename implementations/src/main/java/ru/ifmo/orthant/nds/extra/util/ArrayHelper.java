package ru.ifmo.orthant.nds.extra.util;

/**
 * This class was imported from the following GitHub repository:
 * https://github.com/mbuzdalov/non-dominated-sorting
 * and adapted according to the needs of this repository.
 *
 * The particular revision location is:
 * https://github.com/mbuzdalov/non-dominated-sorting/tree/56fcfc61f5a4009e8ed02c0c3a4b00d390ba6aff
 */
public final class ArrayHelper {
    private ArrayHelper() {}

    public static void swap(int[] array, int a, int b) {
        int tmp = array[a];
        array[a] = array[b];
        array[b] = tmp;
    }

    public static void swap(double[] array, int a, int b) {
        double tmp = array[a];
        array[a] = array[b];
        array[b] = tmp;
    }

    public static boolean equal(double[] a, double[] b) {
        int al = a.length;
        return al == b.length && equal(a, b, al);
    }

    private static boolean equal(double[] a, double[] b, int prefixLength) {
        for (int i = 0; i < prefixLength; ++i) {
            if (a[i] != b[i]) {
                return false;
            }
        }
        return true;
    }

    public static void fillIdentity(int[] array, int n) {
        for (int i = 0; i < n; ++i) {
            array[i] = i;
        }
    }

    public static double destructiveMedian(double[] array, int from, int until) {
        int index = (from + until) >>> 1;
        while (from + 1 < until) {
            double pivot = array[(from + until) >>> 1];
            if (from + 5 < until) {
                pivot = (pivot + array[from] + array[until - 1]) / 3;
            }
            double vl, vr;
            int l = from, r = until - 1;
            while (l <= r) {
                while ((vl = array[l]) < pivot) ++l;
                while ((vr = array[r]) > pivot) --r;
                if (l <= r) {
                    array[l] = vr;
                    array[r] = vl;
                    ++l;
                    --r;
                }
            }
            if (index < r) {
                until = r + 1;
            } else if (l < index) {
                from = l;
            } else if (r == index) {
                return max(array, from, r + 1);
            } else if (l == index) {
                return min(array, l, until);
            } else {
                return array[index];
            }
        }

        return array[index];
    }

    public static int transplant(double[] source, int[] indices, int fromIndex, int untilIndex, double[] target, int targetFrom) {
        for (int i = fromIndex; i < untilIndex; ++i) {
            target[targetFrom++] = source[indices[i]];
        }
        return targetFrom;
    }

    public static double max(double[] array, int from, int until) {
        if (from >= until) {
            return Double.NEGATIVE_INFINITY;
        } else {
            double rv = array[from];
            for (int i = from + 1; i < until; ++i) {
                double v = array[i];
                if (rv < v) {
                    rv = v;
                }
            }
            return rv;
        }
    }

    public static double min(double[] array, int from, int until) {
        if (from >= until) {
            return Double.POSITIVE_INFINITY;
        } else {
            double rv = array[from];
            for (int i = from + 1; i < until; ++i) {
                double v = array[i];
                if (rv > v) {
                    rv = v;
                }
            }
            return rv;
        }
    }
}
