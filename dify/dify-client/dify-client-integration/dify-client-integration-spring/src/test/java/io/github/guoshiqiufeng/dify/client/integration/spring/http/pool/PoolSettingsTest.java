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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for PoolSettings.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026-02-06
 */
@DisplayName("PoolSettings Tests")
class PoolSettingsTest {

    @Test
    @DisplayName("Should create PoolSettings with default values when config is null")
    void testFromNullConfig() {
        PoolSettings settings = PoolSettings.from(null);

        assertEquals(5, settings.getMaxIdleConnections());
        assertEquals(300, settings.getKeepAliveSeconds());
        assertEquals(64, settings.getMaxRequests());
        assertEquals(5, settings.getMaxRequestsPerHost());
        assertEquals(0, settings.getCallTimeoutSeconds());
    }

    @Test
    @DisplayName("Should create PoolSettings with default values")
    void testDefaults() {
        PoolSettings settings = PoolSettings.defaults();

        assertEquals(5, settings.getMaxIdleConnections());
        assertEquals(300, settings.getKeepAliveSeconds());
        assertEquals(64, settings.getMaxRequests());
        assertEquals(5, settings.getMaxRequestsPerHost());
        assertEquals(0, settings.getCallTimeoutSeconds());
    }

    @Test
    @DisplayName("Should create PoolSettings from ClientConfig with custom values")
    void testFromConfigWithCustomValues() {
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setMaxIdleConnections(10);
        config.setKeepAliveSeconds(600);
        config.setMaxRequests(128);
        config.setMaxRequestsPerHost(10);
        config.setCallTimeout(60);

        PoolSettings settings = PoolSettings.from(config);

        assertEquals(10, settings.getMaxIdleConnections());
        assertEquals(600, settings.getKeepAliveSeconds());
        assertEquals(128, settings.getMaxRequests());
        assertEquals(10, settings.getMaxRequestsPerHost());
        assertEquals(60, settings.getCallTimeoutSeconds());
    }

    @Test
    @DisplayName("Should use default values when ClientConfig fields are null")
    void testFromConfigWithNullFields() {
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        // All fields are null

        PoolSettings settings = PoolSettings.from(config);

        assertEquals(5, settings.getMaxIdleConnections());
        assertEquals(300, settings.getKeepAliveSeconds());
        assertEquals(64, settings.getMaxRequests());
        assertEquals(5, settings.getMaxRequestsPerHost());
        assertEquals(0, settings.getCallTimeoutSeconds());
    }

    @Test
    @DisplayName("Should create PoolSettings with partial custom values")
    void testFromConfigWithPartialValues() {
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setMaxIdleConnections(20);
        config.setMaxRequests(256);
        // Other fields are null, should use defaults

        PoolSettings settings = PoolSettings.from(config);

        assertEquals(20, settings.getMaxIdleConnections());
        assertEquals(300, settings.getKeepAliveSeconds()); // default
        assertEquals(256, settings.getMaxRequests());
        assertEquals(5, settings.getMaxRequestsPerHost()); // default
        assertEquals(0, settings.getCallTimeoutSeconds()); // default
    }

    @Test
    @DisplayName("Should have meaningful toString output")
    void testToString() {
        PoolSettings settings = PoolSettings.defaults();
        String str = settings.toString();

        assertTrue(str.contains("maxIdleConnections=5"));
        assertTrue(str.contains("keepAliveSeconds=300"));
        assertTrue(str.contains("maxRequests=64"));
        assertTrue(str.contains("maxRequestsPerHost=5"));
        assertTrue(str.contains("callTimeoutSeconds=0"));
    }

    // ========== Tests for validatePositive() edge cases ==========

    @Test
    @DisplayName("Should use default when maxIdleConnections is negative")
    void testNegativeMaxIdleConnections() {
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setMaxIdleConnections(-1);

        PoolSettings settings = PoolSettings.from(config);

        assertEquals(5, settings.getMaxIdleConnections()); // default
    }

    @Test
    @DisplayName("Should use default when maxIdleConnections is zero")
    void testZeroMaxIdleConnections() {
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setMaxIdleConnections(0);

        PoolSettings settings = PoolSettings.from(config);

        assertEquals(5, settings.getMaxIdleConnections()); // default
    }

    @Test
    @DisplayName("Should use default when keepAliveSeconds is negative")
    void testNegativeKeepAliveSeconds() {
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setKeepAliveSeconds(-100);

        PoolSettings settings = PoolSettings.from(config);

        assertEquals(300, settings.getKeepAliveSeconds()); // default
    }

    @Test
    @DisplayName("Should use default when keepAliveSeconds is zero")
    void testZeroKeepAliveSeconds() {
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setKeepAliveSeconds(0);

        PoolSettings settings = PoolSettings.from(config);

        assertEquals(300, settings.getKeepAliveSeconds()); // default
    }

    @Test
    @DisplayName("Should use default when maxRequests is negative")
    void testNegativeMaxRequests() {
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setMaxRequests(-10);

        PoolSettings settings = PoolSettings.from(config);

        assertEquals(64, settings.getMaxRequests()); // default
    }

    @Test
    @DisplayName("Should use default when maxRequests is zero")
    void testZeroMaxRequests() {
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setMaxRequests(0);

        PoolSettings settings = PoolSettings.from(config);

        assertEquals(64, settings.getMaxRequests()); // default
    }

    @Test
    @DisplayName("Should use default when maxRequestsPerHost is negative")
    void testNegativeMaxRequestsPerHost() {
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setMaxRequestsPerHost(-5);

        PoolSettings settings = PoolSettings.from(config);

        assertEquals(5, settings.getMaxRequestsPerHost()); // default
    }

    @Test
    @DisplayName("Should use default when maxRequestsPerHost is zero")
    void testZeroMaxRequestsPerHost() {
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setMaxRequestsPerHost(0);

        PoolSettings settings = PoolSettings.from(config);

        assertEquals(5, settings.getMaxRequestsPerHost()); // default
    }

    @Test
    @DisplayName("Should use default when connectTimeout is negative")
    void testNegativeConnectTimeout() {
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setConnectTimeout(-30);

        PoolSettings settings = PoolSettings.from(config);

        assertEquals(30, settings.getConnectTimeoutSeconds()); // default
    }

    @Test
    @DisplayName("Should use default when connectTimeout is zero")
    void testZeroConnectTimeout() {
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setConnectTimeout(0);

        PoolSettings settings = PoolSettings.from(config);

        assertEquals(30, settings.getConnectTimeoutSeconds()); // default
    }

    @Test
    @DisplayName("Should use default when readTimeout is negative")
    void testNegativeReadTimeout() {
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setReadTimeout(-60);

        PoolSettings settings = PoolSettings.from(config);

        assertEquals(30, settings.getReadTimeoutSeconds()); // default
    }

    @Test
    @DisplayName("Should use default when readTimeout is zero")
    void testZeroReadTimeout() {
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setReadTimeout(0);

        PoolSettings settings = PoolSettings.from(config);

        assertEquals(30, settings.getReadTimeoutSeconds()); // default
    }

    // ========== Tests for callTimeout special handling (can be 0) ==========

    @Test
    @DisplayName("Should allow callTimeout to be zero (no limit)")
    void testZeroCallTimeout() {
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setCallTimeout(0);

        PoolSettings settings = PoolSettings.from(config);

        assertEquals(0, settings.getCallTimeoutSeconds()); // 0 is valid
    }

    @Test
    @DisplayName("Should normalize negative callTimeout to zero")
    void testNegativeCallTimeout() {
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setCallTimeout(-100);

        PoolSettings settings = PoolSettings.from(config);

        assertEquals(0, settings.getCallTimeoutSeconds()); // normalized to 0
    }

    @Test
    @DisplayName("Should accept positive callTimeout")
    void testPositiveCallTimeout() {
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setCallTimeout(120);

        PoolSettings settings = PoolSettings.from(config);

        assertEquals(120, settings.getCallTimeoutSeconds());
    }

    // ========== Tests for equals() and hashCode() ==========

    @Test
    @DisplayName("Should be equal to itself")
    void testEqualsSameInstance() {
        PoolSettings settings = PoolSettings.defaults();

        assertEquals(settings, settings);
    }

    @Test
    @DisplayName("Should be equal to another instance with same values")
    void testEqualsSameValues() {
        PoolSettings settings1 = PoolSettings.defaults();
        PoolSettings settings2 = PoolSettings.defaults();

        assertEquals(settings1, settings2);
        assertEquals(settings1.hashCode(), settings2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal to null")
    void testEqualsNull() {
        PoolSettings settings = PoolSettings.defaults();

        assertNotEquals(settings, null);
    }

    @Test
    @DisplayName("Should not be equal to different class")
    void testEqualsDifferentClass() {
        PoolSettings settings = PoolSettings.defaults();
        String other = "not a PoolSettings";

        assertNotEquals(settings, other);
    }

    @Test
    @DisplayName("Should not be equal when maxIdleConnections differs")
    void testNotEqualsMaxIdleConnections() {
        DifyProperties.ClientConfig config1 = new DifyProperties.ClientConfig();
        config1.setMaxIdleConnections(5);

        DifyProperties.ClientConfig config2 = new DifyProperties.ClientConfig();
        config2.setMaxIdleConnections(10);

        PoolSettings settings1 = PoolSettings.from(config1);
        PoolSettings settings2 = PoolSettings.from(config2);

        assertNotEquals(settings1, settings2);
        assertNotEquals(settings1.hashCode(), settings2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when keepAliveSeconds differs")
    void testNotEqualsKeepAliveSeconds() {
        DifyProperties.ClientConfig config1 = new DifyProperties.ClientConfig();
        config1.setKeepAliveSeconds(300);

        DifyProperties.ClientConfig config2 = new DifyProperties.ClientConfig();
        config2.setKeepAliveSeconds(600);

        PoolSettings settings1 = PoolSettings.from(config1);
        PoolSettings settings2 = PoolSettings.from(config2);

        assertNotEquals(settings1, settings2);
    }

    @Test
    @DisplayName("Should not be equal when maxRequests differs")
    void testNotEqualsMaxRequests() {
        DifyProperties.ClientConfig config1 = new DifyProperties.ClientConfig();
        config1.setMaxRequests(64);

        DifyProperties.ClientConfig config2 = new DifyProperties.ClientConfig();
        config2.setMaxRequests(128);

        PoolSettings settings1 = PoolSettings.from(config1);
        PoolSettings settings2 = PoolSettings.from(config2);

        assertNotEquals(settings1, settings2);
    }

    @Test
    @DisplayName("Should not be equal when maxRequestsPerHost differs")
    void testNotEqualsMaxRequestsPerHost() {
        DifyProperties.ClientConfig config1 = new DifyProperties.ClientConfig();
        config1.setMaxRequestsPerHost(5);

        DifyProperties.ClientConfig config2 = new DifyProperties.ClientConfig();
        config2.setMaxRequestsPerHost(10);

        PoolSettings settings1 = PoolSettings.from(config1);
        PoolSettings settings2 = PoolSettings.from(config2);

        assertNotEquals(settings1, settings2);
    }

    @Test
    @DisplayName("Should not be equal when callTimeoutSeconds differs")
    void testNotEqualsCallTimeoutSeconds() {
        DifyProperties.ClientConfig config1 = new DifyProperties.ClientConfig();
        config1.setCallTimeout(0);

        DifyProperties.ClientConfig config2 = new DifyProperties.ClientConfig();
        config2.setCallTimeout(60);

        PoolSettings settings1 = PoolSettings.from(config1);
        PoolSettings settings2 = PoolSettings.from(config2);

        assertNotEquals(settings1, settings2);
    }

    @Test
    @DisplayName("Should not be equal when connectTimeoutSeconds differs")
    void testNotEqualsConnectTimeoutSeconds() {
        DifyProperties.ClientConfig config1 = new DifyProperties.ClientConfig();
        config1.setConnectTimeout(30);

        DifyProperties.ClientConfig config2 = new DifyProperties.ClientConfig();
        config2.setConnectTimeout(60);

        PoolSettings settings1 = PoolSettings.from(config1);
        PoolSettings settings2 = PoolSettings.from(config2);

        assertNotEquals(settings1, settings2);
    }

    @Test
    @DisplayName("Should not be equal when readTimeoutSeconds differs")
    void testNotEqualsReadTimeoutSeconds() {
        DifyProperties.ClientConfig config1 = new DifyProperties.ClientConfig();
        config1.setReadTimeout(30);

        DifyProperties.ClientConfig config2 = new DifyProperties.ClientConfig();
        config2.setReadTimeout(90);

        PoolSettings settings1 = PoolSettings.from(config1);
        PoolSettings settings2 = PoolSettings.from(config2);

        assertNotEquals(settings1, settings2);
    }

    @Test
    @DisplayName("Should have consistent hashCode for equal objects")
    void testHashCodeConsistency() {
        PoolSettings settings1 = PoolSettings.defaults();
        PoolSettings settings2 = PoolSettings.defaults();

        assertEquals(settings1.hashCode(), settings2.hashCode());
    }

    @Test
    @DisplayName("Should have different hashCode for different objects")
    void testHashCodeDifferent() {
        DifyProperties.ClientConfig config1 = new DifyProperties.ClientConfig();
        config1.setMaxIdleConnections(5);

        DifyProperties.ClientConfig config2 = new DifyProperties.ClientConfig();
        config2.setMaxIdleConnections(10);

        PoolSettings settings1 = PoolSettings.from(config1);
        PoolSettings settings2 = PoolSettings.from(config2);

        assertNotEquals(settings1.hashCode(), settings2.hashCode());
    }

    // ========== Tests for multiple invalid values ==========

    @Test
    @DisplayName("Should handle multiple invalid values")
    void testMultipleInvalidValues() {
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setMaxIdleConnections(-1);
        config.setKeepAliveSeconds(0);
        config.setMaxRequests(-10);
        config.setMaxRequestsPerHost(0);
        config.setCallTimeout(-50);
        config.setConnectTimeout(-30);
        config.setReadTimeout(0);

        PoolSettings settings = PoolSettings.from(config);

        // All should use defaults
        assertEquals(5, settings.getMaxIdleConnections());
        assertEquals(300, settings.getKeepAliveSeconds());
        assertEquals(64, settings.getMaxRequests());
        assertEquals(5, settings.getMaxRequestsPerHost());
        assertEquals(0, settings.getCallTimeoutSeconds()); // normalized to 0
        assertEquals(30, settings.getConnectTimeoutSeconds());
        assertEquals(30, settings.getReadTimeoutSeconds());
    }
}
