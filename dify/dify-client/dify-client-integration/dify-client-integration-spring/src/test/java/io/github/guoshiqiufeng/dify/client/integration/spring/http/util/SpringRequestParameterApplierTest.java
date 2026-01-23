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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SpringRequestParameterApplier
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026-01-20
 */
@ExtendWith(MockitoExtension.class)
class SpringRequestParameterApplierTest {

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @BeforeEach
    void setUp() {
        //when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        //when(requestBodySpec.cookie(anyString(), anyString())).thenReturn(requestBodySpec);
    }

    @Test
    void testApplyHeadersWithSingleHeader() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        WebClient.RequestBodySpec result = SpringRequestParameterApplier.applyHeaders(requestBodySpec, headers);

        assertNotNull(result);
        verify(requestBodySpec).header("Content-Type", "application/json");
    }

    @Test
    void testApplyHeadersWithMultipleHeaders() {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer token");
        headers.put("X-Custom-Header", "value");

        WebClient.RequestBodySpec result = SpringRequestParameterApplier.applyHeaders(requestBodySpec, headers);

        assertNotNull(result);
        verify(requestBodySpec).header("Content-Type", "application/json");
        verify(requestBodySpec).header("Authorization", "Bearer token");
        verify(requestBodySpec).header("X-Custom-Header", "value");
    }

    @Test
    void testApplyHeadersWithEmptyMap() {
        Map<String, String> headers = new HashMap<>();

        WebClient.RequestBodySpec result = SpringRequestParameterApplier.applyHeaders(requestBodySpec, headers);

        assertNotNull(result);
        verify(requestBodySpec, never()).header(anyString(), anyString());
    }

    @Test
    void testApplyCookiesWithSingleCookie() {
        Map<String, String> cookies = new HashMap<>();
        cookies.put("sessionId", "abc123");

        WebClient.RequestBodySpec result = SpringRequestParameterApplier.applyCookies(requestBodySpec, cookies);

        assertNotNull(result);
        verify(requestBodySpec).cookie("sessionId", "abc123");
    }

    @Test
    void testApplyCookiesWithMultipleCookies() {
        Map<String, String> cookies = new LinkedHashMap<>();
        cookies.put("sessionId", "abc123");
        cookies.put("userId", "user456");
        cookies.put("token", "xyz789");

        WebClient.RequestBodySpec result = SpringRequestParameterApplier.applyCookies(requestBodySpec, cookies);

        assertNotNull(result);
        verify(requestBodySpec).cookie("sessionId", "abc123");
        verify(requestBodySpec).cookie("userId", "user456");
        verify(requestBodySpec).cookie("token", "xyz789");
    }

    @Test
    void testApplyCookiesWithEmptyMap() {
        Map<String, String> cookies = new HashMap<>();

        WebClient.RequestBodySpec result = SpringRequestParameterApplier.applyCookies(requestBodySpec, cookies);

        assertNotNull(result);
        verify(requestBodySpec, never()).cookie(anyString(), anyString());
    }

    @Test
    void testApplyHeadersAndCookiesWithBoth() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        Map<String, String> cookies = new HashMap<>();
        cookies.put("sessionId", "abc123");

        WebClient.RequestBodySpec result = SpringRequestParameterApplier.applyHeadersAndCookies(
                requestBodySpec, headers, cookies);

        assertNotNull(result);
        verify(requestBodySpec).header("Content-Type", "application/json");
        verify(requestBodySpec).header("Cookie", "sessionId=abc123");
        verify(requestBodySpec).cookie("sessionId", "abc123");
    }

    @Test
    void testApplyHeadersAndCookiesWithEmptyCookies() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        Map<String, String> cookies = new HashMap<>();

        WebClient.RequestBodySpec result = SpringRequestParameterApplier.applyHeadersAndCookies(
                requestBodySpec, headers, cookies);

        assertNotNull(result);
        verify(requestBodySpec).header("Content-Type", "application/json");
        verify(requestBodySpec, never()).header(eq("Cookie"), anyString());
        verify(requestBodySpec, never()).cookie(anyString(), anyString());
    }

    @Test
    void testApplyHeadersAndCookiesWithMultipleCookies() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer token");

        Map<String, String> cookies = new LinkedHashMap<>();
        cookies.put("sessionId", "abc123");
        cookies.put("userId", "user456");

        WebClient.RequestBodySpec result = SpringRequestParameterApplier.applyHeadersAndCookies(
                requestBodySpec, headers, cookies);

        assertNotNull(result);
        verify(requestBodySpec).header("Authorization", "Bearer token");
        verify(requestBodySpec).header(eq("Cookie"), contains("sessionId=abc123"));
        verify(requestBodySpec).header(eq("Cookie"), contains("userId=user456"));
        verify(requestBodySpec).cookie("sessionId", "abc123");
        verify(requestBodySpec).cookie("userId", "user456");
    }

    @Test
    void testApplyHeadersWithSpecialCharacters() {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Special", "value with spaces");
        headers.put("X-Symbols", "!@#$%");

        WebClient.RequestBodySpec result = SpringRequestParameterApplier.applyHeaders(requestBodySpec, headers);

        assertNotNull(result);
        verify(requestBodySpec).header("X-Special", "value with spaces");
        verify(requestBodySpec).header("X-Symbols", "!@#$%");
    }

    @Test
    void testApplyCookiesWithSpecialCharacters() {
        Map<String, String> cookies = new HashMap<>();
        cookies.put("name", "John Doe");

        WebClient.RequestBodySpec result = SpringRequestParameterApplier.applyCookies(requestBodySpec, cookies);

        assertNotNull(result);
        verify(requestBodySpec).cookie("name", "John Doe");
    }

    @Test
    void testApplyHeadersWithEmptyValue() {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Empty", "");

        WebClient.RequestBodySpec result = SpringRequestParameterApplier.applyHeaders(requestBodySpec, headers);

        assertNotNull(result);
        verify(requestBodySpec).header("X-Empty", "");
    }

    @Test
    void testApplyCookiesWithEmptyValue() {
        Map<String, String> cookies = new HashMap<>();
        cookies.put("emptyCookie", "");

        WebClient.RequestBodySpec result = SpringRequestParameterApplier.applyCookies(requestBodySpec, cookies);

        assertNotNull(result);
        verify(requestBodySpec).cookie("emptyCookie", "");
    }

    @Test
    void testApplyHeadersWithUnicodeCharacters() {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Unicode", "你好世界");

        WebClient.RequestBodySpec result = SpringRequestParameterApplier.applyHeaders(requestBodySpec, headers);

        assertNotNull(result);
        verify(requestBodySpec).header("X-Unicode", "你好世界");
    }

    @Test
    void testApplyCookiesWithUnicodeCharacters() {
        Map<String, String> cookies = new HashMap<>();
        cookies.put("name", "用户名");

        WebClient.RequestBodySpec result = SpringRequestParameterApplier.applyCookies(requestBodySpec, cookies);

        assertNotNull(result);
        verify(requestBodySpec).cookie("name", "用户名");
    }

    @Test
    void testApplyHeadersReturnsChainableSpec() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Header1", "value1");
        headers.put("Header2", "value2");

        WebClient.RequestBodySpec result = SpringRequestParameterApplier.applyHeaders(requestBodySpec, headers);

        assertSame(requestBodySpec, result);
    }

    @Test
    void testApplyCookiesReturnsChainableSpec() {
        Map<String, String> cookies = new HashMap<>();
        cookies.put("cookie1", "value1");
        cookies.put("cookie2", "value2");

        WebClient.RequestBodySpec result = SpringRequestParameterApplier.applyCookies(requestBodySpec, cookies);

        assertSame(requestBodySpec, result);
    }

    @Test
    void testApplyHeadersAndCookiesReturnsChainableSpec() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Header", "value");

        Map<String, String> cookies = new HashMap<>();
        cookies.put("cookie", "value");

        WebClient.RequestBodySpec result = SpringRequestParameterApplier.applyHeadersAndCookies(
                requestBodySpec, headers, cookies);

        assertSame(requestBodySpec, result);
    }

    @Test
    void testApplyHeadersWithLongValue() {
        Map<String, String> headers = new HashMap<>();
        String longValue = "a".repeat(1000);
        headers.put("X-Long-Header", longValue);

        WebClient.RequestBodySpec result = SpringRequestParameterApplier.applyHeaders(requestBodySpec, headers);

        assertNotNull(result);
        verify(requestBodySpec).header("X-Long-Header", longValue);
    }

    @Test
    void testApplyHeadersAndCookiesWithEmptyHeaders() {
        Map<String, String> headers = new HashMap<>();
        Map<String, String> cookies = new HashMap<>();
        cookies.put("sessionId", "abc123");

        WebClient.RequestBodySpec result = SpringRequestParameterApplier.applyHeadersAndCookies(
                requestBodySpec, headers, cookies);

        assertNotNull(result);
        verify(requestBodySpec).header("Cookie", "sessionId=abc123");
        verify(requestBodySpec).cookie("sessionId", "abc123");
    }

    @Test
    void testApplyHeadersWithNumericValues() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Length", "12345");
        headers.put("X-Rate-Limit", "100");

        WebClient.RequestBodySpec result = SpringRequestParameterApplier.applyHeaders(requestBodySpec, headers);

        assertNotNull(result);
        verify(requestBodySpec).header("Content-Length", "12345");
        verify(requestBodySpec).header("X-Rate-Limit", "100");
    }

    @Test
    void testApplyHeadersAndCookiesBuildsCorrectCookieHeader() {
        Map<String, String> headers = new HashMap<>();
        Map<String, String> cookies = new LinkedHashMap<>();
        cookies.put("cookie1", "value1");
        cookies.put("cookie2", "value2");
        cookies.put("cookie3", "value3");

        SpringRequestParameterApplier.applyHeadersAndCookies(requestBodySpec, headers, cookies);

        verify(requestBodySpec).header(eq("Cookie"), contains("cookie1=value1"));
        verify(requestBodySpec).header(eq("Cookie"), contains("cookie2=value2"));
        verify(requestBodySpec).header(eq("Cookie"), contains("cookie3=value3"));
    }

    @Test
    void testApplyHeadersReflection() throws Exception {
        MockRequestSpec mockSpec = new MockRequestSpec();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer token");

        Object result = SpringRequestParameterApplier.applyHeadersReflection(mockSpec, headers);

        assertNotNull(result);
        assertTrue(mockSpec.headers.containsKey("Content-Type"));
        assertTrue(mockSpec.headers.containsKey("Authorization"));
    }

    @Test
    void testApplyCookiesReflection() throws Exception {
        MockRequestSpec mockSpec = new MockRequestSpec();
        Map<String, String> cookies = new HashMap<>();
        cookies.put("sessionId", "abc123");
        cookies.put("userId", "user456");

        Object result = SpringRequestParameterApplier.applyCookiesReflection(mockSpec, cookies);

        assertNotNull(result);
        assertTrue(mockSpec.cookies.containsKey("sessionId"));
        assertTrue(mockSpec.cookies.containsKey("userId"));
    }

    @Test
    void testApplyHeadersReflectionWithEmptyMap() throws Exception {
        MockRequestSpec mockSpec = new MockRequestSpec();
        Map<String, String> headers = new HashMap<>();

        Object result = SpringRequestParameterApplier.applyHeadersReflection(mockSpec, headers);

        assertNotNull(result);
        assertTrue(mockSpec.headers.isEmpty());
    }

    @Test
    void testApplyCookiesReflectionWithEmptyMap() throws Exception {
        MockRequestSpec mockSpec = new MockRequestSpec();
        Map<String, String> cookies = new HashMap<>();

        Object result = SpringRequestParameterApplier.applyCookiesReflection(mockSpec, cookies);

        assertNotNull(result);
        assertTrue(mockSpec.cookies.isEmpty());
    }

    @Test
    void testApplyHeadersReflectionThrowsExceptionForInvalidObject() {
        Object invalidSpec = new Object(); // Object without header() method
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        assertThrows(Exception.class, () ->
                SpringRequestParameterApplier.applyHeadersReflection(invalidSpec, headers)
        );
    }

    @Test
    void testApplyCookiesReflectionThrowsExceptionForInvalidObject() {
        Object invalidSpec = new Object(); // Object without cookie() method
        Map<String, String> cookies = new HashMap<>();
        cookies.put("sessionId", "abc123");

        assertThrows(Exception.class, () ->
                SpringRequestParameterApplier.applyCookiesReflection(invalidSpec, cookies)
        );
    }

    @Test
    void testApplyHeadersReflectionWithInheritedMethod() throws Exception {
        // Test with a subclass to verify method search in hierarchy
        MockRequestSpecSubclass mockSpec = new MockRequestSpecSubclass();
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Custom", "value");

        Object result = SpringRequestParameterApplier.applyHeadersReflection(mockSpec, headers);

        assertNotNull(result);
        assertTrue(mockSpec.headers.containsKey("X-Custom"));
    }

    @Test
    void testApplyCookiesReflectionWithInheritedMethod() throws Exception {
        // Test with a subclass to verify method search in hierarchy
        MockRequestSpecSubclass mockSpec = new MockRequestSpecSubclass();
        Map<String, String> cookies = new HashMap<>();
        cookies.put("token", "xyz");

        Object result = SpringRequestParameterApplier.applyCookiesReflection(mockSpec, cookies);

        assertNotNull(result);
        assertTrue(mockSpec.cookies.containsKey("token"));
    }

    /**
     * Mock class to simulate RestClient request spec for reflection testing
     */
    static class MockRequestSpec {
        Map<String, String> headers = new HashMap<>();
        Map<String, String> cookies = new HashMap<>();

        public MockRequestSpec header(String name, String... values) {
            if (values != null && values.length > 0) {
                headers.put(name, values[0]);
            }
            return this;
        }

        public MockRequestSpec cookie(String name, String value) {
            cookies.put(name, value);
            return this;
        }
    }

    /**
     * Subclass to test method inheritance in reflection
     */
    static class MockRequestSpecSubclass extends MockRequestSpec {
        // Inherits header() and cookie() methods from parent
    }
}
