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
package io.github.guoshiqiufeng.dify.status.dto;

import io.github.guoshiqiufeng.dify.status.enums.ApiStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ApiStatusResult test
 *
 * @author yanghq
 * @version 1.7.0
 * @since 2025/12/19 15:00
 */
class ApiStatusResultTest {

    @Test
    void testBuilder() {
        LocalDateTime now = LocalDateTime.now();
        ApiStatusResult result = ApiStatusResult.builder()
                .methodName("testMethod")
                .endpoint("/test")
                .status(ApiStatus.NORMAL)
                .responseTimeMs(100L)
                .errorMessage(null)
                .httpStatusCode(200)
                .checkTime(now)
                .details("Test details")
                .build();

        assertEquals("testMethod", result.getMethodName());
        assertEquals("/test", result.getEndpoint());
        assertEquals(ApiStatus.NORMAL, result.getStatus());
        assertEquals(100L, result.getResponseTimeMs());
        assertNull(result.getErrorMessage());
        assertEquals(200, result.getHttpStatusCode());
        assertEquals(now, result.getCheckTime());
        assertEquals("Test details", result.getDetails());
    }

    @Test
    void testSettersAndGetters() {
        ApiStatusResult result = new ApiStatusResult();
        LocalDateTime now = LocalDateTime.now();

        result.setMethodName("testMethod");
        result.setEndpoint("/test");
        result.setStatus(ApiStatus.NORMAL);
        result.setResponseTimeMs(100L);
        result.setErrorMessage("Error");
        result.setHttpStatusCode(200);
        result.setCheckTime(now);
        result.setDetails("Details");

        assertEquals("testMethod", result.getMethodName());
        assertEquals("/test", result.getEndpoint());
        assertEquals(ApiStatus.NORMAL, result.getStatus());
        assertEquals(100L, result.getResponseTimeMs());
        assertEquals("Error", result.getErrorMessage());
        assertEquals(200, result.getHttpStatusCode());
        assertEquals(now, result.getCheckTime());
        assertEquals("Details", result.getDetails());
    }

    @Test
    void testErrorResult() {
        ApiStatusResult result = ApiStatusResult.builder()
                .methodName("failedMethod")
                .endpoint("/failed")
                .status(ApiStatus.NOT_FOUND_404)
                .responseTimeMs(50L)
                .errorMessage("Resource not found")
                .httpStatusCode(404)
                .checkTime(LocalDateTime.now())
                .build();

        assertEquals(ApiStatus.NOT_FOUND_404, result.getStatus());
        assertEquals("Resource not found", result.getErrorMessage());
        assertEquals(404, result.getHttpStatusCode());
    }

    @Test
    void testNoArgsConstructor() {
        ApiStatusResult result = new ApiStatusResult();
        assertNotNull(result);
        assertNull(result.getMethodName());
        assertNull(result.getStatus());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        ApiStatusResult result = new ApiStatusResult(
                "method",
                "/endpoint",
                ApiStatus.NORMAL,
                100L,
                null,
                200,
                now,
                "details"
        );

        assertEquals("method", result.getMethodName());
        assertEquals("/endpoint", result.getEndpoint());
        assertEquals(ApiStatus.NORMAL, result.getStatus());
        assertEquals(100L, result.getResponseTimeMs());
        assertNull(result.getErrorMessage());
        assertEquals(200, result.getHttpStatusCode());
        assertEquals(now, result.getCheckTime());
        assertEquals("details", result.getDetails());
    }
}
