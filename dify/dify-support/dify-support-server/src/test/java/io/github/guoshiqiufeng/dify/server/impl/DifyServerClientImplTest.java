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
package io.github.guoshiqiufeng.dify.server.impl;

import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.server.client.DifyServerClient;
import io.github.guoshiqiufeng.dify.server.dto.request.ChatConversationsRequest;
import io.github.guoshiqiufeng.dify.server.dto.response.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link DifyServerClientImpl}.
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/23 15:02
 */
class DifyServerClientImplTest {


    private DifyServerClient difyServerClient;

    private DifyServerClientImpl difyServerClientImpl;

    @BeforeEach
    void setUp() {
        difyServerClient = mock(DifyServerClient.class);
        difyServerClientImpl = new DifyServerClientImpl(difyServerClient);
    }

    @Test
    void testApps() {
        // Arrange
        String mode = "chat";
        String name = "test-app";

        AppsResponse app1 = new AppsResponse();
        app1.setId("app-123");
        app1.setName("Test App 1");

        AppsResponse app2 = new AppsResponse();
        app2.setId("app-456");
        app2.setName("Test App 2");

        List<AppsResponse> expectedApps = Arrays.asList(app1, app2);

        when(difyServerClient.apps(mode, name)).thenReturn(expectedApps);

        // Act
        List<AppsResponse> actualApps = difyServerClientImpl.apps(mode, name);

        // Assert
        assertNotNull(actualApps);
        assertEquals(expectedApps.size(), actualApps.size());
        assertEquals(expectedApps.get(0).getId(), actualApps.get(0).getId());
        assertEquals(expectedApps.get(1).getName(), actualApps.get(1).getName());
        verify(difyServerClient, times(1)).apps(mode, name);
    }

    @Test
    void testApp() {
        // Arrange
        String appId = "app-123";

        AppsResponse expectedApp = new AppsResponse();
        expectedApp.setId(appId);
        expectedApp.setName("Test App");

        when(difyServerClient.app(appId)).thenReturn(expectedApp);

        // Act
        AppsResponse actualApp = difyServerClientImpl.app(appId);

        // Assert
        assertNotNull(actualApp);
        assertEquals(expectedApp.getId(), actualApp.getId());
        assertEquals(expectedApp.getName(), actualApp.getName());
        verify(difyServerClient, times(1)).app(appId);
    }

    @Test
    void testGetAppApiKey() {
        // Arrange
        String appId = "app-123";

        ApiKeyResponse apiKey1 = new ApiKeyResponse();
        apiKey1.setId("key-123");
        apiKey1.setToken("test-api-key-1");

        ApiKeyResponse apiKey2 = new ApiKeyResponse();
        apiKey2.setId("key-456");
        apiKey2.setToken("test-api-key-2");

        List<ApiKeyResponse> expectedApiKeys = Arrays.asList(apiKey1, apiKey2);

        when(difyServerClient.getAppApiKey(appId)).thenReturn(expectedApiKeys);

        // Act
        List<ApiKeyResponse> actualApiKeys = difyServerClientImpl.getAppApiKey(appId);

        // Assert
        assertNotNull(actualApiKeys);
        assertEquals(expectedApiKeys.size(), actualApiKeys.size());
        assertEquals(expectedApiKeys.get(0).getId(), actualApiKeys.get(0).getId());
        assertEquals(expectedApiKeys.get(1).getToken(), actualApiKeys.get(1).getToken());
        verify(difyServerClient, times(1)).getAppApiKey(appId);
    }

    @Test
    void testInitAppApiKey() {
        // Arrange
        String appId = "app-123";

        ApiKeyResponse apiKey = new ApiKeyResponse();
        apiKey.setId("key-123");
        apiKey.setToken("new-api-key");

        List<ApiKeyResponse> expectedApiKeys = List.of(apiKey);

        when(difyServerClient.initAppApiKey(appId)).thenReturn(expectedApiKeys);

        // Act
        List<ApiKeyResponse> actualApiKeys = difyServerClientImpl.initAppApiKey(appId);

        // Assert
        assertNotNull(actualApiKeys);
        assertEquals(expectedApiKeys.size(), actualApiKeys.size());
        assertEquals(expectedApiKeys.get(0).getId(), actualApiKeys.get(0).getId());
        assertEquals(expectedApiKeys.get(0).getToken(), actualApiKeys.get(0).getToken());
        verify(difyServerClient, times(1)).initAppApiKey(appId);
    }

    @Test
    void testGetDatasetApiKey() {
        // Arrange
        DatasetApiKeyResponse datasetApiKey1 = new DatasetApiKeyResponse();
        datasetApiKey1.setId("dkey-123");
        datasetApiKey1.setToken("dataset-api-key-1");

        DatasetApiKeyResponse datasetApiKey2 = new DatasetApiKeyResponse();
        datasetApiKey2.setId("dkey-456");
        datasetApiKey2.setToken("dataset-api-key-2");

        List<DatasetApiKeyResponse> expectedDatasetApiKeys = Arrays.asList(datasetApiKey1, datasetApiKey2);

        when(difyServerClient.getDatasetApiKey()).thenReturn(expectedDatasetApiKeys);

        // Act
        List<DatasetApiKeyResponse> actualDatasetApiKeys = difyServerClientImpl.getDatasetApiKey();

        // Assert
        assertNotNull(actualDatasetApiKeys);
        assertEquals(expectedDatasetApiKeys.size(), actualDatasetApiKeys.size());
        assertEquals(expectedDatasetApiKeys.get(0).getId(), actualDatasetApiKeys.get(0).getId());
        assertEquals(expectedDatasetApiKeys.get(1).getToken(), actualDatasetApiKeys.get(1).getToken());
        verify(difyServerClient, times(1)).getDatasetApiKey();
    }

    @Test
    void testInitDatasetApiKey() {
        // Arrange
        DatasetApiKeyResponse datasetApiKey = new DatasetApiKeyResponse();
        datasetApiKey.setId("dkey-123");
        datasetApiKey.setToken("new-dataset-api-key");

        List<DatasetApiKeyResponse> expectedDatasetApiKeys = List.of(datasetApiKey);

        when(difyServerClient.initDatasetApiKey()).thenReturn(expectedDatasetApiKeys);

        // Act
        List<DatasetApiKeyResponse> actualDatasetApiKeys = difyServerClientImpl.initDatasetApiKey();

        // Assert
        assertNotNull(actualDatasetApiKeys);
        assertEquals(expectedDatasetApiKeys.size(), actualDatasetApiKeys.size());
        assertEquals(expectedDatasetApiKeys.get(0).getId(), actualDatasetApiKeys.get(0).getId());
        assertEquals(expectedDatasetApiKeys.get(0).getToken(), actualDatasetApiKeys.get(0).getToken());
        verify(difyServerClient, times(1)).initDatasetApiKey();
    }

    @Test
    void testChatConversations() {
        // Arrange
        String appId = "app-123";

        ChatConversationsRequest request = new ChatConversationsRequest();
        request.setAppId(appId);
        request.setPage(1);
        request.setLimit(10);
        request.setAnnotationStatus("all");

        ChatConversationResponse conversation1 = new ChatConversationResponse();
        conversation1.setId("conv-123");
        conversation1.setName("Test Conversation 1");
        conversation1.setAnnotated(false);

        ChatConversationResponse conversation2 = new ChatConversationResponse();
        conversation2.setId("conv-456");
        conversation2.setName("Test Conversation 2");
        conversation2.setAnnotated(true);

        DifyPageResult<ChatConversationResponse> expectedConversations = new DifyPageResult<>();
        expectedConversations.setData(Arrays.asList(conversation1, conversation2));
        expectedConversations.setPage(1);
        expectedConversations.setLimit(10);
        expectedConversations.setTotal(2);
        expectedConversations.setHasMore(false);

        when(difyServerClient.chatConversations(request)).thenReturn(expectedConversations);

        // Act
        DifyPageResult<ChatConversationResponse> actualConversations = difyServerClientImpl.chatConversations(request);

        // Assert
        assertNotNull(actualConversations);
        assertEquals(expectedConversations.getTotal(), actualConversations.getTotal());
        assertEquals(expectedConversations.getData().size(), actualConversations.getData().size());
        assertEquals(expectedConversations.getData().get(0).getId(), actualConversations.getData().get(0).getId());
        assertEquals(expectedConversations.getData().get(1).getName(), actualConversations.getData().get(1).getName());
        verify(difyServerClient, times(1)).chatConversations(request);
    }

    @Test
    void testDailyConversations() {
        // Arrange
        String appId = "app-123";
        java.time.LocalDateTime start = java.time.LocalDateTime.of(2025, 10, 23, 0, 0);
        java.time.LocalDateTime end = java.time.LocalDateTime.of(2025, 10, 30, 23, 59);

        DailyConversationsResponse dailyStat1 =
            new DailyConversationsResponse();
        dailyStat1.setDate("2025-10-23");
        dailyStat1.setConversationCount(10);

        DailyConversationsResponse dailyStat2 =
            new DailyConversationsResponse();
        dailyStat2.setDate("2025-10-24");
        dailyStat2.setConversationCount(15);

        List<DailyConversationsResponse> expectedStats =
            Arrays.asList(dailyStat1, dailyStat2);

        when(difyServerClient.dailyConversations(appId, start, end)).thenReturn(expectedStats);

        // Act
        List<DailyConversationsResponse> actualStats =
            difyServerClientImpl.dailyConversations(appId, start, end);

        // Assert
        assertNotNull(actualStats);
        assertEquals(expectedStats.size(), actualStats.size());
        assertEquals(expectedStats.get(0).getDate(), actualStats.get(0).getDate());
        assertEquals(expectedStats.get(0).getConversationCount(), actualStats.get(0).getConversationCount());
        verify(difyServerClient, times(1)).dailyConversations(appId, start, end);
    }

    @Test
    void testDailyEndUsers() {
        // Arrange
        String appId = "app-123";
        java.time.LocalDateTime start = java.time.LocalDateTime.of(2025, 10, 23, 0, 0);
        java.time.LocalDateTime end = java.time.LocalDateTime.of(2025, 10, 30, 23, 59);

        DailyEndUsersResponse dailyStat1 =
            new DailyEndUsersResponse();
        dailyStat1.setDate("2025-10-23");
        dailyStat1.setTerminalCount(5);

        DailyEndUsersResponse dailyStat2 =
            new DailyEndUsersResponse();
        dailyStat2.setDate("2025-10-24");
        dailyStat2.setTerminalCount(8);

        List<DailyEndUsersResponse> expectedStats =
            Arrays.asList(dailyStat1, dailyStat2);

        when(difyServerClient.dailyEndUsers(appId, start, end)).thenReturn(expectedStats);

        // Act
        List<DailyEndUsersResponse> actualStats =
            difyServerClientImpl.dailyEndUsers(appId, start, end);

        // Assert
        assertNotNull(actualStats);
        assertEquals(expectedStats.size(), actualStats.size());
        assertEquals(expectedStats.get(0).getDate(), actualStats.get(0).getDate());
        assertEquals(expectedStats.get(0).getTerminalCount(), actualStats.get(0).getTerminalCount());
        verify(difyServerClient, times(1)).dailyEndUsers(appId, start, end);
    }

    @Test
    void testAverageSessionInteractions() {
        // Arrange
        String appId = "app-123";
        java.time.LocalDateTime start = java.time.LocalDateTime.of(2025, 10, 23, 0, 0);
        java.time.LocalDateTime end = java.time.LocalDateTime.of(2025, 10, 30, 23, 59);

        AverageSessionInteractionsResponse dailyStat1 =
            new AverageSessionInteractionsResponse();
        dailyStat1.setDate("2025-10-23");
        dailyStat1.setInteractions(2.5);

        AverageSessionInteractionsResponse dailyStat2 =
            new AverageSessionInteractionsResponse();
        dailyStat2.setDate("2025-10-24");
        dailyStat2.setInteractions(3.0);

        List<AverageSessionInteractionsResponse> expectedStats =
            Arrays.asList(dailyStat1, dailyStat2);

        when(difyServerClient.averageSessionInteractions(appId, start, end)).thenReturn(expectedStats);

        // Act
        List<AverageSessionInteractionsResponse> actualStats =
            difyServerClientImpl.averageSessionInteractions(appId, start, end);

        // Assert
        assertNotNull(actualStats);
        assertEquals(expectedStats.size(), actualStats.size());
        assertEquals(expectedStats.get(0).getDate(), actualStats.get(0).getDate());
        assertEquals(expectedStats.get(0).getInteractions(), actualStats.get(0).getInteractions());
        verify(difyServerClient, times(1)).averageSessionInteractions(appId, start, end);
    }

    @Test
    void testTokensPerSecond() {
        // Arrange
        String appId = "app-123";
        java.time.LocalDateTime start = java.time.LocalDateTime.of(2025, 10, 23, 0, 0);
        java.time.LocalDateTime end = java.time.LocalDateTime.of(2025, 10, 30, 23, 59);

        TokensPerSecondResponse dailyStat1 =
            new TokensPerSecondResponse();
        dailyStat1.setDate("2025-10-23");
        dailyStat1.setTps(30.8603);

        TokensPerSecondResponse dailyStat2 =
            new TokensPerSecondResponse();
        dailyStat2.setDate("2025-10-24");
        dailyStat2.setTps(45.12);

        List<TokensPerSecondResponse> expectedStats =
            Arrays.asList(dailyStat1, dailyStat2);

        when(difyServerClient.tokensPerSecond(appId, start, end)).thenReturn(expectedStats);

        // Act
        List<TokensPerSecondResponse> actualStats =
            difyServerClientImpl.tokensPerSecond(appId, start, end);

        // Assert
        assertNotNull(actualStats);
        assertEquals(expectedStats.size(), actualStats.size());
        assertEquals(expectedStats.get(0).getDate(), actualStats.get(0).getDate());
        assertEquals(expectedStats.get(0).getTps(), actualStats.get(0).getTps());
        verify(difyServerClient, times(1)).tokensPerSecond(appId, start, end);
    }

    @Test
    void testUserSatisfactionRate() {
        // Arrange
        String appId = "app-123";
        java.time.LocalDateTime start = java.time.LocalDateTime.of(2025, 10, 23, 0, 0);
        java.time.LocalDateTime end = java.time.LocalDateTime.of(2025, 10, 30, 23, 59);

        UserSatisfactionRateResponse dailyStat1 =
            new UserSatisfactionRateResponse();
        dailyStat1.setDate("2025-10-23");
        dailyStat1.setRate(95.5);

        UserSatisfactionRateResponse dailyStat2 =
            new UserSatisfactionRateResponse();
        dailyStat2.setDate("2025-10-24");
        dailyStat2.setRate(98.0);

        List<UserSatisfactionRateResponse> expectedStats =
            Arrays.asList(dailyStat1, dailyStat2);

        when(difyServerClient.userSatisfactionRate(appId, start, end)).thenReturn(expectedStats);

        // Act
        List<UserSatisfactionRateResponse> actualStats =
            difyServerClientImpl.userSatisfactionRate(appId, start, end);

        // Assert
        assertNotNull(actualStats);
        assertEquals(expectedStats.size(), actualStats.size());
        assertEquals(expectedStats.get(0).getDate(), actualStats.get(0).getDate());
        assertEquals(expectedStats.get(0).getRate(), actualStats.get(0).getRate());
        verify(difyServerClient, times(1)).userSatisfactionRate(appId, start, end);
    }

    @Test
    void testTokenCosts() {
        // Arrange
        String appId = "app-123";
        java.time.LocalDateTime start = java.time.LocalDateTime.of(2025, 10, 23, 0, 0);
        java.time.LocalDateTime end = java.time.LocalDateTime.of(2025, 10, 30, 23, 59);

        TokenCostsResponse dailyStat1 =
            new TokenCostsResponse();
        dailyStat1.setDate("2025-10-23");
        dailyStat1.setTokenCount(1000);
        dailyStat1.setTotalPrice("0.00123");
        dailyStat1.setCurrency("USD");

        TokenCostsResponse dailyStat2 =
            new TokenCostsResponse();
        dailyStat2.setDate("2025-10-24");
        dailyStat2.setTokenCount(2500);
        dailyStat2.setTotalPrice("0.003075");
        dailyStat2.setCurrency("USD");

        List<TokenCostsResponse> expectedStats =
            Arrays.asList(dailyStat1, dailyStat2);

        when(difyServerClient.tokenCosts(appId, start, end)).thenReturn(expectedStats);

        // Act
        List<TokenCostsResponse> actualStats =
            difyServerClientImpl.tokenCosts(appId, start, end);

        // Assert
        assertNotNull(actualStats);
        assertEquals(expectedStats.size(), actualStats.size());
        assertEquals(expectedStats.get(0).getDate(), actualStats.get(0).getDate());
        assertEquals(expectedStats.get(0).getTokenCount(), actualStats.get(0).getTokenCount());
        assertEquals(expectedStats.get(0).getTotalPrice(), actualStats.get(0).getTotalPrice());
        assertEquals(expectedStats.get(0).getCurrency(), actualStats.get(0).getCurrency());
        verify(difyServerClient, times(1)).tokenCosts(appId, start, end);
    }

    @Test
    void testDailyMessages() {
        // Arrange
        String appId = "app-123";
        java.time.LocalDateTime start = java.time.LocalDateTime.of(2025, 10, 23, 0, 0);
        java.time.LocalDateTime end = java.time.LocalDateTime.of(2025, 10, 30, 23, 59);

        DailyMessagesResponse dailyStat1 =
            new DailyMessagesResponse();
        dailyStat1.setDate("2025-10-23");
        dailyStat1.setMessageCount(15);

        DailyMessagesResponse dailyStat2 =
            new DailyMessagesResponse();
        dailyStat2.setDate("2025-10-24");
        dailyStat2.setMessageCount(22);

        List<DailyMessagesResponse> expectedStats =
            Arrays.asList(dailyStat1, dailyStat2);

        when(difyServerClient.dailyMessages(appId, start, end)).thenReturn(expectedStats);

        // Act
        List<DailyMessagesResponse> actualStats =
            difyServerClientImpl.dailyMessages(appId, start, end);

        // Assert
        assertNotNull(actualStats);
        assertEquals(expectedStats.size(), actualStats.size());
        assertEquals(expectedStats.get(0).getDate(), actualStats.get(0).getDate());
        assertEquals(expectedStats.get(0).getMessageCount(), actualStats.get(0).getMessageCount());
        verify(difyServerClient, times(1)).dailyMessages(appId, start, end);
    }

    @Test
    void testDeleteAppApiKey() {
        // Arrange
        String apiKeyId = "89f04b59-6906-4d32-a630-d2911d3b5fd8";
        String appId = "app-123";
        // Act
        difyServerClientImpl.deleteAppApiKey(apiKeyId, appId);

        // Assert
        verify(difyServerClient, times(1)).deleteAppApiKey(apiKeyId, appId);
    }


    @Test
    void testDeleteDatasetApiKey() {
        // Arrange
        String apiKeyId = "89f04b59-6906-4d32-a630-d2911d3b5fd8";

        // Act
        difyServerClientImpl.deleteDatasetApiKey(apiKeyId);

        // Assert
        verify(difyServerClient, times(1)).deleteDatasetApiKey(apiKeyId);
    }
}
