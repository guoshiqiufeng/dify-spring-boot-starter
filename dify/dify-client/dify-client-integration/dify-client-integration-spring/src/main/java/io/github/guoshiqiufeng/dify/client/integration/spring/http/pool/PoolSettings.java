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
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Connection pool settings extracted from DifyProperties.ClientConfig.
 * Provides normalized values with defaults matching OkHttp implementation.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026-02-06
 */
@Slf4j
@Getter
public final class PoolSettings {

    /**
     * Maximum number of idle connections in the pool.
     * Default: 5 (matches OkHttp default)
     */
    private final int maxIdleConnections;

    /**
     * Connection keep-alive time in seconds.
     * Default: 300 seconds (5 minutes, matches OkHttp default)
     */
    private final int keepAliveSeconds;

    /**
     * Maximum number of concurrent requests.
     * Default: 64 (matches OkHttp default)
     */
    private final int maxRequests;

    /**
     * Maximum number of concurrent requests per host.
     * Default: 5 (matches OkHttp default)
     */
    private final int maxRequestsPerHost;

    /**
     * Call timeout in seconds. 0 means no timeout.
     * Default: 0 (matches OkHttp default)
     */
    private final int callTimeoutSeconds;

    /**
     * Connection timeout in seconds.
     * Default: 30 seconds
     */
    private final int connectTimeoutSeconds;

    /**
     * Read timeout in seconds.
     * Default: 30 seconds
     */
    private final int readTimeoutSeconds;

    private PoolSettings(int maxIdleConnections, int keepAliveSeconds, int maxRequests,
                         int maxRequestsPerHost, int callTimeoutSeconds,
                         int connectTimeoutSeconds, int readTimeoutSeconds) {
        this.maxIdleConnections = maxIdleConnections;
        this.keepAliveSeconds = keepAliveSeconds;
        this.maxRequests = maxRequests;
        this.maxRequestsPerHost = maxRequestsPerHost;
        this.callTimeoutSeconds = callTimeoutSeconds;
        this.connectTimeoutSeconds = connectTimeoutSeconds;
        this.readTimeoutSeconds = readTimeoutSeconds;
    }

    /**
     * Create PoolSettings from DifyProperties.ClientConfig with defaults.
     *
     * @param config the client configuration, may be null
     * @return PoolSettings with normalized values
     */
    public static PoolSettings from(DifyProperties.ClientConfig config) {
        if (config == null) {
            return defaults();
        }

        int maxIdleConnections = validatePositive(
                config.getMaxIdleConnections() != null ? config.getMaxIdleConnections() : 5,
                5, "maxIdleConnections");
        int keepAliveSeconds = validatePositive(
                config.getKeepAliveSeconds() != null ? config.getKeepAliveSeconds() : 300,
                300, "keepAliveSeconds");
        int maxRequests = validatePositive(
                config.getMaxRequests() != null ? config.getMaxRequests() : 64,
                64, "maxRequests");
        int maxRequestsPerHost = validatePositive(
                config.getMaxRequestsPerHost() != null ? config.getMaxRequestsPerHost() : 5,
                5, "maxRequestsPerHost");

        // callTimeout can be 0 (no limit), but not negative
        int callTimeoutSeconds = config.getCallTimeout() != null
                ? Math.max(0, config.getCallTimeout()) : 0;
        int connectTimeoutSeconds = validatePositive(
                config.getConnectTimeout() != null ? config.getConnectTimeout() : 30,
                30, "connectTimeout");
        int readTimeoutSeconds = validatePositive(
                config.getReadTimeout() != null ? config.getReadTimeout() : 30,
                30, "readTimeout");

        return new PoolSettings(maxIdleConnections, keepAliveSeconds, maxRequests,
                maxRequestsPerHost, callTimeoutSeconds, connectTimeoutSeconds, readTimeoutSeconds);
    }

    /**
     * Validate and normalize pool parameters to prevent invalid configurations.
     * Ensures all values are >= 1 except callTimeout which can be 0 (no limit).
     *
     * @param value        the value to validate
     * @param defaultValue the default value to use if validation fails
     * @param paramName    the parameter name for logging
     * @return validated value (>= 1)
     */
    private static int validatePositive(int value, int defaultValue, String paramName) {
        if (value < 1) {
            log.warn("Invalid {} value: {}, using default value {}", paramName, value, defaultValue);
            return Math.max(1, defaultValue);
        }
        return value;
    }

    /**
     * Create PoolSettings with default values.
     *
     * @return PoolSettings with defaults matching OkHttp
     */
    public static PoolSettings defaults() {
        return new PoolSettings(5, 300, 64, 5, 0, 30, 30);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PoolSettings that = (PoolSettings) o;
        return maxIdleConnections == that.maxIdleConnections &&
                keepAliveSeconds == that.keepAliveSeconds &&
                maxRequests == that.maxRequests &&
                maxRequestsPerHost == that.maxRequestsPerHost &&
                callTimeoutSeconds == that.callTimeoutSeconds &&
                connectTimeoutSeconds == that.connectTimeoutSeconds &&
                readTimeoutSeconds == that.readTimeoutSeconds;
    }

    @Override
    public int hashCode() {
        int result = maxIdleConnections;
        result = 31 * result + keepAliveSeconds;
        result = 31 * result + maxRequests;
        result = 31 * result + maxRequestsPerHost;
        result = 31 * result + callTimeoutSeconds;
        result = 31 * result + connectTimeoutSeconds;
        result = 31 * result + readTimeoutSeconds;
        return result;
    }

    @Override
    public String toString() {
        return "PoolSettings{" +
                "maxIdleConnections=" + maxIdleConnections +
                ", keepAliveSeconds=" + keepAliveSeconds +
                ", maxRequests=" + maxRequests +
                ", maxRequestsPerHost=" + maxRequestsPerHost +
                ", callTimeoutSeconds=" + callTimeoutSeconds +
                ", connectTimeoutSeconds=" + connectTimeoutSeconds +
                ", readTimeoutSeconds=" + readTimeoutSeconds +
                '}';
    }
}
