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
import io.github.guoshiqiufeng.dify.client.core.http.ResponseErrorHandler;
import io.github.guoshiqiufeng.dify.client.core.response.ResponseEntity;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test for streaming error handling in WebClientExecutor.
 * Covers the exchangeForSse error handling logic.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026-02-25
 */
class WebClientExecutorStreamErrorTest {

    private MockWebServer mockWebServer;
    private WebClientExecutor executor;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = mockWebServer.url("/").toString();
        WebClient webClient = WebClient.builder().baseUrl(baseUrl).build();

        // Use GsonJsonMapper for real JSON processing
        JsonMapper jsonMapper = GsonJsonMapper.getInstance();

        executor = new WebClientExecutor(webClient, jsonMapper);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testStreamingRequest_WithErrorHandler_ShouldThrowCustomException() {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(401)
                .setBody("{\"error\":\"Unauthorized\"}")
                .addHeader("Content-Type", "application/json"));

        ResponseErrorHandler errorHandler = new ResponseErrorHandler() {
            @Override
            public java.util.function.Predicate<Integer> getStatusPredicate() {
                return status -> status == 401;
            }

            @Override
            public void handle(ResponseEntity<?> response) throws Exception {
                throw new RuntimeException("Custom auth error: " + response.getBody());
            }
        };

        URI uri = mockWebServer.url("/stream").uri();
        Map<String, String> headers = new HashMap<>();
        Map<String, String> cookies = new HashMap<>();
        Map<String, String> queryParams = new HashMap<>();

        // Act & Assert
        Flux<String> result = executor.executeStream(
                "GET", uri, headers, cookies, queryParams, null, String.class,
                Collections.singletonList(errorHandler)
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().contains("Custom auth error"))
                .verify();
    }

    @Test
    void testStreamingRequest_WithoutErrorHandler_ShouldThrowDefaultException() {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("{\"error\":\"Internal Server Error\"}")
                .addHeader("Content-Type", "application/json"));

        URI uri = mockWebServer.url("/stream").uri();
        Map<String, String> headers = new HashMap<>();
        Map<String, String> cookies = new HashMap<>();
        Map<String, String> queryParams = new HashMap<>();

        // Act & Assert
        Flux<String> result = executor.executeStream(
                "GET", uri, headers, cookies, queryParams, null, String.class,
                Collections.emptyList()
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof HttpClientException &&
                                ((HttpClientException) throwable).getStatusCode() == 500)
                .verify();
    }

    @Test
    void testStreamingRequest_WithNonMatchingErrorHandler_ShouldThrowDefaultException() {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("{\"error\":\"Not Found\"}")
                .addHeader("Content-Type", "application/json"));

        ResponseErrorHandler errorHandler = new ResponseErrorHandler() {
            @Override
            public java.util.function.Predicate<Integer> getStatusPredicate() {
                return status -> status == 401; // Only handles 401
            }

            @Override
            public void handle(ResponseEntity<?> response) throws Exception {
                throw new RuntimeException("Should not be called");
            }
        };

        URI uri = mockWebServer.url("/stream").uri();
        Map<String, String> headers = new HashMap<>();
        Map<String, String> cookies = new HashMap<>();
        Map<String, String> queryParams = new HashMap<>();

        // Act & Assert
        Flux<String> result = executor.executeStream(
                "GET", uri, headers, cookies, queryParams, null, String.class,
                Collections.singletonList(errorHandler)
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof HttpClientException &&
                                ((HttpClientException) throwable).getStatusCode() == 404)
                .verify();
    }

    @Test
    void testStreamingRequest_WithMultipleErrorHandlers_ShouldExecuteMatchingHandler() {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(429)
                .setBody("{\"error\":\"Rate limit exceeded\"}")
                .addHeader("Content-Type", "application/json"));

        ResponseErrorHandler handler401 = new ResponseErrorHandler() {
            @Override
            public java.util.function.Predicate<Integer> getStatusPredicate() {
                return status -> status == 401;
            }

            @Override
            public void handle(ResponseEntity<?> response) throws Exception {
                throw new RuntimeException("Auth error");
            }
        };

        ResponseErrorHandler handler429 = new ResponseErrorHandler() {
            @Override
            public java.util.function.Predicate<Integer> getStatusPredicate() {
                return status -> status == 429;
            }

            @Override
            public void handle(ResponseEntity<?> response) throws Exception {
                throw new RuntimeException("Rate limit error: " + response.getBody());
            }
        };

        URI uri = mockWebServer.url("/stream").uri();
        Map<String, String> headers = new HashMap<>();
        Map<String, String> cookies = new HashMap<>();
        Map<String, String> queryParams = new HashMap<>();

        List<ResponseErrorHandler> handlers = List.of(handler401, handler429);

        // Act & Assert
        Flux<String> result = executor.executeStream(
                "GET", uri, headers, cookies, queryParams, null, String.class,
                handlers
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().contains("Rate limit error"))
                .verify();
    }

    @Test
    void testStreamingRequest_SuccessfulResponse_ShouldNotTriggerErrorHandler() {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("data: {\"message\":\"test\"}\n\n")
                .addHeader("Content-Type", "text/event-stream"));

        ResponseErrorHandler errorHandler = new ResponseErrorHandler() {
            @Override
            public java.util.function.Predicate<Integer> getStatusPredicate() {
                return status -> status >= 400;
            }

            @Override
            public void handle(ResponseEntity<?> response) throws Exception {
                throw new RuntimeException("Should not be called");
            }
        };

        URI uri = mockWebServer.url("/stream").uri();
        Map<String, String> headers = new HashMap<>();
        Map<String, String> cookies = new HashMap<>();
        Map<String, String> queryParams = new HashMap<>();

        // Act
        Flux<String> result = executor.executeStream(
                "GET", uri, headers, cookies, queryParams, null, String.class,
                Collections.singletonList(errorHandler)
        );

        // Assert - Should emit data and complete without error
        StepVerifier.create(result)
                .expectNextCount(1) // Expect one data item
                .expectComplete()
                .verify();
    }

    @Test
    void testStreamingRequest_WithNullErrorHandlers_ShouldThrowDefaultException() {
        // Arrange
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(503)
                .setBody("{\"error\":\"Service Unavailable\"}")
                .addHeader("Content-Type", "application/json"));

        URI uri = mockWebServer.url("/stream").uri();
        Map<String, String> headers = new HashMap<>();
        Map<String, String> cookies = new HashMap<>();
        Map<String, String> queryParams = new HashMap<>();

        // Act & Assert
        Flux<String> result = executor.executeStream(
                "GET", uri, headers, cookies, queryParams, null, String.class,
                null // null handlers
        );

        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof HttpClientException &&
                                ((HttpClientException) throwable).getStatusCode() == 503)
                .verify();
    }
}
