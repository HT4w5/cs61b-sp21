package deque;

/**
 * Implementation of the deque interface with arrays
 *
 * @param <T> contained value type
 */
public class ArrayDeque<T> {
    // Member variables
    private final static int INIT_SIZE_ = 8;

    private int logicalSize_;
    private int allocatedSize_;
    private int frontOffset_;
    private int backOffset_;

    T[] array_;

    // Constructor
    public ArrayDeque() {
        logicalSize_ = 0;
        allocatedSize_ = INIT_SIZE_;
        array_ = (T[]) new Object[INIT_SIZE_];
        frontOffset_ = INIT_SIZE_ / 2;
        backOffset_ = INIT_SIZE_ / 2;
    }


    // Public methods
    public int size() {
        return logicalSize_;
    }

    public boolean isEmpty() {
        return logicalSize_ == 0;
    }

    public void addFirst(T item) {
        if (logicalSize_ == allocatedSize_) {
            upscale();
        }

        if (frontOffset_ == 0) {
            frontOffset_ = allocatedSize_;
        }
        --frontOffset_;


        array_[frontOffset_] = item;
        ++logicalSize_;
    }

    public void addLast(T item) {
        if (logicalSize_ == allocatedSize_) {
            upscale();
        }

        array_[backOffset_++] = item;
        if (backOffset_ == allocatedSize_) {
            backOffset_ = 0;
        }

        ++logicalSize_;
    }

    public T removeFirst() {
        if (logicalSize_ == 0) {
            return null;
        }

        var result = array_[frontOffset_];
        array_[frontOffset_++] = null;
        if (frontOffset_ == allocatedSize_) {
            frontOffset_ = 0;
        }

        --logicalSize_;
        return result;
    }

    public T removeLast() {
        if (logicalSize_ == 0) {
            return null;
        }

        if (backOffset_ == 0) {
            backOffset_ = allocatedSize_;
        }
        --backOffset_;

        var result = array_[backOffset_];
        array_[backOffset_] = null;
        --logicalSize_;
        return result;
    }

    public T get(int index) {
        if (index >= allocatedSize_) {
            return null;
        }

        if (frontOffset_ < backOffset_) {
            return array_[frontOffset_ + index];
        }

        int rightSegmentSize = allocatedSize_ - frontOffset_;
        if (index >= rightSegmentSize) {
            index -= rightSegmentSize;
            return array_[index];
        }

        return array_[frontOffset_ + index];
    }


    // Private methods

    /**
     * Enlarge the underlying array exponentially
     */
    private void upscale() {
        int newSize = allocatedSize_ * 2;
        var newArray = (T[]) new Object[newSize];
        if (logicalSize_ == 0) {
            array_ = newArray;
            frontOffset_ = newSize / 2;
            backOffset_ = newSize / 2;
            allocatedSize_ = newSize;
            return;
        }
        // Copy data to new array and set new offsets
        if (frontOffset_ < backOffset_) {
            System.arraycopy(array_, frontOffset_, newArray, frontOffset_, logicalSize_);
        } else {
            System.arraycopy(array_, 0, newArray, 0, backOffset_);
            int newFrontOffset = frontOffset_ + (newSize - allocatedSize_);
            System.arraycopy(array_, frontOffset_, newArray, newFrontOffset, allocatedSize_ - frontOffset_);
            frontOffset_ = newFrontOffset;
        }
        array_ = newArray;
        allocatedSize_ = newSize;
    }

}
