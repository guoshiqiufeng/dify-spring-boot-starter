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
package io.github.guoshiqiufeng.dify.client.integration.okhttp.publisher;

import io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper;
import io.github.guoshiqiufeng.dify.client.core.http.HttpClientException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;

/**
 * Unit tests for OkHttpStreamPublisher
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/1/13
 */
class OkHttpStreamPublisherTest {

    private MockWebServer mockServer;
    private OkHttpClient client;
    private JsonMapper jsonMapper;

    @BeforeEach
    void setUp() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start();
        client = new OkHttpClient();
        jsonMapper = new TestJsonMapper();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockServer.shutdown();
    }

    // ========== Basic Streaming Tests ==========

    @Test
    void testStreamWithSingleEvent() {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setBody("data: {\"id\":1,\"message\":\"test\"}\n\n")
                .setHeader("Content-Type", "text/event-stream"));

        Request request = new Request.Builder()
                .url(mockServer.url("/stream"))
                .build();

        OkHttpStreamPublisher<TestEvent> publisher = new OkHttpStreamPublisher<>(
                client, request, jsonMapper, TestEvent.class);

        // Act & Assert
        Flux<TestEvent> flux = Flux.create(publisher::stream);

        StepVerifier.create(flux)
                .expectNextMatches(event -> event.id == 1 && "test".equals(event.message))
                .verifyComplete();
    }

    @Test
    void testStreamWithMultipleEvents() {
        // Arrange
        String sseData = "data: {\"id\":1,\"message\":\"first\"}\n\n" +
                "data: {\"id\":2,\"message\":\"second\"}\n\n" +
                "data: {\"id\":3,\"message\":\"third\"}\n\n";

        mockServer.enqueue(new MockResponse()
                .setBody(sseData)
                .setHeader("Content-Type", "text/event-stream"));

        Request request = new Request.Builder()
                .url(mockServer.url("/stream"))
                .build();

        OkHttpStreamPublisher<TestEvent> publisher = new OkHttpStreamPublisher<>(
                client, request, jsonMapper, TestEvent.class);

        // Act & Assert
        Flux<TestEvent> flux = Flux.create(publisher::stream);

        StepVerifier.create(flux)
                .expectNextMatches(event -> event.id == 1 && "first".equals(event.message))
                .expectNextMatches(event -> event.id == 2 && "second".equals(event.message))
                .expectNextMatches(event -> event.id == 3 && "third".equals(event.message))
                .verifyComplete();
    }

    @Test
    void testStreamWithEmptyResponse() {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setBody("")
                .setHeader("Content-Type", "text/event-stream"));

        Request request = new Request.Builder()
                .url(mockServer.url("/stream"))
                .build();

        OkHttpStreamPublisher<TestEvent> publisher = new OkHttpStreamPublisher<>(
                client, request, jsonMapper, TestEvent.class);

        // Act & Assert
        Flux<TestEvent> flux = Flux.create(publisher::stream);

        StepVerifier.create(flux)
                .verifyComplete();
    }

    @Test
    void testStreamCompletes() {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setBody("data: {\"id\":1,\"message\":\"test\"}\n\n")
                .setHeader("Content-Type", "text/event-stream"));

        Request request = new Request.Builder()
                .url(mockServer.url("/stream"))
                .build();

        OkHttpStreamPublisher<TestEvent> publisher = new OkHttpStreamPublisher<>(
                client, request, jsonMapper, TestEvent.class);

        // Act & Assert
        Flux<TestEvent> flux = Flux.create(publisher::stream);

        StepVerifier.create(flux)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void testStreamWithNullResponseBody() {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("")
                .setHeader("Content-Length", "0"));

        Request request = new Request.Builder()
                .url(mockServer.url("/stream"))
                .build();

        OkHttpStreamPublisher<TestEvent> publisher = new OkHttpStreamPublisher<>(
                client, request, jsonMapper, TestEvent.class);

        // Act & Assert
        Flux<TestEvent> flux = Flux.create(publisher::stream);

        // Empty body should complete without error
        StepVerifier.create(flux)
                .verifyComplete();
    }

    // ========== SSE Format Parsing Tests ==========

    @Test
    void testStreamWithDataPrefix() {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setBody("data: {\"id\":100,\"message\":\"with prefix\"}\n\n")
                .setHeader("Content-Type", "text/event-stream"));

        Request request = new Request.Builder()
                .url(mockServer.url("/stream"))
                .build();

        OkHttpStreamPublisher<TestEvent> publisher = new OkHttpStreamPublisher<>(
                client, request, jsonMapper, TestEvent.class);

        // Act & Assert
        Flux<TestEvent> flux = Flux.create(publisher::stream);

        StepVerifier.create(flux)
                .expectNextMatches(event -> event.id == 100 && "with prefix".equals(event.message))
                .verifyComplete();
    }

    @Test
    void testStreamWithMultilineEvent() {
        // Arrange - multiline event (data spans multiple lines, ended by empty line)
        String sseData = "data: {\"id\":1,\n" +
                "data: \"message\":\"multiline\"}\n" +
                "\n";

        mockServer.enqueue(new MockResponse()
                .setBody(sseData)
                .setHeader("Content-Type", "text/event-stream"));

        Request request = new Request.Builder()
                .url(mockServer.url("/stream"))
                .build();

        OkHttpStreamPublisher<TestEvent> publisher = new OkHttpStreamPublisher<>(
                client, request, jsonMapper, TestEvent.class);

        // Act & Assert
        Flux<TestEvent> flux = Flux.create(publisher::stream);

        StepVerifier.create(flux)
                .expectNextMatches(event -> event.id == 1 && "multiline".equals(event.message))
                .verifyComplete();
    }

    @Test
    void testStreamWithEmptyLines() {
        // Arrange - empty lines as event delimiters
        String sseData = "data: {\"id\":1,\"message\":\"first\"}\n" +
                "\n" +
                "data: {\"id\":2,\"message\":\"second\"}\n" +
                "\n";

        mockServer.enqueue(new MockResponse()
                .setBody(sseData)
                .setHeader("Content-Type", "text/event-stream"));

        Request request = new Request.Builder()
                .url(mockServer.url("/stream"))
                .build();

        OkHttpStreamPublisher<TestEvent> publisher = new OkHttpStreamPublisher<>(
                client, request, jsonMapper, TestEvent.class);

        // Act & Assert
        Flux<TestEvent> flux = Flux.create(publisher::stream);

        StepVerifier.create(flux)
                .expectNextMatches(event -> event.id == 1)
                .expectNextMatches(event -> event.id == 2)
                .verifyComplete();
    }

    @Test
    void testStreamWithCommentLines() {
        // Arrange - comment lines should be ignored
        String sseData = ": this is a comment\n" +
                "data: {\"id\":1,\"message\":\"test\"}\n\n" +
                ": another comment\n" +
                "data: {\"id\":2,\"message\":\"test2\"}\n\n";

        mockServer.enqueue(new MockResponse()
                .setBody(sseData)
                .setHeader("Content-Type", "text/event-stream"));

        Request request = new Request.Builder()
                .url(mockServer.url("/stream"))
                .build();

        OkHttpStreamPublisher<TestEvent> publisher = new OkHttpStreamPublisher<>(
                client, request, jsonMapper, TestEvent.class);

        // Act & Assert
        Flux<TestEvent> flux = Flux.create(publisher::stream);

        StepVerifier.create(flux)
                .expectNextMatches(event -> event.id == 1)
                .expectNextMatches(event -> event.id == 2)
                .verifyComplete();
    }

    @Test
    void testStreamWithEventMetadata() {
        // Arrange - event metadata should be ignored
        String sseData = "event: message\n" +
                "id: 123\n" +
                "retry: 1000\n" +
                "data: {\"id\":1,\"message\":\"test\"}\n\n";

        mockServer.enqueue(new MockResponse()
                .setBody(sseData)
                .setHeader("Content-Type", "text/event-stream"));

        Request request = new Request.Builder()
                .url(mockServer.url("/stream"))
                .build();

        OkHttpStreamPublisher<TestEvent> publisher = new OkHttpStreamPublisher<>(
                client, request, jsonMapper, TestEvent.class);

        // Act & Assert
        Flux<TestEvent> flux = Flux.create(publisher::stream);

        StepVerifier.create(flux)
                .expectNextMatches(event -> event.id == 1 && "test".equals(event.message))
                .verifyComplete();
    }

    @Test
    void testStreamWithMixedContent() {
        // Arrange - mix of comments, metadata, and data
        String sseData = ": comment\n" +
                "event: update\n" +
                "data: {\"id\":1,\"message\":\"first\"}\n\n" +
                ": another comment\n" +
                "id: 456\n" +
                "data: {\"id\":2,\"message\":\"second\"}\n\n";

        mockServer.enqueue(new MockResponse()
                .setBody(sseData)
                .setHeader("Content-Type", "text/event-stream"));

        Request request = new Request.Builder()
                .url(mockServer.url("/stream"))
                .build();

        OkHttpStreamPublisher<TestEvent> publisher = new OkHttpStreamPublisher<>(
                client, request, jsonMapper, TestEvent.class);

        // Act & Assert
        Flux<TestEvent> flux = Flux.create(publisher::stream);

        StepVerifier.create(flux)
                .expectNextMatches(event -> event.id == 1)
                .expectNextMatches(event -> event.id == 2)
                .verifyComplete();
    }

    // ========== JSON Validation Tests ==========

    @Test
    void testIsCompleteJsonWithObject() {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setBody("data: {\"id\":1,\"message\":\"object\"}\n\n")
                .setHeader("Content-Type", "text/event-stream"));

        Request request = new Request.Builder()
                .url(mockServer.url("/stream"))
                .build();

        OkHttpStreamPublisher<TestEvent> publisher = new OkHttpStreamPublisher<>(
                client, request, jsonMapper, TestEvent.class);

        // Act & Assert
        Flux<TestEvent> flux = Flux.create(publisher::stream);

        StepVerifier.create(flux)
                .expectNextMatches(event -> event.id == 1)
                .verifyComplete();
    }

    @Test
    void testIsCompleteJsonWithArray() {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setBody("data: [{\"id\":1},{\"id\":2}]\n\n")
                .setHeader("Content-Type", "text/event-stream"));

        Request request = new Request.Builder()
                .url(mockServer.url("/stream"))
                .build();

        OkHttpStreamPublisher<TestArrayEvent> publisher = new OkHttpStreamPublisher<>(
                client, request, jsonMapper, TestArrayEvent.class);

        // Act & Assert
        Flux<TestArrayEvent> flux = Flux.create(publisher::stream);

        StepVerifier.create(flux)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void testIsCompleteJsonWithString() {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setBody("data: \"test string\"\n\n")
                .setHeader("Content-Type", "text/event-stream"));

        Request request = new Request.Builder()
                .url(mockServer.url("/stream"))
                .build();

        OkHttpStreamPublisher<String> publisher = new OkHttpStreamPublisher<>(
                client, request, jsonMapper, String.class);

        // Act & Assert
        Flux<String> flux = Flux.create(publisher::stream);

        StepVerifier.create(flux)
                .expectNext("test string")
                .verifyComplete();
    }

    @Test
    void testIsCompleteJsonWithPrimitives() {
        // Arrange - test true, false, null, number
        String sseData = "data: true\n\n" +
                "data: false\n\n" +
                "data: null\n\n" +
                "data: 42\n\n" +
                "data: 3.14\n\n";

        mockServer.enqueue(new MockResponse()
                .setBody(sseData)
                .setHeader("Content-Type", "text/event-stream"));

        Request request = new Request.Builder()
                .url(mockServer.url("/stream"))
                .build();

        OkHttpStreamPublisher<Object> publisher = new OkHttpStreamPublisher<>(
                client, request, jsonMapper, Object.class);

        // Act & Assert
        Flux<Object> flux = Flux.create(publisher::stream);

        StepVerifier.create(flux)
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    void testIsCompleteJsonWithIncomplete() {
        // Arrange - incomplete JSON should be accumulated until empty line
        String sseData = "data: {\"id\":1,\n" +
                "data: \"message\":\"incomplete\"}\n" +
                "\n";

        mockServer.enqueue(new MockResponse()
                .setBody(sseData)
                .setHeader("Content-Type", "text/event-stream"));

        Request request = new Request.Builder()
                .url(mockServer.url("/stream"))
                .build();

        OkHttpStreamPublisher<TestEvent> publisher = new OkHttpStreamPublisher<>(
                client, request, jsonMapper, TestEvent.class);

        // Act & Assert
        Flux<TestEvent> flux = Flux.create(publisher::stream);

        StepVerifier.create(flux)
                .expectNextMatches(event -> event.id == 1 && "incomplete".equals(event.message))
                .verifyComplete();
    }

    // ========== Resource Cleanup Tests (SSE Cancellation) ==========

    @Test
    void testStreamCancellation() {
        // Arrange - long-running stream
        String sseData = "data: {\"id\":1,\"message\":\"first\"}\n\n" +
                "data: {\"id\":2,\"message\":\"second\"}\n\n" +
                "data: {\"id\":3,\"message\":\"third\"}\n\n";

        mockServer.enqueue(new MockResponse()
                .setBody(sseData)
                .setHeader("Content-Type", "text/event-stream"));

        Request request = new Request.Builder()
                .url(mockServer.url("/stream"))
                .build();

        OkHttpStreamPublisher<TestEvent> publisher = new OkHttpStreamPublisher<>(
                client, request, jsonMapper, TestEvent.class);

        // Act & Assert
        Flux<TestEvent> flux = Flux.create(publisher::stream);

        // Cancel after receiving first event
        StepVerifier.create(flux)
                .expectNextMatches(event -> event.id == 1)
                .thenCancel()
                .verify();

        // Verify that cancellation was handled (no exception thrown)
        // The onCancel callback should have called call.cancel()
    }

    @Test
    void testStreamDisposal() {
        // Arrange
        String sseData = "data: {\"id\":1,\"message\":\"first\"}\n\n" +
                "data: {\"id\":2,\"message\":\"second\"}\n\n";

        mockServer.enqueue(new MockResponse()
                .setBody(sseData)
                .setHeader("Content-Type", "text/event-stream"));

        Request request = new Request.Builder()
                .url(mockServer.url("/stream"))
                .build();

        OkHttpStreamPublisher<TestEvent> publisher = new OkHttpStreamPublisher<>(
                client, request, jsonMapper, TestEvent.class);

        // Act & Assert
        Flux<TestEvent> flux = Flux.create(publisher::stream);

        // Take only 1 event, which will dispose the flux
        StepVerifier.create(flux.take(1))
                .expectNextMatches(event -> event.id == 1)
                .verifyComplete();

        // Verify that disposal was handled (no exception thrown)
        // The onDispose callback should have called call.cancel()
    }

    @Test
    void testStreamCancellationReleasesConnection() {
        // Arrange - simulate a slow stream
        mockServer.enqueue(new MockResponse()
                .setBody("data: {\"id\":1,\"message\":\"slow\"}\n\n")
                .setHeader("Content-Type", "text/event-stream")
                .setBodyDelay(5, java.util.concurrent.TimeUnit.SECONDS));

        Request request = new Request.Builder()
                .url(mockServer.url("/stream"))
                .build();

        OkHttpStreamPublisher<TestEvent> publisher = new OkHttpStreamPublisher<>(
                client, request, jsonMapper, TestEvent.class);

        // Act & Assert
        Flux<TestEvent> flux = Flux.create(publisher::stream);

        // Cancel immediately without waiting for events
        StepVerifier.create(flux)
                .thenCancel()
                .verify();

        // If connection is properly released, this should complete quickly
        // without waiting for the 5-second delay
    }

    @Test
    void testMultipleSubscriptionsWithCancellation() {
        // Arrange
        String sseData = "data: {\"id\":1,\"message\":\"test\"}\n\n" +
                "data: {\"id\":2,\"message\":\"test2\"}\n\n";

        mockServer.enqueue(new MockResponse()
                .setBody(sseData)
                .setHeader("Content-Type", "text/event-stream"));

        mockServer.enqueue(new MockResponse()
                .setBody(sseData)
                .setHeader("Content-Type", "text/event-stream"));

        Request request1 = new Request.Builder()
                .url(mockServer.url("/stream"))
                .build();

        Request request2 = new Request.Builder()
                .url(mockServer.url("/stream"))
                .build();

        OkHttpStreamPublisher<TestEvent> publisher1 = new OkHttpStreamPublisher<>(
                client, request1, jsonMapper, TestEvent.class);

        OkHttpStreamPublisher<TestEvent> publisher2 = new OkHttpStreamPublisher<>(
                client, request2, jsonMapper, TestEvent.class);

        // Act & Assert - cancel first subscription
        Flux<TestEvent> flux1 = Flux.create(publisher1::stream);
        StepVerifier.create(flux1)
                .expectNextMatches(event -> event.id == 1)
                .thenCancel()
                .verify();

        // Second subscription should still work
        Flux<TestEvent> flux2 = Flux.create(publisher2::stream);
        StepVerifier.create(flux2)
                .expectNextMatches(event -> event.id == 1)
                .expectNextMatches(event -> event.id == 2)
                .verifyComplete();
    }

    // ========== Error Handling Tests ==========

    @Test
    void testStreamWithHttpError() {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("{\"error\":\"Not found\"}"));

        Request request = new Request.Builder()
                .url(mockServer.url("/stream"))
                .build();

        OkHttpStreamPublisher<TestEvent> publisher = new OkHttpStreamPublisher<>(
                client, request, jsonMapper, TestEvent.class);

        // Act & Assert
        Flux<TestEvent> flux = Flux.create(publisher::stream);

        StepVerifier.create(flux)
                .expectErrorMatches(throwable ->
                        throwable instanceof HttpClientException &&
                                ((HttpClientException) throwable).getStatusCode() == 404)
                .verify();
    }

    @Test
    void testStreamWithIOException() {
        // Arrange - server shutdown causes IOException
        mockServer.enqueue(new MockResponse()
                .setBody("data: {\"id\":1,\"message\":\"test\"}\n\n")
                .setHeader("Content-Type", "text/event-stream"));

        Request request = new Request.Builder()
                .url(mockServer.url("/stream"))
                .build();

        // Shutdown server before request completes
        try {
            mockServer.shutdown();
        } catch (IOException e) {
            // Ignore
        }

        OkHttpStreamPublisher<TestEvent> publisher = new OkHttpStreamPublisher<>(
                client, request, jsonMapper, TestEvent.class);

        // Act & Assert
        Flux<TestEvent> flux = Flux.create(publisher::stream);

        StepVerifier.create(flux)
                .expectError(HttpClientException.class)
                .verify();
    }

    @Test
    void testStreamWithInvalidJson() {
        // Arrange - invalid JSON should log warning and continue
        String sseData = "data: {invalid json}\n\n" +
                "data: {\"id\":2,\"message\":\"valid\"}\n\n";

        mockServer.enqueue(new MockResponse()
                .setBody(sseData)
                .setHeader("Content-Type", "text/event-stream"));

        Request request = new Request.Builder()
                .url(mockServer.url("/stream"))
                .build();

        OkHttpStreamPublisher<TestEvent> publisher = new OkHttpStreamPublisher<>(
                client, request, jsonMapper, TestEvent.class);

        // Act & Assert
        Flux<TestEvent> flux = Flux.create(publisher::stream);

        // Should skip invalid JSON and continue with valid event
        StepVerifier.create(flux)
                .expectNextMatches(event -> event.id == 2 && "valid".equals(event.message))
                .verifyComplete();
    }

    @Test
    void testStreamWithErrorResponseNullBody() {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setHeader("Content-Length", "0"));

        Request request = new Request.Builder()
                .url(mockServer.url("/stream"))
                .build();

        OkHttpStreamPublisher<TestEvent> publisher = new OkHttpStreamPublisher<>(
                client, request, jsonMapper, TestEvent.class);

        // Act & Assert
        Flux<TestEvent> flux = Flux.create(publisher::stream);

        StepVerifier.create(flux)
                .expectErrorMatches(throwable ->
                        throwable instanceof HttpClientException &&
                                ((HttpClientException) throwable).getStatusCode() == 500)
                .verify();
    }

    // ========== Test Data Classes ==========

    static class TestEvent {
        int id;
        String message;

        public TestEvent() {
        }

        public TestEvent(int id, String message) {
            this.id = id;
            this.message = message;
        }
    }

    static class TestArrayEvent {
        String data;

        public TestArrayEvent() {
        }

        public TestArrayEvent(String data) {
            this.data = data;
        }
    }

    // ========== Test JsonMapper Implementation ==========

    static class TestJsonMapper implements JsonMapper {
        @Override
        public String toJson(Object obj) {
            if (obj instanceof TestEvent) {
                TestEvent event = (TestEvent) obj;
                return "{\"id\":" + event.id + ",\"message\":\"" + event.message + "\"}";
            }
            if (obj instanceof String) {
                return "\"" + obj + "\"";
            }
            if (obj instanceof Boolean || obj instanceof Number) {
                return obj.toString();
            }
            if (obj == null) {
                return "null";
            }
            return obj.toString();
        }

        @Override
        public String toJsonIgnoreNull(Object object) {
            return toJson(object);
        }

        @Override
        public <T> T fromJson(String json, Class<T> clazz) {
            if (clazz == TestEvent.class) {
                return clazz.cast(parseTestEvent(json));
            }
            if (clazz == TestArrayEvent.class) {
                return clazz.cast(new TestArrayEvent(json));
            }
            if (clazz == String.class) {
                return clazz.cast(json.replace("\"", ""));
            }
            if (clazz == Object.class) {
                return clazz.cast(new Object());
            }
            return null;
        }

        @Override
        public <T> T fromJson(String json, io.github.guoshiqiufeng.dify.client.core.http.TypeReference<T> typeReference) {
            return null;
        }

        @Override
        public io.github.guoshiqiufeng.dify.client.core.codec.JsonNode parseTree(String json) {
            return null;
        }

        @Override
        public <T> T treeToValue(io.github.guoshiqiufeng.dify.client.core.codec.JsonNode node, Class<T> clazz) {
            return null;
        }

        @Override
        public io.github.guoshiqiufeng.dify.client.core.codec.JsonNode valueToTree(Object object) {
            return null;
        }

        private TestEvent parseTestEvent(String json) {
            // Simple JSON parsing for test purposes
            if (json.contains("invalid")) {
                throw new RuntimeException("Invalid JSON");
            }

            int id = 0;
            String message = "";

            // Extract id
            int idIndex = json.indexOf("\"id\":");
            if (idIndex != -1) {
                int start = idIndex + 5;
                int end = json.indexOf(",", start);
                if (end == -1) {
                    end = json.indexOf("}", start);
                }
                if (end != -1) {
                    String idStr = json.substring(start, end).trim();
                    id = Integer.parseInt(idStr);
                }
            }

            // Extract message
            int msgIndex = json.indexOf("\"message\":");
            if (msgIndex != -1) {
                int start = json.indexOf("\"", msgIndex + 10) + 1;
                int end = json.indexOf("\"", start);
                if (end != -1) {
                    message = json.substring(start, end);
                }
            }

            return new TestEvent(id, message);
        }
    }
}
