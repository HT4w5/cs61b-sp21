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
        int idx;
        int offset;

        ArrayDequeIterator() {
            idx = 0;
            offset = 0;
        }

        @Override
        public boolean hasNext() {
            return idx != logicalSize;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            var result = array[frontOffset + (idx++) - offset];
            if (idx + frontOffset == allocatedSize) {
                offset = allocatedSize;
            }

            return result;
        }

    }


    // Member variables
    private static final int INIT_SIZE = 8;

    private int logicalSize;
    private int allocatedSize;
    private int frontOffset;
    private int backOffset;

    private T[] array;

    // Constructor
    public ArrayDeque() {
        logicalSize = 0;
        allocatedSize = INIT_SIZE;
        array = (T[]) new Object[INIT_SIZE];
        frontOffset = INIT_SIZE / 2;
        backOffset = INIT_SIZE / 2;
    }


    // Public methods
    @Override
    public int size() {
        return logicalSize;
    }

    @Override
    public void addFirst(T item) {
        if (logicalSize == allocatedSize) {
            upscale();
        }

        if (frontOffset == 0) {
            frontOffset = allocatedSize;
        }
        --frontOffset;


        array[frontOffset] = item;
        ++logicalSize;
    }

    @Override
    public void addLast(T item) {
        if (logicalSize == allocatedSize) {
            upscale();
        }

        array[backOffset++] = item;
        if (backOffset == allocatedSize) {
            backOffset = 0;
        }

        ++logicalSize;
    }

    @Override
    public T removeFirst() {
        if (logicalSize == 0) {
            return null;
        }

        var result = array[frontOffset];
        array[frontOffset++] = null;
        if (frontOffset == allocatedSize) {
            frontOffset = 0;
        }

        --logicalSize;
        if (needsDownscale()) {
            downscale();
        }


        return result;
    }

    @Override
    public T removeLast() {
        if (logicalSize == 0) {
            return null;
        }

        if (backOffset == 0) {
            backOffset = allocatedSize;
        }
        --backOffset;

        var result = array[backOffset];
        array[backOffset] = null;
        --logicalSize;

        if (needsDownscale()) {
            downscale();
        }

        return result;
    }

    @Override
    public T get(int index) {
        if (index >= allocatedSize) {
            return null;
        }

        if (frontOffset < backOffset) {
            return array[frontOffset + index];
        }

        int rightSegmentSize = allocatedSize - frontOffset;
        if (index >= rightSegmentSize) {
            index -= rightSegmentSize;
            return array[index];
        }

        return array[frontOffset + index];
    }

    @Override
    public void printDeque() {
        if (frontOffset < backOffset || backOffset == 0) {
            for (int i = 0; i < logicalSize; ++i) {
                System.out.print(array[frontOffset + i]);
                if (i != logicalSize - 1) {
                    System.out.print(' ');
                }
            }
        } else {
            for (int i = frontOffset; i < allocatedSize; ++i) {
                System.out.print(array[i]);
                System.out.print(' ');
            }
            for (int i = 0; i < backOffset; ++i) {
                System.out.print(array[i]);
                if (i != logicalSize - 1) {
                    System.out.print(' ');
                }
            }
        }
        System.out.print('\n');
    }

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
        if (!(o instanceof Deque)) {
            return false;
        }

        // Deep equality
        var other = (Deque<T>) o;

        if (size() != other.size()) {
            return false;
        }

        var it1 = iterator();
        var it2 = other.iterator();

        while (it1.hasNext()) {
            if (!it1.next().equals(it2.next())) {
                return false;
            }
        }

        return true;
    }


    // Private methods

    /**
     * Enlarge the underlying array exponentially
     */
    private void upscale() {
        int newSize = allocatedSize * 2;
        var newArray = (T[]) new Object[newSize];
        if (logicalSize == 0) {
            array = newArray;
            frontOffset = newSize / 2;
            backOffset = newSize / 2;
            allocatedSize = newSize;
            return;
        }
        // Copy data to new array and set new offsets
        if (frontOffset < backOffset) {
            System.arraycopy(array, frontOffset, newArray, frontOffset, logicalSize);
        } else {
            System.arraycopy(array, 0, newArray, 0, backOffset);
            int newFrontOffset = frontOffset + (newSize - allocatedSize);
            System.arraycopy(array, frontOffset, newArray, newFrontOffset,
                    allocatedSize - frontOffset);
            frontOffset = newFrontOffset;
        }
        array = newArray;
        allocatedSize = newSize;
    }

    /**
     * Shrink the underlying array by half
     */
    private void downscale() {
        int newSize = allocatedSize / 2;
        var newArray = (T[]) new Object[newSize];
        if (logicalSize == 0) {
            array = newArray;
            frontOffset = newSize / 2;
            backOffset = newSize / 2;
            allocatedSize = newSize;
            return;
        }
        // Copy data to new array and set new offsets
        if (frontOffset < backOffset) {
            System.arraycopy(array, frontOffset, newArray, 0, logicalSize);
            frontOffset = 0;
            backOffset = logicalSize;
        } else {
            System.arraycopy(array, 0, newArray, 0, backOffset);
            int newFrontOffset = frontOffset + (newSize - allocatedSize);
            System.arraycopy(array, frontOffset, newArray, newFrontOffset,
                    allocatedSize - frontOffset);
            frontOffset = newFrontOffset;
        }
        array = newArray;
        allocatedSize = newSize;
    }

    private boolean needsDownscale() {
        if (allocatedSize < 16) {
            return false;
        }
        // Downscale
        return allocatedSize > logicalSize * 4;
    }


}
