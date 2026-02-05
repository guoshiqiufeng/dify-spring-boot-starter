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

    @Test
    void testInterceptWithUrlParameterMasking() throws IOException {
        // Arrange - Test URL parameter masking
        DifyRestLoggingInterceptor maskingInterceptor = new DifyRestLoggingInterceptor(true);

        MockClientHttpRequest request = new MockClientHttpRequest(
                HttpMethod.GET,
                URI.create("http://example.com/api?api_key=secret123&token=abc&user_id=456")
        );
        byte[] requestBody = new byte[0];

        MockClientHttpResponse mockResponse = new MockClientHttpResponse(
                "response".getBytes(StandardCharsets.UTF_8),
                HttpStatus.OK
        );

        when(execution.execute(any(), any())).thenReturn(mockResponse);

        // Act
        ClientHttpResponse response = maskingInterceptor.intercept(request, requestBody, execution);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testInterceptWithMaskingDisabled() throws IOException {
        // Arrange - Test with masking disabled
        DifyRestLoggingInterceptor noMaskInterceptor = new DifyRestLoggingInterceptor(false);

        MockClientHttpRequest request = new MockClientHttpRequest(
                HttpMethod.GET,
                URI.create("http://example.com/api?api_key=secret123")
        );
        request.getHeaders().add("Authorization", "Bearer token");
        byte[] requestBody = "{\"password\":\"secret\"}".getBytes(StandardCharsets.UTF_8);

        MockClientHttpResponse mockResponse = new MockClientHttpResponse(
                "{\"token\":\"new-token\"}".getBytes(StandardCharsets.UTF_8),
                HttpStatus.OK
        );

        when(execution.execute(any(), any())).thenReturn(mockResponse);

        // Act
        ClientHttpResponse response = noMaskInterceptor.intercept(request, requestBody, execution);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testInterceptWithMaskingEnabled() throws IOException {
        // Arrange - Test with masking enabled
        DifyRestLoggingInterceptor maskingInterceptor = new DifyRestLoggingInterceptor(true);

        MockClientHttpRequest request = new MockClientHttpRequest(
                HttpMethod.POST,
                URI.create("http://example.com/api")
        );
        request.getHeaders().add("Authorization", "Bearer secret-token");
        request.getHeaders().add("Cookie", "session=abc123");
        byte[] requestBody = "{\"api_key\":\"secret\",\"password\":\"pass123\"}".getBytes(StandardCharsets.UTF_8);

        MockClientHttpResponse mockResponse = new MockClientHttpResponse(
                "{\"token\":\"new-token\"}".getBytes(StandardCharsets.UTF_8),
                HttpStatus.OK
        );

        when(execution.execute(any(), any())).thenReturn(mockResponse);

        // Act
        ClientHttpResponse response = maskingInterceptor.intercept(request, requestBody, execution);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testInterceptWithMultipleSensitiveUrlParams() throws IOException {
        // Arrange - Test multiple sensitive URL parameters
        DifyRestLoggingInterceptor maskingInterceptor = new DifyRestLoggingInterceptor(true);

        MockClientHttpRequest request = new MockClientHttpRequest(
                HttpMethod.GET,
                URI.create("http://example.com/api?api_key=key1&password=pass1&secret=sec1&access_token=tok1&refresh_token=tok2")
        );
        byte[] requestBody = new byte[0];

        MockClientHttpResponse mockResponse = new MockClientHttpResponse(
                "response".getBytes(StandardCharsets.UTF_8),
                HttpStatus.OK
        );

        when(execution.execute(any(), any())).thenReturn(mockResponse);

        // Act
        ClientHttpResponse response = maskingInterceptor.intercept(request, requestBody, execution);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testInterceptWithSensitiveHeaders() throws IOException {
        // Arrange - Test sensitive headers masking
        DifyRestLoggingInterceptor maskingInterceptor = new DifyRestLoggingInterceptor(true);

        MockClientHttpRequest request = new MockClientHttpRequest(HttpMethod.POST, URI.create("http://example.com/api"));
        request.getHeaders().add("Authorization", "Bearer secret-token");
        request.getHeaders().add("Cookie", "session=abc123");
        request.getHeaders().add("X-API-Key", "api-key-value");
        byte[] requestBody = "{}".getBytes(StandardCharsets.UTF_8);

        MockClientHttpResponse mockResponse = new MockClientHttpResponse(
                "response".getBytes(StandardCharsets.UTF_8),
                HttpStatus.OK
        );

        when(execution.execute(any(), any())).thenReturn(mockResponse);

        // Act
        ClientHttpResponse response = maskingInterceptor.intercept(request, requestBody, execution);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testInterceptWithComplexUrl() throws IOException {
        // Arrange - Test complex URL with mixed parameters
        DifyRestLoggingInterceptor maskingInterceptor = new DifyRestLoggingInterceptor(true);

        MockClientHttpRequest request = new MockClientHttpRequest(
                HttpMethod.GET,
                URI.create("https://api.example.com/v1/chat?api_key=secret&user_id=123&token=abc&page=1&limit=10")
        );
        byte[] requestBody = new byte[0];

        MockClientHttpResponse mockResponse = new MockClientHttpResponse(
                "response".getBytes(StandardCharsets.UTF_8),
                HttpStatus.OK
        );

        when(execution.execute(any(), any())).thenReturn(mockResponse);

        // Act
        ClientHttpResponse response = maskingInterceptor.intercept(request, requestBody, execution);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testInterceptWithEmptyUrl() throws IOException {
        // Arrange - Test with simple URL (no parameters)
        DifyRestLoggingInterceptor maskingInterceptor = new DifyRestLoggingInterceptor(true);

        MockClientHttpRequest request = new MockClientHttpRequest(HttpMethod.GET, URI.create("http://example.com/api"));
        byte[] requestBody = new byte[0];

        MockClientHttpResponse mockResponse = new MockClientHttpResponse(
                "response".getBytes(StandardCharsets.UTF_8),
                HttpStatus.OK
        );

        when(execution.execute(any(), any())).thenReturn(mockResponse);

        // Act
        ClientHttpResponse response = maskingInterceptor.intercept(request, requestBody, execution);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testInterceptWithErrorResponse() throws IOException {
        // Arrange - Test error response
        DifyRestLoggingInterceptor maskingInterceptor = new DifyRestLoggingInterceptor(true);

        MockClientHttpRequest request = new MockClientHttpRequest(HttpMethod.GET, URI.create("http://example.com/api"));
        byte[] requestBody = new byte[0];

        MockClientHttpResponse mockResponse = new MockClientHttpResponse(
                "{\"error\":\"Not found\"}".getBytes(StandardCharsets.UTF_8),
                HttpStatus.NOT_FOUND
        );

        when(execution.execute(any(), any())).thenReturn(mockResponse);

        // Act
        ClientHttpResponse response = maskingInterceptor.intercept(request, requestBody, execution);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testInterceptWithLargeResponseBody() throws IOException {
        // Arrange - Test with large response body
        StringBuilder largeBody = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            largeBody.append("This is line ").append(i).append("\n");
        }

        MockClientHttpRequest request = new MockClientHttpRequest(HttpMethod.POST, URI.create("http://example.com/api"));
        byte[] requestBody = "{\"data\":\"test\"}".getBytes(StandardCharsets.UTF_8);

        MockClientHttpResponse mockResponse = new MockClientHttpResponse(
                largeBody.toString().getBytes(StandardCharsets.UTF_8),
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
        assertTrue(actualBody.contains("This is line 0"));
        assertTrue(actualBody.contains("This is line 999"));
    }

    @Test
    void testMaskSensitiveUrlParamsWithNullUrl() throws IOException {
        // Arrange - Test URL masking with null/empty scenarios
        // We can't directly test the private method, but we can test through intercept
        DifyRestLoggingInterceptor maskingInterceptor = new DifyRestLoggingInterceptor(true);

        // Test with empty URL parameters (no sensitive params)
        MockClientHttpRequest request = new MockClientHttpRequest(HttpMethod.GET, URI.create("http://example.com/api"));
        byte[] requestBody = new byte[0];

        MockClientHttpResponse mockResponse = new MockClientHttpResponse(
                "response".getBytes(StandardCharsets.UTF_8),
                HttpStatus.OK
        );

        when(execution.execute(any(), any())).thenReturn(mockResponse);

        // Act
        ClientHttpResponse response = maskingInterceptor.intercept(request, requestBody, execution);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testInterceptWithNullRequestBody() throws IOException {
        // Arrange - Test with null body
        MockClientHttpRequest request = new MockClientHttpRequest(HttpMethod.POST, URI.create("http://example.com/api"));
        byte[] requestBody = null;

        MockClientHttpResponse mockResponse = new MockClientHttpResponse(
                "response".getBytes(StandardCharsets.UTF_8),
                HttpStatus.OK
        );

        when(execution.execute(any(), any())).thenReturn(mockResponse);

        // Act
        ClientHttpResponse response = interceptor.intercept(request, requestBody, execution);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testInterceptWithEmptyResponseBody() throws IOException {
        // Arrange - Test with empty response body
        MockClientHttpRequest request = new MockClientHttpRequest(HttpMethod.GET, URI.create("http://example.com/api"));
        byte[] requestBody = new byte[0];

        MockClientHttpResponse mockResponse = new MockClientHttpResponse(
                new byte[0],
                HttpStatus.NO_CONTENT
        );

        when(execution.execute(any(), any())).thenReturn(mockResponse);

        // Act
        ClientHttpResponse response = interceptor.intercept(request, requestBody, execution);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
