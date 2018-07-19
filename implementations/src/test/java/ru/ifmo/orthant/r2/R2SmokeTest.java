package ru.ifmo.orthant.r2;

import java.util.Arrays;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import ru.ifmo.orthant.DivideConquerOrthantSearch;
import ru.ifmo.orthant.NaiveOrthantSearch;

public class R2SmokeTest {
    @Test
    public void smokeTest() {
        Random random = new Random(282354312242L);
        R2Indicator[] algorithms = new R2Indicator[] {
                new NaiveImplementation(180, 6),
                new OrthantImplementation(new NaiveOrthantSearch(360, 6)),
                new OrthantImplementation(new DivideConquerOrthantSearch(360, 6, false)),
                new OrthantImplementation(new DivideConquerOrthantSearch(360, 6, true)),
        };

        for (int t = 0; t < 300; ++t) {
            int m = 5 + random.nextInt(150);
            int f = 5 + random.nextInt(150);
            int d = 1 + random.nextInt(6);
            double[][] referenceVectors = new double[m][d];
            double[][] population = new double[f][d];
            double[] referencePoint = new double[d];
            double power = random.nextBoolean() ? 1 : d;
            Arrays.fill(referencePoint, 6);
            if (random.nextBoolean()) {
                for (int i = 0; i < m; ++i) {
                    for (int j = 0; j < d; ++j) {
                        referenceVectors[i][j] = random.nextDouble();
                    }
                }
                for (int i = 0; i < f; ++i) {
                    for (int j = 0; j < d; ++j) {
                        population[i][j] = random.nextDouble();
                    }
                }
            } else {
                for (int i = 0; i < m; ++i) {
                    for (int j = 0; j < d; ++j) {
                        referenceVectors[i][j] = 1 + random.nextInt(5);
                    }
                }
                for (int i = 0; i < f; ++i) {
                    for (int j = 0; j < d; ++j) {
                        population[i][j] = random.nextInt(5);
                    }
                }
            }
            double naiveR2 = algorithms[0].evaluate(referenceVectors, referencePoint, population, power);

            for (int i = 1; i < algorithms.length; ++i) {
                double orthantR2 = algorithms[i].evaluate(referenceVectors, referencePoint, population, power);
                if (Math.abs(naiveR2 - orthantR2) > 1e-9) {
                    System.out.println("double[][] referenceVectors = {");
                    for (double[] pt : referenceVectors) {
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
                    System.out.println("double[][] population = {");
                    for (double[] pt : population) {
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
                    System.out.print("double[] referencePoint = {");
                    for (int j = 0; j < referencePoint.length; ++j) {
                        System.out.print(referencePoint[j]);
                        if (j + 1 == referencePoint.length) {
                            System.out.println("};");
                        } else {
                            System.out.print(", ");
                        }
                    }
                    System.out.println("double power = " + power + ";");
                    System.out.println("double expectedEpsilon = " + naiveR2 + ";");
                }
                Assert.assertEquals(naiveR2, orthantR2, 1e-9);
            }
        }
    }
}
