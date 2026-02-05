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
package io.github.guoshiqiufeng.dify.client.integration.okhttp.logging;

import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for LoggingInterceptor
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/01/06
 */
class LoggingInterceptorTest {

    private LoggingInterceptor interceptor;
    private Interceptor.Chain mockChain;

    @BeforeEach
    void setUp() {
        interceptor = new LoggingInterceptor();
        mockChain = mock(Interceptor.Chain.class);
    }

    @Test
    void testInterceptWithGetRequest() throws IOException {
        // Arrange
        Request request = new Request.Builder()
                .url("https://api.dify.ai/v1/test")
                .get()
                .build();

        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create("test response", MediaType.get("text/plain")))
                .build();

        when(mockChain.request()).thenReturn(request);
        when(mockChain.proceed(any(Request.class))).thenReturn(response);

        // Act
        Response result = interceptor.intercept(mockChain);

        // Assert
        assertNotNull(result);
        verify(mockChain).proceed(request);
    }

    @Test
    void testInterceptWithPostRequest() throws IOException {
        // Arrange
        RequestBody requestBody = RequestBody.create("{\"key\":\"value\"}", MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url("https://api.dify.ai/v1/test")
                .post(requestBody)
                .build();

        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(201)
                .message("Created")
                .body(ResponseBody.create("{\"id\":123}", MediaType.get("application/json")))
                .build();

        when(mockChain.request()).thenReturn(request);
        when(mockChain.proceed(any(Request.class))).thenReturn(response);

        // Act
        Response result = interceptor.intercept(mockChain);

        // Assert
        assertNotNull(result);
        verify(mockChain).proceed(request);
    }

    @Test
    void testInterceptWithNullResponseBody() throws IOException {
        // Arrange
        Request request = new Request.Builder()
                .url("https://api.dify.ai/v1/test")
                .get()
                .build();

        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(204)
                .message("No Content")
                .build();

        when(mockChain.request()).thenReturn(request);
        when(mockChain.proceed(any(Request.class))).thenReturn(response);

        // Act
        Response result = interceptor.intercept(mockChain);

        // Assert
        assertNotNull(result);
        assertEquals(204, result.code());
    }

    @Test
    void testInterceptWithErrorResponse() throws IOException {
        // Arrange
        Request request = new Request.Builder()
                .url("https://api.dify.ai/v1/test")
                .get()
                .build();

        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(404)
                .message("Not Found")
                .body(ResponseBody.create("{\"error\":\"Not found\"}", MediaType.get("application/json")))
                .build();

        when(mockChain.request()).thenReturn(request);
        when(mockChain.proceed(any(Request.class))).thenReturn(response);

        // Act
        Response result = interceptor.intercept(mockChain);

        // Assert
        assertNotNull(result);
        assertEquals(404, result.code());
    }

    @Test
    void testInterceptWithBinaryContent() throws IOException {
        // Arrange
        Request request = new Request.Builder()
                .url("https://api.dify.ai/v1/test")
                .get()
                .build();

        byte[] binaryData = new byte[]{0x00, 0x01, 0x02, 0x03};
        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create(binaryData, MediaType.get("application/octet-stream")))
                .build();

        when(mockChain.request()).thenReturn(request);
        when(mockChain.proceed(any(Request.class))).thenReturn(response);

        // Act
        Response result = interceptor.intercept(mockChain);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.code());
    }

    @Test
    void testInterceptWithHeaders() throws IOException {
        // Arrange
        Request request = new Request.Builder()
                .url("https://api.dify.ai/v1/test")
                .header("Authorization", "Bearer token")
                .header("Content-Type", "application/json")
                .get()
                .build();

        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .header("X-Custom-Header", "value")
                .body(ResponseBody.create("test", MediaType.get("text/plain")))
                .build();

        when(mockChain.request()).thenReturn(request);
        when(mockChain.proceed(any(Request.class))).thenReturn(response);

        // Act
        Response result = interceptor.intercept(mockChain);

        // Assert
        assertNotNull(result);
        assertEquals("Bearer token", request.header("Authorization"));
    }

    @Test
    void testInterceptPreservesRequestObject() throws IOException {
        // Arrange
        Request request = new Request.Builder()
                .url("https://api.dify.ai/v1/test")
                .get()
                .build();

        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create("test", MediaType.get("text/plain")))
                .build();

        when(mockChain.request()).thenReturn(request);
        when(mockChain.proceed(any(Request.class))).thenReturn(response);

        // Act
        interceptor.intercept(mockChain);

        // Assert
        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        verify(mockChain).proceed(requestCaptor.capture());
        assertEquals(request, requestCaptor.getValue());
    }

    @Test
    void testInterceptWithJsonResponse() throws IOException {
        // Arrange
        Request request = new Request.Builder()
                .url("https://api.dify.ai/v1/test")
                .get()
                .build();

        String jsonResponse = "{\"status\":\"success\",\"data\":{\"id\":123}}";
        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create(jsonResponse, MediaType.get("application/json")))
                .build();

        when(mockChain.request()).thenReturn(request);
        when(mockChain.proceed(any(Request.class))).thenReturn(response);

        // Act
        Response result = interceptor.intercept(mockChain);

        // Assert
        assertNotNull(result);
        assertNotNull(result.body());
    }

    @Test
    void testInterceptWithXmlResponse() throws IOException {
        // Arrange
        Request request = new Request.Builder()
                .url("https://api.dify.ai/v1/test")
                .get()
                .build();

        String xmlResponse = "<?xml version=\"1.0\"?><root><item>test</item></root>";
        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create(xmlResponse, MediaType.get("application/xml")))
                .build();

        when(mockChain.request()).thenReturn(request);
        when(mockChain.proceed(any(Request.class))).thenReturn(response);

        // Act
        Response result = interceptor.intercept(mockChain);

        // Assert
        assertNotNull(result);
        assertNotNull(result.body());
    }

    @Test
    void testInterceptWithHtmlResponse() throws IOException {
        // Arrange
        Request request = new Request.Builder()
                .url("https://api.dify.ai/v1/test")
                .get()
                .build();

        String htmlResponse = "<html><body>Test</body></html>";
        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create(htmlResponse, MediaType.get("text/html")))
                .build();

        when(mockChain.request()).thenReturn(request);
        when(mockChain.proceed(any(Request.class))).thenReturn(response);

        // Act
        Response result = interceptor.intercept(mockChain);

        // Assert
        assertNotNull(result);
        assertNotNull(result.body());
    }

    @Test
    void testInterceptWithCharsetInContentType() throws IOException {
        // Arrange
        RequestBody requestBody = RequestBody.create("{\"key\":\"value\"}", MediaType.parse("application/json; charset=ISO-8859-1"));
        Request request = new Request.Builder()
                .url("https://api.dify.ai/v1/test")
                .post(requestBody)
                .build();

        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create("success", MediaType.get("text/plain")))
                .build();

        when(mockChain.request()).thenReturn(request);
        when(mockChain.proceed(any(Request.class))).thenReturn(response);

        // Act
        Response result = interceptor.intercept(mockChain);

        // Assert
        assertNotNull(result);
        verify(mockChain).proceed(request);
    }

    @Test
    void testInterceptWithNullCharsetInContentType() throws IOException {
        // Arrange
        RequestBody requestBody = RequestBody.create("{\"key\":\"value\"}", MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url("https://api.dify.ai/v1/test")
                .post(requestBody)
                .build();

        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create("success", MediaType.get("text/plain")))
                .build();

        when(mockChain.request()).thenReturn(request);
        when(mockChain.proceed(any(Request.class))).thenReturn(response);

        // Act
        Response result = interceptor.intercept(mockChain);

        // Assert
        assertNotNull(result);
        verify(mockChain).proceed(request);
    }

    @Test
    void testInterceptWithFormUrlEncodedResponse() throws IOException {
        // Arrange
        Request request = new Request.Builder()
                .url("https://api.dify.ai/v1/test")
                .get()
                .build();

        String formResponse = "key1=value1&key2=value2";
        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create(formResponse, MediaType.get("application/x-www-form-urlencoded")))
                .build();

        when(mockChain.request()).thenReturn(request);
        when(mockChain.proceed(any(Request.class))).thenReturn(response);

        // Act
        Response result = interceptor.intercept(mockChain);

        // Assert
        assertNotNull(result);
        assertNotNull(result.body());
    }

    @Test
    void testInterceptWithSseResponse() throws IOException {
        // Arrange - Test SSE response detection
        Request request = new Request.Builder()
                .url("https://api.dify.ai/v1/chat-messages")
                .get()
                .build();

        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create("data: test\n\n", MediaType.get("text/event-stream")))
                .build();

        when(mockChain.request()).thenReturn(request);
        when(mockChain.proceed(any(Request.class))).thenReturn(response);

        // Act
        Response result = interceptor.intercept(mockChain);

        // Assert - SSE response should not have body consumed
        assertNotNull(result);
        assertEquals(200, result.code());
        assertNotNull(result.body());
    }

    @Test
    void testInterceptWithMaskingDisabled() throws IOException {
        // Arrange - Test with masking disabled
        LoggingInterceptor noMaskInterceptor = new LoggingInterceptor(false);

        Request request = new Request.Builder()
                .url("https://api.dify.ai/v1/test")
                .header("Authorization", "Bearer secret-token")
                .get()
                .build();

        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create("{\"api_key\":\"secret\"}", MediaType.get("application/json")))
                .build();

        when(mockChain.request()).thenReturn(request);
        when(mockChain.proceed(any(Request.class))).thenReturn(response);

        // Act
        Response result = noMaskInterceptor.intercept(mockChain);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.code());
    }

    @Test
    void testInterceptWithMaskingEnabled() throws IOException {
        // Arrange - Test with masking enabled (default)
        LoggingInterceptor maskInterceptor = new LoggingInterceptor(true);

        Request request = new Request.Builder()
                .url("https://api.dify.ai/v1/test")
                .header("Authorization", "Bearer secret-token")
                .header("Cookie", "session=abc123")
                .get()
                .build();

        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create("{\"password\":\"secret123\"}", MediaType.get("application/json")))
                .build();

        when(mockChain.request()).thenReturn(request);
        when(mockChain.proceed(any(Request.class))).thenReturn(response);

        // Act
        Response result = maskInterceptor.intercept(mockChain);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.code());
    }

    @Test
    void testInterceptWithNullContentType() throws IOException {
        // Arrange - Test response with null content type
        Request request = new Request.Builder()
                .url("https://api.dify.ai/v1/test")
                .get()
                .build();

        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create("test", null))
                .build();

        when(mockChain.request()).thenReturn(request);
        when(mockChain.proceed(any(Request.class))).thenReturn(response);

        // Act
        Response result = interceptor.intercept(mockChain);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.code());
    }

    @Test
    void testInterceptWithSseCharsetResponse() throws IOException {
        // Arrange - Test SSE with charset
        Request request = new Request.Builder()
                .url("https://api.dify.ai/v1/stream")
                .get()
                .build();

        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create("data: test\n\n", MediaType.parse("text/event-stream; charset=utf-8")))
                .build();

        when(mockChain.request()).thenReturn(request);
        when(mockChain.proceed(any(Request.class))).thenReturn(response);

        // Act
        Response result = interceptor.intercept(mockChain);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.code());
    }

    @Test
    void testInterceptWithPostRequestAndMasking() throws IOException {
        // Arrange - Test POST with sensitive data
        RequestBody requestBody = RequestBody.create(
                "{\"api_key\":\"secret123\",\"password\":\"pass456\"}",
                MediaType.get("application/json")
        );

        Request request = new Request.Builder()
                .url("https://api.dify.ai/v1/auth")
                .post(requestBody)
                .header("Authorization", "Bearer token")
                .build();

        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create("{\"token\":\"new-token\"}", MediaType.get("application/json")))
                .build();

        when(mockChain.request()).thenReturn(request);
        when(mockChain.proceed(any(Request.class))).thenReturn(response);

        // Act
        Response result = interceptor.intercept(mockChain);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.code());
    }

    @Test
    void testInterceptWithPostRequestNoMasking() throws IOException {
        // Arrange - Test POST with masking disabled
        LoggingInterceptor noMaskInterceptor = new LoggingInterceptor(false);

        RequestBody requestBody = RequestBody.create(
                "{\"data\":\"test\"}",
                MediaType.get("application/json")
        );

        Request request = new Request.Builder()
                .url("https://api.dify.ai/v1/test")
                .post(requestBody)
                .build();

        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create("success", MediaType.get("text/plain")))
                .build();

        when(mockChain.request()).thenReturn(request);
        when(mockChain.proceed(any(Request.class))).thenReturn(response);

        // Act
        Response result = noMaskInterceptor.intercept(mockChain);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.code());
    }

    @Test
    void testInterceptWithRequestBodyNullContentType() throws IOException {
        // Arrange - Test request body with null content type
        RequestBody requestBody = RequestBody.create("test data", null);

        Request request = new Request.Builder()
                .url("https://api.dify.ai/v1/test")
                .post(requestBody)
                .build();

        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create("success", MediaType.get("text/plain")))
                .build();

        when(mockChain.request()).thenReturn(request);
        when(mockChain.proceed(any(Request.class))).thenReturn(response);

        // Act
        Response result = interceptor.intercept(mockChain);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.code());
    }

    @Test
    void testInterceptWithTextHtmlResponse() throws IOException {
        // Arrange - Test text/html response to cover html subtype check
        Request request = new Request.Builder()
                .url("https://api.dify.ai/v1/test")
                .get()
                .build();

        String htmlResponse = "<!DOCTYPE html><html><body><h1>Test</h1></body></html>";
        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create(htmlResponse, MediaType.get("text/html; charset=utf-8")))
                .build();

        when(mockChain.request()).thenReturn(request);
        when(mockChain.proceed(any(Request.class))).thenReturn(response);

        // Act
        Response result = interceptor.intercept(mockChain);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.code());
        assertNotNull(result.body());
    }

    @Test
    void testInterceptWithApplicationHtmlResponse() throws IOException {
        // Arrange - Test application/html response
        Request request = new Request.Builder()
                .url("https://api.dify.ai/v1/test")
                .get()
                .build();

        String htmlResponse = "<html><body>Test</body></html>";
        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create(htmlResponse, MediaType.get("application/html")))
                .build();

        when(mockChain.request()).thenReturn(request);
        when(mockChain.proceed(any(Request.class))).thenReturn(response);

        // Act
        Response result = interceptor.intercept(mockChain);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.code());
        assertNotNull(result.body());
    }

    @Test
    void testInterceptWithLargeResponseBodySkipped() throws IOException {
        // Arrange - Test large response body is skipped
        LoggingInterceptor limitedInterceptor = new LoggingInterceptor(true, 100, false);

        Request request = new Request.Builder()
                .url("https://api.dify.ai/v1/test")
                .get()
                .build();

        // Create large response (200 bytes, exceeds 100 byte limit)
        String largeResponse = "x".repeat(200);
        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create(largeResponse, MediaType.get("text/plain")))
                .build();

        when(mockChain.request()).thenReturn(request);
        when(mockChain.proceed(any(Request.class))).thenReturn(response);

        // Act
        Response result = limitedInterceptor.intercept(mockChain);

        // Assert - Should return original response without consuming body
        assertNotNull(result);
        assertEquals(200, result.code());
        assertNotNull(result.body());
    }

    @Test
    void testInterceptWithBodyTruncation() throws IOException {
        // Arrange - Test body truncation when exceeds limit
        LoggingInterceptor limitedInterceptor = new LoggingInterceptor(true, 50, false);

        Request request = new Request.Builder()
                .url("https://api.dify.ai/v1/test")
                .get()
                .build();

        // Create response with unknown content length (will be read and truncated)
        String response100Chars = "a".repeat(100);
        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create(response100Chars, MediaType.get("text/plain")))
                .build();

        when(mockChain.request()).thenReturn(request);
        when(mockChain.proceed(any(Request.class))).thenReturn(response);

        // Act
        Response result = limitedInterceptor.intercept(mockChain);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.code());
        assertNotNull(result.body());
        // Verify the full body is still available (not truncated in actual response)
        String actualBody = result.body().string();
        assertEquals(100, actualBody.length());
        assertEquals(response100Chars, actualBody);
    }

    @Test
    void testInterceptWithBodyTruncationInLogging() throws IOException {
        // Arrange - Test that truncation happens in logging but not in actual response
        // This test verifies the code path: if (logBodyMaxBytes > 0 && bodyString.length() > logBodyMaxBytes)
        LoggingInterceptor limitedInterceptor = new LoggingInterceptor(false, 20, false);

        Request request = new Request.Builder()
                .url("https://api.dify.ai/v1/test")
                .get()
                .build();

        // Create a response with body longer than limit
        String longBody = "This is a very long response body that should be truncated in logs";
        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create(longBody, MediaType.get("text/plain")))
                .build();

        when(mockChain.request()).thenReturn(request);
        when(mockChain.proceed(any(Request.class))).thenReturn(response);

        // Act
        Response result = limitedInterceptor.intercept(mockChain);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.code());
        assertNotNull(result.body());

        // Verify the actual response body is NOT truncated
        String actualBody = result.body().string();
        assertEquals(longBody.length(), actualBody.length());
        assertEquals(longBody, actualBody);

        // The truncation only happens in logging (line 198-200 in LoggingInterceptor.java)
        // We can't directly verify the log output, but we verified the response is intact
    }

    @Test
    void testInterceptWithBinaryBodySkipped() throws IOException {
        // Arrange - Test binary body is skipped when logBinaryBody=false
        LoggingInterceptor noBinaryInterceptor = new LoggingInterceptor(true, 4096, false);

        Request request = new Request.Builder()
                .url("https://api.dify.ai/v1/test")
                .get()
                .build();

        byte[] binaryData = new byte[]{0x00, 0x01, 0x02, 0x03};
        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create(binaryData, MediaType.get("application/octet-stream")))
                .build();

        when(mockChain.request()).thenReturn(request);
        when(mockChain.proceed(any(Request.class))).thenReturn(response);

        // Act
        Response result = noBinaryInterceptor.intercept(mockChain);

        // Assert - Should return original response without consuming body
        assertNotNull(result);
        assertEquals(200, result.code());
        assertNotNull(result.body());
    }

    @Test
    void testInterceptWithBinaryBodyLogged() throws IOException {
        // Arrange - Test binary body is logged when logBinaryBody=true
        LoggingInterceptor binaryInterceptor = new LoggingInterceptor(true, 4096, true);

        Request request = new Request.Builder()
                .url("https://api.dify.ai/v1/test")
                .get()
                .build();

        byte[] binaryData = new byte[]{0x00, 0x01, 0x02, 0x03};
        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create(binaryData, MediaType.get("application/octet-stream")))
                .build();

        when(mockChain.request()).thenReturn(request);
        when(mockChain.proceed(any(Request.class))).thenReturn(response);

        // Act
        Response result = binaryInterceptor.intercept(mockChain);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.code());
        assertNotNull(result.body());
    }

    @Test
    void testInterceptWithUnlimitedBodySize() throws IOException {
        // Arrange - Test unlimited body size (logBodyMaxBytes=0)
        LoggingInterceptor unlimitedInterceptor = new LoggingInterceptor(true, 0, false);

        Request request = new Request.Builder()
                .url("https://api.dify.ai/v1/test")
                .get()
                .build();

        String largeResponse = "x".repeat(10000);
        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create(largeResponse, MediaType.get("text/plain")))
                .build();

        when(mockChain.request()).thenReturn(request);
        when(mockChain.proceed(any(Request.class))).thenReturn(response);

        // Act
        Response result = unlimitedInterceptor.intercept(mockChain);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.code());
        assertNotNull(result.body());
    }

    @Test
    void testInterceptWithImageResponse() throws IOException {
        // Arrange - Test image response (binary)
        LoggingInterceptor interceptor = new LoggingInterceptor(true, 4096, false);

        Request request = new Request.Builder()
                .url("https://api.dify.ai/v1/image")
                .get()
                .build();

        byte[] imageData = new byte[1024]; // 1KB image
        Response response = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create(imageData, MediaType.get("image/png")))
                .build();

        when(mockChain.request()).thenReturn(request);
        when(mockChain.proceed(any(Request.class))).thenReturn(response);

        // Act
        Response result = interceptor.intercept(mockChain);

        // Assert - Binary should be skipped
        assertNotNull(result);
        assertEquals(200, result.code());
        assertNotNull(result.body());
    }

    @Test
    void testConstructorWithAllParameters() {
        // Arrange & Act
        LoggingInterceptor interceptor1 = new LoggingInterceptor();
        LoggingInterceptor interceptor2 = new LoggingInterceptor(true);
        LoggingInterceptor interceptor3 = new LoggingInterceptor(false, 8192, true);

        // Assert
        assertNotNull(interceptor1);
        assertNotNull(interceptor2);
        assertNotNull(interceptor3);
    }
}
