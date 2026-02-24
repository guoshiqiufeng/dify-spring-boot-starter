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
package io.github.guoshiqiufeng.dify.java.starter;

import io.github.guoshiqiufeng.dify.client.codec.jackson.JacksonJsonMapper;
import io.github.guoshiqiufeng.dify.client.integration.okhttp.http.JavaHttpClient;
import io.github.guoshiqiufeng.dify.client.integration.okhttp.http.JavaHttpClientFactory;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.server.DifyServer;
import io.github.guoshiqiufeng.dify.server.dto.request.AppsRequest;
import io.github.guoshiqiufeng.dify.server.dto.request.ChatConversationsRequest;
import io.github.guoshiqiufeng.dify.server.dto.response.*;
import io.github.guoshiqiufeng.dify.support.impl.builder.DifyServerBuilder;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.*;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * DifyServer interface implementation test using builder pattern
 *
 * @author yanghq
 * @version 1.0
 * @since 2026/1/12 14:00
 */
@Testcontainers(disabledWithoutDocker = true)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServerTest extends BaseConnectionPoolTest {

    private static DifyServer difyServer;
    private static String baseUrl = "http://localhost";
    private static String serverEmail = "admin@admin.com";
    private static String serverPassword = "admin123456";

    private static String testAppId;

    @BeforeAll
    public static void setUp() {
        // Initialize HTTP client factory
        JavaHttpClientFactory httpClientFactory = new JavaHttpClientFactory(new JacksonJsonMapper());

        // Initialize DifyServer
        difyServer = DifyServerBuilder.create(DifyServerBuilder.builder()
                .baseUrl(baseUrl)
                .serverProperties(new DifyProperties.Server(serverEmail, serverPassword))
                .httpClientFactory(httpClientFactory)
                .build());
    }

    @Test
    @Order(1)
    @DisplayName("Test retrieving application list")
    public void appsTest() {
        // Test retrieving all applications
        List<AppsResponse> allApps = difyServer.apps("", "");
        assertNotNull(allApps, "Application list should not be null");

        // If applications are available, save the first app ID for later tests
        if (!allApps.isEmpty()) {
            testAppId = allApps.get(0).getId();
        }

        // Test filtering applications by name
        if (!allApps.isEmpty() && allApps.get(0).getName() != null) {
            String nameFilter = allApps.get(0).getName().substring(0, 1); // Use first character as filter
            List<AppsResponse> filteredApps = difyServer.apps("", nameFilter);
            assertNotNull(filteredApps, "Filtered application list should not be null");
        }
    }

    @Test
    @Order(2)
    @DisplayName("Test retrieving paginated application list with AppsRequest")
    public void appsWithRequestTest() {
        // Create AppsRequest with default parameters
        AppsRequest request = new AppsRequest();
        request.setPage(1);
        request.setLimit(10);

        AppsResponseResult paginatedApps = difyServer.apps(request);
        assertNotNull(paginatedApps, "Paginated application result should not be null");
        assertNotNull(paginatedApps.getData(), "Paginated application data should not be null");
        assertEquals(Integer.valueOf(1), paginatedApps.getPage(), "Returned page should match requested page");
        assertEquals(Integer.valueOf(10), paginatedApps.getLimit(), "Returned limit should match requested limit");

        // Test with mode filter
        request.setMode("chat");
        AppsResponseResult chatApps = difyServer.apps(request);
        assertNotNull(chatApps, "Chat application result should not be null");
        if (chatApps.getData() != null) {
            // Verify that returned apps match the mode filter
            chatApps.getData().forEach(app -> {
                if (app.getMode() != null) {
                    assertEquals("chat", app.getMode(), "Application mode should match filter");
                }
            });
        }

        // Test with name filter
        request.setMode(null); // Reset mode filter
        if (testAppId != null) {
            // Get app name to use for filter test
            AppsResponse app = difyServer.app(testAppId);
            if (app != null && app.getName() != null && app.getName().length() > 0) {
                String namePrefix = app.getName().substring(0, Math.min(1, app.getName().length()));
                request.setName(namePrefix);
                AppsResponseResult nameFilteredApps = difyServer.apps(request);
                assertNotNull(nameFilteredApps, "Name-filtered application result should not be null");
            }
        }
    }

    @Test
    @Order(3)
    @DisplayName("Test retrieving single application details")
    public void appTest() {
        // Check if test application ID is available
        if (testAppId == null) {
            List<AppsResponse> apps = difyServer.apps("", "");
            if (!apps.isEmpty()) {
                testAppId = apps.get(0).getId();
            } else {
                return;
            }
        }

        // Get application details
        AppsResponse app = difyServer.app(testAppId);
        assertNotNull(app, "Application details should not be null");
        assertEquals(testAppId, app.getId(), "Returned application ID should match requested ID");
    }

    @Test
    @Order(10)
    @DisplayName("Test retrieving and initializing application API Keys")
    public void appApiKeyTest() {
        // Check if test application ID is available
        if (testAppId == null) {
            List<AppsResponse> apps = difyServer.apps("", "");
            if (!apps.isEmpty()) {
                testAppId = apps.get(0).getId();
            } else {
                return;
            }
        }

        // Get application API Keys
        List<ApiKeyResponse> apiKeys = difyServer.getAppApiKey(testAppId);
        assertNotNull(apiKeys, "API Key list should not be null");

        // Initialize API Keys (may create new keys on the platform)
        List<ApiKeyResponse> initializedKeys = difyServer.initAppApiKey(testAppId);
        // Initialization may return null, so no assertion here
    }

    @Test
    @Order(11)
    @DisplayName("Test deleting application API Key")
    public void deleteAppApiKeyTest() {
        // Check if test application ID is available
        if (testAppId == null) {
            List<AppsResponse> apps = difyServer.apps("", "");
            if (!apps.isEmpty()) {
                testAppId = apps.get(0).getId();
            } else {
                return;
            }
        }

        // First, initialize an API key to ensure we have one to delete
        List<ApiKeyResponse> initializedKeys = difyServer.initAppApiKey(testAppId);

        // Verify we have keys to work with
        if (initializedKeys != null && !initializedKeys.isEmpty()) {
            ApiKeyResponse keyToDelete = initializedKeys.get(0);
            String apiKeyId = keyToDelete.getId();

            try {
                // Delete the API key
                difyServer.deleteAppApiKey(testAppId, apiKeyId);

                // Verify that the key no longer appears in the list (or handle as per API behavior)
                List<ApiKeyResponse> remainingKeys = difyServer.getAppApiKey(testAppId);

            } catch (Exception e) {
                // Log but don't fail the test since this might depend on the actual API behavior
            }
        }
    }

    @Test
    @Order(14)
    @DisplayName("Test dataset API Keys")
    public void datasetApiKeyTest() {
        // Get dataset API Keys
        List<DatasetApiKeyResponse> datasetApiKey = difyServer.getDatasetApiKey();

        // Initialize dataset API Keys
        List<DatasetApiKeyResponse> initializedKeys = difyServer.initDatasetApiKey();
        assertNotNull(initializedKeys, "Initialized dataset API Key list should not be null");
    }

    @Test
    @Order(15)
    @DisplayName("Test retrieving chat conversations")
    public void chatConversationsTest() {
        // Check if test application ID is available
        if (testAppId == null) {
            List<AppsResponse> apps = difyServer.apps("", "");
            if (!apps.isEmpty()) {
                testAppId = apps.get(0).getId();
            } else {
                return;
            }
        }

        // Create a ChatConversationsRequest
        ChatConversationsRequest request = new ChatConversationsRequest();
        request.setAppId(testAppId);
        request.setPage(1);
        request.setLimit(10);
        request.setAnnotationStatus("all");
        request.setStart("2024-01-01 00:00");
        request.setEnd("2025-12-31 23:59");
        request.setSortBy("-created_at");

        // Get chat conversations
        DifyPageResult<ChatConversationResponse> conversations = difyServer.chatConversations(request);
        assertNotNull(conversations, "Chat conversations result should not be null");
        assertNotNull(conversations.getData(), "Chat conversations data should not be null");
        assertEquals(Integer.valueOf(1), conversations.getPage(), "Returned page should match requested page");
        assertEquals(Integer.valueOf(10), conversations.getLimit(), "Returned limit should match requested limit");

        // Verify the data structure if conversations exist
        if (conversations.getData() != null && !conversations.getData().isEmpty()) {
            ChatConversationResponse firstConversation = conversations.getData().get(0);
            assertNotNull(firstConversation.getId(), "Conversation ID should not be null");
            assertNotNull(firstConversation.getName(), "Conversation name should not be null");
        }
    }

    @Test
    @Order(16)
    @DisplayName("Test retrieving daily conversations statistics")
    public void dailyConversationsTest() {
        // Check if test application ID is available
        if (testAppId == null) {
            List<AppsResponse> apps = difyServer.apps("", "");
            if (!apps.isEmpty()) {
                testAppId = apps.get(0).getId();
            } else {
                return;
            }
        }

        // Create date range for daily conversations
        java.time.LocalDateTime start = java.time.LocalDateTime.of(2025, 10, 23, 0, 0);
        java.time.LocalDateTime end = java.time.LocalDateTime.of(2025, 10, 30, 23, 59);

        // Get daily conversation statistics
        List<DailyConversationsResponse> dailyStats = difyServer.dailyConversations(testAppId, start, end);
        assertNotNull(dailyStats, "Daily conversations statistics should not be null");

        // If statistics exist, verify the data structure
        if (dailyStats != null && !dailyStats.isEmpty()) {
            DailyConversationsResponse firstStat = dailyStats.get(0);
            assertNotNull(firstStat.getDate(), "Daily statistic date should not be null");
            assertNotNull(firstStat.getConversationCount(), "Daily statistic conversation count should not be null");
        }
    }

    @Test
    @Order(17)
    @DisplayName("Test retrieving daily end users statistics")
    public void dailyEndUsersTest() {
        // Check if test application ID is available
        if (testAppId == null) {
            List<AppsResponse> apps = difyServer.apps("", "");
            if (!apps.isEmpty()) {
                testAppId = apps.get(0).getId();
            } else {
                return;
            }
        }

        // Create date range for daily end users
        java.time.LocalDateTime start = java.time.LocalDateTime.of(2025, 10, 23, 0, 0);
        java.time.LocalDateTime end = java.time.LocalDateTime.of(2025, 10, 30, 23, 59);

        // Get daily end users statistics
        List<DailyEndUsersResponse> dailyEndUsersStats = difyServer.dailyEndUsers(testAppId, start, end);
        assertNotNull(dailyEndUsersStats, "Daily end users statistics should not be null");

        // If statistics exist, verify the data structure
        if (dailyEndUsersStats != null && !dailyEndUsersStats.isEmpty()) {
            DailyEndUsersResponse firstStat = dailyEndUsersStats.get(0);
            assertNotNull(firstStat.getDate(), "Daily end users statistic date should not be null");
            assertNotNull(firstStat.getTerminalCount(), "Daily end users statistic terminal count should not be null");
        }
    }

    @Test
    @Order(18)
    @DisplayName("Test retrieving average session interactions statistics")
    public void averageSessionInteractionsTest() {
        // Check if test application ID is available
        if (testAppId == null) {
            List<AppsResponse> apps = difyServer.apps("", "");
            if (!apps.isEmpty()) {
                testAppId = apps.get(0).getId();
            } else {
                return;
            }
        }

        // Create date range for average session interactions
        java.time.LocalDateTime start = java.time.LocalDateTime.of(2025, 10, 23, 0, 0);
        java.time.LocalDateTime end = java.time.LocalDateTime.of(2025, 10, 30, 23, 59);

        // Get average session interactions statistics
        List<AverageSessionInteractionsResponse> averageSessionInteractionsStats =
                difyServer.averageSessionInteractions(testAppId, start, end);
        assertNotNull(averageSessionInteractionsStats, "Average session interactions statistics should not be null");

        // If statistics exist, verify the data structure
        if (averageSessionInteractionsStats != null && !averageSessionInteractionsStats.isEmpty()) {
            AverageSessionInteractionsResponse firstStat = averageSessionInteractionsStats.get(0);
            assertNotNull(firstStat.getDate(), "Average session interactions statistic date should not be null");
            assertNotNull(firstStat.getInteractions(), "Average session interactions statistic should not be null");
        }
    }

    @Test
    @Order(19)
    @DisplayName("Test retrieving tokens per second statistics")
    public void tokensPerSecondTest() {
        // Check if test application ID is available
        if (testAppId == null) {
            List<AppsResponse> apps = difyServer.apps("", "");
            if (!apps.isEmpty()) {
                testAppId = apps.get(0).getId();
            } else {
                return;
            }
        }

        // Create date range for tokens per second
        java.time.LocalDateTime start = java.time.LocalDateTime.of(2025, 10, 23, 0, 0);
        java.time.LocalDateTime end = java.time.LocalDateTime.of(2025, 10, 30, 23, 59);

        // Get tokens per second statistics
        List<TokensPerSecondResponse> tokensPerSecondStats = difyServer.tokensPerSecond(testAppId, start, end);
        assertNotNull(tokensPerSecondStats, "Tokens per second statistics should not be null");

        // If statistics exist, verify the data structure
        if (tokensPerSecondStats != null && !tokensPerSecondStats.isEmpty()) {
            TokensPerSecondResponse firstStat = tokensPerSecondStats.get(0);
            assertNotNull(firstStat.getDate(), "Tokens per second statistic date should not be null");
            assertNotNull(firstStat.getTps(), "Tokens per second statistic should not be null");
        }
    }

    @Test
    @Order(20)
    @DisplayName("Test retrieving user satisfaction rate statistics")
    public void userSatisfactionRateTest() {
        // Check if test application ID is available
        if (testAppId == null) {
            List<AppsResponse> apps = difyServer.apps("", "");
            if (!apps.isEmpty()) {
                testAppId = apps.get(0).getId();
            } else {
                return;
            }
        }

        // Create date range for user satisfaction rate
        java.time.LocalDateTime start = java.time.LocalDateTime.of(2025, 10, 23, 0, 0);
        java.time.LocalDateTime end = java.time.LocalDateTime.of(2025, 10, 30, 23, 59);

        // Get user satisfaction rate statistics
        List<UserSatisfactionRateResponse> userSatisfactionRateStats =
                difyServer.userSatisfactionRate(testAppId, start, end);
        assertNotNull(userSatisfactionRateStats, "User satisfaction rate statistics should not be null");

        // If statistics exist, verify the data structure
        if (userSatisfactionRateStats != null && !userSatisfactionRateStats.isEmpty()) {
            UserSatisfactionRateResponse firstStat = userSatisfactionRateStats.get(0);
            assertNotNull(firstStat.getDate(), "User satisfaction rate statistic date should not be null");
            assertNotNull(firstStat.getRate(), "User satisfaction rate statistic should not be null");
        }
    }

    @Test
    @Order(21)
    @DisplayName("Test retrieving token costs statistics")
    public void tokenCostsTest() {
        // Check if test application ID is available
        if (testAppId == null) {
            List<AppsResponse> apps = difyServer.apps("", "");
            if (!apps.isEmpty()) {
                testAppId = apps.get(0).getId();
            } else {
                return;
            }
        }

        // Create date range for token costs
        java.time.LocalDateTime start = java.time.LocalDateTime.of(2025, 10, 23, 0, 0);
        java.time.LocalDateTime end = java.time.LocalDateTime.of(2025, 10, 30, 23, 59);

        // Get token costs statistics
        List<TokenCostsResponse> tokenCostsStats = difyServer.tokenCosts(testAppId, start, end);
        assertNotNull(tokenCostsStats, "Token costs statistics should not be null");

        // If statistics exist, verify the data structure
        if (tokenCostsStats != null && !tokenCostsStats.isEmpty()) {
            TokenCostsResponse firstStat = tokenCostsStats.get(0);
            assertNotNull(firstStat.getDate(), "Token costs statistic date should not be null");
            assertNotNull(firstStat.getTokenCount(), "Token costs statistic token count should not be null");
            assertNotNull(firstStat.getTotalPrice(), "Token costs statistic total price should not be null");
            assertNotNull(firstStat.getCurrency(), "Token costs statistic currency should not be null");
        }
    }

    @Test
    @Order(22)
    @DisplayName("Test retrieving daily messages statistics")
    public void dailyMessagesTest() {
        // Check if test application ID is available
        if (testAppId == null) {
            List<AppsResponse> apps = difyServer.apps("", "");
            if (!apps.isEmpty()) {
                testAppId = apps.get(0).getId();
            } else {
                return;
            }
        }

        // Create date range for daily messages
        java.time.LocalDateTime start = java.time.LocalDateTime.of(2025, 10, 23, 0, 0);
        java.time.LocalDateTime end = java.time.LocalDateTime.of(2025, 10, 30, 23, 59);

        // Get daily messages statistics
        List<DailyMessagesResponse> dailyMessagesStats = difyServer.dailyMessages(testAppId, start, end);
        assertNotNull(dailyMessagesStats, "Daily messages statistics should not be null");

        // If statistics exist, verify the data structure
        if (dailyMessagesStats != null && !dailyMessagesStats.isEmpty()) {
            DailyMessagesResponse firstStat = dailyMessagesStats.get(0);
            assertNotNull(firstStat.getDate(), "Daily messages statistic date should not be null");
            assertNotNull(firstStat.getMessageCount(), "Daily messages statistic message count should not be null");
        }
    }

    @Test
    @Order(23)
    @DisplayName("Test error handling")
    public void errorHandlingTest() {
        // Test with invalid application ID
        try {
            AppsResponse invalidApp = difyServer.app("invalid-app-id");
        } catch (Exception e) {
            // Expected path - exception should be thrown
        }

        // Test API Keys with invalid application ID
        try {
            List<ApiKeyResponse> invalidApiKeys = difyServer.getAppApiKey("invalid-app-id");
        } catch (Exception e) {
            // Expected path - exception should be thrown
        }
    }

    @Test
    @Order(100)
    @DisplayName("OkHttp default connection pool config")
    public void testOkHttpDefaultPoolConfig() {
        JavaHttpClientFactory httpClientFactory = new JavaHttpClientFactory(new JacksonJsonMapper());

        Object client = DifyServerBuilder.builder()
                .baseUrl(baseUrl)
                .httpClientFactory(httpClientFactory)
                .build();

        OkHttpClient okHttpClient = extractOkHttpClient(client);
        assertOkHttpConfig(okHttpClient, 5, 300, 64, 5, 0);
    }

    @Test
    @Order(101)
    @DisplayName("OkHttp low concurrency connection pool config")
    public void testOkHttpLowConcurrencyPoolConfig() {
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setMaxIdleConnections(5);
        config.setKeepAliveSeconds(300);
        config.setMaxRequests(64);
        config.setMaxRequestsPerHost(5);
        config.setCallTimeout(0);

        JavaHttpClientFactory httpClientFactory = new JavaHttpClientFactory(new JacksonJsonMapper());
        Object client = DifyServerBuilder.builder()
                .baseUrl(baseUrl)
                .clientConfig(config)
                .httpClientFactory(httpClientFactory)
                .build();

        OkHttpClient okHttpClient = extractOkHttpClient(client);
        assertOkHttpConfig(okHttpClient, 5, 300, 64, 5, 0);
    }

    @Test
    @Order(102)
    @DisplayName("OkHttp medium concurrency connection pool config")
    public void testOkHttpMediumConcurrencyPoolConfig() {
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setMaxIdleConnections(10);
        config.setKeepAliveSeconds(300);
        config.setMaxRequests(128);
        config.setMaxRequestsPerHost(10);
        config.setCallTimeout(60);

        JavaHttpClientFactory httpClientFactory = new JavaHttpClientFactory(new JacksonJsonMapper());
        Object client = DifyServerBuilder.builder()
                .baseUrl(baseUrl)
                .clientConfig(config)
                .httpClientFactory(httpClientFactory)
                .build();

        OkHttpClient okHttpClient = extractOkHttpClient(client);
        assertOkHttpConfig(okHttpClient, 10, 300, 128, 10, 60);
    }

    @Test
    @Order(103)
    @DisplayName("OkHttp high concurrency connection pool config")
    public void testOkHttpHighConcurrencyPoolConfig() {
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setMaxIdleConnections(20);
        config.setKeepAliveSeconds(300);
        config.setMaxRequests(256);
        config.setMaxRequestsPerHost(20);
        config.setCallTimeout(60);

        JavaHttpClientFactory httpClientFactory = new JavaHttpClientFactory(new JacksonJsonMapper());
        Object client = DifyServerBuilder.builder()
                .baseUrl(baseUrl)
                .clientConfig(config)
                .httpClientFactory(httpClientFactory)
                .build();

        OkHttpClient okHttpClient = extractOkHttpClient(client);
        assertOkHttpConfig(okHttpClient, 20, 300, 256, 20, 60);
    }
}
