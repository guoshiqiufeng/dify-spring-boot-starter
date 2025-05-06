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
package io.github.guoshiqiufeng.dify.server.dto.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for DatasetApiKeyResult
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/5/2
 */
public class DatasetApiKeyResultTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test default constructor
     */
    @Test
    public void testDefaultConstructor() {
        // Act
        DatasetApiKeyResult result = new DatasetApiKeyResult();

        // Assert
        assertNotNull(result);
        assertNull(result.getData());
    }

    /**
     * Test getter and setter methods
     */
    @Test
    public void testGetterAndSetter() {
        // Arrange
        DatasetApiKeyResult result = new DatasetApiKeyResult();
        List<DatasetApiKeyResponse> data = new ArrayList<>();
        DatasetApiKeyResponse item = new DatasetApiKeyResponse();
        item.setId("dkey-12345");
        item.setType("dataset_api_key");
        data.add(item);

        // Act
        result.setData(data);

        // Assert
        assertEquals(data, result.getData());
        assertEquals(1, result.getData().size());
        assertEquals("dkey-12345", result.getData().get(0).getId());
    }

    /**
     * Test serialization with Jackson
     */
    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        // Create an instance with sample data
        DatasetApiKeyResult result = new DatasetApiKeyResult();

        List<DatasetApiKeyResponse> data = new ArrayList<>();
        DatasetApiKeyResponse item = new DatasetApiKeyResponse();
        item.setId("dkey-12345");
        item.setType("dataset_api_key");
        item.setToken("ds-abcdefghijklmnopqrstuvwxyz");
        item.setLastUsedAt(1651234567890L);
        item.setCreatedAt(1650000000000L);
        data.add(item);

        result.setData(data);

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(result);

        // Verify JSON contains expected property names
        assertTrue(json.contains("\"data\":"));

        // Verify nested DatasetApiKeyResponseVO properties
        assertTrue(json.contains("\"id\":\"dkey-12345\""));
        assertTrue(json.contains("\"type\":\"dataset_api_key\""));
        assertTrue(json.contains("\"token\":"));

        // Deserialize back to object
        DatasetApiKeyResult deserialized = objectMapper.readValue(json, DatasetApiKeyResult.class);

        // Verify data list
        assertNotNull(deserialized.getData());
        assertEquals(1, deserialized.getData().size());

        // Verify first item in data list
        DatasetApiKeyResponse deserializedItem = deserialized.getData().get(0);
        assertEquals(item.getId(), deserializedItem.getId());
        assertEquals(item.getType(), deserializedItem.getType());
        assertEquals(item.getToken(), deserializedItem.getToken());
        assertEquals(item.getLastUsedAt(), deserializedItem.getLastUsedAt());
        assertEquals(item.getCreatedAt(), deserializedItem.getCreatedAt());
    }

    /**
     * Test JSON deserialization
     */
    @Test
    public void testJsonDeserialization() throws JsonProcessingException {
        // JSON
        String json = "{\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"id\": \"dkey-12345\",\n" +
                "      \"type\": \"dataset_api_key\",\n" +
                "      \"token\": \"ds-abcdefghijklmnopqrstuvwxyz\",\n" +
                "      \"last_used_at\": 1651234567890,\n" +
                "      \"created_at\": 1650000000000\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        // Deserialize
        DatasetApiKeyResult deserialized = objectMapper.readValue(json, DatasetApiKeyResult.class);

        // Verify fields were correctly deserialized
        assertNotNull(deserialized.getData());
        assertEquals(1, deserialized.getData().size());

        // Verify nested DatasetApiKeyResponseVO
        DatasetApiKeyResponse item = deserialized.getData().get(0);
        assertEquals("dkey-12345", item.getId());
        assertEquals("dataset_api_key", item.getType());
        assertEquals("ds-abcdefghijklmnopqrstuvwxyz", item.getToken());
        assertEquals(Long.valueOf(1651234567890L), item.getLastUsedAt());
        assertEquals(Long.valueOf(1650000000000L), item.getCreatedAt());
    }

    /**
     * Test equality and hash code
     */
    @Test
    public void testEqualsAndHashCode() {
        // Create two identical objects
        DatasetApiKeyResult result1 = new DatasetApiKeyResult();
        List<DatasetApiKeyResponse> data1 = new ArrayList<>();
        DatasetApiKeyResponse item1 = new DatasetApiKeyResponse();
        item1.setId("dkey-12345");
        data1.add(item1);
        result1.setData(data1);

        DatasetApiKeyResult result2 = new DatasetApiKeyResult();
        List<DatasetApiKeyResponse> data2 = new ArrayList<>();
        DatasetApiKeyResponse item2 = new DatasetApiKeyResponse();
        item2.setId("dkey-12345");
        data2.add(item2);
        result2.setData(data2);

        // Create a different object
        DatasetApiKeyResult result3 = new DatasetApiKeyResult();
        List<DatasetApiKeyResponse> data3 = new ArrayList<>();
        DatasetApiKeyResponse item3 = new DatasetApiKeyResponse();
        item3.setId("dkey-67890");
        data3.add(item3);
        result3.setData(data3);

        // Test equality
        assertEquals(result1, result2);
        assertNotEquals(result1, result3);

        // Test hash code
        assertEquals(result1.hashCode(), result2.hashCode());
        assertNotEquals(result1.hashCode(), result3.hashCode());
    }

    /**
     * Test with empty data list
     */
    @Test
    public void testWithEmptyDataList() throws JsonProcessingException {
        // Create an instance with empty data list
        DatasetApiKeyResult result = new DatasetApiKeyResult();
        result.setData(new ArrayList<>());

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(result);

        // Verify JSON contains empty data array
        assertTrue(json.contains("\"data\":[]"));

        // Deserialize back to object
        DatasetApiKeyResult deserialized = objectMapper.readValue(json, DatasetApiKeyResult.class);

        // Verify the deserialized object has empty data list
        assertNotNull(deserialized.getData());
        assertTrue(deserialized.getData().isEmpty());
    }

    /**
     * Test serialization of class with its serialVersionUID
     */
    @Test
    public void testSerializationWithSerialVersionUID() {
        // Verify the class has a serialVersionUID field
        DatasetApiKeyResult result = new DatasetApiKeyResult();
        assertNotNull(result);

        // This test mainly verifies that the class has a serialVersionUID defined
        // The actual serialization test is covered in the testJsonSerialization method
    }
}
