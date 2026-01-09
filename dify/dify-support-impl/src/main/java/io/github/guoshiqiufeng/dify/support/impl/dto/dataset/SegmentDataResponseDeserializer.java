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
package io.github.guoshiqiufeng.dify.support.impl.dto.dataset;

import io.github.guoshiqiufeng.dify.client.core.codec.JsonDeserializer;
import io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper;
import io.github.guoshiqiufeng.dify.client.core.codec.JsonNode;
import io.github.guoshiqiufeng.dify.dataset.dto.response.SegmentData;
import io.github.guoshiqiufeng.dify.support.impl.dto.chat.ChatMessageSendCompletionResponseDto;

/**
 * SegmentData 通用反序列化器
 * <p>
 * 基于抽象的 JsonNode 接口实现,与具体 JSON 库解耦
 * </p>
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025/12/30
 */
public class SegmentDataResponseDeserializer implements JsonDeserializer<SegmentDataResponseDto> {

    private static final String CONSTANT_DATA = "data";

    /**
     * 反序列化 JSON 节点为 SegmentData
     *
     * @param root       JSON 根节点
     * @param jsonMapper JSON 映射器
     * @return 反序列化后的对象
     */
    @Override
    public SegmentDataResponseDto deserialize(JsonNode root, JsonMapper jsonMapper) {
        JsonNode dataNode = root.get(CONSTANT_DATA);
        if (dataNode == null || dataNode.isNull()) {
            return new SegmentDataResponseDto(jsonMapper.treeToValue(root, SegmentData.class));
        }
        return new SegmentDataResponseDto(jsonMapper.treeToValue(dataNode, SegmentData.class));
    }
}
