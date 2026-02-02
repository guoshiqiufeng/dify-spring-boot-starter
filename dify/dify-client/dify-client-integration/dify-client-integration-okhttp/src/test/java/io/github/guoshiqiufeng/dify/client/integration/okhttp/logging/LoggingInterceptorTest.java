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
}
