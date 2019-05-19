package ru.ifmo.orthant.util;

import java.util.Arrays;

/**
 * This class was imported from the following GitHub repository:
 * https://github.com/mbuzdalov/non-dominated-sorting
 * and adapted according to the needs of this repository.
 */
public final class SplitMergeHelper {
    private final int[] scratchM, scratchR;

    public SplitMergeHelper(int size) {
        scratchM = new int[size];
        scratchR = new int[size];
    }

    public final int filter(int[] indices, boolean[] condition, int auxFrom, int from, int until) {
        while (from < until && condition[indices[from]]) {
            ++from;
        }
        int result = from;
        int tempEnd = auxFrom;
        for (int i = from; i < until; ++i) {
            int ii = indices[i];
            if (condition[ii]) {
                indices[result] = ii;
                ++result;
            } else {
                scratchM[tempEnd] = ii;
                ++tempEnd;
            }
        }
        if (auxFrom < tempEnd) {
            System.arraycopy(scratchM, auxFrom, indices, result, tempEnd - auxFrom);
        }
        return result;
    }

    public final int splitInTwo(double[] points, int[] indices,
                                int tempFrom, int from, int until, double median,
                                boolean equalToLeft) {
        int left = from, right = tempFrom;
        for (int i = from; i < until; ++i) {
            int ii = indices[i];
            double v = points[ii];
            if (v < median || (equalToLeft && v == median)) {
                indices[left] = ii;
                ++left;
            } else {
                scratchR[right] = ii;
                ++right;
            }
        }
        System.arraycopy(scratchR, tempFrom, indices, left, right - tempFrom);
        return left;
    }

    public final long splitInThree(double[] points, int[] indices,
                                   int tempFrom, int from, int until, double median) {
        int l = from, m = tempFrom, r = tempFrom;
        for (int i = from; i < until; ++i) {
            int ii = indices[i];
            double v = points[ii];
            if (v < median) {
                indices[l] = ii;
                ++l;
            } else if (v == median) {
                scratchM[m] = ii;
                ++m;
            } else {
                scratchR[r] = ii;
                ++r;
            }
        }
        m -= tempFrom;
        System.arraycopy(scratchM, tempFrom, indices, l, m);
        m += l;
        System.arraycopy(scratchR, tempFrom, indices, m, r - tempFrom);
        return pack(l, m);
    }

    public final int mergeThree(int[] indices, int tempFrom,
                                int fromLeft, int untilLeft,
                                int fromMid, int untilMid,
                                int fromRight, int untilRight) {
        if (fromMid != untilMid) {
            untilLeft = mergeTwo(indices, tempFrom, fromLeft, untilLeft, fromMid, untilMid);
        }
        return mergeTwo(indices, tempFrom, fromLeft, untilLeft, fromRight, untilRight);
    }

    public final int mergeTwo(int[] indices, int tempFrom, int fromLeft, int untilLeft, int fromRight, int untilRight) {
        if (fromRight == untilRight) {
            return untilLeft;
        }
        fromLeft = -Arrays.binarySearch(indices, fromLeft, untilLeft, indices[fromRight]) - 1;
        int target = tempFrom;
        int l = fromLeft, r = fromRight;
        if (l < untilLeft && r < untilRight) {
            int il = indices[l];
            int ir = indices[r];
            while (true) {
                if (il <= ir) {
                    scratchM[target] = il;
                    ++target;
                    if (++l == untilLeft) {
                        break;
                    }
                    il = indices[l];
                } else {
                    scratchM[target] = ir;
                    ++target;
                    if (++r == untilRight) {
                        break;
                    }
                    ir = indices[r];
                }
            }
        }
        int newR = fromLeft + (target - tempFrom) + untilLeft - l;
        if (r != newR && untilRight > r) {
            // copy the remainder of right to its place
            System.arraycopy(indices, r, indices, newR, untilRight - r);
        }
        if (newR != untilLeft && untilLeft > l) {
            // copy the remainder of left to its place
            System.arraycopy(indices, l, indices, fromLeft + (target - tempFrom), untilLeft - l);
        }
        if (target > tempFrom) {
            // copy the merged part
            System.arraycopy(scratchM, tempFrom, indices, fromLeft, target - tempFrom);
        }
        return newR + untilRight - r;
    }

    public final int mergeThree(int[] indices, int[] lexIndices, int tempFrom,
                                int fromLeft, int untilLeft,
                                int fromMid, int untilMid,
                                int fromRight, int untilRight) {
        if (fromMid != untilMid) {
            untilLeft = mergeTwo(indices, lexIndices, tempFrom, fromLeft, untilLeft, fromMid, untilMid);
        }
        return mergeTwo(indices, lexIndices, tempFrom, fromLeft, untilLeft, fromRight, untilRight);
    }

    public final int mergeTwo(int[] indices, int[] lexIndices, int tempFrom, int fromLeft, int untilLeft, int fromRight, int untilRight) {
        if (fromRight == untilRight) {
            return untilLeft;
        }
        int target = tempFrom;
        int l = fromLeft, r = fromRight;
        if (l < untilLeft && r < untilRight) {
            int il = indices[l];
            int ill = lexIndices[il];
            int ir = indices[r];
            int irl = lexIndices[ir];
            while (true) {
                if (ill <= irl) {
                    scratchM[target] = il;
                    ++target;
                    if (++l == untilLeft) {
                        break;
                    }
                    il = indices[l];
                    ill = lexIndices[il];
                } else {
                    scratchM[target] = ir;
                    ++target;
                    if (++r == untilRight) {
                        break;
                    }
                    ir = indices[r];
                    irl = lexIndices[ir];
                }
            }
        }
        int newR = fromLeft + (target - tempFrom) + untilLeft - l;
        if (r != newR && untilRight > r) {
            // copy the remainder of right to its place
            System.arraycopy(indices, r, indices, newR, untilRight - r);
        }
        if (newR != untilLeft && untilLeft > l) {
            // copy the remainder of left to its place
            System.arraycopy(indices, l, indices, fromLeft + (target - tempFrom), untilLeft - l);
        }
        if (target > tempFrom) {
            // copy the merged part
            System.arraycopy(scratchM, tempFrom, indices, fromLeft, target - tempFrom);
        }
        return newR + untilRight - r;
    }

    private static long pack(int mid, int right) {
        return (((long) (mid)) << 32) ^ right;
    }

    public static int extractMid(long packed) {
        return (int) (packed >>> 32);
    }

    public static int extractRight(long packed) {
        return (int) (packed);
    }
}
