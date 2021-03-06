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

package org.javimmutable.collections.tree;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Holder;
import org.javimmutable.collections.Holders;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.cursors.StandardCursor;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Collection;
import java.util.Comparator;

@Immutable
public class EmptyNode<K, V>
        extends TreeNode<K, V>
{
    static final EmptyNode INSTANCE = new EmptyNode();

    private EmptyNode()
    {
    }

    @SuppressWarnings("unchecked")
    public static <K, V> EmptyNode<K, V> of()
    {
        return (EmptyNode<K, V>)INSTANCE;
    }

    @Override
    public V getValueOr(Comparator<K> props,
                        K key,
                        V defaultValue)
    {
        return defaultValue;
    }

    @Override
    public Holder<V> find(Comparator<K> props,
                          K key)
    {
        return Holders.of();
    }

    @Override
    public Holder<JImmutableMap.Entry<K, V>> findEntry(Comparator<K> props,
                                                       K key)
    {
        return Holders.of();
    }

    @Override
    public void addEntriesTo(Collection<JImmutableMap.Entry<K, V>> collection)
    {
    }

    @Override
    @Nonnull
    public Cursor<JImmutableMap.Entry<K, V>> cursor()
    {
        return StandardCursor.of();
    }

    @Override
    public boolean isEmpty()
    {
        return true;
    }

    @Override
    int verifyDepthsMatch()
    {
        return 0;
    }

    @Override
    K getMaxKey()
    {
        return null;
    }

    @Override
    UpdateResult<K, V> assignImpl(Comparator<K> props,
                                  K key,
                                  V value)
    {
        return UpdateResult.createInPlace(new LeafNode<K, V>(key, value), 1);
    }

    @Override
    DeleteResult<K, V> deleteImpl(Comparator<K> props,
                                  K key)
    {
        return DeleteResult.createUnchanged();
    }

    @Override
    DeleteMergeResult<K, V> leftDeleteMerge(TreeNode<K, V> node)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    DeleteMergeResult<K, V> rightDeleteMerge(TreeNode<K, V> node)
    {
        throw new UnsupportedOperationException();
    }
}
