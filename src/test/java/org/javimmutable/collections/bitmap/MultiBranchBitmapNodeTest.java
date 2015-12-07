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
import org.javimmutable.collections.Holders;

import java.util.Arrays;
import java.util.Random;

public class MultiBranchBitmapNodeTest
        extends TestCase
{

    public void testForBranchIndex()
    {
        for (int branchIndex = 0; branchIndex < 32; ++branchIndex) {
            BitmapNode leaf = LeafBitmapNode.of(branchIndex);
            MultiBranchBitmapNode node = MultiBranchBitmapNode.forBranchIndex(11, branchIndex, leaf);
            assertEquals(11, node.getShift());
            assertEquals(1 << branchIndex, node.getBitmask());
            assertTrue(Arrays.equals(new BitmapNode[]{leaf}, node.getEntries()));
        }
    }

    public void testForIndex()
    {
        final int shift = 11;
        final int baseIndex = ~(0x1f << shift);
        for (int branchIndex = 0; branchIndex < 32; ++branchIndex) {
            final int index = baseIndex | (branchIndex << shift);
            BitmapNode leaf = LeafBitmapNode.of(branchIndex);
            MultiBranchBitmapNode node = MultiBranchBitmapNode.forIndex(shift, index, leaf);
            assertEquals(shift, node.getShift());
            assertEquals(1 << branchIndex, node.getBitmask());
            assertTrue(Arrays.equals(new BitmapNode[]{leaf}, node.getEntries()));
        }
    }

    public void testForEntries()
    {
        for (int length = 1; length <= 32; ++length) {
            final BitmapNode[] entries = MultiBranchBitmapNode.allocate(length);
            for (int i = 0; i < length; ++i) {
                entries[i] = LeafBitmapNode.of(i);
            }
            final int bitmask = (length == 32) ? -1 : ((1 << length) - 1);
            final MultiBranchBitmapNode node = MultiBranchBitmapNode.forEntries(11, entries);
            assertEquals(11, node.getShift());
            assertEquals(bitmask, node.getBitmask());
            assertTrue(Arrays.equals(entries, node.getEntries()));
        }
    }

    public void testFullWithout()
    {
        final BitmapNode[] full = MultiBranchBitmapNode.allocate(32);
        for (int i = 0; i < 32; ++i) {
            full[i] = LeafBitmapNode.of(50 + i);
        }
        for (int without = 0; without < 32; ++without) {
            final BitmapNode[] entries = MultiBranchBitmapNode.allocate(31);
            int index = 0;
            for (int i = 0; i < 32; ++i) {
                if (i != without) {
                    entries[index++] = full[i];
                }
            }
            final int bitmask = ~(1 << without);
            final MultiBranchBitmapNode node = MultiBranchBitmapNode.fullWithout(11, full, without);
            assertEquals(11, node.getShift());
            assertEquals(bitmask, node.getBitmask());
            assertTrue(Arrays.equals(entries, node.getEntries()));
        }
    }

    public void testAssign()
    {
        BitmapNode node = MultiBranchBitmapNode.forTesting(20);
        for (int i = 0; i < 31; ++i) {
            node = node.assign(20, shiftIndex(20, i));
        }
        BitmapNode fullNode = node.assign(20, shiftIndex(20, 31));
        for (int i = 0; i < 32; ++i) {
            assertEquals(Holders.of(true), fullNode.find(20, shiftIndex(20, i)));
        }
        assertEquals(true, node instanceof MultiBranchBitmapNode);
        for (int i = 30; i >= 0; --i) {
            node = node.assign(20, shiftIndex(20, i));
        }
        for (int i = 0; i < 31; ++i) {
            assertEquals(Holders.of(true), node.find(20, shiftIndex(20, i)));
        }
    }

    public void testRandom()
    {
        BitmapNode node = MultiBranchBitmapNode.of();
        Integer index[] = new Integer[31];
        Random random = new Random();
        for (int i = 0; i < 2; i++) {
            index[i] = random.nextInt();
            node = node.assign(BitmapNode.shiftForIndex(index[i]), index[i]);
        }
        for (int i = 0; i < 2; ++i) {
            assertEquals(true, node.getValue(BitmapNode.shiftForIndex(index[i]), index[i]));
        }
    }

    private int shiftIndex(int shift,
                           int index)
    {
        return ~(0x1f << shift) | (index << shift);
    }
}
