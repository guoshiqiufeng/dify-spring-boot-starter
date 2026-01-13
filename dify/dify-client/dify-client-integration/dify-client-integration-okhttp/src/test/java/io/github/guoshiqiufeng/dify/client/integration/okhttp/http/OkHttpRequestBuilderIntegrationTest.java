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
package io.github.guoshiqiufeng.dify.client.integration.okhttp.http;

import io.github.guoshiqiufeng.dify.client.codec.gson.GsonJsonMapper;
import io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper;
import io.github.guoshiqiufeng.dify.client.core.http.HttpClientException;
import io.github.guoshiqiufeng.dify.client.core.http.TypeReference;
import io.github.guoshiqiufeng.dify.client.core.response.HttpResponse;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for OkHttpRequestBuilder execute methods
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/1/13
 */
class OkHttpRequestBuilderIntegrationTest {

    private MockWebServer mockServer;
    private JavaHttpClient client;

    @BeforeEach
    void setUp() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start();
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        client = new JavaHttpClient(
                mockServer.url("/").toString(),
                config,
                new OkHttpClient.Builder(),
                new GsonJsonMapper()
        );
    }

    @AfterEach
    void tearDown() throws IOException {
        mockServer.shutdown();
    }

    /**
     * Helper method to get OkHttpRequestBuilder from DefaultRequestHeadersUriSpec using reflection
     */
    private OkHttpRequestBuilder getBuilder(Object spec) throws Exception {
        Method method = spec.getClass().getDeclaredMethod("getRequestBuilder");
        method.setAccessible(true);
        return (OkHttpRequestBuilder) method.invoke(spec);
    }

    // ========== execute(Class) Tests ==========

    @Test
    void testExecuteWithSuccessfulResponse() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"test\",\"id\":123}")
                .setHeader("Content-Type", "application/json"));

        // Act
        TestResponse result = getBuilder(client.get()
                .uri("/api/test"))
                .execute(TestResponse.class);

        // Assert
        assertNotNull(result);
        assertEquals("test", result.name);
        assertEquals(123, result.id);

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

        // Act
        String result = getBuilder(client.get()
                .uri("/api/string"))
                .execute(String.class);

        // Assert
        assertNotNull(result);
        assertEquals("test string", result);
    }

    @Test
    void testExecuteWithByteArrayResponse() throws Exception {
        // Arrange
        byte[] expectedBytes = "test data".getBytes();
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(new String(expectedBytes))
                .setHeader("Content-Type", "application/octet-stream"));

        // Act
        byte[] result = getBuilder(client.get()
                .uri("/api/bytes"))
                .execute(byte[].class);

        // Assert
        assertNotNull(result);
        assertArrayEquals(expectedBytes, result);
    }

    @Test
    void testExecuteWithEmptyBody() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("")
                .setHeader("Content-Type", "application/json"));

        // Act
        TestResponse result = getBuilder(client.get()
                .uri("/api/empty"))
                .execute(TestResponse.class);

        // Assert
        assertNull(result);
    }

    @Test
    void testExecuteWithNullBody() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(204)
                .setHeader("Content-Length", "0"));

        // Act
        TestResponse result = getBuilder(client.get()
                .uri("/api/null"))
                .execute(TestResponse.class);

        // Assert
        assertNull(result);
    }

    @Test
    void testExecuteWithHttpError() {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("{\"error\":\"Not found\"}"));

        // Act & Assert
        HttpClientException exception = assertThrows(HttpClientException.class, () ->
                getBuilder(client.get()
                        .uri("/api/notfound"))
                        .execute(TestResponse.class)
        );

        assertEquals(404, exception.getStatusCode());
    }

    @Test
    void testExecuteWithIOException() throws IOException {
        // Arrange - shutdown server to cause IOException
        mockServer.shutdown();

        // Act & Assert
        assertThrows(HttpClientException.class, () ->
                getBuilder(client.get()
                        .uri("/api/test"))
                        .execute(TestResponse.class)
        );
    }

    @Test
    void testExecuteWithJsonDeserializationError() {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{invalid json}")
                .setHeader("Content-Type", "application/json"));

        // Act & Assert
        assertThrows(Exception.class, () ->
                getBuilder(client.get()
                        .uri("/api/invalid"))
                        .execute(TestResponse.class)
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

        // Act
        List<TestResponse> result = getBuilder(client.get()
                .uri("/api/list"))
                .execute(new TypeReference<List<TestResponse>>() {
                });

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("test1", result.get(0).name);
        assertEquals("test2", result.get(1).name);
    }

    @Test
    void testExecuteWithTypeReferenceMap() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"key1\":\"value1\",\"key2\":\"value2\"}")
                .setHeader("Content-Type", "application/json"));

        // Act
        Map<String, String> result = getBuilder(client.get()
                .uri("/api/map"))
                .execute(new TypeReference<Map<String, String>>() {
                });

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

        // Act & Assert
        assertThrows(HttpClientException.class, () ->
                getBuilder(client.get()
                        .uri("/api/error"))
                        .execute(new TypeReference<List<TestResponse>>() {
                        })
        );
    }

    // ========== executeForResponse(Class) Tests ==========

    @Test
    void testExecuteForResponseSuccess() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"test\",\"id\":123}")
                .setHeader("Content-Type", "application/json")
                .setHeader("X-Custom-Header", "custom-value"));

        // Act
        HttpResponse<TestResponse> response = getBuilder(client.get()
                .uri("/api/test"))
                .executeForResponse(TestResponse.class);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test", response.getBody().name);
        assertEquals("custom-value", response.getHeaders().getFirst("X-Custom-Header"));
    }

    @Test
    void testExecuteForResponseWithHeaders() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"test\",\"id\":123}")
                .setHeader("Content-Type", "application/json")
                .setHeader("X-Header-1", "value1")
                .setHeader("X-Header-2", "value2"));

        // Act
        HttpResponse<TestResponse> response = getBuilder(client.get()
                .uri("/api/test"))
                .executeForResponse(TestResponse.class);

        // Assert
        assertNotNull(response.getHeaders());
        assertEquals("value1", response.getHeaders().getFirst("X-Header-1"));
        assertEquals("value2", response.getHeaders().getFirst("X-Header-2"));
    }

    @Test
    void testExecuteForResponseWithStatusCode() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(201)
                .setBody("{\"name\":\"created\",\"id\":456}")
                .setHeader("Content-Type", "application/json"));

        // Act
        HttpResponse<TestResponse> response = getBuilder(client.get()
                .uri("/api/create"))
                .executeForResponse(TestResponse.class);

        // Assert
        assertEquals(201, response.getStatusCode());
    }

    @Test
    void testExecuteForResponseWithEmptyBody() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("")
                .setHeader("Content-Type", "application/json"));

        // Act
        HttpResponse<TestResponse> response = getBuilder(client.get()
                .uri("/api/empty"))
                .executeForResponse(TestResponse.class);

        // Assert
        assertNotNull(response);
        assertNull(response.getBody());
    }

    @Test
    void testExecuteForResponseWithError() {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("{\"error\":\"Bad request\"}"));

        // Act & Assert
        assertThrows(HttpClientException.class, () ->
                getBuilder(client.get()
                        .uri("/api/bad"))
                        .executeForResponse(TestResponse.class)
        );
    }

    @Test
    void testExecuteForResponseWithMultipleHeaders() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"test\",\"id\":123}")
                .setHeader("Content-Type", "application/json")
                .addHeader("Set-Cookie", "cookie1=value1")
                .addHeader("Set-Cookie", "cookie2=value2"));

        // Act
        HttpResponse<TestResponse> response = getBuilder(client.get()
                .uri("/api/test"))
                .executeForResponse(TestResponse.class);

        // Assert
        List<String> cookies = response.getHeaders().get("Set-Cookie");
        assertNotNull(cookies);
        assertEquals(2, cookies.size());
    }

    // ========== executeForResponse(TypeReference) Tests ==========

    @Test
    void testExecuteForResponseTypeReferenceSuccess() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("[{\"name\":\"test1\",\"id\":1}]")
                .setHeader("Content-Type", "application/json"));

        // Act
        HttpResponse<List<TestResponse>> response = getBuilder(client.get()
                .uri("/api/list"))
                .executeForResponse(new TypeReference<List<TestResponse>>() {
                });

        // Assert
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testExecuteForResponseTypeReferenceWithHeaders() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"key\":\"value\"}")
                .setHeader("Content-Type", "application/json")
                .setHeader("X-Custom", "test"));

        // Act
        HttpResponse<Map<String, String>> response = getBuilder(client.get()
                .uri("/api/map"))
                .executeForResponse(new TypeReference<Map<String, String>>() {
                });

        // Assert
        assertEquals("test", response.getHeaders().getFirst("X-Custom"));
    }

    @Test
    void testExecuteForResponseTypeReferenceError() {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(403)
                .setBody("{\"error\":\"Forbidden\"}"));

        // Act & Assert
        assertThrows(HttpClientException.class, () ->
                getBuilder(client.get()
                        .uri("/api/forbidden"))
                        .executeForResponse(new TypeReference<List<TestResponse>>() {
                        })
        );
    }

    // ========== executeForStatus() Tests ==========

    @Test
    void testExecuteForStatusSuccess() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"result\":\"ok\"}"));

        // Act
        int statusCode = getBuilder(client.get()
                .uri("/api/status"))
                .executeForStatus();

        // Assert
        assertEquals(200, statusCode);
    }

    @Test
    void testExecuteForStatusWithError() {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("{\"error\":\"Internal error\"}"));

        // Act & Assert
        HttpClientException exception = assertThrows(HttpClientException.class, () ->
                getBuilder(client.get()
                        .uri("/api/error"))
                        .executeForStatus()
        );

        assertEquals(500, exception.getStatusCode());
    }

    @Test
    void testExecuteForStatusWith204() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(204)
                .setHeader("Content-Length", "0"));

        // Act
        int statusCode = getBuilder(client.get()
                .uri("/api/nocontent"))
                .executeForStatus();

        // Assert
        assertEquals(204, statusCode);
    }

    // ========== stream() Tests ==========

    @Test
    void testStreamReturnsFlux() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setBody("data: {\"name\":\"test\",\"id\":1}\n\n")
                .setHeader("Content-Type", "text/event-stream"));

        // Act
        Flux<TestResponse> flux = getBuilder(client.get()
                .uri("/api/stream"))
                .stream(TestResponse.class);

        // Assert
        assertNotNull(flux);
        StepVerifier.create(flux)
                .expectNextMatches(event -> event.id == 1)
                .verifyComplete();
    }

    @Test
    void testStreamWithSSEEvents() throws Exception {
        // Arrange
        String sseData = "data: {\"name\":\"event1\",\"id\":1}\n\n" +
                "data: {\"name\":\"event2\",\"id\":2}\n\n";

        mockServer.enqueue(new MockResponse()
                .setBody(sseData)
                .setHeader("Content-Type", "text/event-stream"));

        // Act
        Flux<TestResponse> flux = getBuilder(client.get()
                .uri("/api/stream"))
                .stream(TestResponse.class);

        // Assert
        StepVerifier.create(flux)
                .expectNextMatches(event -> event.id == 1)
                .expectNextMatches(event -> event.id == 2)
                .verifyComplete();
    }

    @Test
    void testStreamWithError() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("{\"error\":\"Not found\"}"));

        // Act
        Flux<TestResponse> flux = getBuilder(client.get()
                .uri("/api/stream"))
                .stream(TestResponse.class);

        // Assert
        StepVerifier.create(flux)
                .expectError(HttpClientException.class)
                .verify();
    }

    // ========== Test Data Classes ==========

    static class TestResponse {
        String name;
        int id;

        public TestResponse() {
        }

        public TestResponse(String name, int id) {
            this.name = name;
            this.id = id;
        }
    }
}
