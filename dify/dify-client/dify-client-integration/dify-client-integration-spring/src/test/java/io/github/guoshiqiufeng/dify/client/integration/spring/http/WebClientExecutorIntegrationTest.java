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

import io.github.guoshiqiufeng.dify.client.codec.gson.GsonJsonMapper;
import io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper;
import io.github.guoshiqiufeng.dify.client.core.http.HttpClientException;
import io.github.guoshiqiufeng.dify.client.core.http.TypeReference;
import io.github.guoshiqiufeng.dify.client.core.response.HttpResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for WebClientExecutor
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/1/13
 */
@ExtendWith(MockitoExtension.class)
class WebClientExecutorIntegrationTest {

    private MockWebServer mockServer;
    private WebClientExecutor executor;

    @BeforeEach
    void setUp() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start();

        JsonMapper jsonMapper = new GsonJsonMapper();
        WebClient webClient = WebClient.builder()
                .baseUrl(mockServer.url("/").toString())
                .build();

        executor = new WebClientExecutor(webClient, jsonMapper);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockServer.shutdown();
    }

    // ========== execute(Class) Tests ==========

    @Test
    void testExecuteWithSuccessfulResponse() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"test\",\"value\":123}")
                .setHeader("Content-Type", "application/json"));

        URI uri = mockServer.url("/api/test").uri();

        // Act
        TestDto result = executor.execute("GET", uri, new HashMap<>(), new HashMap<>(),
                new HashMap<>(), null, TestDto.class);

        // Assert
        assertNotNull(result);
        assertEquals("test", result.getName());
        assertEquals(123, result.getValue());

        RecordedRequest request = mockServer.takeRequest();
        assertEquals("GET", request.getMethod());
        assertEquals("/api/test", request.getPath());
    }

    @Test
    void testExecuteWithStringResponse() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("test string")
                .setHeader("Content-Type", "text/plain"));

        URI uri = mockServer.url("/api/string").uri();

        // Act
        String result = executor.execute("GET", uri, new HashMap<>(), new HashMap<>(),
                new HashMap<>(), null, String.class);

        // Assert
        assertNotNull(result);
        assertEquals("test string", result);
    }

    @Test
    void testExecuteWithEmptyBody() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("")
                .setHeader("Content-Type", "application/json"));

        URI uri = mockServer.url("/api/empty").uri();

        // Act
        TestDto result = executor.execute("GET", uri, new HashMap<>(), new HashMap<>(),
                new HashMap<>(), null, TestDto.class);

        // Assert
        assertNull(result);
    }

    @Test
    void testExecuteWithNullBody() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(204)
                .setHeader("Content-Length", "0"));

        URI uri = mockServer.url("/api/null").uri();

        // Act
        TestDto result = executor.execute("GET", uri, new HashMap<>(), new HashMap<>(),
                new HashMap<>(), null, TestDto.class);

        // Assert
        assertNull(result);
    }

    @Test
    void testExecuteWithJsonDeserializationError() {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{invalid json}")
                .setHeader("Content-Type", "application/json"));

        URI uri = mockServer.url("/api/invalid").uri();

        // Act & Assert
        assertThrows(Exception.class, () ->
                executor.execute("GET", uri, new HashMap<>(), new HashMap<>(),
                        new HashMap<>(), null, TestDto.class)
        );
    }

    // ========== execute(TypeReference) Tests ==========

    @Test
    void testExecuteWithTypeReferenceList() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("[{\"name\":\"test1\",\"value\":1},{\"name\":\"test2\",\"value\":2}]")
                .setHeader("Content-Type", "application/json"));

        URI uri = mockServer.url("/api/list").uri();

        // Act
        List<TestDto> result = executor.execute("GET", uri, new HashMap<>(), new HashMap<>(),
                new HashMap<>(), null, new TypeReference<List<TestDto>>() {});

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("test1", result.get(0).getName());
        assertEquals("test2", result.get(1).getName());
    }

    @Test
    void testExecuteWithTypeReferenceMap() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"key1\":\"value1\",\"key2\":\"value2\"}")
                .setHeader("Content-Type", "application/json"));

        URI uri = mockServer.url("/api/map").uri();

        // Act
        Map<String, String> result = executor.execute("GET", uri, new HashMap<>(), new HashMap<>(),
                new HashMap<>(), null, new TypeReference<Map<String, String>>() {});

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("value1", result.get("key1"));
        assertEquals("value2", result.get("key2"));
    }

    // ========== executeForEntity(Class) Tests ==========

    @Test
    void testExecuteForEntitySuccess() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"test\",\"value\":123}")
                .setHeader("Content-Type", "application/json")
                .setHeader("X-Custom-Header", "custom-value"));

        URI uri = mockServer.url("/api/test").uri();

        // Act
        HttpResponse<TestDto> response = executor.executeForEntity("GET", uri, new HashMap<>(),
                new HashMap<>(), new HashMap<>(), null, TestDto.class);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test", response.getBody().getName());
        assertEquals("custom-value", response.getHeaders().getFirst("X-Custom-Header"));
    }

    @Test
    void testExecuteForEntityWithHeaders() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"test\",\"value\":123}")
                .setHeader("Content-Type", "application/json")
                .setHeader("X-Header-1", "value1")
                .setHeader("X-Header-2", "value2"));

        URI uri = mockServer.url("/api/test").uri();

        // Act
        HttpResponse<TestDto> response = executor.executeForEntity("GET", uri, new HashMap<>(),
                new HashMap<>(), new HashMap<>(), null, TestDto.class);

        // Assert
        assertNotNull(response.getHeaders());
        assertEquals("value1", response.getHeaders().getFirst("X-Header-1"));
        assertEquals("value2", response.getHeaders().getFirst("X-Header-2"));
    }

    @Test
    void testExecuteForEntityWithStatusCode() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(201)
                .setBody("{\"name\":\"created\",\"value\":456}")
                .setHeader("Content-Type", "application/json"));

        URI uri = mockServer.url("/api/create").uri();

        // Act
        HttpResponse<TestDto> response = executor.executeForEntity("POST", uri, new HashMap<>(),
                new HashMap<>(), new HashMap<>(), null, TestDto.class);

        // Assert
        assertEquals(201, response.getStatusCode());
    }

    @Test
    void testExecuteForEntityWithEmptyBody() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("")
                .setHeader("Content-Type", "application/json"));

        URI uri = mockServer.url("/api/empty").uri();

        // Act
        HttpResponse<TestDto> response = executor.executeForEntity("GET", uri, new HashMap<>(),
                new HashMap<>(), new HashMap<>(), null, TestDto.class);

        // Assert
        assertNotNull(response);
        assertNull(response.getBody());
    }

    @Test
    void testExecuteForEntityWithError() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("{\"error\":\"Bad request\"}"));

        URI uri = mockServer.url("/api/bad").uri();

        // Act
        HttpResponse<TestDto> response = executor.executeForEntity("GET", uri, new HashMap<>(),
                new HashMap<>(), new HashMap<>(), null, TestDto.class);

        // Assert
        assertNotNull(response);
        assertEquals(400, response.getStatusCode());
    }

    @Test
    void testExecuteForEntityWithMultipleHeaders() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"test\",\"value\":123}")
                .setHeader("Content-Type", "application/json")
                .addHeader("Set-Cookie", "cookie1=value1")
                .addHeader("Set-Cookie", "cookie2=value2"));

        URI uri = mockServer.url("/api/test").uri();

        // Act
        HttpResponse<TestDto> response = executor.executeForEntity("GET", uri, new HashMap<>(),
                new HashMap<>(), new HashMap<>(), null, TestDto.class);

        // Assert
        List<String> cookies = response.getHeaders().get("Set-Cookie");
        assertNotNull(cookies);
        assertEquals(2, cookies.size());
    }

    // ========== executeForEntity(TypeReference) Tests ==========

    @Test
    void testExecuteForEntityTypeReferenceSuccess() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("[{\"name\":\"test1\",\"value\":1}]")
                .setHeader("Content-Type", "application/json"));

        URI uri = mockServer.url("/api/list").uri();

        // Act
        HttpResponse<List<TestDto>> response = executor.executeForEntity("GET", uri, new HashMap<>(),
                new HashMap<>(), new HashMap<>(), null, new TypeReference<List<TestDto>>() {});

        // Assert
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testExecuteForEntityTypeReferenceWithHeaders() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"key\":\"value\"}")
                .setHeader("Content-Type", "application/json")
                .setHeader("X-Custom", "test"));

        URI uri = mockServer.url("/api/map").uri();

        // Act
        HttpResponse<Map<String, String>> response = executor.executeForEntity("GET", uri, new HashMap<>(),
                new HashMap<>(), new HashMap<>(), null, new TypeReference<Map<String, String>>() {});

        // Assert
        assertEquals("test", response.getHeaders().getFirst("X-Custom"));
    }

    @Test
    void testExecuteForEntityTypeReferenceError() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(403)
                .setBody("{\"error\":\"Forbidden\"}"));

        URI uri = mockServer.url("/api/forbidden").uri();

        // Act
        HttpResponse<List<TestDto>> response = executor.executeForEntity("GET", uri, new HashMap<>(),
                new HashMap<>(), new HashMap<>(), null, new TypeReference<List<TestDto>>() {});

        // Assert
        assertNotNull(response);
        assertEquals(403, response.getStatusCode());
    }

    // ========== executeStream() Tests ==========

    @Test
    void testExecuteStreamReturnsFlux() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setBody("data: {\"name\":\"test\",\"value\":1}\n\n")
                .setHeader("Content-Type", "text/event-stream"));

        URI uri = mockServer.url("/api/stream").uri();

        // Act
        Flux<TestDto> flux = executor.executeStream("GET", uri, new HashMap<>(),
                new HashMap<>(), new HashMap<>(), null, TestDto.class);

        // Assert
        assertNotNull(flux);
        StepVerifier.create(flux)
                .expectNextMatches(event -> event.getValue() == 1)
                .verifyComplete();
    }

    @Test
    void testExecuteStreamWithSSEEvents() throws Exception {
        // Arrange
        String sseData = "data: {\"name\":\"event1\",\"value\":1}\n\n" +
                "data: {\"name\":\"event2\",\"value\":2}\n\n";

        mockServer.enqueue(new MockResponse()
                .setBody(sseData)
                .setHeader("Content-Type", "text/event-stream"));

        URI uri = mockServer.url("/api/stream").uri();

        // Act
        Flux<TestDto> flux = executor.executeStream("GET", uri, new HashMap<>(),
                new HashMap<>(), new HashMap<>(), null, TestDto.class);

        // Assert
        StepVerifier.create(flux)
                .expectNextMatches(event -> event.getValue() == 1)
                .expectNextMatches(event -> event.getValue() == 2)
                .verifyComplete();
    }

    @Test
    void testExecuteStreamWithTypeReference() throws Exception {
        // Arrange
        String sseData = "data: {\"key\":\"value1\"}\n\n" +
                "data: {\"key\":\"value2\"}\n\n";

        mockServer.enqueue(new MockResponse()
                .setBody(sseData)
                .setHeader("Content-Type", "text/event-stream"));

        URI uri = mockServer.url("/api/stream").uri();

        // Act
        Flux<Map<String, String>> flux = executor.executeStream("GET", uri, new HashMap<>(),
                new HashMap<>(), new HashMap<>(), null, new TypeReference<Map<String, String>>() {});

        // Assert
        StepVerifier.create(flux)
                .expectNextMatches(map -> "value1".equals(map.get("key")))
                .expectNextMatches(map -> "value2".equals(map.get("key")))
                .verifyComplete();
    }

    // ========== Request Building Tests ==========

    @Test
    void testRequestWithHeaders() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"test\",\"value\":123}")
                .setHeader("Content-Type", "application/json"));

        URI uri = mockServer.url("/api/test").uri();
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer token123");
        headers.put("X-Custom-Header", "custom-value");

        // Act
        TestDto result = executor.execute("GET", uri, headers, new HashMap<>(),
                new HashMap<>(), null, TestDto.class);

        // Assert
        assertNotNull(result);
        RecordedRequest request = mockServer.takeRequest();
        assertEquals("Bearer token123", request.getHeader("Authorization"));
        assertEquals("custom-value", request.getHeader("X-Custom-Header"));
    }

    @Test
    void testRequestWithCookies() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"test\",\"value\":123}")
                .setHeader("Content-Type", "application/json"));

        URI uri = mockServer.url("/api/test").uri();
        Map<String, String> cookies = new HashMap<>();
        cookies.put("sessionId", "abc123");
        cookies.put("userId", "user456");

        // Act
        TestDto result = executor.execute("GET", uri, new HashMap<>(), cookies,
                new HashMap<>(), null, TestDto.class);

        // Assert
        assertNotNull(result);
        RecordedRequest request = mockServer.takeRequest();
        String cookieHeader = request.getHeader("Cookie");
        assertNotNull(cookieHeader);
        assertTrue(cookieHeader.contains("sessionId=abc123"));
        assertTrue(cookieHeader.contains("userId=user456"));
    }

    @Test
    void testRequestWithJsonBody() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"response\",\"value\":999}")
                .setHeader("Content-Type", "application/json"));

        URI uri = mockServer.url("/api/create").uri();
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", "test");
        requestBody.put("value", 123);

        // Act
        TestDto result = executor.execute("POST", uri, new HashMap<>(), new HashMap<>(),
                new HashMap<>(), requestBody, TestDto.class);

        // Assert
        assertNotNull(result);
        RecordedRequest request = mockServer.takeRequest();
        assertEquals("POST", request.getMethod());
        String body = request.getBody().readUtf8();
        assertTrue(body.contains("\"name\""));
        assertTrue(body.contains("\"test\""));
    }

    @Test
    void testRequestWithEmptyBodyForPost() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"created\",\"value\":777}")
                .setHeader("Content-Type", "application/json"));

        URI uri = mockServer.url("/api/create").uri();

        // Act
        TestDto result = executor.execute("POST", uri, new HashMap<>(), new HashMap<>(),
                new HashMap<>(), null, TestDto.class);

        // Assert
        assertNotNull(result);
        RecordedRequest request = mockServer.takeRequest();
        assertEquals("POST", request.getMethod());
    }

    @Test
    void testRequestWithEmptyBodyForPut() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"updated\",\"value\":666}")
                .setHeader("Content-Type", "application/json"));

        URI uri = mockServer.url("/api/update").uri();

        // Act
        TestDto result = executor.execute("PUT", uri, new HashMap<>(), new HashMap<>(),
                new HashMap<>(), null, TestDto.class);

        // Assert
        assertNotNull(result);
        RecordedRequest request = mockServer.takeRequest();
        assertEquals("PUT", request.getMethod());
    }

    // ========== Helper Method Tests ==========

    @Test
    void testConstructorWithTwoParameters() {
        // Arrange
        WebClient webClient = WebClient.builder().build();
        JsonMapper jsonMapper = new GsonJsonMapper();

        // Act
        WebClientExecutor executor = new WebClientExecutor(webClient, jsonMapper);

        // Assert
        assertNotNull(executor);
    }

    @Test
    void testConstructorWithThreeParameters() {
        // Arrange
        WebClient webClient = WebClient.builder().build();
        JsonMapper jsonMapper = new GsonJsonMapper();

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
    static class TestDto {
        private String name;
        private int value;
    }
}
