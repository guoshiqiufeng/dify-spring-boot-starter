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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DifyLoggingControl
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/1/13
 */
class DifyLoggingControlTest {

    @AfterEach
    void tearDown() {
        // Reset state after each test to avoid interference
        DifyLoggingControl.getInstance().reset();
    }

    @Test
    void testGetInstanceReturnsSameInstance() {
        // Act
        DifyLoggingControl instance1 = DifyLoggingControl.getInstance();
        DifyLoggingControl instance2 = DifyLoggingControl.getInstance();

        // Assert
        assertNotNull(instance1);
        assertNotNull(instance2);
        assertSame(instance1, instance2, "getInstance should return the same singleton instance");
    }

    @Test
    void testGetAndMarkInterceptorFirstCall() {
        // Arrange
        DifyLoggingControl control = DifyLoggingControl.getInstance();

        // Act
        DifyRestLoggingInterceptor interceptor = control.getAndMarkInterceptor();

        // Assert
        assertNotNull(interceptor, "First call should return interceptor instance");
    }

    @Test
    void testGetAndMarkInterceptorSecondCall() {
        // Arrange
        DifyLoggingControl control = DifyLoggingControl.getInstance();
        control.getAndMarkInterceptor(); // First call

        // Act
        DifyRestLoggingInterceptor secondInterceptor = control.getAndMarkInterceptor();

        // Assert
        assertNull(secondInterceptor, "Second call should return null");
    }

    @Test
    void testGetAndMarkInterceptorReturnsSameInstanceOnFirstCall() {
        // Arrange
        DifyLoggingControl control = DifyLoggingControl.getInstance();

        // Act
        DifyRestLoggingInterceptor interceptor1 = control.getAndMarkInterceptor();
        control.reset();
        DifyRestLoggingInterceptor interceptor2 = control.getAndMarkInterceptor();

        // Assert
        assertNotNull(interceptor1);
        assertNotNull(interceptor2);
        assertSame(interceptor1, interceptor2, "Should return the same interceptor instance");
    }

    @Test
    void testGetAndMarkFilterFirstCall() {
        // Arrange
        DifyLoggingControl control = DifyLoggingControl.getInstance();

        // Act
        DifyLoggingFilter filter = control.getAndMarkFilter();

        // Assert
        assertNotNull(filter, "First call should return filter instance");
    }

    @Test
    void testGetAndMarkFilterSecondCall() {
        // Arrange
        DifyLoggingControl control = DifyLoggingControl.getInstance();
        control.getAndMarkFilter(); // First call

        // Act
        DifyLoggingFilter secondFilter = control.getAndMarkFilter();

        // Assert
        assertNull(secondFilter, "Second call should return null");
    }

    @Test
    void testGetAndMarkFilterReturnsSameInstanceOnFirstCall() {
        // Arrange
        DifyLoggingControl control = DifyLoggingControl.getInstance();

        // Act
        DifyLoggingFilter filter1 = control.getAndMarkFilter();
        control.reset();
        DifyLoggingFilter filter2 = control.getAndMarkFilter();

        // Assert
        assertNotNull(filter1);
        assertNotNull(filter2);
        assertSame(filter1, filter2, "Should return the same filter instance");
    }

    @Test
    void testResetAllowsInterceptorToBeRetrievedAgain() {
        // Arrange
        DifyLoggingControl control = DifyLoggingControl.getInstance();
        DifyRestLoggingInterceptor firstInterceptor = control.getAndMarkInterceptor();
        assertNotNull(firstInterceptor);
        assertNull(control.getAndMarkInterceptor(), "Should be null before reset");

        // Act
        control.reset();
        DifyRestLoggingInterceptor afterResetInterceptor = control.getAndMarkInterceptor();

        // Assert
        assertNotNull(afterResetInterceptor, "After reset, should be able to get interceptor again");
    }

    @Test
    void testResetAllowsFilterToBeRetrievedAgain() {
        // Arrange
        DifyLoggingControl control = DifyLoggingControl.getInstance();
        DifyLoggingFilter firstFilter = control.getAndMarkFilter();
        assertNotNull(firstFilter);
        assertNull(control.getAndMarkFilter(), "Should be null before reset");

        // Act
        control.reset();
        DifyLoggingFilter afterResetFilter = control.getAndMarkFilter();

        // Assert
        assertNotNull(afterResetFilter, "After reset, should be able to get filter again");
    }

    @Test
    void testInterceptorAndFilterAreIndependent() {
        // Arrange
        DifyLoggingControl control = DifyLoggingControl.getInstance();

        // Act
        DifyRestLoggingInterceptor interceptor1 = control.getAndMarkInterceptor();
        DifyLoggingFilter filter1 = control.getAndMarkFilter();
        DifyRestLoggingInterceptor interceptor2 = control.getAndMarkInterceptor();
        DifyLoggingFilter filter2 = control.getAndMarkFilter();

        // Assert
        assertNotNull(interceptor1, "First interceptor call should succeed");
        assertNotNull(filter1, "First filter call should succeed");
        assertNull(interceptor2, "Second interceptor call should return null");
        assertNull(filter2, "Second filter call should return null");
    }

    @Test
    void testConcurrentAccessToInterceptor() throws InterruptedException {
        // Arrange
        DifyLoggingControl control = DifyLoggingControl.getInstance();
        final DifyRestLoggingInterceptor[] results = new DifyRestLoggingInterceptor[2];

        // Act - simulate concurrent access
        Thread thread1 = new Thread(() -> results[0] = control.getAndMarkInterceptor());
        Thread thread2 = new Thread(() -> results[1] = control.getAndMarkInterceptor());

        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        // Assert - exactly one should succeed
        assertTrue(
                (results[0] != null && results[1] == null) || (results[0] == null && results[1] != null),
                "Exactly one thread should get the interceptor"
        );
    }

    @Test
    void testConcurrentAccessToFilter() throws InterruptedException {
        // Arrange
        DifyLoggingControl control = DifyLoggingControl.getInstance();
        final DifyLoggingFilter[] results = new DifyLoggingFilter[2];

        // Act - simulate concurrent access
        Thread thread1 = new Thread(() -> results[0] = control.getAndMarkFilter());
        Thread thread2 = new Thread(() -> results[1] = control.getAndMarkFilter());

        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();

        // Assert - exactly one should succeed
        assertTrue(
                (results[0] != null && results[1] == null) || (results[0] == null && results[1] != null),
                "Exactly one thread should get the filter"
        );
    }
}
