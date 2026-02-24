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
package io.github.guoshiqiufeng.dify.core.logging.masking;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test coverage for masking exception classes
 *
 * @author yanghq
 * @version 2.1.0
 * @since 2026/2/10
 */
class MaskingExceptionsTest {

    @Test
    void testMaskingParseException_messageConstructor() {
        String message = "Failed to parse masking rule";
        MaskingParseException exception = new MaskingParseException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testMaskingParseException_messageAndCauseConstructor() {
        String message = "Failed to parse masking rule";
        Throwable cause = new IllegalArgumentException("Invalid format");
        MaskingParseException exception = new MaskingParseException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testMaskingParseException_noArgsConstructor() {
        MaskingParseException exception = new MaskingParseException();

        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testUnsupportedFormatException_messageConstructor() {
        String message = "Unsupported format: XML";
        UnsupportedFormatException exception = new UnsupportedFormatException(message);

        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testUnsupportedFormatException_messageAndCauseConstructor() {
        String message = "Unsupported format: XML";
        Throwable cause = new IllegalArgumentException("XML not supported");
        UnsupportedFormatException exception = new UnsupportedFormatException(message, cause);

        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testUnsupportedFormatException_noArgsConstructor() {
        UnsupportedFormatException exception = new UnsupportedFormatException();

        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testMaskingParseException_canBeThrown() {
        assertThrows(MaskingParseException.class, () -> {
            throw new MaskingParseException("Test exception");
        });
    }

    @Test
    void testUnsupportedFormatException_canBeThrown() {
        assertThrows(UnsupportedFormatException.class, () -> {
            throw new UnsupportedFormatException("Test exception");
        });
    }

    @Test
    void testMaskingParseException_isRuntimeException() {
        MaskingParseException exception = new MaskingParseException("Test");
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testUnsupportedFormatException_isRuntimeException() {
        UnsupportedFormatException exception = new UnsupportedFormatException("Test");
        assertTrue(exception instanceof RuntimeException);
    }
}
