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
package io.github.guoshiqiufeng.dify.client.integration.spring.http.util;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for SpringErrorResponseExtractor
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026-01-21
 */
class Spring5ErrorResponseExtractorTest {

    @Test
    void testExtractErrorResponseFromHttpClientErrorException() {
        HttpClientErrorException exception = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND,
                "Not Found",
                HttpHeaders.EMPTY,
                "{\"error\":\"not found\"}".getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
        );

        ResponseEntity<String> response = SpringErrorResponseExtractor.extractErrorResponse(exception);

        assertNotNull(response);
        assertEquals(404, response.getStatusCode().value());
        assertEquals("{\"error\":\"not found\"}", response.getBody());
    }

    @Test
    void testExtractErrorResponseFromHttpServerErrorException() {
        HttpServerErrorException exception = HttpServerErrorException.create(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                HttpHeaders.EMPTY,
                "{\"error\":\"server error\"}".getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
        );

        ResponseEntity<String> response = SpringErrorResponseExtractor.extractErrorResponse(exception);

        assertNotNull(response);
        assertEquals(500, response.getStatusCode().value());
        assertEquals("{\"error\":\"server error\"}", response.getBody());
    }

    @Test
    void testExtractErrorResponseWithEmptyBody() {
        HttpClientErrorException exception = HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                HttpHeaders.EMPTY,
                new byte[0],
                StandardCharsets.UTF_8
        );

        ResponseEntity<String> response = SpringErrorResponseExtractor.extractErrorResponse(exception);

        assertNotNull(response);
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void testExtractErrorResponseWithHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Custom-Header", "test-value");

        HttpClientErrorException exception = HttpClientErrorException.create(
                HttpStatus.UNAUTHORIZED,
                "Unauthorized",
                headers,
                "{\"error\":\"unauthorized\"}".getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
        );

        ResponseEntity<String> response = SpringErrorResponseExtractor.extractErrorResponse(exception);

        assertNotNull(response);
        assertEquals(401, response.getStatusCode().value());
        assertEquals("test-value", response.getHeaders().getFirst("X-Custom-Header"));
    }

    @Test
    void testExtractErrorResponseFromNonSpringException() {
        RuntimeException exception = new RuntimeException("Generic error");

        ResponseEntity<String> response = SpringErrorResponseExtractor.extractErrorResponse(exception);

        assertNull(response);
    }

    @Test
    void testExtractStatusCode() throws Exception {
        HttpClientErrorException exception = HttpClientErrorException.create(
                HttpStatus.FORBIDDEN,
                "Forbidden",
                HttpHeaders.EMPTY,
                new byte[0],
                StandardCharsets.UTF_8
        );

        int statusCode = SpringErrorResponseExtractor.extractStatusCode(exception, exception.getClass());

        assertEquals(403, statusCode);
    }

    @Test
    void testExtractResponseBody() throws Exception {
        HttpClientErrorException exception = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND,
                "Not Found",
                HttpHeaders.EMPTY,
                "{\"message\":\"resource not found\"}".getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
        );

        String body = SpringErrorResponseExtractor.extractResponseBody(exception, exception.getClass());

        assertEquals("{\"message\":\"resource not found\"}", body);
    }

    @Test
    void testExtractHeaders() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("X-Request-Id", "12345");

        HttpClientErrorException exception = HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                headers,
                new byte[0],
                StandardCharsets.UTF_8
        );

        HttpHeaders extractedHeaders = SpringErrorResponseExtractor.extractHeaders(exception, exception.getClass());

        assertNotNull(extractedHeaders);
        assertEquals("application/json", extractedHeaders.getFirst("Content-Type"));
        assertEquals("12345", extractedHeaders.getFirst("X-Request-Id"));
    }

    @Test
    void testExtractErrorResponseWithMultipleExceptionTypes() {
        // Test HttpClientErrorException
        HttpClientErrorException clientError = HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                HttpHeaders.EMPTY,
                "client error".getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
        );
        ResponseEntity<String> response1 = SpringErrorResponseExtractor.extractErrorResponse(clientError);
        assertNotNull(response1);
        assertEquals(400, response1.getStatusCode().value());

        // Test HttpServerErrorException
        HttpServerErrorException serverError = HttpServerErrorException.create(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Service Unavailable",
                HttpHeaders.EMPTY,
                "server error".getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
        );
        ResponseEntity<String> response2 = SpringErrorResponseExtractor.extractErrorResponse(serverError);
        assertNotNull(response2);
        assertEquals(503, response2.getStatusCode().value());
    }

    @Test
    void testExtractStatusCodeThrowsExceptionForUnsupportedException() {
        RuntimeException exception = new RuntimeException("test");

        assertThrows(Exception.class, () ->
                SpringErrorResponseExtractor.extractStatusCode(exception, exception.getClass())
        );
    }

    @Test
    void testExtractResponseBodyWithNullBody() throws Exception {
        // Create exception with null body
        HttpClientErrorException exception = HttpClientErrorException.create(
                HttpStatus.NO_CONTENT,
                "No Content",
                HttpHeaders.EMPTY,
                null,
                StandardCharsets.UTF_8
        );

        String body = SpringErrorResponseExtractor.extractResponseBody(exception, exception.getClass());

        assertNotNull(body);
    }

    @Test
    void testExtractHeadersReturnsEmptyForUnsupportedException() throws Exception {
        RuntimeException exception = new RuntimeException("test");

        HttpHeaders headers = SpringErrorResponseExtractor.extractHeaders(exception, exception.getClass());

        assertNotNull(headers);
        assertTrue(headers.isEmpty());
    }

    @Test
    void testExtractErrorResponseWithNullResponseBody() {
        HttpClientErrorException exception = HttpClientErrorException.create(
                HttpStatus.NO_CONTENT,
                "No Content",
                HttpHeaders.EMPTY,
                null,
                StandardCharsets.UTF_8
        );

        ResponseEntity<String> response = SpringErrorResponseExtractor.extractErrorResponse(exception);

        assertNotNull(response);
        assertEquals(204, response.getStatusCode().value());
    }

    @Test
    void testExtractErrorResponseWithDifferentStatusCodes() {
        // Test various status codes
        HttpStatus[] statuses = {
                HttpStatus.BAD_REQUEST,
                HttpStatus.UNAUTHORIZED,
                HttpStatus.FORBIDDEN,
                HttpStatus.NOT_FOUND,
                HttpStatus.INTERNAL_SERVER_ERROR,
                HttpStatus.BAD_GATEWAY,
                HttpStatus.SERVICE_UNAVAILABLE
        };

        for (HttpStatus status : statuses) {
            HttpClientErrorException exception = HttpClientErrorException.create(
                    status,
                    status.getReasonPhrase(),
                    HttpHeaders.EMPTY,
                    "error".getBytes(StandardCharsets.UTF_8),
                    StandardCharsets.UTF_8
            );

            ResponseEntity<String> response = SpringErrorResponseExtractor.extractErrorResponse(exception);

            assertNotNull(response);
            assertEquals(status.value(), response.getStatusCode().value());
        }
    }

    @Test
    void testExtractErrorResponseFromHttpStatusCodeException() {
        // Test with HttpStatusCodeException (parent class)
        HttpStatusCodeException exception = HttpClientErrorException.create(
                HttpStatus.CONFLICT,
                "Conflict",
                HttpHeaders.EMPTY,
                "conflict".getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
        );

        ResponseEntity<String> response = SpringErrorResponseExtractor.extractErrorResponse(exception);

        assertNotNull(response);
        assertEquals(409, response.getStatusCode().value());
    }

    @Test
    void testExtractResponseBodyWithEmptyString() throws Exception {
        HttpClientErrorException exception = HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                HttpHeaders.EMPTY,
                "".getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
        );

        String body = SpringErrorResponseExtractor.extractResponseBody(exception, exception.getClass());

        assertNotNull(body);
    }

    @Test
    void testExtractErrorResponseWithLargeBody() {
        StringBuilder largeBody = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            largeBody.append("error message ").append(i).append(" ");
        }

        HttpServerErrorException exception = HttpServerErrorException.create(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                HttpHeaders.EMPTY,
                largeBody.toString().getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
        );

        ResponseEntity<String> response = SpringErrorResponseExtractor.extractErrorResponse(exception);

        assertNotNull(response);
        assertEquals(500, response.getStatusCode().value());
        assertTrue(response.getBody().length() > 1000);
    }

    @Test
    void testExtractErrorResponseWithSpecialCharacters() {
        String specialBody = "{\"error\":\"Special chars: <>&\\\"'\"}";

        HttpClientErrorException exception = HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                HttpHeaders.EMPTY,
                specialBody.getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
        );

        ResponseEntity<String> response = SpringErrorResponseExtractor.extractErrorResponse(exception);

        assertNotNull(response);
        assertEquals(400, response.getStatusCode().value());
        assertTrue(response.getBody().contains("Special chars"));
    }

    @Test
    void testExtractErrorResponseWithMultipleHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("X-Request-Id", "req-123");
        headers.add("X-Correlation-Id", "corr-456");

        HttpClientErrorException exception = HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                headers,
                "error".getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
        );

        ResponseEntity<String> response = SpringErrorResponseExtractor.extractErrorResponse(exception);

        assertNotNull(response);
        assertEquals("application/json", response.getHeaders().getFirst("Content-Type"));
        assertEquals("req-123", response.getHeaders().getFirst("X-Request-Id"));
        assertEquals("corr-456", response.getHeaders().getFirst("X-Correlation-Id"));
    }

    @Test
    void testExtractErrorResponseTriggersExceptionHandling() {
        // Create a mock exception that will cause extraction to fail
        Throwable mockException = new Throwable("test") {
            @Override
            public String toString() {
                throw new RuntimeException("Simulated error");
            }
        };

        ResponseEntity<String> response = SpringErrorResponseExtractor.extractErrorResponse(mockException);

        // Should return null when extraction fails
        assertNull(response);
    }
}
