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

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Default implementation of RestClientHttpClientFactory.
 * Creates Apache HttpClient 5 with connection pool settings mapped from OkHttp parameters.
 *
 * <p>Parameter mapping:
 * <ul>
 *   <li>maxRequests → setMaxTotal (total connections in pool)</li>
 *   <li>maxRequestsPerHost → setDefaultMaxPerRoute (connections per route)</li>
 *   <li>keepAliveSeconds → connection TTL and idle eviction</li>
 *   <li>callTimeoutSeconds → response timeout</li>
 * </ul>
 *
 * <p><strong>Resource Management:</strong>
 * This factory caches HttpClient instances by PoolSettings to support multi-tenant scenarios.
 * Each unique PoolSettings configuration gets its own HttpClient instance.
 * Uses PoolSettings object as cache key (with proper equals/hashCode) to avoid hash collision.
 * Implements Closeable for proper resource cleanup.
 * In Spring environments, register as a bean with destroyMethod="close" to ensure automatic cleanup.
 * In non-Spring environments, call close() explicitly when done.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026-02-06
 */
@Slf4j
public final class DefaultRestClientHttpClientFactory implements RestClientHttpClientFactory, Closeable {

    // Sentinel value to cache negative results (HC5 not available)
    private static final Object UNAVAILABLE_SENTINEL = new Object();

    private final Map<PoolSettings, Object> cachedHttpClients = new ConcurrentHashMap<>();

    @Override
    public Object buildHttpClient(PoolSettings settings) {
        // Return cached instance if already created for these settings
        // Use PoolSettings as key to avoid hash collision issues
        // PoolSettings has proper equals/hashCode implementation
        Object cached = cachedHttpClients.get(settings);
        if (cached != null) {
            // Return null if sentinel (HC5 unavailable), otherwise return the client
            return cached == UNAVAILABLE_SENTINEL ? null : cached;
        }

        return cachedHttpClients.computeIfAbsent(settings, k -> {
            log.debug("Creating new Apache HttpClient 5 for settings: {}", k);

            try {
                // Use reflection to create Apache HttpClient 5 (may not be on classpath)
                Class<?> poolingManagerBuilderClass = Class.forName(
                        "org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder");
                Class<?> connectionManagerClass = Class.forName(
                        "org.apache.hc.client5.http.io.HttpClientConnectionManager");
                Class<?> httpClientsClass = Class.forName(
                        "org.apache.hc.client5.http.impl.classic.HttpClients");
                Class<?> httpClientBuilderClass = Class.forName(
                        "org.apache.hc.client5.http.impl.classic.HttpClientBuilder");
                Class<?> timeValueClass = Class.forName("org.apache.hc.core5.util.TimeValue");
                Class<?> timeoutClass = Class.forName("org.apache.hc.core5.util.Timeout");

                // Create PoolingHttpClientConnectionManagerBuilder
                Object managerBuilder = poolingManagerBuilderClass.getMethod("create").invoke(null);

                // Set max total connections (maxRequests)
                managerBuilder = poolingManagerBuilderClass.getMethod("setMaxConnTotal", int.class)
                        .invoke(managerBuilder, settings.getMaxRequests());

                // Set max connections per route (maxRequestsPerHost)
                managerBuilder = poolingManagerBuilderClass.getMethod("setMaxConnPerRoute", int.class)
                        .invoke(managerBuilder, settings.getMaxRequestsPerHost());

                // Set connection time-to-live (keepAliveSeconds)
                Object timeToLive = timeValueClass.getMethod("of", long.class, TimeUnit.class)
                        .invoke(null, (long) settings.getKeepAliveSeconds(), TimeUnit.SECONDS);
                managerBuilder = poolingManagerBuilderClass.getMethod("setConnectionTimeToLive", timeValueClass)
                        .invoke(managerBuilder, timeToLive);

                // Build connection manager
                Object connectionManager = poolingManagerBuilderClass.getMethod("build").invoke(managerBuilder);

                // Create HttpClient builder
                Object httpClientBuilder = httpClientsClass.getMethod("custom").invoke(null);

                // Set connection manager (use interface type, not concrete class)
                httpClientBuilder = httpClientBuilderClass.getMethod("setConnectionManager", connectionManagerClass)
                        .invoke(httpClientBuilder, connectionManager);

                // Create RequestConfig with all timeouts
                Class<?> requestConfigClass = Class.forName("org.apache.hc.client5.http.config.RequestConfig");
                Class<?> requestConfigBuilderClass = Class.forName("org.apache.hc.client5.http.config.RequestConfig$Builder");

                Object requestConfigBuilder = requestConfigClass.getMethod("custom").invoke(null);

                // Set connect timeout
                Object connectTimeout = timeoutClass.getMethod("ofSeconds", long.class)
                        .invoke(null, (long) settings.getConnectTimeoutSeconds());
                requestConfigBuilder = requestConfigBuilderClass.getMethod("setConnectTimeout", timeoutClass)
                        .invoke(requestConfigBuilder, connectTimeout);

                // Set socket timeout (read timeout) - use effective timeout considering callTimeout
                long effectiveReadTimeout;
                if (settings.getCallTimeoutSeconds() > 0) {
                    // Use min of callTimeout and readTimeout to respect both
                    effectiveReadTimeout = Math.min(settings.getCallTimeoutSeconds(), settings.getReadTimeoutSeconds());
                } else {
                    effectiveReadTimeout = settings.getReadTimeoutSeconds();
                }
                Object responseTimeout = timeoutClass.getMethod("ofSeconds", long.class)
                        .invoke(null, effectiveReadTimeout);
                requestConfigBuilder = requestConfigBuilderClass.getMethod("setResponseTimeout", timeoutClass)
                        .invoke(requestConfigBuilder, responseTimeout);

                Object requestConfig = requestConfigBuilderClass.getMethod("build").invoke(requestConfigBuilder);

                httpClientBuilder = httpClientBuilderClass.getMethod("setDefaultRequestConfig", requestConfigClass)
                        .invoke(httpClientBuilder, requestConfig);

                // Build HttpClient
                Object httpClient = httpClientBuilderClass.getMethod("build").invoke(httpClientBuilder);

                log.debug("Created Apache HttpClient 5 with connection pool: maxTotal={}, maxPerRoute={}, timeToLive={}s, connectTimeout={}s, readTimeout={}s, callTimeout={}s",
                        settings.getMaxRequests(), settings.getMaxRequestsPerHost(),
                        settings.getKeepAliveSeconds(), settings.getConnectTimeoutSeconds(),
                        effectiveReadTimeout, settings.getCallTimeoutSeconds());

                return httpClient;
            } catch (Exception e) {
                log.warn("Failed to create Apache HttpClient 5, falling back to default: {}", e.getMessage());
                // Cache the negative result to avoid repeated reflection attempts
                return UNAVAILABLE_SENTINEL;
            }
        });
    }

    /**
     * Close all cached HttpClient instances and release resources.
     * This method is idempotent and thread-safe.
     */
    @Override
    public void close() throws IOException {
        if (cachedHttpClients.isEmpty()) {
            return;
        }

        List<Exception> exceptions = new ArrayList<>();
        for (Map.Entry<PoolSettings, Object> entry : cachedHttpClients.entrySet()) {
            Object httpClient = entry.getValue();
            // Skip sentinel values
            if (httpClient != null && httpClient != UNAVAILABLE_SENTINEL) {
                try {
                    // CloseableHttpClient.close()
                    httpClient.getClass().getMethod("close").invoke(httpClient);
                    log.debug("Closed Apache HttpClient 5 for settings: {}", entry.getKey());
                } catch (Exception e) {
                    log.warn("Failed to close Apache HttpClient 5 for settings {}: {}",
                            entry.getKey(), e.getMessage());
                    exceptions.add(e);
                }
            }
        }
        cachedHttpClients.clear();

        // If any closures failed, throw IOException with all exceptions as suppressed
        if (!exceptions.isEmpty()) {
            IOException ex = new IOException("Failed to close one or more HttpClient instances");
            exceptions.forEach(ex::addSuppressed);
            throw ex;
        }
    }
}
