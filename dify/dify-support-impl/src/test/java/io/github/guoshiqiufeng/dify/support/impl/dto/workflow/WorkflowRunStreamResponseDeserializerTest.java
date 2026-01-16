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
package io.github.guoshiqiufeng.dify.support.impl.dto.workflow;

import io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper;
import io.github.guoshiqiufeng.dify.client.core.codec.JsonNode;
import io.github.guoshiqiufeng.dify.workflow.dto.response.stream.WorkflowStartedData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WorkflowRunStreamResponseDeserializerTest {

    private WorkflowRunStreamResponseDeserializer deserializer;
    private JsonMapper jsonMapper;

    @BeforeEach
    void setUp() {
        deserializer = new WorkflowRunStreamResponseDeserializer();
        jsonMapper = mock(JsonMapper.class);
    }

    @Test
    void testDeserializeWithoutEvent() {
        JsonNode root = mock(JsonNode.class);
        when(root.get("event")).thenReturn(null);

        WorkflowRunStreamResponseDto result = deserializer.deserialize(root, jsonMapper);

        assertNotNull(result);
        assertNotNull(result.getData());
    }

    @Test
    void testDeserializeWithValidEvent() {
        JsonNode root = mock(JsonNode.class);
        JsonNode eventNode = mock(JsonNode.class);
        JsonNode dataNode = mock(JsonNode.class);

        when(root.get("event")).thenReturn(eventNode);
        when(eventNode.isTextual()).thenReturn(true);
        when(eventNode.asText()).thenReturn("workflow_started");
        when(root.has("data")).thenReturn(true);
        when(root.get("data")).thenReturn(dataNode);
        when(dataNode.isNull()).thenReturn(false);
        when(jsonMapper.treeToValue(eq(dataNode), eq(WorkflowStartedData.class))).thenReturn(new WorkflowStartedData());

        WorkflowRunStreamResponseDto result = deserializer.deserialize(root, jsonMapper);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertNotNull(result.getData().getData());
    }

    @Test
    void testDeserializeWithUnknownEvent() {
        JsonNode root = mock(JsonNode.class);
        JsonNode eventNode = mock(JsonNode.class);

        when(root.get("event")).thenReturn(eventNode);
        when(eventNode.isTextual()).thenReturn(true);
        when(eventNode.asText()).thenReturn("unknown_event");

        WorkflowRunStreamResponseDto result = deserializer.deserialize(root, jsonMapper);

        assertNotNull(result);
        assertNotNull(result.getData());
    }

    @Test
    void testDeserializeWithMapDataClass() {
        JsonNode root = mock(JsonNode.class);
        JsonNode eventNode = mock(JsonNode.class);
        JsonNode dataNode = mock(JsonNode.class);

        when(root.get("event")).thenReturn(eventNode);
        when(eventNode.isTextual()).thenReturn(true);
        when(eventNode.asText()).thenReturn("text_chunk");
        when(root.has("data")).thenReturn(true);
        when(root.get("data")).thenReturn(dataNode);
        when(dataNode.isNull()).thenReturn(false);
        when(dataNode.isObject()).thenReturn(true);
        when(dataNode.fieldNames()).thenReturn(Collections.emptyIterator());

        WorkflowRunStreamResponseDto result = deserializer.deserialize(root, jsonMapper);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertNotNull(result.getData().getData());
        assertTrue(result.getData().getData() instanceof Map);
    }

    @Test
    void testDeserializeWithAllFields() {
        JsonNode root = mock(JsonNode.class);
        JsonNode eventNode = mock(JsonNode.class);
        JsonNode taskIdNode = mock(JsonNode.class);
        JsonNode workflowRunIdNode = mock(JsonNode.class);

        when(root.get("event")).thenReturn(eventNode);
        when(eventNode.isTextual()).thenReturn(true);
        when(eventNode.asText()).thenReturn("workflow_started");
        when(root.has("task_id")).thenReturn(true);
        when(root.get("task_id")).thenReturn(taskIdNode);
        when(taskIdNode.asText()).thenReturn("task-123");
        when(root.has("workflow_run_id")).thenReturn(true);
        when(root.get("workflow_run_id")).thenReturn(workflowRunIdNode);
        when(workflowRunIdNode.asText()).thenReturn("workflow-456");

        WorkflowRunStreamResponseDto result = deserializer.deserialize(root, jsonMapper);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals("task-123", result.getData().getTaskId());
        assertEquals("workflow-456", result.getData().getWorkflowRunId());
    }

    @Test
    void testDeserializeWithNullData() {
        JsonNode root = mock(JsonNode.class);
        JsonNode eventNode = mock(JsonNode.class);
        JsonNode dataNode = mock(JsonNode.class);

        when(root.get("event")).thenReturn(eventNode);
        when(eventNode.isTextual()).thenReturn(true);
        when(eventNode.asText()).thenReturn("workflow_started");
        when(root.has("data")).thenReturn(true);
        when(root.get("data")).thenReturn(dataNode);
        when(dataNode.isNull()).thenReturn(true);

        WorkflowRunStreamResponseDto result = deserializer.deserialize(root, jsonMapper);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertNull(result.getData().getData());
    }

    @Test
    void testDeserializeWithComplexMapData() {
        JsonNode root = mock(JsonNode.class);
        JsonNode eventNode = mock(JsonNode.class);
        JsonNode dataNode = mock(JsonNode.class);
        JsonNode fieldNode = mock(JsonNode.class);

        when(root.get("event")).thenReturn(eventNode);
        when(eventNode.isTextual()).thenReturn(true);
        when(eventNode.asText()).thenReturn("text_chunk");
        when(root.has("data")).thenReturn(true);
        when(root.get("data")).thenReturn(dataNode);
        when(dataNode.isNull()).thenReturn(false);
        when(dataNode.isObject()).thenReturn(true);
        Iterator<String> fieldNames = Arrays.asList("key1").iterator();
        when(dataNode.fieldNames()).thenReturn(fieldNames);
        when(dataNode.get("key1")).thenReturn(fieldNode);
        when(fieldNode.isTextual()).thenReturn(true);
        when(fieldNode.asText()).thenReturn("value1");

        WorkflowRunStreamResponseDto result = deserializer.deserialize(root, jsonMapper);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertNotNull(result.getData().getData());
        assertTrue(result.getData().getData() instanceof Map);
        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) result.getData().getData();
        assertEquals("value1", dataMap.get("key1"));
    }
}
