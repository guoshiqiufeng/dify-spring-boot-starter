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
 * Unit tests for MaskingBuffer
 *
 * @author yanghq
 * @version 2.1.0
 * @since 2026/2/4
 */
class MaskingBufferTest {

    @Test
    void testGetBuffer() {
        // Act
        MaskingBuffer buffer = MaskingBuffer.get();

        // Assert
        assertNotNull(buffer);
        assertEquals(0, buffer.length());
    }

    @Test
    void testAppendString() {
        // Arrange
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        buffer.append("hello");

        // Assert
        assertEquals(5, buffer.length());
        assertEquals("hello", buffer.toString());
    }

    @Test
    void testAppendChar() {
        // Arrange
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        buffer.append('a').append('b').append('c');

        // Assert
        assertEquals(3, buffer.length());
        assertEquals("abc", buffer.toString());
    }

    @Test
    void testAppendSubstring() {
        // Arrange
        MaskingBuffer buffer = MaskingBuffer.get();
        String str = "hello world";

        // Act
        buffer.append(str, 0, 5);

        // Assert
        assertEquals(5, buffer.length());
        assertEquals("hello", buffer.toString());
    }

    @Test
    void testClear() {
        // Arrange
        MaskingBuffer buffer = MaskingBuffer.get();
        buffer.append("test");

        // Act
        buffer.clear();

        // Assert
        assertEquals(0, buffer.length());
        assertEquals("", buffer.toString());
    }

    @Test
    void testChainedAppend() {
        // Arrange
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        buffer.append("Hello")
              .append(' ')
              .append("World");

        // Assert
        assertEquals(11, buffer.length());
        assertEquals("Hello World", buffer.toString());
    }

    @Test
    void testThreadLocal() {
        // Arrange & Act
        MaskingBuffer buffer1 = MaskingBuffer.get();
        buffer1.append("test1");

        MaskingBuffer buffer2 = MaskingBuffer.get();

        // Assert - get() should clear the buffer
        assertEquals(0, buffer2.length());
        assertSame(buffer1, buffer2); // Same instance from ThreadLocal
    }

    @Test
    void testLargeContent() {
        // Arrange
        MaskingBuffer buffer = MaskingBuffer.get();
        String largeString = "a".repeat(3000);

        // Act
        buffer.append(largeString);

        // Assert
        assertEquals(3000, buffer.length());
        assertEquals(largeString, buffer.toString());
    }

    @Test
    void testEmptyBuffer() {
        // Arrange
        MaskingBuffer buffer = MaskingBuffer.get();

        // Assert
        assertEquals(0, buffer.length());
        assertEquals("", buffer.toString());
    }

    @Test
    void testMultipleOperations() {
        // Arrange
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        buffer.append("start");
        assertEquals(5, buffer.length());

        buffer.append(" middle");
        assertEquals(12, buffer.length());

        buffer.append(" end");
        assertEquals(16, buffer.length());

        // Assert
        assertEquals("start middle end", buffer.toString());
    }

    @Test
    void testAppendSubstringWithDifferentRanges() {
        // Arrange
        MaskingBuffer buffer = MaskingBuffer.get();
        String str = "0123456789";

        // Act
        buffer.append(str, 0, 3)   // "012"
              .append(str, 5, 8);  // "567"

        // Assert
        assertEquals(6, buffer.length());
        assertEquals("012567", buffer.toString());
    }

    @Test
    void testLengthAfterClear() {
        // Arrange
        MaskingBuffer buffer = MaskingBuffer.get();
        buffer.append("some content");

        // Act
        buffer.clear();

        // Assert
        assertEquals(0, buffer.length());
    }

    @Test
    void testAppendEmptyString() {
        // Arrange
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        buffer.append("");

        // Assert
        assertEquals(0, buffer.length());
        assertEquals("", buffer.toString());
    }

    @Test
    void testAppendNull() {
        // Arrange
        MaskingBuffer buffer = MaskingBuffer.get();

        // Act
        buffer.append((String) null);

        // Assert
        assertEquals(4, buffer.length()); // StringBuilder appends "null"
        assertEquals("null", buffer.toString());
    }
}
