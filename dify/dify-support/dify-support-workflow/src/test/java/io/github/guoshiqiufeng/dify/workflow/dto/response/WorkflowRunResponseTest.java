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

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link WorkflowRunResponse}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class WorkflowRunResponseTest {

    @Test
    public void testGetterAndSetter() {
        // Arrange
        WorkflowRunResponse response = new WorkflowRunResponse();
        String workflowRunId = "test-workflow-run-id";
        String taskId = "test-task-id";
        WorkflowRunResponse.WorkflowRunData data = new WorkflowRunResponse.WorkflowRunData();

        // Act
        response.setWorkflowRunId(workflowRunId);
        response.setTaskId(taskId);
        response.setData(data);

        // Assert
        assertEquals(workflowRunId, response.getWorkflowRunId());
        assertEquals(taskId, response.getTaskId());
        assertEquals(data, response.getData());
    }

    @Test
    public void testDefaultValues() {
        // Arrange
        WorkflowRunResponse response = new WorkflowRunResponse();

        // Assert
        assertNull(response.getWorkflowRunId());
        assertNull(response.getTaskId());
        assertNull(response.getData());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        WorkflowRunResponse response1 = new WorkflowRunResponse();
        response1.setWorkflowRunId("run-id-1");
        response1.setTaskId("task-id-1");

        WorkflowRunResponse response2 = new WorkflowRunResponse();
        response2.setWorkflowRunId("run-id-1");
        response2.setTaskId("task-id-1");

        WorkflowRunResponse response3 = new WorkflowRunResponse();
        response3.setWorkflowRunId("run-id-2");
        response3.setTaskId("task-id-2");

        // Assert
        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1, response3);
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        WorkflowRunResponse response = new WorkflowRunResponse();
        response.setWorkflowRunId("test-workflow-run-id");
        response.setTaskId("test-task-id");

        // Act
        String toString = response.toString();

        // Assert
        assertTrue(toString.contains("workflowRunId=test-workflow-run-id"));
        assertTrue(toString.contains("taskId=test-task-id"));
    }

    @Test
    public void testWorkflowRunDataGetterAndSetter() {
        // Arrange
        WorkflowRunResponse.WorkflowRunData data = new WorkflowRunResponse.WorkflowRunData();
        String id = "test-id";
        String workflowId = "test-workflow-id";
        String status = "succeeded";
        Map<String, Object> outputs = new HashMap<>();
        outputs.put("result", "Test result");
        String error = null;
        Float elapsedTime = 1.5f;
        Integer totalTokens = 100;
        Integer totalSteps = 5;
        Long createdAt = 1619517600000L; // 2021-04-27 12:00:00 UTC
        Long finishedAt = 1619517605000L; // 2021-04-27 12:00:05 UTC

        // Act
        data.setId(id);
        data.setWorkflowId(workflowId);
        data.setStatus(status);
        data.setOutputs(outputs);
        data.setError(error);
        data.setElapsedTime(elapsedTime);
        data.setTotalTokens(totalTokens);
        data.setTotalSteps(totalSteps);
        data.setCreatedAt(createdAt);
        data.setFinishedAt(finishedAt);

        // Assert
        assertEquals(id, data.getId());
        assertEquals(workflowId, data.getWorkflowId());
        assertEquals(status, data.getStatus());
        assertEquals(outputs, data.getOutputs());
        assertEquals(error, data.getError());
        assertEquals(elapsedTime, data.getElapsedTime());
        assertEquals(totalTokens, data.getTotalTokens());
        assertEquals(totalSteps, data.getTotalSteps());
        assertEquals(createdAt, data.getCreatedAt());
        assertEquals(finishedAt, data.getFinishedAt());
    }

    @Test
    public void testWorkflowRunDataDefaultValues() {
        // Arrange
        WorkflowRunResponse.WorkflowRunData data = new WorkflowRunResponse.WorkflowRunData();

        // Assert
        assertNull(data.getId());
        assertNull(data.getWorkflowId());
        assertNull(data.getStatus());
        assertNull(data.getOutputs());
        assertNull(data.getError());
        assertNull(data.getElapsedTime());
        assertNull(data.getTotalTokens());
        assertNull(data.getTotalSteps());
        assertNull(data.getCreatedAt());
        assertNull(data.getFinishedAt());
    }

    @Test
    public void testWorkflowRunDataEqualsAndHashCode() {
        // Arrange
        WorkflowRunResponse.WorkflowRunData data1 = new WorkflowRunResponse.WorkflowRunData();
        data1.setId("id-1");
        data1.setWorkflowId("workflow-id-1");
        data1.setStatus("succeeded");

        WorkflowRunResponse.WorkflowRunData data2 = new WorkflowRunResponse.WorkflowRunData();
        data2.setId("id-1");
        data2.setWorkflowId("workflow-id-1");
        data2.setStatus("succeeded");

        WorkflowRunResponse.WorkflowRunData data3 = new WorkflowRunResponse.WorkflowRunData();
        data3.setId("id-2");
        data3.setWorkflowId("workflow-id-2");
        data3.setStatus("failed");

        // Assert
        assertEquals(data1, data2);
        assertEquals(data1.hashCode(), data2.hashCode());
        assertNotEquals(data1, data3);
        assertNotEquals(data1.hashCode(), data3.hashCode());
    }

    @Test
    public void testWorkflowRunDataToString() {
        // Arrange
        WorkflowRunResponse.WorkflowRunData data = new WorkflowRunResponse.WorkflowRunData();
        data.setId("test-id");
        data.setWorkflowId("test-workflow-id");
        data.setStatus("succeeded");
        data.setElapsedTime(1.5f);
        data.setTotalTokens(100);
        data.setTotalSteps(5);

        // Act
        String toString = data.toString();

        // Assert
        assertTrue(toString.contains("id=test-id"));
        assertTrue(toString.contains("workflowId=test-workflow-id"));
        assertTrue(toString.contains("status=succeeded"));
        assertTrue(toString.contains("elapsedTime=1.5"));
        assertTrue(toString.contains("totalTokens=100"));
        assertTrue(toString.contains("totalSteps=5"));
    }
} 