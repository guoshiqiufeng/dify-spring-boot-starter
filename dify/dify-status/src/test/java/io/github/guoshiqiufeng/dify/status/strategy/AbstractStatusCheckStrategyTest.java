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
package io.github.guoshiqiufeng.dify.status.strategy;

import io.github.guoshiqiufeng.dify.core.exception.BaseException;
import io.github.guoshiqiufeng.dify.status.dto.ApiStatusResult;
import io.github.guoshiqiufeng.dify.status.enums.ApiStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AbstractStatusCheckStrategy test
 *
 * @author yanghq
 * @version 1.7.0
 * @since 2025/12/19 15:00
 */
class AbstractStatusCheckStrategyTest {

    private TestStatusCheckStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new TestStatusCheckStrategy();
    }

    @Test
    void testExecuteCheck_Success() {
        ApiStatusResult result = strategy.testExecuteCheck("testMethod", "/test", () -> {
            // Success - do nothing
        });

        assertEquals("testMethod", result.getMethodName());
        assertEquals("/test", result.getEndpoint());
        assertEquals(ApiStatus.NORMAL, result.getStatus());
        assertEquals(200, result.getHttpStatusCode());
        assertNotNull(result.getResponseTimeMs());
        assertTrue(result.getResponseTimeMs() >= 0);
        assertNotNull(result.getCheckTime());
    }

    @Test
    void testExecuteCheck_NotFound404() {
        ApiStatusResult result = strategy.testExecuteCheck("testMethod", "/test", () -> {
            throw HttpClientErrorException.NotFound.create(
                    org.springframework.http.HttpStatus.NOT_FOUND,
                    "Not Found",
                    null,
                    null,
                    null
            );
        });

        assertEquals(ApiStatus.NOT_FOUND_404, result.getStatus());
        assertEquals(404, result.getHttpStatusCode());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    void testExecuteCheck_Unauthorized401() {
        ApiStatusResult result = strategy.testExecuteCheck("testMethod", "/test", () -> {
            throw HttpClientErrorException.Unauthorized.create(
                    org.springframework.http.HttpStatus.UNAUTHORIZED,
                    "Unauthorized",
                    null,
                    null,
                    null
            );
        });

        assertEquals(ApiStatus.UNAUTHORIZED_401, result.getStatus());
        assertEquals(401, result.getHttpStatusCode());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    void testExecuteCheck_ClientError() {
        ApiStatusResult result = strategy.testExecuteCheck("testMethod", "/test", () -> {
            throw HttpClientErrorException.BadRequest.create(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "Bad Request",
                    null,
                    null,
                    null
            );
        });

        assertEquals(ApiStatus.CLIENT_ERROR, result.getStatus());
        assertEquals(400, result.getHttpStatusCode());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    void testExecuteCheck_ServerError() {
        ApiStatusResult result = strategy.testExecuteCheck("testMethod", "/test", () -> {
            throw HttpServerErrorException.InternalServerError.create(
                    org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR,
                    "Internal Server Error",
                    null,
                    null,
                    null
            );
        });

        assertEquals(ApiStatus.SERVER_ERROR, result.getStatus());
        assertEquals(500, result.getHttpStatusCode());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    void testExecuteCheck_ResourceAccessException() {
        ApiStatusResult result = strategy.testExecuteCheck("testMethod", "/test", () -> {
            throw new ResourceAccessException("Connection refused");
        });

        assertEquals(ApiStatus.TIMEOUT, result.getStatus());
        assertNotNull(result.getErrorMessage());
        assertTrue(result.getErrorMessage().contains("Connection refused"));
    }

    @Test
    void testExecuteCheck_TimeoutException() {
        ApiStatusResult result = strategy.testExecuteCheck("testMethod", "/test", () -> {
            throw new TimeoutException("Request timeout");
        });

        assertEquals(ApiStatus.TIMEOUT, result.getStatus());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    void testExecuteCheck_SocketTimeoutException() {
        ApiStatusResult result = strategy.testExecuteCheck("testMethod", "/test", () -> {
            throw new SocketTimeoutException("Socket timeout");
        });

        assertEquals(ApiStatus.TIMEOUT, result.getStatus());
        assertNotNull(result.getErrorMessage());
    }

    @Test
    void testExecuteCheck_BaseException_Unauthorized() {
        ApiStatusResult result = strategy.testExecuteCheck("testMethod", "/test", () -> {
            throw new BaseException(401, "Access token is invalid");
        });

        // BaseException is inferred from message content
        assertNotNull(result.getStatus());
        assertNotNull(result.getErrorMessage());
        assertTrue(result.getErrorMessage().toLowerCase().contains("invalid") ||
                   result.getErrorMessage().toLowerCase().contains("token"));
    }

    @Test
    void testExecuteCheck_BaseException_NotFound() {
        ApiStatusResult result = strategy.testExecuteCheck("testMethod", "/test", () -> {
            throw new BaseException(404, "Not Found");
        });

        // BaseException is inferred from message content
        assertNotNull(result.getStatus());
        assertNotNull(result.getErrorMessage());
        assertTrue(result.getErrorMessage().toLowerCase().contains("not found"));
    }

    @Test
    void testExecuteCheck_UnknownException() {
        ApiStatusResult result = strategy.testExecuteCheck("testMethod", "/test", () -> {
            throw new RuntimeException("Unknown error");
        });

        assertEquals(ApiStatus.UNKNOWN_ERROR, result.getStatus());
        assertNotNull(result.getErrorMessage());
        assertEquals("Unknown error", result.getErrorMessage());
    }

    @Test
    void testExecuteCheck_ResponseTime() {
        ApiStatusResult result = strategy.testExecuteCheck("testMethod", "/test", () -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        assertEquals(ApiStatus.NORMAL, result.getStatus());
        assertNotNull(result.getResponseTimeMs());
        assertTrue(result.getResponseTimeMs() >= 100);
    }

    @Test
    void testInferStatusFromMessage_Unauthorized() {
        ApiStatusResult result = strategy.testExecuteCheck("testMethod", "/test", () -> {
            throw new BaseException(401, "Unauthorized access");
        });

        assertEquals(ApiStatus.UNAUTHORIZED_401, result.getStatus());
    }

    @Test
    void testInferStatusFromMessage_NotFound() {
        ApiStatusResult result = strategy.testExecuteCheck("testMethod", "/test", () -> {
            throw new BaseException(404, "Resource not found");
        });

        assertEquals(ApiStatus.NOT_FOUND_404, result.getStatus());
    }

    @Test
    void testInferStatusFromMessage_Timeout() {
        ApiStatusResult result = strategy.testExecuteCheck("testMethod", "/test", () -> {
            throw new BaseException(408, "Request timeout");
        });

        assertEquals(ApiStatus.TIMEOUT, result.getStatus());
    }

    @Test
    void testInferStatusFromMessage_ClientError() {
        ApiStatusResult result = strategy.testExecuteCheck("testMethod", "/test", () -> {
            throw new BaseException(400, "Bad request");
        });

        assertEquals(ApiStatus.CLIENT_ERROR, result.getStatus());
    }

    /**
     * Test implementation of AbstractStatusCheckStrategy
     */
    private static class TestStatusCheckStrategy extends AbstractStatusCheckStrategy {

        @Override
        public ApiStatusResult checkStatus(String methodName, String apiKey) {
            return null;
        }

        @Override
        public String getClientName() {
            return "TestClient";
        }

        public ApiStatusResult testExecuteCheck(String methodName, String endpoint, CheckFunction checkFunction) {
            return executeCheck(methodName, endpoint, checkFunction);
        }
    }
}
