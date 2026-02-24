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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DifyLoggingControl
 * Updated to test factory pattern instead of singleton pattern
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/1/13
 */
class DifyLoggingControlTest {

    @Test
    void testCreateInterceptorWithDefaultMasking() {
        // Act
        DifyRestLoggingInterceptor interceptor = DifyLoggingControl.createInterceptor();

        // Assert
        assertNotNull(interceptor, "Should create interceptor instance");
    }

    @Test
    void testCreateInterceptorWithCustomMasking() {
        // Act
        DifyRestLoggingInterceptor interceptor1 = DifyLoggingControl.createInterceptor(true);
        DifyRestLoggingInterceptor interceptor2 = DifyLoggingControl.createInterceptor(false);

        // Assert
        assertNotNull(interceptor1, "Should create interceptor with masking enabled");
        assertNotNull(interceptor2, "Should create interceptor with masking disabled");
        assertNotSame(interceptor1, interceptor2, "Each call should create a new instance");
    }

    @Test
    void testCreateInterceptorWithFullConfiguration() {
        // Act
        DifyRestLoggingInterceptor interceptor = DifyLoggingControl.createInterceptor(true, 8192);

        // Assert
        assertNotNull(interceptor, "Should create interceptor with full configuration");
    }

    @Test
    void testCreateFilterWithDefaultMasking() {
        // Act
        DifyLoggingFilter filter = DifyLoggingControl.createFilter();

        // Assert
        assertNotNull(filter, "Should create filter instance");
    }

    @Test
    void testCreateFilterWithCustomMasking() {
        // Act
        DifyLoggingFilter filter1 = DifyLoggingControl.createFilter(true);
        DifyLoggingFilter filter2 = DifyLoggingControl.createFilter(false);

        // Assert
        assertNotNull(filter1, "Should create filter with masking enabled");
        assertNotNull(filter2, "Should create filter with masking disabled");
        assertNotSame(filter1, filter2, "Each call should create a new instance");
    }

    @Test
    void testCreateFilterWithFullConfiguration() {
        // Act
        DifyLoggingFilter filter = DifyLoggingControl.createFilter(true, 8192);

        // Assert
        assertNotNull(filter, "Should create filter with full configuration");
    }

    @Test
    void testMultipleClientsCanHaveDifferentConfigurations() {
        // Act - simulate multiple clients with different configurations
        DifyRestLoggingInterceptor interceptor1 = DifyLoggingControl.createInterceptor(true, 4096);
        DifyRestLoggingInterceptor interceptor2 = DifyLoggingControl.createInterceptor(false, 8192);
        DifyLoggingFilter filter1 = DifyLoggingControl.createFilter(true, 4096);
        DifyLoggingFilter filter2 = DifyLoggingControl.createFilter(false, 8192);

        // Assert - all instances should be independent
        assertNotNull(interceptor1);
        assertNotNull(interceptor2);
        assertNotNull(filter1);
        assertNotNull(filter2);
        assertNotSame(interceptor1, interceptor2, "Interceptors should be independent instances");
        assertNotSame(filter1, filter2, "Filters should be independent instances");
    }

    @Test
    void testConcurrentCreation() throws InterruptedException {
        // Arrange
        final DifyRestLoggingInterceptor[] interceptors = new DifyRestLoggingInterceptor[2];
        final DifyLoggingFilter[] filters = new DifyLoggingFilter[2];

        // Act - simulate concurrent creation
        Thread thread1 = new Thread(() -> {
            interceptors[0] = DifyLoggingControl.createInterceptor();
            filters[0] = DifyLoggingControl.createFilter();
        });
        Thread thread2 = new Thread(() -> {
            interceptors[1] = DifyLoggingControl.createInterceptor();
            filters[1] = DifyLoggingControl.createFilter();
        });

        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        // Assert - both threads should successfully create instances
        assertNotNull(interceptors[0], "Thread 1 should create interceptor");
        assertNotNull(interceptors[1], "Thread 2 should create interceptor");
        assertNotNull(filters[0], "Thread 1 should create filter");
        assertNotNull(filters[1], "Thread 2 should create filter");
        assertNotSame(interceptors[0], interceptors[1], "Each thread should get independent interceptor");
        assertNotSame(filters[0], filters[1], "Each thread should get independent filter");
    }

    // ========== Tests for deprecated methods (backward compatibility) ==========

    @Test
    void testGetInstanceReturnsSingleton() {
        // Act
        DifyLoggingControl instance1 = DifyLoggingControl.getInstance();
        DifyLoggingControl instance2 = DifyLoggingControl.getInstance();

        // Assert
        assertNotNull(instance1, "getInstance should return non-null instance");
        assertSame(instance1, instance2, "getInstance should return same singleton instance");
    }

    @Test
    void testGetAndMarkInterceptorIdempotent() {
        // Arrange
        DifyLoggingControl control = DifyLoggingControl.getInstance();
        control.reset(); // Ensure clean state

        // Act
        DifyRestLoggingInterceptor first = control.getAndMarkInterceptor();
        DifyRestLoggingInterceptor second = control.getAndMarkInterceptor();

        // Assert
        assertNotNull(first, "First call should return non-null interceptor");
        assertNull(second, "Second call should return null (idempotent behavior)");
    }

    @Test
    void testGetAndMarkInterceptorWithMaskingIdempotent() {
        // Arrange
        DifyLoggingControl control = DifyLoggingControl.getInstance();
        control.reset(); // Ensure clean state

        // Act
        DifyRestLoggingInterceptor first = control.getAndMarkInterceptor(false);
        DifyRestLoggingInterceptor second = control.getAndMarkInterceptor(false);

        // Assert
        assertNotNull(first, "First call should return non-null interceptor");
        assertNull(second, "Second call should return null (idempotent behavior)");
    }

    @Test
    void testGetAndMarkFilterIdempotent() {
        // Arrange
        DifyLoggingControl control = DifyLoggingControl.getInstance();
        control.reset(); // Ensure clean state

        // Act
        DifyLoggingFilter first = control.getAndMarkFilter();
        DifyLoggingFilter second = control.getAndMarkFilter();

        // Assert
        assertNotNull(first, "First call should return non-null filter");
        assertNull(second, "Second call should return null (idempotent behavior)");
    }

    @Test
    void testGetAndMarkFilterWithMaskingIdempotent() {
        // Arrange
        DifyLoggingControl control = DifyLoggingControl.getInstance();
        control.reset(); // Ensure clean state

        // Act
        DifyLoggingFilter first = control.getAndMarkFilter(true);
        DifyLoggingFilter second = control.getAndMarkFilter(true);

        // Assert
        assertNotNull(first, "First call should return non-null filter");
        assertNull(second, "Second call should return null (idempotent behavior)");
    }

    @Test
    void testResetAllowsGetAndMarkAgain() {
        // Arrange
        DifyLoggingControl control = DifyLoggingControl.getInstance();
        control.reset(); // Ensure clean state

        // Act - first cycle
        DifyRestLoggingInterceptor interceptor1 = control.getAndMarkInterceptor();
        DifyLoggingFilter filter1 = control.getAndMarkFilter();
        DifyRestLoggingInterceptor interceptor2 = control.getAndMarkInterceptor();
        DifyLoggingFilter filter2 = control.getAndMarkFilter();

        // Reset
        control.reset();

        // Act - second cycle after reset
        DifyRestLoggingInterceptor interceptor3 = control.getAndMarkInterceptor();
        DifyLoggingFilter filter3 = control.getAndMarkFilter();

        // Assert
        assertNotNull(interceptor1, "First interceptor should be non-null");
        assertNotNull(filter1, "First filter should be non-null");
        assertNull(interceptor2, "Second interceptor should be null before reset");
        assertNull(filter2, "Second filter should be null before reset");
        assertNotNull(interceptor3, "Interceptor after reset should be non-null");
        assertNotNull(filter3, "Filter after reset should be non-null");
    }

    @Test
    void testInterceptorAndFilterMarkedIndependently() {
        // Arrange
        DifyLoggingControl control = DifyLoggingControl.getInstance();
        control.reset(); // Ensure clean state

        // Act - mark interceptor only
        DifyRestLoggingInterceptor interceptor1 = control.getAndMarkInterceptor();
        DifyLoggingFilter filter1 = control.getAndMarkFilter();
        DifyRestLoggingInterceptor interceptor2 = control.getAndMarkInterceptor();
        DifyLoggingFilter filter2 = control.getAndMarkFilter();

        // Assert
        assertNotNull(interceptor1, "First interceptor should be non-null");
        assertNotNull(filter1, "First filter should be non-null");
        assertNull(interceptor2, "Second interceptor should be null (already marked)");
        assertNull(filter2, "Second filter should be null (already marked)");
    }

    @Test
    void testCreateInterceptorWithBinaryBodyLogging() {
        // Act
        DifyRestLoggingInterceptor interceptor = DifyLoggingControl.createInterceptor(true, 8192, true);

        // Assert
        assertNotNull(interceptor, "Should create interceptor with binary body logging enabled");
    }

    @Test
    void testCreateFilterWithBinaryBodyLogging() {
        // Act
        DifyLoggingFilter filter = DifyLoggingControl.createFilter(true, 8192, false);

        // Assert
        assertNotNull(filter, "Should create filter with binary body logging disabled");
    }
}
