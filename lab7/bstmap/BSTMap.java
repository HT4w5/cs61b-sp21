package bstmap;

import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    // Public interface

    /**
     * Removes all of the mappings from this map.
     */
    public void clear() {
        root_ = sentinel_;
        size_ = 0;
    }

    /* Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key) {
        var trav = new BSTTraverser<>(root_, sentinel_);
        return trav.search(key);
    }

    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    public V get(K key) {
        var trav = new BSTTraverser<>(root_, sentinel_);
        if (!trav.search(key)) {
            return null;
        }
        return trav.getValue();
    }

    /* Returns the number of key-value mappings in this map. */
    public int size() {
        return size_;
    }

    /* Associates the specified value with the specified key in this map. */
    public void put(K key, V value) {
        var trav = new BSTTraverser<>(root_, sentinel_);
        if (trav.search(key)) {
            trav.injectValue(value);
        } else {
            ++size_;
            trav.appendNode(key, value);
        }
    }

    /* Returns a Set view of the keys contained in this map. */
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    /* Removes the mapping for the specified key from this map if present. */
    public V remove(K key) {
        var trav = new BSTTraverser<>(root_, sentinel_);
        if (!trav.search(key)) {
            return null;
        }
        --size_;
        var v = trav.getValue();
        trav.deleteNode();
        return v;
    }

    /* Removes the entry for the specified key only if it is currently mapped to
     * the specified value. */
    public V remove(K key, V value) {
        var trav = new BSTTraverser<>(root_, sentinel_);
        if (!trav.search(key)) {
            return null;
        }
        var v = trav.getValue();
        if (v != value) {
            return null;
        }
        --size_;
        trav.deleteNode();
        return v;
    }

    public Iterator<K> iterator() {
        return null;
    }

    // Constructor
    public BSTMap() {
        // Create sentinel node
        sentinel_ = makeSentinelNode();
        root_ = sentinel_;
        size_ = 0;
    }

    // Private methods
    private static class BSTNode<K, V> {
        BSTNode<K, V> left_;
        BSTNode<K, V> right_;
        K key_;
        V value_;
    }

    private static class BSTTraverser<K extends Comparable<K>, V> {
        private Stack<BSTNode<K, V>> refStack_;
        private Stack<Boolean> dirStack_;
        private BSTNode<K, V> root_;
        private BSTNode<K, V> sentinel_;

        public BSTTraverser(BSTNode<K, V> root, BSTNode<K, V> sentinel) {
            root_ = root;
            sentinel_ = sentinel;
            reset();
        }

        /**
         * Reset tree traversal state
         */
        public void reset() {
            refStack_ = new Stack<>();
            dirStack_ = new Stack<>();
            refStack_.push(root_);
        }

        public K getKey() {
            return refStack_.peek().key_;
        }

        public V getValue() {
            return refStack_.peek().value_;
        }

        public BSTNode<K, V> getNode() {
            return refStack_.peek();
        }

        /**
         * Descend the tree in the specified direction
         *
         * @param dir false for left, true for right
         * @return true on success, false on failure
         */
        public boolean descend(boolean dir) {
            var ref = refStack_.peek();
            if (ref == sentinel_) {
                return false;
            }
            if (dir) {
                refStack_.push(ref.right_);
            } else {
                refStack_.push(ref.left_);
            }
            dirStack_.push(dir);
            return true;
        }

        /**
         * Ascend the tree
         *
         * @return true on success, false on failure
         */
        public boolean ascend() {
            if (refStack_.size() == 1) {
                return false;
            }
            refStack_.pop();
            dirStack_.pop();
            return true;
        }

        /**
         * Search for key in the tree. Set current node to the node found, or to the closest
         * sentinel node if not found.
         *
         * @param key key to search for
         * @return true if exact key found, false if not
         */
        public boolean search(K key) {
            reset();
            if(getNode() == sentinel_) {
                return false;
            }
            while (true) {
                var cmp = key.compareTo(getKey());
                if (cmp == 0) {
                    return true;
                }
                if (!descend(cmp > 0)) {
                    return false;
                }
            }
        }

        /**
         * Replace the current node's value with {@code value}.
         *
         * @param value
         */
        public void injectValue(V value) {
            refStack_.peek().value_ = value;
        }

        /**
         * Replace the current node's key with {@code key} and value with {@code value}.
         *
         * @param key
         * @param value
         */
        public void injectKV(K key, V value) {
            refStack_.peek().key_ = key;
            refStack_.peek().value_ = value;
        }

        /**
         * Append a new node at the current sentinel node. Current node must be sentinel node.
         *
         * @param key   key for new node
         * @param value value for new node
         * @throws IllegalArgumentException if current node is not the sentinel node
         */
        public void appendNode(K key, V value) {
            if (refStack_.peek() != sentinel_) {
                throw new IllegalArgumentException();
            }
            refStack_.pop();
            if(dirStack_.isEmpty()) {

            }
            if (dirStack_.peek()) {
                refStack_.peek().right_ = makeNode(key, value, sentinel_);
                refStack_.push(refStack_.peek().right_);
            } else {
                refStack_.peek().left_ = makeNode(key, value, sentinel_);
                refStack_.push(refStack_.peek().left_);
            }
        }

        public void deleteNode() {
            var ref = refStack_.pop();
            if (ref.left_ == sentinel_ && ref.right_ == sentinel_) {
                if (dirStack_.peek()) {
                    refStack_.peek().right_ = sentinel_;
                } else {
                    refStack_.peek().left_ = sentinel_;
                }
            } else if (ref.left_ != sentinel_ && ref.right_ != sentinel_) {
                // Find max node smaller than current
                var trav = new BSTTraverser<>(ref, sentinel_);
                trav.findMax();
                injectKV(trav.getKey(), trav.getValue());
                trav.deleteNode();
            } else {
                BSTNode<K, V> next;
                if (ref.left_ != sentinel_) {
                    next = ref.left_;
                } else {
                    next = ref.right_;
                }
                if (dirStack_.peek()) {
                    refStack_.peek().right_ = next;
                } else {
                    refStack_.peek().left_ = next;
                }
            }
        }

        public void findMax() {
            reset();
            while (descend(true)) {
            }
        }
    }

    private int size_ = 0;
    private BSTNode<K, V> root_;
    // Sentinel node attaches to leaf nodes
    private BSTNode<K, V> sentinel_;

    // Private methods
    private static <K, V> BSTNode<K, V> makeNode(K k, V v, BSTNode<K, V> sentinel) {
        BSTNode<K, V> node = new BSTNode<>();
        node.left_ = sentinel;
        node.right_ = sentinel;
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
}
