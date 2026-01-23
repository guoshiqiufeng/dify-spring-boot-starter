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
package io.github.guoshiqiufeng.dify.support.impl.server;

import io.github.guoshiqiufeng.dify.client.core.http.HttpClientFactory;
import io.github.guoshiqiufeng.dify.client.core.http.HttpHeaders;
import io.github.guoshiqiufeng.dify.client.core.response.ResponseEntity;
import io.github.guoshiqiufeng.dify.client.core.util.LinkedMultiValueMap;
import io.github.guoshiqiufeng.dify.client.core.util.MultiValueMap;
import io.github.guoshiqiufeng.dify.client.core.web.util.UriBuilder;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.core.pojo.DifyResult;
import io.github.guoshiqiufeng.dify.server.client.BaseDifyServerToken;
import io.github.guoshiqiufeng.dify.server.client.DifyServerTokenDefault;
import io.github.guoshiqiufeng.dify.server.constant.ServerUriConstant;
import io.github.guoshiqiufeng.dify.server.dto.request.AppsRequest;
import io.github.guoshiqiufeng.dify.server.dto.request.ChatConversationsRequest;
import io.github.guoshiqiufeng.dify.server.dto.request.DifyLoginRequest;
import io.github.guoshiqiufeng.dify.server.dto.response.*;
import io.github.guoshiqiufeng.dify.support.impl.BaseClientTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link DifyServerDefaultClient}.
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/25 09:40
 */
@SuppressWarnings("unchecked")
public class DifyServerDefaultClientTest extends BaseClientTest {

    private DifyServerDefaultClient client;
    private BaseDifyServerToken difyServerTokenMock;

    @BeforeEach
    public void setup() {
        super.setup();
        difyServerTokenMock = mock(DifyServerTokenDefault.class);
        // Create real client with mocked HttpClient
        DifyProperties.Server serverConfig = new DifyProperties.Server();
        serverConfig.setEmail("test@example.com");
        serverConfig.setPassword("password123");
        client = new DifyServerDefaultClient(httpClientMock, serverConfig, new DifyServerTokenDefault());
    }

    @Test
    public void testConstructor() {
        HttpClientFactory httpClientFactory = mock(HttpClientFactory.class);
        DifyProperties.Server serverConfig = new DifyProperties.Server();
        serverConfig.setEmail("test@example.com");
        serverConfig.setPassword("password123");
        new DifyServerDefaultClient(httpClientMock, null, null);
        new DifyServerDefaultClient(serverConfig, "http://127.0.0.1", new DifyProperties.ClientConfig(), httpClientFactory);
    }

    @Test
    @DisplayName("Test login method with valid credentials")
    public void testLogin() {
        // Create server properties
        DifyProperties.Server serverProperties = new DifyProperties.Server();
        serverProperties.setEmail("test@example.com");
        serverProperties.setPassword("password123");
        client = new DifyServerDefaultClient(httpClientMock, serverProperties, difyServerTokenMock);

        // Mock the login response
        LoginResultResponse mockLoginResult = new LoginResultResponse();
        mockLoginResult.setResult(DifyResult.SUCCESS);
        LoginResponse mockLoginData = new LoginResponse();
        mockLoginData.setAccessToken("test-access-token");
        mockLoginData.setRefreshToken("test-refresh-token");
        mockLoginResult.setData(mockLoginData);

        // Create a HttpResponse with the mock result
        ResponseEntity<LoginResultResponse> responseEntity = ResponseEntity.<LoginResultResponse>builder()
                .statusCode(200)
                .body(mockLoginResult)
                .build();

        // Mock the toEntity method to return the response entity
        when(responseSpecMock.toEntity(LoginResultResponse.class)).thenReturn(responseEntity);

        // Call the method to test
        LoginResponse response = client.login();

        // Verify the response
        assertNotNull(response);
        assertEquals("test-access-token", response.getAccessToken());
        assertEquals("test-refresh-token", response.getRefreshToken());

        // Verify interactions with mocks
        verify(requestBodyUriSpecMock).uri("/console/api/login");

        // Capture and verify the request body
        ArgumentCaptor<DifyLoginRequest> bodyCaptor = ArgumentCaptor.forClass(DifyLoginRequest.class);
        verify(requestBodySpecMock).body(bodyCaptor.capture());

        DifyLoginRequest capturedBody = bodyCaptor.getValue();
        assertEquals("test@example.com", capturedBody.getEmail());
        assertEquals("cGFzc3dvcmQxMjM=", capturedBody.getPassword());
    }

    @Test
    @DisplayName("Test refreshToken method")
    public void testRefreshToken() {
        // Mock the refresh token response
        LoginResultResponse mockRefreshResult = new LoginResultResponse();
        mockRefreshResult.setResult(DifyResult.SUCCESS);
        LoginResponse mockRefreshData = new LoginResponse();
        mockRefreshData.setAccessToken("new-access-token");
        mockRefreshData.setRefreshToken("new-refresh-token");

        mockRefreshResult.setData(mockRefreshData);

        // Create a HttpResponse with the mock result
        ResponseEntity<LoginResultResponse> responseEntity = ResponseEntity.<LoginResultResponse>builder()
                .statusCode(200)
                .body(mockRefreshResult)
                .build();

        // Mock the toEntity method to return the response entity
        when(responseSpecMock.toEntity(LoginResultResponse.class)).thenReturn(responseEntity);

        // Call the method to test
        String refreshToken = "old-refresh-token";
        LoginResponse response = client.refreshToken(refreshToken);

        // Verify the response
        assertNotNull(response);
        assertEquals("new-access-token", response.getAccessToken());
        assertEquals("new-refresh-token", response.getRefreshToken());

        // Verify interactions with mocks
        verify(requestBodyUriSpecMock).uri("/console/api/refresh-token");

        // Capture and verify the request body
        ArgumentCaptor<Map<String, String>> bodyCaptor = ArgumentCaptor.forClass(Map.class);
        verify(requestBodySpecMock).body(bodyCaptor.capture());

        Map<String, String> capturedBody = bodyCaptor.getValue();
        assertEquals("old-refresh-token", capturedBody.get("refresh_token"));
    }

    @Test
    public void testApp() {
        // Prepare test data
        String appId = "app-123456";

        // Create expected response
        AppsResponse expectedResponse = new AppsResponse();
        expectedResponse.setId(appId);
        expectedResponse.setName("Test App");
        expectedResponse.setIcon("app-icon-url");
        expectedResponse.setMode("completion");

        // Set up the response mock to return our expected response
        when(responseSpecMock.body(AppsResponse.class)).thenReturn(expectedResponse);

        // Execute the method
        AppsResponse actualResponse = client.app(appId);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getName(), actualResponse.getName());
        assertEquals(expectedResponse.getIcon(), actualResponse.getIcon());
        assertEquals(expectedResponse.getMode(), actualResponse.getMode());

        // Verify WebClient interactions
        verify(httpClientMock).get();
        verify(requestHeadersUriSpecMock).uri(eq(ServerUriConstant.APPS + "/{appId}"), eq(appId));
        verify(responseSpecMock).body(AppsResponse.class);
    }

    @Test
    public void testApps() {
        // Prepare test data
        String mode = "completion";
        String name = "Test";

        // We need to mock the UriBuilder for the queryParam functionality
        UriBuilder uriBuilderMock = mock(UriBuilder.class);
        URI uriMock = mock(URI.class);

        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenAnswer(invocation -> {
            Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);

            when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("page"), anyInt())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("limit"), anyInt())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("mode"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("name"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.build()).thenReturn(uriMock);

            uriFunction.apply(uriBuilderMock);
            return requestHeadersSpecMock;
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
        when(responseSpecMock.body(AppsResponseResult.class)).thenReturn(resultResponseVO);

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
        verify(httpClientMock).get();
        verify(responseSpecMock).body(AppsResponseResult.class);

        client.apps("", "");
    }

    @Test
    public void testAppsWithEmptyValues() {
        // Prepare test data with empty values to test filtering
        String mode = "";
        String name = "";

        // We need to mock the UriBuilder for the queryParam functionality
        UriBuilder uriBuilderMock = mock(UriBuilder.class);
        URI uriMock = mock(URI.class);

        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenAnswer(invocation -> {
            Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);

            when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("page"), anyInt())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("limit"), anyInt())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("mode"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("name"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.build()).thenReturn(uriMock);

            uriFunction.apply(uriBuilderMock);
            return requestHeadersSpecMock;
        });

        // Create expected response
        AppsResponseResult resultResponseVO = new AppsResponseResult();
        List<AppsResponse> appsPage1 = new ArrayList<>();
        resultResponseVO.setData(appsPage1);
        resultResponseVO.setHasMore(false);

        // Set up the response mock to return our expected response
        when(responseSpecMock.body(AppsResponseResult.class)).thenReturn(resultResponseVO);

        // Execute the method
        List<AppsResponse> actualResponse = client.apps(mode, name);

        // Verify the result
        assertNotNull(actualResponse);

        // Verify WebClient interactions
        verify(httpClientMock).get();
        verify(responseSpecMock).body(AppsResponseResult.class);
    }

    @Test
    public void testAppsWithEmptyData() {
        // Prepare test data with empty values to test filtering
        String mode = "";
        String name = "";

        // We need to mock the UriBuilder for the queryParam functionality
        UriBuilder uriBuilderMock = mock(UriBuilder.class);
        URI uriMock = mock(URI.class);

        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenAnswer(invocation -> {
            Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);

            when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("page"), anyInt())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("limit"), anyInt())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("mode"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("name"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.build()).thenReturn(uriMock);

            uriFunction.apply(uriBuilderMock);
            return requestHeadersSpecMock;
        });

        // Set up the response mock to return our expected response
        when(responseSpecMock.body(AppsResponseResult.class)).thenReturn(null);

        // Execute the method
        List<AppsResponse> actualResponse = client.apps(mode, name);

        // Verify WebClient interactions
        verify(httpClientMock).get();
    }

    @Test
    public void testGetAppApiKey() {
        // Prepare test data
        String appId = "app-123456";

        // Create expected response
        ApiKeyResultResponse resultResponseVO = new ApiKeyResultResponse();
        List<ApiKeyResponse> apiKeys = new ArrayList<>();

        ApiKeyResponse apiKey = new ApiKeyResponse();
        apiKey.setId("api-key-123456");
        apiKey.setToken("sk-123456789");
        apiKey.setType("api");
        apiKeys.add(apiKey);

        resultResponseVO.setData(apiKeys);

        // Set up the response mock to return our expected response
        when(responseSpecMock.body(ApiKeyResultResponse.class)).thenReturn(resultResponseVO);

        // Execute the method
        List<ApiKeyResponse> actualResponse = client.getAppApiKey(appId);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(1, actualResponse.size());
        assertEquals(apiKey.getId(), actualResponse.get(0).getId());
        assertEquals(apiKey.getToken(), actualResponse.get(0).getToken());
        assertEquals(apiKey.getType(), actualResponse.get(0).getType());

        // Verify WebClient interactions
        verify(httpClientMock).get();
        verify(requestHeadersUriSpecMock).uri(eq(ServerUriConstant.APPS + "/{appId}/api-keys"), eq(appId));
        verify(responseSpecMock).body(ApiKeyResultResponse.class);
    }


    @Test
    @DisplayName("Test getAppApiKey method on return null")
    public void testGetAppApiKeyNull() {
        // Prepare test data
        String appId = "app-123456";

        // Set up the response mock to return our expected response
        when(responseSpecMock.body(ApiKeyResultResponse.class)).thenReturn(null);

        // Execute the method
        List<ApiKeyResponse> actualResponse = client.getAppApiKey(appId);

        // Verify the result
        assertNotNull(actualResponse);

        // Verify WebClient interactions
        verify(httpClientMock).get();
        verify(requestHeadersUriSpecMock).uri(eq(ServerUriConstant.APPS + "/{appId}/api-keys"), eq(appId));
        verify(responseSpecMock).body(ApiKeyResultResponse.class);
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
        when(responseSpecMock.body(ApiKeyResponse.class)).thenReturn(apiKey);

        // Execute the method
        List<ApiKeyResponse> actualResponse = client.initAppApiKey(appId);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(1, actualResponse.size());
        assertEquals(apiKey.getId(), actualResponse.get(0).getId());
        assertEquals(apiKey.getToken(), actualResponse.get(0).getToken());
        assertEquals(apiKey.getType(), actualResponse.get(0).getType());

        // Verify WebClient interactions
        verify(httpClientMock).post();
        verify(requestBodyUriSpecMock).uri(eq(ServerUriConstant.APPS + "/{appId}/api-keys"), eq(appId));
        verify(requestBodySpecMock).headers(any());
        verify(responseSpecMock).body(ApiKeyResponse.class);
    }

    @Test
    public void testInitAppApiKeyNull() {
        // Prepare test data
        String appId = "app-123456";

        // Set up the response mock to return our expected response
        when(responseSpecMock.body(ApiKeyResponse.class)).thenReturn(null);

        // Execute the method
        List<ApiKeyResponse> actualResponse = client.initAppApiKey(appId);

        // Verify the result
        assertNull(actualResponse);

        // Verify WebClient interactions
        verify(httpClientMock).post();
        verify(requestBodyUriSpecMock).uri(eq(ServerUriConstant.APPS + "/{appId}/api-keys"), eq(appId));
        verify(requestBodySpecMock).headers(any());
        verify(responseSpecMock).body(ApiKeyResponse.class);
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
        when(responseSpecMock.body(DatasetApiKeyResult.class)).thenReturn(resultResponseVO);

        // Execute the method
        List<DatasetApiKeyResponse> actualResponse = client.getDatasetApiKey();

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(1, actualResponse.size());
        assertEquals(apiKey.getId(), actualResponse.get(0).getId());
        assertEquals(apiKey.getToken(), actualResponse.get(0).getToken());
        assertEquals(apiKey.getCreatedAt(), actualResponse.get(0).getCreatedAt());

        // Verify WebClient interactions
        verify(httpClientMock).get();
        verify(requestHeadersUriSpecMock).uri(ServerUriConstant.DATASETS + "/api-keys");
        verify(responseSpecMock).body(DatasetApiKeyResult.class);
    }

    @Test
    public void testGetDatasetApiKeyNull() {

        // set up the response mock to return our expected response
        when(responseSpecMock.body(DatasetApiKeyResult.class)).thenReturn(null);

        // Execute the method
        List<DatasetApiKeyResponse> actualResponse = client.getDatasetApiKey();

        // Verify the result
        assertNull(actualResponse);

        // Verify WebClient interactions
        verify(httpClientMock).get();
        verify(requestHeadersUriSpecMock).uri(ServerUriConstant.DATASETS + "/api-keys");
        verify(responseSpecMock).body(DatasetApiKeyResult.class);
    }

    @Test
    public void testInitDatasetApiKey() {
        // Create expected response
        DatasetApiKeyResponse apiKey = new DatasetApiKeyResponse();
        apiKey.setId("api-key-123456");
        apiKey.setToken("sk-123456789");
        apiKey.setCreatedAt(1745546400000L);

        // Set up the response mock to return our expected response
        when(responseSpecMock.body(DatasetApiKeyResponse.class)).thenReturn(apiKey);

        // Execute the method
        List<DatasetApiKeyResponse> actualResponse = client.initDatasetApiKey();

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(1, actualResponse.size());
        assertEquals(apiKey.getId(), actualResponse.get(0).getId());
        assertEquals(apiKey.getToken(), actualResponse.get(0).getToken());
        assertEquals(apiKey.getCreatedAt(), actualResponse.get(0).getCreatedAt());

        // Verify WebClient interactions
        verify(httpClientMock).post();
        verify(requestBodyUriSpecMock).uri(ServerUriConstant.DATASETS + "/api-keys");
        verify(requestBodySpecMock).headers(any());
        verify(responseSpecMock).body(DatasetApiKeyResponse.class);
    }

    @Test
    public void testInitDatasetApiKeyNull() {

        // Set up the response mock to return our expected response
        when(responseSpecMock.body(DatasetApiKeyResponse.class)).thenReturn(null);

        // Execute the method
        List<DatasetApiKeyResponse> actualResponse = client.initDatasetApiKey();

        // Verify the result
        assertNull(actualResponse);

        // Verify WebClient interactions
        verify(httpClientMock).post();
        verify(requestBodyUriSpecMock).uri(ServerUriConstant.DATASETS + "/api-keys");
        verify(requestBodySpecMock).headers(any());
        verify(responseSpecMock).body(DatasetApiKeyResponse.class);
    }

    @Test
    @DisplayName("Test chatConversations method with all parameters")
    public void testChatConversations() {
        // Prepare test data
        ChatConversationsRequest request = new ChatConversationsRequest();
        request.setAppId("app-123");
        request.setPage(1);
        request.setLimit(10);
        request.setAnnotationStatus("all");
        request.setStart("2025-10-23 00:00");
        request.setEnd("2025-10-30 23:59");
        request.setSortBy("-created_at");

        // Mock the UriBuilder for queryParam functionality
        UriBuilder uriBuilderMock = mock(UriBuilder.class);
        URI uriMock = mock(URI.class);

        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenAnswer(invocation -> {
            Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);

            when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("page"), anyInt())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("limit"), anyInt())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("start"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("end"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("sort_by"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("annotation_status"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.build()).thenReturn(uriMock);

            uriFunction.apply(uriBuilderMock);
            return requestHeadersSpecMock;
        });

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

        // Set up the response mock to return our expected response
        when(responseSpecMock.body(any(io.github.guoshiqiufeng.dify.client.core.http.TypeReference.class))).thenReturn(mockResponse);

        // Execute the method
        DifyPageResult<ChatConversationResponse> actualResponse = client.chatConversations(request);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(1, actualResponse.getTotal());
        assertEquals(1, actualResponse.getData().size());
        assertEquals("b9f66e5f-ba2b-4179-88d0-2d36c7e2050a", actualResponse.getData().get(0).getId());
        assertEquals("What are the specs of the iPhone 13 Pro Max?", actualResponse.getData().get(0).getName());

        // Verify WebClient interactions
        verify(httpClientMock).get();
        verify(requestHeadersUriSpecMock).uri(any(Function.class));
        verify(responseSpecMock).body(any(io.github.guoshiqiufeng.dify.client.core.http.TypeReference.class));
    }

    @Test
    @DisplayName("Test chatConversations method with only required parameters")
    public void testChatConversationsOnlyRequiredParams() {
        // Prepare test data with only required parameters
        ChatConversationsRequest request = new ChatConversationsRequest();
        request.setAppId("app-123");
        request.setPage(1);
        request.setLimit(10);
        // All optional parameters are null

        // Mock the UriBuilder for queryParam functionality
        UriBuilder uriBuilderMock = mock(UriBuilder.class);
        URI uriMock = mock(URI.class);

        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenAnswer(invocation -> {
            Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);

            when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("page"), anyInt())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("limit"), anyInt())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("start"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("end"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("sort_by"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("annotation_status"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.build()).thenReturn(uriMock);

            uriFunction.apply(uriBuilderMock);
            return requestHeadersSpecMock;
        });

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

        // Set up the response mock to return our expected response
        when(responseSpecMock.body(any(io.github.guoshiqiufeng.dify.client.core.http.TypeReference.class))).thenReturn(mockResponse);

        // Execute the method
        DifyPageResult<ChatConversationResponse> actualResponse = client.chatConversations(request);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(1, actualResponse.getTotal());
        assertEquals(1, actualResponse.getData().size());
        assertEquals("b9f66e5f-ba2b-4179-88d0-2d36c7e2050a", actualResponse.getData().get(0).getId());
        assertEquals("What are the specs of the iPhone 13 Pro Max?", actualResponse.getData().get(0).getName());

        // Verify WebClient interactions
        verify(httpClientMock).get();
        verify(requestHeadersUriSpecMock).uri(any(Function.class));
        verify(responseSpecMock).body(any(io.github.guoshiqiufeng.dify.client.core.http.TypeReference.class));
    }

    @Test
    @DisplayName("Test chatConversations method with some optional parameters")
    public void testChatConversationsSomeOptionalParams() {
        // Prepare test data with some optional parameters
        ChatConversationsRequest request = new ChatConversationsRequest();
        request.setAppId("app-123");
        request.setPage(1);
        request.setLimit(10);
        request.setStart("2025-10-23 00:00");
        // end, sortBy, and annotationStatus are null

        // Mock the UriBuilder for queryParam functionality
        UriBuilder uriBuilderMock = mock(UriBuilder.class);
        URI uriMock = mock(URI.class);

        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenAnswer(invocation -> {
            Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);

            when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("page"), anyInt())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("limit"), anyInt())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("start"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("end"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("sort_by"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("annotation_status"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.build()).thenReturn(uriMock);

            uriFunction.apply(uriBuilderMock);
            return requestHeadersSpecMock;
        });

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

        // Set up the response mock to return our expected response
        when(responseSpecMock.body(any(io.github.guoshiqiufeng.dify.client.core.http.TypeReference.class))).thenReturn(mockResponse);

        // Execute the method
        DifyPageResult<ChatConversationResponse> actualResponse = client.chatConversations(request);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(1, actualResponse.getTotal());
        assertEquals(1, actualResponse.getData().size());
        assertEquals("b9f66e5f-ba2b-4179-88d0-2d36c7e2050a", actualResponse.getData().get(0).getId());
        assertEquals("What are the specs of the iPhone 13 Pro Max?", actualResponse.getData().get(0).getName());

        // Verify WebClient interactions
        verify(httpClientMock).get();
        verify(requestHeadersUriSpecMock).uri(any(Function.class));
        verify(responseSpecMock).body(any(io.github.guoshiqiufeng.dify.client.core.http.TypeReference.class));
    }

    @Test
    @DisplayName("Test dailyConversations method")
    public void testDailyConversations() {
        // Prepare test data
        String appId = "08534c1a-4316-4cd3-806d-bbbca03f58aa";
        java.time.LocalDateTime start = java.time.LocalDateTime.of(2025, 10, 23, 0, 0);
        java.time.LocalDateTime end = java.time.LocalDateTime.of(2025, 10, 30, 23, 59);

        // Mock the UriBuilder for queryParam functionality
        UriBuilder uriBuilderMock = mock(UriBuilder.class);
        URI uriMock = mock(URI.class);

        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenAnswer(invocation -> {
            Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);

            when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("start"), eq("2025-10-23 00:00"))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("end"), eq("2025-10-30 23:59"))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.build(eq(appId))).thenReturn(uriMock);

            uriFunction.apply(uriBuilderMock);
            return requestHeadersSpecMock;
        });

        // Create mock daily conversation data
        DailyConversationsResponse dailyStat = new DailyConversationsResponse();
        dailyStat.setDate("2025-09-02");
        dailyStat.setConversationCount(1);

        // Create mock response
        List<DailyConversationsResponse> mockResponse = List.of(dailyStat);
        DailyConversationsResultResponse response = new DailyConversationsResultResponse();
        response.setData(mockResponse);
        // Set up the response mock to return our expected response
        when(responseSpecMock.body(any(io.github.guoshiqiufeng.dify.client.core.http.TypeReference.class))).thenReturn(response);

        // Execute the method
        List<DailyConversationsResponse> actualResponse = client.dailyConversations(appId, start, end);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(1, actualResponse.size());
        assertEquals("2025-09-02", actualResponse.get(0).getDate());
        assertEquals(1, actualResponse.get(0).getConversationCount());

        // Verify WebClient interactions
        verify(httpClientMock).get();
        verify(requestHeadersUriSpecMock).uri(any(Function.class));
        verify(responseSpecMock).body(any(io.github.guoshiqiufeng.dify.client.core.http.TypeReference.class));

        when(responseSpecMock.body(any(io.github.guoshiqiufeng.dify.client.core.http.TypeReference.class))).thenReturn(null);
        client.dailyConversations(appId, start, end);

    }

    @Test
    @DisplayName("Test dailyEndUsers method")
    public void testDailyEndUsers() {
        // Prepare test data
        String appId = "08534c1a-4316-4cd3-806d-bbbca03f58aa";
        java.time.LocalDateTime start = java.time.LocalDateTime.of(2025, 10, 23, 0, 0);
        java.time.LocalDateTime end = java.time.LocalDateTime.of(2025, 10, 30, 23, 59);

        // Mock the UriBuilder for queryParam functionality
        UriBuilder uriBuilderMock = mock(UriBuilder.class);
        URI uriMock = mock(URI.class);

        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenAnswer(invocation -> {
            Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);

            when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("start"), eq("2025-10-23 00:00"))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("end"), eq("2025-10-30 23:59"))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.build(eq(appId))).thenReturn(uriMock);

            uriFunction.apply(uriBuilderMock);
            return requestHeadersSpecMock;
        });

        // Create mock daily end users data
        DailyEndUsersResponse dailyStat = new DailyEndUsersResponse();
        dailyStat.setDate("2025-09-02");
        dailyStat.setTerminalCount(1);

        // Create mock response
        List<DailyEndUsersResponse> mockResponse = List.of(dailyStat);
        DailyEndUsersResultResponse response = new DailyEndUsersResultResponse();
        response.setData(mockResponse);

        // Set up the response mock to return our expected response
        when(responseSpecMock.body(any(io.github.guoshiqiufeng.dify.client.core.http.TypeReference.class))).thenReturn(response);

        // Execute the method
        List<DailyEndUsersResponse> actualResponse = client.dailyEndUsers(appId, start, end);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(1, actualResponse.size());
        assertEquals("2025-09-02", actualResponse.get(0).getDate());
        assertEquals(1, actualResponse.get(0).getTerminalCount());

        // Verify WebClient interactions
        verify(httpClientMock).get();
        verify(requestHeadersUriSpecMock).uri(any(Function.class));
        verify(responseSpecMock).body(any(io.github.guoshiqiufeng.dify.client.core.http.TypeReference.class));

        // Set up the response mock to return our expected response
        when(responseSpecMock.body(any(io.github.guoshiqiufeng.dify.client.core.http.TypeReference.class))).thenReturn(null);
        client.dailyEndUsers(appId, start, end);
    }

    @Test
    @DisplayName("Test averageSessionInteractions method")
    public void testAverageSessionInteractions() {
        // Prepare test data
        String appId = "08534c1a-4316-4cd3-806d-bbbca03f58aa";
        java.time.LocalDateTime start = java.time.LocalDateTime.of(2025, 10, 23, 0, 0);
        java.time.LocalDateTime end = java.time.LocalDateTime.of(2025, 10, 30, 23, 59);

        // Mock the UriBuilder for queryParam functionality
        UriBuilder uriBuilderMock = mock(UriBuilder.class);
        URI uriMock = mock(URI.class);

        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenAnswer(invocation -> {
            Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);

            when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("start"), eq("2025-10-23 00:00"))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("end"), eq("2025-10-30 23:59"))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.build(eq(appId))).thenReturn(uriMock);

            uriFunction.apply(uriBuilderMock);
            return requestHeadersSpecMock;
        });

        // Create mock average session interactions data
        AverageSessionInteractionsResponse dailyStat = new AverageSessionInteractionsResponse();
        dailyStat.setDate("2025-09-02");
        dailyStat.setInteractions(1.0);

        // Create mock response
        List<AverageSessionInteractionsResponse> mockResponse = List.of(dailyStat);
        AverageSessionInteractionsResultResponse response = new AverageSessionInteractionsResultResponse();
        response.setData(mockResponse);

        // Set up the response mock to return our expected response
        when(responseSpecMock.body(any(io.github.guoshiqiufeng.dify.client.core.http.TypeReference.class))).thenReturn(response);

        // Execute the method
        List<AverageSessionInteractionsResponse> actualResponse = client.averageSessionInteractions(appId, start, end);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(1, actualResponse.size());
        assertEquals("2025-09-02", actualResponse.get(0).getDate());
        assertEquals(1.0, actualResponse.get(0).getInteractions());

        // Verify WebClient interactions
        verify(httpClientMock).get();
        verify(requestHeadersUriSpecMock).uri(any(Function.class));
        verify(responseSpecMock).body(any(io.github.guoshiqiufeng.dify.client.core.http.TypeReference.class));

        when(responseSpecMock.body(any(io.github.guoshiqiufeng.dify.client.core.http.TypeReference.class))).thenReturn(null);

        client.averageSessionInteractions(appId, start, end);
    }

    @Test
    @DisplayName("Test tokensPerSecond method")
    public void testTokensPerSecond() {
        // Prepare test data
        String appId = "08534c1a-4316-4cd3-806d-bbbca03f58aa";
        java.time.LocalDateTime start = java.time.LocalDateTime.of(2025, 10, 23, 0, 0);
        java.time.LocalDateTime end = java.time.LocalDateTime.of(2025, 10, 30, 23, 59);

        // Mock the UriBuilder for queryParam functionality
        UriBuilder uriBuilderMock = mock(UriBuilder.class);
        URI uriMock = mock(URI.class);

        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenAnswer(invocation -> {
            Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);

            when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("start"), eq("2025-10-23 00:00"))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("end"), eq("2025-10-30 23:59"))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.build(eq(appId))).thenReturn(uriMock);

            uriFunction.apply(uriBuilderMock);
            return requestHeadersSpecMock;
        });

        // Create mock tokens per second data
        TokensPerSecondResponse dailyStat = new TokensPerSecondResponse();
        dailyStat.setDate("2025-09-02");
        dailyStat.setTps(30.8603);

        // Create mock response
        List<TokensPerSecondResponse> mockResponse = List.of(dailyStat);
        TokensPerSecondResultResponse response = new TokensPerSecondResultResponse();
        response.setData(mockResponse);

        // Set up the response mock to return our expected response
        when(responseSpecMock.body(any(io.github.guoshiqiufeng.dify.client.core.http.TypeReference.class))).thenReturn(response);

        // Execute the method
        List<TokensPerSecondResponse> actualResponse = client.tokensPerSecond(appId, start, end);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(1, actualResponse.size());
        assertEquals("2025-09-02", actualResponse.get(0).getDate());
        assertEquals(30.8603, actualResponse.get(0).getTps());

        // Verify WebClient interactions
        verify(httpClientMock).get();
        verify(requestHeadersUriSpecMock).uri(any(Function.class));
        verify(responseSpecMock).body(any(io.github.guoshiqiufeng.dify.client.core.http.TypeReference.class));

        when(responseSpecMock.body(any(io.github.guoshiqiufeng.dify.client.core.http.TypeReference.class))).thenReturn(null);

        client.tokensPerSecond(appId, start, end);
    }

    @Test
    @DisplayName("Test userSatisfactionRate method")
    public void testUserSatisfactionRate() {
        // Prepare test data
        String appId = "08534c1a-4316-4cd3-806d-bbbca03f58aa";
        java.time.LocalDateTime start = java.time.LocalDateTime.of(2025, 10, 23, 0, 0);
        java.time.LocalDateTime end = java.time.LocalDateTime.of(2025, 10, 30, 23, 59);

        // Mock the UriBuilder for queryParam functionality
        UriBuilder uriBuilderMock = mock(UriBuilder.class);
        URI uriMock = mock(URI.class);

        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenAnswer(invocation -> {
            Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);

            when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("start"), eq("2025-10-23 00:00"))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("end"), eq("2025-10-30 23:59"))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.build(eq(appId))).thenReturn(uriMock);

            uriFunction.apply(uriBuilderMock);
            return requestHeadersSpecMock;
        });

        // Create mock user satisfaction rate data
        UserSatisfactionRateResponse dailyStat = new UserSatisfactionRateResponse();
        dailyStat.setDate("2025-09-02");
        dailyStat.setRate(1000.0);

        // Create mock response
        List<UserSatisfactionRateResponse> mockResponse = List.of(dailyStat);
        UserSatisfactionRateResultResponse response = new UserSatisfactionRateResultResponse();
        response.setData(mockResponse);

        // Set up the response mock to return our expected response
        when(responseSpecMock.body(any(io.github.guoshiqiufeng.dify.client.core.http.TypeReference.class))).thenReturn(response);

        // Execute the method
        List<UserSatisfactionRateResponse> actualResponse = client.userSatisfactionRate(appId, start, end);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(1, actualResponse.size());
        assertEquals("2025-09-02", actualResponse.get(0).getDate());
        assertEquals(1000.0, actualResponse.get(0).getRate());

        // Verify WebClient interactions
        verify(httpClientMock).get();
        verify(requestHeadersUriSpecMock).uri(any(Function.class));
        verify(responseSpecMock).body(any(io.github.guoshiqiufeng.dify.client.core.http.TypeReference.class));

        when(responseSpecMock.body(any(io.github.guoshiqiufeng.dify.client.core.http.TypeReference.class))).thenReturn(null);

        client.userSatisfactionRate(appId, start, end);
    }

    @Test
    @DisplayName("Test tokenCosts method")
    public void testTokenCosts() {
        // Prepare test data
        String appId = "08534c1a-4316-4cd3-806d-bbbca03f58aa";
        java.time.LocalDateTime start = java.time.LocalDateTime.of(2025, 10, 23, 0, 0);
        java.time.LocalDateTime end = java.time.LocalDateTime.of(2025, 10, 30, 23, 59);

        // Mock the UriBuilder for queryParam functionality
        UriBuilder uriBuilderMock = mock(UriBuilder.class);
        URI uriMock = mock(URI.class);

        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenAnswer(invocation -> {
            Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);

            when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("start"), eq("2025-10-23 00:00"))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("end"), eq("2025-10-30 23:59"))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.build(eq(appId))).thenReturn(uriMock);

            uriFunction.apply(uriBuilderMock);
            return requestHeadersSpecMock;
        });

        // Create mock token costs data
        TokenCostsResponse dailyStat = new TokenCostsResponse();
        dailyStat.setDate("2025-09-02");
        dailyStat.setTokenCount(25686);
        dailyStat.setTotalPrice("0.0039254");
        dailyStat.setCurrency("USD");

        // Create mock response
        List<TokenCostsResponse> mockResponse = List.of(dailyStat);
        TokenCostsResultResponse response = new TokenCostsResultResponse();
        response.setData(mockResponse);

        // Set up the response mock to return our expected response
        when(responseSpecMock.body(any(io.github.guoshiqiufeng.dify.client.core.http.TypeReference.class))).thenReturn(response);

        // Execute the method
        List<TokenCostsResponse> actualResponse = client.tokenCosts(appId, start, end);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(1, actualResponse.size());
        assertEquals("2025-09-02", actualResponse.get(0).getDate());
        assertEquals(Integer.valueOf(25686), actualResponse.get(0).getTokenCount());
        assertEquals("0.0039254", actualResponse.get(0).getTotalPrice());
        assertEquals("USD", actualResponse.get(0).getCurrency());

        // Verify WebClient interactions
        verify(httpClientMock).get();
        verify(requestHeadersUriSpecMock).uri(any(Function.class));
        verify(responseSpecMock).body(any(io.github.guoshiqiufeng.dify.client.core.http.TypeReference.class));

        // Set up the response mock to return our expected response
        when(responseSpecMock.body(any(io.github.guoshiqiufeng.dify.client.core.http.TypeReference.class))).thenReturn(null);

        client.tokenCosts(appId, start, end);
    }

    @Test
    @DisplayName("Test dailyMessages method")
    public void testDailyMessages() {
        // Prepare test data
        String appId = "08534c1a-4316-4cd3-806d-bbbca03f58aa";
        java.time.LocalDateTime start = java.time.LocalDateTime.of(2025, 10, 23, 0, 0);
        java.time.LocalDateTime end = java.time.LocalDateTime.of(2025, 10, 30, 23, 59);

        // Mock the UriBuilder for queryParam functionality
        UriBuilder uriBuilderMock = mock(UriBuilder.class);
        URI uriMock = mock(URI.class);

        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenAnswer(invocation -> {
            Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);

            when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("start"), eq("2025-10-23 00:00"))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("end"), eq("2025-10-30 23:59"))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.build(eq(appId))).thenReturn(uriMock);

            uriFunction.apply(uriBuilderMock);
            return requestHeadersSpecMock;
        });

        // Create mock daily messages data
        DailyMessagesResponse dailyStat = new DailyMessagesResponse();
        dailyStat.setDate("2025-09-02");
        dailyStat.setMessageCount(1);

        // Create mock response
        List<DailyMessagesResponse> mockResponse = List.of(dailyStat);
        DailyMessagesResultResponse response = new DailyMessagesResultResponse();
        response.setData(mockResponse);

        // Set up the response mock to return our expected response
        when(responseSpecMock.body(any(io.github.guoshiqiufeng.dify.client.core.http.TypeReference.class))).thenReturn(response);

        // Execute the method
        List<DailyMessagesResponse> actualResponse = client.dailyMessages(appId, start, end);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(1, actualResponse.size());
        assertEquals("2025-09-02", actualResponse.get(0).getDate());
        assertEquals(Integer.valueOf(1), actualResponse.get(0).getMessageCount());

        // Verify WebClient interactions
        verify(httpClientMock).get();
        verify(requestHeadersUriSpecMock).uri(any(Function.class));
        verify(responseSpecMock).body(any(io.github.guoshiqiufeng.dify.client.core.http.TypeReference.class));

        when(responseSpecMock.body(any(io.github.guoshiqiufeng.dify.client.core.http.TypeReference.class))).thenReturn(null);

        client.dailyMessages(appId, start, end);
    }

    @Test
    public void testAppsRequest() {
        // Prepare test data
        AppsRequest appsRequest = new AppsRequest();
        appsRequest.setPage(1);
        appsRequest.setLimit(10);
        appsRequest.setMode("completion");
        appsRequest.setName("Test App");
        appsRequest.setIsCreatedByMe(true);

        // We need to mock the UriBuilder for the queryParam functionality
        UriBuilder uriBuilderMock = mock(UriBuilder.class);
        URI uriMock = mock(URI.class);

        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenAnswer(invocation -> {
            Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);

            when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("page"), anyInt())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("limit"), anyInt())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("mode"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("name"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("is_created_by_me"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.build()).thenReturn(uriMock);

            uriFunction.apply(uriBuilderMock);
            return requestHeadersSpecMock;
        });

        // Create expected response for first page
        AppsResponseResult resultResponseVO = new AppsResponseResult();
        List<AppsResponse> appsPage1 = new ArrayList<>();

        AppsResponse app1 = new AppsResponse();
        app1.setId("app-123456");
        app1.setName("Test App 1");
        app1.setMode("completion");
        appsPage1.add(app1);

        resultResponseVO.setData(appsPage1);
        resultResponseVO.setHasMore(false);

        // Set up the response mock to return our expected response
        when(responseSpecMock.body(AppsResponseResult.class)).thenReturn(resultResponseVO);

        // Execute the method
        AppsResponseResult actualResponse = client.apps(appsRequest);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(1, actualResponse.getData().size());
        assertEquals(app1.getId(), actualResponse.getData().get(0).getId());
        assertEquals(app1.getName(), actualResponse.getData().get(0).getName());
        assertEquals(app1.getMode(), actualResponse.getData().get(0).getMode());

        // Verify WebClient interactions
        verify(httpClientMock).get();
        verify(responseSpecMock).body(AppsResponseResult.class);
    }

    @Test
    public void testAppsRequestWithEmptyValues() {
        // Prepare test data with empty values that should be filtered out
        AppsRequest appsRequest = new AppsRequest();
        appsRequest.setPage(1);
        appsRequest.setLimit(10);
        appsRequest.setMode("");  // Empty string should be filtered
        appsRequest.setName("");  // Empty string should be filtered
        appsRequest.setIsCreatedByMe(true);

        // We need to mock the UriBuilder for the queryParam functionality
        UriBuilder uriBuilderMock = mock(UriBuilder.class);
        URI uriMock = mock(URI.class);

        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenAnswer(invocation -> {
            Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);

            when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("page"), anyInt())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("limit"), anyInt())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("mode"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("name"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("is_created_by_me"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.build()).thenReturn(uriMock);

            uriFunction.apply(uriBuilderMock);
            return requestHeadersSpecMock;
        });

        // Create expected response
        AppsResponseResult resultResponseVO = new AppsResponseResult();
        List<AppsResponse> appsPage1 = new ArrayList<>();
        resultResponseVO.setData(appsPage1);
        resultResponseVO.setHasMore(false);

        // Set up the response mock to return our expected response
        when(responseSpecMock.body(AppsResponseResult.class)).thenReturn(resultResponseVO);

        // Execute the method
        AppsResponseResult actualResponse = client.apps(appsRequest);

        // Verify the result
        assertNotNull(actualResponse);

        // Verify WebClient interactions
        verify(httpClientMock).get();
        verify(responseSpecMock).body(AppsResponseResult.class);
    }

    @Test
    public void testAppsRequestWithNullValues() {
        // Prepare test data with null values that should be filtered out
        AppsRequest appsRequest = new AppsRequest();
        appsRequest.setPage(1);
        appsRequest.setLimit(10);
        appsRequest.setMode(null);  // Null should be filtered
        appsRequest.setName(null);  // Null should be filtered
        appsRequest.setIsCreatedByMe(true);

        // We need to mock the UriBuilder for the queryParam functionality
        UriBuilder uriBuilderMock = mock(UriBuilder.class);
        URI uriMock = mock(URI.class);

        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenAnswer(invocation -> {
            Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);

            when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("page"), anyInt())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("limit"), anyInt())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("mode"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("name"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("is_created_by_me"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.build()).thenReturn(uriMock);

            uriFunction.apply(uriBuilderMock);
            return requestHeadersSpecMock;
        });

        // Create expected response
        AppsResponseResult resultResponseVO = new AppsResponseResult();
        List<AppsResponse> appsPage1 = new ArrayList<>();
        resultResponseVO.setData(appsPage1);
        resultResponseVO.setHasMore(false);

        // Set up the response mock to return our expected response
        when(responseSpecMock.body(AppsResponseResult.class)).thenReturn(resultResponseVO);

        // Execute the method
        AppsResponseResult actualResponse = client.apps(appsRequest);

        // Verify the result
        assertNotNull(actualResponse);

        // Verify WebClient interactions
        verify(httpClientMock).get();
        verify(responseSpecMock).body(AppsResponseResult.class);
    }

    @Test
    public void testAddAuthorizationHeader() {
        // Test that the addAuthorizationHeader method properly delegates to the server token
        // We can test this by verifying interactions when a method that uses this header is called
        // Create expected response
        AppsResponse expectedResponse = new AppsResponse();
        expectedResponse.setId("app-123");
        expectedResponse.setName("Test App");
        expectedResponse.setMode("completion");

        // Fix for headers(Consumer) method - execute the consumer function
        doAnswer(invocation -> {
            Consumer<HttpHeaders> consumer = invocation.getArgument(0);
            HttpHeaders headers = new HttpHeaders();
            consumer.accept(headers);
            return requestHeadersSpecMock;
        }).when(requestHeadersSpecMock).headers(any(Consumer.class));

        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);

        // Fix for cookies(Consumer) method - execute the consumer function
        doAnswer(invocation -> {
            Consumer<MultiValueMap<String, String>> consumer = invocation.getArgument(0);
            // Create an empty map to pass to the consumer, simulating the cookies map
            MultiValueMap<String, String> cookiesMap = new LinkedMultiValueMap<>();
            consumer.accept(cookiesMap);
            return requestHeadersSpecMock;
        }).when(requestHeadersSpecMock).cookies(any(Consumer.class));

        // Mock the login response
        LoginResultResponse mockLoginResult = new LoginResultResponse();
        mockLoginResult.setResult(DifyResult.SUCCESS);
        LoginResponse mockLoginData = new LoginResponse();
        mockLoginData.setAccessToken("test-access-token");
        mockLoginData.setRefreshToken("test-refresh-token");
        mockLoginResult.setData(mockLoginData);

        // Create a HttpResponse with the mock result
        ResponseEntity<LoginResultResponse> responseEntity = ResponseEntity.<LoginResultResponse>builder()
                .statusCode(200)
                .body(mockLoginResult)
                .build();

        // Mock the toEntity method to return the response entity
        when(responseSpecMock.toEntity(LoginResultResponse.class)).thenReturn(responseEntity);

        // Set up the response mock to return our expected response
        when(responseSpecMock.body(AppsResponse.class)).thenReturn(expectedResponse);

        // Execute the method
        String appId = "app-123";
        AppsResponse actualResponse = client.app(appId);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals("app-123", actualResponse.getId());
        assertEquals("Test App", actualResponse.getName());
        assertEquals("completion", actualResponse.getMode());

        // Verify interactions with mocks - especially that headers are added
        verify(requestHeadersUriSpecMock).uri(eq(ServerUriConstant.APPS + "/{appId}"), eq(appId));
        verify(requestHeadersSpecMock).headers(any());
    }

    @Test
    public void testAddAuthorizationCookies() {
        // Test that the addAuthorizationCookies method properly delegates to the server token
        // We can test this by verifying interactions when a method that uses these cookies is called
        // Create expected response
        AppsResponse expectedResponse = new AppsResponse();
        expectedResponse.setId("app-123");
        expectedResponse.setName("Test App");
        expectedResponse.setMode("completion");

        // Set up the response mock to return our expected response
        when(responseSpecMock.body(AppsResponse.class)).thenReturn(expectedResponse);

        // Execute the method
        String appId = "app-123";
        AppsResponse actualResponse = client.app(appId);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals("app-123", actualResponse.getId());
        assertEquals("Test App", actualResponse.getName());
        assertEquals("completion", actualResponse.getMode());

        // Verify interactions with mocks - especially that cookies are added
        verify(requestHeadersUriSpecMock).uri(eq(ServerUriConstant.APPS + "/{appId}"), eq(appId));
        verify(requestHeadersSpecMock).headers(any());
        // Cookies are added via the cookies method in the client
        verify(requestHeadersSpecMock).cookies(any());
    }


    @Test
    public void testProcessLoginResultWithSuccess() {
        // Create a mock LoginResultResponse with success result
        LoginResultResponse loginResult = new LoginResultResponse();
        loginResult.setResult(DifyResult.SUCCESS);

        LoginResponse loginData = new LoginResponse();
        loginData.setAccessToken("test-access-token");
        loginData.setRefreshToken("test-refresh-token");
        loginData.setCsrfToken("test-csrf-token");

        loginResult.setData(loginData);

        // Use reflection to access the private method
        try {
            java.lang.reflect.Method method = DifyServerDefaultClient.class
                    .getDeclaredMethod("processLoginResult", LoginResultResponse.class);
            method.setAccessible(true);

            LoginResponse result = (LoginResponse) method.invoke(client, loginResult);

            // Verify the result
            assertNotNull(result);
            assertEquals("test-access-token", result.getAccessToken());
            assertEquals("test-refresh-token", result.getRefreshToken());
            assertEquals("test-csrf-token", result.getCsrfToken());
        } catch (Exception e) {
            fail("Failed to test processLoginResult method: " + e.getMessage());
        }
    }

    @Test
    public void testProcessLoginResultWithFailure() {
        // Create a mock LoginResultResponse with failure result
        LoginResultResponse loginResult = new LoginResultResponse();
        loginResult.setResult("failure"); // or any non-success result

        LoginResponse loginData = new LoginResponse();
        loginData.setAccessToken("test-access-token");
        loginData.setRefreshToken("test-refresh-token");
        loginData.setCsrfToken("test-csrf-token");

        loginResult.setData(loginData);

        // Use reflection to access the private method
        try {
            java.lang.reflect.Method method = DifyServerDefaultClient.class
                    .getDeclaredMethod("processLoginResult", LoginResultResponse.class);
            method.setAccessible(true);

            LoginResponse result = (LoginResponse) method.invoke(client, loginResult);

            // Verify the result is null since the result was not SUCCESS
            assertNull(result);
        } catch (Exception e) {
            fail("Failed to test processLoginResult method: " + e.getMessage());
        }
    }

    @Test
    public void testProcessLoginResultWithNull() {
        // Test with null input
        try {
            java.lang.reflect.Method method = DifyServerDefaultClient.class
                    .getDeclaredMethod("processLoginResult", LoginResultResponse.class);
            method.setAccessible(true);

            LoginResponse result = (LoginResponse) method.invoke(client, (LoginResultResponse) null);

            // Verify the result is null
            assertNull(result);
        } catch (Exception e) {
            fail("Failed to test processLoginResult method: " + e.getMessage());
        }
    }

    @Test
    public void testProcessLoginResultWithCookieHeadersSuccess() {
        // Create a mock LoginResultResponse with success result
        LoginResultResponse loginResult = new LoginResultResponse();
        loginResult.setResult(DifyResult.SUCCESS);

        LoginResponse loginData = new LoginResponse();
        loginData.setAccessToken("test-access-token-from-data");
        loginData.setRefreshToken("test-refresh-token-from-data");
        loginData.setCsrfToken("test-csrf-token-from-data");

        loginResult.setData(loginData);

        // Create mock Set-Cookie headers
        List<String> setCookieHeaders = new ArrayList<>();
        setCookieHeaders.add("access_token=new-access-token; Path=/; HttpOnly");
        setCookieHeaders.add("refresh_token=new-refresh-token; Path=/; HttpOnly");
        setCookieHeaders.add("csrf_token=new-csrf-token; Path=/");

        // Use reflection to access the private method
        try {
            java.lang.reflect.Method method = DifyServerDefaultClient.class
                    .getDeclaredMethod("processLoginResult", LoginResultResponse.class, List.class);
            method.setAccessible(true);

            LoginResponse result = (LoginResponse) method.invoke(client, loginResult, setCookieHeaders);

            // Verify the result uses data from the response, not from cookies (since response data exists)
            assertNotNull(result);
            assertEquals("test-access-token-from-data", result.getAccessToken());
            assertEquals("test-refresh-token-from-data", result.getRefreshToken());
            assertEquals("test-csrf-token-from-data", result.getCsrfToken());
        } catch (Exception e) {
            fail("Failed to test processLoginResult method with cookies: " + e.getMessage());
        }
    }

    @Test
    public void testProcessLoginResultWithCookieHeadersFailureAndCookies() {
        // Create a mock LoginResultResponse with failure result
        LoginResultResponse loginResult = new LoginResultResponse();
        loginResult.setResult("failure"); // Failure case to use cookies

        // Create mock Set-Cookie headers
        List<String> setCookieHeaders = new ArrayList<>();
        setCookieHeaders.add("access_token=cookie-access-token; Path=/; HttpOnly");
        setCookieHeaders.add("refresh_token=cookie-refresh-token; Path=/; HttpOnly");
        setCookieHeaders.add("csrf_token=cookie-csrf-token; Path=/");

        // Use reflection to access the private method
        try {
            java.lang.reflect.Method method = DifyServerDefaultClient.class
                    .getDeclaredMethod("processLoginResult", LoginResultResponse.class, List.class);
            method.setAccessible(true);

            LoginResponse result = (LoginResponse) method.invoke(client, loginResult, setCookieHeaders);

            // Verify the result uses data from cookies since response was failure
            assertNotNull(result);
            assertEquals("cookie-access-token", result.getAccessToken());
            assertEquals("cookie-refresh-token", result.getRefreshToken());
            assertEquals("cookie-csrf-token", result.getCsrfToken());
        } catch (Exception e) {
            fail("Failed to test processLoginResult method with cookies: " + e.getMessage());
        }
    }

    @Test
    public void testProcessLoginResultWithCookieHeadersFailureAndEmptyCookies() {
        // Create a mock LoginResultResponse with failure result
        LoginResultResponse loginResult = new LoginResultResponse();
        loginResult.setResult("failure");

        // Empty set of cookie headers
        List<String> setCookieHeaders = new ArrayList<>();

        // Use reflection to access the private method
        try {
            java.lang.reflect.Method method = DifyServerDefaultClient.class
                    .getDeclaredMethod("processLoginResult", LoginResultResponse.class, List.class);
            method.setAccessible(true);

            LoginResponse result = (LoginResponse) method.invoke(client, loginResult, setCookieHeaders);

            // Verify the result is null since both response and cookies are empty
            assertNotNull(result);
        } catch (Exception e) {
            fail("Failed to test processLoginResult method with empty cookies: " + e.getMessage());
        }
    }

    @Test
    public void testProcessLoginResultWithCookieHeadersNullResponse() {
        // Test with null response but with cookies
        List<String> setCookieHeaders = new ArrayList<>();
        setCookieHeaders.add("access_token=cookie-access-token; Path=/; HttpOnly");
        setCookieHeaders.add("refresh_token=cookie-refresh-token; Path=/; HttpOnly");
        setCookieHeaders.add("csrf_token=cookie-csrf-token; Path=/");

        // Use reflection to access the private method
        try {
            java.lang.reflect.Method method = DifyServerDefaultClient.class
                    .getDeclaredMethod("processLoginResult", LoginResultResponse.class, List.class);
            method.setAccessible(true);

            LoginResponse result = (LoginResponse) method.invoke(client, null, setCookieHeaders);

            // Verify the result uses data from cookies since response is null
            assertNotNull(result);
            assertEquals("cookie-access-token", result.getAccessToken());
            assertEquals("cookie-refresh-token", result.getRefreshToken());
            assertEquals("cookie-csrf-token", result.getCsrfToken());
        } catch (Exception e) {
            fail("Failed to test processLoginResult method with null response: " + e.getMessage());
        }
    }

    @Test
    public void testExtractTokenValue() {
        // Use reflection to access the private method
        try {
            java.lang.reflect.Method method = DifyServerDefaultClient.class
                    .getDeclaredMethod("extractTokenValue", String.class, String.class);
            method.setAccessible(true);

            // Test extracting access token
            String cookieHeader1 = "access_token=abc123xyz; Path=/; HttpOnly";
            String result1 = (String) method.invoke(client, cookieHeader1, "access_token=");
            assertEquals("abc123xyz", result1);

            // Test extracting refresh token
            String cookieHeader2 = "refresh_token=def456uvw; Path=/; Secure";
            String result2 = (String) method.invoke(client, cookieHeader2, "refresh_token=");
            assertEquals("def456uvw", result2);

            // Test extracting csrf token
            String cookieHeader3 = "csrf_token=ghi789rst; Path=/";
            String result3 = (String) method.invoke(client, cookieHeader3, "csrf_token=");
            assertEquals("ghi789rst", result3);

            // Test extracting token with semicolon at the end
            String cookieHeader4 = "access_token=full_value; Expires=Thu, 01-Jan-2026 00:00:00 GMT; Path=/";
            String result4 = (String) method.invoke(client, cookieHeader4, "access_token=");
            assertEquals("full_value", result4);

            // Test extracting token from header with multiple values
            String cookieHeader5 = "other_token=other_value; access_token=correct_value; Path=/";
            String result5 = (String) method.invoke(client, cookieHeader5, "access_token=");
            assertEquals("correct_value", result5);

            // Test extracting token from header with multiple values (second test)
            String cookieHeader6 = "access_token=exists; Path=/";
            String result6 = (String) method.invoke(client, cookieHeader6, "refresh_token=");
            assertEquals("exists", result6); // This should return null if prefix not found

            // Test with empty value at the end
            String cookieHeader7 = "access_token=; Path=/";
            String result7 = (String) method.invoke(client, cookieHeader7, "access_token=");
            assertEquals("", result7);

        } catch (Exception e) {
            fail("Failed to test extractTokenValue method: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test deleteAppApiKey method")
    public void testDeleteAppApiKey() {
        // Prepare test data
        String appId = "08534c1a-4316-4cd3-806d-bbbca03f58aa";
        String apiKeyId = "780b00d8-4d3d-4d39-8fb4-a641a992976f";

        // Set up the response mock to return void (for delete operation)
        when(responseSpecMock.body(eq(Void.class))).thenReturn(null);

        // Execute the method
        client.deleteAppApiKey(appId, apiKeyId);

        // Verify WebClient interactions
        verify(httpClientMock).delete();
        verify(requestHeadersUriSpecMock).uri(eq(ServerUriConstant.APP_API_KEYS + "/{apiKeyId}"), eq(appId), eq(apiKeyId));
        verify(requestHeadersSpecMock).headers(any());
        verify(responseSpecMock).body(eq(Void.class));
    }

    @Test
    @DisplayName("Test deleteDatasetApiKey method")
    public void testDeleteDatasetApiKey() {
        // Prepare test data
        String apiKeyId = "780b00d8-4d3d-4d39-8fb4-a641a992976f";

        // Set up the response mock to return void (for delete operation)
        when(responseSpecMock.body(eq(Void.class))).thenReturn(null);

        // Execute the method
        client.deleteDatasetApiKey(apiKeyId);

        // Verify WebClient interactions
        verify(httpClientMock).delete();
        verify(requestHeadersUriSpecMock).uri(eq(ServerUriConstant.DATASET_API_KEYS), eq(apiKeyId));
        verify(requestHeadersSpecMock).headers(any());
        verify(responseSpecMock).body(eq(Void.class));
    }

    @Test
    @DisplayName("Test getDatasetIndexingStatus method")
    public void testGetDatasetIndexingStatus() {
        // Prepare test data
        String datasetId = "dataset-123456";

        // Create mock response
        io.github.guoshiqiufeng.dify.dataset.dto.response.DocumentIndexingStatusResponse mockResponse =
                new io.github.guoshiqiufeng.dify.dataset.dto.response.DocumentIndexingStatusResponse();

        // Set up the response mock
        when(responseSpecMock.body(eq(io.github.guoshiqiufeng.dify.dataset.dto.response.DocumentIndexingStatusResponse.class)))
                .thenReturn(mockResponse);

        // Execute the method
        io.github.guoshiqiufeng.dify.dataset.dto.response.DocumentIndexingStatusResponse actualResponse =
                client.getDatasetIndexingStatus(datasetId);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(mockResponse, actualResponse);

        // Verify WebClient interactions
        verify(httpClientMock).get();
        verify(requestHeadersUriSpecMock).uri(eq(ServerUriConstant.DATASET_INDEXING_STATUS), eq(datasetId));
        verify(requestHeadersSpecMock).headers(any());
        verify(responseSpecMock).body(eq(io.github.guoshiqiufeng.dify.dataset.dto.response.DocumentIndexingStatusResponse.class));
    }

    @Test
    @DisplayName("Test getDocumentIndexingStatus method")
    public void testGetDocumentIndexingStatus() {
        // Prepare test data
        String datasetId = "dataset-123456";
        String documentId = "document-789012";

        // Create mock response
        io.github.guoshiqiufeng.dify.dataset.dto.response.DocumentIndexingStatusResponse.ProcessingStatus mockResponse =
                new io.github.guoshiqiufeng.dify.dataset.dto.response.DocumentIndexingStatusResponse.ProcessingStatus();

        // Set up the response mock
        when(responseSpecMock.body(eq(io.github.guoshiqiufeng.dify.dataset.dto.response.DocumentIndexingStatusResponse.ProcessingStatus.class)))
                .thenReturn(mockResponse);

        // Execute the method
        io.github.guoshiqiufeng.dify.dataset.dto.response.DocumentIndexingStatusResponse.ProcessingStatus actualResponse =
                client.getDocumentIndexingStatus(datasetId, documentId);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(mockResponse, actualResponse);

        // Verify WebClient interactions
        verify(httpClientMock).get();
        ArgumentCaptor<Map> mapCaptor = ArgumentCaptor.forClass(Map.class);
        verify(requestHeadersUriSpecMock).uri(eq(ServerUriConstant.DOCUMENT_INDEXING_STATUS), mapCaptor.capture());
        Map<String, String> capturedMap = mapCaptor.getValue();
        assertEquals(datasetId, capturedMap.get("datasetId"));
        assertEquals(documentId, capturedMap.get("documentId"));
        verify(requestHeadersSpecMock).headers(any());
        verify(responseSpecMock).body(eq(io.github.guoshiqiufeng.dify.dataset.dto.response.DocumentIndexingStatusResponse.ProcessingStatus.class));
    }

    @Test
    @DisplayName("Test getDatasetErrorDocuments method")
    public void testGetDatasetErrorDocuments() {
        // Prepare test data
        String datasetId = "dataset-123456";

        // Create mock response
        DatasetErrorDocumentsResponse mockResponse =
                new DatasetErrorDocumentsResponse();
        mockResponse.setTotal(5);

        // Set up the response mock
        when(responseSpecMock.body(eq(DatasetErrorDocumentsResponse.class)))
                .thenReturn(mockResponse);

        // Execute the method
        DatasetErrorDocumentsResponse actualResponse =
                client.getDatasetErrorDocuments(datasetId);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(mockResponse, actualResponse);
        assertEquals(5, actualResponse.getTotal());

        // Verify WebClient interactions
        verify(httpClientMock).get();
        verify(requestHeadersUriSpecMock).uri(eq(ServerUriConstant.DATASET_ERROR_DOCUMENTS), eq(datasetId));
        verify(requestHeadersSpecMock).headers(any());
        verify(responseSpecMock).body(eq(DatasetErrorDocumentsResponse.class));
    }

    @Test
    @DisplayName("Test retryDocumentIndexing method")
    public void testRetryDocumentIndexing() {
        // Prepare test data
        io.github.guoshiqiufeng.dify.server.dto.request.DocumentRetryRequest request =
                new io.github.guoshiqiufeng.dify.server.dto.request.DocumentRetryRequest();
        request.setDatasetId("dataset-123456");
        request.setDocumentIds(List.of("doc-1", "doc-2", "doc-3"));

        // Set up the response mock to return void
        when(responseSpecMock.toBodilessEntity()).thenReturn(null);

        // Execute the method
        client.retryDocumentIndexing(request);

        // Verify WebClient interactions
        verify(httpClientMock).post();
        verify(requestBodyUriSpecMock).uri(eq(ServerUriConstant.DOCUMENT_RETRY), eq(request.getDatasetId()));
        verify(requestBodySpecMock).headers(any());
        verify(requestBodySpecMock).body(any(Map.class));
        verify(responseSpecMock).toBodilessEntity();
    }

    @Test
    @DisplayName("Test apps method with pagination (hasMore=true)")
    public void testAppsWithPagination() {
        // Prepare test data
        String mode = "completion";
        String name = "Test";

        // We need to mock the UriBuilder for the queryParam functionality
        UriBuilder uriBuilderMock = mock(UriBuilder.class);
        URI uriMock = mock(URI.class);

        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenAnswer(invocation -> {
            Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);

            when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("page"), anyInt())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("limit"), anyInt())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("mode"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("name"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.build()).thenReturn(uriMock);

            uriFunction.apply(uriBuilderMock);
            return requestHeadersSpecMock;
        });

        // Create expected response for first page with hasMore=true
        AppsResponseResult resultResponsePage1 = new AppsResponseResult();
        List<AppsResponse> appsPage1 = new ArrayList<>();

        AppsResponse app1 = new AppsResponse();
        app1.setId("app-123456");
        app1.setName("Test App 1");
        app1.setMode(mode);
        appsPage1.add(app1);

        resultResponsePage1.setData(appsPage1);
        resultResponsePage1.setHasMore(true); // This will trigger pagination

        // Create expected response for second page with hasMore=false
        AppsResponseResult resultResponsePage2 = new AppsResponseResult();
        List<AppsResponse> appsPage2 = new ArrayList<>();

        AppsResponse app2 = new AppsResponse();
        app2.setId("app-789012");
        app2.setName("Test App 2");
        app2.setMode(mode);
        appsPage2.add(app2);

        resultResponsePage2.setData(appsPage2);
        resultResponsePage2.setHasMore(false); // No more pages

        // Set up the response mock to return different responses for each call
        when(responseSpecMock.body(AppsResponseResult.class))
                .thenReturn(resultResponsePage1)
                .thenReturn(resultResponsePage2);

        // Execute the method
        List<AppsResponse> actualResponse = client.apps(mode, name);

        // Verify the result contains apps from both pages
        assertNotNull(actualResponse);
        assertEquals(2, actualResponse.size());
        assertEquals(app1.getId(), actualResponse.get(0).getId());
        assertEquals(app1.getName(), actualResponse.get(0).getName());
        assertEquals(app2.getId(), actualResponse.get(1).getId());
        assertEquals(app2.getName(), actualResponse.get(1).getName());

        // Verify WebClient interactions - should be called twice (once for each page)
        verify(httpClientMock, times(2)).get();
        verify(responseSpecMock, times(2)).body(AppsResponseResult.class);
    }

    @Test
    @DisplayName("Test apps method with null data in response")
    public void testAppsWithNullData() {
        // Prepare test data
        String mode = "completion";
        String name = "Test";

        // We need to mock the UriBuilder for the queryParam functionality
        UriBuilder uriBuilderMock = mock(UriBuilder.class);
        URI uriMock = mock(URI.class);

        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenAnswer(invocation -> {
            Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);

            when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("page"), anyInt())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("limit"), anyInt())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("mode"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("name"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.build()).thenReturn(uriMock);

            uriFunction.apply(uriBuilderMock);
            return requestHeadersSpecMock;
        });

        // Create expected response with null data
        AppsResponseResult resultResponse = new AppsResponseResult();
        resultResponse.setData(null); // Null data to test the branch
        resultResponse.setHasMore(false);

        // Set up the response mock to return our expected response
        when(responseSpecMock.body(AppsResponseResult.class)).thenReturn(resultResponse);

        // Execute the method
        List<AppsResponse> actualResponse = client.apps(mode, name);

        // Verify the result is empty (no apps added when data is null)
        assertNotNull(actualResponse);
        assertEquals(0, actualResponse.size());

        // Verify WebClient interactions
        verify(httpClientMock).get();
        verify(responseSpecMock).body(AppsResponseResult.class);
    }

    @Test
    @DisplayName("Test login with password encryption disabled")
    public void testLoginWithPasswordEncryptionDisabled() {
        // Create server properties with password encryption disabled
        DifyProperties.Server serverProperties = new DifyProperties.Server();
        serverProperties.setEmail("test@example.com");
        serverProperties.setPassword("password123");
        serverProperties.setPasswordEncryption(false); // Disable encryption
        client = new DifyServerDefaultClient(httpClientMock, serverProperties, difyServerTokenMock);

        // Mock the login response
        LoginResultResponse mockLoginResult = new LoginResultResponse();
        mockLoginResult.setResult(DifyResult.SUCCESS);
        LoginResponse mockLoginData = new LoginResponse();
        mockLoginData.setAccessToken("test-access-token");
        mockLoginData.setRefreshToken("test-refresh-token");
        mockLoginResult.setData(mockLoginData);

        // Create a HttpResponse with the mock result
        ResponseEntity<LoginResultResponse> responseEntity = ResponseEntity.<LoginResultResponse>builder()
                .statusCode(200)
                .body(mockLoginResult)
                .build();

        // Mock the toEntity method to return the response entity
        when(responseSpecMock.toEntity(LoginResultResponse.class)).thenReturn(responseEntity);

        // Call the method to test
        LoginResponse response = client.login();

        // Verify the response
        assertNotNull(response);
        assertEquals("test-access-token", response.getAccessToken());
        assertEquals("test-refresh-token", response.getRefreshToken());

        // Verify interactions with mocks
        verify(requestBodyUriSpecMock).uri("/console/api/login");

        // Capture and verify the request body - password should NOT be encrypted
        ArgumentCaptor<DifyLoginRequest> bodyCaptor = ArgumentCaptor.forClass(DifyLoginRequest.class);
        verify(requestBodySpecMock).body(bodyCaptor.capture());

        DifyLoginRequest capturedBody = bodyCaptor.getValue();
        assertEquals("test@example.com", capturedBody.getEmail());
        assertEquals("password123", capturedBody.getPassword()); // Should be plain text
    }

    @Test
    @DisplayName("Test login with empty password")
    public void testLoginWithEmptyPassword() {
        // Create server properties with empty password
        DifyProperties.Server serverProperties = new DifyProperties.Server();
        serverProperties.setEmail("test@example.com");
        serverProperties.setPassword(""); // Empty password
        serverProperties.setPasswordEncryption(true);
        client = new DifyServerDefaultClient(httpClientMock, serverProperties, difyServerTokenMock);

        // Mock the login response
        LoginResultResponse mockLoginResult = new LoginResultResponse();
        mockLoginResult.setResult(DifyResult.SUCCESS);
        LoginResponse mockLoginData = new LoginResponse();
        mockLoginData.setAccessToken("test-access-token");
        mockLoginData.setRefreshToken("test-refresh-token");
        mockLoginResult.setData(mockLoginData);

        // Create a HttpResponse with the mock result
        ResponseEntity<LoginResultResponse> responseEntity = ResponseEntity.<LoginResultResponse>builder()
                .statusCode(200)
                .body(mockLoginResult)
                .build();

        // Mock the toEntity method to return the response entity
        when(responseSpecMock.toEntity(LoginResultResponse.class)).thenReturn(responseEntity);

        // Call the method to test
        LoginResponse response = client.login();

        // Verify the response
        assertNotNull(response);
        assertEquals("test-access-token", response.getAccessToken());
        assertEquals("test-refresh-token", response.getRefreshToken());

        // Verify interactions with mocks
        verify(requestBodyUriSpecMock).uri("/console/api/login");

        // Capture and verify the request body - password should remain empty
        ArgumentCaptor<DifyLoginRequest> bodyCaptor = ArgumentCaptor.forClass(DifyLoginRequest.class);
        verify(requestBodySpecMock).body(bodyCaptor.capture());

        DifyLoginRequest capturedBody = bodyCaptor.getValue();
        assertEquals("test@example.com", capturedBody.getEmail());
        assertEquals("", capturedBody.getPassword()); // Should remain empty
    }

    @Test
    @DisplayName("Test extractToken with __Host- prefix")
    public void testExtractTokenWithHostPrefix() {
        // Use reflection to access the private method
        try {
            java.lang.reflect.Method method = DifyServerDefaultClient.class
                    .getDeclaredMethod("extractToken", String.class, String.class);
            method.setAccessible(true);

            // Test extracting token with __Host- prefix
            String cookieHeader = "__Host-access_token=secure_token_value; Path=/; Secure; HttpOnly";
            String result = (String) method.invoke(client, cookieHeader, "access_token");
            assertEquals("secure_token_value", result);

            // Test extracting refresh token with __Host- prefix
            String cookieHeader2 = "__Host-refresh_token=secure_refresh_value; Path=/; Secure";
            String result2 = (String) method.invoke(client, cookieHeader2, "refresh_token");
            assertEquals("secure_refresh_value", result2);

            // Test extracting csrf token with __Host- prefix
            String cookieHeader3 = "__Host-csrf_token=secure_csrf_value; Path=/";
            String result3 = (String) method.invoke(client, cookieHeader3, "csrf_token");
            assertEquals("secure_csrf_value", result3);

        } catch (Exception e) {
            fail("Failed to test extractToken method with __Host- prefix: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test extractToken with empty value")
    public void testExtractTokenWithEmptyValue() {
        // Use reflection to access the private method
        try {
            java.lang.reflect.Method method = DifyServerDefaultClient.class
                    .getDeclaredMethod("extractToken", String.class, String.class);
            method.setAccessible(true);

            // Test extracting token with empty value (should return null)
            String cookieHeader = "access_token=; Path=/; HttpOnly";
            String result = (String) method.invoke(client, cookieHeader, "access_token");
            assertNull(result); // Empty value should return null

            // Test with __Host- prefix and empty value
            String cookieHeader2 = "__Host-refresh_token=; Path=/; Secure";
            String result2 = (String) method.invoke(client, cookieHeader2, "refresh_token");
            assertNull(result2); // Empty value should return null

        } catch (Exception e) {
            fail("Failed to test extractToken method with empty value: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test extractTokenValue without semicolon at end")
    public void testExtractTokenValueWithoutSemicolon() {
        // Use reflection to access the private method
        try {
            java.lang.reflect.Method method = DifyServerDefaultClient.class
                    .getDeclaredMethod("extractTokenValue", String.class, String.class);
            method.setAccessible(true);

            // Test extracting token value when there's no semicolon at the end
            String cookieHeader = "access_token=token_at_end";
            String result = (String) method.invoke(client, cookieHeader, "access_token=");
            assertEquals("token_at_end", result);

            // Test with __Host- prefix and no semicolon
            String cookieHeader2 = "__Host-refresh_token=refresh_at_end";
            String result2 = (String) method.invoke(client, cookieHeader2, "__Host-refresh_token=");
            assertEquals("refresh_at_end", result2);

        } catch (Exception e) {
            fail("Failed to test extractTokenValue method without semicolon: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test processLoginResult with null response and null cookie headers")
    public void testProcessLoginResultWithBothNull() {
        // Test with both null response and null cookie headers
        try {
            java.lang.reflect.Method method = DifyServerDefaultClient.class
                    .getDeclaredMethod("processLoginResult", LoginResultResponse.class, List.class);
            method.setAccessible(true);

            LoginResponse result = (LoginResponse) method.invoke(client, null, null);

            // Verify the result is null since both are null
            assertNull(result);
        } catch (Exception e) {
            fail("Failed to test processLoginResult method with both null: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test processLoginResult with success response and null cookie headers")
    public void testProcessLoginResultWithSuccessAndNullCookies() {
        // Create a mock LoginResultResponse with success result
        LoginResultResponse loginResult = new LoginResultResponse();
        loginResult.setResult(DifyResult.SUCCESS);

        LoginResponse loginData = new LoginResponse();
        loginData.setAccessToken("test-access-token-from-data");
        loginData.setRefreshToken("test-refresh-token-from-data");
        loginData.setCsrfToken("test-csrf-token-from-data");

        loginResult.setData(loginData);

        // Test with null cookie headers
        try {
            java.lang.reflect.Method method = DifyServerDefaultClient.class
                    .getDeclaredMethod("processLoginResult", LoginResultResponse.class, List.class);
            method.setAccessible(true);

            LoginResponse result = (LoginResponse) method.invoke(client, loginResult, null);

            // Verify the result uses data from the response (cookie headers are null)
            assertNotNull(result);
            assertEquals("test-access-token-from-data", result.getAccessToken());
            assertEquals("test-refresh-token-from-data", result.getRefreshToken());
            assertEquals("test-csrf-token-from-data", result.getCsrfToken());
        } catch (Exception e) {
            fail("Failed to test processLoginResult method with success and null cookies: " + e.getMessage());
        }
    }
}
