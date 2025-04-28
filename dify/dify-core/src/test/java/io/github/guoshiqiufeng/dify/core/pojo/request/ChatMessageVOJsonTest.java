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
package io.github.guoshiqiufeng.dify.core.pojo.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.guoshiqiufeng.dify.core.enums.ResponseModeEnum;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link ChatMessageVO} JSON serialization/deserialization
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/4/28
 */
public class ChatMessageVOJsonTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testJsonSerializationWithJsonProperty() throws Exception {
        // Arrange
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("name", "John Doe");
        inputs.put("age", 25);

        ChatMessageVO chatMessage = new ChatMessageVO()
                .setInputs(inputs)
                .setQuery("How are you today?")
                .setResponseMode(ResponseModeEnum.blocking)
                .setConversationId("conv-123456")
                .setUser("user-789");

        // Act
        String json = objectMapper.writeValueAsString(chatMessage);

        // Assert - Check that the JSON uses the @JsonProperty field names
        assertTrue(json.contains("\"inputs\":{"));
        assertTrue(json.contains("\"query\":\"How are you today?\""));
        assertTrue(json.contains("\"response_mode\":\"blocking\""));  // Converted to snake_case due to @JsonProperty
        assertTrue(json.contains("\"conversation_id\":\"conv-123456\"")); // Converted to snake_case due to @JsonProperty
        assertTrue(json.contains("\"user\":\"user-789\""));
    }

    @Test
    public void testJsonDeserializationWithJsonProperty() throws Exception {
        // Arrange - JSON with snake_case field names (as used in @JsonProperty annotations)
        String json = "{\n" +
                "  \"inputs\": {\"name\": \"John Doe\", \"age\": 25},\n" +
                "  \"query\": \"How are you today?\",\n" +
                "  \"response_mode\": \"blocking\",\n" +
                "  \"conversation_id\": \"conv-123456\",\n" +
                "  \"user\": \"user-789\"\n" +
                "}";

        // Act
        ChatMessageVO chatMessage = objectMapper.readValue(json, ChatMessageVO.class);

        // Assert
        assertNotNull(chatMessage.getInputs());
        assertEquals("John Doe", chatMessage.getInputs().get("name"));
        assertEquals(25, chatMessage.getInputs().get("age"));
        assertEquals("How are you today?", chatMessage.getQuery());
        assertEquals(ResponseModeEnum.blocking, chatMessage.getResponseMode());
        assertEquals("conv-123456", chatMessage.getConversationId());
        assertEquals("user-789", chatMessage.getUser());
    }

    @Test
    public void testJsonDeserializationWithCamelCaseFieldNames() throws Exception {
        // Arrange - JSON with camelCase field names (not as in @JsonProperty annotations)
        String json = "{\n" +
                "  \"inputs\": {\"name\": \"John Doe\", \"age\": 25},\n" +
                "  \"query\": \"How are you today?\",\n" +
                "  \"responseMode\": \"blocking\",\n" +
                "  \"conversationId\": \"conv-123456\",\n" +
                "  \"user\": \"user-789\"\n" +
                "}";

        // Act
        ChatMessageVO chatMessage = objectMapper.readValue(json, ChatMessageVO.class);

        // Assert - Fields with @JsonProperty annotations should not be deserialized with camelCase names
        assertNotNull(chatMessage.getInputs());
        assertEquals("John Doe", chatMessage.getInputs().get("name"));
        assertEquals(25, chatMessage.getInputs().get("age"));
        assertEquals("How are you today?", chatMessage.getQuery());
        assertEquals(ResponseModeEnum.blocking, chatMessage.getResponseMode());
        assertEquals("conv-123456", chatMessage.getConversationId());
        assertEquals("user-789", chatMessage.getUser());
    }

    @Test
    public void testJsonSerializationWithChatMessageFile() throws Exception {
        // Arrange
        List<ChatMessageVO.ChatMessageFile> files = new ArrayList<>();
        ChatMessageVO.ChatMessageFile file = new ChatMessageVO.ChatMessageFile();
        file.setType("image");
        file.setTransferMethod("remote_url");
        file.setUrl("https://example.com/image.jpg");
        files.add(file);

        ChatMessageVO chatMessage = new ChatMessageVO()
                .setQuery("How are you today?")
                .setFiles(files);

        // Act
        String json = objectMapper.writeValueAsString(chatMessage);

        // Assert
        assertTrue(json.contains("\"files\":[{"));
        assertTrue(json.contains("\"type\":\"image\""));
        assertTrue(json.contains("\"transfer_method\":\"remote_url\"")); // Should use snake_case field name
        assertTrue(json.contains("\"url\":\"https://example.com/image.jpg\""));
    }

    @Test
    public void testJsonDeserializationWithChatMessageFile() throws Exception {
        // Arrange
        String json = "{\n" +
                "  \"query\": \"How are you today?\",\n" +
                "  \"files\": [\n" +
                "    {\n" +
                "      \"type\": \"image\",\n" +
                "      \"transfer_method\": \"remote_url\",\n" +
                "      \"url\": \"https://example.com/image.jpg\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        // Act
        ChatMessageVO chatMessage = objectMapper.readValue(json, ChatMessageVO.class);

        // Assert
        assertEquals("How are you today?", chatMessage.getQuery());
        assertNotNull(chatMessage.getFiles());
        assertEquals(1, chatMessage.getFiles().size());
        assertEquals("image", chatMessage.getFiles().get(0).getType());
        assertEquals("remote_url", chatMessage.getFiles().get(0).getTransferMethod());
        assertEquals("https://example.com/image.jpg", chatMessage.getFiles().get(0).getUrl());
    }

    @Test
    public void testJsonDeserializationWithMultipleFiles() throws Exception {
        // Arrange
        String json = "{\n" +
                "  \"query\": \"How are you today?\",\n" +
                "  \"files\": [\n" +
                "    {\n" +
                "      \"type\": \"image\",\n" +
                "      \"transfer_method\": \"remote_url\",\n" +
                "      \"url\": \"https://example.com/image1.jpg\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"type\": \"document\",\n" +
                "      \"transfer_method\": \"remote_url\",\n" +
                "      \"url\": \"https://example.com/document.pdf\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        // Act
        ChatMessageVO chatMessage = objectMapper.readValue(json, ChatMessageVO.class);

        // Assert
        assertEquals("How are you today?", chatMessage.getQuery());
        assertNotNull(chatMessage.getFiles());
        assertEquals(2, chatMessage.getFiles().size());

        assertEquals("image", chatMessage.getFiles().get(0).getType());
        assertEquals("remote_url", chatMessage.getFiles().get(0).getTransferMethod());
        assertEquals("https://example.com/image1.jpg", chatMessage.getFiles().get(0).getUrl());

        assertEquals("document", chatMessage.getFiles().get(1).getType());
        assertEquals("remote_url", chatMessage.getFiles().get(1).getTransferMethod());
        assertEquals("https://example.com/document.pdf", chatMessage.getFiles().get(1).getUrl());
    }

    @Test
    public void testJsonDeserializationWithDefaultFileValues() throws Exception {
        // Arrange - JSON with only url for file, should use default values for type and transfer_method
        String json = "{\n" +
                "  \"query\": \"How are you today?\",\n" +
                "  \"files\": [\n" +
                "    {\n" +
                "      \"url\": \"https://example.com/image.jpg\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        // Act
        ChatMessageVO chatMessage = objectMapper.readValue(json, ChatMessageVO.class);

        // Assert
        assertEquals("How are you today?", chatMessage.getQuery());
        assertNotNull(chatMessage.getFiles());
        assertEquals(1, chatMessage.getFiles().size());
        assertEquals("image", chatMessage.getFiles().get(0).getType()); // Default value
        assertEquals("remote_url", chatMessage.getFiles().get(0).getTransferMethod()); // Default value
        assertEquals("https://example.com/image.jpg", chatMessage.getFiles().get(0).getUrl());
    }

    @Test
    public void testFullCycleSerialization() throws Exception {
        // Arrange
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("name", "John Doe");
        inputs.put("age", 25);

        List<ChatMessageVO.ChatMessageFile> files = new ArrayList<>();
        ChatMessageVO.ChatMessageFile file = new ChatMessageVO.ChatMessageFile();
        file.setType("image");
        file.setTransferMethod("remote_url");
        file.setUrl("https://example.com/image.jpg");
        files.add(file);

        ChatMessageVO originalMessage = new ChatMessageVO()
                .setInputs(inputs)
                .setQuery("How are you today?")
                .setResponseMode(ResponseModeEnum.blocking)
                .setConversationId("conv-123456")
                .setUser("user-789")
                .setFiles(files);

        // Act - Serialize then deserialize
        String json = objectMapper.writeValueAsString(originalMessage);
        ChatMessageVO deserializedMessage = objectMapper.readValue(json, ChatMessageVO.class);

        // Assert
        assertEquals(originalMessage.getQuery(), deserializedMessage.getQuery());
        assertEquals(originalMessage.getResponseMode(), deserializedMessage.getResponseMode());
        assertEquals(originalMessage.getConversationId(), deserializedMessage.getConversationId());
        assertEquals(originalMessage.getUser(), deserializedMessage.getUser());

        assertEquals("John Doe", deserializedMessage.getInputs().get("name"));
        assertEquals(25, deserializedMessage.getInputs().get("age"));

        assertEquals(1, deserializedMessage.getFiles().size());
        assertEquals(originalMessage.getFiles().get(0).getType(), deserializedMessage.getFiles().get(0).getType());
        assertEquals(originalMessage.getFiles().get(0).getTransferMethod(), deserializedMessage.getFiles().get(0).getTransferMethod());
        assertEquals(originalMessage.getFiles().get(0).getUrl(), deserializedMessage.getFiles().get(0).getUrl());
    }
}
