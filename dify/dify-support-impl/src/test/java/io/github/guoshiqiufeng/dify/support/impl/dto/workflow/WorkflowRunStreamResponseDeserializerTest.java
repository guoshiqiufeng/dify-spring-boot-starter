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
        assertInstanceOf(Map.class, result.getData().getData());
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
        Iterator<String> fieldNames = List.of("key1").iterator();
        when(dataNode.fieldNames()).thenReturn(fieldNames);
        when(dataNode.get("key1")).thenReturn(fieldNode);
        when(fieldNode.isNull()).thenReturn(false);
        when(fieldNode.isTextual()).thenReturn(true);
        when(fieldNode.asText()).thenReturn("value1");

        WorkflowRunStreamResponseDto result = deserializer.deserialize(root, jsonMapper);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertNotNull(result.getData().getData());
        assertInstanceOf(Map.class, result.getData().getData());
        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) result.getData().getData();
        assertEquals("value1", dataMap.get("key1"));
    }

    @Test
    void testDeserializeWithNonTextualEvent() {
        JsonNode root = mock(JsonNode.class);
        JsonNode eventNode = mock(JsonNode.class);

        when(root.get("event")).thenReturn(eventNode);
        when(eventNode.isTextual()).thenReturn(false);

        WorkflowRunStreamResponseDto result = deserializer.deserialize(root, jsonMapper);

        assertNotNull(result);
        assertNotNull(result.getData());
    }

    @Test
    void testDeserializeWithDataNodeNull() {
        JsonNode root = mock(JsonNode.class);
        JsonNode eventNode = mock(JsonNode.class);

        when(root.get("event")).thenReturn(eventNode);
        when(eventNode.isTextual()).thenReturn(true);
        when(eventNode.asText()).thenReturn("workflow_started");
        when(root.has("data")).thenReturn(true);
        when(root.get("data")).thenReturn(null);

        WorkflowRunStreamResponseDto result = deserializer.deserialize(root, jsonMapper);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertNull(result.getData().getData());
    }

    @Test
    void testDeserializeWithoutDataField() {
        JsonNode root = mock(JsonNode.class);
        JsonNode eventNode = mock(JsonNode.class);

        when(root.get("event")).thenReturn(eventNode);
        when(eventNode.isTextual()).thenReturn(true);
        when(eventNode.asText()).thenReturn("workflow_started");
        when(root.has("data")).thenReturn(false);

        WorkflowRunStreamResponseDto result = deserializer.deserialize(root, jsonMapper);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertNull(result.getData().getData());
    }

    @Test
    void testBuilderResponseWithInvalidEvent() {
        JsonNode root = mock(JsonNode.class);
        JsonNode eventNode = mock(JsonNode.class);

        when(root.get("event")).thenReturn(null);
        when(root.has("event")).thenReturn(true);
        when(root.get("event")).thenReturn(eventNode);
        when(eventNode.asText()).thenReturn("invalid_event");

        WorkflowRunStreamResponseDto result = deserializer.deserialize(root, jsonMapper);

        assertNotNull(result);
        assertNotNull(result.getData());
    }

    @Test
    void testConvertToMapWithDifferentTypes() {
        JsonNode root = mock(JsonNode.class);
        JsonNode eventNode = mock(JsonNode.class);
        JsonNode dataNode = mock(JsonNode.class);
        JsonNode textNode = mock(JsonNode.class);
        JsonNode numberNode = mock(JsonNode.class);
        JsonNode booleanNode = mock(JsonNode.class);
        JsonNode nullNode = mock(JsonNode.class);
        JsonNode arrayNode = mock(JsonNode.class);
        JsonNode objectNode = mock(JsonNode.class);
        JsonNode arrayElement = mock(JsonNode.class);

        when(root.get("event")).thenReturn(eventNode);
        when(eventNode.isTextual()).thenReturn(true);
        when(eventNode.asText()).thenReturn("text_chunk");
        when(root.has("data")).thenReturn(true);
        when(root.get("data")).thenReturn(dataNode);
        when(dataNode.isNull()).thenReturn(false);
        when(dataNode.isObject()).thenReturn(true);
        Iterator<String> fieldNames = Arrays.asList("text", "number", "boolean", "null", "array", "object", "nullField").iterator();
        when(dataNode.fieldNames()).thenReturn(fieldNames);

        when(dataNode.get("text")).thenReturn(textNode);
        when(textNode.isNull()).thenReturn(false);
        when(textNode.isTextual()).thenReturn(true);
        when(textNode.asText()).thenReturn("textValue");

        when(dataNode.get("number")).thenReturn(numberNode);
        when(numberNode.isNull()).thenReturn(false);
        when(numberNode.isTextual()).thenReturn(false);
        when(numberNode.isNumber()).thenReturn(true);
        when(numberNode.asDouble()).thenReturn(42.5);

        when(dataNode.get("boolean")).thenReturn(booleanNode);
        when(booleanNode.isNull()).thenReturn(false);
        when(booleanNode.isTextual()).thenReturn(false);
        when(booleanNode.isNumber()).thenReturn(false);
        when(booleanNode.isBoolean()).thenReturn(true);
        when(booleanNode.asBoolean()).thenReturn(true);

        when(dataNode.get("null")).thenReturn(nullNode);
        when(nullNode.isNull()).thenReturn(true);

        when(dataNode.get("array")).thenReturn(arrayNode);
        when(arrayNode.isNull()).thenReturn(false);
        when(arrayNode.isTextual()).thenReturn(false);
        when(arrayNode.isNumber()).thenReturn(false);
        when(arrayNode.isBoolean()).thenReturn(false);
        when(arrayNode.isArray()).thenReturn(true);
        when(arrayNode.elements()).thenReturn(Collections.singletonList(arrayElement).iterator());
        when(arrayElement.isNull()).thenReturn(false);
        when(arrayElement.isTextual()).thenReturn(true);
        when(arrayElement.asText()).thenReturn("element1");

        when(dataNode.get("object")).thenReturn(objectNode);
        when(objectNode.isNull()).thenReturn(false);
        when(objectNode.isTextual()).thenReturn(false);
        when(objectNode.isNumber()).thenReturn(false);
        when(objectNode.isBoolean()).thenReturn(false);
        when(objectNode.isArray()).thenReturn(false);
        when(objectNode.isObject()).thenReturn(true);
        when(objectNode.fieldNames()).thenReturn(Collections.emptyIterator());

        when(dataNode.get("nullField")).thenReturn(null);

        WorkflowRunStreamResponseDto result = deserializer.deserialize(root, jsonMapper);

        assertNotNull(result);
        assertNotNull(result.getData());
        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) result.getData().getData();
        assertEquals("textValue", dataMap.get("text"));
        assertEquals(42.5, dataMap.get("number"));
        assertEquals(true, dataMap.get("boolean"));
        assertNull(dataMap.get("null"));
        assertInstanceOf(List.class, dataMap.get("array"));
        assertInstanceOf(Map.class, dataMap.get("object"));
        assertNull(dataMap.get("nullField"));
    }

    @Test
    void testConvertToObjectWithDefaultCase() {
        JsonNode root = mock(JsonNode.class);
        JsonNode eventNode = mock(JsonNode.class);
        JsonNode dataNode = mock(JsonNode.class);
        JsonNode unknownNode = mock(JsonNode.class);

        when(root.get("event")).thenReturn(eventNode);
        when(eventNode.isTextual()).thenReturn(true);
        when(eventNode.asText()).thenReturn("text_chunk");
        when(root.has("data")).thenReturn(true);
        when(root.get("data")).thenReturn(dataNode);
        when(dataNode.isNull()).thenReturn(false);
        when(dataNode.isObject()).thenReturn(true);
        Iterator<String> fieldNames = List.of("unknown").iterator();
        when(dataNode.fieldNames()).thenReturn(fieldNames);

        when(dataNode.get("unknown")).thenReturn(unknownNode);
        when(unknownNode.isNull()).thenReturn(false);
        when(unknownNode.isTextual()).thenReturn(false);
        when(unknownNode.isNumber()).thenReturn(false);
        when(unknownNode.isBoolean()).thenReturn(false);
        when(unknownNode.isArray()).thenReturn(false);
        when(unknownNode.isObject()).thenReturn(false);
        when(unknownNode.asText()).thenReturn("fallback");

        WorkflowRunStreamResponseDto result = deserializer.deserialize(root, jsonMapper);

        assertNotNull(result);
        assertNotNull(result.getData());
        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) result.getData().getData();
        assertEquals("fallback", dataMap.get("unknown"));
    }

    @Test
    void testConvertToMapWithNullNode() {
        JsonNode root = mock(JsonNode.class);
        JsonNode eventNode = mock(JsonNode.class);

        when(root.get("event")).thenReturn(eventNode);
        when(eventNode.isTextual()).thenReturn(true);
        when(eventNode.asText()).thenReturn("text_chunk");
        when(root.has("data")).thenReturn(true);
        when(root.get("data")).thenReturn(null);

        WorkflowRunStreamResponseDto result = deserializer.deserialize(root, jsonMapper);

        assertNotNull(result);
        assertNotNull(result.getData());
    }

    @Test
    void testConvertToMapWithNonObjectNode() {
        JsonNode root = mock(JsonNode.class);
        JsonNode eventNode = mock(JsonNode.class);
        JsonNode dataNode = mock(JsonNode.class);

        when(root.get("event")).thenReturn(eventNode);
        when(eventNode.isTextual()).thenReturn(true);
        when(eventNode.asText()).thenReturn("text_chunk");
        when(root.has("data")).thenReturn(true);
        when(root.get("data")).thenReturn(dataNode);
        when(dataNode.isNull()).thenReturn(false);
        when(dataNode.isObject()).thenReturn(false);

        WorkflowRunStreamResponseDto result = deserializer.deserialize(root, jsonMapper);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertInstanceOf(Map.class, result.getData().getData());
        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap = (Map<String, Object>) result.getData().getData();
        assertTrue(dataMap.isEmpty());
    }

    @Test
    void testBuilderResponseWithValidEvent() {
        JsonNode root = mock(JsonNode.class);
        JsonNode eventNode = mock(JsonNode.class);

        when(root.get("event")).thenReturn(null);
        when(root.has("event")).thenReturn(true);
        when(root.get("event")).thenReturn(eventNode);
        when(eventNode.asText()).thenReturn("workflow_started");

        WorkflowRunStreamResponseDto result = deserializer.deserialize(root, jsonMapper);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertNotNull(result.getData().getEvent());
    }

    @Test
    void testConvertToMapWithNullNodeInMap() {
        JsonNode root = mock(JsonNode.class);
        JsonNode eventNode = mock(JsonNode.class);
        JsonNode dataNode = mock(JsonNode.class);

        when(root.get("event")).thenReturn(eventNode);
        when(eventNode.isTextual()).thenReturn(true);
        when(eventNode.asText()).thenReturn("text_chunk");
        when(root.has("data")).thenReturn(true);
        when(root.get("data")).thenReturn(dataNode);
        when(dataNode.isNull()).thenReturn(true);

        WorkflowRunStreamResponseDto result = deserializer.deserialize(root, jsonMapper);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertNull(result.getData().getData());
    }

    @Test
    void testConvertToMapWithIsNullNode() {
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
        assertInstanceOf(Map.class, result.getData().getData());
    }
}
