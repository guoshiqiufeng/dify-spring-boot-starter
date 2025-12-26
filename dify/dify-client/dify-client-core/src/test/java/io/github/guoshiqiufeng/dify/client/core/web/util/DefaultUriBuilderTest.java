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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DefaultUriBuilder
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/01/06
 */
class DefaultUriBuilderTest {

    @Test
    void testSimplePath() {
        DefaultUriBuilder builder = new DefaultUriBuilder();
        String uri = builder.path("/users").build();
        assertEquals("/users", uri);
    }

    @Test
    void testPathWithSingleVariable() {
        DefaultUriBuilder builder = new DefaultUriBuilder();
        String uri = builder.path("/users/{id}").build(123);
        assertEquals("/users/123", uri);
    }

    @Test
    void testPathWithMultipleVariables() {
        DefaultUriBuilder builder = new DefaultUriBuilder();
        String uri = builder.path("/users/{id}/posts/{postId}").build(123, 456);
        assertEquals("/users/123/posts/456", uri);
    }

    @Test
    void testPathWithSingleQueryParam() {
        DefaultUriBuilder builder = new DefaultUriBuilder();
        String uri = builder.path("/users")
                .queryParam("page", 1)
                .build();
        assertEquals("/users?page=1", uri);
    }

    @Test
    void testPathWithMultipleQueryParams() {
        DefaultUriBuilder builder = new DefaultUriBuilder();
        String uri = builder.path("/users")
                .queryParam("page", 1)
                .queryParam("size", 10)
                .queryParam("sort", "name")
                .build();
        assertEquals("/users?page=1&size=10&sort=name", uri);
    }

    @Test
    void testPathWithVariablesAndQueryParams() {
        DefaultUriBuilder builder = new DefaultUriBuilder();
        String uri = builder.path("/users/{id}/posts")
                .queryParam("page", 1)
                .queryParam("size", 10)
                .build(123);
        assertEquals("/users/123/posts?page=1&size=10", uri);
    }

    @Test
    void testQueryParamWithNullValue() {
        DefaultUriBuilder builder = new DefaultUriBuilder();
        String uri = builder.path("/users")
                .queryParam("page", 1)
                .queryParam("filter", null)
                .queryParam("size", 10)
                .build();
        assertEquals("/users?page=1&size=10", uri);
    }

    @Test
    void testQueryParamWithSpecialCharacters() {
        DefaultUriBuilder builder = new DefaultUriBuilder();
        String uri = builder.path("/search")
                .queryParam("q", "hello world")
                .queryParam("email", "test@example.com")
                .build();
        assertTrue(uri.contains("q=hello world"));
        assertTrue(uri.contains("email=test@example.com"));
    }

    @Test
    void testQueryParamIfPresentWithValue() {
        DefaultUriBuilder builder = new DefaultUriBuilder();
        String uri = builder.path("/users")
                .queryParamIfPresent("filter", Optional.of("active"))
                .build();
        assertEquals("/users?filter=active", uri);
    }

    @Test
    void testQueryParamIfPresentWithEmpty() {
        DefaultUriBuilder builder = new DefaultUriBuilder();
        String uri = builder.path("/users")
                .queryParam("page", 1)
                .queryParamIfPresent("filter", Optional.empty())
                .queryParam("size", 10)
                .build();
        assertEquals("/users?page=1&size=10", uri);
    }

    @Test
    void testBuildWithNullPath() {
        DefaultUriBuilder builder = new DefaultUriBuilder();
        String uri = builder.queryParam("page", 1).build();
        assertEquals("?page=1", uri);
    }

    @Test
    void testBuildWithEmptyPath() {
        DefaultUriBuilder builder = new DefaultUriBuilder();
        String uri = builder.path("").queryParam("page", 1).build();
        assertEquals("?page=1", uri);
    }

    @Test
    void testBuildWithNoQueryParams() {
        DefaultUriBuilder builder = new DefaultUriBuilder();
        String uri = builder.path("/users").build();
        assertEquals("/users", uri);
    }

    @Test
    void testBuildWithNoPathAndNoQueryParams() {
        DefaultUriBuilder builder = new DefaultUriBuilder();
        String uri = builder.build();
        assertEquals("", uri);
    }

    @Test
    void testMethodChaining() {
        DefaultUriBuilder builder = new DefaultUriBuilder();
        UriBuilder result = builder.path("/users")
                .queryParam("page", 1)
                .queryParam("size", 10);
        assertSame(builder, result);
    }

    @Test
    void testQueryParamIfPresentChaining() {
        DefaultUriBuilder builder = new DefaultUriBuilder();
        UriBuilder result = builder.path("/users")
                .queryParamIfPresent("filter", Optional.of("active"));
        assertSame(builder, result);
    }

    @Test
    void testBuildMultipleTimes() {
        DefaultUriBuilder builder = new DefaultUriBuilder();
        builder.path("/users/{id}").queryParam("page", 1);

        String uri1 = builder.build(123);
        String uri2 = builder.build(456);

        assertEquals("/users/123?page=1", uri1);
        assertEquals("/users/456?page=1", uri2);
    }

    @Test
    void testQueryParamWithIntegerValue() {
        DefaultUriBuilder builder = new DefaultUriBuilder();
        String uri = builder.path("/users")
                .queryParam("page", 1)
                .queryParam("size", 10)
                .build();
        assertEquals("/users?page=1&size=10", uri);
    }

    @Test
    void testQueryParamWithBooleanValue() {
        DefaultUriBuilder builder = new DefaultUriBuilder();
        String uri = builder.path("/users")
                .queryParam("active", true)
                .queryParam("deleted", false)
                .build();
        assertEquals("/users?active=true&deleted=false", uri);
    }

    @Test
    void testQueryParamWithLongValue() {
        DefaultUriBuilder builder = new DefaultUriBuilder();
        String uri = builder.path("/users")
                .queryParam("timestamp", 1234567890L)
                .build();
        assertEquals("/users?timestamp=1234567890", uri);
    }

    @Test
    void testComplexUri() {
        DefaultUriBuilder builder = new DefaultUriBuilder();
        String uri = builder.path("/api/v1/users/{userId}/posts/{postId}")
                .queryParam("include", "comments")
                .queryParam("page", 1)
                .queryParam("size", 20)
                .build(123, 456);
        assertEquals("/api/v1/users/123/posts/456?include=comments&page=1&size=20", uri);
    }

    @Test
    void testQueryParamOrder() {
        DefaultUriBuilder builder = new DefaultUriBuilder();
        String uri = builder.path("/users")
                .queryParam("c", "3")
                .queryParam("a", "1")
                .queryParam("b", "2")
                .build();
        // LinkedHashMap should maintain insertion order
        assertEquals("/users?c=3&a=1&b=2", uri);
    }

    @Test
    void testPathOverwrite() {
        DefaultUriBuilder builder = new DefaultUriBuilder();
        builder.path("/users");
        builder.path("/posts");
        String uri = builder.build();
        assertEquals("/posts", uri);
    }

    @Test
    void testQueryParamWithEmptyString() {
        DefaultUriBuilder builder = new DefaultUriBuilder();
        String uri = builder.path("/users")
                .queryParam("filter", "")
                .build();
        assertEquals("/users?filter=", uri);
    }

    @Test
    void testBuildWithNullVariables() {
        DefaultUriBuilder builder = new DefaultUriBuilder();
        String uri = builder.path("/users/{id}").build((Object[]) null);
        assertEquals("/users/{id}", uri);
    }

    @Test
    void testBuildWithEmptyVariables() {
        DefaultUriBuilder builder = new DefaultUriBuilder();
        String uri = builder.path("/users/{id}").build(new Object[0]);
        assertEquals("/users/{id}", uri);
    }

    @Test
    void testMultipleQueryParamsWithSameName() {
        DefaultUriBuilder builder = new DefaultUriBuilder();
        builder.path("/users")
                .queryParam("tag", "java")
                .queryParam("tag", "spring");
        String uri = builder.build();
        // The second call should overwrite the first
        assertEquals("/users?tag=spring", uri);
    }
}
