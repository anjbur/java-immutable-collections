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

import junit.framework.TestCase;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.util.JImmutables;

import java.util.Random;

public class LeafBitmapNodeTest
        extends TestCase
{
    public void testConstructors()
    {
        for (int shift = 0; shift < 32; ++shift) {
            LeafBitmapNode leaf = LeafBitmapNode.of(1 << shift);
            assertEquals((1 << shift) >> 6, leaf.getShiftedIndex());
            assertEquals(true, leaf.getValue(6, 1 << shift));
        }
        for (int index = 0; index < 32; ++index) {
            LeafBitmapNode leaf = LeafBitmapNode.of(index);
            assertEquals(0, leaf.getShiftedIndex());
            assertEquals(true, leaf.getValue(6, index));
        }
    }

    public void testRandom()
    {
        Random random = new Random();
        for (int loops = 0; loops < 100; ++loops) {
            int index = random.nextInt();
            LeafBitmapNode leaf = LeafBitmapNode.of(index);
            assertEquals(true, leaf.getValue(BitmapNode.shiftForIndex(index), index));
        }
    }

    public void testVarious()
    {
        JImmutableSet<Integer> valuesAdded = JImmutables.set();
        BitmapNode node = LeafBitmapNode.of();
        int baseIndex = 0xffffff00;
        node = node.assign(BitmapNode.shiftForIndex(baseIndex | (0x20)), baseIndex | (0x20));
        valuesAdded = valuesAdded.insert(baseIndex | (0x20));
        assertEquals(true, node.getValue(BitmapNode.shiftForIndex(baseIndex | (0x20)), baseIndex | (0x20)));
        node = node.assign(BitmapNode.shiftForIndex(baseIndex | (0x30)), baseIndex | (0x30));
        valuesAdded = valuesAdded.insert(baseIndex | (0x30));

        //check for all values
        for (Integer index : valuesAdded) {
            assertEquals(true, node.getValue(BitmapNode.shiftForIndex(index), index));
        }
    }
}