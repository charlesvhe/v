package charles.v.pushdown;


import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class TestPushDown {
    @Test
    public void testPushDown() {
        PushDown pd = new PushDown();
        List<PushDown.ValueUnit> valList = new ArrayList<PushDown.ValueUnit>();
        long ttl = 0;
        long targetVal = 100;
        for (int i = 0; i < 1000000; i++) {
            PushDown.ValueUnit vu = pd.new ValueUnit((long) (Math.random() * 1000));
            valList.add(vu);
            ttl += vu.getValue();
        }

        long startPushDown = System.currentTimeMillis();
        pd.pushDown(targetVal, valList, ttl);
        long endPushDown = System.currentTimeMillis();
        assertThat("1,000,000 value push down time", (endPushDown - startPushDown), lessThan(100L));

        long actualTtl = 0;
        for (PushDown.ValueUnit vn : valList) {
            actualTtl += vn.getValue();
        }
        assertEquals("push down target and actual", targetVal, actualTtl);
    }

    @Test
    public void testRoundAdjustUp() {
        PushDown pd = new PushDown();
        List<PushDown.ValueUnit> valList = new ArrayList<PushDown.ValueUnit>();
        long ttl = 9;
        long targetVal = 100;

        valList.add(pd.new ValueUnit(1));   // 100 * 1/9 = 11.11 -> 11
        valList.add(pd.new ValueUnit(2));   // 100 * 2/9 = 22.22 -> 22
        valList.add(pd.new ValueUnit(3));   // 100 * 3/9 = 33.33 -> 33 or -> 34
        valList.add(pd.new ValueUnit(3));   // 100 * 3/9 = 33.33 -> 33 or -> 34

        pd.pushDown(targetVal, valList, ttl);

        assertEquals(11, valList.get(0).getValue());
        assertEquals(22, valList.get(1).getValue());
        assertThat(valList.get(2).getValue(), anyOf(equalTo(33L), equalTo(34L)));
        assertThat(valList.get(3).getValue(), anyOf(equalTo(33L), equalTo(34L)));
    }

    @Test
    public void testRoundAdjustDown() {
        PushDown pd = new PushDown();
        List<PushDown.ValueUnit> valList = new ArrayList<PushDown.ValueUnit>();
        long ttl = 8;
        long targetVal = 100;
        valList.add(pd.new ValueUnit(1));   // 100 * 1/8 = 12.5 -> 13 or -> 12
        valList.add(pd.new ValueUnit(2));   // 100 * 2/8 = 25   -> 25
        valList.add(pd.new ValueUnit(2));   // 100 * 2/8 = 25   -> 25
        valList.add(pd.new ValueUnit(3));   // 100 * 3/8 = 37.5 -> 38 or -> 37

        pd.pushDown(targetVal, valList, ttl);

        assertThat(valList.get(0).getValue(), anyOf(equalTo(12L), equalTo(13L)));
        assertEquals(25, valList.get(1).getValue());
        assertEquals(25, valList.get(2).getValue());
        assertThat(valList.get(3).getValue(), anyOf(equalTo(37L), equalTo(38L)));
    }
}
