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
package io.github.guoshiqiufeng.dify.client.core.codec.util;

import io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper;
import io.github.guoshiqiufeng.dify.client.core.codec.exception.JsonException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for JsonSerializationHelper
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026-01-20
 */
class JsonSerializationHelperTest {

    @Test
    void testSerializeWithNullBody() {
        JsonMapper mapper = mock(JsonMapper.class);

        String result = JsonSerializationHelper.serialize(null, mapper, false);

        assertNull(result);
        verifyNoInteractions(mapper);
    }

    @Test
    void testSerializeWithNullBodyAndSkipNull() {
        JsonMapper mapper = mock(JsonMapper.class);

        String result = JsonSerializationHelper.serialize(null, mapper, true);

        assertNull(result);
        verifyNoInteractions(mapper);
    }

    @Test
    void testSerializeWithSkipNullFalse() throws JsonException {
        JsonMapper mapper = mock(JsonMapper.class);
        TestObject body = new TestObject("test", null);
        when(mapper.toJson(body)).thenReturn("{\"name\":\"test\",\"value\":null}");

        String result = JsonSerializationHelper.serialize(body, mapper, false);

        assertEquals("{\"name\":\"test\",\"value\":null}", result);
        verify(mapper).toJson(body);
        verify(mapper, never()).toJsonIgnoreNull(any());
    }

    @Test
    void testSerializeWithSkipNullTrue() throws JsonException {
        JsonMapper mapper = mock(JsonMapper.class);
        TestObject body = new TestObject("test", null);
        when(mapper.toJsonIgnoreNull(body)).thenReturn("{\"name\":\"test\"}");

        String result = JsonSerializationHelper.serialize(body, mapper, true);

        assertEquals("{\"name\":\"test\"}", result);
        verify(mapper).toJsonIgnoreNull(body);
        verify(mapper, never()).toJson(any());
    }

    @Test
    void testSerializeWithComplexObject() throws JsonException {
        JsonMapper mapper = mock(JsonMapper.class);
        ComplexObject body = new ComplexObject("name", 42, true);
        when(mapper.toJson(body)).thenReturn("{\"name\":\"name\",\"value\":42,\"active\":true}");

        String result = JsonSerializationHelper.serialize(body, mapper, false);

        assertEquals("{\"name\":\"name\",\"value\":42,\"active\":true}", result);
        verify(mapper).toJson(body);
    }

    @Test
    void testSerializeWithComplexObjectAndSkipNull() throws JsonException {
        JsonMapper mapper = mock(JsonMapper.class);
        ComplexObject body = new ComplexObject("name", 42, true);
        when(mapper.toJsonIgnoreNull(body)).thenReturn("{\"name\":\"name\",\"value\":42,\"active\":true}");

        String result = JsonSerializationHelper.serialize(body, mapper, true);

        assertEquals("{\"name\":\"name\",\"value\":42,\"active\":true}", result);
        verify(mapper).toJsonIgnoreNull(body);
    }

    @Test
    void testSerializeMultipartFieldSuccess() throws JsonException {
        JsonMapper mapper = mock(JsonMapper.class);
        TestObject value = new TestObject("test", "value");
        when(mapper.toJson(value)).thenReturn("{\"name\":\"test\",\"value\":\"value\"}");

        String result = JsonSerializationHelper.serializeMultipartField("testField", value, mapper, false);

        assertEquals("{\"name\":\"test\",\"value\":\"value\"}", result);
        verify(mapper).toJson(value);
    }

    @Test
    void testSerializeMultipartFieldSuccessWithSkipNull() throws JsonException {
        JsonMapper mapper = mock(JsonMapper.class);
        TestObject value = new TestObject("test", null);
        when(mapper.toJsonIgnoreNull(value)).thenReturn("{\"name\":\"test\"}");

        String result = JsonSerializationHelper.serializeMultipartField("testField", value, mapper, true);

        assertEquals("{\"name\":\"test\"}", result);
        verify(mapper).toJsonIgnoreNull(value);
    }

    @Test
    void testSerializeMultipartFieldWithNullValue() {
        JsonMapper mapper = mock(JsonMapper.class);

        String result = JsonSerializationHelper.serializeMultipartField("testField", null, mapper, false);

        assertNull(result);
        verifyNoInteractions(mapper);
    }

    @Test
    void testSerializeMultipartFieldThrowsException() throws JsonException {
        JsonMapper mapper = mock(JsonMapper.class);
        TestObject value = new TestObject("test", "value");
        when(mapper.toJson(value)).thenThrow(new JsonException("Serialization failed"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            JsonSerializationHelper.serializeMultipartField("testField", value, mapper, false);
        });

        assertEquals("Failed to serialize multipart field to JSON: testField", exception.getMessage());
        assertInstanceOf(JsonException.class, exception.getCause());
        assertEquals("Serialization failed", exception.getCause().getMessage());
    }

    @Test
    void testSerializeMultipartFieldThrowsExceptionWithSkipNull() throws JsonException {
        JsonMapper mapper = mock(JsonMapper.class);
        TestObject value = new TestObject("test", null);
        when(mapper.toJsonIgnoreNull(value)).thenThrow(new JsonException("Serialization failed"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            JsonSerializationHelper.serializeMultipartField("testField", value, mapper, true);
        });

        assertEquals("Failed to serialize multipart field to JSON: testField", exception.getMessage());
        assertInstanceOf(JsonException.class, exception.getCause());
    }

    @Test
    void testSerializeMultipartFieldWithDifferentFieldNames() throws JsonException {
        JsonMapper mapper = mock(JsonMapper.class);
        TestObject value = new TestObject("test", "value");
        when(mapper.toJson(value)).thenReturn("{\"name\":\"test\",\"value\":\"value\"}");

        String result1 = JsonSerializationHelper.serializeMultipartField("field1", value, mapper, false);
        String result2 = JsonSerializationHelper.serializeMultipartField("field2", value, mapper, false);

        assertEquals("{\"name\":\"test\",\"value\":\"value\"}", result1);
        assertEquals("{\"name\":\"test\",\"value\":\"value\"}", result2);
        verify(mapper, times(2)).toJson(value);
    }

    @Test
    void testSerializeMultipartFieldExceptionContainsFieldName() throws JsonException {
        JsonMapper mapper = mock(JsonMapper.class);
        TestObject value = new TestObject("test", "value");
        when(mapper.toJson(value)).thenThrow(new JsonException("Error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            JsonSerializationHelper.serializeMultipartField("myCustomField", value, mapper, false);
        });

        assertTrue(exception.getMessage().contains("myCustomField"));
    }

    @Test
    void testSerializeWithEmptyString() throws JsonException {
        JsonMapper mapper = mock(JsonMapper.class);
        String body = "";
        when(mapper.toJson(body)).thenReturn("\"\"");

        String result = JsonSerializationHelper.serialize(body, mapper, false);

        assertEquals("\"\"", result);
        verify(mapper).toJson(body);
    }

    @Test
    void testSerializeWithPrimitiveTypes() throws JsonException {
        JsonMapper mapper = mock(JsonMapper.class);
        Integer body = 42;
        when(mapper.toJson(body)).thenReturn("42");

        String result = JsonSerializationHelper.serialize(body, mapper, false);

        assertEquals("42", result);
        verify(mapper).toJson(body);
    }

    @Test
    void testSerializeMultipartFieldWithComplexNestedObject() throws JsonException {
        JsonMapper mapper = mock(JsonMapper.class);
        NestedObject value = new NestedObject(new TestObject("inner", "value"), 100);
        when(mapper.toJsonIgnoreNull(value)).thenReturn("{\"inner\":{\"name\":\"inner\",\"value\":\"value\"},\"count\":100}");

        String result = JsonSerializationHelper.serializeMultipartField("nestedField", value, mapper, true);

        assertEquals("{\"inner\":{\"name\":\"inner\",\"value\":\"value\"},\"count\":100}", result);
        verify(mapper).toJsonIgnoreNull(value);
    }

    // Test helper classes

    static class TestObject {
        private String name;
        private String value;

        public TestObject(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }

    static class ComplexObject {
        private String name;
        private int value;
        private boolean active;

        public ComplexObject(String name, int value, boolean active) {
            this.name = name;
            this.value = value;
            this.active = active;
        }

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }

        public boolean isActive() {
            return active;
        }
    }

    static class NestedObject {
        private TestObject inner;
        private int count;

        public NestedObject(TestObject inner, int count) {
            this.inner = inner;
            this.count = count;
        }

        public TestObject getInner() {
            return inner;
        }

        public int getCount() {
            return count;
        }
    }
}
