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
package io.github.guoshiqiufeng.dify.core.pojo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DifyFile
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/01/13
 */
class DifyFileTest {

    @TempDir
    Path tempDir;

    @Test
    void testDefaultConstructor() {
        DifyFile file = new DifyFile();
        assertNotNull(file);
        assertNull(file.getFilename());
        assertNull(file.getContentType());
        assertNull(file.getContent());
        assertEquals(0, file.getSize());
    }

    @Test
    void testConstructorWithAllFields() {
        String filename = "test.txt";
        String contentType = "text/plain";
        byte[] content = "Hello World".getBytes();

        DifyFile file = new DifyFile(filename, contentType, content);

        assertEquals(filename, file.getFilename());
        assertEquals(contentType, file.getContentType());
        assertArrayEquals(content, file.getContent());
        assertEquals(content.length, file.getSize());
    }

    @Test
    void testConstructorWithNullContent() {
        DifyFile file = new DifyFile("test.txt", "text/plain", null);
        assertEquals(0, file.getSize());
        assertNull(file.getContent());
    }

    @Test
    void testFromFile() throws IOException {
        // Create a temporary file
        Path tempFile = tempDir.resolve("test.txt");
        String content = "Test content";
        Files.write(tempFile, content.getBytes());

        DifyFile difyFile = DifyFile.from(tempFile.toFile());

        assertNotNull(difyFile);
        assertEquals("test.txt", difyFile.getFilename());
        assertArrayEquals(content.getBytes(), difyFile.getContent());
        assertEquals(content.length(), difyFile.getSize());
    }

    @Test
    void testFromFileWithNullFile() {
        assertThrows(IllegalArgumentException.class, () -> {
            DifyFile.from((File) null);
        });
    }

    @Test
    void testFromFileWithNonExistentFile() {
        File nonExistent = new File("/nonexistent/file.txt");
        assertThrows(IllegalArgumentException.class, () -> {
            DifyFile.from(nonExistent);
        });
    }

    @Test
    void testFromPath() throws IOException {
        Path tempFile = tempDir.resolve("test.txt");
        String content = "Test content";
        Files.write(tempFile, content.getBytes());

        DifyFile difyFile = DifyFile.from(tempFile);

        assertNotNull(difyFile);
        assertEquals("test.txt", difyFile.getFilename());
        assertArrayEquals(content.getBytes(), difyFile.getContent());
        assertEquals(content.length(), difyFile.getSize());
    }

    @Test
    void testFromPathWithNullPath() {
        assertThrows(IllegalArgumentException.class, () -> {
            DifyFile.from((Path) null);
        });
    }

    @Test
    void testFromPathWithNonExistentPath() {
        Path nonExistent = tempDir.resolve("nonexistent.txt");
        assertThrows(IllegalArgumentException.class, () -> {
            DifyFile.from(nonExistent);
        });
    }

    @Test
    void testFromInputStream() throws IOException {
        String content = "Test content from stream";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes());

        DifyFile difyFile = DifyFile.from(inputStream, "test.txt", "text/plain");

        assertNotNull(difyFile);
        assertEquals("test.txt", difyFile.getFilename());
        assertEquals("text/plain", difyFile.getContentType());
        assertArrayEquals(content.getBytes(), difyFile.getContent());
        assertEquals(content.length(), difyFile.getSize());
    }

    @Test
    void testFromInputStreamWithNullStream() {
        assertThrows(IllegalArgumentException.class, () -> {
            DifyFile.from((InputStream) null, "test.txt", "text/plain");
        });
    }

    @Test
    void testFromInputStreamWithLargeContent() throws IOException {
        // Create content larger than buffer size (8192 bytes)
        byte[] largeContent = new byte[10000];
        for (int i = 0; i < largeContent.length; i++) {
            largeContent[i] = (byte) (i % 256);
        }
        ByteArrayInputStream inputStream = new ByteArrayInputStream(largeContent);

        DifyFile difyFile = DifyFile.from(inputStream, "large.bin", "application/octet-stream");

        assertNotNull(difyFile);
        assertArrayEquals(largeContent, difyFile.getContent());
        assertEquals(largeContent.length, difyFile.getSize());
    }

    @Test
    void testFromByteArray() {
        byte[] content = "Test content".getBytes();
        DifyFile difyFile = DifyFile.from(content, "test.txt", "text/plain");

        assertNotNull(difyFile);
        assertEquals("test.txt", difyFile.getFilename());
        assertEquals("text/plain", difyFile.getContentType());
        assertArrayEquals(content, difyFile.getContent());
        assertEquals(content.length, difyFile.getSize());
    }

    @Test
    void testGetBytes() {
        byte[] content = "Test content".getBytes();
        DifyFile file = new DifyFile("test.txt", "text/plain", content);

        assertArrayEquals(content, file.getBytes());
        assertSame(file.getContent(), file.getBytes());
    }

    @Test
    void testSetContent() {
        DifyFile file = new DifyFile();
        byte[] content = "New content".getBytes();

        file.setContent(content);

        assertArrayEquals(content, file.getContent());
        assertEquals(content.length, file.getSize());
    }

    @Test
    void testSetContentWithNull() {
        DifyFile file = new DifyFile("test.txt", "text/plain", "old".getBytes());
        file.setContent(null);

        assertNull(file.getContent());
        assertEquals(0, file.getSize());
    }

    @Test
    void testIsEmpty() {
        // Empty file
        DifyFile emptyFile = new DifyFile();
        assertTrue(emptyFile.isEmpty());

        // File with null content
        DifyFile nullContentFile = new DifyFile("test.txt", "text/plain", null);
        assertTrue(nullContentFile.isEmpty());

        // File with empty byte array
        DifyFile emptyArrayFile = new DifyFile("test.txt", "text/plain", new byte[0]);
        assertTrue(emptyArrayFile.isEmpty());

        // File with content
        DifyFile fileWithContent = new DifyFile("test.txt", "text/plain", "content".getBytes());
        assertFalse(fileWithContent.isEmpty());
    }

    @Test
    void testGetInputStream() throws IOException {
        byte[] content = "Test content".getBytes();
        DifyFile file = new DifyFile("test.txt", "text/plain", content);

        InputStream inputStream = file.getInputStream();
        assertNotNull(inputStream);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        assertArrayEquals(content, outputStream.toByteArray());
    }

    @Test
    void testGetInputStreamWithNullContent() throws IOException {
        DifyFile file = new DifyFile();
        InputStream inputStream = file.getInputStream();
        assertNotNull(inputStream);
        assertEquals(0, inputStream.available());
    }

    @Test
    void testSettersAndGetters() {
        DifyFile file = new DifyFile();

        file.setFilename("test.txt");
        assertEquals("test.txt", file.getFilename());

        file.setContentType("text/plain");
        assertEquals("text/plain", file.getContentType());

        byte[] content = "content".getBytes();
        file.setContent(content);
        assertArrayEquals(content, file.getContent());
        assertEquals(content.length, file.getSize());
    }

    @Test
    void testSerializable() {
        DifyFile file = new DifyFile("test.txt", "text/plain", "content".getBytes());
        assertTrue(file instanceof Serializable);
    }

    @Test
    void testWithDifferentFileTypes() throws IOException {
        // Text file
        Path textFile = tempDir.resolve("test.txt");
        Files.write(textFile, "text content".getBytes());
        DifyFile textDifyFile = DifyFile.from(textFile);
        assertEquals("test.txt", textDifyFile.getFilename());

        // Binary file
        Path binFile = tempDir.resolve("test.bin");
        Files.write(binFile, new byte[]{0x00, 0x01, 0x02, 0x03});
        DifyFile binDifyFile = DifyFile.from(binFile);
        assertEquals("test.bin", binDifyFile.getFilename());
        assertEquals(4, binDifyFile.getSize());
    }

    @Test
    void testEmptyFile() throws IOException {
        Path emptyFile = tempDir.resolve("empty.txt");
        Files.write(emptyFile, new byte[0]);

        DifyFile difyFile = DifyFile.from(emptyFile);

        assertNotNull(difyFile);
        assertEquals("empty.txt", difyFile.getFilename());
        assertEquals(0, difyFile.getSize());
        assertTrue(difyFile.isEmpty());
    }

    @Test
    void testLargeFile() throws IOException {
        // Create a large file (1MB)
        byte[] largeContent = new byte[1024 * 1024];
        for (int i = 0; i < largeContent.length; i++) {
            largeContent[i] = (byte) (i % 256);
        }

        Path largeFile = tempDir.resolve("large.bin");
        Files.write(largeFile, largeContent);

        DifyFile difyFile = DifyFile.from(largeFile);

        assertNotNull(difyFile);
        assertEquals(largeContent.length, difyFile.getSize());
        assertArrayEquals(largeContent, difyFile.getContent());
    }

    @Test
    void testSpecialCharactersInFilename() throws IOException {
        Path specialFile = tempDir.resolve("test-file_123.txt");
        Files.write(specialFile, "content".getBytes());

        DifyFile difyFile = DifyFile.from(specialFile);

        assertEquals("test-file_123.txt", difyFile.getFilename());
    }
}
