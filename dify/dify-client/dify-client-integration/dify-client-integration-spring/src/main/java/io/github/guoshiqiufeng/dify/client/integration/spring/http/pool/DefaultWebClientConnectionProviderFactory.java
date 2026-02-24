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
package io.github.guoshiqiufeng.dify.client.integration.spring.http.pool;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of WebClientConnectionProviderFactory.
 * Creates reactor-netty ConnectionProvider with settings mapped from OkHttp parameters.
 *
 * <p>Parameter mapping:
 * <ul>
 *   <li>maxRequests → maxConnections (global pool size)</li>
 *   <li>keepAliveSeconds → maxIdleTime</li>
 *   <li>pendingAcquireMaxCount → 2x maxConnections (reasonable queue size)</li>
 * </ul>
 *
 * <p><strong>Note:</strong> reactor-netty does not support per-host connection limits.
 * The maxRequestsPerHost parameter is ignored.
 *
 * <p><strong>Resource Management:</strong>
 * This factory caches ConnectionProviders by pool settings and implements AutoCloseable for proper resource cleanup.
 * In Spring environments, register as a bean with destroyMethod="close" to ensure automatic cleanup.
 * In non-Spring environments, call close() explicitly when done.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026-02-06
 */
@Slf4j
public final class DefaultWebClientConnectionProviderFactory implements WebClientConnectionProviderFactory, AutoCloseable {

    // Sentinel value to indicate reactor-netty is not available
    private static final Object UNAVAILABLE_SENTINEL = new Object();

    // Cache key: combines PoolSettings and poolName for proper multi-tenant isolation
    private static class CacheKey {
        private final PoolSettings settings;
        private final String poolName;

        CacheKey(PoolSettings settings, String poolName) {
            this.settings = settings;
            this.poolName = poolName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CacheKey cacheKey = (CacheKey) o;
            return Objects.equals(settings, cacheKey.settings) && Objects.equals(poolName, cacheKey.poolName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(settings, poolName);
        }
    }

    private final Map<CacheKey, Object> cachedProviders = new ConcurrentHashMap<>();
    private volatile boolean reactorNettyAvailable = true; // Assume available initially

    @Override
    public Object buildConnectionProvider(PoolSettings settings, String poolName) {
        // Fast path: if reactor-netty is known to be unavailable, return null immediately
        if (!reactorNettyAvailable) {
            return null;
        }

        CacheKey key = new CacheKey(settings, poolName);

        // Return cached instance if already created for this specific configuration
        Object result = cachedProviders.computeIfAbsent(key, k -> {
            try {
                // Use reflection to create ConnectionProvider (reactor-netty may not be on classpath)
                Class<?> connectionProviderClass = Class.forName("reactor.netty.resources.ConnectionProvider");
                Class<?> builderClass = Class.forName("reactor.netty.resources.ConnectionProvider$Builder");

                // ConnectionProvider.builder(poolName)
                Object builder = connectionProviderClass.getMethod("builder", String.class)
                        .invoke(null, poolName);

                // Map maxRequests to maxConnections (global pool size)
                // Note: reactor-netty doesn't have per-host limit like OkHttp
                builder = builderClass.getMethod("maxConnections", int.class)
                        .invoke(builder, settings.getMaxRequests());

                // Map keepAliveSeconds to maxIdleTime
                Duration maxIdleTime = Duration.ofSeconds(settings.getKeepAliveSeconds());
                builder = builderClass.getMethod("maxIdleTime", Duration.class)
                        .invoke(builder, maxIdleTime);

                // Set pendingAcquireMaxCount to 2x maxConnections (reasonable queue size)
                // Use long arithmetic to prevent overflow, then cap at Integer.MAX_VALUE
                long pendingAcquireMaxLong = (long) settings.getMaxRequests() * 2;
                int pendingAcquireMax = (int) Math.min(pendingAcquireMaxLong, Integer.MAX_VALUE);

                if (pendingAcquireMaxLong > Integer.MAX_VALUE) {
                    log.warn("pendingAcquireMaxCount calculation overflow detected: {}*2={}, capped at Integer.MAX_VALUE",
                            settings.getMaxRequests(), pendingAcquireMaxLong);
                }

                builder = builderClass.getMethod("pendingAcquireMaxCount", int.class)
                        .invoke(builder, pendingAcquireMax);

                // Build the ConnectionProvider
                Object connectionProvider = builderClass.getMethod("build").invoke(builder);

                log.debug("Created reactor-netty ConnectionProvider: poolName={}, maxConnections={} (from maxRequests), maxIdleTime={}s, pendingAcquireMaxCount={}",
                        poolName, settings.getMaxRequests(), settings.getKeepAliveSeconds(), pendingAcquireMax);
                log.warn("Note: reactor-netty doesn't support per-host connection limits. maxRequestsPerHost={} is ignored.",
                        settings.getMaxRequestsPerHost());

                return connectionProvider;
            } catch (ClassNotFoundException e) {
                // reactor-netty is not available - mark as unavailable to avoid future attempts
                reactorNettyAvailable = false;
                log.warn("reactor-netty is not available on classpath. Connection pool configuration will be ignored. " +
                        "Add reactor-netty dependency to enable connection pooling for WebClient.");
                return UNAVAILABLE_SENTINEL;
            } catch (Exception e) {
                log.warn("Failed to create reactor-netty ConnectionProvider, falling back to default: {}", e.getMessage());
                // Return sentinel to signal fallback to default behavior
                return UNAVAILABLE_SENTINEL;
            }
        });

        // Return null if sentinel (unavailable or failed)
        return (result == UNAVAILABLE_SENTINEL) ? null : result;
    }

    /**
     * Dispose all cached ConnectionProviders and release resources.
     * This method is idempotent and thread-safe.
     */
    @Override
    public void close() {
        if (!cachedProviders.isEmpty()) {
            cachedProviders.forEach((key, connectionProvider) -> {
                // Skip sentinel values
                if (connectionProvider != null && connectionProvider != UNAVAILABLE_SENTINEL) {
                    try {
                        // Try synchronous dispose() first (if available)
                        try {
                            connectionProvider.getClass().getMethod("dispose").invoke(connectionProvider);
                            log.debug("Disposed reactor-netty ConnectionProvider (synchronous) for pool: {}", key.poolName);
                        } catch (NoSuchMethodException e) {
                            // Fallback to disposeLater().block() with timeout
                            Object disposeMono = connectionProvider.getClass().getMethod("disposeLater").invoke(connectionProvider);
                            // Block with timeout to ensure disposal completes
                            Class<?> monoClass = disposeMono.getClass();
                            monoClass.getMethod("block", Duration.class).invoke(disposeMono, Duration.ofSeconds(5));
                            log.debug("Disposed reactor-netty ConnectionProvider (async with block) for pool: {}", key.poolName);
                        }
                    } catch (Exception e) {
                        log.warn("Failed to dispose reactor-netty ConnectionProvider for pool {}: {}", key.poolName, e.getMessage());
                    }
                }
            });
            cachedProviders.clear();
        }
    }
}
