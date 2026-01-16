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
 * Unit tests for ClientStatusReport
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/01/06
 */
class ClientStatusReportTest {

    @Test
    void testBuilder() {
        LocalDateTime now = LocalDateTime.now();
        List<ApiStatusResult> apiStatuses = new ArrayList<>();
        Map<ApiStatus, Integer> statusSummary = new HashMap<>();
        statusSummary.put(ApiStatus.NORMAL, 3);

        ClientStatusReport report = ClientStatusReport.builder()
                .clientName("DifyChat")
                .overallStatus(ApiStatus.NORMAL)
                .apiStatuses(apiStatuses)
                .totalApis(3)
                .normalApis(3)
                .errorApis(0)
                .reportTime(now)
                .statusSummary(statusSummary)
                .build();

        assertNotNull(report);
        assertEquals("DifyChat", report.getClientName());
        assertEquals(ApiStatus.NORMAL, report.getOverallStatus());
        assertEquals(3, report.getTotalApis());
        assertEquals(3, report.getNormalApis());
        assertEquals(0, report.getErrorApis());
        assertEquals(now, report.getReportTime());
        assertSame(statusSummary, report.getStatusSummary());
    }

    @Test
    void testNoArgsConstructor() {
        ClientStatusReport report = new ClientStatusReport();
        assertNotNull(report);
        assertNull(report.getClientName());
        assertNull(report.getOverallStatus());
        assertNull(report.getTotalApis());
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        List<ApiStatusResult> apiStatuses = new ArrayList<>();
        Map<ApiStatus, Integer> statusSummary = new HashMap<>();

        ClientStatusReport report = new ClientStatusReport(
                "DifyDataset",
                ApiStatus.CLIENT_ERROR,
                apiStatuses,
                5,
                3,
                2,
                now,
                statusSummary
        );

        assertNotNull(report);
        assertEquals("DifyDataset", report.getClientName());
        assertEquals(ApiStatus.CLIENT_ERROR, report.getOverallStatus());
        assertEquals(5, report.getTotalApis());
        assertEquals(3, report.getNormalApis());
        assertEquals(2, report.getErrorApis());
    }

    @Test
    void testSettersAndGetters() {
        ClientStatusReport report = new ClientStatusReport();
        LocalDateTime now = LocalDateTime.now();
        List<ApiStatusResult> apiStatuses = new ArrayList<>();
        Map<ApiStatus, Integer> statusSummary = new HashMap<>();

        report.setClientName("DifyWorkflow");
        report.setOverallStatus(ApiStatus.SERVER_ERROR);
        report.setApiStatuses(apiStatuses);
        report.setTotalApis(10);
        report.setNormalApis(7);
        report.setErrorApis(3);
        report.setReportTime(now);
        report.setStatusSummary(statusSummary);

        assertEquals("DifyWorkflow", report.getClientName());
        assertEquals(ApiStatus.SERVER_ERROR, report.getOverallStatus());
        assertSame(apiStatuses, report.getApiStatuses());
        assertEquals(10, report.getTotalApis());
        assertEquals(7, report.getNormalApis());
        assertEquals(3, report.getErrorApis());
        assertEquals(now, report.getReportTime());
        assertSame(statusSummary, report.getStatusSummary());
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();
        Map<ApiStatus, Integer> statusSummary = new HashMap<>();
        statusSummary.put(ApiStatus.NORMAL, 3);

        ClientStatusReport report1 = ClientStatusReport.builder()
                .clientName("DifyChat")
                .overallStatus(ApiStatus.NORMAL)
                .totalApis(3)
                .normalApis(3)
                .errorApis(0)
                .reportTime(now)
                .statusSummary(statusSummary)
                .build();

        ClientStatusReport report2 = ClientStatusReport.builder()
                .clientName("DifyChat")
                .overallStatus(ApiStatus.NORMAL)
                .totalApis(3)
                .normalApis(3)
                .errorApis(0)
                .reportTime(now)
                .statusSummary(statusSummary)
                .build();

        assertEquals(report1, report2);
        assertEquals(report1.hashCode(), report2.hashCode());
    }

    @Test
    void testToString() {
        ClientStatusReport report = ClientStatusReport.builder()
                .clientName("DifyChat")
                .overallStatus(ApiStatus.NORMAL)
                .totalApis(3)
                .build();

        String toString = report.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("DifyChat"));
        assertTrue(toString.contains("NORMAL"));
    }

    @Test
    void testWithNullValues() {
        ClientStatusReport report = ClientStatusReport.builder()
                .clientName(null)
                .overallStatus(null)
                .apiStatuses(null)
                .totalApis(null)
                .normalApis(null)
                .errorApis(null)
                .reportTime(null)
                .statusSummary(null)
                .build();

        assertNotNull(report);
        assertNull(report.getClientName());
        assertNull(report.getOverallStatus());
        assertNull(report.getTotalApis());
    }

    @Test
    void testWithEmptyCollections() {
        ClientStatusReport report = ClientStatusReport.builder()
                .clientName("DifyChat")
                .apiStatuses(Collections.emptyList())
                .statusSummary(Collections.emptyMap())
                .build();

        assertNotNull(report);
        assertTrue(report.getApiStatuses().isEmpty());
        assertTrue(report.getStatusSummary().isEmpty());
    }

    @Test
    void testWithMultipleApiStatuses() {
        List<ApiStatusResult> apiStatuses = Arrays.asList(
                ApiStatusResult.builder().methodName("method1").status(ApiStatus.NORMAL).build(),
                ApiStatusResult.builder().methodName("method2").status(ApiStatus.NORMAL).build(),
                ApiStatusResult.builder().methodName("method3").status(ApiStatus.CLIENT_ERROR).build()
        );

        ClientStatusReport report = ClientStatusReport.builder()
                .clientName("DifyChat")
                .apiStatuses(apiStatuses)
                .totalApis(3)
                .normalApis(2)
                .errorApis(1)
                .build();

        assertEquals(3, report.getApiStatuses().size());
        assertEquals(3, report.getTotalApis());
        assertEquals(2, report.getNormalApis());
        assertEquals(1, report.getErrorApis());
    }

    @Test
    void testStatusSummaryWithMultipleStatuses() {
        Map<ApiStatus, Integer> statusSummary = new HashMap<>();
        statusSummary.put(ApiStatus.NORMAL, 5);
        statusSummary.put(ApiStatus.CLIENT_ERROR, 2);
        statusSummary.put(ApiStatus.SERVER_ERROR, 1);

        ClientStatusReport report = ClientStatusReport.builder()
                .clientName("DifyDataset")
                .statusSummary(statusSummary)
                .totalApis(8)
                .build();

        assertEquals(3, report.getStatusSummary().size());
        assertEquals(5, report.getStatusSummary().get(ApiStatus.NORMAL));
        assertEquals(2, report.getStatusSummary().get(ApiStatus.CLIENT_ERROR));
        assertEquals(1, report.getStatusSummary().get(ApiStatus.SERVER_ERROR));
    }
}
