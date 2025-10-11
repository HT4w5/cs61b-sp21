package hashmap;

import java.util.*;

/**
 * A hash table-backed Map implementation. Provides amortized constant time
 * access to elements via get(), remove(), and put() in the best case.
 * <p>
 * Assumes null keys will never be inserted, and does not resize down upon remove().
 *
 * @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    // Public interface

    /**
     * Removes all of the mappings from this map.
     */
    public void clear() {
        allocatedSize_ = initialSize_;
        logicalSize_ = 0;
        buckets_ = createTable(initialSize_);
    }

    /**
     * Returns true if this map contains a mapping for the specified key.
     */
    public boolean containsKey(K key) {
        int idx = getIndex(key);
        if (buckets_[idx] == null) {
            return false;
        }
        for (Node n : buckets_[idx]) {
            if (n.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    public V get(K key) {
        int idx = getIndex(key);
        if (buckets_[idx] == null) {
            return null;
        }
        for (Node n : buckets_[idx]) {
            if (n.key.equals(key)) {
                return n.value;
            }
        }
        return null;
    }

    /**
     * Returns the number of key-value mappings in this map.
     */
    public int size() {
        return logicalSize_;
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key,
     * the old value is replaced.
     */
    public void put(K key, V value) {
        int idx = getIndex(key);
        if (buckets_[idx] == null) {
            buckets_[idx] = createBucket();
        } else {
            for (Node n : buckets_[idx]) {
                if (n.key.equals(key)) {
                    n.value = value;
                    return;
                }
            }
        }
        ++logicalSize_;
        buckets_[idx].add(createNode(key, value));

        doUpscale();
    }

    /**
     * Returns a Set view of the keys contained in this map.
     */
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     * Not required for Lab 8. If you don't implement this, throw an
     * UnsupportedOperationException.
     */
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    /**
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 8. If you don't implement this,
     * throw an UnsupportedOperationException.
     */
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        return new MyHashMapIterator();
    }

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    private class MyHashMapIterator implements Iterator<K> {
        int idx_;
        int bucketIdx_;
        Iterator<Node> bucketIt_;

        MyHashMapIterator() {
            idx_ = 0;
            bucketIdx_ = 0;
            if (logicalSize_ == 0) {
                // Empty table
                bucketIt_ = null;
                return;
            }
            // Find first valid bucket
            // Make sure bucketIt_ isn't null at first
            while (buckets_[bucketIdx_] == null) {
                ++bucketIdx_;
            }
            bucketIt_ = buckets_[bucketIdx_].iterator();
        }

        @Override
        public boolean hasNext() {
            return idx_ < logicalSize_;
        }

        @Override
        public K next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            // Current bucket still has more Nodes
            if (bucketIt_.hasNext()) {
                ++idx_;
                return bucketIt_.next().key;
            }
            // Find next valid bucket
            do {
                ++bucketIdx_;
            } while (buckets_[bucketIdx_] == null);
            // Assume that existing buckets are not empty (remove() makes sure this is true)
            bucketIt_ = buckets_[bucketIdx_].iterator();
            ++idx_;
            return bucketIt_.next().key;
        }
    }

    /**
     * Constructors
     */
    public MyHashMap() {
        initialSize_ = DEFAULT_INITIAL_SIZE;
        allocatedSize_ = initialSize_;
        maxLoad_ = DEFAULT_MAX_LOAD;
        buckets_ = createTable(allocatedSize_);
        logicalSize_ = 0;
    }

    public MyHashMap(int initialSize) {
        if (initialSize < 1) {
            throw new IllegalArgumentException();
        }
        initialSize_ = initialSize;
        allocatedSize_ = initialSize_;
        maxLoad_ = DEFAULT_MAX_LOAD;
        buckets_ = createTable(allocatedSize_);
        logicalSize_ = 0;
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad     maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        if (initialSize < 1 || maxLoad <= 0.0) {
            throw new IllegalArgumentException();
        }
        initialSize_ = initialSize;
        allocatedSize_ = initialSize_;
        maxLoad_ = maxLoad;
        buckets_ = createTable(allocatedSize_);
        logicalSize_ = 0;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     * <p>
     * The only requirements of a hash table bucket are that we can:
     * 1. Insert items (`add` method)
     * 2. Remove items (`remove` method)
     * 3. Iterate through items (`iterator` method)
     * <p>
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     * <p>
     * Override this method to use different data structures as
     * the underlying bucket type
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new ArrayList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    @SuppressWarnings("unchecked")
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }

    /**
     * Get key index in hash table
     *
     * @param key
     * @return index
     */
    private int getIndex(K key) {
        return Math.floorMod(key.hashCode(), allocatedSize_);
    }

    private void doUpscale() {
        if ((double) logicalSize_ / allocatedSize_ < maxLoad_) {
            return;
        }
        allocatedSize_ *= 2;
        // Rehash
        var newTable = createTable(allocatedSize_);
        for (Collection<Node> b : buckets_) {
            if (b == null) {
                continue;
            }
            for (Node n : b) {
                int idx = getIndex(n.key);
                if (newTable[idx] == null) {
                    newTable[idx] = createBucket();
                }
                newTable[idx].add(n);
            }
        }
        buckets_ = newTable;
    }


    // Private members
    private Collection<Node>[] buckets_;
    private final int initialSize_;
    private int allocatedSize_;
    private int logicalSize_;
    private final double maxLoad_;
    private final static double DEFAULT_MAX_LOAD = 0.75;
    private final static int DEFAULT_INITIAL_SIZE = 16;

}
