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
package io.github.guoshiqiufeng.dify.core.pojo.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test class for MessagesResponseVO
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/26
 */
public class MessagesResponseVOTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test basic getter and setter methods
     */
    @Test
    public void testGetterAndSetter() {
        // Create a MessagesResponseVO instance
        MessagesResponseVO messagesResponseVO = new MessagesResponseVO();

        // Set values for all fields
        String id = "msg_123456";
        String conversationId = "conv_123456";
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("text", "What is the weather today?");
        inputs.put("temperature", 0.7);
        String query = "What is the weather today?";
        Long createdAt = System.currentTimeMillis();
        String answer = "The weather today is sunny with a high of 25°C.";

        messagesResponseVO.setId(id);
        messagesResponseVO.setConversationId(conversationId);
        messagesResponseVO.setInputs(inputs);
        messagesResponseVO.setQuery(query);
        messagesResponseVO.setCreatedAt(createdAt);
        messagesResponseVO.setAnswer(answer);

        // Create and set feedback
        MessagesResponseVO.Feedback feedback = new MessagesResponseVO.Feedback();
        feedback.setRating("like");
        messagesResponseVO.setFeedback(feedback);

        // Create message files
        List<MessagesResponseVO.MessageFile> messageFiles = new ArrayList<>();
        MessagesResponseVO.MessageFile messageFile = new MessagesResponseVO.MessageFile();
        messageFile.setId("file_123456");
        messageFile.setType("image");
        messageFile.setUrl("https://example.com/image.jpg");
        messageFile.setBelongsTo("user");

        // Create agent thoughts for message file
        List<MessagesResponseVO.MessageFileAgentThought> agentThoughts = new ArrayList<>();
        MessagesResponseVO.MessageFileAgentThought agentThought = new MessagesResponseVO.MessageFileAgentThought();
        agentThought.setId("thought_123456");
        agentThought.setMessageId("msg_123456");
        agentThought.setPosition(1);
        agentThought.setThought("I need to check the weather data.");
        agentThought.setObservation("API returned weather data for today.");
        agentThought.setTool("weather_api");
        agentThought.setToolInput("{\"location\": \"New York\"}");
        agentThought.setCreatedAt(createdAt);
        agentThought.setConversationId(conversationId);

        List<String> thoughtMessageFiles = new ArrayList<>();
        thoughtMessageFiles.add("file_123456");
        agentThought.setMessageFiles(thoughtMessageFiles);

        agentThoughts.add(agentThought);
        messageFile.setAgentThoughts(agentThoughts);

        messageFiles.add(messageFile);
        messagesResponseVO.setMessageFiles(messageFiles);

        // Assert all values are set correctly
        assertEquals(id, messagesResponseVO.getId());
        assertEquals(conversationId, messagesResponseVO.getConversationId());
        assertEquals(inputs, messagesResponseVO.getInputs());
        assertEquals(query, messagesResponseVO.getQuery());
        assertEquals(createdAt, messagesResponseVO.getCreatedAt());
        assertEquals(answer, messagesResponseVO.getAnswer());

        // Assert feedback is set correctly
        assertNotNull(messagesResponseVO.getFeedback());
        assertEquals("like", messagesResponseVO.getFeedback().getRating());

        // Assert message files are set correctly
        assertNotNull(messagesResponseVO.getMessageFiles());
        assertEquals(1, messagesResponseVO.getMessageFiles().size());

        MessagesResponseVO.MessageFile retrievedFile = messagesResponseVO.getMessageFiles().get(0);
        assertEquals("file_123456", retrievedFile.getId());
        assertEquals("image", retrievedFile.getType());
        assertEquals("https://example.com/image.jpg", retrievedFile.getUrl());
        assertEquals("user", retrievedFile.getBelongsTo());

        // Assert agent thoughts are set correctly
        assertNotNull(retrievedFile.getAgentThoughts());
        assertEquals(1, retrievedFile.getAgentThoughts().size());

        MessagesResponseVO.MessageFileAgentThought retrievedThought = retrievedFile.getAgentThoughts().get(0);
        assertEquals("thought_123456", retrievedThought.getId());
        assertEquals("msg_123456", retrievedThought.getMessageId());
        assertEquals(Integer.valueOf(1), retrievedThought.getPosition());
        assertEquals("I need to check the weather data.", retrievedThought.getThought());
        assertEquals("API returned weather data for today.", retrievedThought.getObservation());
        assertEquals("weather_api", retrievedThought.getTool());
        assertEquals("{\"location\": \"New York\"}", retrievedThought.getToolInput());
        assertEquals(createdAt, retrievedThought.getCreatedAt());
        assertEquals(conversationId, retrievedThought.getConversationId());

        assertNotNull(retrievedThought.getMessageFiles());
        assertEquals(1, retrievedThought.getMessageFiles().size());
        assertEquals("file_123456", retrievedThought.getMessageFiles().get(0));
    }

    /**
     * Test serialization and deserialization with Jackson
     */
    @Test
    public void testJsonSerialization() throws JsonProcessingException {
        // Create a MessagesResponseVO instance with sample data
        MessagesResponseVO messagesResponseVO = new MessagesResponseVO();
        messagesResponseVO.setId("msg_123456");
        messagesResponseVO.setConversationId("conv_123456");
        messagesResponseVO.setQuery("What is AI?");
        messagesResponseVO.setAnswer("Artificial Intelligence is...");
        messagesResponseVO.setCreatedAt(1705395332L);

        Map<String, Object> inputs = new HashMap<>();
        inputs.put("query", "What is AI?");
        messagesResponseVO.setInputs(inputs);

        MessagesResponseVO.Feedback feedback = new MessagesResponseVO.Feedback();
        feedback.setRating("like");
        messagesResponseVO.setFeedback(feedback);

        // Serialize to JSON
        String json = objectMapper.writeValueAsString(messagesResponseVO);

        // Deserialize back to object
        MessagesResponseVO deserialized = objectMapper.readValue(json, MessagesResponseVO.class);

        // Verify the deserialized object matches the original
        assertEquals(messagesResponseVO.getId(), deserialized.getId());
        assertEquals(messagesResponseVO.getConversationId(), deserialized.getConversationId());
        assertEquals(messagesResponseVO.getQuery(), deserialized.getQuery());
        assertEquals(messagesResponseVO.getAnswer(), deserialized.getAnswer());
        assertEquals(messagesResponseVO.getCreatedAt(), deserialized.getCreatedAt());
        assertEquals(messagesResponseVO.getInputs().get("query"), deserialized.getInputs().get("query"));
        assertEquals(messagesResponseVO.getFeedback().getRating(), deserialized.getFeedback().getRating());
    }

    /**
     * Test JSON deserialization with JsonAlias
     */
    @Test
    public void testJsonDeserialization() throws JsonProcessingException {
        // Create a JSON string with snake_case field names
        String json = "{\n" +
                "  \"id\": \"msg_123456\",\n" +
                "  \"conversation_id\": \"conv_123456\",\n" +
                "  \"query\": \"What is AI?\",\n" +
                "  \"answer\": \"Artificial Intelligence is...\",\n" +
                "  \"created_at\": 1705395332,\n" +
                "  \"inputs\": {\n" +
                "    \"query\": \"What is AI?\"\n" +
                "  },\n" +
                "  \"message_files\": [\n" +
                "    {\n" +
                "      \"id\": \"file_123456\",\n" +
                "      \"type\": \"image\",\n" +
                "      \"url\": \"https://example.com/image.jpg\",\n" +
                "      \"belongs_to\": \"user\",\n" +
                "      \"agent_thoughts\": [\n" +
                "        {\n" +
                "          \"id\": \"thought_123456\",\n" +
                "          \"message_id\": \"msg_123456\",\n" +
                "          \"position\": 1,\n" +
                "          \"thought\": \"I need to analyze this image.\",\n" +
                "          \"observation\": \"The image shows a diagram of AI concepts.\",\n" +
                "          \"tool\": \"image_analyzer\",\n" +
                "          \"tool_input\": \"{\\\"image_url\\\": \\\"https://example.com/image.jpg\\\"}\",\n" +
                "          \"created_at\": 1705395332,\n" +
                "          \"message_files\": [\"file_123456\"],\n" +
                "          \"conversation_id\": \"conv_123456\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ],\n" +
                "  \"feedback\": {\n" +
                "    \"rating\": \"like\"\n" +
                "  }\n" +
                "}";

        // Deserialize
        MessagesResponseVO deserialized = objectMapper.readValue(json, MessagesResponseVO.class);

        // Verify JsonAlias annotations worked correctly
        assertEquals("msg_123456", deserialized.getId());
        assertEquals("conv_123456", deserialized.getConversationId());
        assertEquals(Long.valueOf(1705395332), deserialized.getCreatedAt());

        assertNotNull(deserialized.getMessageFiles());
        assertEquals(1, deserialized.getMessageFiles().size());

        MessagesResponseVO.MessageFile messageFile = deserialized.getMessageFiles().get(0);
        assertEquals("user", messageFile.getBelongsTo());

        assertNotNull(messageFile.getAgentThoughts());
        assertEquals(1, messageFile.getAgentThoughts().size());

        MessagesResponseVO.MessageFileAgentThought agentThought = messageFile.getAgentThoughts().get(0);
        assertEquals("msg_123456", agentThought.getMessageId());
        assertEquals("{\"image_url\": \"https://example.com/image.jpg\"}", agentThought.getToolInput());
        assertEquals(Long.valueOf(1705395332), agentThought.getCreatedAt());

        assertNotNull(agentThought.getMessageFiles());
        assertEquals(1, agentThought.getMessageFiles().size());
        assertEquals("file_123456", agentThought.getMessageFiles().get(0));
    }
}
