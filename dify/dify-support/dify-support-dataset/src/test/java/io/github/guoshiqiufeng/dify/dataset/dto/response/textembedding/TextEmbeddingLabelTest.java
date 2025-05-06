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
package io.github.guoshiqiufeng.dify.dataset.dto.response.textembedding;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for TextEmbeddingLabel
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/5/2
 */
public class TextEmbeddingLabelTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test default constructor
     */
    @Test
    public void testDefaultConstructor() {
        // Act
        TextEmbeddingLabel label = new TextEmbeddingLabel();

        // Assert
        assertNotNull(label);
        assertNull(label.getZhHans());
        assertNull(label.getEnUs());
    }

    /**
     * Test all-args constructor
     */
    @Test
    public void testAllArgsConstructor() {
        // Arrange
        String zhHans = "中文标签";
        String enUs = "English Label";

        // Act
        TextEmbeddingLabel label = new TextEmbeddingLabel(zhHans, enUs);

        // Assert
        assertEquals(zhHans, label.getZhHans());
        assertEquals(enUs, label.getEnUs());
    }

    /**
     * Test getter and setter methods
     */
    @Test
    public void testGetterAndSetter() {
        // Arrange
        TextEmbeddingLabel label = new TextEmbeddingLabel();
        String zhHans = "中文标签";
        String enUs = "English Label";

        // Act
        label.setZhHans(zhHans);
        label.setEnUs(enUs);

        // Assert
        assertEquals(zhHans, label.getZhHans());
        assertEquals(enUs, label.getEnUs());
    }

    /**
     * Test serialization with Jackson
     */
    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        // Create an instance with sample data
        TextEmbeddingLabel label = new TextEmbeddingLabel("中文标签", "English Label");

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(label);

        // Verify JSON contains expected property names
        assertTrue(json.contains("\"zhHans\":"));
        assertTrue(json.contains("\"enUs\":"));

        // Deserialize back to object
        TextEmbeddingLabel deserialized = objectMapper.readValue(json, TextEmbeddingLabel.class);

        // Verify the deserialized object matches the original
        assertEquals(label.getZhHans(), deserialized.getZhHans());
        assertEquals(label.getEnUs(), deserialized.getEnUs());
    }

    /**
     * Test JSON deserialization with aliases
     */
    @Test
    public void testJsonDeserialization() throws JsonProcessingException {
        // JSON with aliases
        String jsonWithAliases = "{\n" +
                "  \"zh_Hans\": \"中文标签\",\n" +
                "  \"en_US\": \"English Label\"\n" +
                "}";

        // Deserialize with aliases
        TextEmbeddingLabel deserialized = objectMapper.readValue(jsonWithAliases, TextEmbeddingLabel.class);

        // Verify fields were correctly deserialized
        assertEquals("中文标签", deserialized.getZhHans());
        assertEquals("English Label", deserialized.getEnUs());
    }

    /**
     * Test equality and hash code
     */
    @Test
    public void testEqualsAndHashCode() {
        // Create two identical objects
        TextEmbeddingLabel label1 = new TextEmbeddingLabel("中文标签", "English Label");
        TextEmbeddingLabel label2 = new TextEmbeddingLabel("中文标签", "English Label");

        // Create a different object
        TextEmbeddingLabel label3 = new TextEmbeddingLabel("不同标签", "Different Label");

        // Test equality
        assertEquals(label1, label2);
        assertNotEquals(label1, label3);

        // Test hash code
        assertEquals(label1.hashCode(), label2.hashCode());
        assertNotEquals(label1.hashCode(), label3.hashCode());
    }
}
