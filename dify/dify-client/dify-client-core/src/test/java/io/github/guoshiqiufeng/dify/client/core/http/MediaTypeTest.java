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
package io.github.guoshiqiufeng.dify.client.core.http;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MediaType constants
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/01/06
 */
class MediaTypeTest {

    @Test
    void testApplicationJsonConstant() {
        assertEquals("application/json", MediaType.APPLICATION_JSON);
    }

    @Test
    void testApplicationJsonUtf8Constant() {
        assertEquals("application/json;charset=UTF-8", MediaType.APPLICATION_JSON_UTF8);
    }

    @Test
    void testApplicationXmlConstant() {
        assertEquals("application/xml", MediaType.APPLICATION_XML);
    }

    @Test
    void testApplicationFormUrlencodedConstant() {
        assertEquals("application/x-www-form-urlencoded", MediaType.APPLICATION_FORM_URLENCODED);
    }

    @Test
    void testMultipartFormDataConstant() {
        assertEquals("multipart/form-data", MediaType.MULTIPART_FORM_DATA);
    }

    @Test
    void testTextPlainConstant() {
        assertEquals("text/plain", MediaType.TEXT_PLAIN);
    }

    @Test
    void testTextHtmlConstant() {
        assertEquals("text/html", MediaType.TEXT_HTML);
    }

    @Test
    void testTextEventStreamConstant() {
        assertEquals("text/event-stream", MediaType.TEXT_EVENT_STREAM);
    }

    @Test
    void testApplicationOctetStreamConstant() {
        assertEquals("application/octet-stream", MediaType.APPLICATION_OCTET_STREAM);
    }

    @Test
    void testAllConstantsAreNotNull() {
        assertNotNull(MediaType.APPLICATION_JSON);
        assertNotNull(MediaType.APPLICATION_JSON_UTF8);
        assertNotNull(MediaType.APPLICATION_XML);
        assertNotNull(MediaType.APPLICATION_FORM_URLENCODED);
        assertNotNull(MediaType.MULTIPART_FORM_DATA);
        assertNotNull(MediaType.TEXT_PLAIN);
        assertNotNull(MediaType.TEXT_HTML);
        assertNotNull(MediaType.TEXT_EVENT_STREAM);
        assertNotNull(MediaType.APPLICATION_OCTET_STREAM);
    }

    @Test
    void testAllConstantsAreNotEmpty() {
        assertFalse(MediaType.APPLICATION_JSON.isEmpty());
        assertFalse(MediaType.APPLICATION_JSON_UTF8.isEmpty());
        assertFalse(MediaType.APPLICATION_XML.isEmpty());
        assertFalse(MediaType.APPLICATION_FORM_URLENCODED.isEmpty());
        assertFalse(MediaType.MULTIPART_FORM_DATA.isEmpty());
        assertFalse(MediaType.TEXT_PLAIN.isEmpty());
        assertFalse(MediaType.TEXT_HTML.isEmpty());
        assertFalse(MediaType.TEXT_EVENT_STREAM.isEmpty());
        assertFalse(MediaType.APPLICATION_OCTET_STREAM.isEmpty());
    }
}
