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
package io.github.guoshiqiufeng.dify.workflow.dto.response;

import io.github.guoshiqiufeng.dify.workflow.dto.response.stream.WorkflowStartedData;
import io.github.guoshiqiufeng.dify.workflow.enums.StreamEventEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link WorkflowRunStreamResponse}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class WorkflowRunStreamResponseTest {

    @Test
    public void testGetterAndSetter() {
        // Arrange
        WorkflowRunStreamResponse response = new WorkflowRunStreamResponse();
        StreamEventEnum event = StreamEventEnum.workflow_started;
        String workflowRunId = "test-workflow-run-id";
        String taskId = "test-task-id";
        WorkflowStartedData data = new WorkflowStartedData();
        data.setWorkflowId("test-data-id");

        // Act
        response.setEvent(event);
        response.setWorkflowRunId(workflowRunId);
        response.setTaskId(taskId);
        response.setData(data);

        // Assert
        assertEquals(event, response.getEvent());
        assertEquals(workflowRunId, response.getWorkflowRunId());
        assertEquals(taskId, response.getTaskId());
        assertEquals(data, response.getData());
    }

    @Test
    public void testDefaultValues() {
        // Arrange
        WorkflowRunStreamResponse response = new WorkflowRunStreamResponse();

        // Assert
        assertNull(response.getEvent());
        assertNull(response.getWorkflowRunId());
        assertNull(response.getTaskId());
        assertNull(response.getData());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        WorkflowRunStreamResponse response1 = new WorkflowRunStreamResponse();
        response1.setEvent(StreamEventEnum.workflow_started);
        response1.setWorkflowRunId("run-id-1");
        response1.setTaskId("task-id-1");
        response1.setData("test-data-1");

        WorkflowRunStreamResponse response2 = new WorkflowRunStreamResponse();
        response2.setEvent(StreamEventEnum.workflow_started);
        response2.setWorkflowRunId("run-id-1");
        response2.setTaskId("task-id-1");
        response2.setData("test-data-1");

        WorkflowRunStreamResponse response3 = new WorkflowRunStreamResponse();
        response3.setEvent(StreamEventEnum.node_started);
        response3.setWorkflowRunId("run-id-2");
        response3.setTaskId("task-id-2");
        response3.setData("test-data-2");

        // Assert
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1, response3);
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        WorkflowRunStreamResponse response = new WorkflowRunStreamResponse();
        response.setEvent(StreamEventEnum.workflow_started);
        response.setWorkflowRunId("test-workflow-run-id");
        response.setTaskId("test-task-id");
        response.setData("test-data");

        // Act
        String toString = response.toString();

        // Assert
        assertTrue(toString.contains("event=workflow_started"));
        assertTrue(toString.contains("workflowRunId=test-workflow-run-id"));
        assertTrue(toString.contains("taskId=test-task-id"));
        assertTrue(toString.contains("data=test-data"));
    }
}
