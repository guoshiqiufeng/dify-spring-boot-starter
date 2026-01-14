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
import io.github.guoshiqiufeng.dify.client.core.http.HttpHeaders;
import io.github.guoshiqiufeng.dify.client.core.http.HttpRequestBuilder;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SpringHttpRequestBuilder
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/1/13
 */
@ExtendWith(MockitoExtension.class)
class SpringHttpRequestBuilderTest {

    @Mock
    private SpringHttpClient client;

    @Mock
    private JsonMapper jsonMapper;

    @Mock
    private WebClient webClient;

    private SpringHttpRequestBuilder builder;

    @BeforeEach
    void setUp() {
        when(client.getWebClient()).thenReturn(webClient);
        when(client.hasRestClient()).thenReturn(false);
        when(client.getClientConfig()).thenReturn(new DifyProperties.ClientConfig());

        builder = new SpringHttpRequestBuilder(client, "GET", jsonMapper, null);
    }

    @Test
    void testConstructor() {
        // Assert
        assertNotNull(builder);
    }

    @Test
    void testConstructorWithDefaultHeaders() {
        // Arrange
        HttpHeaders defaultHeaders = new HttpHeaders();
        defaultHeaders.add("Authorization", "Bearer token");
        defaultHeaders.add("X-Custom-Header", "custom-value");

        // Act
        SpringHttpRequestBuilder builder = new SpringHttpRequestBuilder(client, "POST", jsonMapper, defaultHeaders);

        // Assert
        assertNotNull(builder);
    }

    @Test
    void testUriWithString() {
        // Act
        HttpRequestBuilder result = builder.uri("/api/users");

        // Assert
        assertNotNull(result);
        assertSame(builder, result, "Should return same instance for method chaining");
    }

    @Test
    void testUriWithStringAndParams() {
        // Act
        HttpRequestBuilder result = builder.uri("/api/users/{id}", 123);

        // Assert
        assertNotNull(result);
        assertSame(builder, result);
    }

    @Test
    void testHeaderMethod() {
        // Act
        HttpRequestBuilder result = builder.header("Content-Type", "application/json");

        // Assert
        assertNotNull(result);
        assertSame(builder, result);
    }

    @Test
    void testHeadersMethod() {
        // Arrange
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer token");
        headers.put("X-Custom-Header", "value");

        // Act
        HttpRequestBuilder result = builder.headers(headers);

        // Assert
        assertNotNull(result);
        assertSame(builder, result);
    }

    @Test
    void testQueryParamMethod() {
        // Act
        HttpRequestBuilder result = builder.queryParam("page", "1");

        // Assert
        assertNotNull(result);
        assertSame(builder, result);
    }

    @Test
    void testQueryParamsMethod() {
        // Arrange
        Map<String, String> params = new HashMap<>();
        params.put("page", "1");
        params.put("limit", "10");

        // Act
        HttpRequestBuilder result = builder.queryParams(params);

        // Assert
        assertNotNull(result);
        assertSame(builder, result);
    }

    @Test
    void testBodyMethod() {
        // Arrange
        Map<String, Object> body = new HashMap<>();
        body.put("name", "test");

        // Act
        HttpRequestBuilder result = builder.body(body);

        // Assert
        assertNotNull(result);
        assertSame(builder, result);
    }

    @Test
    void testMultipartMethod() {
        // Arrange
        Map<String, Object> formData = new HashMap<>();
        formData.put("file", "data");

        // Act
        HttpRequestBuilder result = builder.multipart(formData);

        // Assert
        assertNotNull(result);
        assertSame(builder, result);
    }

    @Test
    void testReplaceUriVariablesSimplePath() throws Exception {
        // Arrange
        Method method = SpringHttpRequestBuilder.class.getDeclaredMethod("replaceUriVariables", String.class, Object[].class);
        method.setAccessible(true);

        // Act
        String result = (String) method.invoke(builder, "/api/users/{id}", new Object[]{123});

        // Assert
        assertEquals("/api/users/123", result);
    }

    @Test
    void testReplaceUriVariablesMultiplePlaceholders() throws Exception {
        // Arrange
        Method method = SpringHttpRequestBuilder.class.getDeclaredMethod("replaceUriVariables", String.class, Object[].class);
        method.setAccessible(true);

        // Act
        String result = (String) method.invoke(builder, "/api/users/{id}/posts/{postId}", new Object[]{123, 456});

        // Assert
        assertEquals("/api/users/123/posts/456", result);
    }

    @Test
    void testReplaceUriVariablesNoPlaceholders() throws Exception {
        // Arrange
        Method method = SpringHttpRequestBuilder.class.getDeclaredMethod("replaceUriVariables", String.class, Object[].class);
        method.setAccessible(true);

        // Act
        String result = (String) method.invoke(builder, "/api/users", new Object[]{});

        // Assert
        assertEquals("/api/users", result);
    }

    @Test
    void testReplaceUriVariablesWithQueryParameters() throws Exception {
        // Arrange
        Method method = SpringHttpRequestBuilder.class.getDeclaredMethod("replaceUriVariables", String.class, Object[].class);
        method.setAccessible(true);

        // Act
        String result = (String) method.invoke(builder, "/api/users/{id}?page={page}&limit={limit}", new Object[]{123, 1, 10});

        // Assert
        assertEquals("/api/users/123", result);
    }

    @Test
    void testReplaceUriVariablesWithNullQueryParam() throws Exception {
        // Arrange
        Method method = SpringHttpRequestBuilder.class.getDeclaredMethod("replaceUriVariables", String.class, Object[].class);
        method.setAccessible(true);

        // Act
        String result = (String) method.invoke(builder, "/api/users/{id}?page={page}", new Object[]{123, null});

        // Assert
        assertEquals("/api/users/123", result);
    }

    @Test
    void testMethodChaining() {
        // Act & Assert - test that all methods return the same instance for chaining
        HttpRequestBuilder result = builder
                .uri("/api/users")
                .header("Content-Type", "application/json")
                .queryParam("page", "1")
                .body(new HashMap<>());

        assertSame(builder, result);
    }

    @Test
    void testRetrieveMethod() {
        // Act
        var responseSpec = builder.retrieve();

        // Assert
        assertNotNull(responseSpec);
    }

    @Test
    void testUriWithConsumer() {
        // Act
        HttpRequestBuilder result = builder.uri(uriBuilder -> {
            uriBuilder.path("/api/users");
            uriBuilder.queryParam("page", "1");
        });

        // Assert
        assertNotNull(result);
        assertSame(builder, result);
    }

    @Test
    void testHeadersWithConsumer() {
        // Act
        HttpRequestBuilder result = builder.headers(headers -> {
            headers.add("Content-Type", "application/json");
            headers.add("Authorization", "Bearer token");
        });

        // Assert
        assertNotNull(result);
        assertSame(builder, result);
    }

    @Test
    void testCookiesWithConsumer() {
        // Act
        HttpRequestBuilder result = builder.cookies(cookies -> {
            cookies.add("sessionId", "abc123");
            cookies.add("userId", "user456");
        });

        // Assert
        assertNotNull(result);
        assertSame(builder, result);
    }

    @Test
    void testHeadersWithEmptyConsumer() {
        // Act
        HttpRequestBuilder result = builder.headers(headers -> {
            // Empty consumer - no headers added
        });

        // Assert
        assertNotNull(result);
        assertSame(builder, result);
    }

    @Test
    void testCookiesWithEmptyConsumer() {
        // Act
        HttpRequestBuilder result = builder.cookies(cookies -> {
            // Empty consumer - no cookies added
        });

        // Assert
        assertNotNull(result);
        assertSame(builder, result);
    }

    @Test
    void testMultipleHeadersWithConsumer() {
        // Act
        HttpRequestBuilder result = builder.headers(headers -> {
            headers.add("Content-Type", "application/json");
            headers.add("Authorization", "Bearer token");
            headers.add("X-Custom-1", "value1");
            headers.add("X-Custom-2", "value2");
        });

        // Assert
        assertNotNull(result);
        assertSame(builder, result);
    }

    @Test
    void testExecuteWithClass() throws Exception {
        // Arrange
        builder.uri("/api/test");

        // Use reflection to access webClientExecutor
        java.lang.reflect.Field executorField = SpringHttpRequestBuilder.class.getDeclaredField("webClientExecutor");
        executorField.setAccessible(true);
        WebClientExecutor mockExecutor = mock(WebClientExecutor.class);
        executorField.set(builder, mockExecutor);

        when(mockExecutor.execute(anyString(), any(URI.class), any(), any(), any(), any(), eq(String.class)))
            .thenReturn("test response");

        // Act
        String result = builder.execute(String.class);

        // Assert
        assertEquals("test response", result);
        verify(mockExecutor).execute(anyString(), any(URI.class), any(), any(), any(), any(), eq(String.class));
    }

    @Test
    void testExecuteForStatusMethod() {
        // Arrange
        builder.uri("/api/test");

        try {
            java.lang.reflect.Field executorField = SpringHttpRequestBuilder.class.getDeclaredField("webClientExecutor");
            executorField.setAccessible(true);
            WebClientExecutor mockExecutor = mock(WebClientExecutor.class);
            executorField.set(builder, mockExecutor);

            when(mockExecutor.execute(anyString(), any(URI.class), any(), any(), any(), any(), eq(Void.class)))
                .thenReturn(null);

            // Act
            int status = builder.executeForStatus();

            // Assert
            assertEquals(200, status);
        } catch (Exception e) {
            fail("Test failed with exception: " + e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    void testExecuteWithTypeReference() throws Exception {
        // Arrange
        builder.uri("/api/test");
        io.github.guoshiqiufeng.dify.client.core.http.TypeReference<String> typeRef =
            new io.github.guoshiqiufeng.dify.client.core.http.TypeReference<String>() {};

        java.lang.reflect.Field executorField = SpringHttpRequestBuilder.class.getDeclaredField("webClientExecutor");
        executorField.setAccessible(true);
        WebClientExecutor mockExecutor = mock(WebClientExecutor.class);
        executorField.set(builder, mockExecutor);

        when(mockExecutor.execute(anyString(), any(URI.class), any(), any(), any(), any(),
            any(io.github.guoshiqiufeng.dify.client.core.http.TypeReference.class)))
            .thenReturn("test response");

        // Act
        String result = builder.execute(typeRef);

        // Assert
        assertEquals("test response", result);
    }

    @Test
    void testExecuteForResponseWithClass() throws Exception {
        // Arrange
        builder.uri("/api/test");

        java.lang.reflect.Field executorField = SpringHttpRequestBuilder.class.getDeclaredField("webClientExecutor");
        executorField.setAccessible(true);
        WebClientExecutor mockExecutor = mock(WebClientExecutor.class);
        executorField.set(builder, mockExecutor);

        when(mockExecutor.execute(anyString(), any(URI.class), any(), any(), any(), any(), eq(String.class)))
            .thenReturn("test body");

        // Act
        var response = builder.executeForResponse(String.class);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertEquals("test body", response.getBody());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testExecuteForResponseWithTypeReference() throws Exception {
        // Arrange
        builder.uri("/api/test");
        io.github.guoshiqiufeng.dify.client.core.http.TypeReference<String> typeRef =
            new io.github.guoshiqiufeng.dify.client.core.http.TypeReference<String>() {};

        java.lang.reflect.Field executorField = SpringHttpRequestBuilder.class.getDeclaredField("webClientExecutor");
        executorField.setAccessible(true);
        WebClientExecutor mockExecutor = mock(WebClientExecutor.class);
        executorField.set(builder, mockExecutor);

        when(mockExecutor.execute(anyString(), any(URI.class), any(), any(), any(), any(),
            any(io.github.guoshiqiufeng.dify.client.core.http.TypeReference.class)))
            .thenReturn("test body");

        // Act
        var response = builder.executeForResponse(typeRef);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCode());
        assertEquals("test body", response.getBody());
    }

    @Test
    void testResponseSpecOnStatus() throws Exception {
        // Arrange
        var responseSpec = builder.retrieve();
        io.github.guoshiqiufeng.dify.client.core.http.ResponseErrorHandler errorHandler =
            io.github.guoshiqiufeng.dify.client.core.http.ResponseErrorHandler.onStatus(
                status -> status >= 400,
                response -> {
                    // Error handler logic
                }
            );

        // Act
        var result = responseSpec.onStatus(errorHandler);

        // Assert
        assertNotNull(result);
        assertSame(responseSpec, result);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testResponseSpecToEntityWithClass() throws Exception {
        // Arrange
        builder.uri("/api/test");

        java.lang.reflect.Field executorField = SpringHttpRequestBuilder.class.getDeclaredField("webClientExecutor");
        executorField.setAccessible(true);
        WebClientExecutor mockExecutor = mock(WebClientExecutor.class);
        executorField.set(builder, mockExecutor);

        io.github.guoshiqiufeng.dify.client.core.response.HttpResponse<String> mockResponse =
            mock(io.github.guoshiqiufeng.dify.client.core.response.HttpResponse.class);
        when(mockResponse.getStatusCode()).thenReturn(200);
        when(mockResponse.getBody()).thenReturn("test body");

        when(mockExecutor.executeForEntity(anyString(), any(URI.class), any(), any(), any(), any(), eq(String.class)))
            .thenReturn(mockResponse);

        // Act
        var responseSpec = builder.retrieve();
        var result = responseSpec.toEntity(String.class);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.getStatusCode());
        assertEquals("test body", result.getBody());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testResponseSpecToEntityWithTypeReference() throws Exception {
        // Arrange
        builder.uri("/api/test");

        java.lang.reflect.Field executorField = SpringHttpRequestBuilder.class.getDeclaredField("webClientExecutor");
        executorField.setAccessible(true);
        WebClientExecutor mockExecutor = mock(WebClientExecutor.class);
        executorField.set(builder, mockExecutor);

        io.github.guoshiqiufeng.dify.client.core.response.HttpResponse<String> mockResponse =
            mock(io.github.guoshiqiufeng.dify.client.core.response.HttpResponse.class);
        when(mockResponse.getStatusCode()).thenReturn(200);
        when(mockResponse.getBody()).thenReturn("test body");

        when(mockExecutor.executeForEntity(anyString(), any(URI.class), any(), any(), any(), any(),
            any(io.github.guoshiqiufeng.dify.client.core.http.TypeReference.class)))
            .thenReturn(mockResponse);

        // Act
        var responseSpec = builder.retrieve();
        io.github.guoshiqiufeng.dify.client.core.http.TypeReference<String> typeRef =
            new io.github.guoshiqiufeng.dify.client.core.http.TypeReference<String>() {};
        var result = responseSpec.toEntity(typeRef);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.getStatusCode());
        assertEquals("test body", result.getBody());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testResponseSpecToBodilessEntity() throws Exception {
        // Arrange
        builder.uri("/api/test");

        java.lang.reflect.Field executorField = SpringHttpRequestBuilder.class.getDeclaredField("webClientExecutor");
        executorField.setAccessible(true);
        WebClientExecutor mockExecutor = mock(WebClientExecutor.class);
        executorField.set(builder, mockExecutor);

        io.github.guoshiqiufeng.dify.client.core.response.HttpResponse<Void> mockResponse =
            mock(io.github.guoshiqiufeng.dify.client.core.response.HttpResponse.class);
        when(mockResponse.getStatusCode()).thenReturn(204);

        when(mockExecutor.executeForEntity(anyString(), any(URI.class), any(), any(), any(), any(), eq(Void.class)))
            .thenReturn(mockResponse);

        // Act
        var responseSpec = builder.retrieve();
        var result = responseSpec.toBodilessEntity();

        // Assert
        assertNotNull(result);
        assertEquals(204, result.getStatusCode());
    }

    @Test
    void testResponseSpecBodyToFluxWithClass() throws Exception {
        // Arrange
        builder.uri("/api/test");

        java.lang.reflect.Field executorField = SpringHttpRequestBuilder.class.getDeclaredField("webClientExecutor");
        executorField.setAccessible(true);
        WebClientExecutor mockExecutor = mock(WebClientExecutor.class);
        executorField.set(builder, mockExecutor);

        reactor.core.publisher.Flux<String> mockFlux = reactor.core.publisher.Flux.just("item1", "item2");
        when(mockExecutor.executeStream(anyString(), any(URI.class), any(), any(), any(), any(), eq(String.class)))
            .thenReturn(mockFlux);

        // Act
        var responseSpec = builder.retrieve();
        var result = responseSpec.bodyToFlux(String.class);

        // Assert
        assertNotNull(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testResponseSpecBodyToFluxWithTypeReference() throws Exception {
        // Arrange
        builder.uri("/api/test");

        java.lang.reflect.Field executorField = SpringHttpRequestBuilder.class.getDeclaredField("webClientExecutor");
        executorField.setAccessible(true);
        WebClientExecutor mockExecutor = mock(WebClientExecutor.class);
        executorField.set(builder, mockExecutor);

        reactor.core.publisher.Flux<String> mockFlux = reactor.core.publisher.Flux.just("item1", "item2");
        when(mockExecutor.executeStream(anyString(), any(URI.class), any(), any(), any(), any(),
            any(io.github.guoshiqiufeng.dify.client.core.http.TypeReference.class)))
            .thenReturn(mockFlux);

        // Act
        var responseSpec = builder.retrieve();
        io.github.guoshiqiufeng.dify.client.core.http.TypeReference<String> typeRef =
            new io.github.guoshiqiufeng.dify.client.core.http.TypeReference<String>() {};
        var result = responseSpec.bodyToFlux(typeRef);

        // Assert
        assertNotNull(result);
    }
}
