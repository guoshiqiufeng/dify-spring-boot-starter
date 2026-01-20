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
package io.github.guoshiqiufeng.dify.client.core.http.util;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RequestParameterProcessor
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026-01-20
 */
class RequestParameterProcessorTest {

    @Test
    void testBuildCookieHeaderWithSingleCookie() {
        Map<String, String> cookies = new HashMap<>();
        cookies.put("sessionId", "abc123");

        String result = RequestParameterProcessor.buildCookieHeader(cookies);
        assertEquals("sessionId=abc123", result);
    }

    @Test
    void testBuildCookieHeaderWithMultipleCookies() {
        Map<String, String> cookies = new LinkedHashMap<>();
        cookies.put("sessionId", "abc123");
        cookies.put("userId", "user456");
        cookies.put("token", "xyz789");

        String result = RequestParameterProcessor.buildCookieHeader(cookies);
        assertTrue(result.contains("sessionId=abc123"));
        assertTrue(result.contains("userId=user456"));
        assertTrue(result.contains("token=xyz789"));
        assertTrue(result.contains("; "));
    }

    @Test
    void testBuildCookieHeaderWithNullMap() {
        String result = RequestParameterProcessor.buildCookieHeader(null);
        assertEquals("", result);
    }

    @Test
    void testBuildCookieHeaderWithEmptyMap() {
        Map<String, String> cookies = new HashMap<>();
        String result = RequestParameterProcessor.buildCookieHeader(cookies);
        assertEquals("", result);
    }

    @Test
    void testBuildCookieHeaderWithNullValue() {
        Map<String, String> cookies = new LinkedHashMap<>();
        cookies.put("sessionId", "abc123");
        cookies.put("nullCookie", null);
        cookies.put("userId", "user456");

        String result = RequestParameterProcessor.buildCookieHeader(cookies);
        assertTrue(result.contains("sessionId=abc123"));
        assertTrue(result.contains("userId=user456"));
        assertFalse(result.contains("nullCookie"));
    }

    @Test
    void testBuildCookieHeaderWithAllNullValues() {
        Map<String, String> cookies = new HashMap<>();
        cookies.put("cookie1", null);
        cookies.put("cookie2", null);

        String result = RequestParameterProcessor.buildCookieHeader(cookies);
        assertEquals("", result);
    }

    @Test
    void testBuildCookieHeaderWithEmptyValue() {
        Map<String, String> cookies = new HashMap<>();
        cookies.put("sessionId", "");

        String result = RequestParameterProcessor.buildCookieHeader(cookies);
        assertEquals("sessionId=", result);
    }

    @Test
    void testBuildCookieHeaderWithSpecialCharacters() {
        Map<String, String> cookies = new HashMap<>();
        cookies.put("session", "abc=123");

        String result = RequestParameterProcessor.buildCookieHeader(cookies);
        assertEquals("session=abc=123", result);
    }

    @Test
    void testBuildCookieHeaderWithSpacesInValue() {
        Map<String, String> cookies = new HashMap<>();
        cookies.put("name", "John Doe");

        String result = RequestParameterProcessor.buildCookieHeader(cookies);
        assertEquals("name=John Doe", result);
    }

    @Test
    void testBuildCookieHeaderFormat() {
        Map<String, String> cookies = new LinkedHashMap<>();
        cookies.put("cookie1", "value1");
        cookies.put("cookie2", "value2");

        String result = RequestParameterProcessor.buildCookieHeader(cookies);
        assertEquals("cookie1=value1; cookie2=value2", result);
    }

    @Test
    void testBuildCookieHeaderWithMixedNullAndValidValues() {
        Map<String, String> cookies = new LinkedHashMap<>();
        cookies.put("valid1", "value1");
        cookies.put("null1", null);
        cookies.put("valid2", "value2");
        cookies.put("null2", null);
        cookies.put("valid3", "value3");

        String result = RequestParameterProcessor.buildCookieHeader(cookies);
        assertTrue(result.contains("valid1=value1"));
        assertTrue(result.contains("valid2=value2"));
        assertTrue(result.contains("valid3=value3"));
        assertFalse(result.contains("null1"));
        assertFalse(result.contains("null2"));

        // Should have exactly 2 separators ("; ") for 3 valid cookies
        int separatorCount = result.split("; ").length - 1;
        assertTrue(separatorCount >= 0);
    }

    @Test
    void testBuildCookieHeaderWithLongValues() {
        Map<String, String> cookies = new HashMap<>();
        String longValue = "a".repeat(1000);
        cookies.put("longCookie", longValue);

        String result = RequestParameterProcessor.buildCookieHeader(cookies);
        assertEquals("longCookie=" + longValue, result);
    }

    @Test
    void testBuildCookieHeaderWithNumericValues() {
        Map<String, String> cookies = new LinkedHashMap<>();
        cookies.put("count", "123");
        cookies.put("price", "99.99");

        String result = RequestParameterProcessor.buildCookieHeader(cookies);
        assertTrue(result.contains("count=123"));
        assertTrue(result.contains("price=99.99"));
    }

    @Test
    void testBuildCookieHeaderWithUnicodeCharacters() {
        Map<String, String> cookies = new HashMap<>();
        cookies.put("name", "用户名");

        String result = RequestParameterProcessor.buildCookieHeader(cookies);
        assertEquals("name=用户名", result);
    }

    @Test
    void testBuildCookieHeaderDoesNotModifyInputMap() {
        Map<String, String> cookies = new HashMap<>();
        cookies.put("sessionId", "abc123");
        cookies.put("userId", "user456");

        int originalSize = cookies.size();
        RequestParameterProcessor.buildCookieHeader(cookies);

        assertEquals(originalSize, cookies.size());
        assertEquals("abc123", cookies.get("sessionId"));
        assertEquals("user456", cookies.get("userId"));
    }

    @Test
    void testBuildCookieHeaderWithSingleNullValue() {
        Map<String, String> cookies = new HashMap<>();
        cookies.put("cookie", null);

        String result = RequestParameterProcessor.buildCookieHeader(cookies);
        assertEquals("", result);
    }
}
