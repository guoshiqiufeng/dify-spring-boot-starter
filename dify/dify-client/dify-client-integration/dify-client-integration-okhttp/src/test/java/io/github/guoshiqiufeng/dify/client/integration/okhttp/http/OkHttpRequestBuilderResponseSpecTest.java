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
import io.github.guoshiqiufeng.dify.client.core.http.HttpClientException;
import io.github.guoshiqiufeng.dify.client.core.http.ResponseErrorHandler;
import io.github.guoshiqiufeng.dify.client.core.http.TypeReference;
import io.github.guoshiqiufeng.dify.client.core.response.HttpResponse;
import io.github.guoshiqiufeng.dify.client.core.web.client.ResponseSpec;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import lombok.Data;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for OkHttpRequestBuilder.OkHttpResponseSpec
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/1/14
 */
class OkHttpRequestBuilderResponseSpecTest {

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

    // ========== onStatus() Tests ==========

    @Test
    void testOnStatusReturnsResponseSpec() {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"test\",\"id\":123}")
                .setHeader("Content-Type", "application/json"));

        // Act
        ResponseSpec spec = client.get()
                .uri("/api/test")
                .retrieve()
                .onStatus(ResponseErrorHandler.onStatus(status -> status >= 400, response -> {
                    throw new RuntimeException("Error");
                }));

        // Assert
        assertNotNull(spec);
    }

    @Test
    void testOnStatusMethodChaining() {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"test\",\"id\":123}")
                .setHeader("Content-Type", "application/json"));

        // Act
        ResponseSpec spec = client.get()
                .uri("/api/test")
                .retrieve()
                .onStatus(ResponseErrorHandler.onStatus(status -> status == 404, response -> {
                    throw new RuntimeException("Not found");
                }))
                .onStatus(ResponseErrorHandler.onStatus(status -> status == 500, response -> {
                    throw new RuntimeException("Server error");
                }));

        // Assert
        assertNotNull(spec);
    }

    // ========== body(Class) Tests ==========

    @Test
    void testBodyWithClassSuccess() {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"test\",\"id\":123}")
                .setHeader("Content-Type", "application/json"));

        // Act
        TestResponse result = client.get()
                .uri("/api/test")
                .retrieve()
                .body(TestResponse.class);

        // Assert
        assertNotNull(result);
        assertEquals("test", result.name);
        assertEquals(123, result.id);
    }

    @Test
    void testBodyWithClassAndErrorHandler() {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("{\"error\":\"Not found\"}"));

        AtomicBoolean handlerCalled = new AtomicBoolean(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                client.get()
                        .uri("/api/notfound")
                        .retrieve()
                        .onStatus(ResponseErrorHandler.onStatus(status -> status == 404, response -> {
                            handlerCalled.set(true);
                            throw new RuntimeException("Custom 404 error");
                        }))
                        .body(TestResponse.class)
        );

        assertTrue(handlerCalled.get());
    }

    // ========== body(TypeReference) Tests ==========

    @Test
    void testBodyWithTypeReferenceSuccess() {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("[{\"name\":\"test1\",\"id\":1},{\"name\":\"test2\",\"id\":2}]")
                .setHeader("Content-Type", "application/json"));

        // Act
        List<TestResponse> result = client.get()
                .uri("/api/list")
                .retrieve()
                .body(new TypeReference<List<TestResponse>>() {
                });

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("test1", result.get(0).name);
        assertEquals("test2", result.get(1).name);
    }

    @Test
    void testBodyWithTypeReferenceAndErrorHandler() {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("{\"error\":\"Server error\"}"));

        AtomicBoolean handlerCalled = new AtomicBoolean(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                client.get()
                        .uri("/api/error")
                        .retrieve()
                        .onStatus(ResponseErrorHandler.onStatus(status -> status >= 500, response -> {
                            handlerCalled.set(true);
                            throw new RuntimeException("Server error");
                        }))
                        .body(new TypeReference<DemoResponse<List<TestResponse>>>() {
                        })
        );

        assertTrue(handlerCalled.get());
    }

    // ========== toEntity(Class) Tests ==========

    @Test
    void testToEntityWithClassSuccess() {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"test\",\"id\":123}")
                .setHeader("Content-Type", "application/json")
                .setHeader("X-Custom-Header", "custom-value"));

        // Act
        HttpResponse<TestResponse> response = client.get()
                .uri("/api/test")
                .retrieve()
                .toEntity(TestResponse.class);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test", response.getBody().name);
        assertEquals("custom-value", response.getHeaders().getFirst("X-Custom-Header"));
    }

    @Test
    void testToEntityWithClassAndErrorHandler() {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("{\"error\":\"Bad request\"}"));

        AtomicBoolean handlerCalled = new AtomicBoolean(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                client.get()
                        .uri("/api/bad")
                        .retrieve()
                        .onStatus(ResponseErrorHandler.onStatus(status -> status == 400, response -> {
                            handlerCalled.set(true);
                            throw new RuntimeException("Bad request");
                        }))
                        .toEntity(TestResponse.class)
        );

        assertTrue(handlerCalled.get());
    }

    @Test
    void testToEntityWithClassIOException() throws IOException {
        // Arrange - shutdown server to cause IOException
        mockServer.shutdown();

        // Act & Assert
        assertThrows(HttpClientException.class, () ->
                client.get()
                        .uri("/api/test")
                        .retrieve()
                        .toEntity(TestResponse.class)
        );
    }

    // ========== toEntity(TypeReference) Tests ==========

    @Test
    void testToEntityWithTypeReferenceSuccess() {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"key1\":\"value1\",\"key2\":\"value2\"}")
                .setHeader("Content-Type", "application/json"));

        // Act
        HttpResponse<Map<String, String>> response = client.get()
                .uri("/api/map")
                .retrieve()
                .toEntity(new TypeReference<Map<String, String>>() {
                });

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("value1", response.getBody().get("key1"));
    }

    @Test
    void testToEntityWithTypeReferenceAndErrorHandler() {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(403)
                .setBody("{\"error\":\"Forbidden\"}"));

        AtomicBoolean handlerCalled = new AtomicBoolean(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                client.get()
                        .uri("/api/forbidden")
                        .retrieve()
                        .onStatus(ResponseErrorHandler.onStatus(status -> status == 403, response -> {
                            handlerCalled.set(true);
                            throw new RuntimeException("Forbidden");
                        }))
                        .toEntity(new TypeReference<Map<String, String>>() {
                        })
        );

        assertTrue(handlerCalled.get());
    }

    @Test
    void testToEntityWithTypeReferenceIOException() throws IOException {
        // Arrange - shutdown server to cause IOException
        mockServer.shutdown();

        // Act & Assert
        assertThrows(HttpClientException.class, () ->
                client.get()
                        .uri("/api/test")
                        .retrieve()
                        .toEntity(new TypeReference<Map<String, String>>() {
                        })
        );
    }

    // ========== toBodilessEntity() Tests ==========

    @Test
    void testToBodilessEntitySuccess() {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(204)
                .setHeader("Content-Length", "0"));

        // Act
        HttpResponse<Void> response = client.delete()
                .uri("/api/delete")
                .retrieve()
                .toBodilessEntity();

        // Assert
        assertNotNull(response);
        assertEquals(204, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testToBodilessEntityWithErrorHandler() {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("{\"error\":\"Not found\"}"));

        AtomicBoolean handlerCalled = new AtomicBoolean(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                client.delete()
                        .uri("/api/notfound")
                        .retrieve()
                        .onStatus(ResponseErrorHandler.onStatus(status -> status == 404, response -> {
                            handlerCalled.set(true);
                            throw new RuntimeException("Not found");
                        }))
                        .toBodilessEntity()
        );

        assertTrue(handlerCalled.get());
    }

    // ========== bodyToFlux(Class) Tests ==========

    @Test
    void testBodyToFluxWithClassSuccess() {
        // Arrange
        String sseData = "data: {\"name\":\"event1\",\"id\":1}\n\n" +
                "data: {\"name\":\"event2\",\"id\":2}\n\n";

        mockServer.enqueue(new MockResponse()
                .setBody(sseData)
                .setHeader("Content-Type", "text/event-stream"));

        // Act
        Flux<TestResponse> flux = client.get()
                .uri("/api/stream")
                .retrieve()
                .bodyToFlux(TestResponse.class);

        // Assert
        assertNotNull(flux);
        StepVerifier.create(flux)
                .expectNextMatches(event -> event.id == 1)
                .expectNextMatches(event -> event.id == 2)
                .verifyComplete();
    }

    // ========== bodyToFlux(TypeReference) Tests ==========

    @Test
    void testBodyToFluxWithTypeReferenceThrowsException() {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setBody("data: {\"name\":\"test\",\"id\":1}\n\n")
                .setHeader("Content-Type", "text/event-stream"));

        // Act & Assert
        assertThrows(UnsupportedOperationException.class, () ->
                client.get()
                        .uri("/api/stream")
                        .retrieve()
                        .bodyToFlux(new TypeReference<TestResponse>() {
                        })
        );
    }

    // ========== Error Handler Tests ==========

    @Test
    void testErrorHandlerPredicateMatching() {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("{\"error\":\"Not found\"}"));

        AtomicBoolean handler404Called = new AtomicBoolean(false);
        AtomicBoolean handler500Called = new AtomicBoolean(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                client.get()
                        .uri("/api/notfound")
                        .retrieve()
                        .onStatus(ResponseErrorHandler.onStatus(status -> status == 404, response -> {
                            handler404Called.set(true);
                            throw new RuntimeException("404 error");
                        }))
                        .onStatus(ResponseErrorHandler.onStatus(status -> status == 500, response -> {
                            handler500Called.set(true);
                            throw new RuntimeException("500 error");
                        }))
                        .body(TestResponse.class)
        );

        assertTrue(handler404Called.get());
        assertFalse(handler500Called.get());
    }

    @Test
    void testErrorHandlerRuntimeExceptionPropagation() {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("{\"error\":\"Server error\"}"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                client.get()
                        .uri("/api/error")
                        .retrieve()
                        .onStatus(ResponseErrorHandler.onStatus(status -> status >= 500, response -> {
                            throw new RuntimeException("Custom runtime exception");
                        }))
                        .body(TestResponse.class)
        );

        assertEquals("Custom runtime exception", exception.getMessage());
    }

    @Test
    void testErrorHandlerCheckedExceptionWrapping() {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("{\"error\":\"Server error\"}"));

        // Act & Assert
        HttpClientException exception = assertThrows(HttpClientException.class, () ->
                client.get()
                        .uri("/api/error")
                        .retrieve()
                        .onStatus(ResponseErrorHandler.onStatus(status -> status >= 500, response -> {
                            throw new Exception("Checked exception");
                        }))
                        .body(TestResponse.class)
        );

        assertTrue(exception.getMessage().contains("Error handler failed"));
    }

    @Test
    void testMultipleErrorHandlers() {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("{\"error\":\"Bad request\"}"));

        AtomicBoolean handler1Called = new AtomicBoolean(false);
        AtomicBoolean handler2Called = new AtomicBoolean(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                client.get()
                        .uri("/api/bad")
                        .retrieve()
                        .onStatus(ResponseErrorHandler.onStatus(status -> status >= 400, response -> {
                            handler1Called.set(true);
                            throw new RuntimeException("Handler 1");
                        }))
                        .onStatus(ResponseErrorHandler.onStatus(status -> status == 400, response -> {
                            handler2Called.set(true);
                            // This won't be reached because handler1 throws first
                        }))
                        .body(TestResponse.class)
        );

        assertTrue(handler1Called.get());
        // handler2 won't be called because handler1 throws
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

    @Data
    static class DemoResponse<T> {
        String error;
        T data;
    }
}
