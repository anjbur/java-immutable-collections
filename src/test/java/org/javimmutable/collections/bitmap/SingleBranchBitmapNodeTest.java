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

public class SingleBranchBitmapNodeTest
    extends TestCase
{
    public void testConstructors()
    {
        LeafBitmapNode child = LeafBitmapNode.of(30 << 20);
        SingleBranchBitmapNode node = SingleBranchBitmapNode.forBranchIndex(20, 30, child);
        assertEquals(20, node.getShift());
        assertEquals(30, node.getBranchIndex());
        assertSame(child, node.getChild());

        node = SingleBranchBitmapNode.forIndex(20, 18 << 20, child);
        assertEquals(20, node.getShift());
        assertEquals(18, node.getBranchIndex());
        assertSame(child, node.getChild());
    }

    public void testNormal()
    {
        LeafBitmapNode child = LeafBitmapNode.of(30 << 20);
        SingleBranchBitmapNode node = SingleBranchBitmapNode.forBranchIndex(20, 30, child);
        assertEquals(false, node.getValue(20, 31 << 20));
        assertEquals(true, node.getValue(20, 30 << 20));
        assertEquals(Holders.<Boolean>of(), node.find(20, 31 << 20));
        assertEquals(Holders.of(true), node.find(20, 30 << 20));
        assertSame(node, node.assign(20, 30 << 20));
    }

    public void testPadding()
    {
        LeafBitmapNode child = LeafBitmapNode.of(30 << 20);
        SingleBranchBitmapNode node = SingleBranchBitmapNode.forBranchIndex(20, 30, child);

        BitmapNode newNode = node.paddedToMinimumDepthForShift(20);
        assertSame(node, newNode);

        newNode = node.paddedToMinimumDepthForShift(25);
        assertEquals(25, newNode.getShift());
        assertTrue(newNode instanceof SingleBranchBitmapNode);

        newNode = node.paddedToMinimumDepthForShift(30);
        assertEquals(30, newNode.getShift());
        assertTrue(newNode instanceof SingleBranchBitmapNode);
        assertEquals(0, ((SingleBranchBitmapNode)newNode).getBranchIndex());

        BitmapNode newChild = ((SingleBranchBitmapNode)newNode).getChild();
        assertEquals(25, newChild.getShift());
        assertTrue(newChild instanceof SingleBranchBitmapNode);
        assertEquals(0, ((SingleBranchBitmapNode)newChild).getBranchIndex());

        newChild = ((SingleBranchBitmapNode)newChild).getChild();
        assertSame(node, newChild);

        assertSame(node, node.trimmedToMinimumDepth());
        assertSame(node, newNode.trimmedToMinimumDepth());
    }
}
