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
package io.github.guoshiqiufeng.dify.chat.dto.response.message;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link WorkflowFinishedData}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class WorkflowFinishedDataTest {

    @Test
    public void testGetterAndSetter() {
        // Arrange
        WorkflowFinishedData workflowFinishedData = new WorkflowFinishedData();
        String id = "test-id";
        String workflowId = "test-workflow-id";
        Map<String, Object> outputs = new HashMap<>();
        outputs.put("key1", "value1");
        outputs.put("key2", 2);
        String status = "completed";
        String error = null;
        Integer elapsedTime = 1000;
        Integer totalTokens = 500;
        Integer totalSteps = 5;
        Long createdAt = 1619517600000L; // 2021-04-27 12:00:00 UTC

        // Act
        workflowFinishedData.setId(id);
        workflowFinishedData.setWorkflowId(workflowId);
        workflowFinishedData.setOutputs(outputs);
        workflowFinishedData.setStatus(status);
        workflowFinishedData.setError(error);
        workflowFinishedData.setElapsedTime(elapsedTime);
        workflowFinishedData.setTotalTokens(totalTokens);
        workflowFinishedData.setTotalSteps(totalSteps);
        workflowFinishedData.setCreatedAt(createdAt);

        // Assert
        assertEquals(id, workflowFinishedData.getId());
        assertEquals(workflowId, workflowFinishedData.getWorkflowId());
        assertEquals(outputs, workflowFinishedData.getOutputs());
        assertEquals(status, workflowFinishedData.getStatus());
        assertEquals(error, workflowFinishedData.getError());
        assertEquals(elapsedTime, workflowFinishedData.getElapsedTime());
        assertEquals(totalTokens, workflowFinishedData.getTotalTokens());
        assertEquals(totalSteps, workflowFinishedData.getTotalSteps());
        assertEquals(createdAt, workflowFinishedData.getCreatedAt());
    }

    @Test
    public void testDefaultValues() {
        // Arrange
        WorkflowFinishedData workflowFinishedData = new WorkflowFinishedData();

        // Assert
        assertNull(workflowFinishedData.getId());
        assertNull(workflowFinishedData.getWorkflowId());
        assertNull(workflowFinishedData.getOutputs());
        assertNull(workflowFinishedData.getStatus());
        assertNull(workflowFinishedData.getError());
        assertNull(workflowFinishedData.getElapsedTime());
        assertNull(workflowFinishedData.getTotalTokens());
        assertNull(workflowFinishedData.getTotalSteps());
        assertNull(workflowFinishedData.getCreatedAt());
    }

    @Test
    public void testToString() {
        // Arrange
        WorkflowFinishedData workflowFinishedData = new WorkflowFinishedData();
        workflowFinishedData.setId("test-id");
        workflowFinishedData.setWorkflowId("test-workflow-id");
        workflowFinishedData.setStatus("completed");
        workflowFinishedData.setElapsedTime(1000);
        workflowFinishedData.setTotalTokens(500);
        workflowFinishedData.setTotalSteps(5);

        // Act
        String toString = workflowFinishedData.toString();

        // Assert
        assertTrue(toString.contains("id=test-id"));
        assertTrue(toString.contains("workflowId=test-workflow-id"));
        assertTrue(toString.contains("status=completed"));
        assertTrue(toString.contains("elapsedTime=1000"));
        assertTrue(toString.contains("totalTokens=500"));
        assertTrue(toString.contains("totalSteps=5"));
    }
}
