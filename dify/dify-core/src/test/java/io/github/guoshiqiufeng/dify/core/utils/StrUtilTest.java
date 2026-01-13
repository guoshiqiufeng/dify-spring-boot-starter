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
package io.github.guoshiqiufeng.dify.core.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for StrUtil
 *
 * @author yanghq
 * @version 1.0
 * @since 2026/01/13
 */
class StrUtilTest {

    @Test
    void testIsEmptyWithNull() {
        assertTrue(StrUtil.isEmpty(null));
    }

    @Test
    void testIsEmptyWithEmptyString() {
        assertTrue(StrUtil.isEmpty(""));
    }

    @Test
    void testIsEmptyWithWhitespace() {
        // isEmpty only checks for null or zero length, not whitespace
        assertFalse(StrUtil.isEmpty(" "));
        assertFalse(StrUtil.isEmpty("  "));
        assertFalse(StrUtil.isEmpty("\t"));
        assertFalse(StrUtil.isEmpty("\n"));
    }

    @Test
    void testIsEmptyWithNonEmptyString() {
        assertFalse(StrUtil.isEmpty("hello"));
        assertFalse(StrUtil.isEmpty("a"));
        assertFalse(StrUtil.isEmpty("123"));
    }

    @Test
    void testIsEmptyWithSpecialCharacters() {
        assertFalse(StrUtil.isEmpty("!@#$%"));
        assertFalse(StrUtil.isEmpty("中文"));
        assertFalse(StrUtil.isEmpty("🎉"));
    }

    @Test
    void testIsNotEmptyWithNull() {
        assertFalse(StrUtil.isNotEmpty(null));
    }

    @Test
    void testIsNotEmptyWithEmptyString() {
        assertFalse(StrUtil.isNotEmpty(""));
    }

    @Test
    void testIsNotEmptyWithWhitespace() {
        // isNotEmpty returns true for whitespace since it's not empty
        assertTrue(StrUtil.isNotEmpty(" "));
        assertTrue(StrUtil.isNotEmpty("  "));
        assertTrue(StrUtil.isNotEmpty("\t"));
        assertTrue(StrUtil.isNotEmpty("\n"));
    }

    @Test
    void testIsNotEmptyWithNonEmptyString() {
        assertTrue(StrUtil.isNotEmpty("hello"));
        assertTrue(StrUtil.isNotEmpty("a"));
        assertTrue(StrUtil.isNotEmpty("123"));
    }

    @Test
    void testIsNotEmptyWithSpecialCharacters() {
        assertTrue(StrUtil.isNotEmpty("!@#$%"));
        assertTrue(StrUtil.isNotEmpty("中文"));
        assertTrue(StrUtil.isNotEmpty("🎉"));
    }

    @Test
    void testIsEmptyAndIsNotEmptyAreOpposites() {
        String[] testStrings = {null, "", " ", "hello", "123", "\t", "\n"};

        for (String str : testStrings) {
            assertEquals(!StrUtil.isEmpty(str), StrUtil.isNotEmpty(str),
                    "isEmpty and isNotEmpty should be opposites for: " + str);
        }
    }

    @Test
    void testIsEmptyWithLongString() {
        String longString = "a".repeat(10000);
        assertFalse(StrUtil.isEmpty(longString));
        assertTrue(StrUtil.isNotEmpty(longString));
    }

    @Test
    void testIsEmptyWithMixedContent() {
        assertFalse(StrUtil.isEmpty("hello world"));
        assertFalse(StrUtil.isEmpty("  hello  "));
        assertFalse(StrUtil.isEmpty("123abc"));
        assertFalse(StrUtil.isEmpty("true"));
        assertFalse(StrUtil.isEmpty("false"));
    }

    @Test
    void testEdgeCases() {
        // Single character
        assertFalse(StrUtil.isEmpty("a"));
        assertTrue(StrUtil.isNotEmpty("a"));

        // Zero-width space (still has length)
        assertFalse(StrUtil.isEmpty("\u200B"));
        assertTrue(StrUtil.isNotEmpty("\u200B"));

        // Line separator
        assertFalse(StrUtil.isEmpty(System.lineSeparator()));
        assertTrue(StrUtil.isNotEmpty(System.lineSeparator()));
    }
}
