package charles.v.pushdown;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestPushDown {
    @Test
    public void testPushDown() {
        PushDown pd = new PushDown();
        List<PushDown.ValueUnit> valList = new ArrayList<PushDown.ValueUnit>();
        long ttl = 0;
        long targetVal = 100;
        for (int i = 0; i < 1000000; i++) {
            PushDown.ValueUnit vu = pd.new ValueUnit((long)(Math.random()*1000));
            valList.add(vu);
            ttl += vu.getValue();
        }

        long startPushDown = System.currentTimeMillis();
        pd.pushDown(targetVal, valList, ttl);
        long endPushDown = System.currentTimeMillis();
        assertTrue("1,000,000 push down less then 100 ms", (endPushDown - startPushDown) < 100);

        long actualTtl = 0;
        for (PushDown.ValueUnit vn : valList) {
            actualTtl += vn.getValue();
        }
        assertEquals("push down value equal", targetVal, actualTtl);
    }

    @Test
     public void testRoundAdjustUp() {
        PushDown pd = new PushDown();
        List<PushDown.ValueUnit> valList = new ArrayList<PushDown.ValueUnit>();
        long ttl = 9;
        long targetVal = 100;

        valList.add(pd.new ValueUnit(1));   // 100 * 1/9 = 11.11 -> 11
        valList.add(pd.new ValueUnit(2));   // 100 * 2/9 = 22.22 -> 22
        valList.add(pd.new ValueUnit(3));   // 100 * 3/9 = 33.33 -> 33 -> 34
        valList.add(pd.new ValueUnit(3));   // 100 * 3/9 = 33.33 -> 33 or -> 34

        pd.pushDown(targetVal, valList, ttl);

        assertEquals("value 1", 11, valList.get(0).getValue());
        assertEquals("value 2", 22, valList.get(1).getValue());
        assertTrue("value 3 or value 4 = 34", ((valList.get(2).getValue() == 34) || (valList.get(3).getValue() == 34)));
    }

    @Test
    public void testRoundAdjustDown() {
        PushDown pd = new PushDown();
        List<PushDown.ValueUnit> valList = new ArrayList<PushDown.ValueUnit>();
        long ttl = 8;
        long targetVal = 100;
        valList.add(pd.new ValueUnit(1));   // 100 * 1/8 = 12.5 -> 13 -> 12
        valList.add(pd.new ValueUnit(2));   // 100 * 2/8 = 25   -> 25
        valList.add(pd.new ValueUnit(2));   // 100 * 2/8 = 25   -> 25
        valList.add(pd.new ValueUnit(3));   // 100 * 3/8 = 37.5 -> 38 or -> 37

        pd.pushDown(targetVal, valList, ttl);

        assertEquals("value 2", 25, valList.get(1).getValue());
        assertEquals("value 3", 25, valList.get(2).getValue());
        assertTrue("value 1 = 12 or value 4 = 37", ((valList.get(0).getValue() == 12) || (valList.get(3).getValue() == 37)));
    }
}
