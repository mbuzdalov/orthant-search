package ru.ifmo.orthant.domRank;

import java.util.Arrays;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import ru.ifmo.orthant.domRank.impl.NaiveImplementation;
import ru.ifmo.orthant.domRank.impl.OrthantImplementation;
import ru.ifmo.orthant.impl.DivideConquerOrthantSearch;
import ru.ifmo.orthant.impl.NaiveOrthantSearch;

public class DomRankSmokeTest {
    private DominanceRank[] createAlgorithms(int maxPoints, int maxDimension) {
        return new DominanceRank[] {
                new NaiveImplementation(maxPoints, maxDimension),
                new OrthantImplementation(new NaiveOrthantSearch(maxPoints, maxDimension)),
                new OrthantImplementation(new DivideConquerOrthantSearch(maxPoints, maxDimension, false)),
                new OrthantImplementation(new DivideConquerOrthantSearch(maxPoints, maxDimension, true)),
        };
    }

    @Test
    public void smokeTest() {
        Random random = new Random(282354312242L);
        DominanceRank[] algorithms = createAlgorithms(180, 6);

        for (int t = 0; t < 300; ++t) {
            int n = 30 + random.nextInt(150);
            int d = 1 + random.nextInt(6);
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
            int[] naiveCounts = new int[n];
            int[] orthantCounts = new int[n];

            algorithms[0].evaluate(points, naiveCounts);
            for (int i = 1; i < algorithms.length; ++i) {
                Arrays.fill(orthantCounts, 42);
                algorithms[i].evaluate(points, orthantCounts);
                Assert.assertArrayEquals(naiveCounts, orthantCounts);
            }
        }
    }

    @Test
    public void testTwoPoints() {
        DominanceRank[] algorithms = createAlgorithms(2, 4);
        double[][] points = {{ 1, 2, 3, 4 }, { 5, 6, 7, 8 }};
        int[] expectedDominanceRanks = { 0, 1 };
        for (DominanceRank algorithm : algorithms) {
            int[] foundRanks = new int[points.length];
            algorithm.evaluate(points, foundRanks);
            Assert.assertArrayEquals(expectedDominanceRanks, foundRanks);
        }
    }
}
