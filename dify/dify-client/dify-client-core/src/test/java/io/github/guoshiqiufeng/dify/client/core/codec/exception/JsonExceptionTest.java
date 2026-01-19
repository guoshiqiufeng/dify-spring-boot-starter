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
package io.github.guoshiqiufeng.dify.client.core.codec.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JsonException
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/01/06
 */
class JsonExceptionTest {

    @Test
    void testConstructorWithMessage() {
        String message = "JSON parsing failed";
        JsonException exception = new JsonException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        String message = "JSON parsing failed";
        Throwable cause = new IllegalArgumentException("Invalid JSON");
        JsonException exception = new JsonException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testConstructorWithCause() {
        Throwable cause = new IllegalArgumentException("Invalid JSON");
        JsonException exception = new JsonException(cause);

        assertEquals(cause, exception.getCause());
        assertTrue(exception.getMessage().contains("IllegalArgumentException"));
    }

    @Test
    void testExceptionIsRuntimeException() {
        JsonException exception = new JsonException("test");
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testExceptionCanBeThrown() {
        assertThrows(JsonException.class, () -> {
            throw new JsonException("test exception");
        });
    }

    @Test
    void testExceptionWithNullMessage() {
        JsonException exception = new JsonException((String) null);
        assertNull(exception.getMessage());
    }

    @Test
    void testExceptionWithNullCause() {
        JsonException exception = new JsonException((Throwable) null);
        assertNull(exception.getCause());
    }
}
