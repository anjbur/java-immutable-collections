///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2017, Burton Computer Corporation
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

package org.javimmutable.collections.array.bit32;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.cursors.StandardCursor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public class EmptyBit32Array<T>
        extends Bit32Array<T>
{
    @Override
    @Nullable
    public T getValueOr(int index,
                        @Nullable T defaultValue)
    {
        checkIndex(index);
        return defaultValue;
    }

    @Nonnull
    @Override
    public Holder<T> find(int index)
    {
        checkIndex(index);
        return Holders.of();
    }

    @Nonnull
    @Override
    public Bit32Array<T> assign(int index,
                                @Nullable T value)
    {
        checkIndex(index);
        return new SingleBit32Array<T>(index, value);
    }

    @Nonnull
    @Override
    public Bit32Array<T> delete(int index)
    {
        checkIndex(index);
        return this;
    }

    @Override
    public int size()
    {
        return 0;
    }

    @Override
    public int firstIndex()
    {
        throw new IndexOutOfBoundsException();
    }

    @Override
    @Nonnull
    public Cursor<JImmutableMap.Entry<Integer, T>> cursor()
    {
        return StandardCursor.of();
    }

    @Override
    public void checkInvariants()
    {
        //TODO: fix empty checkInvariants()
    }
}
