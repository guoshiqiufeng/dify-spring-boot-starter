/*
 * Copyright (c) 2025-2026, fubluesky (fubluesky@foxmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.guoshiqiufeng.dify.client.core.util;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LinkedMultiValueMap
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/01/06
 */
class LinkedMultiValueMapTest {

    @Test
    void testDefaultConstructor() {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        assertTrue(map.isEmpty());
        assertEquals(0, map.size());
    }

    @Test
    void testConstructorWithExpectedSize() {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>(10);
        assertTrue(map.isEmpty());
        assertEquals(0, map.size());
    }

    @Test
    void testCopyConstructor() {
        Map<String, List<String>> original = new HashMap<>();
        original.put("key1", Arrays.asList("value1", "value2"));
        original.put("key2", Collections.singletonList("value3"));

        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>(original);
        assertEquals(2, map.size());
        assertEquals(2, map.get("key1").size());
        assertEquals(1, map.get("key2").size());
    }

    @Test
    void testAdd() {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("key1", "value1");
        map.add("key1", "value2");
        map.add("key2", "value3");

        assertEquals(2, map.size());
        List<String> values1 = map.get("key1");
        assertEquals(2, values1.size());
        assertTrue(values1.contains("value1"));
        assertTrue(values1.contains("value2"));

        List<String> values2 = map.get("key2");
        assertEquals(1, values2.size());
        assertEquals("value3", values2.get(0));
    }

    @Test
    void testAddAll() {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("key1", "value1");
        map.addAll("key1", Arrays.asList("value2", "value3"));

        List<String> values = map.get("key1");
        assertEquals(3, values.size());
        assertEquals("value1", values.get(0));
        assertEquals("value2", values.get(1));
        assertEquals("value3", values.get(2));
    }

    @Test
    void testAddAllMultiValueMap() {
        LinkedMultiValueMap<String, String> map1 = new LinkedMultiValueMap<>();
        map1.add("key1", "value1");
        map1.add("key2", "value2");

        LinkedMultiValueMap<String, String> map2 = new LinkedMultiValueMap<>();
        map2.add("key1", "value3");
        map2.add("key3", "value4");

        map1.addAll(map2);

        assertEquals(3, map1.size());
        assertEquals(2, map1.get("key1").size());
        assertEquals(1, map1.get("key2").size());
        assertEquals(1, map1.get("key3").size());
    }

    @Test
    void testSet() {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("key1", "value1");
        map.add("key1", "value2");

        // Set should replace all existing values
        map.set("key1", "value3");

        List<String> values = map.get("key1");
        assertEquals(1, values.size());
        assertEquals("value3", values.get(0));
    }

    @Test
    void testSetAll() {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("key1", "value1");
        map.add("key2", "value2");

        Map<String, String> singleValueMap = new HashMap<>();
        singleValueMap.put("key1", "newValue1");
        singleValueMap.put("key3", "value3");

        map.setAll(singleValueMap);

        assertEquals(3, map.size());
        assertEquals("newValue1", map.getFirst("key1"));
        assertEquals("value2", map.getFirst("key2"));
        assertEquals("value3", map.getFirst("key3"));
    }

    @Test
    void testGetFirst() {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("key1", "value1");
        map.add("key1", "value2");
        map.add("key1", "value3");

        assertEquals("value1", map.getFirst("key1"));
        assertNull(map.getFirst("nonExisting"));
    }

    @Test
    void testGetFirstEmptyList() {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.put("key1", new ArrayList<>());

        assertNull(map.getFirst("key1"));
    }

    @Test
    void testToSingleValueMap() {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("key1", "value1");
        map.add("key1", "value2");
        map.add("key2", "value3");

        Map<String, String> singleValueMap = map.toSingleValueMap();
        assertEquals(2, singleValueMap.size());
        assertEquals("value1", singleValueMap.get("key1")); // First value
        assertEquals("value3", singleValueMap.get("key2"));
    }

    @Test
    void testToSingleValueMapWithEmptyList() {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.put("key1", new ArrayList<>());
        map.add("key2", "value2");

        Map<String, String> singleValueMap = map.toSingleValueMap();
        assertEquals(1, singleValueMap.size());
        assertFalse(singleValueMap.containsKey("key1"));
        assertEquals("value2", singleValueMap.get("key2"));
    }

    @Test
    void testPut() {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        List<String> values = Arrays.asList("value1", "value2");
        map.put("key1", values);

        assertEquals(1, map.size());
        assertEquals(2, map.get("key1").size());
    }

    @Test
    void testPutAll() {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        Map<String, List<String>> otherMap = new HashMap<>();
        otherMap.put("key1", Arrays.asList("value1", "value2"));
        otherMap.put("key2", Collections.singletonList("value3"));

        map.putAll(otherMap);

        assertEquals(2, map.size());
        assertEquals(2, map.get("key1").size());
        assertEquals(1, map.get("key2").size());
    }

    @Test
    void testRemove() {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("key1", "value1");
        map.add("key2", "value2");

        List<String> removed = map.remove("key1");
        assertNotNull(removed);
        assertEquals(1, removed.size());
        assertEquals("value1", removed.get(0));
        assertEquals(1, map.size());
        assertFalse(map.containsKey("key1"));
    }

    @Test
    void testClear() {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("key1", "value1");
        map.add("key2", "value2");

        assertEquals(2, map.size());
        map.clear();
        assertEquals(0, map.size());
        assertTrue(map.isEmpty());
    }

    @Test
    void testContainsKey() {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("key1", "value1");

        assertTrue(map.containsKey("key1"));
        assertFalse(map.containsKey("key2"));
    }

    @Test
    void testContainsValue() {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        List<String> values = Arrays.asList("value1", "value2");
        map.put("key1", values);

        assertTrue(map.containsValue(values));
        assertFalse(map.containsValue(Collections.singletonList("value3")));
    }

    @Test
    void testKeySet() {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("key1", "value1");
        map.add("key2", "value2");
        map.add("key3", "value3");

        Set<String> keys = map.keySet();
        assertEquals(3, keys.size());
        assertTrue(keys.contains("key1"));
        assertTrue(keys.contains("key2"));
        assertTrue(keys.contains("key3"));
    }

    @Test
    void testValues() {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("key1", "value1");
        map.add("key2", "value2");

        Collection<List<String>> values = map.values();
        assertEquals(2, values.size());
    }

    @Test
    void testEntrySet() {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("key1", "value1");
        map.add("key2", "value2");

        Set<Map.Entry<String, List<String>>> entries = map.entrySet();
        assertEquals(2, entries.size());
    }

    @Test
    void testEquals() {
        LinkedMultiValueMap<String, String> map1 = new LinkedMultiValueMap<>();
        map1.add("key1", "value1");
        map1.add("key2", "value2");

        LinkedMultiValueMap<String, String> map2 = new LinkedMultiValueMap<>();
        map2.add("key1", "value1");
        map2.add("key2", "value2");

        LinkedMultiValueMap<String, String> map3 = new LinkedMultiValueMap<>();
        map3.add("key1", "value1");

        assertEquals(map1, map2);
        assertNotEquals(map1, map3);
        assertEquals(map1, map1);
    }

    @Test
    void testHashCode() {
        LinkedMultiValueMap<String, String> map1 = new LinkedMultiValueMap<>();
        map1.add("key1", "value1");

        LinkedMultiValueMap<String, String> map2 = new LinkedMultiValueMap<>();
        map2.add("key1", "value1");

        assertEquals(map1.hashCode(), map2.hashCode());
    }

    @Test
    void testToString() {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("key1", "value1");

        String result = map.toString();
        assertNotNull(result);
        assertTrue(result.contains("key1"));
    }

    @Test
    void testInsertionOrder() {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("key3", "value3");
        map.add("key1", "value1");
        map.add("key2", "value2");

        // LinkedHashMap should maintain insertion order
        List<String> keys = new ArrayList<>(map.keySet());
        assertEquals("key3", keys.get(0));
        assertEquals("key1", keys.get(1));
        assertEquals("key2", keys.get(2));
    }

    @Test
    void testGenericTypes() {
        // Test with Integer keys and String values
        LinkedMultiValueMap<Integer, String> intMap = new LinkedMultiValueMap<>();
        intMap.add(1, "value1");
        intMap.add(1, "value2");
        assertEquals(2, intMap.get(1).size());

        // Test with String keys and Integer values
        LinkedMultiValueMap<String, Integer> stringMap = new LinkedMultiValueMap<>();
        stringMap.add("key1", 1);
        stringMap.add("key1", 2);
        assertEquals(2, stringMap.get("key1").size());
    }

    @Test
    void testToSingleValueMapWithNullList() {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.put("key1", null);
        map.add("key2", "value2");

        Map<String, String> singleValueMap = map.toSingleValueMap();
        assertEquals(1, singleValueMap.size());
        assertFalse(singleValueMap.containsKey("key1"));
        assertEquals("value2", singleValueMap.get("key2"));
    }
}
