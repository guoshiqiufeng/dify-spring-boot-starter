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
package io.github.guoshiqiufeng.dify.workflow.dto.response.stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link WorkflowStartedData}
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/4/27
 */
public class WorkflowStartedDataTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testSettersAndGetters() {
        // Arrange
        String id = "run-123";
        Long createdAt = 1705395332L;
        String workflowId = "workflow-456";
        Integer sequenceNumber = 1;

        // Act
        WorkflowStartedData data = new WorkflowStartedData();
        data.setId(id);
        data.setCreatedAt(createdAt);
        data.setWorkflowId(workflowId);
        data.setSequenceNumber(sequenceNumber);

        // Assert
        assertEquals(id, data.getId());
        assertEquals(createdAt, data.getCreatedAt());
        assertEquals(workflowId, data.getWorkflowId());
        assertEquals(sequenceNumber, data.getSequenceNumber());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        WorkflowStartedData data1 = new WorkflowStartedData();
        data1.setId("run-123");
        data1.setCreatedAt(1705395332L);
        data1.setWorkflowId("workflow-456");
        data1.setSequenceNumber(1);

        WorkflowStartedData data2 = new WorkflowStartedData();
        data2.setId("run-123");
        data2.setCreatedAt(1705395332L);
        data2.setWorkflowId("workflow-456");
        data2.setSequenceNumber(1);

        WorkflowStartedData data3 = new WorkflowStartedData();
        data3.setId("run-789");
        data3.setCreatedAt(1705395332L);
        data3.setWorkflowId("workflow-999");
        data3.setSequenceNumber(2);

        // Assert
        assertEquals(data1, data2);
        assertEquals(data1.hashCode(), data2.hashCode());
        assertNotEquals(data1, data3);
        assertNotEquals(data1.hashCode(), data3.hashCode());
    }

    @Test
    public void testInheritance() {
        // Arrange
        WorkflowStartedData data = new WorkflowStartedData();

        // Assert
        assertInstanceOf(BaseWorkflowRunData.class, data);
    }

    @Test
    public void testJsonSerializationDeserialization() throws Exception {
        // Arrange
        WorkflowStartedData data = new WorkflowStartedData();
        data.setId("run-123");
        data.setCreatedAt(1705395332L);
        data.setWorkflowId("workflow-456");
        data.setSequenceNumber(1);

        // Act - Serialize
        String json = objectMapper.writeValueAsString(data);

        // Assert - Serialization
        assertTrue(json.contains("\"id\":\"run-123\""));
        assertTrue(json.contains("\"createdAt\":1705395332"));
        assertTrue(json.contains("\"workflowId\":\"workflow-456\""));
        assertTrue(json.contains("\"sequenceNumber\":1"));

        // Act - Deserialize
        WorkflowStartedData deserializedData = objectMapper.readValue(json, WorkflowStartedData.class);

        // Assert - Deserialization
        assertEquals(data.getId(), deserializedData.getId());
        assertEquals(data.getCreatedAt(), deserializedData.getCreatedAt());
        assertEquals(data.getWorkflowId(), deserializedData.getWorkflowId());
        assertEquals(data.getSequenceNumber(), deserializedData.getSequenceNumber());
    }

    @Test
    public void testJsonDeserializationWithJsonAlias() throws Exception {
        // Arrange - Use JsonAlias field names
        String json = "{\"id\":\"run-123\",\"created_at\":1705395332,\"workflow_id\":\"workflow-456\",\"sequence_number\":1}";

        // Act
        WorkflowStartedData data = objectMapper.readValue(json, WorkflowStartedData.class);

        // Assert
        assertEquals("run-123", data.getId());
        assertEquals(Long.valueOf(1705395332), data.getCreatedAt());
        assertEquals("workflow-456", data.getWorkflowId());
        assertEquals(Integer.valueOf(1), data.getSequenceNumber());
    }

    @Test
    public void testToString() {
        // Arrange
        WorkflowStartedData data = new WorkflowStartedData();
        data.setId("run-123");
        data.setCreatedAt(1705395332L);
        data.setWorkflowId("workflow-456");
        data.setSequenceNumber(1);

        // Act
        String toString = data.toString();

        // Assert
        assertTrue(toString.contains("id=run-123"));
        assertTrue(toString.contains("createdAt=1705395332"));
        assertTrue(toString.contains("workflowId=workflow-456"));
        assertTrue(toString.contains("sequenceNumber=1"));
    }
}
