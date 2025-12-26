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
package io.github.guoshiqiufeng.dify.server.dto.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ApiKeyResponse
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/5/2
 */
public class ApiKeyResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test default constructor
     */
    @Test
    public void testDefaultConstructor() {
        // Act
        ApiKeyResponse response = new ApiKeyResponse();

        // Assert
        assertNotNull(response);
        assertNull(response.getId());
        assertNull(response.getType());
        assertNull(response.getToken());
        assertNull(response.getLastUsedAt());
        assertNull(response.getCreatedAt());
    }

    /**
     * Test getter and setter methods
     */
    @Test
    public void testGetterAndSetter() {
        // Arrange
        ApiKeyResponse response = new ApiKeyResponse();
        String id = "key-12345";
        String type = "api_key";
        String token = "sk-abcdefghijklmnopqrstuvwxyz";
        Long lastUsedAt = 1651234567890L;
        Long createdAt = 1650000000000L;

        // Act
        response.setId(id);
        response.setType(type);
        response.setToken(token);
        response.setLastUsedAt(lastUsedAt);
        response.setCreatedAt(createdAt);

        // Assert
        assertEquals(id, response.getId());
        assertEquals(type, response.getType());
        assertEquals(token, response.getToken());
        assertEquals(lastUsedAt, response.getLastUsedAt());
        assertEquals(createdAt, response.getCreatedAt());
    }

    /**
     * Test serialization with Jackson
     */
    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        // Create an instance with sample data
        ApiKeyResponse response = new ApiKeyResponse();
        response.setId("key-12345");
        response.setType("api_key");
        response.setToken("sk-abcdefghijklmnopqrstuvwxyz");
        response.setLastUsedAt(1651234567890L);
        response.setCreatedAt(1650000000000L);

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(response);

        // Verify JSON contains expected property names
        assertTrue(json.contains("\"id\":"));
        assertTrue(json.contains("\"type\":"));
        assertTrue(json.contains("\"token\":"));
        assertTrue(json.contains("\"lastUsedAt\":"));
        assertTrue(json.contains("\"createdAt\":"));

        // Deserialize back to object
        ApiKeyResponse deserialized = objectMapper.readValue(json, ApiKeyResponse.class);

        // Verify the deserialized object matches the original
        assertEquals(response.getId(), deserialized.getId());
        assertEquals(response.getType(), deserialized.getType());
        assertEquals(response.getToken(), deserialized.getToken());
        assertEquals(response.getLastUsedAt(), deserialized.getLastUsedAt());
        assertEquals(response.getCreatedAt(), deserialized.getCreatedAt());
    }

    /**
     * Test JSON deserialization with aliases
     */
    @Test
    public void testJsonDeserialization() throws JsonProcessingException {
        // JSON with aliases
        String jsonWithAliases = "{\n" +
                "  \"id\": \"key-12345\",\n" +
                "  \"type\": \"api_key\",\n" +
                "  \"token\": \"sk-abcdefghijklmnopqrstuvwxyz\",\n" +
                "  \"last_used_at\": 1651234567890,\n" +
                "  \"created_at\": 1650000000000\n" +
                "}";

        // Deserialize with aliases
        ApiKeyResponse deserialized = objectMapper.readValue(jsonWithAliases, ApiKeyResponse.class);

        // Verify fields were correctly deserialized
        assertEquals("key-12345", deserialized.getId());
        assertEquals("api_key", deserialized.getType());
        assertEquals("sk-abcdefghijklmnopqrstuvwxyz", deserialized.getToken());
        assertEquals(Long.valueOf(1651234567890L), deserialized.getLastUsedAt());
        assertEquals(Long.valueOf(1650000000000L), deserialized.getCreatedAt());
    }

    /**
     * Test equality and hash code
     */
    @Test
    public void testEqualsAndHashCode() {
        // Create two identical objects
        ApiKeyResponse response1 = new ApiKeyResponse();
        response1.setId("key-12345");
        response1.setType("api_key");
        response1.setToken("sk-abcdefghijklmnopqrstuvwxyz");
        response1.setLastUsedAt(1651234567890L);
        response1.setCreatedAt(1650000000000L);

        ApiKeyResponse response2 = new ApiKeyResponse();
        response2.setId("key-12345");
        response2.setType("api_key");
        response2.setToken("sk-abcdefghijklmnopqrstuvwxyz");
        response2.setLastUsedAt(1651234567890L);
        response2.setCreatedAt(1650000000000L);

        // Create a different object
        ApiKeyResponse response3 = new ApiKeyResponse();
        response3.setId("key-67890");
        response3.setType("different_type");
        response3.setToken("sk-differenttoken");
        response3.setLastUsedAt(1660000000000L);
        response3.setCreatedAt(1655000000000L);

        // Test equality
        assertEquals(response1, response2);
        assertNotEquals(response1, response3);

        // Test hash code
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }
}
