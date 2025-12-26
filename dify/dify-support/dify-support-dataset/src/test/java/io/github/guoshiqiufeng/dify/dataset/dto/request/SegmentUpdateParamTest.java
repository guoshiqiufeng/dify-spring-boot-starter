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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for SegmentUpdateParam
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/26
 */
public class SegmentUpdateParamTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test basic getter and setter methods
     */
    @Test
    public void testGetterAndSetter() {
        // Create a SegmentUpdateParam instance
        SegmentUpdateParam param = new SegmentUpdateParam();

        // Set values for parent class fields
        String content = "This is a test content";
        String answer = "This is a test answer";
        List<String> keywords = Arrays.asList("test", "content", "answer");
        param.setContent(content);
        param.setAnswer(answer);
        param.setKeywords(keywords);

        // Set values for fields
        Boolean enabled = true;
        Boolean regenerateChildChunks = false;
        param.setEnabled(enabled);
        param.setRegenerateChildChunks(regenerateChildChunks);

        // Assert all values are set correctly
        // Parent class fields
        assertEquals(content, param.getContent());
        assertEquals(answer, param.getAnswer());
        assertEquals(keywords, param.getKeywords());

        // Fields in this class
        assertEquals(enabled, param.getEnabled());
        assertEquals(regenerateChildChunks, param.getRegenerateChildChunks());
    }

    /**
     * Test inheritance relationship
     */
    @Test
    public void testInheritance() {
        // Create a SegmentUpdateParam instance
        SegmentUpdateParam param = new SegmentUpdateParam();

        // Test inheritance
        assertInstanceOf(SegmentParam.class, param);

        // Test casting to parent class
        SegmentParam parentParam = param;
        assertSame(param, parentParam);

        // Test setting fields through parent class reference
        parentParam.setContent("Parent content");
        assertEquals("Parent content", param.getContent());
    }

    /**
     * Test chain method calls with @Accessors(chain = true)
     */
    @Test
    public void testChainMethodCalls() {
        // Create and set a SegmentUpdateParam instance using chain method calls
        SegmentUpdateParam param = new SegmentUpdateParam();

        param.setContent("This is a test content")  // Inherited from SegmentParam
                .setAnswer("This is a test answer")    // Inherited from SegmentParam
                .setKeywords(Arrays.asList("test", "content", "answer"));  // Inherited from SegmentParam
        param.setEnabled(true)
                .setRegenerateChildChunks(false);

        // Assert all values are set correctly
        assertEquals("This is a test content", param.getContent());
        assertEquals("This is a test answer", param.getAnswer());
        assertEquals(3, param.getKeywords().size());
        assertTrue(param.getEnabled());
        assertFalse(param.getRegenerateChildChunks());
    }

    /**
     * Test serialization with Jackson
     */
    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        // Create and set a SegmentUpdateParam instance using chain method calls
        SegmentUpdateParam param = new SegmentUpdateParam();

        param.setContent("This is a test content")  // Inherited from SegmentParam
                .setAnswer("This is a test answer")    // Inherited from SegmentParam
                .setKeywords(Arrays.asList("test", "content", "answer"));  // Inherited from SegmentParam
        param.setEnabled(true)
                .setRegenerateChildChunks(false);

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(param);

        // Verify JSON contains expected property names and values
        // Parent class fields
        assertTrue(json.contains("\"content\":"));
        assertTrue(json.contains("\"answer\":"));
        assertTrue(json.contains("\"keywords\":"));

        // Fields in this class - check for snake_case format due to @JsonProperty
        assertTrue(json.contains("\"enabled\":"));
        assertTrue(json.contains("\"regenerate_child_chunks\":"));  // Snake case format from @JsonProperty

        // Verify JSON contains expected values
        assertTrue(json.contains("\"enabled\":true"));
        assertTrue(json.contains("\"regenerate_child_chunks\":false"));

        // Deserialize back to object
        SegmentUpdateParam deserialized = objectMapper.readValue(json, SegmentUpdateParam.class);

        // Verify the deserialized object matches the original
        assertEquals(param.getContent(), deserialized.getContent());
        assertEquals(param.getAnswer(), deserialized.getAnswer());
        assertEquals(param.getKeywords(), deserialized.getKeywords());
        assertEquals(param.getEnabled(), deserialized.getEnabled());
        assertEquals(param.getRegenerateChildChunks(), deserialized.getRegenerateChildChunks());
    }

    /**
     * Test JSON deserialization with snake_case format
     */
    @Test
    public void testSnakeCaseJsonDeserialization() throws JsonProcessingException {
        // JSON with snake_case format for regenerateChildChunks
        String snakeCaseJson = "{\n" +
                "  \"content\": \"This is a test content\",\n" +
                "  \"answer\": \"This is a test answer\",\n" +
                "  \"keywords\": [\"test\", \"content\", \"answer\"],\n" +
                "  \"enabled\": true,\n" +
                "  \"regenerate_child_chunks\": true\n" +
                "}";

        // Deserialize from JSON with snake_case
        SegmentUpdateParam deserialized = objectMapper.readValue(snakeCaseJson, SegmentUpdateParam.class);

        // Verify deserialized values
        assertEquals("This is a test content", deserialized.getContent());
        assertEquals("This is a test answer", deserialized.getAnswer());
        assertEquals(3, deserialized.getKeywords().size());
        assertTrue(deserialized.getEnabled());
        assertTrue(deserialized.getRegenerateChildChunks());
    }

    /**
     * Test JSON deserialization with camelCase format using @JsonAlias
     */
    @Test
    public void testCamelCaseJsonDeserialization() throws JsonProcessingException {
        // JSON with camelCase format for regenerateChildChunks
        String camelCaseJson = "{\n" +
                "  \"content\": \"This is a test content\",\n" +
                "  \"answer\": \"This is a test answer\",\n" +
                "  \"keywords\": [\"test\", \"content\", \"answer\"],\n" +
                "  \"enabled\": true,\n" +
                "  \"regenerateChildChunks\": true\n" +
                "}";

        // Deserialize from JSON with camelCase
        SegmentUpdateParam deserialized = objectMapper.readValue(camelCaseJson, SegmentUpdateParam.class);

        // Verify deserialized values
        assertEquals("This is a test content", deserialized.getContent());
        assertEquals("This is a test answer", deserialized.getAnswer());
        assertEquals(3, deserialized.getKeywords().size());
        assertTrue(deserialized.getEnabled());
        assertTrue(deserialized.getRegenerateChildChunks());
    }

    /**
     * Test null values
     */
    @Test
    public void testNullValues() {
        // Create instance with null values
        SegmentUpdateParam param = new SegmentUpdateParam();
        param.setContent("content");
        param.setEnabled(null)
                .setRegenerateChildChunks(null);

        // Verify null values
        assertNull(param.getEnabled());
        assertNull(param.getRegenerateChildChunks());

        // Serialization should handle null values
        try {
            String json = objectMapper.writeValueAsString(param);
            assertTrue(json.contains("\"content\":\"content\""));

            // Jackson typically omits null values in serialization by default
        } catch (JsonProcessingException e) {
            fail("Serialization should not throw an exception");
        }
    }

    /**
     * Test equality and hash code
     */
    @Test
    public void testEqualsAndHashCode() {
        // Create two identical params
        SegmentUpdateParam param1 = new SegmentUpdateParam();
        param1.setContent("Content")
                .setAnswer("Answer")
                .setKeywords(Arrays.asList("k1", "k2"));
        param1.setEnabled(true)
                .setRegenerateChildChunks(false);

        SegmentUpdateParam param2 = new SegmentUpdateParam();
        param2.setContent("Content")
                .setAnswer("Answer")
                .setKeywords(Arrays.asList("k1", "k2"));
        param2.setEnabled(true)
                .setRegenerateChildChunks(false);

        // Create a different param
        SegmentUpdateParam param3 = new SegmentUpdateParam();
        param3.setContent("Content")
                .setAnswer("Answer")
                .setKeywords(Arrays.asList("k1", "k2"));
        param3.setEnabled(false)  // Different enabled value
                .setRegenerateChildChunks(false);

        // Test equality
        assertEquals(param1, param2);
        assertNotEquals(param1, param3);

        // Test hash code
        assertEquals(param1.hashCode(), param2.hashCode());
        assertNotEquals(param1.hashCode(), param3.hashCode());
    }
}
