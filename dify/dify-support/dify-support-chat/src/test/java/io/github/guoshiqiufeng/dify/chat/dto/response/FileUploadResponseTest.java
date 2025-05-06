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
package io.github.guoshiqiufeng.dify.chat.dto.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for FileUploadResponse
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/5/2
 */
public class FileUploadResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test default constructor
     */
    @Test
    public void testDefaultConstructor() {
        // Act
        FileUploadResponse response = new FileUploadResponse();

        // Assert
        assertNotNull(response);
        assertNull(response.getId());
        assertNull(response.getName());
        assertNull(response.getSize());
        assertNull(response.getExtension());
        assertNull(response.getMimeType());
        assertNull(response.getCreatedBy());
        assertNull(response.getCreatedAt());
    }

    /**
     * Test getter and setter methods
     */
    @Test
    public void testGetterAndSetter() {
        // Arrange
        FileUploadResponse response = new FileUploadResponse();
        String id = "file-12345";
        String name = "test-file.pdf";
        Integer size = 12345;
        String extension = "pdf";
        String mimeType = "application/pdf";
        String createdBy = "user-12345";
        Long createdAt = 1651234567890L;

        // Act
        response.setId(id);
        response.setName(name);
        response.setSize(size);
        response.setExtension(extension);
        response.setMimeType(mimeType);
        response.setCreatedBy(createdBy);
        response.setCreatedAt(createdAt);

        // Assert
        assertEquals(id, response.getId());
        assertEquals(name, response.getName());
        assertEquals(size, response.getSize());
        assertEquals(extension, response.getExtension());
        assertEquals(mimeType, response.getMimeType());
        assertEquals(createdBy, response.getCreatedBy());
        assertEquals(createdAt, response.getCreatedAt());
    }

    /**
     * Test serialization with Jackson
     */
    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        // Create an instance with sample data
        FileUploadResponse response = new FileUploadResponse();
        response.setId("file-12345");
        response.setName("test-file.pdf");
        response.setSize(12345);
        response.setExtension("pdf");
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
        assertTrue(json.contains("\"mimeType\":"));
        assertTrue(json.contains("\"createdBy\":"));
        assertTrue(json.contains("\"createdAt\":"));

        // Deserialize back to object
        FileUploadResponse deserialized = objectMapper.readValue(json, FileUploadResponse.class);

        // Verify the deserialized object matches the original
        assertEquals(response.getId(), deserialized.getId());
        assertEquals(response.getName(), deserialized.getName());
        assertEquals(response.getSize(), deserialized.getSize());
        assertEquals(response.getExtension(), deserialized.getExtension());
        assertEquals(response.getMimeType(), deserialized.getMimeType());
        assertEquals(response.getCreatedBy(), deserialized.getCreatedBy());
        assertEquals(response.getCreatedAt(), deserialized.getCreatedAt());
    }

    /**
     * Test JSON deserialization with aliases
     */
    @Test
    public void testJsonDeserialization() throws JsonProcessingException {
        // JSON with aliases
        String jsonWithAliases = "{\n" +
                "  \"id\": \"file-12345\",\n" +
                "  \"name\": \"test-file.pdf\",\n" +
                "  \"size\": 12345,\n" +
                "  \"extension\": \"pdf\",\n" +
                "  \"mime_type\": \"application/pdf\",\n" +
                "  \"created_by\": \"user-12345\",\n" +
                "  \"created_at\": 1651234567890\n" +
                "}";

        // Deserialize with aliases
        FileUploadResponse deserialized = objectMapper.readValue(jsonWithAliases, FileUploadResponse.class);

        // Verify fields were correctly deserialized
        assertEquals("file-12345", deserialized.getId());
        assertEquals("test-file.pdf", deserialized.getName());
        assertEquals(Integer.valueOf(12345), deserialized.getSize());
        assertEquals("pdf", deserialized.getExtension());
        assertEquals("application/pdf", deserialized.getMimeType());
        assertEquals("user-12345", deserialized.getCreatedBy());
        assertEquals(Long.valueOf(1651234567890L), deserialized.getCreatedAt());
    }

    /**
     * Test equality and hash code
     */
    @Test
    public void testEqualsAndHashCode() {
        // Create two identical objects
        FileUploadResponse response1 = new FileUploadResponse();
        response1.setId("file-12345");
        response1.setName("test-file.pdf");
        response1.setSize(12345);
        response1.setExtension("pdf");
        response1.setMimeType("application/pdf");
        response1.setCreatedBy("user-12345");
        response1.setCreatedAt(1651234567890L);

        FileUploadResponse response2 = new FileUploadResponse();
        response2.setId("file-12345");
        response2.setName("test-file.pdf");
        response2.setSize(12345);
        response2.setExtension("pdf");
        response2.setMimeType("application/pdf");
        response2.setCreatedBy("user-12345");
        response2.setCreatedAt(1651234567890L);

        // Create a different object
        FileUploadResponse response3 = new FileUploadResponse();
        response3.setId("file-67890");
        response3.setName("other-file.txt");
        response3.setSize(5678);
        response3.setExtension("txt");
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
