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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UriUtils
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/01/06
 */
class UriUtilsTest {

    @Test
    void testReplacePlaceholdersWithSingleVariable() {
        String path = "/users/{id}";
        String result = UriUtils.replacePlaceholders(path, 123);
        assertEquals("/users/123", result);
    }

    @Test
    void testReplacePlaceholdersWithMultipleVariables() {
        String path = "/users/{id}/posts/{postId}";
        String result = UriUtils.replacePlaceholders(path, 123, 456);
        assertEquals("/users/123/posts/456", result);
    }

    @Test
    void testReplacePlaceholdersWithNoVariables() {
        String path = "/users/all";
        String result = UriUtils.replacePlaceholders(path);
        assertEquals("/users/all", result);
    }

    @Test
    void testReplacePlaceholdersWithNullVariables() {
        String path = "/users/{id}";
        assertThrows(IllegalArgumentException.class, () -> {
            UriUtils.replacePlaceholders(path, (Object[]) null);
        });
    }

    @Test
    void testReplacePlaceholdersWithEmptyArray() {
        String path = "/users/{id}";
        assertThrows(IllegalArgumentException.class, () -> {
            UriUtils.replacePlaceholders(path, new Object[0]);
        });
    }

    @Test
    void testReplacePlaceholdersWithNullPath() {
        assertThrows(IllegalArgumentException.class, () -> {
            UriUtils.replacePlaceholders(null, 123);
        });
    }

    @Test
    void testReplacePlaceholdersWithStringVariable() {
        String path = "/users/{username}";
        String result = UriUtils.replacePlaceholders(path, "john");
        assertEquals("/users/john", result);
    }

    @Test
    void testReplacePlaceholdersWithMixedTypes() {
        String path = "/users/{id}/posts/{slug}";
        String result = UriUtils.replacePlaceholders(path, 123, "my-post");
        assertEquals("/users/123/posts/my-post", result);
    }

    @Test
    void testReplacePlaceholdersWithNullValue() {
        String path = "/users/{id}/posts/{postId}";
        assertThrows(IllegalArgumentException.class, () -> {
            UriUtils.replacePlaceholders(path, 123, null);
        });
    }

    @Test
    void testReplacePlaceholdersInOrder() {
        String path = "/{a}/{b}/{c}";
        String result = UriUtils.replacePlaceholders(path, "first", "second", "third");
        assertEquals("/first/second/third", result);
    }

    @Test
    void testEncodeQueryParam() {
        assertEquals("hello+world", UriUtils.encodeQueryParam("hello world"));
        assertEquals("test%40example.com", UriUtils.encodeQueryParam("test@example.com"));
        assertEquals("a%2Bb", UriUtils.encodeQueryParam("a+b"));
        assertEquals("100%25", UriUtils.encodeQueryParam("100%"));
    }

    @Test
    void testEncodeQueryParamWithSpecialCharacters() {
        assertEquals("a%26b", UriUtils.encodeQueryParam("a&b"));
        assertEquals("a%3Db", UriUtils.encodeQueryParam("a=b"));
        assertEquals("a%2Fb", UriUtils.encodeQueryParam("a/b"));
        assertEquals("a%3Fb", UriUtils.encodeQueryParam("a?b"));
    }

    @Test
    void testEncodeQueryParamWithEmptyString() {
        assertEquals("", UriUtils.encodeQueryParam(""));
    }

    @Test
    void testEncodeQueryParamWithNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            UriUtils.encodeQueryParam(null);
        });
    }

    @Test
    void testEncodeQueryParamOrEmpty() {
        assertEquals("hello+world", UriUtils.encodeQueryParamOrEmpty("hello world"));
        assertEquals("test", UriUtils.encodeQueryParamOrEmpty("test"));
        assertEquals("", UriUtils.encodeQueryParamOrEmpty(null));
        assertEquals("", UriUtils.encodeQueryParamOrEmpty(""));
    }

    @Test
    void testHasPlaceholders() {
        assertTrue(UriUtils.hasPlaceholders("/users/{id}"));
        assertTrue(UriUtils.hasPlaceholders("/users/{id}/posts/{postId}"));
        assertTrue(UriUtils.hasPlaceholders("{id}"));
        assertFalse(UriUtils.hasPlaceholders("/users/123"));
        assertFalse(UriUtils.hasPlaceholders("/users"));
        assertFalse(UriUtils.hasPlaceholders(""));
        assertFalse(UriUtils.hasPlaceholders(null));
    }

    @Test
    void testHasPlaceholdersWithMalformedPlaceholders() {
        assertFalse(UriUtils.hasPlaceholders("/users/{"));
        assertFalse(UriUtils.hasPlaceholders("/users/}"));
        assertFalse(UriUtils.hasPlaceholders("/users/{}"));
    }

    @Test
    void testCountPlaceholders() {
        assertEquals(0, UriUtils.countPlaceholders(null));
        assertEquals(0, UriUtils.countPlaceholders(""));
        assertEquals(0, UriUtils.countPlaceholders("/users/123"));
        assertEquals(1, UriUtils.countPlaceholders("/users/{id}"));
        assertEquals(2, UriUtils.countPlaceholders("/users/{id}/posts/{postId}"));
        assertEquals(3, UriUtils.countPlaceholders("/{a}/{b}/{c}"));
    }

    @Test
    void testCountPlaceholdersWithMalformedPlaceholders() {
        assertEquals(0, UriUtils.countPlaceholders("/users/{"));
        assertEquals(0, UriUtils.countPlaceholders("/users/}"));
        assertEquals(0, UriUtils.countPlaceholders("/users/{}"));
    }

    @Test
    void testUriVariablePattern() {
        assertNotNull(UriUtils.URI_VARIABLE_PATTERN);
        assertTrue(UriUtils.URI_VARIABLE_PATTERN.matcher("{id}").find());
        assertTrue(UriUtils.URI_VARIABLE_PATTERN.matcher("{userId}").find());
        assertFalse(UriUtils.URI_VARIABLE_PATTERN.matcher("{}").find());
    }

    @Test
    void testUtilityClassCannotBeInstantiated() throws Exception {
        java.lang.reflect.Constructor<UriUtils> constructor = UriUtils.class.getDeclaredConstructor();
        assertTrue(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    void testReplacePlaceholdersWithComplexPath() {
        String path = "/api/v1/users/{userId}/posts/{postId}/comments/{commentId}";
        String result = UriUtils.replacePlaceholders(path, 1, 2, 3);
        assertEquals("/api/v1/users/1/posts/2/comments/3", result);
    }

    @Test
    void testEncodeQueryParamWithUnicodeCharacters() {
        String result = UriUtils.encodeQueryParam("你好");
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.contains("%"));
    }

    @Test
    void testReplacePlaceholdersWithMoreVariablesThanPlaceholders() {
        String path = "/users/{id}";
        String result = UriUtils.replacePlaceholders(path, 123, 456, 789);
        assertEquals("/users/123", result);
    }

    @Test
    void testReplacePlaceholdersWithFewerVariablesThanPlaceholders() {
        String path = "/users/{id}/posts/{postId}/comments/{commentId}";
        assertThrows(IllegalArgumentException.class, () -> {
            UriUtils.replacePlaceholders(path, 123);
        });
    }
}
