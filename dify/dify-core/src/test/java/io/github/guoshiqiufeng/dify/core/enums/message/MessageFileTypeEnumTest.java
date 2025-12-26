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
package io.github.guoshiqiufeng.dify.core.enums.message;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for the {@link MessageFileTypeEnum} class.
 *
 * @author yanghq
 * @version 1.1.0
 * @since 2023/5/30 13:13
 */
public class MessageFileTypeEnumTest {

    @ParameterizedTest
    @EnumSource(MessageFileTypeEnum.class)
    void shouldHaveValidEnumValues(MessageFileTypeEnum type) {
        assertNotNull(type);
        assertNotNull(type.name());
    }

    @Test
    void shouldHaveExpectedEnumValues() {
        assertEquals(5, MessageFileTypeEnum.values().length);
        assertEquals("document", MessageFileTypeEnum.document.name());
        assertEquals("custom", MessageFileTypeEnum.custom.name());
    }
}