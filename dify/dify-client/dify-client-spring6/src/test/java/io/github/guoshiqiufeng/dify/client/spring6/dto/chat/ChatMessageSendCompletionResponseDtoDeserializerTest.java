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
package io.github.guoshiqiufeng.dify.client.spring6.dto.chat;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test class for {@link ChatMessageSendCompletionResponseDtoDeserializer}.
 *
 * @author yanghq
 * @version 1.6.2
 * @since 2025/12/12 10:00
 */
class ChatMessageSendCompletionResponseDtoDeserializerTest {

    private ChatMessageSendCompletionResponseDtoDeserializer deserializer;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        deserializer = new ChatMessageSendCompletionResponseDtoDeserializer();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testDeserializeWithoutEventField() throws IOException {
        // Given
        String json = "{\"task_id\":\"task123\",\"message\":\"test message\",\"code\":\"200\"}";

        JsonFactory factory = new JsonFactory();
        StringWriter writer = new StringWriter();
        try (JsonGenerator generator = factory.createGenerator(writer)) {
            generator.writeRaw(json);
        }

        JsonParser parser = factory.createParser(json);

        // When
        ChatMessageSendCompletionResponseDto result = deserializer.deserialize(parser, null);

        // Then
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals("task123", result.getData().getTaskId());
        assertEquals("test message", result.getData().getMessage());
        assertEquals("200", result.getData().getCode());
    }

    @Test
    void testDeserializeWithInvalidEvent() throws IOException {
        // Given
        String json = "{\"event\":\"INVALID_EVENT\",\"task_id\":\"task123\"}";
        JsonParser parser = objectMapper.getFactory().createParser(json);

        // When
        ChatMessageSendCompletionResponseDto result = deserializer.deserialize(parser, null);

        // Then
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals("task123", result.getData().getTaskId());
    }

    @Test
    void testDeserializeWithPositionField() throws IOException {
        // Given
        String json = "{" +
                "\"position\":1," +
                "\"thought\":\"thinking...\"," +
                "\"task_id\":\"task123\"" +
                "}";
        JsonParser parser = objectMapper.getFactory().createParser(json);

        // When
        ChatMessageSendCompletionResponseDto result = deserializer.deserialize(parser, null);

        // Then
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getPosition());
        assertEquals("thinking...", result.getData().getThought());
        assertEquals("task123", result.getData().getTaskId());
    }

    @Test
    void testDeserializeWithToolFields() throws IOException {
        // Given
        String json = "{" +
                "\"tool\":\"calculator\"," +
                "\"tool_labels\":{\"key\":\"value\"}," +
                "\"tool_input\":\"{\\\"operation\\\": \\\"add\\\", \\\"x\\\": 1, \\\"y\\\": 2}\"," +
                "\"message_files\":[\"file1.txt\",\"file2.txt\"]," +
                "\"task_id\":\"task123\"" +
                "}";
        JsonParser parser = objectMapper.getFactory().createParser(json);

        // When
        ChatMessageSendCompletionResponseDto result = deserializer.deserialize(parser, null);

        // Then
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals("calculator", result.getData().getTool());
        assertEquals(Collections.singletonMap("key", "value"), result.getData().getToolLabels());
        assertEquals("{\"operation\": \"add\", \"x\": 1, \"y\": 2}", result.getData().getToolInput());
        assertEquals(2, result.getData().getMessageFiles().size());
        assertEquals("file1.txt", result.getData().getMessageFiles().get(0));
        assertEquals("file2.txt", result.getData().getMessageFiles().get(1));
        assertEquals("task123", result.getData().getTaskId());
    }

    @Test
    void testDeserializeWithStatusFields() throws IOException {
        // Given
        String json = "{" +
                "\"task_id\":\"task123\"," +
                "\"status\":200," +
                "\"code\":\"SUCCESS\"," +
                "\"message\":\"Operation succeeded\"" +
                "}";
        JsonParser parser = objectMapper.getFactory().createParser(json);

        // When
        ChatMessageSendCompletionResponseDto result = deserializer.deserialize(parser, null);

        // Then
        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals("task123", result.getData().getTaskId());
        assertEquals(200, result.getData().getStatus());
        assertEquals("SUCCESS", result.getData().getCode());
        assertEquals("Operation succeeded", result.getData().getMessage());
    }
}