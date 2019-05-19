package ru.ifmo.orthant.nds.extra;

import java.util.Arrays;

import ru.ifmo.orthant.nds.NonDominatedSorting;
import ru.ifmo.orthant.nds.extra.util.Split;
import ru.ifmo.orthant.nds.extra.util.SplitBuilder;

import ru.ifmo.orthant.util.ArrayHelper;
import ru.ifmo.orthant.util.ArraySorter;

/**
 * This class was imported from the following GitHub repository:
 * https://github.com/mbuzdalov/non-dominated-sorting
 * and adapted according to the needs of this repository.
 *
 * The particular name of the class in that repository was: ru.ifmo.nds.ndt.ENS_NDT_Arrays.
 *
 * The particular revision location is:
 * https://github.com/mbuzdalov/non-dominated-sorting/tree/56fcfc61f5a4009e8ed02c0c3a4b00d390ba6aff
 */
public class NonDominationTree extends NonDominatedSorting {
    private ArraySorter sorter;
    private SplitBuilder splitBuilder;
    private int[] indices;
    private int[] ranks;
    private double[][] transposedPoints;
    private double[][] points;

    private int[] nodeArray;
    private int nNodes;

    public NonDominationTree(int maximumPoints, int maximumDimension) {
        this.sorter = new ArraySorter(maximumPoints);
        this.indices = new int[maximumPoints];
        this.ranks = new int[maximumPoints];
        this.transposedPoints = new double[maximumDimension][maximumPoints];
        this.points = new double[maximumPoints][];
        this.splitBuilder = new SplitBuilder(transposedPoints, maximumPoints, 2);

        // We need to have:
        // - N nodes for roots of layers
        // - N nodes for all tree leaves
        // - N nodes for internal nodes
        // in total 3 * N nodes.
        // We have two cells per node, this is why 6.
        this.nodeArray = new int[maximumPoints * 6];
    }

    @Override
    public int getMaximumPoints() {
        return points.length;
    }

    @Override
    public int getMaximumDimension() {
        return transposedPoints.length;
    }

    private void add(int node, int index, Split split) {
        double[] point = points[index];
        while (true) {
            int v1 = nodeArray[2 * node];
            int v2 = nodeArray[2 * node + 1];
            if (v1 >= 0) {
                if (v1 == 0) {
                    nodeArray[2 * node] = index + 1;
                    break;
                } else if (v2 == 0) {
                    nodeArray[2 * node + 1] = index + 1;
                    break;
                } else {
                    int goodNode = nNodes++;
                    int weakNode = nNodes++;
                    Arrays.fill(nodeArray, 2 * goodNode, 2 * weakNode + 2, 0);
                    int coordinate = split.coordinate;
                    double value = split.value;
                    if (points[v1 - 1][coordinate] < value) {
                        add(goodNode, v1 - 1, split.good);
                    } else {
                        add(weakNode, v1 - 1, split.weak);
                    }
                    if (points[v2 - 1][coordinate] < value) {
                        add(goodNode, v2 - 1, split.good);
                    } else {
                        add(weakNode, v2 - 1, split.weak);
                    }
                    nodeArray[2 * node] = -goodNode - 1;
                    nodeArray[2 * node + 1] = -weakNode - 1;
                    if (point[coordinate] < value) {
                        node = goodNode;
                        split = split.good;
                    } else {
                        node = weakNode;
                        split = split.weak;
                    }
                }
            } else {
                if (point[split.coordinate] < split.value) {
                    node = -v1 - 1;
                    split = split.good;
                } else {
                    node = -v2 - 1;
                    split = split.weak;
                }
            }
        }
    }

    private boolean dominates(int good, int weak) {
        double[] goodPoint = points[good];
        double[] weakPoint = points[weak];
        // objective 0 is not compared since points are presorted.
        for (int o = goodPoint.length - 1; o > 0; --o) {
            if (goodPoint[o] > weakPoint[o]) {
                return false;
            }
        }
        return true;
    }

    private boolean dominates(int node, int index, Split split) {
        int v1 = nodeArray[2 * node];
        int v2 = nodeArray[2 * node + 1];
        if (v1 < 0) {
            // Branching node
            return dominates(-v1 - 1, index, split.good) ||
                    points[index][split.coordinate] >= split.value && dominates(-v2 - 1, index, split.weak);
        } else {
            // Terminal node
            return v1 > 0 && (dominates(v1 - 1, index) || v2 > 0 && dominates(v2 - 1, index));
        }
    }

    @Override
    public void sort(double[][] points, int[] ranks) {
        int n = points.length;
        int dim = points[0].length;
        if (dim == 0) {
            Arrays.fill(ranks, 0);
            return;
        }
        ArrayHelper.fillIdentity(indices, n);
        sorter.lexicographicalSort(points, indices, 0, n, points[0].length);

        if (dim == 1) {
            int currRank = ranks[indices[0]] = 0;
            double prevValue = points[indices[0]][0];
            for (int i = 1; i < n; ++i) {
                int ii = indices[i];
                double currValue = points[ii][0];
                if (prevValue != currValue) {
                    ++currRank;
                    prevValue = currValue;
                }
                ranks[ii] = currRank;
            }
            return;
        }

        int newN = ArraySorter.retainUniquePoints(points, indices, this.points, ranks);
        Arrays.fill(this.ranks, 0, newN, 0);
        Arrays.fill(this.nodeArray, 0, 2 * newN, 0);

        for (int i = 0; i < newN; ++i) {
            for (int j = 0; j < dim; ++j) {
                transposedPoints[j][i] = this.points[i][j];
            }
        }

        Split split = splitBuilder.result(newN, dim);

        int maxRank = 1;
        nNodes = newN;
        add(0, 0, split);
        for (int i = 1; i < newN; ++i) {
            if (dominates(0, i, split)) {
                int left = 0, right = maxRank;
                while (right - left > 1) {
                    int mid = (left + right) >>> 1;
                    if (dominates(mid, i, split)) {
                        left = mid;
                    } else {
                        right = mid;
                    }
                }
                int rank = left + 1;
                this.ranks[i] = rank;
                add(rank, i, split);
                if (rank == maxRank) {
                    ++maxRank;
                }
            } else {
                add(0, i, split);
            }
        }

        for (int i = 0; i < n; ++i) {
            ranks[i] = this.ranks[ranks[i]];
            this.points[i] = null;
        }
    }
}
