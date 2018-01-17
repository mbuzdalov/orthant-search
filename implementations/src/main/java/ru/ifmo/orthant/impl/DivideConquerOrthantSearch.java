package ru.ifmo.orthant.impl;

import java.util.Arrays;

import ru.ifmo.orthant.OrthantSearch;
import ru.ifmo.orthant.ValueTypeClass;
import ru.ifmo.orthant.util.ArrayHelper;

public final class DivideConquerOrthantSearch extends OrthantSearch {
    private final double[][] transposedPoints;
    private final int[] indices;
    private final int[] lexIndices;
    private final double[] swap;
    private final int[] integerSwap;

    public DivideConquerOrthantSearch(int maxPoints, int maxDimension) {
        transposedPoints = new double[maxDimension][maxPoints];
        indices = new int[maxPoints];
        lexIndices = new int[maxPoints];
        swap = new double[maxPoints];
        integerSwap = new int[maxPoints];
    }

    @Override
    public int getMaximumPoints() {
        return indices.length;
    }

    @Override
    public int getMaximumDimension() {
        return transposedPoints.length;
    }

    @Override
    public int getAdditionalCollectionSize(int nPoints) {
        // additional collection is needed for values in the Fenwick tree.
        // +1 is for the collected result, which will be at the end.
        return nPoints + 1;
    }

    @Override
    protected <T> void runSearchImpl(
            double[][] points, T dataCollection, T queryCollection, int from, int until,
            boolean[] isDataPoint, boolean[] isQueryPoint, T additionalCollection,
            ValueTypeClass<T> typeClass, boolean[] isObjectiveStrict) {
        for (int i = from; i < until; ++i) {
            for (int j = 0; j < points[i].length; ++j) {
                transposedPoints[j][i] = points[i][j];
            }
            indices[i] = i;
        }
        int dimension = points[from].length;

        typeClass.fillWithZeroes(queryCollection, from, until);
        if (dimension == 0) {
            for (int i = from; i < until; ++i) {
                if (isDataPoint[i] && isQueryPoint[i]) {
                    typeClass.queryToData(queryCollection, i, dataCollection, i);
                }
            }
        } else {
            RecursionHandler<T> handler = new RecursionHandler<>(
                    points, transposedPoints, isDataPoint, isQueryPoint,
                    indices, lexIndices, swap, integerSwap,
                    dataCollection, queryCollection, additionalCollection,
                    typeClass, isObjectiveStrict, dimension);

            handler.lexSort(from, until);

            if (dimension == 1) {
                typeClass.fillWithZeroes(additionalCollection, 0, 1);
                for (int i = from, prev = from; i < until; ++i) {
                    int ii = indices[i];
                    if (isQueryPoint[ii]) {
                        int ip = indices[prev];
                        while (lexIndices[ip] < lexIndices[ii]) {
                            if (isDataPoint[ip]) {
                                typeClass.add(dataCollection, ip, additionalCollection, 0);
                            }
                            ip = indices[++prev];
                        }
                        typeClass.add(additionalCollection, 0, queryCollection, ii);
                        if (isDataPoint[ii]) {
                            typeClass.queryToData(queryCollection, ii, dataCollection, ii);
                        }
                    }
                }
            } else {
                handler.solve(from, until);
            }
        }
    }

    private static class RecursionHandler<T> {
        private final double[][] points;
        private final double[][] transposedPoints;
        private final boolean[] isDataPoint;
        private final boolean[] isQueryPoint;
        private final double[] swap;
        private final int[] integerSwap;
        private final int[] indices;
        private final int[] lexIndices;
        private final T dataCollection;
        private final T queryCollection;
        private final T additionalCollection;
        private final ValueTypeClass<T> typeClass;
        private final boolean[] isObjectiveStrict;
        private final int dimension;

        private boolean allPointsAreQueryPoints;
        private boolean allPointsAreDataPoints;

        private RecursionHandler(
                double[][] points, double[][] transposedPoints, boolean[] isDataPoint, boolean[] isQueryPoint,
                int[] indices, int[] lexIndices, double[] swap, int[] integerSwap,
                T dataCollection, T queryCollection, T additionalCollection,
                ValueTypeClass<T> typeClass, boolean[] isObjectiveStrict, int dimension) {
            this.points = points;
            this.transposedPoints = transposedPoints;
            this.isDataPoint = isDataPoint;
            this.isQueryPoint = isQueryPoint;
            this.indices = indices;
            this.lexIndices = lexIndices;
            this.swap = swap;
            this.integerSwap = integerSwap;
            this.dataCollection = dataCollection;
            this.queryCollection = queryCollection;
            this.additionalCollection = additionalCollection;
            this.typeClass = typeClass;
            this.isObjectiveStrict = isObjectiveStrict;
            this.dimension = dimension;
        }

        void solve(int from, int until) {
            boolean hasNonQueryPoint = false;
            boolean hasNonDataPoint = false;
            for (int i = from; i < until; ++i) {
                hasNonDataPoint |= !isDataPoint[i];
                hasNonQueryPoint |= !isQueryPoint[i];
            }
            this.allPointsAreDataPoints = !hasNonDataPoint;
            this.allPointsAreQueryPoints = !hasNonQueryPoint;
            helperA(from, until, dimension - 1);
        }

        // Assumption: for points P and Q at indices i < j,
        // there is no contradiction that P can dominate Q in objectives [d + 1; dimension)
        void helperA(int from, int until, int d) {
            if (from < until) {
                if (from + 1 == until) {
                    // A single point needs to be processed. This means it is complete.
                    completePoint(indices[from]);
                } else if (from + 2 == until) {
                    int firstIndex = indices[from], secondIndex = indices[from + 1];
                    completePoint(firstIndex);
                    if (isDataPoint[firstIndex] && isQueryPoint[secondIndex]) {
                        addIfDominates(firstIndex, secondIndex, d);
                    }
                    completePoint(secondIndex);
                } else if (d == 1) {
                    sweepA(from, until);
                } else {
                    double[] obj = transposedPoints[d];
                    ArrayHelper.transplant(obj, indices, from, until, swap, from);
                    double min = ArrayHelper.min(swap, from, until);
                    double max = ArrayHelper.max(swap, from, until);
                    if (min == max) {
                        if (!isObjectiveStrict[d]) {
                            helperA(from, until, d - 1);
                        } else {
                            completeRangeOfPoints(from, until);
                        }
                    } else {
                        double median = ArrayHelper.destructiveMedian(swap, from, until);
                        long splitResult = ArrayHelper.splitInThree(indices, obj, from, until, median, integerSwap);
                        int middleStart = (int) (splitResult >>> 32);
                        int greaterStart = (int) (splitResult);

                        helperA(from, middleStart, d);
                        helperBAdapter(from, middleStart, middleStart, greaterStart, d - 1);
                        if (!isObjectiveStrict[d]) {
                            helperA(middleStart, greaterStart, d - 1);
                        } else {
                            completeRangeOfPoints(middleStart, greaterStart);
                        }
                        ArrayHelper.merge(indices, lexIndices, from, middleStart, greaterStart, integerSwap);
                        helperBAdapter(from, greaterStart, greaterStart, until, d - 1);
                        helperA(greaterStart, until, d);
                        ArrayHelper.merge(indices, lexIndices, from, greaterStart, until, integerSwap);
                    }
                }
            }
        }

        // Assumption: for any good point P and any weak point Q,
        // there is no contradiction that P can dominate Q in objectives [d + 1; dimension).
        // We ensure that only data points remain among good ones, and only query points remain among weak ones.
        void helperBAdapter(int goodFrom, int goodUntil, int weakFrom, int weakUntil, int d) {
            int newGoodUntil = allPointsAreDataPoints
                    ? goodUntil
                    : ArrayHelper.filter(indices, isDataPoint, goodFrom, goodUntil, integerSwap);
            int newWeakUntil = allPointsAreQueryPoints
                    ? weakUntil
                    : ArrayHelper.filter(indices, isQueryPoint, weakFrom, weakUntil, integerSwap);
            helperB(goodFrom, newGoodUntil, weakFrom, newWeakUntil, d);
            if (goodUntil != newGoodUntil) {
                ArrayHelper.merge(indices, lexIndices, goodFrom, newGoodUntil, goodUntil, integerSwap);
            }
            if (weakUntil != newWeakUntil) {
                ArrayHelper.merge(indices, lexIndices, weakFrom, newWeakUntil, weakUntil, integerSwap);
            }
        }

        // Assumption: for any good point P and any weak point Q,
        // there is no contradiction that P can dominate Q in objectives [d + 1; dimension).
        // Additionally, all good points are data points, and all weak points are query points.
        void helperB(int goodFrom, int goodUntil, int weakFrom, int weakUntil, int d) {
            if (goodFrom < goodUntil && weakFrom < weakUntil) {
                if (goodFrom + 1 == goodUntil) {
                    helperBGood1(goodFrom, weakFrom, weakUntil, d);
                } else if (weakFrom + 1 == weakUntil) {
                    helperBWeak1(goodFrom, goodUntil, weakFrom, d);
                } else if (d == 1) {
                    sweepB(goodFrom, goodUntil, weakFrom, weakUntil);
                } else {
                    double[] obj = transposedPoints[d];
                    boolean isStrict = isObjectiveStrict[d];
                    ArrayHelper.transplant(obj, indices, goodFrom, goodUntil, swap, goodFrom);
                    double goodMin = ArrayHelper.min(swap, goodFrom, goodUntil);
                    double goodMax = ArrayHelper.max(swap, goodFrom, goodUntil);
                    ArrayHelper.transplant(obj, indices, weakFrom, weakUntil, swap, goodUntil);
                    double weakMin = ArrayHelper.min(swap, goodUntil, goodUntil + weakUntil - weakFrom);
                    double weakMax = ArrayHelper.max(swap, goodUntil, goodUntil + weakUntil - weakFrom);

                    if (isStrict && goodMin >= weakMax || !isStrict && goodMin > weakMax) {
                        // no good can dominate any weak
                        return;
                    }
                    if (isStrict && goodMax < weakMin || !isStrict && goodMax <= weakMin) {
                        // in coordinate d, every good dominate every weak
                        helperB(goodFrom, goodUntil, weakFrom, weakUntil, d - 1);
                    } else {
                        double median = ArrayHelper.destructiveMedian(swap, goodFrom, goodUntil + weakUntil - weakFrom);

                        long goodSplit = ArrayHelper.splitInThree(indices, obj, goodFrom, goodUntil, median, integerSwap);
                        int goodMiddleStart = (int) (goodSplit >>> 32);
                        int goodGreaterStart = (int) (goodSplit);

                        long weakSplit = ArrayHelper.splitInThree(indices, obj, weakFrom, weakUntil, median, integerSwap);
                        int weakMiddleStart = (int) (weakSplit >>> 32);
                        int weakGreaterStart = (int) (weakSplit);

                        // Heavy sub-problems
                        helperB(goodFrom, goodMiddleStart, weakFrom, weakMiddleStart, d);
                        helperB(goodGreaterStart, goodUntil, weakGreaterStart, weakUntil, d);

                        // Guaranteed light sub-problems
                        helperB(goodFrom, goodMiddleStart, weakMiddleStart, weakGreaterStart, d - 1);
                        helperB(goodFrom, goodMiddleStart, weakGreaterStart, weakUntil, d - 1);
                        helperB(goodMiddleStart, goodGreaterStart, weakGreaterStart, weakUntil, d - 1);

                        if (!isStrict) {
                            helperB(goodMiddleStart, goodGreaterStart, weakMiddleStart, weakGreaterStart, d - 1);
                        }

                        ArrayHelper.merge(indices, lexIndices, goodFrom, goodMiddleStart, goodGreaterStart, integerSwap);
                        ArrayHelper.merge(indices, lexIndices, weakFrom, weakMiddleStart, weakGreaterStart, integerSwap);
                        ArrayHelper.merge(indices, lexIndices, goodFrom, goodGreaterStart, goodUntil, integerSwap);
                        ArrayHelper.merge(indices, lexIndices, weakFrom, weakGreaterStart, weakUntil, integerSwap);
                    }
                }
            }
        }

        void helperBGood1(int good, int weakFrom, int weakUntil, int d) {
            int gi = indices[good];
            for (int i = weakFrom; i < weakUntil; ++i) {
                addIfDominates(gi, indices[i], d);
            }
        }

        void helperBWeak1(int goodFrom, int goodUntil, int weak, int d) {
            int wi = indices[weak];
            for (int i = goodFrom; i < goodUntil; ++i) {
                addIfDominates(indices[i], wi, d);
            }
        }

        void sweepA(int from, int until) {
            double[] local = transposedPoints[1];
            int fwSize = initFenwickKeysInSwap(local, from, until);
            typeClass.fillWithZeroes(additionalCollection, 0, fwSize);

            int last = from;
            int lastIndex = indices[last];
            if (isObjectiveStrict[0]) {
                double[] obj0 = transposedPoints[0];
                for (int i = from; i < until; ++i) {
                    int currIndex = indices[i];
                    if (isQueryPoint[currIndex]) {
                        while (obj0[lastIndex] < obj0[currIndex]) {
                            if (isDataPoint[lastIndex]) {
                                fenwickSet(fwSize, local[lastIndex], lastIndex);
                            }
                            lastIndex = indices[++last];
                        }
                        fenwickQuery(fwSize, local[currIndex]);
                        typeClass.add(additionalCollection, fwSize, queryCollection, currIndex);
                        completePoint(currIndex);
                    }
                }
            } else {
                for (int i = from; i < until; ++i) {
                    int currIndex = indices[i];
                    if (isQueryPoint[currIndex]) {
                        while (lexIndices[lastIndex] < lexIndices[currIndex]) {
                            if (isDataPoint[lastIndex]) {
                                fenwickSet(fwSize, local[lastIndex], lastIndex);
                            }
                            lastIndex = indices[++last];
                        }
                        fenwickQuery(fwSize, local[currIndex]);
                        typeClass.add(additionalCollection, fwSize, queryCollection, currIndex);
                        completePoint(currIndex);
                    }
                }
            }
        }

        void sweepB(int goodFrom, int goodUntil, int weakFrom, int weakUntil) {
            double[] local = transposedPoints[1];
            int fwSize = initFenwickKeysInSwap(local, goodFrom, goodUntil);
            typeClass.fillWithZeroes(additionalCollection, 0, fwSize);

            int last = goodFrom;
            int lastIndex = indices[last];
            if (isObjectiveStrict[0]) {
                double[] obj0 = transposedPoints[0];
                for (int i = weakFrom; i < weakUntil; ++i) {
                    int currIndex = indices[i];
                    while (last < goodUntil && obj0[lastIndex] < obj0[currIndex]) {
                        fenwickSet(fwSize, local[lastIndex], lastIndex);
                        lastIndex = indices[++last]; // this may ask for indices[goodUntil], but that's safe
                    }
                    fenwickQuery(fwSize, local[currIndex]);
                    typeClass.add(additionalCollection, fwSize, queryCollection, currIndex);
                }
            } else {
                for (int i = weakFrom; i < weakUntil; ++i) {
                    int currIndex = indices[i];
                    while (last < goodUntil && lexIndices[lastIndex] < lexIndices[currIndex]) {
                        fenwickSet(fwSize, local[lastIndex], lastIndex);
                        lastIndex = indices[++last]; // this may ask for indices[goodUntil], but that's safe
                    }
                    fenwickQuery(fwSize, local[currIndex]);
                    typeClass.add(additionalCollection, fwSize, queryCollection, currIndex);
                }
            }
        }

        void fenwickQuery(int fwSize, double key) {
            typeClass.fillWithZeroes(additionalCollection, fwSize, fwSize + 1);
            int keyIndex = Arrays.binarySearch(swap, 0, fwSize, key);
            if (keyIndex == -1) {
                // this means the key would be inserted before the array.
                // In turn, this means that "zero" is the right answer.
                return;
            }
            if (keyIndex < 0) {
                // -keyIndex - 1 is the place where key would be inserted.
                // We need one less.
                keyIndex = -keyIndex - 2;
            } else if (isObjectiveStrict[1]) {
                // We have a perfect match, but the objective is strict, so one less.
                --keyIndex;
            }
            while (keyIndex >= 0) {
                typeClass.add(additionalCollection, keyIndex, additionalCollection, fwSize);
                keyIndex = (keyIndex & (keyIndex + 1)) - 1;
            }
        }

        void fenwickSet(int fwSize, double key, int valueIndex) {
            int keyIndex = Arrays.binarySearch(swap, 0, fwSize, key);
            if (keyIndex < 0) {
                throw new AssertionError();
            }
            while (keyIndex < fwSize) {
                typeClass.add(dataCollection, valueIndex, additionalCollection, keyIndex);
                keyIndex |= keyIndex + 1;
            }
        }

        int initFenwickKeysInSwap(double[] keys, int from, int until) {
            for (int i = from; i < until; ++i) {
                swap[i - from] = keys[indices[i]];
            }
            int sz = until - from;
            Arrays.sort(swap, 0, sz);
            int realSize = 1;
            double last = swap[0];
            for (int i = 1; i < sz; ++i) {
                if (swap[i] != last) {
                    swap[realSize++] = last = swap[i];
                }
            }
            return realSize;
        }

        void completeRangeOfPoints(int from, int until) {
            for (int i = from; i < until; ++i) {
                completePoint(indices[i]);
            }
        }

        void completePoint(int index) {
            // Action is needed only if it is both a data point and a query point.
            if (isDataPoint[index] && isQueryPoint[index]) {
                typeClass.queryToData(queryCollection, index, dataCollection, index);
            }
        }

        // Assumes that there is no contradiction to domination in higher objectives, e.g. [maxObjective + 1; dimension)
        void addIfDominates(int goodIndex, int weakIndex, int maxObjective) {
            // If goodIndex comes lexicographically later than weakIndex, then goodIndex cannot dominate weakIndex.
            // Equal points cannot dominate each other by definition.
            if (lexIndices[goodIndex] < lexIndices[weakIndex]
                    && typeClass.targetChangesOnAdd(dataCollection, goodIndex, queryCollection, weakIndex)) {
                double[] goodPoint = points[goodIndex];
                double[] weakPoint = points[weakIndex];
                for (int i = maxObjective; i >= 0; --i) {
                    double goodValue = goodPoint[i];
                    double weakValue = weakPoint[i];
                    if (isObjectiveStrict[i] ? goodValue >= weakValue : goodValue > weakValue) {
                        return;
                    }
                }
                typeClass.add(dataCollection, goodIndex, queryCollection, weakIndex);
            }
        }

        void lexSort(int from, int until) {
            lexSortImpl(from, until, 0);
            int value = 0;
            int prevIndex = indices[from];
            lexIndices[prevIndex] = value;
            for (int i = from + 1; i < until; ++i) {
                int currIndex = indices[i];
                if (!Arrays.equals(points[prevIndex], points[currIndex])) {
                    value += 1;
                    prevIndex = currIndex;
                }
                lexIndices[currIndex] = value;
            }
        }

        void lexSortImpl(int from, int until, int d) {
            double[] local = transposedPoints[d];
            sortIndicesBy(local, from, until);
            if (d + 1 < dimension) {
                int last = from;
                double lastV = local[indices[last]];
                for (int i = from + 1; i < until; ++i) {
                    double currV = local[indices[i]];
                    if (currV != lastV) {
                        if (last + 1 < i) {
                            lexSortImpl(last, i, d + 1);
                        }
                        last = i;
                        lastV = currV;
                    }
                }
                if (last + 1 < until) {
                    lexSortImpl(last, until, d + 1);
                }
            }
        }

        void sortIndicesBy(double[] values, int from, int until) {
            if (from + 1 < until) {
                int l = from, r = until - 1;
                double pivot = values[indices[(l + r) >>> 1]];
                while (l <= r) {
                    while (values[indices[l]] < pivot) ++l;
                    while (values[indices[r]] > pivot) --r;
                    if (l <= r) {
                        int tmp = indices[l];
                        indices[l] = indices[r];
                        indices[r] = tmp;
                        ++l;
                        --r;
                    }
                }
                if (from < r) {
                    sortIndicesBy(values, from, r + 1);
                }
                if (l + 1 < until) {
                    sortIndicesBy(values, l, until);
                }
            }
        }
    }
}
