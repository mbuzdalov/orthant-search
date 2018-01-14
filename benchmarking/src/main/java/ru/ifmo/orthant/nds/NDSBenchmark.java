package ru.ifmo.orthant.nds;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import ru.ifmo.orthant.impl.NaiveOrthantSearch;
import ru.ifmo.orthant.nds.impl.NaiveImplementation;
import ru.ifmo.orthant.nds.impl.OrthantImplementation;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Timeout(time = 1, timeUnit = TimeUnit.HOURS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 1, time = 1)
@Fork(value = 3)
public class NDSBenchmark {
    private NonDominatedSorting sorting;
    private double[][][] instances;
    private int[] ranks;

    @Param({"10", "100", "1000", "10000"})
    private int n;

    @Param({"2", "3", "4", "5", "6", "7", "8", "9", "10"})
    private int d;

    @Param({"uniform.hypercube", "uniform.hyperplanes.f1"})
    private String datasetId;

    @Param({"NaiveImplementation", "OrthantNaive"})
    private String algorithmId;

    @Setup
    public void initialize() {
        switch (datasetId) {
            case "uniform.hypercube": instances = Instances.generateUniformHypercube(n, d); break;
            case "uniform.hyperplanes.f1": instances = Instances.generateUniformHyperplanes(n, d, 1); break;
            default: throw new AssertionError("Dataset ID '" + datasetId + "' is not known");
        }
        switch (algorithmId) {
            case "NaiveImplementation": sorting = new NaiveImplementation(n, d); break;
            case "OrthantNaive": sorting = new OrthantImplementation(new NaiveOrthantSearch(n, d)); break;
            default: throw new AssertionError("Algorithm ID '" + algorithmId + "' is not known");
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
