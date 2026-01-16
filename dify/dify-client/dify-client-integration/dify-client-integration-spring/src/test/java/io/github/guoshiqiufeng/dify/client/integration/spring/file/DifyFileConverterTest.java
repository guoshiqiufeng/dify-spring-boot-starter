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
package io.github.guoshiqiufeng.dify.client.integration.spring.file;

import io.github.guoshiqiufeng.dify.core.pojo.DifyFile;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DifyFileConverter
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025/12/31
 */
class DifyFileConverterTest {

    @Test
    void testFromMultipartFile() {
        // Arrange
        String filename = "test.txt";
        String contentType = "text/plain";
        byte[] content = "Hello, World!".getBytes();
        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                filename,
                contentType,
                content
        );

        // Act
        DifyFile difyFile = DifyFileConverter.from(multipartFile);

        // Assert
        assertNotNull(difyFile);
        assertEquals(filename, difyFile.getFilename());
        assertEquals(contentType, difyFile.getContentType());
        assertArrayEquals(content, difyFile.getContent());
        assertEquals(content.length, difyFile.getSize());
        assertFalse(difyFile.isEmpty());
    }

    @Test
    void testConvertMultipartFile() {
        // Arrange
        String filename = "image.png";
        String contentType = "image/png";
        byte[] content = new byte[]{1, 2, 3, 4, 5};
        MultipartFile multipartFile = new MockMultipartFile(
                "image",
                filename,
                contentType,
                content
        );

        // Act
        DifyFile difyFile = DifyFileConverter.convert(multipartFile);

        // Assert
        assertNotNull(difyFile);
        assertEquals(filename, difyFile.getFilename());
        assertEquals(contentType, difyFile.getContentType());
        assertArrayEquals(content, difyFile.getContent());
        assertEquals(content.length, difyFile.getSize());
    }

    @Test
    void testFromNullMultipartFile() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> DifyFileConverter.from(null)
        );
        assertEquals("MultipartFile cannot be null", exception.getMessage());
    }

    @Test
    void testFromEmptyMultipartFile() {
        // Arrange
        MultipartFile emptyFile = new MockMultipartFile(
                "empty",
                "empty.txt",
                "text/plain",
                new byte[0]
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> DifyFileConverter.from(emptyFile)
        );
        assertEquals("MultipartFile cannot be empty", exception.getMessage());
    }

    @Test
    void testConstructorThrowsException() throws Exception {
        // Act & Assert
        java.lang.reflect.Constructor<DifyFileConverter> constructor =
                DifyFileConverter.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        java.lang.reflect.InvocationTargetException exception = assertThrows(
                java.lang.reflect.InvocationTargetException.class,
                constructor::newInstance
        );

        assertInstanceOf(UnsupportedOperationException.class, exception.getCause());
        assertEquals("Utility class", exception.getCause().getMessage());
    }

    @Test
    void testFromMultipartFileWithIOException() throws IOException {
        // Arrange - Create a mock MultipartFile that throws IOException
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getBytes()).thenThrow(new IOException("Failed to read file"));

        // Act & Assert - Verify that RuntimeException is thrown wrapping the IOException
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> DifyFileConverter.from(mockFile)
        );

        // Verify the cause is the IOException we threw
        assertInstanceOf(IOException.class, exception.getCause());
        assertEquals("Failed to read file", exception.getCause().getMessage());
    }

    @Test
    void testConvertMultipartFileWithIOException() throws IOException {
        // Arrange - Create a mock MultipartFile that throws IOException
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getBytes()).thenThrow(new IOException("Read error"));

        // Act & Assert - Verify that convert() also handles IOException properly
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> DifyFileConverter.convert(mockFile)
        );

        // Verify the cause is the IOException
        assertInstanceOf(IOException.class, exception.getCause());
        assertEquals("Read error", exception.getCause().getMessage());
    }
}
