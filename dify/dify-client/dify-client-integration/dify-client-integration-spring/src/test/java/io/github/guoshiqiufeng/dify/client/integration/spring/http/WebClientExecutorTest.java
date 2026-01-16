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
package io.github.guoshiqiufeng.dify.client.integration.spring.http;

import io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for WebClientExecutor
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/1/13
 */
@ExtendWith(MockitoExtension.class)
class WebClientExecutorTest {

    @Mock
    private WebClient webClient;

    @Mock
    private JsonMapper jsonMapper;

    private WebClientExecutor executor;

    @BeforeEach
    void setUp() {
        executor = new WebClientExecutor(webClient, jsonMapper);
    }

//    @Test
//    void testExecute() {
//        URI uri = URI.create("http://localhost:8080/api/test");
//        Map<String, String> headers = new HashMap<>();
//        headers.put("Content-Type", "application/json");
//        Map<String, String> cookies = new HashMap<>();
//
//        String jsonResponse = "{\"name\":\"test\",\"value\":123}";
//        TestDto expectedDto = new TestDto("test", 123);
//        Object get = executor.execute("GET", uri, headers, cookies, null, null, TestDto.class);
//    }

    @Test
    void testConstructorWithTwoParameters() {
        // Act
        WebClientExecutor executor = new WebClientExecutor(webClient, jsonMapper);

        // Assert
        assertNotNull(executor);
    }

    @Test
    void testConstructorWithThreeParameters() {
        // Act
        WebClientExecutor executor = new WebClientExecutor(webClient, jsonMapper, false);

        // Assert
        assertNotNull(executor);
    }

    @Test
    void testIsCompleteJsonWithJsonObject() throws Exception {
        // Arrange
        Method method = WebClientExecutor.class.getDeclaredMethod("isCompleteJson", String.class);
        method.setAccessible(true);

        // Act & Assert
        assertTrue((Boolean) method.invoke(executor, "{\"key\":\"value\"}"));
        assertTrue((Boolean) method.invoke(executor, "{}"));
        assertTrue((Boolean) method.invoke(executor, "  {\"key\":\"value\"}  "));
        assertFalse((Boolean) method.invoke(executor, "{\"key\":\"value\""));
        assertFalse((Boolean) method.invoke(executor, "\"key\":\"value\"}"));
    }

    @Test
    void testIsCompleteJsonWithJsonArray() throws Exception {
        // Arrange
        Method method = WebClientExecutor.class.getDeclaredMethod("isCompleteJson", String.class);
        method.setAccessible(true);

        // Act & Assert
        assertTrue((Boolean) method.invoke(executor, "[1,2,3]"));
        assertTrue((Boolean) method.invoke(executor, "[]"));
        assertTrue((Boolean) method.invoke(executor, "  [1,2,3]  "));
        assertFalse((Boolean) method.invoke(executor, "[1,2,3"));
        assertFalse((Boolean) method.invoke(executor, "1,2,3]"));
    }

    @Test
    void testIsCompleteJsonWithJsonString() throws Exception {
        // Arrange
        Method method = WebClientExecutor.class.getDeclaredMethod("isCompleteJson", String.class);
        method.setAccessible(true);

        // Act & Assert
        assertTrue((Boolean) method.invoke(executor, "\"hello\""));
        assertTrue((Boolean) method.invoke(executor, "\"\""));
        assertFalse((Boolean) method.invoke(executor, "\"hello"));
        assertFalse((Boolean) method.invoke(executor, "hello\""));
    }

    @Test
    void testIsCompleteJsonWithJsonPrimitives() throws Exception {
        // Arrange
        Method method = WebClientExecutor.class.getDeclaredMethod("isCompleteJson", String.class);
        method.setAccessible(true);

        // Act & Assert
        assertTrue((Boolean) method.invoke(executor, "true"));
        assertTrue((Boolean) method.invoke(executor, "false"));
        assertTrue((Boolean) method.invoke(executor, "null"));
        assertTrue((Boolean) method.invoke(executor, "123"));
        assertTrue((Boolean) method.invoke(executor, "123.456"));
        assertTrue((Boolean) method.invoke(executor, "-123.456"));
        assertFalse((Boolean) method.invoke(executor, "invalid"));
    }

    @Test
    void testIsCompleteJsonWithNullOrEmpty() throws Exception {
        // Arrange
        Method method = WebClientExecutor.class.getDeclaredMethod("isCompleteJson", String.class);
        method.setAccessible(true);

        // Act & Assert
        assertFalse((Boolean) method.invoke(executor, (String) null));
        assertFalse((Boolean) method.invoke(executor, ""));
        assertFalse((Boolean) method.invoke(executor, "   "));
    }

    @Test
    void testExtractFilenameFromContentDisposition() throws Exception {
        // Arrange
        Method method = WebClientExecutor.class.getDeclaredMethod("extractFilename", String.class);
        method.setAccessible(true);

        // Act & Assert
        assertEquals("test.txt", method.invoke(executor, "form-data; name=\"file\"; filename=\"test.txt\""));
        assertEquals("document.pdf", method.invoke(executor, "attachment; filename=\"document.pdf\""));
        assertEquals("file", method.invoke(executor, "form-data; name=\"file\""));
        assertEquals("file", method.invoke(executor, (String) null));
        assertEquals("file", method.invoke(executor, ""));
    }

    @Test
    void testExtractFilenameWithSpecialCharacters() throws Exception {
        // Arrange
        Method method = WebClientExecutor.class.getDeclaredMethod("extractFilename", String.class);
        method.setAccessible(true);

        // Act & Assert
        assertEquals("test file.txt", method.invoke(executor, "form-data; name=\"file\"; filename=\"test file.txt\""));
        assertEquals("test-file_123.txt", method.invoke(executor, "form-data; name=\"file\"; filename=\"test-file_123.txt\""));
    }

    @Test
    void testConvertHeadersWithNullHeaders() throws Exception {
        // Arrange
        Method method = WebClientExecutor.class.getDeclaredMethod("convertHeaders", org.springframework.http.HttpHeaders.class);
        method.setAccessible(true);

        // Act
        var result = method.invoke(executor, (org.springframework.http.HttpHeaders) null);

        // Assert
        assertNotNull(result);
    }

    @Test
    void testConvertHeadersWithMultipleValues() throws Exception {
        // Arrange
        Method method = WebClientExecutor.class.getDeclaredMethod("convertHeaders", org.springframework.http.HttpHeaders.class);
        method.setAccessible(true);

        org.springframework.http.HttpHeaders springHeaders = new org.springframework.http.HttpHeaders();
        springHeaders.add("Content-Type", "application/json");
        springHeaders.add("Authorization", "Bearer token");

        // Act
        var result = method.invoke(executor, springHeaders);

        // Assert
        assertNotNull(result);
    }

    @Test
    void testExtractFilenameWithMissingClosingQuote() throws Exception {
        // Arrange
        Method method = WebClientExecutor.class.getDeclaredMethod("extractFilename", String.class);
        method.setAccessible(true);

        // Act & Assert - missing closing quote should return "file"
        assertEquals("file", method.invoke(executor, "filename=\"test.txt"));
    }

    /**
     * Test DTO for deserialization testing
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class TestDto {
        private String name;
        private int value;
    }
}
