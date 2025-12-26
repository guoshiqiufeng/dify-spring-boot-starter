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
package io.github.guoshiqiufeng.dify.chat.dto.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link FilePreviewRequest}
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/1/6 10:30
 */
public class FilePreviewRequestTest {

    @Test
    public void testBasicConstructor() {
        FilePreviewRequest request = new FilePreviewRequest();
        assertNull(request.getFileId());
        assertNull(request.getAsAttachment());
        assertNull(request.getApiKey());
        assertNull(request.getUserId());
    }

    @Test
    public void testFileIdOnlyConstructor() {
        String fileId = "test-file-id-123";
        FilePreviewRequest request = new FilePreviewRequest(fileId);

        assertEquals(fileId, request.getFileId());
        assertFalse(request.getAsAttachment()); // Default is false
        assertNull(request.getApiKey());
        assertNull(request.getUserId());
    }

    @Test
    public void testFullConstructor() {
        String fileId = "test-file-id-123";
        Boolean asAttachment = true;
        String apiKey = "test-api-key";
        String userId = "test-user-id";

        FilePreviewRequest request = new FilePreviewRequest(fileId, asAttachment, apiKey, userId);

        assertEquals(fileId, request.getFileId());
        assertEquals(asAttachment, request.getAsAttachment());
        assertEquals(apiKey, request.getApiKey());
        assertEquals(userId, request.getUserId());
    }

    @Test
    public void testSettersAndGetters() {
        FilePreviewRequest request = new FilePreviewRequest();

        String fileId = "test-file-id-456";
        Boolean asAttachment = true;
        String apiKey = "test-api-key-456";
        String userId = "test-user-id-456";

        request.setFileId(fileId)
                .setAsAttachment(asAttachment)
                .setApiKey(apiKey);
        request.setUserId(userId);

        assertEquals(fileId, request.getFileId());
        assertEquals(asAttachment, request.getAsAttachment());
        assertEquals(apiKey, request.getApiKey());
        assertEquals(userId, request.getUserId());
    }

    @Test
    public void testChainedSetters() {
        FilePreviewRequest request = new FilePreviewRequest()
                .setFileId("test-file-id")
                .setAsAttachment(false);
        request.setApiKey("test-api-key");
        request.setUserId("test-user-id");

        assertNotNull(request);
        assertEquals("test-file-id", request.getFileId());
        assertFalse(request.getAsAttachment());
        assertEquals("test-api-key", request.getApiKey());
        assertEquals("test-user-id", request.getUserId());
    }

    @Test
    public void testJsonSerialization() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        FilePreviewRequest request = new FilePreviewRequest("test-file-123", true, "api-key-123", "user-123");

        String json = objectMapper.writeValueAsString(request);
        assertNotNull(json);
        assertTrue(json.contains("test-file-123"));
        assertTrue(json.contains("true"));
        assertTrue(json.contains("api-key-123"));
        assertTrue(json.contains("user-123"));
    }

    @Test
    public void testJsonDeserialization() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        String json = """
                {
                    "fileId": "test-file-456",
                    "asAttachment": false,
                    "apiKey": "api-key-456",
                    "userId": "user-456"
                }
                """;

        FilePreviewRequest request = objectMapper.readValue(json, FilePreviewRequest.class);

        assertNotNull(request);
        assertEquals("test-file-456", request.getFileId());
        assertFalse(request.getAsAttachment());
        assertEquals("api-key-456", request.getApiKey());
        assertEquals("user-456", request.getUserId());
    }

    @Test
    public void testEqualsAndHashCode() {
        FilePreviewRequest request1 = new FilePreviewRequest("file-id", true, "api-key", "user-id");
        FilePreviewRequest request2 = new FilePreviewRequest("file-id", true, "api-key", "user-id");
        FilePreviewRequest request3 = new FilePreviewRequest("different-file-id", true, "api-key", "user-id");

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());

        assertNotEquals(request1, request3);
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    @Test
    public void testToString() {
        FilePreviewRequest request = new FilePreviewRequest("test-file", false, "test-key", "test-user");
        String toString = request.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("FilePreviewRequest"));
    }

    @Test
    public void testAsAttachmentDefaultValue() {
        // Test with fileId-only constructor should default asAttachment to false
        FilePreviewRequest request = new FilePreviewRequest("test-file-id");
        assertNotNull(request.getAsAttachment());
        assertFalse(request.getAsAttachment());
    }

    @Test
    public void testNullAsAttachmentHandling() {
        FilePreviewRequest request = new FilePreviewRequest();
        request.setFileId("test-file-id");
        request.setAsAttachment(null);

        assertNull(request.getAsAttachment());
    }
}
