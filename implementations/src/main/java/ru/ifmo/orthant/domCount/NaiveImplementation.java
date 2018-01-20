package ru.ifmo.orthant.domCount;

import java.util.Arrays;

import ru.ifmo.orthant.domCount.DominanceCount;
import ru.ifmo.orthant.util.DominanceHelper;
import ru.ifmo.orthant.util.PointWrapper;

public final class NaiveImplementation extends DominanceCount {
    private final PointWrapper[] wrappers;
    private final int maxDimension;

    public NaiveImplementation(int maxPoints, int maxDimension) {
        this.maxDimension = maxDimension;
        wrappers = new PointWrapper[maxPoints];
        for (int i = 0; i < maxPoints; ++i) {
            wrappers[i] = new PointWrapper();
        }
    }

    @Override
    public int getMaximumPoints() {
        return wrappers.length;
    }

    @Override
    public int getMaximumDimension() {
        return maxDimension;
    }

    @Override
    public void evaluate(double[][] points, int[] dominanceCounts) {
        int n = points.length;
        if (n == 0) {
            return;
        }
        int dimension = points[0].length;
        for (int i = 0; i < n; ++i) {
            wrappers[i].point = points[i];
            wrappers[i].index = i;
            wrappers[i].value = 0;
        }
        Arrays.sort(wrappers, 0, n);
        Arrays.fill(dominanceCounts, 0, n, 0);
        for (int i = 0; i < n; ++i) {
            PointWrapper good = wrappers[i];
            for (int j = i + 1; j < n; ++j) {
                if (DominanceHelper.strictlyDominates(good.point, wrappers[j].point, dimension)) {
                    ++good.value;
                }
            }
        }
        for (int i = 0; i < n; ++i) {
            PointWrapper wrapper = wrappers[i];
            dominanceCounts[wrapper.index] = wrapper.value;
            wrapper.point = null;
        }
    }
}
