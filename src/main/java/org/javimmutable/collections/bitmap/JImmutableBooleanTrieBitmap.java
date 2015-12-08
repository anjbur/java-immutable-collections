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
import javax.annotation.concurrent.Immutable;

@Immutable
public class JImmutableBooleanTrieBitmap
    implements JImmutableBitmap
{
    JImmutableArray<Boolean> array;

    private static final JImmutableBooleanTrieBitmap EMPTY = new JImmutableBooleanTrieBitmap(TrieArray.<Boolean>of());

    private JImmutableBooleanTrieBitmap(JImmutableArray<Boolean> array)
    {
        this.array = array;
    }

    public static JImmutableBooleanTrieBitmap of()
    {
        return EMPTY;
    }

    @Nonnull
    @Override
    public JImmutableBooleanTrieBitmap insert(int index)
    {
        return (getValue(index)) ? this : new JImmutableBooleanTrieBitmap(array.assign(index, true));
    }

    @Nonnull
    public JImmutableBooleanTrieBitmap delete(int index)
    {
        return (getValue(index)) ? new JImmutableBooleanTrieBitmap(array.delete(index)) : this;
    }

    @Override
    public boolean getValue(int index)
    {
        return array.find(index).isFilled();
    }


    public int size()
    {
        return array.size();
    }

    public boolean isEmpty()
    {
        return array.isEmpty();
    }

    public void checkInvariants()
    {
        for (int index : array.keysCursor()) {
            if (array.get(index) == null || !array.get(index)) {
                throw new IllegalStateException(String.format("array contains non-true value. Found %s at %d%n",
                                                              array.get(index), index));

            }
        }
    }
}