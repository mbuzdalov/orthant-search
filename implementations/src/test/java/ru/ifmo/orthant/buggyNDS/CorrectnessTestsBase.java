package ru.ifmo.orthant.buggyNDS;

import java.util.*;
import java.util.function.BiFunction;

import org.junit.Assert;
import org.junit.Test;

public abstract class CorrectnessTestsBase {
    protected abstract BiFunction<Integer, Integer, BuggyNonDominatedSorting> getFactory();

    private void check(int[][] input, int[] expectedOutput) {
        double[][] doubleInput = new double[input.length][];
        for (int i = 0; i < input.length; ++i) {
            doubleInput[i] = new double[input[i].length];
            for (int j = 0; j < input[i].length; ++j) {
                doubleInput[i][j] = input[i][j];
            }
        }
        check(doubleInput, expectedOutput);
    }

    private void check(double[][] input, int[] expectedOutput) {
        expectedOutput = expectedOutput.clone();
        BiFunction<Integer, Integer, BuggyNonDominatedSorting> factory = getFactory();

        {
            BuggyNonDominatedSorting sorting = factory.apply(input.length, input[0].length);
            int[] actualOutput = new int[expectedOutput.length];
            sorting.sort(input, actualOutput);
            Assert.assertArrayEquals(expectedOutput, actualOutput);
            Arrays.fill(actualOutput, 2347);
            sorting.sort(input, actualOutput);
            Assert.assertArrayEquals(expectedOutput, actualOutput);
        }
        {
            BuggyNonDominatedSorting sorting = factory.apply(input.length + 71, input[0].length + 2);
            int[] actualOutput = new int[expectedOutput.length];
            sorting.sort(input, actualOutput);
            Assert.assertArrayEquals(expectedOutput, actualOutput);
            Arrays.fill(actualOutput, 2347);
            sorting.sort(input, actualOutput);
            Assert.assertArrayEquals(expectedOutput, actualOutput);
        }
    }

    private int[][] concat(int[][] a, int[][] b) {
        int[][] rv = new int[a.length + b.length][];
        for (int i = 0; i < a.length; ++i) {
            rv[i] = a[i].clone();
        }
        for (int i = 0; i < b.length; ++i) {
            rv[a.length + i] = b[i].clone();
        }
        return rv;
    }

    private int[] concat(int[] a, int[] b) {
        int[] rv = new int[a.length + b.length];
        System.arraycopy(a, 0, rv, 0, a.length);
        System.arraycopy(b, 0, rv, a.length, b.length);
        return rv;
    }

    private int[][] fill(int rows, int cols, int value) {
        int[][] rv = new int[rows][cols];
        for (int[] a : rv) {
            Arrays.fill(a, value);
        }
        return rv;
    }

    private int[][] generateHypercube(int dim, int size) {
        if (dim == 1) {
            int[][] rv = new int[size][dim];
            for (int i = 0; i < size; ++i) {
                rv[i] = new int[] {i};
            }
            return rv;
        } else {
            int[][] rec = generateHypercube(dim - 1, size);
            int[][] rv = new int[size * rec.length][];
            for (int i = 0; i < rec.length; ++i) {
                for (int j = 0; j < size; ++j) {
                    int[] newValue = new int[dim];
                    System.arraycopy(rec[i], 0, newValue, 0, dim - 1);
                    newValue[dim - 1] = j;
                    rv[i * size + j] = newValue;
                }
            }
            return rv;
        }
    }

    private int[] hypercubeRanks(int[][] hypercube, int offset, int multiple) {
        int[] rv = new int[hypercube.length];
        for (int i = 0; i < hypercube.length; ++i) {
            for (int j = 0; j < hypercube[i].length; ++j) {
                rv[i] += hypercube[i][j];
            }
            rv[i] = rv[i] * multiple + offset;
        }
        return rv;
    }

    private void hypercubeImpl(int dim, int size) {
        Random r = new Random(8352762);
        for (int multiple = 1; multiple <= 4; ++multiple) {
            int[][] test = null;
            int[] ranks = null;
            for (int t = 0; t < multiple; ++t) {
                int[][] hc = generateHypercube(dim, size);
                Collections.shuffle(Arrays.asList(hc), r);
                int[] hr = hypercubeRanks(hc, t, multiple);
                if (test == null) {
                    test = hc;
                    ranks = hr;
                } else {
                    test = concat(test, hc);
                    ranks = concat(ranks, hr);
                }
            }
            check(test, ranks);
        }
    }

    @Test
    public void maximumBoundariesAreSane() {
        Random random = new Random(325465);
        for (int i = 0; i < 100; ++i) {
            int maxPoints = 1 + random.nextInt(100);
            int maxDimension = 1 + random.nextInt(20);
            BuggyNonDominatedSorting s = getFactory().apply(maxPoints, maxDimension);
            Assert.assertEquals(maxPoints, s.getMaximumPoints());
            Assert.assertEquals(maxDimension, s.getMaximumDimension());
        }
    }

    @Test
    public void doesNotShareState() {
        BuggyNonDominatedSorting sorting = getFactory().apply(2, 3);
        double[][] nonDominated = {{1, 0, 0}, {0, 1, 0}};
        double[][] dominated = {{0, 0, 0}, {1, 1, 1}};
        int[] output = new int[2];
        for (int t = 0; t < 10; ++t) {
            sorting.sort(nonDominated, output);
            Assert.assertEquals(0, output[0]);
            Assert.assertEquals(0, output[1]);
            sorting.sort(dominated, output);
            Assert.assertEquals(0, output[0]);
            Assert.assertEquals(1, output[1]);
        }
    }

    @Test
    public void single1D() {
        check(fill(1, 1, 239), new int[] {0});
        check(fill(2, 1, 239), new int[] {0, 1});
        check(fill(3, 1, 239), new int[] {0, 1, 2});
        check(fill(4, 1, 239), new int[] {0, 1, 2, 3});
    }

    @Test
    public void single100D() {
        check(fill(1, 100, 239), new int[] {0});
        check(fill(2, 100, 239), new int[] {0, 1});
        check(fill(3, 100, 239), new int[] {0, 1, 2});
        check(fill(4, 100, 239), new int[] {0, 1, 2, 3});
    }

    @Test
    public void single0D() {
        check(fill(1, 0, 239), new int[] {0});
        check(fill(2, 0, 239), new int[] {0, 1});
        check(fill(3, 0, 239), new int[] {0, 1, 2});
        check(fill(4, 0, 239), new int[] {0, 1, 2, 3});
    }

    @Test
    public void two1DUnequalDecreasing() {
        check(new int[][] {{2}, {1}}, new int[] {1, 0});
    }

    @Test
    public void two1DUnequalIncreasing() {
        check(new int[][] {{1}, {2}}, new int[] {0, 1});
    }

    @Test
    public void two1DEqual() {
        check(new int[][] {{2}, {2}}, new int[] {0, 1});
    }

    @Test
    public void many1DOfTwoTypes() {
        check(new int[][] {{2}, {1}, {1}, {2}, {1}}, new int[] {3, 0, 1, 4, 2});
    }

    @Test
    public void two2DUnequalIncomparable() {
        check(new int[][] {{1, 2}, {2, 1}}, new int[] {0, 0});
    }

    @Test
    public void two2DUnequalIncomparableWithDuplicates() {
        check(new int[][] {{1, 2}, {2, 1}, {2, 1}, {1, 2}}, new int[] {0, 0, 1, 1});
    }

    @Test
    public void two2DUnequalIncreasing() {
        check(new int[][] {{1, 1}, {2, 2}}, new int[] {0, 1});
    }

    @Test
    public void two2DUnequalDecreasing() {
        check(new int[][] {{2, 2}, {1, 1}}, new int[] {1, 0});
    }

    @Test
    public void two2DUnequalWithDominanceAndDuplicates() {
        check(new int[][] {{1, 1}, {2, 2}, {1, 1}}, new int[] {0, 2, 1});
        check(new int[][] {{2, 2}, {1, 1}, {2, 2}, {1, 1}}, new int[] {2, 0, 3, 1});
    }

    @Test
    public void twoGroupsOfManyEqualPoints() {
        int[] ranks = new int[20];
        for (int i = 0; i < 20; ++i) {
            ranks[i] = i;
        }
        check(concat(fill(10, 10, 11110), fill(10, 10, 11111)), ranks);
    }

    @Test
    public void twoGroupsOfManyEqualPointsReversed() {
        int[] ranks = new int[20];
        for (int i = 0; i < 20; ++i) {
            ranks[i] = (i + 10) % 20;
        }
        check(concat(fill(10, 10, 11111), fill(10, 10, 11110)), ranks);
    }

    @Test
    public void hypercube2D() {
        hypercubeImpl(2, 10);
    }

    @Test
    public void hypercube3D() {
        hypercubeImpl(3, 8);
    }

    @Test
    public void hypercube4D() {
        hypercubeImpl(4, 5);
    }

    @Test
    public void hypercube5D() {
        hypercubeImpl(5, 4);
    }

    @Test
    public void hypercube6D() {
        hypercubeImpl(6, 3);
    }

    private void hyperplaneImpl(int dimension) {
        Random r = new Random(346357);
        for (int multiple = 1; multiple <= 4; ++multiple) {
            int[][] data = new int[100][dimension];
            for (int i = 0; i < data.length; ++i) {
                data[i][0] = 1000000000;
                for (int j = 1; j < data[i].length; ++j) {
                    data[i][j] = r.nextInt(10000000);
                    data[i][0] -= data[i][j];
                }
            }
            int[][] realData = data;
            for (int t = 1; t < multiple; ++t) {
                realData = concat(realData, data);
            }
            int[] ranks = new int[100 * multiple];
            for (int i = 0; i < ranks.length; ++i) {
                ranks[i] = i / 100;
            }
            check(realData, ranks);
        }
    }

    @Test
    public void singleMeaningfulRow() {
        int n = 100, d = 5;
        int[][] data = new int[n][d];
        int[] ranks = new int[n];
        for (int i = 0; i < d; ++i) {
            for (int j = 0; j < n; ++j) {
                data[j][i] = j;
                ranks[j] = j;
            }
            check(data, ranks);
            for (int j = 0; j < n; ++j) {
                data[j][i] = 0;
            }
        }
    }

    @Test
    public void hyperplane2D() {
        hyperplaneImpl(2);
    }

    @Test
    public void hyperplane3D() {
        hyperplaneImpl(3);
    }

    @Test
    public void hyperplane4D() {
        hyperplaneImpl(4);
    }

    @Test
    public void hyperplane5D() {
        hyperplaneImpl(5);
    }
}
