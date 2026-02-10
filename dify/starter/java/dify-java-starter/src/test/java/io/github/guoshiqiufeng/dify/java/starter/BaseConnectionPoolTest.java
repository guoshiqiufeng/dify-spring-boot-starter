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
package io.github.guoshiqiufeng.dify.java.starter;

import io.github.guoshiqiufeng.dify.client.integration.okhttp.http.JavaHttpClient;
import io.github.guoshiqiufeng.dify.support.impl.base.BaseDifyDefaultClient;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Base test class for OkHttp connection pool configuration tests.
 * Provides common utility methods for extracting and asserting OkHttp client configurations.
 *
 * <p>Note: Connection pool parameter assertions rely on OkHttp internal implementation
 * via reflection. If OkHttp is upgraded and internal fields change, these methods may
 * need to be updated accordingly.</p>
 *
 * @author yanghq
 * @version 1.0
 * @since 2026/2/6
 */
public abstract class BaseConnectionPoolTest {

    /**
     * Extract OkHttpClient from a Dify client instance using reflection.
     *
     * @param difyClient the Dify client instance (DifyChat, DifyDataset, or DifyServer)
     * @return the underlying OkHttpClient instance
     * @throws RuntimeException if extraction fails
     */
    protected static OkHttpClient extractOkHttpClient(Object difyClient) {
        try {
            Field httpClientField = BaseDifyDefaultClient.class.getDeclaredField("httpClient");
            httpClientField.setAccessible(true);
            Object httpClient = httpClientField.get(difyClient);
            assertNotNull(httpClient, "HttpClient should not be null");
            assertTrue(httpClient instanceof JavaHttpClient, "HttpClient should be JavaHttpClient");
            return ((JavaHttpClient) httpClient).getOkHttpClient();
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract OkHttpClient", e);
        }
    }

    /**
     * Assert OkHttp client configuration matches expected values.
     *
     * @param okHttpClient       the OkHttpClient instance to verify
     * @param maxIdleConnections expected maximum idle connections
     * @param keepAliveSeconds   expected keep-alive duration in seconds
     * @param maxRequests        expected maximum concurrent requests
     * @param maxRequestsPerHost expected maximum concurrent requests per host
     * @param callTimeoutSeconds expected call timeout in seconds
     */
    protected static void assertOkHttpConfig(OkHttpClient okHttpClient, int maxIdleConnections, int keepAliveSeconds,
                                             int maxRequests, int maxRequestsPerHost, int callTimeoutSeconds) {
        Dispatcher dispatcher = okHttpClient.dispatcher();
        assertEquals(maxRequests, dispatcher.getMaxRequests(), "maxRequests should match");
        assertEquals(maxRequestsPerHost, dispatcher.getMaxRequestsPerHost(), "maxRequestsPerHost should match");
        assertEquals(callTimeoutSeconds * 1000, okHttpClient.callTimeoutMillis(), "callTimeoutMillis should match");
        assertConnectionPoolConfig(okHttpClient.connectionPool(), maxIdleConnections, keepAliveSeconds);
    }

    /**
     * Assert connection pool configuration using reflection to access internal fields.
     *
     * <p>This method relies on OkHttp internal implementation details and may need
     * updates if OkHttp is upgraded.</p>
     *
     * @param pool               the ConnectionPool instance
     * @param maxIdleConnections expected maximum idle connections
     * @param keepAliveSeconds   expected keep-alive duration in seconds
     * @throws RuntimeException if reflection fails
     */
    protected static void assertConnectionPoolConfig(ConnectionPool pool, int maxIdleConnections, int keepAliveSeconds) {
        Object delegate = pool.getDelegate$okhttp();
        try {
            Field maxIdleField = delegate.getClass().getDeclaredField("maxIdleConnections");
            maxIdleField.setAccessible(true);
            int actualMaxIdle = (int) maxIdleField.get(delegate);

            Field keepAliveField = delegate.getClass().getDeclaredField("keepAliveDurationNs");
            keepAliveField.setAccessible(true);
            long actualKeepAliveNs = (long) keepAliveField.get(delegate);

            assertEquals(maxIdleConnections, actualMaxIdle, "maxIdleConnections should match");
            assertEquals(TimeUnit.SECONDS.toNanos(keepAliveSeconds), actualKeepAliveNs, "keepAliveSeconds should match");
        } catch (Exception e) {
            throw new RuntimeException("Failed to read ConnectionPool config via reflection", e);
        }
    }
}
