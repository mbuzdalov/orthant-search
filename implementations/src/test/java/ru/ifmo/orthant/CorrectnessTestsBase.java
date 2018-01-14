package ru.ifmo.orthant;

import java.util.Arrays;
import java.util.Random;
import java.util.function.BiFunction;

import org.junit.Assert;
import org.junit.Test;

public abstract class CorrectnessTestsBase {
    protected abstract BiFunction<Integer, Integer, OrthantSearch> getFactory();

    @Test
    public void smokeTest() {
        Random random = new Random(282354312242L);
        ValueTypeClass<int[]> tc = new SumTypeClass();
        OrthantSearch orthantSearch = getFactory().apply(200, 10);
        int[] additionalCollection = tc.createCollection(orthantSearch.getAdditionalCollectionSize(orthantSearch.getMaximumPoints()));

        for (int t = 0; t < 300; ++t) {
            int n = 130 + random.nextInt(50);
            int d = 2 + random.nextInt(5);
            double[][] points = new double[n][d];
            if (random.nextBoolean()) {
                for (int i = 0; i < n; ++i) {
                    for (int j = 0; j < d; ++j) {
                        points[i][j] = random.nextDouble();
                    }
                }
            } else {
                for (int i = 0; i < n; ++i) {
                    for (int j = 0; j < d; ++j) {
                        points[i][j] = random.nextInt(5);
                    }
                }
            }
            int[] dataValues = new int[n];
            int[] queryValues = new int[n];
            int[] expectedQueryValues = new int[n];
            boolean[] isQueryPoint = new boolean[n];
            boolean[] isDataPoint = new boolean[n];
            for (int i = 0; i < n; ++i) {
                isDataPoint[i] = random.nextBoolean();
                isQueryPoint[i] = random.nextBoolean();
                dataValues[i] = random.nextInt();
            }
            boolean[] isStrict = new boolean[d];
            for (int i = 0; i < d; ++i) {
                isStrict[i] = random.nextBoolean();
            }

            orthantSearch.runSearch(points, dataValues.clone(), queryValues, 0, n,
                    isDataPoint, isQueryPoint, additionalCollection, tc, isStrict);
            runSearch(points, dataValues.clone(), expectedQueryValues, isDataPoint, isQueryPoint, isStrict);
            Assert.assertArrayEquals(expectedQueryValues, queryValues);
        }
    }

    private static boolean dominates(double[] good, double[] weak, boolean[] isStrict) {
        if (Arrays.equals(good, weak)) {
            return false;
        }
        int d = good.length;
        for (int i = 0; i < d; ++i) {
            if (isStrict[i] ? good[i] >= weak[i] : good[i] > weak[i]) {
                return false;
            }
        }
        return true;
    }

    private static void runSearch(double[][] points, int[] dataValues, int[] queryValues,
                                  boolean[] isDataPoint, boolean[] isQueryPoint, boolean[] isStrict) {
        int n = points.length;
        PointWrapper[] wrappers = new PointWrapper[n];
        for (int i = 0; i < n; ++i) {
            wrappers[i] = new PointWrapper(i, points[i]);
        }
        Arrays.sort(wrappers);
        Arrays.fill(queryValues, 0);
        for (int q = 0; q < n; ++q) {
            PointWrapper wq = wrappers[q];
            int qi = wq.index;
            if (isQueryPoint[qi]) {
                for (int p = 0; p < q; ++p) {
                    PointWrapper wp = wrappers[p];
                    int pi = wp.index;
                    if (isDataPoint[pi] && dominates(wp.point, wq.point, isStrict)) {
                        queryValues[qi] += dataValues[pi];
                    }
                }
                if (isDataPoint[qi]) {
                    dataValues[qi] += queryValues[qi];
                }
            }
        }
    }

    private static class SumTypeClass extends ValueTypeClass<int[]> {
        @Override
        public int[] createCollection(int size) {
            return new int[size];
        }

        @Override
        public int size(int[] collection) {
            return collection.length;
        }

        @Override
        public void fillWithZeroes(int[] collection, int from, int until) {
            Arrays.fill(collection, from, until, 0);
        }

        @Override
        public void add(int[] source, int sourceIndex, int[] target, int targetIndex) {
            target[targetIndex] += source[sourceIndex];
        }

        @Override
        public void queryToData(int[] source, int sourceIndex, int[] target, int targetIndex) {
            target[targetIndex] += source[sourceIndex];
        }
    }

    private static class PointWrapper implements Comparable<PointWrapper> {
        private int index;
        private double[] point;

        private PointWrapper(int index, double[] point) {
            this.index = index;
            this.point = point;
        }

        @Override
        public int compareTo(PointWrapper o) {
            double[] l = point, r = o.point;
            int d = l.length;
            for (int i = 0; i < d; ++i) {
                int cmp = Double.compare(l[i], r[i]);
                if (cmp != 0) {
                    return cmp;
                }
            }
            return 0;
        }
    }
}
