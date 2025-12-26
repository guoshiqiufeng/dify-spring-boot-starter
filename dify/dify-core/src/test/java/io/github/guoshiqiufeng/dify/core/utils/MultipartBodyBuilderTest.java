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

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for MultipartBodyBuilder
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025/12/31
 */
class MultipartBodyBuilderTest {

    @Test
    void testAddStringPart() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("field", "value");

        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        assertNotNull(parts);
        assertEquals(1, parts.size());
        assertTrue(parts.containsKey("field"));

        MultipartBodyBuilder.Part part = parts.get("field");
        assertEquals("field", part.getName());
        assertEquals("value", part.getValue());
    }

    @Test
    void testAddByteArrayPart() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        byte[] data = "test data".getBytes();
        builder.part("file", data);

        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        assertNotNull(parts);
        assertEquals(1, parts.size());

        MultipartBodyBuilder.Part part = parts.get("file");
        assertEquals("file", part.getName());
        assertArrayEquals(data, (byte[]) part.getValue());
    }

    @Test
    void testAddPartWithHeaders() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", "content")
                .header("Content-Type", "text/plain")
                .header("Content-Disposition", "form-data; name=\"file\"");

        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        MultipartBodyBuilder.Part part = parts.get("file");
        assertNotNull(part);

        assertEquals("text/plain", part.getHeader("Content-Type"));
        assertEquals("form-data; name=\"file\"", part.getHeader("Content-Disposition"));
    }

    @Test
    void testAddMultipleHeaderValues() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("field", "value")
                .headers("X-Custom", "value1", "value2", "value3");

        MultipartBodyBuilder.Part part = builder.build().get("field");
        List<String> headerValues = part.getHeaderValues("X-Custom");

        assertEquals(3, headerValues.size());
        assertTrue(headerValues.contains("value1"));
        assertTrue(headerValues.contains("value2"));
        assertTrue(headerValues.contains("value3"));
    }

    @Test
    void testMultipleParts() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("field1", "value1")
                .header("Content-Type", "text/plain");
        builder.part("field2", "value2")
                .header("Content-Type", "application/json");
        builder.part("file", new byte[]{1, 2, 3})
                .header("Content-Type", "application/octet-stream");

        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        assertEquals(3, parts.size());
        assertTrue(parts.containsKey("field1"));
        assertTrue(parts.containsKey("field2"));
        assertTrue(parts.containsKey("file"));
    }

    @Test
    void testHeaderRetrieval() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("field", "value")
                .header("Header1", "value1")
                .header("Header2", "value2");

        MultipartBodyBuilder.Part part = builder.build().get("field");

        assertEquals("value1", part.getHeader("Header1"));
        assertEquals("value2", part.getHeader("Header2"));
        assertNull(part.getHeader("NonExistent"));
    }

    @Test
    void testGetHeaders() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("field", "value")
                .header("Content-Type", "text/plain")
                .header("Custom-Header", "custom-value");

        MultipartBodyBuilder.Part part = builder.build().get("field");
        Map<String, List<String>> headers = part.getHeaders();

        assertNotNull(headers);
        assertEquals(2, headers.size());
        assertTrue(headers.containsKey("Content-Type"));
        assertTrue(headers.containsKey("Custom-Header"));
    }

    @Test
    void testGetParts() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("field1", "value1");
        builder.part("field2", "value2");

        Map<String, MultipartBodyBuilder.Part> parts = builder.getParts();

        assertEquals(2, parts.size());
        assertThrows(UnsupportedOperationException.class, () ->
            parts.put("field3", null)
        );
    }

    @Test
    void testPartBuilderChaining() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        MultipartBodyBuilder.Part part = builder.part("field", "value")
                .header("Header1", "value1")
                .header("Header2", "value2")
                .header("Header3", "value3")
                .build();

        assertEquals("field", part.getName());
        assertEquals("value", part.getValue());
        assertEquals("value1", part.getHeader("Header1"));
        assertEquals("value2", part.getHeader("Header2"));
        assertEquals("value3", part.getHeader("Header3"));
    }

    @Test
    void testEmptyBuilder() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        assertNotNull(parts);
        assertTrue(parts.isEmpty());
    }

    @Test
    void testGetHeaderValuesForNonExistentHeader() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("field", "value");

        MultipartBodyBuilder.Part part = builder.build().get("field");
        List<String> values = part.getHeaderValues("NonExistent");

        assertNotNull(values);
        assertTrue(values.isEmpty());
    }

    @Test
    void testComplexObject() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        TestObject testObject = new TestObject("test", 123);
        builder.part("data", testObject)
                .header("Content-Type", "application/json");

        MultipartBodyBuilder.Part part = builder.build().get("data");

        assertNotNull(part);
        assertEquals(testObject, part.getValue());
        assertEquals("application/json", part.getHeader("Content-Type"));
    }

    @Test
    void testOverwritePart() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("field", "value1");
        builder.part("field", "value2");

        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        assertEquals(1, parts.size());
        assertEquals("value2", parts.get("field").getValue());
    }

    private static class TestObject {
        private final String name;
        private final int value;

        public TestObject(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }
    }
}
