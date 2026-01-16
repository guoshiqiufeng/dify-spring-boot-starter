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
package io.github.guoshiqiufeng.dify.core.utils;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CollUtil
 *
 * @author yanghq
 * @version 1.0
 * @since 2026/01/13
 */
class CollUtilTest {

    // Collection isEmpty tests

    @Test
    void testIsEmptyCollectionWithNull() {
        assertTrue(CollUtil.isEmpty((Collection<?>) null));
    }

    @Test
    void testIsEmptyCollectionWithEmptyList() {
        assertTrue(CollUtil.isEmpty(new ArrayList<>()));
        assertTrue(CollUtil.isEmpty(Collections.emptyList()));
    }

    @Test
    void testIsEmptyCollectionWithEmptySet() {
        assertTrue(CollUtil.isEmpty(new HashSet<>()));
        assertTrue(CollUtil.isEmpty(Collections.emptySet()));
    }

    @Test
    void testIsEmptyCollectionWithNonEmptyList() {
        List<String> list = Arrays.asList("a", "b", "c");
        assertFalse(CollUtil.isEmpty(list));
    }

    @Test
    void testIsEmptyCollectionWithSingleElement() {
        List<String> list = Collections.singletonList("a");
        assertFalse(CollUtil.isEmpty(list));
    }

    // Collection isNotEmpty tests

    @Test
    void testIsNotEmptyCollectionWithNull() {
        assertFalse(CollUtil.isNotEmpty((Collection<?>) null));
    }

    @Test
    void testIsNotEmptyCollectionWithEmptyList() {
        assertFalse(CollUtil.isNotEmpty(new ArrayList<>()));
        assertFalse(CollUtil.isNotEmpty(Collections.emptyList()));
    }

    @Test
    void testIsNotEmptyCollectionWithNonEmptyList() {
        List<String> list = Arrays.asList("a", "b", "c");
        assertTrue(CollUtil.isNotEmpty(list));
    }

    @Test
    void testIsNotEmptyCollectionWithSingleElement() {
        List<String> list = Collections.singletonList("a");
        assertTrue(CollUtil.isNotEmpty(list));
    }

    // Map isEmpty tests

    @Test
    void testIsEmptyMapWithNull() {
        assertTrue(CollUtil.isEmpty((Map<?, ?>) null));
    }

    @Test
    void testIsEmptyMapWithEmptyMap() {
        assertTrue(CollUtil.isEmpty(new HashMap<>()));
        assertTrue(CollUtil.isEmpty(Collections.emptyMap()));
    }

    @Test
    void testIsEmptyMapWithNonEmptyMap() {
        Map<String, String> map = new HashMap<>();
        map.put("key", "value");
        assertFalse(CollUtil.isEmpty(map));
    }

    @Test
    void testIsEmptyMapWithSingleEntry() {
        Map<String, String> map = Collections.singletonMap("key", "value");
        assertFalse(CollUtil.isEmpty(map));
    }

    // Map isNotEmpty tests

    @Test
    void testIsNotEmptyMapWithNull() {
        assertFalse(CollUtil.isNotEmpty((Map<?, ?>) null));
    }

    @Test
    void testIsNotEmptyMapWithEmptyMap() {
        assertFalse(CollUtil.isNotEmpty(new HashMap<>()));
        assertFalse(CollUtil.isNotEmpty(Collections.emptyMap()));
    }

    @Test
    void testIsNotEmptyMapWithNonEmptyMap() {
        Map<String, String> map = new HashMap<>();
        map.put("key", "value");
        assertTrue(CollUtil.isNotEmpty(map));
    }

    @Test
    void testIsNotEmptyMapWithSingleEntry() {
        Map<String, String> map = Collections.singletonMap("key", "value");
        assertTrue(CollUtil.isNotEmpty(map));
    }

    // Collection size tests

    @Test
    void testSizeCollectionWithNull() {
        assertEquals(0, CollUtil.size((Collection<?>) null));
    }

    @Test
    void testSizeCollectionWithEmptyList() {
        assertEquals(0, CollUtil.size(new ArrayList<>()));
        assertEquals(0, CollUtil.size(Collections.emptyList()));
    }

    @Test
    void testSizeCollectionWithNonEmptyList() {
        List<String> list = Arrays.asList("a", "b", "c");
        assertEquals(3, CollUtil.size(list));
    }

    @Test
    void testSizeCollectionWithSingleElement() {
        List<String> list = Collections.singletonList("a");
        assertEquals(1, CollUtil.size(list));
    }

    @Test
    void testSizeCollectionWithLargeList() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            list.add(i);
        }
        assertEquals(1000, CollUtil.size(list));
    }

    // Map size tests

    @Test
    void testSizeMapWithNull() {
        assertEquals(0, CollUtil.size((Map<?, ?>) null));
    }

    @Test
    void testSizeMapWithEmptyMap() {
        assertEquals(0, CollUtil.size(new HashMap<>()));
        assertEquals(0, CollUtil.size(Collections.emptyMap()));
    }

    @Test
    void testSizeMapWithNonEmptyMap() {
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.put("key3", "value3");
        assertEquals(3, CollUtil.size(map));
    }

    @Test
    void testSizeMapWithSingleEntry() {
        Map<String, String> map = Collections.singletonMap("key", "value");
        assertEquals(1, CollUtil.size(map));
    }

    @Test
    void testSizeMapWithLargeMap() {
        Map<Integer, String> map = new HashMap<>();
        for (int i = 0; i < 1000; i++) {
            map.put(i, "value" + i);
        }
        assertEquals(1000, CollUtil.size(map));
    }

    // Test isEmpty and isNotEmpty are opposites

    @Test
    void testCollectionIsEmptyAndIsNotEmptyAreOpposites() {
        Collection<?>[] collections = {
                null,
                new ArrayList<>(),
                Collections.emptyList(),
                Collections.singletonList("a"),
                Arrays.asList("a", "b", "c")
        };

        for (Collection<?> coll : collections) {
            assertEquals(!CollUtil.isEmpty(coll), CollUtil.isNotEmpty(coll),
                    "isEmpty and isNotEmpty should be opposites for collection");
        }
    }

    @Test
    void testMapIsEmptyAndIsNotEmptyAreOpposites() {
        Map<?, ?>[] maps = {
                null,
                new HashMap<>(),
                Collections.emptyMap(),
                Collections.singletonMap("key", "value"),
                new HashMap<String, String>() {{
                    put("key1", "value1");
                    put("key2", "value2");
                }}
        };

        for (Map<?, ?> map : maps) {
            assertEquals(!CollUtil.isEmpty(map), CollUtil.isNotEmpty(map),
                    "isEmpty and isNotEmpty should be opposites for map");
        }
    }

    // Test different collection types

    @Test
    void testWithDifferentCollectionTypes() {
        // ArrayList
        List<String> arrayList = new ArrayList<>(Arrays.asList("a", "b"));
        assertFalse(CollUtil.isEmpty(arrayList));
        assertEquals(2, CollUtil.size(arrayList));

        // LinkedList
        List<String> linkedList = new LinkedList<>(Arrays.asList("a", "b"));
        assertFalse(CollUtil.isEmpty(linkedList));
        assertEquals(2, CollUtil.size(linkedList));

        // HashSet
        Set<String> hashSet = new HashSet<>(Arrays.asList("a", "b"));
        assertFalse(CollUtil.isEmpty(hashSet));
        assertEquals(2, CollUtil.size(hashSet));

        // TreeSet
        Set<String> treeSet = new TreeSet<>(Arrays.asList("a", "b"));
        assertFalse(CollUtil.isEmpty(treeSet));
        assertEquals(2, CollUtil.size(treeSet));
    }

    @Test
    void testWithDifferentMapTypes() {
        // HashMap
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("key", "value");
        assertFalse(CollUtil.isEmpty(hashMap));
        assertEquals(1, CollUtil.size(hashMap));

        // TreeMap
        Map<String, String> treeMap = new TreeMap<>();
        treeMap.put("key", "value");
        assertFalse(CollUtil.isEmpty(treeMap));
        assertEquals(1, CollUtil.size(treeMap));

        // LinkedHashMap
        Map<String, String> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("key", "value");
        assertFalse(CollUtil.isEmpty(linkedHashMap));
        assertEquals(1, CollUtil.size(linkedHashMap));
    }

    @Test
    void testWithNullElements() {
        // Collection with null elements
        List<String> listWithNulls = new ArrayList<>();
        listWithNulls.add(null);
        listWithNulls.add("a");
        listWithNulls.add(null);

        assertFalse(CollUtil.isEmpty(listWithNulls));
        assertTrue(CollUtil.isNotEmpty(listWithNulls));
        assertEquals(3, CollUtil.size(listWithNulls));

        // Map with null values
        Map<String, String> mapWithNulls = new HashMap<>();
        mapWithNulls.put("key1", null);
        mapWithNulls.put("key2", "value");

        assertFalse(CollUtil.isEmpty(mapWithNulls));
        assertTrue(CollUtil.isNotEmpty(mapWithNulls));
        assertEquals(2, CollUtil.size(mapWithNulls));
    }
}
