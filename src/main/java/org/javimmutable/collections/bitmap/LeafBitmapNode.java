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

import javax.annotation.concurrent.Immutable;

@Immutable
public class LeafBitmapNode
        extends BitmapNode
{
    private final int shiftedIndex; //26 bit integer
    private final long value;
    private final int shift;

    private LeafBitmapNode(int index,
                           long value,
                           int shift)
    {
        this.shiftedIndex = index >> 6;
        this.value = value;
        this.shift = shift;
    }

    static LeafBitmapNode of(int index)
    {
        long value = 1 << (index & 0x3f);
        return new LeafBitmapNode(index, value, shiftForIndex(index));
    }


    static LeafBitmapNode of(int index,
                             long value)
    {
        return new LeafBitmapNode(index, value, shiftForIndex(index));
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
        assert shift >= 1;
        if (this.shiftedIndex != (index >> 6)) {
            return false;
        } else {
            long bit = 1 << (index & 0x3f);
            return (value & bit) != 0;
        }
    }

    @Override
    public Holder<Boolean> find(int shift,
                                int index)
    {
        return Holders.of(getValue(shift, index));
    }


    @Override
    public BitmapNode assign(int shift,
                             int index)
    {
        assert shift >= 1;
        if (this.shiftedIndex == index >> 6) {
            long bit = 1 << (index & 0x3f);
            if ((value & bit) != 0) {
                return this;
            } else {
                return withValue(value | bit);
            }
        } else {
            assert shift > 1;
            return SingleBranchBitmapNode.forIndex(shift, this.shiftedIndex << 6, this).assign(shift, index);
        }
    }

    //@Override
    public BitmapNode delete(int shift,
                             int index)
    {
        assert shift >= 1;
        if (this.shiftedIndex == index >> 6) {

            return of();
        } else {
            return this;
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
        return true;
    }

    public int getShiftedIndex()
    {
        return shiftedIndex;
    }

    @Override
    public BitmapNode paddedToMinimumDepthForShift(int shift)
    {
        if (this.shift >= shift) {
            return this;
        } else {
            return SingleBranchBitmapNode.forIndex(shift, shiftedIndex, this);
        }
    }


    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }

        LeafBitmapNode that = (LeafBitmapNode)o;

        if (shiftedIndex != that.shiftedIndex) {
            return false;
        }
        if (shift != that.shift) {
            return false;
        }
        //noinspection RedundantIfStatement
        if (value != that.value) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = shiftedIndex;
        result = 31 * result + getHashCode(value);
        result = 31 * result + shift;
        return result;
    }

    private int getHashCode(long value)
    {
        return ((Long)value).hashCode();
    }

    private BitmapNode withValue(long newValue)
    {
        return new LeafBitmapNode(shiftedIndex << 6, newValue, shift);
    }
}
