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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.guoshiqiufeng.dify.chat.dto.response.ChatMessageSendCompletionResponse;
import io.github.guoshiqiufeng.dify.chat.dto.response.ChatMessageSendResponse;
import io.github.guoshiqiufeng.dify.chat.dto.response.message.CompletionData;
import io.github.guoshiqiufeng.dify.chat.enums.StreamEventEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author yanghq
 * @version 1.0.3
 * @since 2025/5/29 09:53
 */
@Slf4j
public class ChatMessageSendCompletionResponseDeserializer extends StdDeserializer<ChatMessageSendCompletionResponse> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String CONSTANT_EVENT = "event";
    private static final String CONSTANT_WORKFLOW_RUN_ID = "workflow_run_id";
    private static final String CONSTANT_TASK_ID = "task_id";
    private static final String CONSTANT_STATUS = "status";
    private static final String CONSTANT_CODE = "code";
    private static final String CONSTANT_MESSAGE = "message";
    private static final String CONSTANT_DATA = "data";

    public static final String POSITION = "position";
    public static final String THOUGHT = "thought";
    public static final String OBSERVATION = "observation";
    public static final String TOOL = "tool";
    public static final String TOOL_LABELS = "tool_labels";
    public static final String TOOL_INPUT = "tool_input";
    public static final String MESSAGE_FILES = "message_files";

    static {
        MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public ChatMessageSendCompletionResponseDeserializer() {
        super(ChatMessageSendCompletionResponse.class);
    }

    @Override
    public ChatMessageSendCompletionResponse deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {

        ObjectNode root = MAPPER.readTree(p);

        JsonNode eventNode = root.get(CONSTANT_EVENT);
        if (eventNode == null || !eventNode.isTextual()) {
            return builderResponse(root);
        }

        StreamEventEnum event;
        Class<? extends CompletionData> dataClass;
        try {
            event = StreamEventEnum.valueOf(eventNode.asText());
            dataClass = event.getClazz();
        } catch (IllegalArgumentException e) {
            log.warn("Unknown event type: {}", eventNode.asText());
            return builderResponse(root);
        }

        ChatMessageSendCompletionResponse response = builderResponse(root);

        if (root.has(CONSTANT_DATA)) {
            CompletionData data = null;
            JsonNode dataNode = root.get(CONSTANT_DATA);
            if (dataNode != null && !dataNode.isNull()) {
                data = MAPPER.treeToValue(dataNode, dataClass);
            }
            response.setData(data);
        }
        return response;
    }

    private static ChatMessageSendCompletionResponse builderResponse(ObjectNode root) throws JsonProcessingException {
        ChatMessageSendResponse chatMessageSendResponse = MAPPER.treeToValue(root, ChatMessageSendResponse.class);
        ChatMessageSendCompletionResponse chatMessageSendCompletionResponse = new ChatMessageSendCompletionResponse();
        BeanUtils.copyProperties(chatMessageSendResponse, chatMessageSendCompletionResponse);
        if (root.has(CONSTANT_WORKFLOW_RUN_ID)) {
            chatMessageSendCompletionResponse.setWorkflowRunId(root.get(CONSTANT_WORKFLOW_RUN_ID).asText());
        }
        if (root.has(POSITION)) {
            chatMessageSendCompletionResponse.setPosition(root.get(POSITION).asInt());
        }
        if (root.has(THOUGHT)) {
            chatMessageSendCompletionResponse.setThought(root.get(THOUGHT).asText());
        }
        if (root.has(OBSERVATION)) {
            chatMessageSendCompletionResponse.setObservation(root.get(OBSERVATION).asText());
        }
        if (root.has(TOOL)) {
            chatMessageSendCompletionResponse.setTool(root.get(TOOL).asText());
        }
        if (root.has(TOOL_LABELS)) {
            JsonNode toolLabelsNode = root.get(TOOL_LABELS);
            Map<String, Object> toolLabels = MAPPER.convertValue(
                    toolLabelsNode,
                    new TypeReference<Map<String, Object>>() {
                    }
            );
            chatMessageSendCompletionResponse.setToolLabels(toolLabels);
        }
        if (root.has(TOOL_INPUT)) {
            chatMessageSendCompletionResponse.setToolInput(root.get(TOOL_INPUT).asText());
        }
        if (root.has(MESSAGE_FILES)) {
            JsonNode messageFilesNode = root.get(MESSAGE_FILES);
            List<String> messageFiles = MAPPER.convertValue(
                    messageFilesNode,
                    new TypeReference<List<String>>() {
                    }
            );
            chatMessageSendCompletionResponse.setMessageFiles(messageFiles);
        }

        if (root.has(CONSTANT_TASK_ID)) {
            chatMessageSendCompletionResponse.setTaskId(root.get(CONSTANT_TASK_ID).asText());
        }
        if (root.has(CONSTANT_STATUS)) {
            chatMessageSendCompletionResponse.setStatus(root.get(CONSTANT_STATUS).asInt());
        }
        if (root.has(CONSTANT_CODE)) {
            chatMessageSendCompletionResponse.setCode(root.get(CONSTANT_CODE).asText());
        }
        if (root.has(CONSTANT_MESSAGE)) {
            chatMessageSendCompletionResponse.setMessage(root.get(CONSTANT_MESSAGE).asText());
        }
        return chatMessageSendCompletionResponse;
    }
}
