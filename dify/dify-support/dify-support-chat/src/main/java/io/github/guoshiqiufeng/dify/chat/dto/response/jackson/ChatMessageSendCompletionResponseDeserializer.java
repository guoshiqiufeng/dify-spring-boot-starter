package io.github.guoshiqiufeng.dify.chat.dto.response.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.guoshiqiufeng.dify.chat.dto.response.ChatMessageSendCompletionResponse;
import io.github.guoshiqiufeng.dify.chat.dto.response.ChatMessageSendResponse;
import io.github.guoshiqiufeng.dify.chat.dto.response.message.CompletionData;
import io.github.guoshiqiufeng.dify.chat.enums.StreamEventEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.io.IOException;

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
    private static final String CONSTANT_DATA = "data";

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
        return chatMessageSendCompletionResponse;
    }
}
