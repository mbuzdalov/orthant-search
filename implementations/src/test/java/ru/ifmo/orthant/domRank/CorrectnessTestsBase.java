package ru.ifmo.orthant.domRank;

import java.util.function.BiFunction;

import org.junit.Assert;
import org.junit.Test;

public abstract class CorrectnessTestsBase {
    protected abstract BiFunction<Integer, Integer, DominanceRank> getFactory();

    @Test
    public void testTwoPoints() {
        DominanceRank algorithm = getFactory().apply(2, 4);
        double[][] points = {{ 1, 2, 3, 4 }, { 5, 6, 7, 8 }};
        int[] expectedDominanceRanks = { 0, 1 };
        int[] foundRanks = new int[points.length];
        Assert.assertTrue(points.length <= algorithm.getMaximumPoints());
        Assert.assertTrue(points[0].length <= algorithm.getMaximumDimension());
        algorithm.evaluate(points, foundRanks);
        Assert.assertArrayEquals(expectedDominanceRanks, foundRanks);
    }
}
