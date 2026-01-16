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

import io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper;
import io.github.guoshiqiufeng.dify.client.core.codec.JsonNode;
import io.github.guoshiqiufeng.dify.dataset.dto.response.SegmentData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SegmentDataResponseDeserializerTest {

    private SegmentDataResponseDeserializer deserializer;
    private JsonMapper jsonMapper;

    @BeforeEach
    void setUp() {
        deserializer = new SegmentDataResponseDeserializer();
        jsonMapper = mock(JsonMapper.class);
    }

    @Test
    void testDeserializeWithoutDataNode() {
        JsonNode root = mock(JsonNode.class);
        SegmentData segmentData = new SegmentData();

        when(root.get("data")).thenReturn(null);
        when(jsonMapper.treeToValue(eq(root), eq(SegmentData.class))).thenReturn(segmentData);

        SegmentDataResponseDto result = deserializer.deserialize(root, jsonMapper);

        assertNotNull(result);
        assertEquals(segmentData, result.getData());
    }

    @Test
    void testDeserializeWithNullDataNode() {
        JsonNode root = mock(JsonNode.class);
        JsonNode dataNode = mock(JsonNode.class);
        SegmentData segmentData = new SegmentData();

        when(root.get("data")).thenReturn(dataNode);
        when(dataNode.isNull()).thenReturn(true);
        when(jsonMapper.treeToValue(eq(root), eq(SegmentData.class))).thenReturn(segmentData);

        SegmentDataResponseDto result = deserializer.deserialize(root, jsonMapper);

        assertNotNull(result);
        assertEquals(segmentData, result.getData());
    }

    @Test
    void testDeserializeWithValidDataNode() {
        JsonNode root = mock(JsonNode.class);
        JsonNode dataNode = mock(JsonNode.class);
        SegmentData segmentData = new SegmentData();

        when(root.get("data")).thenReturn(dataNode);
        when(dataNode.isNull()).thenReturn(false);
        when(jsonMapper.treeToValue(eq(dataNode), eq(SegmentData.class))).thenReturn(segmentData);

        SegmentDataResponseDto result = deserializer.deserialize(root, jsonMapper);

        assertNotNull(result);
        assertEquals(segmentData, result.getData());
    }
}
