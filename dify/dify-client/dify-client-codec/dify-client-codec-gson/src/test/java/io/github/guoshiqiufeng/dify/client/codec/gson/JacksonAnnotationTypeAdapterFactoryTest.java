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

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JacksonAnnotationTypeAdapterFactory
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/1/13
 */
class JacksonAnnotationTypeAdapterFactoryTest {

    private Gson gson;

    @BeforeEach
    void setUp() {
        gson = new GsonBuilder()
                .registerTypeAdapterFactory(new JacksonAnnotationTypeAdapterFactory())
                .create();
    }

    @Test
    void testSerializationWithJsonProperty() {
        // Arrange
        TestClass obj = new TestClass();
        obj.fieldName = "test value";

        // Act
        String json = gson.toJson(obj);

        // Assert
        assertTrue(json.contains("\"json_name\":\"test value\""));
        assertFalse(json.contains("\"fieldName\""));
    }

    @Test
    void testDeserializationWithJsonProperty() {
        // Arrange
        String json = "{\"json_name\":\"test value\"}";

        // Act
        TestClass result = gson.fromJson(json, TestClass.class);

        // Assert
        assertNotNull(result);
        assertEquals("test value", result.fieldName);
    }

    @Test
    void testDeserializationWithJsonAlias() {
        // Arrange
        String json = "{\"alternative_name\":\"alias value\"}";

        // Act
        TestClassWithAlias result = gson.fromJson(json, TestClassWithAlias.class);

        // Assert
        assertNotNull(result);
        assertEquals("alias value", result.fieldName);
    }

    @Test
    void testDeserializationWithMultipleAliases() {
        // Arrange - test first alias
        String json1 = "{\"alias1\":\"value1\"}";
        TestClassWithMultipleAliases result1 = gson.fromJson(json1, TestClassWithMultipleAliases.class);
        assertEquals("value1", result1.fieldName);

        // Arrange - test second alias
        String json2 = "{\"alias2\":\"value2\"}";
        TestClassWithMultipleAliases result2 = gson.fromJson(json2, TestClassWithMultipleAliases.class);
        assertEquals("value2", result2.fieldName);
    }

    @Test
    void testClassWithoutAnnotations() {
        // Arrange
        PlainClass obj = new PlainClass();
        obj.plainField = "plain value";

        // Act
        String json = gson.toJson(obj);
        PlainClass result = gson.fromJson(json, PlainClass.class);

        // Assert
        assertTrue(json.contains("\"plainField\":\"plain value\""));
        assertEquals("plain value", result.plainField);
    }

    @Test
    void testNullValues() {
        // Arrange
        TestClass obj = new TestClass();
        obj.fieldName = null;

        // Act
        String json = gson.toJson(obj);

        // Assert
        assertNotNull(json);
    }

    // Test class with @JsonProperty annotation
    static class TestClass {
        @JsonProperty("json_name")
        String fieldName;
    }

    // Test class with @JsonAlias annotation
    static class TestClassWithAlias {
        @JsonAlias("alternative_name")
        String fieldName;
    }

    // Test class with multiple @JsonAlias values
    static class TestClassWithMultipleAliases {
        @JsonAlias({"alias1", "alias2"})
        String fieldName;
    }

    // Plain class without annotations
    static class PlainClass {
        String plainField;
    }
}