package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    // Public interface

    /**
     * Removes all of the mappings from this map.
     */
    public void clear() {
        root_ = null;
        size_ = 0;
    }

    /* Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key) {
        var ref = root_;
        while (ref != null) {
            var cmp = ref.key_.compareTo(key);
            if (cmp == 0) {
                return true;
            }
            if (cmp < 0) {
                ref = ref.left_;
            } else {
                ref = ref.right_;
            }
        }
        return false;
    }

    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    public V get(K key) {
        var ref = root_;
        while (ref != null) {
            var cmp = ref.key_.compareTo(key);
            if (cmp == 0) {
                return ref.value_;
            }
            if (cmp < 0) {
                ref = ref.left_;
            } else {
                ref = ref.right_;
            }
        }
        return null;
    }

    /* Returns the number of key-value mappings in this map. */
    public int size() {
        return size_;
    }

    /* Associates the specified value with the specified key in this map. */
    public void put(K key, V value) {
        var ref = root_;
        BSTNode<K, V> parent = null;
        // false: left; true: right
        boolean direction = false;
        while (ref != null) {
            var cmp = ref.key_.compareTo(key);
            if (cmp == 0) {
                ref.value_ = value;
                return;
            }
            if (cmp < 0) {
                parent = ref;
                ref = ref.left_;
                direction = false;
            } else {
                parent = ref;
                ref = ref.right_;
                direction = true;
            }
        }
        ++size_;
        if (parent == null) {
            root_ = new BSTNode<>(key, value);
            return;
        }
        if (direction) {
            // Right
            parent.right_ = new BSTNode<>(key, value);
        } else {
            // Left
            parent.left_ = new BSTNode<>(key, value);
        }
    }

    /* Returns a Set view of the keys contained in this map. */
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    /* Removes the mapping for the specified key from this map if present. */
    public V remove(K key) {
        // Find node
        var ref = root_;
        BSTNode<K, V> parent = null;
        // false: left; true: right
        boolean direction = false;
        while (ref != null) {
            var cmp = ref.key_.compareTo(key);
            if (cmp == 0) {
                break;
            }
            if (cmp < 0) {
                parent = ref;
                ref = ref.left_;
                direction = false;
            } else {
                parent = ref;
                ref = ref.right_;
                direction = true;
            }
        }
        // Not found
        if (ref == null) {
            return null;
        }

        --size_;
        // Check node state
        if (ref.left_ == null && ref.right_ == null) { // Node has no children
            var value = ref.value_;
            // Discard
            if (parent == null) { // parent is root_
                root_ = null;
            } else {
                if (direction) {
                    parent.right_ = null;
                } else {
                    parent.left_ = null;
                }
            }
            return value;
        } else if (ref.left_ != null && ref.right_ != null) { // Node has two children
            var value = ref.value_;
            // Find the largest node smaller than ref
            var ref2 = ref.left_;
            BSTNode<K, V> parent2 = null;
            while (ref2.right_ != null) {
                parent2 = ref2;
                ref2 = ref2.right_;
            }
            // New node might have left child
            if (ref2.left_ != null) {
                if (parent2 == null) {
                    ref.left_ = ref2.left_;
                } else {
                    parent2.right_ = ref2.left_;
                }
            }
            // Replace ref with ref2
            ref.key_ = ref2.key_;
            ref.value_ = ref2.value_;
            return value;
        } else { // Node has one child
            // Connect parent ref with child
            var value = ref.value_;
            BSTNode<K, V> next;
            if (ref.left_ != null) {
                next = ref.left_;
            } else {
                next = ref.right_;
            }
            if (parent == null) { // parent is root_
                root_ = next;
            } else {
                if (direction) {
                    parent.right_ = next;
                } else {
                    parent.left_ = next;
                }
            }
            return value;
        }
    }

    /* Removes the entry for the specified key only if it is currently mapped to
     * the specified value. */
    public V remove(K key, V value) {
        // Find node
        var ref = root_;
        BSTNode<K, V> parent = null;
        // false: left; true: right
        boolean direction = false;
        while (ref != null) {
            var cmp = ref.key_.compareTo(key);
            if (cmp == 0) {
                if (ref.value_ != value) {
                    return null;
                }
                break;
            }
            if (cmp < 0) {
                parent = ref;
                ref = ref.left_;
                direction = false;
            } else {
                parent = ref;
                ref = ref.right_;
                direction = true;
            }
        }
        // Not found
        if (ref == null) {
            return null;
        }

        --size_;
        // Check node state
        if (ref.left_ == null && ref.right_ == null) { // Node has no children
            var v = ref.value_;
            // Discard
            if (parent == null) { // parent is root_
                root_ = null;
            } else {
                if (direction) {
                    parent.right_ = null;
                } else {
                    parent.left_ = null;
                }
            }
            return value;
        } else if (ref.left_ != null && ref.right_ != null) { // Node has two children
            var v = ref.value_;
            // Find the largest node smaller than ref
            var ref2 = ref.left_;
            BSTNode<K, V> parent2 = null;
            while (ref2.right_ != null) {
                parent2 = ref2;
                ref2 = ref2.right_;
            }
            // New node might have left child
            if (ref2.left_ != null) {
                if (parent2 == null) {
                    ref.left_ = ref2.left_;
                } else {
                    parent2.right_ = ref2.left_;
                }
            }
            // Replace ref with ref2
            ref.key_ = ref2.key_;
            ref.value_ = ref2.value_;
            return value;
        } else { // Node has one child
            // Connect parent ref with child
            var v = ref.value_;
            BSTNode<K, V> next;
            if (ref.left_ != null) {
                next = ref.left_;
            } else {
                next = ref.right_;
            }
            if (parent == null) { // parent is root_
                root_ = next;
            } else {
                if (direction) {
                    parent.right_ = next;
                } else {
                    parent.left_ = next;
                }
            }
            return v;
        }
    }

    public Iterator<K> iterator() {
        return null;
    }

    /**
     * Constructor
     */
    public BSTMap() {
    }

    // Private members
    private static class BSTNode<K, V> {
        BSTNode<K, V> left_;
        BSTNode<K, V> right_;
        K key_;
        V value_;

        BSTNode(K key, V value) {
            key_ = key;
            value_ = value;
        }
    }

    private BSTNode<K, V> root_ = null;
    private int size_ = 0;
}
