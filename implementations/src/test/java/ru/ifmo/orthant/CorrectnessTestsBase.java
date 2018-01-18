package ru.ifmo.orthant;

import java.util.Arrays;
import java.util.Random;
import java.util.function.BiFunction;

import org.junit.Assert;
import org.junit.Test;

public abstract class CorrectnessTestsBase {
    protected abstract BiFunction<Integer, Integer, OrthantSearch> getFactory();

    private void checkOneSet(double[][] points, int[] dataValues, int[] expectedQueryValues,
                             boolean[] isDataPoint, boolean[] isQueryPoint, boolean[] isStrict) {
        ValueTypeClass<int[]> tc = new SumTypeClass();
        int[] queryValues = new int[expectedQueryValues.length];
        OrthantSearch orthantSearch = getFactory().apply(points.length, isStrict.length);
        int[] additionalCollection = tc.createCollection(orthantSearch.getAdditionalCollectionSize(orthantSearch.getMaximumPoints()));

        orthantSearch.runSearch(points, dataValues, queryValues, 0, points.length,
                isDataPoint, isQueryPoint, additionalCollection, tc, isStrict);
        Assert.assertArrayEquals(expectedQueryValues, queryValues);
    }

    @Test
    public void generatedTest0() {
        double[][] points = {
                {0.0, 3.0, 3.0}, {1.0, 3.0, 4.0}, {1.0, 0.0, 4.0}, {1.0, 2.0, 3.0}, {4.0, 4.0, 1.0},
                {0.0, 1.0, 4.0}, {2.0, 3.0, 3.0}, {0.0, 4.0, 4.0}, {1.0, 0.0, 1.0}, {1.0, 4.0, 2.0}
        };
        int[] dataValues = {
                -2081227235, -635758656, 1221586320, -1953677882, -1092809140,
                135343476, 670169209, 1849033278, 2070422417, 224983625
        };
        int[] expectedQueryValues = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        boolean[] isStrict = { true, true, false };
        boolean[] isQueryPoint = { false, true, true, true, true, false, true, false, true, false };
        boolean[] isDataPoint = { false, false, true, false, true, false, true, true, false, true };
        checkOneSet(points, dataValues, expectedQueryValues, isDataPoint, isQueryPoint, isStrict);
    }

    @Test
    public void generatedTest1() {
        double[][] points = {
                {3.0, 2.0, 3.0, 3.0, 0.0, 1.0}, {3.0, 2.0, 2.0, 0.0, 1.0, 3.0}, {3.0, 1.0, 2.0, 3.0, 2.0, 3.0},
                {4.0, 0.0, 4.0, 4.0, 3.0, 3.0}, {3.0, 3.0, 2.0, 2.0, 3.0, 1.0}, {4.0, 0.0, 2.0, 0.0, 2.0, 1.0},
                {2.0, 4.0, 2.0, 2.0, 1.0, 1.0}, {3.0, 2.0, 3.0, 0.0, 3.0, 0.0}, {0.0, 0.0, 4.0, 1.0, 0.0, 4.0},
                {1.0, 0.0, 2.0, 4.0, 0.0, 4.0}, {2.0, 1.0, 0.0, 1.0, 4.0, 2.0}, {3.0, 0.0, 1.0, 2.0, 3.0, 1.0},
                {1.0, 0.0, 2.0, 4.0, 4.0, 1.0}, {2.0, 3.0, 1.0, 1.0, 1.0, 0.0}, {3.0, 4.0, 4.0, 1.0, 4.0, 1.0},
                {3.0, 2.0, 0.0, 1.0, 3.0, 1.0}, {3.0, 1.0, 3.0, 2.0, 2.0, 2.0}, {3.0, 0.0, 2.0, 0.0, 1.0, 1.0},
                {1.0, 4.0, 4.0, 1.0, 3.0, 4.0}, {3.0, 4.0, 3.0, 2.0, 1.0, 2.0}, {2.0, 2.0, 2.0, 4.0, 1.0, 4.0},
                {1.0, 4.0, 4.0, 2.0, 0.0, 4.0}, {3.0, 4.0, 3.0, 4.0, 0.0, 4.0}, {0.0, 1.0, 3.0, 2.0, 3.0, 1.0},
                {1.0, 4.0, 0.0, 4.0, 1.0, 4.0}, {3.0, 2.0, 3.0, 2.0, 0.0, 1.0}, {0.0, 1.0, 3.0, 1.0, 3.0, 4.0},
                {4.0, 0.0, 1.0, 3.0, 4.0, 2.0}, {4.0, 2.0, 3.0, 4.0, 4.0, 0.0}, {2.0, 0.0, 1.0, 4.0, 0.0, 2.0},
                {4.0, 2.0, 1.0, 2.0, 1.0, 1.0}, {2.0, 4.0, 1.0, 0.0, 1.0, 0.0}, {2.0, 3.0, 2.0, 0.0, 4.0, 4.0},
                {3.0, 0.0, 0.0, 2.0, 3.0, 1.0}, {4.0, 1.0, 0.0, 0.0, 1.0, 4.0}, {0.0, 4.0, 1.0, 2.0, 0.0, 4.0},
                {1.0, 3.0, 0.0, 0.0, 4.0, 0.0}, {3.0, 0.0, 2.0, 3.0, 0.0, 1.0}, {3.0, 4.0, 3.0, 1.0, 1.0, 4.0},
                {1.0, 1.0, 2.0, 3.0, 1.0, 1.0}, {4.0, 3.0, 0.0, 2.0, 1.0, 0.0}, {3.0, 4.0, 3.0, 2.0, 3.0, 3.0},
                {2.0, 2.0, 3.0, 4.0, 2.0, 4.0}, {3.0, 4.0, 3.0, 0.0, 0.0, 2.0}, {1.0, 0.0, 3.0, 4.0, 0.0, 0.0},
                {1.0, 4.0, 4.0, 2.0, 1.0, 0.0}, {4.0, 2.0, 2.0, 0.0, 1.0, 0.0}, {4.0, 4.0, 2.0, 1.0, 0.0, 4.0},
                {1.0, 4.0, 3.0, 1.0, 3.0, 0.0}, {4.0, 2.0, 1.0, 4.0, 0.0, 2.0}, {1.0, 0.0, 2.0, 3.0, 0.0, 0.0},
                {2.0, 2.0, 1.0, 4.0, 0.0, 4.0}, {0.0, 3.0, 1.0, 2.0, 0.0, 2.0}, {2.0, 0.0, 2.0, 4.0, 0.0, 2.0},
                {3.0, 2.0, 4.0, 4.0, 0.0, 1.0}, {2.0, 4.0, 3.0, 2.0, 1.0, 4.0}, {3.0, 4.0, 1.0, 4.0, 0.0, 4.0},
                {0.0, 1.0, 0.0, 3.0, 4.0, 0.0}, {4.0, 1.0, 0.0, 1.0, 2.0, 1.0}, {0.0, 3.0, 0.0, 2.0, 3.0, 3.0},
                {3.0, 4.0, 2.0, 2.0, 3.0, 2.0}, {2.0, 1.0, 0.0, 3.0, 4.0, 1.0}, {2.0, 4.0, 3.0, 1.0, 1.0, 3.0},
                {4.0, 1.0, 4.0, 2.0, 0.0, 1.0}, {0.0, 2.0, 4.0, 4.0, 1.0, 2.0}, {2.0, 0.0, 4.0, 0.0, 1.0, 4.0},
                {2.0, 0.0, 3.0, 2.0, 1.0, 2.0}, {2.0, 2.0, 1.0, 0.0, 3.0, 3.0}, {0.0, 0.0, 3.0, 2.0, 0.0, 1.0},
                {0.0, 0.0, 3.0, 1.0, 4.0, 1.0}, {3.0, 1.0, 3.0, 2.0, 3.0, 2.0}, {3.0, 1.0, 4.0, 4.0, 2.0, 1.0},
                {4.0, 4.0, 0.0, 1.0, 0.0, 2.0}, {4.0, 3.0, 4.0, 4.0, 2.0, 1.0}, {0.0, 4.0, 0.0, 3.0, 4.0, 0.0},
                {0.0, 0.0, 1.0, 4.0, 2.0, 0.0}, {2.0, 4.0, 4.0, 3.0, 4.0, 2.0}, {4.0, 4.0, 0.0, 0.0, 3.0, 2.0},
                {2.0, 3.0, 4.0, 4.0, 1.0, 1.0}, {3.0, 0.0, 3.0, 4.0, 3.0, 4.0}, {0.0, 2.0, 2.0, 3.0, 0.0, 0.0},
                {4.0, 4.0, 2.0, 3.0, 0.0, 1.0}, {4.0, 3.0, 0.0, 1.0, 0.0, 0.0}, {0.0, 4.0, 1.0, 2.0, 0.0, 1.0},
                {1.0, 2.0, 4.0, 4.0, 0.0, 1.0}, {0.0, 1.0, 3.0, 0.0, 3.0, 0.0}, {4.0, 1.0, 2.0, 1.0, 4.0, 1.0},
                {1.0, 2.0, 0.0, 1.0, 3.0, 0.0}, {1.0, 0.0, 1.0, 2.0, 3.0, 4.0}, {1.0, 0.0, 0.0, 3.0, 1.0, 2.0},
                {3.0, 3.0, 2.0, 2.0, 4.0, 2.0}, {2.0, 0.0, 3.0, 3.0, 1.0, 3.0}, {1.0, 1.0, 0.0, 3.0, 4.0, 1.0},
                {1.0, 0.0, 3.0, 1.0, 4.0, 0.0}, {1.0, 0.0, 3.0, 2.0, 4.0, 1.0}, {1.0, 3.0, 1.0, 3.0, 2.0, 3.0},
                {3.0, 4.0, 3.0, 4.0, 4.0, 0.0}, {2.0, 3.0, 3.0, 4.0, 0.0, 4.0}, {0.0, 3.0, 2.0, 0.0, 4.0, 1.0},
                {4.0, 2.0, 0.0, 3.0, 3.0, 2.0}, {0.0, 3.0, 2.0, 1.0, 1.0, 2.0}, {0.0, 2.0, 3.0, 0.0, 2.0, 2.0},
                {4.0, 2.0, 1.0, 3.0, 0.0, 0.0}, {1.0, 1.0, 1.0, 0.0, 0.0, 3.0}, {3.0, 0.0, 0.0, 4.0, 3.0, 2.0},
                {2.0, 2.0, 1.0, 0.0, 1.0, 3.0}, {2.0, 0.0, 3.0, 1.0, 3.0, 4.0}, {1.0, 0.0, 2.0, 4.0, 0.0, 3.0},
                {3.0, 2.0, 3.0, 0.0, 3.0, 1.0}, {3.0, 2.0, 0.0, 3.0, 4.0, 1.0}, {0.0, 3.0, 0.0, 0.0, 2.0, 3.0},
                {2.0, 2.0, 1.0, 0.0, 2.0, 3.0}, {0.0, 0.0, 3.0, 2.0, 2.0, 1.0}, {2.0, 0.0, 0.0, 1.0, 4.0, 0.0},
                {3.0, 3.0, 2.0, 1.0, 1.0, 4.0}, {2.0, 4.0, 2.0, 1.0, 1.0, 0.0}, {2.0, 3.0, 0.0, 0.0, 0.0, 3.0},
                {3.0, 4.0, 0.0, 3.0, 3.0, 3.0}, {2.0, 0.0, 3.0, 3.0, 4.0, 2.0}, {1.0, 2.0, 3.0, 1.0, 1.0, 1.0},
                {2.0, 2.0, 0.0, 2.0, 1.0, 4.0}, {3.0, 3.0, 2.0, 0.0, 4.0, 2.0}, {1.0, 3.0, 1.0, 2.0, 2.0, 2.0},
                {1.0, 4.0, 3.0, 2.0, 4.0, 1.0}, {4.0, 2.0, 2.0, 0.0, 2.0, 0.0}, {2.0, 2.0, 3.0, 4.0, 3.0, 3.0},
                {0.0, 0.0, 0.0, 3.0, 1.0, 3.0}, {2.0, 3.0, 1.0, 2.0, 0.0, 2.0}, {3.0, 3.0, 2.0, 2.0, 2.0, 0.0},
                {2.0, 4.0, 0.0, 1.0, 1.0, 2.0}, {3.0, 2.0, 3.0, 3.0, 2.0, 3.0}, {1.0, 3.0, 2.0, 2.0, 2.0, 4.0},
                {0.0, 3.0, 1.0, 3.0, 1.0, 1.0}, {3.0, 1.0, 1.0, 2.0, 4.0, 4.0}, {1.0, 0.0, 2.0, 4.0, 0.0, 2.0},
                {3.0, 3.0, 0.0, 0.0, 3.0, 3.0}, {0.0, 2.0, 0.0, 0.0, 1.0, 2.0}, {4.0, 4.0, 3.0, 2.0, 4.0, 2.0},
                {4.0, 0.0, 2.0, 3.0, 1.0, 3.0}, {2.0, 3.0, 4.0, 2.0, 3.0, 2.0}, {0.0, 1.0, 3.0, 2.0, 0.0, 1.0},
                {4.0, 3.0, 2.0, 1.0, 3.0, 0.0}, {2.0, 4.0, 2.0, 1.0, 4.0, 1.0}, {2.0, 3.0, 0.0, 3.0, 3.0, 3.0},
                {4.0, 0.0, 2.0, 4.0, 2.0, 0.0}, {0.0, 4.0, 3.0, 1.0, 0.0, 1.0}, {3.0, 0.0, 3.0, 4.0, 0.0, 1.0},
                {2.0, 3.0, 4.0, 2.0, 1.0, 2.0}, {1.0, 2.0, 2.0, 2.0, 3.0, 0.0}, {3.0, 3.0, 1.0, 2.0, 2.0, 4.0},
                {2.0, 1.0, 0.0, 1.0, 1.0, 1.0}, {2.0, 1.0, 2.0, 2.0, 4.0, 3.0}, {2.0, 3.0, 0.0, 1.0, 4.0, 2.0},
                {1.0, 3.0, 1.0, 0.0, 1.0, 2.0}, {1.0, 4.0, 4.0, 4.0, 2.0, 0.0}, {3.0, 0.0, 2.0, 1.0, 4.0, 3.0},
                {3.0, 4.0, 2.0, 4.0, 0.0, 2.0}, {4.0, 4.0, 2.0, 3.0, 0.0, 0.0}, {0.0, 1.0, 2.0, 2.0, 2.0, 1.0},
                {1.0, 0.0, 0.0, 4.0, 3.0, 1.0}, {0.0, 4.0, 1.0, 3.0, 4.0, 1.0}, {3.0, 0.0, 2.0, 3.0, 4.0, 4.0},
                {1.0, 0.0, 2.0, 1.0, 0.0, 0.0}, {3.0, 0.0, 0.0, 4.0, 1.0, 0.0},
        };
        int[] dataValues = {
                1932294274, 73106068, -309228262, 743621903, -893164842, -1657428397, 493317876, 662193827, 809639410,
                -109333599, 852848581, 1117905385, 738001195, 898610399, 903979415, 1014352668, -1159636832, -1553345971,
                -1164482815, 1389154134, -1818546545, 1712463896, -127970909, 1191285305, 1116444151, -570765657,
                1907718115, -1328079903, 313242228, -568252125, 349740518, -611728631, -276563625, -187691451,
                -440545838, -1376796804, -393844731, 1738461029, -561956030, 927789247, -1516393581, -283926081,
                -2131325430, -890788612, -790465121, 90770952, 1763700780, 1287810098, 1466137200, 1805257265,
                1081375515, -1369357412, 67359825, -1169643141, -1942233041, 594453855, -1327052758, -1349546308,
                -1591842712, 1605322872, 1861166945, -510227216, 1386063720, 439983332, 482593521, 1847009795,
                -507806293, 160976913, 13755336, -1289898018, -102611447, -912431739, -651617178, -1954490448,
                -781563282, 1651879886, 973916083, -1811956865, 2065554392, 155366826, -34563807, -1291982353,
                857647506, -916238474, 1888395778, 916572241, -1563675729, -85882660, -132673492, 33050351,
                1208251594, 846631609, 2087545291, 1139297175, 59580156, -226766515, 438583942, 1349770854,
                1407518750, 506614439, -1226070570, 1481055447, -1939168959, -1572325041, 1950790307, -1172371700,
                -403860118, 313386805, 1700275233, -2022950053, 1084681107, 383972570, 843236329, -453604, 1903445983,
                1596276925, -797572773, 1502288818, 2085627932, -828726520, 1081190963, 649331417, -207997057,
                1558712700, 1750794347, 1301185839, 1444418971, -1930477669, -194467504, 1513613317, -202888029,
                -488765676, 1811034600, 1538719591, 204305121, 2040079476, -384621437, -1695685271, 2068067190,
                991302666, -1701778528, -1133785163, -15386028, -1599202533, 692977155, 230207315, -1646395527,
                -1676228280, 2102136851, 1464108728, -1967018642, 129662524, 659703418, -205479407, 1173364480,
                -236601446, -721705481, -1232003631, 325398589, 1105488677, -629326713, 1400465273, -25952591,
                -1526984398,
        };
        int[] expectedQueryValues = {
                0, 0, 0, -1105443685, -1068408243, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1908123222, 0, -1967018642, 0, 0, 0,
                -1572325041, 0, 0, 0, 0, 0, 0, -154641100, 0, 0, 0, 0, -1956946478, 0, 0, 0, 0, 0, -1572325041, 0, 0,
                837889822, -514040963, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 429606977, 0, 0, 0, 0, -4778527, 0,
                -1572325041, 0, 0, 0, 0, -384621437, 0, 0, -1641620053, 1551238869, 0, 0, 0, 0, 258726287, 0, 0,
                1477469322, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, -384621437, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -590839885, 0, -188642374, 0, 0, 898610399, 0,
                1813907691, 0, 0, -1967018642, 0, 0, 0, -1714720331, 0, 797768817, 0, 898610399, -85882660, 0,
                -1526984398, 0, 0, 13755336, 0, 0, 0, -1572325041, 0, 0, -34563807, 0, 0, 0, 0, 0, 0, 1344795830, 0, 0,
        };
        boolean[] isStrict = {
                true, false, true, false, true, false,
        };
        boolean[] isQueryPoint = {
                false, false, false, true, true, true, false, false, false, true, true, true, true, false, true,
                false, true, false, false, false, true, false, false, true, true, true, true, true, false, false,
                false, true, true, true, true, false, true, true, true, true, true, true, true, true, true, true,
                true, true, false, false, false, true, true, false, false, true, true, true, true, false, true, false,
                true, true, false, true, true, true, false, false, true, true, false, false, true, true, true, false,
                false, true, false, false, true, false, false, true, false, true, true, true, false, false, false,
                true, true, false, false, false, false, true, true, false, true, true, false, true, true, true, false,
                false, false, true, true, true, false, false, false, false, false, true, false, false, false, true,
                false, true, false, false, true, true, true, false, true, true, false, true, true, true, false, true,
                true, true, true, true, true, true, false, true, false, false, true, true, false, true, true, true,
                true, true, false, false, false, true, true, true,
        };
        boolean[] isDataPoint = {
                true, false, false, false, true, false, true, true, false, false, true, false, false, true, true,
                false, false, true, false, true, true, true, true, true, false, false, false, true, false, false,
                true, false, false, true, true, true, false, true, true, false, true, false, true, false, true,
                true, true, true, true, false, false, true, false, false, true, false, false, true, true, true,
                true, false, true, false, false, true, true, false, true, false, false, true, false, true, false,
                false, false, false, false, true, true, false, true, true, false, false, true, true, true, true,
                true, true, true, false, true, false, true, true, false, true, false, false, true, true, false,
                false, false, false, true, true, false, false, true, false, true, true, false, false, false, false,
                true, true, false, false, false, false, true, true, false, false, true, false, false, false, false,
                false, true, true, false, true, false, false, true, true, true, false, true, true, true, false, true,
                true, true, false, true, false, false, false, true, false, true, false, false, true,
        };

        checkOneSet(points, dataValues, expectedQueryValues, isDataPoint, isQueryPoint, isStrict);
    }

    @Test
    public void smokeTest() {
        Random random = new Random(282354312242L);
        ValueTypeClass<int[]> tc = new SumTypeClass();
        OrthantSearch orthantSearch = getFactory().apply(200, 10);
        int[] additionalCollection = tc.createCollection(orthantSearch.getAdditionalCollectionSize(orthantSearch.getMaximumPoints()));

        for (int t = 0; t < 300; ++t) {
            int n = 30 + random.nextInt(150);
            int d = 1 + random.nextInt(6);
            double[][] points = new double[n][d];
            if (random.nextBoolean()) {
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
            int[] dataValues = new int[n];
            int[] queryValues = new int[n];
            int[] expectedQueryValues = new int[n];
            boolean[] isQueryPoint = new boolean[n];
            boolean[] isDataPoint = new boolean[n];
            for (int i = 0; i < n; ++i) {
                isDataPoint[i] = random.nextBoolean();
                isQueryPoint[i] = random.nextBoolean();
                dataValues[i] = random.nextInt();
            }
            boolean[] isStrict = new boolean[d];
            for (int i = 0; i < d; ++i) {
                isStrict[i] = random.nextBoolean();
            }

            orthantSearch.runSearch(points, dataValues.clone(), queryValues, 0, n,
                    isDataPoint, isQueryPoint, additionalCollection, tc, isStrict);
            runSearch(points, dataValues.clone(), expectedQueryValues, isDataPoint, isQueryPoint, isStrict);
            if (!Arrays.equals(expectedQueryValues, queryValues)) {
                System.out.println("double[][] points = {");
                for (int i = 0; i < n; ++i) {
                    System.out.print("    {");
                    for (int j = 0; j < d; ++j) {
                        System.out.print(points[i][j]);
                        if (j + 1 == d) {
                            System.out.println("},");
                        } else {
                            System.out.print(", ");
                        }
                    }
                }
                System.out.println("};");
                System.out.println("int[] dataValues = {");
                System.out.print("    ");
                for (int i = 0; i < n; ++i) {
                    System.out.print(dataValues[i]);
                    System.out.print(", ");
                }
                System.out.println();
                System.out.println("};");
                System.out.println("int[] expectedQueryValues = {");
                System.out.print("    ");
                for (int i = 0; i < n; ++i) {
                    System.out.print(expectedQueryValues[i]);
                    System.out.print(", ");
                }
                System.out.println();
                System.out.println("};");
                System.out.println("boolean[] isStrict = {");
                System.out.print("    ");
                for (int i = 0; i < d; ++i) {
                    System.out.print(isStrict[i]);
                    System.out.print(", ");
                }
                System.out.println();
                System.out.println("};");
                System.out.println("boolean[] isQueryPoint = {");
                System.out.print("    ");
                for (int i = 0; i < n; ++i) {
                    System.out.print(isQueryPoint[i]);
                    System.out.print(", ");
                }
                System.out.println();
                System.out.println("};");
                System.out.println("boolean[] isDataPoint = {");
                System.out.print("    ");
                for (int i = 0; i < n; ++i) {
                    System.out.print(isDataPoint[i]);
                    System.out.print(", ");
                }
                System.out.println();
                System.out.println("};");
            }
            Assert.assertArrayEquals(expectedQueryValues, queryValues);
        }
    }

    private static boolean dominates(double[] good, double[] weak, boolean[] isStrict) {
        if (Arrays.equals(good, weak)) {
            return false;
        }
        int d = good.length;
        for (int i = 0; i < d; ++i) {
            if (isStrict[i] ? good[i] >= weak[i] : good[i] > weak[i]) {
                return false;
            }
        }
        return true;
    }

    private static void runSearch(double[][] points, int[] dataValues, int[] queryValues,
                                  boolean[] isDataPoint, boolean[] isQueryPoint, boolean[] isStrict) {
        int n = points.length;
        PointWrapper[] wrappers = new PointWrapper[n];
        for (int i = 0; i < n; ++i) {
            wrappers[i] = new PointWrapper(i, points[i]);
        }
        Arrays.sort(wrappers);
        Arrays.fill(queryValues, 0);
        for (int q = 0; q < n; ++q) {
            PointWrapper wq = wrappers[q];
            int qi = wq.index;
            if (isQueryPoint[qi]) {
                for (int p = 0; p < q; ++p) {
                    PointWrapper wp = wrappers[p];
                    int pi = wp.index;
                    if (isDataPoint[pi] && dominates(wp.point, wq.point, isStrict)) {
                        queryValues[qi] += dataValues[pi];
                    }
                }
                if (isDataPoint[qi]) {
                    dataValues[qi] += queryValues[qi];
                }
            }
        }
    }

    private static class SumTypeClass extends ValueTypeClass<int[]> {
        @Override
        public int[] createCollection(int size) {
            return new int[size];
        }

        @Override
        public int size(int[] collection) {
            return collection.length;
        }

        @Override
        public void fillWithZeroes(int[] collection, int from, int until) {
            Arrays.fill(collection, from, until, 0);
        }

        @Override
        public void add(int[] source, int sourceIndex, int[] target, int targetIndex) {
            target[targetIndex] += source[sourceIndex];
        }

        @Override
        public void queryToData(int[] source, int sourceIndex, int[] target) {
            target[sourceIndex] += source[sourceIndex];
        }
    }

    private static class PointWrapper implements Comparable<PointWrapper> {
        private int index;
        private double[] point;

        private PointWrapper(int index, double[] point) {
            this.index = index;
            this.point = point;
        }

        @Override
        public int compareTo(PointWrapper o) {
            double[] l = point, r = o.point;
            int d = l.length;
            for (int i = 0; i < d; ++i) {
                int cmp = Double.compare(l[i], r[i]);
                if (cmp != 0) {
                    return cmp;
                }
            }
            return 0;
        }
    }
}
