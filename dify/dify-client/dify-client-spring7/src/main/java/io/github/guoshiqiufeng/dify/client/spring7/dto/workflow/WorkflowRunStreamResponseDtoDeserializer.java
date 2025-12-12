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
package io.github.guoshiqiufeng.dify.client.spring7.dto.workflow;

import io.github.guoshiqiufeng.dify.workflow.dto.response.WorkflowRunStreamResponse;
import io.github.guoshiqiufeng.dify.workflow.enums.StreamEventEnum;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.JsonParser;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.json.JsonMapper;

import java.util.Map;

/**
 * @author yanghq
 * @version 1.6.2
 * @since 2025/12/12 09:29
 */
@Slf4j
public class WorkflowRunStreamResponseDtoDeserializer extends StdDeserializer<WorkflowRunStreamResponseDto> {

    private static final JsonMapper MAPPER = JsonMapper.builder().build();

    private static final String CONSTANT_EVENT = "event";
    private static final String CONSTANT_TASK_ID = "task_id";
    private static final String CONSTANT_WORKFLOW_RUN_ID = "workflow_run_id";
    private static final String CONSTANT_DATA = "data";

    public WorkflowRunStreamResponseDtoDeserializer() {
        super(WorkflowRunStreamResponseDto.class);
    }

    @Override
    public WorkflowRunStreamResponseDto deserialize(JsonParser p, DeserializationContext ctxt) {

        JsonNode root = MAPPER.readTree(p);

        JsonNode eventNode = root.get(CONSTANT_EVENT);
        if (eventNode == null || !eventNode.isString()) {
            return new WorkflowRunStreamResponseDto(builderResponse(root));
        }

        StreamEventEnum event;
        Class<?> dataClass;
        try {
            event = StreamEventEnum.valueOf(eventNode.asString());
            dataClass = event.getClazz();
        } catch (IllegalArgumentException e) {
            log.warn("Unknown event type: {}", eventNode.asString());
            return new WorkflowRunStreamResponseDto(builderResponse(root));
        }

        WorkflowRunStreamResponse response = builderResponse(root);
        JsonNode dataNode = root.get("data");

        if (root.has(CONSTANT_DATA)) {
            Object data = null;
            if (dataNode != null && !dataNode.isNull()) {
                if (dataClass == Map.class) {
                    data = MAPPER.convertValue(dataNode, new TypeReference<Map<String, Object>>() {
                    });
                } else {
                    data = MAPPER.treeToValue(dataNode, dataClass);
                }
            }
            response.setData(data);
        }
        return new WorkflowRunStreamResponseDto(response);
    }

    private static WorkflowRunStreamResponse builderResponse(JsonNode root) {
        WorkflowRunStreamResponse chatMessageSendCompletionResponse = new WorkflowRunStreamResponse();
        if (root.has(CONSTANT_TASK_ID)) {
            chatMessageSendCompletionResponse.setTaskId(root.get(CONSTANT_TASK_ID).asString());
        }
        if (root.has(CONSTANT_EVENT)) {
            try {
                StreamEventEnum streamEventEnum = StreamEventEnum.valueOf(root.get(CONSTANT_EVENT).asString());
                chatMessageSendCompletionResponse.setEvent(streamEventEnum);
            } catch (Exception ignored) {
            }
        }
        if (root.has(CONSTANT_WORKFLOW_RUN_ID)) {
            chatMessageSendCompletionResponse.setWorkflowRunId(root.get(CONSTANT_WORKFLOW_RUN_ID).asString());
        }
        return chatMessageSendCompletionResponse;
    }
}
