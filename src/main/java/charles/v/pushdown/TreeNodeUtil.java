package charles.v.pushdown;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.functors.TruePredicate;

import java.util.ArrayList;
import java.util.List;


public class TreeNodeUtil {
    public static final TreeNodeUtil INSTANCE = new TreeNodeUtil();

    public static <K, V extends TreeNode.Value> void leaf(TreeNode<K, V> node,
                                                          LeafContext<K, V> ctx,
                                                          int level,
                                                          List<Predicate<TreeNode<K, V>>> levelPredicates) {
        if (node.isValueLeaf()) {
            ctx.processLeaf(node);
        } else {
            int nextLevel = level + 1;
            // default true
            Predicate<TreeNode<K, V>> curLevelPredicate =
                    (null != levelPredicates && levelPredicates.size() > level) ? levelPredicates.get(level)
                            : TruePredicate.<TreeNode<K, V>>truePredicate();

            for (TreeNode<K, V> childNode : node.childNodes.values()) {
                if (curLevelPredicate.evaluate(childNode)) {
                    leaf(childNode, ctx, nextLevel, levelPredicates);
                }
            }
        }
    }


    public static <K, V extends TreeNode.Value> Predicate<TreeNode<K, V>> idPredicate(final String id) {
        return new Predicate<TreeNode<K, V>>() {
            @Override
            public boolean evaluate(TreeNode<K, V> node) {
                return null == id ? node.id == null : id.equals(node.id);
            }
        };
    }

    public static <K, V extends TreeNode.Value> Predicate<TreeNode<K, V>> pathPredicate(final List<K> path) {
        return new Predicate<TreeNode<K, V>>() {
            @Override
            public boolean evaluate(TreeNode<K, V> node) {
                return path.equals(node.getPath());
            }
        };
    }


    public static <K, V extends TreeNode.Value> ConvertContext<K, V> convertContext(List<String> srcLevel, List<String> destLevel) {
        List<Integer> indexList = new ArrayList<Integer>();
        for (String level : destLevel) {
            int index = srcLevel.indexOf(level);
            if (index < 0) {
                throw new IllegalArgumentException();
            }
            indexList.add(index);
        }
        return INSTANCE.new ConvertContext<K, V>(indexList);
    }

    public interface LeafContext<K, V extends TreeNode.Value> {
        public void processLeaf(TreeNode<K, V> leaf);
    }

    public class ConvertContext<K, V extends TreeNode.Value> implements LeafContext<K, V> {
        public TreeNode<K, V> root = new TreeNode<K, V>(null, null);
        public List<Integer> indexList;

        public ConvertContext(List<Integer> indexList) {
            this.indexList = indexList;
        }

        @Override
        public void processLeaf(TreeNode<K, V> leaf) {
            List<K> srcPath = leaf.getPath();
            List<K> destPath = new ArrayList<K>();
            for (int index : indexList) {
                destPath.add(srcPath.get(index));
            }
            System.out.println(srcPath + " -> " + destPath);
            // add to root with new destPath
            TreeNode<K, V> child = root.getChild(destPath, true);
            if(child.value == null){
                child.value = leaf.value.clone();
            }else{
                child.value.aggregate(leaf.value, true);
            }
        }
    }
}


