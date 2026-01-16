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
package io.github.guoshiqiufeng.dify.support.impl.utils;

import io.github.guoshiqiufeng.dify.core.pojo.DifyFile;
import io.github.guoshiqiufeng.dify.core.utils.MultipartBodyBuilder;
import io.github.guoshiqiufeng.dify.dataset.dto.request.file.FileOperation;
import io.github.guoshiqiufeng.dify.dataset.exception.DiftDatasetException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MultipartBodyUtil
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/01/06
 */
class MultipartBodyUtilTest {

    /**
     * Simple test implementation of FileOperation
     */
    private static class TestFileOperation implements FileOperation {
        private DifyFile file;

        @Override
        public void setFile(DifyFile file) {
            this.file = file;
        }

        public DifyFile getFile() {
            return file;
        }
    }

    @Test
    void testGetMultipartBodyBuilderWithValidFile() {
        // Arrange
        byte[] content = "test content".getBytes();
        DifyFile file = new DifyFile("test.txt", "text/plain", content);
        TestFileOperation request = new TestFileOperation();

        // Act
        MultipartBodyBuilder builder = MultipartBodyUtil.getMultipartBodyBuilder(file, request);

        // Assert
        assertNotNull(builder);
        assertNull(request.getFile());
    }

    @Test
    void testGetMultipartBodyBuilderWithNullFile() {
        // Arrange
        TestFileOperation request = new TestFileOperation();

        // Act & Assert
        assertThrows(DiftDatasetException.class, () -> {
            MultipartBodyUtil.getMultipartBodyBuilder(null, request);
        });
    }

    @Test
    void testGetMultipartBodyBuilderWithEmptyContentType() {
        // Arrange
        byte[] content = "test content".getBytes();
        DifyFile file = new DifyFile("test.txt", null, content);
        TestFileOperation request = new TestFileOperation();

        // Act
        MultipartBodyBuilder builder = MultipartBodyUtil.getMultipartBodyBuilder(file, request);

        // Assert
        assertNotNull(builder);
        // Should default to text/plain when content type is null
    }

    @Test
    void testGetMultipartBodyBuilderWithEmptyStringContentType() {
        // Arrange
        byte[] content = "test content".getBytes();
        DifyFile file = new DifyFile("test.txt", "", content);
        TestFileOperation request = new TestFileOperation();

        // Act
        MultipartBodyBuilder builder = MultipartBodyUtil.getMultipartBodyBuilder(file, request);

        // Assert
        assertNotNull(builder);
        // Should default to text/plain when content type is empty
    }

    @Test
    void testGetMultipartBodyBuilderWithDifferentContentTypes() {
        // Test with JSON content type
        byte[] jsonContent = "{\"key\":\"value\"}".getBytes();
        DifyFile jsonFile = new DifyFile("data.json", "application/json", jsonContent);
        TestFileOperation jsonRequest = new TestFileOperation();
        MultipartBodyBuilder jsonBuilder = MultipartBodyUtil.getMultipartBodyBuilder(jsonFile, jsonRequest);
        assertNotNull(jsonBuilder);

        // Test with PDF content type
        byte[] pdfContent = new byte[]{0x25, 0x50, 0x44, 0x46}; // PDF header
        DifyFile pdfFile = new DifyFile("document.pdf", "application/pdf", pdfContent);
        TestFileOperation pdfRequest = new TestFileOperation();
        MultipartBodyBuilder pdfBuilder = MultipartBodyUtil.getMultipartBodyBuilder(pdfFile, pdfRequest);
        assertNotNull(pdfBuilder);
    }

    @Test
    void testGetMultipartBodyBuilderSetsFileToNull() {
        // Arrange
        byte[] content = "test content".getBytes();
        DifyFile file = new DifyFile("test.txt", "text/plain", content);
        TestFileOperation request = new TestFileOperation();

        // Act
        MultipartBodyBuilder builder = MultipartBodyUtil.getMultipartBodyBuilder(file, request);

        // Assert
        assertNotNull(builder);
        assertNull(request.getFile()); // File should be set to null after processing
    }

    @Test
    void testUtilityClassCannotBeInstantiated() throws Exception {
        // Act & Assert
        java.lang.reflect.Constructor<MultipartBodyUtil> constructor =
                MultipartBodyUtil.class.getDeclaredConstructor();
        assertTrue(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    void testGetMultipartBodyBuilderWithLargeFile() {
        // Arrange
        byte[] largeContent = new byte[1024 * 1024]; // 1MB
        for (int i = 0; i < largeContent.length; i++) {
            largeContent[i] = (byte) (i % 256);
        }
        DifyFile file = new DifyFile("large.bin", "application/octet-stream", largeContent);
        TestFileOperation request = new TestFileOperation();

        // Act
        MultipartBodyBuilder builder = MultipartBodyUtil.getMultipartBodyBuilder(file, request);

        // Assert
        assertNotNull(builder);
    }

    @Test
    void testGetMultipartBodyBuilderWithSpecialCharactersInFilename() {
        // Arrange
        byte[] content = "test content".getBytes();
        DifyFile file = new DifyFile("test file (1).txt", "text/plain", content);
        TestFileOperation request = new TestFileOperation();

        // Act
        MultipartBodyBuilder builder = MultipartBodyUtil.getMultipartBodyBuilder(file, request);

        // Assert
        assertNotNull(builder);
    }

    @Test
    void testGetMultipartBodyBuilderWithUnicodeFilename() {
        // Arrange
        byte[] content = "test content".getBytes();
        DifyFile file = new DifyFile("测试文件.txt", "text/plain", content);
        TestFileOperation request = new TestFileOperation();

        // Act
        MultipartBodyBuilder builder = MultipartBodyUtil.getMultipartBodyBuilder(file, request);

        // Assert
        assertNotNull(builder);
    }
}
