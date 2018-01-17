package ru.ifmo.orthant.util;

public final class ArrayHelper {
    private ArrayHelper() {}

    public static int filter(int[] indices, boolean[] condition, int from, int until, int[] temp) {
        int result = from;
        int tempEnd = from;
        for (int i = from; i < until; ++i) {
            int ii = indices[i];
            if (condition[ii]) {
                indices[result++] = ii;
            } else {
                temp[tempEnd++] = ii;
            }
        }
        System.arraycopy(temp, from, indices, result, tempEnd - from);
        return result;
    }

    public static void merge(int[] indices, int[] lexIndices, int from, int middle, int until, int[] temp) {
        int left = from, right = middle, target = from;
        while (left < middle && right < until) {
            int li = indices[left], ri = indices[right];
            if (lexIndices[li] <= lexIndices[ri]) {
                temp[target] = li;
                ++left;
            } else {
                temp[target] = ri;
                ++right;
            }
            ++target;
        }
        if (left < middle) {
            System.arraycopy(indices, left, indices, right - middle + left, middle - left);
        }
        System.arraycopy(temp, from, indices, from, target - from);
    }

    public static long splitInThree(int[] indices, double[] values, int from, int until, double middle, int[] temp) {
        int lessLast = from;
        int greaterLast = from;
        int equalFirst = until;
        for (int i = from; i < until; ++i) {
            int ii = indices[i];
            double key = values[ii];
            if (key < middle) {
                indices[lessLast++] = ii;
            } else if (key > middle) {
                temp[greaterLast++] = ii;
            } else {
                temp[--equalFirst] = ii;
            }
        }
        int middleStart = lessLast;
        // This reverses the middle indices, so it's not what System.arraycopy can do.
        for (int equalLast = until - 1; equalFirst <= equalLast; --equalLast) {
            indices[lessLast++] = temp[equalLast];
        }
        System.arraycopy(temp, from, indices, lessLast, greaterLast - from);
        int greaterStart = lessLast;
        return ((long) (middleStart) << 32) | greaterStart;
    }

    public static double min(double[] values, int from, int until) {
        double rv = Double.POSITIVE_INFINITY;
        for (int i = from; i < until; ++i) {
            double curr = values[i];
            if (rv > curr) {
                rv = curr;
            }
        }
        return rv;
    }

    public static double max(double[] values, int from, int until) {
        double rv = Double.NEGATIVE_INFINITY;
        for (int i = from; i < until; ++i) {
            double curr = values[i];
            if (rv < curr) {
                rv = curr;
            }
        }
        return rv;
    }

    public static void transplant(double[] values, int[] indices, int from, int until, double[] target, int targetIndex) {
        for (int i = from, j = targetIndex; i < until; ++i, ++j) {
            target[j] = values[indices[i]];
        }
    }

    public static double destructiveMedian(double[] array, int from, int until) {
        int index = (from + until) >>> 1;
        while (from + 1 < until) {
            double pivot = array[(from + until) >>> 1];
            int l = from, r = until - 1;
            while (l <= r) {
                while (array[l] < pivot) ++l;
                while (array[r] > pivot) --r;
                if (l <= r) {
                    double tmp = array[l];
                    array[l] = array[r];
                    array[r] = tmp;
                    ++l;
                    --r;
                }
            }
            if (from < r && index <= r) {
                until = r + 1;
            } else if (l + 1 < until && l <= index) {
                from = l;
            } else {
                break;
            }
        }
        return array[index];
    }
}
