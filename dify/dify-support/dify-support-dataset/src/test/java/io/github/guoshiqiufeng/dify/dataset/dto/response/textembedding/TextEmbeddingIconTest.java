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
 * Test class for TextEmbeddingIcon
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/5/2
 */
public class TextEmbeddingIconTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test default constructor
     */
    @Test
    public void testDefaultConstructor() {
        // Act
        TextEmbeddingIcon icon = new TextEmbeddingIcon();

        // Assert
        assertNotNull(icon);
        assertNull(icon.getZhHans());
        assertNull(icon.getEnUs());
    }

    /**
     * Test all-args constructor
     */
    @Test
    public void testAllArgsConstructor() {
        // Arrange
        String zhHans = "中文图标路径";
        String enUs = "english_icon_path";

        // Act
        TextEmbeddingIcon icon = new TextEmbeddingIcon(zhHans, enUs);

        // Assert
        assertEquals(zhHans, icon.getZhHans());
        assertEquals(enUs, icon.getEnUs());
    }

    /**
     * Test getter and setter methods
     */
    @Test
    public void testGetterAndSetter() {
        // Arrange
        TextEmbeddingIcon icon = new TextEmbeddingIcon();
        String zhHans = "中文图标路径";
        String enUs = "english_icon_path";

        // Act
        icon.setZhHans(zhHans);
        icon.setEnUs(enUs);

        // Assert
        assertEquals(zhHans, icon.getZhHans());
        assertEquals(enUs, icon.getEnUs());
    }

    /**
     * Test serialization with Jackson
     */
    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        // Create an instance with sample data
        TextEmbeddingIcon icon = new TextEmbeddingIcon("中文图标路径", "english_icon_path");

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(icon);

        // Verify JSON contains expected property names
        assertTrue(json.contains("\"zhHans\":"));
        assertTrue(json.contains("\"enUs\":"));

        // Deserialize back to object
        TextEmbeddingIcon deserialized = objectMapper.readValue(json, TextEmbeddingIcon.class);

        // Verify the deserialized object matches the original
        assertEquals(icon.getZhHans(), deserialized.getZhHans());
        assertEquals(icon.getEnUs(), deserialized.getEnUs());
    }

    /**
     * Test JSON deserialization with aliases
     */
    @Test
    public void testJsonDeserialization() throws JsonProcessingException {
        // JSON with aliases
        String jsonWithAliases = "{\n" +
                "  \"zh_Hans\": \"中文图标路径\",\n" +
                "  \"en_US\": \"english_icon_path\"\n" +
                "}";

        // Deserialize with aliases
        TextEmbeddingIcon deserialized = objectMapper.readValue(jsonWithAliases, TextEmbeddingIcon.class);

        // Verify fields were correctly deserialized
        assertEquals("中文图标路径", deserialized.getZhHans());
        assertEquals("english_icon_path", deserialized.getEnUs());
    }

    /**
     * Test equality and hash code
     */
    @Test
    public void testEqualsAndHashCode() {
        // Create two identical objects
        TextEmbeddingIcon icon1 = new TextEmbeddingIcon("中文图标路径", "english_icon_path");
        TextEmbeddingIcon icon2 = new TextEmbeddingIcon("中文图标路径", "english_icon_path");

        // Create a different object
        TextEmbeddingIcon icon3 = new TextEmbeddingIcon("不同路径", "different_path");

        // Test equality
        assertEquals(icon1, icon2);
        assertNotEquals(icon1, icon3);

        // Test hash code
        assertEquals(icon1.hashCode(), icon2.hashCode());
        assertNotEquals(icon1.hashCode(), icon3.hashCode());
    }
}
