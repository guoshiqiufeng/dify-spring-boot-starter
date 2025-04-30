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

import io.github.guoshiqiufeng.dify.client.spring6.BaseClientTest;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.core.pojo.DifyResult;
import io.github.guoshiqiufeng.dify.server.client.DifyServerTokenDefault;
import io.github.guoshiqiufeng.dify.server.constant.ServerUriConstant;
import io.github.guoshiqiufeng.dify.server.dto.request.DifyLoginRequestVO;
import io.github.guoshiqiufeng.dify.server.dto.response.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link DifyServerDefaultClient}.
 *
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/21 14:55
 */
@SuppressWarnings("unchecked")
@DisplayName("DifyServerDefaultClient Tests")
public class DifyServerDefaultClientTest extends BaseClientTest {

    private static final String BASE_URL = "https://api.dify.ai";

    private DifyServerDefaultClient client;

    private RestClient restClient;
    private RestClient.RequestBodySpec requestBodySpec;
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    private RestClient.ResponseSpec responseSpec;
    private RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;
    private RestClient.RequestHeadersSpec<?> requestHeadersSpec;

    @BeforeEach
    public void setup() {
        super.setup();
        client = new DifyServerDefaultClient(new DifyProperties.Server(), new DifyServerTokenDefault(),
                BASE_URL, new DifyProperties.ClientConfig(), restClientMock.getRestClientBuilder(), webClientMock.getWebClientBuilder());
        restClient = restClientMock.getRestClient();
        requestBodySpec = restClientMock.getRequestBodySpec();
        requestBodyUriSpec = restClientMock.getRequestBodyUriSpec();

        responseSpec = restClientMock.getResponseSpec();
        requestHeadersUriSpec = restClientMock.getRequestHeadersUriSpec();
        requestHeadersSpec = restClientMock.getRequestHeadersSpec();
    }

    @Test
    @DisplayName("Test login method with valid credentials")
    public void testLogin() {
        RestClient.RequestBodySpec requestBodySpec = restClientMock.getRequestBodySpec();
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestBodyUriSpec requestBodyUriSpec = restClientMock.getRequestBodyUriSpec();

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
        client = new DifyServerDefaultClient(serverProperties, new DifyServerTokenDefault(),
                BASE_URL, new DifyProperties.ClientConfig(), restClientMock.getRestClientBuilder(), webClientMock.getWebClientBuilder());
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
        RestClient.RequestBodySpec requestBodySpec = restClientMock.getRequestBodySpec();
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestBodyUriSpec requestBodyUriSpec = restClientMock.getRequestBodyUriSpec();

        // Mock the refresh token response
        LoginResultResponseVO mockRefreshResult = new LoginResultResponseVO();
        mockRefreshResult.setResult(DifyResult.SUCCESS);
        LoginResponseVO mockRefreshData = new LoginResponseVO();
        mockRefreshData.setAccessToken("new-access-token");
        mockRefreshData.setRefreshToken("new-refresh-token");

        mockRefreshResult.setData(mockRefreshData);
        when(responseSpec.body(LoginResultResponseVO.class)).thenReturn(mockRefreshResult);

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
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = restClientMock.getRequestHeadersUriSpec();
        RestClient.RequestHeadersSpec<?> requestHeadersSpec = restClientMock.getRequestHeadersSpec();

        // Mock the app response
        AppsResponseVO mockAppResponse = new AppsResponseVO();
        mockAppResponse.setId("app-123");
        mockAppResponse.setName("Test App");
        mockAppResponse.setMode("completion");
        when(responseSpec.body(AppsResponseVO.class)).thenReturn(mockAppResponse);

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
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = restClientMock.getRequestHeadersUriSpec();
        RestClient.RequestHeadersSpec<?> requestHeadersSpec = restClientMock.getRequestHeadersSpec();

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

    @Test
    public void testInitAppApiKey() {
        // Prepare test data
        String appId = "app-123456";

        // Create expected response
        ApiKeyResponseVO apiKey = new ApiKeyResponseVO();
        apiKey.setId("api-key-123456");
        apiKey.setToken("sk-123456789");
        apiKey.setType("api");

        // Set up the response mock to return our expected response
        when(responseSpec.body(ApiKeyResponseVO.class)).thenReturn(apiKey);

        // Execute the method
        List<ApiKeyResponseVO> actualResponse = client.initAppApiKey(appId);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(1, actualResponse.size());
        assertEquals(apiKey.getId(), actualResponse.get(0).getId());
        assertEquals(apiKey.getToken(), actualResponse.get(0).getToken());
        assertEquals(apiKey.getType(), actualResponse.get(0).getType());

        // Verify WebClient interactions
        verify(restClient).post();
        verify(requestBodyUriSpec).uri(eq(ServerUriConstant.APPS + "/{appId}/api-keys"), eq(appId));
        verify(requestBodySpec).headers(any());
        verify(responseSpec).body(ApiKeyResponseVO.class);
    }

    @Test
    public void testGetDatasetApiKey() {
        // Create expected response
        DatasetApiKeyResultVO resultResponseVO = new DatasetApiKeyResultVO();
        List<DatasetApiKeyResponseVO> apiKeys = new ArrayList<>();

        DatasetApiKeyResponseVO apiKey = new DatasetApiKeyResponseVO();
        apiKey.setId("api-key-123456");
        apiKey.setToken("sk-123456789");
        apiKey.setCreatedAt(1745546400000L);
        apiKeys.add(apiKey);

        resultResponseVO.setData(apiKeys);

        // Set up the response mock to return our expected response
        when(responseSpec.body(DatasetApiKeyResultVO.class)).thenReturn(resultResponseVO);

        // Execute the method
        List<DatasetApiKeyResponseVO> actualResponse = client.getDatasetApiKey();

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(1, actualResponse.size());
        assertEquals(apiKey.getId(), actualResponse.get(0).getId());
        assertEquals(apiKey.getToken(), actualResponse.get(0).getToken());
        assertEquals(apiKey.getCreatedAt(), actualResponse.get(0).getCreatedAt());

        // Verify WebClient interactions
        verify(restClient).get();
        verify(requestHeadersUriSpec).uri(ServerUriConstant.DATASETS + "/api-keys");
        verify(responseSpec).body(DatasetApiKeyResultVO.class);
    }

    @Test
    public void testInitDatasetApiKey() {
        // Create expected response
        DatasetApiKeyResponseVO apiKey = new DatasetApiKeyResponseVO();
        apiKey.setId("api-key-123456");
        apiKey.setToken("sk-123456789");
        apiKey.setCreatedAt(1745546400000L);

        // Set up the response mock to return our expected response
        when(responseSpec.body(DatasetApiKeyResponseVO.class)).thenReturn(apiKey);

        // Execute the method
        List<DatasetApiKeyResponseVO> actualResponse = client.initDatasetApiKey();

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(1, actualResponse.size());
        assertEquals(apiKey.getId(), actualResponse.get(0).getId());
        assertEquals(apiKey.getToken(), actualResponse.get(0).getToken());
        assertEquals(apiKey.getCreatedAt(), actualResponse.get(0).getCreatedAt());

        // Verify WebClient interactions
        verify(restClient).post();
        verify(requestBodyUriSpec).uri(ServerUriConstant.DATASETS + "/api-keys");
        verify(requestBodySpec).headers(any());
        verify(responseSpec).body(DatasetApiKeyResponseVO.class);
    }
}
