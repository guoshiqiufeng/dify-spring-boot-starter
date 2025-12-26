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
package io.github.guoshiqiufeng.dify.dataset.enums.document;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link ParentModeEnum}.
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/3/13 15:19
 */
class ParentModeEnumTest {

    @Test
    @DisplayName("Test enum count")
    void testEnumCount() {
        assertEquals(2, ParentModeEnum.values().length);
    }

    @ParameterizedTest
    @EnumSource(ParentModeEnum.class)
    @DisplayName("Test each enum value has non-null code")
    void testEnumValuesNotNull(ParentModeEnum modeEnum) {
        assertNotNull(modeEnum.getCode());
        assertFalse(modeEnum.getCode().isEmpty());
    }

    @Test
    @DisplayName("Test specific enum values and their codes")
    void testSpecificEnumValues() {
        assertEquals("full-doc", ParentModeEnum.FULL_DOC.getCode());
        assertEquals("paragraph", ParentModeEnum.PARAGRAPH.getCode());
    }

    @Test
    @DisplayName("Test fromCode method with valid codes")
    void testFromCodeWithValidCodes() {
        assertEquals(ParentModeEnum.FULL_DOC, ParentModeEnum.fromCode("full-doc"));
        assertEquals(ParentModeEnum.PARAGRAPH, ParentModeEnum.fromCode("paragraph"));
    }

    @Test
    @DisplayName("Test fromCode method with invalid code")
    void testFromCodeWithInvalidCode() {
        assertNull(ParentModeEnum.fromCode("invalid-mode"));
    }

    @Test
    @DisplayName("Test valueOf method with valid names")
    void testValueOfWithValidNames() {
        assertEquals(ParentModeEnum.FULL_DOC, ParentModeEnum.valueOf("FULL_DOC"));
        assertEquals(ParentModeEnum.PARAGRAPH, ParentModeEnum.valueOf("PARAGRAPH"));
    }

    @Test
    @DisplayName("Test valueOf method with invalid name")
    void testValueOfWithInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> ParentModeEnum.valueOf("INVALID_MODE"));
    }
}
