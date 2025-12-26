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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for AppInfoResponse
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/5/2
 */
public class AppInfoResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test default constructor
     */
    @Test
    public void testDefaultConstructor() {
        // Act
        AppInfoResponse response = new AppInfoResponse();

        // Assert
        assertNotNull(response);
        assertNull(response.getName());
        assertNull(response.getDescription());
        assertNull(response.getTags());
    }

    /**
     * Test getter and setter methods
     */
    @Test
    public void testGetterAndSetter() {
        // Arrange
        AppInfoResponse response = new AppInfoResponse();
        String name = "Test App";
        String description = "This is a test application";
        List<String> tags = Arrays.asList("test", "app", "example");

        // Act
        response.setName(name);
        response.setDescription(description);
        response.setTags(tags);

        // Assert
        assertEquals(name, response.getName());
        assertEquals(description, response.getDescription());
        assertEquals(tags, response.getTags());
    }

    /**
     * Test serialization with Jackson
     */
    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        // Create an instance with sample data
        AppInfoResponse response = new AppInfoResponse();
        response.setName("Test App");
        response.setDescription("This is a test application");
        response.setTags(Arrays.asList("test", "app", "example"));

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(response);

        // Verify JSON contains expected property names
        assertTrue(json.contains("\"name\":"));
        assertTrue(json.contains("\"description\":"));
        assertTrue(json.contains("\"tags\":"));

        // Verify tags array contains expected values
        assertTrue(json.contains("\"test\""));
        assertTrue(json.contains("\"app\""));
        assertTrue(json.contains("\"example\""));

        // Deserialize back to object
        AppInfoResponse deserialized = objectMapper.readValue(json, AppInfoResponse.class);

        // Verify the deserialized object matches the original
        assertEquals(response.getName(), deserialized.getName());
        assertEquals(response.getDescription(), deserialized.getDescription());
        assertEquals(response.getTags().size(), deserialized.getTags().size());
        assertTrue(deserialized.getTags().containsAll(response.getTags()));
    }

    /**
     * Test JSON deserialization
     */
    @Test
    public void testJsonDeserialization() throws JsonProcessingException {
        // Standard JSON format
        String jsonStandard = "{\n" +
                "  \"name\": \"Test App\",\n" +
                "  \"description\": \"This is a test application\",\n" +
                "  \"tags\": [\"test\", \"app\", \"example\"]\n" +
                "}";

        // Deserialize standard format
        AppInfoResponse deserialized = objectMapper.readValue(jsonStandard, AppInfoResponse.class);

        // Verify fields were correctly deserialized
        assertEquals("Test App", deserialized.getName());
        assertEquals("This is a test application", deserialized.getDescription());
        assertEquals(3, deserialized.getTags().size());
        assertTrue(deserialized.getTags().contains("test"));
        assertTrue(deserialized.getTags().contains("app"));
        assertTrue(deserialized.getTags().contains("example"));
    }

    /**
     * Test equality and hash code
     */
    @Test
    public void testEqualsAndHashCode() {
        // Create two identical objects
        AppInfoResponse response1 = new AppInfoResponse();
        response1.setName("Test App");
        response1.setDescription("This is a test application");
        response1.setTags(Arrays.asList("test", "app", "example"));

        AppInfoResponse response2 = new AppInfoResponse();
        response2.setName("Test App");
        response2.setDescription("This is a test application");
        response2.setTags(Arrays.asList("test", "app", "example"));

        // Create a different object
        AppInfoResponse response3 = new AppInfoResponse();
        response3.setName("Different App");
        response3.setDescription("This is a different application");
        response3.setTags(Arrays.asList("different", "tags"));

        // Test equality
        assertEquals(response1, response2);
        assertNotEquals(response1, response3);

        // Test hash code
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }
}
