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

import io.github.guoshiqiufeng.dify.client.codec.jackson.JacksonJsonMapper;
import io.github.guoshiqiufeng.dify.client.core.http.HttpClientException;
import io.github.guoshiqiufeng.dify.client.core.http.TypeReference;
import io.github.guoshiqiufeng.dify.client.core.response.HttpResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for RestClientExecutor execute methods
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/1/14
 */
class RestClientExecutorIntegrationTest {

    private MockWebServer mockServer;
    private RestClientExecutor executor;

    @BeforeEach
    void setUp() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start();

        RestClient restClient = RestClient.builder()
                .baseUrl(mockServer.url("/").toString())
                .build();

        executor = new RestClientExecutor(restClient, new JacksonJsonMapper());
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
                .setBody("{\"name\":\"test\",\"id\":123}")
                .setHeader("Content-Type", "application/json"));

        Map<String, String> headers = new HashMap<>();
        Map<String, String> cookies = new HashMap<>();

        // Act
        TestResponse result = executor.execute(
                "GET",
                URI.create(mockServer.url("/api/test").toString()),
                headers,
                cookies,
                null,
                TestResponse.class
        );

        // Assert
        assertNotNull(result);
        assertEquals("test", result.getName());
        assertEquals(123, result.getId());

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
                .setHeader("Content-Type", "application/json"));

        Map<String, String> headers = new HashMap<>();
        Map<String, String> cookies = new HashMap<>();

        // Act
        String result = executor.execute(
                "GET",
                URI.create(mockServer.url("/api/string").toString()),
                headers,
                cookies,
                null,
                String.class
        );

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

        Map<String, String> headers = new HashMap<>();
        Map<String, String> cookies = new HashMap<>();

        // Act
        TestResponse result = executor.execute(
                "GET",
                URI.create(mockServer.url("/api/empty").toString()),
                headers,
                cookies,
                null,
                TestResponse.class
        );

        // Assert
        assertNull(result);
    }

    @Test
    void testExecuteWithNullBody() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(204)
                .setHeader("Content-Length", "0"));

        Map<String, String> headers = new HashMap<>();
        Map<String, String> cookies = new HashMap<>();

        // Act
        TestResponse result = executor.execute(
                "GET",
                URI.create(mockServer.url("/api/null").toString()),
                headers,
                cookies,
                null,
                TestResponse.class
        );

        // Assert
        assertNull(result);
    }

    @Test
    void testExecuteWithHttpError() {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("{\"error\":\"Not found\"}"));

        Map<String, String> headers = new HashMap<>();
        Map<String, String> cookies = new HashMap<>();

        // Act & Assert
        HttpClientException exception = assertThrows(HttpClientException.class, () ->
                executor.execute(
                        "GET",
                        URI.create(mockServer.url("/api/notfound").toString()),
                        headers,
                        cookies,
                        null,
                        TestResponse.class
                )
        );

        assertTrue(exception.getMessage().contains("404") || exception.getMessage().contains("Not Found"));
    }

    @Test
    void testExecuteWithIOException() throws IOException {
        // Arrange - shutdown server to cause IOException
        mockServer.shutdown();

        Map<String, String> headers = new HashMap<>();
        Map<String, String> cookies = new HashMap<>();

        // Act & Assert
        assertThrows(HttpClientException.class, () ->
                executor.execute(
                        "GET",
                        URI.create(mockServer.url("/api/test").toString()),
                        headers,
                        cookies,
                        null,
                        TestResponse.class
                )
        );
    }

    @Test
    void testExecuteWithJsonDeserializationError() {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{invalid json}")
                .setHeader("Content-Type", "application/json"));

        Map<String, String> headers = new HashMap<>();
        Map<String, String> cookies = new HashMap<>();

        // Act & Assert
        assertThrows(Exception.class, () ->
                executor.execute(
                        "GET",
                        URI.create(mockServer.url("/api/invalid").toString()),
                        headers,
                        cookies,
                        null,
                        TestResponse.class
                )
        );
    }

    // ========== execute(TypeReference) Tests ==========

    @Test
    void testExecuteWithTypeReferenceList() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("[{\"name\":\"test1\",\"id\":1},{\"name\":\"test2\",\"id\":2}]")
                .setHeader("Content-Type", "application/json"));

        Map<String, String> headers = new HashMap<>();
        Map<String, String> cookies = new HashMap<>();

        // Act
        List<TestResponse> result = executor.execute(
                "GET",
                URI.create(mockServer.url("/api/list").toString()),
                headers,
                cookies,
                null,
                new TypeReference<List<TestResponse>>() {}
        );

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

        Map<String, String> headers = new HashMap<>();
        Map<String, String> cookies = new HashMap<>();

        // Act
        Map<String, String> result = executor.execute(
                "GET",
                URI.create(mockServer.url("/api/map").toString()),
                headers,
                cookies,
                null,
                new TypeReference<Map<String, String>>() {}
        );

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("value1", result.get("key1"));
        assertEquals("value2", result.get("key2"));
    }

    @Test
    void testExecuteWithTypeReferenceError() {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("{\"error\":\"Server error\"}"));

        Map<String, String> headers = new HashMap<>();
        Map<String, String> cookies = new HashMap<>();

        // Act & Assert
        assertThrows(HttpClientException.class, () ->
                executor.execute(
                        "GET",
                        URI.create(mockServer.url("/api/error").toString()),
                        headers,
                        cookies,
                        null,
                        new TypeReference<List<TestResponse>>() {}
                )
        );
    }

    // ========== executeForEntity(Class) Tests ==========

    @Test
    void testExecuteForEntitySuccess() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"test\",\"id\":123}")
                .setHeader("Content-Type", "application/json")
                .setHeader("X-Custom-Header", "custom-value"));

        Map<String, String> headers = new HashMap<>();
        Map<String, String> cookies = new HashMap<>();

        // Act
        HttpResponse<TestResponse> response = executor.executeForEntity(
                "GET",
                URI.create(mockServer.url("/api/test").toString()),
                headers,
                cookies,
                null,
                TestResponse.class
        );

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
                .setBody("{\"name\":\"test\",\"id\":123}")
                .setHeader("Content-Type", "application/json")
                .setHeader("X-Header-1", "value1")
                .setHeader("X-Header-2", "value2"));

        Map<String, String> headers = new HashMap<>();
        Map<String, String> cookies = new HashMap<>();

        // Act
        HttpResponse<TestResponse> response = executor.executeForEntity(
                "GET",
                URI.create(mockServer.url("/api/test").toString()),
                headers,
                cookies,
                null,
                TestResponse.class
        );

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
                .setBody("{\"name\":\"created\",\"id\":456}")
                .setHeader("Content-Type", "application/json"));

        Map<String, String> headers = new HashMap<>();
        Map<String, String> cookies = new HashMap<>();

        // Act
        HttpResponse<TestResponse> response = executor.executeForEntity(
                "POST",
                URI.create(mockServer.url("/api/create").toString()),
                headers,
                cookies,
                null,
                TestResponse.class
        );

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

        Map<String, String> headers = new HashMap<>();
        Map<String, String> cookies = new HashMap<>();

        // Act
        HttpResponse<TestResponse> response = executor.executeForEntity(
                "GET",
                URI.create(mockServer.url("/api/empty").toString()),
                headers,
                cookies,
                null,
                TestResponse.class
        );

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

        Map<String, String> headers = new HashMap<>();
        Map<String, String> cookies = new HashMap<>();

        // Act
        HttpResponse<TestResponse> response = executor.executeForEntity(
                "GET",
                URI.create(mockServer.url("/api/bad").toString()),
                headers,
                cookies,
                null,
                TestResponse.class
        );

        // Assert - executeForEntity returns response even for errors
        assertNotNull(response);
        assertEquals(400, response.getStatusCode());
    }

    @Test
    void testExecuteForEntityWithMultipleHeaders() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"test\",\"id\":123}")
                .setHeader("Content-Type", "application/json")
                .addHeader("Set-Cookie", "cookie1=value1")
                .addHeader("Set-Cookie", "cookie2=value2"));

        Map<String, String> headers = new HashMap<>();
        Map<String, String> cookies = new HashMap<>();

        // Act
        HttpResponse<TestResponse> response = executor.executeForEntity(
                "GET",
                URI.create(mockServer.url("/api/test").toString()),
                headers,
                cookies,
                null,
                TestResponse.class
        );

        // Assert
        List<String> cookieHeaders = response.getHeaders().get("Set-Cookie");
        assertNotNull(cookieHeaders);
        assertEquals(2, cookieHeaders.size());
    }

    // ========== executeForEntity(TypeReference) Tests ==========

    @Test
    void testExecuteForEntityTypeReferenceSuccess() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("[{\"name\":\"test1\",\"id\":1}]")
                .setHeader("Content-Type", "application/json"));

        Map<String, String> headers = new HashMap<>();
        Map<String, String> cookies = new HashMap<>();

        // Act
        HttpResponse<List<TestResponse>> response = executor.executeForEntity(
                "GET",
                URI.create(mockServer.url("/api/list").toString()),
                headers,
                cookies,
                null,
                new TypeReference<List<TestResponse>>() {}
        );

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

        Map<String, String> headers = new HashMap<>();
        Map<String, String> cookies = new HashMap<>();

        // Act
        HttpResponse<Map<String, String>> response = executor.executeForEntity(
                "GET",
                URI.create(mockServer.url("/api/map").toString()),
                headers,
                cookies,
                null,
                new TypeReference<Map<String, String>>() {}
        );

        // Assert
        assertEquals("test", response.getHeaders().getFirst("X-Custom"));
    }

    @Test
    void testExecuteForEntityTypeReferenceError() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(403)
                .setBody("{\"error\":\"Forbidden\"}"));

        Map<String, String> headers = new HashMap<>();
        Map<String, String> cookies = new HashMap<>();

        // Act
        HttpResponse<List<TestResponse>> response = executor.executeForEntity(
                "GET",
                URI.create(mockServer.url("/api/forbidden").toString()),
                headers,
                cookies,
                null,
                new TypeReference<List<TestResponse>>() {}
        );

        // Assert - executeForEntity returns response even for errors
        assertNotNull(response);
        assertEquals(403, response.getStatusCode());
    }

    // ========== Request Building Tests ==========

    @Test
    void testRequestWithHeaders() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"test\",\"id\":123}")
                .setHeader("Content-Type", "application/json"));

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer token123");
        headers.put("X-Custom-Header", "custom-value");
        Map<String, String> cookies = new HashMap<>();

        // Act
        TestResponse result = executor.execute(
                "GET",
                URI.create(mockServer.url("/api/test").toString()),
                headers,
                cookies,
                null,
                TestResponse.class
        );

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
                .setBody("{\"name\":\"test\",\"id\":123}")
                .setHeader("Content-Type", "application/json"));

        Map<String, String> headers = new HashMap<>();
        Map<String, String> cookies = new HashMap<>();
        cookies.put("sessionId", "abc123");
        cookies.put("userId", "user456");

        // Act
        TestResponse result = executor.execute(
                "GET",
                URI.create(mockServer.url("/api/test").toString()),
                headers,
                cookies,
                null,
                TestResponse.class
        );

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
                .setBody("{\"name\":\"response\",\"id\":999}")
                .setHeader("Content-Type", "application/json"));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", "test");
        requestBody.put("value", 123);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        Map<String, String> cookies = new HashMap<>();

        // Act
        TestResponse result = executor.execute(
                "POST",
                URI.create(mockServer.url("/api/create").toString()),
                headers,
                cookies,
                requestBody,
                TestResponse.class
        );

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
                .setBody("{\"name\":\"created\",\"id\":777}")
                .setHeader("Content-Type", "application/json"));

        Map<String, String> headers = new HashMap<>();
        Map<String, String> cookies = new HashMap<>();

        // Act
        TestResponse result = executor.execute(
                "POST",
                URI.create(mockServer.url("/api/create").toString()),
                headers,
                cookies,
                null,
                TestResponse.class
        );

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
                .setBody("{\"name\":\"updated\",\"id\":666}")
                .setHeader("Content-Type", "application/json"));

        Map<String, String> headers = new HashMap<>();
        Map<String, String> cookies = new HashMap<>();

        // Act
        TestResponse result = executor.execute(
                "PUT",
                URI.create(mockServer.url("/api/update").toString()),
                headers,
                cookies,
                null,
                TestResponse.class
        );

        // Assert
        assertNotNull(result);
        RecordedRequest request = mockServer.takeRequest();
        assertEquals("PUT", request.getMethod());
    }

    @Test
    void testHandleErrorWithNullBody() {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setHeader("Content-Length", "0"));

        Map<String, String> headers = new HashMap<>();
        Map<String, String> cookies = new HashMap<>();

        // Act & Assert
        HttpClientException exception = assertThrows(HttpClientException.class, () ->
                executor.execute(
                        "GET",
                        URI.create(mockServer.url("/api/error").toString()),
                        headers,
                        cookies,
                        null,
                        TestResponse.class
                )
        );

        assertTrue(exception.getMessage().contains("500") || exception.getMessage().contains("Internal Server Error"));
    }

    // ========== Test Data Classes ==========

    public static class TestResponse {
        private String name;
        private int id;

        public TestResponse() {
        }

        public TestResponse(String name, int id) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
