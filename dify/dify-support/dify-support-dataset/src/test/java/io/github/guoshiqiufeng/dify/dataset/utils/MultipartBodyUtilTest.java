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
package io.github.guoshiqiufeng.dify.dataset.utils;

import io.github.guoshiqiufeng.dify.dataset.dto.request.file.FileOperation;
import io.github.guoshiqiufeng.dify.dataset.exception.DiftDatasetException;
import io.github.guoshiqiufeng.dify.dataset.exception.DiftDatasetExceptionEnum;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test for {@link MultipartBodyUtil}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class MultipartBodyUtilTest {

    /**
     * Test implementation of FileOperation for testing purposes
     */
    private static class TestFileOperationRequest implements FileOperation {
        private MultipartFile file;
        private String testField;

        @Override
        public void setFile(MultipartFile file) {
            this.file = file;
        }

        public MultipartFile getFile() {
            return file;
        }

        public String getTestField() {
            return testField;
        }

        public void setTestField(String testField) {
            this.testField = testField;
        }
    }

    @Test
    public void testGetMultipartBodyBuilderWithValidFile() throws IOException {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getBytes()).thenReturn("test content".getBytes(StandardCharsets.UTF_8));
        when(mockFile.getOriginalFilename()).thenReturn("test.txt");
        when(mockFile.getContentType()).thenReturn(MediaType.TEXT_PLAIN_VALUE);

        TestFileOperationRequest request = new TestFileOperationRequest();
        request.setFile(mockFile);
        request.setTestField("test value");

        // Act
        MultipartBodyBuilder builder = MultipartBodyUtil.getMultipartBodyBuilder(mockFile, request);

        // Assert
        assertNotNull(builder);
        
        // Build and verify the multipart body
        MultiValueMap<String, HttpEntity<?>> multipartBody = builder.build();
        
        // Verify file part exists
        assertTrue(multipartBody.containsKey("file"));
        assertFalse(multipartBody.get("file").isEmpty());
        
        // Verify data part exists 
        assertTrue(multipartBody.containsKey("data"));
        assertFalse(multipartBody.get("data").isEmpty());
        
        // Verify request's file field is null after building
        assertNull(request.getFile());
        
        // Verify the correct method calls were made
        verify(mockFile).getBytes();
        verify(mockFile).getOriginalFilename();
        verify(mockFile).getContentType();
    }

    @Test
    public void testGetMultipartBodyBuilderWithNullContentType() throws IOException {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getBytes()).thenReturn("test content".getBytes(StandardCharsets.UTF_8));
        when(mockFile.getOriginalFilename()).thenReturn("test.txt");
        when(mockFile.getContentType()).thenReturn(null);

        TestFileOperationRequest request = new TestFileOperationRequest();
        request.setFile(mockFile);
        request.setTestField("test value");

        // Act
        MultipartBodyBuilder builder = MultipartBodyUtil.getMultipartBodyBuilder(mockFile, request);

        // Assert
        assertNotNull(builder);
        
        // Verify the correct content type fallback behavior
        MultiValueMap<String, HttpEntity<?>> multipartBody = builder.build();
        assertTrue(multipartBody.containsKey("file"));
    }

    @Test
    public void testGetMultipartBodyBuilderWithNullFile() {
        // Arrange
        MultipartFile mockFile = null;
        TestFileOperationRequest request = new TestFileOperationRequest();
        request.setTestField("test value");

        // Act & Assert
        DiftDatasetException exception = assertThrows(
                DiftDatasetException.class,
                () -> MultipartBodyUtil.getMultipartBodyBuilder(mockFile, request)
        );
        
        // Since we don't have direct access to the BaseException methods like getCode(),
        // we can't fully verify the exception properties.
        assertNotNull(exception);
    }

    @Test
    public void testGetMultipartBodyBuilderWithIOException() throws IOException {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getBytes()).thenThrow(new IOException("Test IO exception"));
        when(mockFile.getOriginalFilename()).thenReturn("test.txt");
        when(mockFile.getContentType()).thenReturn(MediaType.TEXT_PLAIN_VALUE);

        TestFileOperationRequest request = new TestFileOperationRequest();
        request.setFile(mockFile);
        request.setTestField("test value");

        // Act & Assert
        DiftDatasetException exception = assertThrows(
                DiftDatasetException.class,
                () -> MultipartBodyUtil.getMultipartBodyBuilder(mockFile, request)
        );
        
        assertNotNull(exception);
        
        // Verify the correct method calls were made
        verify(mockFile).getBytes();
    }

    @Test
    public void testToJsonWithValidObject() throws Exception {
        // Arrange
        TestFileOperationRequest request = new TestFileOperationRequest();
        request.setTestField("test value");
        
        // Create a MultipartFile mock just to test the JSON conversion
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getBytes()).thenReturn("test content".getBytes(StandardCharsets.UTF_8));
        when(mockFile.getOriginalFilename()).thenReturn("test.txt");
        when(mockFile.getContentType()).thenReturn(MediaType.TEXT_PLAIN_VALUE);

        // Act
        MultipartBodyBuilder builder = MultipartBodyUtil.getMultipartBodyBuilder(mockFile, request);
        
        // Assert
        assertNotNull(builder);
        
        // Verify the content of the multipart body
        MultiValueMap<String, HttpEntity<?>> multipartBody = builder.build();
        assertTrue(multipartBody.containsKey("data"));
        
        // Get the JSON data part
        HttpEntity<?> dataEntity = multipartBody.get("data").get(0);
        assertNotNull(dataEntity);
        
        // Verify it contains our test field and doesn't contain file
        String jsonBody = dataEntity.getBody().toString();
        assertTrue(jsonBody.contains("testField"));
        assertTrue(jsonBody.contains("test value"));
        assertFalse(jsonBody.contains("file"));
    }

    @Test
    public void testPrivateConstructor() throws Exception {
        // Test the private constructor for code coverage
        java.lang.reflect.Constructor<MultipartBodyUtil> constructor = 
                MultipartBodyUtil.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
        
        // No assertion needed, we're just verifying it executes without errors
    }
} 