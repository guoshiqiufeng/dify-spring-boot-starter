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
package io.github.guoshiqiufeng.dify.dataset.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link ModelFeatureEnum}.
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/10 14:46
 */
class ModelFeatureEnumTest {

    @Test
    @DisplayName("Test enum count")
    void testEnumCount() {
        assertEquals(8, ModelFeatureEnum.values().length);
    }

    @ParameterizedTest
    @EnumSource(ModelFeatureEnum.class)
    @DisplayName("Test each enum value has non-null code")
    void testEnumValuesNotNull(ModelFeatureEnum featureEnum) {
        assertNotNull(featureEnum.getCode());
        assertFalse(featureEnum.getCode().isEmpty());
    }

    @Test
    @DisplayName("Test specific enum values and their codes")
    void testSpecificEnumValues() {
        assertEquals("tool-call", ModelFeatureEnum.TOOL_CALL.getCode());
        assertEquals("multi-tool-call", ModelFeatureEnum.MULTI_TOOL_CALL.getCode());
        assertEquals("agent-thought", ModelFeatureEnum.AGENT_THOUGHT.getCode());
        assertEquals("vision", ModelFeatureEnum.VISION.getCode());
        assertEquals("stream-tool-call", ModelFeatureEnum.STREAM_TOOL_CALL.getCode());
        assertEquals("document", ModelFeatureEnum.DOCUMENT.getCode());
        assertEquals("video", ModelFeatureEnum.VIDEO.getCode());
        assertEquals("audio", ModelFeatureEnum.AUDIO.getCode());
    }

    @Test
    @DisplayName("Test codeOf method with valid codes")
    void testCodeOfWithValidCodes() {
        assertEquals(ModelFeatureEnum.TOOL_CALL, ModelFeatureEnum.codeOf("tool-call"));
        assertEquals(ModelFeatureEnum.MULTI_TOOL_CALL, ModelFeatureEnum.codeOf("multi-tool-call"));
        assertEquals(ModelFeatureEnum.AGENT_THOUGHT, ModelFeatureEnum.codeOf("agent-thought"));
        assertEquals(ModelFeatureEnum.VISION, ModelFeatureEnum.codeOf("vision"));
        assertEquals(ModelFeatureEnum.STREAM_TOOL_CALL, ModelFeatureEnum.codeOf("stream-tool-call"));
        assertEquals(ModelFeatureEnum.DOCUMENT, ModelFeatureEnum.codeOf("document"));
        assertEquals(ModelFeatureEnum.VIDEO, ModelFeatureEnum.codeOf("video"));
        assertEquals(ModelFeatureEnum.AUDIO, ModelFeatureEnum.codeOf("audio"));
    }

    @Test
    @DisplayName("Test codeOf method with invalid code")
    void testCodeOfWithInvalidCode() {
        assertNull(ModelFeatureEnum.codeOf("invalid-feature"));
    }

    @Test
    @DisplayName("Test valueOf method with valid names")
    void testValueOfWithValidNames() {
        assertEquals(ModelFeatureEnum.TOOL_CALL, ModelFeatureEnum.valueOf("TOOL_CALL"));
        assertEquals(ModelFeatureEnum.MULTI_TOOL_CALL, ModelFeatureEnum.valueOf("MULTI_TOOL_CALL"));
        assertEquals(ModelFeatureEnum.AGENT_THOUGHT, ModelFeatureEnum.valueOf("AGENT_THOUGHT"));
        assertEquals(ModelFeatureEnum.VISION, ModelFeatureEnum.valueOf("VISION"));
        assertEquals(ModelFeatureEnum.STREAM_TOOL_CALL, ModelFeatureEnum.valueOf("STREAM_TOOL_CALL"));
        assertEquals(ModelFeatureEnum.DOCUMENT, ModelFeatureEnum.valueOf("DOCUMENT"));
        assertEquals(ModelFeatureEnum.VIDEO, ModelFeatureEnum.valueOf("VIDEO"));
        assertEquals(ModelFeatureEnum.AUDIO, ModelFeatureEnum.valueOf("AUDIO"));
    }

    @Test
    @DisplayName("Test valueOf method with invalid name")
    void testValueOfWithInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> ModelFeatureEnum.valueOf("INVALID_FEATURE"));
    }
}
