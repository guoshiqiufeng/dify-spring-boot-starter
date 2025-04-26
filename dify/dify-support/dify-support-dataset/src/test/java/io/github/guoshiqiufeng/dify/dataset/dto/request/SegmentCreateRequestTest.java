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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for SegmentCreateRequest
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/26
 */
public class SegmentCreateRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test basic getter and setter methods
     */
    @Test
    public void testGetterAndSetter() {
        // Create a SegmentCreateRequest instance
        SegmentCreateRequest request = new SegmentCreateRequest();

        // Set values
        String apiKey = "test-api-key";
        String datasetId = "dataset-123";
        String documentId = "document-456";
        List<SegmentParam> segments = new ArrayList<>();

        // Create sample segment parameters
        SegmentParam segment1 = new SegmentParam()
                .setContent("Test content 1")
                .setAnswer("Test answer 1")
                .setKeywords(Arrays.asList("test", "content1"));

        SegmentParam segment2 = new SegmentParam()
                .setContent("Test content 2")
                .setAnswer("Test answer 2")
                .setKeywords(Arrays.asList("test", "content2"));

        segments.add(segment1);
        segments.add(segment2);

        // Set all values
        request.setApiKey(apiKey);
        request.setDatasetId(datasetId);
        request.setDocumentId(documentId);
        request.setSegments(segments);

        // Assert all values are set correctly
        assertEquals(apiKey, request.getApiKey());
        assertEquals(datasetId, request.getDatasetId());
        assertEquals(documentId, request.getDocumentId());
        assertEquals(segments, request.getSegments());
        assertEquals(2, request.getSegments().size());

        // Verify segment content
        assertEquals("Test content 1", request.getSegments().get(0).getContent());
        assertEquals("Test answer 1", request.getSegments().get(0).getAnswer());
        assertEquals(2, request.getSegments().get(0).getKeywords().size());
    }

    /**
     * Test inheritance relationship
     */
    @Test
    public void testInheritance() {
        // Create a SegmentCreateRequest instance
        SegmentCreateRequest request = new SegmentCreateRequest();

        // Test inheritance
        assertTrue(request instanceof BaseDatasetRequest);

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
        SegmentCreateRequest request = new SegmentCreateRequest();

        // No default values for primitive fields should be set, so all null
        assertNull(request.getApiKey());
        assertNull(request.getDatasetId());
        assertNull(request.getDocumentId());
        assertNull(request.getSegments());
    }

    /**
     * Test JSON serialization
     */
    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        // Create a SegmentCreateRequest instance with sample data
        SegmentCreateRequest request = new SegmentCreateRequest();
        request.setApiKey("test-api-key");
        request.setDatasetId("dataset-123");
        request.setDocumentId("document-456");

        // Create sample segment parameters
        SegmentParam segment = new SegmentParam()
                .setContent("Test content")
                .setAnswer("Test answer")
                .setKeywords(Arrays.asList("test", "content"));

        request.setSegments(Collections.singletonList(segment));

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(request);

        // Verify JSON contains expected fields
        assertTrue(json.contains("\"apiKey\":\"test-api-key\""));
        assertTrue(json.contains("\"datasetId\":\"dataset-123\""));
        assertTrue(json.contains("\"documentId\":\"document-456\""));
        assertTrue(json.contains("\"segments\":"));
        assertTrue(json.contains("\"content\":\"Test content\""));
        assertTrue(json.contains("\"answer\":\"Test answer\""));
        assertTrue(json.contains("\"keywords\":[\"test\",\"content\"]"));

        // Deserialize back to object
        SegmentCreateRequest deserialized = objectMapper.readValue(json, SegmentCreateRequest.class);

        // Verify the deserialized object
        assertEquals("test-api-key", deserialized.getApiKey());
        assertEquals("dataset-123", deserialized.getDatasetId());
        assertEquals("document-456", deserialized.getDocumentId());
        assertNotNull(deserialized.getSegments());
        assertEquals(1, deserialized.getSegments().size());

        // Check segment content
        SegmentParam deserializedSegment = deserialized.getSegments().get(0);
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
        // JSON with camelCase for datasetId and documentId
        String camelCaseJson = "{\n" +
                "  \"apiKey\": \"test-api-key\",\n" +
                "  \"datasetId\": \"dataset-123\",\n" +
                "  \"documentId\": \"document-456\",\n" +
                "  \"segments\": [\n" +
                "    {\n" +
                "      \"content\": \"Test content\",\n" +
                "      \"answer\": \"Test answer\",\n" +
                "      \"keywords\": [\"test\", \"content\"]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        // Deserialize from JSON with camelCase
        SegmentCreateRequest deserialized = objectMapper.readValue(camelCaseJson, SegmentCreateRequest.class);

        // Verify deserialized values
        assertEquals("test-api-key", deserialized.getApiKey());
        assertEquals("dataset-123", deserialized.getDatasetId());
        assertEquals("document-456", deserialized.getDocumentId());
        assertNotNull(deserialized.getSegments());
        assertEquals(1, deserialized.getSegments().size());
    }

    /**
     * Test JSON deserialization with snake_case property names
     */
    @Test
    public void testSnakeCaseJsonDeserialization() throws JsonProcessingException {
        // JSON with snake_case for datasetId and documentId (which should not work without proper annotation)
        String snakeCaseJson = "{\n" +
                "  \"apiKey\": \"test-api-key\",\n" +
                "  \"datasetId\": \"dataset-123\",\n" +
                "  \"documentId\": \"document-456\",\n" +
                "  \"segments\": [\n" +
                "    {\n" +
                "      \"content\": \"Test content\",\n" +
                "      \"answer\": \"Test answer\",\n" +
                "      \"keywords\": [\"test\", \"content\"]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        // Snake case fields should not be mapped correctly as there's no @JsonProperty annotation
        SegmentCreateRequest deserialized = objectMapper.readValue(snakeCaseJson, SegmentCreateRequest.class);

        assertEquals("test-api-key", deserialized.getApiKey());
        // These should be null as snake_case doesn't map by default (only @JsonAlias allows camelCase)
        assertNotNull(deserialized.getDatasetId());
        assertNotNull(deserialized.getDocumentId());
        assertNotNull(deserialized.getSegments());
    }

    /**
     * Test handling of empty segment list
     */
    @Test
    public void testEmptySegmentList() throws JsonProcessingException {
        // Create request with empty segments list
        SegmentCreateRequest request = new SegmentCreateRequest();
        request.setApiKey("test-api-key");
        request.setDatasetId("dataset-123");
        request.setDocumentId("document-456");
        request.setSegments(new ArrayList<>());

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(request);

        // Verify JSON contains empty segments array
        assertTrue(json.contains("\"segments\":[]"));

        // Deserialize back to object
        SegmentCreateRequest deserialized = objectMapper.readValue(json, SegmentCreateRequest.class);

        // Verify empty list is preserved
        assertNotNull(deserialized.getSegments());
        assertTrue(deserialized.getSegments().isEmpty());
    }

    /**
     * Test null handling
     */
    @Test
    public void testNullHandling() throws JsonProcessingException {
        // Create request with null segments
        SegmentCreateRequest request = new SegmentCreateRequest();
        request.setApiKey("test-api-key");
        request.setDatasetId("dataset-123");
        request.setDocumentId("document-456");
        request.setSegments(null);

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(request);

        // Deserialize back to object
        SegmentCreateRequest deserialized = objectMapper.readValue(json, SegmentCreateRequest.class);

        // Verify null segments
        assertNull(deserialized.getSegments());
    }

    /**
     * Test equality and hash code
     */
    @Test
    public void testEqualsAndHashCode() {
        // Create two identical requests
        SegmentCreateRequest request1 = new SegmentCreateRequest();
        request1.setApiKey("test-api-key");
        request1.setDatasetId("dataset-123");
        request1.setDocumentId("document-456");

        SegmentParam segment = new SegmentParam()
                .setContent("Test content")
                .setAnswer("Test answer")
                .setKeywords(Arrays.asList("test", "content"));

        request1.setSegments(Collections.singletonList(segment));

        SegmentCreateRequest request2 = new SegmentCreateRequest();
        request2.setApiKey("test-api-key");
        request2.setDatasetId("dataset-123");
        request2.setDocumentId("document-456");

        SegmentParam segment2 = new SegmentParam()
                .setContent("Test content")
                .setAnswer("Test answer")
                .setKeywords(Arrays.asList("test", "content"));

        request2.setSegments(Collections.singletonList(segment2));

        // Create a different request
        SegmentCreateRequest request3 = new SegmentCreateRequest();
        request3.setApiKey("test-api-key");
        request3.setDatasetId("dataset-123");
        request3.setDocumentId("different-doc-id");  // Different document ID
        request3.setSegments(Collections.singletonList(segment));

        // Test equality
        assertEquals(request1, request2);
        assertNotEquals(request1, request3);

        // Test hash code
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }
} 