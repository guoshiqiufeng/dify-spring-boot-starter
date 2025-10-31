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
package io.github.guoshiqiufeng.dify.client.spring5.server;

import io.github.guoshiqiufeng.dify.client.spring5.BaseClientTest;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    @BeforeEach
    public void setup() {
        super.setup();
        // Create real client with mocked WebClient
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        DifyProperties.Server serverConfig = new DifyProperties.Server();
        serverConfig.setEmail("test@example.com");
        serverConfig.setPassword("password123");
        client = new DifyServerDefaultClient(serverConfig, new DifyServerTokenDefault(),
                "https://api.dify.ai", clientConfig, webClientBuilderMock);
    }

    @Test
    public void testLogin() {
        // Prepare test data
        String email = "test@example.com";
        String password = "password123";

        // Create expected response
        LoginResultResponse resultResponseVO = new LoginResultResponse();
        resultResponseVO.setResult(DifyResult.SUCCESS);

        LoginResponse responseVO = new LoginResponse();
        responseVO.setAccessToken("test-access-token");
        responseVO.setRefreshToken("test-refresh-token");
        resultResponseVO.setData(responseVO);

        // Create a ResponseEntity with the expected result
        ResponseEntity<LoginResultResponse> responseEntity = ResponseEntity.ok(resultResponseVO);

        // Set up the response mock to return our expected response
        when(responseSpecMock.toEntity(LoginResultResponse.class)).thenReturn(Mono.just(responseEntity));

        // Execute the method
        LoginResponse actualResponse = client.login();

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(responseVO.getAccessToken(), actualResponse.getAccessToken());
        assertEquals(responseVO.getRefreshToken(), actualResponse.getRefreshToken());

        // Verify WebClient interactions
        verify(webClientMock).post();
        verify(requestBodyUriSpecMock).uri(ServerUriConstant.LOGIN);
        verify(requestBodySpecMock).bodyValue(any(DifyLoginRequest.class));
        verify(responseSpecMock).toEntity(LoginResultResponse.class);
    }

    @Test
    public void testRefreshToken() {
        // Prepare test data
        String refreshToken = "test-refresh-token";

        // Create expected response
        LoginResultResponse resultResponseVO = new LoginResultResponse();
        resultResponseVO.setResult(DifyResult.SUCCESS);

        LoginResponse responseVO = new LoginResponse();
        responseVO.setAccessToken("new-access-token");
        responseVO.setRefreshToken("new-refresh-token");
        resultResponseVO.setData(responseVO);

        // Create a ResponseEntity with the expected result
        ResponseEntity<LoginResultResponse> responseEntity = ResponseEntity.ok(resultResponseVO);

        // Set up the response mock to return our expected response - toEntity returns Mono<ResponseEntity<T>>
        when(responseSpecMock.toEntity(LoginResultResponse.class)).thenReturn(Mono.just(responseEntity));

        // Execute the method
        LoginResponse actualResponse = client.refreshToken(refreshToken);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(responseVO.getAccessToken(), actualResponse.getAccessToken());
        assertEquals(responseVO.getRefreshToken(), actualResponse.getRefreshToken());

        // Verify WebClient interactions
        verify(webClientMock).post();
        verify(requestBodyUriSpecMock).uri(ServerUriConstant.REFRESH_TOKEN);
        verify(requestBodySpecMock).bodyValue(anyMap());
        verify(responseSpecMock).toEntity(LoginResultResponse.class);
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
        when(responseSpecMock.bodyToMono(AppsResponse.class)).thenReturn(Mono.just(expectedResponse));

        // Execute the method
        AppsResponse actualResponse = client.app(appId);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getName(), actualResponse.getName());
        assertEquals(expectedResponse.getIcon(), actualResponse.getIcon());
        assertEquals(expectedResponse.getMode(), actualResponse.getMode());

        // Verify WebClient interactions
        verify(webClientMock).get();
        verify(requestHeadersUriSpecMock).uri(eq(ServerUriConstant.APPS + "/{appId}"), eq(appId));
        verify(responseSpecMock).bodyToMono(AppsResponse.class);
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
        when(responseSpecMock.bodyToMono(AppsResponseResult.class)).thenReturn(Mono.just(resultResponseVO));

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
        verify(webClientMock).get();
        verify(responseSpecMock).bodyToMono(AppsResponseResult.class);

        client.apps("", "");
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
        when(responseSpecMock.bodyToMono(ApiKeyResultResponse.class)).thenReturn(Mono.just(resultResponseVO));

        // Execute the method
        List<ApiKeyResponse> actualResponse = client.getAppApiKey(appId);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(1, actualResponse.size());
        assertEquals(apiKey.getId(), actualResponse.get(0).getId());
        assertEquals(apiKey.getToken(), actualResponse.get(0).getToken());
        assertEquals(apiKey.getType(), actualResponse.get(0).getType());

        // Verify WebClient interactions
        verify(webClientMock).get();
        verify(requestHeadersUriSpecMock).uri(eq(ServerUriConstant.APPS + "/{appId}/api-keys"), eq(appId));
        verify(responseSpecMock).bodyToMono(ApiKeyResultResponse.class);
    }


    @Test
    @DisplayName("Test getAppApiKey method on return null")
    public void testGetAppApiKeyNull() {
        // Prepare test data
        String appId = "app-123456";

        // Set up the response mock to return our expected response
        when(responseSpecMock.bodyToMono(ApiKeyResultResponse.class)).thenReturn(Mono.empty());

        // Execute the method
        List<ApiKeyResponse> actualResponse = client.getAppApiKey(appId);

        // Verify the result
        assertNotNull(actualResponse);

        // Verify WebClient interactions
        verify(webClientMock).get();
        verify(requestHeadersUriSpecMock).uri(eq(ServerUriConstant.APPS + "/{appId}/api-keys"), eq(appId));
        verify(responseSpecMock).bodyToMono(ApiKeyResultResponse.class);
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
        when(responseSpecMock.bodyToMono(ApiKeyResponse.class)).thenReturn(Mono.just(apiKey));

        // Execute the method
        List<ApiKeyResponse> actualResponse = client.initAppApiKey(appId);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(1, actualResponse.size());
        assertEquals(apiKey.getId(), actualResponse.get(0).getId());
        assertEquals(apiKey.getToken(), actualResponse.get(0).getToken());
        assertEquals(apiKey.getType(), actualResponse.get(0).getType());

        // Verify WebClient interactions
        verify(webClientMock).post();
        verify(requestBodyUriSpecMock).uri(eq(ServerUriConstant.APPS + "/{appId}/api-keys"), eq(appId));
        verify(requestBodySpecMock).headers(any());
        verify(responseSpecMock).bodyToMono(ApiKeyResponse.class);
    }

    @Test
    public void testInitAppApiKeyNull() {
        // Prepare test data
        String appId = "app-123456";

        // Set up the response mock to return our expected response
        when(responseSpecMock.bodyToMono(ApiKeyResponse.class)).thenReturn(Mono.empty());

        // Execute the method
        List<ApiKeyResponse> actualResponse = client.initAppApiKey(appId);

        // Verify the result
        assertNull(actualResponse);

        // Verify WebClient interactions
        verify(webClientMock).post();
        verify(requestBodyUriSpecMock).uri(eq(ServerUriConstant.APPS + "/{appId}/api-keys"), eq(appId));
        verify(requestBodySpecMock).headers(any());
        verify(responseSpecMock).bodyToMono(ApiKeyResponse.class);
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
        when(responseSpecMock.bodyToMono(DatasetApiKeyResult.class)).thenReturn(Mono.just(resultResponseVO));

        // Execute the method
        List<DatasetApiKeyResponse> actualResponse = client.getDatasetApiKey();

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(1, actualResponse.size());
        assertEquals(apiKey.getId(), actualResponse.get(0).getId());
        assertEquals(apiKey.getToken(), actualResponse.get(0).getToken());
        assertEquals(apiKey.getCreatedAt(), actualResponse.get(0).getCreatedAt());

        // Verify WebClient interactions
        verify(webClientMock).get();
        verify(requestHeadersUriSpecMock).uri(ServerUriConstant.DATASETS + "/api-keys");
        verify(responseSpecMock).bodyToMono(DatasetApiKeyResult.class);
    }

    @Test
    public void testGetDatasetApiKeyNull() {

        // set up the response mock to return our expected response
        when(responseSpecMock.bodyToMono(DatasetApiKeyResult.class)).thenReturn(Mono.empty());

        // Execute the method
        List<DatasetApiKeyResponse> actualResponse = client.getDatasetApiKey();

        // Verify the result
        assertNull(actualResponse);

        // Verify WebClient interactions
        verify(webClientMock).get();
        verify(requestHeadersUriSpecMock).uri(ServerUriConstant.DATASETS + "/api-keys");
        verify(responseSpecMock).bodyToMono(DatasetApiKeyResult.class);
    }

    @Test
    public void testInitDatasetApiKey() {
        // Create expected response
        DatasetApiKeyResponse apiKey = new DatasetApiKeyResponse();
        apiKey.setId("api-key-123456");
        apiKey.setToken("sk-123456789");
        apiKey.setCreatedAt(1745546400000L);

        // Set up the response mock to return our expected response
        when(responseSpecMock.bodyToMono(DatasetApiKeyResponse.class)).thenReturn(Mono.just(apiKey));

        // Execute the method
        List<DatasetApiKeyResponse> actualResponse = client.initDatasetApiKey();

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(1, actualResponse.size());
        assertEquals(apiKey.getId(), actualResponse.get(0).getId());
        assertEquals(apiKey.getToken(), actualResponse.get(0).getToken());
        assertEquals(apiKey.getCreatedAt(), actualResponse.get(0).getCreatedAt());

        // Verify WebClient interactions
        verify(webClientMock).post();
        verify(requestBodyUriSpecMock).uri(ServerUriConstant.DATASETS + "/api-keys");
        verify(requestBodySpecMock).headers(any());
        verify(responseSpecMock).bodyToMono(DatasetApiKeyResponse.class);
    }

    @Test
    public void testInitDatasetApiKeyNull() {

        // Set up the response mock to return our expected response
        when(responseSpecMock.bodyToMono(DatasetApiKeyResponse.class)).thenReturn(Mono.empty());

        // Execute the method
        List<DatasetApiKeyResponse> actualResponse = client.initDatasetApiKey();

        // Verify the result
        assertNull(actualResponse);

        // Verify WebClient interactions
        verify(webClientMock).post();
        verify(requestBodyUriSpecMock).uri(ServerUriConstant.DATASETS + "/api-keys");
        verify(requestBodySpecMock).headers(any());
        verify(responseSpecMock).bodyToMono(DatasetApiKeyResponse.class);
    }

    @Test
    @DisplayName("Test chatConversations method")
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
        when(responseSpecMock.bodyToMono(any(org.springframework.core.ParameterizedTypeReference.class))).thenReturn(Mono.just(mockResponse));

        // Execute the method
        DifyPageResult<ChatConversationResponse> actualResponse = client.chatConversations(request);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(1, actualResponse.getTotal());
        assertEquals(1, actualResponse.getData().size());
        assertEquals("b9f66e5f-ba2b-4179-88d0-2d36c7e2050a", actualResponse.getData().get(0).getId());
        assertEquals("What are the specs of the iPhone 13 Pro Max?", actualResponse.getData().get(0).getName());

        // Verify WebClient interactions
        verify(webClientMock).get();
        verify(requestHeadersUriSpecMock).uri(any(Function.class));
        verify(responseSpecMock).bodyToMono(any(org.springframework.core.ParameterizedTypeReference.class));
    }

    @Test
    @DisplayName("Test dailyConversations method")
    public void testDailyConversations() {
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
        // Set up the response mock to return our expected response
        when(responseSpecMock.bodyToMono(any(org.springframework.core.ParameterizedTypeReference.class))).thenReturn(Mono.just(response));

        // Execute the method
        List<DailyConversationsResponse> actualResponse = client.dailyConversations(appId, start, end);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(1, actualResponse.size());
        assertEquals("2025-09-02", actualResponse.get(0).getDate());
        assertEquals(1, actualResponse.get(0).getConversationCount());

        // Verify WebClient interactions
        verify(webClientMock).get();
        verify(requestHeadersUriSpecMock).uri(any(Function.class));
        verify(responseSpecMock).bodyToMono(any(org.springframework.core.ParameterizedTypeReference.class));
    }

    @Test
    @DisplayName("Test dailyEndUsers method")
    public void testDailyEndUsers() {
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

        // Set up the response mock to return our expected response
        when(responseSpecMock.bodyToMono(any(org.springframework.core.ParameterizedTypeReference.class))).thenReturn(Mono.just(response));

        // Execute the method
        List<DailyEndUsersResponse> actualResponse = client.dailyEndUsers(appId, start, end);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(1, actualResponse.size());
        assertEquals("2025-09-02", actualResponse.get(0).getDate());
        assertEquals(1, actualResponse.get(0).getTerminalCount());

        // Verify WebClient interactions
        verify(webClientMock).get();
        verify(requestHeadersUriSpecMock).uri(any(Function.class));
        verify(responseSpecMock).bodyToMono(any(org.springframework.core.ParameterizedTypeReference.class));
    }

    @Test
    @DisplayName("Test averageSessionInteractions method")
    public void testAverageSessionInteractions() {
        // Prepare test data
        String appId = "08534c1a-4316-4cd3-806d-bbbca03f58aa";
        java.time.LocalDateTime start = java.time.LocalDateTime.of(2025, 10, 23, 0, 0);
        java.time.LocalDateTime end = java.time.LocalDateTime.of(2025, 10, 30, 23, 59);

        // Create mock average session interactions data
        AverageSessionInteractionsResponse dailyStat = new AverageSessionInteractionsResponse();
        dailyStat.setDate("2025-09-02");
        dailyStat.setInteractions(1.0);

        // Create mock response
        List<AverageSessionInteractionsResponse> mockResponse = List.of(dailyStat);
        AverageSessionInteractionsResultResponse response = new AverageSessionInteractionsResultResponse();
        response.setData(mockResponse);

        // Set up the response mock to return our expected response
        when(responseSpecMock.bodyToMono(any(org.springframework.core.ParameterizedTypeReference.class))).thenReturn(Mono.just(response));

        // Execute the method
        List<AverageSessionInteractionsResponse> actualResponse = client.averageSessionInteractions(appId, start, end);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(1, actualResponse.size());
        assertEquals("2025-09-02", actualResponse.get(0).getDate());
        assertEquals(1.0, actualResponse.get(0).getInteractions());

        // Verify WebClient interactions
        verify(webClientMock).get();
        verify(requestHeadersUriSpecMock).uri(any(Function.class));
        verify(responseSpecMock).bodyToMono(any(org.springframework.core.ParameterizedTypeReference.class));
    }

    @Test
    @DisplayName("Test tokensPerSecond method")
    public void testTokensPerSecond() {
        // Prepare test data
        String appId = "08534c1a-4316-4cd3-806d-bbbca03f58aa";
        java.time.LocalDateTime start = java.time.LocalDateTime.of(2025, 10, 23, 0, 0);
        java.time.LocalDateTime end = java.time.LocalDateTime.of(2025, 10, 30, 23, 59);

        // Create mock tokens per second data
        TokensPerSecondResponse dailyStat = new TokensPerSecondResponse();
        dailyStat.setDate("2025-09-02");
        dailyStat.setTps(30.8603);

        // Create mock response
        List<TokensPerSecondResponse> mockResponse = List.of(dailyStat);
        TokensPerSecondResultResponse response = new TokensPerSecondResultResponse();
        response.setData(mockResponse);

        // Set up the response mock to return our expected response
        when(responseSpecMock.bodyToMono(any(org.springframework.core.ParameterizedTypeReference.class))).thenReturn(Mono.just(response));

        // Execute the method
        List<TokensPerSecondResponse> actualResponse = client.tokensPerSecond(appId, start, end);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(1, actualResponse.size());
        assertEquals("2025-09-02", actualResponse.get(0).getDate());
        assertEquals(30.8603, actualResponse.get(0).getTps());

        // Verify WebClient interactions
        verify(webClientMock).get();
        verify(requestHeadersUriSpecMock).uri(any(Function.class));
        verify(responseSpecMock).bodyToMono(any(org.springframework.core.ParameterizedTypeReference.class));
    }

    @Test
    @DisplayName("Test userSatisfactionRate method")
    public void testUserSatisfactionRate() {
        // Prepare test data
        String appId = "08534c1a-4316-4cd3-806d-bbbca03f58aa";
        java.time.LocalDateTime start = java.time.LocalDateTime.of(2025, 10, 23, 0, 0);
        java.time.LocalDateTime end = java.time.LocalDateTime.of(2025, 10, 30, 23, 59);

        // Create mock user satisfaction rate data
        UserSatisfactionRateResponse dailyStat = new UserSatisfactionRateResponse();
        dailyStat.setDate("2025-09-02");
        dailyStat.setRate(1000.0);

        // Create mock response
        List<UserSatisfactionRateResponse> mockResponse = List.of(dailyStat);
        UserSatisfactionRateResultResponse response = new UserSatisfactionRateResultResponse();
        response.setData(mockResponse);

        // Set up the response mock to return our expected response
        when(responseSpecMock.bodyToMono(any(org.springframework.core.ParameterizedTypeReference.class))).thenReturn(Mono.just(response));

        // Execute the method
        List<UserSatisfactionRateResponse> actualResponse = client.userSatisfactionRate(appId, start, end);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(1, actualResponse.size());
        assertEquals("2025-09-02", actualResponse.get(0).getDate());
        assertEquals(1000.0, actualResponse.get(0).getRate());

        // Verify WebClient interactions
        verify(webClientMock).get();
        verify(requestHeadersUriSpecMock).uri(any(Function.class));
        verify(responseSpecMock).bodyToMono(any(org.springframework.core.ParameterizedTypeReference.class));
    }

    @Test
    @DisplayName("Test tokenCosts method")
    public void testTokenCosts() {
        // Prepare test data
        String appId = "08534c1a-4316-4cd3-806d-bbbca03f58aa";
        java.time.LocalDateTime start = java.time.LocalDateTime.of(2025, 10, 23, 0, 0);
        java.time.LocalDateTime end = java.time.LocalDateTime.of(2025, 10, 30, 23, 59);

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
        when(responseSpecMock.bodyToMono(any(org.springframework.core.ParameterizedTypeReference.class))).thenReturn(Mono.just(response));

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
        verify(webClientMock).get();
        verify(requestHeadersUriSpecMock).uri(any(Function.class));
        verify(responseSpecMock).bodyToMono(any(org.springframework.core.ParameterizedTypeReference.class));
    }
}
