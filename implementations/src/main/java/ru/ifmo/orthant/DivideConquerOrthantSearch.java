package ru.ifmo.orthant;

import java.util.Arrays;

import ru.ifmo.orthant.util.ArrayHelper;
import ru.ifmo.orthant.util.SplitMergeHelper;

public final class DivideConquerOrthantSearch extends OrthantSearch {
    private final double[][] transposedPoints;
    private final int[] indices;
    private final int[] lexIndices;
    private final double[] swap;
    private final int threshold3D, thresholdAll;
    private final SplitMergeHelper helper;

    public DivideConquerOrthantSearch(int maxPoints, int maxDimension, boolean useThreshold) {
        transposedPoints = new double[maxDimension][maxPoints];
        indices = new int[maxPoints];
        lexIndices = new int[maxPoints];
        swap = new double[maxPoints];
        helper = new SplitMergeHelper(maxPoints);
        threshold3D = useThreshold ? 50 : 0;
        thresholdAll = useThreshold ? 100 : 0;
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
            double[][] points, T dataCollection, T queryCollection, int from, int until, int dimension,
            boolean[] isDataPoint, boolean[] isQueryPoint, T additionalCollection,
            ValueTypeClass<T> typeClass, boolean[] isObjectiveStrict) {
        for (int i = from; i < until; ++i) {
            for (int j = 0; j < points[i].length; ++j) {
                transposedPoints[j][i] = points[i][j];
            }
            indices[i] = i;
        }

        typeClass.fillWithZeroes(queryCollection, from, until);

        if (dimension == 0) {
            for (int i = from; i < until; ++i) {
                if (isQueryPoint[i]) {
                    typeClass.queryToData(queryCollection, i, dataCollection);
                }
            }
        } else {
            RecursionHandler<T> handler = new RecursionHandler<>(
                    points, transposedPoints, isDataPoint, isQueryPoint,
                    indices, lexIndices, swap, helper,
                    dataCollection, queryCollection, additionalCollection,
                    typeClass, isObjectiveStrict, dimension,
                    threshold3D, thresholdAll);

            handler.lexSort(from, until);

            if (dimension == 1) {
                // isObjectiveStrict[0] does not matter here, since we have only one dimension.
                typeClass.fillWithZeroes(additionalCollection, 0, 1);
                for (int i = from, prev = from; i < until; ++i) {
                    int ii = indices[i];
                    int lii = lexIndices[ii];
                    if (isQueryPoint[ii]) {
                        int ip = indices[prev];
                        while (lexIndices[ip] < lii) {
                            if (isDataPoint[ip]) {
                                typeClass.add(dataCollection, ip, additionalCollection, 0);
                            }
                            ip = indices[++prev];
                        }
                        typeClass.add(additionalCollection, 0, queryCollection, ii);
                        typeClass.queryToData(queryCollection, ii, dataCollection);
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
        private final SplitMergeHelper splitMergeHelper;
        private final int[] indices;
        private final int[] lexIndices;
        private final T dataCollection;
        private final T queryCollection;
        private final T additionalCollection;
        private final ValueTypeClass<T> typeClass;
        private final boolean[] isObjectiveStrict;
        private final int dimension;
        private final int threshold3D, thresholdAll;

        private boolean allPointsAreQueryPoints;
        private boolean allPointsAreDataPoints;

        private RecursionHandler(
                double[][] points, double[][] transposedPoints, boolean[] isDataPoint, boolean[] isQueryPoint,
                int[] indices, int[] lexIndices, double[] swap, SplitMergeHelper splitMergeHelper,
                T dataCollection, T queryCollection, T additionalCollection,
                ValueTypeClass<T> typeClass, boolean[] isObjectiveStrict, int dimension,
                int threshold3D, int thresholdAll) {
            this.points = points;
            this.transposedPoints = transposedPoints;
            this.isDataPoint = isDataPoint;
            this.isQueryPoint = isQueryPoint;
            this.indices = indices;
            this.lexIndices = lexIndices;
            this.swap = swap;
            this.splitMergeHelper = splitMergeHelper;
            this.dataCollection = dataCollection;
            this.queryCollection = queryCollection;
            this.additionalCollection = additionalCollection;
            this.typeClass = typeClass;
            this.isObjectiveStrict = isObjectiveStrict;
            this.dimension = dimension;
            this.threshold3D = threshold3D;
            this.thresholdAll = thresholdAll;
        }

        private int getThreshold(int dimension) {
            return dimension == 2 ? threshold3D : thresholdAll;
        }

        private void solve(int from, int until) {
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
        private void helperA(int from, int until, int d) {
            if (from < until) {
                if (from + 1 == until) {
                    // A single point needs to be processed. This means it is complete.
                    completePoint(indices[from]);
                } else if (from + 2 == until) {
                    int firstIndex = indices[from], secondIndex = indices[from + 1];
                    completePoint(firstIndex);
                    if (lexIndices[firstIndex] != lexIndices[secondIndex] && isDataPoint[firstIndex] && isQueryPoint[secondIndex]) {
                        addIfDominates(firstIndex, secondIndex, d);
                    }
                    completePoint(secondIndex);
                } else {
                    while (d > 1) {
                        if (until - from <= getThreshold(d)) {
                            hookA(from, until, d);
                            return;
                        }
                        double[] obj = transposedPoints[d];
                        if (ArrayHelper.transplantAndCheckIfSame(obj, indices, from, until, swap, from)) {
                            if (!isObjectiveStrict[d]) {
                                --d;
                            } else {
                                completeRangeOfPoints(from, until);
                                return;
                            }
                        } else {
                            double median = ArrayHelper.destructiveMedian(swap, from, until);
                            long splitResult = splitMergeHelper.splitInThree(obj, indices, from, from, until, median);
                            int middleStart = SplitMergeHelper.extractMid(splitResult);
                            int greaterStart = SplitMergeHelper.extractRight(splitResult);

                            helperA(from, middleStart, d);
                            helperBAdapter(from, middleStart, greaterStart, d - 1);
                            if (!isObjectiveStrict[d]) {
                                helperA(middleStart, greaterStart, d - 1);
                            } else {
                                completeRangeOfPoints(middleStart, greaterStart);
                            }
                            splitMergeHelper.mergeTwo(indices, lexIndices, from, from, middleStart, middleStart, greaterStart);
                            helperBAdapter(from, greaterStart, until, d - 1);
                            helperA(greaterStart, until, d);
                            splitMergeHelper.mergeTwo(indices, lexIndices, from, from, greaterStart, greaterStart, until);
                            return;
                        }
                    }
                    sweepA(from, until);
                }
            }
        }

        // Assumption: for any good point P and any weak point Q,
        // there is no contradiction that P can dominate Q in objectives [d + 1; dimension).
        // We ensure that only data points remain among good ones, and only query points remain among weak ones.
        private void helperBAdapter(int first, int mid, int last, int d) {
            int newGoodUntil = allPointsAreDataPoints
                    ? mid
                    : splitMergeHelper.filter(indices, isDataPoint, first, first, mid);
            int newWeakUntil = allPointsAreQueryPoints
                    ? last
                    : splitMergeHelper.filter(indices, isQueryPoint, first, mid, last);
            helperB(first, newGoodUntil, mid, newWeakUntil, first, d);
            if (mid != newGoodUntil) {
                splitMergeHelper.mergeTwo(indices, lexIndices, first, first, newGoodUntil, newGoodUntil, mid);
            }
            if (last != newWeakUntil) {
                splitMergeHelper.mergeTwo(indices, lexIndices, first, mid, newWeakUntil, newWeakUntil, last);
            }
        }

        // Assumption: for any good point P and any weak point Q,
        // there is no contradiction that P can dominate Q in objectives [d + 1; dimension).
        // Additionally, all good points are data points, and all weak points are query points.
        private void helperB(int goodFrom, int goodUntil, int weakFrom, int weakUntil, int auxFrom, int d) {
            if (goodFrom < goodUntil && weakFrom < weakUntil) {
                if (goodFrom + 1 == goodUntil) {
                    helperBGood1(goodFrom, weakFrom, weakUntil, d);
                } else if (weakFrom + 1 == weakUntil) {
                    helperBWeak1(goodFrom, goodUntil, weakFrom, d);
                } else {
                    int problemSize = goodUntil - goodFrom + weakUntil - weakFrom;
                    while (d > 1) {
                        if (problemSize <= getThreshold(d)) {
                            hookB(goodFrom, goodUntil, weakFrom, weakUntil, d);
                            return;
                        }
                        double[] currentPoints = transposedPoints[d];
                        boolean isStrict = isObjectiveStrict[d];
                        switch (ArrayHelper.transplantAndDecide(currentPoints, indices,
                                goodFrom, goodUntil, weakFrom, weakUntil, swap, auxFrom, isStrict)) {
                            case ArrayHelper.TRANSPLANT_LEFT_NOT_GREATER:
                                --d;
                                continue;
                            case ArrayHelper.TRANSPLANT_RIGHT_SMALLER:
                                return;
                            case ArrayHelper.TRANSPLANT_GENERAL_CASE:
                                double median = ArrayHelper.destructiveMedian(swap, auxFrom, auxFrom + problemSize);
                                long goodSplit = splitMergeHelper.splitInThree(currentPoints, indices, auxFrom, goodFrom, goodUntil, median);
                                int goodMidL = SplitMergeHelper.extractMid(goodSplit);
                                int goodMidR = SplitMergeHelper.extractRight(goodSplit);
                                long weakSplit = splitMergeHelper.splitInThree(currentPoints, indices, auxFrom, weakFrom, weakUntil, median);
                                int weakMidL = SplitMergeHelper.extractMid(weakSplit);
                                int weakMidR = SplitMergeHelper.extractRight(weakSplit);

                                --d;
                                helperB(goodFrom, goodMidL, weakMidR, weakUntil, auxFrom, d);
                                helperB(goodFrom, goodMidL, weakMidL, weakMidR, auxFrom, d);
                                helperB(goodMidL, goodMidR, weakMidR, weakUntil, auxFrom, d);
                                if (!isStrict) {
                                    helperB(goodMidL, goodMidR, weakMidL, weakMidR, auxFrom, d);
                                }
                                ++d;

                                int tempMid = auxFrom + (problemSize >>> 1);
                                helperB(goodMidR, goodUntil, weakMidR, weakUntil, auxFrom, d);
                                helperB(goodFrom, goodMidL, weakFrom, weakMidL, tempMid, d);

                                splitMergeHelper.mergeThree(indices, lexIndices, auxFrom, goodFrom, goodMidL, goodMidL, goodMidR, goodMidR, goodUntil);
                                splitMergeHelper.mergeThree(indices, lexIndices, auxFrom, weakFrom, weakMidL, weakMidL, weakMidR, weakMidR, weakUntil);
                                return;
                        }
                    }
                    sweepB(goodFrom, goodUntil, weakFrom, weakUntil, auxFrom);
                }
            }
        }

        private void helperBGood1(int good, int weakFrom, int weakUntil, int d) {
            int gi = indices[good];
            int giLex = lexIndices[gi];
            for (int i = weakUntil - 1; i >= weakFrom; --i) {
                int wi = indices[i];
                if (lexIndices[wi] < giLex) {
                    break;
                }
                addIfDominates(gi, wi, d);
            }
        }

        private void helperBWeak1(int goodFrom, int goodUntil, int weak, int d) {
            int wi = indices[weak];
            int wiLex = lexIndices[wi];
            for (int i = goodFrom; i < goodUntil; ++i) {
                int gi = indices[i];
                if (lexIndices[gi] > wiLex) {
                    break;
                }
                addIfDominates(gi, wi, d);
            }
        }

        private void hookA(int from, int until, int d) {
            for (int q = from; q < until; ++q) {
                int qi = indices[q];
                if (allPointsAreQueryPoints || isQueryPoint[qi]) {
                    int qiLex = lexIndices[qi];
                    for (int v = q - 1; v >= from; --v) {
                        int vi = indices[v];
                        if (qiLex == lexIndices[vi]) {
                            continue;
                        }
                        if ((allPointsAreDataPoints || isDataPoint[vi])
                                && typeClass.targetChangesOnAdd(dataCollection, vi, queryCollection, qi)) {
                            addIfDominates(vi, qi, d);
                        }
                    }
                    completePoint(qi);
                }
            }
        }

        private void hookB(int goodFrom, int goodUntil, int weakFrom, int weakUntil, int d) {
            for (int w = weakFrom; w < weakUntil; ++w) {
                helperBWeak1(goodFrom, goodUntil, w, d);
            }
        }

        private void sweepA(int from, int until) {
            double[] local = transposedPoints[1];
            int fwUntil = initFenwickKeysInSwap(local, from, until, from);
            typeClass.fillWithZeroes(additionalCollection, from, fwUntil);

            int last = from;
            int lastIndex = indices[last];
            if (isObjectiveStrict[0]) {
                double[] obj0 = transposedPoints[0];
                for (int i = from; i < until; ++i) {
                    int currIndex = indices[i];
                    if (allPointsAreQueryPoints || isQueryPoint[currIndex]) {
                        while (obj0[lastIndex] < obj0[currIndex]) {
                            ++last;
                            if (allPointsAreDataPoints || isDataPoint[lastIndex]) {
                                fenwickSet(from, fwUntil, local[lastIndex], lastIndex);
                            }
                            lastIndex = indices[last];
                        }
                        fenwickQuery(from, fwUntil, local[currIndex]);
                        typeClass.add(additionalCollection, fwUntil, queryCollection, currIndex);
                        completePoint(currIndex);
                    }
                }
            } else {
                for (int i = from; i < until; ++i) {
                    int currIndex = indices[i];
                    int currLex = lexIndices[currIndex];
                    if (allPointsAreQueryPoints || isQueryPoint[currIndex]) {
                        while (lexIndices[lastIndex] < currLex) {
                            ++last;
                            if (allPointsAreDataPoints || isDataPoint[lastIndex]) {
                                fenwickSet(from, fwUntil, local[lastIndex], lastIndex);
                            }
                            lastIndex = indices[last];
                        }
                        fenwickQuery(from, fwUntil, local[currIndex]);
                        typeClass.add(additionalCollection, fwUntil, queryCollection, currIndex);
                        completePoint(currIndex);
                    }
                }
            }
        }

        private void sweepB(int goodFrom, int goodUntil, int weakFrom, int weakUntil, int auxFrom) {
            double[] local = transposedPoints[1];
            int fwUntil = initFenwickKeysInSwap(local, goodFrom, goodUntil, auxFrom);
            typeClass.fillWithZeroes(additionalCollection, auxFrom, fwUntil);

            int last = goodFrom;
            int lastIndex = indices[last];
            if (isObjectiveStrict[0]) {
                double[] obj0 = transposedPoints[0];
                for (int i = weakFrom; i < weakUntil; ++i) {
                    int currIndex = indices[i];
                    while (last < goodUntil && obj0[lastIndex] < obj0[currIndex]) {
                        ++last;
                        fenwickSet(auxFrom, fwUntil, local[lastIndex], lastIndex);
                        lastIndex = indices[last]; // this may ask for indices[goodUntil], but that's safe
                    }
                    fenwickQuery(auxFrom, fwUntil, local[currIndex]);
                    typeClass.add(additionalCollection, fwUntil, queryCollection, currIndex);
                }
            } else {
                for (int i = weakFrom; i < weakUntil; ++i) {
                    int currIndex = indices[i];
                    int currLex = lexIndices[currIndex];
                    while (last < goodUntil && lexIndices[lastIndex] < currLex) {
                        ++last;
                        fenwickSet(auxFrom, fwUntil, local[lastIndex], lastIndex);
                        lastIndex = indices[last]; // this may ask for indices[goodUntil], but that's safe
                    }
                    fenwickQuery(auxFrom, fwUntil, local[currIndex]);
                    typeClass.add(additionalCollection, fwUntil, queryCollection, currIndex);
                }
            }
        }

        private void fenwickQuery(int fwFrom, int fwUntil, double key) {
            typeClass.fillWithZeroes(additionalCollection, fwUntil, fwUntil + 1);
            int keyIndex = Arrays.binarySearch(swap, fwFrom, fwUntil, key);
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
            keyIndex -= fwFrom;
            while (keyIndex >= 0) {
                typeClass.add(additionalCollection, keyIndex + fwFrom, additionalCollection, fwUntil);
                keyIndex = (keyIndex & (keyIndex + 1)) - 1;
            }
        }

        private void fenwickSet(int fwFrom, int fwUntil, double key, int valueIndex) {
            int keyIndex = Arrays.binarySearch(swap, fwFrom, fwUntil, key);
            int fwSize = fwUntil - fwFrom;
            keyIndex -= fwFrom;
            while (keyIndex < fwSize) {
                typeClass.add(dataCollection, valueIndex, additionalCollection, keyIndex + fwFrom);
                keyIndex |= keyIndex + 1;
            }
        }

        private int initFenwickKeysInSwap(double[] keys, int from, int until, int auxFrom) {
            for (int i = from, j = auxFrom; i < until; ++i, ++j) {
                swap[j] = keys[indices[i]];
            }
            int auxUntil = auxFrom + until - from;
            Arrays.sort(swap, auxFrom, auxUntil);
            int realUntil = auxFrom + 1;
            double last = swap[auxFrom];
            for (int i = auxFrom + 1; i < auxUntil; ++i) {
                if (swap[i] != last) {
                    swap[realUntil] = last = swap[i];
                    ++realUntil;
                }
            }
            return realUntil;
        }

        private void completeRangeOfPoints(int from, int until) {
            for (int i = from; i < until; ++i) {
                completePoint(indices[i]);
            }
        }

        private void completePoint(int index) {
            if (allPointsAreQueryPoints || isQueryPoint[index]) {
                typeClass.queryToData(queryCollection, index, dataCollection);
            }
        }

        // Assumes that:
        // - there is no contradiction to domination in higher objectives, e.g. [maxObjective + 1; dimension)
        // - lexIndices[goodIndex] < lexIndices[weakIndex]
        private void addIfDominates(int goodIndex, int weakIndex, int maxObjective) {
            if (typeClass.targetChangesOnAdd(dataCollection, goodIndex, queryCollection, weakIndex)) {
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

        private void lexSort(int from, int until) {
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

        private void lexSortImpl(int from, int until, int d) {
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

        private void sortIndicesBy(double[] values, int from, int until) {
            if (from + 1 < until) {
                int l = from, r = until - 1;
                double pivot = values[indices[(l + r) >>> 1]];
                int il, ir;
                while (l <= r) {
                    while (values[il = indices[l]] < pivot) ++l;
                    while (values[ir = indices[r]] > pivot) --r;
                    if (l <= r) {
                        indices[l] = ir;
                        indices[r] = il;
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
