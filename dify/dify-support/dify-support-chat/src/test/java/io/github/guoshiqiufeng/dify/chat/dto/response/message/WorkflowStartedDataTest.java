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
package io.github.guoshiqiufeng.dify.chat.dto.response.message;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link WorkflowStartedData}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class WorkflowStartedDataTest {

    @Test
    public void testGetterAndSetter() {
        // Arrange
        WorkflowStartedData workflowStartedData = new WorkflowStartedData();
        String id = "test-id";
        String workflowId = "test-workflow-id";
        Integer sequenceNumber = 1;
        Long createdAt = 1619517600000L; // 2021-04-27 12:00:00 UTC

        // Act
        workflowStartedData.setId(id);
        workflowStartedData.setWorkflowId(workflowId);
        workflowStartedData.setSequenceNumber(sequenceNumber);
        workflowStartedData.setCreatedAt(createdAt);

        // Assert
        assertEquals(id, workflowStartedData.getId());
        assertEquals(workflowId, workflowStartedData.getWorkflowId());
        assertEquals(sequenceNumber, workflowStartedData.getSequenceNumber());
        assertEquals(createdAt, workflowStartedData.getCreatedAt());
    }

    @Test
    public void testDefaultValues() {
        // Arrange
        WorkflowStartedData workflowStartedData = new WorkflowStartedData();

        // Assert
        assertNull(workflowStartedData.getId());
        assertNull(workflowStartedData.getWorkflowId());
        assertNull(workflowStartedData.getSequenceNumber());
        assertNull(workflowStartedData.getCreatedAt());
    }

    @Test
    public void testToString() {
        // Arrange
        WorkflowStartedData workflowStartedData = new WorkflowStartedData();
        workflowStartedData.setId("test-id");
        workflowStartedData.setWorkflowId("test-workflow-id");
        workflowStartedData.setSequenceNumber(1);
        workflowStartedData.setCreatedAt(1619517600000L);

        // Act
        String toString = workflowStartedData.toString();

        // Assert
        assertTrue(toString.contains("id=test-id"));
        assertTrue(toString.contains("workflowId=test-workflow-id"));
        assertTrue(toString.contains("sequenceNumber=1"));
        assertTrue(toString.contains("createdAt=1619517600000"));
    }
}
