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
package io.github.guoshiqiufeng.dify.client.core.codec;

import io.github.guoshiqiufeng.dify.client.core.codec.exception.JsonException;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JsonDeserialize annotation and JsonDeserializer interface
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/01/12
 */
class JsonDeserializeTest {

    @Test
    void testJsonDeserializeAnnotationPresent() {
        assertTrue(TestClassWithDeserializer.class.isAnnotationPresent(JsonDeserialize.class));
    }

    @Test
    void testJsonDeserializeAnnotationNotPresent() {
        assertFalse(TestClassWithoutDeserializer.class.isAnnotationPresent(JsonDeserialize.class));
    }

    @Test
    void testJsonDeserializeAnnotationUsing() {
        JsonDeserialize annotation = TestClassWithDeserializer.class.getAnnotation(JsonDeserialize.class);
        assertNotNull(annotation);
        assertEquals(TestDeserializer.class, annotation.using());
    }

    @Test
    void testJsonDeserializeAnnotationRetention() {
        JsonDeserialize annotation = TestClassWithDeserializer.class.getAnnotation(JsonDeserialize.class);
        assertNotNull(annotation);
        // Verify annotation is retained at runtime
        assertTrue(annotation.annotationType().isAnnotation());
    }

    @Test
    void testJsonDeserializeAnnotationTarget() {
        // Verify annotation can be applied to TYPE
        Annotation[] annotations = TestClassWithDeserializer.class.getAnnotations();
        boolean found = false;
        for (Annotation annotation : annotations) {
            if (annotation instanceof JsonDeserialize) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    void testCustomDeserializerImplementation() throws JsonException {
        TestDeserializer deserializer = new TestDeserializer();
        JsonMapper mockMapper = new MockJsonMapper();
        JsonNode mockNode = new MockJsonNode();

        TestObject result = deserializer.deserialize(mockNode, mockMapper);

        assertNotNull(result);
        assertEquals("test", result.getValue());
    }

    @Test
    void testCustomDeserializerWithNullNode() {
        TestDeserializer deserializer = new TestDeserializer();
        JsonMapper mockMapper = new MockJsonMapper();

        assertThrows(JsonException.class, () -> {
            deserializer.deserialize(null, mockMapper);
        });
    }

    @Test
    void testCustomDeserializerWithNullMapper() {
        TestDeserializer deserializer = new TestDeserializer();
        JsonNode mockNode = new MockJsonNode();

        assertThrows(JsonException.class, () -> {
            deserializer.deserialize(mockNode, null);
        });
    }

    @Test
    void testMultipleClassesWithDifferentDeserializers() {
        JsonDeserialize annotation1 = TestClassWithDeserializer.class.getAnnotation(JsonDeserialize.class);
        JsonDeserialize annotation2 = AnotherTestClassWithDeserializer.class.getAnnotation(JsonDeserialize.class);

        assertNotNull(annotation1);
        assertNotNull(annotation2);
        assertEquals(TestDeserializer.class, annotation1.using());
        assertEquals(AnotherTestDeserializer.class, annotation2.using());
    }

    @Test
    void testDeserializerInterfaceContract() {
        // Verify that JsonDeserializer is a functional interface
        assertTrue(JsonDeserializer.class.isInterface());
        assertEquals(1, JsonDeserializer.class.getDeclaredMethods().length);
    }

    @Test
    void testDeserializerWithComplexObject() throws JsonException {
        ComplexObjectDeserializer deserializer = new ComplexObjectDeserializer();
        JsonMapper mockMapper = new MockJsonMapper();
        JsonNode mockNode = new MockJsonNode();

        ComplexObject result = deserializer.deserialize(mockNode, mockMapper);

        assertNotNull(result);
        assertEquals("complex", result.getName());
        assertEquals(42, result.getValue());
    }

    @Test
    void testDeserializerThrowsJsonException() {
        FailingDeserializer deserializer = new FailingDeserializer();
        JsonMapper mockMapper = new MockJsonMapper();
        JsonNode mockNode = new MockJsonNode();

        assertThrows(JsonException.class, () -> {
            deserializer.deserialize(mockNode, mockMapper);
        });
    }

    // Test classes and deserializers

    @JsonDeserialize(using = TestDeserializer.class)
    static class TestClassWithDeserializer {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    static class TestClassWithoutDeserializer {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    @JsonDeserialize(using = AnotherTestDeserializer.class)
    static class AnotherTestClassWithDeserializer {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    static class TestObject {
        private String value;

        public TestObject(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    static class ComplexObject {
        private String name;
        private int value;

        public ComplexObject(String name, int value) {
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

    static class TestDeserializer implements JsonDeserializer<TestObject> {
        @Override
        public TestObject deserialize(JsonNode root, JsonMapper jsonMapper) throws JsonException {
            if (root == null) {
                throw new JsonException("JsonNode cannot be null");
            }
            if (jsonMapper == null) {
                throw new JsonException("JsonMapper cannot be null");
            }
            return new TestObject("test");
        }
    }

    static class AnotherTestDeserializer implements JsonDeserializer<TestObject> {
        @Override
        public TestObject deserialize(JsonNode root, JsonMapper jsonMapper) throws JsonException {
            return new TestObject("another");
        }
    }

    static class ComplexObjectDeserializer implements JsonDeserializer<ComplexObject> {
        @Override
        public ComplexObject deserialize(JsonNode root, JsonMapper jsonMapper) throws JsonException {
            return new ComplexObject("complex", 42);
        }
    }

    static class FailingDeserializer implements JsonDeserializer<TestObject> {
        @Override
        public TestObject deserialize(JsonNode root, JsonMapper jsonMapper) throws JsonException {
            throw new JsonException("Deserialization failed");
        }
    }

    // Mock implementations for testing

    static class MockJsonMapper implements JsonMapper {
        @Override
        public String toJson(Object obj) {
            return "{}";
        }

        @Override
        public String toJsonIgnoreNull(Object obj) {
            return "{}";
        }

        @Override
        public <T> T fromJson(String json, Class<T> clazz) {
            return null;
        }

        @Override
        public <T> T fromJson(String json, io.github.guoshiqiufeng.dify.client.core.http.TypeReference<T> typeReference) {
            return null;
        }

        @Override
        public JsonNode parseTree(String json) {
            return new MockJsonNode();
        }

        @Override
        public <T> T treeToValue(JsonNode node, Class<T> clazz) {
            return null;
        }

        @Override
        public JsonNode valueToTree(Object obj) {
            return new MockJsonNode();
        }
    }

    static class MockJsonNode implements JsonNode {
        @Override
        public JsonNode get(String fieldName) {
            return this;
        }

        @Override
        public boolean isNull() {
            return false;
        }

        @Override
        public boolean isTextual() {
            return false;
        }

        @Override
        public boolean isNumber() {
            return false;
        }

        @Override
        public boolean isBoolean() {
            return false;
        }

        @Override
        public boolean isArray() {
            return false;
        }

        @Override
        public boolean isObject() {
            return true;
        }

        @Override
        public boolean has(String fieldName) {
            return true;
        }

        @Override
        public String asText() {
            return "test";
        }

        @Override
        public int asInt() {
            return 42;
        }

        @Override
        public long asLong() {
            return 42L;
        }

        @Override
        public double asDouble() {
            return 42.0;
        }

        @Override
        public boolean asBoolean() {
            return true;
        }

        @Override
        public java.util.Iterator<JsonNode> elements() {
            return java.util.Collections.emptyIterator();
        }

        @Override
        public java.util.Iterator<String> fieldNames() {
            return java.util.Collections.emptyIterator();
        }

        @Override
        public Object unwrap() {
            return this;
        }
    }
}
