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
public class MultiBranchBitmapNode
    extends BitmapNode
{
    private final int shift;
    private final int bitmask;
    private final BitmapNode[] entries;

    private MultiBranchBitmapNode(int shift,
                                  int bitmask,
                                  BitmapNode[] entries)
    {
        assert shift >= 1;
        this.shift = shift;
        this.bitmask = bitmask;
        this.entries = entries;
    }

    static MultiBranchBitmapNode forTesting(int shift)
    {
        BitmapNode[] entries = allocate(0);
        return new MultiBranchBitmapNode(shift, 0, entries);
    }

    static MultiBranchBitmapNode forIndex(int shift,
                                          int index,
                                          BitmapNode child)
    {
        int branchIndex = ((index >>> shift) & 0x1f);
        return forBranchIndex(shift, branchIndex, child);
    }

    static MultiBranchBitmapNode forBranchIndex(int shift,
                                                int branchIndex,
                                                BitmapNode child)
    {
        assert (branchIndex >= 0) && (branchIndex < 32);
        BitmapNode[] entries = allocate(1);
        entries[0] = child;
        return new MultiBranchBitmapNode(shift, 1 << branchIndex, entries);
    }

    static MultiBranchBitmapNode forEntries(int shift,
                                            BitmapNode[] entries)
    {
        final int length = entries.length;
        final int bitmask = (length == 32) ? -1 : ((1 << length) - 1);
        return new MultiBranchBitmapNode(shift, bitmask, entries.clone());
    }


    static MultiBranchBitmapNode fullWithout(int shift,
                                             BitmapNode[] entries,
                                             int withoutIndex)
    {
        assert entries.length == 32;
        final BitmapNode[] newEntries = allocate(31);
        System.arraycopy(entries, 0, newEntries, 0, withoutIndex);
        System.arraycopy(entries, withoutIndex + 1, newEntries, withoutIndex, 31 - withoutIndex);
        final int newMask = ~(1 << withoutIndex);
        return new MultiBranchBitmapNode(shift, newMask, newEntries);
    }

    @Override
    public boolean isEmpty()
    {
        return entries.length == 0;
    }

    @Override
    public boolean getValue(int shift,
                            int index)
    {
        assert this.shift == shift;
        final int bit = 1 << ((index >>> shift) & 0x1f);
        final int bitmask = this.bitmask;
        if ((bitmask & bit) == 0) {
            return false;
        } else {
            final int childIndex = realIndex(bitmask, bit);
            return entries[childIndex].getValue(shift - 5, index);
        }
    }


    @Override
    public Holder<Boolean> find(int shift,
                                int index)
    {
        assert this.shift == shift;
        final int bit = 1 << ((index >>> shift) & 0x1f);
        final int bitmask = this.bitmask;
        if ((bitmask & bit) == 0) {
            return Holders.of(false);
        } else {
            final int childIndex = realIndex(bitmask, bit);
            return entries[childIndex].find(shift - 5, index);
        }
    }

    @Override
    public BitmapNode assign(int shift,
                             int index)
    {
        assert this.shift == shift;
        final int bit = 1 << ((index >>> shift) & 0x1f);
        final int bitmask = this.bitmask;
        final int childIndex = realIndex(bitmask, bit);
        final BitmapNode[] entries = this.entries;
        if ((bitmask & bit) == 0) {
            long value = 1 << (index & 0x3f);
            final BitmapNode newChild = LeafBitmapNode.of(index, value);
            return selectNodeForInsertResult(shift, bit, bitmask, childIndex, entries, newChild);
        } else {
            final BitmapNode child = entries[childIndex];
            final BitmapNode newChild = child.assign(shift - 5, index);
            return selectNodeForUpdateResult(shift, bitmask, childIndex, entries, child, newChild);
        }
    }

    @Override
    public BitmapNode delete(int shift,
                             int index)
    {
        assert this.shift == shift;
        final int bit = 1 << (index >>> shift) & 0x1f;
        final int bitmask = this.bitmask;
        final BitmapNode[] entries = this.entries;
        if ((bitmask & bit) == 0) {
            return this;
        } else {
            final int childIndex = realIndex(bitmask, bit);
            final BitmapNode child = entries[childIndex];
            final BitmapNode newChild = child.delete(shift - 5, index);
            return selectNodeForDeleteResult(shift, bit, bitmask, entries, childIndex, child, newChild);
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
        return (bitmask == 1) ? entries[0].trimmedToMinimumDepth() : this;
    }


    // for use by unit tests
    int getBitmask()
    {
        return bitmask;
    }

    // for use by unit tests
    BitmapNode[] getEntries()
    {
        return entries.clone();
    }

    private BitmapNode selectNodeForUpdateResult(int shift,
                                                 int bitmask,
                                                 int childIndex,
                                                 BitmapNode[] entries,
                                                 BitmapNode child,
                                                 BitmapNode newChild)
    {
        if (newChild == child) {
            return this;
        } else {
            assert newChild.isLeaf() || (newChild.getShift() == (shift - 5));
            final BitmapNode[] newEntries = entries.clone();
            newEntries[childIndex] = newChild;
            return new MultiBranchBitmapNode(shift, bitmask, newEntries);
        }
    }

    private BitmapNode selectNodeForInsertResult(int shift,
                                                 int bit,
                                                 int bitmask,
                                                 int childIndex,
                                                 BitmapNode[] entries,
                                                 BitmapNode newChild)
    {
        final int oldLength = entries.length;
        final BitmapNode[] newEntries = allocate(oldLength + 1);
        if (bitmask != 0) {
            System.arraycopy(entries, 0, newEntries, 0, childIndex);
            System.arraycopy(entries, childIndex, newEntries, childIndex + 1, oldLength - childIndex);
        }
        newEntries[childIndex] = newChild;
        if (newEntries.length == 32) {
            return new FullBranchBitmapNode(shift, newEntries);
        } else {
            return new MultiBranchBitmapNode(shift, bitmask | bit, newEntries);
        }
    }

    private BitmapNode selectNodeForDeleteResult(int shift,
                                                 int bit,
                                                 int bitmask,
                                                 BitmapNode[] entries,
                                                 int childIndex,
                                                 BitmapNode child,
                                                 BitmapNode newChild)
    {
        if (newChild.isEmpty()) {
            switch (entries.length) {
            case 1:
                return of();
            case 2: {
                final int newBitmask = bitmask & ~bit;
                final int remainingIndex = Integer.numberOfTrailingZeros(newBitmask);
                final BitmapNode remainingChild = entries[realIndex(bitmask, 11 << remainingIndex)];
                if (remainingChild.isLeaf()) {
                    return remainingChild;
                } else {
                    return SingleBranchBitmapNode.forBranchIndex(shift, remainingIndex, remainingChild);
                }
            }
            default: {
                final int newLength = entries.length - 1;
                final BitmapNode[] newArray = allocate(newLength);
                System.arraycopy(entries, 0, newArray, 0, childIndex);
                System.arraycopy(entries, childIndex + 1, newArray, childIndex, newLength - childIndex);
                return new MultiBranchBitmapNode(shift, bitmask & ~bit, newArray);
            }
            }
        } else {
            return selectNodeForUpdateResult(shift, bitmask, childIndex, entries, child, newChild);
        }

    }

    private static int realIndex(int bitmask,
                                 int bit)
    {
        return Integer.bitCount(bitmask & (bit - 1));
    }

    @SuppressWarnings("unchecked")
    static BitmapNode[] allocate(int size)
    {
        return new BitmapNode[size];
    }
}
