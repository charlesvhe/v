package charles.v.pushdown;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ZipValueTest {

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {

    }

    private void assertZipValueEquals(long[] data, ZipValue zv) {
        assertArrayEquals(data, zv.getValue());
        for (int i = 0; i < data.length; i++) {
            assertEquals(data[i], zv.getValue(i));
        }
    }

    @Test
    public void testPerformance() {
        long[] data = new long[30];
        ZipValue zv = new ZipValue(data);

        long startPut = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            int index = (int) (Math.random() * 30);
            long val = (long) (Math.random() * Short.MAX_VALUE);
            val = val * val * val * val;
            zv.putValue(val, index);
        }
        long endPut = System.currentTimeMillis();

        long startRandom = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            int index = (int) (Math.random() * 30);
            long val = (long) (Math.random() * Short.MAX_VALUE);
            val = val * val * val * val;
        }
        long endRandom = System.currentTimeMillis();

        assertThat("1,000,000 value put time", (endPut - startPut)-(endRandom-startRandom), lessThan(200L));
    }

    @Test
    public void testGetValue() {
        // full fill byte meta
        long[] data = new long[]{
                -0, 0,
                Short.MIN_VALUE, Short.MAX_VALUE,
                Integer.MIN_VALUE, Integer.MAX_VALUE,
                Long.MIN_VALUE, Long.MAX_VALUE
        };
        ZipValue zv = new ZipValue(data);
        this.assertZipValueEquals(data, zv);

        // partial fill byte meta
        data = new long[]{
                0,
                Short.MIN_VALUE, Short.MAX_VALUE,
                Integer.MIN_VALUE, Integer.MAX_VALUE,
                Long.MIN_VALUE, Long.MAX_VALUE
        };
        zv = new ZipValue(data);
        this.assertZipValueEquals(data, zv);
    }


    @Test
    public void testPutValue() {
        long[] data = new long[]{0, 0, 0};
        ZipValue zv = new ZipValue(data);
        assertZipValueEquals(data, zv);

        List<Long> permutationsData = new ArrayList<Long>();
        permutationsData.add(-0L);
        permutationsData.add(0L);
        permutationsData.add((long) Short.MIN_VALUE);
        permutationsData.add((long) Short.MAX_VALUE);
        permutationsData.add((long) Integer.MIN_VALUE);
        permutationsData.add((long) Integer.MAX_VALUE);
        permutationsData.add(Long.MIN_VALUE);
        permutationsData.add(Long.MAX_VALUE);
        Collection<List<Long>> fullPermutations = CollectionUtils.permutations(permutationsData);

        Set<List<Long>> threeDataPermutations = new HashSet<List<Long>>();
        for (List<Long> fullPermutation : fullPermutations) {
            threeDataPermutations.add(fullPermutation.subList(0, 3));
        }

        for (List<Long> threeDataPermutation : threeDataPermutations) {
            data = new long[]{0, 0, 0};
            zv = new ZipValue(data);
            int index = 0;
            for (Long val : threeDataPermutation) {
                data[index] = val;
                zv.putValue(val, index);
                index++;
            }
            assertZipValueEquals(data, zv);
        }
    }
}