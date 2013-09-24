package org.javimmutable.collections.util;

import org.javimmutable.collections.PersistentMap;
import org.javimmutable.collections.hash.PersistentHashMap;
import org.javimmutable.collections.tree.PersistentTreeMap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Test program to run an infinite loop feeding data to a PersistentMap, querying the
 * data, and deleting the data to verify the map always contains what it should.
 */
public class RandomMapLoop
{
    private class MapFactory
    {
        private int count;

        private PersistentMap<String, String> createMap()
        {
            count += 1;
            if (count % 2 == 0) {
                return PersistentHashMap.of();
            } else {
                return PersistentTreeMap.of();
            }
        }
    }

    public void execute(String[] filenames)
            throws Exception
    {
        MapFactory factory = new MapFactory();
        Random random = new Random(50);
        List<String> tokens = loadTokens(filenames);
        //noinspection InfiniteLoopStatement
        while (true) {
            testMaps(factory, tokens, random);
        }
    }

    public static void main(String[] argv)
            throws Exception
    {
        new RandomMapLoop().execute(argv);
    }

    private void testMaps(MapFactory factory,
                          List<String> tokens,
                          Random random)
            throws Exception
    {
        final int tokenCount = random.nextInt(100000);
        final List<String> keys = new ArrayList<String>();
        final Map<String, String> expected = new HashMap<String, String>();
        PersistentMap<String, String> map = factory.createMap();
        System.out.printf("starting test with %d tokens and factory %s%n", tokenCount, map.getClass().getSimpleName());
        for (int loops = 1; loops <= 6; ++loops) {
            System.out.printf("growing %d%n", map.size());
            for (int i = 0; i < tokenCount / 3; ++i) {
                String key = makeKey(tokens, random);
                keys.add(key);
                expected.put(key, key);
                map = map.setValue(key, key);
            }
            verifyContents(expected, map);
            System.out.printf("shrinking %d%n", map.size());
            for (int i = 0; i < tokenCount / 6; ++i) {
                int keyIndex = random.nextInt(keys.size());
                String key = keys.get(keyIndex);
                expected.remove(key);
                map = map.removeValue(key);
            }
            verifyContents(expected, map);
        }
        System.out.printf("cleanup %d%n", map.size());
        for (String key : keys) {
            expected.remove(key);
            map = map.removeValue(key);
        }
        verifyContents(expected, map);
    }

    private void verifyContents(Map<String, String> expected,
                                PersistentMap<String, String> map)
    {
        System.out.printf("checking contents with size %d%n", map.size());
        if (map.size() != expected.size()) {
            throw new RuntimeException(String.format("size mismatch - expected %d found %d", expected.size(), map.size()));
        }
        for (PersistentMap.Entry<String, String> entry : map) {
            String mapValue = map.findValue(entry.getKey()).getValueOrNull();
            String expectedValue = expected.get(entry.getKey());
            if (!mapValue.equals(expectedValue)) {
                throw new RuntimeException(String.format("size mismatch - expected %s found %s%n", expectedValue, mapValue));
            }
        }
        for (Map.Entry<String, String> entry : expected.entrySet()) {
            String mapValue = map.findValue(entry.getKey()).getValueOrNull();
            String expectedValue = expected.get(entry.getKey());
            if (!mapValue.equals(expectedValue)) {
                throw new RuntimeException(String.format("size mismatch - expected %s found %s%n", expectedValue, mapValue));
            }
        }
    }

    private String makeKey(List<String> tokens,
                           Random random)
    {
        int length = 1 + random.nextInt(50);
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length) {
            sb.append(tokens.get(random.nextInt(tokens.size())));
        }
        return sb.toString();
    }

    private List<String> loadTokens(String[] filenames)
            throws IOException
    {
        Set<String> tokens = new HashSet<String>();
        for (String filename : filenames) {
            addTokensFromFile(filename, tokens);
        }
        List<String> answer = new ArrayList<String>();
        answer.addAll(tokens);
        return answer;
    }

    private void addTokensFromFile(String filename,
                                   Collection<String> tokens)
            throws IOException
    {
        BufferedReader inp = new BufferedReader(new FileReader(filename));
        try {
            for (String line = inp.readLine(); line != null; line = inp.readLine()) {
                StringTokenizer tokenizer = new StringTokenizer(line);
                while (tokenizer.hasMoreTokens()) {
                    tokens.add(tokenizer.nextToken());
                }
            }
        } finally {
            inp.close();
        }
    }
}
