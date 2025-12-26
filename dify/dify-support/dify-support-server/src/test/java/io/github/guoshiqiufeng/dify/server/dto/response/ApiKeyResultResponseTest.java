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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ApiKeyResultResponse
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/5/2
 */
public class ApiKeyResultResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test default constructor
     */
    @Test
    public void testDefaultConstructor() {
        // Act
        ApiKeyResultResponse response = new ApiKeyResultResponse();

        // Assert
        assertNotNull(response);
        assertNull(response.getData());
        assertNull(response.getTotal());
        assertNull(response.getHasMore());
    }

    /**
     * Test getter and setter methods
     */
    @Test
    public void testGetterAndSetter() {
        // Arrange
        ApiKeyResultResponse response = new ApiKeyResultResponse();
        List<ApiKeyResponse> data = new ArrayList<>();
        ApiKeyResponse item = new ApiKeyResponse();
        item.setId("key-12345");
        item.setType("api_key");
        data.add(item);

        Integer total = 1;
        Boolean hasMore = false;

        // Act
        response.setData(data);
        response.setTotal(total);
        response.setHasMore(hasMore);

        // Assert
        assertEquals(data, response.getData());
        assertEquals(total, response.getTotal());
        assertEquals(hasMore, response.getHasMore());
    }

    /**
     * Test serialization with Jackson
     */
    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        // Create an instance with sample data
        ApiKeyResultResponse response = new ApiKeyResultResponse();

        List<ApiKeyResponse> data = new ArrayList<>();
        ApiKeyResponse item = new ApiKeyResponse();
        item.setId("key-12345");
        item.setType("api_key");
        item.setToken("sk-abcdefghijklmnopqrstuvwxyz");
        item.setLastUsedAt(1651234567890L);
        item.setCreatedAt(1650000000000L);
        data.add(item);

        response.setData(data);
        response.setTotal(1);
        response.setHasMore(false);

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(response);

        // Verify JSON contains expected property names
        assertTrue(json.contains("\"data\":"));
        assertTrue(json.contains("\"total\":"));
        assertTrue(json.contains("\"hasMore\":"));

        // Verify nested ApiKeyResponseVO properties
        assertTrue(json.contains("\"id\":\"key-12345\""));
        assertTrue(json.contains("\"type\":\"api_key\""));
        assertTrue(json.contains("\"token\":"));

        // Deserialize back to object
        ApiKeyResultResponse deserialized = objectMapper.readValue(json, ApiKeyResultResponse.class);

        // Verify the deserialized object matches the original
        assertEquals(response.getTotal(), deserialized.getTotal());
        assertEquals(response.getHasMore(), deserialized.getHasMore());

        // Verify data list
        assertNotNull(deserialized.getData());
        assertEquals(1, deserialized.getData().size());

        // Verify first item in data list
        ApiKeyResponse deserializedItem = deserialized.getData().get(0);
        assertEquals(item.getId(), deserializedItem.getId());
        assertEquals(item.getType(), deserializedItem.getType());
        assertEquals(item.getToken(), deserializedItem.getToken());
        assertEquals(item.getLastUsedAt(), deserializedItem.getLastUsedAt());
        assertEquals(item.getCreatedAt(), deserializedItem.getCreatedAt());
    }

    /**
     * Test JSON deserialization with aliases
     */
    @Test
    public void testJsonDeserialization() throws JsonProcessingException {
        // JSON with aliases
        String jsonWithAliases = "{\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"id\": \"key-12345\",\n" +
                "      \"type\": \"api_key\",\n" +
                "      \"token\": \"sk-abcdefghijklmnopqrstuvwxyz\",\n" +
                "      \"last_used_at\": 1651234567890,\n" +
                "      \"created_at\": 1650000000000\n" +
                "    }\n" +
                "  ],\n" +
                "  \"total\": 1,\n" +
                "  \"has_more\": false\n" +
                "}";

        // Deserialize with aliases
        ApiKeyResultResponse deserialized = objectMapper.readValue(jsonWithAliases, ApiKeyResultResponse.class);

        // Verify fields were correctly deserialized
        assertNotNull(deserialized.getData());
        assertEquals(1, deserialized.getData().size());
        assertEquals(1, deserialized.getTotal());
        assertEquals(Boolean.FALSE, deserialized.getHasMore());

        // Verify nested ApiKeyResponseVO
        ApiKeyResponse item = deserialized.getData().get(0);
        assertEquals("key-12345", item.getId());
        assertEquals("api_key", item.getType());
        assertEquals("sk-abcdefghijklmnopqrstuvwxyz", item.getToken());
        assertEquals(Long.valueOf(1651234567890L), item.getLastUsedAt());
        assertEquals(Long.valueOf(1650000000000L), item.getCreatedAt());
    }

    /**
     * Test equality and hash code
     */
    @Test
    public void testEqualsAndHashCode() {
        // Create two identical objects
        ApiKeyResultResponse response1 = new ApiKeyResultResponse();
        List<ApiKeyResponse> data1 = new ArrayList<>();
        ApiKeyResponse item1 = new ApiKeyResponse();
        item1.setId("key-12345");
        data1.add(item1);
        response1.setData(data1);
        response1.setTotal(1);
        response1.setHasMore(false);

        ApiKeyResultResponse response2 = new ApiKeyResultResponse();
        List<ApiKeyResponse> data2 = new ArrayList<>();
        ApiKeyResponse item2 = new ApiKeyResponse();
        item2.setId("key-12345");
        data2.add(item2);
        response2.setData(data2);
        response2.setTotal(1);
        response2.setHasMore(false);

        // Create a different object
        ApiKeyResultResponse response3 = new ApiKeyResultResponse();
        List<ApiKeyResponse> data3 = new ArrayList<>();
        ApiKeyResponse item3 = new ApiKeyResponse();
        item3.setId("key-67890");
        data3.add(item3);
        response3.setData(data3);
        response3.setTotal(2);
        response3.setHasMore(true);

        // Test equality
        assertEquals(response1, response2);
        assertNotEquals(response1, response3);

        // Test hash code
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }

    /**
     * Test with empty data list
     */
    @Test
    public void testWithEmptyDataList() throws JsonProcessingException {
        // Create an instance with empty data list
        ApiKeyResultResponse response = new ApiKeyResultResponse();
        response.setData(new ArrayList<>());
        response.setTotal(0);
        response.setHasMore(false);

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(response);

        // Verify JSON contains empty data array
        assertTrue(json.contains("\"data\":[]"));

        // Deserialize back to object
        ApiKeyResultResponse deserialized = objectMapper.readValue(json, ApiKeyResultResponse.class);

        // Verify the deserialized object has empty data list
        assertNotNull(deserialized.getData());
        assertTrue(deserialized.getData().isEmpty());
        assertEquals(0, deserialized.getTotal());
        assertEquals(Boolean.FALSE, deserialized.getHasMore());
    }
}
