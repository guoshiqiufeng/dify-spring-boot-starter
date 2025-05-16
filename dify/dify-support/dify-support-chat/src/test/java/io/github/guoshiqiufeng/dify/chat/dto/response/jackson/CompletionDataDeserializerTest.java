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
import io.github.guoshiqiufeng.dify.chat.dto.response.message.CompletionData;
import io.github.guoshiqiufeng.dify.chat.dto.response.message.NodeStartedData;
import io.github.guoshiqiufeng.dify.chat.dto.response.message.WorkflowStartedData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * Test for {@link CompletionDataDeserializer}
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/27
 */
public class CompletionDataDeserializerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testDeserializeNodeStartedData() throws IOException {
        // Prepare test JSON
        String json = "{\n" +
                "  \"event\": \"node_started\",\n" +
                "  \"id\": \"test-id-123\",\n" +
                "  \"node_id\": \"node-123\",\n" +
                "  \"node_type\": \"text\",\n" +
                "  \"title\": \"Test Node\",\n" +
                "  \"index\": 1,\n" +
                "  \"predecessor_node_id\": \"prev-node-456\",\n" +
                "  \"inputs\": {\n" +
                "    \"key1\": \"value1\",\n" +
                "    \"key2\": 123\n" +
                "  },\n" +
                "  \"created_at\": 1681556000000\n" +
                "}";

        // Register the module with our custom deserializer
        objectMapper.registerModule(new CompletionDataModule());

        // Deserialize the JSON
        CompletionData data = objectMapper.readValue(json, CompletionData.class);

        // Assertions
        Assertions.assertNotNull(data);
        Assertions.assertInstanceOf(NodeStartedData.class, data);

        NodeStartedData nodeStartedData = (NodeStartedData) data;
        Assertions.assertEquals("test-id-123", nodeStartedData.getId());
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
        // Prepare test JSON for workflow_started
        String json = "{\n" +
                "  \"event\": \"workflow_started\",\n" +
                "  \"id\": \"workflow-123\",\n" +
                "  \"created_at\": 1681556000000\n" +
                "}";

        // Register the module with our custom deserializer
        objectMapper.registerModule(new CompletionDataModule());

        // Deserialize the JSON
        CompletionData data = objectMapper.readValue(json, CompletionData.class);

        // Assertions
        Assertions.assertNotNull(data);
        Assertions.assertInstanceOf(WorkflowStartedData.class, data);

        WorkflowStartedData workflowData = (WorkflowStartedData) data;
        Assertions.assertEquals("workflow-123", workflowData.getId());
        Assertions.assertEquals(1681556000000L, workflowData.getCreatedAt());
    }

    @Test
    public void testDeserializeWithMissingEventField() throws IOException {
        // Prepare test JSON without event field
        String json = "{\n" +
                "  \"id\": \"test-id-123\",\n" +
                "  \"node_id\": \"node-123\"\n" +
                "}";

        // Register the module with our custom deserializer
        objectMapper.registerModule(new CompletionDataModule());

        // Deserialize the JSON - should return null as per deserializer logic
        CompletionData data = objectMapper.readValue(json, CompletionData.class);
        Assertions.assertNull(data);
    }

    public static class CompletionDataModule extends SimpleModule {

        private static final long serialVersionUID = 1L;

        public CompletionDataModule() {
            super("CompletionDataModule");
            addDeserializer(CompletionData.class, new CompletionDataDeserializer());
        }
    }
}
