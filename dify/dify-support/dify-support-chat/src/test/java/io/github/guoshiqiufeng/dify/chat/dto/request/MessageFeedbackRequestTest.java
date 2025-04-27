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
 * Test for {@link MessageFeedbackRequest}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class MessageFeedbackRequestTest {

    @Test
    public void testGetterAndSetter() {
        // Arrange
        MessageFeedbackRequest request = new MessageFeedbackRequest();
        String messageId = "test-message-id";
        MessageFeedbackRequest.Rating rating = MessageFeedbackRequest.Rating.LIKE;
        String content = "test feedback content";

        // Act
        request.setMessageId(messageId);
        request.setRating(rating);
        request.setContent(content);

        // Assert
        assertEquals(messageId, request.getMessageId());
        assertEquals(rating, request.getRating());
        assertEquals(content, request.getContent());
    }

    @Test
    public void testInheritedFields() {
        // Arrange
        MessageFeedbackRequest request = new MessageFeedbackRequest();
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
        MessageFeedbackRequest request1 = new MessageFeedbackRequest();
        request1.setMessageId("msg1");
        request1.setRating(MessageFeedbackRequest.Rating.LIKE);
        request1.setContent("content1");

        MessageFeedbackRequest request2 = new MessageFeedbackRequest();
        request2.setMessageId("msg1");
        request2.setRating(MessageFeedbackRequest.Rating.LIKE);
        request2.setContent("content1");

        MessageFeedbackRequest request3 = new MessageFeedbackRequest();
        request3.setMessageId("msg2");
        request3.setRating(MessageFeedbackRequest.Rating.DISLIKE);
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
        MessageFeedbackRequest request = new MessageFeedbackRequest();
        request.setMessageId("test-msg-id");
        request.setRating(MessageFeedbackRequest.Rating.LIKE);
        request.setContent("test content");

        // Act
        String toString = request.toString();

        // Assert
        assertTrue(toString.contains("messageId=test-msg-id"));
        assertTrue(toString.contains("rating=" + MessageFeedbackRequest.Rating.LIKE));
        assertTrue(toString.contains("content=test content"));
    }

    @Test
    public void testRatingEnum() {
        // Test enum values
        assertEquals("like", MessageFeedbackRequest.Rating.LIKE.getKey());
        assertEquals("dislike", MessageFeedbackRequest.Rating.DISLIKE.getKey());
        assertNull(MessageFeedbackRequest.Rating.UNLIKE.getKey());

        // Test keyOf method
        assertEquals(MessageFeedbackRequest.Rating.LIKE, MessageFeedbackRequest.Rating.keyOf("like"));
        assertEquals(MessageFeedbackRequest.Rating.DISLIKE, MessageFeedbackRequest.Rating.keyOf("dislike"));
        assertNull(MessageFeedbackRequest.Rating.keyOf(null));
        assertNull(MessageFeedbackRequest.Rating.keyOf("invalid"));
    }
}
