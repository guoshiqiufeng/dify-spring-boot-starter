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
 * Test class for SegmentPageRequest
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/5/2
 */
public class SegmentPageRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test default constructor
     */
    @Test
    public void testDefaultConstructor() {
        // Act
        SegmentPageRequest request = new SegmentPageRequest();

        // Assert
        assertNotNull(request);
        assertNull(request.getApiKey());
        assertNull(request.getDatasetId());
        assertNull(request.getDocumentId());
        assertNull(request.getKeyword());
        assertNull(request.getStatus());
    }

    /**
     * Test getter and setter methods
     */
    @Test
    public void testGetterAndSetter() {
        // Arrange
        SegmentPageRequest request = new SegmentPageRequest();
        String apiKey = "test-api-key";
        String datasetId = "test-dataset-id";
        String documentId = "test-document-id";
        String keyword = "test-keyword";
        String status = "active";

        // Act
        request.setApiKey(apiKey);
        request.setDatasetId(datasetId);
        request.setDocumentId(documentId);
        request.setKeyword(keyword);
        request.setStatus(status);

        // Assert
        assertEquals(apiKey, request.getApiKey());
        assertEquals(datasetId, request.getDatasetId());
        assertEquals(documentId, request.getDocumentId());
        assertEquals(keyword, request.getKeyword());
        assertEquals(status, request.getStatus());
    }

    /**
     * Test serialization with Jackson
     */
    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        // Create a request instance with sample data
        SegmentPageRequest request = new SegmentPageRequest();
        request.setApiKey("sk-12345678");
        request.setDatasetId("ds-12345");
        request.setDocumentId("doc-12345");
        request.setKeyword("search term");
        request.setStatus("active");

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(request);

        // Verify JSON contains expected property names
        assertTrue(json.contains("\"apiKey\":"));
        assertTrue(json.contains("\"datasetId\":"));
        assertTrue(json.contains("\"documentId\":"));
        assertTrue(json.contains("\"keyword\":"));
        assertTrue(json.contains("\"status\":"));

        // Deserialize back to object
        SegmentPageRequest deserialized = objectMapper.readValue(json, SegmentPageRequest.class);

        // Verify the deserialized object matches the original
        assertEquals(request.getApiKey(), deserialized.getApiKey());
        assertEquals(request.getDatasetId(), deserialized.getDatasetId());
        assertEquals(request.getDocumentId(), deserialized.getDocumentId());
        assertEquals(request.getKeyword(), deserialized.getKeyword());
        assertEquals(request.getStatus(), deserialized.getStatus());
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
                "  \"documentId\": \"doc-12345\",\n" +
                "  \"keyword\": \"search term\",\n" +
                "  \"status\": \"active\"\n" +
                "}";

        SegmentPageRequest deserializedStandard = objectMapper.readValue(jsonStandard, SegmentPageRequest.class);
        assertEquals("sk-12345678", deserializedStandard.getApiKey());
        assertEquals("ds-12345", deserializedStandard.getDatasetId());
        assertEquals("doc-12345", deserializedStandard.getDocumentId());
        assertEquals("search term", deserializedStandard.getKeyword());
        assertEquals("active", deserializedStandard.getStatus());
    }

    /**
     * Test equality and hash code
     */
    @Test
    public void testEqualsAndHashCode() {
        // Create two identical requests
        SegmentPageRequest request1 = new SegmentPageRequest();
        request1.setApiKey("sk-12345678");
        request1.setDatasetId("ds-12345");
        request1.setDocumentId("doc-12345");
        request1.setKeyword("search term");
        request1.setStatus("active");

        SegmentPageRequest request2 = new SegmentPageRequest();
        request2.setApiKey("sk-12345678");
        request2.setDatasetId("ds-12345");
        request2.setDocumentId("doc-12345");
        request2.setKeyword("search term");
        request2.setStatus("active");

        // Create a different request
        SegmentPageRequest request3 = new SegmentPageRequest();
        request3.setApiKey("sk-87654321");
        request3.setDatasetId("ds-54321");
        request3.setDocumentId("doc-54321");
        request3.setKeyword("different term");
        request3.setStatus("inactive");

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
        SegmentPageRequest request = new SegmentPageRequest();
        request.setApiKey("sk-12345678");
        request.setDatasetId("ds-12345");
        request.setDocumentId("doc-12345");
        request.setKeyword("search term");
        request.setStatus("active");

        // Act
        String toString = request.toString();

        // Assert
        assertTrue(toString.contains("apiKey=sk-12345678"));
        assertTrue(toString.contains("datasetId=ds-12345"));
        assertTrue(toString.contains("documentId=doc-12345"));
        assertTrue(toString.contains("keyword=search term"));
        assertTrue(toString.contains("status=active"));
    }
}
