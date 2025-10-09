package bstmap;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    // Public interface

    /**
     * Removes all of the mappings from this map.
     */
    public void clear() {
        root_ = sentinel_;
    }

    /* Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key) {
        return find(key) != sentinel_;
    }

    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    public V get(K key) {
        BSTNode<K, V> res = find(key);
        if (res == sentinel_) {
            return null;
        } else {
            return res.value_;
        }
    }

    /* Returns the number of key-value mappings in this map. */
    public int size() {
        return root_.size_;
    }

    /* Associates the specified value with the specified key in this map. */
    public void put(K key, V value) {
        sentinel_.key_ = key;
        root_ = insertRec(root_, key, value);
    }

    /* Returns a Set view of the keys contained in this map. */
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    /* Removes the mapping for the specified key from this map if present. */
    public V remove(K key) {
        BSTNode<K, V> res = find(key);
        if (res == sentinel_) {
            return null;
        }
        sentinel_.key_ = key;
        root_ = removeRec(root_, key);
        return res.value_;
    }

    /* Removes the entry for the specified key only if it is currently mapped to
     * the specified value. */
    public V remove(K key, V value) {
        BSTNode<K, V> res = find(key);
        if (res == sentinel_ || res.value_ != value) {
            return null;
        }
        sentinel_.key_ = key;
        root_ = removeRec(root_, key);
        return res.value_;
    }

    public Iterator<K> iterator() {
        return null;
    }

    // Constructor
    public BSTMap() {
        sentinel_ = makeSentinelNode();
        root_ = sentinel_;
    }

    // Static
    private static class BSTNode<K, V> {
        BSTNode<K, V> left_;
        BSTNode<K, V> right_;
        K key_;
        V value_;
        int size_;
    }

    private static <K, V> BSTNode<K, V> makeLeafNode(K key, V value, BSTNode<K, V> sentinel) {
        BSTNode<K, V> node = new BSTNode<>();
        node.left_ = sentinel;
        node.right_ = sentinel;
        node.key_ = key;
        node.value_ = value;
        node.size_ = 1;
        return node;
    }

    private static <K, V> BSTNode<K, V> makeNode(K key, V value, BSTNode<K, V> left, BSTNode<K,
            V> right) {
        BSTNode<K, V> node = new BSTNode<>();
        node.left_ = left;
        node.right_ = right;
        node.key_ = key;
        node.value_ = value;
        node.size_ = left.size_ + right.size_ + 1;
        return node;
    }

    private static <K, V> BSTNode<K, V> makeSentinelNode() {
        BSTNode<K, V> node = new BSTNode<>();
        node.left_ = node;
        node.right_ = node;
        node.key_ = null;
        node.value_ = null;
        node.size_ = 0;
        return node;
    }

    // Private members
    private BSTNode<K, V> root_;
    private BSTNode<K, V> sentinel_;

    // Private methods
    private BSTNode<K, V> find(K key) {
        sentinel_.key_ = key;
        return findRec(root_, key);
    }

    private static <K extends Comparable<K>, V> BSTNode<K, V> findRec(BSTNode<K, V> node,
                                                                      K key) {
        var cmp = key.compareTo(node.key_);
        if (cmp == 0) {
            return node;
        } else if (cmp < 0) {
            return findRec(node.left_, key);
        } else {
            return findRec(node.right_, key);
        }
    }

    private static <K extends Comparable<K>, V> BSTNode<K, V> insertRec(BSTNode<K, V> node,
                                                                        K key, V value) {
        var cmp = key.compareTo(node.key_);
        if (cmp == 0) {
            return makeNode(key, value, node.left_, node.right_);
        } else if (cmp < 0) {
            node.left_ = insertRec(node.left_, key, value);
        } else {
            node.right_ = insertRec(node.right_, key, value);
        }
        node.size_ = node.left_.size_ + node.right_.size_ + 1;
        return node;
    }

    private static <K extends Comparable<K>, V> BSTNode<K, V> removeRec(BSTNode<K, V> node, K key) {
        var cmp = key.compareTo(node.key_);
        if (cmp == 0) {
            if (node.left_.size_ == 0) {
                return node.right_;
            }
            if (node.right_.size_ == 0) {
                return node.left_;
            }
            BSTNode<K, V> old = node;
            node = getMinRec(old.right_);
            node.right_ = removeMinRec(old.right_);
            node.left_ = old.left_;
        } else if (cmp < 0) {
            node.left_ = removeRec(node.left_, key);
        } else {
            node.right_ = removeRec(node.right_, key);
        }
        node.size_ = node.left_.size_ + node.right_.size_ + 1;
        return node;
    }

    private static <K extends Comparable<K>, V> BSTNode<K, V> removeMinRec(BSTNode<K, V> node) {
        if (node.left_.size_ == 0) {
            return node.right_;
        }
        node.left_ = removeMinRec(node.left_);
        node.size_ = node.left_.size_ + node.right_.size_ + 1;
        return node;
    }

    private static <K extends Comparable<K>, V> BSTNode<K, V> removeMaxRec(BSTNode<K, V> node) {
        if (node.right_.size_ == 0) {
            return node.left_;
        }
        node.right_ = removeMaxRec(node.right_);
        node.size_ = node.left_.size_ + node.right_.size_ + 1;
        return node;
    }

    private static <K extends Comparable<K>, V> BSTNode<K, V> getMinRec(BSTNode<K, V> node) {
        if (node.left_.size_ == 0) {
            return node;
        }
        return getMinRec(node.left_);
    }

    private static <K extends Comparable<K>, V> BSTNode<K, V> getMaxRec(BSTNode<K, V> node) {
        if (node.right_.size_ == 0) {
            return node;
        }
        return getMaxRec(node.right_);
    }
}
