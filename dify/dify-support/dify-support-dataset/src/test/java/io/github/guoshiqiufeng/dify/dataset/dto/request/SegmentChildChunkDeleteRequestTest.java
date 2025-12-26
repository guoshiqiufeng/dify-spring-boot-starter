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
 * Test class for SegmentChildChunkDeleteRequest
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/5/2
 */
public class SegmentChildChunkDeleteRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test default constructor
     */
    @Test
    public void testDefaultConstructor() {
        // Act
        SegmentChildChunkDeleteRequest request = new SegmentChildChunkDeleteRequest();

        // Assert
        assertNotNull(request);
        assertNull(request.getApiKey());
        assertNull(request.getDatasetId());
        assertNull(request.getDocumentId());
        assertNull(request.getSegmentId());
        assertNull(request.getChildChunkId());
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
        String childChunkId = "test-child-chunk-id";

        // Act
        SegmentChildChunkDeleteRequest request = new SegmentChildChunkDeleteRequest(datasetId, documentId, segmentId, childChunkId);
        request.setApiKey(apiKey);

        // Assert
        assertEquals(apiKey, request.getApiKey());
        assertEquals(datasetId, request.getDatasetId());
        assertEquals(documentId, request.getDocumentId());
        assertEquals(segmentId, request.getSegmentId());
        assertEquals(childChunkId, request.getChildChunkId());
    }

    /**
     * Test getter and setter methods
     */
    @Test
    public void testGetterAndSetter() {
        // Arrange
        SegmentChildChunkDeleteRequest request = new SegmentChildChunkDeleteRequest();
        String apiKey = "test-api-key";
        String datasetId = "test-dataset-id";
        String documentId = "test-document-id";
        String segmentId = "test-segment-id";
        String childChunkId = "test-child-chunk-id";

        // Act
        request.setApiKey(apiKey);
        request.setDatasetId(datasetId);
        request.setDocumentId(documentId);
        request.setSegmentId(segmentId);
        request.setChildChunkId(childChunkId);

        // Assert
        assertEquals(apiKey, request.getApiKey());
        assertEquals(datasetId, request.getDatasetId());
        assertEquals(documentId, request.getDocumentId());
        assertEquals(segmentId, request.getSegmentId());
        assertEquals(childChunkId, request.getChildChunkId());
    }

    /**
     * Test serialization with Jackson
     */
    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        // Create a request instance with sample data
        SegmentChildChunkDeleteRequest request = new SegmentChildChunkDeleteRequest();
        request.setApiKey("sk-12345678");
        request.setDatasetId("ds-12345");
        request.setDocumentId("doc-12345");
        request.setSegmentId("seg-12345");
        request.setChildChunkId("chunk-12345");

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(request);

        // Verify JSON contains expected property names
        assertTrue(json.contains("\"apiKey\":"));
        assertTrue(json.contains("\"datasetId\":"));
        assertTrue(json.contains("\"documentId\":"));
        assertTrue(json.contains("\"segmentId\":"));
        assertTrue(json.contains("\"childChunkId\":"));

        // Deserialize back to object
        SegmentChildChunkDeleteRequest deserialized = objectMapper.readValue(json, SegmentChildChunkDeleteRequest.class);

        // Verify the deserialized object matches the original
        assertEquals(request.getApiKey(), deserialized.getApiKey());
        assertEquals(request.getDatasetId(), deserialized.getDatasetId());
        assertEquals(request.getDocumentId(), deserialized.getDocumentId());
        assertEquals(request.getSegmentId(), deserialized.getSegmentId());
        assertEquals(request.getChildChunkId(), deserialized.getChildChunkId());
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
                "  \"childChunkId\": \"chunk-12345\"\n" +
                "}";

        SegmentChildChunkDeleteRequest deserializedStandard = objectMapper.readValue(jsonStandard, SegmentChildChunkDeleteRequest.class);
        assertEquals("sk-12345678", deserializedStandard.getApiKey());
        assertEquals("ds-12345", deserializedStandard.getDatasetId());
        assertEquals("doc-12345", deserializedStandard.getDocumentId());
        assertEquals("seg-12345", deserializedStandard.getSegmentId());
        assertEquals("chunk-12345", deserializedStandard.getChildChunkId());

    }

    /**
     * Test equality and hash code
     */
    @Test
    public void testEqualsAndHashCode() {
        // Create two identical requests
        SegmentChildChunkDeleteRequest request1 = new SegmentChildChunkDeleteRequest();
        request1.setApiKey("sk-12345678");
        request1.setDatasetId("ds-12345");
        request1.setDocumentId("doc-12345");
        request1.setSegmentId("seg-12345");
        request1.setChildChunkId("chunk-12345");

        SegmentChildChunkDeleteRequest request2 = new SegmentChildChunkDeleteRequest();
        request2.setApiKey("sk-12345678");
        request2.setDatasetId("ds-12345");
        request2.setDocumentId("doc-12345");
        request2.setSegmentId("seg-12345");
        request2.setChildChunkId("chunk-12345");

        // Create a different request
        SegmentChildChunkDeleteRequest request3 = new SegmentChildChunkDeleteRequest();
        request3.setApiKey("sk-87654321");
        request3.setDatasetId("ds-54321");
        request3.setDocumentId("doc-54321");
        request3.setSegmentId("seg-54321");
        request3.setChildChunkId("chunk-54321");

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
        SegmentChildChunkDeleteRequest request = new SegmentChildChunkDeleteRequest();
        request.setApiKey("sk-12345678");
        request.setDatasetId("ds-12345");
        request.setDocumentId("doc-12345");
        request.setSegmentId("seg-12345");
        request.setChildChunkId("chunk-12345");

        // Act
        String toString = request.toString();

        // Assert
        assertTrue(toString.contains("apiKey=sk-12345678"));
        assertTrue(toString.contains("datasetId=ds-12345"));
        assertTrue(toString.contains("documentId=doc-12345"));
        assertTrue(toString.contains("segmentId=seg-12345"));
        assertTrue(toString.contains("childChunkId=chunk-12345"));
    }
}
