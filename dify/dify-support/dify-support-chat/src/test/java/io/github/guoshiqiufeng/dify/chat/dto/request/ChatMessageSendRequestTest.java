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
package io.github.guoshiqiufeng.dify.chat.dto.request;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link ChatMessageSendRequest}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class ChatMessageSendRequestTest {

    @Test
    public void testGetterAndSetter() {
        // Arrange
        ChatMessageSendRequest request = new ChatMessageSendRequest();
        String conversationId = "test-conversation-id";
        String content = "test content";
        List<ChatMessageSendRequest.ChatMessageFile> files = new ArrayList<>();
        ChatMessageSendRequest.ChatMessageFile file = new ChatMessageSendRequest.ChatMessageFile();
        file.setUrl("https://example.com/image.jpg");
        files.add(file);
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("key1", "value1");

        // Act
        request.setConversationId(conversationId);
        request.setContent(content);
        request.setFiles(files);
        request.setInputs(inputs);

        // Assert
        assertEquals(conversationId, request.getConversationId());
        assertEquals(content, request.getContent());
        assertEquals(files, request.getFiles());
        assertEquals(inputs, request.getInputs());
    }

    @Test
    public void testChainSetter() {
        // Arrange
        String conversationId = "test-conversation-id";
        String content = "test content";
        List<ChatMessageSendRequest.ChatMessageFile> files = new ArrayList<>();
        ChatMessageSendRequest.ChatMessageFile file = new ChatMessageSendRequest.ChatMessageFile();
        file.setUrl("https://example.com/image.jpg");
        files.add(file);
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("key1", "value1");

        // Act
        ChatMessageSendRequest request = new ChatMessageSendRequest()
                .setConversationId(conversationId)
                .setContent(content)
                .setFiles(files)
                .setInputs(inputs);

        // Assert
        assertEquals(conversationId, request.getConversationId());
        assertEquals(content, request.getContent());
        assertEquals(files, request.getFiles());
        assertEquals(inputs, request.getInputs());
    }

    @Test
    public void testInheritedFields() {
        // Arrange
        ChatMessageSendRequest request = new ChatMessageSendRequest();
        String apiKey = "test-api-key";
        String userId = "test-user-id";

        // Act
        request.setApiKey(apiKey);
        request.setUserId(userId);

        // Assert
        assertEquals(apiKey, request.getApiKey());
        assertEquals(userId, request.getUserId());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        ChatMessageSendRequest request1 = new ChatMessageSendRequest();
        request1.setConversationId("conv1");
        request1.setContent("content1");

        ChatMessageSendRequest request2 = new ChatMessageSendRequest();
        request2.setConversationId("conv1");
        request2.setContent("content1");

        ChatMessageSendRequest request3 = new ChatMessageSendRequest();
        request3.setConversationId("conv2");
        request3.setContent("content2");

        // Assert
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1, request3);
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        ChatMessageSendRequest request = new ChatMessageSendRequest();
        request.setConversationId("test-conv-id");
        request.setContent("test content");

        // Act
        String toString = request.toString();

        // Assert
        assertTrue(toString.contains("conversationId=test-conv-id"));
        assertTrue(toString.contains("content=test content"));
    }

    @Test
    public void testChatMessageFile() {
        // Arrange
        ChatMessageSendRequest.ChatMessageFile file = new ChatMessageSendRequest.ChatMessageFile();
        String url = "https://example.com/image.jpg";

        // Act
        file.setUrl(url);

        // Assert
        assertEquals("image", file.getType());
        assertEquals("remote_url", file.getTransferMethod());
        assertEquals(url, file.getUrl());
    }

    @Test
    public void testChatMessageFileToString() {
        // Arrange
        ChatMessageSendRequest.ChatMessageFile file = new ChatMessageSendRequest.ChatMessageFile();
        file.setUrl("https://example.com/image.jpg");

        // Act
        String toString = file.toString();

        // Assert
        assertTrue(toString.contains("type=image"));
        assertTrue(toString.contains("transferMethod=remote_url"));
        assertTrue(toString.contains("url=https://example.com/image.jpg"));
    }
}
