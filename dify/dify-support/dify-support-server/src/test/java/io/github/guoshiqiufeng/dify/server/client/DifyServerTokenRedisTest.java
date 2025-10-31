/*
 * Copyright (c) 2025-2025, fubluesky (fubluesky@foxmail.com)
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

import io.github.guoshiqiufeng.dify.server.cache.DifyRedisKey;
import io.github.guoshiqiufeng.dify.server.dto.response.LoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpHeaders;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.*;

/**
 * Test class for DifyServerTokenRedis
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/23 14:45
 */
@ExtendWith(MockitoExtension.class)
class DifyServerTokenRedisTest {

    private DifyServerTokenRedis tokenRedis;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private DifyServerClient difyServerClient;

    @Mock
    private HttpHeaders httpHeaders;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        tokenRedis = new DifyServerTokenRedis(redisTemplate);
    }

    @Test
    @DisplayName("Test addAuthorizationHeader with token in Redis")
    void testAddAuthorizationHeaderWithTokenInRedis() {
        // Setup
        when(valueOperations.get(DifyRedisKey.ACCESS_TOKEN)).thenReturn("redis-access-token");

        // Execute
        tokenRedis.addAuthorizationHeader(httpHeaders, difyServerClient);

        // Verify
        verify(valueOperations).get(DifyRedisKey.ACCESS_TOKEN);
        verify(difyServerClient, never()).login();
        verify(httpHeaders).setBearerAuth("redis-access-token");
    }

    @Test
    @DisplayName("Test addAuthorizationHeader without token in Redis")
    void testAddAuthorizationHeaderWithoutTokenInRedis() {
        // Setup
        when(valueOperations.get(DifyRedisKey.ACCESS_TOKEN)).thenReturn(null);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setAccessToken("new-access-token");
        loginResponse.setRefreshToken("new-refresh-token");
        when(difyServerClient.login()).thenReturn(loginResponse);

        // Execute
        tokenRedis.addAuthorizationHeader(httpHeaders, difyServerClient);

        // Verify
        verify(valueOperations).get(DifyRedisKey.ACCESS_TOKEN);
        verify(difyServerClient).login();
        verify(valueOperations).set(eq(DifyRedisKey.ACCESS_TOKEN), eq("new-access-token"));
        verify(redisTemplate).expire(eq(DifyRedisKey.ACCESS_TOKEN), anyLong(), eq(TimeUnit.MINUTES));
        verify(valueOperations).set(eq(DifyRedisKey.REFRESH_TOKEN), eq("new-refresh-token"));
        verify(httpHeaders).setBearerAuth("new-access-token");
    }

    @Test
    @DisplayName("Test refreshOrObtainNewToken with valid refresh token")
    void testRefreshOrObtainNewTokenWithValidRefreshToken() {
        // Setup
        when(valueOperations.get(DifyRedisKey.REFRESH_TOKEN)).thenReturn("stored-refresh-token");

        LoginResponse refreshResponse = new LoginResponse();
        refreshResponse.setAccessToken("refreshed-access-token");
        refreshResponse.setRefreshToken("refreshed-refresh-token");
        when(difyServerClient.refreshToken("stored-refresh-token")).thenReturn(refreshResponse);

        // Execute
        tokenRedis.refreshOrObtainNewToken(difyServerClient);

        // Verify
        verify(valueOperations).get(DifyRedisKey.REFRESH_TOKEN);
        verify(difyServerClient).refreshToken("stored-refresh-token");
        verify(difyServerClient, never()).login();

        verify(valueOperations).set(eq(DifyRedisKey.ACCESS_TOKEN), eq("refreshed-access-token"));
        verify(redisTemplate).expire(eq(DifyRedisKey.ACCESS_TOKEN), anyLong(), eq(TimeUnit.MINUTES));
        verify(valueOperations).set(eq(DifyRedisKey.REFRESH_TOKEN), eq("refreshed-refresh-token"));
    }

    @Test
    @DisplayName("Test refreshOrObtainNewToken with failed refresh")
    void testRefreshOrObtainNewTokenWithFailedRefresh() {
        // Setup
        when(valueOperations.get(DifyRedisKey.REFRESH_TOKEN)).thenReturn("stored-refresh-token");
        when(difyServerClient.refreshToken("stored-refresh-token")).thenReturn(null);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setAccessToken("new-access-token");
        loginResponse.setRefreshToken("new-refresh-token");
        when(difyServerClient.login()).thenReturn(loginResponse);

        // Execute
        tokenRedis.refreshOrObtainNewToken(difyServerClient);

        // Verify
        verify(valueOperations).get(DifyRedisKey.REFRESH_TOKEN);
        verify(difyServerClient).refreshToken("stored-refresh-token");
        verify(difyServerClient).login();

        verify(valueOperations).set(eq(DifyRedisKey.ACCESS_TOKEN), eq("new-access-token"));
        verify(redisTemplate).expire(eq(DifyRedisKey.ACCESS_TOKEN), anyLong(), eq(TimeUnit.MINUTES));
        verify(valueOperations).set(eq(DifyRedisKey.REFRESH_TOKEN), eq("new-refresh-token"));
    }

    @Test
    @DisplayName("Test refreshOrObtainNewToken with no refresh token")
    void testRefreshOrObtainNewTokenWithNoRefreshToken() {
        // Setup
        when(valueOperations.get(DifyRedisKey.REFRESH_TOKEN)).thenReturn(null);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setAccessToken("new-access-token");
        loginResponse.setRefreshToken("new-refresh-token");
        when(difyServerClient.login()).thenReturn(loginResponse);

        // Execute
        tokenRedis.refreshOrObtainNewToken(difyServerClient);

        // Verify
        verify(valueOperations).get(DifyRedisKey.REFRESH_TOKEN);
        verify(difyServerClient, never()).refreshToken(anyString());
        verify(difyServerClient).login();

        verify(valueOperations).set(eq(DifyRedisKey.ACCESS_TOKEN), eq("new-access-token"));
        verify(redisTemplate).expire(eq(DifyRedisKey.ACCESS_TOKEN), anyLong(), eq(TimeUnit.MINUTES));
        verify(valueOperations).set(eq(DifyRedisKey.REFRESH_TOKEN), eq("new-refresh-token"));
    }

    @Test
    @DisplayName("Test executeWithRetry with successful operation")
    void testExecuteWithRetrySuccessful() {
        // Setup
        String expectedResult = "success";
        RequestSupplier<String> supplier = () -> expectedResult;

        // Execute
        String result = tokenRedis.executeWithRetry(supplier, difyServerClient);

        // Verify
        assertEquals(expectedResult, result);
    }

    @Test
    @DisplayName("Test executeWithRetry with 401 error that gets resolved after retry")
    void testExecuteWithRetryWith401() {
        // Setup - first call throws 401, second succeeds
        String expectedResult = "success";
        RequestSupplier<String> mockSupplier = mock(RequestSupplier.class);
        when(mockSupplier.get())
            .thenThrow(new RuntimeException("[401] Unauthorized"))
            .thenReturn(expectedResult);

        // Execute - this should trigger refresh and retry
        String result = tokenRedis.executeWithRetry(mockSupplier, difyServerClient);

        // Verify
        assertEquals(expectedResult, result);
        verify(mockSupplier, times(2)).get();
        verify(difyServerClient, times(1)).login(); // Should attempt to get new token
    }

    @Test
    @DisplayName("Test executeWithRetry with non-401 error that should not retry")
    void testExecuteWithRetryWithNon401Error() {
        // Setup
        RequestSupplier<String> supplier = mock(RequestSupplier.class);
        RuntimeException expectedException = new RuntimeException("Some other error");
        when(supplier.get()).thenThrow(expectedException);

        // Execute and verify exception is re-thrown
        RuntimeException thrown = org.junit.jupiter.api.Assertions.assertThrows(
            RuntimeException.class,
            () -> tokenRedis.executeWithRetry(supplier, difyServerClient)
        );

        // Verify
        assertEquals(expectedException, thrown);
        verify(supplier, times(1)).get();
        verify(difyServerClient, never()).login(); // Should not attempt to refresh
    }

    @Test
    @DisplayName("Test executeWithRetry with multiple 401 errors that reach max retries")
    void testExecuteWithRetryMaxRetriesReached() {
        // Setup - always throw 401
        RequestSupplier<String> supplier = mock(RequestSupplier.class);
        when(supplier.get()).thenThrow(new RuntimeException("[401] Unauthorized"));

        // Execute and verify exception is thrown after max retries
        RuntimeException thrown = org.junit.jupiter.api.Assertions.assertThrows(
            RuntimeException.class,
            () -> tokenRedis.executeWithRetry(supplier, difyServerClient)
        );

        // Verify
        org.junit.jupiter.api.Assertions.assertTrue(thrown.getMessage().contains("[401] Unauthorized"));
        verify(supplier, times(3)).get(); // MAX_RETRY_ATTEMPTS = 3
        verify(difyServerClient, times(2)).login(); // Should attempt to refresh token 3 times
    }

    @Test
    @DisplayName("Test addAuthorizationCookies with token in Redis")
    void testAddAuthorizationCookiesWithTokenInRedis() {
        // Setup
        org.springframework.util.MultiValueMap<String, String> cookies = mock(org.springframework.util.MultiValueMap.class);

        when(valueOperations.get(DifyRedisKey.ACCESS_TOKEN)).thenReturn("redis-access-token");
        when(valueOperations.get(DifyRedisKey.CSRF_TOKEN)).thenReturn("redis-csrf-token");

        // Execute
        tokenRedis.addAuthorizationCookies(cookies, difyServerClient);

        // Verify
        verify(valueOperations).get(DifyRedisKey.ACCESS_TOKEN);
        verify(valueOperations).get(DifyRedisKey.CSRF_TOKEN);
        verify(difyServerClient, never()).login();
        verify(cookies).add("access_token", "redis-access-token");
        verify(cookies).add("csrf_token", "redis-csrf-token");
    }

    @Test
    @DisplayName("Test addAuthorizationCookies without token in Redis")
    void testAddAuthorizationCookiesWithoutTokenInRedis() {
        // Setup
        org.springframework.util.MultiValueMap<String, String> cookies = mock(org.springframework.util.MultiValueMap.class);

        when(valueOperations.get(DifyRedisKey.ACCESS_TOKEN)).thenReturn(null);
        when(valueOperations.get(DifyRedisKey.CSRF_TOKEN)).thenReturn(null);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setAccessToken("new-access-token");
        loginResponse.setRefreshToken("new-refresh-token");
        loginResponse.setCsrfToken("new-csrf-token");
        when(difyServerClient.login()).thenReturn(loginResponse);

        // Execute
        tokenRedis.addAuthorizationCookies(cookies, difyServerClient);

        // Verify
        verify(valueOperations).get(DifyRedisKey.ACCESS_TOKEN);
        verify(valueOperations).get(DifyRedisKey.CSRF_TOKEN);
        verify(difyServerClient).login();
        verify(valueOperations).set(eq(DifyRedisKey.ACCESS_TOKEN), eq("new-access-token"));
        verify(redisTemplate).expire(eq(DifyRedisKey.ACCESS_TOKEN), anyLong(), eq(TimeUnit.MINUTES));
        verify(valueOperations).set(eq(DifyRedisKey.REFRESH_TOKEN), eq("new-refresh-token"));
        verify(valueOperations).set(eq(DifyRedisKey.CSRF_TOKEN), eq("new-csrf-token"));
        verify(cookies).add("access_token", "new-access-token");
        verify(cookies).add("csrf_token", null);
    }
}
