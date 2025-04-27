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
package io.github.guoshiqiufeng.dify.chat.dto.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link MessagesRequest}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class MessagesRequestTest {

    @Test
    public void testGetterAndSetter() {
        // Arrange
        MessagesRequest request = new MessagesRequest();
        String conversationId = "test-conversation-id";
        String firstId = "test-first-id";
        Integer limit = 50;

        // Act
        request.setConversationId(conversationId);
        request.setFirstId(firstId);
        request.setLimit(limit);

        // Assert
        assertEquals(conversationId, request.getConversationId());
        assertEquals(firstId, request.getFirstId());
        assertEquals(limit, request.getLimit());
    }

    @Test
    public void testDefaultValues() {
        // Arrange
        MessagesRequest request = new MessagesRequest();

        // Assert
        assertEquals(20, request.getLimit());
        assertNull(request.getConversationId());
        assertNull(request.getFirstId());
    }

    @Test
    public void testInheritedFields() {
        // Arrange
        MessagesRequest request = new MessagesRequest();
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
        MessagesRequest request1 = new MessagesRequest();
        request1.setConversationId("conv1");
        request1.setFirstId("first1");
        request1.setLimit(30);

        MessagesRequest request2 = new MessagesRequest();
        request2.setConversationId("conv1");
        request2.setFirstId("first1");
        request2.setLimit(30);

        MessagesRequest request3 = new MessagesRequest();
        request3.setConversationId("conv2");
        request3.setFirstId("first2");
        request3.setLimit(40);

        // Assert
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1, request3);
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        MessagesRequest request = new MessagesRequest();
        request.setConversationId("test-conv-id");
        request.setFirstId("test-first-id");
        request.setLimit(25);

        // Act
        String toString = request.toString();

        // Assert
        assertTrue(toString.contains("conversationId=test-conv-id"));
        assertTrue(toString.contains("firstId=test-first-id"));
        assertTrue(toString.contains("limit=25"));
    }
}
