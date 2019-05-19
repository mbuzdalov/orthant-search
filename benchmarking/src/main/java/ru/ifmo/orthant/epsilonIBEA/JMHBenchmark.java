package ru.ifmo.orthant.epsilonIBEA;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import ru.ifmo.orthant.ParameterParser;
import ru.ifmo.orthant.PointSets;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Timeout(time = 1, timeUnit = TimeUnit.HOURS)
@Warmup(iterations = 1, time = 6)
@Measurement(iterations = 1, time = 1)
@Fork(value = 5)
public class JMHBenchmark {
    private EpsilonIBEAFitnessAssignment algorithm;
    private double[][][] instances;
    private double[] results;

    private static final double kappa = 1.5432;

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
        if (usedAlgorithm.equals("NaiveImplementation")) {
            algorithm = new NaiveImplementation(n, dimension, kappa);
        } else {
            algorithm = new OrthantImplementation(ParameterParser.parseUsedOrthantAlgorithm(usedAlgorithm, n, dimension - 1), kappa);
        }
        results = new double[n];
    }

    @Benchmark
    public void benchmark(Blackhole bh) {
        for (double[][] dataset : instances) {
            algorithm.assignFitness(dataset, results);
            bh.consume(results);
        }
    }
}
