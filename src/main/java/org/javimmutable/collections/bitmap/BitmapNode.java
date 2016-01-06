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

import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class BitmapNode
{
    public abstract boolean isEmpty();

    public abstract boolean getValue(int shift,
                                    int index);


    public abstract Holder<Boolean> find(int shift,
                                         int index);

    public abstract BitmapNode assign(int shift,
                                      int index);

    //public abstract BitmapNode delete(int shift,
    //                                  int index);

    public abstract int getShift();

    public abstract boolean isLeaf();

    public BitmapNode trimmedToMinimumDepth()
    {
        return this;
    }

    public BitmapNode paddedToMinimumDepthForShift(int shift)
    {
        BitmapNode node = this;
        int nodeShift = node.getShift();
        while (nodeShift < shift) {
            nodeShift += 5;
            node = SingleBranchBitmapNode.forBranchIndex(nodeShift, 0, node);
        }
        return node;
    }

    public static BitmapNode of()
    {
        return EmptyBitmapNode.instance();
    }

    public static int shiftForIndex(int index)
    {
        switch (Integer.numberOfLeadingZeros(index)) {
        case 0:
            return 25 + 6;
        case 1:
        case 2:
        case 3:
        case 4:
        case 5:
            return 20 + 6;

        case 6:
        case 7:
        case 8:
        case 9:
        case 10:
            return 15 + 6;

        case 11:
        case 12:
        case 13:
        case 14:
        case 15:
            return 10 + 6;

        case 16:
        case 17:
        case 18:
        case 19:
        case 20:
            return 5 + 6;

        case 21:
        case 22:
        case 23:
        case 24:
        case 25:

        case 26:
        case 27:
        case 28:
        case 29:
        case 30:
        case 31:
        case 32:
            return 6;
        }
        throw new IllegalArgumentException();
    }
}
