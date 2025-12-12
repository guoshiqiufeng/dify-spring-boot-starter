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
package io.github.guoshiqiufeng.dify.client.spring7.dto.dataset;


import io.github.guoshiqiufeng.dify.dataset.dto.response.SegmentData;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.json.JsonMapper;

/**
 * @author yanghq
 * @version 1.6.2
 * @since 2025/12/11 19:55
 */
public class SegmentDataResponseDtoDeserializer extends StdDeserializer<SegmentDataResponseDto> {

    private static final JsonMapper MAPPER = JsonMapper.builder().build();

    private static final String CONSTANT_DATA = "data";

    public SegmentDataResponseDtoDeserializer() {
        super(SegmentDataResponseDto.class);
    }

    @Override
    public SegmentDataResponseDto deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        JsonNode root = MAPPER.readTree(p);

        JsonNode eventNode = root.get(CONSTANT_DATA);
        if (eventNode == null) {
            SegmentData segmentData = MAPPER.treeToValue(root, SegmentData.class);
            return new SegmentDataResponseDto(segmentData);
        }
        SegmentData segmentData = MAPPER.treeToValue(eventNode, SegmentData.class);
        return new SegmentDataResponseDto(segmentData);
    }


}
