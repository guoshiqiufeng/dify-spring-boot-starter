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
package io.github.guoshiqiufeng.dify.client.core.http;

import io.github.guoshiqiufeng.dify.client.core.map.LinkedMultiValueMap;
import io.github.guoshiqiufeng.dify.client.core.map.MultiValueMap;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for HttpHeaders
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/01/06
 */
class HttpHeadersTest {

    @Test
    void testDefaultConstructor() {
        HttpHeaders headers = new HttpHeaders();
        assertTrue(headers.isEmpty());
        assertEquals(0, headers.size());
    }

    @Test
    void testCopyConstructor() {
        // Arrange
        HttpHeaders original = new HttpHeaders();
        original.add("Content-Type", "application/json");
        original.add("Accept", "text/html");

        // Act
        HttpHeaders copy = new HttpHeaders(original);

        // Assert
        assertEquals(original.size(), copy.size());
        assertEquals("application/json", copy.getFirst("Content-Type"));
        assertEquals("text/html", copy.getFirst("Accept"));

        // Verify deep copy
        original.add("Content-Type", "text/plain");
        assertEquals(2, original.get("Content-Type").size());
        assertEquals(1, copy.get("Content-Type").size());
    }

    @Test
    void testCopyConstructorWithNull() {
        HttpHeaders headers = new HttpHeaders((HttpHeaders) null);
        assertTrue(headers.isEmpty());
    }

    @Test
    void testMapConstructor() {
        // Arrange
        Map<String, List<String>> map = new HashMap<>();
        map.put("Content-Type", Collections.singletonList("application/json"));
        map.put("Accept", Arrays.asList("text/html", "application/xml"));

        // Act
        HttpHeaders headers = new HttpHeaders(map);

        // Assert
        assertEquals(2, headers.size());
        assertEquals("application/json", headers.getFirst("Content-Type"));
        assertEquals(2, headers.get("Accept").size());
    }

    @Test
    void testMapConstructorWithNull() {
        HttpHeaders headers = new HttpHeaders((Map<String, List<String>>) null);
        assertTrue(headers.isEmpty());
    }

    @Test
    void testAdd() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "text/html");

        assertEquals(2, headers.size());
        assertEquals("application/json", headers.getFirst("Content-Type"));
        assertEquals("text/html", headers.getFirst("Accept"));
    }

    @Test
    void testAddMultipleValues() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", "session=abc");
        headers.add("Set-Cookie", "token=xyz");

        List<String> cookies = headers.get("Set-Cookie");
        assertEquals(2, cookies.size());
        assertTrue(cookies.contains("session=abc"));
        assertTrue(cookies.contains("token=xyz"));
    }

    @Test
    void testAddAll() {
        HttpHeaders headers = new HttpHeaders();
        headers.addAll("Accept", Arrays.asList("text/html", "application/xml", "application/json"));

        List<String> accepts = headers.get("Accept");
        assertEquals(3, accepts.size());
        assertTrue(accepts.contains("text/html"));
        assertTrue(accepts.contains("application/xml"));
        assertTrue(accepts.contains("application/json"));
    }

    @Test
    void testAddAllMultiValueMap() {
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("Content-Type", "application/json");
        map.add("Accept", "text/html");
        map.add("Accept", "application/xml");

        headers.addAll(map);

        assertEquals(2, headers.size());
        assertEquals("application/json", headers.getFirst("Content-Type"));
        assertEquals(2, headers.get("Accept").size());
    }

    @Test
    void testSet() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "text/plain");
        headers.add("Content-Type", "application/json");

        // Set should replace all existing values
        headers.set("Content-Type", "application/xml");

        List<String> values = headers.get("Content-Type");
        assertEquals(1, values.size());
        assertEquals("application/xml", values.get(0));
    }

    @Test
    void testSetAll() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "text/plain");
        headers.add("Accept", "text/html");

        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json");
        map.put("Authorization", "Bearer token");

        headers.setAll(map);

        assertEquals("application/json", headers.getFirst("Content-Type"));
        assertEquals("Bearer token", headers.getFirst("Authorization"));
        assertEquals("text/html", headers.getFirst("Accept"));
    }

    @Test
    void testCaseInsensitiveGet() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        assertEquals("application/json", headers.getFirst("content-type"));
        assertEquals("application/json", headers.getFirst("CONTENT-TYPE"));
        assertEquals("application/json", headers.getFirst("Content-Type"));
    }

    @Test
    void testCaseInsensitiveContainsKey() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        assertTrue(headers.containsKey("content-type"));
        assertTrue(headers.containsKey("CONTENT-TYPE"));
        assertTrue(headers.containsKey("Content-Type"));
        assertFalse(headers.containsKey("Accept"));
    }

    @Test
    void testCaseInsensitiveRemove() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        List<String> removed = headers.remove("content-type");
        assertNotNull(removed);
        assertEquals(1, removed.size());
        assertEquals("application/json", removed.get(0));
        assertFalse(headers.containsKey("Content-Type"));
    }

    @Test
    void testGetFirst() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", "session=abc");
        headers.add("Set-Cookie", "token=xyz");

        assertEquals("session=abc", headers.getFirst("Set-Cookie"));
        assertNull(headers.getFirst("Non-Existing"));
    }

    @Test
    void testGetOrEmpty() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        List<String> existing = headers.getOrEmpty("Content-Type");
        assertEquals(1, existing.size());
        assertEquals("application/json", existing.get(0));

        List<String> nonExisting = headers.getOrEmpty("Non-Existing");
        assertNotNull(nonExisting);
        assertTrue(nonExisting.isEmpty());
    }

    @Test
    void testGet() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        List<String> values = headers.get("Content-Type");
        assertNotNull(values);
        assertEquals(1, values.size());
        assertEquals("application/json", values.get(0));

        // Verify returned list is unmodifiable
        assertThrows(UnsupportedOperationException.class, () -> values.add("text/plain"));
    }

    @Test
    void testGetNonExisting() {
        HttpHeaders headers = new HttpHeaders();
        assertNull(headers.get("Non-Existing"));
    }

    @Test
    void testPut() {
        HttpHeaders headers = new HttpHeaders();
        headers.put("Content-Type", Arrays.asList("application/json", "text/plain"));

        List<String> values = headers.get("Content-Type");
        assertEquals(2, values.size());
    }

    @Test
    void testPutReplacesExistingCaseInsensitive() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "text/plain");
        headers.put("content-type", Collections.singletonList("application/json"));

        assertEquals(1, headers.size());
        assertEquals("application/json", headers.getFirst("Content-Type"));
    }

    @Test
    void testPutAll() {
        HttpHeaders headers = new HttpHeaders();
        Map<String, List<String>> map = new HashMap<>();
        map.put("Content-Type", Collections.singletonList("application/json"));
        map.put("Accept", Arrays.asList("text/html", "application/xml"));

        headers.putAll(map);

        assertEquals(2, headers.size());
        assertEquals("application/json", headers.getFirst("Content-Type"));
        assertEquals(2, headers.get("Accept").size());
    }

    @Test
    void testClear() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "text/html");

        assertEquals(2, headers.size());
        headers.clear();
        assertEquals(0, headers.size());
        assertTrue(headers.isEmpty());
    }

    @Test
    void testContainsValue() {
        HttpHeaders headers = new HttpHeaders();
        List<String> values = Arrays.asList("application/json");
        headers.put("Content-Type", values);

        assertTrue(headers.containsValue(values));
        assertFalse(headers.containsValue(Arrays.asList("text/plain")));
    }

    @Test
    void testKeySet() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "text/html");

        Set<String> keys = headers.keySet();
        assertEquals(2, keys.size());
        assertTrue(keys.contains("Content-Type"));
        assertTrue(keys.contains("Accept"));
    }

    @Test
    void testValues() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "text/html");

        Collection<List<String>> values = headers.values();
        assertEquals(2, values.size());
    }

    @Test
    void testEntrySet() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Accept", "text/html");

        Set<Map.Entry<String, List<String>>> entries = headers.entrySet();
        assertEquals(2, entries.size());
    }

    @Test
    void testToSingleValueMap() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Set-Cookie", "session=abc");
        headers.add("Set-Cookie", "token=xyz");

        Map<String, String> singleValueMap = headers.toSingleValueMap();
        assertEquals(2, singleValueMap.size());
        assertEquals("application/json", singleValueMap.get("Content-Type"));
        assertEquals("session=abc", singleValueMap.get("Set-Cookie")); // First value
    }

    @Test
    void testEquals() {
        HttpHeaders headers1 = new HttpHeaders();
        headers1.add("Content-Type", "application/json");
        headers1.add("Accept", "text/html");

        HttpHeaders headers2 = new HttpHeaders();
        headers2.add("Content-Type", "application/json");
        headers2.add("Accept", "text/html");

        HttpHeaders headers3 = new HttpHeaders();
        headers3.add("Content-Type", "text/plain");

        assertEquals(headers1, headers2);
        assertNotEquals(headers1, headers3);
        assertEquals(headers1, headers1);
        assertNotEquals(headers1, null);
        assertNotEquals(headers1, "string");
    }

    @Test
    void testHashCode() {
        HttpHeaders headers1 = new HttpHeaders();
        headers1.add("Content-Type", "application/json");

        HttpHeaders headers2 = new HttpHeaders();
        headers2.add("Content-Type", "application/json");

        assertEquals(headers1.hashCode(), headers2.hashCode());
    }

    @Test
    void testToString() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        String result = headers.toString();
        assertNotNull(result);
        assertTrue(result.contains("Content-Type"));
    }

    @Test
    void testConstants() {
        assertEquals("AUTHORIZATION", HttpHeaders.AUTHORIZATION);
        assertEquals("Bearer ", HttpHeaders.AUTHORIZATION_BEARER_KEY);
    }

    @Test
    void testSerializable() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        // Verify it implements Serializable
        assertTrue(headers instanceof java.io.Serializable);
    }

    @Test
    void testSetBearerAuth() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("test-token-123");

        assertEquals("Bearer test-token-123", headers.getFirst("AUTHORIZATION"));
    }

    @Test
    void testSetBearerAuthReplacesExisting() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("AUTHORIZATION", "Basic abc123");
        headers.setBearerAuth("new-token");

        List<String> authValues = headers.get("AUTHORIZATION");
        assertEquals(1, authValues.size());
        assertEquals("Bearer new-token", authValues.get(0));
    }

    @Test
    void testGetNull() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("AUTHORIZATION-empty", "");
        headers.add("AUTHORIZATION-null", null);
        headers.setBearerAuth("test-token-123");

        headers.containsKey(null);

        headers.getFirst("AUTHORIZATION-empty");
        headers.getFirst("AUTHORIZATION-null");
    }
}
