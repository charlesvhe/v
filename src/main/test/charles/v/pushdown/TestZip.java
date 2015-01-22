package charles.v.pushdown;

import org.junit.Test;

import java.util.Arrays;

public class TestZip {
    @Test
    public void testZip(){
        long[] data = new long[]{1,2,3,4,5,6,7,8,9,10};
        ZipValue val = new ZipValue(data);
        System.out.println(Arrays.toString(val.getValue()));
    }
}
