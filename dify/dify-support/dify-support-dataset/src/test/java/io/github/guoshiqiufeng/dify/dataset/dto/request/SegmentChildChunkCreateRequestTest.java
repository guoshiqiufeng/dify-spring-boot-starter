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
 * Test class for SegmentChildChunkCreateRequest
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/5/2
 */
public class SegmentChildChunkCreateRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test default constructor
     */
    @Test
    public void testDefaultConstructor() {
        // Act
        SegmentChildChunkCreateRequest request = new SegmentChildChunkCreateRequest();

        // Assert
        assertNotNull(request);
        assertNull(request.getApiKey());
        assertNull(request.getDatasetId());
        assertNull(request.getDocumentId());
        assertNull(request.getSegmentId());
        assertNull(request.getContent());
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
        String content = "Test content";

        // Act
        SegmentChildChunkCreateRequest request = new SegmentChildChunkCreateRequest(datasetId, documentId, segmentId, content);
        request.setApiKey(apiKey);

        // Assert
        assertEquals(apiKey, request.getApiKey());
        assertEquals(datasetId, request.getDatasetId());
        assertEquals(documentId, request.getDocumentId());
        assertEquals(segmentId, request.getSegmentId());
        assertEquals(content, request.getContent());
    }

    /**
     * Test getter and setter methods
     */
    @Test
    public void testGetterAndSetter() {
        // Arrange
        SegmentChildChunkCreateRequest request = new SegmentChildChunkCreateRequest();
        String apiKey = "test-api-key";
        String datasetId = "test-dataset-id";
        String documentId = "test-document-id";
        String segmentId = "test-segment-id";
        String content = "Test content";

        // Act
        request.setApiKey(apiKey);
        request.setDatasetId(datasetId);
        request.setDocumentId(documentId);
        request.setSegmentId(segmentId);
        request.setContent(content);

        // Assert
        assertEquals(apiKey, request.getApiKey());
        assertEquals(datasetId, request.getDatasetId());
        assertEquals(documentId, request.getDocumentId());
        assertEquals(segmentId, request.getSegmentId());
        assertEquals(content, request.getContent());
    }

    /**
     * Test serialization with Jackson
     */
    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        // Create a request instance with sample data
        SegmentChildChunkCreateRequest request = new SegmentChildChunkCreateRequest();
        request.setApiKey("sk-12345678");
        request.setDatasetId("ds-12345");
        request.setDocumentId("doc-12345");
        request.setSegmentId("seg-12345");
        request.setContent("This is a test content for child chunk");

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(request);

        // Verify JSON contains expected property names
        assertTrue(json.contains("\"apiKey\":"));
        assertTrue(json.contains("\"datasetId\":"));
        assertTrue(json.contains("\"documentId\":"));
        assertTrue(json.contains("\"segmentId\":"));
        assertTrue(json.contains("\"content\":"));

        // Deserialize back to object
        SegmentChildChunkCreateRequest deserialized = objectMapper.readValue(json, SegmentChildChunkCreateRequest.class);

        // Verify the deserialized object matches the original
        assertEquals(request.getApiKey(), deserialized.getApiKey());
        assertEquals(request.getDatasetId(), deserialized.getDatasetId());
        assertEquals(request.getDocumentId(), deserialized.getDocumentId());
        assertEquals(request.getSegmentId(), deserialized.getSegmentId());
        assertEquals(request.getContent(), deserialized.getContent());
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
                "  \"content\": \"This is a test content for child chunk\"\n" +
                "}";

        SegmentChildChunkCreateRequest deserializedStandard = objectMapper.readValue(jsonStandard, SegmentChildChunkCreateRequest.class);
        assertEquals("sk-12345678", deserializedStandard.getApiKey());
        assertEquals("ds-12345", deserializedStandard.getDatasetId());
        assertEquals("doc-12345", deserializedStandard.getDocumentId());
        assertEquals("seg-12345", deserializedStandard.getSegmentId());
        assertEquals("This is a test content for child chunk", deserializedStandard.getContent());
    }

    /**
     * Test equality and hash code
     */
    @Test
    public void testEqualsAndHashCode() {
        // Create two identical requests
        SegmentChildChunkCreateRequest request1 = new SegmentChildChunkCreateRequest();
        request1.setApiKey("sk-12345678");
        request1.setDatasetId("ds-12345");
        request1.setDocumentId("doc-12345");
        request1.setSegmentId("seg-12345");
        request1.setContent("This is a test content for child chunk");

        SegmentChildChunkCreateRequest request2 = new SegmentChildChunkCreateRequest();
        request2.setApiKey("sk-12345678");
        request2.setDatasetId("ds-12345");
        request2.setDocumentId("doc-12345");
        request2.setSegmentId("seg-12345");
        request2.setContent("This is a test content for child chunk");

        // Create a different request
        SegmentChildChunkCreateRequest request3 = new SegmentChildChunkCreateRequest();
        request3.setApiKey("sk-87654321");
        request3.setDatasetId("ds-54321");
        request3.setDocumentId("doc-54321");
        request3.setSegmentId("seg-54321");
        request3.setContent("Different content");

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
        SegmentChildChunkCreateRequest request = new SegmentChildChunkCreateRequest();
        request.setApiKey("sk-12345678");
        request.setDatasetId("ds-12345");
        request.setDocumentId("doc-12345");
        request.setSegmentId("seg-12345");
        request.setContent("This is a test content for child chunk");

        // Act
        String toString = request.toString();

        // Assert
        assertTrue(toString.contains("apiKey=sk-12345678"));
        assertTrue(toString.contains("datasetId=ds-12345"));
        assertTrue(toString.contains("documentId=doc-12345"));
        assertTrue(toString.contains("segmentId=seg-12345"));
        assertTrue(toString.contains("content=This is a test content for child chunk"));
    }
}
