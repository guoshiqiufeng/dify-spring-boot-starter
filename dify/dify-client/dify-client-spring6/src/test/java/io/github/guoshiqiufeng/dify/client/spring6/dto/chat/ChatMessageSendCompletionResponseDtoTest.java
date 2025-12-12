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
package io.github.guoshiqiufeng.dify.client.spring6.dto.chat;

import io.github.guoshiqiufeng.dify.chat.dto.response.ChatMessageSendCompletionResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ChatMessageSendCompletionResponseDto}.
 *
 * @author yanghq
 * @version 1.6.2
 * @since 2025/12/12 10:00
 */
class ChatMessageSendCompletionResponseDtoTest {

    @Test
    void testNoArgsConstructor() {
        // Given
        ChatMessageSendCompletionResponseDto dto = new ChatMessageSendCompletionResponseDto();

        // Then
        assertNotNull(dto);
        assertNull(dto.getData());
    }

    @Test
    void testAllArgsConstructor() {
        // Given
        ChatMessageSendCompletionResponse data = new ChatMessageSendCompletionResponse();
        ChatMessageSendCompletionResponseDto dto = new ChatMessageSendCompletionResponseDto(data);

        // Then
        assertNotNull(dto);
        assertEquals(data, dto.getData());
    }

    @Test
    void testDataSetterAndGetter() {
        // Given
        ChatMessageSendCompletionResponseDto dto = new ChatMessageSendCompletionResponseDto();
        ChatMessageSendCompletionResponse data = new ChatMessageSendCompletionResponse();

        // When
        dto.setData(data);

        // Then
        assertEquals(data, dto.getData());
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        ChatMessageSendCompletionResponse data1 = new ChatMessageSendCompletionResponse();
        data1.setWorkflowRunId("workflow-run-id-1");
        data1.setAnswer("answer-1");

        ChatMessageSendCompletionResponse data2 = new ChatMessageSendCompletionResponse();
        data2.setWorkflowRunId("workflow-run-id-2");  // Different value
        data2.setAnswer("answer-2");                  // Different value

        ChatMessageSendCompletionResponseDto dto1 = new ChatMessageSendCompletionResponseDto(data1);
        ChatMessageSendCompletionResponseDto dto2 = new ChatMessageSendCompletionResponseDto(data1); // Same data as dto1
        ChatMessageSendCompletionResponseDto dto3 = new ChatMessageSendCompletionResponseDto(data2); // Different data

        // Then
        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        ChatMessageSendCompletionResponse data = new ChatMessageSendCompletionResponse();
        ChatMessageSendCompletionResponseDto dto = new ChatMessageSendCompletionResponseDto(data);

        // When
        String toString = dto.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("ChatMessageSendCompletionResponseDto"));
    }
}