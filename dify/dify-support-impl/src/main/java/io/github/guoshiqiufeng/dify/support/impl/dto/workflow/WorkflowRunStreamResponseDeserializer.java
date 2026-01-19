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

import io.github.guoshiqiufeng.dify.client.core.codec.JsonDeserializer;
import io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper;
import io.github.guoshiqiufeng.dify.client.core.codec.JsonNode;
import io.github.guoshiqiufeng.dify.client.core.codec.utils.JsonNodeUtils;
import io.github.guoshiqiufeng.dify.workflow.dto.response.WorkflowRunStreamResponse;
import io.github.guoshiqiufeng.dify.workflow.enums.StreamEventEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * WorkflowRunStreamResponse 通用反序列化器
 * <p>
 * 基于抽象的 JsonNode 接口实现,与具体 JSON 库解耦
 * </p>
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025/12/30
 */
@Slf4j
public class WorkflowRunStreamResponseDeserializer implements JsonDeserializer<WorkflowRunStreamResponseDto> {

    private static final String CONSTANT_EVENT = "event";
    private static final String CONSTANT_TASK_ID = "task_id";
    private static final String CONSTANT_WORKFLOW_RUN_ID = "workflow_run_id";
    private static final String CONSTANT_DATA = "data";

    /**
     * 反序列化 JSON 节点为 WorkflowRunStreamResponse
     *
     * @param root       JSON 根节点
     * @param jsonMapper JSON 映射器
     * @return 反序列化后的对象
     */
    @Override
    public WorkflowRunStreamResponseDto deserialize(JsonNode root, JsonMapper jsonMapper) {
        JsonNode eventNode = root.get(CONSTANT_EVENT);
        if (eventNode == null || !eventNode.isTextual()) {
            return new WorkflowRunStreamResponseDto(builderResponse(root, jsonMapper));
        }

        StreamEventEnum event;
        Class<?> dataClass;
        try {
            event = StreamEventEnum.valueOf(eventNode.asText());
            dataClass = event.getClazz();
        } catch (IllegalArgumentException e) {
            log.warn("Unknown event type: {}", eventNode.asText());
            return new WorkflowRunStreamResponseDto(builderResponse(root, jsonMapper));
        }

        WorkflowRunStreamResponse response = builderResponse(root, jsonMapper);

        if (root.has(CONSTANT_DATA)) {
            JsonNode dataNode = root.get(CONSTANT_DATA);
            if (dataNode != null && !dataNode.isNull()) {
                Object data;
                if (dataClass == Map.class) {
                    data = JsonNodeUtils.convertToMap(dataNode);
                } else {
                    data = jsonMapper.treeToValue(dataNode, dataClass);
                }
                response.setData(data);
            }
        }
        return new WorkflowRunStreamResponseDto(response);
    }

    private static WorkflowRunStreamResponse builderResponse(JsonNode root, JsonMapper jsonMapper) {
        WorkflowRunStreamResponse response = new WorkflowRunStreamResponse();

        if (root.has(CONSTANT_TASK_ID)) {
            response.setTaskId(root.get(CONSTANT_TASK_ID).asText());
        }
        if (root.has(CONSTANT_EVENT)) {
            try {
                StreamEventEnum streamEventEnum = StreamEventEnum.valueOf(root.get(CONSTANT_EVENT).asText());
                response.setEvent(streamEventEnum);
            } catch (Exception ignored) {
            }
        }
        if (root.has(CONSTANT_WORKFLOW_RUN_ID)) {
            response.setWorkflowRunId(root.get(CONSTANT_WORKFLOW_RUN_ID).asText());
        }
        return response;
    }
}
