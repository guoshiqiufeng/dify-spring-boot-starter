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
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for SpringErrorResponseExtractor
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026-01-21
 */
class SpringErrorResponseExtractorTest {

    @Test
    void testExtractResponseBodyFallsBackToExceptionMessage() throws Exception {
        // Create a custom exception that has a message but no response body methods
        Throwable exception = new RuntimeException("Error message from exception");

        String body = SpringErrorResponseExtractor.extractResponseBody(exception, exception.getClass());

        assertEquals("Error message from exception", body);
    }

    @Test
    void testExtractResponseBodyReturnsEmptyWhenNoBodyAndNoMessage() throws Exception {
        // Create exception with null message
        Throwable exception = new RuntimeException((String) null);

        String body = SpringErrorResponseExtractor.extractResponseBody(exception, exception.getClass());

        assertNotNull(body);
        assertEquals("", body);
    }

    @Test
    void testExtractErrorResponseWithRestClientResponseExceptionClassName() {
        // Create a mock exception with RestClientResponseException in class name
        MockRestClientResponseException exception = new MockRestClientResponseException(
                404, "Not Found", "{\"error\":\"not found\"}"
        );

        ResponseEntity<String> response = SpringErrorResponseExtractor.extractErrorResponse(exception);

        assertNotNull(response);
        assertEquals(404, response.getStatusCode().value());
        assertEquals("{\"error\":\"not found\"}", response.getBody());
    }

    @Test
    void testExtractErrorResponseWithWebClientResponseExceptionClassName() {
        // Create a mock exception with WebClientResponseException in class name
        MockWebClientResponseException exception = new MockWebClientResponseException(
                500, "Server Error", "{\"error\":\"server error\"}"
        );

        ResponseEntity<String> response = SpringErrorResponseExtractor.extractErrorResponse(exception);

        assertNotNull(response);
        assertEquals(500, response.getStatusCode().value());
        assertEquals("{\"error\":\"server error\"}", response.getBody());
    }

    @Test
    void testExtractStatusCodeWithIntegerReturn() throws Exception {
        // Test with exception that returns Integer directly from getStatusCode()
        MockExceptionWithIntegerStatusCode exception = new MockExceptionWithIntegerStatusCode(403);

        int statusCode = SpringErrorResponseExtractor.extractStatusCode(exception, exception.getClass());

        assertEquals(403, statusCode);
    }

    @Test
    void testExtractStatusCodeWithRawStatusCode() throws Exception {
        // Test getRawStatusCode() fallback
        MockExceptionWithRawStatusCode exception = new MockExceptionWithRawStatusCode(502);

        int statusCode = SpringErrorResponseExtractor.extractStatusCode(exception, exception.getClass());

        assertEquals(502, statusCode);
    }

    @Test
    void testExtractStatusCodeWithStatusCodeValue() throws Exception {
        // Test getStatusCodeValue() fallback
        MockExceptionWithStatusCodeValue exception = new MockExceptionWithStatusCodeValue(503);

        int statusCode = SpringErrorResponseExtractor.extractStatusCode(exception, exception.getClass());

        assertEquals(503, statusCode);
    }

    @Test
    void testExtractHeadersWithGetHeadersFallback() throws Exception {
        // Test getHeaders() fallback for WebClientResponseException
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Test", "value");
        MockExceptionWithGetHeaders exception = new MockExceptionWithGetHeaders(headers);

        HttpHeaders extractedHeaders = SpringErrorResponseExtractor.extractHeaders(exception, exception.getClass());

        assertNotNull(extractedHeaders);
        assertEquals("value", extractedHeaders.getFirst("X-Test"));
    }

    @Test
    void testExtractResponseBodyWithByteArray() throws Exception {
        // Test getResponseBodyAsByteArray() method
        MockExceptionWithByteArrayBody exception = new MockExceptionWithByteArrayBody(
                "test body".getBytes(StandardCharsets.UTF_8)
        );

        String body = SpringErrorResponseExtractor.extractResponseBody(exception, exception.getClass());

        assertEquals("test body", body);
    }

    @Test
    void testExtractResponseBodyWithEmptyByteArray() throws Exception {
        // Test getResponseBodyAsByteArray() returning empty array
        MockExceptionWithByteArrayBody exception = new MockExceptionWithByteArrayBody(new byte[0]);

        String body = SpringErrorResponseExtractor.extractResponseBody(exception, exception.getClass());

        assertNotNull(body);
    }

    @Test
    void testExtractResponseBodyWithNullByteArray() throws Exception {
        // Test getResponseBodyAsByteArray() returning null
        MockExceptionWithByteArrayBody exception = new MockExceptionWithByteArrayBody(null);

        String body = SpringErrorResponseExtractor.extractResponseBody(exception, exception.getClass());

        assertNotNull(body);
    }

    @Test
    void testExtractStatusCodeThrowsExceptionWhenNoMethodFound() {
        // Exception with no status code methods
        RuntimeException exception = new RuntimeException("test");

        assertThrows(Exception.class, () ->
                SpringErrorResponseExtractor.extractStatusCode(exception, exception.getClass())
        );
    }

    @Test
    void testExtractErrorResponseHandlesExceptionDuringExtraction() {
        // Exception that throws during method invocation
        MockExceptionThatThrows exception = new MockExceptionThatThrows();

        ResponseEntity<String> response = SpringErrorResponseExtractor.extractErrorResponse(exception);

        assertNull(response);
    }

    @Test
    void testExtractErrorResponseWithHttpStatusCodeException() {
        // Test HttpStatusCodeException class name detection
        MockHttpStatusCodeException exception = new MockHttpStatusCodeException(409, "Conflict");

        ResponseEntity<String> response = SpringErrorResponseExtractor.extractErrorResponse(exception);

        assertNotNull(response);
        assertEquals(409, response.getStatusCode().value());
    }

    @Test
    void testExtractResponseBodyWithNullResponseBodyAsString() throws Exception {
        // Test when getResponseBodyAsString() returns null
        MockExceptionWithNullBody exception = new MockExceptionWithNullBody();

        String body = SpringErrorResponseExtractor.extractResponseBody(exception, exception.getClass());

        assertNotNull(body);
    }

    @Test
    void testExtractHeadersReturnsNonHttpHeaders() throws Exception {
        // Test when getResponseHeaders() returns non-HttpHeaders object
        MockExceptionWithInvalidHeaders exception = new MockExceptionWithInvalidHeaders();

        HttpHeaders headers = SpringErrorResponseExtractor.extractHeaders(exception, exception.getClass());

        assertNotNull(headers);
        assertTrue(headers.isEmpty());
    }

    @Test
    void testExtractStatusCodeWithStatusObjectWithoutValueMethod() {
        // Test when getStatusCode() returns object without value() method
        MockExceptionWithInvalidStatusObject exception = new MockExceptionWithInvalidStatusObject();

        assertThrows(Exception.class, () ->
                SpringErrorResponseExtractor.extractStatusCode(exception, exception.getClass())
        );
    }

    // Mock exception classes for testing

    static class MockRestClientResponseException extends RuntimeException {
        private final int statusCode;
        private final String statusText;
        private final String body;

        MockRestClientResponseException(int statusCode, String statusText, String body) {
            this.statusCode = statusCode;
            this.statusText = statusText;
            this.body = body;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getResponseBodyAsString() {
            return body;
        }

        public HttpHeaders getResponseHeaders() {
            return new HttpHeaders();
        }
    }

    static class MockWebClientResponseException extends RuntimeException {
        private final int statusCode;
        private final String statusText;
        private final String body;

        MockWebClientResponseException(int statusCode, String statusText, String body) {
            this.statusCode = statusCode;
            this.statusText = statusText;
            this.body = body;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getResponseBodyAsString() {
            return body;
        }

        public HttpHeaders getHeaders() {
            return new HttpHeaders();
        }
    }

    static class MockExceptionWithIntegerStatusCode extends RuntimeException {
        private final int statusCode;

        MockExceptionWithIntegerStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public Integer getStatusCode() {
            return statusCode;
        }

        public String getResponseBodyAsString() {
            return "";
        }

        public HttpHeaders getResponseHeaders() {
            return new HttpHeaders();
        }
    }

    static class MockExceptionWithRawStatusCode extends RuntimeException {
        private final int statusCode;

        MockExceptionWithRawStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        public int getRawStatusCode() {
            return statusCode;
        }

        public String getResponseBodyAsString() {
            return "";
        }

        public HttpHeaders getResponseHeaders() {
            return new HttpHeaders();
        }
    }

    static class MockExceptionWithStatusCodeValue extends RuntimeException {
        private final int statusCode;

        MockExceptionWithStatusCodeValue(int statusCode) {
            this.statusCode = statusCode;
        }

        public int getStatusCodeValue() {
            return statusCode;
        }

        public String getResponseBodyAsString() {
            return "";
        }

        public HttpHeaders getResponseHeaders() {
            return new HttpHeaders();
        }
    }

    static class MockExceptionWithGetHeaders extends RuntimeException {
        private final HttpHeaders headers;

        MockExceptionWithGetHeaders(HttpHeaders headers) {
            this.headers = headers;
        }

        public int getStatusCode() {
            return 200;
        }

        public String getResponseBodyAsString() {
            return "";
        }

        public HttpHeaders getHeaders() {
            return headers;
        }
    }

    static class MockExceptionWithByteArrayBody extends RuntimeException {
        private final byte[] body;

        MockExceptionWithByteArrayBody(byte[] body) {
            this.body = body;
        }

        public int getStatusCode() {
            return 200;
        }

        public byte[] getResponseBodyAsByteArray() {
            return body;
        }

        public HttpHeaders getResponseHeaders() {
            return new HttpHeaders();
        }
    }

    static class MockExceptionThatThrows extends RuntimeException {
        public int getStatusCode() {
            throw new RuntimeException("Simulated error");
        }
    }

    static class MockHttpStatusCodeException extends RuntimeException {
        private final int statusCode;
        private final String statusText;

        MockHttpStatusCodeException(int statusCode, String statusText) {
            this.statusCode = statusCode;
            this.statusText = statusText;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getResponseBodyAsString() {
            return "";
        }

        public HttpHeaders getResponseHeaders() {
            return new HttpHeaders();
        }
    }

    static class MockExceptionWithNullBody extends RuntimeException {
        public String getResponseBodyAsString() {
            return null;
        }

        public byte[] getResponseBodyAsByteArray() {
            return null;
        }
    }

    static class MockExceptionWithInvalidHeaders extends RuntimeException {
        public Object getResponseHeaders() {
            return "not a HttpHeaders object";
        }

        public Object getHeaders() {
            return "not a HttpHeaders object";
        }
    }

    static class MockExceptionWithInvalidStatusObject extends RuntimeException {
        public Object getStatusCode() {
            return new Object(); // Object without value() method
        }
    }
}
