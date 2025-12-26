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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link ModeEnum}.
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/3/13 15:21
 */
class ModeEnumTest {

    @Test
    @DisplayName("Test enum count")
    void testEnumCount() {
        assertEquals(3, ModeEnum.values().length);
    }

    @Test
    @DisplayName("Test specific enum values")
    void testSpecificEnumValues() {
        assertNotNull(ModeEnum.automatic);
        assertNotNull(ModeEnum.custom);
        assertNotNull(ModeEnum.hierarchical);
    }

    @Test
    @DisplayName("Test enum names")
    void testEnumNames() {
        assertEquals("automatic", ModeEnum.automatic.name());
        assertEquals("custom", ModeEnum.custom.name());
        assertEquals("hierarchical", ModeEnum.hierarchical.name());
    }

    @Test
    @DisplayName("Test enum ordinals")
    void testEnumOrdinals() {
        assertEquals(0, ModeEnum.automatic.ordinal());
        assertEquals(1, ModeEnum.custom.ordinal());
        assertEquals(2, ModeEnum.hierarchical.ordinal());
    }

    @Test
    @DisplayName("Test valueOf method with valid names")
    void testValueOfWithValidNames() {
        assertEquals(ModeEnum.automatic, ModeEnum.valueOf("automatic"));
        assertEquals(ModeEnum.custom, ModeEnum.valueOf("custom"));
        assertEquals(ModeEnum.hierarchical, ModeEnum.valueOf("hierarchical"));
    }

    @Test
    @DisplayName("Test valueOf method with invalid name")
    void testValueOfWithInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> ModeEnum.valueOf("invalid_mode"));
    }
}
