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
package io.github.guoshiqiufeng.dify.core.utils;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Assert
 *
 * @author yanghq
 * @version 1.0
 * @since 2026/01/13
 */
class AssertTest {

    // notNull tests

    @Test
    void testNotNullWithNonNullObject() {
        assertDoesNotThrow(() -> Assert.notNull("test", "Should not throw"));
        assertDoesNotThrow(() -> Assert.notNull(123, "Should not throw"));
        assertDoesNotThrow(() -> Assert.notNull(new Object(), "Should not throw"));
    }

    @Test
    void testNotNullWithNullObject() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Assert.notNull(null, "Object must not be null"));
        assertEquals("Object must not be null", exception.getMessage());
    }

    // isNull tests

    @Test
    void testIsNullWithNullObject() {
        assertDoesNotThrow(() -> Assert.isNull(null, "Should not throw"));
    }

    @Test
    void testIsNullWithNonNullObject() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Assert.isNull("test", "Object must be null"));
        assertEquals("Object must be null", exception.getMessage());
    }

    // isTrue tests

    @Test
    void testIsTrueWithTrueExpression() {
        assertDoesNotThrow(() -> Assert.isTrue(true, "Should not throw"));
        assertDoesNotThrow(() -> Assert.isTrue(1 == 1, "Should not throw"));
        assertDoesNotThrow(() -> Assert.isTrue("test".equals("test"), "Should not throw"));
    }

    @Test
    void testIsTrueWithFalseExpression() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Assert.isTrue(false, "Expression must be true"));
        assertEquals("Expression must be true", exception.getMessage());
    }

    // isFalse tests

    @Test
    void testIsFalseWithFalseExpression() {
        assertDoesNotThrow(() -> Assert.isFalse(false, "Should not throw"));
        assertDoesNotThrow(() -> Assert.isFalse(1 == 2, "Should not throw"));
        assertDoesNotThrow(() -> Assert.isFalse("test".equals("other"), "Should not throw"));
    }

    @Test
    void testIsFalseWithTrueExpression() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Assert.isFalse(true, "Expression must be false"));
        assertEquals("Expression must be false", exception.getMessage());
    }

    // notEmpty String tests

    @Test
    void testNotEmptyStringWithValidString() {
        assertDoesNotThrow(() -> Assert.notEmpty("test", "Should not throw"));
        assertDoesNotThrow(() -> Assert.notEmpty("a", "Should not throw"));
        assertDoesNotThrow(() -> Assert.notEmpty("  a  ", "Should not throw"));
    }

    @Test
    void testNotEmptyStringWithNullString() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Assert.notEmpty((String) null, "String must not be empty"));
        assertEquals("String must not be empty", exception.getMessage());
    }

    @Test
    void testNotEmptyStringWithEmptyString() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Assert.notEmpty("", "String must not be empty"));
        assertEquals("String must not be empty", exception.getMessage());
    }

    @Test
    void testNotEmptyStringWithWhitespaceOnly() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Assert.notEmpty("   ", "String must not be empty"));
        assertEquals("String must not be empty", exception.getMessage());
    }

    @Test
    void testNotEmptyStringWithTabsAndNewlines() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Assert.notEmpty("\t\n", "String must not be empty"));
        assertEquals("String must not be empty", exception.getMessage());
    }

    // hasText tests

    @Test
    void testHasTextWithValidString() {
        assertDoesNotThrow(() -> Assert.hasText("test", "Should not throw"));
        assertDoesNotThrow(() -> Assert.hasText("a", "Should not throw"));
        assertDoesNotThrow(() -> Assert.hasText("  a  ", "Should not throw"));
    }

    @Test
    void testHasTextWithNullString() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Assert.hasText(null, "String must have text"));
        assertEquals("String must have text", exception.getMessage());
    }

    @Test
    void testHasTextWithEmptyString() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Assert.hasText("", "String must have text"));
        assertEquals("String must have text", exception.getMessage());
    }

    @Test
    void testHasTextWithWhitespaceOnly() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Assert.hasText("   ", "String must have text"));
        assertEquals("String must have text", exception.getMessage());
    }

    // notEmpty Collection tests

    @Test
    void testNotEmptyCollectionWithValidCollection() {
        assertDoesNotThrow(() -> Assert.notEmpty(Arrays.asList("a", "b"), "Should not throw"));
        assertDoesNotThrow(() -> Assert.notEmpty(Collections.singletonList("a"), "Should not throw"));
    }

    @Test
    void testNotEmptyCollectionWithNullCollection() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Assert.notEmpty((Collection<?>) null, "Collection must not be empty"));
        assertEquals("Collection must not be empty", exception.getMessage());
    }

    @Test
    void testNotEmptyCollectionWithEmptyCollection() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Assert.notEmpty(new ArrayList<>(), "Collection must not be empty"));
        assertEquals("Collection must not be empty", exception.getMessage());
    }

    // notEmpty Map tests

    @Test
    void testNotEmptyMapWithValidMap() {
        Map<String, String> map = new HashMap<>();
        map.put("key", "value");
        assertDoesNotThrow(() -> Assert.notEmpty(map, "Should not throw"));
        assertDoesNotThrow(() -> Assert.notEmpty(Collections.singletonMap("key", "value"), "Should not throw"));
    }

    @Test
    void testNotEmptyMapWithNullMap() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Assert.notEmpty((Map<?, ?>) null, "Map must not be empty"));
        assertEquals("Map must not be empty", exception.getMessage());
    }

    @Test
    void testNotEmptyMapWithEmptyMap() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Assert.notEmpty(new HashMap<>(), "Map must not be empty"));
        assertEquals("Map must not be empty", exception.getMessage());
    }

    // notEmpty Array tests

    @Test
    void testNotEmptyArrayWithValidArray() {
        assertDoesNotThrow(() -> Assert.notEmpty(new String[]{"a", "b"}, "Should not throw"));
        assertDoesNotThrow(() -> Assert.notEmpty(new Integer[]{1}, "Should not throw"));
    }

    @Test
    void testNotEmptyArrayWithNullArray() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Assert.notEmpty((Object[]) null, "Array must not be empty"));
        assertEquals("Array must not be empty", exception.getMessage());
    }

    @Test
    void testNotEmptyArrayWithEmptyArray() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Assert.notEmpty(new String[]{}, "Array must not be empty"));
        assertEquals("Array must not be empty", exception.getMessage());
    }

    // noNullElements tests

    @Test
    void testNoNullElementsWithValidArray() {
        assertDoesNotThrow(() -> Assert.noNullElements(new String[]{"a", "b", "c"}, "Should not throw"));
        assertDoesNotThrow(() -> Assert.noNullElements(new Integer[]{1, 2, 3}, "Should not throw"));
    }

    @Test
    void testNoNullElementsWithNullArray() {
        // null array should not throw
        assertDoesNotThrow(() -> Assert.noNullElements(null, "Should not throw"));
    }

    @Test
    void testNoNullElementsWithNullElement() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Assert.noNullElements(new String[]{"a", null, "c"}, "Array must not contain null elements"));
        assertEquals("Array must not contain null elements", exception.getMessage());
    }

    @Test
    void testNoNullElementsWithAllNullElements() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Assert.noNullElements(new String[]{null, null}, "Array must not contain null elements"));
        assertEquals("Array must not contain null elements", exception.getMessage());
    }

    @Test
    void testNoNullElementsWithEmptyArray() {
        // Empty array has no null elements
        assertDoesNotThrow(() -> Assert.noNullElements(new String[]{}, "Should not throw"));
    }

    // state tests

    @Test
    void testStateWithTrueState() {
        assertDoesNotThrow(() -> Assert.state(true, "Should not throw"));
        assertDoesNotThrow(() -> Assert.state(1 == 1, "Should not throw"));
    }

    @Test
    void testStateWithFalseState() {
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> Assert.state(false, "State must be true"));
        assertEquals("State must be true", exception.getMessage());
    }

    // Complex scenarios

    @Test
    void testMultipleAssertionsInSequence() {
        String value = "test";
        assertDoesNotThrow(() -> {
            Assert.notNull(value, "Value must not be null");
            Assert.notEmpty(value, "Value must not be empty");
            Assert.isTrue(value.length() > 0, "Value must have length");
        });
    }

    @Test
    void testAssertionWithComplexConditions() {
        List<String> list = Arrays.asList("a", "b", "c");
        assertDoesNotThrow(() -> {
            Assert.notNull(list, "List must not be null");
            Assert.notEmpty(list, "List must not be empty");
            Assert.isTrue(list.size() == 3, "List must have 3 elements");
            Assert.isFalse(list.isEmpty(), "List must not be empty");
        });
    }

    @Test
    void testAssertionFailureStopsExecution() {
        assertThrows(IllegalArgumentException.class, () -> {
            Assert.notNull(null, "First assertion fails");
            // This line should never be reached
            Assert.isTrue(true, "This should not be evaluated");
        });
    }

    @Test
    void testCustomErrorMessages() {
        String customMessage = "Custom error message with details: value=null";
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Assert.notNull(null, customMessage));
        assertEquals(customMessage, exception.getMessage());
    }

    @Test
    void testAssertWithDifferentTypes() {
        // String
        assertDoesNotThrow(() -> Assert.notNull("test", "String must not be null"));

        // Integer
        assertDoesNotThrow(() -> Assert.notNull(123, "Integer must not be null"));

        // Boolean
        assertDoesNotThrow(() -> Assert.notNull(Boolean.TRUE, "Boolean must not be null"));

        // Object
        assertDoesNotThrow(() -> Assert.notNull(new Object(), "Object must not be null"));

        // Array
        assertDoesNotThrow(() -> Assert.notNull(new int[]{1, 2, 3}, "Array must not be null"));
    }

    @Test
    void testBoundaryConditions() {
        // Single character string
        assertDoesNotThrow(() -> Assert.notEmpty("a", "Should not throw"));

        // Single element collection
        assertDoesNotThrow(() -> Assert.notEmpty(Collections.singletonList("a"), "Should not throw"));

        // Single element array
        assertDoesNotThrow(() -> Assert.notEmpty(new String[]{"a"}, "Should not throw"));

        // Single entry map
        assertDoesNotThrow(() -> Assert.notEmpty(Collections.singletonMap("k", "v"), "Should not throw"));
    }

    @Test
    void testStateVsIsTrue() {
        // state throws IllegalStateException
        assertThrows(IllegalStateException.class, () -> Assert.state(false, "State error"));

        // isTrue throws IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> Assert.isTrue(false, "Argument error"));
    }

    @Test
    void testArrayWithMixedContent() {
        Object[] mixedArray = {"string", 123, true, new Object()};
        assertDoesNotThrow(() -> Assert.notEmpty(mixedArray, "Should not throw"));
        assertDoesNotThrow(() -> Assert.noNullElements(mixedArray, "Should not throw"));
    }

    @Test
    void testCollectionWithNullElements() {
        List<String> listWithNulls = new ArrayList<>();
        listWithNulls.add("a");
        listWithNulls.add(null);
        listWithNulls.add("c");

        // notEmpty should pass (collection is not empty)
        assertDoesNotThrow(() -> Assert.notEmpty(listWithNulls, "Should not throw"));
    }

    @Test
    void testStringWithSpecialCharacters() {
        assertDoesNotThrow(() -> Assert.notEmpty("!@#$%^&*()", "Should not throw"));
        assertDoesNotThrow(() -> Assert.notEmpty("中文字符", "Should not throw"));
        assertDoesNotThrow(() -> Assert.notEmpty("🎉🎊", "Should not throw"));
    }
}
