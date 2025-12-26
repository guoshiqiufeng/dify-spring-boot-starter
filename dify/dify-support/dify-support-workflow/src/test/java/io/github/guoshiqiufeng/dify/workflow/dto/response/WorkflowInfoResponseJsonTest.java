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
package io.github.guoshiqiufeng.dify.workflow.dto.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link WorkflowInfoResponse} JSON serialization/deserialization
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/4/27
 */
public class WorkflowInfoResponseJsonTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testJsonDeserializationWithJsonAlias() throws Exception {
        // Arrange - Use JsonAlias field names
        String json = "{" +
                "\"id\":\"run-123\"," +
                "\"workflow_id\":\"workflow-456\"," +
                "\"status\":\"succeeded\"," +
                "\"inputs\":\"{\\\"text\\\":\\\"Hello, world!\\\"}\"," +
                "\"outputs\":\"{\\\"result\\\":\\\"Processed: Hello, world!\\\"}\"," +
                "\"elapsed_time\":2.5," +
                "\"total_tokens\":150," +
                "\"total_steps\":3," +
                "\"created_at\":1705395332," +
                "\"finished_at\":1705395335" +
                "}";

        // Act
        WorkflowInfoResponse response = objectMapper.readValue(json, WorkflowInfoResponse.class);

        // Assert
        assertEquals("run-123", response.getId());
        assertEquals("workflow-456", response.getWorkflowId());
        assertEquals("succeeded", response.getStatus());
        assertEquals("Hello, world!", response.getInputs().get("text"));
        assertEquals("Processed: Hello, world!", response.getOutputs().get("result"));
        assertEquals(2.5f, response.getElapsedTime());
        assertEquals(Integer.valueOf(150), response.getTotalTokens());
        assertEquals(Integer.valueOf(3), response.getTotalSteps());
        assertEquals(Long.valueOf(1705395332), response.getCreatedAt());
        assertEquals(Long.valueOf(1705395335), response.getFinishedAt());
    }

    @Test
    public void testJsonDeserializationWithDateString() throws Exception {
        // Arrange - Using date strings in RFC 822 format for timestamps
        String json = "{" +
                "\"id\":\"run-123\"," +
                "\"workflow_id\":\"workflow-456\"," +
                "\"status\":\"succeeded\"," +
                "\"created_at\":\"Wed, 16 Jan 2024 12:15:32 +0000\"," +
                "\"finished_at\":\"Wed, 16 Jan 2024 12:16:40 +0000\"" +
                "}";

        // Act
        WorkflowInfoResponse response = objectMapper.readValue(json, WorkflowInfoResponse.class);

        // Assert
        assertEquals("run-123", response.getId());
        assertEquals("workflow-456", response.getWorkflowId());
        assertEquals("succeeded", response.getStatus());

        // We don't know the exact expected timestamp without duplicating the parsing logic,
        // but we can check that timestamps were correctly parsed to non-null values and the
        // finished time is greater than the created time
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getFinishedAt());
        assertTrue(response.getFinishedAt() > response.getCreatedAt());
    }

    @Test
    public void testJsonDeserializationWithEmptyFields() throws Exception {
        // Arrange - Test with empty inputs and outputs
        String json = "{" +
                "\"id\":\"run-123\"," +
                "\"workflow_id\":\"workflow-456\"," +
                "\"status\":\"succeeded\"," +
                "\"inputs\":\"\"," +
                "\"outputs\":\"\"," +
                "\"created_at\":1705395332," +
                "\"finished_at\":1705395335" +
                "}";

        // Act
        WorkflowInfoResponse response = objectMapper.readValue(json, WorkflowInfoResponse.class);

        // Assert
        assertEquals("run-123", response.getId());
        assertEquals("workflow-456", response.getWorkflowId());
        assertEquals("succeeded", response.getStatus());
        assertNull(response.getInputs());
        assertNull(response.getOutputs());
        assertEquals(Long.valueOf(1705395332), response.getCreatedAt());
        assertEquals(Long.valueOf(1705395335), response.getFinishedAt());
    }

    @Test
    public void testJsonDeserializationWithFailedStatus() throws Exception {
        // Arrange - Test with failed status and error message
        String json = "{" +
                "\"id\":\"run-123\"," +
                "\"workflow_id\":\"workflow-456\"," +
                "\"status\":\"failed\"," +
                "\"error\":\"Workflow execution failed due to runtime error\"," +
                "\"created_at\":1705395332," +
                "\"finished_at\":1705395335" +
                "}";

        // Act
        WorkflowInfoResponse response = objectMapper.readValue(json, WorkflowInfoResponse.class);

        // Assert
        assertEquals("run-123", response.getId());
        assertEquals("workflow-456", response.getWorkflowId());
        assertEquals("failed", response.getStatus());
        assertEquals("Workflow execution failed due to runtime error", response.getError());
        assertEquals(Long.valueOf(1705395332), response.getCreatedAt());
        assertEquals(Long.valueOf(1705395335), response.getFinishedAt());
    }

    @Test
    public void testJsonDeserializationWithComplexInputsOutputs() throws Exception {
        // Arrange - Test with complex nested inputs and outputs
        String json = "{" +
                "\"id\":\"run-123\"," +
                "\"workflow_id\":\"workflow-456\"," +
                "\"status\":\"succeeded\"," +
                "\"inputs\":\"{\\\"user_query\\\":\\\"What is machine learning?\\\",\\\"parameters\\\":{\\\"max_tokens\\\":1000,\\\"temperature\\\":0.7}}\"," +
                "\"outputs\":\"{\\\"llm_response\\\":{\\\"text\\\":\\\"Machine learning is a subfield of artificial intelligence...\\\",\\\"tokens_used\\\":145},\\\"metadata\\\":{\\\"model\\\":\\\"gpt-4\\\",\\\"version\\\":\\\"1.0\\\"}}\"," +
                "\"created_at\":1705395332," +
                "\"finished_at\":1705395335" +
                "}";

        // Act
        WorkflowInfoResponse response = objectMapper.readValue(json, WorkflowInfoResponse.class);

        // Assert
        assertEquals("run-123", response.getId());
        assertEquals("workflow-456", response.getWorkflowId());
        assertEquals("succeeded", response.getStatus());

        // Check inputs
        assertNotNull(response.getInputs());
        assertEquals("What is machine learning?", response.getInputs().get("user_query"));
        @SuppressWarnings("unchecked")
        Map<String, Object> parameters = (Map<String, Object>) response.getInputs().get("parameters");
        assertNotNull(parameters);
        assertEquals(1000, parameters.get("max_tokens"));
        assertEquals(0.7, parameters.get("temperature"));

        // Check outputs
        assertNotNull(response.getOutputs());
        @SuppressWarnings("unchecked")
        Map<String, Object> llmResponse = (Map<String, Object>) response.getOutputs().get("llm_response");
        assertNotNull(llmResponse);
        assertEquals("Machine learning is a subfield of artificial intelligence...", llmResponse.get("text"));
        assertEquals(145, llmResponse.get("tokens_used"));

        @SuppressWarnings("unchecked")
        Map<String, Object> metadata = (Map<String, Object>) response.getOutputs().get("metadata");
        assertNotNull(metadata);
        assertEquals("gpt-4", metadata.get("model"));
        assertEquals("1.0", metadata.get("version"));
    }

    @Test
    public void testFullCycleSerialization() throws Exception {
        // Arrange
        WorkflowInfoResponse originalResponse = new WorkflowInfoResponse();
        originalResponse.setId("run-123");
        originalResponse.setWorkflowId("workflow-456");
        originalResponse.setStatus("succeeded");
        originalResponse.setElapsedTime(2.5f);
        originalResponse.setTotalTokens(150);
        originalResponse.setTotalSteps(3);
        originalResponse.setCreatedAt(1705395332L);
        originalResponse.setFinishedAt(1705395335L);

        // Act - Serialize then deserialize
        String json = objectMapper.writeValueAsString(originalResponse);
        WorkflowInfoResponse deserializedResponse = objectMapper.readValue(json, WorkflowInfoResponse.class);

        // Assert
        assertEquals(originalResponse.getId(), deserializedResponse.getId());
        assertEquals(originalResponse.getWorkflowId(), deserializedResponse.getWorkflowId());
        assertEquals(originalResponse.getStatus(), deserializedResponse.getStatus());
        assertEquals(originalResponse.getElapsedTime(), deserializedResponse.getElapsedTime());
        assertEquals(originalResponse.getTotalTokens(), deserializedResponse.getTotalTokens());
        assertEquals(originalResponse.getTotalSteps(), deserializedResponse.getTotalSteps());
        assertEquals(originalResponse.getCreatedAt(), deserializedResponse.getCreatedAt());
        assertEquals(originalResponse.getFinishedAt(), deserializedResponse.getFinishedAt());
    }
}
