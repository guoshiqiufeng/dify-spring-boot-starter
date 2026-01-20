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
package io.github.guoshiqiufeng.dify.client.core.http.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for HttpStatusValidator
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026-01-20
 */
class HttpStatusValidatorTest {

    @Test
    void testIsSuccessfulWithValidSuccessCodes() {
        assertTrue(HttpStatusValidator.isSuccessful(200));
        assertTrue(HttpStatusValidator.isSuccessful(201));
        assertTrue(HttpStatusValidator.isSuccessful(204));
        assertTrue(HttpStatusValidator.isSuccessful(299));
    }

    @Test
    void testIsSuccessfulWithBoundaryValues() {
        assertTrue(HttpStatusValidator.isSuccessful(200));
        assertTrue(HttpStatusValidator.isSuccessful(299));
        assertFalse(HttpStatusValidator.isSuccessful(199));
        assertFalse(HttpStatusValidator.isSuccessful(300));
    }

    @Test
    void testIsSuccessfulWithNonSuccessCodes() {
        assertFalse(HttpStatusValidator.isSuccessful(100));
        assertFalse(HttpStatusValidator.isSuccessful(199));
        assertFalse(HttpStatusValidator.isSuccessful(300));
        assertFalse(HttpStatusValidator.isSuccessful(400));
        assertFalse(HttpStatusValidator.isSuccessful(500));
    }

    @Test
    void testIsClientErrorWithValidClientErrorCodes() {
        assertTrue(HttpStatusValidator.isClientError(400));
        assertTrue(HttpStatusValidator.isClientError(401));
        assertTrue(HttpStatusValidator.isClientError(403));
        assertTrue(HttpStatusValidator.isClientError(404));
        assertTrue(HttpStatusValidator.isClientError(499));
    }

    @Test
    void testIsClientErrorWithBoundaryValues() {
        assertTrue(HttpStatusValidator.isClientError(400));
        assertTrue(HttpStatusValidator.isClientError(499));
        assertFalse(HttpStatusValidator.isClientError(399));
        assertFalse(HttpStatusValidator.isClientError(500));
    }

    @Test
    void testIsClientErrorWithNonClientErrorCodes() {
        assertFalse(HttpStatusValidator.isClientError(200));
        assertFalse(HttpStatusValidator.isClientError(300));
        assertFalse(HttpStatusValidator.isClientError(399));
        assertFalse(HttpStatusValidator.isClientError(500));
        assertFalse(HttpStatusValidator.isClientError(600));
    }

    @Test
    void testIsServerErrorWithValidServerErrorCodes() {
        assertTrue(HttpStatusValidator.isServerError(500));
        assertTrue(HttpStatusValidator.isServerError(501));
        assertTrue(HttpStatusValidator.isServerError(502));
        assertTrue(HttpStatusValidator.isServerError(503));
        assertTrue(HttpStatusValidator.isServerError(599));
    }

    @Test
    void testIsServerErrorWithBoundaryValues() {
        assertTrue(HttpStatusValidator.isServerError(500));
        assertTrue(HttpStatusValidator.isServerError(599));
        assertFalse(HttpStatusValidator.isServerError(499));
        assertFalse(HttpStatusValidator.isServerError(600));
    }

    @Test
    void testIsServerErrorWithNonServerErrorCodes() {
        assertFalse(HttpStatusValidator.isServerError(200));
        assertFalse(HttpStatusValidator.isServerError(300));
        assertFalse(HttpStatusValidator.isServerError(400));
        assertFalse(HttpStatusValidator.isServerError(499));
        assertFalse(HttpStatusValidator.isServerError(600));
    }

    @Test
    void testIsErrorWithClientErrorCodes() {
        assertTrue(HttpStatusValidator.isError(400));
        assertTrue(HttpStatusValidator.isError(404));
        assertTrue(HttpStatusValidator.isError(499));
    }

    @Test
    void testIsErrorWithServerErrorCodes() {
        assertTrue(HttpStatusValidator.isError(500));
        assertTrue(HttpStatusValidator.isError(503));
        assertTrue(HttpStatusValidator.isError(599));
    }

    @Test
    void testIsErrorWithBoundaryValues() {
        assertTrue(HttpStatusValidator.isError(400));
        assertTrue(HttpStatusValidator.isError(599));
        assertFalse(HttpStatusValidator.isError(399));
        assertFalse(HttpStatusValidator.isError(600));
    }

    @Test
    void testIsErrorWithNonErrorCodes() {
        assertFalse(HttpStatusValidator.isError(100));
        assertFalse(HttpStatusValidator.isError(200));
        assertFalse(HttpStatusValidator.isError(300));
        assertFalse(HttpStatusValidator.isError(399));
        assertFalse(HttpStatusValidator.isError(600));
    }

    @Test
    void testCommonHttpStatusCodes() {
        // Success codes
        assertTrue(HttpStatusValidator.isSuccessful(200)); // OK
        assertTrue(HttpStatusValidator.isSuccessful(201)); // Created
        assertTrue(HttpStatusValidator.isSuccessful(204)); // No Content

        // Client error codes
        assertTrue(HttpStatusValidator.isClientError(400)); // Bad Request
        assertTrue(HttpStatusValidator.isClientError(401)); // Unauthorized
        assertTrue(HttpStatusValidator.isClientError(403)); // Forbidden
        assertTrue(HttpStatusValidator.isClientError(404)); // Not Found

        // Server error codes
        assertTrue(HttpStatusValidator.isServerError(500)); // Internal Server Error
        assertTrue(HttpStatusValidator.isServerError(502)); // Bad Gateway
        assertTrue(HttpStatusValidator.isServerError(503)); // Service Unavailable
    }

    @Test
    void testNegativeStatusCodes() {
        assertFalse(HttpStatusValidator.isSuccessful(-1));
        assertFalse(HttpStatusValidator.isClientError(-1));
        assertFalse(HttpStatusValidator.isServerError(-1));
        assertFalse(HttpStatusValidator.isError(-1));
    }

    @Test
    void testVeryLargeStatusCodes() {
        assertFalse(HttpStatusValidator.isSuccessful(1000));
        assertFalse(HttpStatusValidator.isClientError(1000));
        assertFalse(HttpStatusValidator.isServerError(1000));
        assertFalse(HttpStatusValidator.isError(1000));
    }

    @Test
    void testZeroStatusCode() {
        assertFalse(HttpStatusValidator.isSuccessful(0));
        assertFalse(HttpStatusValidator.isClientError(0));
        assertFalse(HttpStatusValidator.isServerError(0));
        assertFalse(HttpStatusValidator.isError(0));
    }
}
