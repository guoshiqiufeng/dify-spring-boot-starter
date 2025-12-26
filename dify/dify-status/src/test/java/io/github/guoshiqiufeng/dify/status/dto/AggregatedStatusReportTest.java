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
package io.github.guoshiqiufeng.dify.status.dto;

import io.github.guoshiqiufeng.dify.status.enums.ApiStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AggregatedStatusReport
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/01/06
 */
class AggregatedStatusReportTest {

    @Test
    void testBuilder() {
        LocalDateTime now = LocalDateTime.now();
        List<ClientStatusReport> clientReports = new ArrayList<>();
        Map<String, ApiStatus> clientSummary = new HashMap<>();
        clientSummary.put("DifyChat", ApiStatus.NORMAL);

        AggregatedStatusReport report = AggregatedStatusReport.builder()
                .overallStatus(ApiStatus.NORMAL)
                .clientReports(clientReports)
                .totalApis(10)
                .healthyApis(10)
                .unhealthyApis(0)
                .reportTime(now)
                .clientSummary(clientSummary)
                .build();

        assertNotNull(report);
        assertEquals(ApiStatus.NORMAL, report.getOverallStatus());
        assertEquals(10, report.getTotalApis());
        assertEquals(10, report.getHealthyApis());
        assertEquals(0, report.getUnhealthyApis());
        assertEquals(now, report.getReportTime());
        assertSame(clientSummary, report.getClientSummary());
    }

    @Test
    void testNoArgsConstructor() {
        AggregatedStatusReport report = new AggregatedStatusReport();
        assertNotNull(report);
        assertNull(report.getOverallStatus());
        assertNull(report.getTotalApis());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        List<ClientStatusReport> clientReports = new ArrayList<>();
        Map<String, ApiStatus> clientSummary = new HashMap<>();

        AggregatedStatusReport report = new AggregatedStatusReport(
                ApiStatus.CLIENT_ERROR,
                clientReports,
                20,
                15,
                5,
                now,
                clientSummary
        );

        assertNotNull(report);
        assertEquals(ApiStatus.CLIENT_ERROR, report.getOverallStatus());
        assertEquals(20, report.getTotalApis());
        assertEquals(15, report.getHealthyApis());
        assertEquals(5, report.getUnhealthyApis());
    }

    @Test
    void testSettersAndGetters() {
        AggregatedStatusReport report = new AggregatedStatusReport();
        LocalDateTime now = LocalDateTime.now();
        List<ClientStatusReport> clientReports = new ArrayList<>();
        Map<String, ApiStatus> clientSummary = new HashMap<>();

        report.setOverallStatus(ApiStatus.SERVER_ERROR);
        report.setClientReports(clientReports);
        report.setTotalApis(30);
        report.setHealthyApis(20);
        report.setUnhealthyApis(10);
        report.setReportTime(now);
        report.setClientSummary(clientSummary);

        assertEquals(ApiStatus.SERVER_ERROR, report.getOverallStatus());
        assertSame(clientReports, report.getClientReports());
        assertEquals(30, report.getTotalApis());
        assertEquals(20, report.getHealthyApis());
        assertEquals(10, report.getUnhealthyApis());
        assertEquals(now, report.getReportTime());
        assertSame(clientSummary, report.getClientSummary());
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();
        Map<String, ApiStatus> clientSummary = new HashMap<>();
        clientSummary.put("DifyChat", ApiStatus.NORMAL);

        AggregatedStatusReport report1 = AggregatedStatusReport.builder()
                .overallStatus(ApiStatus.NORMAL)
                .totalApis(10)
                .healthyApis(10)
                .unhealthyApis(0)
                .reportTime(now)
                .clientSummary(clientSummary)
                .build();

        AggregatedStatusReport report2 = AggregatedStatusReport.builder()
                .overallStatus(ApiStatus.NORMAL)
                .totalApis(10)
                .healthyApis(10)
                .unhealthyApis(0)
                .reportTime(now)
                .clientSummary(clientSummary)
                .build();

        assertEquals(report1, report2);
        assertEquals(report1.hashCode(), report2.hashCode());
    }

    @Test
    void testToString() {
        AggregatedStatusReport report = AggregatedStatusReport.builder()
                .overallStatus(ApiStatus.NORMAL)
                .totalApis(10)
                .healthyApis(10)
                .build();

        String toString = report.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("NORMAL"));
        assertTrue(toString.contains("10"));
    }

    @Test
    void testWithNullValues() {
        AggregatedStatusReport report = AggregatedStatusReport.builder()
                .overallStatus(null)
                .clientReports(null)
                .totalApis(null)
                .healthyApis(null)
                .unhealthyApis(null)
                .reportTime(null)
                .clientSummary(null)
                .build();

        assertNotNull(report);
        assertNull(report.getOverallStatus());
        assertNull(report.getTotalApis());
    }

    @Test
    void testWithEmptyCollections() {
        AggregatedStatusReport report = AggregatedStatusReport.builder()
                .overallStatus(ApiStatus.NORMAL)
                .clientReports(Collections.emptyList())
                .clientSummary(Collections.emptyMap())
                .build();

        assertNotNull(report);
        assertTrue(report.getClientReports().isEmpty());
        assertTrue(report.getClientSummary().isEmpty());
    }

    @Test
    void testWithMultipleClientReports() {
        List<ClientStatusReport> clientReports = Arrays.asList(
                ClientStatusReport.builder().clientName("DifyChat").overallStatus(ApiStatus.NORMAL).build(),
                ClientStatusReport.builder().clientName("DifyDataset").overallStatus(ApiStatus.NORMAL).build(),
                ClientStatusReport.builder().clientName("DifyWorkflow").overallStatus(ApiStatus.CLIENT_ERROR).build()
        );

        AggregatedStatusReport report = AggregatedStatusReport.builder()
                .overallStatus(ApiStatus.CLIENT_ERROR)
                .clientReports(clientReports)
                .totalApis(30)
                .healthyApis(25)
                .unhealthyApis(5)
                .build();

        assertEquals(3, report.getClientReports().size());
        assertEquals(30, report.getTotalApis());
        assertEquals(25, report.getHealthyApis());
        assertEquals(5, report.getUnhealthyApis());
    }

    @Test
    void testClientSummaryWithMultipleClients() {
        Map<String, ApiStatus> clientSummary = new HashMap<>();
        clientSummary.put("DifyChat", ApiStatus.NORMAL);
        clientSummary.put("DifyDataset", ApiStatus.NORMAL);
        clientSummary.put("DifyWorkflow", ApiStatus.CLIENT_ERROR);
        clientSummary.put("DifyServer", ApiStatus.SERVER_ERROR);

        AggregatedStatusReport report = AggregatedStatusReport.builder()
                .overallStatus(ApiStatus.CLIENT_ERROR)
                .clientSummary(clientSummary)
                .totalApis(40)
                .build();

        assertEquals(4, report.getClientSummary().size());
        assertEquals(ApiStatus.NORMAL, report.getClientSummary().get("DifyChat"));
        assertEquals(ApiStatus.CLIENT_ERROR, report.getClientSummary().get("DifyWorkflow"));
        assertEquals(ApiStatus.SERVER_ERROR, report.getClientSummary().get("DifyServer"));
    }

    @Test
    void testHealthyAndUnhealthyApisSum() {
        AggregatedStatusReport report = AggregatedStatusReport.builder()
                .totalApis(100)
                .healthyApis(75)
                .unhealthyApis(25)
                .build();

        assertEquals(100, report.getTotalApis());
        assertEquals(100, report.getHealthyApis() + report.getUnhealthyApis());
    }

    @Test
    void testAllHealthyScenario() {
        AggregatedStatusReport report = AggregatedStatusReport.builder()
                .overallStatus(ApiStatus.NORMAL)
                .totalApis(50)
                .healthyApis(50)
                .unhealthyApis(0)
                .build();

        assertEquals(ApiStatus.NORMAL, report.getOverallStatus());
        assertEquals(50, report.getHealthyApis());
        assertEquals(0, report.getUnhealthyApis());
    }

    @Test
    void testAllUnhealthyScenario() {
        AggregatedStatusReport report = AggregatedStatusReport.builder()
                .overallStatus(ApiStatus.SERVER_ERROR)
                .totalApis(50)
                .healthyApis(0)
                .unhealthyApis(50)
                .build();

        assertEquals(ApiStatus.SERVER_ERROR, report.getOverallStatus());
        assertEquals(0, report.getHealthyApis());
        assertEquals(50, report.getUnhealthyApis());
    }
}
