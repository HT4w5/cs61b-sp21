package deque;

import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.*;

class ArithmeticIntegerComparison implements Comparator<Integer> {
    @Override
    public int compare(Integer a, Integer b) {
        return Integer.compare(a, b);
    }
}

class ReversedArithmeticIntegerComparison implements Comparator<Integer> {
    @Override
    public int compare(Integer a, Integer b) {
        return Integer.compare(b, a);
    }
}


public class MaxArrayDequeTest {
    @Test
    /* Test MaxArrayDeque with Integers and arithmetic comparison  */
    public void integerMADTest() {
        final var comp1 = new ArithmeticIntegerComparison();
        final var comp2 = new ReversedArithmeticIntegerComparison();
        var mad1 = new MaxArrayDeque<Integer>(comp1);

        for (int i = 0; i < 10; ++i) {
            mad1.addFirst(i);
            assertEquals("Max element should be " + i, i, mad1.max().intValue());
            assertEquals("Min element should be 0", 0, mad1.max(comp2).intValue());
        }
    }

    @Test
    /* Test MaxArrayDeque.max() returns null when empty */
    public void emptyMADTest() {
        final var comp1 = new ArithmeticIntegerComparison();
        final var comp2 = new ReversedArithmeticIntegerComparison();
        var mad1 = new MaxArrayDeque<Integer>(comp1);

        assertNull("max() should return null when empty", mad1.max());
        assertNull("max(Comparator<T>) should return null when empty", mad1.max(comp2));
    }
}
