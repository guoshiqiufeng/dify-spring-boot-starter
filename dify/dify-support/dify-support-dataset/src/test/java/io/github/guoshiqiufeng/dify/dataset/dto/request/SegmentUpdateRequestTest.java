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

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for SegmentUpdateRequest
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/26
 */
public class SegmentUpdateRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test basic getter and setter methods
     */
    @Test
    public void testGetterAndSetter() {
        // Create a SegmentUpdateRequest instance
        SegmentUpdateRequest request = new SegmentUpdateRequest();

        // Set values
        String apiKey = "test-api-key";
        String datasetId = "dataset-123";
        String documentId = "document-456";
        String segmentId = "segment-789";

        // Create sample segment parameter
        SegmentParam segment = new SegmentParam()
                .setContent("Test content")
                .setAnswer("Test answer")
                .setKeywords(Arrays.asList("test", "content"));

        // Set all values
        request.setApiKey(apiKey);
        request.setDatasetId(datasetId);
        request.setDocumentId(documentId);
        request.setSegmentId(segmentId);
        request.setSegment(segment);

        // Assert all values are set correctly
        assertEquals(apiKey, request.getApiKey());
        assertEquals(datasetId, request.getDatasetId());
        assertEquals(documentId, request.getDocumentId());
        assertEquals(segmentId, request.getSegmentId());
        assertEquals(segment, request.getSegment());

        // Verify segment content
        assertEquals("Test content", request.getSegment().getContent());
        assertEquals("Test answer", request.getSegment().getAnswer());
        assertEquals(2, request.getSegment().getKeywords().size());
    }

    /**
     * Test inheritance relationship
     */
    @Test
    public void testInheritance() {
        // Create a SegmentUpdateRequest instance
        SegmentUpdateRequest request = new SegmentUpdateRequest();

        // Test inheritance
        assertInstanceOf(BaseDatasetRequest.class, request);

        // Test casting to parent class
        BaseDatasetRequest baseRequest = request;
        assertSame(request, baseRequest);

        // Test setting fields through parent class reference
        baseRequest.setApiKey("api-key-from-parent");
        assertEquals("api-key-from-parent", request.getApiKey());
    }

    /**
     * Test default values
     */
    @Test
    public void testDefaultValues() {
        // Create a fresh instance
        SegmentUpdateRequest request = new SegmentUpdateRequest();

        // No default values for primitive fields should be set, so all null
        assertNull(request.getApiKey());
        assertNull(request.getDatasetId());
        assertNull(request.getDocumentId());
        assertNull(request.getSegmentId());
        assertNull(request.getSegment());
    }

    /**
     * Test JSON serialization
     */
    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        // Create a SegmentUpdateRequest instance with sample data
        SegmentUpdateRequest request = new SegmentUpdateRequest();
        request.setApiKey("test-api-key");
        request.setDatasetId("dataset-123");
        request.setDocumentId("document-456");
        request.setSegmentId("segment-789");

        // Create sample segment parameter
        SegmentParam segment = new SegmentParam()
                .setContent("Test content")
                .setAnswer("Test answer")
                .setKeywords(Arrays.asList("test", "content"));

        request.setSegment(segment);

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(request);

        // Verify JSON contains expected fields
        assertTrue(json.contains("\"apiKey\":\"test-api-key\""));
        assertTrue(json.contains("\"datasetId\":\"dataset-123\""));
        assertTrue(json.contains("\"documentId\":\"document-456\""));
        assertTrue(json.contains("\"segmentId\":\"segment-789\""));
        assertTrue(json.contains("\"segment\":"));
        assertTrue(json.contains("\"content\":\"Test content\""));
        assertTrue(json.contains("\"answer\":\"Test answer\""));
        assertTrue(json.contains("\"keywords\":[\"test\",\"content\"]"));

        // Deserialize back to object
        SegmentUpdateRequest deserialized = objectMapper.readValue(json, SegmentUpdateRequest.class);

        // Verify the deserialized object
        assertEquals("test-api-key", deserialized.getApiKey());
        assertEquals("dataset-123", deserialized.getDatasetId());
        assertEquals("document-456", deserialized.getDocumentId());
        assertEquals("segment-789", deserialized.getSegmentId());
        assertNotNull(deserialized.getSegment());

        // Check segment content
        SegmentParam deserializedSegment = deserialized.getSegment();
        assertEquals("Test content", deserializedSegment.getContent());
        assertEquals("Test answer", deserializedSegment.getAnswer());
        assertEquals(2, deserializedSegment.getKeywords().size());
        assertEquals("test", deserializedSegment.getKeywords().get(0));
        assertEquals("content", deserializedSegment.getKeywords().get(1));
    }

    /**
     * Test JSON deserialization with camelCase property names
     */
    @Test
    public void testCamelCaseJsonDeserialization() throws JsonProcessingException {
        // JSON with camelCase for datasetId, documentId and segmentId
        String camelCaseJson = "{\n" +
                "  \"apiKey\": \"test-api-key\",\n" +
                "  \"datasetId\": \"dataset-123\",\n" +
                "  \"documentId\": \"document-456\",\n" +
                "  \"segmentId\": \"segment-789\",\n" +
                "  \"segment\": {\n" +
                "    \"content\": \"Test content\",\n" +
                "    \"answer\": \"Test answer\",\n" +
                "    \"keywords\": [\"test\", \"content\"]\n" +
                "  }\n" +
                "}";

        // Deserialize from JSON with camelCase
        SegmentUpdateRequest deserialized = objectMapper.readValue(camelCaseJson, SegmentUpdateRequest.class);

        // Verify deserialized values
        assertEquals("test-api-key", deserialized.getApiKey());
        assertEquals("dataset-123", deserialized.getDatasetId());
        assertEquals("document-456", deserialized.getDocumentId());
        assertEquals("segment-789", deserialized.getSegmentId());
        assertNotNull(deserialized.getSegment());
    }

    /**
     * Test JSON deserialization with snake_case property names
     */
    @Test
    public void testSnakeCaseJsonDeserialization() throws JsonProcessingException {
        // JSON with snake_case for datasetId, documentId and segmentId (JsonAlias should handle it)
        String snakeCaseJson = "{\n" +
                "  \"apiKey\": \"test-api-key\",\n" +
                "  \"datasetId\": \"dataset-123\",\n" +
                "  \"documentId\": \"document-456\",\n" +
                "  \"segmentId\": \"segment-789\",\n" +
                "  \"segment\": {\n" +
                "    \"content\": \"Test content\",\n" +
                "    \"answer\": \"Test answer\",\n" +
                "    \"keywords\": [\"test\", \"content\"]\n" +
                "  }\n" +
                "}";

        // Snake case fields should be mapped correctly due to @JsonAlias
        SegmentUpdateRequest deserialized = objectMapper.readValue(snakeCaseJson, SegmentUpdateRequest.class);

        assertEquals("test-api-key", deserialized.getApiKey());
        // These should map correctly due to @JsonAlias in the class
        assertNotNull(deserialized.getDatasetId());
        assertNotNull(deserialized.getDocumentId());
        assertNotNull(deserialized.getSegmentId());
        assertNotNull(deserialized.getSegment());
    }

    /**
     * Test null handling for segment
     */
    @Test
    public void testNullSegment() throws JsonProcessingException {
        // Create request with null segment
        SegmentUpdateRequest request = new SegmentUpdateRequest();
        request.setApiKey("test-api-key");
        request.setDatasetId("dataset-123");
        request.setDocumentId("document-456");
        request.setSegmentId("segment-789");
        request.setSegment(null);

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(request);

        // Deserialize back to object
        SegmentUpdateRequest deserialized = objectMapper.readValue(json, SegmentUpdateRequest.class);

        // Verify null segment
        assertNull(deserialized.getSegment());
    }

    /**
     * Test equality and hash code
     */
    @Test
    public void testEqualsAndHashCode() {
        // Create two identical requests
        SegmentUpdateRequest request1 = new SegmentUpdateRequest();
        request1.setApiKey("test-api-key");
        request1.setDatasetId("dataset-123");
        request1.setDocumentId("document-456");
        request1.setSegmentId("segment-789");

        SegmentParam segment1 = new SegmentParam()
                .setContent("Test content")
                .setAnswer("Test answer")
                .setKeywords(Arrays.asList("test", "content"));

        request1.setSegment(segment1);

        SegmentUpdateRequest request2 = new SegmentUpdateRequest();
        request2.setApiKey("test-api-key");
        request2.setDatasetId("dataset-123");
        request2.setDocumentId("document-456");
        request2.setSegmentId("segment-789");

        SegmentParam segment2 = new SegmentParam()
                .setContent("Test content")
                .setAnswer("Test answer")
                .setKeywords(Arrays.asList("test", "content"));

        request2.setSegment(segment2);

        // Create a different request
        SegmentUpdateRequest request3 = new SegmentUpdateRequest();
        request3.setApiKey("test-api-key");
        request3.setDatasetId("dataset-123");
        request3.setDocumentId("document-456");
        request3.setSegmentId("different-id");  // Different segment ID
        request3.setSegment(segment1);

        // Test equality
        assertEquals(request1, request2);
        assertNotEquals(request1, request3);

        // Test hash code
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    /**
     * Test object modification after creation
     */
    @Test
    public void testModificationAfterCreation() {
        // Create request
        SegmentUpdateRequest request = new SegmentUpdateRequest();
        request.setApiKey("test-api-key");
        request.setDatasetId("dataset-123");
        request.setDocumentId("document-456");
        request.setSegmentId("segment-789");

        SegmentParam segment = new SegmentParam()
                .setContent("Original content")
                .setAnswer("Original answer")
                .setKeywords(Arrays.asList("original", "content"));

        request.setSegment(segment);

        // Modify request properties
        request.setSegmentId("updated-segment-id");

        // Modify segment properties
        request.getSegment().setContent("Updated content");
        request.getSegment().setAnswer("Updated answer");

        // Verify changes
        assertEquals("updated-segment-id", request.getSegmentId());
        assertEquals("Updated content", request.getSegment().getContent());
        assertEquals("Updated answer", request.getSegment().getAnswer());
    }
}
