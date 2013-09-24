///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2013, Burton Computer Corporation
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
//     Redistributions of source code must retain the above copyright
//     notice, this list of conditions and the following disclaimer.
//
//     Redistributions in binary form must reproduce the above copyright
//     notice, this list of conditions and the following disclaimer in
//     the documentation and/or other materials provided with the
//     distribution.
//
//     Neither the name of the Burton Computer Corporation nor the names
//     of its contributors may be used to endorse or promote products
//     derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package org.javimmutable.collections.cursors;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Func1;
import junit.framework.TestCase;

public class MultiTransformCursorTest
        extends TestCase
{
    public void testEmpty()
    {
        RangeTransform transform = new RangeTransform();
        Cursor<Integer> multi = MultiTransformCursor.of(EmptyCursor.<Integer>of(), transform);
        try {
            multi.hasValue();
        } catch (IllegalStateException ex) {
            //expected
        }
        try {
            multi.getValue();
        } catch (IllegalStateException ex) {
            //expected
        }
        multi = multi.next();
        assertTrue(multi instanceof EmptyCursor);
        assertEquals(false, multi.hasValue());
    }

    public void testSingle()
    {
        RangeTransform transform = new RangeTransform();
        Cursor<Integer> multi = MultiTransformCursor.of(SingleValueCursor.<Integer>of(3), transform);
        try {
            multi.hasValue();
        } catch (IllegalStateException ex) {
            //expected
        }
        try {
            multi.getValue();
        } catch (IllegalStateException ex) {
            //expected
        }

        multi = multi.next();
        assertEquals(true, multi.hasValue());
        assertEquals(1, (int)multi.getValue());

        multi = multi.next();
        assertEquals(true, multi.hasValue());
        assertEquals(2, (int)multi.getValue());

        multi = multi.next();
        assertEquals(true, multi.hasValue());
        assertEquals(3, (int)multi.getValue());

        multi = multi.next();
        assertTrue(multi instanceof EmptyCursor);
        assertEquals(false, multi.hasValue());
    }

    public void testMultiple()
    {
        RangeTransform transform = new RangeTransform();
        Cursor<Integer> multi = MultiTransformCursor.of(StandardCursor.forRange(1, 3), transform);
        try {
            multi.hasValue();
        } catch (IllegalStateException ex) {
            //expected
        }
        try {
            multi.getValue();
        } catch (IllegalStateException ex) {
            //expected
        }

        // 1
        multi = multi.next();
        assertEquals(true, multi.hasValue());
        assertEquals(1, (int)multi.getValue());

        // 2
        multi = multi.next();
        assertEquals(true, multi.hasValue());
        assertEquals(1, (int)multi.getValue());

        multi = multi.next();
        assertEquals(true, multi.hasValue());
        assertEquals(2, (int)multi.getValue());

        // 3
        multi = multi.next();
        assertEquals(true, multi.hasValue());
        assertEquals(1, (int)multi.getValue());

        multi = multi.next();
        assertEquals(true, multi.hasValue());
        assertEquals(2, (int)multi.getValue());

        multi = multi.next();
        assertEquals(true, multi.hasValue());
        assertEquals(3, (int)multi.getValue());

        multi = multi.next();
        assertTrue(multi instanceof EmptyCursor);
        assertEquals(false, multi.hasValue());
    }

    private static class RangeTransform
            implements Func1<Integer, Cursor<Integer>>
    {
        @Override
        public Cursor<Integer> apply(Integer value)
        {
            return StandardCursor.forRange(1, value);
        }
    }
}
