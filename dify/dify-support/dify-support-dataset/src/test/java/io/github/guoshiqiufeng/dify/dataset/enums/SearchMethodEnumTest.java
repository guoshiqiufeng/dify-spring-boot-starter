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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link SearchMethodEnum}.
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/3/13 14:58
 */
class SearchMethodEnumTest {

    @Test
    @DisplayName("Test enum count")
    void testEnumCount() {
        assertEquals(3, SearchMethodEnum.values().length);
    }

    @Test
    @DisplayName("Test specific enum values")
    void testSpecificEnumValues() {
        assertNotNull(SearchMethodEnum.hybrid_search);
        assertNotNull(SearchMethodEnum.semantic_search);
        assertNotNull(SearchMethodEnum.full_text_search);
    }

    @Test
    @DisplayName("Test enum names")
    void testEnumNames() {
        assertEquals("hybrid_search", SearchMethodEnum.hybrid_search.name());
        assertEquals("semantic_search", SearchMethodEnum.semantic_search.name());
        assertEquals("full_text_search", SearchMethodEnum.full_text_search.name());
    }

    @Test
    @DisplayName("Test enum ordinals")
    void testEnumOrdinals() {
        assertEquals(0, SearchMethodEnum.hybrid_search.ordinal());
        assertEquals(1, SearchMethodEnum.semantic_search.ordinal());
        assertEquals(2, SearchMethodEnum.full_text_search.ordinal());
    }

    @Test
    @DisplayName("Test valueOf method with valid names")
    void testValueOfWithValidNames() {
        assertEquals(SearchMethodEnum.hybrid_search, SearchMethodEnum.valueOf("hybrid_search"));
        assertEquals(SearchMethodEnum.semantic_search, SearchMethodEnum.valueOf("semantic_search"));
        assertEquals(SearchMethodEnum.full_text_search, SearchMethodEnum.valueOf("full_text_search"));
    }

    @Test
    @DisplayName("Test valueOf method with invalid name")
    void testValueOfWithInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> SearchMethodEnum.valueOf("invalid_search_method"));
    }
}
