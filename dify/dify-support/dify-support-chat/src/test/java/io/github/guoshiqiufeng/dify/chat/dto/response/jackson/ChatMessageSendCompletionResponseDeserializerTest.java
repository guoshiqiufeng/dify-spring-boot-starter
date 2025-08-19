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
package io.github.guoshiqiufeng.dify.chat.dto.response.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.github.guoshiqiufeng.dify.chat.dto.response.ChatMessageSendCompletionResponse;
import io.github.guoshiqiufeng.dify.chat.dto.response.message.NodeStartedData;
import io.github.guoshiqiufeng.dify.chat.dto.response.message.WorkflowStartedData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * Test for {@link ChatMessageSendCompletionResponseDeserializer}
 *
 * @author yanghq
 * @version 1.0.3
 * @since 2025/5/29
 */
public class ChatMessageSendCompletionResponseDeserializerTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(ChatMessageSendCompletionResponse.class, new ChatMessageSendCompletionResponseDeserializer());
        objectMapper.registerModule(module);
    }

    @Test
    public void testDeserializeWithNodeStartedEvent() throws IOException {
        // Prepare test JSON with node_started event
        String json = "{\n" +
                "  \"event\": \"node_started\",\n" +
                "  \"conversation_id\": \"conv-123\",\n" +
                "  \"message_id\": \"msg-456\",\n" +
                "  \"task_id\": \"task-789\",\n" +
                "  \"id\": \"id-abc\",\n" +
                "  \"created_at\": 1681556000000,\n" +
                "  \"workflow_run_id\": \"workflow-run-123\",\n" +
                "  \"data\": {\n" +
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

        // Deserialize the JSON
        ChatMessageSendCompletionResponse response = objectMapper.readValue(json, ChatMessageSendCompletionResponse.class);

        // Assertions for base properties
        Assertions.assertNotNull(response);
        Assertions.assertEquals("node_started", response.getEvent());
        Assertions.assertEquals("conv-123", response.getConversationId());
        Assertions.assertEquals("msg-456", response.getMessageId());
        Assertions.assertEquals("task-789", response.getTaskId());
        Assertions.assertEquals("id-abc", response.getId());
        Assertions.assertEquals(1681556000000L, response.getCreatedAt());
        Assertions.assertEquals("workflow-run-123", response.getWorkflowRunId());

        // Assertions for data object
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
    public void testDeserializeWithWorkflowStartedEvent() throws IOException {
        // Prepare test JSON with workflow_started event
        String json = "{\n" +
                "  \"event\": \"workflow_started\",\n" +
                "  \"conversation_id\": \"conv-456\",\n" +
                "  \"message_id\": \"msg-789\",\n" +
                "  \"task_id\": \"task-abc\",\n" +
                "  \"id\": \"id-def\",\n" +
                "  \"created_at\": 1681556100000,\n" +
                "  \"workflow_run_id\": \"workflow-run-456\",\n" +
                "  \"data\": {\n" +
                "    \"id\": \"workflow-123\",\n" +
                "    \"created_at\": 1681556000000\n" +
                "  }\n" +
                "}";

        // Deserialize the JSON
        ChatMessageSendCompletionResponse response = objectMapper.readValue(json, ChatMessageSendCompletionResponse.class);

        // Assertions for base properties
        Assertions.assertNotNull(response);
        Assertions.assertEquals("workflow_started", response.getEvent());
        Assertions.assertEquals("conv-456", response.getConversationId());
        Assertions.assertEquals("msg-789", response.getMessageId());
        Assertions.assertEquals("task-abc", response.getTaskId());
        Assertions.assertEquals("id-def", response.getId());
        Assertions.assertEquals(1681556100000L, response.getCreatedAt());
        Assertions.assertEquals("workflow-run-456", response.getWorkflowRunId());

        // Assertions for data object
        Assertions.assertNotNull(response.getData());
        Assertions.assertInstanceOf(WorkflowStartedData.class, response.getData());

        WorkflowStartedData workflowStartedData = (WorkflowStartedData) response.getData();
        Assertions.assertEquals("workflow-123", workflowStartedData.getId());
        Assertions.assertEquals(1681556000000L, workflowStartedData.getCreatedAt());
    }

    @Test
    public void testDeserializeWithUnknownEvent() throws IOException {
        // Prepare test JSON with unknown event
        String json = "{\n" +
                "  \"event\": \"unknown_event\",\n" +
                "  \"conversation_id\": \"conv-789\",\n" +
                "  \"message_id\": \"msg-abc\",\n" +
                "  \"task_id\": \"task-def\",\n" +
                "  \"id\": \"id-ghi\",\n" +
                "  \"created_at\": 1681556200000,\n" +
                "  \"workflow_run_id\": \"workflow-run-789\",\n" +
                "  \"data\": {\n" +
                "    \"some_field\": \"some_value\"\n" +
                "  }\n" +
                "}";

        // Deserialize the JSON
        ChatMessageSendCompletionResponse response = objectMapper.readValue(json, ChatMessageSendCompletionResponse.class);

        // Assertions for base properties
        Assertions.assertNotNull(response);
        Assertions.assertEquals("unknown_event", response.getEvent());
        Assertions.assertEquals("conv-789", response.getConversationId());
        Assertions.assertEquals("msg-abc", response.getMessageId());
        Assertions.assertEquals("task-def", response.getTaskId());
        Assertions.assertEquals("id-ghi", response.getId());
        Assertions.assertEquals(1681556200000L, response.getCreatedAt());
        Assertions.assertEquals("workflow-run-789", response.getWorkflowRunId());

        // Data should be null since the event is unknown
        Assertions.assertNull(response.getData());
    }

    @Test
    public void testDeserializeWithMissingEvent() throws IOException {
        // Prepare test JSON without event field
        String json = "{\n" +
                "  \"conversation_id\": \"conv-abc\",\n" +
                "  \"message_id\": \"msg-def\",\n" +
                "  \"task_id\": \"task-ghi\",\n" +
                "  \"id\": \"id-jkl\",\n" +
                "  \"created_at\": 1681556300000,\n" +
                "  \"workflow_run_id\": \"workflow-run-abc\"\n" +
                "}";

        // Deserialize the JSON
        ChatMessageSendCompletionResponse response = objectMapper.readValue(json, ChatMessageSendCompletionResponse.class);

        // Assertions for base properties
        Assertions.assertNotNull(response);
        Assertions.assertNull(response.getEvent());
        Assertions.assertEquals("conv-abc", response.getConversationId());
        Assertions.assertEquals("msg-def", response.getMessageId());
        Assertions.assertEquals("task-ghi", response.getTaskId());
        Assertions.assertEquals("id-jkl", response.getId());
        Assertions.assertEquals(1681556300000L, response.getCreatedAt());
        Assertions.assertEquals("workflow-run-abc", response.getWorkflowRunId());

        // Data should be null since there's no event field
        Assertions.assertNull(response.getData());
    }

    @Test
    public void testDeserializeWithMissingData() throws IOException {
        // Prepare test JSON with node_started event but missing data field
        String json = "{\n" +
                "  \"event\": \"node_started\",\n" +
                "  \"conversation_id\": \"conv-def\",\n" +
                "  \"message_id\": \"msg-ghi\",\n" +
                "  \"task_id\": \"task-jkl\",\n" +
                "  \"id\": \"id-mno\",\n" +
                "  \"created_at\": 1681556400000,\n" +
                "  \"workflow_run_id\": \"workflow-run-def\"\n" +
                "}";

        // Deserialize the JSON
        ChatMessageSendCompletionResponse response = objectMapper.readValue(json, ChatMessageSendCompletionResponse.class);

        // Assertions for base properties
        Assertions.assertNotNull(response);
        Assertions.assertEquals("node_started", response.getEvent());
        Assertions.assertEquals("conv-def", response.getConversationId());
        Assertions.assertEquals("msg-ghi", response.getMessageId());
        Assertions.assertEquals("task-jkl", response.getTaskId());
        Assertions.assertEquals("id-mno", response.getId());
        Assertions.assertEquals(1681556400000L, response.getCreatedAt());
        Assertions.assertEquals("workflow-run-def", response.getWorkflowRunId());

        // Data should be null since data field is missing
        Assertions.assertNull(response.getData());
    }

    @Test
    public void testDeserializeWithNullData() throws IOException {
        // Prepare test JSON with node_started event but null data
        String json = "{\n" +
                "  \"event\": \"node_started\",\n" +
                "  \"conversation_id\": \"conv-ghi\",\n" +
                "  \"message_id\": \"msg-jkl\",\n" +
                "  \"task_id\": \"task-mno\",\n" +
                "  \"id\": \"id-pqr\",\n" +
                "  \"created_at\": 1681556500000,\n" +
                "  \"workflow_run_id\": \"workflow-run-ghi\",\n" +
                "  \"data\": null\n" +
                "}";

        // Deserialize the JSON
        ChatMessageSendCompletionResponse response = objectMapper.readValue(json, ChatMessageSendCompletionResponse.class);

        // Assertions for base properties
        Assertions.assertNotNull(response);
        Assertions.assertEquals("node_started", response.getEvent());
        Assertions.assertEquals("conv-ghi", response.getConversationId());
        Assertions.assertEquals("msg-jkl", response.getMessageId());
        Assertions.assertEquals("task-mno", response.getTaskId());
        Assertions.assertEquals("id-pqr", response.getId());
        Assertions.assertEquals(1681556500000L, response.getCreatedAt());
        Assertions.assertEquals("workflow-run-ghi", response.getWorkflowRunId());

        // Data should be null
        Assertions.assertNull(response.getData());
    }

    @Test
    public void testDeserializeWithAgentThought() throws IOException {
        // Prepare test JSON with node_started event but null data
        String json = "{\n" +
                "    \"event\": \"agent_thought\",\n" +
                "    \"conversation_id\": \"f10c0118-3d3a-4402-a094-26862b10e33d\",\n" +
                "    \"message_id\": \"ce331ffb-2b8a-41f1-a01b-f7fab9748906\",\n" +
                "    \"created_at\": 1755051876,\n" +
                "    \"task_id\": \"4f4bb0ab-a332-4205-94b8-d49495a2c6e2\",\n" +
                "    \"id\": \"f1a0a900-fa97-4874-bb17-6b4d3ce16e1b\",\n" +
                "    \"position\": 1,\n" +
                "    \"thought\": \"\",\n" +
                "    \"observation\": \"\",\n" +
                "    \"tool\": \"standard_nl2sql\",\n" +
                "    \"tool_labels\": {\n" +
                "        \"standard_nl2sql\": {\n" +
                "            \"en_US\": \"standard_nl2sql\",\n" +
                "            \"zh_Hans\": \"standard_nl2sql\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"tool_input\": \"{\\\"standard_nl2sql\\\": {\\\"BOT_USER_INPUT\\\": \\\"按单位统计银行账户数量\\\", \\\"tenantCode\\\": \\\"default\\\"}}\",\n" +
                "    \"message_files\": [\n" +
                "\n" +
                "    ]\n" +
                "}";

        // Deserialize the JSON
        ChatMessageSendCompletionResponse response = objectMapper.readValue(json, ChatMessageSendCompletionResponse.class);

        // Assertions for base properties
        Assertions.assertNotNull(response);
        Assertions.assertEquals("agent_thought", response.getEvent());
        Assertions.assertEquals("ce331ffb-2b8a-41f1-a01b-f7fab9748906", response.getMessageId());
        Assertions.assertEquals("4f4bb0ab-a332-4205-94b8-d49495a2c6e2", response.getTaskId());
        Assertions.assertEquals(1, response.getPosition());

        // Data should be null
        Assertions.assertNull(response.getData());
    }

    @Test
    public void testDeserializeWithError() throws IOException {
        // Prepare test JSON with node_started event but null data
        String json = "{\n" +
                "  \"event\": \"error\",\n" +
                "  \"message\": \"t-ghi\",\n" +
                "  \"code\": \"code-ghi\",\n" +
                "  \"status\": 1,\n" +
                "  \"message_id\": \"msg-jkl\",\n" +
                "  \"task_id\": \"task-mno\",\n" +
                "  \"id\": \"id-pqr\",\n" +
                "  \"created_at\": 1681556500000,\n" +
                "  \"workflow_run_id\": \"workflow-run-ghi\",\n" +
                "  \"data\": null\n" +
                "}";

        // Deserialize the JSON
        ChatMessageSendCompletionResponse response = objectMapper.readValue(json, ChatMessageSendCompletionResponse.class);

        // Assertions for base properties
        Assertions.assertNotNull(response);
        Assertions.assertEquals("error", response.getEvent());
        Assertions.assertEquals("t-ghi", response.getMessage());
        Assertions.assertEquals("code-ghi", response.getCode());
        Assertions.assertEquals(1, response.getStatus());
        Assertions.assertEquals("msg-jkl", response.getMessageId());
        Assertions.assertEquals("task-mno", response.getTaskId());
        Assertions.assertEquals("id-pqr", response.getId());
        Assertions.assertEquals(1681556500000L, response.getCreatedAt());
        Assertions.assertEquals("workflow-run-ghi", response.getWorkflowRunId());

        // Data should be null
        Assertions.assertNull(response.getData());
    }
}
