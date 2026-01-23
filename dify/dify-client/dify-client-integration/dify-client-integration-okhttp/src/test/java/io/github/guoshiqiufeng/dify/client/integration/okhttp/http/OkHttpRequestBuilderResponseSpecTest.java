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
import io.github.guoshiqiufeng.dify.client.core.response.ResponseEntity;
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
        ResponseEntity<TestResponse> response = client.get()
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
        ResponseEntity<Map<String, String>> response = client.get()
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
        ResponseEntity<Void> response = client.delete()
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

    @Test
    void testErrorHandlerWithSuccessfulResponse() {
        // Test that error handlers are not called for successful responses
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"test\",\"id\":123}")
                .setHeader("Content-Type", "application/json"));

        AtomicBoolean handlerCalled = new AtomicBoolean(false);

        TestResponse result = client.get()
                .uri("/api/test")
                .retrieve()
                .onStatus(ResponseErrorHandler.onStatus(status -> status >= 400, response -> {
                    handlerCalled.set(true);
                    throw new RuntimeException("Should not be called");
                }))
                .body(TestResponse.class);

        assertNotNull(result);
        assertFalse(handlerCalled.get());
    }

    @Test
    void testErrorHandlerAccessesResponseBody() {
        // Test that error handler can access response body
        mockServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("{\"error\":\"Bad request\",\"details\":\"Invalid parameter\"}"));

        AtomicBoolean bodyAccessed = new AtomicBoolean(false);

        assertThrows(RuntimeException.class, () ->
                client.get()
                        .uri("/api/bad")
                        .retrieve()
                        .onStatus(ResponseErrorHandler.onStatus(status -> status == 400, response -> {
                            // Access response body in error handler
                            Object body = response.getBody();
                            if (body != null && body.toString().contains("Bad request")) {
                                bodyAccessed.set(true);
                            }
                            throw new RuntimeException("Bad request");
                        }))
                        .body(TestResponse.class)
        );

        assertTrue(bodyAccessed.get());
    }

    @Test
    void testErrorHandlerAccessesResponseHeaders() {
        // Test that error handler can access response headers
        mockServer.enqueue(new MockResponse()
                .setResponseCode(401)
                .setBody("{\"error\":\"Unauthorized\"}")
                .setHeader("WWW-Authenticate", "Bearer realm=\"example\""));

        AtomicBoolean headerAccessed = new AtomicBoolean(false);

        assertThrows(RuntimeException.class, () ->
                client.get()
                        .uri("/api/protected")
                        .retrieve()
                        .onStatus(ResponseErrorHandler.onStatus(status -> status == 401, response -> {
                            // Access response headers in error handler
                            String authHeader = response.getHeaders().getFirst("WWW-Authenticate");
                            if (authHeader != null && authHeader.contains("Bearer")) {
                                headerAccessed.set(true);
                            }
                            throw new RuntimeException("Unauthorized");
                        }))
                        .body(TestResponse.class)
        );

        assertTrue(headerAccessed.get());
    }

    @Test
    void testErrorHandlerAccessesStatusCode() {
        // Test that error handler can access status code
        mockServer.enqueue(new MockResponse()
                .setResponseCode(403)
                .setBody("{\"error\":\"Forbidden\"}"));

        AtomicBoolean statusCodeChecked = new AtomicBoolean(false);

        assertThrows(RuntimeException.class, () ->
                client.get()
                        .uri("/api/forbidden")
                        .retrieve()
                        .onStatus(ResponseErrorHandler.onStatus(status -> status == 403, response -> {
                            // Access status code in error handler
                            if (response.getStatusCode() == 403) {
                                statusCodeChecked.set(true);
                            }
                            throw new RuntimeException("Forbidden");
                        }))
                        .body(TestResponse.class)
        );

        assertTrue(statusCodeChecked.get());
    }

    @Test
    void testErrorHandlerWithToEntity() {
        // Test error handler is called in toEntity() method
        mockServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("{\"error\":\"Server error\"}"));

        AtomicBoolean handlerCalled = new AtomicBoolean(false);

        assertThrows(RuntimeException.class, () ->
                client.get()
                        .uri("/api/error")
                        .retrieve()
                        .onStatus(ResponseErrorHandler.onStatus(status -> status >= 500, response -> {
                            handlerCalled.set(true);
                            throw new RuntimeException("Server error");
                        }))
                        .toEntity(TestResponse.class)
        );

        assertTrue(handlerCalled.get());
    }

    @Test
    void testErrorHandlerWithToEntityTypeReference() {
        // Test error handler is called in toEntity(TypeReference) method
        mockServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("{\"error\":\"Not found\"}"));

        AtomicBoolean handlerCalled = new AtomicBoolean(false);

        assertThrows(RuntimeException.class, () ->
                client.get()
                        .uri("/api/notfound")
                        .retrieve()
                        .onStatus(ResponseErrorHandler.onStatus(status -> status == 404, response -> {
                            handlerCalled.set(true);
                            throw new RuntimeException("Not found");
                        }))
                        .toEntity(new TypeReference<List<TestResponse>>() {})
        );

        assertTrue(handlerCalled.get());
    }

    @Test
    void testErrorHandlerWithToBodilessEntity() {
        // Test error handler is called in toBodilessEntity() method
        mockServer.enqueue(new MockResponse()
                .setResponseCode(403)
                .setBody("{\"error\":\"Forbidden\"}"));

        AtomicBoolean handlerCalled = new AtomicBoolean(false);

        assertThrows(RuntimeException.class, () ->
                client.delete()
                        .uri("/api/forbidden")
                        .retrieve()
                        .onStatus(ResponseErrorHandler.onStatus(status -> status == 403, response -> {
                            handlerCalled.set(true);
                            throw new RuntimeException("Forbidden");
                        }))
                        .toBodilessEntity()
        );

        assertTrue(handlerCalled.get());
    }

    @Test
    void testErrorHandlerWithDifferentStatusRanges() {
        // Test error handlers with different status code ranges
        int[] errorCodes = {400, 401, 403, 404, 500, 502, 503};

        for (int errorCode : errorCodes) {
            mockServer.enqueue(new MockResponse()
                    .setResponseCode(errorCode)
                    .setBody("{\"error\":\"Error " + errorCode + "\"}"));

            AtomicBoolean handlerCalled = new AtomicBoolean(false);

            assertThrows(RuntimeException.class, () ->
                    client.get()
                            .uri("/api/error")
                            .retrieve()
                            .onStatus(ResponseErrorHandler.onStatus(status -> status >= 400, response -> {
                                handlerCalled.set(true);
                                throw new RuntimeException("Error " + response.getStatusCode());
                            }))
                            .body(TestResponse.class)
            );

            assertTrue(handlerCalled.get(), "Handler should be called for status code " + errorCode);
        }
    }

    @Test
    void testErrorHandlerDoesNotThrowException() {
        // Test error handler that doesn't throw exception (just logs or processes)
        mockServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("{\"error\":\"Bad request\"}"));

        AtomicBoolean handlerCalled = new AtomicBoolean(false);

        // This should still throw because the response is an error, but handler is called
        assertThrows(RuntimeException.class, () ->
                client.get()
                        .uri("/api/bad")
                        .retrieve()
                        .onStatus(ResponseErrorHandler.onStatus(status -> status == 400, response -> {
                            handlerCalled.set(true);
                            // Don't throw, just process
                        }))
                        .onStatus(ResponseErrorHandler.onStatus(status -> status >= 400, response -> {
                            // This handler throws
                            throw new RuntimeException("Bad request");
                        }))
                        .body(TestResponse.class)
        );

        assertTrue(handlerCalled.get());
    }

    @Test
    void testErrorHandlerWithCustomException() {
        // Test error handler throwing custom exception
        mockServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("{\"error\":\"Not found\"}"));

        class CustomNotFoundException extends RuntimeException {
            public CustomNotFoundException(String message) {
                super(message);
            }
        }

        assertThrows(CustomNotFoundException.class, () ->
                client.get()
                        .uri("/api/notfound")
                        .retrieve()
                        .onStatus(ResponseErrorHandler.onStatus(status -> status == 404, response -> {
                            throw new CustomNotFoundException("Resource not found");
                        }))
                        .body(TestResponse.class)
        );
    }

    @Test
    void testToEntityWithErrorResponseAndNoThrowingHandler() {
        // Test toEntity returns HttpResponse when error handler doesn't throw
        // This covers the line: handleErrors(httpResponse); return httpResponse; in error branch
        mockServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("{\"error\":\"Not found\"}"));

        AtomicBoolean handlerCalled = new AtomicBoolean(false);

        // Error handler that doesn't throw - just processes the error
        ResponseEntity<TestResponse> response = client.get()
                .uri("/api/notfound")
                .retrieve()
                .onStatus(ResponseErrorHandler.onStatus(status -> status == 404, httpResponse -> {
                    handlerCalled.set(true);
                    // Don't throw, just log or process
                }))
                .toEntity(TestResponse.class);

        // Verify handler was called
        assertTrue(handlerCalled.get());

        // Verify response is returned with error status
        assertNotNull(response);
        assertEquals(404, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testToEntityWithErrorResponseAndMultipleNonThrowingHandlers() {
        // Test multiple error handlers that don't throw
        mockServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("{\"error\":\"Server error\"}"));

        AtomicBoolean handler1Called = new AtomicBoolean(false);
        AtomicBoolean handler2Called = new AtomicBoolean(false);

        ResponseEntity<TestResponse> response = client.get()
                .uri("/api/error")
                .retrieve()
                .onStatus(ResponseErrorHandler.onStatus(status -> status >= 500, httpResponse -> {
                    handler1Called.set(true);
                    // Don't throw
                }))
                .onStatus(ResponseErrorHandler.onStatus(status -> status == 500, httpResponse -> {
                    handler2Called.set(true);
                    // Don't throw
                }))
                .toEntity(TestResponse.class);

        // Both handlers should be called
        assertTrue(handler1Called.get());
        assertTrue(handler2Called.get());

        // Response should be returned
        assertNotNull(response);
        assertEquals(500, response.getStatusCode());
    }

    @Test
    void testToEntityTypeReferenceWithErrorResponseAndNoThrowingHandler() {
        // Test toEntity(TypeReference) returns HttpResponse when error handler doesn't throw
        mockServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("{\"error\":\"Bad request\"}"));

        AtomicBoolean handlerCalled = new AtomicBoolean(false);

        ResponseEntity<List<TestResponse>> response = client.get()
                .uri("/api/bad")
                .retrieve()
                .onStatus(ResponseErrorHandler.onStatus(status -> status == 400, httpResponse -> {
                    handlerCalled.set(true);
                    // Don't throw
                }))
                .toEntity(new TypeReference<List<TestResponse>>() {});

        assertTrue(handlerCalled.get());
        assertNotNull(response);
        assertEquals(400, response.getStatusCode());
    }

    @Test
    void testToEntityWithSuccessResponseAndNonThrowingHandler() {
        // Test toEntity with success response and error handler (handler shouldn't be called)
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"test\",\"id\":123}")
                .setHeader("Content-Type", "application/json"));

        AtomicBoolean handlerCalled = new AtomicBoolean(false);

        ResponseEntity<TestResponse> response = client.get()
                .uri("/api/test")
                .retrieve()
                .onStatus(ResponseErrorHandler.onStatus(status -> status >= 400, httpResponse -> {
                    handlerCalled.set(true);
                }))
                .toEntity(TestResponse.class);

        // Handler should not be called for success response
        assertFalse(handlerCalled.get());
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test", response.getBody().name);
    }

    @Test
    void testToEntityWithErrorResponseEmptyBody() {
        // Test toEntity with error response and empty body
        mockServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody(""));

        AtomicBoolean handlerCalled = new AtomicBoolean(false);

        ResponseEntity<TestResponse> response = client.get()
                .uri("/api/error")
                .retrieve()
                .onStatus(ResponseErrorHandler.onStatus(status -> status >= 500, httpResponse -> {
                    handlerCalled.set(true);
                    // Verify body is empty string
                    assertEquals("", httpResponse.getBody());
                }))
                .toEntity(TestResponse.class);

        assertTrue(handlerCalled.get());
        assertNotNull(response);
        assertEquals(500, response.getStatusCode());
    }

    @Test
    void testToEntityWithErrorResponseNullBody() {
        // Test toEntity with error response and null body
        mockServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setHeader("Content-Length", "0"));

        AtomicBoolean handlerCalled = new AtomicBoolean(false);

        ResponseEntity<TestResponse> response = client.get()
                .uri("/api/error")
                .retrieve()
                .onStatus(ResponseErrorHandler.onStatus(status -> status >= 500, httpResponse -> {
                    handlerCalled.set(true);
                    // Verify body is empty string (not null)
                    assertEquals("", httpResponse.getBody());
                }))
                .toEntity(TestResponse.class);

        assertTrue(handlerCalled.get());
        assertNotNull(response);
        assertEquals(500, response.getStatusCode());
    }

    @Test
    void testToEntityWithDifferentErrorStatusCodes() {
        // Test toEntity with various error status codes and non-throwing handlers
        int[] errorCodes = {400, 401, 403, 404, 500, 502, 503};

        for (int errorCode : errorCodes) {
            mockServer.enqueue(new MockResponse()
                    .setResponseCode(errorCode)
                    .setBody("{\"error\":\"Error " + errorCode + "\"}"));

            AtomicBoolean handlerCalled = new AtomicBoolean(false);

            ResponseEntity<TestResponse> response = client.get()
                    .uri("/api/error")
                    .retrieve()
                    .onStatus(ResponseErrorHandler.onStatus(status -> status >= 400, httpResponse -> {
                        handlerCalled.set(true);
                        assertEquals(errorCode, httpResponse.getStatusCode());
                    }))
                    .toEntity(TestResponse.class);

            assertTrue(handlerCalled.get(), "Handler should be called for status " + errorCode);
            assertNotNull(response);
            assertEquals(errorCode, response.getStatusCode());
        }
    }

    @Test
    void testToEntityWithErrorHandlerAccessingErrorBody() {
        // Test that error handler can access and process error body
        mockServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("{\"error\":\"Validation failed\",\"field\":\"email\"}"));

        AtomicBoolean bodyProcessed = new AtomicBoolean(false);

        ResponseEntity<TestResponse> response = client.get()
                .uri("/api/validate")
                .retrieve()
                .onStatus(ResponseErrorHandler.onStatus(status -> status == 400, httpResponse -> {
                    Object body = httpResponse.getBody();
                    if (body != null && body.toString().contains("Validation failed")) {
                        bodyProcessed.set(true);
                    }
                }))
                .toEntity(TestResponse.class);

        assertTrue(bodyProcessed.get());
        assertNotNull(response);
        assertEquals(400, response.getStatusCode());
    }

    @Test
    void testToEntitySuccessWithNullResponseBody() {
        // Test toEntity with success response (2xx) but null body
        // This tests handleResponse(response, responseType, false) with responseBody == null
        mockServer.enqueue(new MockResponse()
                .setResponseCode(204));

        ResponseEntity<TestResponse> response = client.get()
                .uri("/api/nocontent")
                .retrieve()
                .toEntity(TestResponse.class);

        assertNotNull(response);
        assertEquals(204, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testToEntitySuccessWithEmptyBodyString() {
        // Test toEntity with success response (2xx) but empty body string
        // This tests handleResponse(response, responseType, false) with bodyString.isEmpty()
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("")
                .setHeader("Content-Type", "application/json"));

        ResponseEntity<TestResponse> response = client.get()
                .uri("/api/empty")
                .retrieve()
                .toEntity(TestResponse.class);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testToEntitySuccessWithVoidType() {
        // Test toEntity with success response and Void.class
        // This tests handleResponse(response, Void.class, false)
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"result\":\"ok\"}")
                .setHeader("Content-Type", "application/json"));

        ResponseEntity<Void> response = client.get()
                .uri("/api/void")
                .retrieve()
                .toEntity(Void.class);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testToEntitySuccessWithByteArrayType() {
        // Test toEntity with success response and byte[].class
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("test data")
                .setHeader("Content-Type", "application/octet-stream"));

        ResponseEntity<byte[]> response = client.get()
                .uri("/api/bytes")
                .retrieve()
                .toEntity(byte[].class);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getBody());
        assertArrayEquals("test data".getBytes(), response.getBody());
    }

    @Test
    void testToEntitySuccessWithStringType() {
        // Test toEntity with success response and String.class
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("test string")
                .setHeader("Content-Type", "text/plain"));

        ResponseEntity<String> response = client.get()
                .uri("/api/string")
                .retrieve()
                .toEntity(String.class);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test string", response.getBody());
    }

    @Test
    void testToEntitySuccessWithJsonDeserialization() {
        // Test toEntity with success response and JSON deserialization
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"test\",\"id\":123}")
                .setHeader("Content-Type", "application/json"));

        ResponseEntity<TestResponse> response = client.get()
                .uri("/api/test")
                .retrieve()
                .toEntity(TestResponse.class);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test", response.getBody().name);
        assertEquals(123, response.getBody().id);
    }

    @Test
    void testToEntitySuccessWithDeserializationError() {
        // Test toEntity with success response but JSON deserialization fails
        // This tests the Exception branch in handleResponse when checkError=false
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{invalid json}")
                .setHeader("Content-Type", "application/json"));

        assertThrows(HttpClientException.class, () ->
                client.get()
                        .uri("/api/invalid")
                        .retrieve()
                        .toEntity(TestResponse.class)
        );
    }

    @Test
    void testToEntityTypeReferenceSuccessWithNullBody() {
        // Test toEntity(TypeReference) with success response but null body
        mockServer.enqueue(new MockResponse()
                .setResponseCode(204));

        ResponseEntity<List<TestResponse>> response = client.get()
                .uri("/api/nocontent")
                .retrieve()
                .toEntity(new TypeReference<List<TestResponse>>() {});

        assertNotNull(response);
        assertEquals(204, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testToEntityTypeReferenceSuccessWithEmptyBody() {
        // Test toEntity(TypeReference) with success response but empty body
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("")
                .setHeader("Content-Type", "application/json"));

        ResponseEntity<List<TestResponse>> response = client.get()
                .uri("/api/empty")
                .retrieve()
                .toEntity(new TypeReference<List<TestResponse>>() {});

        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testToEntityTypeReferenceSuccessWithDeserializationError() {
        // Test toEntity(TypeReference) with success response but deserialization fails
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{invalid json}")
                .setHeader("Content-Type", "application/json"));

        assertThrows(HttpClientException.class, () ->
                client.get()
                        .uri("/api/invalid")
                        .retrieve()
                        .toEntity(new TypeReference<List<TestResponse>>() {})
        );
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
