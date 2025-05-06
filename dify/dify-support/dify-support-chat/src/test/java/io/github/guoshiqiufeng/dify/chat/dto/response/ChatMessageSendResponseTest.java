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
package io.github.guoshiqiufeng.dify.chat.dto.response;

import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link ChatMessageSendResponse}
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/4/27
 */
public class ChatMessageSendResponseTest {

    @Test
    public void testSettersAndGetters() {
        // Arrange
        String event = "message";
        String conversationId = "conv-123";
        String messageId = "msg-456";
        Long createdAt = 1705395332L;
        String taskId = "task-789";
        String id = "id-abc";
        String answer = "This is the assistant's response.";
        Object fromVariableSelector = null;

        // Create ChatMessageSendResponse instance
        ChatMessageSendResponse response = new ChatMessageSendResponse();
        response.setEvent(event);
        response.setConversationId(conversationId);
        response.setMessageId(messageId);
        response.setCreatedAt(createdAt);
        response.setTaskId(taskId);
        response.setId(id);
        response.setAnswer(answer);
        response.setFromVariableSelector(fromVariableSelector);

        // Assert
        assertEquals(event, response.getEvent());
        assertEquals(conversationId, response.getConversationId());
        assertEquals(messageId, response.getMessageId());
        assertEquals(createdAt, response.getCreatedAt());
        assertEquals(taskId, response.getTaskId());
        assertEquals(id, response.getId());
        assertEquals(answer, response.getAnswer());
        assertEquals(fromVariableSelector, response.getFromVariableSelector());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        ChatMessageSendResponse response1 = new ChatMessageSendResponse();
        response1.setEvent("message");
        response1.setConversationId("conv-123");
        response1.setMessageId("msg-456");
        response1.setCreatedAt(1705395332L);

        ChatMessageSendResponse response2 = new ChatMessageSendResponse();
        response2.setEvent("message");
        response2.setConversationId("conv-123");
        response2.setMessageId("msg-456");
        response2.setCreatedAt(1705395332L);

        ChatMessageSendResponse response3 = new ChatMessageSendResponse();
        response3.setEvent("message");
        response3.setConversationId("conv-789");
        response3.setMessageId("msg-abc");
        response3.setCreatedAt(1705395332L);

        // Assert
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1, response3);
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }

    @Test
    public void testNestedMetadataClass() {
        // Arrange
        ChatMessageSendResponse.Metadata metadata = new ChatMessageSendResponse.Metadata();
        ChatMessageSendResponse.Usage usage = new ChatMessageSendResponse.Usage();
        List<ChatMessageSendResponse.RetrieverResources> retrieverResources = new ArrayList<>();

        // Act
        metadata.setUsage(usage);
        metadata.setRetrieverResources(retrieverResources);

        // Assert
        assertSame(usage, metadata.getUsage());
        assertSame(retrieverResources, metadata.getRetrieverResources());
    }

    @Test
    public void testNestedRetrieverResourcesClass() {
        // Arrange
        Integer position = 1;
        String datasetId = "dataset-123";
        String datasetName = "Knowledge Base";
        String documentId = "doc-456";
        String documentName = "Research Paper";
        String segmentId = "segment-789";
        Float score = 0.95f;
        String content = "This is the retrieved content.";

        // Act
        ChatMessageSendResponse.RetrieverResources resources = new ChatMessageSendResponse.RetrieverResources();
        resources.setPosition(position);
        resources.setDatasetId(datasetId);
        resources.setDatasetName(datasetName);
        resources.setDocumentId(documentId);
        resources.setDocumentName(documentName);
        resources.setSegmentId(segmentId);
        resources.setScore(score);
        resources.setContent(content);

        // Assert
        assertEquals(position, resources.getPosition());
        assertEquals(datasetId, resources.getDatasetId());
        assertEquals(datasetName, resources.getDatasetName());
        assertEquals(documentId, resources.getDocumentId());
        assertEquals(documentName, resources.getDocumentName());
        assertEquals(segmentId, resources.getSegmentId());
        assertEquals(score, resources.getScore());
        assertEquals(content, resources.getContent());
    }

    @Test
    public void testNestedUsageClass() {
        // Arrange
        Integer promptTokens = 100;
        String promptUnitPrice = "0.0001";
        String promptPriceUnit = "0.00001";
        String promptPrice = "0.01";
        Integer completionTokens = 150;
        String completionUnitPrice = "0.0002";
        String completionPriceUnit = "0.00002";
        String completionPrice = "0.03";
        Integer totalTokens = 250;
        String totalPrice = "0.04";
        String currency = "USD";
        Double latency = 1.5;

        // Act
        ChatMessageSendResponse.Usage usage = new ChatMessageSendResponse.Usage();
        usage.setPromptTokens(promptTokens);
        usage.setPromptUnitPrice(promptUnitPrice);
        usage.setPromptPriceUnit(promptPriceUnit);
        usage.setPromptPrice(promptPrice);
        usage.setCompletionTokens(completionTokens);
        usage.setCompletionUnitPrice(completionUnitPrice);
        usage.setCompletionPriceUnit(completionPriceUnit);
        usage.setCompletionPrice(completionPrice);
        usage.setTotalTokens(totalTokens);
        usage.setTotalPrice(totalPrice);
        usage.setCurrency(currency);
        usage.setLatency(latency);

        // Assert
        assertEquals(promptTokens, usage.getPromptTokens());
        assertEquals(promptUnitPrice, usage.getPromptUnitPrice());
        assertEquals(promptPriceUnit, usage.getPromptPriceUnit());
        assertEquals(promptPrice, usage.getPromptPrice());
        assertEquals(completionTokens, usage.getCompletionTokens());
        assertEquals(completionUnitPrice, usage.getCompletionUnitPrice());
        assertEquals(completionPriceUnit, usage.getCompletionPriceUnit());
        assertEquals(completionPrice, usage.getCompletionPrice());
        assertEquals(totalTokens, usage.getTotalTokens());
        assertEquals(totalPrice, usage.getTotalPrice());
        assertEquals(currency, usage.getCurrency());
        assertEquals(latency, usage.getLatency());
    }

    @Test
    public void testNestedClassesEqualsAndHashCode() {
        // Arrange - RetrieverResources
        ChatMessageSendResponse.RetrieverResources resources1 = new ChatMessageSendResponse.RetrieverResources();
        resources1.setPosition(1);
        resources1.setDatasetId("dataset-123");
        resources1.setScore(0.95f);

        ChatMessageSendResponse.RetrieverResources resources2 = new ChatMessageSendResponse.RetrieverResources();
        resources2.setPosition(1);
        resources2.setDatasetId("dataset-123");
        resources2.setScore(0.95f);

        ChatMessageSendResponse.RetrieverResources resources3 = new ChatMessageSendResponse.RetrieverResources();
        resources3.setPosition(2);
        resources3.setDatasetId("dataset-456");
        resources3.setScore(0.85f);

        // Assert - RetrieverResources
        assertEquals(resources1, resources2);
        assertEquals(resources1.hashCode(), resources2.hashCode());
        assertNotEquals(resources1, resources3);
        assertNotEquals(resources1.hashCode(), resources3.hashCode());

        // Arrange - Usage
        ChatMessageSendResponse.Usage usage1 = new ChatMessageSendResponse.Usage();
        usage1.setPromptTokens(100);
        usage1.setCompletionTokens(150);
        usage1.setTotalTokens(250);

        ChatMessageSendResponse.Usage usage2 = new ChatMessageSendResponse.Usage();
        usage2.setPromptTokens(100);
        usage2.setCompletionTokens(150);
        usage2.setTotalTokens(250);

        ChatMessageSendResponse.Usage usage3 = new ChatMessageSendResponse.Usage();
        usage3.setPromptTokens(200);
        usage3.setCompletionTokens(300);
        usage3.setTotalTokens(500);

        // Assert - Usage
        assertEquals(usage1, usage2);
        assertEquals(usage1.hashCode(), usage2.hashCode());
        assertNotEquals(usage1, usage3);
        assertNotEquals(usage1.hashCode(), usage3.hashCode());

        // Arrange - Metadata
        ChatMessageSendResponse.Metadata metadata1 = new ChatMessageSendResponse.Metadata();
        metadata1.setUsage(usage1);
        List<ChatMessageSendResponse.RetrieverResources> resourcesList1 = new ArrayList<>();
        resourcesList1.add(resources1);
        metadata1.setRetrieverResources(resourcesList1);

        ChatMessageSendResponse.Metadata metadata2 = new ChatMessageSendResponse.Metadata();
        metadata2.setUsage(usage2);
        List<ChatMessageSendResponse.RetrieverResources> resourcesList2 = new ArrayList<>();
        resourcesList2.add(resources2);
        metadata2.setRetrieverResources(resourcesList2);

        ChatMessageSendResponse.Metadata metadata3 = new ChatMessageSendResponse.Metadata();
        metadata3.setUsage(usage3);
        List<ChatMessageSendResponse.RetrieverResources> resourcesList3 = new ArrayList<>();
        resourcesList3.add(resources3);
        metadata3.setRetrieverResources(resourcesList3);

        // Assert - Metadata
        assertEquals(metadata1, metadata2);
        assertEquals(metadata1.hashCode(), metadata2.hashCode());
        assertNotEquals(metadata1, metadata3);
        assertNotEquals(metadata1.hashCode(), metadata3.hashCode());
    }

    @Test
    public void testFullResponseSetup() {
        // Arrange
        ChatMessageSendResponse response = new ChatMessageSendResponse();
        response.setEvent("message");
        response.setConversationId("conv-123");
        response.setMessageId("msg-456");
        response.setCreatedAt(1705395332L);
        response.setTaskId("task-789");
        response.setId("id-abc");
        response.setAnswer("This is the assistant's response.");

        // Create nested objects
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

        List<ChatMessageSendResponse.RetrieverResources> resourcesList = new ArrayList<>();
        resourcesList.add(resources);

        ChatMessageSendResponse.Metadata metadata = new ChatMessageSendResponse.Metadata();
        metadata.setUsage(usage);
        metadata.setRetrieverResources(resourcesList);

        response.setMetadata(metadata);

        // Assert
        assertEquals("message", response.getEvent());
        assertEquals("conv-123", response.getConversationId());
        assertEquals("msg-456", response.getMessageId());
        assertEquals(Long.valueOf(1705395332), response.getCreatedAt());
        assertEquals("task-789", response.getTaskId());
        assertEquals("id-abc", response.getId());
        assertEquals("This is the assistant's response.", response.getAnswer());

        assertNotNull(response.getMetadata());
        assertNotNull(response.getMetadata().getUsage());
        assertEquals(Integer.valueOf(100), response.getMetadata().getUsage().getPromptTokens());
        assertEquals(Integer.valueOf(150), response.getMetadata().getUsage().getCompletionTokens());
        assertEquals(Integer.valueOf(250), response.getMetadata().getUsage().getTotalTokens());
        assertEquals("0.04", response.getMetadata().getUsage().getTotalPrice());
        assertEquals("USD", response.getMetadata().getUsage().getCurrency());

        assertNotNull(response.getMetadata().getRetrieverResources());
        assertEquals(1, response.getMetadata().getRetrieverResources().size());
        assertEquals(Integer.valueOf(1), response.getMetadata().getRetrieverResources().get(0).getPosition());
        assertEquals("dataset-123", response.getMetadata().getRetrieverResources().get(0).getDatasetId());
        assertEquals("Knowledge Base", response.getMetadata().getRetrieverResources().get(0).getDatasetName());
        assertEquals("doc-456", response.getMetadata().getRetrieverResources().get(0).getDocumentId());
        assertEquals(0.95f, response.getMetadata().getRetrieverResources().get(0).getScore());
        assertEquals("This is the retrieved content.", response.getMetadata().getRetrieverResources().get(0).getContent());
    }

    @Test
    public void testToString() {
        // Arrange
        ChatMessageSendResponse response = new ChatMessageSendResponse();
        response.setEvent("message");
        response.setConversationId("conv-123");
        response.setMessageId("msg-456");
        response.setAnswer("Hello world");

        // Act
        String toString = response.toString();

        // Assert
        assertTrue(toString.contains("event=message"));
        assertTrue(toString.contains("conversationId=conv-123"));
        assertTrue(toString.contains("messageId=msg-456"));
        assertTrue(toString.contains("answer=Hello world"));
    }

    @Test
    public void testSerializable() {
        // Verify that all classes implement Serializable
        ChatMessageSendResponse response = new ChatMessageSendResponse();
        assertInstanceOf(Serializable.class, response);

        ChatMessageSendResponse.Metadata metadata = new ChatMessageSendResponse.Metadata();
        assertInstanceOf(Serializable.class, metadata);

        ChatMessageSendResponse.RetrieverResources resources = new ChatMessageSendResponse.RetrieverResources();
        assertInstanceOf(Serializable.class, resources);

        ChatMessageSendResponse.Usage usage = new ChatMessageSendResponse.Usage();
        assertInstanceOf(Serializable.class, usage);
    }
}
