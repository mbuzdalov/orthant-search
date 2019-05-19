package ru.ifmo.orthant.nds.extra;

import java.util.Arrays;

import ru.ifmo.orthant.nds.NonDominatedSorting;

import ru.ifmo.orthant.util.ArrayHelper;
import ru.ifmo.orthant.util.ArraySorter;
import ru.ifmo.orthant.util.DominanceHelper;

/**
 * This class was imported from the following GitHub repository:
 * https://github.com/mbuzdalov/non-dominated-sorting
 * and adapted according to the needs of this repository.
 */
public final class BestOrderSort extends NonDominatedSorting {
    private int[][] objectiveIndices;
    private double[][] points;
    private int[] indices;
    private int[] ranks;
    private int[][] lastFrontIndex;
    private int[][] prevFrontIndex;
    private int[] indexNeededCount;
    private ArraySorter sorter;

    public BestOrderSort(int maximumPoints, int maximumDimension) {
        objectiveIndices = new int[maximumDimension][maximumPoints];
        lastFrontIndex = new int[maximumDimension][maximumPoints];
        prevFrontIndex = new int[maximumDimension][maximumPoints];
        indexNeededCount = new int[maximumPoints];
        points = new double[maximumPoints][];
        indices = new int[maximumPoints];
        ranks = new int[maximumPoints];
        sorter = new ArraySorter(maximumPoints);
    }

    private void initializeObjectiveIndices(int newN, int dim) {
        for (int d = 0; d < dim; ++d) {
            int[] currentObjectiveIndex = objectiveIndices[d];
            ArrayHelper.fillIdentity(currentObjectiveIndex, newN);
            if (d > 0) {
                sorter.sortComparingByIndicesIfEqual(this.points, currentObjectiveIndex, 0, newN, d);
            }
        }
    }

    private void rankPoint(int currIndex, int[] prevFI, int[] lastFI, int smallestRank) {
        double[] p2 = points[currIndex];
        int maxObj = p2.length - 1;
        int currRank = smallestRank;
        // This is currently implemented as sequential search.
        // A binary search implementation is expected as well.
        while (true) {
            int prevIndex = lastFI[currRank];
            boolean someoneDominatesMe = false;
            while (prevIndex != -1) {
                if (prevIndex < currIndex && // For now, we totally ignore that some coordinates are unneeded.
                        DominanceHelper.strictlyDominatesAssumingLexicographicallySmaller(points[prevIndex], p2, maxObj)) {
                    someoneDominatesMe = true;
                    break;
                }
                prevIndex = prevFI[prevIndex];
            }
            if (!someoneDominatesMe) {
                break;
            }
            ++currRank;
        }
        this.ranks[currIndex] = currRank;
    }

    @Override
    public int getMaximumPoints() {
        return indices.length;
    }

    @Override
    public int getMaximumDimension() {
        return objectiveIndices.length;
    }

    @Override
    public void sort(double[][] points, int[] ranks) {
        int origN = ranks.length;
        int dim = points[0].length;

        if (dim == 0) {
            Arrays.fill(ranks, 0);
            return;
        }

        ArrayHelper.fillIdentity(indices, origN);
        sorter.lexicographicalSort(points, indices, 0, origN, dim);

        if (dim == 1) {
            for (int i = 0, r = 0; i < origN; ++i) {
                int ii = indices[i];
                ranks[ii] = r;
                if (i + 1 < origN && points[ii][0] != points[indices[i + 1]][0]) {
                    ++r;
                }
            }
            return;
        }

        int newN = ArraySorter.retainUniquePoints(points, indices, this.points, ranks);
        initializeObjectiveIndices(newN, dim);

        Arrays.fill(this.ranks, 0, newN, -1);
        Arrays.fill(indexNeededCount, 0, newN, dim);

        for (int d = 0; d < dim; ++d) {
            Arrays.fill(lastFrontIndex[d], 0, newN, -1);
            Arrays.fill(prevFrontIndex[d], 0, newN, -1);
        }

        int smallestRank = 0;

        for (int hIndex = 0, ranked = 0; hIndex < newN && ranked < newN; ++hIndex) {
            for (int oIndex = 0; oIndex < dim; ++oIndex) {
                int currIndex = objectiveIndices[oIndex][hIndex];
                int[] prevFI = prevFrontIndex[oIndex];
                int[] lastFI = lastFrontIndex[oIndex];
                if (this.ranks[currIndex] == -1) {
                    rankPoint(currIndex, prevFI, lastFI, smallestRank);
                    ++ranked;
                }
                int myRank = this.ranks[currIndex];
                prevFI[currIndex] = lastFI[myRank];
                lastFI[myRank] = currIndex;
                if (--indexNeededCount[currIndex] == 0) {
                    if (smallestRank <= myRank) {
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
}
