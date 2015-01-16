package charles.v.commons.collections;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.Predicate;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestList {
    @Test
    public void testList(){
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(i*2);
        }

        Predicate<Integer> predicate = new Predicate<Integer>() {
            @Override
            public boolean evaluate(Integer object) {
                return object % 2 == 0;
            }
        };
        CollectionUtils.filter(list,predicate);
        System.out.println(list);

        List<Integer> predicateList = ListUtils.predicatedList(list, predicate);

        predicateList.add(2);
//        predicateList.add(3);
        System.out.println(predicateList);
    }
}
