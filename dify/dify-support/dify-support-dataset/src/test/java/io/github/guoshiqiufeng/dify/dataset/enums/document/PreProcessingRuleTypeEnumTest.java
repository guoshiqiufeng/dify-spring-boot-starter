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
package io.github.guoshiqiufeng.dify.dataset.enums.document;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link PreProcessingRuleTypeEnum}.
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/3/13 15:20
 */
class PreProcessingRuleTypeEnumTest {

    @Test
    @DisplayName("Test enum count")
    void testEnumCount() {
        assertEquals(2, PreProcessingRuleTypeEnum.values().length);
    }

    @Test
    @DisplayName("Test specific enum values")
    void testSpecificEnumValues() {
        assertNotNull(PreProcessingRuleTypeEnum.remove_extra_spaces);
        assertNotNull(PreProcessingRuleTypeEnum.remove_urls_emails);
    }

    @Test
    @DisplayName("Test enum names")
    void testEnumNames() {
        assertEquals("remove_extra_spaces", PreProcessingRuleTypeEnum.remove_extra_spaces.name());
        assertEquals("remove_urls_emails", PreProcessingRuleTypeEnum.remove_urls_emails.name());
    }

    @Test
    @DisplayName("Test enum ordinals")
    void testEnumOrdinals() {
        assertEquals(0, PreProcessingRuleTypeEnum.remove_extra_spaces.ordinal());
        assertEquals(1, PreProcessingRuleTypeEnum.remove_urls_emails.ordinal());
    }

    @Test
    @DisplayName("Test valueOf method with valid names")
    void testValueOfWithValidNames() {
        assertEquals(PreProcessingRuleTypeEnum.remove_extra_spaces, PreProcessingRuleTypeEnum.valueOf("remove_extra_spaces"));
        assertEquals(PreProcessingRuleTypeEnum.remove_urls_emails, PreProcessingRuleTypeEnum.valueOf("remove_urls_emails"));
    }

    @Test
    @DisplayName("Test valueOf method with invalid name")
    void testValueOfWithInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> PreProcessingRuleTypeEnum.valueOf("invalid_rule_type"));
    }
}
