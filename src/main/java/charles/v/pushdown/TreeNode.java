package charles.v.pushdown;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TreeNode<K, V extends TreeNode.Value> implements Cloneable {
    public K id;
    public TreeNode<K, V> parent;
    public HashMap<K, TreeNode<K, V>> childNodes = new HashMap<K, TreeNode<K, V>>();
    public V value;

    public TreeNode(K id, TreeNode<K, V> parent) {
        this.id = id;
        this.parent = parent;
    }

    public List<K> getPath() {
        LinkedList<K> path = new LinkedList<K>();
        TreeNode<K, V> curNode = this;
        while (curNode.parent != null) {
            path.addFirst(curNode.id);
            curNode = curNode.parent;
        }
        return path;
    }

    public TreeNode<K, V> getChild(K id, boolean create) {
        TreeNode<K, V> child = this.childNodes.get(id);
        if (child == null && create) {
            child = new TreeNode<K, V>(id, this);
            this.childNodes.put(id, child);
        }
        return child;
    }

    public TreeNode<K, V> getChild(List<K> path, boolean create) {
        TreeNode<K, V> node = this;
        for (K id : path) {
            node = node.getChild(id, create);
            if (null == node) {
                break;
            }
        }
        return node;
    }

    public void removeChild(K id) {
        this.childNodes.remove(id);
        if (this.isEmptyNode() && (null != this.parent)) {
            this.parent.removeChild(this.id);
        }
    }

    public void removeChild(List<K> path) {
        TreeNode<K, V> child = this.getChild(path, false);
        if (child != null && child.parent != null) {
            child.parent.removeChild(child.id);
        }
    }

    public void mergeChild(TreeNode<K, V> child, boolean override, boolean aggregate) {
        if (this.childNodes.containsKey(child.id)) {
            TreeNode<K, V> thisChild = this.childNodes.get(child.id);
            if (child.isValueLeaf()) { // leaf
                if (aggregate) {
                    thisChild.value.aggregate(child.value, true);
                } else if (override) {
                    thisChild.value = child.value.clone();
                }
            } else {
                for (Map.Entry<K, TreeNode<K, V>> childEntry : child.childNodes.entrySet()) {
                    thisChild.mergeChild(childEntry.getValue(), override, aggregate);
                }
            }
        } else {
            TreeNode<K, V> clone = child.clone();
            clone.parent = this;
            this.childNodes.put(clone.id, clone);
        }
    }

    public boolean isEmptyNode() {
        return (null == this.childNodes || this.childNodes.isEmpty()) && (this.value == null || this.value.isEmpty());
    }

    public boolean isValueLeaf() {
        return (this.childNodes == null || this.childNodes.isEmpty()) && (this.value != null && !this.value.isEmpty());
    }

    @Override
    public TreeNode<K, V> clone() {
        TreeNode<K, V> clone = new TreeNode<K, V>(this.id, this.parent);
        if (this.childNodes != null && !this.childNodes.isEmpty()) {
            for (Map.Entry<K, TreeNode<K, V>> childEntry : this.childNodes.entrySet()) {
                clone.childNodes.put(childEntry.getKey(), childEntry.getValue().clone());
            }
        }
        if (this.value != null) {
            clone.value = this.value.clone();
        }
        return clone;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        TreeNode<K, V> curNode = this;
        while (curNode.parent != null){
            sb.append("|\t");
            curNode = curNode.parent;
        }
        sb.append(this.id);
        sb.append(" : ");
        sb.append(this.value);
        sb.append("\n");
        for (Map.Entry<K,TreeNode<K,V>> childEntry : this.childNodes.entrySet()) {
            sb.append(childEntry.getValue());
        }
        return sb.toString();
    }

    public interface Value extends Cloneable {
        public void aggregate(Value that, boolean clone);

        public <V extends Value> V clone();

        public boolean isEmpty();
    }
}
