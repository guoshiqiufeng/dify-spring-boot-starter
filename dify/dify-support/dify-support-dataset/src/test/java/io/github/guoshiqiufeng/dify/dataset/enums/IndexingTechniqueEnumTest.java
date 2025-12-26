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
package io.github.guoshiqiufeng.dify.dataset.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link IndexingTechniqueEnum}.
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/3/13 13:27
 */
class IndexingTechniqueEnumTest {

    @Test
    @DisplayName("Test enum count")
    void testEnumCount() {
        assertEquals(2, IndexingTechniqueEnum.values().length);
    }

    @ParameterizedTest
    @EnumSource(IndexingTechniqueEnum.class)
    @DisplayName("Test each enum value has non-null code")
    void testEnumValuesNotNull(IndexingTechniqueEnum techniqueEnum) {
        assertNotNull(techniqueEnum.getCode());
        assertFalse(techniqueEnum.getCode().isEmpty());
    }

    @Test
    @DisplayName("Test specific enum values and their codes")
    void testSpecificEnumValues() {
        assertEquals("high_quality", IndexingTechniqueEnum.HIGH_QUALITY.getCode());
        assertEquals("economy", IndexingTechniqueEnum.ECONOMY.getCode());
    }

    @Test
    @DisplayName("Test codeOf method with valid codes")
    void testCodeOfWithValidCodes() {
        assertEquals(IndexingTechniqueEnum.HIGH_QUALITY, IndexingTechniqueEnum.codeOf("high_quality"));
        assertEquals(IndexingTechniqueEnum.ECONOMY, IndexingTechniqueEnum.codeOf("economy"));
    }

    @Test
    @DisplayName("Test codeOf method with invalid code")
    void testCodeOfWithInvalidCode() {
        assertNull(IndexingTechniqueEnum.codeOf("invalid_technique"));
    }

    @Test
    @DisplayName("Test valueOf method with valid names")
    void testValueOfWithValidNames() {
        assertEquals(IndexingTechniqueEnum.HIGH_QUALITY, IndexingTechniqueEnum.valueOf("HIGH_QUALITY"));
        assertEquals(IndexingTechniqueEnum.ECONOMY, IndexingTechniqueEnum.valueOf("ECONOMY"));
    }

    @Test
    @DisplayName("Test valueOf method with invalid name")
    void testValueOfWithInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> IndexingTechniqueEnum.valueOf("INVALID_TECHNIQUE"));
    }
}
