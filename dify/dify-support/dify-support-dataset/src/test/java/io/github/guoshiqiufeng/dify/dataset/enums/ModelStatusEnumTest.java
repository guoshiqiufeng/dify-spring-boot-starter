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
 * Tests for {@link ModelStatusEnum}.
 *
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/10 14:39
 */
class ModelStatusEnumTest {

    @Test
    @DisplayName("Test enum count")
    void testEnumCount() {
        assertEquals(5, ModelStatusEnum.values().length);
    }

    @ParameterizedTest
    @EnumSource(ModelStatusEnum.class)
    @DisplayName("Test each enum value has non-null code")
    void testEnumValuesNotNull(ModelStatusEnum statusEnum) {
        assertNotNull(statusEnum.getCode());
        assertFalse(statusEnum.getCode().isEmpty());
    }

    @Test
    @DisplayName("Test specific enum values and their codes")
    void testSpecificEnumValues() {
        assertEquals("active", ModelStatusEnum.ACTIVE.getCode());
        assertEquals("no-configure", ModelStatusEnum.NO_CONFIGURE.getCode());
        assertEquals("quota-exceeded", ModelStatusEnum.QUOTA_EXCEEDED.getCode());
        assertEquals("no-permission", ModelStatusEnum.NO_PERMISSION.getCode());
        assertEquals("disabled", ModelStatusEnum.DISABLED.getCode());
    }

    @Test
    @DisplayName("Test codeOf method with valid codes")
    void testCodeOfWithValidCodes() {
        assertEquals(ModelStatusEnum.ACTIVE, ModelStatusEnum.codeOf("active"));
        assertEquals(ModelStatusEnum.NO_CONFIGURE, ModelStatusEnum.codeOf("no-configure"));
        assertEquals(ModelStatusEnum.QUOTA_EXCEEDED, ModelStatusEnum.codeOf("quota-exceeded"));
        assertEquals(ModelStatusEnum.NO_PERMISSION, ModelStatusEnum.codeOf("no-permission"));
        assertEquals(ModelStatusEnum.DISABLED, ModelStatusEnum.codeOf("disabled"));
    }

    @Test
    @DisplayName("Test codeOf method with invalid code")
    void testCodeOfWithInvalidCode() {
        assertNull(ModelStatusEnum.codeOf("invalid-code"));
    }

    @Test
    @DisplayName("Test valueOf method with valid names")
    void testValueOfWithValidNames() {
        assertEquals(ModelStatusEnum.ACTIVE, ModelStatusEnum.valueOf("ACTIVE"));
        assertEquals(ModelStatusEnum.NO_CONFIGURE, ModelStatusEnum.valueOf("NO_CONFIGURE"));
        assertEquals(ModelStatusEnum.QUOTA_EXCEEDED, ModelStatusEnum.valueOf("QUOTA_EXCEEDED"));
        assertEquals(ModelStatusEnum.NO_PERMISSION, ModelStatusEnum.valueOf("NO_PERMISSION"));
        assertEquals(ModelStatusEnum.DISABLED, ModelStatusEnum.valueOf("DISABLED"));
    }

    @Test
    @DisplayName("Test valueOf method with invalid name")
    void testValueOfWithInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> ModelStatusEnum.valueOf("INVALID_STATUS"));
    }
} 