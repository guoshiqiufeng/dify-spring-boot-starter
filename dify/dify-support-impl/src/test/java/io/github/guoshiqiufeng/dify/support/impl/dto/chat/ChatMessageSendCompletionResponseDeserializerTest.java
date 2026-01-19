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
package io.github.guoshiqiufeng.dify.support.impl.dto.chat;

import io.github.guoshiqiufeng.dify.chat.dto.response.message.EmptyData;
import io.github.guoshiqiufeng.dify.chat.dto.response.message.WorkflowStartedData;
import io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper;
import io.github.guoshiqiufeng.dify.client.core.codec.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ChatMessageSendCompletionResponseDeserializerTest {

    private ChatMessageSendCompletionResponseDeserializer deserializer;
    private JsonMapper jsonMapper;

    @BeforeEach
    void setUp() {
        deserializer = new ChatMessageSendCompletionResponseDeserializer();
        jsonMapper = mock(JsonMapper.class);
    }

    @Test
    void testDeserializeWithoutEvent() {
        JsonNode root = mock(JsonNode.class);
        when(root.get("event")).thenReturn(null);
        when(jsonMapper.treeToValue(any(), any())).thenReturn(new io.github.guoshiqiufeng.dify.chat.dto.response.ChatMessageSendResponse());

        ChatMessageSendCompletionResponseDto result = deserializer.deserialize(root, jsonMapper);

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
        when(jsonMapper.treeToValue(eq(root), any())).thenReturn(new io.github.guoshiqiufeng.dify.chat.dto.response.ChatMessageSendResponse());
        when(jsonMapper.treeToValue(eq(dataNode), eq(WorkflowStartedData.class))).thenReturn(new WorkflowStartedData());

        ChatMessageSendCompletionResponseDto result = deserializer.deserialize(root, jsonMapper);

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
        when(jsonMapper.treeToValue(any(), any())).thenReturn(new io.github.guoshiqiufeng.dify.chat.dto.response.ChatMessageSendResponse());

        ChatMessageSendCompletionResponseDto result = deserializer.deserialize(root, jsonMapper);

        assertNotNull(result);
        assertNotNull(result.getData());
    }

    @Test
    void testDeserializeWithAllFields() {
        JsonNode root = mock(JsonNode.class);
        JsonNode eventNode = mock(JsonNode.class);
        JsonNode workflowRunIdNode = mock(JsonNode.class);
        JsonNode positionNode = mock(JsonNode.class);
        JsonNode thoughtNode = mock(JsonNode.class);
        JsonNode observationNode = mock(JsonNode.class);
        JsonNode toolNode = mock(JsonNode.class);
        JsonNode toolLabelsNode = mock(JsonNode.class);
        JsonNode toolInputNode = mock(JsonNode.class);
        JsonNode messageFilesNode = mock(JsonNode.class);
        JsonNode taskIdNode = mock(JsonNode.class);
        JsonNode statusNode = mock(JsonNode.class);
        JsonNode codeNode = mock(JsonNode.class);
        JsonNode messageNode = mock(JsonNode.class);

        when(root.get("event")).thenReturn(eventNode);
        when(eventNode.isTextual()).thenReturn(true);
        when(eventNode.asText()).thenReturn("message");
        when(root.has("workflow_run_id")).thenReturn(true);
        when(root.get("workflow_run_id")).thenReturn(workflowRunIdNode);
        when(workflowRunIdNode.asText()).thenReturn("workflow-123");
        when(root.has("position")).thenReturn(true);
        when(root.get("position")).thenReturn(positionNode);
        when(positionNode.asInt()).thenReturn(1);
        when(root.has("thought")).thenReturn(true);
        when(root.get("thought")).thenReturn(thoughtNode);
        when(thoughtNode.asText()).thenReturn("thinking");
        when(root.has("observation")).thenReturn(true);
        when(root.get("observation")).thenReturn(observationNode);
        when(observationNode.asText()).thenReturn("observing");
        when(root.has("tool")).thenReturn(true);
        when(root.get("tool")).thenReturn(toolNode);
        when(toolNode.asText()).thenReturn("calculator");
        when(root.has("tool_labels")).thenReturn(true);
        when(root.get("tool_labels")).thenReturn(toolLabelsNode);
        when(toolLabelsNode.isObject()).thenReturn(true);
        when(toolLabelsNode.fieldNames()).thenReturn(Collections.emptyIterator());
        when(root.has("tool_input")).thenReturn(true);
        when(root.get("tool_input")).thenReturn(toolInputNode);
        when(toolInputNode.asText()).thenReturn("2+2");
        when(root.has("message_files")).thenReturn(true);
        when(root.get("message_files")).thenReturn(messageFilesNode);
        when(messageFilesNode.isArray()).thenReturn(true);
        when(messageFilesNode.elements()).thenReturn(Collections.emptyIterator());
        when(root.has("task_id")).thenReturn(true);
        when(root.get("task_id")).thenReturn(taskIdNode);
        when(taskIdNode.asText()).thenReturn("task-456");
        when(root.has("status")).thenReturn(true);
        when(root.get("status")).thenReturn(statusNode);
        when(statusNode.asInt()).thenReturn(200);
        when(root.has("code")).thenReturn(true);
        when(root.get("code")).thenReturn(codeNode);
        when(codeNode.asText()).thenReturn("success");
        when(root.has("message")).thenReturn(true);
        when(root.get("message")).thenReturn(messageNode);
        when(messageNode.asText()).thenReturn("OK");
        when(jsonMapper.treeToValue(eq(root), any())).thenReturn(new io.github.guoshiqiufeng.dify.chat.dto.response.ChatMessageSendResponse());
        when(jsonMapper.treeToValue(any(), eq(EmptyData.class))).thenReturn(new EmptyData());

        ChatMessageSendCompletionResponseDto result = deserializer.deserialize(root, jsonMapper);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertEquals("workflow-123", result.getData().getWorkflowRunId());
        assertEquals(1, result.getData().getPosition());
        assertEquals("thinking", result.getData().getThought());
        assertEquals("observing", result.getData().getObservation());
        assertEquals("calculator", result.getData().getTool());
        assertEquals("2+2", result.getData().getToolInput());
        assertEquals("task-456", result.getData().getTaskId());
        assertEquals(200, result.getData().getStatus());
        assertEquals("success", result.getData().getCode());
        assertEquals("OK", result.getData().getMessage());
    }

    @Test
    void testDeserializeWithNullData() {
        JsonNode root = mock(JsonNode.class);
        JsonNode eventNode = mock(JsonNode.class);
        JsonNode dataNode = mock(JsonNode.class);

        when(root.get("event")).thenReturn(eventNode);
        when(eventNode.isTextual()).thenReturn(true);
        when(eventNode.asText()).thenReturn("message");
        when(root.has("data")).thenReturn(true);
        when(root.get("data")).thenReturn(dataNode);
        when(dataNode.isNull()).thenReturn(true);
        when(jsonMapper.treeToValue(eq(root), any())).thenReturn(new io.github.guoshiqiufeng.dify.chat.dto.response.ChatMessageSendResponse());

        ChatMessageSendCompletionResponseDto result = deserializer.deserialize(root, jsonMapper);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertNull(result.getData().getData());
    }

    @Test
    void testDeserializeWithDataNodeNull() {
        JsonNode root = mock(JsonNode.class);
        JsonNode eventNode = mock(JsonNode.class);

        when(root.get("event")).thenReturn(eventNode);
        when(eventNode.isTextual()).thenReturn(true);
        when(eventNode.asText()).thenReturn("message");
        when(root.has("data")).thenReturn(true);
        when(root.get("data")).thenReturn(null);
        when(jsonMapper.treeToValue(eq(root), any())).thenReturn(new io.github.guoshiqiufeng.dify.chat.dto.response.ChatMessageSendResponse());

        ChatMessageSendCompletionResponseDto result = deserializer.deserialize(root, jsonMapper);

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
        when(eventNode.asText()).thenReturn("message");
        when(root.has("data")).thenReturn(false);
        when(jsonMapper.treeToValue(eq(root), any())).thenReturn(new io.github.guoshiqiufeng.dify.chat.dto.response.ChatMessageSendResponse());

        ChatMessageSendCompletionResponseDto result = deserializer.deserialize(root, jsonMapper);

        assertNotNull(result);
        assertNotNull(result.getData());
        assertNull(result.getData().getData());
    }

    @Test
    void testDeserializeWithToolLabels() {
        JsonNode root = mock(JsonNode.class);
        JsonNode toolLabelsNode = mock(JsonNode.class);
        JsonNode labelValueNode = mock(JsonNode.class);

        when(root.get("event")).thenReturn(null);
        when(root.has("tool_labels")).thenReturn(true);
        when(root.get("tool_labels")).thenReturn(toolLabelsNode);
        when(toolLabelsNode.isObject()).thenReturn(true);
        Iterator<String> fieldNames = Arrays.asList("label1").iterator();
        when(toolLabelsNode.fieldNames()).thenReturn(fieldNames);
        when(toolLabelsNode.get("label1")).thenReturn(labelValueNode);
        when(labelValueNode.isTextual()).thenReturn(true);
        when(labelValueNode.asText()).thenReturn("value1");
        when(jsonMapper.treeToValue(any(), any())).thenReturn(new io.github.guoshiqiufeng.dify.chat.dto.response.ChatMessageSendResponse());

        ChatMessageSendCompletionResponseDto result = deserializer.deserialize(root, jsonMapper);

        assertNotNull(result);
        assertNotNull(result.getData().getToolLabels());
        assertEquals("value1", result.getData().getToolLabels().get("label1"));
    }

    @Test
    void testDeserializeWithMessageFiles() {
        JsonNode root = mock(JsonNode.class);
        JsonNode messageFilesNode = mock(JsonNode.class);
        JsonNode fileNode = mock(JsonNode.class);

        when(root.get("event")).thenReturn(null);
        when(root.has("message_files")).thenReturn(true);
        when(root.get("message_files")).thenReturn(messageFilesNode);
        when(messageFilesNode.isArray()).thenReturn(true);
        Iterator<JsonNode> elements = Arrays.asList(fileNode).iterator();
        when(messageFilesNode.elements()).thenReturn(elements);
        when(fileNode.asText()).thenReturn("file-123");
        when(jsonMapper.treeToValue(any(), any())).thenReturn(new io.github.guoshiqiufeng.dify.chat.dto.response.ChatMessageSendResponse());

        ChatMessageSendCompletionResponseDto result = deserializer.deserialize(root, jsonMapper);

        assertNotNull(result);
        assertNotNull(result.getData().getMessageFiles());
        assertEquals(1, result.getData().getMessageFiles().size());
        assertEquals("file-123", result.getData().getMessageFiles().get(0));
    }

    @Test
    void testDeserializeWithNonTextualEvent() {
        JsonNode root = mock(JsonNode.class);
        JsonNode eventNode = mock(JsonNode.class);

        when(root.get("event")).thenReturn(eventNode);
        when(eventNode.isTextual()).thenReturn(false);
        when(jsonMapper.treeToValue(any(), any())).thenReturn(new io.github.guoshiqiufeng.dify.chat.dto.response.ChatMessageSendResponse());

        ChatMessageSendCompletionResponseDto result = deserializer.deserialize(root, jsonMapper);

        assertNotNull(result);
        assertNotNull(result.getData());
    }

    @Test
    void testDeserializeWithNullToolLabels() {
        JsonNode root = mock(JsonNode.class);
        JsonNode toolLabelsNode = mock(JsonNode.class);

        when(root.get("event")).thenReturn(null);
        when(root.has("tool_labels")).thenReturn(true);
        when(root.get("tool_labels")).thenReturn(toolLabelsNode);
        when(toolLabelsNode.isObject()).thenReturn(false);
        when(jsonMapper.treeToValue(any(), any())).thenReturn(new io.github.guoshiqiufeng.dify.chat.dto.response.ChatMessageSendResponse());

        ChatMessageSendCompletionResponseDto result = deserializer.deserialize(root, jsonMapper);

        assertNotNull(result);
        assertNotNull(result.getData().getToolLabels());
        assertTrue(result.getData().getToolLabels().isEmpty());
    }

    @Test
    void testDeserializeWithNullMessageFiles() {
        JsonNode root = mock(JsonNode.class);
        JsonNode messageFilesNode = mock(JsonNode.class);

        when(root.get("event")).thenReturn(null);
        when(root.has("message_files")).thenReturn(true);
        when(root.get("message_files")).thenReturn(messageFilesNode);
        when(messageFilesNode.isArray()).thenReturn(false);
        when(jsonMapper.treeToValue(any(), any())).thenReturn(new io.github.guoshiqiufeng.dify.chat.dto.response.ChatMessageSendResponse());

        ChatMessageSendCompletionResponseDto result = deserializer.deserialize(root, jsonMapper);

        assertNotNull(result);
        assertNotNull(result.getData().getMessageFiles());
        assertTrue(result.getData().getMessageFiles().isEmpty());
    }

    @Test
    void testDeserializeWithToolLabelsContainingDifferentTypes() {
        JsonNode root = mock(JsonNode.class);
        JsonNode toolLabelsNode = mock(JsonNode.class);
        JsonNode textNode = mock(JsonNode.class);
        JsonNode numberNode = mock(JsonNode.class);
        JsonNode booleanNode = mock(JsonNode.class);
        JsonNode nullNode = mock(JsonNode.class);
        JsonNode arrayNode = mock(JsonNode.class);
        JsonNode objectNode = mock(JsonNode.class);
        JsonNode arrayElement = mock(JsonNode.class);

        when(root.get("event")).thenReturn(null);
        when(root.has("tool_labels")).thenReturn(true);
        when(root.get("tool_labels")).thenReturn(toolLabelsNode);
        when(toolLabelsNode.isObject()).thenReturn(true);
        Iterator<String> fieldNames = Arrays.asList("text", "number", "boolean", "null", "array", "object", "nullField").iterator();
        when(toolLabelsNode.fieldNames()).thenReturn(fieldNames);

        when(toolLabelsNode.get("text")).thenReturn(textNode);
        when(textNode.isNull()).thenReturn(false);
        when(textNode.isTextual()).thenReturn(true);
        when(textNode.asText()).thenReturn("textValue");

        when(toolLabelsNode.get("number")).thenReturn(numberNode);
        when(numberNode.isNull()).thenReturn(false);
        when(numberNode.isTextual()).thenReturn(false);
        when(numberNode.isNumber()).thenReturn(true);
        when(numberNode.asDouble()).thenReturn(42.5);

        when(toolLabelsNode.get("boolean")).thenReturn(booleanNode);
        when(booleanNode.isNull()).thenReturn(false);
        when(booleanNode.isTextual()).thenReturn(false);
        when(booleanNode.isNumber()).thenReturn(false);
        when(booleanNode.isBoolean()).thenReturn(true);
        when(booleanNode.asBoolean()).thenReturn(true);

        when(toolLabelsNode.get("null")).thenReturn(nullNode);
        when(nullNode.isNull()).thenReturn(true);

        when(toolLabelsNode.get("array")).thenReturn(arrayNode);
        when(arrayNode.isNull()).thenReturn(false);
        when(arrayNode.isTextual()).thenReturn(false);
        when(arrayNode.isNumber()).thenReturn(false);
        when(arrayNode.isBoolean()).thenReturn(false);
        when(arrayNode.isArray()).thenReturn(true);
        when(arrayNode.elements()).thenReturn(Arrays.asList(arrayElement).iterator());
        when(arrayElement.isNull()).thenReturn(false);
        when(arrayElement.isTextual()).thenReturn(true);
        when(arrayElement.asText()).thenReturn("element1");

        when(toolLabelsNode.get("object")).thenReturn(objectNode);
        when(objectNode.isNull()).thenReturn(false);
        when(objectNode.isTextual()).thenReturn(false);
        when(objectNode.isNumber()).thenReturn(false);
        when(objectNode.isBoolean()).thenReturn(false);
        when(objectNode.isArray()).thenReturn(false);
        when(objectNode.isObject()).thenReturn(true);
        when(objectNode.fieldNames()).thenReturn(Collections.emptyIterator());

        when(toolLabelsNode.get("nullField")).thenReturn(null);

        when(jsonMapper.treeToValue(any(), any())).thenReturn(new io.github.guoshiqiufeng.dify.chat.dto.response.ChatMessageSendResponse());

        ChatMessageSendCompletionResponseDto result = deserializer.deserialize(root, jsonMapper);

        assertNotNull(result);
        Map<String, Object> toolLabels = result.getData().getToolLabels();
        assertNotNull(toolLabels);
        assertEquals("textValue", toolLabels.get("text"));
        assertEquals(42.5, toolLabels.get("number"));
        assertEquals(true, toolLabels.get("boolean"));
        assertNull(toolLabels.get("null"));
        assertTrue(toolLabels.get("array") instanceof List);
        assertTrue(toolLabels.get("object") instanceof Map);
        assertNull(toolLabels.get("nullField"));
    }

    @Test
    void testDeserializeWithDefaultConvertToObject() {
        JsonNode root = mock(JsonNode.class);
        JsonNode toolLabelsNode = mock(JsonNode.class);
        JsonNode unknownNode = mock(JsonNode.class);

        when(root.get("event")).thenReturn(null);
        when(root.has("tool_labels")).thenReturn(true);
        when(root.get("tool_labels")).thenReturn(toolLabelsNode);
        when(toolLabelsNode.isObject()).thenReturn(true);
        Iterator<String> fieldNames = Arrays.asList("unknown").iterator();
        when(toolLabelsNode.fieldNames()).thenReturn(fieldNames);

        when(toolLabelsNode.get("unknown")).thenReturn(unknownNode);
        when(unknownNode.isNull()).thenReturn(false);
        when(unknownNode.isTextual()).thenReturn(false);
        when(unknownNode.isNumber()).thenReturn(false);
        when(unknownNode.isBoolean()).thenReturn(false);
        when(unknownNode.isArray()).thenReturn(false);
        when(unknownNode.isObject()).thenReturn(false);
        when(unknownNode.asText()).thenReturn("fallback");

        when(jsonMapper.treeToValue(any(), any())).thenReturn(new io.github.guoshiqiufeng.dify.chat.dto.response.ChatMessageSendResponse());

        ChatMessageSendCompletionResponseDto result = deserializer.deserialize(root, jsonMapper);

        assertNotNull(result);
        Map<String, Object> toolLabels = result.getData().getToolLabels();
        assertNotNull(toolLabels);
        assertEquals("fallback", toolLabels.get("unknown"));
    }

    @Test
    void testDeserializeWithNullToolLabelsNode() {
        JsonNode root = mock(JsonNode.class);

        when(root.get("event")).thenReturn(null);
        when(root.has("tool_labels")).thenReturn(true);
        when(root.get("tool_labels")).thenReturn(null);
        when(jsonMapper.treeToValue(any(), any())).thenReturn(new io.github.guoshiqiufeng.dify.chat.dto.response.ChatMessageSendResponse());

        ChatMessageSendCompletionResponseDto result = deserializer.deserialize(root, jsonMapper);

        assertNotNull(result);
        assertNotNull(result.getData().getToolLabels());
        assertTrue(result.getData().getToolLabels().isEmpty());
    }

    @Test
    void testDeserializeWithNullMessageFilesNode() {
        JsonNode root = mock(JsonNode.class);

        when(root.get("event")).thenReturn(null);
        when(root.has("message_files")).thenReturn(true);
        when(root.get("message_files")).thenReturn(null);
        when(jsonMapper.treeToValue(any(), any())).thenReturn(new io.github.guoshiqiufeng.dify.chat.dto.response.ChatMessageSendResponse());

        ChatMessageSendCompletionResponseDto result = deserializer.deserialize(root, jsonMapper);

        assertNotNull(result);
        assertNotNull(result.getData().getMessageFiles());
        assertTrue(result.getData().getMessageFiles().isEmpty());
    }
}
