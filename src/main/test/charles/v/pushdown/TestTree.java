package charles.v.pushdown;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.PredicateUtils;
import org.apache.commons.collections4.functors.TruePredicate;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestTree {
    @Test
    public void testTree() {
        TreeNode<String, TreeNode.Value> root = new TreeNode<>(null, null);
        for (int lA = 0; lA < 3; lA++) {
            String idlA = "LA" + lA;
            TreeNode<String, TreeNode.Value> lANode = new TreeNode<>(idlA, root);
            root.childNodes.put(idlA, lANode);

            for (int lB = 0; lB < 3; lB++) {
                String idlB = "LB" + lB;
                TreeNode<String, TreeNode.Value> lBNode = new TreeNode<>(idlB, lANode);
                lANode.childNodes.put(idlB, lBNode);

                for (int lC = 0; lC < 3; lC++) {
                    String idlC = "LC" + lC;
                    TreeNode<String, TreeNode.Value> lCNode = new TreeNode<>(idlC, lBNode);
                    lBNode.childNodes.put(idlC, lCNode);
                }
            }
        }

        List<Predicate<TreeNode<String, TreeNode.Value>>> levelPredicates = new ArrayList<>();
//*/
        // case1 (LA1, LA2) (LC1, LC2)
        List<Predicate<TreeNode<String, TreeNode.Value>>> laList = new ArrayList<>();
        laList.add(TreeNodeUtil.<String, TreeNode.Value>idPredicate("LA1"));
        laList.add(TreeNodeUtil.<String, TreeNode.Value>idPredicate("LA2"));

        levelPredicates.add(PredicateUtils.anyPredicate(laList));

        levelPredicates.add(TruePredicate.<TreeNode<String, TreeNode.Value>>truePredicate());

        List<Predicate<TreeNode<String, TreeNode.Value>>> lcList = new ArrayList<>();
        lcList.add(TreeNodeUtil.<String, TreeNode.Value>idPredicate("LC1"));
        lcList.add(TreeNodeUtil.<String, TreeNode.Value>idPredicate("LC2"));

        levelPredicates.add(PredicateUtils.anyPredicate(lcList));
/*/
        // case2 (LA1, LA2) (LC1, LC2) 但是 LA1-LB1-LC1 组合不存在POS
        List<Predicate<charles.v.pushdown.TreeNode<String, charles.v.pushdown.TreeNode.Value>>> laList = new ArrayList<Predicate<charles.v.pushdown.TreeNode<String, charles.v.pushdown.TreeNode.Value>>>();
        laList.add(charles.v.pushdown.TreeNodeUtil.<String, charles.v.pushdown.TreeNode.Value>idPredicate("LA1"));
        laList.add(charles.v.pushdown.TreeNodeUtil.<String, charles.v.pushdown.TreeNode.Value>idPredicate("LA2"));

        levelPredicates.add(PredicateUtils.anyPredicate(laList));

        levelPredicates.add(TruePredicate.<charles.v.pushdown.TreeNode<String, charles.v.pushdown.TreeNode.Value>>truePredicate());

        List<Predicate<charles.v.pushdown.TreeNode<String, charles.v.pushdown.TreeNode.Value>>> lcList = new ArrayList<Predicate<charles.v.pushdown.TreeNode<String, charles.v.pushdown.TreeNode.Value>>>();
        lcList.add(charles.v.pushdown.TreeNodeUtil.<String, charles.v.pushdown.TreeNode.Value>idPredicate("LC1"));
        lcList.add(charles.v.pushdown.TreeNodeUtil.<String, charles.v.pushdown.TreeNode.Value>idPredicate("LC2"));

        levelPredicates.add(
                PredicateUtils.andPredicate(
                        PredicateUtils.anyPredicate(lcList),
                        NotPredicate.notPredicate(charles.v.pushdown.TreeNodeUtil.pathPredicate(Arrays.asList("LA1", "LB1", "LC1"))))
        );
//*/
        TreeNodeUtil.ConvertContext<String, TreeNode.Value> ctx = TreeNodeUtil.convertContext(Arrays.asList("A", "B", "C"), Arrays.asList("C", "A", "B"));

        TreeNodeUtil.leaf(root, ctx, 0, levelPredicates);
        System.out.println(ctx.root);
    }
}
