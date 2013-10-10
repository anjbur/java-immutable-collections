///###////////////////////////////////////////////////////////////////////////
//
// Burton Computer Corporation
// http://www.burton-computer.com
//
// Copyright (c) 2013, Burton Computer Corporation
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

package org.javimmutable.collections.util;

import org.javimmutable.collections.Cursor;
import org.javimmutable.collections.Cursorable;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableMap;
import org.javimmutable.collections.JImmutableRandomAccessList;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.JImmutableStack;
import org.javimmutable.collections.hash.JImmutableHashMap;
import org.javimmutable.collections.hash.JImmutableHashSet;
import org.javimmutable.collections.list.JImmutableArrayList;
import org.javimmutable.collections.list.JImmutableLinkedStack;
import org.javimmutable.collections.tree.ComparableComparator;
import org.javimmutable.collections.tree.JImmutableTreeMap;
import org.javimmutable.collections.tree.JImmutableTreeSet;
import org.javimmutable.collections.tree_list.JImmutableTreeList;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

/**
 * This class contains static factory methods to create instances of each of the collection interfaces.
 * Overloaded variants are provided for each to pre-populate the created collection with existing values.
 * Where possible the empty collection methods return a common singleton instance to save memory.  The
 * factory methods always return the fastest implementation of each interface (i.e. hash when sort not
 * required, trie when random access not required, etc).
 */
public final class JImmutables
{
    public static <T> JImmutableStack<T> stack()
    {
        return JImmutableLinkedStack.of();
    }

    public static <T> JImmutableStack<T> stack(T... values)
    {
        return Functions.insertAll(JImmutableLinkedStack.<T>of(), values);
    }

    public static <T> JImmutableStack<T> stack(Cursor<T> cursor)
    {
        return Functions.insertAll(JImmutableLinkedStack.<T>of(), cursor);
    }

    public static <T> JImmutableStack<T> stack(Cursorable<T> cursorable)
    {
        return Functions.insertAll(JImmutableLinkedStack.<T>of(), cursorable.cursor());
    }

    public static <T> JImmutableStack<T> stack(Iterator<T> iterator)
    {
        return Functions.insertAll(JImmutableLinkedStack.<T>of(), iterator);
    }

    public static <T> JImmutableStack<T> stack(Collection<T> collection)
    {
        return Functions.insertAll(JImmutableLinkedStack.<T>of(), collection.iterator());
    }

    public static <T> JImmutableList<T> list()
    {
        return JImmutableArrayList.of();
    }

    public static <T> JImmutableList<T> list(T... values)
    {
        return Functions.insertAll(JImmutableArrayList.<T>of(), values);
    }

    public static <T> JImmutableList<T> list(Cursor<T> cursor)
    {
        return Functions.insertAll(JImmutableArrayList.<T>of(), cursor);
    }

    public static <T> JImmutableList<T> list(Cursorable<T> cursorable)
    {
        return Functions.insertAll(JImmutableArrayList.<T>of(), cursorable.cursor());
    }

    public static <T> JImmutableList<T> list(Iterator<T> iterator)
    {
        return Functions.insertAll(JImmutableArrayList.<T>of(), iterator);
    }

    public static <T> JImmutableList<T> list(Collection<T> collection)
    {
        return Functions.insertAll(JImmutableArrayList.<T>of(), collection.iterator());
    }

    public static <T> JImmutableRandomAccessList<T> ralist()
    {
        return JImmutableTreeList.of();
    }

    public static <T> JImmutableRandomAccessList ralist(Cursor<T> cursor)
    {
        return Functions.insertAll(JImmutableTreeList.<T>of(), cursor);
    }

    public static <T> JImmutableRandomAccessList ralist(T... values)
    {
        return Functions.insertAll(JImmutableTreeList.<T>of(), values);
    }

    public static <T> JImmutableRandomAccessList ralist(Cursorable<T> cursorable)
    {
        return Functions.insertAll(JImmutableTreeList.<T>of(), cursorable.cursor());
    }

    public static <T> JImmutableRandomAccessList<T> ralist(Iterator<T> iterator)
    {
        return Functions.insertAll(JImmutableTreeList.<T>of(), iterator);
    }

    public static <T> JImmutableRandomAccessList<T> ralist(Collection<T> collection)
    {
        return Functions.insertAll(JImmutableTreeList.<T>of(), collection.iterator());
    }

    public static <K, V> JImmutableMap<K, V> map()
    {
        return JImmutableHashMap.of();
    }

    public static <K, V> JImmutableMap<K, V> map(Map<K, V> map)
    {
        return Functions.assignAll(JImmutableHashMap.<K, V>of(), map);
    }

    public static <K, V> JImmutableMap<K, V> map(JImmutableMap<K, V> map)
    {
        return Functions.assignAll(JImmutableHashMap.<K, V>of(), map);
    }

    public static <K extends Comparable<K>, V> JImmutableMap<K, V> sortedMap()
    {
        return JImmutableTreeMap.of();
    }

    public static <K extends Comparable<K>, V> JImmutableMap<K, V> sortedMap(Map<K, V> map)
    {
        return Functions.assignAll(JImmutableTreeMap.<K, V>of(), map);
    }

    public static <K extends Comparable<K>, V> JImmutableMap<K, V> sortedMap(JImmutableMap<K, V> map)
    {
        if (map instanceof JImmutableTreeMap) {
            JImmutableTreeMap treemap = (JImmutableTreeMap)map;
            if (treemap.getComparatorClass().equals(ComparableComparator.class)) {
                return map;
            }
        }
        return Functions.assignAll(JImmutableTreeMap.<K, V>of(), map);
    }

    public static <K, V> JImmutableMap<K, V> sortedMap(Comparator<K> comparator)
    {
        return JImmutableTreeMap.of(comparator);
    }

    public static <K, V> JImmutableMap<K, V> sortedMap(Comparator<K> comparator,
                                                       Map<K, V> map)
    {
        return Functions.assignAll(JImmutableTreeMap.<K, V>of(comparator), map);
    }

    public static <K, V> JImmutableMap<K, V> sortedMap(Comparator<K> comparator,
                                                       JImmutableMap<K, V> map)
    {
        return Functions.assignAll(JImmutableTreeMap.<K, V>of(comparator), map);
    }

    public static <T> JImmutableSet<T> set()
    {
        return JImmutableHashSet.of();
    }

    public static <T> JImmutableSet<T> set(Cursor<T> cursor)
    {
        return Functions.insertAll(JImmutableHashSet.<T>of(), cursor);
    }

    public static <T> JImmutableSet<T> set(T... values)
    {
        return Functions.insertAll(JImmutableHashSet.<T>of(), values);
    }

    public static <T> JImmutableSet<T> set(Cursorable<T> cursorable)
    {
        return Functions.insertAll(JImmutableHashSet.<T>of(), cursorable.cursor());
    }

    public static <T> JImmutableSet<T> set(Iterator<T> iterator)
    {
        return Functions.insertAll(JImmutableHashSet.<T>of(), iterator);
    }

    public static <T> JImmutableSet<T> set(Collection<T> collection)
    {
        return Functions.insertAll(JImmutableHashSet.<T>of(), collection.iterator());
    }

    public static <T extends Comparable<T>> JImmutableSet<T> sortedSet()
    {
        return JImmutableTreeSet.of();
    }

    public static <T extends Comparable<T>> JImmutableSet<T> sortedSet(T... values)
    {
        return Functions.insertAll(JImmutableTreeSet.<T>of(), values);
    }

    public static <T extends Comparable<T>> JImmutableSet<T> sortedSet(Cursor<T> cursor)
    {
        return Functions.insertAll(JImmutableTreeSet.<T>of(), cursor);
    }

    public static <T extends Comparable<T>> JImmutableSet<T> sortedSet(Cursorable<T> cursorable)
    {
        return Functions.insertAll(JImmutableTreeSet.<T>of(), cursorable.cursor());
    }

    public static <T extends Comparable<T>> JImmutableSet<T> sortedSet(Iterator<T> iterator)
    {
        return Functions.insertAll(JImmutableTreeSet.<T>of(), iterator);
    }

    public static <T extends Comparable<T>> JImmutableSet<T> sortedSet(Collection<T> collection)
    {
        return Functions.insertAll(JImmutableTreeSet.<T>of(), collection.iterator());
    }

    public static <T> JImmutableSet<T> sortedSet(Comparator<T> comparator)
    {
        return JImmutableTreeSet.of(comparator);
    }

    public static <T> JImmutableSet<T> sortedSet(Comparator<T> comparator,
                                                 Cursor<T> cursor)
    {
        return Functions.insertAll(JImmutableTreeSet.<T>of(comparator), cursor);
    }

    public static <T> JImmutableSet<T> sortedSet(Comparator<T> comparator,
                                                 T... values)
    {
        return Functions.insertAll(JImmutableTreeSet.<T>of(comparator), values);
    }

    public static <T> JImmutableSet<T> sortedSet(Comparator<T> comparator,
                                                 Cursorable<T> cursorable)
    {
        return Functions.insertAll(JImmutableTreeSet.<T>of(comparator), cursorable.cursor());
    }

    public static <T> JImmutableSet<T> sortedSet(Comparator<T> comparator,
                                                 Iterator<T> iterator)
    {
        return Functions.insertAll(JImmutableTreeSet.<T>of(comparator), iterator);
    }

    public static <T> JImmutableSet<T> sortedSet(Comparator<T> comparator,
                                                 Collection<T> collection)
    {
        return Functions.insertAll(JImmutableTreeSet.<T>of(comparator), collection.iterator());
    }
}