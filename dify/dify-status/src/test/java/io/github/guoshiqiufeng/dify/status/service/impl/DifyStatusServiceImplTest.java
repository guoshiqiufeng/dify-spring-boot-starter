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

import static org.junit.jupiter.api.Assertions.*;

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
