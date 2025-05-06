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
package io.github.guoshiqiufeng.dify.dataset.dto.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for MetaDataCreateRequest
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/5/2
 */
public class MetaDataCreateRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test default constructor
     */
    @Test
    public void testDefaultConstructor() {
        // Act
        MetaDataCreateRequest request = new MetaDataCreateRequest();

        // Assert
        assertNotNull(request);
        assertNull(request.getApiKey());
        assertNull(request.getDatasetId());
        assertNull(request.getType());
        assertNull(request.getName());
    }

    /**
     * Test getter and setter methods
     */
    @Test
    public void testGetterAndSetter() {
        // Arrange
        MetaDataCreateRequest request = new MetaDataCreateRequest();
        String apiKey = "test-api-key";
        String datasetId = "test-dataset-id";
        String type = "test-type";
        String name = "test-name";

        // Act
        request.setApiKey(apiKey);
        request.setDatasetId(datasetId);
        request.setType(type);
        request.setName(name);

        // Assert
        assertEquals(apiKey, request.getApiKey());
        assertEquals(datasetId, request.getDatasetId());
        assertEquals(type, request.getType());
        assertEquals(name, request.getName());
    }

    /**
     * Test serialization with Jackson
     */
    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        // Create a MetaDataCreateRequest instance with sample data
        MetaDataCreateRequest request = new MetaDataCreateRequest();
        request.setApiKey("sk-12345678");
        request.setDatasetId("ds-12345");
        request.setType("text");
        request.setName("Test Metadata");

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(request);

        // Verify JSON contains expected property names
        assertTrue(json.contains("\"apiKey\":"));
        assertTrue(json.contains("\"datasetId\":"));
        assertTrue(json.contains("\"type\":"));
        assertTrue(json.contains("\"name\":"));

        // Deserialize back to object
        MetaDataCreateRequest deserialized = objectMapper.readValue(json, MetaDataCreateRequest.class);

        // Verify the deserialized object matches the original
        assertEquals(request.getApiKey(), deserialized.getApiKey());
        assertEquals(request.getDatasetId(), deserialized.getDatasetId());
        assertEquals(request.getType(), deserialized.getType());
        assertEquals(request.getName(), deserialized.getName());
    }

    /**
     * Test JSON deserialization
     */
    @Test
    public void testJsonDeserialization() throws JsonProcessingException {
        // JSON with datasetId
        String jsonWithDatasetId = "{\n" +
                "  \"apiKey\": \"sk-12345678\",\n" +
                "  \"datasetId\": \"ds-12345\",\n" +
                "  \"type\": \"text\",\n" +
                "  \"name\": \"Test Metadata\"\n" +
                "}";

        MetaDataCreateRequest deserializedWithDatasetId = objectMapper.readValue(jsonWithDatasetId, MetaDataCreateRequest.class);
        assertEquals("sk-12345678", deserializedWithDatasetId.getApiKey());
        assertEquals("ds-12345", deserializedWithDatasetId.getDatasetId());
        assertEquals("text", deserializedWithDatasetId.getType());
        assertEquals("Test Metadata", deserializedWithDatasetId.getName());
    }

    /**
     * Test equality and hash code
     */
    @Test
    public void testEqualsAndHashCode() {
        // Create two identical requests
        MetaDataCreateRequest request1 = new MetaDataCreateRequest();
        request1.setApiKey("sk-12345678");
        request1.setDatasetId("ds-12345");
        request1.setType("text");
        request1.setName("Test Metadata");

        MetaDataCreateRequest request2 = new MetaDataCreateRequest();
        request2.setApiKey("sk-12345678");
        request2.setDatasetId("ds-12345");
        request2.setType("text");
        request2.setName("Test Metadata");

        // Create a different request
        MetaDataCreateRequest request3 = new MetaDataCreateRequest();
        request3.setApiKey("sk-87654321");
        request3.setDatasetId("ds-54321");
        request3.setType("vector");
        request3.setName("Different Metadata");

        // Test equality
        assertEquals(request1, request2);
        assertNotEquals(request1, request3);

        // Test hash code
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    /**
     * Test toString method
     */
    @Test
    public void testToString() {
        // Arrange
        MetaDataCreateRequest request = new MetaDataCreateRequest();
        request.setApiKey("sk-12345678");
        request.setDatasetId("ds-12345");
        request.setType("text");
        request.setName("Test Metadata");

        // Act
        String toString = request.toString();

        // Assert
        assertTrue(toString.contains("apiKey=sk-12345678"));
        assertTrue(toString.contains("datasetId=ds-12345"));
        assertTrue(toString.contains("type=text"));
        assertTrue(toString.contains("name=Test Metadata"));
    }
}
