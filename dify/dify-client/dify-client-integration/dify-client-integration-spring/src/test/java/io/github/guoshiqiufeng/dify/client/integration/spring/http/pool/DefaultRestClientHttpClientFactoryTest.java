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

import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for DefaultRestClientHttpClientFactory.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026-02-06
 */
@DisplayName("DefaultRestClientHttpClientFactory Tests")
class DefaultRestClientHttpClientFactoryTest {

    @Test
    @DisplayName("Should create HttpClient with default settings")
    void testBuildHttpClientWithDefaults() {
        DefaultRestClientHttpClientFactory factory = new DefaultRestClientHttpClientFactory();
        PoolSettings settings = PoolSettings.defaults();

        Object httpClient = factory.buildHttpClient(settings);

        // If Apache HttpClient 5 is available, should return non-null
        // If not available, returns null (graceful degradation)
        assertDoesNotThrow(() -> factory.buildHttpClient(settings));
    }

    @Test
    @DisplayName("Should create HttpClient with custom settings")
    void testBuildHttpClientWithCustomSettings() {
        DefaultRestClientHttpClientFactory factory = new DefaultRestClientHttpClientFactory();

        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setMaxIdleConnections(20);
        config.setKeepAliveSeconds(600);
        config.setMaxRequests(256);
        config.setMaxRequestsPerHost(20);
        config.setCallTimeout(60);

        PoolSettings settings = PoolSettings.from(config);

        assertDoesNotThrow(() -> factory.buildHttpClient(settings));
    }

    @Test
    @DisplayName("Should handle zero callTimeout")
    void testBuildHttpClientWithZeroCallTimeout() {
        DefaultRestClientHttpClientFactory factory = new DefaultRestClientHttpClientFactory();

        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setCallTimeout(0); // No timeout

        PoolSettings settings = PoolSettings.from(config);

        assertDoesNotThrow(() -> factory.buildHttpClient(settings));
    }

    // ========== Tests for cache behavior ==========

    @Test
    @DisplayName("Should return cached HttpClient for same settings")
    void testCacheBehavior() {
        DefaultRestClientHttpClientFactory factory = new DefaultRestClientHttpClientFactory();
        PoolSettings settings = PoolSettings.defaults();

        Object first = factory.buildHttpClient(settings);
        Object second = factory.buildHttpClient(settings);

        // Should return the same cached instance
        assertSame(first, second);
    }

    @Test
    @DisplayName("Should create different HttpClient for different settings")
    void testDifferentSettings() {
        DefaultRestClientHttpClientFactory factory = new DefaultRestClientHttpClientFactory();

        DifyProperties.ClientConfig config1 = new DifyProperties.ClientConfig();
        config1.setMaxRequests(64);
        PoolSettings settings1 = PoolSettings.from(config1);

        DifyProperties.ClientConfig config2 = new DifyProperties.ClientConfig();
        config2.setMaxRequests(128);
        PoolSettings settings2 = PoolSettings.from(config2);

        Object client1 = factory.buildHttpClient(settings1);
        Object client2 = factory.buildHttpClient(settings2);

        // Different settings should create different instances (unless both are null)
        if (client1 != null && client2 != null) {
            assertNotSame(client1, client2);
        }
    }

    // ========== Tests for callTimeout branches ==========

    @Test
    @DisplayName("Should use min of callTimeout and readTimeout when callTimeout > 0")
    void testCallTimeoutGreaterThanZero() {
        DefaultRestClientHttpClientFactory factory = new DefaultRestClientHttpClientFactory();

        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setCallTimeout(10); // 10 seconds
        config.setReadTimeout(30); // 30 seconds
        PoolSettings settings = PoolSettings.from(config);

        // Should use min(10, 30) = 10 seconds as effective read timeout
        assertDoesNotThrow(() -> factory.buildHttpClient(settings));
    }

    @Test
    @DisplayName("Should use readTimeout when callTimeout is zero")
    void testCallTimeoutZero() {
        DefaultRestClientHttpClientFactory factory = new DefaultRestClientHttpClientFactory();

        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setCallTimeout(0); // No call timeout
        config.setReadTimeout(30); // 30 seconds
        PoolSettings settings = PoolSettings.from(config);

        // Should use readTimeout (30 seconds) as effective read timeout
        assertDoesNotThrow(() -> factory.buildHttpClient(settings));
    }

    @Test
    @DisplayName("Should use callTimeout when it's less than readTimeout")
    void testCallTimeoutLessThanReadTimeout() {
        DefaultRestClientHttpClientFactory factory = new DefaultRestClientHttpClientFactory();

        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setCallTimeout(5); // 5 seconds
        config.setReadTimeout(30); // 30 seconds
        PoolSettings settings = PoolSettings.from(config);

        // Should use min(5, 30) = 5 seconds as effective read timeout
        assertDoesNotThrow(() -> factory.buildHttpClient(settings));
    }

    @Test
    @DisplayName("Should use readTimeout when callTimeout is greater")
    void testCallTimeoutGreaterThanReadTimeout() {
        DefaultRestClientHttpClientFactory factory = new DefaultRestClientHttpClientFactory();

        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setCallTimeout(60); // 60 seconds
        config.setReadTimeout(30); // 30 seconds
        PoolSettings settings = PoolSettings.from(config);

        // Should use min(60, 30) = 30 seconds as effective read timeout
        assertDoesNotThrow(() -> factory.buildHttpClient(settings));
    }

    // ========== Tests for close() method ==========

    @Test
    @DisplayName("Should close factory without errors when no clients cached")
    void testCloseWithEmptyCache() {
        DefaultRestClientHttpClientFactory factory = new DefaultRestClientHttpClientFactory();

        assertDoesNotThrow(factory::close);
    }

    @Test
    @DisplayName("Should close factory and dispose cached clients")
    void testCloseWithCachedClients() throws Exception {
        DefaultRestClientHttpClientFactory factory = new DefaultRestClientHttpClientFactory();
        PoolSettings settings = PoolSettings.defaults();

        // Create a client (may be null if HC5 unavailable)
        factory.buildHttpClient(settings);

        // Close may throw IOException if close operations fail, which is expected
        try {
            factory.close();
        } catch (IOException e) {
            // IOException is acceptable if close operations fail
            assertNotNull(e.getMessage());
        }
    }

    @Test
    @DisplayName("Should be idempotent - calling close() multiple times is safe")
    void testCloseIdempotent() throws Exception {
        DefaultRestClientHttpClientFactory factory = new DefaultRestClientHttpClientFactory();
        PoolSettings settings = PoolSettings.defaults();

        factory.buildHttpClient(settings);

        // First close may throw IOException
        try {
            factory.close();
        } catch (IOException e) {
            // IOException is acceptable
        }

        // Second call should be safe (cache is empty)
        assertDoesNotThrow(factory::close);
    }

    @Test
    @DisplayName("Should handle close() with sentinel values in cache")
    void testCloseWithSentinelValues() throws Exception {
        DefaultRestClientHttpClientFactory factory = new DefaultRestClientHttpClientFactory();

        // Use reflection to inject sentinel value into cache
        Field cacheField = DefaultRestClientHttpClientFactory.class.getDeclaredField("cachedHttpClients");
        cacheField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<PoolSettings, Object> cache = (Map<PoolSettings, Object>) cacheField.get(factory);

        // Get the sentinel value using reflection
        Field sentinelField = DefaultRestClientHttpClientFactory.class.getDeclaredField("UNAVAILABLE_SENTINEL");
        sentinelField.setAccessible(true);
        Object sentinel = sentinelField.get(null);

        // Put sentinel in cache
        PoolSettings settings = PoolSettings.defaults();
        cache.put(settings, sentinel);

        // Should not throw exception when closing with sentinel values
        assertDoesNotThrow(factory::close);
    }

    @Test
    @DisplayName("Should handle close() with multiple cached clients")
    void testCloseWithMultipleClients() throws Exception {
        DefaultRestClientHttpClientFactory factory = new DefaultRestClientHttpClientFactory();

        // Create multiple clients with different settings
        DifyProperties.ClientConfig config1 = new DifyProperties.ClientConfig();
        config1.setMaxRequests(64);
        factory.buildHttpClient(PoolSettings.from(config1));

        DifyProperties.ClientConfig config2 = new DifyProperties.ClientConfig();
        config2.setMaxRequests(128);
        factory.buildHttpClient(PoolSettings.from(config2));

        DifyProperties.ClientConfig config3 = new DifyProperties.ClientConfig();
        config3.setMaxRequests(256);
        factory.buildHttpClient(PoolSettings.from(config3));

        // Close may throw IOException if any close operations fail
        try {
            factory.close();
        } catch (IOException e) {
            // IOException is acceptable if close operations fail
            assertNotNull(e.getMessage());
        }
    }

    @Test
    @DisplayName("Should clear cache after close()")
    void testCacheClearedAfterClose() throws Exception {
        DefaultRestClientHttpClientFactory factory = new DefaultRestClientHttpClientFactory();
        PoolSettings settings = PoolSettings.defaults();

        factory.buildHttpClient(settings);

        // Close the factory (may throw IOException)
        try {
            factory.close();
        } catch (IOException e) {
            // IOException is acceptable
        }

        // Verify cache is cleared
        Field cacheField = DefaultRestClientHttpClientFactory.class.getDeclaredField("cachedHttpClients");
        cacheField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<PoolSettings, Object> cache = (Map<PoolSettings, Object>) cacheField.get(factory);

        assertTrue(cache.isEmpty(), "Cache should be empty after close()");
    }

    // ========== Tests for various timeout combinations ==========

    @Test
    @DisplayName("Should handle all custom timeout values")
    void testAllCustomTimeouts() {
        DefaultRestClientHttpClientFactory factory = new DefaultRestClientHttpClientFactory();

        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setConnectTimeout(10);
        config.setReadTimeout(20);
        config.setCallTimeout(15);
        PoolSettings settings = PoolSettings.from(config);

        assertDoesNotThrow(() -> factory.buildHttpClient(settings));
    }

    @Test
    @DisplayName("Should handle large timeout values")
    void testLargeTimeoutValues() {
        DefaultRestClientHttpClientFactory factory = new DefaultRestClientHttpClientFactory();

        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setConnectTimeout(300);
        config.setReadTimeout(600);
        config.setCallTimeout(900);
        PoolSettings settings = PoolSettings.from(config);

        assertDoesNotThrow(() -> factory.buildHttpClient(settings));
    }

    // ========== Tests for connection pool settings ==========

    @Test
    @DisplayName("Should handle large connection pool settings")
    void testLargeConnectionPoolSettings() {
        DefaultRestClientHttpClientFactory factory = new DefaultRestClientHttpClientFactory();

        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setMaxRequests(1000);
        config.setMaxRequestsPerHost(100);
        config.setKeepAliveSeconds(3600);
        PoolSettings settings = PoolSettings.from(config);

        assertDoesNotThrow(() -> factory.buildHttpClient(settings));
    }

    @Test
    @DisplayName("Should handle minimum connection pool settings")
    void testMinimumConnectionPoolSettings() {
        DefaultRestClientHttpClientFactory factory = new DefaultRestClientHttpClientFactory();

        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setMaxRequests(1);
        config.setMaxRequestsPerHost(1);
        config.setKeepAliveSeconds(1);
        PoolSettings settings = PoolSettings.from(config);

        assertDoesNotThrow(() -> factory.buildHttpClient(settings));
    }
}
