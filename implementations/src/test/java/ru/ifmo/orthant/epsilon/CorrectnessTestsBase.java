package ru.ifmo.orthant.epsilon;

import java.util.function.BiFunction;

import org.junit.Assert;
import org.junit.Test;

public abstract class CorrectnessTestsBase {
    protected abstract BiFunction<Integer, Integer, AdditiveEpsilonIndicator> getFactory();

    private double runEpsilon(double[][] moving, double[][] fixed) {
        AdditiveEpsilonIndicator algorithm = getFactory().apply(Math.max(moving.length, fixed.length), moving[0].length);
        Assert.assertTrue(moving.length <= algorithm.getMaximumSetSize());
        Assert.assertTrue(fixed.length <= algorithm.getMaximumSetSize());
        Assert.assertTrue(moving[0].length <= algorithm.getMaximumDimension());
        Assert.assertTrue(fixed[0].length <= algorithm.getMaximumDimension());
        return algorithm.evaluate(moving, fixed);
    }

    @Test
    public void simpleTwoPoints() {
        double[][] moving = {{ 1, 2, 3, 4 }};
        double[][] fixed = {{ 5, 6, 7, 8 }};
        Assert.assertEquals(-4.0, runEpsilon(moving, fixed), 1e-9);
        Assert.assertEquals(4.0, runEpsilon(fixed, moving), 1e-9);
    }

    @Test
    public void singleEqualPoints() {
        double[] point = {1, 2, 3, 4, 5};
        Assert.assertEquals(0, runEpsilon(new double[][] { point }, new double[][] { point }), 1e-9);
    }

    @Test
    public void singleIncomparablePoints() {
        double[] pointA = {1, 0};
        double[] pointB = {0, 2};
        Assert.assertEquals(1, runEpsilon(new double[][] { pointA }, new double[][] { pointB }), 1e-9);
        Assert.assertEquals(2, runEpsilon(new double[][] { pointB }, new double[][] { pointA }), 1e-9);
    }

    @Test
    public void singleDominatingPoints() {
        double[] pointA = {1, 1};
        double[] pointB = {3, 2};
        Assert.assertEquals(-1, runEpsilon(new double[][] { pointA }, new double[][] { pointB }), 1e-9);
        Assert.assertEquals(2,  runEpsilon(new double[][] { pointB }, new double[][] { pointA }), 1e-9);
    }

    @Test
    public void parallelSets() {
        double[][] setA = {{2, 0}, {0, 2}};
        double[][] setB = {{3, 1}, {1, 3}};
        Assert.assertEquals(-1, runEpsilon(setA, setB), 1e-9);
        Assert.assertEquals(1,  runEpsilon(setB, setA), 1e-9);
    }

    @Test
    public void notSoParallelSets() {
        double[][] setA = {{1, 0}, {0, 2}};
        double[][] setB = {{2, 1}, {0, 3}};
        Assert.assertEquals(0, runEpsilon(setA, setB), 1e-9);
        Assert.assertEquals(1, runEpsilon(setB, setA), 1e-9);
    }

    @Test
    public void crossingSets() {
        double[][] setA = {{0, 1}, {3, 2}};
        double[][] setB = {{1, 3}, {3, 1}};
        Assert.assertEquals(0, runEpsilon(setA, setB), 1e-9);
        Assert.assertEquals(2, runEpsilon(setB, setA), 1e-9);
    }

    @Test
    public void dominationInMoving() {
        double[][] moving = {
            { 0.0, 9.0, },
            { 3.0, 9.0, },
        };
        double[][] fixed = {
            { 5.0, 0.0, },
            { 1.0, 1.0, },
        };
        Assert.assertEquals(9, runEpsilon(moving, fixed), 1e-9);
    }

    @Test
    public void simple3D() {
        double[][] moving = {
            { 34.0, 72.0, 48.0, },
            { 69.0, 8.0, 57.0, },
        };
        double[][] fixed = {
            { 80.0, 94.0, 40.0, },
            { 57.0, 45.0, 64.0, },
        };
        Assert.assertEquals(12, runEpsilon(moving, fixed), 1e-9);
    }

    @Test
    public void decompositionBug4Dv1() {
        double[][] moving = {
          { 0.7890958235898934, 0.8922686876689713, 0.030843185314033184, 0.40879408078638757, },
          { 0.7388658711594208, 0.9747242287588833, 0.4909901631090605, 0.9522724897767506, },
          { 0.6740702708246834, 0.32937012573685887, 0.36036018605223197, 0.34248371857070703, },
          { 0.7548988344304038, 0.818591877268516, 0.9205976935423161, 0.06282389440211567, },
          { 0.9116280252227353, 0.8337063426517574, 0.10038752197946099, 0.6759179646144207, },
          { 0.4830395453863978, 0.11799092241196185, 0.785746957855115, 0.189711558213143, },
          { 0.7887730994786929, 0.5243870652290928, 0.037228754449704415, 0.3411016011216823, },
          { 0.9421624957790548, 0.6611555384609277, 0.6988069330333451, 0.7137618164367816, },
          { 0.7931118670707383, 0.09458147316073018, 0.5443495037218342, 0.5257654872618504, },
          { 0.8798268481938238, 0.14989743331134664, 0.6749658485385469, 0.9978019904789638, },
        };
        double[][] fixed = {
          { 0.6637230116757932, 0.3799128418248223, 0.3541488765752141, 0.4442300656497852, },
          { 0.12359207596202426, 0.4554089167569, 0.8475145593377471, 0.6169051235658549, },
          { 0.9931139029198522, 0.5721779079093718, 0.12007523079668969, 0.3756639533980891, },
          { 0.5400603738762294, 0.4666563799352895, 0.43636478327774186, 0.459545139604461, },
          { 0.8060446247729307, 0.7854221939378718, 0.00736727281176941, 0.9892686759470497, },
          { 0.6524276192240517, 0.8633952599855024, 0.8330834691601823, 0.34419409627860276, },
          { 0.7910009760303661, 0.9314025762141717, 0.8007019056906056, 0.2259210824404898, },
          { 0.21732369780523442, 0.39583799981327206, 0.9157246103025881, 0.04589249186582334, },
          { 0.11009752762892411, 0.11146822051622507, 0.26703018888003394, 0.2558044337980576, },
        };
        Assert.assertEquals(0.518716768975081, runEpsilon(moving, fixed), 1e-9);
    }

    @Test
    public void decompositionBug4Dv2() {
        double[][] moving = {
          { 2.0, 0.0, 67.0, 62.0, },
          { 58.0, 88.0, 56.0, 50.0, },
          { 33.0, 29.0, 71.0, 5.0, },
          { 49.0, 98.0, 77.0, 31.0, },
          { 59.0, 38.0, 83.0, 77.0, },
          { 87.0, 18.0, 61.0, 20.0, },
          { 7.0, 72.0, 3.0, 52.0, },
          { 37.0, 56.0, 85.0, 86.0, },
          { 5.0, 39.0, 43.0, 3.0, },
        };
        double[][] fixed = {
          { 44.0, 80.0, 2.0, 65.0, },
          { 99.0, 63.0, 95.0, 1.0, },
          { 75.0, 10.0, 5.0, 7.0, },
          { 34.0, 58.0, 69.0, 1.0, },
          { 88.0, 22.0, 91.0, 67.0, },
          { 17.0, 66.0, 27.0, 80.0, },
          { 6.0, 35.0, 79.0, 78.0, },
          { 6.0, 77.0, 84.0, 72.0, },
          { 6.0, 27.0, 28.0, 5.0, },
        };
        Assert.assertEquals(38.0, runEpsilon(moving, fixed), 1e-9);
    }

    @Test
    public void runtimeError4Dv1() {
        double[][] moving = {
          { 27.0, 35.0, 35.0, 31.0, },
          { 61.0, 72.0, 76.0, 73.0, },
          { 35.0, 45.0, 31.0, 1.0, },
          { 22.0, 19.0, 64.0, 25.0, },
          { 43.0, 47.0, 96.0, 68.0, },
          { 14.0, 77.0, 10.0, 64.0, },
          { 44.0, 9.0, 61.0, 98.0, },
          { 70.0, 28.0, 48.0, 45.0, },
          { 32.0, 35.0, 1.0, 90.0, },
          { 73.0, 52.0, 75.0, 78.0, },
        };
        double[][] fixed = {
          { 57.0, 94.0, 14.0, 58.0, },
          { 54.0, 32.0, 21.0, 71.0, },
          { 36.0, 69.0, 0.0, 74.0, },
          { 61.0, 45.0, 52.0, 71.0, },
          { 79.0, 84.0, 53.0, 58.0, },
          { 92.0, 37.0, 47.0, 35.0, },
          { 16.0, 8.0, 80.0, 87.0, },
          { 27.0, 13.0, 41.0, 76.0, },
          { 92.0, 13.0, 4.0, 10.0, },
          { 5.0, 82.0, 79.0, 93.0, },
        };
        Assert.assertEquals(31.0, runEpsilon(moving, fixed), 1e-9);
    }


    @Test
    public void generatedTest1() {
        double[][] moving = {
                {0.9428567767983383, 0.8755184805715142},
                {0.17725859055487936, 0.713817857474533},
                {0.037795496435638176, 0.1257180928646051},
                {0.8286060949519989, 0.49041610541040326},
                {0.8673758236876935, 0.3961914799592563},
        };
        double[][] fixed = {
                {0.32994621420045667, 0.8893426840341866},
                {0.01463794989307221, 0.10433577159606744},
                {0.13223960273603375, 0.13604049229829795},
                {0.8519759118381918, 0.9421775532427836},
                {0.45477677557793106, 0.5451239279444157},
        };
        Assert.assertEquals(0.023157546542565965, runEpsilon(moving, fixed), 1e-9);
    }
}
