package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> alnr1 = new AListNoResizing<>();
        BuggyAList<Integer> bal1 = new BuggyAList<>();

        alnr1.addLast(1);
        bal1.addLast(1);

        alnr1.addLast(2);
        bal1.addLast(2);

        alnr1.addLast(3);
        bal1.addLast(3);

        assertEquals(alnr1.removeLast().intValue(), bal1.removeLast().intValue());
        assertEquals(alnr1.removeLast().intValue(), bal1.removeLast().intValue());
        assertEquals(alnr1.removeLast().intValue(), bal1.removeLast().intValue());
    }

    @Test
    public void testRandomOps() {
        // Make sure no removeLast() is called on empty lists
        final int len = 10;
        final int ops = 5000;
        AListNoResizing<Integer> alnr1 = new AListNoResizing<>();
        BuggyAList<Integer> bal1 = new BuggyAList<>();

        for (int i = 0; i < len; ++i) {
            alnr1.addLast(i);
            bal1.addLast(i);
        }

        for (int i = 0; i < ops; ++i) {
            int op = StdRandom.uniform(0, 4);
            switch (op) {
                case 0:
                    alnr1.addLast(i);
                    bal1.addLast(i);
                    break;
                case 1:
                    assertEquals(alnr1.getLast().intValue(), bal1.getLast().intValue());
                    break;
                case 2:
                    assertEquals(alnr1.size(), bal1.size());
                    for (int j = 0; j < alnr1.size(); ++j) {
                        assertEquals(alnr1.get(j).intValue(), bal1.get(j).intValue());
                    }
                    break;
                case 3:
                    assertEquals(alnr1.removeLast().intValue(), bal1.removeLast().intValue());
                    break;
            }
        }
    }
}
