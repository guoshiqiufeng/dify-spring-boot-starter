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
 * Tests for {@link PermissionEnum}.
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/3/13 13:31
 */
class PermissionEnumTest {

    @Test
    @DisplayName("Test enum count")
    void testEnumCount() {
        assertEquals(3, PermissionEnum.values().length);
    }

    @ParameterizedTest
    @EnumSource(PermissionEnum.class)
    @DisplayName("Test each enum value has non-null code")
    void testEnumValuesNotNull(PermissionEnum permissionEnum) {
        assertNotNull(permissionEnum.getCode());
        assertFalse(permissionEnum.getCode().isEmpty());
    }

    @Test
    @DisplayName("Test specific enum values and their codes")
    void testSpecificEnumValues() {
        assertEquals("only_me", PermissionEnum.ONLY_ME.getCode());
        assertEquals("all_team_members", PermissionEnum.ALL_TEAM_MEMBERS.getCode());
        assertEquals("partial_members", PermissionEnum.PARTIAL_MEMBERS.getCode());
    }

    @Test
    @DisplayName("Test codeOf method with valid codes")
    void testCodeOfWithValidCodes() {
        assertEquals(PermissionEnum.ONLY_ME, PermissionEnum.codeOf("only_me"));
        assertEquals(PermissionEnum.ALL_TEAM_MEMBERS, PermissionEnum.codeOf("all_team_members"));
        assertEquals(PermissionEnum.PARTIAL_MEMBERS, PermissionEnum.codeOf("partial_members"));
    }

    @Test
    @DisplayName("Test codeOf method with invalid code")
    void testCodeOfWithInvalidCode() {
        assertNull(PermissionEnum.codeOf("invalid_permission"));
    }

    @Test
    @DisplayName("Test valueOf method with valid names")
    void testValueOfWithValidNames() {
        assertEquals(PermissionEnum.ONLY_ME, PermissionEnum.valueOf("ONLY_ME"));
        assertEquals(PermissionEnum.ALL_TEAM_MEMBERS, PermissionEnum.valueOf("ALL_TEAM_MEMBERS"));
        assertEquals(PermissionEnum.PARTIAL_MEMBERS, PermissionEnum.valueOf("PARTIAL_MEMBERS"));
    }

    @Test
    @DisplayName("Test valueOf method with invalid name")
    void testValueOfWithInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> PermissionEnum.valueOf("INVALID_PERMISSION"));
    }
}
