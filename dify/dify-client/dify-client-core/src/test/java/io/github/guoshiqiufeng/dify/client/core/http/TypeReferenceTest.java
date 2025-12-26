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
package io.github.guoshiqiufeng.dify.client.core.http;

import org.junit.jupiter.api.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TypeReference
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/01/06
 */
class TypeReferenceTest {

    @Test
    void testSimpleTypeReference() {
        TypeReference<String> typeRef = new TypeReference<String>() {};
        Type type = typeRef.getType();

        assertNotNull(type);
        assertEquals(String.class, type);
    }

    @Test
    void testListTypeReference() {
        TypeReference<List<String>> typeRef = new TypeReference<List<String>>() {};
        Type type = typeRef.getType();

        assertNotNull(type);
        assertTrue(type instanceof ParameterizedType);

        ParameterizedType paramType = (ParameterizedType) type;
        assertEquals(List.class, paramType.getRawType());
        assertEquals(String.class, paramType.getActualTypeArguments()[0]);
    }

    @Test
    void testMapTypeReference() {
        TypeReference<Map<String, Integer>> typeRef = new TypeReference<Map<String, Integer>>() {};
        Type type = typeRef.getType();

        assertNotNull(type);
        assertTrue(type instanceof ParameterizedType);

        ParameterizedType paramType = (ParameterizedType) type;
        assertEquals(Map.class, paramType.getRawType());
        assertEquals(String.class, paramType.getActualTypeArguments()[0]);
        assertEquals(Integer.class, paramType.getActualTypeArguments()[1]);
    }

    @Test
    void testNestedGenericTypeReference() {
        TypeReference<List<Map<String, Object>>> typeRef = new TypeReference<List<Map<String, Object>>>() {};
        Type type = typeRef.getType();

        assertNotNull(type);
        assertTrue(type instanceof ParameterizedType);

        ParameterizedType paramType = (ParameterizedType) type;
        assertEquals(List.class, paramType.getRawType());

        Type innerType = paramType.getActualTypeArguments()[0];
        assertTrue(innerType instanceof ParameterizedType);

        ParameterizedType innerParamType = (ParameterizedType) innerType;
        assertEquals(Map.class, innerParamType.getRawType());
    }

    @Test
    void testIntegerTypeReference() {
        TypeReference<Integer> typeRef = new TypeReference<Integer>() {};
        Type type = typeRef.getType();

        assertNotNull(type);
        assertEquals(Integer.class, type);
    }

    @Test
    void testCustomClassTypeReference() {
        TypeReference<CustomTestClass> typeRef = new TypeReference<CustomTestClass>() {};
        Type type = typeRef.getType();

        assertNotNull(type);
        assertEquals(CustomTestClass.class, type);
    }

    @Test
    void testListOfCustomClassTypeReference() {
        TypeReference<List<CustomTestClass>> typeRef = new TypeReference<List<CustomTestClass>>() {};
        Type type = typeRef.getType();

        assertNotNull(type);
        assertTrue(type instanceof ParameterizedType);

        ParameterizedType paramType = (ParameterizedType) type;
        assertEquals(List.class, paramType.getRawType());
        assertEquals(CustomTestClass.class, paramType.getActualTypeArguments()[0]);
    }

    @Test
    void testNonParameterizedTypeReferenceThrowsException() {
        // This should throw an exception because TypeReference is not parameterized
        assertThrows(IllegalArgumentException.class, () -> {
            @SuppressWarnings("rawtypes")
            TypeReference typeRef = new TypeReference() {};
        });
    }

    @Test
    void testTypeReferenceIsAbstract() {
        // Verify that TypeReference is abstract and cannot be instantiated directly
        assertTrue(java.lang.reflect.Modifier.isAbstract(TypeReference.class.getModifiers()));
    }

    @Test
    void testMultipleInstancesOfSameType() {
        TypeReference<String> typeRef1 = new TypeReference<String>() {};
        TypeReference<String> typeRef2 = new TypeReference<String>() {};

        assertEquals(typeRef1.getType(), typeRef2.getType());
    }

    @Test
    void testDifferentTypes() {
        TypeReference<String> stringTypeRef = new TypeReference<String>() {};
        TypeReference<Integer> integerTypeRef = new TypeReference<Integer>() {};

        assertNotEquals(stringTypeRef.getType(), integerTypeRef.getType());
    }

    // Helper class for testing
    private static class CustomTestClass {
        private String field;

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }
    }
}
