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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link ProviderEnum}.
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/3/13 13:36
 */
class ProviderEnumTest {

    @Test
    @DisplayName("Test enum count")
    void testEnumCount() {
        assertEquals(2, ProviderEnum.values().length);
    }

    @Test
    @DisplayName("Test specific enum values")
    void testSpecificEnumValues() {
        assertNotNull(ProviderEnum.vendor);
        assertNotNull(ProviderEnum.external);
    }

    @Test
    @DisplayName("Test enum names")
    void testEnumNames() {
        assertEquals("vendor", ProviderEnum.vendor.name());
        assertEquals("external", ProviderEnum.external.name());
    }

    @Test
    @DisplayName("Test enum ordinals")
    void testEnumOrdinals() {
        assertEquals(0, ProviderEnum.vendor.ordinal());
        assertEquals(1, ProviderEnum.external.ordinal());
    }

    @Test
    @DisplayName("Test valueOf method with valid names")
    void testValueOfWithValidNames() {
        assertEquals(ProviderEnum.vendor, ProviderEnum.valueOf("vendor"));
        assertEquals(ProviderEnum.external, ProviderEnum.valueOf("external"));
    }

    @Test
    @DisplayName("Test valueOf method with invalid name")
    void testValueOfWithInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> ProviderEnum.valueOf("invalid_provider"));
    }
}
