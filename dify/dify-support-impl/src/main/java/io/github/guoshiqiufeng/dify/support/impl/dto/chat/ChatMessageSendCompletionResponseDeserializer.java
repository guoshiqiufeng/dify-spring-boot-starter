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

import io.github.guoshiqiufeng.dify.chat.dto.response.ChatMessageSendCompletionResponse;
import io.github.guoshiqiufeng.dify.chat.dto.response.ChatMessageSendResponse;
import io.github.guoshiqiufeng.dify.chat.dto.response.message.CompletionData;
import io.github.guoshiqiufeng.dify.chat.enums.StreamEventEnum;
import io.github.guoshiqiufeng.dify.client.core.codec.JsonDeserializer;
import io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper;
import io.github.guoshiqiufeng.dify.client.core.codec.JsonNode;
import io.github.guoshiqiufeng.dify.client.core.codec.utils.JsonNodeUtils;
import io.github.guoshiqiufeng.dify.core.bean.BeanUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * ChatMessageSendCompletionResponse 通用反序列化器
 * <p>
 * 基于抽象的 JsonNode 接口实现,与具体 JSON 库解耦
 * 各模块传入对应的 JsonMapper 实现即可使用
 * </p>
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025/12/30
 */
@Slf4j
public class ChatMessageSendCompletionResponseDeserializer implements JsonDeserializer<ChatMessageSendCompletionResponseDto> {

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

    /**
     * 反序列化 JSON 节点为 ChatMessageSendCompletionResponseDto
     *
     * @param root       JSON 根节点
     * @param jsonMapper JSON 映射器
     * @return 反序列化后的对象
     */
    @Override
    public ChatMessageSendCompletionResponseDto deserialize(JsonNode root, JsonMapper jsonMapper) {
        JsonNode eventNode = root.get(CONSTANT_EVENT);
        if (eventNode == null || !eventNode.isTextual()) {
            return new ChatMessageSendCompletionResponseDto(builderResponse(root, jsonMapper));
        }

        StreamEventEnum event;
        Class<? extends CompletionData> dataClass;
        try {
            event = StreamEventEnum.valueOf(eventNode.asText());
            dataClass = event.getClazz();
        } catch (IllegalArgumentException e) {
            log.warn("Unknown event type: {}", eventNode.asText());
            return new ChatMessageSendCompletionResponseDto(builderResponse(root, jsonMapper));
        }

        ChatMessageSendCompletionResponse response = builderResponse(root, jsonMapper);

        if (root.has(CONSTANT_DATA)) {
            JsonNode dataNode = root.get(CONSTANT_DATA);
            if (dataNode != null && !dataNode.isNull()) {
                CompletionData data = jsonMapper.treeToValue(dataNode, dataClass);
                response.setData(data);
            }
        }
        return new ChatMessageSendCompletionResponseDto(response);
    }

    private static ChatMessageSendCompletionResponse builderResponse(JsonNode root, JsonMapper jsonMapper) {
        ChatMessageSendResponse chatMessageSendResponse = jsonMapper.treeToValue(root, ChatMessageSendResponse.class);
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
            Map<String, Object> toolLabels = JsonNodeUtils.convertToMap(toolLabelsNode);
            chatMessageSendCompletionResponse.setToolLabels(toolLabels);
        }
        if (root.has(TOOL_INPUT)) {
            chatMessageSendCompletionResponse.setToolInput(root.get(TOOL_INPUT).asText());
        }
        if (root.has(MESSAGE_FILES)) {
            JsonNode messageFilesNode = root.get(MESSAGE_FILES);
            List<String> messageFiles = JsonNodeUtils.convertToStringList(messageFilesNode);
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
