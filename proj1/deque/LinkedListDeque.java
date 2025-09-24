package deque;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Implementation of the deque interface with linked lists
 *
 * @param <T> contained value type
 */
public class LinkedListDeque<T> implements Deque<T> {
    /**
     * Node class for the LinkedListDeque
     *
     * @param <T>
     */
    private static class LLNode<T> {
        T value;
        LLNode<T> prev;
        LLNode<T> next;

        /**
         * Constructor for LLNode
         *
         * @param v value of T contained in node
         * @param n reference to next node
         */
        LLNode(T v, LLNode<T> p, LLNode<T> n) {
            value = v;
            prev = p;
            next = n;
        }

        /**
         * Empty constructor for creating a sentinel node
         */
        LLNode() {
            // Point to self
            prev = this;
            next = this;
        }

    }

    /**
     * Iterator class for LinkedListDeque
     */
    private class LinkedListDequeIterator implements Iterator<T> {
        LLNode<T> nodeRef;

        LinkedListDequeIterator() {
            nodeRef = sentinel;
        }

        @Override
        public boolean hasNext() {
            return nodeRef.next != sentinel;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            nodeRef = nodeRef.next;
            return nodeRef.value;
        }
    }

    // Constructor

    /**
     * Creates an empty LinkedListDeque
     */
    public LinkedListDeque() {
        sentinel = new LLNode<>();
        size = 0;
    }

    // Member variables
    private final LLNode<T> sentinel;
    private int size;

    // Public methods
    @Override
    public int size() {
        return size;
    }

    @Override
    public void addFirst(T item) {
        var newNode = new LLNode<>(item, sentinel, sentinel.next);
        // Modify reference for both sentinel and sentinel.next
        sentinel.next.prev = newNode;
        sentinel.next = newNode;
        ++size;
    }

    @Override
    public void addLast(T item) {
        var newNode = new LLNode<>(item, sentinel.prev, sentinel);
        // Modify reference for both sentinel and sentinel.prev
        sentinel.prev.next = newNode;
        sentinel.prev = newNode;
        ++size;
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }

        var result = sentinel.next;

        // Modify references for adjacent nodes
        result.next.prev = sentinel;
        sentinel.next = result.next;
        --size;

        return result.value;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }

        var result = sentinel.prev;

        // Modify references for adjacent nodes
        result.prev.next = sentinel;
        sentinel.prev = result.prev;
        --size;

        return result.value;
    }

    @Override
    public T get(int index) {
        if (index >= size) {
            return null;
        }

        // Determine the closest route
        var revertedIndex = size - index - 1;
        LLNode<T> temp;
        if (index <= revertedIndex) {
            temp = sentinel.next;
            for (int i = 0; i < index; ++i) {
                temp = temp.next;
            }
        } else {
            temp = sentinel.prev;
            for (int i = 0; i < revertedIndex; ++i) {
                temp = temp.prev;
            }
        }
        return temp.value;
    }

    public T getRecursive(int index) {
        if (index >= size) {
            return null;
        }

        // Determine the closest route
        var revertedIndex = size - index - 1;
        if (index <= revertedIndex) {
            return recursiveGetNext(sentinel.next, index);
        } else {
            return recursiveGetPrev(sentinel.prev, index);
        }
    }

    @Override
    public void printDeque() {
        var temp = sentinel;
        while (temp.next != sentinel) {
            System.out.print(temp.value);
            if (temp.next.next != sentinel) {
                System.out.print(' ');
            }
            temp = temp.next;
        }
        System.out.print('\n');
    }

    @Override
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }

        // Deep equality
        var other = (LinkedListDeque<T>) o;

        var it1 = iterator();
        var it2 = other.iterator();

        while (true) {
            if (it1.hasNext() ^ it2.hasNext()) {
                return false;
            }
            if (!it1.hasNext()) {
                return true;
            }
            if (it1.next() != it2.next()) {
                return false;
            }
        }
    }

    // Private methods

    /**
     * Recursively get value of node [index] times next to the current node
     *
     * @param node  the current node
     * @param index count of getting next node
     * @return value of node
     */
    private T recursiveGetNext(LLNode<T> node, int index) {
        if (index == 0) {
            return node.value;
        }
        return recursiveGetNext(node.next, index - 1);
    }

    /**
     * Recursively get value of node [index] times previous to the current node
     *
     * @param node  the current node
     * @param index count of getting previous node
     * @return value of node
     */
    private T recursiveGetPrev(LLNode<T> node, int index) {
        if (index == 0) {
            return node.value;
        }
        return recursiveGetNext(node.prev, index - 1);
    }

}
