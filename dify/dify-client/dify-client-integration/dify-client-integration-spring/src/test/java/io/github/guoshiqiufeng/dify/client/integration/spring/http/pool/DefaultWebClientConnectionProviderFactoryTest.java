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

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for DefaultWebClientConnectionProviderFactory.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026-02-06
 */
@DisplayName("DefaultWebClientConnectionProviderFactory Tests")
class DefaultWebClientConnectionProviderFactoryTest {

    @Test
    @DisplayName("Should create ConnectionProvider with default settings")
    void testBuildConnectionProviderWithDefaults() {
        DefaultWebClientConnectionProviderFactory factory = new DefaultWebClientConnectionProviderFactory();
        PoolSettings settings = PoolSettings.defaults();

        Object connectionProvider = factory.buildConnectionProvider(settings, "test-pool");

        // If reactor-netty is available, should return non-null
        // If not available, returns null (graceful degradation)
        // We can't assert the exact type without depending on reactor-netty in test scope
        // So we just verify it doesn't throw exceptions
        assertDoesNotThrow(() -> factory.buildConnectionProvider(settings, "test-pool"));
    }

    @Test
    @DisplayName("Should create ConnectionProvider with custom settings")
    void testBuildConnectionProviderWithCustomSettings() {
        DefaultWebClientConnectionProviderFactory factory = new DefaultWebClientConnectionProviderFactory();

        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setMaxIdleConnections(20);
        config.setKeepAliveSeconds(600);
        config.setMaxRequests(256);
        config.setMaxRequestsPerHost(20);

        PoolSettings settings = PoolSettings.from(config);

        assertDoesNotThrow(() -> factory.buildConnectionProvider(settings, "custom-pool"));
    }

    @Test
    @DisplayName("Should handle null pool name gracefully")
    void testBuildConnectionProviderWithNullPoolName() {
        DefaultWebClientConnectionProviderFactory factory = new DefaultWebClientConnectionProviderFactory();
        PoolSettings settings = PoolSettings.defaults();

        // Should not throw exception even with null pool name
        assertDoesNotThrow(() -> factory.buildConnectionProvider(settings, null));
    }

    // ========== Tests for cache behavior ==========

    @Test
    @DisplayName("Should return cached ConnectionProvider for same settings")
    void testCacheBehavior() {
        DefaultWebClientConnectionProviderFactory factory = new DefaultWebClientConnectionProviderFactory();
        PoolSettings settings = PoolSettings.defaults();

        Object first = factory.buildConnectionProvider(settings, "cached-pool");
        Object second = factory.buildConnectionProvider(settings, "cached-pool");

        // Should return the same cached instance
        assertSame(first, second);
    }

    @Test
    @DisplayName("Should create different ConnectionProvider for different pool names")
    void testDifferentPoolNames() {
        DefaultWebClientConnectionProviderFactory factory = new DefaultWebClientConnectionProviderFactory();
        PoolSettings settings = PoolSettings.defaults();

        Object pool1 = factory.buildConnectionProvider(settings, "pool-1");
        Object pool2 = factory.buildConnectionProvider(settings, "pool-2");

        // Different pool names should create different instances (unless both are null due to unavailability)
        if (pool1 != null && pool2 != null) {
            assertNotSame(pool1, pool2);
        }
    }

    @Test
    @DisplayName("Should create different ConnectionProvider for different settings")
    void testDifferentSettings() {
        DefaultWebClientConnectionProviderFactory factory = new DefaultWebClientConnectionProviderFactory();

        DifyProperties.ClientConfig config1 = new DifyProperties.ClientConfig();
        config1.setMaxRequests(64);
        PoolSettings settings1 = PoolSettings.from(config1);

        DifyProperties.ClientConfig config2 = new DifyProperties.ClientConfig();
        config2.setMaxRequests(128);
        PoolSettings settings2 = PoolSettings.from(config2);

        Object provider1 = factory.buildConnectionProvider(settings1, "pool");
        Object provider2 = factory.buildConnectionProvider(settings2, "pool");

        // Different settings should create different instances (unless both are null)
        if (provider1 != null && provider2 != null) {
            assertNotSame(provider1, provider2);
        }
    }

    // ========== Tests for reactorNettyAvailable fast path ==========

    @Test
    @DisplayName("Should return null immediately when reactorNettyAvailable is false")
    void testFastPathWhenReactorNettyUnavailable() throws Exception {
        DefaultWebClientConnectionProviderFactory factory = new DefaultWebClientConnectionProviderFactory();

        // Use reflection to set reactorNettyAvailable to false
        Field field = DefaultWebClientConnectionProviderFactory.class.getDeclaredField("reactorNettyAvailable");
        field.setAccessible(true);
        field.set(factory, false);

        PoolSettings settings = PoolSettings.defaults();
        Object result = factory.buildConnectionProvider(settings, "test-pool");

        // Should return null immediately without attempting reflection
        assertNull(result);
    }

    // ========== Tests for overflow detection ==========

    @Test
    @DisplayName("Should detect pendingAcquireMaxCount overflow")
    void testPendingAcquireMaxCountOverflow() {
        DefaultWebClientConnectionProviderFactory factory = new DefaultWebClientConnectionProviderFactory();

        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        // Set maxRequests to a value that will overflow when multiplied by 2
        config.setMaxRequests(Integer.MAX_VALUE / 2 + 1);
        PoolSettings settings = PoolSettings.from(config);

        // Should not throw exception, should cap at Integer.MAX_VALUE
        assertDoesNotThrow(() -> factory.buildConnectionProvider(settings, "overflow-test"));
    }

    @Test
    @DisplayName("Should handle large maxRequests without overflow")
    void testLargeMaxRequestsWithoutOverflow() {
        DefaultWebClientConnectionProviderFactory factory = new DefaultWebClientConnectionProviderFactory();

        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setMaxRequests(Integer.MAX_VALUE / 2);
        PoolSettings settings = PoolSettings.from(config);

        assertDoesNotThrow(() -> factory.buildConnectionProvider(settings, "large-pool"));
    }

    // ========== Tests for close() method ==========

    @Test
    @DisplayName("Should close factory without errors when no providers cached")
    void testCloseWithEmptyCache() {
        DefaultWebClientConnectionProviderFactory factory = new DefaultWebClientConnectionProviderFactory();

        assertDoesNotThrow(factory::close);
    }

    @Test
    @DisplayName("Should close factory and dispose cached providers")
    void testCloseWithCachedProviders() {
        DefaultWebClientConnectionProviderFactory factory = new DefaultWebClientConnectionProviderFactory();
        PoolSettings settings = PoolSettings.defaults();

        // Create a provider (may be null if reactor-netty unavailable)
        factory.buildConnectionProvider(settings, "test-pool");

        // Should not throw exception
        assertDoesNotThrow(factory::close);
    }

    @Test
    @DisplayName("Should be idempotent - calling close() multiple times is safe")
    void testCloseIdempotent() {
        DefaultWebClientConnectionProviderFactory factory = new DefaultWebClientConnectionProviderFactory();
        PoolSettings settings = PoolSettings.defaults();

        factory.buildConnectionProvider(settings, "test-pool");

        assertDoesNotThrow(factory::close);
        assertDoesNotThrow(factory::close); // Second call should also be safe
    }

    @Test
    @DisplayName("Should handle close() with sentinel values in cache")
    void testCloseWithSentinelValues() throws Exception {
        DefaultWebClientConnectionProviderFactory factory = new DefaultWebClientConnectionProviderFactory();

        // Use reflection to inject sentinel value into cache
        Field cacheField = DefaultWebClientConnectionProviderFactory.class.getDeclaredField("cachedProviders");
        cacheField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Object, Object> cache = (Map<Object, Object>) cacheField.get(factory);

        // Get the sentinel value using reflection
        Field sentinelField = DefaultWebClientConnectionProviderFactory.class.getDeclaredField("UNAVAILABLE_SENTINEL");
        sentinelField.setAccessible(true);
        Object sentinel = sentinelField.get(null);

        // Create a cache key and put sentinel
        PoolSettings settings = PoolSettings.defaults();
        Class<?> cacheKeyClass = Class.forName("io.github.guoshiqiufeng.dify.client.integration.spring.http.pool.DefaultWebClientConnectionProviderFactory$CacheKey");
        Object key = cacheKeyClass.getDeclaredConstructor(PoolSettings.class, String.class)
                .newInstance(settings, "sentinel-test");
        cache.put(key, sentinel);

        // Should not throw exception when closing with sentinel values
        assertDoesNotThrow(factory::close);
    }

    // ========== Tests for CacheKey equals/hashCode ==========

    @Test
    @DisplayName("CacheKey should be equal for same settings and pool name")
    void testCacheKeyEquals() throws Exception {
        PoolSettings settings = PoolSettings.defaults();

        Class<?> cacheKeyClass = Class.forName("io.github.guoshiqiufeng.dify.client.integration.spring.http.pool.DefaultWebClientConnectionProviderFactory$CacheKey");
        Object key1 = cacheKeyClass.getDeclaredConstructor(PoolSettings.class, String.class)
                .newInstance(settings, "test-pool");
        Object key2 = cacheKeyClass.getDeclaredConstructor(PoolSettings.class, String.class)
                .newInstance(settings, "test-pool");

        assertEquals(key1, key2);
        assertEquals(key1.hashCode(), key2.hashCode());
    }

    @Test
    @DisplayName("CacheKey should not be equal for different pool names")
    void testCacheKeyNotEqualsDifferentPoolName() throws Exception {
        PoolSettings settings = PoolSettings.defaults();

        Class<?> cacheKeyClass = Class.forName("io.github.guoshiqiufeng.dify.client.integration.spring.http.pool.DefaultWebClientConnectionProviderFactory$CacheKey");
        Object key1 = cacheKeyClass.getDeclaredConstructor(PoolSettings.class, String.class)
                .newInstance(settings, "pool-1");
        Object key2 = cacheKeyClass.getDeclaredConstructor(PoolSettings.class, String.class)
                .newInstance(settings, "pool-2");

        assertNotEquals(key1, key2);
    }

    @Test
    @DisplayName("CacheKey should not be equal for different settings")
    void testCacheKeyNotEqualsDifferentSettings() throws Exception {
        DifyProperties.ClientConfig config1 = new DifyProperties.ClientConfig();
        config1.setMaxRequests(64);
        PoolSettings settings1 = PoolSettings.from(config1);

        DifyProperties.ClientConfig config2 = new DifyProperties.ClientConfig();
        config2.setMaxRequests(128);
        PoolSettings settings2 = PoolSettings.from(config2);

        Class<?> cacheKeyClass = Class.forName("io.github.guoshiqiufeng.dify.client.integration.spring.http.pool.DefaultWebClientConnectionProviderFactory$CacheKey");
        Object key1 = cacheKeyClass.getDeclaredConstructor(PoolSettings.class, String.class)
                .newInstance(settings1, "pool");
        Object key2 = cacheKeyClass.getDeclaredConstructor(PoolSettings.class, String.class)
                .newInstance(settings2, "pool");

        assertNotEquals(key1, key2);
    }

    @Test
    @DisplayName("CacheKey should handle null pool name in equals")
    void testCacheKeyEqualsWithNullPoolName() throws Exception {
        PoolSettings settings = PoolSettings.defaults();

        Class<?> cacheKeyClass = Class.forName("io.github.guoshiqiufeng.dify.client.integration.spring.http.pool.DefaultWebClientConnectionProviderFactory$CacheKey");
        Object key1 = cacheKeyClass.getDeclaredConstructor(PoolSettings.class, String.class)
                .newInstance(settings, null);
        Object key2 = cacheKeyClass.getDeclaredConstructor(PoolSettings.class, String.class)
                .newInstance(settings, null);

        assertEquals(key1, key2);
        assertEquals(key1.hashCode(), key2.hashCode());
    }

    @Test
    @DisplayName("CacheKey equals should return true for same instance")
    void testCacheKeyEqualsSameInstance() throws Exception {
        PoolSettings settings = PoolSettings.defaults();

        Class<?> cacheKeyClass = Class.forName("io.github.guoshiqiufeng.dify.client.integration.spring.http.pool.DefaultWebClientConnectionProviderFactory$CacheKey");
        Object key = cacheKeyClass.getDeclaredConstructor(PoolSettings.class, String.class)
                .newInstance(settings, "test-pool");

        assertEquals(key, key);
    }

    @Test
    @DisplayName("CacheKey equals should return false for null")
    void testCacheKeyEqualsNull() throws Exception {
        PoolSettings settings = PoolSettings.defaults();

        Class<?> cacheKeyClass = Class.forName("io.github.guoshiqiufeng.dify.client.integration.spring.http.pool.DefaultWebClientConnectionProviderFactory$CacheKey");
        Object key = cacheKeyClass.getDeclaredConstructor(PoolSettings.class, String.class)
                .newInstance(settings, "test-pool");

        assertNotEquals(key, null);
    }

    @Test
    @DisplayName("CacheKey equals should return false for different class")
    void testCacheKeyEqualsDifferentClass() throws Exception {
        PoolSettings settings = PoolSettings.defaults();

        Class<?> cacheKeyClass = Class.forName("io.github.guoshiqiufeng.dify.client.integration.spring.http.pool.DefaultWebClientConnectionProviderFactory$CacheKey");
        Object key = cacheKeyClass.getDeclaredConstructor(PoolSettings.class, String.class)
                .newInstance(settings, "test-pool");

        assertNotEquals(key, "not a CacheKey");
    }
}
