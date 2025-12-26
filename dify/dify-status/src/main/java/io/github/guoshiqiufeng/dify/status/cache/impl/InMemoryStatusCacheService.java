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

import io.github.guoshiqiufeng.dify.status.cache.StatusCacheService;
import io.github.guoshiqiufeng.dify.status.dto.AggregatedStatusReport;
import io.github.guoshiqiufeng.dify.status.dto.ClientStatusReport;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory status cache service implementation
 *
 * @author yanghq
 * @version 1.7.0
 * @since 2025/12/19 14:00
 */
@Slf4j
public class InMemoryStatusCacheService implements StatusCacheService {

    private final ConcurrentHashMap<String, CacheEntry<ClientStatusReport>> clientReportCache = new ConcurrentHashMap<>();
    private volatile CacheEntry<AggregatedStatusReport> aggregatedReportCache;

    @Override
    public void cacheClientReport(String clientName, ClientStatusReport report, long ttlSeconds) {
        LocalDateTime expirationTime = LocalDateTime.now().plusSeconds(ttlSeconds);
        clientReportCache.put(clientName, new CacheEntry<>(report, expirationTime));
        log.debug("Cached client report for {} with TTL {} seconds", clientName, ttlSeconds);
    }

    @Override
    public Optional<ClientStatusReport> getCachedClientReport(String clientName) {
        CacheEntry<ClientStatusReport> entry = clientReportCache.get(clientName);
        if (entry == null) {
            log.debug("No cached report found for {}", clientName);
            return Optional.empty();
        }

        if (entry.isExpired()) {
            clientReportCache.remove(clientName);
            log.debug("Cached report for {} has expired", clientName);
            return Optional.empty();
        }

        log.debug("Retrieved cached report for {}", clientName);
        return Optional.of(entry.getData());
    }

    @Override
    public void cacheAggregatedReport(AggregatedStatusReport report, long ttlSeconds) {
        LocalDateTime expirationTime = LocalDateTime.now().plusSeconds(ttlSeconds);
        aggregatedReportCache = new CacheEntry<>(report, expirationTime);
        log.debug("Cached aggregated report with TTL {} seconds", ttlSeconds);
    }

    @Override
    public Optional<AggregatedStatusReport> getCachedAggregatedReport() {
        CacheEntry<AggregatedStatusReport> entry = aggregatedReportCache;
        if (entry == null) {
            log.debug("No cached aggregated report found");
            return Optional.empty();
        }

        if (entry.isExpired()) {
            aggregatedReportCache = null;
            log.debug("Cached aggregated report has expired");
            return Optional.empty();
        }

        log.debug("Retrieved cached aggregated report");
        return Optional.of(entry.getData());
    }

    @Override
    public void clearCache() {
        clientReportCache.clear();
        aggregatedReportCache = null;
        log.info("Cleared all cached reports");
    }

    /**
     * Cache entry with expiration time
     *
     * @param <T> Type of cached data
     */
    @Data
    @AllArgsConstructor
    private static class CacheEntry<T> {
        private final T data;
        private final LocalDateTime expirationTime;

        /**
         * Check if the cache entry has expired
         *
         * @return true if expired, false otherwise
         */
        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expirationTime);
        }
    }
}
