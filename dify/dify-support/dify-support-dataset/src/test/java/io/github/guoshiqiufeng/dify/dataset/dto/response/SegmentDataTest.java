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
        Integer position = 1;
        String content = "Test segment content";
        String answer = "Test answer";
        Integer wordCount = 100;
        Integer tokens = 50;
        String indexingStatus = "completed";
        String error = null;
        String enabled = "true";
        Long disabledAt = null;
        String disabledBy = null;
        Boolean archived = false;
        
        // Set all properties
        segmentData.setId(id);
        segmentData.setPosition(position);
        segmentData.setContent(content);
        segmentData.setAnswer(answer);
        segmentData.setWordCount(wordCount);
        segmentData.setTokens(tokens);
        segmentData.setIndexingStatus(indexingStatus);
        segmentData.setError(error);
        segmentData.setEnabled(enabled);
        segmentData.setDisabledAt(disabledAt);
        segmentData.setDisabledBy(disabledBy);
        segmentData.setArchived(archived);
        
        // Verify all properties
        assertEquals(id, segmentData.getId());
        assertEquals(position, segmentData.getPosition());
        assertEquals(content, segmentData.getContent());
        assertEquals(answer, segmentData.getAnswer());
        assertEquals(wordCount, segmentData.getWordCount());
        assertEquals(tokens, segmentData.getTokens());
        assertEquals(indexingStatus, segmentData.getIndexingStatus());
        assertEquals(error, segmentData.getError());
        assertEquals(enabled, segmentData.getEnabled());
        assertEquals(disabledAt, segmentData.getDisabledAt());
        assertEquals(disabledBy, segmentData.getDisabledBy());
        assertEquals(archived, segmentData.getArchived());
    }
    
    @Test
    @DisplayName("Test chained setter methods of SegmentData")
    public void testChainSetter() {
        // Use chained setter methods (if available)
        SegmentData segmentData = new SegmentData();
        
        // @Data annotation does not provide chained setters, so we need to reassign the variable after each set
        segmentData.setId("segment-123");
        segmentData.setContent("Test content");
        segmentData.setAnswer("Test answer");
        
        assertEquals("segment-123", segmentData.getId());
        assertEquals("Test content", segmentData.getContent());
        assertEquals("Test answer", segmentData.getAnswer());
    }
    
    @Test
    @DisplayName("Test JSON serialization and deserialization of SegmentData")
    public void testJsonSerializationDeserialization() throws JsonProcessingException {
        // Create object with complete data
        SegmentData segmentData = new SegmentData();
        segmentData.setId("segment-123");
        segmentData.setPosition(1);
        segmentData.setContent("Test content");
        segmentData.setAnswer("Test answer");
        segmentData.setWordCount(100);
        segmentData.setTokens(50);
        segmentData.setIndexingStatus("completed");
        segmentData.setEnabled("true");
        segmentData.setArchived(false);
        
        // Serialize to JSON
        String json = objectMapper.writeValueAsString(segmentData);
        
        // Check that JSON contains expected fields
        assertTrue(json.contains("\"id\":\"segment-123\""));
        assertTrue(json.contains("\"position\":1"));
        assertTrue(json.contains("\"content\":\"Test content\""));
        assertTrue(json.contains("\"answer\":\"Test answer\""));
        assertTrue(json.contains("\"wordCount\":100"));
        assertTrue(json.contains("\"tokens\":50"));
        assertTrue(json.contains("\"indexingStatus\":\"completed\""));
        assertTrue(json.contains("\"enabled\":\"true\""));
        assertTrue(json.contains("\"archived\":false"));
        
        // Deserialize from JSON
        SegmentData deserializedData = objectMapper.readValue(json, SegmentData.class);
        
        // Verify deserialized object
        assertEquals(segmentData.getId(), deserializedData.getId());
        assertEquals(segmentData.getPosition(), deserializedData.getPosition());
        assertEquals(segmentData.getContent(), deserializedData.getContent());
        assertEquals(segmentData.getAnswer(), deserializedData.getAnswer());
        assertEquals(segmentData.getWordCount(), deserializedData.getWordCount());
        assertEquals(segmentData.getTokens(), deserializedData.getTokens());
        assertEquals(segmentData.getIndexingStatus(), deserializedData.getIndexingStatus());
        assertEquals(segmentData.getEnabled(), deserializedData.getEnabled());
        assertEquals(segmentData.getArchived(), deserializedData.getArchived());
    }
    
    @Test
    @DisplayName("Test JSON deserialization of SegmentData using snake case naming")
    public void testSnakeCaseJsonDeserialization() throws JsonProcessingException {
        // JSON using snake case naming
        String snakeCaseJson = "{\n" +
                "  \"id\": \"segment-123\",\n" +
                "  \"position\": 1,\n" +
                "  \"content\": \"Test content\",\n" +
                "  \"answer\": \"Test answer\",\n" +
                "  \"word_count\": 100,\n" +
                "  \"tokens\": 50,\n" +
                "  \"indexing_status\": \"completed\",\n" +
                "  \"error\": null,\n" +
                "  \"enabled\": \"true\",\n" +
                "  \"disabled_at\": null,\n" +
                "  \"disabled_by\": null,\n" +
                "  \"archived\": false\n" +
                "}";
        
        // Deserialize from snake case JSON
        SegmentData deserializedData = objectMapper.readValue(snakeCaseJson, SegmentData.class);
        
        // Verify fields with @JsonAlias annotations are correctly mapped
        assertEquals("segment-123", deserializedData.getId());
        assertEquals(Integer.valueOf(1), deserializedData.getPosition());
        assertEquals("Test content", deserializedData.getContent());
        assertEquals("Test answer", deserializedData.getAnswer());
        assertEquals(Integer.valueOf(100), deserializedData.getWordCount());
        assertEquals(Integer.valueOf(50), deserializedData.getTokens());
        assertEquals("completed", deserializedData.getIndexingStatus());
        assertNull(deserializedData.getError());
        assertEquals("true", deserializedData.getEnabled());
        assertNull(deserializedData.getDisabledAt());
        assertNull(deserializedData.getDisabledBy());
        assertEquals(Boolean.FALSE, deserializedData.getArchived());
    }
    
    @Test
    @DisplayName("Test equality and hash code of SegmentData")
    public void testEqualsAndHashCode() {
        // Create two objects with identical content
        SegmentData data1 = new SegmentData();
        data1.setId("segment-123");
        data1.setContent("Same content");
        data1.setAnswer("Same answer");
        
        SegmentData data2 = new SegmentData();
        data2.setId("segment-123");
        data2.setContent("Same content");
        data2.setAnswer("Same answer");
        
        // Test equality
        assertEquals(data1, data2);
        assertEquals(data2, data1);
        
        // Test hash code
        assertEquals(data1.hashCode(), data2.hashCode());
        
        // Create object with different content
        SegmentData data3 = new SegmentData();
        data3.setId("segment-456");  // Different ID
        data3.setContent("Same content");
        data3.setAnswer("Same answer");
        
        // Test inequality
        assertNotEquals(data1, data3);
        assertNotEquals(data3, data1);
        
        // Test comparison with null and other types
        assertNotEquals(data1, null);
        assertNotEquals(data1, "String");
    }
    
    @Test
    @DisplayName("Test default values of SegmentData")
    public void testDefaultValues() {
        // Create new object to verify default values
        SegmentData segmentData = new SegmentData();
        
        // All fields should have null as default value
        assertNull(segmentData.getId());
        assertNull(segmentData.getPosition());
        assertNull(segmentData.getContent());
        assertNull(segmentData.getAnswer());
        assertNull(segmentData.getWordCount());
        assertNull(segmentData.getTokens());
        assertNull(segmentData.getIndexingStatus());
        assertNull(segmentData.getError());
        assertNull(segmentData.getEnabled());
        assertNull(segmentData.getDisabledAt());
        assertNull(segmentData.getDisabledBy());
        assertNull(segmentData.getArchived());
    }
    
    @Test
    @DisplayName("Test toString method of SegmentData")
    public void testToString() {
        // Create test object
        SegmentData segmentData = new SegmentData();
        segmentData.setId("segment-123");
        segmentData.setContent("Test content");
        segmentData.setAnswer("Test answer");
        
        // Get toString result
        String toStringResult = segmentData.toString();
        
        // Verify toString contains all important fields
        assertTrue(toStringResult.contains("id=segment-123"));
        assertTrue(toStringResult.contains("content=Test content"));
        assertTrue(toStringResult.contains("answer=Test answer"));
    }
} 
