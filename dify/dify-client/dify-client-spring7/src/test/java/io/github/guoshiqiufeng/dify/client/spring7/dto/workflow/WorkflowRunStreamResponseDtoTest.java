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
package io.github.guoshiqiufeng.dify.client.spring7.dto.workflow;

import io.github.guoshiqiufeng.dify.workflow.dto.response.WorkflowRunStreamResponse;
import io.github.guoshiqiufeng.dify.workflow.enums.StreamEventEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link WorkflowRunStreamResponseDto}.
 *
 * @author yanghq
 * @version 1.6.2
 * @since 2025/12/12 10:00
 */
class WorkflowRunStreamResponseDtoTest {

    @Test
    void testNoArgsConstructor() {
        // Given
        WorkflowRunStreamResponseDto dto = new WorkflowRunStreamResponseDto();

        // Then
        assertNotNull(dto);
        assertNull(dto.getData());
    }

    @Test
    void testAllArgsConstructor() {
        // Given
        WorkflowRunStreamResponse data = new WorkflowRunStreamResponse();
        WorkflowRunStreamResponseDto dto = new WorkflowRunStreamResponseDto(data);

        // Then
        assertNotNull(dto);
        assertEquals(data, dto.getData());
    }

    @Test
    void testDataSetterAndGetter() {
        // Given
        WorkflowRunStreamResponseDto dto = new WorkflowRunStreamResponseDto();
        WorkflowRunStreamResponse data = new WorkflowRunStreamResponse();

        // When
        dto.setData(data);

        // Then
        assertEquals(data, dto.getData());
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        WorkflowRunStreamResponse data1 = new WorkflowRunStreamResponse();
        data1.setEvent(StreamEventEnum.workflow_started); // Using valid enum value
        data1.setTaskId("task-id-1");

        WorkflowRunStreamResponse data2 = new WorkflowRunStreamResponse();
        data2.setEvent(StreamEventEnum.workflow_finished);  // Different enum value
        data2.setTaskId("task-id-2"); // Different value

        WorkflowRunStreamResponseDto dto1 = new WorkflowRunStreamResponseDto(data1);
        WorkflowRunStreamResponseDto dto2 = new WorkflowRunStreamResponseDto(data1); // Same data as dto1
        WorkflowRunStreamResponseDto dto3 = new WorkflowRunStreamResponseDto(data2); // Different data

        // Then
        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        WorkflowRunStreamResponse data = new WorkflowRunStreamResponse();
        WorkflowRunStreamResponseDto dto = new WorkflowRunStreamResponseDto(data);

        // When
        String toString = dto.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("WorkflowRunStreamResponseDto"));
    }
}