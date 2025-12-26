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
 * Tests for {@link DocFormEnum}.
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/3/13 14:27
 */
class DocFormEnumTest {

    @Test
    @DisplayName("Test enum count")
    void testEnumCount() {
        assertEquals(3, DocFormEnum.values().length);
    }

    @Test
    @DisplayName("Test specific enum values")
    void testSpecificEnumValues() {
        assertNotNull(DocFormEnum.text_model);
        assertNotNull(DocFormEnum.hierarchical_model);
        assertNotNull(DocFormEnum.qa_model);
    }

    @Test
    @DisplayName("Test enum names")
    void testEnumNames() {
        assertEquals("text_model", DocFormEnum.text_model.name());
        assertEquals("hierarchical_model", DocFormEnum.hierarchical_model.name());
        assertEquals("qa_model", DocFormEnum.qa_model.name());
    }

    @Test
    @DisplayName("Test enum ordinals")
    void testEnumOrdinals() {
        assertEquals(0, DocFormEnum.text_model.ordinal());
        assertEquals(1, DocFormEnum.hierarchical_model.ordinal());
        assertEquals(2, DocFormEnum.qa_model.ordinal());
    }

    @Test
    @DisplayName("Test valueOf method with valid names")
    void testValueOfWithValidNames() {
        assertEquals(DocFormEnum.text_model, DocFormEnum.valueOf("text_model"));
        assertEquals(DocFormEnum.hierarchical_model, DocFormEnum.valueOf("hierarchical_model"));
        assertEquals(DocFormEnum.qa_model, DocFormEnum.valueOf("qa_model"));
    }

    @Test
    @DisplayName("Test valueOf method with invalid name")
    void testValueOfWithInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> DocFormEnum.valueOf("invalid_form"));
    }
}
