package ru.ifmo.orthant.util;

public final class ArrayHelper {
    private ArrayHelper() {}

    public static int filter(int[] indices, boolean[] condition, int from, int until, int[] temp) {
        while (from < until && condition[indices[from]]) {
            ++from;
        }
        int result = from;
        int tempEnd = from;
        for (int i = from; i < until; ++i) {
            int ii = indices[i];
            if (condition[ii]) {
                indices[result] = ii;
                ++result;
            } else {
                temp[tempEnd] = ii;
                ++tempEnd;
            }
        }
        if (from < tempEnd) {
            System.arraycopy(temp, from, indices, result, tempEnd - from);
        }
        return result;
    }

    public static void merge(int[] indices, int[] lexIndices, int from, int middle, int until, int[] temp) {
        if (middle == until) {
            return;
        }
        int li = indices[from], lli = lexIndices[li];
        int ri = indices[middle], rri = lexIndices[ri];
        while (from < middle && lli <= rri) {
            ++from;
            li = indices[from];
            lli = lexIndices[li];
        }
        if (from == middle) {
            return;
        }
        int left = from, right = middle, target = from;
        while (true) {
            if (lli <= rri) {
                temp[target] = li;
                ++left;
                ++target;
                if (left == middle) {
                    break;
                }
                li = indices[left];
                lli = lexIndices[li];
            } else {
                temp[target] = ri;
                ++right;
                ++target;
                if (right == until) {
                    break;
                }
                ri = indices[right];
                rri = lexIndices[ri];
            }
        }
        if (left < middle) {
            System.arraycopy(indices, left, indices, right - middle + left, middle - left);
        }
        if (from < target) {
            System.arraycopy(temp, from, indices, from, target - from);
        }
    }

    public static long splitInThree(int[] indices, double[] values, int from, int until, double middle, int[] temp) {
        int equalLast = from;
        int greaterFirst = from + (until - from);
        int greaterLast = greaterFirst;
        int lessLast = from;
        for (int i = from; i < until; ++i) {
            int ii = indices[i];
            double key = values[ii];
            if (key < middle) {
                indices[lessLast] = ii;
                ++lessLast;
            } else if (key > middle) {
                temp[greaterLast] = ii;
                ++greaterLast;
            } else {
                temp[equalLast] = ii;
                ++equalLast;
            }
        }
        if (from < equalLast) {
            System.arraycopy(temp, from, indices, lessLast, equalLast - from);
        }
        int middleStart = lessLast;
        lessLast += equalLast - from;
        int greaterStart = lessLast;
        if (greaterFirst < greaterLast) {
            System.arraycopy(temp, greaterFirst, indices, lessLast, greaterLast - greaterFirst);
        }
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
            if (from + 5 < until) {
                double mid = (array[from] + array[until - 1]) / 2;
                pivot = (pivot + mid) / 2;
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
}
