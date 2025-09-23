package deque;

import org.junit.Test;

import java.util.NoSuchElementException;

import static org.junit.Assert.*;


/**
 * Performs some basic linked list tests.
 */
public class ArrayDequeTest {

    @Test
    /** Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {
        ArrayDeque<String> lld1 = new ArrayDeque<String>();

        assertTrue("A newly initialized LLDeque should be empty", lld1.isEmpty());
        lld1.addFirst("front");

        // The && operator is the same as "and" in Python.
        // It's a binary operator that returns true if both arguments true, and false otherwise.
        assertEquals(1, lld1.size());
        assertFalse("lld1 should now contain 1 item", lld1.isEmpty());

        lld1.addLast("middle");
        assertEquals(2, lld1.size());

        lld1.addLast("back");
        assertEquals(3, lld1.size());

        System.out.println("Printing out deque: ");
        lld1.printDeque();

    }

    @Test
    /** Adds an item, then removes an item, and ensures that dll is empty afterwards. */
    public void addRemoveTest() {


        ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();
        // should be empty
        assertTrue("lld1 should be empty upon initialization", lld1.isEmpty());

        lld1.addFirst(10);
        // should not be empty
        assertFalse("lld1 should contain 1 item", lld1.isEmpty());

        lld1.removeFirst();
        // should be empty
        assertTrue("lld1 should be empty after removal", lld1.isEmpty());

    }

    @Test
    /* Tests removing from an empty deque */
    public void removeEmptyTest() {

        ArrayDeque<Integer> lld1 = new ArrayDeque<>();
        lld1.addFirst(3);

        lld1.removeLast();
        lld1.removeFirst();
        lld1.removeLast();
        lld1.removeFirst();

        int size = lld1.size();
        String errorMsg = "  Bad size returned when removing from empty deque.\n";
        errorMsg += "  student size() returned " + size + "\n";
        errorMsg += "  actual size() returned 0\n";

        assertEquals(errorMsg, 0, size);

    }

    @Test
    /* Check if you can create ArrayDeques with different parameterized types*/
    public void multipleParamTest() {


        ArrayDeque<String> lld1 = new ArrayDeque<String>();
        ArrayDeque<Double> lld2 = new ArrayDeque<Double>();
        ArrayDeque<Boolean> lld3 = new ArrayDeque<Boolean>();

        lld1.addFirst("string");
        lld2.addFirst(3.14159);
        lld3.addFirst(true);

        String s = lld1.removeFirst();
        double d = lld2.removeFirst();
        boolean b = lld3.removeFirst();

    }

    @Test
    /* check if null is return when removing from an empty ArrayDeque. */
    public void emptyNullReturnTest() {

        ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();

        boolean passed1 = false;
        boolean passed2 = false;
        assertEquals("Should return null when removeFirst is called on an empty Deque,", null, lld1.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,", null, lld1.removeLast());


    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void bigLLDequeTest() {


        ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();
        for (int i = 0; i < 1000000; i++) {
            lld1.addLast(i);
        }

        for (double i = 0; i < 500000; i++) {
            assertEquals("Should have the same value", i, (double) lld1.removeFirst(), 0.0);
        }

        for (double i = 999999; i > 500000; i--) {
            assertEquals("Should have the same value", i, (double) lld1.removeLast(), 0.0);
        }


    }

    @Test
    /* Add more than 8 elements to deque; check if upscale is being performed */
    public void upscaleTest() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        for (int i = 0; i <= 2000; i++) {
            ad1.addLast(i);
            ad1.addFirst(-i);
        }

        for (int i = 2000; i >= 0; --i) {
            assertEquals("Should have the same value", -i, ad1.removeFirst().intValue());
            assertEquals("Should have the same value", i, ad1.removeLast().intValue());
        }
    }

    @Test
    /* Add and remove, then verify to check downscale */
    public void downscaleTest1() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        for (int i = 0; i <= 20; ++i) {
            ad1.addFirst(i);
        }

        for (int i = 0; i < 10000; ++i) {
            ad1.addLast(-1);
            ad1.addFirst(-1);
        }

        for (int i = 0; i < 10000; ++i) {
            ad1.removeFirst();
            ad1.removeLast();
        }

        // Verify
        for (int i = 20; i >= 0; --i) {
            assertEquals("Should have the same value", i, ad1.removeFirst().intValue());
        }
    }

    @Test
    /* Add and remove, then verify to check downscale */
    public void downscaleTest2() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        for (int i = 0; i <= 10; ++i) {
            ad1.addFirst(i);
        }

        for (int i = 0; i < 10000; ++i) {
            ad1.addLast(-1);
            ad1.addFirst(-1);
        }

        for (int i = 0; i < 10000; ++i) {
            ad1.removeFirst();
            ad1.removeLast();
        }

        // Verify
        for (int i = 10; i >= 0; --i) {
            assertEquals("Should have the same value", i, ad1.removeFirst().intValue());
        }
    }


    @Test
    /* Test ring buffer wrap around */
    public void ringTest() {
        for (int bufferSize = 0; bufferSize <= 256; ++bufferSize) {
            ArrayDeque<Integer> ad1 = new ArrayDeque<>();

            for (int i = 0; i < bufferSize; ++i) {
                ad1.addFirst(i);
            }

            for (int i = 0; i < 1000; ++i) {
                ad1.addFirst(bufferSize + i);
                assertEquals("Should have the same value", i, ad1.removeLast().intValue());
            }

            for (int i = 0; i < bufferSize; ++i) {
                assertEquals("Should have the same value", i + 1000, ad1.removeLast().intValue());
            }
        }
    }

    @Test
    /* Test iterator for ArrayDeque */
    public void iteratorTest() {
        ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();
        for (int i = 0; i < 10; i++) {
            lld1.addLast(i);
        }

        var expected = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        var result = new int[10];

        var it = lld1.iterator();
        int idx = 0;
        while (it.hasNext()) {
            result[idx] = it.next();
            ++idx;
        }

        assertArrayEquals("Should have the same values", expected, result);
    }

    @Test
    /* Test whether next() throws exception when hasNext() is false */
    public void iteratorExceptionTest() {
        ArrayDeque<Integer> lld1 = new ArrayDeque<Integer>();
        var it = lld1.iterator();
        assertFalse("Empty deque's iterator has no next", it.hasNext());
        boolean thrown = false;
        try {
            it.next();
        } catch (NoSuchElementException e) {
            thrown = true;
        }

        assertTrue("next() should throw NoSuchElementException when hasNext() is false", thrown);
    }

    @Test
    /* Test equals() */
    public void equalsTest1() {
        ArrayDeque<Integer> lld1 = new ArrayDeque<>();
        ArrayDeque<Integer> lld2 = new ArrayDeque<>();

        assertFalse("Return false if other is null", lld1.equals(null));
        assertTrue("Return true for the same object", lld1.equals(lld1));
        assertTrue("Return true for two empty llds", lld1.equals(lld2));
    }

    @Test
    /* Test equals() */
    public void equalsTest2() {
        ArrayDeque<Integer> lld1 = new ArrayDeque<>();
        ArrayDeque<Integer> lld2 = new ArrayDeque<>();

        for (int i = 0; i < 10; ++i) {
            lld1.addFirst(i);
            lld2.addFirst(i);
        }

        assertTrue("LLDs with same content are equal", lld1.equals(lld2));

        // Modify
        lld2.removeFirst();
        lld2.addFirst(-1);
        assertFalse("LLDs with different content are not equal", lld1.equals(lld2));
    }

    @Test
    /* Test equals() */
    public void equalsTest3() {
        ArrayDeque<Integer> lld1 = new ArrayDeque<>();
        ArrayDeque<Integer> lld2 = new ArrayDeque<>();

        for (int i = 0; i < 10; ++i) {
            lld1.addFirst(i);
            lld2.addLast(i);
        }

        assertFalse("LLDs with different content are not equal", lld1.equals(lld2));
    }
}
