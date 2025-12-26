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
package io.github.guoshiqiufeng.dify.status.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ApiStatus enum test
 *
 * @author yanghq
 * @version 1.7.0
 * @since 2025/12/19 15:00
 */
class ApiStatusTest {

    @Test
    void testCodeOf_Normal() {
        ApiStatus status = ApiStatus.codeOf("normal");
        assertEquals(ApiStatus.NORMAL, status);
    }

    @Test
    void testCodeOf_NotFound404() {
        ApiStatus status = ApiStatus.codeOf("not_found_404");
        assertEquals(ApiStatus.NOT_FOUND_404, status);
    }

    @Test
    void testCodeOf_Unauthorized401() {
        ApiStatus status = ApiStatus.codeOf("unauthorized_401");
        assertEquals(ApiStatus.UNAUTHORIZED_401, status);
    }

    @Test
    void testCodeOf_Timeout() {
        ApiStatus status = ApiStatus.codeOf("timeout");
        assertEquals(ApiStatus.TIMEOUT, status);
    }

    @Test
    void testCodeOf_NetworkError() {
        ApiStatus status = ApiStatus.codeOf("network_error");
        assertEquals(ApiStatus.NETWORK_ERROR, status);
    }

    @Test
    void testCodeOf_ServerError() {
        ApiStatus status = ApiStatus.codeOf("server_error");
        assertEquals(ApiStatus.SERVER_ERROR, status);
    }

    @Test
    void testCodeOf_ClientError() {
        ApiStatus status = ApiStatus.codeOf("client_error");
        assertEquals(ApiStatus.CLIENT_ERROR, status);
    }

    @Test
    void testCodeOf_UnknownError() {
        ApiStatus status = ApiStatus.codeOf("unknown_error");
        assertEquals(ApiStatus.UNKNOWN_ERROR, status);
    }

    @Test
    void testCodeOf_NotConfigured() {
        ApiStatus status = ApiStatus.codeOf("not_configured");
        assertEquals(ApiStatus.NOT_CONFIGURED, status);
    }

    @Test
    void testCodeOf_InvalidCode() {
        ApiStatus status = ApiStatus.codeOf("invalid_code");
        assertEquals(ApiStatus.UNKNOWN_ERROR, status);
    }

    @Test
    void testGetCode() {
        assertEquals("normal", ApiStatus.NORMAL.getCode());
        assertEquals("not_found_404", ApiStatus.NOT_FOUND_404.getCode());
        assertEquals("unauthorized_401", ApiStatus.UNAUTHORIZED_401.getCode());
    }

    @Test
    void testGetDescription() {
        assertEquals("API is functioning normally", ApiStatus.NORMAL.getDescription());
        assertEquals("Resource not found", ApiStatus.NOT_FOUND_404.getDescription());
        assertEquals("Unauthorized access", ApiStatus.UNAUTHORIZED_401.getDescription());
    }

    @Test
    void testAllValues() {
        ApiStatus[] values = ApiStatus.values();
        assertEquals(9, values.length);
    }
}
