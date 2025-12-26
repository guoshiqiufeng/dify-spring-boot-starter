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
 * Test class for SegmentChildChunkPageRequest
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/5/2
 */
public class SegmentChildChunkPageRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test default constructor
     */
    @Test
    public void testDefaultConstructor() {
        // Act
        SegmentChildChunkPageRequest request = new SegmentChildChunkPageRequest();

        // Assert
        assertNotNull(request);
        assertNull(request.getApiKey());
        assertNull(request.getDatasetId());
        assertNull(request.getDocumentId());
        assertNull(request.getSegmentId());
        assertNull(request.getKeyword());
        assertEquals(Integer.valueOf(1), request.getPage());
        assertEquals(Integer.valueOf(20), request.getLimit());
    }

    /**
     * Test all-args constructor
     */
    @Test
    public void testAllArgsConstructor() {
        // Arrange
        String apiKey = "test-api-key";
        String datasetId = "test-dataset-id";
        String documentId = "test-document-id";
        String segmentId = "test-segment-id";
        String keyword = "test-keyword";
        Integer page = 2;
        Integer limit = 50;

        // Act
        SegmentChildChunkPageRequest request = new SegmentChildChunkPageRequest(datasetId, documentId, segmentId, keyword, page, limit);
        request.setApiKey(apiKey);

        // Assert
        assertEquals(apiKey, request.getApiKey());
        assertEquals(datasetId, request.getDatasetId());
        assertEquals(documentId, request.getDocumentId());
        assertEquals(segmentId, request.getSegmentId());
        assertEquals(keyword, request.getKeyword());
        assertEquals(page, request.getPage());
        assertEquals(limit, request.getLimit());
    }

    /**
     * Test getter and setter methods
     */
    @Test
    public void testGetterAndSetter() {
        // Arrange
        SegmentChildChunkPageRequest request = new SegmentChildChunkPageRequest();
        String apiKey = "test-api-key";
        String datasetId = "test-dataset-id";
        String documentId = "test-document-id";
        String segmentId = "test-segment-id";
        String keyword = "test-keyword";
        Integer page = 2;
        Integer limit = 50;

        // Act
        request.setApiKey(apiKey);
        request.setDatasetId(datasetId);
        request.setDocumentId(documentId);
        request.setSegmentId(segmentId);
        request.setKeyword(keyword);
        request.setPage(page);
        request.setLimit(limit);

        // Assert
        assertEquals(apiKey, request.getApiKey());
        assertEquals(datasetId, request.getDatasetId());
        assertEquals(documentId, request.getDocumentId());
        assertEquals(segmentId, request.getSegmentId());
        assertEquals(keyword, request.getKeyword());
        assertEquals(page, request.getPage());
        assertEquals(limit, request.getLimit());
    }

    /**
     * Test default values
     */
    @Test
    public void testDefaultValues() {
        // Act
        SegmentChildChunkPageRequest request = new SegmentChildChunkPageRequest();

        // Assert - default values should be set
        assertEquals(Integer.valueOf(1), request.getPage());
        assertEquals(Integer.valueOf(20), request.getLimit());
    }

    /**
     * Test serialization with Jackson
     */
    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        // Create a request instance with sample data
        SegmentChildChunkPageRequest request = new SegmentChildChunkPageRequest();
        request.setApiKey("sk-12345678");
        request.setDatasetId("ds-12345");
        request.setDocumentId("doc-12345");
        request.setSegmentId("seg-12345");
        request.setKeyword("search term");
        request.setPage(2);
        request.setLimit(30);

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(request);

        // Verify JSON contains expected property names
        assertTrue(json.contains("\"apiKey\":"));
        assertTrue(json.contains("\"datasetId\":"));
        assertTrue(json.contains("\"documentId\":"));
        assertTrue(json.contains("\"segmentId\":"));
        assertTrue(json.contains("\"keyword\":"));
        assertTrue(json.contains("\"page\":"));
        assertTrue(json.contains("\"limit\":"));

        // Deserialize back to object
        SegmentChildChunkPageRequest deserialized = objectMapper.readValue(json, SegmentChildChunkPageRequest.class);

        // Verify the deserialized object matches the original
        assertEquals(request.getApiKey(), deserialized.getApiKey());
        assertEquals(request.getDatasetId(), deserialized.getDatasetId());
        assertEquals(request.getDocumentId(), deserialized.getDocumentId());
        assertEquals(request.getSegmentId(), deserialized.getSegmentId());
        assertEquals(request.getKeyword(), deserialized.getKeyword());
        assertEquals(request.getPage(), deserialized.getPage());
        assertEquals(request.getLimit(), deserialized.getLimit());
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
                "  \"segmentId\": \"seg-12345\",\n" +
                "  \"keyword\": \"search term\",\n" +
                "  \"page\": 2,\n" +
                "  \"limit\": 30\n" +
                "}";

        SegmentChildChunkPageRequest deserializedStandard = objectMapper.readValue(jsonStandard, SegmentChildChunkPageRequest.class);
        assertEquals("sk-12345678", deserializedStandard.getApiKey());
        assertEquals("ds-12345", deserializedStandard.getDatasetId());
        assertEquals("doc-12345", deserializedStandard.getDocumentId());
        assertEquals("seg-12345", deserializedStandard.getSegmentId());
        assertEquals("search term", deserializedStandard.getKeyword());
        assertEquals(Integer.valueOf(2), deserializedStandard.getPage());
        assertEquals(Integer.valueOf(30), deserializedStandard.getLimit());

    }

    /**
     * Test equality and hash code
     */
    @Test
    public void testEqualsAndHashCode() {
        // Create two identical requests
        SegmentChildChunkPageRequest request1 = new SegmentChildChunkPageRequest();
        request1.setApiKey("sk-12345678");
        request1.setDatasetId("ds-12345");
        request1.setDocumentId("doc-12345");
        request1.setSegmentId("seg-12345");
        request1.setKeyword("search term");
        request1.setPage(2);
        request1.setLimit(30);

        SegmentChildChunkPageRequest request2 = new SegmentChildChunkPageRequest();
        request2.setApiKey("sk-12345678");
        request2.setDatasetId("ds-12345");
        request2.setDocumentId("doc-12345");
        request2.setSegmentId("seg-12345");
        request2.setKeyword("search term");
        request2.setPage(2);
        request2.setLimit(30);

        // Create a different request
        SegmentChildChunkPageRequest request3 = new SegmentChildChunkPageRequest();
        request3.setApiKey("sk-87654321");
        request3.setDatasetId("ds-54321");
        request3.setDocumentId("doc-54321");
        request3.setSegmentId("seg-54321");
        request3.setKeyword("different term");
        request3.setPage(3);
        request3.setLimit(10);

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
        SegmentChildChunkPageRequest request = new SegmentChildChunkPageRequest();
        request.setApiKey("sk-12345678");
        request.setDatasetId("ds-12345");
        request.setDocumentId("doc-12345");
        request.setSegmentId("seg-12345");
        request.setKeyword("search term");
        request.setPage(2);
        request.setLimit(30);

        // Act
        String toString = request.toString();

        // Assert
        assertTrue(toString.contains("apiKey=sk-12345678"));
        assertTrue(toString.contains("datasetId=ds-12345"));
        assertTrue(toString.contains("documentId=doc-12345"));
        assertTrue(toString.contains("segmentId=seg-12345"));
        assertTrue(toString.contains("keyword=search term"));
        assertTrue(toString.contains("page=2"));
        assertTrue(toString.contains("limit=30"));
    }
}
