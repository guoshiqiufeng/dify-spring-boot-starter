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
        // Set Content-Length header so the interceptor will buffer the body
        mockResponse.getHeaders().setContentLength(responseBody.getBytes(StandardCharsets.UTF_8).length);

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

    // ========== Branch coverage tests for SSE, binary content, and content-length ==========

    @Test
    void testInterceptWithSSEResponse() throws IOException {
        // Arrange - Test SSE response (text/event-stream)
        MockClientHttpRequest request = new MockClientHttpRequest(HttpMethod.GET, URI.create("http://example.com/stream"));
        byte[] requestBody = new byte[0];

        MockClientHttpResponse mockResponse = new MockClientHttpResponse(
                "data: test stream\n\n".getBytes(StandardCharsets.UTF_8),
                HttpStatus.OK
        );
        mockResponse.getHeaders().add("Content-Type", "text/event-stream");

        when(execution.execute(any(), any())).thenReturn(mockResponse);

        // Act
        ClientHttpResponse response = interceptor.intercept(request, requestBody, execution);

        // Assert - SSE responses should not be buffered
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testInterceptWithBinaryContentAndLogBinaryBodyEnabled() throws IOException {
        // Arrange - Test binary content with logBinaryBody=true
        DifyRestLoggingInterceptor binaryInterceptor = new DifyRestLoggingInterceptor(true, 10240, true);

        MockClientHttpRequest request = new MockClientHttpRequest(HttpMethod.GET, URI.create("http://example.com/file"));
        byte[] requestBody = new byte[0];

        byte[] binaryData = new byte[1024];
        for (int i = 0; i < binaryData.length; i++) {
            binaryData[i] = (byte) i;
        }

        MockClientHttpResponse mockResponse = new MockClientHttpResponse(binaryData, HttpStatus.OK);
        mockResponse.getHeaders().add("Content-Type", "application/octet-stream");
        mockResponse.getHeaders().setContentLength(binaryData.length);

        when(execution.execute(any(), any())).thenReturn(mockResponse);

        // Act
        ClientHttpResponse response = binaryInterceptor.intercept(request, requestBody, execution);

        // Assert - should buffer binary body when enabled
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testInterceptWithBinaryContentAndLogBinaryBodyDisabled() throws IOException {
        // Arrange - Test binary content with logBinaryBody=false (default)
        DifyRestLoggingInterceptor binaryInterceptor = new DifyRestLoggingInterceptor(true, 10240, false);

        MockClientHttpRequest request = new MockClientHttpRequest(HttpMethod.GET, URI.create("http://example.com/file"));
        byte[] requestBody = new byte[0];

        byte[] binaryData = new byte[2048];
        MockClientHttpResponse mockResponse = new MockClientHttpResponse(binaryData, HttpStatus.OK);
        mockResponse.getHeaders().add("Content-Type", "application/octet-stream");
        mockResponse.getHeaders().setContentLength(binaryData.length);

        when(execution.execute(any(), any())).thenReturn(mockResponse);

        // Act
        ClientHttpResponse response = binaryInterceptor.intercept(request, requestBody, execution);

        // Assert - should skip binary body logging
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testInterceptWithUnknownContentLength() throws IOException {
        // Arrange - Test with unknown content-length (-1)
        MockClientHttpRequest request = new MockClientHttpRequest(HttpMethod.GET, URI.create("http://example.com/api"));
        byte[] requestBody = new byte[0];

        MockClientHttpResponse mockResponse = new MockClientHttpResponse(
                "{\"data\":\"test\"}".getBytes(StandardCharsets.UTF_8),
                HttpStatus.OK
        );
        // No Content-Length header = -1

        when(execution.execute(any(), any())).thenReturn(mockResponse);

        // Act
        ClientHttpResponse response = interceptor.intercept(request, requestBody, execution);

        // Assert - should handle unknown content-length
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testInterceptWithContentLengthExceedsLimit() throws IOException {
        // Arrange - Test with content-length > logBodyMaxBytes
        DifyRestLoggingInterceptor limitedInterceptor = new DifyRestLoggingInterceptor(true, 100); // 100 bytes limit

        MockClientHttpRequest request = new MockClientHttpRequest(HttpMethod.GET, URI.create("http://example.com/api"));
        byte[] requestBody = new byte[0];

        String largeBody = "x".repeat(10240); // 10KB
        MockClientHttpResponse mockResponse = new MockClientHttpResponse(
                largeBody.getBytes(StandardCharsets.UTF_8),
                HttpStatus.OK
        );
        mockResponse.getHeaders().setContentLength(10240);

        when(execution.execute(any(), any())).thenReturn(mockResponse);

        // Act
        ClientHttpResponse response = limitedInterceptor.intercept(request, requestBody, execution);

        // Assert - should skip body logging when exceeds limit
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testInterceptWithContentLengthWithinLimit() throws IOException {
        // Arrange - Test with content-length <= logBodyMaxBytes
        DifyRestLoggingInterceptor limitedInterceptor = new DifyRestLoggingInterceptor(true, 10240); // 10KB limit

        MockClientHttpRequest request = new MockClientHttpRequest(HttpMethod.GET, URI.create("http://example.com/api"));
        byte[] requestBody = new byte[0];

        String smallBody = "{\"data\":\"small\"}";
        MockClientHttpResponse mockResponse = new MockClientHttpResponse(
                smallBody.getBytes(StandardCharsets.UTF_8),
                HttpStatus.OK
        );
        mockResponse.getHeaders().setContentLength(smallBody.length());

        when(execution.execute(any(), any())).thenReturn(mockResponse);

        // Act
        ClientHttpResponse response = limitedInterceptor.intercept(request, requestBody, execution);

        // Assert - should buffer body when within limit
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify body can be read multiple times
        String firstRead = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
        String secondRead = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
        assertEquals(smallBody, firstRead);
        assertEquals(smallBody, secondRead);
    }

    @Test
    void testInterceptWithUnlimitedBodyLogging() throws IOException {
        // Arrange - Test with logBodyMaxBytes=0 (unlimited)
        DifyRestLoggingInterceptor unlimitedInterceptor = new DifyRestLoggingInterceptor(true, 0);

        MockClientHttpRequest request = new MockClientHttpRequest(HttpMethod.GET, URI.create("http://example.com/api"));
        byte[] requestBody = new byte[0];

        String largeBody = "x".repeat(100000); // 100KB
        MockClientHttpResponse mockResponse = new MockClientHttpResponse(
                largeBody.getBytes(StandardCharsets.UTF_8),
                HttpStatus.OK
        );
        mockResponse.getHeaders().setContentLength(100000);

        when(execution.execute(any(), any())).thenReturn(mockResponse);

        // Act
        ClientHttpResponse response = unlimitedInterceptor.intercept(request, requestBody, execution);

        // Assert - should buffer body regardless of size
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testInterceptWithGetStatusCodeException() throws IOException {
        // Arrange - Test getStatusCodeSafely exception path
        MockClientHttpRequest request = new MockClientHttpRequest(HttpMethod.GET, URI.create("http://example.com/api"));
        byte[] requestBody = new byte[0];

        // Create a response that throws on getStatusCode
        ClientHttpResponse faultyResponse = new MockClientHttpResponse(
                "response".getBytes(StandardCharsets.UTF_8),
                HttpStatus.OK
        ) {
            @Override
            public HttpStatus getStatusCode() {
                throw new RuntimeException("Status code unavailable");
            }
        };

        when(execution.execute(any(), any())).thenReturn(faultyResponse);

        // Act
        ClientHttpResponse response = interceptor.intercept(request, requestBody, execution);

        // Assert - should handle exception and return response
        assertNotNull(response);
    }

    @Test
    void testInterceptWithExecutionException() throws IOException {
        // Arrange - Test exception path in intercept (cleanup)
        MockClientHttpRequest request = new MockClientHttpRequest(HttpMethod.GET, URI.create("http://example.com/api"));
        byte[] requestBody = new byte[0];

        when(execution.execute(any(), any())).thenThrow(new IOException("Network error"));

        // Act & Assert - should propagate exception and cleanup cache
        assertThrows(IOException.class, () -> {
            interceptor.intercept(request, requestBody, execution);
        });
    }

    @Test
    void testInterceptWithImageContentType() throws IOException {
        // Arrange - Test with image content type (binary)
        DifyRestLoggingInterceptor binaryInterceptor = new DifyRestLoggingInterceptor(true, 10240, false);

        MockClientHttpRequest request = new MockClientHttpRequest(HttpMethod.GET, URI.create("http://example.com/image.png"));
        byte[] requestBody = new byte[0];

        byte[] imageData = new byte[5000];
        MockClientHttpResponse mockResponse = new MockClientHttpResponse(imageData, HttpStatus.OK);
        mockResponse.getHeaders().add("Content-Type", "image/png");
        mockResponse.getHeaders().setContentLength(imageData.length);

        when(execution.execute(any(), any())).thenReturn(mockResponse);

        // Act
        ClientHttpResponse response = binaryInterceptor.intercept(request, requestBody, execution);

        // Assert - should skip binary body logging
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testInterceptWithVideoContentType() throws IOException {
        // Arrange - Test with video content type (binary)
        DifyRestLoggingInterceptor binaryInterceptor = new DifyRestLoggingInterceptor(true, 10240, true);

        MockClientHttpRequest request = new MockClientHttpRequest(HttpMethod.GET, URI.create("http://example.com/video.mp4"));
        byte[] requestBody = new byte[0];

        byte[] videoData = new byte[8000];
        MockClientHttpResponse mockResponse = new MockClientHttpResponse(videoData, HttpStatus.OK);
        mockResponse.getHeaders().add("Content-Type", "video/mp4");
        mockResponse.getHeaders().setContentLength(videoData.length);

        when(execution.execute(any(), any())).thenReturn(mockResponse);

        // Act
        ClientHttpResponse response = binaryInterceptor.intercept(request, requestBody, execution);

        // Assert - should buffer binary body when enabled
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // ========== Tests for log.isDebugEnabled()=false branch ==========

    @Test
    void testInterceptWithDebugDisabled() throws IOException {
        // Arrange - Temporarily disable DEBUG logging to test the else branch
        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger)
            org.slf4j.LoggerFactory.getLogger(DifyRestLoggingInterceptor.class);
        ch.qos.logback.classic.Level originalLevel = logger.getLevel();

        try {
            // Set to INFO to disable debug
            logger.setLevel(ch.qos.logback.classic.Level.INFO);

            MockClientHttpRequest request = new MockClientHttpRequest(HttpMethod.GET, URI.create("http://example.com/api"));
            byte[] requestBody = new byte[0];

            MockClientHttpResponse mockResponse = new MockClientHttpResponse(
                    "{\"data\":\"test\"}".getBytes(StandardCharsets.UTF_8),
                    HttpStatus.OK
            );

            when(execution.execute(any(), any())).thenReturn(mockResponse);

            // Act
            ClientHttpResponse response = interceptor.intercept(request, requestBody, execution);

            // Assert - should skip logging when debug is disabled
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        } finally {
            // Restore original level
            logger.setLevel(originalLevel);
        }
    }

    @Test
    void testInterceptWithDebugDisabledAndMaskingEnabled() throws IOException {
        // Arrange - Test debug disabled with masking enabled
        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger)
            org.slf4j.LoggerFactory.getLogger(DifyRestLoggingInterceptor.class);
        ch.qos.logback.classic.Level originalLevel = logger.getLevel();

        try {
            logger.setLevel(ch.qos.logback.classic.Level.INFO);

            DifyRestLoggingInterceptor maskingInterceptor = new DifyRestLoggingInterceptor(true);

            MockClientHttpRequest request = new MockClientHttpRequest(
                    HttpMethod.POST,
                    URI.create("http://example.com/api?api_key=secret")
            );
            request.getHeaders().add("Authorization", "Bearer token");
            byte[] requestBody = "{\"password\":\"secret\"}".getBytes(StandardCharsets.UTF_8);

            MockClientHttpResponse mockResponse = new MockClientHttpResponse(
                    "{\"token\":\"new-token\"}".getBytes(StandardCharsets.UTF_8),
                    HttpStatus.OK
            );

            when(execution.execute(any(), any())).thenReturn(mockResponse);

            // Act
            ClientHttpResponse response = maskingInterceptor.intercept(request, requestBody, execution);

            // Assert - should skip all logging when debug is disabled
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
        } finally {
            logger.setLevel(originalLevel);
        }
    }

    @Test
    void testInterceptWithDebugDisabledAndError() throws IOException {
        // Arrange - Test debug disabled with error
        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger)
            org.slf4j.LoggerFactory.getLogger(DifyRestLoggingInterceptor.class);
        ch.qos.logback.classic.Level originalLevel = logger.getLevel();

        try {
            logger.setLevel(ch.qos.logback.classic.Level.INFO);

            MockClientHttpRequest request = new MockClientHttpRequest(HttpMethod.GET, URI.create("http://example.com/api"));
            byte[] requestBody = new byte[0];

            when(execution.execute(any(), any())).thenThrow(new IOException("Network error"));

            // Act & Assert - should propagate exception and cleanup cache
            assertThrows(IOException.class, () -> {
                interceptor.intercept(request, requestBody, execution);
            });
        } finally {
            logger.setLevel(originalLevel);
        }
    }

    // ========== Tests for additional content-type and size branches ==========

    @Test
    void testInterceptWithApplicationStreamContentType() throws IOException {
        // Arrange - Test with application/stream+json (contains "stream")
        MockClientHttpRequest request = new MockClientHttpRequest(HttpMethod.GET, URI.create("http://example.com/stream"));
        byte[] requestBody = new byte[0];

        MockClientHttpResponse mockResponse = new MockClientHttpResponse(
                "stream data".getBytes(StandardCharsets.UTF_8),
                HttpStatus.OK
        );
        mockResponse.getHeaders().add("Content-Type", "application/stream+json");

        when(execution.execute(any(), any())).thenReturn(mockResponse);

        // Act
        ClientHttpResponse response = interceptor.intercept(request, requestBody, execution);

        // Assert - should treat as streaming and return original response
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testInterceptWithOctetStreamContentType() throws IOException {
        // Arrange - Test with application/octet-stream (binary)
        DifyRestLoggingInterceptor binaryInterceptor = new DifyRestLoggingInterceptor(true, 10240, false);

        MockClientHttpRequest request = new MockClientHttpRequest(HttpMethod.GET, URI.create("http://example.com/file"));
        byte[] requestBody = new byte[0];

        byte[] binaryData = new byte[3000];
        MockClientHttpResponse mockResponse = new MockClientHttpResponse(binaryData, HttpStatus.OK);
        mockResponse.getHeaders().add("Content-Type", "application/octet-stream");
        mockResponse.getHeaders().setContentLength(binaryData.length);

        when(execution.execute(any(), any())).thenReturn(mockResponse);

        // Act
        ClientHttpResponse response = binaryInterceptor.intercept(request, requestBody, execution);

        // Assert - should skip binary body when logBinaryBody=false
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testInterceptWithPdfContentType() throws IOException {
        // Arrange - Test with application/pdf (binary)
        DifyRestLoggingInterceptor binaryInterceptor = new DifyRestLoggingInterceptor(true, 10240, true);

        MockClientHttpRequest request = new MockClientHttpRequest(HttpMethod.GET, URI.create("http://example.com/doc.pdf"));
        byte[] requestBody = new byte[0];

        byte[] pdfData = new byte[5000];
        MockClientHttpResponse mockResponse = new MockClientHttpResponse(pdfData, HttpStatus.OK);
        mockResponse.getHeaders().add("Content-Type", "application/pdf");
        mockResponse.getHeaders().setContentLength(pdfData.length);

        when(execution.execute(any(), any())).thenReturn(mockResponse);

        // Act
        ClientHttpResponse response = binaryInterceptor.intercept(request, requestBody, execution);

        // Assert - should buffer binary body when logBinaryBody=true
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testInterceptWithZeroContentLength() throws IOException {
        // Arrange - Test with content-length=0
        MockClientHttpRequest request = new MockClientHttpRequest(HttpMethod.GET, URI.create("http://example.com/api"));
        byte[] requestBody = new byte[0];

        MockClientHttpResponse mockResponse = new MockClientHttpResponse(
                new byte[0],
                HttpStatus.OK
        );
        mockResponse.getHeaders().add("Content-Type", "application/json");
        mockResponse.getHeaders().setContentLength(0);

        when(execution.execute(any(), any())).thenReturn(mockResponse);

        // Act
        ClientHttpResponse response = interceptor.intercept(request, requestBody, execution);

        // Assert - should handle zero content-length
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testInterceptWithVeryLargeContentLength() throws IOException {
        // Arrange - Test with very large content-length
        DifyRestLoggingInterceptor limitedInterceptor = new DifyRestLoggingInterceptor(true, 1024); // 1KB limit

        MockClientHttpRequest request = new MockClientHttpRequest(HttpMethod.GET, URI.create("http://example.com/api"));
        byte[] requestBody = new byte[0];

        String largeBody = "x".repeat(1048576); // 1MB
        MockClientHttpResponse mockResponse = new MockClientHttpResponse(
                largeBody.getBytes(StandardCharsets.UTF_8),
                HttpStatus.OK
        );
        mockResponse.getHeaders().add("Content-Type", "application/json");
        mockResponse.getHeaders().setContentLength(1048576);

        when(execution.execute(any(), any())).thenReturn(mockResponse);

        // Act
        ClientHttpResponse response = limitedInterceptor.intercept(request, requestBody, execution);

        // Assert - should skip body logging when exceeds limit
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testInterceptWithMaskingDisabledAndSensitiveData() throws IOException {
        // Arrange - Test masking disabled with sensitive data
        DifyRestLoggingInterceptor noMaskInterceptor = new DifyRestLoggingInterceptor(false);

        MockClientHttpRequest request = new MockClientHttpRequest(
                HttpMethod.POST,
                URI.create("http://example.com/api?password=secret123")
        );
        request.getHeaders().add("Authorization", "Bearer token123");
        request.getHeaders().add("Cookie", "session=abc");
        byte[] requestBody = "{\"api_key\":\"key123\"}".getBytes(StandardCharsets.UTF_8);

        MockClientHttpResponse mockResponse = new MockClientHttpResponse(
                "{\"token\":\"new-token\"}".getBytes(StandardCharsets.UTF_8),
                HttpStatus.OK
        );

        when(execution.execute(any(), any())).thenReturn(mockResponse);

        // Act
        ClientHttpResponse response = noMaskInterceptor.intercept(request, requestBody, execution);

        // Assert - should log without masking
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}
