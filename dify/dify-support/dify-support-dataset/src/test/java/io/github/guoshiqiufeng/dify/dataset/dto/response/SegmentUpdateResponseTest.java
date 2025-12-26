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
package io.github.guoshiqiufeng.dify.dataset.dto.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test functionality of SegmentUpdateResponse class
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/26 16:35
 */
public class SegmentUpdateResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Test setting and getting basic properties of SegmentUpdateResponse")
    public void testBasicProperties() {
        // Create test object
        SegmentUpdateResponse response = new SegmentUpdateResponse();
        SegmentData data = new SegmentData();

        // Set properties
        data.setId("segment-123");
        data.setContent("Test content");
        data.setAnswer("Test answer");
        response.setData(data);

        // Verify properties
        assertNotNull(response.getData());
        assertEquals("segment-123", response.getData().getId());
        assertEquals("Test content", response.getData().getContent());
        assertEquals("Test answer", response.getData().getAnswer());
    }

    @Test
    @DisplayName("Test JSON serialization and deserialization of SegmentUpdateResponse")
    public void testJsonSerializationDeserialization() throws JsonProcessingException {
        // Create test object
        SegmentUpdateResponse response = new SegmentUpdateResponse();
        SegmentData data = new SegmentData();
        data.setId("segment-123");
        data.setContent("Test content");
        data.setAnswer("Test answer");
        response.setData(data);

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(response);

        // Verify JSON contains expected fields
        assertTrue(json.contains("\"data\":"));
        assertTrue(json.contains("\"id\":\"segment-123\""));
        assertTrue(json.contains("\"content\":\"Test content\""));
        assertTrue(json.contains("\"answer\":\"Test answer\""));

        // Deserialize from JSON
        SegmentUpdateResponse deserializedResponse = objectMapper.readValue(json, SegmentUpdateResponse.class);

        // Verify deserialized object
        assertNotNull(deserializedResponse.getData());
        assertEquals("segment-123", deserializedResponse.getData().getId());
        assertEquals("Test content", deserializedResponse.getData().getContent());
        assertEquals("Test answer", deserializedResponse.getData().getAnswer());
    }

    @Test
    @DisplayName("Test JSON deserialization of SegmentUpdateResponse using snake case naming")
    public void testSnakeCaseJsonDeserialization() throws JsonProcessingException {
        // JSON with snake case naming
        String snakeCaseJson = "{\n" +
                "  \"data\": {\n" +
                "    \"id\": \"segment-123\",\n" +
                "    \"content\": \"Test content\"\n" +
                "  }\n" +
                "}";

        // Deserialize from snake case JSON
        SegmentUpdateResponse deserializedResponse = objectMapper.readValue(snakeCaseJson, SegmentUpdateResponse.class);

        // Verify deserialized object
        assertNotNull(deserializedResponse.getData());
        assertEquals("segment-123", deserializedResponse.getData().getId());
        assertEquals("Test content", deserializedResponse.getData().getContent());
    }

    @Test
    @DisplayName("Test equality and hash code of SegmentUpdateResponse")
    public void testEqualsAndHashCode() {
        // Create two objects with same content
        SegmentUpdateResponse response1 = new SegmentUpdateResponse();
        SegmentData data1 = new SegmentData();
        data1.setId("segment-123");
        data1.setContent("Same content");
        response1.setData(data1);

        SegmentUpdateResponse response2 = new SegmentUpdateResponse();
        SegmentData data2 = new SegmentData();
        data2.setId("segment-123");
        data2.setContent("Same content");
        response2.setData(data2);

        // Test equality
        assertEquals(response1, response2);
        assertEquals(response2, response1);

        // Test hash code
        assertEquals(response1.hashCode(), response2.hashCode());

        // Create object with different content
        SegmentUpdateResponse response3 = new SegmentUpdateResponse();
        SegmentData data3 = new SegmentData();
        data3.setId("segment-456"); // Different ID
        data3.setContent("Same content");
        response3.setData(data3);

        // Test inequality
        assertNotEquals(response1, response3);
        assertNotEquals(response3, response1);
    }

    @Test
    @DisplayName("Test default values of SegmentUpdateResponse")
    public void testDefaultValues() {
        // Create new object to verify default values
        SegmentUpdateResponse response = new SegmentUpdateResponse();

        // Verify default values
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Test empty data object in SegmentUpdateResponse")
    public void testEmptyData() throws JsonProcessingException {
        // Create response with empty data
        SegmentUpdateResponse response = new SegmentUpdateResponse();
        response.setData(new SegmentData());

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(response);

        // Verify JSON
        assertTrue(json.contains("\"data\":"));

        // Empty objects should have default values (null) for all fields
        assertTrue(json.contains("\"data\":{}") || json.contains("\"data\":{"));

        // Deserialize from JSON
        SegmentUpdateResponse deserializedResponse = objectMapper.readValue(json, SegmentUpdateResponse.class);

        // Verify deserialized object
        assertNotNull(deserializedResponse.getData());
        assertNull(deserializedResponse.getData().getId());
        assertNull(deserializedResponse.getData().getContent());
        assertNull(deserializedResponse.getData().getAnswer());
    }

    @Test
    @DisplayName("Test toString method of SegmentUpdateResponse")
    public void testToString() {
        // Create test object
        SegmentUpdateResponse response = new SegmentUpdateResponse();
        SegmentData data = new SegmentData();
        data.setId("segment-123");
        data.setContent("Test content");
        response.setData(data);

        // Get toString result
        String toStringResult = response.toString();

        // Verify toString contains important fields
        assertTrue(toStringResult.contains("data="));
        assertTrue(toStringResult.contains("id=segment-123"));
        assertTrue(toStringResult.contains("content=Test content"));
    }
}
