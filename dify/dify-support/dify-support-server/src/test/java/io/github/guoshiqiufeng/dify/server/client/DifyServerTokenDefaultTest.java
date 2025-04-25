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

import io.github.guoshiqiufeng.dify.server.dto.response.LoginResponseVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Test class for DifyServerTokenDefault
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/23 14:35
 */
@ExtendWith(MockitoExtension.class)
class DifyServerTokenDefaultTest {

    private DifyServerTokenDefault tokenDefault;

    @Mock
    private DifyServerClient difyServerClient;

    @BeforeEach
    void setUp() {
        tokenDefault = new DifyServerTokenDefault();
    }

    @Test
    @DisplayName("Test addAuthorizationHeader with no existing token")
    void testAddAuthorizationHeaderWithNoToken() {
        // Setup mock responses
        LoginResponseVO loginResponse = new LoginResponseVO();
        loginResponse.setAccessToken("test-access-token");
        loginResponse.setRefreshToken("test-refresh-token");
        when(difyServerClient.login()).thenReturn(loginResponse);

        // Create a fresh mock for this test
        HttpHeaders httpHeaders = mock(HttpHeaders.class);

        // Execute
        tokenDefault.addAuthorizationHeader(httpHeaders, difyServerClient);

        // Verify
        verify(difyServerClient).login();
        verify(httpHeaders).setBearerAuth("test-access-token");
    }

    @Test
    @DisplayName("Test addAuthorizationHeader with existing token")
    void testAddAuthorizationHeaderWithExistingToken() {
        // Setup - first call to set the token
        LoginResponseVO loginResponse = new LoginResponseVO();
        loginResponse.setAccessToken("test-access-token");
        loginResponse.setRefreshToken("test-refresh-token");
        when(difyServerClient.login()).thenReturn(loginResponse);

        // First call to set token
        HttpHeaders firstHeaders = mock(HttpHeaders.class);
        tokenDefault.addAuthorizationHeader(firstHeaders, difyServerClient);

        // Verify first call
        verify(difyServerClient).login();
        verify(firstHeaders).setBearerAuth("test-access-token");

        // Reset difyServerClient mock
        reset(difyServerClient);

        // Create a new mock for the second call
        HttpHeaders secondHeaders = mock(HttpHeaders.class);

        // Execute second call, should use existing token
        tokenDefault.addAuthorizationHeader(secondHeaders, difyServerClient);

        // Verify
        verify(difyServerClient, never()).login(); // Should not call login again
        verify(secondHeaders).setBearerAuth("test-access-token");
    }

    @Test
    @DisplayName("Test refreshOrObtainNewToken with valid refresh token")
    void testRefreshOrObtainNewTokenWithValidRefreshToken() throws Exception {
        // Setup - first set initial tokens
        LoginResponseVO initialLoginResponse = new LoginResponseVO();
        initialLoginResponse.setAccessToken("initial-access-token");
        initialLoginResponse.setRefreshToken("initial-refresh-token");
        when(difyServerClient.login()).thenReturn(initialLoginResponse);

        // Initialize token with initial values
        HttpHeaders initialHeaders = mock(HttpHeaders.class);
        tokenDefault.addAuthorizationHeader(initialHeaders, difyServerClient);

        // Verify initial setup
        verify(difyServerClient).login();
        verify(initialHeaders).setBearerAuth("initial-access-token");

        // Reset mocks and prepare for refresh
        reset(difyServerClient);

        // Setup refresh response
        LoginResponseVO refreshResponse = new LoginResponseVO();
        refreshResponse.setAccessToken("refreshed-access-token");
        refreshResponse.setRefreshToken("refreshed-refresh-token");
        when(difyServerClient.refreshToken("initial-refresh-token")).thenReturn(refreshResponse);

        // Execute refresh
        tokenDefault.refreshOrObtainNewToken(difyServerClient);

        // Verify refresh calls
        verify(difyServerClient).refreshToken("initial-refresh-token");
        verify(difyServerClient, never()).login();

        // Use reflection to check internal token state directly
        Field accessTokenField = DifyServerTokenDefault.class.getDeclaredField("accessToken");
        accessTokenField.setAccessible(true);
        String accessToken = (String) accessTokenField.get(tokenDefault);
        assertEquals("refreshed-access-token", accessToken);

        Field refreshTokenField = DifyServerTokenDefault.class.getDeclaredField("refreshToken");
        refreshTokenField.setAccessible(true);
        String storedRefreshToken = (String) refreshTokenField.get(tokenDefault);
        assertEquals("refreshed-refresh-token", storedRefreshToken);
    }

    @Test
    @DisplayName("Test refreshOrObtainNewToken with failed refresh")
    void testRefreshOrObtainNewTokenWithFailedRefresh() throws Exception {
        // Setup - first set initial tokens
        LoginResponseVO initialLoginResponse = new LoginResponseVO();
        initialLoginResponse.setAccessToken("initial-access-token");
        initialLoginResponse.setRefreshToken("initial-refresh-token");
        when(difyServerClient.login()).thenReturn(initialLoginResponse);

        // Initialize token with initial values
        HttpHeaders initialHeaders = mock(HttpHeaders.class);
        tokenDefault.addAuthorizationHeader(initialHeaders, difyServerClient);

        // Verify initial setup
        verify(difyServerClient).login();
        verify(initialHeaders).setBearerAuth("initial-access-token");

        // Reset mocks and prepare for refresh
        reset(difyServerClient);

        // Setup failed refresh
        when(difyServerClient.refreshToken("initial-refresh-token")).thenReturn(null);

        // Setup new login response
        LoginResponseVO newLoginResponse = new LoginResponseVO();
        newLoginResponse.setAccessToken("new-access-token");
        newLoginResponse.setRefreshToken("new-refresh-token");
        when(difyServerClient.login()).thenReturn(newLoginResponse);

        // Execute
        tokenDefault.refreshOrObtainNewToken(difyServerClient);

        // Verify
        verify(difyServerClient).refreshToken("initial-refresh-token");
        verify(difyServerClient).login();

        // Use reflection to check internal token state directly
        Field accessTokenField = DifyServerTokenDefault.class.getDeclaredField("accessToken");
        accessTokenField.setAccessible(true);
        String accessToken = (String) accessTokenField.get(tokenDefault);
        assertEquals("new-access-token", accessToken);

        Field refreshTokenField = DifyServerTokenDefault.class.getDeclaredField("refreshToken");
        refreshTokenField.setAccessible(true);
        String storedRefreshToken = (String) refreshTokenField.get(tokenDefault);
        assertEquals("new-refresh-token", storedRefreshToken);
    }
}
