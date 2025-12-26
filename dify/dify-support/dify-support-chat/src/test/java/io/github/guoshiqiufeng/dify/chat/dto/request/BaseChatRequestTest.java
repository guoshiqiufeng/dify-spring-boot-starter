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
 * Test for {@link BaseChatRequest}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class BaseChatRequestTest {

    @Test
    public void testGetterAndSetter() {
        // Arrange
        BaseChatRequest request = new BaseChatRequest();
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
        BaseChatRequest request1 = new BaseChatRequest();
        request1.setApiKey("key1");
        request1.setUserId("user1");

        BaseChatRequest request2 = new BaseChatRequest();
        request2.setApiKey("key1");
        request2.setUserId("user1");

        BaseChatRequest request3 = new BaseChatRequest();
        request3.setApiKey("key2");
        request3.setUserId("user2");

        // Assert
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1, request3);
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        BaseChatRequest request = new BaseChatRequest();
        request.setApiKey("test-api-key");
        request.setUserId("test-user-id");

        // Act
        String toString = request.toString();

        // Assert
        assertTrue(toString.contains("apiKey=test-api-key"));
        assertTrue(toString.contains("userId=test-user-id"));
    }
}
