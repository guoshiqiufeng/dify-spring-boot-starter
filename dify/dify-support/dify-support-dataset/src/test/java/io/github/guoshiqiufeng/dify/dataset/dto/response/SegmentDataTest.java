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
package io.github.guoshiqiufeng.dify.dataset.dto.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test functionality of SegmentData class
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/26 16:02
 */
public class SegmentDataTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Test setting and getting basic properties of SegmentData")
    public void testBasicProperties() {
        // Create test object
        SegmentData segmentData = new SegmentData();

        // Set basic properties
        String id = "segment-123";
        String content = "Test segment content";
        String answer = "Test answer";
        String segmentHash = "hash123";
        String docId = "doc-456";
        Long indexedAt = 1615379400000L;
        Boolean enabled = true;
        String keywords = "test,segment,content";
        Integer position = 1;
        String documentMetadata = "{\"author\":\"Test Author\",\"source\":\"Test Source\"}";

        segmentData.setId(id);
        segmentData.setContent(content);
        segmentData.setAnswer(answer);
        segmentData.setSegmentHash(segmentHash);
        segmentData.setDocId(docId);
        segmentData.setIndexedAt(indexedAt);
        segmentData.setEnabled(enabled);
        segmentData.setKeywords(keywords);
        segmentData.setPosition(position);
        segmentData.setDocumentMetadata(documentMetadata);

        // Verify properties are correctly set
        assertEquals(id, segmentData.getId());
        assertEquals(content, segmentData.getContent());
        assertEquals(answer, segmentData.getAnswer());
        assertEquals(segmentHash, segmentData.getSegmentHash());
        assertEquals(docId, segmentData.getDocId());
        assertEquals(indexedAt, segmentData.getIndexedAt());
        assertEquals(enabled, segmentData.getEnabled());
        assertEquals(keywords, segmentData.getKeywords());
        assertEquals(position, segmentData.getPosition());
        assertEquals(documentMetadata, segmentData.getDocumentMetadata());
    }

    @Test
    @DisplayName("Test chained setter methods of SegmentData")
    public void testChainedSetters() {
        // Create a new SegmentData using chained setters
        SegmentData segmentData = new SegmentData()
                .setId("segment-123")
                .setContent("Test content")
                .setAnswer("Test answer")
                .setSegmentHash("hash123")
                .setDocId("doc-456")
                .setIndexedAt(1615379400000L)
                .setEnabled(true)
                .setKeywords("test,content")
                .setPosition(1)
                .setDocumentMetadata("{\"author\":\"Test Author\"}");

        // Verify all properties are set correctly
        assertEquals("segment-123", segmentData.getId());
        assertEquals("Test content", segmentData.getContent());
        assertEquals("Test answer", segmentData.getAnswer());
        assertEquals("hash123", segmentData.getSegmentHash());
        assertEquals("doc-456", segmentData.getDocId());
        assertEquals(Long.valueOf(1615379400000L), segmentData.getIndexedAt());
        assertEquals(Boolean.TRUE, segmentData.getEnabled());
        assertEquals("test,content", segmentData.getKeywords());
        assertEquals(Integer.valueOf(1), segmentData.getPosition());
        assertEquals("{\"author\":\"Test Author\"}", segmentData.getDocumentMetadata());
    }

    @Test
    @DisplayName("Test JSON serialization and deserialization of SegmentData")
    public void testJsonSerializationDeserialization() throws JsonProcessingException {
        // Create test object
        SegmentData segmentData = new SegmentData();
        segmentData.setId("segment-123");
        segmentData.setContent("Test content");
        segmentData.setAnswer("Test answer");
        segmentData.setSegmentHash("hash123");
        segmentData.setDocId("doc-456");
        segmentData.setIndexedAt(1615379400000L);
        segmentData.setEnabled(true);
        segmentData.setKeywords("test,content");
        segmentData.setPosition(1);
        segmentData.setDocumentMetadata("{\"author\":\"Test Author\"}");

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(segmentData);

        // Verify JSON contains expected fields
        assertTrue(json.contains("\"id\":\"segment-123\""));
        assertTrue(json.contains("\"content\":\"Test content\""));
        assertTrue(json.contains("\"answer\":\"Test answer\""));
        assertTrue(json.contains("\"segmentHash\":\"hash123\""));
        assertTrue(json.contains("\"docId\":\"doc-456\""));
        assertTrue(json.contains("\"indexedAt\":1615379400000"));
        assertTrue(json.contains("\"enabled\":true"));
        assertTrue(json.contains("\"keywords\":\"test,content\""));
        assertTrue(json.contains("\"position\":1"));
        assertTrue(json.contains("\"documentMetadata\":\"{\\\"author\\\":\\\"Test Author\\\"}\""));

        // Deserialize from JSON
        SegmentData deserializedData = objectMapper.readValue(json, SegmentData.class);

        // Verify deserialized object
        assertEquals("segment-123", deserializedData.getId());
        assertEquals("Test content", deserializedData.getContent());
        assertEquals("Test answer", deserializedData.getAnswer());
        assertEquals("hash123", deserializedData.getSegmentHash());
        assertEquals("doc-456", deserializedData.getDocId());
        assertEquals(Long.valueOf(1615379400000L), deserializedData.getIndexedAt());
        assertEquals(Boolean.TRUE, deserializedData.getEnabled());
        assertEquals("test,content", deserializedData.getKeywords());
        assertEquals(Integer.valueOf(1), deserializedData.getPosition());
        assertEquals("{\"author\":\"Test Author\"}", deserializedData.getDocumentMetadata());
    }

    @Test
    @DisplayName("Test JSON deserialization of SegmentData using snake case naming")
    public void testSnakeCaseJsonDeserialization() throws JsonProcessingException {
        // JSON using snake case naming
        String snakeCaseJson = "{\n" +
                "  \"id\": \"segment-123\",\n" +
                "  \"content\": \"Test content\",\n" +
                "  \"answer\": \"Test answer\",\n" +
                "  \"segment_hash\": \"hash123\",\n" +
                "  \"doc_id\": \"doc-456\",\n" +
                "  \"indexed_at\": 1615379400000,\n" +
                "  \"enabled\": true,\n" +
                "  \"keywords\": \"test,content\",\n" +
                "  \"position\": 1,\n" +
                "  \"document_metadata\": \"{\\\"author\\\":\\\"Test Author\\\"}\"\n" +
                "}";

        // Deserialize from snake case JSON
        SegmentData deserializedData = objectMapper.readValue(snakeCaseJson, SegmentData.class);

        // Verify snake case field mapping
        assertEquals("segment-123", deserializedData.getId());
        assertEquals("Test content", deserializedData.getContent());
        assertEquals("Test answer", deserializedData.getAnswer());
        assertEquals("hash123", deserializedData.getSegmentHash());  // Verify segment_hash mapping
        assertEquals("doc-456", deserializedData.getDocId());  // Verify doc_id mapping
        assertEquals(Long.valueOf(1615379400000L), deserializedData.getIndexedAt());  // Verify indexed_at mapping
        assertEquals(Boolean.TRUE, deserializedData.getEnabled());
        assertEquals("test,content", deserializedData.getKeywords());
        assertEquals(Integer.valueOf(1), deserializedData.getPosition());
        assertEquals("{\"author\":\"Test Author\"}", deserializedData.getDocumentMetadata());  // Verify document_metadata mapping
    }

    @Test
    @DisplayName("Test equality and hash code of SegmentData")
    public void testEqualsAndHashCode() {
        // Create two identical objects
        SegmentData data1 = new SegmentData();
        data1.setId("segment-123");
        data1.setContent("Test content");
        data1.setDocId("doc-456");

        SegmentData data2 = new SegmentData();
        data2.setId("segment-123");
        data2.setContent("Test content");
        data2.setDocId("doc-456");

        // Test equality
        assertEquals(data1, data2);
        assertEquals(data2, data1);

        // Test hash code
        assertEquals(data1.hashCode(), data2.hashCode());

        // Create object with different content
        SegmentData data3 = new SegmentData();
        data3.setId("segment-456");  // Different ID
        data3.setContent("Test content");
        data3.setDocId("doc-456");

        // Test inequality
        assertNotEquals(data1, data3);
        assertNotEquals(data3, data1);

        // Test comparison with null and other types
        assertNotEquals(data1, null);
        assertNotEquals(data1, "Not a SegmentData");
    }

    @Test
    @DisplayName("Test default values of SegmentData")
    public void testDefaultValues() {
        // Create new object to verify default values
        SegmentData segmentData = new SegmentData();

        // Verify default values are null
        assertNull(segmentData.getId());
        assertNull(segmentData.getContent());
        assertNull(segmentData.getAnswer());
        assertNull(segmentData.getSegmentHash());
        assertNull(segmentData.getDocId());
        assertNull(segmentData.getIndexedAt());
        assertNull(segmentData.getEnabled());
        assertNull(segmentData.getKeywords());
        assertNull(segmentData.getPosition());
        assertNull(segmentData.getDocumentMetadata());
    }

    @Test
    @DisplayName("Test toString method of SegmentData")
    public void testToString() {
        // Create test object
        SegmentData segmentData = new SegmentData();
        segmentData.setId("segment-123");
        segmentData.setContent("Test content");
        segmentData.setAnswer("Test answer");
        segmentData.setDocId("doc-456");

        // Get toString result
        String toStringResult = segmentData.toString();

        // Verify toString contains important fields
        assertTrue(toStringResult.contains("id=segment-123"));
        assertTrue(toStringResult.contains("content=Test content"));
        assertTrue(toStringResult.contains("answer=Test answer"));
        assertTrue(toStringResult.contains("docId=doc-456"));
    }
} 