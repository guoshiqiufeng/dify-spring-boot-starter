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
package io.github.guoshiqiufeng.dify.workflow.dto.response.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.guoshiqiufeng.dify.workflow.dto.response.WorkflowRunStreamResponse;
import io.github.guoshiqiufeng.dify.workflow.enums.StreamEventEnum;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/13 11:01
 */
@Slf4j
public class WorkflowRunStreamResponseDeserializer
        extends StdDeserializer<WorkflowRunStreamResponse> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String CONSTANT_EVENT = "event";
    private static final String CONSTANT_TASK_ID = "task_id";
    private static final String CONSTANT_WORKFLOW_RUN_ID = "workflow_run_id";
    private static final String CONSTANT_DATA = "data";

    static {
        MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public WorkflowRunStreamResponseDeserializer() {
        super(WorkflowRunStreamResponse.class);
    }

    @Override
    public WorkflowRunStreamResponse deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {

        ObjectNode root = MAPPER.readTree(p);

        JsonNode eventNode = root.get(CONSTANT_EVENT);
        if (eventNode == null || !eventNode.isTextual()) {
            return builderResponse(root);
        }

        StreamEventEnum event;
        Class<?> dataClass;
        try {
            event = StreamEventEnum.valueOf(eventNode.asText());
            dataClass = event.getClazz();
        } catch (IllegalArgumentException e) {
            log.warn("Unknown event type: {}", eventNode.asText());
            return builderResponse(root);
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
        return response;
    }

    private static WorkflowRunStreamResponse builderResponse(ObjectNode root) throws JsonProcessingException {
        WorkflowRunStreamResponse chatMessageSendCompletionResponse = new WorkflowRunStreamResponse();
        if (root.has(CONSTANT_TASK_ID)) {
            chatMessageSendCompletionResponse.setTaskId(root.get(CONSTANT_TASK_ID).asText());
        }
        if (root.has(CONSTANT_EVENT)) {
            try {
                StreamEventEnum streamEventEnum = StreamEventEnum.valueOf(root.get(CONSTANT_EVENT).asText());
                chatMessageSendCompletionResponse.setEvent(streamEventEnum);
            } catch (Exception ignored) {
            }
        }
        if (root.has(CONSTANT_WORKFLOW_RUN_ID)) {
            chatMessageSendCompletionResponse.setWorkflowRunId(root.get(CONSTANT_WORKFLOW_RUN_ID).asText());
        }
        return chatMessageSendCompletionResponse;
    }
}
