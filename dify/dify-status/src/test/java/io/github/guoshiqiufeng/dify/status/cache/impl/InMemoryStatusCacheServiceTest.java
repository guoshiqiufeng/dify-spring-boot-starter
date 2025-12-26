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
package io.github.guoshiqiufeng.dify.status.cache.impl;

import io.github.guoshiqiufeng.dify.status.dto.AggregatedStatusReport;
import io.github.guoshiqiufeng.dify.status.dto.ClientStatusReport;
import io.github.guoshiqiufeng.dify.status.enums.ApiStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * InMemoryStatusCacheService test
 *
 * @author yanghq
 * @version 1.7.0
 * @since 2025/12/19 15:00
 */
class InMemoryStatusCacheServiceTest {

    private InMemoryStatusCacheService cacheService;

    @BeforeEach
    void setUp() {
        cacheService = new InMemoryStatusCacheService();
    }

    @Test
    void testCacheAndGetClientReport() {
        ClientStatusReport report = ClientStatusReport.builder()
                .clientName("DifyChat")
                .overallStatus(ApiStatus.NORMAL)
                .totalApis(10)
                .normalApis(10)
                .errorApis(0)
                .reportTime(LocalDateTime.now())
                .apiStatuses(new ArrayList<>())
                .statusSummary(new HashMap<>())
                .build();

        cacheService.cacheClientReport("DifyChat", report, 60);

        Optional<ClientStatusReport> cached = cacheService.getCachedClientReport("DifyChat");
        assertTrue(cached.isPresent());
        assertEquals("DifyChat", cached.get().getClientName());
        assertEquals(ApiStatus.NORMAL, cached.get().getOverallStatus());
    }

    @Test
    void testGetNonExistentClientReport() {
        Optional<ClientStatusReport> cached = cacheService.getCachedClientReport("NonExistent");
        assertFalse(cached.isPresent());
    }

    @Test
    void testCacheExpiration() throws InterruptedException {
        ClientStatusReport report = ClientStatusReport.builder()
                .clientName("DifyChat")
                .overallStatus(ApiStatus.NORMAL)
                .totalApis(10)
                .normalApis(10)
                .errorApis(0)
                .reportTime(LocalDateTime.now())
                .apiStatuses(new ArrayList<>())
                .statusSummary(new HashMap<>())
                .build();

        // Cache with 1 second TTL
        cacheService.cacheClientReport("DifyChat", report, 1);

        // Should be present immediately
        Optional<ClientStatusReport> cached = cacheService.getCachedClientReport("DifyChat");
        assertTrue(cached.isPresent());

        // Wait for expiration
        Thread.sleep(1100);

        // Should be expired
        cached = cacheService.getCachedClientReport("DifyChat");
        assertFalse(cached.isPresent());
    }

    @Test
    void testCacheAggregatedReport() {
        AggregatedStatusReport report = AggregatedStatusReport.builder()
                .overallStatus(ApiStatus.NORMAL)
                .totalApis(20)
                .healthyApis(20)
                .unhealthyApis(0)
                .reportTime(LocalDateTime.now())
                .clientReports(new ArrayList<>())
                .clientSummary(new HashMap<>())
                .build();

        cacheService.cacheAggregatedReport(report, 60);

        Optional<AggregatedStatusReport> cached = cacheService.getCachedAggregatedReport();
        assertTrue(cached.isPresent());
        assertEquals(ApiStatus.NORMAL, cached.get().getOverallStatus());
        assertEquals(20, cached.get().getTotalApis());
    }

    @Test
    void testGetNonExistentAggregatedReport() {
        Optional<AggregatedStatusReport> cached = cacheService.getCachedAggregatedReport();
        assertFalse(cached.isPresent());
    }

    @Test
    void testAggregatedReportExpiration() throws InterruptedException {
        AggregatedStatusReport report = AggregatedStatusReport.builder()
                .overallStatus(ApiStatus.NORMAL)
                .totalApis(20)
                .healthyApis(20)
                .unhealthyApis(0)
                .reportTime(LocalDateTime.now())
                .clientReports(new ArrayList<>())
                .clientSummary(new HashMap<>())
                .build();

        // Cache with 1 second TTL
        cacheService.cacheAggregatedReport(report, 1);

        // Should be present immediately
        Optional<AggregatedStatusReport> cached = cacheService.getCachedAggregatedReport();
        assertTrue(cached.isPresent());

        // Wait for expiration
        Thread.sleep(1100);

        // Should be expired
        cached = cacheService.getCachedAggregatedReport();
        assertFalse(cached.isPresent());
    }

    @Test
    void testClearCache() {
        ClientStatusReport clientReport = ClientStatusReport.builder()
                .clientName("DifyChat")
                .overallStatus(ApiStatus.NORMAL)
                .totalApis(10)
                .normalApis(10)
                .errorApis(0)
                .reportTime(LocalDateTime.now())
                .apiStatuses(new ArrayList<>())
                .statusSummary(new HashMap<>())
                .build();

        AggregatedStatusReport aggregatedReport = AggregatedStatusReport.builder()
                .overallStatus(ApiStatus.NORMAL)
                .totalApis(20)
                .healthyApis(20)
                .unhealthyApis(0)
                .reportTime(LocalDateTime.now())
                .clientReports(new ArrayList<>())
                .clientSummary(new HashMap<>())
                .build();

        cacheService.cacheClientReport("DifyChat", clientReport, 60);
        cacheService.cacheAggregatedReport(aggregatedReport, 60);

        // Verify cached
        assertTrue(cacheService.getCachedClientReport("DifyChat").isPresent());
        assertTrue(cacheService.getCachedAggregatedReport().isPresent());

        // Clear cache
        cacheService.clearCache();

        // Verify cleared
        assertFalse(cacheService.getCachedClientReport("DifyChat").isPresent());
        assertFalse(cacheService.getCachedAggregatedReport().isPresent());
    }

    @Test
    void testMultipleClientReports() {
        ClientStatusReport report1 = ClientStatusReport.builder()
                .clientName("DifyChat")
                .overallStatus(ApiStatus.NORMAL)
                .totalApis(10)
                .normalApis(10)
                .errorApis(0)
                .reportTime(LocalDateTime.now())
                .apiStatuses(new ArrayList<>())
                .statusSummary(new HashMap<>())
                .build();

        ClientStatusReport report2 = ClientStatusReport.builder()
                .clientName("DifyDataset")
                .overallStatus(ApiStatus.NORMAL)
                .totalApis(5)
                .normalApis(5)
                .errorApis(0)
                .reportTime(LocalDateTime.now())
                .apiStatuses(new ArrayList<>())
                .statusSummary(new HashMap<>())
                .build();

        cacheService.cacheClientReport("DifyChat", report1, 60);
        cacheService.cacheClientReport("DifyDataset", report2, 60);

        Optional<ClientStatusReport> cached1 = cacheService.getCachedClientReport("DifyChat");
        Optional<ClientStatusReport> cached2 = cacheService.getCachedClientReport("DifyDataset");

        assertTrue(cached1.isPresent());
        assertTrue(cached2.isPresent());
        assertEquals("DifyChat", cached1.get().getClientName());
        assertEquals("DifyDataset", cached2.get().getClientName());
    }

    @Test
    void testOverwriteCache() {
        ClientStatusReport report1 = ClientStatusReport.builder()
                .clientName("DifyChat")
                .overallStatus(ApiStatus.NORMAL)
                .totalApis(10)
                .normalApis(10)
                .errorApis(0)
                .reportTime(LocalDateTime.now())
                .apiStatuses(new ArrayList<>())
                .statusSummary(new HashMap<>())
                .build();

        ClientStatusReport report2 = ClientStatusReport.builder()
                .clientName("DifyChat")
                .overallStatus(ApiStatus.CLIENT_ERROR)
                .totalApis(10)
                .normalApis(8)
                .errorApis(2)
                .reportTime(LocalDateTime.now())
                .apiStatuses(new ArrayList<>())
                .statusSummary(new HashMap<>())
                .build();

        cacheService.cacheClientReport("DifyChat", report1, 60);
        cacheService.cacheClientReport("DifyChat", report2, 60);

        Optional<ClientStatusReport> cached = cacheService.getCachedClientReport("DifyChat");
        assertTrue(cached.isPresent());
        assertEquals(ApiStatus.CLIENT_ERROR, cached.get().getOverallStatus());
        assertEquals(2, cached.get().getErrorApis());
    }
}
