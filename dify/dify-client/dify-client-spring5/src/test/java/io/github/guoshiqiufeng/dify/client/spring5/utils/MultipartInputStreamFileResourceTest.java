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
package io.github.guoshiqiufeng.dify.client.spring5.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author yanghq
 * @version 0.9.0
 * @since 2025/4/21 11:50
 */
public class MultipartInputStreamFileResourceTest {

    @Test
    @DisplayName("Test constructor and getFilename")
    public void testConstructorAndGetFilename() {
        // Create test data
        byte[] testData = "Test content".getBytes();
        InputStream inputStream = new ByteArrayInputStream(testData);
        String filename = "test-file.txt";
        
        // Create the resource
        MultipartInputStreamFileResource resource = new MultipartInputStreamFileResource(inputStream, filename);
        
        // Verify the filename is set correctly
        assertEquals(filename, resource.getFilename(), "Filename should match the one provided in constructor");
    }
    
    @Test
    @DisplayName("Test contentLength returns -1")
    public void testContentLengthReturnsNegativeOne() {
        // Create test data
        byte[] testData = "Test content".getBytes();
        InputStream inputStream = new ByteArrayInputStream(testData);
        String filename = "test-file.txt";
        
        // Create the resource
        MultipartInputStreamFileResource resource = new MultipartInputStreamFileResource(inputStream, filename);
        
        // Verify contentLength returns -1 as specified in the implementation
        assertEquals(-1, resource.contentLength(), "contentLength should return -1");
    }
    
    @Test
    @DisplayName("Test with null filename")
    public void testWithNullFilename() {
        // Create test data
        byte[] testData = "Test content".getBytes();
        InputStream inputStream = new ByteArrayInputStream(testData);
        
        // Create the resource with null filename
        MultipartInputStreamFileResource resource = new MultipartInputStreamFileResource(inputStream, null);
        
        // Verify the filename is null
        assertNull(resource.getFilename(), "Filename should be null");
    }
    
    @Test
    @DisplayName("Test with empty filename")
    public void testWithEmptyFilename() {
        // Create test data
        byte[] testData = "Test content".getBytes();
        InputStream inputStream = new ByteArrayInputStream(testData);
        String filename = "";
        
        // Create the resource with empty filename
        MultipartInputStreamFileResource resource = new MultipartInputStreamFileResource(inputStream, filename);
        
        // Verify the filename is empty
        assertEquals("", resource.getFilename(), "Filename should be empty string");
    }
}
