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
package io.github.guoshiqiufeng.dify.client.codec.jackson3;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.*;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Jackson3JsonNode
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/01/06
 */
class Jackson3JsonNodeTest {

    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    @Test
    void testIsNull() {
        Jackson3JsonNode nullNode = new Jackson3JsonNode(NullNode.getInstance());
        assertTrue(nullNode.isNull());

        Jackson3JsonNode actualNullNode = new Jackson3JsonNode(null);
        assertTrue(actualNullNode.isNull());

        Jackson3JsonNode stringNode = new Jackson3JsonNode(jsonMapper.getNodeFactory().stringNode("test"));
        assertFalse(stringNode.isNull());
    }

    @Test
    void testIsTextual() {
        Jackson3JsonNode stringNode = new Jackson3JsonNode(jsonMapper.getNodeFactory().stringNode("test"));
        assertTrue(stringNode.isTextual());

        Jackson3JsonNode numberNode = new Jackson3JsonNode(IntNode.valueOf(123));
        assertFalse(numberNode.isTextual());

        Jackson3JsonNode booleanNode = new Jackson3JsonNode(BooleanNode.TRUE);
        assertFalse(booleanNode.isTextual());
    }

    @Test
    void testIsTextualWithNullNode() {
        Jackson3JsonNode nullNode = new Jackson3JsonNode(null);
        assertFalse(nullNode.isTextual());
    }

    @Test
    void testIsNumber() {
        Jackson3JsonNode intNode = new Jackson3JsonNode(IntNode.valueOf(123));
        assertTrue(intNode.isNumber());

        Jackson3JsonNode doubleNode = new Jackson3JsonNode(DoubleNode.valueOf(123.45));
        assertTrue(doubleNode.isNumber());

        Jackson3JsonNode stringNode = new Jackson3JsonNode(jsonMapper.getNodeFactory().stringNode("test"));
        assertFalse(stringNode.isNumber());
    }

    @Test
    void testIsNumberWithNullNode() {
        Jackson3JsonNode nullNode = new Jackson3JsonNode(null);
        assertFalse(nullNode.isNumber());
    }

    @Test
    void testIsBoolean() {
        Jackson3JsonNode trueNode = new Jackson3JsonNode(BooleanNode.TRUE);
        assertTrue(trueNode.isBoolean());

        Jackson3JsonNode falseNode = new Jackson3JsonNode(BooleanNode.FALSE);
        assertTrue(falseNode.isBoolean());

        Jackson3JsonNode stringNode = new Jackson3JsonNode(jsonMapper.getNodeFactory().stringNode("test"));
        assertFalse(stringNode.isBoolean());
    }

    @Test
    void testIsBooleanWithNullNode() {
        Jackson3JsonNode nullNode = new Jackson3JsonNode(null);
        assertFalse(nullNode.isBoolean());
    }

    @Test
    void testIsArray() {
        ArrayNode array = jsonMapper.createArrayNode();
        array.add("test");
        Jackson3JsonNode arrayNode = new Jackson3JsonNode(array);
        assertTrue(arrayNode.isArray());

        Jackson3JsonNode stringNode = new Jackson3JsonNode(jsonMapper.getNodeFactory().stringNode("test"));
        assertFalse(stringNode.isArray());
    }

    @Test
    void testIsArrayWithNullNode() {
        Jackson3JsonNode nullNode = new Jackson3JsonNode(null);
        assertFalse(nullNode.isArray());
    }

    @Test
    void testIsObject() {
        ObjectNode object = jsonMapper.createObjectNode();
        object.put("key", "value");
        Jackson3JsonNode objectNode = new Jackson3JsonNode(object);
        assertTrue(objectNode.isObject());

        Jackson3JsonNode stringNode = new Jackson3JsonNode(jsonMapper.getNodeFactory().stringNode("test"));
        assertFalse(stringNode.isObject());
    }

    @Test
    void testIsObjectWithNullNode() {
        Jackson3JsonNode nullNode = new Jackson3JsonNode(null);
        assertFalse(nullNode.isObject());
    }

    @Test
    void testGet() {
        ObjectNode object = jsonMapper.createObjectNode();
        object.put("name", "John");
        object.put("age", 30);
        Jackson3JsonNode objectNode = new Jackson3JsonNode(object);

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
        Jackson3JsonNode nullNode = new Jackson3JsonNode(null);
        io.github.guoshiqiufeng.dify.client.core.codec.JsonNode result = nullNode.get("field");
        assertNull(result);
    }

    @Test
    void testHas() {
        ObjectNode object = jsonMapper.createObjectNode();
        object.put("name", "John");
        Jackson3JsonNode objectNode = new Jackson3JsonNode(object);

        assertTrue(objectNode.has("name"));
        assertFalse(objectNode.has("age"));
    }

    @Test
    void testHasOnNull() {
        Jackson3JsonNode nullNode = new Jackson3JsonNode(null);
        assertFalse(nullNode.has("field"));
    }

    @Test
    void testAsText() {
        Jackson3JsonNode stringNode = new Jackson3JsonNode(jsonMapper.getNodeFactory().stringNode("test"));
        assertEquals("test", stringNode.asText());

        Jackson3JsonNode nullNode = new Jackson3JsonNode(null);
        assertNull(nullNode.asText());

        Jackson3JsonNode numberNode = new Jackson3JsonNode(IntNode.valueOf(123));
        assertNotNull(numberNode.asText());
    }

    @Test
    void testAsInt() {
        Jackson3JsonNode intNode = new Jackson3JsonNode(IntNode.valueOf(123));
        assertEquals(123, intNode.asInt());

        Jackson3JsonNode nullNode = new Jackson3JsonNode(null);
        assertEquals(0, nullNode.asInt());
    }

    @Test
    void testAsLong() {
        Jackson3JsonNode longNode = new Jackson3JsonNode(LongNode.valueOf(123456789L));
        assertEquals(123456789L, longNode.asLong());

        Jackson3JsonNode nullNode = new Jackson3JsonNode(null);
        assertEquals(0L, nullNode.asLong());
    }

    @Test
    void testAsDouble() {
        Jackson3JsonNode doubleNode = new Jackson3JsonNode(DoubleNode.valueOf(123.45));
        assertEquals(123.45, doubleNode.asDouble(), 0.001);

        Jackson3JsonNode nullNode = new Jackson3JsonNode(null);
        assertEquals(0.0, nullNode.asDouble(), 0.001);
    }

    @Test
    void testAsBoolean() {
        Jackson3JsonNode trueNode = new Jackson3JsonNode(BooleanNode.TRUE);
        assertTrue(trueNode.asBoolean());

        Jackson3JsonNode falseNode = new Jackson3JsonNode(BooleanNode.FALSE);
        assertFalse(falseNode.asBoolean());

        Jackson3JsonNode nullNode = new Jackson3JsonNode(null);
        assertFalse(nullNode.asBoolean());
    }

    @Test
    void testElements() {
        ArrayNode array = jsonMapper.createArrayNode();
        array.add("a");
        array.add("b");
        array.add("c");
        Jackson3JsonNode arrayNode = new Jackson3JsonNode(array);

        Iterator<io.github.guoshiqiufeng.dify.client.core.codec.JsonNode> elements = arrayNode.elements();
        assertTrue(elements.hasNext());
        assertEquals("a", elements.next().asText());
        assertEquals("b", elements.next().asText());
        assertEquals("c", elements.next().asText());
        assertFalse(elements.hasNext());
    }

    @Test
    void testElementsOnNonArray() {
        Jackson3JsonNode stringNode = new Jackson3JsonNode(jsonMapper.getNodeFactory().stringNode("test"));
        Iterator<io.github.guoshiqiufeng.dify.client.core.codec.JsonNode> elements = stringNode.elements();
        assertFalse(elements.hasNext());
    }

    @Test
    void testElementsOnNull() {
        Jackson3JsonNode nullNode = new Jackson3JsonNode(null);
        Iterator<io.github.guoshiqiufeng.dify.client.core.codec.JsonNode> elements = nullNode.elements();
        assertFalse(elements.hasNext());
    }

    @Test
    void testFieldNames() {
        ObjectNode object = jsonMapper.createObjectNode();
        object.put("name", "John");
        object.put("age", 30);
        object.put("city", "NYC");
        Jackson3JsonNode objectNode = new Jackson3JsonNode(object);

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
        Jackson3JsonNode stringNode = new Jackson3JsonNode(jsonMapper.getNodeFactory().stringNode("test"));
        Iterator<String> fieldNames = stringNode.fieldNames();
        assertFalse(fieldNames.hasNext());
    }

    @Test
    void testFieldNamesOnNull() {
        Jackson3JsonNode nullNode = new Jackson3JsonNode(null);
        Iterator<String> fieldNames = nullNode.fieldNames();
        assertFalse(fieldNames.hasNext());
    }

    @Test
    void testUnwrap() {
        JsonNode stringNode = jsonMapper.getNodeFactory().stringNode("test");
        Jackson3JsonNode node = new Jackson3JsonNode(stringNode);
        Object unwrapped = node.unwrap();
        assertSame(stringNode, unwrapped);
    }

    @Test
    void testComplexObject() {
        ObjectNode person = jsonMapper.createObjectNode();
        person.put("name", "John");
        person.put("age", 30);

        ArrayNode hobbies = jsonMapper.createArrayNode();
        hobbies.add("reading");
        hobbies.add("coding");
        person.set("hobbies", hobbies);

        Jackson3JsonNode personNode = new Jackson3JsonNode(person);

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
        ObjectNode address = jsonMapper.createObjectNode();
        address.put("city", "NYC");
        address.put("zip", "10001");

        ObjectNode person = jsonMapper.createObjectNode();
        person.put("name", "John");
        person.set("address", address);

        Jackson3JsonNode personNode = new Jackson3JsonNode(person);

        io.github.guoshiqiufeng.dify.client.core.codec.JsonNode addressNode = personNode.get("address");
        assertNotNull(addressNode);
        assertTrue(addressNode.isObject());
        assertEquals("NYC", addressNode.get("city").asText());
        assertEquals("10001", addressNode.get("zip").asText());
    }

    @Test
    void testEmptyArray() {
        ArrayNode array = jsonMapper.createArrayNode();
        Jackson3JsonNode arrayNode = new Jackson3JsonNode(array);

        assertTrue(arrayNode.isArray());
        Iterator<io.github.guoshiqiufeng.dify.client.core.codec.JsonNode> elements = arrayNode.elements();
        assertFalse(elements.hasNext());
    }

    @Test
    void testEmptyObject() {
        ObjectNode object = jsonMapper.createObjectNode();
        Jackson3JsonNode objectNode = new Jackson3JsonNode(object);

        assertTrue(objectNode.isObject());
        Iterator<String> fieldNames = objectNode.fieldNames();
        assertFalse(fieldNames.hasNext());
    }
}
