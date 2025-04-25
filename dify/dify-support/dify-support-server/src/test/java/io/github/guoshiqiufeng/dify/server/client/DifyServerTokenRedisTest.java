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
import io.github.guoshiqiufeng.dify.server.dto.response.LoginResponseVO;
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

import static org.mockito.ArgumentMatchers.*;
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
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
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

        LoginResponseVO loginResponse = new LoginResponseVO();
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

        LoginResponseVO refreshResponse = new LoginResponseVO();
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

        LoginResponseVO loginResponse = new LoginResponseVO();
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

        LoginResponseVO loginResponse = new LoginResponseVO();
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
}
