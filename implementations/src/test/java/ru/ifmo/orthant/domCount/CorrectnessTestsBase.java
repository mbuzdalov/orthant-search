package ru.ifmo.orthant.domCount;

import java.util.function.BiFunction;

import org.junit.Assert;
import org.junit.Test;

public abstract class CorrectnessTestsBase {
    protected abstract BiFunction<Integer, Integer, DominanceCount> getFactory();

    @Test
    public void testTwoPoints() {
        DominanceCount algorithm = getFactory().apply(2, 4);
        double[][] points = {{ 1, 2, 3, 4 }, { 5, 6, 7, 8 }};
        int[] expectedDominanceRanks = { 1, 0 };
        int[] foundRanks = new int[points.length];
        algorithm.evaluate(points, foundRanks);
        Assert.assertArrayEquals(expectedDominanceRanks, foundRanks);
    }
}
