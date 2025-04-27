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
package io.github.guoshiqiufeng.dify.chat.dto.request;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link FileUploadRequest}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class FileUploadRequestTest {

    @Test
    public void testGetterAndSetter() {
        // Arrange
        FileUploadRequest request = new FileUploadRequest();
        MultipartFile file = new MockMultipartFile("test-file", "test.txt", "text/plain", "test content".getBytes());

        // Act
        request.setFile(file);

        // Assert
        assertEquals(file, request.getFile());
    }

    @Test
    public void testChainSetter() {
        // Arrange
        MultipartFile file = new MockMultipartFile("test-file", "test.txt", "text/plain", "test content".getBytes());

        // Act
        FileUploadRequest request = new FileUploadRequest()
                .setFile(file);

        // Assert
        assertEquals(file, request.getFile());
    }

    @Test
    public void testAllArgsConstructor() {
        // Arrange
        MultipartFile file = new MockMultipartFile("test-file", "test.txt", "text/plain", "test content".getBytes());

        // Act
        FileUploadRequest request = new FileUploadRequest(file);

        // Assert
        assertEquals(file, request.getFile());
    }

    @Test
    public void testNoArgsConstructor() {
        // Act
        FileUploadRequest request = new FileUploadRequest();

        // Assert
        assertNull(request.getFile());
    }

    @Test
    public void testInheritedFields() {
        // Arrange
        FileUploadRequest request = new FileUploadRequest();
        String apiKey = "test-api-key";
        String userId = "test-user-id";

        // Act
        request.setApiKey(apiKey);
        request.setUserId(userId);

        // Assert
        assertEquals(apiKey, request.getApiKey());
        assertEquals(userId, request.getUserId());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        MultipartFile file1 = new MockMultipartFile("test-file", "test1.txt", "text/plain", "content1".getBytes());
        MultipartFile file2 = new MockMultipartFile("test-file", "test2.txt", "text/plain", "content2".getBytes());

        FileUploadRequest request1 = new FileUploadRequest(file1);
        FileUploadRequest request2 = new FileUploadRequest(file1);
        FileUploadRequest request3 = new FileUploadRequest(file2);

        // Assert
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1, request3);
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        MultipartFile file = new MockMultipartFile("test-file", "test.txt", "text/plain", "test content".getBytes());
        FileUploadRequest request = new FileUploadRequest(file);

        // Act
        String toString = request.toString();

        // Assert
        assertTrue(toString.contains("file=" + file.toString()));
    }
}
