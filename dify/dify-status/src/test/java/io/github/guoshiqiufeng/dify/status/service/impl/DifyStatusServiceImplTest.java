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
package io.github.guoshiqiufeng.dify.status.service.impl;

import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.status.checker.DifyChatStatusChecker;
import io.github.guoshiqiufeng.dify.status.checker.DifyDatasetStatusChecker;
import io.github.guoshiqiufeng.dify.status.checker.DifyServerStatusChecker;
import io.github.guoshiqiufeng.dify.status.checker.DifyWorkflowStatusChecker;
import io.github.guoshiqiufeng.dify.status.config.StatusCheckConfig;
import io.github.guoshiqiufeng.dify.status.dto.AggregatedStatusReport;
import io.github.guoshiqiufeng.dify.status.dto.ApiStatusResult;
import io.github.guoshiqiufeng.dify.status.dto.ClientStatusReport;
import io.github.guoshiqiufeng.dify.status.enums.ApiStatus;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * DifyStatusServiceImpl test
 *
 * @author yanghq
 * @version 1.7.0
 * @since 2025/12/19 15:00
 */
class DifyStatusServiceImplTest {

    private TestDifyChatStatusChecker chatChecker;
    private TestDifyDatasetStatusChecker datasetChecker;
    private TestDifyServerStatusChecker serverChecker;
    private TestDifyWorkflowStatusChecker workflowChecker;

    private DifyStatusServiceImpl statusService;

    private static final String TEST_API_KEY = "test-api-key";

    private static final DifyProperties.StatusConfig statusConfig = DifyProperties.StatusConfig.builder()
            .healthIndicatorEnabled(true)
            .apiKey(TEST_API_KEY)
            .build();

    @BeforeEach
    void setUp() {
        chatChecker = new TestDifyChatStatusChecker();
        datasetChecker = new TestDifyDatasetStatusChecker();
        serverChecker = new TestDifyServerStatusChecker();
        workflowChecker = new TestDifyWorkflowStatusChecker();

        statusService = new DifyStatusServiceImpl(
                chatChecker,
                datasetChecker,
                serverChecker,
                workflowChecker
        );
    }

    @Test
    void testCheckClientStatus_DifyChat() {
        ClientStatusReport mockReport = createMockClientReport("DifyChat", ApiStatus.NORMAL, 10, 10, 0);
        chatChecker.setReportToReturn(mockReport);

        ClientStatusReport report = statusService.checkClientStatus("DifyChat", "test-api-key");

        assertNotNull(report);
        assertEquals("DifyChat", report.getClientName());
        assertEquals(ApiStatus.NORMAL, report.getOverallStatus());
        assertTrue(chatChecker.wasCheckAllApisCalled());
    }

    @Test
    void testCheckClientStatus_DifyDataset() {
        ClientStatusReport mockReport = createMockClientReport("DifyDataset", ApiStatus.NORMAL, 5, 5, 0);
        datasetChecker.setReportToReturn(mockReport);

        ClientStatusReport report = statusService.checkClientStatus("DifyDataset", "test-api-key");

        assertNotNull(report);
        assertEquals("DifyDataset", report.getClientName());
        assertTrue(datasetChecker.wasCheckAllApisCalled());
    }

    @Test
    void testCheckClientStatus_DifyServer() {
        ClientStatusReport mockReport = createMockClientReport("DifyServer", ApiStatus.NORMAL, 2, 2, 0);
        serverChecker.setReportToReturn(mockReport);

        ClientStatusReport report = statusService.checkClientStatus("DifyServer", "test-api-key");

        assertNotNull(report);
        assertEquals("DifyServer", report.getClientName());
        assertTrue(serverChecker.wasCheckAllApisCalled());
    }

    @Test
    void testCheckClientStatus_DifyWorkflow() {
        ClientStatusReport mockReport = createMockClientReport("DifyWorkflow", ApiStatus.NORMAL, 1, 1, 0);
        workflowChecker.setReportToReturn(mockReport);

        ClientStatusReport report = statusService.checkClientStatus("DifyWorkflow", "test-api-key");

        assertNotNull(report);
        assertEquals("DifyWorkflow", report.getClientName());
        assertTrue(workflowChecker.wasCheckAllApisCalled());
    }

    @Test
    void testCheckClientStatus_UnknownClient() {
        assertThrows(IllegalArgumentException.class, () -> {
            statusService.checkClientStatus("UnknownClient", "test-api-key");
        });
    }

    @Test
    void testCheckAllClientsStatus_AllNormal() {
        chatChecker.setReportToReturn(createMockClientReport("DifyChat", ApiStatus.NORMAL, 10, 10, 0));
        datasetChecker.setReportToReturn(createMockClientReport("DifyDataset", ApiStatus.NORMAL, 5, 5, 0));
        serverChecker.setReportToReturn(createMockClientReport("DifyServer", ApiStatus.NORMAL, 2, 2, 0));
        workflowChecker.setReportToReturn(createMockClientReport("DifyWorkflow", ApiStatus.NORMAL, 1, 1, 0));

        AggregatedStatusReport report = statusService.checkAllClientsStatus(statusConfig);

        assertNotNull(report);
        assertEquals(ApiStatus.NORMAL, report.getOverallStatus());
        assertEquals(18, report.getTotalApis());
        assertEquals(18, report.getHealthyApis());
        assertEquals(0, report.getUnhealthyApis());
        assertEquals(4, report.getClientReports().size());
        assertNotNull(report.getClientSummary());
        assertEquals(4, report.getClientSummary().size());
    }

    @Test
    void testCheckAllClientsStatus_SomeErrors() {
        chatChecker.setReportToReturn(createMockClientReport("DifyChat", ApiStatus.CLIENT_ERROR, 10, 8, 2));
        datasetChecker.setReportToReturn(createMockClientReport("DifyDataset", ApiStatus.NORMAL, 5, 5, 0));
        serverChecker.setReportToReturn(createMockClientReport("DifyServer", ApiStatus.NORMAL, 2, 2, 0));
        workflowChecker.setReportToReturn(createMockClientReport("DifyWorkflow", ApiStatus.NORMAL, 1, 1, 0));

        AggregatedStatusReport report = statusService.checkAllClientsStatus(statusConfig);

        assertNotNull(report);
        assertNotEquals(ApiStatus.NORMAL, report.getOverallStatus());
        assertEquals(18, report.getTotalApis());
        assertEquals(16, report.getHealthyApis());
        assertEquals(2, report.getUnhealthyApis());
    }

    @Test
    void testCheckAllClientsStatus_AllErrors() {
        chatChecker.setReportToReturn(createMockClientReport("DifyChat", ApiStatus.SERVER_ERROR, 10, 0, 10));
        datasetChecker.setReportToReturn(createMockClientReport("DifyDataset", ApiStatus.SERVER_ERROR, 5, 0, 5));
        serverChecker.setReportToReturn(createMockClientReport("DifyServer", ApiStatus.SERVER_ERROR, 2, 0, 2));
        workflowChecker.setReportToReturn(createMockClientReport("DifyWorkflow", ApiStatus.SERVER_ERROR, 1, 0, 1));

        AggregatedStatusReport report = statusService.checkAllClientsStatus(statusConfig);

        assertNotNull(report);
        assertEquals(ApiStatus.SERVER_ERROR, report.getOverallStatus());
        assertEquals(18, report.getTotalApis());
        assertEquals(0, report.getHealthyApis());
        assertEquals(18, report.getUnhealthyApis());
    }

    @Test
    void testCheckStatus_WithParallelConfig() {
        StatusCheckConfig config = StatusCheckConfig.builder()
                .apiKey("test-api-key")
                .parallel(true)
                .build();

        chatChecker.setReportToReturn(createMockClientReport("DifyChat", ApiStatus.NORMAL, 10, 10, 0));
        datasetChecker.setReportToReturn(createMockClientReport("DifyDataset", ApiStatus.NORMAL, 5, 5, 0));
        serverChecker.setReportToReturn(createMockClientReport("DifyServer", ApiStatus.NORMAL, 2, 2, 0));
        workflowChecker.setReportToReturn(createMockClientReport("DifyWorkflow", ApiStatus.NORMAL, 1, 1, 0));

        AggregatedStatusReport report = statusService.checkStatus(config);

        assertNotNull(report);
        assertEquals(ApiStatus.NORMAL, report.getOverallStatus());
        assertTrue(chatChecker.wasCheckAllApisCalled());
        assertTrue(datasetChecker.wasCheckAllApisCalled());
        assertTrue(serverChecker.wasCheckAllApisCalled());
        assertTrue(workflowChecker.wasCheckAllApisCalled());
    }

    @Test
    void testCheckStatus_WithSequentialConfig() {
        StatusCheckConfig config = StatusCheckConfig.builder()
                .apiKey("test-api-key")
                .parallel(false)
                .build();

        chatChecker.setReportToReturn(createMockClientReport("DifyChat", ApiStatus.NORMAL, 10, 10, 0));
        datasetChecker.setReportToReturn(createMockClientReport("DifyDataset", ApiStatus.NORMAL, 5, 5, 0));
        serverChecker.setReportToReturn(createMockClientReport("DifyServer", ApiStatus.NORMAL, 2, 2, 0));
        workflowChecker.setReportToReturn(createMockClientReport("DifyWorkflow", ApiStatus.NORMAL, 1, 1, 0));

        AggregatedStatusReport report = statusService.checkStatus(config);

        assertNotNull(report);
        assertEquals(ApiStatus.NORMAL, report.getOverallStatus());
        assertTrue(chatChecker.wasCheckAllApisCalled());
        assertTrue(datasetChecker.wasCheckAllApisCalled());
        assertTrue(serverChecker.wasCheckAllApisCalled());
        assertTrue(workflowChecker.wasCheckAllApisCalled());
    }

    @Test
    void testClientSummary() {
        chatChecker.setReportToReturn(createMockClientReport("DifyChat", ApiStatus.NORMAL, 10, 10, 0));
        datasetChecker.setReportToReturn(createMockClientReport("DifyDataset", ApiStatus.CLIENT_ERROR, 5, 4, 1));
        serverChecker.setReportToReturn(createMockClientReport("DifyServer", ApiStatus.NORMAL, 2, 2, 0));
        workflowChecker.setReportToReturn(createMockClientReport("DifyWorkflow", ApiStatus.NORMAL, 1, 1, 0));

        AggregatedStatusReport report = statusService.checkAllClientsStatus(statusConfig);

        assertNotNull(report.getClientSummary());
        assertEquals(ApiStatus.NORMAL, report.getClientSummary().get("DifyChat"));
        assertEquals(ApiStatus.CLIENT_ERROR, report.getClientSummary().get("DifyDataset"));
        assertEquals(ApiStatus.NORMAL, report.getClientSummary().get("DifyServer"));
        assertEquals(ApiStatus.NORMAL, report.getClientSummary().get("DifyWorkflow"));
    }

    @Test
    void testCheckStatus_WithDuplicateClientNames() {
        // Test case: Multiple API keys produce the same clientName
        // This should not throw IllegalStateException anymore
        StatusCheckConfig config = StatusCheckConfig.builder()
                .chatApiKey(java.util.Arrays.asList("key1", "key2"))  // Two keys for same client
                .parallel(false)
                .build();

        // Create a custom checker that returns different statuses for different calls
        TestDifyChatStatusChecker multiCallChecker = new TestDifyChatStatusChecker() {
            private int callCount = 0;

            @Override
            public ClientStatusReport checkAllApis(String apiKey) {
                callCount++;
                ApiStatus status = (callCount == 1) ? ApiStatus.NORMAL : ApiStatus.CLIENT_ERROR;
                return createMockClientReport("DifyChat", status, 10, 10 - callCount, callCount);
            }
        };

        DifyStatusServiceImpl service = new DifyStatusServiceImpl(
                multiCallChecker,
                null,  // No dataset checker
                null,  // No server checker
                null   // No workflow checker
        );

        // This should not throw exception
        AggregatedStatusReport report = service.checkStatus(config);

        assertNotNull(report);
        assertNotNull(report.getClientSummary());

        // The more severe status (CLIENT_ERROR) should be kept
        assertEquals(ApiStatus.CLIENT_ERROR, report.getClientSummary().get("DifyChat"));
    }

    @Test
    void testCheckAllApis_WithException() {
        // Test that exceptions in checkStatus are handled gracefully
        TestDifyChatStatusChecker exceptionChecker = new TestDifyChatStatusChecker() {
            @Override
            public ApiStatusResult checkStatus(String methodName, String apiKey) {
                // Throw exception to test error handling in AbstractClientStatusChecker
                throw new RuntimeException("Simulated API check failure");
            }

            @Override
            protected String[] methodsToCheck() {
                return new String[]{"testMethod1", "testMethod2"};
            }

            @Override
            public ClientStatusReport checkAllApis(String apiKey) {
                // Call the template method to trigger exception handling
                return checkAllApisInternal(apiKey);
            }
        };

        ClientStatusReport report = exceptionChecker.checkAllApis("test-key");

        assertNotNull(report);
        assertEquals("DifyChat", report.getClientName());
        assertEquals(2, report.getTotalApis());
        assertEquals(0, report.getNormalApis());
        assertEquals(2, report.getErrorApis());
        assertEquals(ApiStatus.SERVER_ERROR, report.getOverallStatus());

        // Verify error results were created
        assertEquals(2, report.getApiStatuses().size());
        for (ApiStatusResult result : report.getApiStatuses()) {
            assertEquals(ApiStatus.UNKNOWN_ERROR, result.getStatus());
            assertNotNull(result.getErrorMessage());
            assertTrue(result.getErrorMessage().contains("Simulated API check failure"));
        }
    }

    @Test
    void testMergeClientStatus_SeverityOrder() {
        // Test that more severe statuses are preserved when merging
        StatusCheckConfig config = StatusCheckConfig.builder()
                .chatApiKey(java.util.Arrays.asList("key1", "key2", "key3"))
                .parallel(false)
                .build();

        // Create a custom checker that returns different statuses
        TestDifyChatStatusChecker multiStatusChecker = new TestDifyChatStatusChecker() {
            private int callCount = 0;

            @Override
            public ClientStatusReport checkAllApis(String apiKey) {
                callCount++;
                ApiStatus status;
                switch (callCount) {
                    case 1:
                        status = ApiStatus.NORMAL;
                        break;
                    case 2:
                        status = ApiStatus.CLIENT_ERROR;
                        break;
                    case 3:
                        status = ApiStatus.SERVER_ERROR;
                        break;
                    default:
                        status = ApiStatus.NORMAL;
                }
                return createMockClientReport("DifyChat", status, 10, 10 - callCount, callCount);
            }
        };

        DifyStatusServiceImpl service = new DifyStatusServiceImpl(
                multiStatusChecker,
                null,  // No dataset checker
                null,  // No server checker
                null   // No workflow checker
        );

        AggregatedStatusReport report = service.checkStatus(config);

        assertNotNull(report);
        assertNotNull(report.getClientSummary());

        // SERVER_ERROR is most severe, should be kept
        assertEquals(ApiStatus.SERVER_ERROR, report.getClientSummary().get("DifyChat"));
    }

    @Test
    void testShutdown() {
        // Test executor service shutdown
        statusService.shutdown();
        // Verify no exception is thrown
        // Note: We can't easily verify the executor is actually shut down without exposing it
    }

    @Test
    void testMergeClientStatus_WithUnknownStatus() {
        // Test merging when one or both statuses are not in the severity list
        // This tests the edge cases in mergeClientStatus method

        // We need to use reflection to test the private mergeClientStatus method
        // Or we can trigger it through duplicate client names with various statuses
        StatusCheckConfig config = StatusCheckConfig.builder()
                .chatApiKey(java.util.Arrays.asList("key1", "key2"))
                .parallel(false)
                .build();

        TestDifyChatStatusChecker checker = new TestDifyChatStatusChecker() {
            private int callCount = 0;

            @Override
            public ClientStatusReport checkAllApis(String apiKey) {
                callCount++;
                // Return different statuses to test merge logic
                ApiStatus status = (callCount == 1) ? ApiStatus.TIMEOUT : ApiStatus.UNAUTHORIZED_401;
                return createMockClientReport("DifyChat", status, 5, 0, 5);
            }
        };

        DifyStatusServiceImpl service = new DifyStatusServiceImpl(checker, null, null, null);
        AggregatedStatusReport report = service.checkStatus(config);

        assertNotNull(report);
        // TIMEOUT is more severe than UNAUTHORIZED_401
        assertEquals(ApiStatus.TIMEOUT, report.getClientSummary().get("DifyChat"));
    }

    @Test
    void testCheckAllClientsStatusByServer() {
        // Create mock DifyServer
        io.github.guoshiqiufeng.dify.server.DifyServer mockDifyServer =
            mock(io.github.guoshiqiufeng.dify.server.DifyServer.class);

        // Mock apps response
        io.github.guoshiqiufeng.dify.server.dto.response.AppsResponseResult appsResult =
            new io.github.guoshiqiufeng.dify.server.dto.response.AppsResponseResult();

        io.github.guoshiqiufeng.dify.server.dto.response.AppsResponse chatApp =
            new io.github.guoshiqiufeng.dify.server.dto.response.AppsResponse();
        chatApp.setId("chat-app-id");
        chatApp.setMode("chat");

        io.github.guoshiqiufeng.dify.server.dto.response.AppsResponse workflowApp =
            new io.github.guoshiqiufeng.dify.server.dto.response.AppsResponse();
        workflowApp.setId("workflow-app-id");
        workflowApp.setMode("workflow");

        appsResult.setData(java.util.Arrays.asList(chatApp, workflowApp));

        // Mock API key responses
        io.github.guoshiqiufeng.dify.server.dto.response.ApiKeyResponse chatApiKey =
            new io.github.guoshiqiufeng.dify.server.dto.response.ApiKeyResponse();
        chatApiKey.setToken("chat-api-key");

        io.github.guoshiqiufeng.dify.server.dto.response.ApiKeyResponse workflowApiKey =
            new io.github.guoshiqiufeng.dify.server.dto.response.ApiKeyResponse();
        workflowApiKey.setToken("workflow-api-key");

        io.github.guoshiqiufeng.dify.server.dto.response.DatasetApiKeyResponse datasetApiKey =
            new io.github.guoshiqiufeng.dify.server.dto.response.DatasetApiKeyResponse();
        datasetApiKey.setToken("dataset-api-key");

        // Setup mock behaviors
        when(mockDifyServer.apps(any())).thenReturn(appsResult);
        when(mockDifyServer.getAppApiKey("chat-app-id"))
            .thenReturn(java.util.Collections.singletonList(chatApiKey));
        when(mockDifyServer.getAppApiKey("workflow-app-id"))
            .thenReturn(java.util.Collections.singletonList(workflowApiKey));
        when(mockDifyServer.getDatasetApiKey())
            .thenReturn(java.util.Collections.singletonList(datasetApiKey));

        // Create a custom server checker with the mock DifyServer
        TestDifyServerStatusChecker customServerChecker = new TestDifyServerStatusChecker() {
            @Override
            public io.github.guoshiqiufeng.dify.server.DifyServer getDifyServer() {
                return mockDifyServer;
            }
        };
        customServerChecker.setReportToReturn(createMockClientReport("DifyServer", ApiStatus.NORMAL, 2, 2, 0));

        // Set up other checkers
        chatChecker.setReportToReturn(createMockClientReport("DifyChat", ApiStatus.NORMAL, 10, 10, 0));
        datasetChecker.setReportToReturn(createMockClientReport("DifyDataset", ApiStatus.NORMAL, 5, 5, 0));
        workflowChecker.setReportToReturn(createMockClientReport("DifyWorkflow", ApiStatus.NORMAL, 1, 1, 0));

        DifyStatusServiceImpl service = new DifyStatusServiceImpl(
                chatChecker,
                datasetChecker,
                customServerChecker,
                workflowChecker
        );

        AggregatedStatusReport report = service.checkAllClientsStatusByServer();

        assertNotNull(report);
        assertEquals(ApiStatus.NORMAL, report.getOverallStatus());
        assertTrue(report.getClientReports().size() >= 3); // At least chat, dataset, workflow
    }

    @Test
    void testCheckStatus_WithNullCheckers() {
        // Test when some checkers are null
        StatusCheckConfig config = StatusCheckConfig.builder()
                .apiKey("test-key")
                .parallel(false)
                .build();

        // Create service with only chat checker
        DifyStatusServiceImpl service = new DifyStatusServiceImpl(
                chatChecker,
                null,  // No dataset checker
                null,  // No server checker
                null   // No workflow checker
        );

        chatChecker.setReportToReturn(createMockClientReport("DifyChat", ApiStatus.NORMAL, 10, 10, 0));

        AggregatedStatusReport report = service.checkStatus(config);

        assertNotNull(report);
        assertEquals(1, report.getClientReports().size());
        assertEquals(ApiStatus.NORMAL, report.getOverallStatus());
    }

    // ========== Cache Integration Tests ==========

    @Test
    void testCheckStatus_WithCacheHit() {
        // Arrange - create mock cache service
        io.github.guoshiqiufeng.dify.status.cache.StatusCacheService mockCacheService =
                mock(io.github.guoshiqiufeng.dify.status.cache.StatusCacheService.class);

        AggregatedStatusReport cachedReport = createMockAggregatedReport(ApiStatus.NORMAL, 18, 18, 0);
        when(mockCacheService.getCachedAggregatedReport()).thenReturn(Optional.of(cachedReport));

        // Create service with cache
        DifyStatusServiceImpl service = new DifyStatusServiceImpl(
                chatChecker,
                datasetChecker,
                serverChecker,
                workflowChecker,
                null,
                mockCacheService
        );

        StatusCheckConfig config = StatusCheckConfig.builder()
                .apiKey("test-key")
                .useCache(true)
                .parallel(false)
                .build();

        // Act
        AggregatedStatusReport report = service.checkStatus(config);

        // Assert
        assertNotNull(report);
        assertEquals(cachedReport, report);
        assertEquals(ApiStatus.NORMAL, report.getOverallStatus());
        assertEquals(18, report.getTotalApis());

        // Verify cache was checked
        verify(mockCacheService, times(1)).getCachedAggregatedReport();

        // Verify no actual checks were performed (checkers should not be called)
        assertFalse(chatChecker.wasCheckAllApisCalled());
        assertFalse(datasetChecker.wasCheckAllApisCalled());
        assertFalse(serverChecker.wasCheckAllApisCalled());
        assertFalse(workflowChecker.wasCheckAllApisCalled());
    }

    @Test
    void testCheckStatus_WithCacheMiss() {
        // Arrange - create mock cache service
        io.github.guoshiqiufeng.dify.status.cache.StatusCacheService mockCacheService =
                mock(io.github.guoshiqiufeng.dify.status.cache.StatusCacheService.class);

        when(mockCacheService.getCachedAggregatedReport()).thenReturn(Optional.empty());

        // Set up checkers to return reports
        chatChecker.setReportToReturn(createMockClientReport("DifyChat", ApiStatus.NORMAL, 10, 10, 0));
        datasetChecker.setReportToReturn(createMockClientReport("DifyDataset", ApiStatus.NORMAL, 5, 5, 0));
        serverChecker.setReportToReturn(createMockClientReport("DifyServer", ApiStatus.NORMAL, 2, 2, 0));
        workflowChecker.setReportToReturn(createMockClientReport("DifyWorkflow", ApiStatus.NORMAL, 1, 1, 0));

        // Create service with cache
        DifyStatusServiceImpl service = new DifyStatusServiceImpl(
                chatChecker,
                datasetChecker,
                serverChecker,
                workflowChecker,
                null,
                mockCacheService
        );

        StatusCheckConfig config = StatusCheckConfig.builder()
                .apiKey("test-key")
                .useCache(true)
                .cacheTtlSeconds(120L)
                .parallel(false)
                .build();

        // Act
        AggregatedStatusReport report = service.checkStatus(config);

        // Assert
        assertNotNull(report);
        assertEquals(ApiStatus.NORMAL, report.getOverallStatus());
        assertEquals(18, report.getTotalApis());

        // Verify cache was checked
        verify(mockCacheService, times(1)).getCachedAggregatedReport();

        // Verify actual checks were performed
        assertTrue(chatChecker.wasCheckAllApisCalled());
        assertTrue(datasetChecker.wasCheckAllApisCalled());
        assertTrue(serverChecker.wasCheckAllApisCalled());
        assertTrue(workflowChecker.wasCheckAllApisCalled());

        // Verify result was cached with correct TTL
        verify(mockCacheService, times(1)).cacheAggregatedReport(any(AggregatedStatusReport.class), eq(120L));
    }

    @Test
    void testCheckStatus_WithCacheDisabled() {
        // Arrange - create mock cache service
        io.github.guoshiqiufeng.dify.status.cache.StatusCacheService mockCacheService =
                mock(io.github.guoshiqiufeng.dify.status.cache.StatusCacheService.class);

        // Set up checkers to return reports
        chatChecker.setReportToReturn(createMockClientReport("DifyChat", ApiStatus.NORMAL, 10, 10, 0));

        // Create service with cache
        DifyStatusServiceImpl service = new DifyStatusServiceImpl(
                chatChecker,
                null,
                null,
                null,
                null,
                mockCacheService
        );

        StatusCheckConfig config = StatusCheckConfig.builder()
                .apiKey("test-key")
                .useCache(false)  // Cache disabled
                .parallel(false)
                .build();

        // Act
        AggregatedStatusReport report = service.checkStatus(config);

        // Assert
        assertNotNull(report);
        assertEquals(ApiStatus.NORMAL, report.getOverallStatus());

        // Verify cache was NOT checked
        verify(mockCacheService, never()).getCachedAggregatedReport();

        // Verify actual checks were performed
        assertTrue(chatChecker.wasCheckAllApisCalled());

        // Verify result was NOT cached
        verify(mockCacheService, never()).cacheAggregatedReport(any(), anyLong());
    }

    @Test
    void testCheckStatus_WithNullCacheService() {
        // Arrange - no cache service
        chatChecker.setReportToReturn(createMockClientReport("DifyChat", ApiStatus.NORMAL, 10, 10, 0));

        // Create service without cache
        DifyStatusServiceImpl service = new DifyStatusServiceImpl(
                chatChecker,
                null,
                null,
                null,
                null,
                null  // No cache service
        );

        StatusCheckConfig config = StatusCheckConfig.builder()
                .apiKey("test-key")
                .useCache(true)  // Cache enabled but service is null
                .parallel(false)
                .build();

        // Act
        AggregatedStatusReport report = service.checkStatus(config);

        // Assert
        assertNotNull(report);
        assertEquals(ApiStatus.NORMAL, report.getOverallStatus());

        // Verify actual checks were performed (no cache available)
        assertTrue(chatChecker.wasCheckAllApisCalled());
    }

    @Test
    void testCheckStatus_WithDefaultCacheTtl() {
        // Arrange - create mock cache service
        io.github.guoshiqiufeng.dify.status.cache.StatusCacheService mockCacheService =
                mock(io.github.guoshiqiufeng.dify.status.cache.StatusCacheService.class);

        when(mockCacheService.getCachedAggregatedReport()).thenReturn(Optional.empty());

        chatChecker.setReportToReturn(createMockClientReport("DifyChat", ApiStatus.NORMAL, 10, 10, 0));

        // Create service with cache
        DifyStatusServiceImpl service = new DifyStatusServiceImpl(
                chatChecker,
                null,
                null,
                null,
                null,
                mockCacheService
        );

        StatusCheckConfig config = StatusCheckConfig.builder()
                .apiKey("test-key")
                .useCache(true)
                // No cacheTtlSeconds specified, should use default 60
                .parallel(false)
                .build();

        // Act
        service.checkStatus(config);

        // Assert - verify default TTL of 60 seconds was used
        verify(mockCacheService, times(1)).cacheAggregatedReport(any(AggregatedStatusReport.class), eq(60L));
    }

    @Test
    void testCheckStatus_CacheWithParallelExecution() {
        // Arrange - create mock cache service
        io.github.guoshiqiufeng.dify.status.cache.StatusCacheService mockCacheService =
                mock(io.github.guoshiqiufeng.dify.status.cache.StatusCacheService.class);

        when(mockCacheService.getCachedAggregatedReport()).thenReturn(Optional.empty());

        chatChecker.setReportToReturn(createMockClientReport("DifyChat", ApiStatus.NORMAL, 10, 10, 0));
        datasetChecker.setReportToReturn(createMockClientReport("DifyDataset", ApiStatus.NORMAL, 5, 5, 0));

        // Create service with cache
        DifyStatusServiceImpl service = new DifyStatusServiceImpl(
                chatChecker,
                datasetChecker,
                null,
                null,
                null,
                mockCacheService
        );

        StatusCheckConfig config = StatusCheckConfig.builder()
                .apiKey("test-key")
                .useCache(true)
                .cacheTtlSeconds(90L)
                .parallel(true)  // Parallel execution
                .build();

        // Act
        AggregatedStatusReport report = service.checkStatus(config);

        // Assert
        assertNotNull(report);
        assertEquals(ApiStatus.NORMAL, report.getOverallStatus());

        // Verify cache was checked and result was cached
        verify(mockCacheService, times(1)).getCachedAggregatedReport();
        verify(mockCacheService, times(1)).cacheAggregatedReport(any(AggregatedStatusReport.class), eq(90L));
    }

    @Test
    void testConstructorWithExecutorService() {
        // Test constructor: DifyStatusServiceImpl(checkers, executorService)
        // This covers line 79: this(chatChecker, datasetChecker, serverChecker, workflowChecker, executorService, null);
        java.util.concurrent.ExecutorService customExecutor = java.util.concurrent.Executors.newFixedThreadPool(2);

        chatChecker.setReportToReturn(createMockClientReport("DifyChat", ApiStatus.NORMAL, 10, 10, 0));

        DifyStatusServiceImpl service = new DifyStatusServiceImpl(
                chatChecker,
                null,
                null,
                null,
                customExecutor
        );

        StatusCheckConfig config = StatusCheckConfig.builder()
                .apiKey("test-key")
                .parallel(false)
                .build();

        AggregatedStatusReport report = service.checkStatus(config);

        assertNotNull(report);
        assertEquals(ApiStatus.NORMAL, report.getOverallStatus());

        // Clean up
        service.shutdown();
        customExecutor.shutdown();
    }

    @Test
    void testCheckStatus_WithUseCacheNull() {
        // Test when config.getUseCache() returns null
        // This covers the condition: if (cacheService != null && config.getUseCache() != null && config.getUseCache())
        io.github.guoshiqiufeng.dify.status.cache.StatusCacheService mockCacheService =
                mock(io.github.guoshiqiufeng.dify.status.cache.StatusCacheService.class);

        chatChecker.setReportToReturn(createMockClientReport("DifyChat", ApiStatus.NORMAL, 10, 10, 0));

        DifyStatusServiceImpl service = new DifyStatusServiceImpl(
                chatChecker,
                null,
                null,
                null,
                null,
                mockCacheService
        );

        StatusCheckConfig config = StatusCheckConfig.builder()
                .apiKey("test-key")
                .useCache(null)  // useCache is null
                .parallel(false)
                .build();

        // Act
        AggregatedStatusReport report = service.checkStatus(config);

        // Assert
        assertNotNull(report);
        assertEquals(ApiStatus.NORMAL, report.getOverallStatus());

        // Verify cache was NOT checked because useCache is null
        verify(mockCacheService, never()).getCachedAggregatedReport();
        verify(mockCacheService, never()).cacheAggregatedReport(any(), anyLong());
    }

    @Test
    void testCheckStatus_WithUseCacheFalseExplicit() {
        // Test when config.getUseCache() explicitly returns false
        io.github.guoshiqiufeng.dify.status.cache.StatusCacheService mockCacheService =
                mock(io.github.guoshiqiufeng.dify.status.cache.StatusCacheService.class);

        chatChecker.setReportToReturn(createMockClientReport("DifyChat", ApiStatus.NORMAL, 10, 10, 0));

        DifyStatusServiceImpl service = new DifyStatusServiceImpl(
                chatChecker,
                null,
                null,
                null,
                null,
                mockCacheService
        );

        StatusCheckConfig config = StatusCheckConfig.builder()
                .apiKey("test-key")
                .useCache(false)  // useCache is explicitly false
                .parallel(false)
                .build();

        // Act
        AggregatedStatusReport report = service.checkStatus(config);

        // Assert
        assertNotNull(report);
        assertEquals(ApiStatus.NORMAL, report.getOverallStatus());

        // Verify cache was NOT checked because useCache is false
        verify(mockCacheService, never()).getCachedAggregatedReport();
        verify(mockCacheService, never()).cacheAggregatedReport(any(), anyLong());
    }

    private AggregatedStatusReport createMockAggregatedReport(ApiStatus status, int totalApis, int healthyApis, int unhealthyApis) {
        return AggregatedStatusReport.builder()
                .overallStatus(status)
                .totalApis(totalApis)
                .healthyApis(healthyApis)
                .unhealthyApis(unhealthyApis)
                .reportTime(LocalDateTime.now())
                .clientReports(new ArrayList<>())
                .clientSummary(new HashMap<>())
                .build();
    }

    private ClientStatusReport createMockClientReport(String clientName, ApiStatus status,
                                                      int totalApis, int normalApis, int errorApis) {
        return ClientStatusReport.builder()
                .clientName(clientName)
                .overallStatus(status)
                .totalApis(totalApis)
                .normalApis(normalApis)
                .errorApis(errorApis)
                .reportTime(LocalDateTime.now())
                .apiStatuses(new ArrayList<>())
                .statusSummary(new HashMap<>())
                .build();
    }

    // Test stub classes
    private static class TestDifyChatStatusChecker extends DifyChatStatusChecker {
        @Setter
        private ClientStatusReport reportToReturn;
        private boolean checkAllApisCalled = false;

        public TestDifyChatStatusChecker() {
            super(null);
        }

        public boolean wasCheckAllApisCalled() {
            return checkAllApisCalled;
        }

        @Override
        public ClientStatusReport checkAllApis(String apiKey) {
            checkAllApisCalled = true;
            return reportToReturn;
        }

        @Override
        public ApiStatusResult checkStatus(String methodName, String apiKey) {
            return null;
        }
    }

    private static class TestDifyDatasetStatusChecker extends DifyDatasetStatusChecker {
        @Setter
        private ClientStatusReport reportToReturn;
        private boolean checkAllApisCalled = false;

        public TestDifyDatasetStatusChecker() {
            super(null);
        }

        public boolean wasCheckAllApisCalled() {
            return checkAllApisCalled;
        }

        @Override
        public ClientStatusReport checkAllApis(String apiKey) {
            checkAllApisCalled = true;
            return reportToReturn;
        }

        @Override
        public ApiStatusResult checkStatus(String methodName, String apiKey) {
            return null;
        }
    }

    private static class TestDifyServerStatusChecker extends DifyServerStatusChecker {
        @Setter
        private ClientStatusReport reportToReturn;
        private boolean checkAllApisCalled = false;

        public TestDifyServerStatusChecker() {
            super(null);
        }

        public boolean wasCheckAllApisCalled() {
            return checkAllApisCalled;
        }

        @Override
        public ClientStatusReport checkAllApis() {
            checkAllApisCalled = true;
            return reportToReturn;
        }

        @Override
        public ApiStatusResult checkStatus(String methodName, String apiKey) {
            return null;
        }
    }

    private static class TestDifyWorkflowStatusChecker extends DifyWorkflowStatusChecker {
        @Setter
        private ClientStatusReport reportToReturn;
        private boolean checkAllApisCalled = false;

        public TestDifyWorkflowStatusChecker() {
            super(null);
        }

        public boolean wasCheckAllApisCalled() {
            return checkAllApisCalled;
        }

        @Override
        public ClientStatusReport checkAllApis(String apiKey) {
            checkAllApisCalled = true;
            return reportToReturn;
        }

        @Override
        public ApiStatusResult checkStatus(String methodName, String apiKey) {
            return null;
        }
    }
}
