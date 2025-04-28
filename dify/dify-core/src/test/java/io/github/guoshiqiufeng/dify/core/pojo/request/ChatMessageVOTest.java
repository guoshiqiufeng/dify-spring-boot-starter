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

import io.github.guoshiqiufeng.dify.core.enums.ResponseModeEnum;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link ChatMessageVO}
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/4/28
 */
public class ChatMessageVOTest {

    @Test
    public void testSettersAndGetters() {
        // Arrange
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("name", "John Doe");
        inputs.put("age", 25);

        String query = "How are you today?";
        ResponseModeEnum responseMode = ResponseModeEnum.blocking;
        String conversationId = "conv-123456";
        String user = "user-789";

        List<ChatMessageVO.ChatMessageFile> files = new ArrayList<>();
        ChatMessageVO.ChatMessageFile file = new ChatMessageVO.ChatMessageFile();
        file.setType("image");
        file.setTransferMethod("remote_url");
        file.setUrl("https://example.com/image.jpg");
        files.add(file);

        // Act - Test method chaining style
        ChatMessageVO chatMessage = new ChatMessageVO()
                .setInputs(inputs)
                .setQuery(query)
                .setResponseMode(responseMode)
                .setConversationId(conversationId)
                .setUser(user)
                .setFiles(files);

        // Assert
        assertSame(inputs, chatMessage.getInputs());
        assertEquals(query, chatMessage.getQuery());
        assertEquals(responseMode, chatMessage.getResponseMode());
        assertEquals(conversationId, chatMessage.getConversationId());
        assertEquals(user, chatMessage.getUser());
        assertSame(files, chatMessage.getFiles());
        assertEquals(1, chatMessage.getFiles().size());
        assertEquals("image", chatMessage.getFiles().get(0).getType());
        assertEquals("remote_url", chatMessage.getFiles().get(0).getTransferMethod());
        assertEquals("https://example.com/image.jpg", chatMessage.getFiles().get(0).getUrl());
    }

    @Test
    public void testDefaultValues() {
        // Act
        ChatMessageVO chatMessage = new ChatMessageVO();

        // Assert
        assertNull(chatMessage.getInputs());
        assertNull(chatMessage.getQuery());
        assertNull(chatMessage.getResponseMode());
        assertNull(chatMessage.getConversationId());
        assertNull(chatMessage.getUser());
        assertNull(chatMessage.getFiles());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("name", "John Doe");

        ChatMessageVO chatMessage1 = new ChatMessageVO()
                .setInputs(inputs)
                .setQuery("Hello")
                .setResponseMode(ResponseModeEnum.blocking)
                .setConversationId("conv-123")
                .setUser("user-456");

        ChatMessageVO chatMessage2 = new ChatMessageVO()
                .setInputs(inputs)
                .setQuery("Hello")
                .setResponseMode(ResponseModeEnum.blocking)
                .setConversationId("conv-123")
                .setUser("user-456");

        ChatMessageVO chatMessage3 = new ChatMessageVO()
                .setInputs(inputs)
                .setQuery("Different query")
                .setResponseMode(ResponseModeEnum.blocking)
                .setConversationId("conv-123")
                .setUser("user-456");

        // Assert
        assertEquals(chatMessage1, chatMessage2);
        assertEquals(chatMessage1.hashCode(), chatMessage2.hashCode());
        assertNotEquals(chatMessage1, chatMessage3);
        assertNotEquals(chatMessage1.hashCode(), chatMessage3.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        ChatMessageVO chatMessage = new ChatMessageVO()
                .setQuery("Hello world")
                .setResponseMode(ResponseModeEnum.blocking)
                .setConversationId("conv-123");

        // Act
        String toString = chatMessage.toString();

        // Assert
        assertTrue(toString.contains("query=Hello world"));
        assertTrue(toString.contains("responseMode=" + ResponseModeEnum.blocking));
        assertTrue(toString.contains("conversationId=conv-123"));
    }

    @Test
    public void testNestedChatMessageFileClass() {
        // Arrange
        ChatMessageVO.ChatMessageFile file = new ChatMessageVO.ChatMessageFile();

        // Assert default values
        assertEquals("image", file.getType());
        assertEquals("remote_url", file.getTransferMethod());
        assertNull(file.getUrl());

        // Act
        file.setType("document");
        file.setTransferMethod("base64");
        file.setUrl("data:text/plain;base64,SGVsbG8gV29ybGQ=");

        // Assert
        assertEquals("document", file.getType());
        assertEquals("base64", file.getTransferMethod());
        assertEquals("data:text/plain;base64,SGVsbG8gV29ybGQ=", file.getUrl());
    }

    @Test
    public void testChatMessageFileEqualsAndHashCode() {
        // Arrange
        ChatMessageVO.ChatMessageFile file1 = new ChatMessageVO.ChatMessageFile();
        file1.setType("image");
        file1.setTransferMethod("remote_url");
        file1.setUrl("https://example.com/image.jpg");

        ChatMessageVO.ChatMessageFile file2 = new ChatMessageVO.ChatMessageFile();
        file2.setType("image");
        file2.setTransferMethod("remote_url");
        file2.setUrl("https://example.com/image.jpg");

        ChatMessageVO.ChatMessageFile file3 = new ChatMessageVO.ChatMessageFile();
        file3.setType("image");
        file3.setTransferMethod("remote_url");
        file3.setUrl("https://example.com/different.jpg");

        // Assert
        assertEquals(file1, file2);
        assertEquals(file1.hashCode(), file2.hashCode());
        assertNotEquals(file1, file3);
        assertNotEquals(file1.hashCode(), file3.hashCode());
    }

    @Test
    public void testSerializable() {
        // Verify that the classes implement Serializable
        ChatMessageVO chatMessage = new ChatMessageVO();
        assertInstanceOf(Serializable.class, chatMessage);

        ChatMessageVO.ChatMessageFile file = new ChatMessageVO.ChatMessageFile();
        assertInstanceOf(Serializable.class, file);
    }
}
