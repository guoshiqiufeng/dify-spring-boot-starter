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
package io.github.guoshiqiufeng.dify.workflow.dto.response.stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link WorkflowFinishedData}
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/4/27
 */
public class WorkflowFinishedDataTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testSettersAndGetters() {
        // Arrange
        String id = "run-123";
        Long createdAt = 1705395332L;
        String workflowId = "workflow-456";
        Map<String, Object> outputs = new HashMap<>();
        outputs.put("result", "success");
        String status = "succeeded";
        String error = null;
        Float elapsedTime = 2.5f;
        Integer totalTokens = 150;
        Integer totalSteps = 3;
        Long finishedAt = 1705395335L;

        // Act
        WorkflowFinishedData data = new WorkflowFinishedData();
        data.setId(id);
        data.setCreatedAt(createdAt);
        data.setWorkflowId(workflowId);
        data.setOutputs(outputs);
        data.setStatus(status);
        data.setError(error);
        data.setElapsedTime(elapsedTime);
        data.setTotalTokens(totalTokens);
        data.setTotalSteps(totalSteps);
        data.setFinishedAt(finishedAt);

        // Assert
        assertEquals(id, data.getId());
        assertEquals(createdAt, data.getCreatedAt());
        assertEquals(workflowId, data.getWorkflowId());
        assertEquals(outputs, data.getOutputs());
        assertEquals(status, data.getStatus());
        assertEquals(error, data.getError());
        assertEquals(elapsedTime, data.getElapsedTime());
        assertEquals(totalTokens, data.getTotalTokens());
        assertEquals(totalSteps, data.getTotalSteps());
        assertEquals(finishedAt, data.getFinishedAt());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        WorkflowFinishedData data1 = new WorkflowFinishedData();
        data1.setId("run-123");
        data1.setCreatedAt(1705395332L);
        data1.setWorkflowId("workflow-456");
        data1.setStatus("succeeded");
        data1.setFinishedAt(1705395335L);

        WorkflowFinishedData data2 = new WorkflowFinishedData();
        data2.setId("run-123");
        data2.setCreatedAt(1705395332L);
        data2.setWorkflowId("workflow-456");
        data2.setStatus("succeeded");
        data2.setFinishedAt(1705395335L);

        WorkflowFinishedData data3 = new WorkflowFinishedData();
        data3.setId("run-789");
        data3.setCreatedAt(1705395332L);
        data3.setWorkflowId("workflow-999");
        data3.setStatus("failed");
        data3.setFinishedAt(1705395340L);

        // Assert
        assertEquals(data1, data2);
        assertEquals(data1.hashCode(), data2.hashCode());
        assertNotEquals(data1, data3);
        assertNotEquals(data1.hashCode(), data3.hashCode());
    }

    @Test
    public void testInheritance() {
        // Arrange
        WorkflowFinishedData data = new WorkflowFinishedData();

        // Assert
        assertInstanceOf(BaseWorkflowRunData.class, data);
    }

    @Test
    public void testJsonSerializationDeserialization() throws Exception {
        // Arrange
        WorkflowFinishedData data = new WorkflowFinishedData();
        data.setId("run-123");
        data.setCreatedAt(1705395332L);
        data.setWorkflowId("workflow-456");
        Map<String, Object> outputs = new HashMap<>();
        outputs.put("result", "success");
        data.setOutputs(outputs);
        data.setStatus("succeeded");
        data.setElapsedTime(2.5f);
        data.setTotalTokens(150);
        data.setTotalSteps(3);
        data.setFinishedAt(1705395335L);

        // Act - Serialize
        String json = objectMapper.writeValueAsString(data);

        // Assert - Serialization
        assertTrue(json.contains("\"id\":\"run-123\""));
        assertTrue(json.contains("\"createdAt\":1705395332"));
        assertTrue(json.contains("\"workflowId\":\"workflow-456\""));
        assertTrue(json.contains("\"outputs\":{\"result\":\"success\"}"));
        assertTrue(json.contains("\"status\":\"succeeded\""));
        assertTrue(json.contains("\"elapsedTime\":2.5"));
        assertTrue(json.contains("\"totalTokens\":150"));
        assertTrue(json.contains("\"totalSteps\":3"));
        assertTrue(json.contains("\"finishedAt\":1705395335"));

        // Act - Deserialize
        WorkflowFinishedData deserializedData = objectMapper.readValue(json, WorkflowFinishedData.class);

        // Assert - Deserialization
        assertEquals(data.getId(), deserializedData.getId());
        assertEquals(data.getCreatedAt(), deserializedData.getCreatedAt());
        assertEquals(data.getWorkflowId(), deserializedData.getWorkflowId());
        assertEquals(data.getOutputs().get("result"), deserializedData.getOutputs().get("result"));
        assertEquals(data.getStatus(), deserializedData.getStatus());
        assertEquals(data.getElapsedTime(), deserializedData.getElapsedTime());
        assertEquals(data.getTotalTokens(), deserializedData.getTotalTokens());
        assertEquals(data.getTotalSteps(), deserializedData.getTotalSteps());
        assertEquals(data.getFinishedAt(), deserializedData.getFinishedAt());
    }

    @Test
    public void testJsonDeserializationWithJsonAlias() throws Exception {
        // Arrange - Use JsonAlias field names
        String json = "{\"id\":\"run-123\",\"created_at\":1705395332,\"workflow_id\":\"workflow-456\"," +
                "\"outputs\":{\"result\":\"success\"},\"status\":\"succeeded\",\"error\":null," +
                "\"elapsed_time\":2.5,\"total_tokens\":150,\"total_steps\":3,\"finished_at\":1705395335}";

        // Act
        WorkflowFinishedData data = objectMapper.readValue(json, WorkflowFinishedData.class);

        // Assert
        assertEquals("run-123", data.getId());
        assertEquals(Long.valueOf(1705395332), data.getCreatedAt());
        assertEquals("workflow-456", data.getWorkflowId());
        assertEquals("success", data.getOutputs().get("result"));
        assertEquals("succeeded", data.getStatus());
        assertNull(data.getError());
        assertEquals(2.5f, data.getElapsedTime());
        assertEquals(Integer.valueOf(150), data.getTotalTokens());
        assertEquals(Integer.valueOf(3), data.getTotalSteps());
        assertEquals(Long.valueOf(1705395335), data.getFinishedAt());
    }

    @Test
    public void testToString() {
        // Arrange
        WorkflowFinishedData data = new WorkflowFinishedData();
        data.setId("run-123");
        data.setCreatedAt(1705395332L);
        data.setWorkflowId("workflow-456");
        data.setStatus("succeeded");
        data.setElapsedTime(2.5f);
        data.setFinishedAt(1705395335L);

        // Act
        String toString = data.toString();

        // Assert
        assertTrue(toString.contains("id=run-123"));
        assertTrue(toString.contains("createdAt=1705395332"));
        assertTrue(toString.contains("workflowId=workflow-456"));
        assertTrue(toString.contains("status=succeeded"));
        assertTrue(toString.contains("elapsedTime=2.5"));
        assertTrue(toString.contains("finishedAt=1705395335"));
    }
}
