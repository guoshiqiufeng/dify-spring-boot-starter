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
package io.github.guoshiqiufeng.dify.server.client;

import io.github.guoshiqiufeng.dify.client.core.map.MultiValueMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for BaseDifyServerToken
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/23 14:25
 */
@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class BaseDifyServerTokenTest {

    private TestBaseDifyServerToken token;

    @Mock
    private DifyServerClient difyServerClient;

    @BeforeEach
    void setUp() {
        token = new TestBaseDifyServerToken();
    }

    @Test
    @DisplayName("Test executeWithRetry successful on first attempt")
    void testExecuteWithRetrySuccessOnFirstAttempt() {
        // Setup
        RequestSupplier<String> supplier = () -> "success";

        // Execute
        String result = token.executeWithRetry(supplier, difyServerClient);

        // Verify
        assertEquals("success", result);
        assertEquals(0, token.refreshTokenCallCount);
    }

    @Test
    @DisplayName("Test executeWithRetry with one retry")
    void testExecuteWithRetryWithOneRetry() {
        // Setup - first call fails with 401, second succeeds
        RequestSupplier<String> supplier = mock(RequestSupplier.class);
        when(supplier.get())
                .thenThrow(new RuntimeException("[401] Unauthorized"))
                .thenReturn("success after retry");

        // Execute
        String result = token.executeWithRetry(supplier, difyServerClient);

        // Verify
        assertEquals("success after retry", result);
        assertEquals(1, token.refreshTokenCallCount);
        verify(supplier, times(2)).get();
    }

    @Test
    @DisplayName("Test executeWithRetry with maximum retries")
    void testExecuteWithRetryMaximumRetries() {
        // Setup - all attempts fail with 401
        RuntimeException authError = new RuntimeException("[401] Unauthorized");
        RequestSupplier<String> supplier = mock(RequestSupplier.class);
        when(supplier.get()).thenThrow(authError);

        // Execute and assert
        Exception exception = assertThrows(RuntimeException.class, () ->
                token.executeWithRetry(supplier, difyServerClient));

        // Check that the original error is thrown, not a "Max retry attempts reached" exception
        assertEquals("[401] Unauthorized", exception.getMessage());
        assertSame(authError, exception);
        assertEquals(2, token.refreshTokenCallCount); // Should try to refresh 3 times
        verify(supplier, times(3)).get();
    }

    @Test
    @DisplayName("Test executeWithRetry with non-401 error")
    void testExecuteWithRetryNon401Error() {
        // Setup - non-401 error should not trigger retry
        RuntimeException error = new RuntimeException("[500] Server Error");
        RequestSupplier<String> supplier = mock(RequestSupplier.class);
        when(supplier.get()).thenThrow(error);

        // Execute and assert
        Exception exception = assertThrows(RuntimeException.class, () ->
                token.executeWithRetry(supplier, difyServerClient));

        assertSame(error, exception);
        assertEquals(0, token.refreshTokenCallCount); // Should not try to refresh
        verify(supplier, times(1)).get();
    }

    /**
     * Test implementation of BaseDifyServerToken
     * for testing purposes
     */
    private static class TestBaseDifyServerToken extends BaseDifyServerToken {
        int refreshTokenCallCount = 0;

        @Override
        public void addAuthorizationHeader(io.github.guoshiqiufeng.dify.client.core.http.HttpHeaders headers, DifyServerClient difyServerClient) {
            // No-op for testing
        }

        @Override
        public void addAuthorizationCookies(io.github.guoshiqiufeng.dify.client.core.map.MultiValueMap<String, String> cookies, DifyServerClient difyServerClient) {

        }

        @Override
        void refreshOrObtainNewToken(DifyServerClient difyServerClient) {
            refreshTokenCallCount++;
        }
    }
}
