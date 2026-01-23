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
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
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
    void testTypeAdapterFactory() {
        GsonJsonNode stringNode = new GsonJsonNode(new JsonPrimitive("test"));
        gson.toJson(stringNode);

        JsonArray array = new JsonArray();
        array.add("a");
        array.add("b");
        array.add("c");
        GsonJsonNode arrayNode = new GsonJsonNode(array);
        gson.toJson(arrayNode);

        GsonJsonNode nullNode = new GsonJsonNode(null);
        gson.toJson(nullNode);

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

    @Test
    void testStaticAndTransientFieldsIgnored() {
        // Arrange
        ClassWithStaticAndTransient obj = new ClassWithStaticAndTransient();
        obj.normalField = "normal";
        ClassWithStaticAndTransient.staticField = "static";

        // Act
        String json = gson.toJson(obj);
        ClassWithStaticAndTransient result = gson.fromJson(json, ClassWithStaticAndTransient.class);

        // Assert
        assertTrue(json.contains("\"normal_field\":\"normal\""));
        assertFalse(json.contains("\"staticField\""));
        assertFalse(json.contains("\"transientField\""));
        assertEquals("normal", result.normalField);
    }

    @Test
    void testEmptyJsonPropertyValue() {
        // Arrange
        ClassWithEmptyJsonProperty obj = new ClassWithEmptyJsonProperty();
        obj.fieldWithEmptyAnnotation = "test";

        // Act
        String json = gson.toJson(obj);
        ClassWithEmptyJsonProperty result = gson.fromJson(json, ClassWithEmptyJsonProperty.class);

        // Assert
        // Empty @JsonProperty value should be ignored, field name used as-is
        assertTrue(json.contains("\"fieldWithEmptyAnnotation\":\"test\""));
        assertEquals("test", result.fieldWithEmptyAnnotation);
    }

    @Test
    void testInheritanceWithAnnotations() {
        // Arrange
        ChildClass obj = new ChildClass();
        obj.parentField = "parent value";
        obj.childField = "child value";

        // Act
        String json = gson.toJson(obj);
        ChildClass result = gson.fromJson(json, ChildClass.class);

        // Assert
        assertTrue(json.contains("\"parent_json\":\"parent value\""));
        assertTrue(json.contains("\"child_json\":\"child value\""));
        assertEquals("parent value", result.parentField);
        assertEquals("child value", result.childField);
    }

    @Test
    void testMixedAnnotatedAndPlainFields() {
        // Arrange
        MixedClass obj = new MixedClass();
        obj.annotatedField = "annotated";
        obj.plainField = "plain";

        // Act
        String json = gson.toJson(obj);
        MixedClass result = gson.fromJson(json, MixedClass.class);

        // Assert
        assertTrue(json.contains("\"annotated_json\":\"annotated\""));
        assertTrue(json.contains("\"plainField\":\"plain\""));
        assertEquals("annotated", result.annotatedField);
        assertEquals("plain", result.plainField);
    }

    @Test
    void testDeserializationPrefersMappedName() {
        // Arrange - JSON with both mapped and original field names
        String json = "{\"json_name\":\"mapped\",\"fieldName\":\"original\"}";

        // Act
        TestClass result = gson.fromJson(json, TestClass.class);

        // Assert
        // Should prefer the mapped name and avoid duplicate assignment
        assertEquals("mapped", result.fieldName);
    }

    @Test
    void testPrimitiveTypesSkipped() {
        // Arrange
        Integer primitiveInt = 42;
        String primitiveString = "test";

        // Act & Assert - Should use default adapters for primitives
        String intJson = gson.toJson(primitiveInt);
        String strJson = gson.toJson(primitiveString);

        assertEquals("42", intJson);
        assertEquals("\"test\"", strJson);
    }

    @Test
    void testArrayTypesSkipped() {
        // Arrange
        String[] array = {"a", "b", "c"};

        // Act
        String json = gson.toJson(array);
        String[] result = gson.fromJson(json, String[].class);

        // Assert
        assertEquals("[\"a\",\"b\",\"c\"]", json);
        assertArrayEquals(array, result);
    }

    @Test
    void testEnumTypesSkipped() {
        // Arrange
        TestEnum enumValue = TestEnum.VALUE1;

        // Act
        String json = gson.toJson(enumValue);
        TestEnum result = gson.fromJson(json, TestEnum.class);

        // Assert
        assertEquals("\"VALUE1\"", json);
        assertEquals(TestEnum.VALUE1, result);
    }

    @Test
    void testJavaStandardLibraryClassesSkipped() {
        // Arrange
        java.util.Date date = new java.util.Date(0);

        // Act & Assert - Should use default Gson adapter
        String json = gson.toJson(date);
        assertNotNull(json);
    }

    @Test
    void testComplexNestedObject() {
        // Arrange
        ComplexClass obj = new ComplexClass();
        obj.outerField = "outer";
        obj.nested = new TestClass();
        obj.nested.fieldName = "nested value";

        // Act
        String json = gson.toJson(obj);
        ComplexClass result = gson.fromJson(json, ComplexClass.class);

        // Assert
        assertTrue(json.contains("\"outer_json\":\"outer\""));
        assertTrue(json.contains("\"json_name\":\"nested value\""));
        assertEquals("outer", result.outerField);
        assertEquals("nested value", result.nested.fieldName);
    }

    @Test
    void testJsonPropertyAndJsonAliasTogether() {
        // Arrange - Test with JsonProperty name
        String json1 = "{\"json_name\":\"value1\"}";
        ClassWithBothAnnotations result1 = gson.fromJson(json1, ClassWithBothAnnotations.class);
        assertEquals("value1", result1.field);

        // Arrange - Test with JsonAlias name
        String json2 = "{\"alias_name\":\"value2\"}";
        ClassWithBothAnnotations result2 = gson.fromJson(json2, ClassWithBothAnnotations.class);
        assertEquals("value2", result2.field);

        // Arrange - Test serialization uses JsonProperty
        ClassWithBothAnnotations obj = new ClassWithBothAnnotations();
        obj.field = "test";
        String json = gson.toJson(obj);
        assertTrue(json.contains("\"json_name\":\"test\""));
        assertFalse(json.contains("\"alias_name\""));
    }

    // Test class with @JsonProperty annotation
    static class TestClass {
        @JsonProperty("json_name")
        String fieldName;
    }

    // Test class with static and transient fields
    static class ClassWithStaticAndTransient {
        @JsonProperty("normal_field")
        String normalField;

        static String staticField;

        transient String transientField = "transient";
    }

    // Test class with empty @JsonProperty value
    static class ClassWithEmptyJsonProperty {
        @JsonProperty("")
        String fieldWithEmptyAnnotation;
    }

    // Parent class with annotation
    static class ParentClass {
        @JsonProperty("parent_json")
        String parentField;
    }

    // Child class extending parent
    static class ChildClass extends ParentClass {
        @JsonProperty("child_json")
        String childField;
    }

    // Class with mixed annotated and plain fields
    static class MixedClass {
        @JsonProperty("annotated_json")
        String annotatedField;

        String plainField;
    }

    // Test enum
    enum TestEnum {
        VALUE1, VALUE2
    }

    // Complex nested class
    static class ComplexClass {
        @JsonProperty("outer_json")
        String outerField;

        TestClass nested;
    }

    // Class with both @JsonProperty and @JsonAlias
    static class ClassWithBothAnnotations {
        @JsonProperty("json_name")
        @JsonAlias("alias_name")
        String field;
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
