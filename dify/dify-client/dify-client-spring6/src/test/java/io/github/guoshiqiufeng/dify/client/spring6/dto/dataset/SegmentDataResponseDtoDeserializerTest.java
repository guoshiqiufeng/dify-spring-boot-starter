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
package io.github.guoshiqiufeng.dify.client.spring6.dto.dataset;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.guoshiqiufeng.dify.dataset.dto.response.SegmentData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SegmentDataResponseDtoDeserializer}.
 *
 * @author yanghq
 * @version 1.6.2
 * @since 2025/12/12 10:00
 */
class SegmentDataResponseDtoDeserializerTest {

    private SegmentDataResponseDtoDeserializer deserializer;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        deserializer = new SegmentDataResponseDtoDeserializer();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testDeserializeWithRootLevelData() throws IOException {
        // Given
        String json = "{" +
                "\"id\":\"segment123\"," +
                "\"document_id\":\"doc456\"," +
                "\"position\":1," +
                "\"content\":\"Test content\"," +
                "\"word_count\":10," +
                "\"tokens\":15," +
                "\"created_at\":1678886400" +
                "}";
        JsonParser parser = objectMapper.getFactory().createParser(json);

        // When
        SegmentDataResponseDto result = deserializer.deserialize(parser, null);

        // Then
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals("segment123", result.getData().getId());
        assertEquals("doc456", result.getData().getDocumentId());
        assertEquals(1, result.getData().getPosition());
        assertEquals("Test content", result.getData().getContent());
        assertEquals(10, result.getData().getWordCount());
        assertEquals(15, result.getData().getTokens());
        assertEquals(1678886400L, result.getData().getCreatedAt());
    }

    @Test
    void testDeserializeWithDataObject() throws IOException {
        // Given
        String json = "{" +
                "\"data\":{" +
                "\"id\":\"segment123\"," +
                "\"document_id\":\"doc456\"," +
                "\"position\":2," +
                "\"content\":\"Content in data object\"," +
                "\"word_count\":20," +
                "\"tokens\":25," +
                "\"created_at\":1678886500" +
                "}" +
                "}";
        JsonParser parser = objectMapper.getFactory().createParser(json);

        // When
        SegmentDataResponseDto result = deserializer.deserialize(parser, null);

        // Then
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals("segment123", result.getData().getId());
        assertEquals("doc456", result.getData().getDocumentId());
        assertEquals(2, result.getData().getPosition());
        assertEquals("Content in data object", result.getData().getContent());
        assertEquals(20, result.getData().getWordCount());
        assertEquals(25, result.getData().getTokens());
        assertEquals(1678886500L, result.getData().getCreatedAt());
    }

    @Test
    void testDeserializeWithNullDataObject() throws IOException {
        // Given
        String json = "{" +
                "\"data\":null" +
                "}";
        JsonParser parser = objectMapper.getFactory().createParser(json);

        // When
        SegmentDataResponseDto result = deserializer.deserialize(parser, null);

        // Then
        assertNotNull(result);
        assertNull(result.getData()); // Should create empty SegmentData object
    }

    @Test
    void testDeserializeWithEmptyJsonObject() throws IOException {
        // Given
        String json = "{}";
        JsonParser parser = objectMapper.getFactory().createParser(json);

        // When
        SegmentDataResponseDto result = deserializer.deserialize(parser, null);

        // Then
        assertNotNull(result);
        assertNotNull(result.getData()); // Should create empty SegmentData object
    }

    @Test
    void testDeserializeWithIncompleteSegmentData() throws IOException {
        // Given
        String json = "{" +
                "\"id\":\"segment123\"," +
                "\"content\":\"Partial content\"" +
                "}";
        JsonParser parser = objectMapper.getFactory().createParser(json);

        // When
        SegmentDataResponseDto result = deserializer.deserialize(parser, null);

        // Then
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals("segment123", result.getData().getId());
        assertEquals("Partial content", result.getData().getContent());
        // Other fields should be null/default values
        assertNull(result.getData().getDocumentId());
    }
}
