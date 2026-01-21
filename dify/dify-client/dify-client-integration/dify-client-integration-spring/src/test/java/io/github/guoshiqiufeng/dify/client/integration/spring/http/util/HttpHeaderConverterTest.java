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

import io.github.guoshiqiufeng.dify.client.core.http.HttpHeaders;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for HttpHeaderConverter
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026-01-20
 */
class HttpHeaderConverterTest {

    @Test
    void testFromSpringHeadersWithSingleHeader() {
        org.springframework.http.HttpHeaders springHeaders = new org.springframework.http.HttpHeaders();
        springHeaders.add("Content-Type", "application/json");

        HttpHeaders result = HttpHeaderConverter.fromSpringHeaders(springHeaders);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey("Content-Type"));
        assertEquals("application/json", result.getFirst("Content-Type"));
    }

    @Test
    void testFromSpringHeadersWithMultipleHeaders() {
        org.springframework.http.HttpHeaders springHeaders = new org.springframework.http.HttpHeaders();
        springHeaders.add("Content-Type", "application/json");
        springHeaders.add("Authorization", "Bearer token");
        springHeaders.add("X-Custom-Header", "value");

        HttpHeaders result = HttpHeaderConverter.fromSpringHeaders(springHeaders);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("application/json", result.getFirst("Content-Type"));
        assertEquals("Bearer token", result.getFirst("Authorization"));
        assertEquals("value", result.getFirst("X-Custom-Header"));
    }

    @Test
    void testFromSpringHeadersWithMultipleValues() {
        org.springframework.http.HttpHeaders springHeaders = new org.springframework.http.HttpHeaders();
        springHeaders.add("Accept", "application/json");
        springHeaders.add("Accept", "text/plain");
        springHeaders.add("Accept", "application/xml");

        HttpHeaders result = HttpHeaderConverter.fromSpringHeaders(springHeaders);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey("Accept"));
        assertEquals(3, result.get("Accept").size());
        assertTrue(result.get("Accept").contains("application/json"));
        assertTrue(result.get("Accept").contains("text/plain"));
        assertTrue(result.get("Accept").contains("application/xml"));
    }

    @Test
    void testFromSpringHeadersWithNullHeaders() {
        HttpHeaders result = HttpHeaderConverter.fromSpringHeaders(null);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testFromSpringHeadersWithEmptyHeaders() {
        org.springframework.http.HttpHeaders springHeaders = new org.springframework.http.HttpHeaders();

        HttpHeaders result = HttpHeaderConverter.fromSpringHeaders(springHeaders);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testFromSpringHeadersWithEmptyValue() {
        org.springframework.http.HttpHeaders springHeaders = new org.springframework.http.HttpHeaders();
        springHeaders.add("X-Empty", "");

        HttpHeaders result = HttpHeaderConverter.fromSpringHeaders(springHeaders);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("", result.getFirst("X-Empty"));
    }

    @Test
    void testFromSpringHeadersWithSpecialCharacters() {
        org.springframework.http.HttpHeaders springHeaders = new org.springframework.http.HttpHeaders();
        springHeaders.add("X-Special", "value with spaces");
        springHeaders.add("X-Symbols", "!@#$%^&*()");

        HttpHeaders result = HttpHeaderConverter.fromSpringHeaders(springHeaders);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("value with spaces", result.getFirst("X-Special"));
        assertEquals("!@#$%^&*()", result.getFirst("X-Symbols"));
    }

    @Test
    void testFromSpringHeadersWithUnicodeCharacters() {
        org.springframework.http.HttpHeaders springHeaders = new org.springframework.http.HttpHeaders();
        springHeaders.add("X-Unicode", "你好世界");
        springHeaders.add("X-Japanese", "こんにちは");

        HttpHeaders result = HttpHeaderConverter.fromSpringHeaders(springHeaders);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("你好世界", result.getFirst("X-Unicode"));
        assertEquals("こんにちは", result.getFirst("X-Japanese"));
    }

    @Test
    void testFromSpringHeadersWithCommonHttpHeaders() {
        org.springframework.http.HttpHeaders springHeaders = new org.springframework.http.HttpHeaders();
        springHeaders.add("Content-Type", "application/json");
        springHeaders.add("Content-Length", "1234");
        springHeaders.add("Cache-Control", "no-cache");
        springHeaders.add("User-Agent", "TestAgent/1.0");
        springHeaders.add("Accept-Encoding", "gzip, deflate");

        HttpHeaders result = HttpHeaderConverter.fromSpringHeaders(springHeaders);

        assertNotNull(result);
        assertEquals(5, result.size());
        assertEquals("application/json", result.getFirst("Content-Type"));
        assertEquals("1234", result.getFirst("Content-Length"));
        assertEquals("no-cache", result.getFirst("Cache-Control"));
        assertEquals("TestAgent/1.0", result.getFirst("User-Agent"));
        assertEquals("gzip, deflate", result.getFirst("Accept-Encoding"));
    }

    @Test
    void testFromSpringHeadersWithCaseInsensitiveHeaders() {
        org.springframework.http.HttpHeaders springHeaders = new org.springframework.http.HttpHeaders();
        springHeaders.add("content-type", "application/json");
        springHeaders.add("AUTHORIZATION", "Bearer token");
        springHeaders.add("X-Custom-Header", "value");

        HttpHeaders result = HttpHeaderConverter.fromSpringHeaders(springHeaders);

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    void testFromSpringHeadersWithLongHeaderValue() {
        org.springframework.http.HttpHeaders springHeaders = new org.springframework.http.HttpHeaders();
        String longValue = "a".repeat(1000);
        springHeaders.add("X-Long-Header", longValue);

        HttpHeaders result = HttpHeaderConverter.fromSpringHeaders(springHeaders);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(longValue, result.getFirst("X-Long-Header"));
    }

    @Test
    void testFromSpringHeadersWithMultipleHeadersAndValues() {
        org.springframework.http.HttpHeaders springHeaders = new org.springframework.http.HttpHeaders();
        springHeaders.add("Accept", "application/json");
        springHeaders.add("Accept", "text/plain");
        springHeaders.add("X-Custom", "value1");
        springHeaders.add("X-Custom", "value2");
        springHeaders.add("Authorization", "Bearer token");

        HttpHeaders result = HttpHeaderConverter.fromSpringHeaders(springHeaders);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(2, result.get("Accept").size());
        assertEquals(2, result.get("X-Custom").size());
        assertEquals(1, result.get("Authorization").size());
    }

    @Test
    void testFromSpringHeadersDoesNotModifyOriginal() {
        org.springframework.http.HttpHeaders springHeaders = new org.springframework.http.HttpHeaders();
        springHeaders.add("Content-Type", "application/json");
        springHeaders.add("Authorization", "Bearer token");

        int originalSize = springHeaders.size();

        HttpHeaders result = HttpHeaderConverter.fromSpringHeaders(springHeaders);

        // Verify original headers are unchanged
        assertEquals(originalSize, springHeaders.size());
        assertEquals("application/json", springHeaders.getFirst("Content-Type"));
        assertEquals("Bearer token", springHeaders.getFirst("Authorization"));

        // Verify result is independent
        result.add("X-New-Header", "new value");
        assertFalse(springHeaders.containsKey("X-New-Header"));
    }

    @Test
    void testFromSpringHeadersWithNumericValues() {
        org.springframework.http.HttpHeaders springHeaders = new org.springframework.http.HttpHeaders();
        springHeaders.add("Content-Length", "12345");
        springHeaders.add("X-Rate-Limit", "100");

        HttpHeaders result = HttpHeaderConverter.fromSpringHeaders(springHeaders);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("12345", result.getFirst("Content-Length"));
        assertEquals("100", result.getFirst("X-Rate-Limit"));
    }
}
