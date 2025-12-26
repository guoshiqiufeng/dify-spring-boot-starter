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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link ChatMessageSendResponse} JSON serialization/deserialization
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/4/27
 */
public class ChatMessageSendResponseJsonTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testJsonDeserializationWithJsonAlias() throws Exception {
        // Arrange - Use JsonAlias field names
        String json = "{" +
                "\"event\":\"message\"," +
                "\"conversation_id\":\"conv-123\"," +
                "\"message_id\":\"msg-456\"," +
                "\"created_at\":1705395332," +
                "\"task_id\":\"task-789\"," +
                "\"id\":\"id-abc\"," +
                "\"answer\":\"This is the assistant's response.\"," +
                "\"from_variable_selector\":null," +
                "\"metadata\":{" +
                "  \"usage\":{" +
                "    \"prompt_tokens\":100," +
                "    \"prompt_unit_price\":\"0.0001\"," +
                "    \"prompt_price_unit\":\"0.00001\"," +
                "    \"prompt_price\":\"0.01\"," +
                "    \"completion_tokens\":150," +
                "    \"completion_unit_price\":\"0.0002\"," +
                "    \"completion_price_unit\":\"0.00002\"," +
                "    \"completion_price\":\"0.03\"," +
                "    \"total_tokens\":250," +
                "    \"total_price\":\"0.04\"," +
                "    \"currency\":\"USD\"," +
                "    \"latency\":1.5" +
                "  }," +
                "  \"retriever_resources\":[" +
                "    {" +
                "      \"position\":1," +
                "      \"dataset_id\":\"dataset-123\"," +
                "      \"dataset_name\":\"Knowledge Base\"," +
                "      \"document_id\":\"doc-456\"," +
                "      \"document_name\":\"Research Paper\"," +
                "      \"segment_id\":\"segment-789\"," +
                "      \"score\":0.95," +
                "      \"content\":\"This is the retrieved content.\"" +
                "    }" +
                "  ]" +
                "}" +
                "}";

        // Act
        ChatMessageSendResponse response = objectMapper.readValue(json, ChatMessageSendResponse.class);

        // Assert - Main fields
        assertEquals("message", response.getEvent());
        assertEquals("conv-123", response.getConversationId());
        assertEquals("msg-456", response.getMessageId());
        assertEquals(Long.valueOf(1705395332), response.getCreatedAt());
        assertEquals("task-789", response.getTaskId());
        assertEquals("id-abc", response.getId());
        assertEquals("This is the assistant's response.", response.getAnswer());
        assertNull(response.getFromVariableSelector());

        // Assert - Metadata
        assertNotNull(response.getMetadata());

        // Assert - Usage
        assertNotNull(response.getMetadata().getUsage());
        assertEquals(Integer.valueOf(100), response.getMetadata().getUsage().getPromptTokens());
        assertEquals("0.0001", response.getMetadata().getUsage().getPromptUnitPrice());
        assertEquals("0.00001", response.getMetadata().getUsage().getPromptPriceUnit());
        assertEquals("0.01", response.getMetadata().getUsage().getPromptPrice());
        assertEquals(Integer.valueOf(150), response.getMetadata().getUsage().getCompletionTokens());
        assertEquals("0.0002", response.getMetadata().getUsage().getCompletionUnitPrice());
        assertEquals("0.00002", response.getMetadata().getUsage().getCompletionPriceUnit());
        assertEquals("0.03", response.getMetadata().getUsage().getCompletionPrice());
        assertEquals(Integer.valueOf(250), response.getMetadata().getUsage().getTotalTokens());
        assertEquals("0.04", response.getMetadata().getUsage().getTotalPrice());
        assertEquals("USD", response.getMetadata().getUsage().getCurrency());
        assertEquals(1.5, response.getMetadata().getUsage().getLatency());

        // Assert - RetrieverResources
        assertNotNull(response.getMetadata().getRetrieverResources());
        assertEquals(1, response.getMetadata().getRetrieverResources().size());

        ChatMessageSendResponse.RetrieverResources resources = response.getMetadata().getRetrieverResources().get(0);
        assertEquals(Integer.valueOf(1), resources.getPosition());
        assertEquals("dataset-123", resources.getDatasetId());
        assertEquals("Knowledge Base", resources.getDatasetName());
        assertEquals("doc-456", resources.getDocumentId());
        assertEquals("Research Paper", resources.getDocumentName());
        assertEquals("segment-789", resources.getSegmentId());
        assertEquals(0.95f, resources.getScore());
        assertEquals("This is the retrieved content.", resources.getContent());
    }

    @Test
    public void testJsonDeserializationWithoutMetadata() throws Exception {
        // Arrange - Response without metadata
        String json = "{" +
                "\"event\":\"message\"," +
                "\"conversation_id\":\"conv-123\"," +
                "\"message_id\":\"msg-456\"," +
                "\"created_at\":1705395332," +
                "\"id\":\"id-abc\"," +
                "\"answer\":\"This is the assistant's response.\"" +
                "}";

        // Act
        ChatMessageSendResponse response = objectMapper.readValue(json, ChatMessageSendResponse.class);

        // Assert
        assertEquals("message", response.getEvent());
        assertEquals("conv-123", response.getConversationId());
        assertEquals("msg-456", response.getMessageId());
        assertEquals(Long.valueOf(1705395332), response.getCreatedAt());
        assertEquals("id-abc", response.getId());
        assertEquals("This is the assistant's response.", response.getAnswer());
        assertNull(response.getTaskId());
        assertNull(response.getFromVariableSelector());
        assertNull(response.getMetadata());
    }

    @Test
    public void testJsonDeserializationWithEmptyMetadata() throws Exception {
        // Arrange - Response with empty metadata
        String json = "{" +
                "\"event\":\"message\"," +
                "\"conversation_id\":\"conv-123\"," +
                "\"message_id\":\"msg-456\"," +
                "\"created_at\":1705395332," +
                "\"id\":\"id-abc\"," +
                "\"answer\":\"This is the assistant's response.\"," +
                "\"metadata\":{}" +
                "}";

        // Act
        ChatMessageSendResponse response = objectMapper.readValue(json, ChatMessageSendResponse.class);

        // Assert
        assertEquals("message", response.getEvent());
        assertEquals("conv-123", response.getConversationId());
        assertEquals("msg-456", response.getMessageId());
        assertEquals(Long.valueOf(1705395332), response.getCreatedAt());
        assertEquals("id-abc", response.getId());
        assertEquals("This is the assistant's response.", response.getAnswer());
        assertNotNull(response.getMetadata());
        assertNull(response.getMetadata().getUsage());
        assertNull(response.getMetadata().getRetrieverResources());
    }

    @Test
    public void testJsonDeserializationWithPartialMetadata() throws Exception {
        // Arrange - Response with partial metadata (only usage, no retriever_resources)
        String json = "{" +
                "\"event\":\"message\"," +
                "\"conversation_id\":\"conv-123\"," +
                "\"message_id\":\"msg-456\"," +
                "\"created_at\":1705395332," +
                "\"id\":\"id-abc\"," +
                "\"answer\":\"This is the assistant's response.\"," +
                "\"metadata\":{" +
                "  \"usage\":{" +
                "    \"prompt_tokens\":100," +
                "    \"completion_tokens\":150," +
                "    \"total_tokens\":250," +
                "    \"total_price\":\"0.04\"," +
                "    \"currency\":\"USD\"" +
                "  }" +
                "}" +
                "}";

        // Act
        ChatMessageSendResponse response = objectMapper.readValue(json, ChatMessageSendResponse.class);

        // Assert
        assertEquals("message", response.getEvent());
        assertEquals("conv-123", response.getConversationId());
        assertEquals("msg-456", response.getMessageId());
        assertEquals(Long.valueOf(1705395332), response.getCreatedAt());
        assertEquals("id-abc", response.getId());
        assertEquals("This is the assistant's response.", response.getAnswer());

        assertNotNull(response.getMetadata());
        assertNotNull(response.getMetadata().getUsage());
        assertEquals(Integer.valueOf(100), response.getMetadata().getUsage().getPromptTokens());
        assertEquals(Integer.valueOf(150), response.getMetadata().getUsage().getCompletionTokens());
        assertEquals(Integer.valueOf(250), response.getMetadata().getUsage().getTotalTokens());
        assertEquals("0.04", response.getMetadata().getUsage().getTotalPrice());
        assertEquals("USD", response.getMetadata().getUsage().getCurrency());

        assertNull(response.getMetadata().getRetrieverResources());
    }

    @Test
    public void testJsonSerializationShouldUseStandardFieldNames() throws Exception {
        // Arrange
        ChatMessageSendResponse response = new ChatMessageSendResponse();
        response.setEvent("message");
        response.setConversationId("conv-123");
        response.setMessageId("msg-456");
        response.setCreatedAt(1705395332L);
        response.setTaskId("task-789");
        response.setId("id-abc");
        response.setAnswer("This is the assistant's response.");

        // Act
        String json = objectMapper.writeValueAsString(response);

        // Assert - When serializing, we use standard field names (camelCase)
        assertTrue(json.contains("\"event\":\"message\""));
        assertTrue(json.contains("\"conversationId\":\"conv-123\""));
        assertTrue(json.contains("\"messageId\":\"msg-456\""));
        assertTrue(json.contains("\"createdAt\":1705395332"));
        assertTrue(json.contains("\"taskId\":\"task-789\""));
        assertTrue(json.contains("\"id\":\"id-abc\""));
        assertTrue(json.contains("\"answer\":\"This is the assistant's response.\""));
    }

    @Test
    public void testFullCycleSerialization() throws Exception {
        // Arrange - Create a full response object
        ChatMessageSendResponse.Usage usage = new ChatMessageSendResponse.Usage();
        usage.setPromptTokens(100);
        usage.setCompletionTokens(150);
        usage.setTotalTokens(250);
        usage.setTotalPrice("0.04");
        usage.setCurrency("USD");

        ChatMessageSendResponse.RetrieverResources resources = new ChatMessageSendResponse.RetrieverResources();
        resources.setPosition(1);
        resources.setDatasetId("dataset-123");
        resources.setDatasetName("Knowledge Base");
        resources.setDocumentId("doc-456");
        resources.setScore(0.95f);
        resources.setContent("This is the retrieved content.");

        ChatMessageSendResponse.Metadata metadata = new ChatMessageSendResponse.Metadata();
        metadata.setUsage(usage);
        metadata.setRetrieverResources(List.of(resources));

        ChatMessageSendResponse originalResponse = new ChatMessageSendResponse();
        originalResponse.setEvent("message");
        originalResponse.setConversationId("conv-123");
        originalResponse.setMessageId("msg-456");
        originalResponse.setCreatedAt(1705395332L);
        originalResponse.setTaskId("task-789");
        originalResponse.setId("id-abc");
        originalResponse.setAnswer("This is the assistant's response.");
        originalResponse.setMetadata(metadata);

        // Act - Serialize then deserialize
        String json = objectMapper.writeValueAsString(originalResponse);
        ChatMessageSendResponse deserializedResponse = objectMapper.readValue(json, ChatMessageSendResponse.class);

        // Assert
        assertEquals(originalResponse.getEvent(), deserializedResponse.getEvent());
        assertEquals(originalResponse.getConversationId(), deserializedResponse.getConversationId());
        assertEquals(originalResponse.getMessageId(), deserializedResponse.getMessageId());
        assertEquals(originalResponse.getCreatedAt(), deserializedResponse.getCreatedAt());
        assertEquals(originalResponse.getTaskId(), deserializedResponse.getTaskId());
        assertEquals(originalResponse.getId(), deserializedResponse.getId());
        assertEquals(originalResponse.getAnswer(), deserializedResponse.getAnswer());

        // Assert metadata
        assertNotNull(deserializedResponse.getMetadata());
        assertNotNull(deserializedResponse.getMetadata().getUsage());
        assertEquals(originalResponse.getMetadata().getUsage().getPromptTokens(),
                deserializedResponse.getMetadata().getUsage().getPromptTokens());
        assertEquals(originalResponse.getMetadata().getUsage().getCompletionTokens(),
                deserializedResponse.getMetadata().getUsage().getCompletionTokens());
        assertEquals(originalResponse.getMetadata().getUsage().getTotalTokens(),
                deserializedResponse.getMetadata().getUsage().getTotalTokens());
        assertEquals(originalResponse.getMetadata().getUsage().getTotalPrice(),
                deserializedResponse.getMetadata().getUsage().getTotalPrice());
        assertEquals(originalResponse.getMetadata().getUsage().getCurrency(),
                deserializedResponse.getMetadata().getUsage().getCurrency());

        // Assert retriever resources
        assertNotNull(deserializedResponse.getMetadata().getRetrieverResources());
        assertEquals(1, deserializedResponse.getMetadata().getRetrieverResources().size());
        assertEquals(originalResponse.getMetadata().getRetrieverResources().get(0).getPosition(),
                deserializedResponse.getMetadata().getRetrieverResources().get(0).getPosition());
        assertEquals(originalResponse.getMetadata().getRetrieverResources().get(0).getDatasetId(),
                deserializedResponse.getMetadata().getRetrieverResources().get(0).getDatasetId());
        assertEquals(originalResponse.getMetadata().getRetrieverResources().get(0).getDatasetName(),
                deserializedResponse.getMetadata().getRetrieverResources().get(0).getDatasetName());
        assertEquals(originalResponse.getMetadata().getRetrieverResources().get(0).getDocumentId(),
                deserializedResponse.getMetadata().getRetrieverResources().get(0).getDocumentId());
        assertEquals(originalResponse.getMetadata().getRetrieverResources().get(0).getScore(),
                deserializedResponse.getMetadata().getRetrieverResources().get(0).getScore());
        assertEquals(originalResponse.getMetadata().getRetrieverResources().get(0).getContent(),
                deserializedResponse.getMetadata().getRetrieverResources().get(0).getContent());
    }
}
