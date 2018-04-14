package ru.ifmo.orthant.nds.extra;

import java.util.Arrays;

import ru.ifmo.orthant.nds.NonDominatedSorting;
import ru.ifmo.orthant.nds.extra.util.ArrayHelper;
import ru.ifmo.orthant.nds.extra.util.DoubleArraySorter;

/**
 * This class was imported from the following GitHub repository:
 * https://github.com/mbuzdalov/non-dominated-sorting
 * and adapted according to the needs of this repository.
 *
 * The particular name of the class in that repository was: ru.ifmo.nds.bos.Improved.
 *
 * The particular revision location is:
 * https://github.com/mbuzdalov/non-dominated-sorting/tree/56fcfc61f5a4009e8ed02c0c3a4b00d390ba6aff
 */
public class BestOrderSort extends NonDominatedSorting {
    private int[][] objectiveIndices;
    private int[] reindex;
    private double[][] points;
    private int[] ranks;
    private int[][] lastFrontIndex;
    private int[][] prevFrontIndex;

    private int[][] checkIndices;
    private int[] checkIndicesCount;
    private boolean[][] indexNeeded;
    private int[] indexNeededCount;

    private DoubleArraySorter sorter;

    public BestOrderSort(int maximumPoints, int maximumDimension) {
        objectiveIndices = new int[maximumDimension][maximumPoints];
        lastFrontIndex = new int[maximumDimension][maximumPoints];
        prevFrontIndex = new int[maximumDimension][maximumPoints];
        checkIndices = new int[maximumPoints][maximumDimension];
        checkIndicesCount = new int[maximumPoints];
        indexNeededCount = new int[maximumPoints];
        indexNeeded = new boolean[maximumPoints][maximumDimension];
        reindex = new int[maximumPoints];
        points = new double[maximumPoints][];
        ranks = new int[maximumPoints];
        sorter = new DoubleArraySorter(maximumPoints);
    }

    @Override
    public int getMaximumPoints() {
        return points.length;
    }

    @Override
    public int getMaximumDimension() {
        return objectiveIndices.length;
    }

    private boolean dominates(int i1, int i2) {
        if (i1 > i2) {
            return false;
        }
        double[] p1 = points[i1];
        double[] p2 = points[i2];
        int dim = p1.length;

        // I have not yet validated this empirically,
        // but when needed count is high, the simple loop is preferable.
        if (indexNeededCount[i1] * 3 < p1.length) {
            int[] checkIdx = checkIndices[i1];
            boolean[] idxNeeded = indexNeeded[i1];

            int count = checkIndicesCount[i1];
            int index = 0;
            while (index < count) {
                int currIndex = checkIdx[index];
                if (idxNeeded[currIndex]) {
                    if (p1[currIndex] > p2[currIndex]) {
                        checkIndicesCount[i1] = count;
                        return false;
                    }
                    ++index;
                } else {
                    checkIdx[index] = checkIdx[--count];
                }
            }
            checkIndicesCount[i1] = count;
            return true;
        } else {
            return strictlyDominates(p1, p2, dim);
        }
    }

    private void initializeObjectiveIndices(int newN, int dim) {
        for (int d = 0; d < dim; ++d) {
            int[] currentObjectiveIndex = objectiveIndices[d];
            ArrayHelper.fillIdentity(currentObjectiveIndex, newN);
            if (d > 0) {
                sorter.sortWhileResolvingEqual(this.points, currentObjectiveIndex, 0, newN, d, objectiveIndices[0]);
            }
        }
    }

    private void rankPoint(int currIndex, int[] prevFI, int[] lastFI, int smallestRank) {
        int currRank = smallestRank;
        // This is currently implemented as sequential search.
        // A binary search implementation is expected as well.
        while (true) {
            int prevIndex = lastFI[currRank];
            boolean someoneDominatesMe = false;
            while (prevIndex != -1) {
                if (dominates(prevIndex, currIndex)) {
                    someoneDominatesMe = true;
                    break;
                } else {
                    prevIndex = prevFI[prevIndex];
                }
            }
            if (!someoneDominatesMe) {
                break;
            }
            ++currRank;
        }
        this.ranks[currIndex] = currRank;
    }

    public void sort(double[][] points, int[] ranks) {
        int origN = ranks.length;
        int dim = points[0].length;
        if (dim == 0) {
            Arrays.fill(ranks, 0);
            return;
        }
        ArrayHelper.fillIdentity(reindex, origN);
        sorter.lexicographicalSort(points, reindex, 0, origN, dim);
        int newN = DoubleArraySorter.retainUniquePoints(points, reindex, this.points, ranks);
        initializeObjectiveIndices(newN, dim);

        Arrays.fill(this.ranks, 0, newN, -1);
        Arrays.fill(checkIndicesCount, 0, newN, dim);
        Arrays.fill(indexNeededCount, 0, newN, dim);

        for (int i = 0; i < newN; ++i) {
            ArrayHelper.fillIdentity(checkIndices[i], dim);
            Arrays.fill(indexNeeded[i], 0, dim, true);
        }

        for (int d = 0; d < dim; ++d) {
            Arrays.fill(lastFrontIndex[d], 0, newN, -1);
            Arrays.fill(prevFrontIndex[d], 0, newN, -1);
        }

        int smallestRank = 0;

        for (int hIndex = 0, ranked = 0;
             hIndex < newN && ranked < newN;
             ++hIndex) {
            for (int oIndex = 0; oIndex < dim; ++oIndex) {
                int currIndex = objectiveIndices[oIndex][hIndex];
                int[] prevFI = prevFrontIndex[oIndex];
                int[] lastFI = lastFrontIndex[oIndex];
                if (this.ranks[currIndex] == -1) {
                    rankPoint(currIndex, prevFI, lastFI, smallestRank);
                    ++ranked;
                }
                indexNeeded[currIndex][oIndex] = false;
                int myRank = this.ranks[currIndex];
                prevFI[currIndex] = lastFI[myRank];
                lastFI[myRank] = currIndex;
                if (--indexNeededCount[currIndex] == 0) {
                    if (smallestRank < myRank + 1) {
                        smallestRank = myRank + 1;
                    }
                }
            }
        }

        Arrays.fill(this.points, 0, origN, null);
        for (int i = 0; i < origN; ++i) {
            ranks[i] = this.ranks[ranks[i]];
        }
    }

    private static boolean strictlyDominates(double[] a, double[] b, int dim) {
        boolean hasSmaller = false;
        for (int i = 0; i < dim; ++i) {
            double ai = a[i], bi = b[i];
            if (ai > bi) {
                return false;
            }
            if (ai < bi) {
                hasSmaller = true;
            }
        }
        return hasSmaller;
    }

}
