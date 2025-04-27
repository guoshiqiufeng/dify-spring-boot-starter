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
 * Test for {@link MessageConversationsRequest}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class MessageConversationsRequestTest {

    @Test
    public void testGetterAndSetter() {
        // Arrange
        MessageConversationsRequest request = new MessageConversationsRequest();
        String lastId = "test-last-id";
        Integer limit = 50;
        String sortBy = "-created_at";

        // Act
        request.setLastId(lastId);
        request.setLimit(limit);
        request.setSortBy(sortBy);

        // Assert
        assertEquals(lastId, request.getLastId());
        assertEquals(limit, request.getLimit());
        assertEquals(sortBy, request.getSortBy());
    }

    @Test
    public void testDefaultValues() {
        // Arrange
        MessageConversationsRequest request = new MessageConversationsRequest();

        // Assert
        assertEquals(20, request.getLimit());
        assertNull(request.getLastId());
        assertNull(request.getSortBy());
    }

    @Test
    public void testInheritedFields() {
        // Arrange
        MessageConversationsRequest request = new MessageConversationsRequest();
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
        MessageConversationsRequest request1 = new MessageConversationsRequest();
        request1.setLastId("last1");
        request1.setLimit(30);
        request1.setSortBy("-updated_at");

        MessageConversationsRequest request2 = new MessageConversationsRequest();
        request2.setLastId("last1");
        request2.setLimit(30);
        request2.setSortBy("-updated_at");

        MessageConversationsRequest request3 = new MessageConversationsRequest();
        request3.setLastId("last2");
        request3.setLimit(40);
        request3.setSortBy("created_at");

        // Assert
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1, request3);
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        MessageConversationsRequest request = new MessageConversationsRequest();
        request.setLastId("test-last-id");
        request.setLimit(25);
        request.setSortBy("-created_at");

        // Act
        String toString = request.toString();

        // Assert
        assertTrue(toString.contains("lastId=test-last-id"));
        assertTrue(toString.contains("limit=25"));
        assertTrue(toString.contains("sortBy=-created_at"));
    }
}
