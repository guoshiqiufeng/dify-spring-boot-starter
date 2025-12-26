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
package io.github.guoshiqiufeng.dify.chat.dto.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for MessageConversationsResponse
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/5/2
 */
public class MessageConversationsResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test default constructor
     */
    @Test
    public void testDefaultConstructor() {
        // Act
        MessageConversationsResponse response = new MessageConversationsResponse();

        // Assert
        assertNotNull(response);
        assertNull(response.getId());
        assertNull(response.getName());
        assertNull(response.getInputs());
        assertNull(response.getStatus());
        assertNull(response.getIntroduction());
        assertNull(response.getCreatedAt());
        assertNull(response.getUpdatedAt());
    }

    /**
     * Test getter and setter methods
     */
    @Test
    public void testGetterAndSetter() {
        // Arrange
        MessageConversationsResponse response = new MessageConversationsResponse();
        String id = "conv-12345";
        String name = "Test Conversation";
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("query", "What is AI?");
        inputs.put("options", "detailed");
        String status = "active";
        String introduction = "Welcome to the conversation!";
        Long createdAt = 1651234567890L;
        Long updatedAt = 1651234567999L;

        // Act
        response.setId(id);
        response.setName(name);
        response.setInputs(inputs);
        response.setStatus(status);
        response.setIntroduction(introduction);
        response.setCreatedAt(createdAt);
        response.setUpdatedAt(updatedAt);

        // Assert
        assertEquals(id, response.getId());
        assertEquals(name, response.getName());
        assertEquals(inputs, response.getInputs());
        assertEquals(status, response.getStatus());
        assertEquals(introduction, response.getIntroduction());
        assertEquals(createdAt, response.getCreatedAt());
        assertEquals(updatedAt, response.getUpdatedAt());
    }

    /**
     * Test serialization with Jackson
     */
    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        // Create an instance with sample data
        MessageConversationsResponse response = new MessageConversationsResponse();
        response.setId("conv-12345");
        response.setName("Test Conversation");
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("query", "What is AI?");
        inputs.put("options", "detailed");
        response.setInputs(inputs);
        response.setStatus("active");
        response.setIntroduction("Welcome to the conversation!");
        response.setCreatedAt(1651234567890L);
        response.setUpdatedAt(1651234567999L);

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(response);

        // Verify JSON contains expected property names
        assertTrue(json.contains("\"id\":"));
        assertTrue(json.contains("\"name\":"));
        assertTrue(json.contains("\"inputs\":"));
        assertTrue(json.contains("\"status\":"));
        assertTrue(json.contains("\"introduction\":"));
        assertTrue(json.contains("\"createdAt\":"));
        assertTrue(json.contains("\"updatedAt\":"));

        // Verify inputs object has expected keys
        assertTrue(json.contains("\"query\":"));
        assertTrue(json.contains("\"options\":"));

        // Deserialize back to object
        MessageConversationsResponse deserialized = objectMapper.readValue(json, MessageConversationsResponse.class);

        // Verify the deserialized object matches the original
        assertEquals(response.getId(), deserialized.getId());
        assertEquals(response.getName(), deserialized.getName());
        assertEquals(response.getStatus(), deserialized.getStatus());
        assertEquals(response.getIntroduction(), deserialized.getIntroduction());
        assertEquals(response.getCreatedAt(), deserialized.getCreatedAt());
        assertEquals(response.getUpdatedAt(), deserialized.getUpdatedAt());

        // Verify inputs map
        assertEquals(response.getInputs().size(), deserialized.getInputs().size());
        assertEquals(response.getInputs().get("query"), deserialized.getInputs().get("query"));
        assertEquals(response.getInputs().get("options"), deserialized.getInputs().get("options"));
    }

    /**
     * Test JSON deserialization with aliases
     */
    @Test
    public void testJsonDeserialization() throws JsonProcessingException {
        // JSON with aliases
        String jsonWithAliases = "{\n" +
                "  \"id\": \"conv-12345\",\n" +
                "  \"name\": \"Test Conversation\",\n" +
                "  \"inputs\": {\n" +
                "    \"query\": \"What is AI?\",\n" +
                "    \"options\": \"detailed\"\n" +
                "  },\n" +
                "  \"status\": \"active\",\n" +
                "  \"introduction\": \"Welcome to the conversation!\",\n" +
                "  \"created_at\": 1651234567890,\n" +
                "  \"updated_at\": 1651234567999\n" +
                "}";

        // Deserialize with aliases
        MessageConversationsResponse deserialized = objectMapper.readValue(jsonWithAliases, MessageConversationsResponse.class);

        // Verify fields were correctly deserialized
        assertEquals("conv-12345", deserialized.getId());
        assertEquals("Test Conversation", deserialized.getName());
        assertEquals("active", deserialized.getStatus());
        assertEquals("Welcome to the conversation!", deserialized.getIntroduction());
        assertEquals(Long.valueOf(1651234567890L), deserialized.getCreatedAt());
        assertEquals(Long.valueOf(1651234567999L), deserialized.getUpdatedAt());

        // Verify inputs map
        assertNotNull(deserialized.getInputs());
        assertEquals(2, deserialized.getInputs().size());
        assertEquals("What is AI?", deserialized.getInputs().get("query"));
        assertEquals("detailed", deserialized.getInputs().get("options"));
    }

    /**
     * Test equality and hash code
     */
    @Test
    public void testEqualsAndHashCode() {
        // Create two identical objects
        MessageConversationsResponse response1 = new MessageConversationsResponse();
        response1.setId("conv-12345");
        response1.setName("Test Conversation");
        Map<String, Object> inputs1 = new HashMap<>();
        inputs1.put("query", "What is AI?");
        response1.setInputs(inputs1);
        response1.setStatus("active");
        response1.setIntroduction("Welcome to the conversation!");
        response1.setCreatedAt(1651234567890L);
        response1.setUpdatedAt(1651234567999L);

        MessageConversationsResponse response2 = new MessageConversationsResponse();
        response2.setId("conv-12345");
        response2.setName("Test Conversation");
        Map<String, Object> inputs2 = new HashMap<>();
        inputs2.put("query", "What is AI?");
        response2.setInputs(inputs2);
        response2.setStatus("active");
        response2.setIntroduction("Welcome to the conversation!");
        response2.setCreatedAt(1651234567890L);
        response2.setUpdatedAt(1651234567999L);

        // Create a different object
        MessageConversationsResponse response3 = new MessageConversationsResponse();
        response3.setId("conv-67890");
        response3.setName("Different Conversation");
        Map<String, Object> inputs3 = new HashMap<>();
        inputs3.put("query", "Different query");
        response3.setInputs(inputs3);
        response3.setStatus("completed");
        response3.setIntroduction("Different introduction");
        response3.setCreatedAt(1650123456789L);
        response3.setUpdatedAt(1650123456999L);

        // Test equality
        assertEquals(response1, response2);
        assertNotEquals(response1, response3);

        // Test hash code
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }

    /**
     * Test complex inputs object
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testComplexInputs() throws JsonProcessingException {
        // Create an instance with nested objects in inputs
        MessageConversationsResponse response = new MessageConversationsResponse();
        response.setId("conv-12345");

        Map<String, Object> inputs = new HashMap<>();
        Map<String, Object> options = new HashMap<>();
        options.put("temperature", 0.7);
        options.put("max_tokens", 1000);

        inputs.put("query", "What is AI?");
        inputs.put("options", options);

        response.setInputs(inputs);

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(response);

        // Verify JSON contains expected nested structure
        assertTrue(json.contains("\"inputs\":"));
        assertTrue(json.contains("\"query\":"));
        assertTrue(json.contains("\"options\":"));
        assertTrue(json.contains("\"temperature\":"));
        assertTrue(json.contains("\"max_tokens\":"));

        // Deserialize back to object
        MessageConversationsResponse deserialized = objectMapper.readValue(json, MessageConversationsResponse.class);

        // Verify the deserialized object contains the nested structure
        assertEquals("What is AI?", deserialized.getInputs().get("query"));

        Map<String, Object> deserializedOptions = (Map<String, Object>) deserialized.getInputs().get("options");
        assertEquals(0.7, deserializedOptions.get("temperature"));
        assertEquals(1000, deserializedOptions.get("max_tokens"));
    }
}
