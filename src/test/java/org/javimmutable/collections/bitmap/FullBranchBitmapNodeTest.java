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
import org.javimmutable.collections.common.IndexedList;

import java.util.ArrayList;
import java.util.List;

public class FullBranchBitmapNodeTest
        extends TestCase
{
    public void testOperations()
    {
        BitmapNode[] children = new BitmapNode[32];
        int shift = 6;
        for (int i = 0; i < 32; i++) {
            children[i] = LeafBitmapNode.of(i << shift);
        }
        BitmapNode node = new FullBranchBitmapNode(shift, children);
        for (int i = 0; i < 32; ++i) {
            assertEquals(true, node.getValue(shift, i << shift));
            assertTrue(node.find(shift, i << shift).getValue());
            assertEquals(false, node.getValue(shift, (32 + i) << shift));
            assertEquals(false, node.find(shift, (32 + i) << shift).isEmpty());
        }
        for (int i = 31; i >= 0; --i) {
            node = node.assign(shift, i << shift);
            assertTrue(node instanceof FullBranchBitmapNode);
        }
        for (int i = 0; i < 32; ++i) {
            assertEquals(true, node.getValue(shift, i << shift));
            assertTrue(node.find(shift, i << shift).getValue());
        }
    }
}
