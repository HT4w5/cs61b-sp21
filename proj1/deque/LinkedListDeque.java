package deque;

/**
 * Implementation of the deque interface with linked lists
 * @param <T> contained value type
 */
public class LinkedListDeque<T> {
    /**
     * Node class for the LinkedListDeque
     *
     * @param <T>
     */
    private static class LLNode<T> {
        T value_;
        LLNode<T> prev_;
        LLNode<T> next_;

        /**
         * Constructor for LLNode
         *
         * @param v value of T contained in node
         * @param n reference to next node
         */
        public LLNode(T v, LLNode<T> p, LLNode<T> n) {
            value_ = v;
            prev_ = p;
            next_ = n;
        }

        /**
         * Empty constructor for creating a sentinel node
         */
        public LLNode() {
            // Point to self
            prev_ = this;
            next_ = this;
        }

    }

    // Constructor

    /**
     * Creates an empty LinkedListDeque
     */
    public LinkedListDeque() {
        sentinel_ = new LLNode<>();
        size_ = 0;
    }

    // Member variables
    private final LLNode<T> sentinel_;
    int size_;

    // Public methods

    public int size() {
        return size_;
    }

    public boolean isEmpty() {
        return size_ == 0;
    }

    public void addFirst(T item) {
        var newNode = new LLNode<>(item, sentinel_, sentinel_.next_);
        // Modify reference for both sentinel_ and sentinel_.next_
        sentinel_.next_.prev_ = newNode;
        sentinel_.next_ = newNode;
        ++size_;
    }

    public void addLast(T item) {
        var newNode = new LLNode<>(item, sentinel_.prev_, sentinel_);
        // Modify reference for both sentinel_ and sentinel_.prev_
        sentinel_.prev_.next_ = newNode;
        sentinel_.prev_ = newNode;
        ++size_;
    }

    public T removeFirst() {
        if (size_ == 0) {
            return null;
        }

        var result = sentinel_.next_;

        // Modify references for adjacent nodes
        result.next_.prev_ = sentinel_;
        sentinel_.next_ = result.next_;
        --size_;

        return result.value_;
    }

    public T removeLast() {
        if (size_ == 0) {
            return null;
        }

        var result = sentinel_.prev_;

        // Modify references for adjacent nodes
        result.prev_.next_ = sentinel_;
        sentinel_.prev_ = result.prev_;
        --size_;

        return result.value_;
    }

    public T get(int index) {
        if (index >= size_) {
            return null;
        }

        // Determine the closest route
        var revertedIndex = size_ - index - 1;
        LLNode<T> temp;
        if (index <= revertedIndex) {
            temp = sentinel_.next_;
            for (int i = 0; i < index; ++i) {
                temp = temp.next_;
            }
        } else {
            temp = sentinel_.prev_;
            for (int i = 0; i < revertedIndex; ++i) {
                temp = temp.prev_;
            }
        }
        return temp.value_;
    }

    public T getRecursive(int index) {
        if (index >= size_) {
            return null;
        }

        // Determine the closest route
        var revertedIndex = size_ - index - 1;
        if (index <= revertedIndex) {
            return recursiveGetNext(sentinel_.next_, index);
        } else {
            return recursiveGetPrev(sentinel_.prev_, index);
        }
    }

    public void printDeque() {
        var temp = sentinel_;
        while (temp.next_ != sentinel_) {
            System.out.print(temp.value_);
            if (temp.next_.next_ != sentinel_) {
                System.out.print(' ');
            }
            temp = temp.next_;
        }
        System.out.print('\n');
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
            return node.value_;
        }
        return recursiveGetNext(node.next_, index - 1);
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
            return node.value_;
        }
        return recursiveGetNext(node.prev_, index - 1);
    }

}
