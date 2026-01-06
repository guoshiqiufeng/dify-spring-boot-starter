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
package io.github.guoshiqiufeng.dify.boot;

import cn.hutool.json.JSONUtil;
import io.github.guoshiqiufeng.dify.boot.base.BaseServerContainerTest;
import io.github.guoshiqiufeng.dify.client.spring6.dataset.DifyDatasetDefaultClient;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.dataset.DifyDataset;
import io.github.guoshiqiufeng.dify.dataset.client.DifyDatasetClient;
import io.github.guoshiqiufeng.dify.dataset.dto.request.DatasetCreateRequest;
import io.github.guoshiqiufeng.dify.dataset.dto.request.document.DocumentCreateByTextRequest;
import io.github.guoshiqiufeng.dify.dataset.dto.response.DatasetResponse;
import io.github.guoshiqiufeng.dify.dataset.dto.response.DocumentResponse;
import io.github.guoshiqiufeng.dify.dataset.dto.response.DocumentIndexingStatusResponse;
import io.github.guoshiqiufeng.dify.dataset.impl.DifyDatasetClientImpl;
import io.github.guoshiqiufeng.dify.server.DifyServer;
import io.github.guoshiqiufeng.dify.server.dto.request.AppsRequest;
import io.github.guoshiqiufeng.dify.server.dto.request.ChatConversationsRequest;
import io.github.guoshiqiufeng.dify.server.dto.request.DocumentRetryRequest;
import io.github.guoshiqiufeng.dify.server.dto.response.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * DifyServer interface implementation test
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/3/31 09:55
 */
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServerTest extends BaseServerContainerTest {

    private static final String API_KEY_CACHE_KEY = "dify:api:key";
    private static final Duration CACHE_EXPIRATION = Duration.ofHours(1);

    @Resource
    private DifyServer difyServer;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private DifyProperties difyProperties;

    protected DifyDataset difyDataset;

    private static String testAppId;
    private static String testDatasetId;
    private static String testDocumentId;

    @BeforeEach
    public void setUp() {
        // Initialize DifyDataset for dataset operations
        String apiKey = initializeApiKeyWithCache();

        DifyDatasetClient difyDatasetClient = new DifyDatasetDefaultClient(difyProperties.getUrl(),
                difyProperties.getClientConfig(),
                WebClient.builder().defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey));

        this.difyDataset = new DifyDatasetClientImpl(difyDatasetClient);
    }

    private String initializeApiKeyWithCache() {
        String cachedToken = stringRedisTemplate.opsForValue().get(API_KEY_CACHE_KEY);
        if (StringUtils.hasText(cachedToken)) {
            log.debug("Using cached API Key");
            return cachedToken;
        }

        synchronized (this) {
            cachedToken = stringRedisTemplate.opsForValue().get(API_KEY_CACHE_KEY);
            if (StringUtils.hasText(cachedToken)) {
                return cachedToken;
            }

            String newToken = fetchOrCreateApiKey();

            try {
                stringRedisTemplate.opsForValue().set(API_KEY_CACHE_KEY, newToken, CACHE_EXPIRATION);
                log.debug("API Key cached with expiration: {}", CACHE_EXPIRATION);
            } catch (Exception e) {
                log.error("Failed to cache API Key", e);
            }
            return newToken;
        }
    }

    /**
     * Initialize API Key
     */
    private String fetchOrCreateApiKey() {
        if (difyServer == null) {
            throw new IllegalStateException("DifyServer is not initialized");
        }

        List<DatasetApiKeyResponse> apiKeys = difyServer.getDatasetApiKey();
        if (apiKeys == null) {
            log.debug("No existing API keys found, creating new key");
            apiKeys = difyServer.initDatasetApiKey();
            if (CollectionUtils.isEmpty(apiKeys)) {
                throw new IllegalStateException("Failed to initialize API Key");
            }
            return apiKeys.getFirst().getToken();
        }

        return apiKeys.stream()
                .findFirst()
                .map(DatasetApiKeyResponse::getToken)
                .orElseGet(() -> {
                    List<DatasetApiKeyResponse> newKeys = difyServer.initDatasetApiKey();
                    if (CollectionUtils.isEmpty(newKeys)) {
                        throw new IllegalStateException("Failed to initialize API Key");
                    }
                    return newKeys.getFirst().getToken();
                });
    }

    @Test
    @Order(0)
    @DisplayName("Initialize test dataset and document for Server API tests")
    public void initializeTestDatasetAndDocument() {
        try {
            // Create test dataset
            DatasetCreateRequest createRequest = new DatasetCreateRequest();
            createRequest.setName("server-api-test-dataset");
            createRequest.setDescription("Test dataset for Server API integration tests");

            DatasetResponse datasetResponse = difyDataset.create(createRequest);
            assertNotNull(datasetResponse, "Dataset response should not be null");
            assertNotNull(datasetResponse.getId(), "Dataset ID should not be null");

            testDatasetId = datasetResponse.getId();
            log.info("Created test dataset with ID: {}", testDatasetId);

            // Create test document by text
            DocumentCreateByTextRequest documentRequest = new DocumentCreateByTextRequest();
            documentRequest.setDatasetId(testDatasetId);
            documentRequest.setName("test-document");
            documentRequest.setText("This is a test document for Server API integration tests. " +
                    "It contains some sample text for indexing and testing dataset-related server operations.");

            DocumentResponse documentResponse = difyDataset.createByText(documentRequest);
            assertNotNull(documentResponse, "Document response should not be null");
            assertNotNull(documentResponse.getDocument(), "Document object should not be null");
            assertNotNull(documentResponse.getDocument().getId(), "Document ID should not be null");

            testDocumentId = documentResponse.getDocument().getId();
            log.info("Created test document with ID: {}", testDocumentId);

            // Wait briefly for document to be processed
            try {
                Thread.sleep(2000); // Wait 2 seconds for initial processing
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        } catch (Exception e) {
            log.error("Failed to initialize test dataset and document: {} - {}",
                    e.getClass().getSimpleName(), e.getMessage());
            throw e; // Rethrow to fail the test
        }
    }

    @Test
    @Order(1)
    @DisplayName("Test retrieving application list")
    public void appsTest() {
        // Test retrieving all applications
        List<AppsResponse> allApps = difyServer.apps("", "");
        log.debug("All applications: {}", JSONUtil.toJsonStr(allApps));
        assertNotNull(allApps, "Application list should not be null");

        // If applications are available, save the first app ID for later tests
        if (!allApps.isEmpty()) {
            testAppId = allApps.getFirst().getId();
            log.debug("Retrieved test application ID: {}", testAppId);
        }

        // Test filtering applications by name
        if (!allApps.isEmpty() && allApps.getFirst().getName() != null) {
            String nameFilter = allApps.getFirst().getName().substring(0, 1); // Use first character as filter
            List<AppsResponse> filteredApps = difyServer.apps("", nameFilter);
            log.debug("Applications filtered by name '{}': {}", nameFilter, JSONUtil.toJsonStr(filteredApps));
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
        log.debug("Paginated applications: {}", JSONUtil.toJsonStr(paginatedApps));
        assertNotNull(paginatedApps, "Paginated application result should not be null");
        assertNotNull(paginatedApps.getData(), "Paginated application data should not be null");
        assertEquals(Integer.valueOf(1), paginatedApps.getPage(), "Returned page should match requested page");
        assertEquals(Integer.valueOf(10), paginatedApps.getLimit(), "Returned limit should match requested limit");

        // Test with mode filter
        request.setMode("chat");
        AppsResponseResult chatApps = difyServer.apps(request);
        log.debug("Chat applications: {}", JSONUtil.toJsonStr(chatApps));
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
                log.debug("Applications filtered by name '{}': {}", namePrefix, JSONUtil.toJsonStr(nameFilteredApps));
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
                testAppId = apps.getFirst().getId();
            } else {
                log.warn("No applications available, skipping single application test");
                return;
            }
        }

        // Get application details
        AppsResponse app = difyServer.app(testAppId);
        log.debug("Application details: {}", JSONUtil.toJsonStr(app));
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
                testAppId = apps.getFirst().getId();
            } else {
                log.warn("No applications available, skipping API Key test");
                return;
            }
        }

        // Get application API Keys
        List<ApiKeyResponse> apiKeys = difyServer.getAppApiKey(testAppId);
        log.debug("Application API Keys: {}", JSONUtil.toJsonStr(apiKeys));
        assertNotNull(apiKeys, "API Key list should not be null");

        // Initialize API Keys (may create new keys on the platform)
        List<ApiKeyResponse> initializedKeys = difyServer.initAppApiKey(testAppId);
        log.debug("Initialized API Keys: {}", JSONUtil.toJsonStr(initializedKeys));
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
                testAppId = apps.getFirst().getId();
            } else {
                log.warn("No applications available, skipping delete API Key test");
                return;
            }
        }

        // First, initialize an API key to ensure we have one to delete
        List<ApiKeyResponse> initializedKeys = difyServer.initAppApiKey(testAppId);
        log.debug("Initialized API Keys for deletion test: {}", JSONUtil.toJsonStr(initializedKeys));
        
        // Verify we have keys to work with
        if (initializedKeys != null && !initializedKeys.isEmpty()) {
            ApiKeyResponse keyToDelete = initializedKeys.getFirst();
            String apiKeyId = keyToDelete.getId();
            log.debug("Attempting to delete API Key with ID: {}", apiKeyId);

            try {
                // Delete the API key
                difyServer.deleteAppApiKey(testAppId, apiKeyId);
                log.info("Successfully deleted API Key with ID: {}", apiKeyId);

                // Verify that the key no longer appears in the list (or handle as per API behavior)
                List<ApiKeyResponse> remainingKeys = difyServer.getAppApiKey(testAppId);
                log.debug("Remaining API Keys after deletion: {}", JSONUtil.toJsonStr(remainingKeys));

            } catch (Exception e) {
                log.warn("Exception occurred during API Key deletion: {} - {}", 
                        e.getClass().getSimpleName(), e.getMessage());
                // Log but don't fail the test since this might depend on the actual API behavior
            }
        } else {
            log.warn("No API Keys available to delete, skipping deletion test");
        }
    }

    @Test
    @Order(14)
    @DisplayName("Test dataset API Keys")
    public void datasetApiKeyTest() {
        // Get dataset API Keys
        List<DatasetApiKeyResponse> datasetApiKey = difyServer.getDatasetApiKey();
        log.debug("Dataset API Keys: {}", JSONUtil.toJsonStr(datasetApiKey));

        // Initialize dataset API Keys
        List<DatasetApiKeyResponse> initializedKeys = difyServer.initDatasetApiKey();
        log.debug("Initialized dataset API Keys: {}", JSONUtil.toJsonStr(initializedKeys));
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
                testAppId = apps.getFirst().getId();
            } else {
                log.warn("No applications available, skipping chat conversations test");
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
        log.debug("Chat conversations: {}", JSONUtil.toJsonStr(conversations));
        assertNotNull(conversations, "Chat conversations result should not be null");
        assertNotNull(conversations.getData(), "Chat conversations data should not be null");
        assertEquals(Integer.valueOf(1), conversations.getPage(), "Returned page should match requested page");
        assertEquals(Integer.valueOf(10), conversations.getLimit(), "Returned limit should match requested limit");

        // Verify the data structure if conversations exist
        if (conversations.getData() != null && !conversations.getData().isEmpty()) {
            ChatConversationResponse firstConversation = conversations.getData().getFirst();
            log.debug("First conversation: {}", JSONUtil.toJsonStr(firstConversation));
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
                testAppId = apps.getFirst().getId();
            } else {
                log.warn("No applications available, skipping daily conversations test");
                return;
            }
        }

        // Create date range for daily conversations
        java.time.LocalDateTime start = java.time.LocalDateTime.of(2025, 10, 23, 0, 0);
        java.time.LocalDateTime end = java.time.LocalDateTime.of(2025, 10, 30, 23, 59);

        // Get daily conversation statistics
        List<io.github.guoshiqiufeng.dify.server.dto.response.DailyConversationsResponse> dailyStats =
                difyServer.dailyConversations(testAppId, start, end);
        log.debug("Daily conversations statistics: {}", JSONUtil.toJsonStr(dailyStats));
        assertNotNull(dailyStats, "Daily conversations statistics should not be null");

        // If statistics exist, verify the data structure
        if (dailyStats != null && !dailyStats.isEmpty()) {
            io.github.guoshiqiufeng.dify.server.dto.response.DailyConversationsResponse firstStat = dailyStats.get(0);
            log.debug("First daily statistic: {}", JSONUtil.toJsonStr(firstStat));
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
                testAppId = apps.getFirst().getId();
            } else {
                log.warn("No applications available, skipping daily end users test");
                return;
            }
        }

        // Create date range for daily end users
        java.time.LocalDateTime start = java.time.LocalDateTime.of(2025, 10, 23, 0, 0);
        java.time.LocalDateTime end = java.time.LocalDateTime.of(2025, 10, 30, 23, 59);

        // Get daily end users statistics
        List<io.github.guoshiqiufeng.dify.server.dto.response.DailyEndUsersResponse> dailyEndUsersStats =
                difyServer.dailyEndUsers(testAppId, start, end);
        log.debug("Daily end users statistics: {}", JSONUtil.toJsonStr(dailyEndUsersStats));
        assertNotNull(dailyEndUsersStats, "Daily end users statistics should not be null");

        // If statistics exist, verify the data structure
        if (dailyEndUsersStats != null && !dailyEndUsersStats.isEmpty()) {
            io.github.guoshiqiufeng.dify.server.dto.response.DailyEndUsersResponse firstStat = dailyEndUsersStats.get(0);
            log.debug("First daily end users statistic: {}", JSONUtil.toJsonStr(firstStat));
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
                testAppId = apps.getFirst().getId();
            } else {
                log.warn("No applications available, skipping average session interactions test");
                return;
            }
        }

        // Create date range for average session interactions
        java.time.LocalDateTime start = java.time.LocalDateTime.of(2025, 10, 23, 0, 0);
        java.time.LocalDateTime end = java.time.LocalDateTime.of(2025, 10, 30, 23, 59);

        // Get average session interactions statistics
        List<io.github.guoshiqiufeng.dify.server.dto.response.AverageSessionInteractionsResponse> averageSessionInteractionsStats =
                difyServer.averageSessionInteractions(testAppId, start, end);
        log.debug("Average session interactions statistics: {}", JSONUtil.toJsonStr(averageSessionInteractionsStats));
        assertNotNull(averageSessionInteractionsStats, "Average session interactions statistics should not be null");

        // If statistics exist, verify the data structure
        if (averageSessionInteractionsStats != null && !averageSessionInteractionsStats.isEmpty()) {
            io.github.guoshiqiufeng.dify.server.dto.response.AverageSessionInteractionsResponse firstStat = averageSessionInteractionsStats.get(0);
            log.debug("First average session interactions statistic: {}", JSONUtil.toJsonStr(firstStat));
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
                testAppId = apps.getFirst().getId();
            } else {
                log.warn("No applications available, skipping tokens per second test");
                return;
            }
        }

        // Create date range for tokens per second
        java.time.LocalDateTime start = java.time.LocalDateTime.of(2025, 10, 23, 0, 0);
        java.time.LocalDateTime end = java.time.LocalDateTime.of(2025, 10, 30, 23, 59);

        // Get tokens per second statistics
        List<io.github.guoshiqiufeng.dify.server.dto.response.TokensPerSecondResponse> tokensPerSecondStats =
                difyServer.tokensPerSecond(testAppId, start, end);
        log.debug("Tokens per second statistics: {}", JSONUtil.toJsonStr(tokensPerSecondStats));
        assertNotNull(tokensPerSecondStats, "Tokens per second statistics should not be null");

        // If statistics exist, verify the data structure
        if (tokensPerSecondStats != null && !tokensPerSecondStats.isEmpty()) {
            io.github.guoshiqiufeng.dify.server.dto.response.TokensPerSecondResponse firstStat = tokensPerSecondStats.get(0);
            log.debug("First tokens per second statistic: {}", JSONUtil.toJsonStr(firstStat));
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
                testAppId = apps.getFirst().getId();
            } else {
                log.warn("No applications available, skipping user satisfaction rate test");
                return;
            }
        }

        // Create date range for user satisfaction rate
        java.time.LocalDateTime start = java.time.LocalDateTime.of(2025, 10, 23, 0, 0);
        java.time.LocalDateTime end = java.time.LocalDateTime.of(2025, 10, 30, 23, 59);

        // Get user satisfaction rate statistics
        List<io.github.guoshiqiufeng.dify.server.dto.response.UserSatisfactionRateResponse> userSatisfactionRateStats =
                difyServer.userSatisfactionRate(testAppId, start, end);
        log.debug("User satisfaction rate statistics: {}", JSONUtil.toJsonStr(userSatisfactionRateStats));
        assertNotNull(userSatisfactionRateStats, "User satisfaction rate statistics should not be null");

        // If statistics exist, verify the data structure
        if (userSatisfactionRateStats != null && !userSatisfactionRateStats.isEmpty()) {
            io.github.guoshiqiufeng.dify.server.dto.response.UserSatisfactionRateResponse firstStat = userSatisfactionRateStats.get(0);
            log.debug("First user satisfaction rate statistic: {}", JSONUtil.toJsonStr(firstStat));
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
                testAppId = apps.getFirst().getId();
            } else {
                log.warn("No applications available, skipping token costs test");
                return;
            }
        }

        // Create date range for token costs
        java.time.LocalDateTime start = java.time.LocalDateTime.of(2025, 10, 23, 0, 0);
        java.time.LocalDateTime end = java.time.LocalDateTime.of(2025, 10, 30, 23, 59);

        // Get token costs statistics
        List<io.github.guoshiqiufeng.dify.server.dto.response.TokenCostsResponse> tokenCostsStats =
                difyServer.tokenCosts(testAppId, start, end);
        log.debug("Token costs statistics: {}", JSONUtil.toJsonStr(tokenCostsStats));
        assertNotNull(tokenCostsStats, "Token costs statistics should not be null");

        // If statistics exist, verify the data structure
        if (tokenCostsStats != null && !tokenCostsStats.isEmpty()) {
            io.github.guoshiqiufeng.dify.server.dto.response.TokenCostsResponse firstStat = tokenCostsStats.get(0);
            log.debug("First token costs statistic: {}", JSONUtil.toJsonStr(firstStat));
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
                testAppId = apps.getFirst().getId();
            } else {
                log.warn("No applications available, skipping daily messages test");
                return;
            }
        }

        // Create date range for daily messages
        java.time.LocalDateTime start = java.time.LocalDateTime.of(2025, 10, 23, 0, 0);
        java.time.LocalDateTime end = java.time.LocalDateTime.of(2025, 10, 30, 23, 59);

        // Get daily messages statistics
        List<io.github.guoshiqiufeng.dify.server.dto.response.DailyMessagesResponse> dailyMessagesStats =
                difyServer.dailyMessages(testAppId, start, end);
        log.debug("Daily messages statistics: {}", JSONUtil.toJsonStr(dailyMessagesStats));
        assertNotNull(dailyMessagesStats, "Daily messages statistics should not be null");

        // If statistics exist, verify the data structure
        if (dailyMessagesStats != null && !dailyMessagesStats.isEmpty()) {
            io.github.guoshiqiufeng.dify.server.dto.response.DailyMessagesResponse firstStat = dailyMessagesStats.get(0);
            log.debug("First daily messages statistic: {}", JSONUtil.toJsonStr(firstStat));
            assertNotNull(firstStat.getDate(), "Daily messages statistic date should not be null");
            assertNotNull(firstStat.getMessageCount(), "Daily messages statistic message count should not be null");
        }
    }

    @Test
    @Order(23)
    @DisplayName("Test retrieving dataset indexing status")
    public void getDatasetIndexingStatusTest() {
        assertNotNull(testDatasetId, "Dataset ID should be available from initialization test");

        try {
            // Get dataset indexing status
            DocumentIndexingStatusResponse indexingStatus = difyServer.getDatasetIndexingStatus(testDatasetId);
            log.debug("Dataset indexing status: {}", JSONUtil.toJsonStr(indexingStatus));
            assertNotNull(indexingStatus, "Dataset indexing status should not be null");

            // Verify the data structure
            if (indexingStatus.getData() != null && !indexingStatus.getData().isEmpty()) {
                log.debug("Dataset has {} documents", indexingStatus.getData().size());
                DocumentIndexingStatusResponse.ProcessingStatus firstDocument = indexingStatus.getData().getFirst();
                assertNotNull(firstDocument.getId(), "Document ID should not be null");
                log.debug("First document ID: {}, Status: {}", firstDocument.getId(), firstDocument.getIndexingStatus());
            }
        } catch (Exception e) {
            log.error("Exception occurred during dataset indexing status test: {} - {}",
                    e.getClass().getSimpleName(), e.getMessage());
            throw e; // Rethrow to fail the test
        }
    }

    @Test
    @Order(24)
    @DisplayName("Test retrieving document indexing status")
    public void getDocumentIndexingStatusTest() {
        assertNotNull(testDatasetId, "Dataset ID should be available from initialization test");
        assertNotNull(testDocumentId, "Document ID should be available from initialization test");

        try {
            // Get document indexing status
            DocumentIndexingStatusResponse.ProcessingStatus documentStatus =
                    difyServer.getDocumentIndexingStatus(testDatasetId, testDocumentId);
            log.debug("Document indexing status: {}", JSONUtil.toJsonStr(documentStatus));
            assertNotNull(documentStatus, "Document indexing status should not be null");

            // Verify the data structure
            assertNotNull(documentStatus.getId(), "Document ID should not be null");
            assertEquals(testDocumentId, documentStatus.getId(), "Document ID should match the test document");
            log.debug("Document ID: {}, Status: {}", documentStatus.getId(), documentStatus.getIndexingStatus());
        } catch (Exception e) {
            log.error("Exception occurred during document indexing status test: {} - {}",
                    e.getClass().getSimpleName(), e.getMessage());
            throw e; // Rethrow to fail the test
        }
    }

    @Test
    @Order(25)
    @DisplayName("Test retrieving dataset error documents")
    public void getDatasetErrorDocumentsTest() {
        assertNotNull(testDatasetId, "Dataset ID should be available from initialization test");

        try {
            // Get dataset error documents
            DatasetErrorDocumentsResponse errorDocuments = difyServer.getDatasetErrorDocuments(testDatasetId);
            log.debug("Dataset error documents: {}", JSONUtil.toJsonStr(errorDocuments));
            assertNotNull(errorDocuments, "Dataset error documents response should not be null");

            // Verify the data structure
            assertNotNull(errorDocuments.getTotal(), "Error documents total should not be null");
            log.debug("Total error documents: {}", errorDocuments.getTotal());

            // If there are error documents, log details
            if (errorDocuments.getData() != null && !errorDocuments.getData().isEmpty()) {
                log.debug("First error document: {}", JSONUtil.toJsonStr(errorDocuments.getData().getFirst()));
            } else {
                log.debug("No error documents found (this is expected for successful indexing)");
            }
        } catch (Exception e) {
            log.error("Exception occurred during dataset error documents test: {} - {}",
                    e.getClass().getSimpleName(), e.getMessage());
            throw e; // Rethrow to fail the test
        }
    }

    @Test
    @Order(26)
    @DisplayName("Test retrying document indexing")
    public void retryDocumentIndexingTest() {
        assertNotNull(testDatasetId, "Dataset ID should be available from initialization test");
        assertNotNull(testDocumentId, "Document ID should be available from initialization test");

        try {
            // Create retry request
            DocumentRetryRequest request = new DocumentRetryRequest();
            request.setDatasetId(testDatasetId);
            request.setDocumentIds(java.util.Collections.singletonList(testDocumentId));

            log.debug("Attempting to retry indexing for document: {} in dataset: {}", testDocumentId, testDatasetId);

            // Retry document indexing
            difyServer.retryDocumentIndexing(request);
            log.info("Successfully triggered retry for document indexing");

            // Note: The actual indexing happens asynchronously, so we can't verify completion immediately
            // In a real test scenario, you may want to wait and check status again
        } catch (Exception e) {
            // Some exceptions may be expected if the document is not in error state
            log.warn("Exception occurred during retry document indexing test: {} - {}",
                    e.getClass().getSimpleName(), e.getMessage());
            log.info("Note: Retry may fail if document is not in error state, which is expected behavior");
            // Don't rethrow - this is acceptable behavior
        }
    }

    @Test
    @Order(99)
    @DisplayName("Test error handling")
    public void errorHandlingTest() {
        // Test with invalid application ID
        try {
            AppsResponse invalidApp = difyServer.app("invalid-app-id");
            log.info("No exception thrown for invalid app ID, received response: {}",
                    invalidApp == null ? "null" : JSONUtil.toJsonStr(invalidApp));
        } catch (Exception e) {
            // Expected path - exception should be thrown
            log.info("Expected exception when retrieving app with invalid ID: {} - {}",
                    e.getClass().getSimpleName(), e.getMessage());
        }

        // Test API Keys with invalid application ID
        try {
            List<ApiKeyResponse> invalidApiKeys = difyServer.getAppApiKey("invalid-app-id");
            log.info("No exception thrown for invalid API Key ID, received response size: {}",
                    invalidApiKeys == null ? "null" : invalidApiKeys.size());
        } catch (Exception e) {
            // Expected path - exception should be thrown
            log.info("Expected exception when retrieving API keys with invalid ID: {} - {}",
                    e.getClass().getSimpleName(), e.getMessage());
        }
    }
}
