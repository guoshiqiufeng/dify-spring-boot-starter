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
package io.github.guoshiqiufeng.dify.client.integration.spring.logging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.mock.http.client.MockClientHttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for DifyRestLoggingInterceptor
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/1/13
 */
@ExtendWith(MockitoExtension.class)
class DifyRestLoggingInterceptorTest {

    @Mock
    private ClientHttpRequestExecution execution;

    private DifyRestLoggingInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new DifyRestLoggingInterceptor();
    }

    @Test
    void testInterceptWithEmptyBody() throws IOException {
        // Arrange
        MockClientHttpRequest request = new MockClientHttpRequest(HttpMethod.GET, URI.create("http://example.com/api"));
        byte[] requestBody = new byte[0];
        String responseBody = "{\"status\":\"ok\"}";

        MockClientHttpResponse mockResponse = new MockClientHttpResponse(
                responseBody.getBytes(StandardCharsets.UTF_8),
                HttpStatus.OK
        );

        when(execution.execute(any(), any())).thenReturn(mockResponse);

        // Act
        ClientHttpResponse response = interceptor.intercept(request, requestBody, execution);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify body can be read
        String actualBody = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
        assertEquals(responseBody, actualBody);
    }

    @Test
    void testInterceptWithRequestBody() throws IOException {
        // Arrange
        MockClientHttpRequest request = new MockClientHttpRequest(HttpMethod.POST, URI.create("http://example.com/api"));
        byte[] requestBody = "{\"name\":\"test\"}".getBytes(StandardCharsets.UTF_8);
        String responseBody = "{\"id\":123}";

        MockClientHttpResponse mockResponse = new MockClientHttpResponse(
                responseBody.getBytes(StandardCharsets.UTF_8),
                HttpStatus.CREATED
        );

        when(execution.execute(any(), any())).thenReturn(mockResponse);

        // Act
        ClientHttpResponse response = interceptor.intercept(request, requestBody, execution);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testInterceptPreservesHeaders() throws IOException {
        // Arrange
        MockClientHttpRequest request = new MockClientHttpRequest(HttpMethod.GET, URI.create("http://example.com/api"));
        request.getHeaders().add("Authorization", "Bearer token");
        byte[] requestBody = new byte[0];

        MockClientHttpResponse mockResponse = new MockClientHttpResponse(
                "response".getBytes(StandardCharsets.UTF_8),
                HttpStatus.OK
        );
        mockResponse.getHeaders().add("Content-Type", "application/json");
        mockResponse.getHeaders().add("X-Custom-Header", "custom-value");

        when(execution.execute(any(), any())).thenReturn(mockResponse);

        // Act
        ClientHttpResponse response = interceptor.intercept(request, requestBody, execution);

        // Assert
        assertNotNull(response);
        assertEquals("application/json", response.getHeaders().getFirst("Content-Type"));
        assertEquals("custom-value", response.getHeaders().getFirst("X-Custom-Header"));
    }

    @Test
    void testInterceptBodyCanBeReadMultipleTimes() throws IOException {
        // Arrange
        MockClientHttpRequest request = new MockClientHttpRequest(HttpMethod.GET, URI.create("http://example.com/api"));
        byte[] requestBody = new byte[0];
        String responseBody = "test response body";

        MockClientHttpResponse mockResponse = new MockClientHttpResponse(
                responseBody.getBytes(StandardCharsets.UTF_8),
                HttpStatus.OK
        );

        when(execution.execute(any(), any())).thenReturn(mockResponse);

        // Act
        ClientHttpResponse response = interceptor.intercept(request, requestBody, execution);

        // Assert - read body multiple times
        String firstRead = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
        String secondRead = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);

        assertEquals(responseBody, firstRead);
        assertEquals(responseBody, secondRead);
    }

    @Test
    void testInterceptWithDifferentHttpMethods() throws IOException {
        HttpMethod[] methods = {HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.PATCH};

        for (HttpMethod method : methods) {
            // Arrange
            MockClientHttpRequest request = new MockClientHttpRequest(method, URI.create("http://example.com/api"));
            byte[] requestBody = "test".getBytes(StandardCharsets.UTF_8);

            MockClientHttpResponse mockResponse = new MockClientHttpResponse(
                    "response".getBytes(StandardCharsets.UTF_8),
                    HttpStatus.OK
            );

            when(execution.execute(any(), any())).thenReturn(mockResponse);

            // Act
            ClientHttpResponse response = interceptor.intercept(request, requestBody, execution);

            // Assert
            assertNotNull(response, "Response should not be null for " + method);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }

    @Test
    void testBufferedResponseInvocationHandler() throws Throwable {
        // Arrange
        String responseBody = "test body";
        MockClientHttpResponse originalResponse = new MockClientHttpResponse(
                responseBody.getBytes(StandardCharsets.UTF_8),
                HttpStatus.OK
        );
        originalResponse.getHeaders().add("Content-Type", "text/plain");

        byte[] cachedBody = responseBody.getBytes(StandardCharsets.UTF_8);
        DifyRestLoggingInterceptor.BufferedResponseInvocationHandler handler =
                new DifyRestLoggingInterceptor.BufferedResponseInvocationHandler(originalResponse, cachedBody);

        // Act - test getBody method
        Object bodyResult = handler.invoke(null, ClientHttpResponse.class.getMethod("getBody"), null);

        // Assert
        assertInstanceOf(InputStream.class, bodyResult);
        String bodyContent = new String(((InputStream) bodyResult).readAllBytes(), StandardCharsets.UTF_8);
        assertEquals(responseBody, bodyContent);
    }

    @Test
    void testBufferedResponseInvocationHandlerDelegatesOtherMethods() throws Throwable {
        // Arrange
        MockClientHttpResponse originalResponse = new MockClientHttpResponse(
                "body".getBytes(StandardCharsets.UTF_8),
                HttpStatus.OK
        );
        originalResponse.getHeaders().add("X-Test", "value");

        byte[] cachedBody = "body".getBytes(StandardCharsets.UTF_8);
        DifyRestLoggingInterceptor.BufferedResponseInvocationHandler handler =
                new DifyRestLoggingInterceptor.BufferedResponseInvocationHandler(originalResponse, cachedBody);

        // Act - test getHeaders method (should delegate to original)
        Object headersResult = handler.invoke(null, ClientHttpResponse.class.getMethod("getHeaders"), null);

        // Assert
        assertInstanceOf(HttpHeaders.class, headersResult);
        HttpHeaders headers = (HttpHeaders) headersResult;
        assertEquals("value", headers.getFirst("X-Test"));
    }
}
