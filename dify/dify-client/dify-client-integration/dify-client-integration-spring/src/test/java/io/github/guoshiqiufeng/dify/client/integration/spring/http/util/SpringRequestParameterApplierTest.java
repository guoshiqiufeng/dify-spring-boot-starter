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
package io.github.guoshiqiufeng.dify.client.integration.spring.http.util;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SpringRequestParameterApplier
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/2/24
 */
class SpringRequestParameterApplierTest {

    @Test
    void testApplyHeaders() {
        // Arrange
        WebClient.Builder builder = WebClient.builder();
        WebClient webClient = builder.baseUrl("http://example.com").build();
        WebClient.RequestBodySpec spec = webClient.post();

        Map<String, String> headers = new HashMap<>();
        headers.put("X-Custom-Header", "test-value");
        headers.put("Authorization", "Bearer token");

        // Act
        WebClient.RequestBodySpec result = SpringRequestParameterApplier.applyHeaders(spec, headers);

        // Assert
        assertNotNull(result);
        assertSame(spec, result);
    }

    @Test
    void testApplyHeadersWithEmptyMap() {
        // Arrange
        WebClient.Builder builder = WebClient.builder();
        WebClient webClient = builder.baseUrl("http://example.com").build();
        WebClient.RequestBodySpec spec = webClient.post();

        Map<String, String> headers = new HashMap<>();

        // Act
        WebClient.RequestBodySpec result = SpringRequestParameterApplier.applyHeaders(spec, headers);

        // Assert
        assertNotNull(result);
        assertSame(spec, result);
    }

    @Test
    void testApplyCookies() {
        // Arrange
        WebClient.Builder builder = WebClient.builder();
        WebClient webClient = builder.baseUrl("http://example.com").build();
        WebClient.RequestBodySpec spec = webClient.post();

        Map<String, String> cookies = new HashMap<>();
        cookies.put("session", "abc123");
        cookies.put("user", "john");

        // Act
        WebClient.RequestBodySpec result = SpringRequestParameterApplier.applyCookies(spec, cookies);

        // Assert
        assertNotNull(result);
        assertSame(spec, result);
    }

    @Test
    void testApplyCookiesWithEmptyMap() {
        // Arrange
        WebClient.Builder builder = WebClient.builder();
        WebClient webClient = builder.baseUrl("http://example.com").build();
        WebClient.RequestBodySpec spec = webClient.post();

        Map<String, String> cookies = new HashMap<>();

        // Act
        WebClient.RequestBodySpec result = SpringRequestParameterApplier.applyCookies(spec, cookies);

        // Assert
        assertNotNull(result);
        assertSame(spec, result);
    }

    @Test
    void testApplyHeadersAndCookies() {
        // Arrange
        WebClient.Builder builder = WebClient.builder();
        WebClient webClient = builder.baseUrl("http://example.com").build();
        WebClient.RequestBodySpec spec = webClient.post();

        Map<String, String> headers = new HashMap<>();
        headers.put("X-Custom-Header", "test-value");

        Map<String, String> cookies = new HashMap<>();
        cookies.put("session", "abc123");

        // Act
        WebClient.RequestBodySpec result = SpringRequestParameterApplier.applyHeadersAndCookies(spec, headers, cookies);

        // Assert
        assertNotNull(result);
        assertSame(spec, result);
    }

    @Test
    void testApplyHeadersAndCookiesWithEmptyCookies() {
        // Arrange
        WebClient.Builder builder = WebClient.builder();
        WebClient webClient = builder.baseUrl("http://example.com").build();
        WebClient.RequestBodySpec spec = webClient.post();

        Map<String, String> headers = new HashMap<>();
        headers.put("X-Custom-Header", "test-value");

        Map<String, String> cookies = new HashMap<>();

        // Act
        WebClient.RequestBodySpec result = SpringRequestParameterApplier.applyHeadersAndCookies(spec, headers, cookies);

        // Assert
        assertNotNull(result);
        assertSame(spec, result);
    }

    @Test
    void testApplyHeadersAndCookiesWithMultipleCookies() {
        // Arrange
        WebClient.Builder builder = WebClient.builder();
        WebClient webClient = builder.baseUrl("http://example.com").build();
        WebClient.RequestBodySpec spec = webClient.post();

        Map<String, String> headers = new HashMap<>();
        headers.put("X-Custom-Header", "test-value");

        Map<String, String> cookies = new HashMap<>();
        cookies.put("session", "abc123");
        cookies.put("user", "john");
        cookies.put("token", "xyz789");

        // Act
        WebClient.RequestBodySpec result = SpringRequestParameterApplier.applyHeadersAndCookies(spec, headers, cookies);

        // Assert
        assertNotNull(result);
        assertSame(spec, result);
    }

    @Test
    void testApplyHeadersAndCookiesBothEmpty() {
        // Arrange
        WebClient.Builder builder = WebClient.builder();
        WebClient webClient = builder.baseUrl("http://example.com").build();
        WebClient.RequestBodySpec spec = webClient.post();

        Map<String, String> headers = new HashMap<>();
        Map<String, String> cookies = new HashMap<>();

        // Act
        WebClient.RequestBodySpec result = SpringRequestParameterApplier.applyHeadersAndCookies(spec, headers, cookies);

        // Assert
        assertNotNull(result);
        assertSame(spec, result);
    }
}
