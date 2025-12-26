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
 * Test class for DatasetPageRequest
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/26
 */
public class DatasetPageRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test basic getter and setter methods
     */
    @Test
    public void testGetterAndSetter() {
        // Create a DatasetPageRequest instance
        DatasetPageRequest request = new DatasetPageRequest();

        // Set values for parent class fields
        String apiKey = "sk-12345678";
        request.setApiKey(apiKey);

        // Set values for fields
        Integer page = 5;
        Integer limit = 50;
        request.setPage(page);
        request.setLimit(limit);

        // Assert all values are set correctly
        assertEquals(apiKey, request.getApiKey());
        assertEquals(page, request.getPage());
        assertEquals(limit, request.getLimit());
    }

    /**
     * Test default values
     */
    @Test
    public void testDefaultValues() {
        // Create a DatasetPageRequest instance without setting any values
        DatasetPageRequest request = new DatasetPageRequest();

        // Verify default values
        assertEquals(1, request.getPage());  // page defaults to 1
        assertEquals(20, request.getLimit());  // limit defaults to 20
    }

    /**
     * Test serialization with Jackson
     */
    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        // Create a DatasetPageRequest instance with sample data
        DatasetPageRequest request = new DatasetPageRequest();
        request.setApiKey("sk-12345678");
        request.setPage(3);
        request.setLimit(30);

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(request);

        // Verify JSON contains expected property names
        assertTrue(json.contains("\"apiKey\":"));
        assertTrue(json.contains("\"page\":"));
        assertTrue(json.contains("\"limit\":"));

        // Verify JSON contains expected values
        assertTrue(json.contains("\"page\":3"));
        assertTrue(json.contains("\"limit\":30"));

        // Deserialize back to object
        DatasetPageRequest deserialized = objectMapper.readValue(json, DatasetPageRequest.class);

        // Verify the deserialized object matches the original
        assertEquals(request.getApiKey(), deserialized.getApiKey());
        assertEquals(request.getPage(), deserialized.getPage());
        assertEquals(request.getLimit(), deserialized.getLimit());
    }

    /**
     * Test JSON deserialization
     */
    @Test
    public void testJsonDeserialization() throws JsonProcessingException {
        // JSON with all fields
        String jsonAllFields = "{\n" +
                "  \"apiKey\": \"sk-12345678\",\n" +
                "  \"page\": 5,\n" +
                "  \"limit\": 50\n" +
                "}";

        DatasetPageRequest deserializedAllFields = objectMapper.readValue(jsonAllFields, DatasetPageRequest.class);
        assertEquals("sk-12345678", deserializedAllFields.getApiKey());
        assertEquals(5, deserializedAllFields.getPage());
        assertEquals(50, deserializedAllFields.getLimit());

        // JSON with only apiKey - should use default values for other fields
        String jsonApiKeyOnly = "{\n" +
                "  \"apiKey\": \"sk-12345678\"\n" +
                "}";

        DatasetPageRequest deserializedApiKeyOnly = objectMapper.readValue(jsonApiKeyOnly, DatasetPageRequest.class);
        assertEquals("sk-12345678", deserializedApiKeyOnly.getApiKey());
        assertEquals(1, deserializedApiKeyOnly.getPage());  // Should use default value
        assertEquals(20, deserializedApiKeyOnly.getLimit());  // Should use default value
    }

    /**
     * Test equality and hash code
     */
    @Test
    public void testEqualsAndHashCode() {
        // Create two identical requests
        DatasetPageRequest request1 = new DatasetPageRequest();
        request1.setApiKey("sk-12345678");
        request1.setPage(3);
        request1.setLimit(30);

        DatasetPageRequest request2 = new DatasetPageRequest();
        request2.setApiKey("sk-12345678");
        request2.setPage(3);
        request2.setLimit(30);

        // Create a different request
        DatasetPageRequest request3 = new DatasetPageRequest();
        request3.setApiKey("sk-12345678");
        request3.setPage(4);  // Different page
        request3.setLimit(30);

        // Test equality
        assertEquals(request1, request2);
        assertNotEquals(request1, request3);

        // Test hash code
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    /**
     * Test boundary values for pagination
     */
    @Test
    public void testBoundaryValues() {
        DatasetPageRequest request = new DatasetPageRequest();

        // Test setting minimum values
        request.setPage(1);
        request.setLimit(1);
        assertEquals(1, request.getPage());
        assertEquals(1, request.getLimit());

        // Test setting large values
        request.setPage(1000);
        request.setLimit(100);  // API typically limits to 100
        assertEquals(1000, request.getPage());
        assertEquals(100, request.getLimit());
    }
} 