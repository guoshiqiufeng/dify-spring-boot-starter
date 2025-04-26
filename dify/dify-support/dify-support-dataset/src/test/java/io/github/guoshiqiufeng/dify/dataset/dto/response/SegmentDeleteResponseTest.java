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
 * Test functionality of SegmentDeleteResponse class
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/26 16:35
 */
public class SegmentDeleteResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Test setting and getting basic properties of SegmentDeleteResponse")
    public void testBasicProperties() {
        // Create test object
        SegmentDeleteResponse response = new SegmentDeleteResponse();

        // Set properties
        String result = "success";
        response.setResult(result);

        // Verify properties
        assertEquals(result, response.getResult());
    }

    @Test
    @DisplayName("Test JSON serialization and deserialization of SegmentDeleteResponse")
    public void testJsonSerializationDeserialization() throws JsonProcessingException {
        // Create test object
        SegmentDeleteResponse response = new SegmentDeleteResponse();
        response.setResult("success");

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(response);

        // Verify JSON contains expected fields
        assertTrue(json.contains("\"result\":\"success\""));

        // Deserialize from JSON
        SegmentDeleteResponse deserializedResponse = objectMapper.readValue(json, SegmentDeleteResponse.class);

        // Verify deserialized object
        assertEquals("success", deserializedResponse.getResult());
    }

    @Test
    @DisplayName("Test equality and hash code of SegmentDeleteResponse")
    public void testEqualsAndHashCode() {
        // Create two objects with the same content
        SegmentDeleteResponse response1 = new SegmentDeleteResponse();
        response1.setResult("success");

        SegmentDeleteResponse response2 = new SegmentDeleteResponse();
        response2.setResult("success");

        // Test equality
        assertEquals(response1, response2);
        assertEquals(response2, response1);

        // Test hash code
        assertEquals(response1.hashCode(), response2.hashCode());

        // Create object with different content
        SegmentDeleteResponse response3 = new SegmentDeleteResponse();
        response3.setResult("failed");

        // Test inequality
        assertNotEquals(response1, response3);
        assertNotEquals(response3, response1);
    }

    @Test
    @DisplayName("Test default values of SegmentDeleteResponse")
    public void testDefaultValues() {
        // Create new object to verify default values
        SegmentDeleteResponse response = new SegmentDeleteResponse();

        // Default should be null
        assertNull(response.getResult());
    }

    @Test
    @DisplayName("Test empty string handling in SegmentDeleteResponse")
    public void testEmptyStringResult() throws JsonProcessingException {
        // Create response with empty string result
        SegmentDeleteResponse response = new SegmentDeleteResponse();
        response.setResult("");

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(response);

        // Verify JSON
        assertTrue(json.contains("\"result\":\"\""));

        // Deserialize from JSON
        SegmentDeleteResponse deserializedResponse = objectMapper.readValue(json, SegmentDeleteResponse.class);

        // Verify deserialized object
        assertEquals("", deserializedResponse.getResult());
    }

    @Test
    @DisplayName("Test toString method of SegmentDeleteResponse")
    public void testToString() {
        // Create test object
        SegmentDeleteResponse response = new SegmentDeleteResponse();
        response.setResult("success");

        // Get toString result
        String toStringResult = response.toString();

        // Verify toString contains result field
        assertTrue(toStringResult.contains("result=success"));
    }
} 