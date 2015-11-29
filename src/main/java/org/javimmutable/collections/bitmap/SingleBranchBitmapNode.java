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


import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;

public class SingleBranchBitmapNode
        extends BitmapNode
{
    private final int shift;
    private final int branchIndex;
    private final BitmapNode child;

    private SingleBranchBitmapNode(int shift,
                                   int branchIndex,
                                   BitmapNode child)
    {
        assert shift >= 0;
        this.shift = shift;
        this.branchIndex = branchIndex;
        this.child = child;
    }

    static SingleBranchBitmapNode forIndex(int shift,
                                           int index,
                                           BitmapNode child)
    {
        final int branchIndex = (index >>> shift) & 0x1f;
        return new SingleBranchBitmapNode(shift, branchIndex, child);
    }

    static SingleBranchBitmapNode forBranchIndex(int shift,
                                                 int branchIndex,
                                                 BitmapNode child)
    {
        return new SingleBranchBitmapNode(shift, branchIndex, child);
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }

    @Override
    public boolean getValue(int shift,
                            int index)
    {
        assert this.shift == shift;
        final int branchIndex = (index >>> shift) & 0x1f;
        return (this.branchIndex == branchIndex) && child.getValue(shift - 5, index);
    }


    @Override
    public Holder<Boolean> find(int shift,
                                int index)
    {
        assert this.shift == shift;
        final int branchIndex = (index >>> shift) & 0x1f;
        return (this.branchIndex == branchIndex) ? child.find(shift - 5, index) : Holders.<Boolean>of();
    }


    @Override
    public BitmapNode assign(int shift,
                             int index)
    {
        assert this.shift == shift;
        final int branchIndex = (index >>> shift) & 0x1f;
        if (this.branchIndex == branchIndex) {
            BitmapNode newChild = child.assign(shift - 5, index);
            return selectNodeForUpdateResult(shift, branchIndex, newChild);
        } else {
            return MultiBranchBitmapNode.forBranchIndex(shift, this.branchIndex, child).assign(shift, index);
        }
    }

    @Override
    public int getShift()
    {
        return shift;
    }

    @Override
    public boolean isLeaf()
    {
        return false;
    }

    @Override
    public BitmapNode trimmedToMinimumDepth()
    {
        return (branchIndex == 0) ? child.trimmedToMinimumDepth() : this;
    }


    // for tests
    int getBranchIndex()
    {
        return branchIndex;
    }

    // for tests
    BitmapNode getChild()
    {
        return child;
    }

    private BitmapNode selectNodeForUpdateResult(int shift,
                                                 int branchIndex,
                                                 BitmapNode newChild)
    {
        assert newChild.isLeaf() || (newChild.getShift() == (shift - 5));
        return (newChild == child) ? this : new SingleBranchBitmapNode(shift, branchIndex, newChild);
    }

    private BitmapNode selectNodeForDeleteResult(int shift,
                                                 int branchIndex,
                                                 BitmapNode newChild)
    {
        if (newChild == child) {
            return this;
        } else if (newChild.isEmpty()) {
            return of();
        } else if (newChild.isLeaf()) {
            return newChild;
        } else {
            assert newChild.getShift() == (shift - 5);
            return new SingleBranchBitmapNode(shift, branchIndex, newChild);
        }
    }
}
