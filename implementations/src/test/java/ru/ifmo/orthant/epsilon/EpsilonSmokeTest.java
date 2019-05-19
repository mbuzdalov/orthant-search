package ru.ifmo.orthant.epsilon;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import ru.ifmo.orthant.DivideConquerOrthantSearch;
import ru.ifmo.orthant.NaiveOrthantSearch;

public class EpsilonSmokeTest {
    @Test
    public void smokeTest() {
        Random random = new Random(282354312242L);
        AdditiveEpsilonIndicator[] algorithms = new AdditiveEpsilonIndicator[] {
                new NaiveImplementation(360, 6),
                new OrthantImplementation(new NaiveOrthantSearch(720, 6)),
                new OrthantImplementation(new DivideConquerOrthantSearch(720, 6, false, 1)),
                new OrthantImplementation(new DivideConquerOrthantSearch(720, 6, true, 1)),
                new OrthantImplementation(new DivideConquerOrthantSearch(720, 6, false, -1)),
                new OrthantImplementation(new DivideConquerOrthantSearch(720, 6, true, -1)),
        };

        for (int t = 0; t < 300; ++t) {
            int m = 5 + random.nextInt(300);
            int f = 5 + random.nextInt(300);
            int d = 1 + random.nextInt(6);
            double[][] moving = new double[m][d];
            double[][] fixed = new double[f][d];
            if (random.nextBoolean()) {
                for (int i = 0; i < m; ++i) {
                    for (int j = 0; j < d; ++j) {
                        moving[i][j] = random.nextDouble();
                    }
                }
                for (int i = 0; i < f; ++i) {
                    for (int j = 0; j < d; ++j) {
                        fixed[i][j] = random.nextDouble();
                    }
                }
            } else {
                for (int i = 0; i < m; ++i) {
                    for (int j = 0; j < d; ++j) {
                        moving[i][j] = random.nextInt(5);
                    }
                }
                for (int i = 0; i < f; ++i) {
                    for (int j = 0; j < d; ++j) {
                        fixed[i][j] = random.nextInt(5);
                    }
                }
            }
            double naiveEpsilon = algorithms[0].evaluate(moving, fixed);

            for (int i = 1; i < algorithms.length; ++i) {
                double orthantEpsilon = algorithms[i].evaluate(moving, fixed);
                if (Math.abs(naiveEpsilon - orthantEpsilon) > 1e-9) {
                    System.out.println("double[][] moving = {");
                    for (double[] pt : moving) {
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
                    System.out.println("double[][] fixed = {");
                    for (double[] pt : fixed) {
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
                    System.out.println("double expectedEpsilon = " + naiveEpsilon + ";");
                }
                Assert.assertEquals(naiveEpsilon, orthantEpsilon, 1e-9);
            }
        }
    }
}
