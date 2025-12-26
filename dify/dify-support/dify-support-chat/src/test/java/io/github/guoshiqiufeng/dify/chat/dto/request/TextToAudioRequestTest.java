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
 * Test for {@link TextToAudioRequest}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class TextToAudioRequestTest {

    @Test
    public void testGetterAndSetter() {
        // Arrange
        TextToAudioRequest request = new TextToAudioRequest();
        String text = "test text content";
        String messageId = "test-message-id";

        // Act
        request.setText(text);
        request.setMessageId(messageId);

        // Assert
        assertEquals(text, request.getText());
        assertEquals(messageId, request.getMessageId());
    }

    @Test
    public void testInheritedFields() {
        // Arrange
        TextToAudioRequest request = new TextToAudioRequest();
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
        TextToAudioRequest request1 = new TextToAudioRequest();
        request1.setText("text1");
        request1.setMessageId("msg1");

        TextToAudioRequest request2 = new TextToAudioRequest();
        request2.setText("text1");
        request2.setMessageId("msg1");

        TextToAudioRequest request3 = new TextToAudioRequest();
        request3.setText("text2");
        request3.setMessageId("msg2");

        // Assert
        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1, request3);
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        TextToAudioRequest request = new TextToAudioRequest();
        request.setText("test text");
        request.setMessageId("test-msg-id");

        // Act
        String toString = request.toString();

        // Assert
        assertTrue(toString.contains("text=test text"));
        assertTrue(toString.contains("messageId=test-msg-id"));
    }
}
