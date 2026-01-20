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

import io.github.guoshiqiufeng.dify.client.core.codec.JsonNode;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JsonNodeUtilTest {

    @Test
    void testConvertToMapWithNull() {
        Map<String, Object> result = JsonNodeUtil.convertToMap(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testConvertToMapWithNonObjectNode() {
        JsonNode node = mock(JsonNode.class);
        when(node.isObject()).thenReturn(false);

        Map<String, Object> result = JsonNodeUtil.convertToMap(node);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testConvertToMapWithEmptyObject() {
        JsonNode node = mock(JsonNode.class);
        when(node.isObject()).thenReturn(true);
        when(node.fieldNames()).thenReturn(Collections.emptyIterator());

        Map<String, Object> result = JsonNodeUtil.convertToMap(node);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testConvertToMapWithSimpleFields() {
        JsonNode node = mock(JsonNode.class);
        JsonNode textNode = mock(JsonNode.class);
        JsonNode numberNode = mock(JsonNode.class);

        when(node.isObject()).thenReturn(true);
        Iterator<String> fieldNames = Arrays.asList("text", "number").iterator();
        when(node.fieldNames()).thenReturn(fieldNames);

        when(node.get("text")).thenReturn(textNode);
        when(textNode.isNull()).thenReturn(false);
        when(textNode.isTextual()).thenReturn(true);
        when(textNode.asText()).thenReturn("value");

        when(node.get("number")).thenReturn(numberNode);
        when(numberNode.isNull()).thenReturn(false);
        when(numberNode.isTextual()).thenReturn(false);
        when(numberNode.isNumber()).thenReturn(true);
        when(numberNode.asDouble()).thenReturn(42.0);

        Map<String, Object> result = JsonNodeUtil.convertToMap(node);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("value", result.get("text"));
        assertEquals(42.0, result.get("number"));
    }

    @Test
    void testConvertToMapWithAllTypes() {
        JsonNode node = mock(JsonNode.class);
        JsonNode textNode = mock(JsonNode.class);
        JsonNode numberNode = mock(JsonNode.class);
        JsonNode booleanNode = mock(JsonNode.class);
        JsonNode nullNode = mock(JsonNode.class);
        JsonNode arrayNode = mock(JsonNode.class);
        JsonNode objectNode = mock(JsonNode.class);
        JsonNode arrayElement = mock(JsonNode.class);

        when(node.isObject()).thenReturn(true);
        Iterator<String> fieldNames = Arrays.asList("text", "number", "boolean", "null", "array", "object").iterator();
        when(node.fieldNames()).thenReturn(fieldNames);

        when(node.get("text")).thenReturn(textNode);
        when(textNode.isNull()).thenReturn(false);
        when(textNode.isTextual()).thenReturn(true);
        when(textNode.asText()).thenReturn("textValue");

        when(node.get("number")).thenReturn(numberNode);
        when(numberNode.isNull()).thenReturn(false);
        when(numberNode.isTextual()).thenReturn(false);
        when(numberNode.isNumber()).thenReturn(true);
        when(numberNode.asDouble()).thenReturn(123.45);

        when(node.get("boolean")).thenReturn(booleanNode);
        when(booleanNode.isNull()).thenReturn(false);
        when(booleanNode.isTextual()).thenReturn(false);
        when(booleanNode.isNumber()).thenReturn(false);
        when(booleanNode.isBoolean()).thenReturn(true);
        when(booleanNode.asBoolean()).thenReturn(true);

        when(node.get("null")).thenReturn(nullNode);
        when(nullNode.isNull()).thenReturn(true);

        when(node.get("array")).thenReturn(arrayNode);
        when(arrayNode.isNull()).thenReturn(false);
        when(arrayNode.isTextual()).thenReturn(false);
        when(arrayNode.isNumber()).thenReturn(false);
        when(arrayNode.isBoolean()).thenReturn(false);
        when(arrayNode.isArray()).thenReturn(true);
        when(arrayNode.elements()).thenReturn(Collections.singletonList(arrayElement).iterator());
        when(arrayElement.isNull()).thenReturn(false);
        when(arrayElement.isTextual()).thenReturn(true);
        when(arrayElement.asText()).thenReturn("element");

        when(node.get("object")).thenReturn(objectNode);
        when(objectNode.isNull()).thenReturn(false);
        when(objectNode.isTextual()).thenReturn(false);
        when(objectNode.isNumber()).thenReturn(false);
        when(objectNode.isBoolean()).thenReturn(false);
        when(objectNode.isArray()).thenReturn(false);
        when(objectNode.isObject()).thenReturn(true);
        when(objectNode.fieldNames()).thenReturn(Collections.emptyIterator());

        Map<String, Object> result = JsonNodeUtil.convertToMap(node);

        assertNotNull(result);
        assertEquals(6, result.size());
        assertEquals("textValue", result.get("text"));
        assertEquals(123.45, result.get("number"));
        assertEquals(true, result.get("boolean"));
        assertNull(result.get("null"));
        assertInstanceOf(List.class, result.get("array"));
        assertInstanceOf(Map.class, result.get("object"));
    }

    @Test
    void testConvertToStringListWithNull() {
        List<String> result = JsonNodeUtil.convertToStringList(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testConvertToStringListWithNonArrayNode() {
        JsonNode node = mock(JsonNode.class);
        when(node.isArray()).thenReturn(false);

        List<String> result = JsonNodeUtil.convertToStringList(node);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testConvertToStringListWithEmptyArray() {
        JsonNode node = mock(JsonNode.class);
        when(node.isArray()).thenReturn(true);
        when(node.elements()).thenReturn(Collections.emptyIterator());

        List<String> result = JsonNodeUtil.convertToStringList(node);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testConvertToStringListWithElements() {
        JsonNode node = mock(JsonNode.class);
        JsonNode element1 = mock(JsonNode.class);
        JsonNode element2 = mock(JsonNode.class);
        JsonNode element3 = mock(JsonNode.class);

        when(node.isArray()).thenReturn(true);
        when(node.elements()).thenReturn(Arrays.asList(element1, element2, element3).iterator());
        when(element1.asText()).thenReturn("first");
        when(element2.asText()).thenReturn("second");
        when(element3.asText()).thenReturn("third");

        List<String> result = JsonNodeUtil.convertToStringList(node);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("first", result.get(0));
        assertEquals("second", result.get(1));
        assertEquals("third", result.get(2));
    }

    @Test
    void testConvertToObjectWithNull() {
        Object result = JsonNodeUtil.convertToObject(null);

        assertNull(result);
    }

    @Test
    void testConvertToObjectWithNullNode() {
        JsonNode node = mock(JsonNode.class);
        when(node.isNull()).thenReturn(true);

        Object result = JsonNodeUtil.convertToObject(node);

        assertNull(result);
    }

    @Test
    void testConvertToObjectWithTextual() {
        JsonNode node = mock(JsonNode.class);
        when(node.isNull()).thenReturn(false);
        when(node.isTextual()).thenReturn(true);
        when(node.asText()).thenReturn("text value");

        Object result = JsonNodeUtil.convertToObject(node);

        assertEquals("text value", result);
    }

    @Test
    void testConvertToObjectWithNumber() {
        JsonNode node = mock(JsonNode.class);
        when(node.isNull()).thenReturn(false);
        when(node.isTextual()).thenReturn(false);
        when(node.isNumber()).thenReturn(true);
        when(node.asDouble()).thenReturn(99.99);

        Object result = JsonNodeUtil.convertToObject(node);

        assertEquals(99.99, result);
    }

    @Test
    void testConvertToObjectWithBoolean() {
        JsonNode node = mock(JsonNode.class);
        when(node.isNull()).thenReturn(false);
        when(node.isTextual()).thenReturn(false);
        when(node.isNumber()).thenReturn(false);
        when(node.isBoolean()).thenReturn(true);
        when(node.asBoolean()).thenReturn(false);

        Object result = JsonNodeUtil.convertToObject(node);

        assertEquals(false, result);
    }

    @Test
    void testConvertToObjectWithArray() {
        JsonNode node = mock(JsonNode.class);
        JsonNode element1 = mock(JsonNode.class);
        JsonNode element2 = mock(JsonNode.class);

        when(node.isNull()).thenReturn(false);
        when(node.isTextual()).thenReturn(false);
        when(node.isNumber()).thenReturn(false);
        when(node.isBoolean()).thenReturn(false);
        when(node.isArray()).thenReturn(true);
        when(node.elements()).thenReturn(Arrays.asList(element1, element2).iterator());

        when(element1.isNull()).thenReturn(false);
        when(element1.isTextual()).thenReturn(true);
        when(element1.asText()).thenReturn("item1");

        when(element2.isNull()).thenReturn(false);
        when(element2.isTextual()).thenReturn(true);
        when(element2.asText()).thenReturn("item2");

        Object result = JsonNodeUtil.convertToObject(node);

        assertInstanceOf(List.class, result);
        @SuppressWarnings("unchecked")
        List<Object> list = (List<Object>) result;
        assertEquals(2, list.size());
        assertEquals("item1", list.get(0));
        assertEquals("item2", list.get(1));
    }

    @Test
    void testConvertToObjectWithObject() {
        JsonNode node = mock(JsonNode.class);
        JsonNode fieldNode = mock(JsonNode.class);

        when(node.isNull()).thenReturn(false);
        when(node.isTextual()).thenReturn(false);
        when(node.isNumber()).thenReturn(false);
        when(node.isBoolean()).thenReturn(false);
        when(node.isArray()).thenReturn(false);
        when(node.isObject()).thenReturn(true);
        when(node.fieldNames()).thenReturn(Collections.singletonList("field").iterator());
        when(node.get("field")).thenReturn(fieldNode);

        when(fieldNode.isNull()).thenReturn(false);
        when(fieldNode.isTextual()).thenReturn(true);
        when(fieldNode.asText()).thenReturn("fieldValue");

        Object result = JsonNodeUtil.convertToObject(node);

        assertInstanceOf(Map.class, result);
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) result;
        assertEquals(1, map.size());
        assertEquals("fieldValue", map.get("field"));
    }

    @Test
    void testConvertToObjectWithDefaultCase() {
        JsonNode node = mock(JsonNode.class);

        when(node.isNull()).thenReturn(false);
        when(node.isTextual()).thenReturn(false);
        when(node.isNumber()).thenReturn(false);
        when(node.isBoolean()).thenReturn(false);
        when(node.isArray()).thenReturn(false);
        when(node.isObject()).thenReturn(false);
        when(node.asText()).thenReturn("fallback");

        Object result = JsonNodeUtil.convertToObject(node);

        assertEquals("fallback", result);
    }

    @Test
    void testConvertToObjectWithNestedStructures() {
        JsonNode node = mock(JsonNode.class);
        JsonNode arrayNode = mock(JsonNode.class);
        JsonNode arrayElement = mock(JsonNode.class);
        JsonNode nestedObject = mock(JsonNode.class);
        JsonNode nestedField = mock(JsonNode.class);

        when(node.isNull()).thenReturn(false);
        when(node.isTextual()).thenReturn(false);
        when(node.isNumber()).thenReturn(false);
        when(node.isBoolean()).thenReturn(false);
        when(node.isArray()).thenReturn(false);
        when(node.isObject()).thenReturn(true);
        when(node.fieldNames()).thenReturn(Collections.singletonList("array").iterator());
        when(node.get("array")).thenReturn(arrayNode);

        when(arrayNode.isNull()).thenReturn(false);
        when(arrayNode.isTextual()).thenReturn(false);
        when(arrayNode.isNumber()).thenReturn(false);
        when(arrayNode.isBoolean()).thenReturn(false);
        when(arrayNode.isArray()).thenReturn(true);
        when(arrayNode.elements()).thenReturn(Collections.singletonList(arrayElement).iterator());

        when(arrayElement.isNull()).thenReturn(false);
        when(arrayElement.isTextual()).thenReturn(false);
        when(arrayElement.isNumber()).thenReturn(false);
        when(arrayElement.isBoolean()).thenReturn(false);
        when(arrayElement.isArray()).thenReturn(false);
        when(arrayElement.isObject()).thenReturn(true);
        when(arrayElement.fieldNames()).thenReturn(Collections.singletonList("nested").iterator());
        when(arrayElement.get("nested")).thenReturn(nestedField);

        when(nestedField.isNull()).thenReturn(false);
        when(nestedField.isTextual()).thenReturn(true);
        when(nestedField.asText()).thenReturn("nestedValue");

        Object result = JsonNodeUtil.convertToObject(node);

        assertInstanceOf(Map.class, result);
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) result;
        assertInstanceOf(List.class, map.get("array"));
        @SuppressWarnings("unchecked")
        List<Object> list = (List<Object>) map.get("array");
        assertInstanceOf(Map.class, list.get(0));
        @SuppressWarnings("unchecked")
        Map<String, Object> nestedMap = (Map<String, Object>) list.get(0);
        assertEquals("nestedValue", nestedMap.get("nested"));
    }

    @Test
    void testConvertToMapWithNullFieldValue() {
        JsonNode node = mock(JsonNode.class);
        when(node.isObject()).thenReturn(true);
        when(node.fieldNames()).thenReturn(Collections.singletonList("nullField").iterator());
        when(node.get("nullField")).thenReturn(null);

        Map<String, Object> result = JsonNodeUtil.convertToMap(node);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.get("nullField"));
    }

    @Test
    void testConvertToObjectWithArrayContainingNull() {
        JsonNode node = mock(JsonNode.class);
        JsonNode nullElement = mock(JsonNode.class);

        when(node.isNull()).thenReturn(false);
        when(node.isTextual()).thenReturn(false);
        when(node.isNumber()).thenReturn(false);
        when(node.isBoolean()).thenReturn(false);
        when(node.isArray()).thenReturn(true);
        when(node.elements()).thenReturn(Collections.singletonList(nullElement).iterator());

        when(nullElement.isNull()).thenReturn(true);

        Object result = JsonNodeUtil.convertToObject(node);

        assertInstanceOf(List.class, result);
        @SuppressWarnings("unchecked")
        List<Object> list = (List<Object>) result;
        assertEquals(1, list.size());
        assertNull(list.get(0));
    }
}
