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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

/**
 * Unit tests for HttpRequestBuilder default methods
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/01/16
 */
class HttpRequestBuilderTest {

    private HttpRequestBuilder builder;

    @BeforeEach
    void setUp() {
        // Create a mock that delegates to real default methods
        builder = mock(HttpRequestBuilder.class, CALLS_REAL_METHODS);
    }

    @Test
    void testContentType() {
        builder.contentType("application/json");

        verify(builder).header("Content-Type", "application/json");
    }

    @Test
    void testContentTypeWithDifferentMediaType() {
        builder.contentType("text/html");

        verify(builder).header("Content-Type", "text/html");
    }

    @Test
    void testSetBearerAuth() {
        builder.setBearerAuth("test-token-123");

        verify(builder).header("AUTHORIZATION", "Bearer test-token-123");
    }

    @Test
    void testSetBearerAuthWithNullToken() {
        HttpRequestBuilder result = builder.setBearerAuth(null);

        assertSame(builder, result);
        verify(builder, never()).header(anyString(), anyString());
    }

    @Test
    void testSetBearerAuthWithEmptyToken() {
        HttpRequestBuilder result = builder.setBearerAuth("");

        assertSame(builder, result);
        verify(builder, never()).header(anyString(), anyString());
    }

    @Test
    void testSetBearerAuthWithValidToken() {
        builder.setBearerAuth("my-secret-token");

        verify(builder).header("AUTHORIZATION", "Bearer my-secret-token");
    }

    @Test
    void testContentTypeWithMultipartFormData() {
        builder.contentType("multipart/form-data");

        verify(builder).header("Content-Type", "multipart/form-data");
    }

    @Test
    void testSetBearerAuthWithLongToken() {
        String longToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        builder.setBearerAuth(longToken);

        verify(builder).header("AUTHORIZATION", "Bearer " + longToken);
    }
}
