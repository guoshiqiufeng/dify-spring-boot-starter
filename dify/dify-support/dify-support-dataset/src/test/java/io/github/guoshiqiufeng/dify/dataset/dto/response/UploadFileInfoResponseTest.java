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
package io.github.guoshiqiufeng.dify.dataset.dto.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for UploadFileInfoResponse
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/5/20
 */
public class UploadFileInfoResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test default constructor
     */
    @Test
    public void testDefaultConstructor() {
        // Act
        UploadFileInfoResponse response = new UploadFileInfoResponse();

        // Assert
        assertNotNull(response);
        assertNull(response.getId());
        assertNull(response.getName());
        assertNull(response.getSize());
        assertNull(response.getExtension());
        assertNull(response.getUrl());
        assertNull(response.getDownloadUrl());
        assertNull(response.getMimeType());
        assertNull(response.getCreatedBy());
        assertNull(response.getCreatedAt());
        assertNull(response.getPreviewUrl());
        assertNull(response.getSourceUrl());
        assertNull(response.getOriginalUrl());
        assertNull(response.getUserId());
        assertNull(response.getTenantId());
        assertNull(response.getConversationId());
        assertNull(response.getFileKey());
    }

    /**
     * Test getter and setter methods
     */
    @Test
    public void testGetterAndSetter() {
        // Arrange
        UploadFileInfoResponse response = new UploadFileInfoResponse();
        String id = "file-12345";
        String name = "test-file.pdf";
        Integer size = 12345;
        String extension = "pdf";
        String url = "https://example.com/file-12345";
        String downloadUrl = "https://example.com/download/file-12345";
        String mimeType = "application/pdf";
        String createdBy = "user-12345";
        Long createdAt = 1651234567890L;
        String previewUrl = "https://example.com/preview/file-12345";
        String sourceUrl = "https://example.com/source/file-12345";
        String originalUrl = "https://example.com/original/file-12345";
        String userId = "user-abc123";
        String tenantId = "tenant-xyz789";
        String conversationId = "conv-456def";
        String fileKey = "uploads/test-file.pdf";

        // Act
        response.setId(id);
        response.setName(name);
        response.setSize(size);
        response.setExtension(extension);
        response.setUrl(url);
        response.setDownloadUrl(downloadUrl);
        response.setMimeType(mimeType);
        response.setCreatedBy(createdBy);
        response.setCreatedAt(createdAt);
        response.setPreviewUrl(previewUrl);
        response.setSourceUrl(sourceUrl);
        response.setOriginalUrl(originalUrl);
        response.setUserId(userId);
        response.setTenantId(tenantId);
        response.setConversationId(conversationId);
        response.setFileKey(fileKey);

        // Assert
        assertEquals(id, response.getId());
        assertEquals(name, response.getName());
        assertEquals(size, response.getSize());
        assertEquals(extension, response.getExtension());
        assertEquals(url, response.getUrl());
        assertEquals(downloadUrl, response.getDownloadUrl());
        assertEquals(mimeType, response.getMimeType());
        assertEquals(createdBy, response.getCreatedBy());
        assertEquals(createdAt, response.getCreatedAt());
        assertEquals(previewUrl, response.getPreviewUrl());
        assertEquals(sourceUrl, response.getSourceUrl());
        assertEquals(originalUrl, response.getOriginalUrl());
        assertEquals(userId, response.getUserId());
        assertEquals(tenantId, response.getTenantId());
        assertEquals(conversationId, response.getConversationId());
        assertEquals(fileKey, response.getFileKey());
    }

    /**
     * Test serialization with Jackson
     */
    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        // Create an instance with sample data
        UploadFileInfoResponse response = new UploadFileInfoResponse();
        response.setId("file-12345");
        response.setName("test-file.pdf");
        response.setSize(12345);
        response.setExtension("pdf");
        response.setUrl("https://example.com/file-12345");
        response.setDownloadUrl("https://example.com/download/file-12345");
        response.setMimeType("application/pdf");
        response.setCreatedBy("user-12345");
        response.setCreatedAt(1651234567890L);

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(response);

        // Verify JSON contains expected property names
        assertTrue(json.contains("\"id\":"));
        assertTrue(json.contains("\"name\":"));
        assertTrue(json.contains("\"size\":"));
        assertTrue(json.contains("\"extension\":"));
        assertTrue(json.contains("\"url\":"));
        assertTrue(json.contains("\"downloadUrl\":"));
        assertTrue(json.contains("\"mimeType\":"));
        assertTrue(json.contains("\"createdBy\":"));
        assertTrue(json.contains("\"createdAt\":"));
        assertTrue(json.contains("\"previewUrl\":"));
        assertTrue(json.contains("\"sourceUrl\":"));
        assertTrue(json.contains("\"originalUrl\":"));
        assertTrue(json.contains("\"userId\":"));
        assertTrue(json.contains("\"tenantId\":"));
        assertTrue(json.contains("\"conversationId\":"));
        assertTrue(json.contains("\"fileKey\":"));

        // Deserialize back to object
        UploadFileInfoResponse deserialized = objectMapper.readValue(json, UploadFileInfoResponse.class);

        // Verify the deserialized object matches the original
        assertEquals(response.getId(), deserialized.getId());
        assertEquals(response.getName(), deserialized.getName());
        assertEquals(response.getSize(), deserialized.getSize());
        assertEquals(response.getExtension(), deserialized.getExtension());
        assertEquals(response.getUrl(), deserialized.getUrl());
        assertEquals(response.getDownloadUrl(), deserialized.getDownloadUrl());
        assertEquals(response.getMimeType(), deserialized.getMimeType());
        assertEquals(response.getCreatedBy(), deserialized.getCreatedBy());
        assertEquals(response.getCreatedAt(), deserialized.getCreatedAt());
    }

    /**
     * Test JSON deserialization with aliases (including mime_type bug fix)
     */
    @Test
    public void testJsonDeserialization() throws JsonProcessingException {
        // JSON with aliases
        String jsonWithAliases = "{\n" +
                "  \"id\": \"file-12345\",\n" +
                "  \"name\": \"test-file.pdf\",\n" +
                "  \"size\": 12345,\n" +
                "  \"extension\": \"pdf\",\n" +
                "  \"url\": \"https://example.com/file-12345\",\n" +
                "  \"download_url\": \"https://example.com/download/file-12345\",\n" +
                "  \"mime_type\": \"application/pdf\",\n" +
                "  \"created_by\": \"user-12345\",\n" +
                "  \"created_at\": 1651234567890,\n" +
                "  \"preview_url\": \"https://example.com/preview\",\n" +
                "  \"source_url\": \"https://example.com/source\",\n" +
                "  \"original_url\": \"https://example.com/original\",\n" +
                "  \"user_id\": \"user-abc123\",\n" +
                "  \"tenant_id\": \"tenant-xyz789\",\n" +
                "  \"conversation_id\": \"conv-456def\",\n" +
                "  \"file_key\": \"uploads/test-file.pdf\"\n" +
                "}";

        // Deserialize with aliases
        UploadFileInfoResponse deserialized = objectMapper.readValue(jsonWithAliases, UploadFileInfoResponse.class);

        // Verify fields were correctly deserialized
        assertEquals("file-12345", deserialized.getId());
        assertEquals("test-file.pdf", deserialized.getName());
        assertEquals(Integer.valueOf(12345), deserialized.getSize());
        assertEquals("pdf", deserialized.getExtension());
        assertEquals("https://example.com/file-12345", deserialized.getUrl());
        assertEquals("https://example.com/download/file-12345", deserialized.getDownloadUrl());
        assertEquals("application/pdf", deserialized.getMimeType());
        assertEquals("user-12345", deserialized.getCreatedBy());
        assertEquals(Long.valueOf(1651234567890L), deserialized.getCreatedAt());
        assertEquals("https://example.com/preview", deserialized.getPreviewUrl());
        assertEquals("https://example.com/source", deserialized.getSourceUrl());
        assertEquals("https://example.com/original", deserialized.getOriginalUrl());
        assertEquals("user-abc123", deserialized.getUserId());
        assertEquals("tenant-xyz789", deserialized.getTenantId());
        assertEquals("conv-456def", deserialized.getConversationId());
        assertEquals("uploads/test-file.pdf", deserialized.getFileKey());
    }

    /**
     * Test equality and hash code
     */
    @Test
    public void testEqualsAndHashCode() {
        // Create two identical objects
        UploadFileInfoResponse response1 = new UploadFileInfoResponse();
        response1.setId("file-12345");
        response1.setName("test-file.pdf");
        response1.setSize(12345);
        response1.setExtension("pdf");
        response1.setUrl("https://example.com/file-12345");
        response1.setDownloadUrl("https://example.com/download/file-12345");
        response1.setMimeType("application/pdf");
        response1.setCreatedBy("user-12345");
        response1.setCreatedAt(1651234567890L);
        response1.setPreviewUrl("https://example.com/preview");
        response1.setSourceUrl("https://example.com/source");
        response1.setOriginalUrl("https://example.com/original");
        response1.setUserId("user-abc123");
        response1.setTenantId("tenant-xyz789");
        response1.setConversationId("conv-456def");
        response1.setFileKey("uploads/test-file.pdf");

        UploadFileInfoResponse response2 = new UploadFileInfoResponse();
        response2.setId("file-12345");
        response2.setName("test-file.pdf");
        response2.setSize(12345);
        response2.setExtension("pdf");
        response2.setUrl("https://example.com/file-12345");
        response2.setDownloadUrl("https://example.com/download/file-12345");
        response2.setMimeType("application/pdf");
        response2.setCreatedBy("user-12345");
        response2.setCreatedAt(1651234567890L);
        response2.setPreviewUrl("https://example.com/preview");
        response2.setSourceUrl("https://example.com/source");
        response2.setOriginalUrl("https://example.com/original");
        response2.setUserId("user-abc123");
        response2.setTenantId("tenant-xyz789");
        response2.setConversationId("conv-456def");
        response2.setFileKey("uploads/test-file.pdf");

        // Create a different object
        UploadFileInfoResponse response3 = new UploadFileInfoResponse();
        response3.setId("file-67890");
        response3.setName("other-file.txt");
        response3.setSize(5678);
        response3.setExtension("txt");
        response3.setUrl("https://example.com/file-67890");
        response3.setDownloadUrl("https://example.com/download/file-67890");
        response3.setMimeType("text/plain");
        response3.setCreatedBy("user-67890");
        response3.setCreatedAt(1650123456789L);

        // Test equality
        assertEquals(response1, response2);
        assertNotEquals(response1, response3);

        // Test hash code
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }
}
