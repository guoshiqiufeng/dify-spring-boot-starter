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
package io.github.guoshiqiufeng.dify.workflow.dto.response.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.github.guoshiqiufeng.dify.workflow.dto.response.WorkflowRunStreamResponse;
import io.github.guoshiqiufeng.dify.workflow.dto.response.stream.NodeStartedData;
import io.github.guoshiqiufeng.dify.workflow.dto.response.stream.WorkflowStartedData;
import io.github.guoshiqiufeng.dify.workflow.enums.StreamEventEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

/**
 * Test for {@link WorkflowRunStreamResponseDeserializer}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/28
 */
@SuppressWarnings("unchecked")
public final class WorkflowRunStreamResponseDeserializerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testDeserializeNodeStartedData() throws IOException {
        // Prepare test JSON
        String json = "{\n" +
                "  \"event\": \"node_started\",\n" +
                "  \"workflow_run_id\": \"wfr-123456\",\n" +
                "  \"task_id\": \"task-123456\",\n" +
                "  \"data\": {\n" +
                "    \"id\": \"test-id-123\",\n" +
                "    \"node_id\": \"node-123\",\n" +
                "    \"node_type\": \"text\",\n" +
                "    \"title\": \"Test Node\",\n" +
                "    \"index\": 1,\n" +
                "    \"predecessor_node_id\": \"prev-node-456\",\n" +
                "    \"inputs\": {\n" +
                "      \"key1\": \"value1\",\n" +
                "      \"key2\": 123\n" +
                "    },\n" +
                "    \"created_at\": 1681556000000\n" +
                "  }\n" +
                "}";

        // Register custom deserializer module
        objectMapper.registerModule(new WorkflowRunStreamResponseModule());

        // Deserialize JSON
        WorkflowRunStreamResponse response = objectMapper.readValue(json, WorkflowRunStreamResponse.class);

        // Assertions
        Assertions.assertNotNull(response);
        Assertions.assertEquals(StreamEventEnum.node_started, response.getEvent());
        Assertions.assertEquals("wfr-123456", response.getWorkflowRunId());
        Assertions.assertEquals("task-123456", response.getTaskId());

        Assertions.assertNotNull(response.getData());
        Assertions.assertInstanceOf(NodeStartedData.class, response.getData());

        NodeStartedData nodeStartedData = (NodeStartedData) response.getData();

        Assertions.assertEquals("node-123", nodeStartedData.getNodeId());
        Assertions.assertEquals("text", nodeStartedData.getNodeType());
        Assertions.assertEquals("Test Node", nodeStartedData.getTitle());
        Assertions.assertEquals(1, nodeStartedData.getIndex());
        Assertions.assertEquals("prev-node-456", nodeStartedData.getPredecessorNodeId());
        Assertions.assertNotNull(nodeStartedData.getInputs());
        Assertions.assertEquals("value1", nodeStartedData.getInputs().get("key1"));
        Assertions.assertEquals(123, nodeStartedData.getInputs().get("key2"));
        Assertions.assertEquals(1681556000000L, nodeStartedData.getCreatedAt());
    }

    @Test
    public void testDeserializeWorkflowStartedData() throws IOException {
        // Prepare test JSON
        String json = "{\n" +
                "  \"event\": \"workflow_started\",\n" +
                "  \"workflow_run_id\": \"wfr-123456\",\n" +
                "  \"task_id\": \"task-123456\",\n" +
                "  \"data\": {\n" +
                "    \"id\": \"workflow-123\",\n" +
                "    \"workflow_id\": \"wf-123\",\n" +
                "    \"sequence_number\": 42,\n" +
                "    \"created_at\": 1681556000000\n" +
                "  }\n" +
                "}";

        // Register custom deserializer module
        objectMapper.registerModule(new WorkflowRunStreamResponseModule());

        // Deserialize JSON
        WorkflowRunStreamResponse response = objectMapper.readValue(json, WorkflowRunStreamResponse.class);

        // Assertions
        Assertions.assertNotNull(response);
        Assertions.assertEquals(StreamEventEnum.workflow_started, response.getEvent());
        Assertions.assertEquals("wfr-123456", response.getWorkflowRunId());
        Assertions.assertEquals("task-123456", response.getTaskId());

        Assertions.assertNotNull(response.getData());
        Assertions.assertInstanceOf(WorkflowStartedData.class, response.getData());

        WorkflowStartedData workflowStartedData = (WorkflowStartedData) response.getData();
        Assertions.assertEquals("workflow-123", workflowStartedData.getId());
        Assertions.assertEquals("wf-123", workflowStartedData.getWorkflowId());
        Assertions.assertEquals(42, workflowStartedData.getSequenceNumber());
        Assertions.assertEquals(1681556000000L, workflowStartedData.getCreatedAt());
    }

    @Test
    public void testDeserializeTextChunkData() throws IOException {
        // Prepare test JSON
        String json = "{\n" +
                "  \"event\": \"text_chunk\",\n" +
                "  \"workflow_run_id\": \"wfr-123456\",\n" +
                "  \"task_id\": \"task-123456\",\n" +
                "  \"data\": {\n" +
                "    \"text\": \"This is a test text\",\n" +
                "    \"node_id\": \"node-456\"\n" +
                "  }\n" +
                "}";

        // Register custom deserializer module
        objectMapper.registerModule(new WorkflowRunStreamResponseModule());

        // Deserialize JSON
        WorkflowRunStreamResponse response = objectMapper.readValue(json, WorkflowRunStreamResponse.class);

        // Assertions
        Assertions.assertNotNull(response);
        Assertions.assertEquals(StreamEventEnum.text_chunk, response.getEvent());
        Assertions.assertEquals("wfr-123456", response.getWorkflowRunId());
        Assertions.assertEquals("task-123456", response.getTaskId());

        Assertions.assertNotNull(response.getData());
        Assertions.assertInstanceOf(Map.class, response.getData());

        Map<String, Object> textChunkData = (Map<String, Object>) response.getData();
        Assertions.assertEquals("This is a test text", textChunkData.get("text"));
        Assertions.assertEquals("node-456", textChunkData.get("node_id"));
    }

    @Test
    public void testDeserializeTextChunkDataNewline() throws IOException {
        // Prepare test JSON
        String json = "{\n" +
                "    \"event\": \"text_chunk\",\n" +
                "    \"workflow_run_id\": \"wfr-123456\",\n" +
                "    \"task_id\": \"task-123456\",\n" +
                "    \"data\": {\n" +
                "        \"text\": \"\\n\\n\",\n" +
                "        \"from_variable_selector\": [\n" +
                "        ]\n" +
                "    }\n" +
                "}";

        // Register custom deserializer module
        objectMapper.registerModule(new WorkflowRunStreamResponseModule());

        // Deserialize JSON
        WorkflowRunStreamResponse response = objectMapper.readValue(json, WorkflowRunStreamResponse.class);

        // Assertions
        Assertions.assertNotNull(response);
        Assertions.assertEquals(StreamEventEnum.text_chunk, response.getEvent());

    }

    @Test
    public void testDeserializeWithInvalidEventType() throws JsonProcessingException {
        // Prepare test JSON with invalid event type
        String json = "{\n" +
                "  \"event\": \"invalid_event_type\",\n" +
                "  \"workflow_run_id\": \"wfr-123456\",\n" +
                "  \"task_id\": \"task-123456\",\n" +
                "  \"data\": {}\n" +
                "}";

        // Register custom deserializer module
        objectMapper.registerModule(new WorkflowRunStreamResponseModule());

        objectMapper.readValue(json, WorkflowRunStreamResponse.class);
    }

    @Test
    public void testDeserializeWithMissingEventField() throws JsonProcessingException {
        // Prepare test JSON without event field
        String json = "{\n" +
                "  \"workflow_run_id\": \"wfr-123456\",\n" +
                "  \"task_id\": \"task-123456\",\n" +
                "  \"data\": {}\n" +
                "}";

        // Register custom deserializer module
        objectMapper.registerModule(new WorkflowRunStreamResponseModule());

        objectMapper.readValue(json, WorkflowRunStreamResponse.class);

    }

    public static class WorkflowRunStreamResponseModule extends SimpleModule {

        private static final long serialVersionUID = 1L;

        public WorkflowRunStreamResponseModule() {
            super("WorkflowRunStreamResponseModule");
            addDeserializer(WorkflowRunStreamResponse.class, new WorkflowRunStreamResponseDeserializer());
        }
    }
}
