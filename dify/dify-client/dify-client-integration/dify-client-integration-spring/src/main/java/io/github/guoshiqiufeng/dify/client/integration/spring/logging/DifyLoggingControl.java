/*
 * Copyright (c) 2025-2025, fubluesky (fubluesky@foxmail.com)
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

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Control the global state of Dify loggers to prevent duplicate interceptors from being added
 *
 * @author yanghq
 * @version 0.11.0
 * @since 2025/4/29 15:00
 */
@Slf4j
public class DifyLoggingControl {

    private static final DifyLoggingControl INSTANCE = new DifyLoggingControl();

    private final AtomicBoolean interceptorAdded = new AtomicBoolean(false);
    private final AtomicBoolean filterAdded = new AtomicBoolean(false);

    private final DifyRestLoggingInterceptor loggingInterceptor = new DifyRestLoggingInterceptor();
    private final DifyLoggingFilter loggingFilter = new DifyLoggingFilter();

    private DifyLoggingControl() {
    }

    /**
     * getInstance
     *
     * @return DifyLoggingControl实例
     */
    public static DifyLoggingControl getInstance() {
        return INSTANCE;
    }

    /**
     * getAndMarkInterceptor
     *
     * @return DifyRestLoggingInterceptor instance, or null if it has already been added.
     */
    public DifyRestLoggingInterceptor getAndMarkInterceptor() {
        if (interceptorAdded.compareAndSet(false, true)) {
            return loggingInterceptor;
        }
        return null;
    }

    /**
     * Gets the log filter instance and marks it as added
     *
     * @return DifyLoggingFilter instance, or null if it has already been added.
     */
    public DifyLoggingFilter getAndMarkFilter() {
        if (filterAdded.compareAndSet(false, true)) {
            return loggingFilter;
        }
        return null;
    }

    /**
     * Reset state, mainly used for testing
     */
    public void reset() {
        interceptorAdded.set(false);
        filterAdded.set(false);
    }
}
