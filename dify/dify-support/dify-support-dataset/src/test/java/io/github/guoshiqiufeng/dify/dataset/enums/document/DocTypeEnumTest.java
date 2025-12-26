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
 * Tests for {@link DocTypeEnum}.
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/3/13 14:19
 */
class DocTypeEnumTest {

    @Test
    @DisplayName("Test enum count")
    void testEnumCount() {
        assertEquals(11, DocTypeEnum.values().length);
    }

    @Test
    @DisplayName("Test specific enum values")
    void testSpecificEnumValues() {
        assertNotNull(DocTypeEnum.book);
        assertNotNull(DocTypeEnum.web_page);
        assertNotNull(DocTypeEnum.paper);
        assertNotNull(DocTypeEnum.social_media_post);
        assertNotNull(DocTypeEnum.wikipedia_entry);
        assertNotNull(DocTypeEnum.personal_document);
        assertNotNull(DocTypeEnum.business_document);
        assertNotNull(DocTypeEnum.im_chat_log);
        assertNotNull(DocTypeEnum.synced_from_notion);
        assertNotNull(DocTypeEnum.synced_from_github);
        assertNotNull(DocTypeEnum.others);
    }

    @Test
    @DisplayName("Test enum names")
    void testEnumNames() {
        assertEquals("book", DocTypeEnum.book.name());
        assertEquals("web_page", DocTypeEnum.web_page.name());
        assertEquals("paper", DocTypeEnum.paper.name());
        assertEquals("social_media_post", DocTypeEnum.social_media_post.name());
        assertEquals("wikipedia_entry", DocTypeEnum.wikipedia_entry.name());
        assertEquals("personal_document", DocTypeEnum.personal_document.name());
        assertEquals("business_document", DocTypeEnum.business_document.name());
        assertEquals("im_chat_log", DocTypeEnum.im_chat_log.name());
        assertEquals("synced_from_notion", DocTypeEnum.synced_from_notion.name());
        assertEquals("synced_from_github", DocTypeEnum.synced_from_github.name());
        assertEquals("others", DocTypeEnum.others.name());
    }

    @Test
    @DisplayName("Test enum ordinals")
    void testEnumOrdinals() {
        assertEquals(0, DocTypeEnum.book.ordinal());
        assertEquals(1, DocTypeEnum.web_page.ordinal());
        assertEquals(2, DocTypeEnum.paper.ordinal());
        assertEquals(3, DocTypeEnum.social_media_post.ordinal());
        assertEquals(4, DocTypeEnum.wikipedia_entry.ordinal());
        assertEquals(5, DocTypeEnum.personal_document.ordinal());
        assertEquals(6, DocTypeEnum.business_document.ordinal());
        assertEquals(7, DocTypeEnum.im_chat_log.ordinal());
        assertEquals(8, DocTypeEnum.synced_from_notion.ordinal());
        assertEquals(9, DocTypeEnum.synced_from_github.ordinal());
        assertEquals(10, DocTypeEnum.others.ordinal());
    }

    @Test
    @DisplayName("Test valueOf method with valid names")
    void testValueOfWithValidNames() {
        assertEquals(DocTypeEnum.book, DocTypeEnum.valueOf("book"));
        assertEquals(DocTypeEnum.web_page, DocTypeEnum.valueOf("web_page"));
        assertEquals(DocTypeEnum.paper, DocTypeEnum.valueOf("paper"));
        assertEquals(DocTypeEnum.social_media_post, DocTypeEnum.valueOf("social_media_post"));
        assertEquals(DocTypeEnum.wikipedia_entry, DocTypeEnum.valueOf("wikipedia_entry"));
        assertEquals(DocTypeEnum.personal_document, DocTypeEnum.valueOf("personal_document"));
        assertEquals(DocTypeEnum.business_document, DocTypeEnum.valueOf("business_document"));
        assertEquals(DocTypeEnum.im_chat_log, DocTypeEnum.valueOf("im_chat_log"));
        assertEquals(DocTypeEnum.synced_from_notion, DocTypeEnum.valueOf("synced_from_notion"));
        assertEquals(DocTypeEnum.synced_from_github, DocTypeEnum.valueOf("synced_from_github"));
        assertEquals(DocTypeEnum.others, DocTypeEnum.valueOf("others"));
    }

    @Test
    @DisplayName("Test valueOf method with invalid name")
    void testValueOfWithInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> DocTypeEnum.valueOf("invalid_doc_type"));
    }
}
