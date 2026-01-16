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
package io.github.guoshiqiufeng.dify.core.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UtilException
 *
 * @author yanghq
 * @version 1.0
 * @since 2026/01/13
 */
class UtilExceptionTest {

    @Test
    void testDefaultConstructor() {
        UtilException exception = new UtilException();
        assertNotNull(exception);
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithMessage() {
        String message = "Test error message";
        UtilException exception = new UtilException(message);
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        String message = "Test error message";
        Throwable cause = new RuntimeException("Cause exception");
        UtilException exception = new UtilException(message, cause);
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void testConstructorWithCause() {
        Throwable cause = new RuntimeException("Cause exception");
        UtilException exception = new UtilException(cause);
        assertNotNull(exception);
        assertSame(cause, exception.getCause());
        assertTrue(exception.getMessage().contains("RuntimeException"));
    }

    @Test
    void testIsRuntimeException() {
        UtilException exception = new UtilException("Test");
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testThrowAndCatch() {
        assertThrows(UtilException.class, () -> {
            throw new UtilException("Test exception");
        });
    }

    @Test
    void testExceptionChaining() {
        Exception originalException = new IllegalArgumentException("Original error");
        UtilException wrappedException = new UtilException("Wrapped error", originalException);

        assertEquals("Wrapped error", wrappedException.getMessage());
        assertSame(originalException, wrappedException.getCause());
        assertEquals("Original error", wrappedException.getCause().getMessage());
    }

    @Test
    void testStackTrace() {
        UtilException exception = new UtilException("Test exception");
        StackTraceElement[] stackTrace = exception.getStackTrace();
        assertNotNull(stackTrace);
        assertTrue(stackTrace.length > 0);
    }

    @Test
    void testNullMessage() {
        UtilException exception = new UtilException((String) null);
        assertNull(exception.getMessage());
    }

    @Test
    void testNullCause() {
        UtilException exception = new UtilException((Throwable) null);
        assertNull(exception.getCause());
    }

    @Test
    void testEmptyMessage() {
        UtilException exception = new UtilException("");
        assertEquals("", exception.getMessage());
    }
}
