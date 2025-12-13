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

import io.github.guoshiqiufeng.dify.server.dto.response.LoginResponse;
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
        LoginResponse loginResponse = new LoginResponse();
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
        LoginResponse loginResponse = new LoginResponse();
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
        LoginResponse initialLoginResponse = new LoginResponse();
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
        LoginResponse refreshResponse = new LoginResponse();
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
        LoginResponse initialLoginResponse = new LoginResponse();
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
        LoginResponse newLoginResponse = new LoginResponse();
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

    @Test
    @DisplayName("Test executeWithRetry with successful operation")
    void testExecuteWithRetrySuccessful() {
        // Setup
        String expectedResult = "success";
        RequestSupplier<String> supplier = () -> expectedResult;

        // Execute
        String result = tokenDefault.executeWithRetry(supplier, difyServerClient);

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
        String result = tokenDefault.executeWithRetry(mockSupplier, difyServerClient);

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
            () -> tokenDefault.executeWithRetry(supplier, difyServerClient)
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
            () -> tokenDefault.executeWithRetry(supplier, difyServerClient)
        );

        // Verify
        org.junit.jupiter.api.Assertions.assertTrue(thrown.getMessage().contains("[401] Unauthorized"));
        verify(supplier, times(3)).get(); // MAX_RETRY_ATTEMPTS = 3
        verify(difyServerClient, times(2)).login(); // Should attempt to refresh token 3 times
    }

    @Test
    @DisplayName("Test addAuthorizationCookies with no existing token")
    void testAddAuthorizationCookiesWithNoToken() {
        // Setup
        org.springframework.util.MultiValueMap<String, String> cookies = mock(org.springframework.util.MultiValueMap.class);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setAccessToken("test-access-token");
        loginResponse.setRefreshToken("test-refresh-token");
        loginResponse.setCsrfToken("test-csrf-token");
        when(difyServerClient.login()).thenReturn(loginResponse);

        // Execute
        tokenDefault.addAuthorizationCookies(cookies, difyServerClient);

        // Verify
        verify(difyServerClient).login();
        verify(cookies).add("access_token", "test-access-token");
        verify(cookies).add("csrf_token", "test-csrf-token");
        verify(cookies).add("__Host-access_token", "test-access-token");
        verify(cookies).add("__Host-csrf_token", "test-csrf-token");
    }

    @Test
    @DisplayName("Test addAuthorizationCookies with existing token")
    void testAddAuthorizationCookiesWithExistingToken() {
        // Setup - first call to set the token
        org.springframework.util.MultiValueMap<String, String> initialCookies = mock(org.springframework.util.MultiValueMap.class);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setAccessToken("test-access-token");
        loginResponse.setRefreshToken("test-refresh-token");
        loginResponse.setCsrfToken("test-csrf-token");
        when(difyServerClient.login()).thenReturn(loginResponse);

        // First call to set token
        tokenDefault.addAuthorizationCookies(initialCookies, difyServerClient);

        // Verify first call
        verify(difyServerClient).login();
        verify(initialCookies).add("access_token", "test-access-token");
        verify(initialCookies).add("csrf_token", "test-csrf-token");
        verify(initialCookies).add("__Host-access_token", "test-access-token");
        verify(initialCookies).add("__Host-csrf_token", "test-csrf-token");

        // Reset difyServerClient mock
        reset(difyServerClient);

        // Create a new mock for the second call
        org.springframework.util.MultiValueMap<String, String> secondCookies = mock(org.springframework.util.MultiValueMap.class);

        // Execute second call, should use existing token
        tokenDefault.addAuthorizationCookies(secondCookies, difyServerClient);

        // Verify
        verify(difyServerClient, never()).login(); // Should not call login again
        verify(secondCookies).add("access_token", "test-access-token");
        verify(secondCookies).add("csrf_token", "test-csrf-token");
        verify(secondCookies).add("__Host-access_token", "test-access-token");
        verify(secondCookies).add("__Host-csrf_token", "test-csrf-token");
    }
}
