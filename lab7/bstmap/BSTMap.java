package bstmap;

import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    // Public interface

    /**
     * Removes all of the mappings from this map.
     */
    public void clear() {
    }

    /* Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key) {
        return false;
    }

    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    public V get(K key) {
        return null;
    }

    /* Returns the number of key-value mappings in this map. */
    public int size() {
        return 0;
    }

    /* Associates the specified value with the specified key in this map. */
    public void put(K key, V value) {
    }

    /* Returns a Set view of the keys contained in this map. */
    public Set<K> keySet() {
        return null;
    }

    /* Removes the mapping for the specified key from this map if present. */
    public V remove(K key) {
        return null;
    }

    /* Removes the entry for the specified key only if it is currently mapped to
     * the specified value. */
    public V remove(K key, V value) {
        return null;
    }

    public Iterator<K> iterator() {
        return null;
    }

    // Constructor
    public BSTMap() {
        // Create sentinel node
        sentinel_ = makeSentinelNode();
        root_ = sentinel_;
        reset();
    }

    // Private methods
    private static class BSTNode<K, V> {
        BSTNode<K, V> left_;
        BSTNode<K, V> right_;
        K key_;
        V value_;
    }

    private int size_ = 0;
    private BSTNode<K, V> root_;
    // Sentinel node attaches to leaf nodes
    private BSTNode<K, V> sentinel_;
    private Stack<BSTNode<K, V>> refStack_;

    // Private methods
    private BSTNode<K, V> makeNode(K k, V v) {
        BSTNode<K, V> node = new BSTNode<>();
        node.left_ = sentinel_;
        node.right_ = sentinel_;
        node.key_ = k;
        node.value_ = v;
        return node;
    }

    private BSTNode<K, V> makeSentinelNode() {
        BSTNode<K, V> node = new BSTNode<>();
        node.left_ = null;
        node.right_ = null;
        node.key_ = null;
        node.value_ = null;
        return node;
    }

    /**
     * Reset tree traversal state
     */
    private void reset() {
        refStack_ = new Stack<>();
        refStack_.push(root_);
    }

    private K getKey() {
        return refStack_.peek().key_;
    }

    private V getValue() {
        return refStack_.peek().value_;
    }

    /**
     * Descend the tree in the specified direction
     *
     * @param dir false for left, true for right
     * @return true on success, false on failure
     */
    private boolean descend(boolean dir) {
        var ref = refStack_.peek();
        if (ref == sentinel_) {
            return false;
        }
        if (dir) {
            refStack_.push(ref.right_);
        } else {
            refStack_.push(ref.left_);
        }
        return true;
    }

    /**
     * Ascend the tree
     * @return true on success, false on failure
     */
    private boolean ascend() {
        if (refStack_.size() == 1) {
            return false;
        }
        refStack_.pop();
        return true;
    }


}
