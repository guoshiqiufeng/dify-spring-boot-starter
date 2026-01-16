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
package io.github.guoshiqiufeng.dify.client.codec.gson;

import com.google.gson.*;
import io.github.guoshiqiufeng.dify.client.core.codec.JsonNode;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GsonJsonNode
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/01/06
 */
class GsonJsonNodeTest {

    @Test
    void testIsNull() {
        GsonJsonNode nullNode = new GsonJsonNode(JsonNull.INSTANCE);
        assertTrue(nullNode.isNull());

        GsonJsonNode actualNullNode = new GsonJsonNode(null);
        assertTrue(actualNullNode.isNull());

        GsonJsonNode stringNode = new GsonJsonNode(new JsonPrimitive("test"));
        assertFalse(stringNode.isNull());
    }

    @Test
    void testIsTextual() {
        GsonJsonNode stringNode = new GsonJsonNode(new JsonPrimitive("test"));
        assertTrue(stringNode.isTextual());

        GsonJsonNode numberNode = new GsonJsonNode(new JsonPrimitive(123));
        assertFalse(numberNode.isTextual());

        GsonJsonNode booleanNode = new GsonJsonNode(new JsonPrimitive(true));
        assertFalse(booleanNode.isTextual());

        GsonJsonNode objectNode = new GsonJsonNode(new JsonObject());
        assertFalse(objectNode.isTextual());

        GsonJsonNode nullNode = new GsonJsonNode(null);
        assertFalse(nullNode.isTextual());
    }

    @Test
    void testIsNumber() {
        GsonJsonNode intNode = new GsonJsonNode(new JsonPrimitive(123));
        assertTrue(intNode.isNumber());

        GsonJsonNode doubleNode = new GsonJsonNode(new JsonPrimitive(123.45));
        assertTrue(doubleNode.isNumber());

        GsonJsonNode stringNode = new GsonJsonNode(new JsonPrimitive("test"));
        assertFalse(stringNode.isNumber());

        GsonJsonNode objectNode = new GsonJsonNode(new JsonObject());
        assertFalse(objectNode.isNumber());

        GsonJsonNode nullNode = new GsonJsonNode(null);
        assertFalse(nullNode.isNumber());
    }

    @Test
    void testIsBoolean() {
        GsonJsonNode trueNode = new GsonJsonNode(new JsonPrimitive(true));
        assertTrue(trueNode.isBoolean());

        GsonJsonNode falseNode = new GsonJsonNode(new JsonPrimitive(false));
        assertTrue(falseNode.isBoolean());

        GsonJsonNode stringNode = new GsonJsonNode(new JsonPrimitive("test"));
        assertFalse(stringNode.isBoolean());

        GsonJsonNode objectNode = new GsonJsonNode(new JsonObject());
        assertFalse(objectNode.isBoolean());

        GsonJsonNode nullNode = new GsonJsonNode(null);
        assertFalse(nullNode.isBoolean());
    }

    @Test
    void testIsArray() {
        JsonArray array = new JsonArray();
        array.add("test");
        GsonJsonNode arrayNode = new GsonJsonNode(array);
        assertTrue(arrayNode.isArray());

        GsonJsonNode stringNode = new GsonJsonNode(new JsonPrimitive("test"));
        assertFalse(stringNode.isArray());

        GsonJsonNode objectNode = new GsonJsonNode(new JsonObject());
        assertFalse(objectNode.isArray());

        GsonJsonNode nullNode = new GsonJsonNode(null);
        assertFalse(nullNode.isArray());
    }

    @Test
    void testIsObject() {
        JsonObject object = new JsonObject();
        object.addProperty("key", "value");
        GsonJsonNode objectNode = new GsonJsonNode(object);
        assertTrue(objectNode.isObject());

        GsonJsonNode stringNode = new GsonJsonNode(new JsonPrimitive("test"));
        assertFalse(stringNode.isObject());

        GsonJsonNode nullNode = new GsonJsonNode(null);
        assertFalse(nullNode.isObject());
    }

    @Test
    void testGet() {
        JsonObject object = new JsonObject();
        object.addProperty("name", "John");
        object.addProperty("age", 30);
        GsonJsonNode objectNode = new GsonJsonNode(object);

        JsonNode nameNode = objectNode.get("name");
        assertNotNull(nameNode);
        assertEquals("John", nameNode.asText());

        JsonNode ageNode = objectNode.get("age");
        assertNotNull(ageNode);
        assertEquals(30, ageNode.asInt());

        JsonNode nonExistentNode = objectNode.get("nonexistent");
        assertNull(nonExistentNode);

        GsonJsonNode stringNode = new GsonJsonNode(new JsonPrimitive("test"));
        assertNull(stringNode.get("name"));

        GsonJsonNode nullNode = new GsonJsonNode(null);
        assertNull(nullNode.get("name"));
    }

    @Test
    void testGetOnNonObject() {
        GsonJsonNode stringNode = new GsonJsonNode(new JsonPrimitive("test"));
        JsonNode result = stringNode.get("field");
        assertNull(result);
    }

    @Test
    void testHas() {
        JsonObject object = new JsonObject();
        object.addProperty("name", "John");
        GsonJsonNode objectNode = new GsonJsonNode(object);

        assertTrue(objectNode.has("name"));
        assertFalse(objectNode.has("age"));

        GsonJsonNode stringNode = new GsonJsonNode(new JsonPrimitive("test"));
        assertFalse(stringNode.has("name"));

        GsonJsonNode nullNode = new GsonJsonNode(null);
        assertFalse(nullNode.has("name"));
    }

    @Test
    void testHasOnNonObject() {
        GsonJsonNode stringNode = new GsonJsonNode(new JsonPrimitive("test"));
        assertFalse(stringNode.has("field"));
    }

    @Test
    void testAsText() {
        GsonJsonNode stringNode = new GsonJsonNode(new JsonPrimitive("test"));
        assertEquals("test", stringNode.asText());

        GsonJsonNode stringNode2 = new GsonJsonNode(new JsonObject());
        assertEquals("{}", stringNode2.asText());

        GsonJsonNode nullNode = new GsonJsonNode(JsonNull.INSTANCE);
        assertNull(nullNode.asText());

        GsonJsonNode nullNode2 = new GsonJsonNode(null);
        assertNull(nullNode2.asText());

        GsonJsonNode numberNode = new GsonJsonNode(new JsonPrimitive(123));
        assertNotNull(numberNode.asText());
    }

    @Test
    void testAsInt() {
        GsonJsonNode intNode = new GsonJsonNode(new JsonPrimitive(123));
        assertEquals(123, intNode.asInt());

        GsonJsonNode stringIntNode = new GsonJsonNode(new JsonPrimitive("456"));
        assertEquals(456, stringIntNode.asInt());

        GsonJsonNode booleanIntNode = new GsonJsonNode(new JsonPrimitive(false));
        assertEquals(0, booleanIntNode.asInt());

        GsonJsonNode nullNode = new GsonJsonNode(JsonNull.INSTANCE);
        assertEquals(0, nullNode.asInt());

        GsonJsonNode nullNode2 = new GsonJsonNode(null);
        assertEquals(0, nullNode2.asInt());

        GsonJsonNode nullNode3 = new GsonJsonNode(new JsonObject());
        assertEquals(0, nullNode3.asInt());

    }

    @Test
    void testAsLong() {
        GsonJsonNode longNode = new GsonJsonNode(new JsonPrimitive(123456789L));
        assertEquals(123456789L, longNode.asLong());

        GsonJsonNode stringLongNode = new GsonJsonNode(new JsonPrimitive("987654321"));
        assertEquals(987654321L, stringLongNode.asLong());

        GsonJsonNode booleanNode = new GsonJsonNode(new JsonPrimitive(false));
        assertEquals(0L, booleanNode.asLong());

        GsonJsonNode nullNode = new GsonJsonNode(JsonNull.INSTANCE);
        assertEquals(0L, nullNode.asLong());

        GsonJsonNode nullNode2 = new GsonJsonNode(null);
        assertEquals(0L, nullNode2.asLong());

        GsonJsonNode nullNode3 = new GsonJsonNode(new JsonObject());
        assertEquals(0L, nullNode3.asLong());
    }

    @Test
    void testAsDouble() {
        GsonJsonNode doubleNode = new GsonJsonNode(new JsonPrimitive(123.45));
        assertEquals(123.45, doubleNode.asDouble(), 0.001);

        GsonJsonNode stringDoubleNode = new GsonJsonNode(new JsonPrimitive("678.90"));
        assertEquals(678.90, stringDoubleNode.asDouble(), 0.001);

        GsonJsonNode booleanNode = new GsonJsonNode(new JsonPrimitive(false));
        assertEquals(0.0, booleanNode.asDouble(), 0.001);

        GsonJsonNode nullNode = new GsonJsonNode(JsonNull.INSTANCE);
        assertEquals(0.0, nullNode.asDouble(), 0.001);

        GsonJsonNode nullNode2 = new GsonJsonNode(null);
        assertEquals(0.0, nullNode2.asDouble(), 0.001);

        GsonJsonNode nullNode3 = new GsonJsonNode(new JsonObject());
        assertEquals(0.0, nullNode3.asDouble(), 0.001);
    }

    @Test
    void testAsBoolean() {
        GsonJsonNode trueNode = new GsonJsonNode(new JsonPrimitive(true));
        assertTrue(trueNode.asBoolean());

        GsonJsonNode falseNode = new GsonJsonNode(new JsonPrimitive(false));
        assertFalse(falseNode.asBoolean());

        GsonJsonNode stringTrueNode = new GsonJsonNode(new JsonPrimitive("true"));
        assertTrue(stringTrueNode.asBoolean());

        GsonJsonNode stringFalseNode = new GsonJsonNode(new JsonPrimitive("false"));
        assertFalse(stringFalseNode.asBoolean());

        GsonJsonNode intNode = new GsonJsonNode(new JsonPrimitive(0));
        assertFalse(intNode.asBoolean());

        GsonJsonNode nullNode = new GsonJsonNode(JsonNull.INSTANCE);
        assertFalse(nullNode.asBoolean());

        GsonJsonNode nullNode2 = new GsonJsonNode(null);
        assertFalse(nullNode2.asBoolean());

        GsonJsonNode nullNode3 = new GsonJsonNode(new JsonObject());
        assertFalse(nullNode3.asBoolean());
    }

    @Test
    void testElements() {
        JsonArray array = new JsonArray();
        array.add("a");
        array.add("b");
        array.add("c");
        GsonJsonNode arrayNode = new GsonJsonNode(array);

        Iterator<JsonNode> elements = arrayNode.elements();
        assertTrue(elements.hasNext());
        assertEquals("a", elements.next().asText());
        assertEquals("b", elements.next().asText());
        assertEquals("c", elements.next().asText());
        assertFalse(elements.hasNext());

        GsonJsonNode stringNode = new GsonJsonNode(new JsonPrimitive("test"));
        assertNotNull(stringNode.elements());

        GsonJsonNode nullNode = new GsonJsonNode(null);
        assertNotNull(nullNode.elements());
    }

    @Test
    void testElementsOnNonArray() {
        GsonJsonNode stringNode = new GsonJsonNode(new JsonPrimitive("test"));
        Iterator<JsonNode> elements = stringNode.elements();
        assertFalse(elements.hasNext());
    }

    @Test
    void testFieldNames() {
        JsonObject object = new JsonObject();
        object.addProperty("name", "John");
        object.addProperty("age", 30);
        object.addProperty("city", "NYC");
        GsonJsonNode objectNode = new GsonJsonNode(object);

        Iterator<String> fieldNames = objectNode.fieldNames();
        assertTrue(fieldNames.hasNext());

        int count = 0;
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            assertTrue(fieldName.equals("name") || fieldName.equals("age") || fieldName.equals("city"));
            count++;
        }
        assertEquals(3, count);

        GsonJsonNode stringNode = new GsonJsonNode(new JsonPrimitive("test"));
        assertNotNull(stringNode.fieldNames());

        GsonJsonNode nullNode = new GsonJsonNode(null);
        assertNotNull(nullNode.fieldNames());
    }

    @Test
    void testFieldNamesOnNonObject() {
        GsonJsonNode stringNode = new GsonJsonNode(new JsonPrimitive("test"));
        Iterator<String> fieldNames = stringNode.fieldNames();
        assertFalse(fieldNames.hasNext());
    }

    @Test
    void testUnwrap() {
        JsonPrimitive primitive = new JsonPrimitive("test");
        GsonJsonNode node = new GsonJsonNode(primitive);
        Object unwrapped = node.unwrap();
        assertSame(primitive, unwrapped);
    }

    @Test
    void testComplexObject() {
        JsonObject person = new JsonObject();
        person.addProperty("name", "John");
        person.addProperty("age", 30);

        JsonArray hobbies = new JsonArray();
        hobbies.add("reading");
        hobbies.add("coding");
        person.add("hobbies", hobbies);

        GsonJsonNode personNode = new GsonJsonNode(person);

        assertTrue(personNode.isObject());
        assertTrue(personNode.has("name"));
        assertTrue(personNode.has("age"));
        assertTrue(personNode.has("hobbies"));

        assertEquals("John", personNode.get("name").asText());
        assertEquals(30, personNode.get("age").asInt());

        JsonNode hobbiesNode = personNode.get("hobbies");
        assertTrue(hobbiesNode.isArray());

        Iterator<JsonNode> hobbiesIterator = hobbiesNode.elements();
        assertEquals("reading", hobbiesIterator.next().asText());
        assertEquals("coding", hobbiesIterator.next().asText());
    }

    @Test
    void testNestedObject() {
        JsonObject address = new JsonObject();
        address.addProperty("city", "NYC");
        address.addProperty("zip", "10001");

        JsonObject person = new JsonObject();
        person.addProperty("name", "John");
        person.add("address", address);

        GsonJsonNode personNode = new GsonJsonNode(person);

        JsonNode addressNode = personNode.get("address");
        assertNotNull(addressNode);
        assertTrue(addressNode.isObject());
        assertEquals("NYC", addressNode.get("city").asText());
        assertEquals("10001", addressNode.get("zip").asText());
    }
}
