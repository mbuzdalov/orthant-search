package ru.ifmo.orthant.domCount;

import java.util.Arrays;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import ru.ifmo.orthant.DivideConquerOrthantSearch;
import ru.ifmo.orthant.NaiveOrthantSearch;

public class DomCountSmokeTest {
    @Test
    public void smokeTest() {
        Random random = new Random(282354312242L);
        DominanceCount[] algorithms = new DominanceCount[]{
                new NaiveImplementation(678, 6),
                new OrthantImplementation(new NaiveOrthantSearch(678, 6)),
                new OrthantImplementation(new DivideConquerOrthantSearch(678, 6, false, 1)),
                new OrthantImplementation(new DivideConquerOrthantSearch(678, 6, true, 1)),
                new OrthantImplementation(new DivideConquerOrthantSearch(678, 6, false, -1)),
                new OrthantImplementation(new DivideConquerOrthantSearch(678, 6, true, -1)),
        };

        for (int t = 0; t < 300; ++t) {
            int n = 30 + random.nextInt(640);
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
}
