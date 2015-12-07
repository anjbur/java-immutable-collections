///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2015, Burton Computer Corporation
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

package org.javimmutable.collections.bitmap;

import junit.framework.AssertionFailedError;
import org.javimmutable.collections.JImmutableBitmap;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.util.JImmutables;

import java.util.Random;

import static junit.framework.Assert.assertEquals;


public class StandardJImmutableBitmapTests
{
    private StandardJImmutableBitmapTests()
    {
    }

    public static void verifyBitmap(JImmutableBitmap template)
    {
        template.checkInvariants();
        JImmutableBitmap bitmap = template;
        JImmutableSet<Integer> valuesAdded = JImmutables.set();
        assertEquals(false, bitmap.getValue(10));
        bitmap = bitmap.insert(10);
        valuesAdded = valuesAdded.insert(10);
        assertEquals(false, bitmap == template);
        verifyAddedValues(bitmap, valuesAdded);
        assertEquals(bitmap, bitmap.insert(10));
        bitmap.checkInvariants();

        bitmap = bitmap.insert(20);
        valuesAdded = valuesAdded.insert(20);
        verifyAddedValues(bitmap, valuesAdded);

        bitmap = bitmap.insert(1024);
        valuesAdded = valuesAdded.insert(1024);
        verifyAddedValues(bitmap, valuesAdded);

        bitmap = bitmap.insert(2048);
        valuesAdded = valuesAdded.insert(2048);
        verifyAddedValues(bitmap, valuesAdded);

        bitmap = bitmap.insert(32768);
        valuesAdded = valuesAdded.insert(32768);
        verifyAddedValues(bitmap, valuesAdded);

        bitmap = bitmap.insert(33554432);
        valuesAdded = valuesAdded.insert(33554432);
        verifyAddedValues(bitmap, valuesAdded);

        bitmap = bitmap.insert(1073741924);
        valuesAdded = valuesAdded.insert(1073741924);
        verifyAddedValues(bitmap, valuesAdded);

        bitmap = bitmap.insert(-10);
        valuesAdded = valuesAdded.insert(-10);
        verifyAddedValues(bitmap, valuesAdded);

        bitmap = bitmap.insert(-1073741824);
        valuesAdded = valuesAdded.insert(-1073741824);
        verifyAddedValues(bitmap, valuesAdded);
    }

    private static void verifyAddedValues(JImmutableBitmap bitmap,
                                          JImmutableSet<Integer> valuesAdded)
    {
        for (Integer value : valuesAdded) {
            try {
                assertEquals(true, bitmap.getValue(value));
            } catch (AssertionFailedError e) {
                System.out.println("value : " + value);
                throw e;
            }
        }
    }

    public static void testSingleValue(JImmutableBitmap template)
    {
        Random random = new Random();
        for (int loop = 0; loop < 100; loop++) {
            int index = random.nextInt();
            JImmutableBitmap bitmap = template.insert(index);
            assertEquals(true, bitmap.getValue(index));
            int index2 = random.nextInt();
            bitmap = bitmap.insert(index2);
            assertEquals(true, bitmap.getValue(index));
            assertEquals(true, bitmap.getValue(index2));
        }
    }

    public static void testRandom(JImmutableBitmap template)
    {
        JImmutableBitmap bitmap = template;
        JImmutableSet<Integer> valuesAdded = JImmutables.set();
        Random random = new Random();
        for (int i = 0; i < 50; i++) {
            int index = random.nextInt();
            bitmap = bitmap.insert(index);
            valuesAdded = valuesAdded.insert(index);
            if (valuesAdded.size() != i + 1) {
                throw new AssertionFailedError();
            }
            assertEquals(true, bitmap.getValue(index));
            try {
                verifyAddedValues(bitmap, valuesAdded);
            } catch (AssertionFailedError e) {
                System.out.println("loop: " + i);
                throw e;
            }
        }
    }
}
