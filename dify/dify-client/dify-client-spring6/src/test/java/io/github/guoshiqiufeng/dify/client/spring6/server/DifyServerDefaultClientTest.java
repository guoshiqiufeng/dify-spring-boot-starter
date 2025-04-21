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
package io.github.guoshiqiufeng.dify.client.spring6.server;

import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.core.pojo.DifyResult;
import io.github.guoshiqiufeng.dify.server.client.BaseDifyServerToken;
import io.github.guoshiqiufeng.dify.server.client.DifyServerTokenDefault;
import io.github.guoshiqiufeng.dify.server.dto.request.DifyLoginRequestVO;
import io.github.guoshiqiufeng.dify.server.dto.response.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/21 14:55
 */
@DisplayName("DifyServerDefaultClient Tests")
public class DifyServerDefaultClientTest {

    @Test
    @DisplayName("Test login method with valid credentials")
    public void testLogin() {
        // Create mock objects
        RestClient.RequestBodyUriSpec requestBodyUriSpec = Mockito.mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec requestBodySpec = Mockito.mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec responseSpec = Mockito.mock(RestClient.ResponseSpec.class);
        RestClient.Builder mockRestClientBuilder = Mockito.mock(RestClient.Builder.class);
        RestClient mockRestClient = Mockito.mock(RestClient.class);
        WebClient.Builder mockWebClientBuilder = Mockito.mock(WebClient.Builder.class);
        WebClient mockWebClient = Mockito.mock(WebClient.class);
        BaseDifyServerToken mockServerToken = Mockito.mock(BaseDifyServerToken.class);

        // Setup mock behavior
        when(mockRestClientBuilder.baseUrl(anyString())).thenReturn(mockRestClientBuilder);
        when(mockRestClientBuilder.defaultHeaders(any())).thenReturn(mockRestClientBuilder);
        when(mockRestClientBuilder.build()).thenReturn(mockRestClient);

        when(mockWebClientBuilder.baseUrl(anyString())).thenReturn(mockWebClientBuilder);
        when(mockWebClientBuilder.defaultHeaders(any())).thenReturn(mockWebClientBuilder);
        when(mockWebClientBuilder.build()).thenReturn(mockWebClient);

        doReturn(requestBodyUriSpec).when(mockRestClient).post();
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        doReturn(requestBodySpec).when(requestBodySpec).body(any(DifyLoginRequestVO.class));
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any())).thenReturn(responseSpec);

        // Mock the login response
        LoginResultResponseVO mockLoginResult = new LoginResultResponseVO();
        mockLoginResult.setResult(DifyResult.SUCCESS);
        LoginResponseVO mockLoginData = new LoginResponseVO();
        mockLoginData.setAccessToken("test-access-token");
        mockLoginData.setRefreshToken("test-refresh-token");
        mockLoginResult.setData(mockLoginData);
        when(responseSpec.body(LoginResultResponseVO.class)).thenReturn(mockLoginResult);

        // Create server properties
        DifyProperties.Server serverProperties = new DifyProperties.Server();
        serverProperties.setEmail("test@example.com");
        serverProperties.setPassword("password123");

        // Create the client with mocked dependencies
        String baseUrl = "https://cloud.dify.ai";
        DifyServerDefaultClient client = new DifyServerDefaultClient(serverProperties, mockServerToken, baseUrl, null, mockRestClientBuilder, mockWebClientBuilder);

        // Call the method to test
        LoginResponseVO response = client.login();

        // Verify the response
        assertNotNull(response);
        assertEquals("test-access-token", response.getAccessToken());
        assertEquals("test-refresh-token", response.getRefreshToken());

        // Verify interactions with mocks
        verify(requestBodyUriSpec).uri("/console/api/login");

        // Capture and verify the request body
        ArgumentCaptor<DifyLoginRequestVO> bodyCaptor = ArgumentCaptor.forClass(DifyLoginRequestVO.class);
        verify(requestBodySpec).body(bodyCaptor.capture());

        DifyLoginRequestVO capturedBody = bodyCaptor.getValue();
        assertEquals("test@example.com", capturedBody.getEmail());
        assertEquals("password123", capturedBody.getPassword());
    }

    @Test
    @DisplayName("Test refreshToken method")
    public void testRefreshToken() {
        // Create mock objects
        RestClient.RequestBodyUriSpec requestBodyUriSpec = Mockito.mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec requestBodySpec = Mockito.mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec responseSpec = Mockito.mock(RestClient.ResponseSpec.class);
        RestClient.Builder mockRestClientBuilder = Mockito.mock(RestClient.Builder.class);
        RestClient mockRestClient = Mockito.mock(RestClient.class);
        WebClient.Builder mockWebClientBuilder = Mockito.mock(WebClient.Builder.class);
        WebClient mockWebClient = Mockito.mock(WebClient.class);
        BaseDifyServerToken mockServerToken = Mockito.mock(BaseDifyServerToken.class);

        // Setup mock behavior
        when(mockRestClientBuilder.baseUrl(anyString())).thenReturn(mockRestClientBuilder);
        when(mockRestClientBuilder.defaultHeaders(any())).thenReturn(mockRestClientBuilder);
        when(mockRestClientBuilder.build()).thenReturn(mockRestClient);

        when(mockWebClientBuilder.baseUrl(anyString())).thenReturn(mockWebClientBuilder);
        when(mockWebClientBuilder.defaultHeaders(any())).thenReturn(mockWebClientBuilder);
        when(mockWebClientBuilder.build()).thenReturn(mockWebClient);

        doReturn(requestBodyUriSpec).when(mockRestClient).post();
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        doReturn(requestBodySpec).when(requestBodySpec).body(any(Map.class));
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any())).thenReturn(responseSpec);

        // Mock the refresh token response
        LoginResultResponseVO mockRefreshResult = new LoginResultResponseVO();
        mockRefreshResult.setResult(DifyResult.SUCCESS);
        LoginResponseVO mockRefreshData = new LoginResponseVO();
        mockRefreshData.setAccessToken("new-access-token");
        mockRefreshData.setRefreshToken("new-refresh-token");

        mockRefreshResult.setData(mockRefreshData);
        when(responseSpec.body(LoginResultResponseVO.class)).thenReturn(mockRefreshResult);

        // Create server properties
        DifyProperties.Server serverProperties = new DifyProperties.Server();

        // Create the client with mocked dependencies
        String baseUrl = "https://cloud.dify.ai";
        DifyServerDefaultClient client = new DifyServerDefaultClient(serverProperties, mockServerToken, baseUrl, null, mockRestClientBuilder, mockWebClientBuilder);

        // Call the method to test
        String refreshToken = "old-refresh-token";
        LoginResponseVO response = client.refreshToken(refreshToken);

        // Verify the response
        assertNotNull(response);
        assertEquals("new-access-token", response.getAccessToken());
        assertEquals("new-refresh-token", response.getRefreshToken());

        // Verify interactions with mocks
        verify(requestBodyUriSpec).uri("/console/api/refresh-token");

        // Capture and verify the request body
        ArgumentCaptor<Map<String, String>> bodyCaptor = ArgumentCaptor.forClass(Map.class);
        verify(requestBodySpec).body(bodyCaptor.capture());

        Map<String, String> capturedBody = bodyCaptor.getValue();
        assertEquals("old-refresh-token", capturedBody.get("refresh_token"));
    }

    @Test
    @DisplayName("Test app method")
    public void testApp() {
        // Create mock objects
        RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec<?> requestHeadersSpec = Mockito.mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = Mockito.mock(RestClient.ResponseSpec.class);
        RestClient.Builder mockRestClientBuilder = Mockito.mock(RestClient.Builder.class);
        RestClient mockRestClient = Mockito.mock(RestClient.class);
        WebClient.Builder mockWebClientBuilder = Mockito.mock(WebClient.Builder.class);
        WebClient mockWebClient = Mockito.mock(WebClient.class);
        BaseDifyServerToken mockServerToken = new DifyServerTokenDefault();

        // Setup mock behavior
        when(mockRestClientBuilder.baseUrl(anyString())).thenReturn(mockRestClientBuilder);
        when(mockRestClientBuilder.defaultHeaders(any())).thenReturn(mockRestClientBuilder);
        when(mockRestClientBuilder.build()).thenReturn(mockRestClient);

        when(mockWebClientBuilder.baseUrl(anyString())).thenReturn(mockWebClientBuilder);
        when(mockWebClientBuilder.defaultHeaders(any())).thenReturn(mockWebClientBuilder);
        when(mockWebClientBuilder.build()).thenReturn(mockWebClient);

        doReturn(requestHeadersUriSpec).when(mockRestClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(anyString(), anyString());
        doReturn(requestHeadersSpec).when(requestHeadersSpec).headers(any());
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any())).thenReturn(responseSpec);

        // Mock the app response
        AppsResponseVO mockAppResponse = new AppsResponseVO();
        mockAppResponse.setId("app-123");
        mockAppResponse.setName("Test App");
        mockAppResponse.setMode("completion");
        when(responseSpec.body(AppsResponseVO.class)).thenReturn(mockAppResponse);

        // Create server properties
        DifyProperties.Server serverProperties = new DifyProperties.Server();

        // Create the client with mocked dependencies
        String baseUrl = "https://cloud.dify.ai";
        DifyServerDefaultClient client = new DifyServerDefaultClient(serverProperties, mockServerToken, baseUrl, null, mockRestClientBuilder, mockWebClientBuilder);

        // Call the method to test
        String appId = "app-123";
        AppsResponseVO response = client.app(appId);

        // Verify the response
        assertNotNull(response);
        assertEquals("app-123", response.getId());
        assertEquals("Test App", response.getName());
        assertEquals("completion", response.getMode());

        // Verify interactions with mocks
        verify(requestHeadersUriSpec).uri("/console/api/apps/{appId}", appId);
        verify(requestHeadersSpec).headers(any());
    }

    @Test
    @DisplayName("Test getAppApiKey method")
    public void testGetAppApiKey() {
        // Create mock objects
        RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec<?> requestHeadersSpec = Mockito.mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = Mockito.mock(RestClient.ResponseSpec.class);
        RestClient.Builder mockRestClientBuilder = Mockito.mock(RestClient.Builder.class);
        RestClient mockRestClient = Mockito.mock(RestClient.class);
        WebClient.Builder mockWebClientBuilder = Mockito.mock(WebClient.Builder.class);
        WebClient mockWebClient = Mockito.mock(WebClient.class);
        BaseDifyServerToken mockServerToken = new DifyServerTokenDefault();

        // Setup mock behavior
        when(mockRestClientBuilder.baseUrl(anyString())).thenReturn(mockRestClientBuilder);
        when(mockRestClientBuilder.defaultHeaders(any())).thenReturn(mockRestClientBuilder);
        when(mockRestClientBuilder.build()).thenReturn(mockRestClient);

        when(mockWebClientBuilder.baseUrl(anyString())).thenReturn(mockWebClientBuilder);
        when(mockWebClientBuilder.defaultHeaders(any())).thenReturn(mockWebClientBuilder);
        when(mockWebClientBuilder.build()).thenReturn(mockWebClient);

        doReturn(requestHeadersUriSpec).when(mockRestClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(anyString(), anyString());
        doReturn(requestHeadersSpec).when(requestHeadersSpec).headers(any());
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any())).thenReturn(responseSpec);

        // Mock the API key response
        ApiKeyResultResponseVO mockResult = new ApiKeyResultResponseVO();
        List<ApiKeyResponseVO> apiKeys = new ArrayList<>();
        ApiKeyResponseVO apiKey1 = new ApiKeyResponseVO();
        apiKey1.setId("key-1");
        apiKey1.setToken("sk-123456");
        apiKey1.setType("web");
        apiKeys.add(apiKey1);
        ApiKeyResponseVO apiKey2 = new ApiKeyResponseVO();
        apiKey2.setId("key-2");
        apiKey2.setToken("sk-789012");
        apiKey2.setType("api");
        apiKeys.add(apiKey2);
        mockResult.setData(apiKeys);
        doReturn(mockResult).when(responseSpec).body(ApiKeyResultResponseVO.class);

        // Create server properties
        DifyProperties.Server serverProperties = new DifyProperties.Server();

        // Create the client with mocked dependencies
        String baseUrl = "https://cloud.dify.ai";
        DifyServerDefaultClient client = new DifyServerDefaultClient(serverProperties, mockServerToken, baseUrl, null, mockRestClientBuilder, mockWebClientBuilder);

        // Call the method to test
        String appId = "app-123";
        List<ApiKeyResponseVO> response = client.getAppApiKey(appId);

        // Verify the response
        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("key-1", response.get(0).getId());
        assertEquals("sk-123456", response.get(0).getToken());
        assertEquals("web", response.get(0).getType());
        assertEquals("key-2", response.get(1).getId());
        assertEquals("sk-789012", response.get(1).getToken());
        assertEquals("api", response.get(1).getType());

        // Verify interactions with mocks
        verify(requestHeadersUriSpec).uri("/console/api/apps/{appId}/api-keys", appId);
        verify(requestHeadersSpec).headers(any());
    }
}
