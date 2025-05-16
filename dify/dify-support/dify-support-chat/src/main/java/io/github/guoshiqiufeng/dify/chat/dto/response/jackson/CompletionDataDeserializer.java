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
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.guoshiqiufeng.dify.chat.dto.response.message.CompletionData;
import io.github.guoshiqiufeng.dify.chat.enums.StreamEventEnum;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/4/15 19:16
 */
@Slf4j
public class CompletionDataDeserializer
        extends JsonDeserializer<CompletionData> {

    @Override
    public CompletionData deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {

        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        JsonNode root = mapper.readTree(p);

        // 解析 event 字段
        JsonNode eventNode = root.get("event");
        if (eventNode == null || !eventNode.isTextual()) {
            return null;
        }

        try {
            StreamEventEnum event = StreamEventEnum.valueOf(eventNode.asText());
            Class<? extends CompletionData> dataClass = event.getClazz();
            // Create a copy of the node without the "event" field to avoid UnrecognizedPropertyException
            ObjectNode rootCopy = root.deepCopy();
            rootCopy.remove("event");

            return mapper.treeToValue(rootCopy, dataClass);
        } catch (IllegalArgumentException e) {
            log.warn("Unknown event type: {}", eventNode.asText());
            return null;
        }
    }
}
