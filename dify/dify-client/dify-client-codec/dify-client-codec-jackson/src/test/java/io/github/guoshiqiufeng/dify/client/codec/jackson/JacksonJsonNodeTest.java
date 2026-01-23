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
package io.github.guoshiqiufeng.dify.client.codec.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JacksonJsonNode
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/01/06
 */
class JacksonJsonNodeTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testIsNull() {
        JacksonJsonNode nullNode = new JacksonJsonNode(NullNode.getInstance());
        assertTrue(nullNode.isNull());

        JacksonJsonNode actualNullNode = new JacksonJsonNode(null);
        assertTrue(actualNullNode.isNull());

        JacksonJsonNode stringNode = new JacksonJsonNode(new TextNode("test"));
        assertFalse(stringNode.isNull());
    }

    @Test
    void testIsTextual() {
        JacksonJsonNode stringNode = new JacksonJsonNode(new TextNode("test"));
        assertTrue(stringNode.isTextual());

        JacksonJsonNode numberNode = new JacksonJsonNode(new IntNode(123));
        assertFalse(numberNode.isTextual());

        JacksonJsonNode booleanNode = new JacksonJsonNode(BooleanNode.TRUE);
        assertFalse(booleanNode.isTextual());
    }

    @Test
    void testIsTextualWithNullNode() {
        JacksonJsonNode nullNode = new JacksonJsonNode(null);
        assertFalse(nullNode.isTextual());
    }

    @Test
    void testIsNumber() {
        JacksonJsonNode intNode = new JacksonJsonNode(new IntNode(123));
        assertTrue(intNode.isNumber());

        JacksonJsonNode doubleNode = new JacksonJsonNode(new DoubleNode(123.45));
        assertTrue(doubleNode.isNumber());

        JacksonJsonNode stringNode = new JacksonJsonNode(new TextNode("test"));
        assertFalse(stringNode.isNumber());
    }

    @Test
    void testIsNumberWithNullNode() {
        JacksonJsonNode nullNode = new JacksonJsonNode(null);
        assertFalse(nullNode.isNumber());
    }

    @Test
    void testIsBoolean() {
        JacksonJsonNode trueNode = new JacksonJsonNode(BooleanNode.TRUE);
        assertTrue(trueNode.isBoolean());

        JacksonJsonNode falseNode = new JacksonJsonNode(BooleanNode.FALSE);
        assertTrue(falseNode.isBoolean());

        JacksonJsonNode stringNode = new JacksonJsonNode(new TextNode("test"));
        assertFalse(stringNode.isBoolean());
    }

    @Test
    void testIsBooleanWithNullNode() {
        JacksonJsonNode nullNode = new JacksonJsonNode(null);
        assertFalse(nullNode.isBoolean());
    }

    @Test
    void testIsArray() {
        ArrayNode array = objectMapper.createArrayNode();
        array.add("test");
        JacksonJsonNode arrayNode = new JacksonJsonNode(array);
        assertTrue(arrayNode.isArray());

        JacksonJsonNode stringNode = new JacksonJsonNode(new TextNode("test"));
        assertFalse(stringNode.isArray());
    }

    @Test
    void testIsArrayWithNullNode() {
        JacksonJsonNode nullNode = new JacksonJsonNode(null);
        assertFalse(nullNode.isArray());
    }

    @Test
    void testIsObject() {
        ObjectNode object = objectMapper.createObjectNode();
        object.put("key", "value");
        JacksonJsonNode objectNode = new JacksonJsonNode(object);
        assertTrue(objectNode.isObject());

        JacksonJsonNode stringNode = new JacksonJsonNode(new TextNode("test"));
        assertFalse(stringNode.isObject());
    }

    @Test
    void testIsObjectWithNullNode() {
        JacksonJsonNode nullNode = new JacksonJsonNode(null);
        assertFalse(nullNode.isObject());
    }

    @Test
    void testGet() {
        ObjectNode object = objectMapper.createObjectNode();
        object.put("name", "John");
        object.put("age", 30);
        JacksonJsonNode objectNode = new JacksonJsonNode(object);

        io.github.guoshiqiufeng.dify.client.core.codec.JsonNode nameNode = objectNode.get("name");
        assertNotNull(nameNode);
        assertEquals("John", nameNode.asText());

        io.github.guoshiqiufeng.dify.client.core.codec.JsonNode ageNode = objectNode.get("age");
        assertNotNull(ageNode);
        assertEquals(30, ageNode.asInt());

        io.github.guoshiqiufeng.dify.client.core.codec.JsonNode nonExistentNode = objectNode.get("nonexistent");
        assertNull(nonExistentNode);
    }

    @Test
    void testGetOnNull() {
        JacksonJsonNode nullNode = new JacksonJsonNode(null);
        io.github.guoshiqiufeng.dify.client.core.codec.JsonNode result = nullNode.get("field");
        assertNull(result);
    }

    @Test
    void testHas() {
        ObjectNode object = objectMapper.createObjectNode();
        object.put("name", "John");
        JacksonJsonNode objectNode = new JacksonJsonNode(object);

        assertTrue(objectNode.has("name"));
        assertFalse(objectNode.has("age"));
    }

    @Test
    void testHasOnNull() {
        JacksonJsonNode nullNode = new JacksonJsonNode(null);
        assertFalse(nullNode.has("field"));
    }

    @Test
    void testAsText() {
        JacksonJsonNode stringNode = new JacksonJsonNode(new TextNode("test"));
        assertEquals("test", stringNode.asText());

        JacksonJsonNode nullNode = new JacksonJsonNode(null);
        assertNull(nullNode.asText());

        JacksonJsonNode numberNode = new JacksonJsonNode(new IntNode(123));
        assertEquals("123", numberNode.asText());
    }

    @Test
    void testAsInt() {
        JacksonJsonNode intNode = new JacksonJsonNode(new IntNode(123));
        assertEquals(123, intNode.asInt());

        JacksonJsonNode nullNode = new JacksonJsonNode(null);
        assertEquals(0, nullNode.asInt());
    }

    @Test
    void testAsLong() {
        JacksonJsonNode longNode = new JacksonJsonNode(new LongNode(123456789L));
        assertEquals(123456789L, longNode.asLong());

        JacksonJsonNode nullNode = new JacksonJsonNode(null);
        assertEquals(0L, nullNode.asLong());
    }

    @Test
    void testAsDouble() {
        JacksonJsonNode doubleNode = new JacksonJsonNode(new DoubleNode(123.45));
        assertEquals(123.45, doubleNode.asDouble(), 0.001);

        JacksonJsonNode nullNode = new JacksonJsonNode(null);
        assertEquals(0.0, nullNode.asDouble(), 0.001);
    }

    @Test
    void testAsBoolean() {
        JacksonJsonNode trueNode = new JacksonJsonNode(BooleanNode.TRUE);
        assertTrue(trueNode.asBoolean());

        JacksonJsonNode falseNode = new JacksonJsonNode(BooleanNode.FALSE);
        assertFalse(falseNode.asBoolean());

        JacksonJsonNode nullNode = new JacksonJsonNode(null);
        assertFalse(nullNode.asBoolean());
    }

    @Test
    void testElements() {
        ArrayNode array = objectMapper.createArrayNode();
        array.add("a");
        array.add("b");
        array.add("c");
        JacksonJsonNode arrayNode = new JacksonJsonNode(array);

        Iterator<io.github.guoshiqiufeng.dify.client.core.codec.JsonNode> elements = arrayNode.elements();
        assertTrue(elements.hasNext());
        assertEquals("a", elements.next().asText());
        assertEquals("b", elements.next().asText());
        assertEquals("c", elements.next().asText());
        assertFalse(elements.hasNext());
    }

    @Test
    void testElementsOnNonArray() {
        JacksonJsonNode stringNode = new JacksonJsonNode(new TextNode("test"));
        Iterator<io.github.guoshiqiufeng.dify.client.core.codec.JsonNode> elements = stringNode.elements();
        assertFalse(elements.hasNext());
    }

    @Test
    void testElementsOnNull() {
        JacksonJsonNode nullNode = new JacksonJsonNode(null);
        Iterator<io.github.guoshiqiufeng.dify.client.core.codec.JsonNode> elements = nullNode.elements();
        assertFalse(elements.hasNext());
    }

    @Test
    void testFieldNames() {
        ObjectNode object = objectMapper.createObjectNode();
        object.put("name", "John");
        object.put("age", 30);
        object.put("city", "NYC");
        JacksonJsonNode objectNode = new JacksonJsonNode(object);

        Iterator<String> fieldNames = objectNode.fieldNames();
        assertTrue(fieldNames.hasNext());

        int count = 0;
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            assertTrue(fieldName.equals("name") || fieldName.equals("age") || fieldName.equals("city"));
            count++;
        }
        assertEquals(3, count);
    }

    @Test
    void testFieldNamesOnNonObject() {
        JacksonJsonNode stringNode = new JacksonJsonNode(new TextNode("test"));
        Iterator<String> fieldNames = stringNode.fieldNames();
        assertFalse(fieldNames.hasNext());
    }

    @Test
    void testFieldNamesOnNull() {
        JacksonJsonNode nullNode = new JacksonJsonNode(null);
        Iterator<String> fieldNames = nullNode.fieldNames();
        assertFalse(fieldNames.hasNext());
    }

    @Test
    void testUnwrap() {
        TextNode textNode = new TextNode("test");
        JacksonJsonNode node = new JacksonJsonNode(textNode);
        Object unwrapped = node.unwrap();
        assertSame(textNode, unwrapped);
    }

    @Test
    void testComplexObject() {
        ObjectNode person = objectMapper.createObjectNode();
        person.put("name", "John");
        person.put("age", 30);

        ArrayNode hobbies = objectMapper.createArrayNode();
        hobbies.add("reading");
        hobbies.add("coding");
        person.set("hobbies", hobbies);

        JacksonJsonNode personNode = new JacksonJsonNode(person);

        assertTrue(personNode.isObject());
        assertTrue(personNode.has("name"));
        assertTrue(personNode.has("age"));
        assertTrue(personNode.has("hobbies"));

        assertEquals("John", personNode.get("name").asText());
        assertEquals(30, personNode.get("age").asInt());

        io.github.guoshiqiufeng.dify.client.core.codec.JsonNode hobbiesNode = personNode.get("hobbies");
        assertTrue(hobbiesNode.isArray());

        Iterator<io.github.guoshiqiufeng.dify.client.core.codec.JsonNode> hobbiesIterator = hobbiesNode.elements();
        assertEquals("reading", hobbiesIterator.next().asText());
        assertEquals("coding", hobbiesIterator.next().asText());
    }

    @Test
    void testNestedObject() {
        ObjectNode address = objectMapper.createObjectNode();
        address.put("city", "NYC");
        address.put("zip", "10001");

        ObjectNode person = objectMapper.createObjectNode();
        person.put("name", "John");
        person.set("address", address);

        JacksonJsonNode personNode = new JacksonJsonNode(person);

        io.github.guoshiqiufeng.dify.client.core.codec.JsonNode addressNode = personNode.get("address");
        assertNotNull(addressNode);
        assertTrue(addressNode.isObject());
        assertEquals("NYC", addressNode.get("city").asText());
        assertEquals("10001", addressNode.get("zip").asText());
    }

    @Test
    void testEmptyArray() {
        ArrayNode array = objectMapper.createArrayNode();
        JacksonJsonNode arrayNode = new JacksonJsonNode(array);

        assertTrue(arrayNode.isArray());
        Iterator<io.github.guoshiqiufeng.dify.client.core.codec.JsonNode> elements = arrayNode.elements();
        assertFalse(elements.hasNext());
    }

    @Test
    void testEmptyObject() {
        ObjectNode object = objectMapper.createObjectNode();
        JacksonJsonNode objectNode = new JacksonJsonNode(object);

        assertTrue(objectNode.isObject());
        Iterator<String> fieldNames = objectNode.fieldNames();
        assertFalse(fieldNames.hasNext());
    }
}
