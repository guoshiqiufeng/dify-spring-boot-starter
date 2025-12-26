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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link RenameConversationRequest}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class RenameConversationRequestTest {

    @Test
    public void testGetterAndSetter() {
        // Arrange
        RenameConversationRequest request = new RenameConversationRequest();
        String conversationId = "test-conversation-id";
        String name = "test conversation name";
        Boolean autoGenerate = true;

        // Act
        request.setConversationId(conversationId);
        request.setName(name);
        request.setAutoGenerate(autoGenerate);

        // Assert
        assertEquals(conversationId, request.getConversationId());
        assertEquals(name, request.getName());
        assertEquals(autoGenerate, request.getAutoGenerate());
    }

    @Test
    public void testDefaultValues() {
        // Arrange
        RenameConversationRequest request = new RenameConversationRequest();

        // Assert
        assertFalse(request.getAutoGenerate());
        assertNull(request.getConversationId());
        assertNull(request.getName());
    }

    @Test
    public void testInheritedFields() {
        // Arrange
        RenameConversationRequest request = new RenameConversationRequest();
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
        RenameConversationRequest request1 = new RenameConversationRequest();
        request1.setConversationId("conv1");
        request1.setName("name1");
        request1.setAutoGenerate(true);

        RenameConversationRequest request2 = new RenameConversationRequest();
        request2.setConversationId("conv1");
        request2.setName("name1");
        request2.setAutoGenerate(true);

        RenameConversationRequest request3 = new RenameConversationRequest();
        request3.setConversationId("conv2");
        request3.setName("name2");
        request3.setAutoGenerate(false);

        // Assert
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1, request3);
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        RenameConversationRequest request = new RenameConversationRequest();
        request.setConversationId("test-conv-id");
        request.setName("test name");
        request.setAutoGenerate(true);

        // Act
        String toString = request.toString();

        // Assert
        assertTrue(toString.contains("conversationId=test-conv-id"));
        assertTrue(toString.contains("name=test name"));
        assertTrue(toString.contains("autoGenerate=true"));
    }
}
