package charles.v.commons;

import charles.v.pushdown.TreeNode;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Charles on 2015/7/2.
 */
public class TestJson {
    TreeNode.Value value = new TreeNode.Value() {
        public void aggregate(TreeNode.Value that, boolean clone) {
        }

        public boolean isEmpty() {
            return false;
        }

        @Override
        public TreeNode.Value clone() {
            return this;
        }
    };
    TreeNode<String, TreeNode.Value> root = null;
    @Before
    public void setUp() {
        root = new TreeNode<String, TreeNode.Value>(null, null);
        for (int lA = 0; lA < 3; lA++) {
            String idlA = "LA" + lA;
            TreeNode<String, TreeNode.Value> lANode = new TreeNode<String, TreeNode.Value>(idlA, root);
            root.childNodes.put(idlA, lANode);

            for (int lB = 0; lB < 3; lB++) {
                String idlB = "LB" + lB;
                TreeNode<String, TreeNode.Value> lBNode = new TreeNode<String, TreeNode.Value>(idlB, lANode);
                lANode.childNodes.put(idlB, lBNode);

                for (int lC = 0; lC < 3; lC++) {
                    String idlC = "LC" + lC;
                    TreeNode<String, TreeNode.Value> lCNode = new TreeNode<String, TreeNode.Value>(idlC, lBNode);
                    lCNode.value = this.value;
                    lBNode.childNodes.put(idlC, lCNode);
                }
            }
        }
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testToJson() {
        Object pathObject = JSONPath.eval(root, "$.childNodes.*[?(id)].id");

        System.out.println(pathObject);
    }
}
