package ru.ifmo.orthant.nds;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import ru.ifmo.orthant.PointSets;
import ru.ifmo.orthant.DivideConquerOrthantSearch;
import ru.ifmo.orthant.NaiveOrthantSearch;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Timeout(time = 1, timeUnit = TimeUnit.HOURS)
@Warmup(iterations = 2, time = 3)
@Measurement(iterations = 1, time = 1)
@Fork(value = 5)
public class NDSBenchmark {
    private NonDominatedSorting sorting;
    private double[][][] instances;
    private int[] ranks;

    @Param("10")
    private int nInstances;

    @Param({"10", "31", "100", "316", "1000", "3162", "10000"})
    private int n;

    @Param({"2", "3", "4", "5", "7", "10", "15", "20"})
    private int dimension;

    @Param({"uniform.hypercube", "uniform.hyperplane"})
    private String datasetId;

    @Param({"NaiveImplementation", "OrthantNaive", "OrthantDivideConquer", "OrthantDivideConquerThreshold"})
    private String usedAlgorithm;

    @Setup
    public void initialize() {
        switch (datasetId) {
            case "uniform.hypercube": instances = PointSets.generateUniformHypercube(nInstances, n, dimension); break;
            case "uniform.hyperplane": instances = PointSets.generateUniformHyperplanes(nInstances, n, dimension, 1); break;
            default: throw new AssertionError("Dataset ID '" + datasetId + "' is not known");
        }
        switch (usedAlgorithm) {
            case "NaiveImplementation":
                sorting = new NaiveImplementation(n, dimension);
                break;
            case "OrthantNaive":
                sorting = new OrthantImplementation(new NaiveOrthantSearch(n, dimension));
                break;
            case "OrthantDivideConquer":
                sorting = new OrthantImplementation(new DivideConquerOrthantSearch(n, dimension, false));
                break;
            case "OrthantDivideConquerThreshold":
                sorting = new OrthantImplementation(new DivideConquerOrthantSearch(n, dimension, true));
                break;
            default: throw new AssertionError("Algorithm ID '" + usedAlgorithm + "' is not known");
        }
        ranks = new int[n];
    }

    @Benchmark
    public void benchmark(Blackhole bh) {
        for (double[][] dataset : instances) {
            sorting.sort(dataset, ranks);
            bh.consume(ranks);
        }
    }
}
