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

import io.github.guoshiqiufeng.dify.client.core.codec.JsonException;
import io.github.guoshiqiufeng.dify.client.core.codec.JsonNode;
import io.github.guoshiqiufeng.dify.client.core.http.TypeReference;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Jackson3JsonMapper
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/01/06
 */
class Jackson3JsonMapperTest {

    private final Jackson3JsonMapper mapper = Jackson3JsonMapper.getInstance();

    @Test
    void testGetInstance() {
        Jackson3JsonMapper instance1 = Jackson3JsonMapper.getInstance();
        Jackson3JsonMapper instance2 = Jackson3JsonMapper.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    void testGetJsonMapper() {
        assertNotNull(Jackson3JsonMapper.getJsonMapper());
    }

    @Test
    void testToJsonSimpleObject() {
        TestPojo pojo = new TestPojo("John", 30);
        String json = mapper.toJson(pojo);
        assertNotNull(json);
        assertTrue(json.contains("John"));
        assertTrue(json.contains("30"));
    }

    @Test
    void testToJsonWithNull() {
        TestPojo pojo = new TestPojo(null, 30);
        String json = mapper.toJson(pojo);
        assertNotNull(json);
        // Jackson by default doesn't serialize null values unless configured
        // Just verify the JSON is valid and contains the age
        assertTrue(json.contains("30"));
    }

    @Test
    void testToJsonNull() {
        String json = mapper.toJson(null);
        assertEquals("null", json);
    }

    @Test
    void testFromJsonSimpleObject() {
        String json = "{\"name\":\"John\",\"age\":30}";
        TestPojo pojo = mapper.fromJson(json, TestPojo.class);
        assertNotNull(pojo);
        assertEquals("John", pojo.getName());
        assertEquals(30, pojo.getAge());
    }

    @Test
    void testFromJsonWithNullField() {
        String json = "{\"name\":null,\"age\":30}";
        TestPojo pojo = mapper.fromJson(json, TestPojo.class);
        assertNotNull(pojo);
        assertNull(pojo.getName());
        assertEquals(30, pojo.getAge());
    }

    @Test
    void testFromJsonInvalidJson() {
        assertThrows(JsonException.class, () -> {
            mapper.fromJson("{invalid json}", TestPojo.class);
        });
    }

    @Test
    void testFromJsonWithTypeReference() {
        String json = "[\"a\",\"b\",\"c\"]";
        List<String> list = mapper.fromJson(json, new TypeReference<List<String>>() {});
        assertNotNull(list);
        assertEquals(3, list.size());
        assertEquals("a", list.get(0));
        assertEquals("b", list.get(1));
        assertEquals("c", list.get(2));
    }

    @Test
    void testFromJsonMapWithTypeReference() {
        String json = "{\"key1\":\"value1\",\"key2\":\"value2\"}";
        Map<String, String> map = mapper.fromJson(json, new TypeReference<Map<String, String>>() {});
        assertNotNull(map);
        assertEquals(2, map.size());
        assertEquals("value1", map.get("key1"));
        assertEquals("value2", map.get("key2"));
    }

    @Test
    void testFromJsonComplexTypeReference() {
        String json = "[{\"name\":\"John\",\"age\":30},{\"name\":\"Jane\",\"age\":25}]";
        List<TestPojo> list = mapper.fromJson(json, new TypeReference<List<TestPojo>>() {});
        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals("John", list.get(0).getName());
        assertEquals(30, list.get(0).getAge());
        assertEquals("Jane", list.get(1).getName());
        assertEquals(25, list.get(1).getAge());
    }

    @Test
    void testParseTree() {
        String json = "{\"name\":\"John\",\"age\":30}";
        JsonNode node = mapper.parseTree(json);
        assertNotNull(node);
        assertTrue(node.isObject());
        assertTrue(node.has("name"));
        assertTrue(node.has("age"));
        assertEquals("John", node.get("name").asText());
        assertEquals(30, node.get("age").asInt());
    }

    @Test
    void testParseTreeArray() {
        String json = "[1,2,3]";
        JsonNode node = mapper.parseTree(json);
        assertNotNull(node);
        assertTrue(node.isArray());
        Iterator<JsonNode> elements = node.elements();
        assertTrue(elements.hasNext());
        assertEquals(1, elements.next().asInt());
        assertEquals(2, elements.next().asInt());
        assertEquals(3, elements.next().asInt());
        assertFalse(elements.hasNext());
    }

    @Test
    void testParseTreeInvalidJson() {
        assertThrows(JsonException.class, () -> {
            mapper.parseTree("{invalid}");
        });
    }

    @Test
    void testTreeToValue() {
        String json = "{\"name\":\"John\",\"age\":30}";
        JsonNode node = mapper.parseTree(json);
        TestPojo pojo = mapper.treeToValue(node, TestPojo.class);
        assertNotNull(pojo);
        assertEquals("John", pojo.getName());
        assertEquals(30, pojo.getAge());
    }

    @Test
    void testTreeToValueWithNull() {
        TestPojo pojo = mapper.treeToValue(null, TestPojo.class);
        assertNull(pojo);
    }

    @Test
    void testTreeToValueWithNullNode() {
        JsonNode node = mapper.parseTree("null");
        TestPojo pojo = mapper.treeToValue(node, TestPojo.class);
        assertNull(pojo);
    }

    @Test
    void testValueToTree() {
        TestPojo pojo = new TestPojo("John", 30);
        JsonNode node = mapper.valueToTree(pojo);
        assertNotNull(node);
        assertTrue(node.isObject());
        assertTrue(node.has("name"));
        assertTrue(node.has("age"));
        assertEquals("John", node.get("name").asText());
        assertEquals(30, node.get("age").asInt());
    }

    @Test
    void testValueToTreeWithNull() {
        JsonNode node = mapper.valueToTree(null);
        assertNotNull(node);
        assertTrue(node.isNull());
    }

    @Test
    void testValueToTreeList() {
        List<String> list = Arrays.asList("a", "b", "c");
        JsonNode node = mapper.valueToTree(list);
        assertNotNull(node);
        assertTrue(node.isArray());
        Iterator<JsonNode> elements = node.elements();
        assertEquals("a", elements.next().asText());
        assertEquals("b", elements.next().asText());
        assertEquals("c", elements.next().asText());
    }

    @Test
    void testRoundTripSerialization() {
        TestPojo original = new TestPojo("John", 30);
        String json = mapper.toJson(original);
        TestPojo deserialized = mapper.fromJson(json, TestPojo.class);
        assertEquals(original.getName(), deserialized.getName());
        assertEquals(original.getAge(), deserialized.getAge());
    }

    @Test
    void testRoundTripWithTree() {
        TestPojo original = new TestPojo("John", 30);
        JsonNode node = mapper.valueToTree(original);
        TestPojo deserialized = mapper.treeToValue(node, TestPojo.class);
        assertEquals(original.getName(), deserialized.getName());
        assertEquals(original.getAge(), deserialized.getAge());
    }

    @Test
    void testNestedObject() {
        String json = "{\"person\":{\"name\":\"John\",\"age\":30}}";
        JsonNode node = mapper.parseTree(json);
        JsonNode personNode = node.get("person");
        assertNotNull(personNode);
        assertTrue(personNode.isObject());
        assertEquals("John", personNode.get("name").asText());
        assertEquals(30, personNode.get("age").asInt());
    }

    @Test
    void testArrayOfObjects() {
        List<TestPojo> list = Arrays.asList(
                new TestPojo("John", 30),
                new TestPojo("Jane", 25)
        );
        String json = mapper.toJson(list);
        assertNotNull(json);
        assertTrue(json.contains("John"));
        assertTrue(json.contains("Jane"));
    }

    // Test POJO class
    public static class TestPojo {
        private String name;
        private int age;

        public TestPojo() {
        }

        public TestPojo(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }
}
