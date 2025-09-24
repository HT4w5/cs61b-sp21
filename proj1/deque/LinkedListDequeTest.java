package deque;

import org.junit.Test;

import java.util.NoSuchElementException;

import static org.junit.Assert.*;


/**
 * Performs some basic linked list tests.
 */
public class LinkedListDequeTest {

    @Test
    /** Adds a few things to the list, checking isEmpty() and size() are correct,
     * finally printing the results.
     *
     * && is the "and" operation. */
    public void addIsEmptySizeTest() {
        LinkedListDeque<String> lld1 = new LinkedListDeque<String>();

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


        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
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

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
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
    /* Check if you can create LinkedListDeques with different parameterized types*/
    public void multipleParamTest() {


        LinkedListDeque<String> lld1 = new LinkedListDeque<String>();
        LinkedListDeque<Double> lld2 = new LinkedListDeque<Double>();
        LinkedListDeque<Boolean> lld3 = new LinkedListDeque<Boolean>();

        lld1.addFirst("string");
        lld2.addFirst(3.14159);
        lld3.addFirst(true);

        String s = lld1.removeFirst();
        double d = lld2.removeFirst();
        boolean b = lld3.removeFirst();

    }

    @Test
    /* check if null is return when removing from an empty LinkedListDeque. */
    public void emptyNullReturnTest() {

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();

        boolean passed1 = false;
        boolean passed2 = false;
        assertEquals("Should return null when removeFirst is called on an empty Deque,", null,
                lld1.removeFirst());
        assertEquals("Should return null when removeLast is called on an empty Deque,", null,
                lld1.removeLast());


    }

    @Test
    /* Add large number of elements to deque; check if order is correct. */
    public void bigLLDequeTest() {


        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
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
    /* Test iterator for LinkedListDeque */
    public void iteratorTest() {
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
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
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<Integer>();
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
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        LinkedListDeque<Integer> lld2 = new LinkedListDeque<>();

        assertFalse("Return false if other is null", lld1.equals(null));
        assertTrue("Return true for the same object", lld1.equals(lld1));
        assertTrue("Return true for two empty llds", lld1.equals(lld2));
    }

    @Test
    /* Test equals() */
    public void equalsTest2() {
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        LinkedListDeque<Integer> lld2 = new LinkedListDeque<>();

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
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        LinkedListDeque<Integer> lld2 = new LinkedListDeque<>();

        for (int i = 0; i < 10; ++i) {
            lld1.addFirst(i);
            lld2.addLast(i);
        }

        assertFalse("LLDs with different content are not equal", lld1.equals(lld2));
    }
}
