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
 * Test class for MetaDataUpdateRequest
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/5/2
 */
public class MetaDataUpdateRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test default constructor
     */
    @Test
    public void testDefaultConstructor() {
        // Act
        MetaDataUpdateRequest request = new MetaDataUpdateRequest();

        // Assert
        assertNotNull(request);
        assertNull(request.getApiKey());
        assertNull(request.getDatasetId());
        assertNull(request.getMetaDataId());
        assertNull(request.getName());
    }

    /**
     * Test getter and setter methods
     */
    @Test
    public void testGetterAndSetter() {
        // Arrange
        MetaDataUpdateRequest request = new MetaDataUpdateRequest();
        String apiKey = "test-api-key";
        String datasetId = "test-dataset-id";
        String metaDataId = "test-metadata-id";
        String name = "test-name";

        // Act
        request.setApiKey(apiKey);
        request.setDatasetId(datasetId);
        request.setMetaDataId(metaDataId);
        request.setName(name);

        // Assert
        assertEquals(apiKey, request.getApiKey());
        assertEquals(datasetId, request.getDatasetId());
        assertEquals(metaDataId, request.getMetaDataId());
        assertEquals(name, request.getName());
    }

    /**
     * Test serialization with Jackson
     */
    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        // Create a MetaDataUpdateRequest instance with sample data
        MetaDataUpdateRequest request = new MetaDataUpdateRequest();
        request.setApiKey("sk-12345678");
        request.setDatasetId("ds-12345");
        request.setMetaDataId("md-12345");
        request.setName("Updated Metadata");

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(request);

        // Verify JSON contains expected property names
        assertTrue(json.contains("\"apiKey\":"));
        assertTrue(json.contains("\"datasetId\":"));
        assertTrue(json.contains("\"metaDataId\":"));
        assertTrue(json.contains("\"name\":"));

        // Deserialize back to object
        MetaDataUpdateRequest deserialized = objectMapper.readValue(json, MetaDataUpdateRequest.class);

        // Verify the deserialized object matches the original
        assertEquals(request.getApiKey(), deserialized.getApiKey());
        assertEquals(request.getDatasetId(), deserialized.getDatasetId());
        assertEquals(request.getMetaDataId(), deserialized.getMetaDataId());
        assertEquals(request.getName(), deserialized.getName());
    }

    /**
     * Test JSON deserialization
     */
    @Test
    public void testJsonDeserialization() throws JsonProcessingException {
        // Standard JSON format
        String jsonStandard = "{\n" +
                "  \"apiKey\": \"sk-12345678\",\n" +
                "  \"datasetId\": \"ds-12345\",\n" +
                "  \"metaDataId\": \"md-12345\",\n" +
                "  \"name\": \"Updated Metadata\"\n" +
                "}";

        MetaDataUpdateRequest deserializedStandard = objectMapper.readValue(jsonStandard, MetaDataUpdateRequest.class);
        assertEquals("sk-12345678", deserializedStandard.getApiKey());
        assertEquals("ds-12345", deserializedStandard.getDatasetId());
        assertEquals("md-12345", deserializedStandard.getMetaDataId());
        assertEquals("Updated Metadata", deserializedStandard.getName());
    }

    /**
     * Test equality and hash code
     */
    @Test
    public void testEqualsAndHashCode() {
        // Create two identical requests
        MetaDataUpdateRequest request1 = new MetaDataUpdateRequest();
        request1.setApiKey("sk-12345678");
        request1.setDatasetId("ds-12345");
        request1.setMetaDataId("md-12345");
        request1.setName("Updated Metadata");

        MetaDataUpdateRequest request2 = new MetaDataUpdateRequest();
        request2.setApiKey("sk-12345678");
        request2.setDatasetId("ds-12345");
        request2.setMetaDataId("md-12345");
        request2.setName("Updated Metadata");

        // Create a different request
        MetaDataUpdateRequest request3 = new MetaDataUpdateRequest();
        request3.setApiKey("sk-87654321");
        request3.setDatasetId("ds-54321");
        request3.setMetaDataId("md-54321");
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
        MetaDataUpdateRequest request = new MetaDataUpdateRequest();
        request.setApiKey("sk-12345678");
        request.setDatasetId("ds-12345");
        request.setMetaDataId("md-12345");
        request.setName("Updated Metadata");

        // Act
        String toString = request.toString();

        // Assert
        assertTrue(toString.contains("apiKey=sk-12345678"));
        assertTrue(toString.contains("datasetId=ds-12345"));
        assertTrue(toString.contains("metaDataId=md-12345"));
        assertTrue(toString.contains("name=Updated Metadata"));
    }
}
