package ru.ifmo.orthant.epsilon;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import ru.ifmo.orthant.DivideConquerOrthantSearch;
import ru.ifmo.orthant.NaiveOrthantSearch;
import ru.ifmo.orthant.PointSets;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Timeout(time = 1, timeUnit = TimeUnit.HOURS)
@Warmup(iterations = 1, time = 6)
@Measurement(iterations = 1, time = 1)
@Fork(value = 5)
public class JMHBenchmark {
    private AdditiveEpsilonIndicator algorithm;
    private double[][][] instances;

    @Param("10")
    private int nInstances;

    @Param({"10", "31", "100", "316", "1000", "3162", "10000"})
    private int n;

    @Param({"2", "3", "4", "5", "7", "10", "15", "20"})
    private int dimension;

    @Param({"uniform.hypercube", "discrete.hypercube", "uniform.hyperplane"})
    private String datasetId;

    @Param({"NaiveImplementation", "OrthantDivideConquer", "OrthantDivideConquerThreshold"})
    private String usedAlgorithm;

    @Setup
    public void initialize() {
        switch (datasetId) {
            case "uniform.hypercube":  instances = PointSets.generateUniformHypercube(nInstances, n, dimension); break;
            case "discrete.hypercube": instances = PointSets.generateDiscreteHypercube(nInstances, n, dimension); break;
            case "uniform.hyperplane": instances = PointSets.generateUniformHyperplanes(nInstances, n, dimension, 1); break;
            default: throw new AssertionError("Dataset ID '" + datasetId + "' is not known");
        }
        switch (usedAlgorithm) {
            case "NaiveImplementation":
                algorithm = new NaiveImplementation(n, dimension);
                break;
            case "OrthantNaive":
                algorithm = new OrthantImplementation(new NaiveOrthantSearch(n * 2, dimension));
                break;
            case "OrthantDivideConquer":
                algorithm = new OrthantImplementation(new DivideConquerOrthantSearch(n * 2, dimension, false, 1));
                break;
            case "OrthantDivideConquerThreshold":
                algorithm = new OrthantImplementation(new DivideConquerOrthantSearch(n * 2, dimension, true, 1));
                break;
            default: throw new AssertionError("Algorithm ID '" + usedAlgorithm + "' is not known");
        }
    }

    @Benchmark
    public void benchmark(Blackhole bh) {
        for (int i = 0; i < instances.length; ++i) {
            double[][] moving = instances[i];
            double[][] fixed = instances[(i + 1) % instances.length];
            bh.consume(algorithm.evaluate(moving, fixed));
        }
    }
}
