package bstmap;

import java.util.Iterator;
import java.util.Set;

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
    public void put(K key, V value) {}

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
}
