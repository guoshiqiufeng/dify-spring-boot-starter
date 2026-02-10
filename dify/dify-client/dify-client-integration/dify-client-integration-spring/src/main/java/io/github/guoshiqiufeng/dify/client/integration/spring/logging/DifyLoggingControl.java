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
package io.github.guoshiqiufeng.dify.client.integration.spring.logging;

import lombok.extern.slf4j.Slf4j;

/**
 * Factory for creating Dify logging components.
 * Changed from singleton pattern to factory pattern to support multiple clients
 * with different configurations (e.g., multi-tenant scenarios).
 *
 * @author yanghq
 * @version 0.11.0
 * @since 2025/4/29 15:00
 */
@Slf4j
public class DifyLoggingControl {

    private static final DifyLoggingControl INSTANCE = new DifyLoggingControl();

    private DifyLoggingControl() {
    }

    /**
     * Get singleton instance (for backward compatibility).
     *
     * @return DifyLoggingControl instance
     * @deprecated Use static factory methods {@link #createInterceptor()} or {@link #createFilter()} instead.
     *             The singleton pattern has been replaced with factory pattern to support multiple clients.
     */
    @Deprecated
    public static DifyLoggingControl getInstance() {
        return INSTANCE;
    }

    // Track whether deprecated methods have been called (for backward compatibility)
    private volatile boolean interceptorMarked = false;
    private volatile boolean filterMarked = false;

    /**
     * Get and mark interceptor (for backward compatibility).
     * Preserves original idempotent behavior: returns instance on first call, null on subsequent calls.
     *
     * @return DifyRestLoggingInterceptor instance on first call, null on subsequent calls
     * @deprecated Use {@link #createInterceptor()} instead for multi-tenant scenarios.
     *             This method maintains backward compatibility by returning null after first call.
     */
    @Deprecated
    public synchronized DifyRestLoggingInterceptor getAndMarkInterceptor() {
        return getAndMarkInterceptor(true);
    }

    /**
     * Get and mark interceptor with configurable masking (for backward compatibility).
     * Preserves original idempotent behavior: returns instance on first call, null on subsequent calls.
     *
     * @param maskingEnabled whether to enable log masking
     * @return DifyRestLoggingInterceptor instance on first call, null on subsequent calls
     * @deprecated Use {@link #createInterceptor(boolean)} instead for multi-tenant scenarios.
     *             This method maintains backward compatibility by returning null after first call.
     */
    @Deprecated
    public synchronized DifyRestLoggingInterceptor getAndMarkInterceptor(boolean maskingEnabled) {
        if (!interceptorMarked) {
            interceptorMarked = true;
            return createInterceptor(maskingEnabled);
        }
        return null;
    }

    /**
     * Get and mark filter (for backward compatibility).
     * Preserves original idempotent behavior: returns instance on first call, null on subsequent calls.
     *
     * @return DifyLoggingFilter instance on first call, null on subsequent calls
     * @deprecated Use {@link #createFilter()} instead for multi-tenant scenarios.
     *             This method maintains backward compatibility by returning null after first call.
     */
    @Deprecated
    public synchronized DifyLoggingFilter getAndMarkFilter() {
        return getAndMarkFilter(true);
    }

    /**
     * Get and mark filter with configurable masking (for backward compatibility).
     * Preserves original idempotent behavior: returns instance on first call, null on subsequent calls.
     *
     * @param maskingEnabled whether to enable log masking
     * @return DifyLoggingFilter instance on first call, null on subsequent calls
     * @deprecated Use {@link #createFilter(boolean)} instead for multi-tenant scenarios.
     *             This method maintains backward compatibility by returning null after first call.
     */
    @Deprecated
    public synchronized DifyLoggingFilter getAndMarkFilter(boolean maskingEnabled) {
        if (!filterMarked) {
            filterMarked = true;
            return createFilter(maskingEnabled);
        }
        return null;
    }

    /**
     * Reset state (for backward compatibility).
     * Resets the marked flags so getAndMark* methods can be called again.
     *
     * @deprecated Use factory methods {@link #createInterceptor()} or {@link #createFilter()} instead.
     */
    @Deprecated
    public synchronized void reset() {
        interceptorMarked = false;
        filterMarked = false;
    }

    /**
     * Create a new RestClient logging interceptor with default masking enabled
     *
     * @return new DifyRestLoggingInterceptor instance
     */
    public static DifyRestLoggingInterceptor createInterceptor() {
        return createInterceptor(true);
    }

    /**
     * Create a new RestClient logging interceptor with configurable masking
     *
     * @param maskingEnabled whether to enable log masking
     * @return new DifyRestLoggingInterceptor instance
     */
    public static DifyRestLoggingInterceptor createInterceptor(boolean maskingEnabled) {
        return new DifyRestLoggingInterceptor(maskingEnabled);
    }

    /**
     * Create a new RestClient logging interceptor with full configuration
     *
     * @param maskingEnabled  whether to enable log masking
     * @param logBodyMaxBytes maximum bytes to log for body (0 = unlimited)
     * @return new DifyRestLoggingInterceptor instance
     */
    public static DifyRestLoggingInterceptor createInterceptor(boolean maskingEnabled, int logBodyMaxBytes) {
        return new DifyRestLoggingInterceptor(maskingEnabled, logBodyMaxBytes);
    }

    /**
     * Create a new WebClient logging filter with default masking enabled
     *
     * @return new DifyLoggingFilter instance
     */
    public static DifyLoggingFilter createFilter() {
        return createFilter(true);
    }

    /**
     * Create a new WebClient logging filter with configurable masking
     *
     * @param maskingEnabled whether to enable log masking
     * @return new DifyLoggingFilter instance
     */
    public static DifyLoggingFilter createFilter(boolean maskingEnabled) {
        return new DifyLoggingFilter(maskingEnabled);
    }

    /**
     * Create a new WebClient logging filter with full configuration
     *
     * @param maskingEnabled  whether to enable log masking
     * @param logBodyMaxBytes maximum bytes to log for body (0 = unlimited)
     * @return new DifyLoggingFilter instance
     */
    public static DifyLoggingFilter createFilter(boolean maskingEnabled, int logBodyMaxBytes) {
        return new DifyLoggingFilter(maskingEnabled, logBodyMaxBytes);
    }

    /**
     * Create a new WebClient logging filter with full configuration including binary body logging
     *
     * @param maskingEnabled  whether to enable log masking
     * @param logBodyMaxBytes maximum bytes to log for body (0 = unlimited)
     * @param logBinaryBody   whether to log binary body content (default: false, only log metadata)
     * @return new DifyLoggingFilter instance
     */
    public static DifyLoggingFilter createFilter(boolean maskingEnabled, int logBodyMaxBytes, boolean logBinaryBody) {
        return new DifyLoggingFilter(maskingEnabled, logBodyMaxBytes, logBinaryBody);
    }

    /**
     * Create a new RestClient logging interceptor with full configuration including binary body logging
     *
     * @param maskingEnabled  whether to enable log masking
     * @param logBodyMaxBytes maximum bytes to log for body (0 = unlimited)
     * @param logBinaryBody   whether to log binary body content (default: false, only log metadata)
     * @return new DifyRestLoggingInterceptor instance
     */
    public static DifyRestLoggingInterceptor createInterceptor(boolean maskingEnabled, int logBodyMaxBytes, boolean logBinaryBody) {
        return new DifyRestLoggingInterceptor(maskingEnabled, logBodyMaxBytes, logBinaryBody);
    }
}
