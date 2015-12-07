package org.javimmutable.collections.bitmap;


import junit.framework.TestCase;

public class BitmapNodeTest
        extends TestCase
{
    public void testShift()
    {
        assertEquals(31, BitmapNode.shiftForIndex(1 << 31));
        for (int i = 26; i < 31; i++) {
            assertEquals(26, BitmapNode.shiftForIndex(1 << i));
        }
        for (int i = 21; i < 26; i++) {
            assertEquals(21, BitmapNode.shiftForIndex(1 << i));
        }
        for (int i = 16; i < 21; i++) {
            assertEquals(16, BitmapNode.shiftForIndex(1 << i));
        }
        for (int i = 11; i < 16; i++) {
            assertEquals(11, BitmapNode.shiftForIndex(1 << i));
        }
        for (int i = 0; i < 11; i++) {
            assertEquals(6, BitmapNode.shiftForIndex(1 << i));
        }
        assertEquals(6, BitmapNode.shiftForIndex(0));
    }
}
