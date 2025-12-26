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
package io.github.guoshiqiufeng.dify.dataset.dto.response.textembedding;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for TextEmbeddingModelProperties
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/5/2
 */
public class TextEmbeddingModelPropertiesTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test default constructor
     */
    @Test
    public void testDefaultConstructor() {
        // Act
        TextEmbeddingModelProperties properties = new TextEmbeddingModelProperties();

        // Assert
        assertNotNull(properties);
        assertNull(properties.getContextSize());
        assertNull(properties.getMaxChunks());
    }

    /**
     * Test all-args constructor
     */
    @Test
    public void testAllArgsConstructor() {
        // Arrange
        Integer contextSize = 4096;
        Integer maxChunks = 10;

        // Act
        TextEmbeddingModelProperties properties = new TextEmbeddingModelProperties(contextSize, maxChunks);

        // Assert
        assertEquals(contextSize, properties.getContextSize());
        assertEquals(maxChunks, properties.getMaxChunks());
    }

    /**
     * Test getter and setter methods
     */
    @Test
    public void testGetterAndSetter() {
        // Arrange
        TextEmbeddingModelProperties properties = new TextEmbeddingModelProperties();
        Integer contextSize = 4096;
        Integer maxChunks = 10;

        // Act
        properties.setContextSize(contextSize);
        properties.setMaxChunks(maxChunks);

        // Assert
        assertEquals(contextSize, properties.getContextSize());
        assertEquals(maxChunks, properties.getMaxChunks());
    }

    /**
     * Test serialization with Jackson
     */
    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        // Create an instance with sample data
        TextEmbeddingModelProperties properties = new TextEmbeddingModelProperties(4096, 10);

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(properties);

        // Verify JSON contains expected property names
        assertTrue(json.contains("\"contextSize\":"));
        assertTrue(json.contains("\"maxChunks\":"));

        // Deserialize back to object
        TextEmbeddingModelProperties deserialized = objectMapper.readValue(json, TextEmbeddingModelProperties.class);

        // Verify the deserialized object matches the original
        assertEquals(properties.getContextSize(), deserialized.getContextSize());
        assertEquals(properties.getMaxChunks(), deserialized.getMaxChunks());
    }

    /**
     * Test JSON deserialization with aliases
     */
    @Test
    public void testJsonDeserialization() throws JsonProcessingException {
        // JSON with aliases
        String jsonWithAliases = "{\n" +
                "  \"context_size\": 4096,\n" +
                "  \"max_chunks\": 10\n" +
                "}";

        // Deserialize with aliases
        TextEmbeddingModelProperties deserialized = objectMapper.readValue(jsonWithAliases, TextEmbeddingModelProperties.class);

        // Verify fields were correctly deserialized
        assertEquals(Integer.valueOf(4096), deserialized.getContextSize());
        assertEquals(Integer.valueOf(10), deserialized.getMaxChunks());
    }

    /**
     * Test equality and hash code
     */
    @Test
    public void testEqualsAndHashCode() {
        // Create two identical objects
        TextEmbeddingModelProperties properties1 = new TextEmbeddingModelProperties(4096, 10);
        TextEmbeddingModelProperties properties2 = new TextEmbeddingModelProperties(4096, 10);

        // Create a different object
        TextEmbeddingModelProperties properties3 = new TextEmbeddingModelProperties(8192, 20);

        // Test equality
        assertEquals(properties1, properties2);
        assertNotEquals(properties1, properties3);

        // Test hash code
        assertEquals(properties1.hashCode(), properties2.hashCode());
        assertNotEquals(properties1.hashCode(), properties3.hashCode());
    }
}
