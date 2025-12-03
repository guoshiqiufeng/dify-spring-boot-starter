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
package io.github.guoshiqiufeng.dify.client.spring7.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for {@link MultipartInputStreamFileResource}.
 *
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/21 14:40
 */
public class MultipartInputStreamFileResourceTest {

    @Test
    @DisplayName("Test getFilename returns the filename provided in constructor")
    public void testGetFilename() {
        // Create test data
        String testData = "Test content";
        InputStream inputStream = new ByteArrayInputStream(testData.getBytes());
        String filename = "test-file.txt";

        // Create the resource
        MultipartInputStreamFileResource resource = new MultipartInputStreamFileResource(inputStream, filename);

        // Verify that getFilename returns the expected filename
        assertEquals(filename, resource.getFilename());
    }

    @Test
    @DisplayName("Test contentLength always returns -1")
    public void testContentLength() {
        // Create test data
        String testData = "Test content with specific length";
        InputStream inputStream = new ByteArrayInputStream(testData.getBytes());
        String filename = "test-file.txt";

        // Create the resource
        MultipartInputStreamFileResource resource = new MultipartInputStreamFileResource(inputStream, filename);

        // Verify that contentLength always returns -1 as specified in the implementation
        assertEquals(-1, resource.contentLength());
    }

    @Test
    @DisplayName("Test resource with empty filename")
    public void testEmptyFilename() {
        // Create test data
        String testData = "Test content";
        InputStream inputStream = new ByteArrayInputStream(testData.getBytes());
        String filename = "";

        // Create the resource
        MultipartInputStreamFileResource resource = new MultipartInputStreamFileResource(inputStream, filename);

        // Verify that getFilename returns the empty string
        assertEquals(filename, resource.getFilename());
    }

    @Test
    @DisplayName("Test resource with null filename")
    public void testNullFilename() {
        // Create test data
        String testData = "Test content";
        InputStream inputStream = new ByteArrayInputStream(testData.getBytes());
        String filename = null;

        // Create the resource
        MultipartInputStreamFileResource resource = new MultipartInputStreamFileResource(inputStream, filename);

        // Verify that getFilename returns null
        assertEquals(filename, resource.getFilename());
    }
}
