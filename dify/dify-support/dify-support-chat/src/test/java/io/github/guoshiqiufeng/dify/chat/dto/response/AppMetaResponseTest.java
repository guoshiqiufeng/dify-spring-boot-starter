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
 * Test class for AppMetaResponse
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/5/2
 */
public class AppMetaResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test default constructor
     */
    @Test
    public void testDefaultConstructor() {
        // Act
        AppMetaResponse response = new AppMetaResponse();

        // Assert
        assertNotNull(response);
        assertNull(response.getToolIcons());
    }

    /**
     * Test getter and setter methods
     */
    @Test
    public void testGetterAndSetter() {
        // Arrange
        AppMetaResponse response = new AppMetaResponse();
        Map<String, Object> toolIcons = new HashMap<>();
        toolIcons.put("search", "search_icon_path");
        toolIcons.put("calculator", "calculator_icon_path");

        // Act
        response.setToolIcons(toolIcons);

        // Assert
        assertEquals(toolIcons, response.getToolIcons());
        assertEquals(2, response.getToolIcons().size());
        assertEquals("search_icon_path", response.getToolIcons().get("search"));
        assertEquals("calculator_icon_path", response.getToolIcons().get("calculator"));
    }

    /**
     * Test serialization with Jackson
     */
    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        // Create an instance with sample data
        AppMetaResponse response = new AppMetaResponse();
        Map<String, Object> toolIcons = new HashMap<>();
        toolIcons.put("search", "search_icon_path");
        toolIcons.put("calculator", "calculator_icon_path");
        response.setToolIcons(toolIcons);

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(response);

        // Verify JSON contains expected property names
        assertTrue(json.contains("\"toolIcons\":"));
        assertTrue(json.contains("\"search\":"));
        assertTrue(json.contains("\"calculator\":"));
        assertTrue(json.contains("\"search_icon_path\""));
        assertTrue(json.contains("\"calculator_icon_path\""));

        // Deserialize back to object
        AppMetaResponse deserialized = objectMapper.readValue(json, AppMetaResponse.class);

        // Verify the deserialized object matches the original
        assertEquals(response.getToolIcons().size(), deserialized.getToolIcons().size());
        assertEquals(response.getToolIcons().get("search"), deserialized.getToolIcons().get("search"));
        assertEquals(response.getToolIcons().get("calculator"), deserialized.getToolIcons().get("calculator"));
    }

    /**
     * Test JSON deserialization with aliases
     */
    @Test
    public void testJsonDeserialization() throws JsonProcessingException {
        // JSON with aliases
        String jsonWithAliases = "{\n" +
                "  \"tool_icons\": {\n" +
                "    \"search\": \"search_icon_path\",\n" +
                "    \"calculator\": \"calculator_icon_path\"\n" +
                "  }\n" +
                "}";

        // Deserialize with aliases
        AppMetaResponse deserialized = objectMapper.readValue(jsonWithAliases, AppMetaResponse.class);

        // Verify fields were correctly deserialized
        assertNotNull(deserialized.getToolIcons());
        assertEquals(2, deserialized.getToolIcons().size());
        assertEquals("search_icon_path", deserialized.getToolIcons().get("search"));
        assertEquals("calculator_icon_path", deserialized.getToolIcons().get("calculator"));
    }

    /**
     * Test equality and hash code
     */
    @Test
    public void testEqualsAndHashCode() {
        // Create two identical objects
        AppMetaResponse response1 = new AppMetaResponse();
        Map<String, Object> toolIcons1 = new HashMap<>();
        toolIcons1.put("search", "search_icon_path");
        toolIcons1.put("calculator", "calculator_icon_path");
        response1.setToolIcons(toolIcons1);

        AppMetaResponse response2 = new AppMetaResponse();
        Map<String, Object> toolIcons2 = new HashMap<>();
        toolIcons2.put("search", "search_icon_path");
        toolIcons2.put("calculator", "calculator_icon_path");
        response2.setToolIcons(toolIcons2);

        // Create a different object
        AppMetaResponse response3 = new AppMetaResponse();
        Map<String, Object> toolIcons3 = new HashMap<>();
        toolIcons3.put("different", "different_icon_path");
        response3.setToolIcons(toolIcons3);

        // Test equality
        assertEquals(response1, response2);
        assertNotEquals(response1, response3);

        // Test hash code
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }

    /**
     * Test with nested objects in the map
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testNestedObjects() throws JsonProcessingException {
        // Create an instance with nested objects in the map
        AppMetaResponse response = new AppMetaResponse();
        Map<String, Object> toolIcons = new HashMap<>();

        Map<String, String> searchIcon = new HashMap<>();
        searchIcon.put("small", "small_search_icon_path");
        searchIcon.put("large", "large_search_icon_path");

        toolIcons.put("search", searchIcon);
        response.setToolIcons(toolIcons);

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(response);

        // Verify JSON contains expected nested structure
        assertTrue(json.contains("\"toolIcons\":"));
        assertTrue(json.contains("\"search\":"));
        assertTrue(json.contains("\"small\":"));
        assertTrue(json.contains("\"large\":"));

        // Deserialize back to object
        AppMetaResponse deserialized = objectMapper.readValue(json, AppMetaResponse.class);

        // Verify the deserialized object contains the nested structure
        Map<String, Object> deserializedSearchIcon = (Map<String, Object>) deserialized.getToolIcons().get("search");
        assertEquals("small_search_icon_path", deserializedSearchIcon.get("small"));
        assertEquals("large_search_icon_path", deserializedSearchIcon.get("large"));
    }
}
