package com.paytm.digital.education.utility;

import org.junit.Test;

import static com.paytm.digital.education.utility.ArrayUtils.padArray;
import static org.junit.Assert.assertEquals;

public class ArrayUtilsTest {

    @Test
    public void testNumberOfArgsLessThanArgsArrayLength() {
        Object[] actual = padArray(2, new String[] {"1", "2", "3", "4"});
        assertEquals(actual.length, 2);
        assertEquals(actual[0], "1");
        assertEquals(actual[1], "2");
    }

    @Test
    public void testNumberOfArgsMoreThanArgsArrayLength() {
        Object[] actual = padArray(5, new String[] {"1", "2", "3"});
        assertEquals(actual.length, 5);
        assertEquals(actual[0], "1");
        assertEquals(actual[1], "2");
        assertEquals(actual[2], "3");
        assertEquals(actual[3], "?");
        assertEquals(actual[4], "?");
    }

    @Test
    public void testNumberOfArgsSameAsArgsArrayLength() {
        Object[] actual = padArray(3, new String[] {"1", "2", "3"});
        assertEquals(actual.length, 3);
        assertEquals(actual[0], "1");
        assertEquals(actual[1], "2");
        assertEquals(actual[2], "3");
    }

    @Test
    public void testMixedArgType() {
        Object[] actual = padArray(3, new Object[] {"1", 2, 3L});
        assertEquals(actual.length, 3);
        assertEquals(actual[0], "1");
        assertEquals(actual[1], 2);
        assertEquals(actual[2], 3L);
    }
}
