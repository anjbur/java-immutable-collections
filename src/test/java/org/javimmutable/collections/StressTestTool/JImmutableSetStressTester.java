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

package org.javimmutable.collections.StressTestTool;

import org.javimmutable.collections.Insertable;
import org.javimmutable.collections.JImmutableList;
import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.cursors.IterableCursorable;
import org.javimmutable.collections.cursors.StandardCursorTest;
import org.javimmutable.collections.hash.JImmutableHashMultiset;
import org.javimmutable.collections.hash.JImmutableHashSet;
import org.javimmutable.collections.util.JImmutables;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Test program for all implementations of JImmutableSet, including JImmutableMultiset. Divided
 * into four sections: growing (adds new values), shrinking (removes values), contains (tests
 * methods that check for specified values), and cleanup (empties the set of all values).
 * <p/>
 * Final sets in cleanup stage will be smaller than the goal size computed at the beginning of
 * the test (by approx. 1500 elements on average). This is due to how the insert(T) and
 * delete(T) methods are tested.
 */
public class JImmutableSetStressTester
        extends AbstractSetStressTestable
{
    private JImmutableSet<String> set;
    private final Class<? extends Set> expectedClass;

    public JImmutableSetStressTester(JImmutableSet<String> set,
                                     Class<? extends Set> expectedClass)
    {
        this.set = set;
        this.expectedClass = expectedClass;
    }

    @Override
    public JImmutableList<String> getOptions()
    {
        JImmutableList<String> options = JImmutables.list();
        return options.insert("set").insert(makeClassOption(set));
    }

    @Override
    public void execute(Random random,
                        JImmutableList<String> tokens)
            throws IllegalAccessException, InstantiationException
    {
        @SuppressWarnings("unchecked") Set<String> expected = expectedClass.newInstance();
        JImmutableSet<String> set = this.set;
        List<String> setList = new ArrayList<String>();
        final int size = random.nextInt(100000);
        System.out.printf("JImmutableSetStressTest on %s of size %d%n", set.getClass().getSimpleName(), size);

        for (int loops = 1; loops <= 6; ++loops) {
            System.out.printf("growing %d%n", set.size());
            for (int i = 0; i < size / 3; ++i) {
                switch (random.nextInt(5)) {
                case 0: //insert(T)
                    String value = makeInsertValue(tokens, random, setList, expected);
                    insertUniqueToSetList(value, setList, expected);
                    set = set.insert(value);
                    expected.add(value);
                    verifySetList(setList, expected);
                    break;
                case 1: //insertAll(Cursorable)
                    JImmutableList<String> values = makeInsertJList(tokens, random, setList, expected);
                    insertAllUniqueToSetList(values, setList, expected);
                    set = set.insertAll(values);
                    expected.addAll(values.getList());
                    verifySetList(setList, expected);
                    break;
                case 2: //insertAll(Collection)
                    values = makeInsertJList(tokens, random, setList, expected);
                    insertAllUniqueToSetList(values, setList, expected);
                    set = set.insertAll(values.getList());
                    expected.addAll(values.getList());
                    verifySetList(setList, expected);
                    break;
                case 3: //union(Cursorable)
                    values = makeInsertJList(tokens, random, setList, expected);
                    insertAllUniqueToSetList(values, setList, expected);
                    set = set.union(values);
                    expected.addAll(values.getList());
                    verifySetList(setList, expected);
                    break;
                case 4: //union(Collection)
                    values = makeInsertJList(tokens, random, setList, expected);
                    insertAllUniqueToSetList(values, setList, expected);
                    set = set.union(values.getList());
                    expected.addAll(values.getList());
                    verifySetList(setList, expected);
                    break;
                default:
                    throw new RuntimeException();
                }

            }
            verifyContents(set, expected);
            verifySetList(setList, expected);

            System.out.printf("shrinking %d%n", set.size());
            for (int i = 0; i < size / 6; ++i) {
                switch (random.nextInt(3)) {
                case 0: //delete(T)
                    String value = makeDeleteValue(tokens, random, setList, expected);
                    set = set.delete(value);
                    expected.remove(value);
                    verifySetList(setList, expected);
                    break;
                case 1: //deleteAll(Cursorable)
                    JImmutableList<String> values = makeDeleteJList(tokens, random, setList, expected);
                    set = set.deleteAll(values);
                    expected.removeAll(values.getList());
                    verifySetList(setList, expected);
                    break;
                case 2: //deleteAll(Collection)
                    values = makeDeleteJList(tokens, random, setList, expected);
                    set = set.deleteAll(values.getList());
                    expected.removeAll(values.getList());
                    verifySetList(setList, expected);
                    break;
                default:
                    throw new RuntimeException();
                }
            }
            verifyContents(set, expected);
            verifySetList(setList, expected);


            System.out.printf("contains %d%n", set.size());
            for (int i = 0; i < size / 12; ++i) {
                switch (random.nextInt(5)) {
                case 0: //contains(T)
                    String value = (random.nextBoolean()) ? containedValue(setList, random) : notContainedValue(tokens, random, expected);
                    if (set.contains(value) != expected.contains(value)) {
                        throw new RuntimeException(String.format("contains(value) method call failed for %s - expected %b found %b%n", value, expected.contains(value), set.contains(value)));
                    }
                    break;
                case 1: //containsAll(Cursorable)
                    List<String> values = makeContainsList(tokens, random, setList, expected);
                    if (set.containsAll(IterableCursorable.of(values)) != expected.containsAll(values)) {
                        throw new RuntimeException(String.format("containsAll(Cursorable) method call failed for %s - expected %b found %b%n", values, set.containsAll(IterableCursorable.of(values)), expected.containsAll(values)));
                    }
                    break;
                case 2: //containsAll(Collection)
                    values = makeContainsList(tokens, random, setList, expected);
                    if (set.containsAll(values) != expected.containsAll(values)) {
                        throw new RuntimeException(String.format("containsAll(Collection) method call failed for %s - expected %b found %b%n", values, set.containsAll(values), expected.containsAll(values)));
                    }
                    break;
                case 3: //containsAny(Cursorable)
                    values = makeContainsList(tokens, random, setList, expected);
                    if (set.containsAny(IterableCursorable.of(values)) != containsAny(expected, values)) {
                        throw new RuntimeException(String.format("containsAny(Cursorable) method call failed for %s - expected %b found %b%n", values, set.containsAny(IterableCursorable.of(values)), containsAny(expected, values)));
                    }
                    break;
                case 4: //containsAny(Collection)
                    values = makeContainsList(tokens, random, setList, expected);
                    if (set.containsAny(values) != containsAny(expected, values)) {
                        throw new RuntimeException(String.format("containsAny(Collection) method call failed for %s - expected %b found %b%n", values, set.containsAny(values), containsAny(expected, values)));
                    }
                    break;
                default:
                    throw new RuntimeException();
                }
            }
            verifyCursor(set, expected);
        }
        verifyContents(set, expected);
        verifySetList(setList, expected);

        System.out.printf("cleanup %d%n", expected.size());
        while (setList.size() > size / 80) {
            List<String> deleteValues = new ArrayList<String>();
            for (int n = 0, limit = random.nextInt(size / 25); setList.size() >= 1 && n < limit; ++n) {
                int index = random.nextInt(setList.size());
                deleteValues.add(setList.get(index));
                setList.remove(index);
            }
            switch (random.nextInt(4)) {
            case 0: //intersection(Cursorable)
                JImmutableList<String> listIntersectValues = (JImmutableList<String>)makeIntersectValues(tokens, random, setList, expected, JImmutables.list(setList));
                expected.removeAll(deleteValues);
                set = set.intersection(listIntersectValues);
                break;
            case 1: //intersection(Collection)
                listIntersectValues = (JImmutableList<String>)makeIntersectValues(tokens, random, setList, expected, JImmutables.list(setList));
                expected.removeAll(deleteValues);
                set = set.intersection(listIntersectValues.getList());
                break;
            case 2: //intersection(JSet)
                JImmutableSet<String> setIntersectValues = (JImmutableSet<String>)makeIntersectValues(tokens, random, setList, expected, JImmutables.set(setList));
                expected.removeAll(deleteValues);
                set = set.intersection(setIntersectValues);
                break;
            case 3: //intersection(Set)
                setIntersectValues = (JImmutableSet<String>)makeIntersectValues(tokens, random, setList, expected, JImmutables.set(setList));
                expected.removeAll(deleteValues);
                set = set.intersection(setIntersectValues.getSet());
                break;
            default:
                throw new RuntimeException();
            }
            verifySetList(setList, expected);
        }
        if (set.size() != 0) {
            verifyContents(set, expected);
            set = set.deleteAll();
            expected.clear();
        }

        if (set.size() != 0) {
            throw new RuntimeException(String.format("expected map to be empty but it contained %d keys%n", set.size()));
        }
        verifyContents(set, expected);
    }

    private void verifyContents(final JImmutableSet<String> set,
                                final Set<String> expected)
    {
        System.out.printf("checking contents with size %d%n", set.size());
        if (set.isEmpty() != expected.isEmpty()) {
            throw new RuntimeException(String.format("isEmpty mismatch - expected %b found %b%n", expected.isEmpty(), set.isEmpty()));
        }
        if (set.size() != expected.size()) {
            throw new RuntimeException(String.format("size mismatch - expected %d found %d%n", expected.size(), set.size()));
        }
        for (String expectedValue : expected) {
            if (!set.contains(expectedValue)) {
                throw new RuntimeException(String.format("value mismatch - expected %s but not in %s%n", expectedValue, set.getClass().getSimpleName()));
            }
        }
        for (String expectedValue : set) {
            if (!expected.contains(expectedValue)) {
                throw new RuntimeException(String.format("value mismatch - expected %s but not in Set%n", expectedValue));
            }
        }
        if (!expected.equals(set.getSet())) {
            throw new RuntimeException("method call failed - getSet()\n");
        }

        set.checkInvariants();
    }

    private void verifyCursor(final JImmutableSet<String> set,
                              final Set<String> expected)
    {
        System.out.printf("checking cursor with size %d%n", set.size());
        List<String> cursorListTest;
        List<String> iteratorListTest;

        //HashSet iterates in a different order than JImmutableHashSet. Therefore, to test
        // the cursor and iterator for that class, the list of values cannot be built from
        //expected. Other implementations are in the same order as the java.util classes,
        //and can have the test list built from expected.
        if (set instanceof JImmutableHashSet || set instanceof JImmutableHashMultiset) {
            cursorListTest = asList(set.getSet());
            iteratorListTest = asList(set.cursor());
        } else {
            cursorListTest = asList(expected);
            iteratorListTest = cursorListTest;
        }
        StandardCursorTest.listCursorTest(cursorListTest, set.cursor());
        StandardCursorTest.listIteratorTest(iteratorListTest, set.iterator());
    }

    private void insertUniqueToSetList(String value,
                                       List<String> setList,
                                       Set<String> expected)
    {
        if (!expected.contains(value)) {
            setList.add(value);
        }
    }

    private void insertAllUniqueToSetList(Iterable<String> values,
                                          List<String> setList,
                                          Set<String> expected)
    {
        JImmutableSet<String> duplicates = JImmutables.set();
        for (String value : values) {
            if (!expected.contains(value) && !duplicates.contains(value)) {
                setList.add(value);
            }
            duplicates = duplicates.insert(value);
        }
    }

    private void verifySetList(List<String> setList,
                               Set<String> expected)
    {
        if (setList.size() != expected.size()) {
            throw new RuntimeException(String.format("set size mismatch - set: %d, setList: %d", expected.size(), setList.size()));
        }
    }

//    private List<String> makeContainsList(JImmutableList<String> tokens,
//                                          Random random,
//                                          List<String> setList,
//                                          Set<String> expected)
//    {
//        List<String> values = new ArrayList<String>();
//        for (int n = 0, limit = random.nextInt(3); n < limit; ++n) {
//            if (random.nextBoolean()) {
//                values.add(containedValue(setList, random));
//            } else {
//                values.add(notContainedValue(tokens, random, expected));
//            }
//        }
//        return values;
//    }

    private boolean containsAny(Set<String> expected,
                                List<String> values)
    {
        for (String value : values) {
            if (expected.contains(value)) {
                return true;
            }
        }
        return false;
    }

//    private String makeInsertValue(JImmutableList<String> tokens,
//                                   Random random,
//                                   List<String> setList,
//                                   Set<String> expected)
//    {
//        String value;
//        if (random.nextBoolean()) { //make unique token
//            value = notContainedValue(tokens, random, expected);
//        } else { //make duplicate
//            value = (setList.size() > 0) ? containedValue(setList, random) : makeValue(tokens, random);
//        }
//        return value;
//    }

//    private String makeDeleteValue(JImmutableList<String> tokens,
//                                   Random random,
//                                   List<String> setList,
//                                   Set<String> expected)
//    {
//        String value;
//        if (random.nextBoolean()) { //make unique token
//            value = makeValue(tokens, random);
//            while (expected.contains(value)) {
//                value = makeValue(tokens, random);
//            }
//        } else { //make duplicate
//            int index = random.nextInt(setList.size());
//            value = setList.get(index);
//            setList.remove(index);
//        }
//        return value;
//    }

    //on average, adds 1 value to set
    private JImmutableList<String> makeInsertJList(JImmutableList<String> tokens,
                                                   Random random,
                                                   List<String> setList,
                                                   Set<String> expected)
    {
        JImmutableList<String> list = JImmutables.list();
        switch (random.nextInt(8)) {
        case 0: //adds 0 - empty
            break;
        case 1: //adds 0 - value already in set
            list = (setList.size() > 0) ? list.insert(containedValue(setList, random)) : list;
            break;
        case 2: //adds 1 - unique value
            String value = notContainedValue(tokens, random, expected);
            list = list.insert(value);
            break;
        case 3: //adds 1 - unique value, value already in set
            value = notContainedValue(tokens, random, expected);
            list = list.insert(value);

            list = (setList.size() > 0) ? list.insert(containedValue(setList, random)) : list;
            break;
        case 4: //adds 1 - two copies of unique value
            value = notContainedValue(tokens, random, expected);
            list = list.insert(value).insert(value);
            break;
        case 5: //adds 1 - two copies of unique value, value already in set
            value = notContainedValue(tokens, random, expected);
            list = list.insert(value).insert(value);

            list = (setList.size() > 0) ? list.insert(containedValue(setList, random)) : list;
            break;
        case 6: //adds 2 - two unique values
        case 7: //adds 2 - two unique values
            for (int n = 0; n < 2; ++n) {
                value = notContainedValue(tokens, random, expected);
                list = list.insert(value);
            }
            break;
        default:
            throw new RuntimeException();
        }
        return list;
    }

    //on average, deletes 1 value from set
    private JImmutableList<String> makeDeleteJList(JImmutableList<String> tokens,
                                                   Random random,
                                                   List<String> setList,
                                                   Set<String> expected)
    {
        JImmutableList<String> list = JImmutables.list();
        switch (random.nextInt(8)) {
        case 0: //deletes 0 - empty
            break;
        case 1: //deletes 0 - value not in set
            String value = notContainedValue(tokens, random, expected);
            list = list.insert(value);
            break;
        case 2: //deletes 1 - value in set
            int index = random.nextInt(setList.size());
            list = list.insert(setList.get(index));
            setList.remove(index);
            break;
        case 3: //deletes 1 - two copies of value in set
            index = random.nextInt(setList.size());
            list = list.insert(setList.get(index)).insert(setList.get(index));
            setList.remove(index);
            break;
        case 4: //deletes 1 - value in set, value not in set
            index = random.nextInt(setList.size());
            list = list.insert(setList.get(index));
            setList.remove(index);

            value = notContainedValue(tokens, random, expected);
            list = list.insert(value);
            break;
        case 5: //deletes 1 - two copies of value in set, value not in set
            index = random.nextInt(setList.size());
            list = list.insert(setList.get(index)).insert(setList.get(index));
            setList.remove(index);

            value = notContainedValue(tokens, random, expected);
            list = list.insert(value);
            break;
        case 6: //deletes 2 - two different values in set
        case 7: //deletes 2 - two different values in set
            for (int n = 0; n < 2; ++n) {
                index = random.nextInt(setList.size());
                list = list.insert(setList.get(index));
                setList.remove(index);
            }
            break;
        default:
            throw new RuntimeException();
        }
        return list;
    }

    private Insertable<String> makeIntersectValues(JImmutableList<String> tokens,
                                                   Random random,
                                                   List<String> setList,
                                                   Set<String> expected,
                                                   Insertable<String> values)
    {
        int maxSize = setList.size() / 20;
        for (int n = 0, limit = (maxSize > 0) ? random.nextInt(maxSize) : random.nextInt(3); n < limit; ++n) {
            switch (random.nextInt(2)) {
            case 0: //add duplicate
                values = (setList.size() > 0) ? values.insert(containedValue(setList, random)) : values;
                break;
            case 1: //add values not in set
                String value = notContainedValue(tokens, random, expected);
                values = values.insert(value);
            }
        }
        return values;
    }

}