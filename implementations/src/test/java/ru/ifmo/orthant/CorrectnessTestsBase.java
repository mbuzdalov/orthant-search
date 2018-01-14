package ru.ifmo.orthant;

import java.util.Arrays;
import java.util.function.BiFunction;

import org.junit.Test;

public abstract class CorrectnessTestsBase {
    protected abstract BiFunction<Integer, Integer, OrthantSearch> getFactory();

    @Test
    public void smokeTest() {

    }

    private static class SumProduct extends ValueTypeClass<int[]> {

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
            target[targetIndex] *= source[sourceIndex];
        }
    }
}
