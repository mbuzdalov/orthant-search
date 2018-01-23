package ru.ifmo.orthant.epsilonIBEA;

import java.util.Arrays;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import ru.ifmo.orthant.DivideConquerOrthantSearch;
import ru.ifmo.orthant.NaiveOrthantSearch;

public class EpsilonIBEASmokeTest {
    @Test
    public void smokeTest() {
        Random random = new Random(282354312242L);
        double kappa = 1.2345;
        EpsilonIBEAFitnessAssignment[] algorithms = new EpsilonIBEAFitnessAssignment[]{
                new NaiveImplementation(180, 6, kappa),
                new OrthantImplementation(new NaiveOrthantSearch(180, 6), kappa),
                new OrthantImplementation(new DivideConquerOrthantSearch(180, 6, false), kappa),
                new OrthantImplementation(new DivideConquerOrthantSearch(180, 6, true), kappa),
        };

        for (int t = 0; t < 300; ++t) {
            int n = 1 + random.nextInt(5);
            int d = 1 + random.nextInt(2);
            double[][] points = new double[n][d];
            boolean isGeneralPosition = random.nextBoolean();
            if (isGeneralPosition) {
                for (int i = 0; i < n; ++i) {
                    for (int j = 0; j < d; ++j) {
                        points[i][j] = random.nextDouble();
                    }
                }
            } else {
                for (int i = 0; i < n; ++i) {
                    for (int j = 0; j < d; ++j) {
                        points[i][j] = random.nextInt(5);
                    }
                }
            }
            double[] naiveFitness = new double[n];
            double[] orthantFitness = new double[n];

            algorithms[0].assignFitness(points, naiveFitness);
            for (int i = 1; i < algorithms.length; ++i) {
                Arrays.fill(orthantFitness, 42);
                algorithms[i].assignFitness(points, orthantFitness);
                try {
                    Assert.assertArrayEquals(isGeneralPosition ? "General position" : "Integer points",
                            naiveFitness, orthantFitness, 1e-9);
                } catch (AssertionError e) {
                    System.out.println("double[][] points = {");
                    for (double[] pt : points) {
                        System.out.print("    {");
                        for (int j = 0; j < pt.length; ++j) {
                            System.out.print(pt[j]);
                            if (j + 1 == pt.length) {
                                System.out.println("},");
                            } else {
                                System.out.print(", ");
                            }
                        }
                    }
                    System.out.println("};");
                    System.out.println("double[] expectedFitness = {");
                    System.out.print("    ");
                    for (double fitness : naiveFitness) {
                        System.out.print(fitness + ", ");
                    }
                    System.out.println();
                    System.out.println("};");
                    System.out.println("double kappa = " + kappa + ";");
                    throw e;
                }
            }
        }
    }
}
