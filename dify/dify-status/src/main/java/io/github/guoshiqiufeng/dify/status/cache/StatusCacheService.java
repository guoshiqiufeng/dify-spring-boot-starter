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
package io.github.guoshiqiufeng.dify.status.cache;

import io.github.guoshiqiufeng.dify.status.dto.AggregatedStatusReport;
import io.github.guoshiqiufeng.dify.status.dto.ClientStatusReport;

import java.util.Optional;

/**
 * Status cache service interface
 *
 * @author yanghq
 * @version 1.7.0
 * @since 2025/12/19 14:00
 */
public interface StatusCacheService {
    /**
     * Cache a client status report
     *
     * @param clientName Client name
     * @param report     Client status report
     * @param ttlSeconds TTL in seconds
     */
    void cacheClientReport(String clientName, ClientStatusReport report, long ttlSeconds);

    /**
     * Get cached client report
     *
     * @param clientName Client name
     * @return Optional of ClientStatusReport
     */
    Optional<ClientStatusReport> getCachedClientReport(String clientName);

    /**
     * Cache aggregated report
     *
     * @param report     Aggregated status report
     * @param ttlSeconds TTL in seconds
     */
    void cacheAggregatedReport(AggregatedStatusReport report, long ttlSeconds);

    /**
     * Get cached aggregated report
     *
     * @return Optional of AggregatedStatusReport
     */
    Optional<AggregatedStatusReport> getCachedAggregatedReport();

    /**
     * Clear all cached reports
     */
    void clearCache();
}
