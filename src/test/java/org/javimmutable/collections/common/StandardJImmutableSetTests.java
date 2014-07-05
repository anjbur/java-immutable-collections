package org.javimmutable.collections.common;

import org.javimmutable.collections.JImmutableSet;
import org.javimmutable.collections.cursors.IterableCursor;
import org.javimmutable.collections.cursors.IterableCursorable;
import org.javimmutable.collections.hash.JImmutableHashSet;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;

public final class StandardJImmutableSetTests
{
    private StandardJImmutableSetTests()
    {
    }

    public static void verifySet(JImmutableSet<Integer> template)
    {
        testVarious(template);
        testRandom(template);

        assertEquals(0, template.size());
        assertEquals(true, template.isEmpty());
        assertEquals(template, new HashSet<Integer>());
        assertEquals(template.getSet(), new HashSet<Integer>());

        JImmutableSet<Integer> jet = template;
        assertEquals(false, jet.contains(10));
        jet = jet.insert(10);
        assertEquals(true, jet != template);
        assertEquals(1, jet.size());
        assertEquals(false, jet.isEmpty());
        assertEquals(true, jet.contains(10));

        jet = jet.delete(10);
        assertEquals(0, jet.size());
        assertEquals(true, template.isEmpty());
        assertEquals(false, jet.contains(10));

        final List<Integer> values = Arrays.asList(1, 2, 3, 4);
        verifyContents(jet.union(IterableCursorable.of(values)), values);
        verifyContents(jet.union(values), values);
        verifyContents(jet.union(IterableCursor.of(values)), values);
        verifyContents(jet.union(values.iterator()), values);

        // intersect with larger set
        jet = template.union(values);
        final List<Integer> withExtra = Arrays.asList(0, 1, 2, 3, 4, 5);
        Set<Integer> intersectionSet = new HashSet<Integer>(withExtra);
        JImmutableSet<Integer> intersectionJet = template.union(withExtra);
        verifyContents(jet.intersection(IterableCursorable.of(withExtra)), values);
        verifyContents(jet.intersection(withExtra), values);
        verifyContents(jet.intersection(IterableCursor.of(withExtra)), values);
        verifyContents(jet.intersection(withExtra.iterator()), values);
        verifyContents(jet.intersection(intersectionJet), values);
        verifyContents(jet.intersection(intersectionSet), values);

        // intersect with smaller set
        jet = template.union(withExtra);
        intersectionSet = new HashSet<Integer>(values);
        intersectionJet = template.union(values);
        verifyContents(jet.intersection(IterableCursorable.of(values)), values);
        verifyContents(jet.intersection(values), values);
        verifyContents(jet.intersection(IterableCursor.of(values)), values);
        verifyContents(jet.intersection(values.iterator()), values);
        verifyContents(jet.intersection(intersectionJet), values);
        verifyContents(jet.intersection(intersectionSet), values);

        // empty set intersection with non-empty set
        final List<Integer> empty = Collections.emptyList();
        verifyContents(template.intersection(IterableCursorable.of(withExtra)), empty);
        verifyContents(template.intersection(withExtra), empty);
        verifyContents(template.intersection(IterableCursor.of(withExtra)), empty);
        verifyContents(template.intersection(withExtra.iterator()), empty);
        verifyContents(template.intersection(intersectionJet), empty);
        verifyContents(template.intersection(intersectionSet), empty);

        // non-empty set intersection with empty set
        intersectionSet = new HashSet<Integer>();
        intersectionJet = template;
        verifyContents(jet.intersection(IterableCursorable.of(empty)), empty);
        verifyContents(jet.intersection(empty), empty);
        verifyContents(jet.intersection(IterableCursor.of(empty)), empty);
        verifyContents(jet.intersection(empty.iterator()), empty);
        verifyContents(jet.intersection(intersectionJet), empty);
        verifyContents(jet.intersection(intersectionSet), empty);

        // deleteAll from smaller set
        final List<Integer> extra = Arrays.asList(0, 5);
        jet = template.union(withExtra);
        verifyContents(jet.deleteAll(IterableCursorable.of(values)), extra);
        verifyContents(jet.deleteAll(values), extra);
        verifyContents(jet.deleteAll(IterableCursor.of(values)), extra);
        verifyContents(jet.deleteAll(values.iterator()), extra);

        // deleteAll from larger set
        jet = template.union(values);
        verifyContents(jet.deleteAll(IterableCursorable.of(withExtra)), empty);
        verifyContents(jet.deleteAll(withExtra), empty);
        verifyContents(jet.deleteAll(IterableCursor.of(withExtra)), empty);
        verifyContents(jet.deleteAll(withExtra.iterator()), empty);
    }

    private static void testVarious(JImmutableSet<Integer> template)
    {
        List<Integer> expected = Arrays.asList(100, 200, 300, 400);

        JImmutableSet<Integer> set = JImmutableHashSet.of();
        assertTrue(set.isEmpty());
        assertEquals(0, set.size());
        assertEquals(false, set.contains(100));
        assertEquals(false, set.contains(200));
        assertEquals(false, set.contains(300));
        assertEquals(false, set.contains(400));
        assertEquals(false, set.containsAny(expected));
        assertEquals(false, set.containsAll(expected));

        set = set.insert(100);
        assertFalse(set.isEmpty());
        assertEquals(1, set.size());
        assertEquals(true, set.contains(100));
        assertEquals(false, set.contains(200));
        assertEquals(false, set.contains(300));
        assertEquals(false, set.contains(400));
        assertEquals(true, set.containsAny(expected));
        assertEquals(false, set.containsAll(expected));

        set = set.insert(200);
        assertFalse(set.isEmpty());
        assertEquals(2, set.size());
        assertEquals(true, set.contains(100));
        assertEquals(true, set.contains(200));
        assertEquals(false, set.contains(300));
        assertEquals(false, set.contains(400));
        assertEquals(true, set.containsAny(expected));
        assertEquals(false, set.containsAll(expected));

        assertSame(set, set.insert(100));
        assertSame(set, set.insert(200));

        JImmutableSet<Integer> set2 = set.union(expected);
        assertFalse(set2.isEmpty());
        assertEquals(4, set2.size());
        assertEquals(true, set2.contains(100));
        assertEquals(true, set2.contains(200));
        assertEquals(true, set2.contains(300));
        assertEquals(true, set2.contains(400));
        assertEquals(true, set2.containsAny(expected));
        assertEquals(true, set2.containsAll(expected));
        assertEquals(new HashSet<Integer>(Arrays.asList(100, 200, 300, 400)), set2.getSet());

        assertEquals(set, set.intersection(set2));
        assertEquals(set, set2.intersection(set));
        assertEquals(set, set2.delete(300).delete(400));

        set2 = set2.deleteAll(set);
        assertFalse(set2.isEmpty());
        assertEquals(2, set2.size());
        assertEquals(false, set2.contains(100));
        assertEquals(false, set2.contains(200));
        assertEquals(true, set2.contains(300));
        assertEquals(true, set2.contains(400));
        assertEquals(true, set2.containsAny(expected));
        assertEquals(false, set2.containsAny(set));
        assertEquals(false, set2.containsAll(expected));

        JImmutableSet<Integer> set3 = set.union(expected).insert(500).insert(600);
        assertFalse(set3.isEmpty());
        assertEquals(6, set3.size());
        assertEquals(true, set3.contains(100));
        assertEquals(true, set3.contains(200));
        assertEquals(true, set3.contains(300));
        assertEquals(true, set3.contains(400));
        assertEquals(true, set3.contains(500));
        assertEquals(true, set3.contains(600));
        assertEquals(true, set3.containsAny(expected));
        assertEquals(true, set3.containsAny(set));
        assertEquals(true, set3.containsAny(set2));
        assertEquals(true, set3.containsAll(expected));
        assertEquals(true, set3.containsAll(set));
        assertEquals(true, set3.containsAll(set2));
        assertEquals(new HashSet<Integer>(Arrays.asList(100, 200, 300, 400, 500, 600)), set3.getSet());
        assertEquals(set, set3.intersection(set));
        assertEquals(set2, set3.intersection(set2));
        assertEquals(set, set.intersection(set));
        assertEquals(set, set.intersection(set3));
        assertEquals(template, set.intersection(set2));
        assertEquals(template, set2.intersection(set));
        assertEquals(template, set3.deleteAll(set3));
    }

    private static void testRandom(JImmutableSet<Integer> template)
    {
        Random random = new Random(2500L);
        for (int i = 0; i < 50; ++i) {
            int size = 1 + random.nextInt(20000);
            Set<Integer> expected = new HashSet<Integer>();
            JImmutableSet<Integer> set = template;
            for (int loops = 0; loops < (4 * size); ++loops) {
                int command = random.nextInt(4);
                int value = random.nextInt(size);
                switch (command) {
                case 0:
                case 1:
                    set = set.insert(value);
                    expected.add(value);
                    assertEquals(true, set.contains(value));
                    break;
                case 2:
                    assertEquals(expected.contains(value), set.contains(value));
                    break;
                case 3:
                    set = set.delete(value);
                    expected.remove(value);
                    assertEquals(false, set.contains(value));
                    break;
                }
                assertEquals(expected.size(), set.size());
            }
            assertEquals(expected, set.getSet());
            for (Integer value : set) {
                assertSame(set, set.insert(value));
            }
            for (Integer value : set) {
                set = set.delete(value);
            }
            assertEquals(0, set.size());
            assertEquals(true, set.isEmpty());
        }
    }

    private static void verifyContents(JImmutableSet<Integer> jet,
                                       List<Integer> expected)
    {
        assertEquals(expected.isEmpty(), jet.isEmpty());
        assertEquals(expected.size(), jet.size());
        for (Integer value : expected) {
            assertEquals(true, jet.contains(value));
        }
        assertEquals(true, jet.containsAll(IterableCursorable.of(expected)));
        assertEquals(true, jet.containsAll(expected));
        assertEquals(true, jet.containsAll(IterableCursor.of(expected)));
        assertEquals(true, jet.containsAll(expected.iterator()));

        assertEquals(!expected.isEmpty(), jet.containsAny(IterableCursorable.of(expected)));
        assertEquals(!expected.isEmpty(), jet.containsAny(expected));
        assertEquals(!expected.isEmpty(), jet.containsAny(IterableCursor.of(expected)));
        assertEquals(!expected.isEmpty(), jet.containsAny(expected.iterator()));

        if (!expected.isEmpty()) {
            List<Integer> subset = Arrays.asList(expected.get(0));
            assertEquals(true, jet.containsAll(IterableCursorable.of(subset)));
            assertEquals(true, jet.containsAll(subset));
            assertEquals(true, jet.containsAll(IterableCursor.of(subset)));
            assertEquals(true, jet.containsAll(subset.iterator()));

            assertEquals(true, jet.containsAny(IterableCursorable.of(subset)));
            assertEquals(true, jet.containsAny(subset));
            assertEquals(true, jet.containsAny(IterableCursor.of(subset)));
            assertEquals(true, jet.containsAny(subset.iterator()));
        }
    }
}