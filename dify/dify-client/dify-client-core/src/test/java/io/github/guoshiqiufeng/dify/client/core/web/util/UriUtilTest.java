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
package io.github.guoshiqiufeng.dify.client.core.web.util;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UriUtils
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/01/06
 */
class UriUtilTest {

    @Test
    void testReplacePlaceholdersWithSingleVariable() {
        String path = "/users/{id}";
        String result = UriUtil.replacePlaceholders(path, 123);
        assertEquals("/users/123", result);
    }

    @Test
    void testReplacePlaceholdersWithMultipleVariables() {
        String path = "/users/{id}/posts/{postId}";
        String result = UriUtil.replacePlaceholders(path, 123, 456);
        assertEquals("/users/123/posts/456", result);
    }

    @Test
    void testReplacePlaceholdersWithNoVariables() {
        String path = "/users/all";
        String result = UriUtil.replacePlaceholders(path);
        assertEquals("/users/all", result);
    }

    @Test
    void testReplacePlaceholdersWithNullVariables() {
        String path = "/users/{id}";
        assertThrows(IllegalArgumentException.class, () -> {
            UriUtil.replacePlaceholders(path, (Object[]) null);
        });
    }

    @Test
    void testReplacePlaceholdersWithEmptyArray() {
        String path = "/users/{id}";
        assertThrows(IllegalArgumentException.class, () -> {
            UriUtil.replacePlaceholders(path, new Object[0]);
        });
    }

    @Test
    void testReplacePlaceholdersWithNullPath() {
        assertThrows(IllegalArgumentException.class, () -> {
            UriUtil.replacePlaceholders(null, 123);
        });
    }

    @Test
    void testReplacePlaceholdersWithStringVariable() {
        String path = "/users/{username}";
        String result = UriUtil.replacePlaceholders(path, "john");
        assertEquals("/users/john", result);
    }

    @Test
    void testReplacePlaceholdersWithMixedTypes() {
        String path = "/users/{id}/posts/{slug}";
        String result = UriUtil.replacePlaceholders(path, 123, "my-post");
        assertEquals("/users/123/posts/my-post", result);
    }

    @Test
    void testReplacePlaceholdersWithNullValue() {
        String path = "/users/{id}/posts/{postId}";
        assertThrows(IllegalArgumentException.class, () -> {
            UriUtil.replacePlaceholders(path, 123, null);
        });
    }

    @Test
    void testReplacePlaceholdersInOrder() {
        String path = "/{a}/{b}/{c}";
        String result = UriUtil.replacePlaceholders(path, "first", "second", "third");
        assertEquals("/first/second/third", result);
    }

    @Test
    void testEncodeQueryParam() {
        assertEquals("hello+world", UriUtil.encodeQueryParam("hello world"));
        assertEquals("test%40example.com", UriUtil.encodeQueryParam("test@example.com"));
        assertEquals("a%2Bb", UriUtil.encodeQueryParam("a+b"));
        assertEquals("100%25", UriUtil.encodeQueryParam("100%"));
    }

    @Test
    void testEncodeQueryParamWithSpecialCharacters() {
        assertEquals("a%26b", UriUtil.encodeQueryParam("a&b"));
        assertEquals("a%3Db", UriUtil.encodeQueryParam("a=b"));
        assertEquals("a%2Fb", UriUtil.encodeQueryParam("a/b"));
        assertEquals("a%3Fb", UriUtil.encodeQueryParam("a?b"));
    }

    @Test
    void testEncodeQueryParamWithEmptyString() {
        assertEquals("", UriUtil.encodeQueryParam(""));
    }

    @Test
    void testEncodeQueryParamWithNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            UriUtil.encodeQueryParam(null);
        });
    }

    @Test
    void testEncodeQueryParamOrEmpty() {
        assertEquals("hello+world", UriUtil.encodeQueryParamOrEmpty("hello world"));
        assertEquals("test", UriUtil.encodeQueryParamOrEmpty("test"));
        assertEquals("", UriUtil.encodeQueryParamOrEmpty(null));
        assertEquals("", UriUtil.encodeQueryParamOrEmpty(""));
    }

    @Test
    void testHasPlaceholders() {
        assertTrue(UriUtil.hasPlaceholders("/users/{id}"));
        assertTrue(UriUtil.hasPlaceholders("/users/{id}/posts/{postId}"));
        assertTrue(UriUtil.hasPlaceholders("{id}"));
        assertFalse(UriUtil.hasPlaceholders("/users/123"));
        assertFalse(UriUtil.hasPlaceholders("/users"));
        assertFalse(UriUtil.hasPlaceholders(""));
        assertFalse(UriUtil.hasPlaceholders(null));
    }

    @Test
    void testHasPlaceholdersWithMalformedPlaceholders() {
        assertFalse(UriUtil.hasPlaceholders("/users/{"));
        assertFalse(UriUtil.hasPlaceholders("/users/}"));
        assertFalse(UriUtil.hasPlaceholders("/users/{}"));
    }

    @Test
    void testCountPlaceholders() {
        assertEquals(0, UriUtil.countPlaceholders(null));
        assertEquals(0, UriUtil.countPlaceholders(""));
        assertEquals(0, UriUtil.countPlaceholders("/users/123"));
        assertEquals(1, UriUtil.countPlaceholders("/users/{id}"));
        assertEquals(2, UriUtil.countPlaceholders("/users/{id}/posts/{postId}"));
        assertEquals(3, UriUtil.countPlaceholders("/{a}/{b}/{c}"));
    }

    @Test
    void testCountPlaceholdersWithMalformedPlaceholders() {
        assertEquals(0, UriUtil.countPlaceholders("/users/{"));
        assertEquals(0, UriUtil.countPlaceholders("/users/}"));
        assertEquals(0, UriUtil.countPlaceholders("/users/{}"));
    }

    @Test
    void testUriVariablePattern() {
        assertNotNull(UriUtil.URI_VARIABLE_PATTERN);
        assertTrue(UriUtil.URI_VARIABLE_PATTERN.matcher("{id}").find());
        assertTrue(UriUtil.URI_VARIABLE_PATTERN.matcher("{userId}").find());
        assertFalse(UriUtil.URI_VARIABLE_PATTERN.matcher("{}").find());
    }

    @Test
    void testUtilityClassCannotBeInstantiated() throws Exception {
        java.lang.reflect.Constructor<UriUtil> constructor = UriUtil.class.getDeclaredConstructor();
        assertTrue(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    void testReplacePlaceholdersWithComplexPath() {
        String path = "/api/v1/users/{userId}/posts/{postId}/comments/{commentId}";
        String result = UriUtil.replacePlaceholders(path, 1, 2, 3);
        assertEquals("/api/v1/users/1/posts/2/comments/3", result);
    }

    @Test
    void testEncodeQueryParamWithUnicodeCharacters() {
        String result = UriUtil.encodeQueryParam("你好");
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains("%"));
    }

    @Test
    void testReplacePlaceholdersWithMoreVariablesThanPlaceholders() {
        String path = "/users/{id}";
        String result = UriUtil.replacePlaceholders(path, 123, 456, 789);
        assertEquals("/users/123", result);
    }

    @Test
    void testReplacePlaceholdersWithFewerVariablesThanPlaceholders() {
        String path = "/users/{id}/posts/{postId}/comments/{commentId}";
        assertThrows(IllegalArgumentException.class, () -> {
            UriUtil.replacePlaceholders(path, 123);
        });
    }

    @Test
    void testVariablePathWithNoPlaceholders() {
        // This should not throw an exception
        assertDoesNotThrow(() -> UriUtil.variablePath("/users/123"));
    }

    @Test
    void testVariablePathWithPlaceholders() {
        // This should throw an exception
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            UriUtil.variablePath("/users/{id}");
        });
        assertEquals("Not enough variable values available to expand 'id'", exception.getMessage());
    }

    @Test
    void testEncodeQueryParamWithAllSpecialCharacters() {
        // Test comprehensive special character encoding
        String input = "!@#$%^&*()_+-=[]{}|;':\",./<>?";
        String result = UriUtil.encodeQueryParam(input);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testEncodeQueryParamOrEmptyWithSpecialCharacters() {
        // Test comprehensive special character encoding
        String input = "test@example.com";
        String result = UriUtil.encodeQueryParamOrEmpty(input);
        assertEquals("test%40example.com", result);
    }

    @Test
    void testReplacePlaceholdersWithNullValueInMiddle() {
        // Test when a null value is in the middle of multiple parameters
        // When param is null, it's skipped, so the second placeholder remains
        String path = "/users/{id}/posts/{postId}";
        assertThrows(IllegalArgumentException.class, () -> {
            UriUtil.replacePlaceholders(path, 123, null);
        });
    }

    @Test
    void testEncodeQueryParamWithUnsupportedEncodingException() {
        // Test the catch block for UnsupportedEncodingException
        // This uses Mockito to mock URLEncoder.encode to throw the exception
        try (MockedStatic<URLEncoder> mockedEncoder = mockStatic(URLEncoder.class)) {
            mockedEncoder.when(() -> URLEncoder.encode(anyString(), eq(StandardCharsets.UTF_8.name())))
                    .thenThrow(new java.io.UnsupportedEncodingException("Mock exception"));

            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                UriUtil.encodeQueryParam("test");
            });
            assertEquals("UTF-8 encoding not supported", exception.getMessage());
            assertNotNull(exception.getCause());
            assertInstanceOf(UnsupportedEncodingException.class, exception.getCause());
        }
    }

    @Test
    void testEncodeQueryParamOrEmptyWithUnsupportedEncodingException() {
        // Test the catch block for UnsupportedEncodingException in encodeQueryParamOrEmpty
        try (MockedStatic<URLEncoder> mockedEncoder = mockStatic(URLEncoder.class)) {
            mockedEncoder.when(() -> URLEncoder.encode(anyString(), eq(StandardCharsets.UTF_8.name())))
                    .thenThrow(new java.io.UnsupportedEncodingException("Mock exception"));

            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                UriUtil.encodeQueryParamOrEmpty("test");
            });
            assertEquals("UTF-8 encoding not supported", exception.getMessage());
            assertNotNull(exception.getCause());
            assertInstanceOf(UnsupportedEncodingException.class, exception.getCause());
        }
    }
}
