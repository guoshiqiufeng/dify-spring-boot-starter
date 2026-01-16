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
import io.github.guoshiqiufeng.dify.client.core.http.HttpHeaders;
import io.github.guoshiqiufeng.dify.client.core.http.TypeReference;
import io.github.guoshiqiufeng.dify.client.core.response.HttpResponse;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.core.utils.MultipartBodyBuilder;
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
        HttpHeaders defaultHeaders = new HttpHeaders();
        defaultHeaders.set("test-key", "test-value");
        defaultHeaders.set("test-null", null);
        client = new JavaHttpClient(
                mockServer.url("/").toString(),
                config,
                null,
                new GsonJsonMapper(),
                defaultHeaders
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

    @Test
    void testExecuteWithTypeReferenceIOException() throws IOException {
        // Arrange - shutdown server to cause IOException
        mockServer.shutdown();

        // Act & Assert
        assertThrows(HttpClientException.class, () ->
                getBuilder(client.get()
                        .uri("/api/test"))
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

    @Test
    void testExecuteForResponseWithIOException() throws IOException {
        // Arrange - shutdown server to cause IOException
        mockServer.shutdown();

        // Act & Assert
        assertThrows(HttpClientException.class, () ->
                getBuilder(client.get()
                        .uri("/api/test"))
                        .executeForResponse(TestResponse.class)
        );
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

    @Test
    void testExecuteForResponseTypeReferenceIOException() throws IOException {
        // Arrange - shutdown server to cause IOException
        mockServer.shutdown();

        // Act & Assert
        assertThrows(HttpClientException.class, () ->
                getBuilder(client.get()
                        .uri("/api/test"))
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

    // ========== Request Building Tests ==========

//    @Test
//    void testRequestWithQueryParameters() throws Exception {
//        // Arrange
//        mockServer.enqueue(new MockResponse()
//                .setResponseCode(200)
//                .setBody("{\"name\":\"test\",\"id\":123}")
//                .setHeader("Content-Type", "application/json"));
//
//        // Act
//        TestResponse result = getBuilder(client.get()
//                .uri(uri ->
//                    uri.queryParam("page", "1")
//                            .queryParam("limit", "10").build()
//                ).retrieve()
//                .execute(TestResponse.class);
//
//        // Assert
//        assertNotNull(result);
//        RecordedRequest request = mockServer.takeRequest();
//        assertTrue(request.getPath().contains("page=1"));
//        assertTrue(request.getPath().contains("limit=10"));
//    }

    @Test
    void testRequestWithHeaders() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"test\",\"id\":123}")
                .setHeader("Content-Type", "application/json"));

        // Act
        TestResponse result = getBuilder(client.get()
                .uri("/api/test")
                .header("Authorization", "Bearer token123")
                .header("X-Custom-Header", "custom-value"))
                .execute(TestResponse.class);

        // Assert
        assertNotNull(result);
        RecordedRequest request = mockServer.takeRequest();
        assertEquals("Bearer token123", request.getHeader("Authorization"));
        assertEquals("custom-value", request.getHeader("X-Custom-Header"));
    }

    @Test
    void testRequestWithHeadersNullValue() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"test\",\"id\":123}")
                .setHeader("Content-Type", "application/json"));

        // Act
        TestResponse result = getBuilder(client.get()
                .uri("/api/test")
                .header("Authorization", null)
                .header("X-Custom-Header", "custom-value"))
                .execute(TestResponse.class);

        // Assert
        assertNotNull(result);
        RecordedRequest request = mockServer.takeRequest();
        assertNull(request.getHeader("Authorization"));
        assertEquals("custom-value", request.getHeader("X-Custom-Header"));
    }

    @Test
    void testRequestWithCookies() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"test\",\"id\":123}")
                .setHeader("Content-Type", "application/json"));

        // Act
        TestResponse result = getBuilder(client.get()
                .uri("/api/test")
                .cookies(cookies -> {
                    cookies.add("sessionId", "abc123");
                    cookies.add("userId", "user456");
                }))
                .execute(TestResponse.class);

        // Assert
        assertNotNull(result);
        RecordedRequest request = mockServer.takeRequest();
        String cookieHeader = request.getHeader("Cookie");
        assertNotNull(cookieHeader);
        assertTrue(cookieHeader.contains("sessionId=abc123"));
        assertTrue(cookieHeader.contains("userId=user456"));
    }

    @Test
    void testRequestWithCookiesNullValue() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"test\",\"id\":123}")
                .setHeader("Content-Type", "application/json"));

        // Act
        TestResponse result = getBuilder(client.get()
                .uri("/api/test")
                .cookies(cookies -> {
                    cookies.add("sessionId", null);
                    cookies.add("userId", "user456");
                }))
                .execute(TestResponse.class);

        // Assert
        assertNotNull(result);
        RecordedRequest request = mockServer.takeRequest();
        String cookieHeader = request.getHeader("Cookie");
        // Should only contain userId since sessionId is null
        assertNotNull(cookieHeader);
        assertTrue(cookieHeader.contains("userId=user456"));
        assertFalse(cookieHeader.contains("sessionId"));
    }

    @Test
    void testRequestWithJsonBody() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"response\",\"id\":999}")
                .setHeader("Content-Type", "application/json"));

        Map<String, Object> requestBody = new java.util.HashMap<>();
        requestBody.put("name", "test");
        requestBody.put("value", 123);

        // Act
        TestResponse result = getBuilder(client.post()
                .uri("/api/create")
                .body(requestBody))
                .execute(TestResponse.class);

        // Assert
        assertNotNull(result);
        RecordedRequest request = mockServer.takeRequest();
        assertEquals("POST", request.getMethod());
        String body = request.getBody().readUtf8();
        assertTrue(body.contains("\"name\""));
        assertTrue(body.contains("\"test\""));
    }

    @Test
    void testRequestWithMultipartData() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"uploaded\",\"id\":888}")
                .setHeader("Content-Type", "application/json"));

        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("field1", "value1");
        multipartBodyBuilder.part("field2", "value2");
        multipartBodyBuilder.part("field3", 3);
        multipartBodyBuilder.part("field4", false);
        multipartBodyBuilder.part("file", "test data".getBytes())
                .header("Content-Disposition",
                        "form-data; name=\"file\"; filename=\"file\";");
        // Act
        TestResponse result = getBuilder(client.post()
                .uri("/api/upload")
                .body(multipartBodyBuilder.build()))
                .execute(TestResponse.class);

        // Assert
        assertNotNull(result);
        RecordedRequest request = mockServer.takeRequest();
        assertEquals("POST", request.getMethod());
        String body = request.getBody().readUtf8();
        assertTrue(body.contains("field1"));
        assertTrue(body.contains("value1"));
    }

    @Test
    void testRequestWithEmptyBodyForPost() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"created\",\"id\":777}")
                .setHeader("Content-Type", "application/json"));

        // Act
        TestResponse result = getBuilder(client.post()
                .uri("/api/create"))
                .execute(TestResponse.class);

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

        // Act
        TestResponse result = getBuilder(client.put()
                .uri("/api/update"))
                .execute(TestResponse.class);

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

        // Act & Assert
        HttpClientException exception = assertThrows(HttpClientException.class, () ->
                getBuilder(client.get()
                        .uri("/api/error"))
                        .execute(TestResponse.class)
        );

        assertEquals(500, exception.getStatusCode());
    }

    // ========== Multipart Form Data Tests (multipart() method) ==========

    @Test
    void testMultipartWithStringValues() throws Exception {
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"uploaded\",\"id\":100}")
                .setHeader("Content-Type", "application/json"));

        java.util.Map<String, Object> formData = new java.util.HashMap<>();
        formData.put("field1", "value1");
        formData.put("field2", "value2");

        TestResponse result = getBuilder(client.post()
                .uri("/api/upload"))
                .multipart(formData)
                .execute(TestResponse.class);

        assertNotNull(result);
        RecordedRequest request = mockServer.takeRequest();
        String body = request.getBody().readUtf8();
        assertTrue(body.contains("field1"));
        assertTrue(body.contains("value1"));
    }

    @Test
    void testMultipartWithByteArray() throws Exception {
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"uploaded\",\"id\":101}")
                .setHeader("Content-Type", "application/json"));

        java.util.Map<String, Object> formData = new java.util.HashMap<>();
        formData.put("file", "test data".getBytes());

        TestResponse result = getBuilder(client.post()
                .uri("/api/upload"))
                .multipart(formData)
                .execute(TestResponse.class);

        assertNotNull(result);
        RecordedRequest request = mockServer.takeRequest();
        assertTrue(request.getHeader("Content-Type").contains("multipart/form-data"));
    }

    @Test
    void testMultipartWithComplexObject() throws Exception {
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"uploaded\",\"id\":102}")
                .setHeader("Content-Type", "application/json"));

        java.util.Map<String, Object> formData = new java.util.HashMap<>();
        TestResponse complexObj = new TestResponse("complex", 999);
        formData.put("data", complexObj);

        TestResponse result = getBuilder(client.post()
                .uri("/api/upload"))
                .multipart(formData)
                .execute(TestResponse.class);

        assertNotNull(result);
    }

    // ========== Multipart from Parts Tests ==========

    @Test
    void testMultipartFromPartsWithByteArray() throws Exception {
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"uploaded\",\"id\":103}")
                .setHeader("Content-Type", "application/json"));

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", "test file content".getBytes())
                .header("Content-Disposition", "form-data; name=\"file\"; filename=\"test.txt\"")
                .header("Content-Type", "text/plain");

        TestResponse result = getBuilder(client.post()
                .uri("/api/upload")
                .header("Content-Type", "multipart/form-data")
                .body(builder.build()))
                .execute(TestResponse.class);

        assertNotNull(result);
        RecordedRequest request = mockServer.takeRequest();
        String body = request.getBody().readUtf8();
        assertTrue(body.contains("test.txt"));
    }

    @Test
    void testMultipartFromPartsWithString() throws Exception {
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"uploaded\",\"id\":104}")
                .setHeader("Content-Type", "application/json"));

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("textField", "text value");

        TestResponse result = getBuilder(client.post()
                .uri("/api/upload")
                .header("Content-Type", "multipart/form-data")
                .body(builder.build()))
                .execute(TestResponse.class);

        assertNotNull(result);
    }

    @Test
    void testMultipartFromPartsWithNumber() throws Exception {
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"uploaded\",\"id\":105}")
                .setHeader("Content-Type", "application/json"));

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("numberField", 12345);

        TestResponse result = getBuilder(client.post()
                .uri("/api/upload")
                .header("Content-Type", "multipart/form-data")
                .body(builder.build()))
                .execute(TestResponse.class);

        assertNotNull(result);
        RecordedRequest request = mockServer.takeRequest();
        String body = request.getBody().readUtf8();
        assertTrue(body.contains("12345"));
    }

    @Test
    void testMultipartFromPartsWithBoolean() throws Exception {
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"uploaded\",\"id\":106}")
                .setHeader("Content-Type", "application/json"));

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("boolField", true);

        TestResponse result = getBuilder(client.post()
                .uri("/api/upload")
                .header("Content-Type", "multipart/form-data")
                .body(builder.build()))
                .execute(TestResponse.class);

        assertNotNull(result);
        RecordedRequest request = mockServer.takeRequest();
        String body = request.getBody().readUtf8();
        assertTrue(body.contains("true"));
    }

    @Test
    void testMultipartFromPartsWithComplexObject() throws Exception {
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"uploaded\",\"id\":107}")
                .setHeader("Content-Type", "application/json"));

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        TestResponse complexObj = new TestResponse("complex", 888);
        builder.part("objectField", complexObj);

        TestResponse result = getBuilder(client.post()
                .uri("/api/upload")
                .header("Content-Type", "multipart/form-data")
                .body(builder.build()))
                .execute(TestResponse.class);

        assertNotNull(result);
    }

    @Test
    void testMultipartFromPartsWithComplexObjectAndSkipNull() throws Exception {
        // Test with skipNull enabled to cover the skipNull branch in buildMultipartBodyFromParts
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setSkipNull(true);
        JavaHttpClient skipNullClient = new JavaHttpClient(
                mockServer.url("/").toString(),
                config,
                null,
                new GsonJsonMapper(),
                null
        );

        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"uploaded\",\"id\":108}")
                .setHeader("Content-Type", "application/json"));

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        TestResponse complexObj = new TestResponse("complex", 999);
        builder.part("objectField", complexObj);

        TestResponse result = getBuilder(skipNullClient.post()
                .uri("/api/upload")
                .header("Content-Type", "multipart/form-data")
                .body(builder.build()))
                .execute(TestResponse.class);

        assertNotNull(result);
    }

    @Test
    void testMultipartFromPartsWithNullContentDisposition() throws Exception {
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"uploaded\",\"id\":108}")
                .setHeader("Content-Type", "application/json"));

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", "test content".getBytes());

        TestResponse result = getBuilder(client.post()
                .uri("/api/upload")
                .header("Content-Type", "multipart/form-data")
                .body(builder.build()))
                .execute(TestResponse.class);

        assertNotNull(result);
    }

    @Test
    void testMultipartFromPartsWithDifferentContentType() throws Exception {
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"uploaded\",\"id\":109}")
                .setHeader("Content-Type", "application/json"));

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", "image data".getBytes())
                .header("Content-Disposition", "form-data; name=\"file\"; filename=\"image.png\"")
                .header("Content-Type", "image/png");

        TestResponse result = getBuilder(client.post()
                .uri("/api/upload")
                .header("Content-Type", "multipart/form-data")
                .body(builder.build()))
                .execute(TestResponse.class);

        assertNotNull(result);
    }

    // ========== Query Parameters Tests ==========

    @Test
    void testRequestWithQueryParameters() throws Exception {
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"test\",\"id\":123}")
                .setHeader("Content-Type", "application/json"));

        TestResponse result = getBuilder(client.get()
                .uri("/api/test"))
                .queryParam("page", "1")
                .queryParam("limit", "10")
                .execute(TestResponse.class);

        assertNotNull(result);
        RecordedRequest request = mockServer.takeRequest();
        assertTrue(request.getPath().contains("page=1"));
        assertTrue(request.getPath().contains("limit=10"));
    }

    @Test
    void testRequestWithSpecialCharactersInQueryParams() throws Exception {
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"test\",\"id\":123}")
                .setHeader("Content-Type", "application/json"));

        TestResponse result = getBuilder(client.get()
                .uri("/api/test"))
                .queryParam("search", "hello world")
                .queryParam("filter", "a&b")
                .execute(TestResponse.class);

        assertNotNull(result);
        RecordedRequest request = mockServer.takeRequest();
        assertNotNull(request.getPath());
    }

    // ========== URL Building Tests ==========

    @Test
    void testUrlBuildingWithDoubleSlash() throws Exception {
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"test\",\"id\":123}")
                .setHeader("Content-Type", "application/json"));

        TestResponse result = getBuilder(client.get()
                .uri("/api/test"))
                .execute(TestResponse.class);

        assertNotNull(result);
        RecordedRequest request = mockServer.takeRequest();
        assertFalse(request.getPath().contains("//api"));
    }

    // ========== Error Handling Tests ==========

    @Test
    void testExecuteForStatusWithIOException() throws IOException {
        mockServer.shutdown();

        assertThrows(HttpClientException.class, () ->
                getBuilder(client.get()
                        .uri("/api/test"))
                        .executeForStatus()
        );
    }

    @Test
    void testHandleResponseWithVoidClass() throws Exception {
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("")
                .setHeader("Content-Type", "application/json"));

        Void result = getBuilder(client.get()
                .uri("/api/void"))
                .execute(Void.class);

        assertNull(result);
    }

    @Test
    void testHandleResponseWithEmptyBodyString() throws Exception {
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("")
                .setHeader("Content-Type", "application/json"));

        TestResponse result = getBuilder(client.get()
                .uri("/api/empty"))
                .execute(TestResponse.class);

        assertNull(result);
    }

    @Test
    void testHandleResponseWithNonSuccessfulAndNonEmptyBody() throws Exception {
        mockServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("{\"error\":\"bad request\"}")
                .setHeader("Content-Type", "application/json"));

        assertThrows(HttpClientException.class, () ->
                getBuilder(client.get()
                        .uri("/api/error"))
                        .execute(TestResponse.class)
        );
    }

    // ========== buildRequestBody() Branch Coverage Tests ==========

    @Test
    void testRequestBodyWithNullContentType() throws Exception {
        // Test: contentType == null, body != null -> buildJsonBody()
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"response\",\"id\":123}")
                .setHeader("Content-Type", "application/json"));

        Map<String, Object> requestBody = new java.util.HashMap<>();
        requestBody.put("test", "value");

        TestResponse result = getBuilder(client.post()
                .uri("/api/test")
                // No Content-Type header set
                .body(requestBody))
                .execute(TestResponse.class);

        assertNotNull(result);
    }

    @Test
    void testRequestBodyWithNonMultipartContentType() throws Exception {
        // Test: contentType != null but not multipart -> buildJsonBody()
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"response\",\"id\":123}")
                .setHeader("Content-Type", "application/json"));

        Map<String, Object> requestBody = new java.util.HashMap<>();
        requestBody.put("test", "value");

        TestResponse result = getBuilder(client.post()
                .uri("/api/test")
                .header("Content-Type", "application/json")
                .body(requestBody))
                .execute(TestResponse.class);

        assertNotNull(result);
    }

    @Test
    void testRequestBodyWithMultipartButNotMap() throws Exception {
        // Test: isMultipart=true but body is not Map -> buildJsonBody()
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"response\",\"id\":123}")
                .setHeader("Content-Type", "application/json"));

        String requestBody = "not a map";

        TestResponse result = getBuilder(client.post()
                .uri("/api/test")
                .header("Content-Type", "multipart/form-data")
                .body(requestBody))
                .execute(TestResponse.class);

        assertNotNull(result);
    }

    @Test
    void testRequestBodyWithEmptyMap() throws Exception {
        // Test: isMultipart=true, body is Map but empty -> buildJsonBody()
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"response\",\"id\":123}")
                .setHeader("Content-Type", "application/json"));

        Map<String, Object> emptyMap = new java.util.HashMap<>();

        TestResponse result = getBuilder(client.post()
                .uri("/api/test")
                .header("Content-Type", "multipart/form-data")
                .body(emptyMap))
                .execute(TestResponse.class);

        assertNotNull(result);
    }

    @Test
    void testRequestBodyWithMapButNotPart() throws Exception {
        // Test: isMultipart=true, body is Map but firstValue is not Part -> buildJsonBody()
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"response\",\"id\":123}")
                .setHeader("Content-Type", "application/json"));

        Map<String, Object> mapWithNonPart = new java.util.HashMap<>();
        mapWithNonPart.put("key", "regular string value");

        TestResponse result = getBuilder(client.post()
                .uri("/api/test")
                .header("Content-Type", "multipart/form-data")
                .body(mapWithNonPart))
                .execute(TestResponse.class);

        assertNotNull(result);
    }

    @Test
    void testRequestBodyWithPatchMethod() throws Exception {
        // Test: PATCH method with empty body
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"patched\",\"id\":123}")
                .setHeader("Content-Type", "application/json"));

        TestResponse result = getBuilder(client.patch()
                .uri("/api/patch"))
                .execute(TestResponse.class);

        assertNotNull(result);
        RecordedRequest request = mockServer.takeRequest();
        assertEquals("PATCH", request.getMethod());
    }

    @Test
    void testRequestBodyWithGetMethodReturnsNull() throws Exception {
        // Test: GET method returns null body
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"test\",\"id\":123}")
                .setHeader("Content-Type", "application/json"));

        TestResponse result = getBuilder(client.get()
                .uri("/api/test"))
                .execute(TestResponse.class);

        assertNotNull(result);
        RecordedRequest request = mockServer.takeRequest();
        assertEquals("GET", request.getMethod());
        // GET requests should have no body
        assertEquals(0, request.getBodySize());
    }

    @Test
    void testRequestBodyWithDeleteMethodReturnsNull() throws Exception {
        // Test: DELETE method returns null body
        mockServer.enqueue(new MockResponse()
                .setResponseCode(204)
                .setHeader("Content-Length", "0"));

        int statusCode = getBuilder(client.delete()
                .uri("/api/delete"))
                .executeForStatus();

        assertEquals(204, statusCode);
        RecordedRequest request = mockServer.takeRequest();
        assertEquals("DELETE", request.getMethod());
    }

    @Test
    void testRequestBodyWithPostAndNullBody() throws Exception {
        // Test: POST with null body and null multipartData -> empty body
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"created\",\"id\":123}")
                .setHeader("Content-Type", "application/json"));

        TestResponse result = getBuilder(client.post()
                .uri("/api/create"))
                .execute(TestResponse.class);

        assertNotNull(result);
        RecordedRequest request = mockServer.takeRequest();
        assertEquals("POST", request.getMethod());
    }

    @Test
    void testRequestBodyWithPutAndNullBody() throws Exception {
        // Test: PUT with null body and null multipartData -> empty body
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"updated\",\"id\":123}")
                .setHeader("Content-Type", "application/json"));

        TestResponse result = getBuilder(client.put()
                .uri("/api/update"))
                .execute(TestResponse.class);

        assertNotNull(result);
        RecordedRequest request = mockServer.takeRequest();
        assertEquals("PUT", request.getMethod());
    }

    @Test
    void testRequestBodyWithPatchAndNullBody() throws Exception {
        // Test: PATCH with null body and null multipartData -> empty body
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"patched\",\"id\":123}")
                .setHeader("Content-Type", "application/json"));

        TestResponse result = getBuilder(client.patch()
                .uri("/api/patch"))
                .execute(TestResponse.class);

        assertNotNull(result);
        RecordedRequest request = mockServer.takeRequest();
        assertEquals("PATCH", request.getMethod());
    }

    @Test
    void testRequestBodyWithMultipartContentTypeVariations() throws Exception {
        // Test: Content-Type with different case variations
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"uploaded\",\"id\":123}")
                .setHeader("Content-Type", "application/json"));

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("field", "value");

        TestResponse result = getBuilder(client.post()
                .uri("/api/upload")
                .header("Content-Type", "MULTIPART/FORM-DATA; boundary=----")
                .body(builder.build()))
                .execute(TestResponse.class);

        assertNotNull(result);
    }

    // ========== handleError() Coverage Tests ==========

    @Test
    void testHandleErrorWith400BadRequest() {
        // Test 400 Bad Request error
        mockServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("{\"error\":\"Bad request\",\"message\":\"Invalid parameters\"}"));

        HttpClientException exception = assertThrows(HttpClientException.class, () ->
                getBuilder(client.post()
                        .uri("/api/bad-request"))
                        .execute(TestResponse.class)
        );

        assertEquals(400, exception.getStatusCode());
        assertTrue(exception.getResponseBody().contains("Bad request"));
    }

    @Test
    void testHandleErrorWith401Unauthorized() {
        // Test 401 Unauthorized error
        mockServer.enqueue(new MockResponse()
                .setResponseCode(401)
                .setBody("{\"error\":\"Unauthorized\",\"message\":\"Invalid credentials\"}"));

        HttpClientException exception = assertThrows(HttpClientException.class, () ->
                getBuilder(client.get()
                        .uri("/api/protected"))
                        .execute(TestResponse.class)
        );

        assertEquals(401, exception.getStatusCode());
        assertTrue(exception.getResponseBody().contains("Unauthorized"));
    }

    @Test
    void testHandleErrorWith403Forbidden() {
        // Test 403 Forbidden error
        mockServer.enqueue(new MockResponse()
                .setResponseCode(403)
                .setBody("{\"error\":\"Forbidden\",\"message\":\"Access denied\"}"));

        HttpClientException exception = assertThrows(HttpClientException.class, () ->
                getBuilder(client.get()
                        .uri("/api/forbidden"))
                        .execute(TestResponse.class)
        );

        assertEquals(403, exception.getStatusCode());
        assertTrue(exception.getResponseBody().contains("Forbidden"));
    }

    @Test
    void testHandleErrorWith404NotFound() {
        // Test 404 Not Found error
        mockServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("{\"error\":\"Not found\",\"message\":\"Resource not found\"}"));

        HttpClientException exception = assertThrows(HttpClientException.class, () ->
                getBuilder(client.get()
                        .uri("/api/notfound"))
                        .execute(TestResponse.class)
        );

        assertEquals(404, exception.getStatusCode());
        assertTrue(exception.getResponseBody().contains("Not found"));
    }

    @Test
    void testHandleErrorWith500InternalServerError() {
        // Test 500 Internal Server Error
        mockServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("{\"error\":\"Internal server error\",\"message\":\"Something went wrong\"}"));

        HttpClientException exception = assertThrows(HttpClientException.class, () ->
                getBuilder(client.get()
                        .uri("/api/error"))
                        .execute(TestResponse.class)
        );

        assertEquals(500, exception.getStatusCode());
        assertTrue(exception.getResponseBody().contains("Internal server error"));
    }

    @Test
    void testHandleErrorWith502BadGateway() {
        // Test 502 Bad Gateway error
        mockServer.enqueue(new MockResponse()
                .setResponseCode(502)
                .setBody("{\"error\":\"Bad gateway\"}"));

        HttpClientException exception = assertThrows(HttpClientException.class, () ->
                getBuilder(client.get()
                        .uri("/api/gateway"))
                        .execute(TestResponse.class)
        );

        assertEquals(502, exception.getStatusCode());
        assertTrue(exception.getResponseBody().contains("Bad gateway"));
    }

    @Test
    void testHandleErrorWith503ServiceUnavailable() {
        // Test 503 Service Unavailable error
        mockServer.enqueue(new MockResponse()
                .setResponseCode(503)
                .setBody("{\"error\":\"Service unavailable\"}"));

        HttpClientException exception = assertThrows(HttpClientException.class, () ->
                getBuilder(client.get()
                        .uri("/api/unavailable"))
                        .execute(TestResponse.class)
        );

        assertEquals(503, exception.getStatusCode());
        assertTrue(exception.getResponseBody().contains("Service unavailable"));
    }

    @Test
    void testHandleErrorWithEmptyResponseBody() {
        // Test error with empty response body (response.body() != null but string is empty)
        mockServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody(""));

        HttpClientException exception = assertThrows(HttpClientException.class, () ->
                getBuilder(client.get()
                        .uri("/api/error-empty"))
                        .execute(TestResponse.class)
        );

        assertEquals(500, exception.getStatusCode());
        assertEquals("", exception.getResponseBody());
    }

    @Test
    void testHandleErrorWithPlainTextResponse() {
        // Test error with plain text response (not JSON)
        mockServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
                .setHeader("Content-Type", "text/plain"));

        HttpClientException exception = assertThrows(HttpClientException.class, () ->
                getBuilder(client.get()
                        .uri("/api/error-text"))
                        .execute(TestResponse.class)
        );

        assertEquals(500, exception.getStatusCode());
        assertEquals("Internal Server Error", exception.getResponseBody());
    }

    @Test
    void testHandleErrorInExecuteForResponse() {
        // Test handleError is called in executeForResponse method
        mockServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("{\"error\":\"Not found\"}"));

        HttpClientException exception = assertThrows(HttpClientException.class, () ->
                getBuilder(client.get()
                        .uri("/api/notfound"))
                        .executeForResponse(TestResponse.class)
        );

        assertEquals(404, exception.getStatusCode());
    }

    @Test
    void testHandleErrorInExecuteForResponseWithTypeReference() {
        // Test handleError is called in executeForResponse with TypeReference
        mockServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("{\"error\":\"Server error\"}"));

        HttpClientException exception = assertThrows(HttpClientException.class, () ->
                getBuilder(client.get()
                        .uri("/api/error"))
                        .executeForResponse(new TypeReference<List<TestResponse>>() {
                        })
        );

        assertEquals(500, exception.getStatusCode());
    }

    @Test
    void testHandleErrorInExecuteForStatus() {
        // Test handleError is called in executeForStatus method
        mockServer.enqueue(new MockResponse()
                .setResponseCode(403)
                .setBody("{\"error\":\"Forbidden\"}"));

        HttpClientException exception = assertThrows(HttpClientException.class, () ->
                getBuilder(client.get()
                        .uri("/api/forbidden"))
                        .executeForStatus()
        );

        assertEquals(403, exception.getStatusCode());
    }

    @Test
    void testHandleErrorWithLargeResponseBody() {
        // Test error with large response body
        StringBuilder largeBody = new StringBuilder("{\"error\":\"Large error\",\"details\":\"");
        for (int i = 0; i < 1000; i++) {
            largeBody.append("Error detail ").append(i).append(". ");
        }
        largeBody.append("\"}");

        mockServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody(largeBody.toString()));

        HttpClientException exception = assertThrows(HttpClientException.class, () ->
                getBuilder(client.post()
                        .uri("/api/large-error"))
                        .execute(TestResponse.class)
        );

        assertEquals(500, exception.getStatusCode());
        assertTrue(exception.getResponseBody().contains("Large error"));
    }

    @Test
    void testHandleErrorWithSpecialCharactersInResponseBody() {
        // Test error with special characters in response body
        mockServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("{\"error\":\"Invalid input\",\"message\":\"Special chars: <>&\\\"'\"}"));

        HttpClientException exception = assertThrows(HttpClientException.class, () ->
                getBuilder(client.post()
                        .uri("/api/special"))
                        .execute(TestResponse.class)
        );

        assertEquals(400, exception.getStatusCode());
        assertTrue(exception.getResponseBody().contains("Special chars"));
    }

    // ========== handleResponse with TypeReference Coverage Tests ==========

    @Test
    void testHandleResponseTypeReferenceWithError() {
        // Test handleError branch in handleResponse(TypeReference, checkError=true) when response is not successful
        // This tests the path: execute(TypeReference) -> handleResponse(response, typeReference) -> handleResponse(response, typeReference, true)
        mockServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("{\"error\":\"Not found\"}"));

        HttpClientException exception = assertThrows(HttpClientException.class, () ->
                getBuilder(client.get()
                        .uri("/api/notfound"))
                        .execute(new TypeReference<List<TestResponse>>() {
                        })
        );

        assertEquals(404, exception.getStatusCode());
        assertTrue(exception.getResponseBody().contains("Not found"));
    }

    @Test
    void testHandleResponseTypeReferenceWithError500() {
        // Additional test for handleError branch with 500 error
        mockServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("{\"error\":\"Internal server error\"}"));

        HttpClientException exception = assertThrows(HttpClientException.class, () ->
                getBuilder(client.get()
                        .uri("/api/error"))
                        .execute(new TypeReference<Map<String, String>>() {
                        })
        );

        assertEquals(500, exception.getStatusCode());
    }

    @Test
    void testHandleResponseTypeReferenceWithError400() {
        // Additional test for handleError branch with 400 error
        mockServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("{\"error\":\"Bad request\"}"));

        HttpClientException exception = assertThrows(HttpClientException.class, () ->
                getBuilder(client.post()
                        .uri("/api/bad"))
                        .execute(new TypeReference<List<TestResponse>>() {
                        })
        );

        assertEquals(400, exception.getStatusCode());
    }

    @Test
    void testHandleResponseTypeReferenceWithNullBody() throws Exception {
        // Test return null branch when responseBody == null
        // Using 204 No Content which should have no body
        mockServer.enqueue(new MockResponse()
                .setResponseCode(204));

        List<TestResponse> result = getBuilder(client.get()
                .uri("/api/nocontent"))
                .execute(new TypeReference<List<TestResponse>>() {
                });

        assertNull(result);
    }

    @Test
    void testHandleResponseTypeReferenceWithNullBodyIn205() throws Exception {
        // Test return null branch with 205 Reset Content
        mockServer.enqueue(new MockResponse()
                .setResponseCode(205));

        Map<String, String> result = getBuilder(client.get()
                .uri("/api/reset"))
                .execute(new TypeReference<Map<String, String>>() {
                });

        assertNull(result);
    }

    @Test
    void testHandleResponseTypeReferenceWithEmptyBodyString() throws Exception {
        // Test return null branch when bodyString.isEmpty()
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("")
                .setHeader("Content-Type", "application/json"));

        List<TestResponse> result = getBuilder(client.get()
                .uri("/api/empty"))
                .execute(new TypeReference<List<TestResponse>>() {
                });

        assertNull(result);
    }

    @Test
    void testHandleResponseTypeReferenceWithDeserializationError() {
        // Test Exception branch when JSON deserialization fails
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{invalid json syntax}")
                .setHeader("Content-Type", "application/json"));

        assertThrows(HttpClientException.class, () ->
                getBuilder(client.get()
                        .uri("/api/invalid"))
                        .execute(new TypeReference<List<TestResponse>>() {
                        })
        );
    }

    @Test
    void testHandleResponseTypeReferenceWithMalformedJson() {
        // Test Exception branch with malformed JSON
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("[{\"name\":\"test\",\"id\":}]")  // Missing value for id
                .setHeader("Content-Type", "application/json"));

        assertThrows(HttpClientException.class, () ->
                getBuilder(client.get()
                        .uri("/api/malformed"))
                        .execute(new TypeReference<List<TestResponse>>() {
                        })
        );
    }

    @Test
    void testHandleResponseTypeReferenceWithTypeMismatch() {
        // Test Exception branch when response type doesn't match expected type
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"name\":\"test\",\"id\":123}")  // Single object, not a list
                .setHeader("Content-Type", "application/json"));

        assertThrows(HttpClientException.class, () ->
                getBuilder(client.get()
                        .uri("/api/mismatch"))
                        .execute(new TypeReference<List<TestResponse>>() {
                        })
        );
    }

    @Test
    void testExecuteForResponseTypeReferenceWithNullBody() throws Exception {
        // Test return null in executeForResponse with TypeReference when body is null
        mockServer.enqueue(new MockResponse()
                .setResponseCode(204)
                .setHeader("Content-Length", "0"));

        HttpResponse<List<TestResponse>> response = getBuilder(client.get()
                .uri("/api/nocontent"))
                .executeForResponse(new TypeReference<List<TestResponse>>() {
                });

        assertNotNull(response);
        assertEquals(204, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testExecuteForResponseTypeReferenceWithEmptyBody() throws Exception {
        // Test return null in executeForResponse with TypeReference when body is empty
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("")
                .setHeader("Content-Type", "application/json"));

        HttpResponse<List<TestResponse>> response = getBuilder(client.get()
                .uri("/api/empty"))
                .executeForResponse(new TypeReference<List<TestResponse>>() {
                });

        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testExecuteForResponseTypeReferenceWithDeserializationError() {
        // Test Exception in executeForResponse with TypeReference
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("not a valid json")
                .setHeader("Content-Type", "application/json"));

        assertThrows(HttpClientException.class, () ->
                getBuilder(client.get()
                        .uri("/api/invalid"))
                        .executeForResponse(new TypeReference<List<TestResponse>>() {
                        })
        );
    }

    @Test
    void testExecuteForResponseTypeReferenceWithError() {
        // Test handleError is called in executeForResponse(TypeReference)
        mockServer.enqueue(new MockResponse()
                .setResponseCode(401)
                .setBody("{\"error\":\"Unauthorized\"}"));

        HttpClientException exception = assertThrows(HttpClientException.class, () ->
                getBuilder(client.get()
                        .uri("/api/protected"))
                        .executeForResponse(new TypeReference<List<TestResponse>>() {
                        })
        );

        assertEquals(401, exception.getStatusCode());
    }

    @Test
    void testExecuteTypeReferenceWithMultipleErrorCodes() {
        // Test handleError with different error codes to ensure branch coverage
        int[] errorCodes = {400, 401, 403, 404, 500, 502, 503};

        for (int errorCode : errorCodes) {
            mockServer.enqueue(new MockResponse()
                    .setResponseCode(errorCode)
                    .setBody("{\"error\":\"Error " + errorCode + "\"}"));

            HttpClientException exception = assertThrows(HttpClientException.class, () ->
                    getBuilder(client.get()
                            .uri("/api/error"))
                            .execute(new TypeReference<List<TestResponse>>() {
                            })
            );

            assertEquals(errorCode, exception.getStatusCode());
        }
    }

    // ========== buildJsonBody and buildMultipartBody Exception Coverage Tests ==========

    /**
     * Helper class with circular reference to trigger JSON serialization error
     */
    static class CircularReferenceObject {
        String name;
        CircularReferenceObject self;

        public CircularReferenceObject(String name) {
            this.name = name;
            this.self = this; // Circular reference
        }
    }

    /**
     * Helper class that cannot be serialized
     */
    static class UnserializableObject {
        private final Thread thread = new Thread(); // Thread objects cannot be serialized by Gson
        String name;

        public UnserializableObject(String name) {
            this.name = name;
        }
    }

    @Test
    void testBuildJsonBodyWithUnserializableObject() {
        // Test Exception branch in buildJsonBody with unserializable object
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"result\":\"ok\"}")
                .setHeader("Content-Type", "application/json"));

        UnserializableObject unserializable = new UnserializableObject("test");

        assertThrows(HttpClientException.class, () ->
                getBuilder(client.post()
                        .uri("/api/create")
                        .body(unserializable))
                        .execute(TestResponse.class)
        );
    }

    @Test
    void testBuildMultipartBodyWithUnserializableComplexObject() {
        // Test Exception branch in buildMultipartBody with unserializable complex object
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"result\":\"ok\"}")
                .setHeader("Content-Type", "application/json"));

        Map<String, Object> formData = new java.util.HashMap<>();
        formData.put("field1", "value1");
        formData.put("unserializable", new UnserializableObject("test"));

        assertThrows(HttpClientException.class, () ->
                {
                    TestResponse execute = getBuilder(client.post()
                            .uri("/api/upload"))
                            .multipart(formData)
                            .execute(TestResponse.class);
                }
        );
    }

    @Test
    void testBuildMultipartBodyFromPartsWithUnserializableObject() {
        // Test Exception branch in buildMultipartBodyFromParts with unserializable object
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"result\":\"ok\"}")
                .setHeader("Content-Type", "application/json"));

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("field1", "value1");
        builder.part("unserializable", new UnserializableObject("test"));

        assertThrows(HttpClientException.class, () ->
                getBuilder(client.post()
                        .uri("/api/upload")
                        .header("Content-Type", "multipart/form-data")
                        .body(builder.build()))
                        .execute(TestResponse.class)
        );
    }

    // ========== handleResponse(Class, boolean) Coverage Tests ==========

    @Test
    void testHandleResponseClassWithErrorAndCheckError() {
        // Test handleError branch in handleResponse(Class, checkError=true) when response is not successful
        mockServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("{\"error\":\"Not found\"}"));

        HttpClientException exception = assertThrows(HttpClientException.class, () ->
                getBuilder(client.get()
                        .uri("/api/notfound"))
                        .execute(TestResponse.class)
        );

        assertEquals(404, exception.getStatusCode());
    }

    @Test
    void testHandleResponseClassWithNullBody() throws Exception {
        // Test return null branch when responseBody == null
        mockServer.enqueue(new MockResponse()
                .setResponseCode(204));

        TestResponse result = getBuilder(client.get()
                .uri("/api/nocontent"))
                .execute(TestResponse.class);

        assertNull(result);
    }

    @Test
    void testHandleResponseClassWithError() throws Exception {
        // Test return null branch when responseBody == null
        mockServer.enqueue(new MockResponse()
                .setResponseCode(500));

        assertThrows(HttpClientException.class, () -> {
            getBuilder(client.get()
                    .uri("/api/nocontent"))
                    .execute(TestResponse.class);
        });
    }

    @Test
    void testHandleResponseClassWithEmptyBodyString() throws Exception {
        // Test return null branch when bodyString.isEmpty()
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("")
                .setHeader("Content-Type", "application/json"));

        TestResponse result = getBuilder(client.get()
                .uri("/api/empty"))
                .execute(TestResponse.class);

        assertNull(result);
    }

    @Test
    void testHandleResponseClassWithUnsuccessfulAndNonEmptyBody() throws Exception {
        // Test return null branch when !response.isSuccessful() but checkError=false
        // This happens in retrieve().toEntity() path where checkError=false for success responses
        // But we need to test the case where response is not successful and checkError=false
        // This is tested in OkHttpResponseSpec.toEntity() which handles errors differently

        // For direct execute() call, if response is not successful, handleError is called first
        // So we test the path through executeForResponse which also uses handleResponse
        mockServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("{\"error\":\"Bad request\"}"));

        assertThrows(HttpClientException.class, () ->
                getBuilder(client.get()
                        .uri("/api/bad"))
                        .execute(TestResponse.class)
        );
    }

    @Test
    void testHandleResponseClassWithVoidType() throws Exception {
        // Test return null branch when responseType == Void.class
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"result\":\"ok\"}")
                .setHeader("Content-Type", "application/json"));

        Void result = getBuilder(client.get()
                .uri("/api/void"))
                .execute(Void.class);

        assertNull(result);
    }

    @Test
    void testHandleResponseClassWithVoidPrimitiveType() throws Exception {
        // Test return null branch when responseType == void.class
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"result\":\"ok\"}")
                .setHeader("Content-Type", "application/json"));

        Object result = getBuilder(client.get()
                .uri("/api/void"))
                .execute(void.class);

        assertNull(result);
    }

    @Test
    void testHandleResponseClassWithByteArrayType() throws Exception {
        // Test byte array response handling
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("test data")
                .setHeader("Content-Type", "application/octet-stream"));

        byte[] result = getBuilder(client.get()
                .uri("/api/bytes"))
                .execute(byte[].class);

        assertNotNull(result);
        assertArrayEquals("test data".getBytes(), result);
    }

    @Test
    void testHandleResponseClassWithStringType() throws Exception {
        // Test String response handling
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("test string")
                .setHeader("Content-Type", "text/plain"));

        String result = getBuilder(client.get()
                .uri("/api/string"))
                .execute(String.class);

        assertNotNull(result);
        assertEquals("test string", result);
    }

    @Test
    void testHandleResponseClassWithDeserializationError() {
        // Test Exception branch when JSON deserialization fails
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{invalid json}")
                .setHeader("Content-Type", "application/json"));

        assertThrows(HttpClientException.class, () ->
                getBuilder(client.get()
                        .uri("/api/invalid"))
                        .execute(TestResponse.class)
        );
    }

    @Test
    void testExecuteForResponseClassWithNullBody() throws Exception {
        // Test return null in executeForResponse when body is null
        mockServer.enqueue(new MockResponse()
                .setResponseCode(204));

        HttpResponse<TestResponse> response = getBuilder(client.get()
                .uri("/api/nocontent"))
                .executeForResponse(TestResponse.class);

        assertNotNull(response);
        assertEquals(204, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testExecuteForResponseClassWithEmptyBody() throws Exception {
        // Test return null in executeForResponse when body is empty
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("")
                .setHeader("Content-Type", "application/json"));

        HttpResponse<TestResponse> response = getBuilder(client.get()
                .uri("/api/empty"))
                .executeForResponse(TestResponse.class);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testExecuteForResponseClassWithVoidType() throws Exception {
        // Test executeForResponse with Void.class
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"result\":\"ok\"}")
                .setHeader("Content-Type", "application/json"));

        HttpResponse<Void> response = getBuilder(client.get()
                .uri("/api/void"))
                .executeForResponse(Void.class);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testHandleResponseClassWithMultipleStatusCodes() throws Exception {
        // Test handleResponse with various 2xx success codes
        int[] successCodes = {200, 201, 202, 204};

        for (int statusCode : successCodes) {
            if (statusCode == 204) {
                mockServer.enqueue(new MockResponse()
                        .setResponseCode(statusCode));
            } else {
                mockServer.enqueue(new MockResponse()
                        .setResponseCode(statusCode)
                        .setBody("{\"name\":\"test\",\"id\":123}")
                        .setHeader("Content-Type", "application/json"));
            }

            TestResponse result = getBuilder(client.get()
                    .uri("/api/test"))
                    .execute(TestResponse.class);

            if (statusCode == 204) {
                assertNull(result);
            } else {
                assertNotNull(result);
            }
        }
    }

}
