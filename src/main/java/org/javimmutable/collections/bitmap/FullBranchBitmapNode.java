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

public class FullBranchBitmapNode
        extends BitmapNode
{
    private final int shift;
    private final BitmapNode[] entries;

    FullBranchBitmapNode(int shift,
                         BitmapNode[] entries)
    {
        this.shift = shift;
        this.entries = entries;
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
        final int childIndex = (index >>> shift) & 0x1f;
        return entries[childIndex].getValue(shift - 5, index);
    }

    @Override
    public Holder<Boolean> find(int shift,
                                int index)
    {
        assert this.shift == shift;
        final int childIndex = (index >>> shift) & 0x1f;
        return entries[childIndex].find(shift - 5, index);
    }


    @Override
    public BitmapNode assign(int shift,
                             int index)
    {
        assert this.shift == shift;
        final int childIndex = (index >>> shift) & 0x1f;
        final BitmapNode child = entries[childIndex];
        final BitmapNode newChild = child.assign(shift - 5, index);
        if (newChild == child) {
            return this;
        } else {
            return createUpdatedEnBitmaps(shift, childIndex, newChild);
        }
    }

    @Override
    public BitmapNode delete(int shift,
                             int index)
    {
        assert this.shift == shift;
        final int childIndex = (index >>> shift) & 0x1f;
        final BitmapNode child = entries[childIndex];
        final BitmapNode newChild = child.delete(shift - 5, index);
        if (newChild == child) {
            return this;
        } else if (newChild.isEmpty()) {
            return MultiBranchBitmapNode.fullWithout(shift, entries, childIndex);
        } else {
            return createUpdatedEnBitmaps(shift, childIndex, newChild);
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


    private BitmapNode createUpdatedEnBitmaps(int shift,
                                              int childIndex,
                                              BitmapNode newChild)
    {
        assert newChild.isLeaf() || (newChild.getShift() == (shift - 5));
        BitmapNode[] newEnBitmaps = entries.clone();
        newEnBitmaps[childIndex] = newChild;
        return new FullBranchBitmapNode(shift, newEnBitmaps);
    }


}
