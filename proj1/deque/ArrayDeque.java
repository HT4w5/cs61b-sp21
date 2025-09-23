package deque;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Implementation of the deque interface with arrays
 *
 * @param <T> contained value type
 */
public class ArrayDeque<T> implements Deque<T> {
    /**
     * Iterator class for ArrayDeque
     */
    private class ArrayDequeIterator implements Iterator<T> {
        int idx_;

        public ArrayDequeIterator() {
            idx_ = frontOffset_;
        }

        @Override
        public boolean hasNext() {
            return idx_ != backOffset_;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            var result = array_[idx_++];
            if (idx_ == allocatedSize_) {
                idx_ = 0;
            }

            return result;
        }

    }


    // Member variables
    private final static int INIT_SIZE_ = 8;

    private int logicalSize_;
    private int allocatedSize_;
    private int frontOffset_;
    private int backOffset_;

    private T[] array_;

    // Constructor
    public ArrayDeque() {
        logicalSize_ = 0;
        allocatedSize_ = INIT_SIZE_;
        array_ = (T[]) new Object[INIT_SIZE_];
        frontOffset_ = INIT_SIZE_ / 2;
        backOffset_ = INIT_SIZE_ / 2;
    }


    // Public methods
    @Override
    public int size() {
        return logicalSize_;
    }

    @Override
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

    @Override
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

    @Override
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
        if (needsDownscale()) {
            downscale();
        }


        return result;
    }

    @Override
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

        if (needsDownscale()) {
            downscale();
        }

        return result;
    }

    @Override
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

    @Override
    public void printDeque() {
        if (frontOffset_ < backOffset_ || backOffset_ == 0) {
            for (int i = 0; i < logicalSize_; ++i) {
                System.out.print(array_[frontOffset_ + i]);
                if (i != logicalSize_ - 1) {
                    System.out.print(' ');
                }
            }
        } else {
            for (int i = frontOffset_; i < allocatedSize_; ++i) {
                System.out.print(array_[i]);
                System.out.print(' ');
            }
            for (int i = 0; i < backOffset_; ++i) {
                System.out.print(array_[i]);
                if (i != logicalSize_ - 1) {
                    System.out.print(' ');
                }
            }
        }
        System.out.print('\n');
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
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
        var other = (ArrayDeque<T>) o;

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

    /**
     * Shrink the underlying array by half
     */
    private void downscale() {
        int newSize = allocatedSize_ / 2;
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
            System.arraycopy(array_, frontOffset_, newArray, 0, logicalSize_);
            frontOffset_ = 0;
            backOffset_ = logicalSize_;
        } else {
            System.arraycopy(array_, 0, newArray, 0, backOffset_);
            int newFrontOffset = frontOffset_ + (newSize - allocatedSize_);
            System.arraycopy(array_, frontOffset_, newArray, newFrontOffset, allocatedSize_ - frontOffset_);
            frontOffset_ = newFrontOffset;
        }
        array_ = newArray;
        allocatedSize_ = newSize;
    }

    private boolean needsDownscale() {
        if (allocatedSize_ < 16) {
            return false;
        }
        // Downscale
        return allocatedSize_ > logicalSize_ * 4;
    }


}
