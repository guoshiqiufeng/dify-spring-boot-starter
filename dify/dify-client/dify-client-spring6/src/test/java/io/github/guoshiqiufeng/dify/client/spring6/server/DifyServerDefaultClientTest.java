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
import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.core.pojo.DifyResult;
import io.github.guoshiqiufeng.dify.server.client.DifyServerTokenDefault;
import io.github.guoshiqiufeng.dify.server.constant.ServerUriConstant;
import io.github.guoshiqiufeng.dify.server.dto.request.ChatConversationsRequest;
import io.github.guoshiqiufeng.dify.server.dto.request.DifyLoginRequest;
import io.github.guoshiqiufeng.dify.server.dto.response.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
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
        LoginResultResponse mockLoginResult = new LoginResultResponse();
        mockLoginResult.setResult(DifyResult.SUCCESS);
        LoginResponse mockLoginData = new LoginResponse();
        mockLoginData.setAccessToken("test-access-token");
        mockLoginData.setRefreshToken("test-refresh-token");
        mockLoginResult.setData(mockLoginData);

        // Create a ResponseEntity with the mock result
        ResponseEntity<LoginResultResponse> responseEntity = ResponseEntity.ok(mockLoginResult);

        // Mock the toEntity method to return the response entity
        when(responseSpec.toEntity(LoginResultResponse.class)).thenReturn(responseEntity);

        // Create server properties
        DifyProperties.Server serverProperties = new DifyProperties.Server();
        serverProperties.setEmail("test@example.com");
        serverProperties.setPassword("password123");
        client = new DifyServerDefaultClient(serverProperties, new DifyServerTokenDefault(),
                BASE_URL, new DifyProperties.ClientConfig(), restClientMock.getRestClientBuilder(), webClientMock.getWebClientBuilder());
        // Call the method to test
        LoginResponse response = client.login();

        // Verify the response
        assertNotNull(response);
        assertEquals("test-access-token", response.getAccessToken());
        assertEquals("test-refresh-token", response.getRefreshToken());

        // Verify interactions with mocks
        verify(requestBodyUriSpec).uri("/console/api/login");

        // Capture and verify the request body
        ArgumentCaptor<DifyLoginRequest> bodyCaptor = ArgumentCaptor.forClass(DifyLoginRequest.class);
        verify(requestBodySpec).body(bodyCaptor.capture());

        DifyLoginRequest capturedBody = bodyCaptor.getValue();
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
        LoginResultResponse mockRefreshResult = new LoginResultResponse();
        mockRefreshResult.setResult(DifyResult.SUCCESS);
        LoginResponse mockRefreshData = new LoginResponse();
        mockRefreshData.setAccessToken("new-access-token");
        mockRefreshData.setRefreshToken("new-refresh-token");

        mockRefreshResult.setData(mockRefreshData);

        // Create a ResponseEntity with the mock result
        ResponseEntity<LoginResultResponse> responseEntity = ResponseEntity.ok(mockRefreshResult);

        // Mock the toEntity method to return the response entity
        when(responseSpec.toEntity(LoginResultResponse.class)).thenReturn(responseEntity);

        // Call the method to test
        String refreshToken = "old-refresh-token";
        LoginResponse response = client.refreshToken(refreshToken);

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
        AppsResponse mockAppResponse = new AppsResponse();
        mockAppResponse.setId("app-123");
        mockAppResponse.setName("Test App");
        mockAppResponse.setMode("completion");
        when(responseSpec.body(AppsResponse.class)).thenReturn(mockAppResponse);

        // Call the method to test
        String appId = "app-123";
        AppsResponse response = client.app(appId);

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
    public void testApps() {
        // Prepare test data
        String mode = "completion";
        String name = "Test";

        // We need to mock the UriBuilder for the queryParam functionality
        UriBuilder uriBuilderMock = mock(UriBuilder.class);
        URI uriMock = mock(URI.class);

        when(requestHeadersUriSpec.uri(any(Function.class))).thenAnswer(invocation -> {
            Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);

            when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("page"), anyInt())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("limit"), anyInt())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("mode"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("name"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.build()).thenReturn(uriMock);

            uriFunction.apply(uriBuilderMock);
            return requestHeadersSpec;
        });

        // Create expected response for first page
        AppsResponseResult resultResponseVO = new AppsResponseResult();
        List<AppsResponse> appsPage1 = new ArrayList<>();

        AppsResponse app1 = new AppsResponse();
        app1.setId("app-123456");
        app1.setName("Test App 1");
        app1.setMode(mode);
        appsPage1.add(app1);

        AppsResponse app2 = new AppsResponse();
        app2.setId("app-789012");
        app2.setName("Test App 2");
        app2.setMode(mode);
        appsPage1.add(app2);

        resultResponseVO.setData(appsPage1);
        resultResponseVO.setHasMore(false); // Only one page to keep the test simple

        // Set up the response mock to return our expected response
        when(responseSpec.body(AppsResponseResult.class)).thenReturn(resultResponseVO);

        // Execute the method
        List<AppsResponse> actualResponse = client.apps(mode, name);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(2, actualResponse.size());
        assertEquals(app1.getId(), actualResponse.get(0).getId());
        assertEquals(app1.getName(), actualResponse.get(0).getName());
        assertEquals(app2.getId(), actualResponse.get(1).getId());
        assertEquals(app2.getName(), actualResponse.get(1).getName());

        // Verify WebClient interactions
        verify(restClient).get();
        verify(responseSpec).body(AppsResponseResult.class);

        client.apps("", "");
    }

    @Test
    @DisplayName("Test getAppApiKey method")
    public void testGetAppApiKey() {
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = restClientMock.getRequestHeadersUriSpec();
        RestClient.RequestHeadersSpec<?> requestHeadersSpec = restClientMock.getRequestHeadersSpec();

        ApiKeyResultResponse mockResult = new ApiKeyResultResponse();
        List<ApiKeyResponse> apiKeys = new ArrayList<>();
        ApiKeyResponse apiKey1 = new ApiKeyResponse();
        apiKey1.setId("key-1");
        apiKey1.setToken("sk-123456");
        apiKey1.setType("web");
        apiKeys.add(apiKey1);
        ApiKeyResponse apiKey2 = new ApiKeyResponse();
        apiKey2.setId("key-2");
        apiKey2.setToken("sk-789012");
        apiKey2.setType("api");
        apiKeys.add(apiKey2);
        mockResult.setData(apiKeys);
        doReturn(mockResult).when(responseSpec).body(ApiKeyResultResponse.class);

        // Call the method to test
        String appId = "app-123";
        List<ApiKeyResponse> response = client.getAppApiKey(appId);

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
    @DisplayName("Test getAppApiKey method on return null")
    public void testGetAppApiKeyNull() {
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = restClientMock.getRequestHeadersUriSpec();
        RestClient.RequestHeadersSpec<?> requestHeadersSpec = restClientMock.getRequestHeadersSpec();

        doReturn(null).when(responseSpec).body(ApiKeyResultResponse.class);

        // Call the method to test
        String appId = "app-123";
        List<ApiKeyResponse> response = client.getAppApiKey(appId);

        // Verify the response
        assertNotNull(response);

        // Verify interactions with mocks
        verify(requestHeadersUriSpec).uri("/console/api/apps/{appId}/api-keys", appId);
        verify(requestHeadersSpec).headers(any());
    }

    @Test
    public void testInitAppApiKey() {
        // Prepare test data
        String appId = "app-123456";

        // Create expected response
        ApiKeyResponse apiKey = new ApiKeyResponse();
        apiKey.setId("api-key-123456");
        apiKey.setToken("sk-123456789");
        apiKey.setType("api");

        // Set up the response mock to return our expected response
        when(responseSpec.body(ApiKeyResponse.class)).thenReturn(apiKey);

        // Execute the method
        List<ApiKeyResponse> actualResponse = client.initAppApiKey(appId);

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
        verify(responseSpec).body(ApiKeyResponse.class);
    }

    @Test
    public void testInitAppApiKeyNull() {
        // Prepare test data
        String appId = "app-123456";

        // Set up the response mock to return our expected response
        when(responseSpec.body(ApiKeyResponse.class)).thenReturn(null);

        // Execute the method
        List<ApiKeyResponse> actualResponse = client.initAppApiKey(appId);

        // Verify the result
        assertNull(actualResponse);

        // Verify WebClient interactions
        verify(restClient).post();
        verify(requestBodyUriSpec).uri(eq(ServerUriConstant.APPS + "/{appId}/api-keys"), eq(appId));
        verify(requestBodySpec).headers(any());
        verify(responseSpec).body(ApiKeyResponse.class);
    }

    @Test
    public void testGetDatasetApiKey() {
        // Create expected response
        DatasetApiKeyResult resultResponseVO = new DatasetApiKeyResult();
        List<DatasetApiKeyResponse> apiKeys = new ArrayList<>();

        DatasetApiKeyResponse apiKey = new DatasetApiKeyResponse();
        apiKey.setId("api-key-123456");
        apiKey.setToken("sk-123456789");
        apiKey.setCreatedAt(1745546400000L);
        apiKeys.add(apiKey);

        resultResponseVO.setData(apiKeys);

        // Set up the response mock to return our expected response
        when(responseSpec.body(DatasetApiKeyResult.class)).thenReturn(resultResponseVO);

        // Execute the method
        List<DatasetApiKeyResponse> actualResponse = client.getDatasetApiKey();

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(1, actualResponse.size());
        assertEquals(apiKey.getId(), actualResponse.get(0).getId());
        assertEquals(apiKey.getToken(), actualResponse.get(0).getToken());
        assertEquals(apiKey.getCreatedAt(), actualResponse.get(0).getCreatedAt());

        // Verify WebClient interactions
        verify(restClient).get();
        verify(requestHeadersUriSpec).uri(ServerUriConstant.DATASETS + "/api-keys");
        verify(responseSpec).body(DatasetApiKeyResult.class);
    }

    @Test
    public void testGetDatasetApiKeyNull() {
        // Set up the response mock to return our expected response
        when(responseSpec.body(DatasetApiKeyResult.class)).thenReturn(null);

        // Execute the method
        List<DatasetApiKeyResponse> actualResponse = client.getDatasetApiKey();

        // Verify the result
        assertNull(actualResponse);

        // Verify WebClient interactions
        verify(restClient).get();
        verify(requestHeadersUriSpec).uri(ServerUriConstant.DATASETS + "/api-keys");
        verify(responseSpec).body(DatasetApiKeyResult.class);
    }

    @Test
    public void testInitDatasetApiKey() {
        // Create expected response
        DatasetApiKeyResponse apiKey = new DatasetApiKeyResponse();
        apiKey.setId("api-key-123456");
        apiKey.setToken("sk-123456789");
        apiKey.setCreatedAt(1745546400000L);

        // Set up the response mock to return our expected response
        when(responseSpec.body(DatasetApiKeyResponse.class)).thenReturn(apiKey);

        // Execute the method
        List<DatasetApiKeyResponse> actualResponse = client.initDatasetApiKey();

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
        verify(responseSpec).body(DatasetApiKeyResponse.class);
    }

    @Test
    public void testInitDatasetApiKeyNull() {
        // Set up the response mock to return our expected response
        when(responseSpec.body(DatasetApiKeyResponse.class)).thenReturn(null);

        // Execute the method
        List<DatasetApiKeyResponse> actualResponse = client.initDatasetApiKey();

        // Verify the result
        assertNull(actualResponse);

        // Verify WebClient interactions
        verify(restClient).post();
        verify(requestBodyUriSpec).uri(ServerUriConstant.DATASETS + "/api-keys");
        verify(requestBodySpec).headers(any());
        verify(responseSpec).body(DatasetApiKeyResponse.class);
    }

    @Test
    @DisplayName("Test chatConversations method")
    public void testChatConversations() {
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = restClientMock.getRequestHeadersUriSpec();
        RestClient.RequestHeadersSpec<?> requestHeadersSpec = restClientMock.getRequestHeadersSpec();

        // Create test request
        ChatConversationsRequest request = new ChatConversationsRequest();
        request.setAppId("app-123");
        request.setPage(1);
        request.setLimit(10);
        request.setAnnotationStatus("all");
        request.setStart("2025-10-23 00:00");
        request.setEnd("2025-10-30 23:59");
        request.setSortBy("-created_at");

        // Create mock conversation data
        ChatConversationResponse conversation = new ChatConversationResponse();
        conversation.setId("b9f66e5f-ba2b-4179-88d0-2d36c7e2050a");
        conversation.setName("What are the specs of the iPhone 13 Pro Max?");
        conversation.setAnnotated(false);

        // Create mock response
        DifyPageResult<ChatConversationResponse> mockResponse = new DifyPageResult<>();
        mockResponse.setData(List.of(conversation));
        mockResponse.setPage(1);
        mockResponse.setLimit(10);
        mockResponse.setTotal(1);
        mockResponse.setHasMore(false);

        // Mock the response
        when(responseSpec.body(any(org.springframework.core.ParameterizedTypeReference.class))).thenReturn(mockResponse);

        // Call the method to test
        DifyPageResult<ChatConversationResponse> response = client.chatConversations(request);

        // Verify the response
        assertNotNull(response);
        assertEquals(1, response.getTotal());
        assertEquals(1, response.getData().size());
        assertEquals("b9f66e5f-ba2b-4179-88d0-2d36c7e2050a", response.getData().get(0).getId());
        assertEquals("What are the specs of the iPhone 13 Pro Max?", response.getData().get(0).getName());

        // Verify interactions with mocks
        verify(requestHeadersUriSpec).uri(any(Function.class));
        verify(requestHeadersSpec).headers(any());
    }

    @Test
    @DisplayName("Test dailyConversations method")
    public void testDailyConversations() {
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = restClientMock.getRequestHeadersUriSpec();
        RestClient.RequestHeadersSpec<?> requestHeadersSpec = restClientMock.getRequestHeadersSpec();

        // Prepare test data
        String appId = "08534c1a-4316-4cd3-806d-bbbca03f58aa";
        java.time.LocalDateTime start = java.time.LocalDateTime.of(2025, 10, 23, 0, 0);
        java.time.LocalDateTime end = java.time.LocalDateTime.of(2025, 10, 30, 23, 59);

        // Create mock daily conversation data
        DailyConversationsResponse dailyStat = new DailyConversationsResponse();
        dailyStat.setDate("2025-09-02");
        dailyStat.setConversationCount(1);

        // Create mock response
        List<DailyConversationsResponse> mockResponse = List.of(dailyStat);
        DailyConversationsResultResponse response = new DailyConversationsResultResponse();
        response.setData(mockResponse);
        // Mock the response
        when(responseSpec.body(any(org.springframework.core.ParameterizedTypeReference.class))).thenReturn(response);

        // Call the method to test
        List<DailyConversationsResponse> actualResponse = client.dailyConversations(appId, start, end);

        // Verify the response
        assertNotNull(actualResponse);
        assertEquals(1, actualResponse.size());
        assertEquals("2025-09-02", actualResponse.get(0).getDate());
        assertEquals(1, actualResponse.get(0).getConversationCount().intValue());

        // Verify interactions with mocks
        verify(requestHeadersUriSpec).uri(any(Function.class));
        verify(requestHeadersSpec).headers(any());
    }

    @Test
    @DisplayName("Test dailyEndUsers method")
    public void testDailyEndUsers() {
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = restClientMock.getRequestHeadersUriSpec();
        RestClient.RequestHeadersSpec<?> requestHeadersSpec = restClientMock.getRequestHeadersSpec();

        // Prepare test data
        String appId = "08534c1a-4316-4cd3-806d-bbbca03f58aa";
        java.time.LocalDateTime start = java.time.LocalDateTime.of(2025, 10, 23, 0, 0);
        java.time.LocalDateTime end = java.time.LocalDateTime.of(2025, 10, 30, 23, 59);

        // Create mock daily end users data
        DailyEndUsersResponse dailyStat = new DailyEndUsersResponse();
        dailyStat.setDate("2025-09-02");
        dailyStat.setTerminalCount(1);

        // Create mock response
        List<DailyEndUsersResponse> mockResponse = List.of(dailyStat);
        DailyEndUsersResultResponse response = new DailyEndUsersResultResponse();
        response.setData(mockResponse);

        // Mock the response
        when(responseSpec.body(any(org.springframework.core.ParameterizedTypeReference.class))).thenReturn(response);

        // Call the method to test
        List<DailyEndUsersResponse> actualResponse = client.dailyEndUsers(appId, start, end);

        // Verify the response
        assertNotNull(actualResponse);
        assertEquals(1, actualResponse.size());
        assertEquals("2025-09-02", actualResponse.get(0).getDate());
        assertEquals(1, actualResponse.get(0).getTerminalCount().intValue());

        // Verify interactions with mocks
        verify(requestHeadersUriSpec).uri(any(Function.class));
        verify(requestHeadersSpec).headers(any());
    }
}
