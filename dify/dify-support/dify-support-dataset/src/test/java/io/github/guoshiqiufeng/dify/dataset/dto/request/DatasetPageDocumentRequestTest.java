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
package io.github.guoshiqiufeng.dify.dataset.dto.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for DatasetPageDocumentRequest
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/26
 */
public class DatasetPageDocumentRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test basic getter and setter methods
     */
    @Test
    public void testGetterAndSetter() {
        // Create a DatasetPageDocumentRequest instance
        DatasetPageDocumentRequest request = new DatasetPageDocumentRequest();

        // Set values for parent class fields
        String apiKey = "sk-12345678";
        request.setApiKey(apiKey);

        // Set values for fields
        String datasetId = "ds-12345";
        String keyword = "machine learning";
        Integer page = 5;
        Integer limit = 50;

        request.setDatasetId(datasetId);
        request.setKeyword(keyword);
        request.setPage(page);
        request.setLimit(limit);

        // Assert all values are set correctly
        assertEquals(apiKey, request.getApiKey());
        assertEquals(datasetId, request.getDatasetId());
        assertEquals(keyword, request.getKeyword());
        assertEquals(page, request.getPage());
        assertEquals(limit, request.getLimit());
    }

    /**
     * Test default values
     */
    @Test
    public void testDefaultValues() {
        // Create a DatasetPageDocumentRequest instance without setting any values
        DatasetPageDocumentRequest request = new DatasetPageDocumentRequest();

        // Verify default values
        assertEquals(1, request.getPage());  // page defaults to 1
        assertEquals(20, request.getLimit());  // limit defaults to 20

        // datasetId and keyword should be null by default
        assertNull(request.getDatasetId());
        assertNull(request.getKeyword());
    }

    /**
     * Test serialization with Jackson
     */
    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        // Create a DatasetPageDocumentRequest instance with sample data
        DatasetPageDocumentRequest request = new DatasetPageDocumentRequest();
        request.setApiKey("sk-12345678");
        request.setDatasetId("ds-12345");
        request.setKeyword("machine learning");
        request.setPage(3);
        request.setLimit(30);

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(request);

        // Verify JSON contains expected property names and values
        assertTrue(json.contains("\"apiKey\":\"sk-12345678\""));
        assertTrue(json.contains("\"datasetId\":\"ds-12345\""));
        assertTrue(json.contains("\"keyword\":\"machine learning\""));
        assertTrue(json.contains("\"page\":3"));
        assertTrue(json.contains("\"limit\":30"));

        // Deserialize back to object
        DatasetPageDocumentRequest deserialized = objectMapper.readValue(json, DatasetPageDocumentRequest.class);

        // Verify the deserialized object matches the original
        assertEquals(request.getApiKey(), deserialized.getApiKey());
        assertEquals(request.getDatasetId(), deserialized.getDatasetId());
        assertEquals(request.getKeyword(), deserialized.getKeyword());
        assertEquals(request.getPage(), deserialized.getPage());
        assertEquals(request.getLimit(), deserialized.getLimit());
    }

    /**
     * Test JSON deserialization
     */
    @Test
    public void testJsonDeserialization() throws JsonProcessingException {
        // Test with all fields provided
        String jsonAllFields = "{\n" +
                "  \"apiKey\": \"sk-12345678\",\n" +
                "  \"datasetId\": \"ds-12345\",\n" +
                "  \"keyword\": \"machine learning\",\n" +
                "  \"page\": 5,\n" +
                "  \"limit\": 50\n" +
                "}";

        DatasetPageDocumentRequest deserializedAllFields = objectMapper.readValue(jsonAllFields, DatasetPageDocumentRequest.class);
        assertEquals("sk-12345678", deserializedAllFields.getApiKey());
        assertEquals("ds-12345", deserializedAllFields.getDatasetId());
        assertEquals("machine learning", deserializedAllFields.getKeyword());
        assertEquals(5, deserializedAllFields.getPage());
        assertEquals(50, deserializedAllFields.getLimit());

        // Test with required fields only
        String jsonRequired = "{\n" +
                "  \"apiKey\": \"sk-12345678\",\n" +
                "  \"datasetId\": \"ds-12345\"\n" +
                "}";

        DatasetPageDocumentRequest deserializedRequired = objectMapper.readValue(jsonRequired, DatasetPageDocumentRequest.class);
        assertEquals("sk-12345678", deserializedRequired.getApiKey());
        assertEquals("ds-12345", deserializedRequired.getDatasetId());
        assertNull(deserializedRequired.getKeyword());  // Should be null
        assertEquals(1, deserializedRequired.getPage());  // Should use default value
        assertEquals(20, deserializedRequired.getLimit());  // Should use default value

        // Test with empty keyword
        String jsonEmptyKeyword = "{\n" +
                "  \"apiKey\": \"sk-12345678\",\n" +
                "  \"datasetId\": \"ds-12345\",\n" +
                "  \"keyword\": \"\",\n" +
                "  \"page\": 1,\n" +
                "  \"limit\": 20\n" +
                "}";

        DatasetPageDocumentRequest deserializedEmptyKeyword = objectMapper.readValue(jsonEmptyKeyword, DatasetPageDocumentRequest.class);
        assertEquals("", deserializedEmptyKeyword.getKeyword());  // Should be empty string, not null
    }

    /**
     * Test equality and hash code
     */
    @Test
    public void testEqualsAndHashCode() {
        // Create two identical requests
        DatasetPageDocumentRequest request1 = new DatasetPageDocumentRequest();
        request1.setApiKey("sk-12345678");
        request1.setDatasetId("ds-12345");
        request1.setKeyword("machine learning");
        request1.setPage(3);
        request1.setLimit(30);

        DatasetPageDocumentRequest request2 = new DatasetPageDocumentRequest();
        request2.setApiKey("sk-12345678");
        request2.setDatasetId("ds-12345");
        request2.setKeyword("machine learning");
        request2.setPage(3);
        request2.setLimit(30);

        // Create a different request
        DatasetPageDocumentRequest request3 = new DatasetPageDocumentRequest();
        request3.setApiKey("sk-12345678");
        request3.setDatasetId("ds-12345");
        request3.setKeyword("artificial intelligence");  // Different keyword
        request3.setPage(3);
        request3.setLimit(30);

        // Test equality
        assertEquals(request1, request2);
        assertNotEquals(request1, request3);

        // Test hash code
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    /**
     * Test null handling
     */
    @Test
    public void testNullHandling() {
        // Create request with null values
        DatasetPageDocumentRequest request = new DatasetPageDocumentRequest();
        request.setApiKey("sk-12345678");
        request.setDatasetId("ds-12345");
        request.setKeyword(null);  // Explicitly set to null

        // Test with explicit null
        assertNull(request.getKeyword());

        // Serialization should still work
        try {
            String json = objectMapper.writeValueAsString(request);
            assertTrue(json.contains("\"apiKey\":"));
            assertTrue(json.contains("\"datasetId\":"));

            // Jackson typically omits null values in serialization by default
            // But this behavior can be customized with SerializationFeature.WRITE_NULL_MAP_VALUES
        } catch (JsonProcessingException e) {
            fail("Serialization should not throw an exception");
        }
    }
} 