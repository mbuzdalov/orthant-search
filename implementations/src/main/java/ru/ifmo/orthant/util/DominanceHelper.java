package ru.ifmo.orthant.util;

public final class DominanceHelper {
    private DominanceHelper() {}

    public static boolean strictlyDominates(double[] a, double[] b) {
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

    public static boolean weaklyDominates(double[] a, double[] b) {
        int dim = a.length;
        for (int i = 0; i < dim; ++i) {
            double aa = a[i], bb = b[i];
            if (aa > bb) {
                return false;
            }
        }
        return true;
    }

    public static boolean strictlyDominates(double[] a, double[] b, boolean[] isStrict) {
        int d = a.length;
        boolean isEqual = true;
        for (int i = 0; i < d; ++i) {
            double g = a[i], w = b[i];
            if (isStrict[i] ? g >= w : g > w) {
                return false;
            }
            isEqual &= g == w;
        }
        return !isEqual;
    }
}
