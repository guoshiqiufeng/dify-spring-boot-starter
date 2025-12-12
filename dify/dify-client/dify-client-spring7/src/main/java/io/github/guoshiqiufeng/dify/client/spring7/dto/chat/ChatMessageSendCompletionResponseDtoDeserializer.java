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
package io.github.guoshiqiufeng.dify.client.spring7.dto.chat;

import io.github.guoshiqiufeng.dify.chat.dto.response.ChatMessageSendCompletionResponse;
import io.github.guoshiqiufeng.dify.chat.dto.response.ChatMessageSendResponse;
import io.github.guoshiqiufeng.dify.chat.dto.response.message.CompletionData;
import io.github.guoshiqiufeng.dify.chat.enums.StreamEventEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import tools.jackson.core.JsonParser;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Map;

/**
 * @author yanghq
 * @version 1.6.2
 * @since 2025/12/11 20:16
 */
@Slf4j
public class ChatMessageSendCompletionResponseDtoDeserializer extends StdDeserializer<ChatMessageSendCompletionResponseDto> {

    private static final JsonMapper MAPPER = JsonMapper.builder().build();

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

    public ChatMessageSendCompletionResponseDtoDeserializer() {
        super(ChatMessageSendCompletionResponseDto.class);
    }

    @Override
    public ChatMessageSendCompletionResponseDto deserialize(JsonParser p, DeserializationContext ctxt) {

        JsonNode root = MAPPER.readTree(p);

        JsonNode eventNode = root.get(CONSTANT_EVENT);
        if (eventNode == null || !eventNode.isString()) {
            return new ChatMessageSendCompletionResponseDto(builderResponse(root));
        }

        StreamEventEnum event;
        Class<? extends CompletionData> dataClass;
        try {
            event = StreamEventEnum.valueOf(eventNode.asString());
            dataClass = event.getClazz();
        } catch (IllegalArgumentException e) {
            log.warn("Unknown event type: {}", eventNode.asString());
            return new ChatMessageSendCompletionResponseDto(builderResponse(root));
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
        return new ChatMessageSendCompletionResponseDto(response);
    }

    private static ChatMessageSendCompletionResponse builderResponse(JsonNode root) {
        ChatMessageSendResponse chatMessageSendResponse = MAPPER.treeToValue(root, ChatMessageSendResponse.class);
        ChatMessageSendCompletionResponse chatMessageSendCompletionResponse = new ChatMessageSendCompletionResponse();
        BeanUtils.copyProperties(chatMessageSendResponse, chatMessageSendCompletionResponse);
        if (root.has(CONSTANT_WORKFLOW_RUN_ID)) {
            chatMessageSendCompletionResponse.setWorkflowRunId(root.get(CONSTANT_WORKFLOW_RUN_ID).asString());
        }
        if (root.has(POSITION)) {
            chatMessageSendCompletionResponse.setPosition(root.get(POSITION).asInt());
        }
        if (root.has(THOUGHT)) {
            chatMessageSendCompletionResponse.setThought(root.get(THOUGHT).asString());
        }
        if (root.has(OBSERVATION)) {
            chatMessageSendCompletionResponse.setObservation(root.get(OBSERVATION).asString());
        }
        if (root.has(TOOL)) {
            chatMessageSendCompletionResponse.setTool(root.get(TOOL).asString());
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
            chatMessageSendCompletionResponse.setToolInput(root.get(TOOL_INPUT).asString());
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
            chatMessageSendCompletionResponse.setTaskId(root.get(CONSTANT_TASK_ID).asString());
        }
        if (root.has(CONSTANT_STATUS)) {
            chatMessageSendCompletionResponse.setStatus(root.get(CONSTANT_STATUS).asInt());
        }
        if (root.has(CONSTANT_CODE)) {
            chatMessageSendCompletionResponse.setCode(root.get(CONSTANT_CODE).asString());
        }
        if (root.has(CONSTANT_MESSAGE)) {
            chatMessageSendCompletionResponse.setMessage(root.get(CONSTANT_MESSAGE).asString());
        }
        return chatMessageSendCompletionResponse;
    }
}
