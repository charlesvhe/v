package charles.v.pushdown;

import java.util.*;

public class PushDown {
    public class ValueUnit{
        private long value;

        public ValueUnit(long value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "ValueUnit{" +
                    "v=" + value +
                    '}';
        }

        public long getValue() {
            return this.value;
        }

        public void setValue(long value) {
            this.value = value;
        }

        public long setValue(double value) {
            this.value = Math.round(value);
            // improve performance, use 1 digit error for sort
            return  Math.round((this.value - value)*10);
        }
    }

    public static double division(long val1, long val2){
        if(0 == val1 || 0 == val2){
            return 0;
        }else{
            return ((double)val1)/val2;
        }
    }

    public void pushDown(long targetVal, List<ValueUnit> valList, long ttlVal) {
        double even = PushDown.division(targetVal, valList.size());
        long actualVal = 0;
        // distribute by ratio
        TreeMap<Long, List<ValueUnit>> errorMap = new TreeMap<Long, List<ValueUnit>>();
        for (ValueUnit vn : valList) {
            long error = 0;
            if (0 == ttlVal) {
                error = vn.setValue(even);
            } else {
                error = vn.setValue(targetVal * PushDown.division(vn.getValue(), ttlVal));
            }
            actualVal += vn.getValue();

            List<ValueUnit> errorList = errorMap.get(error);
            if(null == errorList){
                errorList = new ArrayList<ValueUnit>();
                errorMap.put(error, errorList);
            }
            errorList.add(vn);
        }
        // adjust error
        if (actualVal != targetVal) {
            long error = targetVal - actualVal;
            int diff = error > 0 ? 1 : -1;
            while (0 != error){
                List<ValueUnit> errorList = null;
                if(diff > 0){
                    errorList = errorMap.pollFirstEntry().getValue();
                }else{
                    errorList = errorMap.pollLastEntry().getValue();
                }
                for (ValueUnit vu : errorList) {
                    vu.setValue(vu.getValue()+diff);
                    error -= diff;
                    if(0 == error){
                        break;
                    }
                }
            }
        }
    }
}
