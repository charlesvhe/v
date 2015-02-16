package charles.v.pushdown;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TreeNodeTest {
    TreeNode.Value value = new TreeNode.Value() {
        @Override
        public void aggregate(TreeNode.Value that, boolean clone) {
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public TreeNode.Value clone() {
            return this;
        }
    };

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testTreeStructure() {
        TreeNode<String, TreeNode.Value> src = new TreeNode<String, TreeNode.Value>(null, null);
        List<String> path = new ArrayList<String>();
        path.add("A1");
        path.add("B1");
        path.add("C1");

        TreeNode<String, TreeNode.Value> child = src.getChild(path, false);
        assertNull(child);

        child = src.getChild(path, true);
        assertNotNull(child);
        assertEquals(path, child.getPath());

        assertFalse(src.isEmptyNode());
        src.removeChild(path);
        assertTrue(src.isEmptyNode());

        child = src.getChild(path, true);
        child.value = this.value;
        assertTrue(child.isValueLeaf());

        TreeNode<String, TreeNode.Value> clone = src.clone();
        assertEquals(src.toString(), clone.toString());

        TreeNode<String, TreeNode.Value> dest = new TreeNode<String, TreeNode.Value>(null, null);
        List<String> pathDest = new ArrayList<String>();
        pathDest.add("A1");
        pathDest.add("B1");
        pathDest.add("C2");
        TreeNode<String, TreeNode.Value> childDest = dest.getChild(pathDest, true);
        childDest.value = this.value;

        dest.mergeChild(src.getChild("A1", false), false, false);
        childDest = dest.getChild(path, false);
        assertNotNull(childDest);

    }
}