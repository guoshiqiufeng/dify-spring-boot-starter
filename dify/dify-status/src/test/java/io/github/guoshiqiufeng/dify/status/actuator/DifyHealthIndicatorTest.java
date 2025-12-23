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
package io.github.guoshiqiufeng.dify.status.actuator;

import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.status.dto.AggregatedStatusReport;
import io.github.guoshiqiufeng.dify.status.enums.ApiStatus;
import io.github.guoshiqiufeng.dify.status.service.DifyStatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * DifyHealthIndicator test
 *
 * @author yanghq
 * @version 1.7.0
 * @since 2025/12/19 15:00
 */
class DifyHealthIndicatorTest {

    @Mock
    private DifyStatusService statusService;

    private DifyHealthIndicator healthIndicator;

    private static final String TEST_API_KEY = "test-api-key";

    private static final DifyProperties.StatusConfig statusConfig = DifyProperties.StatusConfig.builder()
            .healthIndicatorEnabled(true)
            .healthIndicatorInitByServer(false)
            .apiKey(TEST_API_KEY)
            .build();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        healthIndicator = new DifyHealthIndicator(statusService, statusConfig);
    }

    @Test
    void testHealth_AllNormal() {
        Map<String, ApiStatus> clientSummary = new HashMap<>();
        clientSummary.put("DifyChat", ApiStatus.NORMAL);
        clientSummary.put("DifyDataset", ApiStatus.NORMAL);
        clientSummary.put("DifyServer", ApiStatus.NORMAL);
        clientSummary.put("DifyWorkflow", ApiStatus.NORMAL);

        AggregatedStatusReport report = AggregatedStatusReport.builder()
                .overallStatus(ApiStatus.NORMAL)
                .totalApis(18)
                .healthyApis(18)
                .unhealthyApis(0)
                .reportTime(LocalDateTime.now())
                .clientReports(new ArrayList<>())
                .clientSummary(clientSummary)
                .build();

        when(statusService.checkAllClientsStatus(statusConfig)).thenReturn(report);

        Health health = healthIndicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals(18, health.getDetails().get("totalApis"));
        assertEquals(18, health.getDetails().get("healthyApis"));
        assertEquals(0, health.getDetails().get("unhealthyApis"));
        assertNotNull(health.getDetails().get("clientSummary"));
        assertNotNull(health.getDetails().get("reportTime"));
    }

    @Test
    void testHealth_SomeErrors() {
        Map<String, ApiStatus> clientSummary = new HashMap<>();
        clientSummary.put("DifyChat", ApiStatus.CLIENT_ERROR);
        clientSummary.put("DifyDataset", ApiStatus.NORMAL);
        clientSummary.put("DifyServer", ApiStatus.NORMAL);
        clientSummary.put("DifyWorkflow", ApiStatus.NORMAL);

        AggregatedStatusReport report = AggregatedStatusReport.builder()
                .overallStatus(ApiStatus.CLIENT_ERROR)
                .totalApis(18)
                .healthyApis(16)
                .unhealthyApis(2)
                .reportTime(LocalDateTime.now())
                .clientReports(new ArrayList<>())
                .clientSummary(clientSummary)
                .build();

        when(statusService.checkAllClientsStatus(statusConfig)).thenReturn(report);

        Health health = healthIndicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals(ApiStatus.CLIENT_ERROR, health.getDetails().get("overallStatus"));
        assertEquals(18, health.getDetails().get("totalApis"));
        assertEquals(16, health.getDetails().get("healthyApis"));
        assertEquals(2, health.getDetails().get("unhealthyApis"));
    }

    @Test
    void testHealth_AllErrors() {
        Map<String, ApiStatus> clientSummary = new HashMap<>();
        clientSummary.put("DifyChat", ApiStatus.SERVER_ERROR);
        clientSummary.put("DifyDataset", ApiStatus.SERVER_ERROR);
        clientSummary.put("DifyServer", ApiStatus.SERVER_ERROR);
        clientSummary.put("DifyWorkflow", ApiStatus.SERVER_ERROR);

        AggregatedStatusReport report = AggregatedStatusReport.builder()
                .overallStatus(ApiStatus.SERVER_ERROR)
                .totalApis(18)
                .healthyApis(0)
                .unhealthyApis(18)
                .reportTime(LocalDateTime.now())
                .clientReports(new ArrayList<>())
                .clientSummary(clientSummary)
                .build();

        when(statusService.checkAllClientsStatus(statusConfig)).thenReturn(report);

        Health health = healthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals(ApiStatus.SERVER_ERROR, health.getDetails().get("overallStatus"));
        assertEquals(0, health.getDetails().get("healthyApis"));
        assertEquals(18, health.getDetails().get("unhealthyApis"));
    }

    @Test
    void testHealth_Exception() {
        when(statusService.checkAllClientsStatus(statusConfig))
                .thenThrow(new RuntimeException("Test exception"));

        Health health = healthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertNotNull(health.getDetails().get("error"));
    }

    @Test
    void testHealth_VerifyApiKeyCalled() {
        Map<String, ApiStatus> clientSummary = new HashMap<>();
        clientSummary.put("DifyChat", ApiStatus.NORMAL);

        AggregatedStatusReport report = AggregatedStatusReport.builder()
                .overallStatus(ApiStatus.NORMAL)
                .totalApis(10)
                .healthyApis(10)
                .unhealthyApis(0)
                .reportTime(LocalDateTime.now())
                .clientReports(new ArrayList<>())
                .clientSummary(clientSummary)
                .build();

        when(statusService.checkAllClientsStatus(statusConfig)).thenReturn(report);

        healthIndicator.health();

        verify(statusService, times(1)).checkAllClientsStatus(statusConfig);
    }

    @Test
    void testHealth_ClientSummaryDetails() {
        Map<String, ApiStatus> clientSummary = new HashMap<>();
        clientSummary.put("DifyChat", ApiStatus.NORMAL);
        clientSummary.put("DifyDataset", ApiStatus.NOT_FOUND_404);
        clientSummary.put("DifyServer", ApiStatus.UNAUTHORIZED_401);
        clientSummary.put("DifyWorkflow", ApiStatus.TIMEOUT);

        AggregatedStatusReport report = AggregatedStatusReport.builder()
                .overallStatus(ApiStatus.CLIENT_ERROR)
                .totalApis(18)
                .healthyApis(10)
                .unhealthyApis(8)
                .reportTime(LocalDateTime.now())
                .clientReports(new ArrayList<>())
                .clientSummary(clientSummary)
                .build();

        when(statusService.checkAllClientsStatus(statusConfig)).thenReturn(report);

        Health health = healthIndicator.health();

        assertEquals(Status.UP, health.getStatus());
        @SuppressWarnings("unchecked")
        Map<String, ApiStatus> returnedSummary = (Map<String, ApiStatus>) health.getDetails().get("clientSummary");
        assertNotNull(returnedSummary);
        assertEquals(4, returnedSummary.size());
        assertEquals(ApiStatus.NORMAL, returnedSummary.get("DifyChat"));
        assertEquals(ApiStatus.NOT_FOUND_404, returnedSummary.get("DifyDataset"));
        assertEquals(ApiStatus.UNAUTHORIZED_401, returnedSummary.get("DifyServer"));
        assertEquals(ApiStatus.TIMEOUT, returnedSummary.get("DifyWorkflow"));
    }
}
