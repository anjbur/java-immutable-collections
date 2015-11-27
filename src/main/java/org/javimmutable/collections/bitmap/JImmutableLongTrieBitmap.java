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

import org.javimmutable.collections.JImmutableArray;
import org.javimmutable.collections.JImmutableBitmap;
import org.javimmutable.collections.array.trie32.TrieArray;

import javax.annotation.Nonnull;

/*
 * This implementation is more efficient than JImmutableBooleanTrieBitmap, because each
 * element in the array can hold 64 different bit instead of just one. This is done by
 * shifting the index to set/get by 6 to index into the array, and then setting or checking
 * individual bits in the Long stored at that element. *
 */
public class JImmutableLongTrieBitmap
    implements JImmutableBitmap
{
    JImmutableArray<Long> array;

    private static final JImmutableLongTrieBitmap EMPTY = new JImmutableLongTrieBitmap(TrieArray.<Long>of());

    private JImmutableLongTrieBitmap(JImmutableArray<Long> array)
    {
        this.array = array;
    }

    public static JImmutableLongTrieBitmap of()
    {
        return EMPTY;
    }

    @Nonnull
    @Override
    public JImmutableBitmap insert(int index)
    {
        JImmutableArray<Long> newArray = array;
        int arrayIndex = index >>> 6;
        long mask = 1 << (index & 0x3f);
        Long arrayElement = newArray.get(arrayIndex);
        if (arrayElement == null) { //doesn't have any element for index
            newArray = newArray.assign(arrayIndex, mask);
        } else if ((arrayElement & mask) == 0) { //does not contain index
            newArray = newArray.assign(arrayIndex, arrayElement | mask);
        }
        return (newArray != array) ? new JImmutableLongTrieBitmap(newArray) : this;
    }

    @Override
    public boolean contains(int index)
    {
        int arrayIndex = index >>> 6;
        long mask = 1 << (index & 0x3f);
        Long arrayElement = array.get(arrayIndex);
        return (arrayElement != null) && ((mask & arrayElement) != 0);
    }

    @Override
    public void checkInvariants()
    {
        for (int index : array.keysCursor()) {
            if (array.get(index) == null || array.get(index) == (long)0) {
                throw new IllegalStateException(String.format("array contains zero. Found %s at %d%n",
                                                              array.get(index), index));

            }
        }
    }
}
