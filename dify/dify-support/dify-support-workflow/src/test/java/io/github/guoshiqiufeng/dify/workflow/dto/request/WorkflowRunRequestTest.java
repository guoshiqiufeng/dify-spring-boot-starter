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
package io.github.guoshiqiufeng.dify.workflow.dto.request;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link WorkflowRunRequest}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class WorkflowRunRequestTest {

    @Test
    public void testGetterAndSetter() {
        // Arrange
        WorkflowRunRequest request = new WorkflowRunRequest();
        List<WorkflowRunRequest.WorkflowFile> files = new ArrayList<>();
        WorkflowRunRequest.WorkflowFile file = new WorkflowRunRequest.WorkflowFile();
        file.setType("image");
        file.setTransferMethod("remote_url");
        file.setUrl("https://example.com/image.jpg");
        files.add(file);

        Map<String, Object> inputs = new HashMap<>();
        inputs.put("prompt", "This is a test prompt");
        inputs.put("temperature", 0.7);

        // Act
        request.setFiles(files);
        request.setInputs(inputs);

        // Assert
        assertEquals(files, request.getFiles());
        assertEquals(inputs, request.getInputs());
        assertEquals(1, request.getFiles().size());
        assertEquals("image", request.getFiles().get(0).getType());
        assertEquals("remote_url", request.getFiles().get(0).getTransferMethod());
        assertEquals("https://example.com/image.jpg", request.getFiles().get(0).getUrl());
    }

    @Test
    public void testDefaultValues() {
        // Arrange
        WorkflowRunRequest request = new WorkflowRunRequest();

        // Assert
        assertNull(request.getFiles());
        assertNull(request.getInputs());
    }

    @Test
    public void testInheritedFields() {
        // Arrange
        WorkflowRunRequest request = new WorkflowRunRequest();
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
    public void testWorkflowFileGetterAndSetter() {
        // Arrange
        WorkflowRunRequest.WorkflowFile file = new WorkflowRunRequest.WorkflowFile();
        String type = "image";
        String transferMethod = "remote_url";
        String url = "https://example.com/image.jpg";

        // Act
        file.setType(type);
        file.setTransferMethod(transferMethod);
        file.setUrl(url);

        // Assert
        assertEquals(type, file.getType());
        assertEquals(transferMethod, file.getTransferMethod());
        assertEquals(url, file.getUrl());
    }

    @Test
    public void testWorkflowFileDefaultValues() {
        // Arrange
        WorkflowRunRequest.WorkflowFile file = new WorkflowRunRequest.WorkflowFile();

        // Assert
        assertEquals("image", file.getType());
        assertEquals("remote_url", file.getTransferMethod());
        assertNull(file.getUrl());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        WorkflowRunRequest request1 = new WorkflowRunRequest();
        List<WorkflowRunRequest.WorkflowFile> files1 = new ArrayList<>();
        WorkflowRunRequest.WorkflowFile file1 = new WorkflowRunRequest.WorkflowFile();
        file1.setUrl("https://example.com/image1.jpg");
        files1.add(file1);
        Map<String, Object> inputs1 = new HashMap<>();
        inputs1.put("prompt", "Test prompt 1");
        request1.setFiles(files1);
        request1.setInputs(inputs1);

        WorkflowRunRequest request2 = new WorkflowRunRequest();
        List<WorkflowRunRequest.WorkflowFile> files2 = new ArrayList<>();
        WorkflowRunRequest.WorkflowFile file2 = new WorkflowRunRequest.WorkflowFile();
        file2.setUrl("https://example.com/image1.jpg");
        files2.add(file2);
        Map<String, Object> inputs2 = new HashMap<>();
        inputs2.put("prompt", "Test prompt 1");
        request2.setFiles(files2);
        request2.setInputs(inputs2);

        WorkflowRunRequest request3 = new WorkflowRunRequest();
        List<WorkflowRunRequest.WorkflowFile> files3 = new ArrayList<>();
        WorkflowRunRequest.WorkflowFile file3 = new WorkflowRunRequest.WorkflowFile();
        file3.setUrl("https://example.com/image2.jpg");
        files3.add(file3);
        Map<String, Object> inputs3 = new HashMap<>();
        inputs3.put("prompt", "Test prompt 2");
        request3.setFiles(files3);
        request3.setInputs(inputs3);

        // Assert
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1, request3);
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    @Test
    public void testWorkflowFileEqualsAndHashCode() {
        // Arrange
        WorkflowRunRequest.WorkflowFile file1 = new WorkflowRunRequest.WorkflowFile();
        file1.setType("image");
        file1.setTransferMethod("remote_url");
        file1.setUrl("https://example.com/image1.jpg");

        WorkflowRunRequest.WorkflowFile file2 = new WorkflowRunRequest.WorkflowFile();
        file2.setType("image");
        file2.setTransferMethod("remote_url");
        file2.setUrl("https://example.com/image1.jpg");

        WorkflowRunRequest.WorkflowFile file3 = new WorkflowRunRequest.WorkflowFile();
        file3.setType("video");
        file3.setTransferMethod("local_file");
        file3.setUrl("https://example.com/video.mp4");

        // Assert
        assertEquals(file1, file2);
        assertEquals(file1.hashCode(), file2.hashCode());
        assertNotEquals(file1, file3);
        assertNotEquals(file1.hashCode(), file3.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        WorkflowRunRequest request = new WorkflowRunRequest();
        List<WorkflowRunRequest.WorkflowFile> files = new ArrayList<>();
        WorkflowRunRequest.WorkflowFile file = new WorkflowRunRequest.WorkflowFile();
        file.setUrl("https://example.com/image.jpg");
        files.add(file);
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("prompt", "Test prompt");
        request.setFiles(files);
        request.setInputs(inputs);

        // Act
        String toString = request.toString();

        // Assert
        assertTrue(toString.contains("files="));
        assertTrue(toString.contains("inputs={prompt=Test prompt}"));
    }

    @Test
    public void testWorkflowFileToString() {
        // Arrange
        WorkflowRunRequest.WorkflowFile file = new WorkflowRunRequest.WorkflowFile();
        file.setType("image");
        file.setTransferMethod("remote_url");
        file.setUrl("https://example.com/image.jpg");

        // Act
        String toString = file.toString();

        // Assert
        assertTrue(toString.contains("type=image"));
        assertTrue(toString.contains("transferMethod=remote_url"));
        assertTrue(toString.contains("url=https://example.com/image.jpg"));
    }
} 