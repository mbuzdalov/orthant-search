package ru.ifmo.orthant;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public final class PointSets {
    private PointSets() {}
    private static final int N_INSTANCES = 10;

    public static double[][][] generateUniformHypercube(int n, int d) {
        Random random = new Random(n * 63142311L + d * 55182243);
        double[][][] rv = new double[N_INSTANCES][n][d];
        for (int x = 0; x < N_INSTANCES; ++x) {
            boolean isDiscrete = x % 2 == 0;
            for (int i = 0; i < n; ++i) {
                for (int j = 0; j < d; ++j) {
                    rv[x][i][j] = isDiscrete ? random.nextInt(10) : random.nextDouble();
                }
            }
        }
        return rv;
    }

    public static double[][][] generateUniformHyperplanes(int n, int d, int f) {
        Random random = new Random(n * 772983252L + d * 52462563);
        int frontSize = n / f;
        int firstFrontSize = n - (f - 1) * frontSize;
        double[][][] rv = new double[N_INSTANCES][n][d];
        for (int x = 0; x < N_INSTANCES; ++x) {
            for (int i = 0; i < firstFrontSize; ++i) {
                double sum = 1.0;
                for (int j = d - 1; j > 0; --j) {
                    rv[x][i][j] = sum * (1 - Math.pow(1 - random.nextDouble(), 1.0 / j));
                    sum -= rv[x][i][j];
                }
                rv[x][i][0] = sum;
            }
            for (int i = firstFrontSize; i < n; ++i) {
                System.arraycopy(rv[x][i - frontSize], 0, rv[x][i], 0, d);
                for (int j = 0; j < d; ++j) {
                    rv[x][i][j] += 1e-9;
                }
            }
            Collections.shuffle(Arrays.asList(rv), random);
        }
        return rv;
    }
}
