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
package io.github.guoshiqiufeng.dify.client.core.http;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for HttpClientException
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/01/06
 */
class HttpClientExceptionTest {

    @Test
    void testConstructorWithMessage() {
        String message = "HTTP request failed";
        HttpClientException exception = new HttpClientException(message);

        assertEquals(message, exception.getMessage());
        assertEquals(-1, exception.getStatusCode());
        assertNull(exception.getResponseBody());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        String message = "HTTP request failed";
        Throwable cause = new IllegalArgumentException("Invalid request");
        HttpClientException exception = new HttpClientException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(-1, exception.getStatusCode());
        assertNull(exception.getResponseBody());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testConstructorWithStatusCodeAndResponseBody() {
        int statusCode = 404;
        String responseBody = "{\"error\":\"Not found\"}";
        HttpClientException exception = new HttpClientException(statusCode, responseBody);

        assertEquals(statusCode, exception.getStatusCode());
        assertEquals(responseBody, exception.getResponseBody());
        assertTrue(exception.getMessage().contains("404"));
        assertTrue(exception.getMessage().contains(responseBody));
    }

    @Test
    void testConstructorWithMessageStatusCodeAndResponseBody() {
        String message = "Custom error message";
        int statusCode = 500;
        String responseBody = "{\"error\":\"Internal server error\"}";
        HttpClientException exception = new HttpClientException(message, statusCode, responseBody);

        assertEquals(message, exception.getMessage());
        assertEquals(statusCode, exception.getStatusCode());
        assertEquals(responseBody, exception.getResponseBody());
    }

    @Test
    void testExceptionIsRuntimeException() {
        HttpClientException exception = new HttpClientException("test");
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testExceptionCanBeThrown() {
        assertThrows(HttpClientException.class, () -> {
            throw new HttpClientException("test exception");
        });
    }

    @Test
    void testStatusCodeDefaultValue() {
        HttpClientException exception = new HttpClientException("test");
        assertEquals(-1, exception.getStatusCode());
    }

    @Test
    void testResponseBodyDefaultValue() {
        HttpClientException exception = new HttpClientException("test");
        assertNull(exception.getResponseBody());
    }

    @Test
    void testWithNullResponseBody() {
        HttpClientException exception = new HttpClientException(404, null);
        assertEquals(404, exception.getStatusCode());
        assertNull(exception.getResponseBody());
    }

    @Test
    void testWithEmptyResponseBody() {
        HttpClientException exception = new HttpClientException(404, "");
        assertEquals(404, exception.getStatusCode());
        assertEquals("", exception.getResponseBody());
    }

    @Test
    void testWithVariousStatusCodes() {
        HttpClientException exception400 = new HttpClientException(400, "Bad Request");
        assertEquals(400, exception400.getStatusCode());

        HttpClientException exception401 = new HttpClientException(401, "Unauthorized");
        assertEquals(401, exception401.getStatusCode());

        HttpClientException exception403 = new HttpClientException(403, "Forbidden");
        assertEquals(403, exception403.getStatusCode());

        HttpClientException exception500 = new HttpClientException(500, "Internal Server Error");
        assertEquals(500, exception500.getStatusCode());

        HttpClientException exception503 = new HttpClientException(503, "Service Unavailable");
        assertEquals(503, exception503.getStatusCode());
    }
}
