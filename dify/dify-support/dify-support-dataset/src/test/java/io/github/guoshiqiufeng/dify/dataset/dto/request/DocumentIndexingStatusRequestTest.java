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
 * Test class for DocumentIndexingStatusRequest
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/26
 */
public class DocumentIndexingStatusRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test basic getter and setter methods
     */
    @Test
    public void testGetterAndSetter() {
        // Create a DocumentIndexingStatusRequest instance
        DocumentIndexingStatusRequest request = new DocumentIndexingStatusRequest();

        // Set values for parent class fields
        String apiKey = "sk-12345678";
        request.setApiKey(apiKey);

        // Set values for fields
        String datasetId = "ds-12345";
        String batch = "batch-20230101";
        request.setDatasetId(datasetId);
        request.setBatch(batch);

        // Assert all values are set correctly
        assertEquals(apiKey, request.getApiKey());
        assertEquals(datasetId, request.getDatasetId());
        assertEquals(batch, request.getBatch());
    }

    /**
     * Test serialization with Jackson
     */
    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        // Create a DocumentIndexingStatusRequest instance with sample data
        DocumentIndexingStatusRequest request = new DocumentIndexingStatusRequest();
        request.setApiKey("sk-12345678");
        request.setDatasetId("ds-12345");
        request.setBatch("batch-20230101");

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(request);

        // Verify JSON contains expected property names
        assertTrue(json.contains("\"apiKey\":"));
        assertTrue(json.contains("\"datasetId\":"));
        assertTrue(json.contains("\"batch\":"));

        // Deserialize back to object
        DocumentIndexingStatusRequest deserialized = objectMapper.readValue(json, DocumentIndexingStatusRequest.class);

        // Verify the deserialized object matches the original
        assertEquals(request.getApiKey(), deserialized.getApiKey());
        assertEquals(request.getDatasetId(), deserialized.getDatasetId());
        assertEquals(request.getBatch(), deserialized.getBatch());
    }

    /**
     * Test JSON deserialization
     */
    @Test
    public void testJsonDeserialization() throws JsonProcessingException {
        // JSON string with all fields
        String json = "{\n" +
                "  \"apiKey\": \"sk-12345678\",\n" +
                "  \"datasetId\": \"ds-12345\",\n" +
                "  \"batch\": \"batch-20230101\"\n" +
                "}";

        // Deserialize from JSON
        DocumentIndexingStatusRequest deserialized = objectMapper.readValue(json, DocumentIndexingStatusRequest.class);

        // Verify deserialized values
        assertEquals("sk-12345678", deserialized.getApiKey());
        assertEquals("ds-12345", deserialized.getDatasetId());
        assertEquals("batch-20230101", deserialized.getBatch());
    }

    /**
     * Test equality and hash code
     */
    @Test
    public void testEqualsAndHashCode() {
        // Create two identical requests
        DocumentIndexingStatusRequest request1 = new DocumentIndexingStatusRequest();
        request1.setApiKey("sk-12345678");
        request1.setDatasetId("ds-12345");
        request1.setBatch("batch-20230101");

        DocumentIndexingStatusRequest request2 = new DocumentIndexingStatusRequest();
        request2.setApiKey("sk-12345678");
        request2.setDatasetId("ds-12345");
        request2.setBatch("batch-20230101");

        // Create a different request
        DocumentIndexingStatusRequest request3 = new DocumentIndexingStatusRequest();
        request3.setApiKey("sk-12345678");
        request3.setDatasetId("ds-54321");  // Different dataset ID
        request3.setBatch("batch-20230101");

        // Test equality
        assertEquals(request1, request2);
        assertNotEquals(request1, request3);

        // Test hash code
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }
} 