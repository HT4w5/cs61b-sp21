package deque;

import org.junit.Test;

import static org.junit.Assert.*;

public class DequeTest {
    @Test
    /* LLD and AD with the same values are considered equal */
    public void equalityTest1() {
        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        ArrayDeque<Integer> ad1 = new ArrayDeque<>();

        for (int i = 0; i < 10; ++i) {
            lld1.addFirst(i);
            ad1.addFirst(i);
        }

        assertEquals("LLD and AD with the same values are considered equal", lld1, ad1);
        assertEquals("LLD and AD with the same values are considered equal", ad1, lld1);
    }

    @Test
    /* LLD and AD with the same values are considered equal */
    public void equalityTest2() {
        LinkedListDeque<String> lld1 = new LinkedListDeque<>();
        ArrayDeque<String> ad1 = new ArrayDeque<>();

        for (int i = 0; i < 10; ++i) {
            lld1.addFirst("" + i);
            ad1.addFirst("" + i);
        }

        assertEquals("LLD and AD with the same values are considered equal", lld1, ad1);
        assertEquals("LLD and AD with the same values are considered equal", ad1, lld1);
    }

}
