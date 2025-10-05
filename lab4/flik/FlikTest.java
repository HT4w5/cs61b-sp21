package flik;

import static org.junit.Assert.*;

import org.junit.Test;


public class FlikTest {
    @Test
    public void intTests() {
        int a = 0;
        int b = 0;
        for (int i = 0; i < 1000; ++i) {
            assertTrue(Flik.isSameNumber(a, b));
            ++a;
            ++b;
        }
    }
}
