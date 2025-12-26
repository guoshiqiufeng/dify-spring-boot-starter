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

import io.github.guoshiqiufeng.dify.client.core.constant.MediaType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ContentDisposition
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/01/06
 */
class ContentDispositionTest {

    @Test
    void testAttachmentWithFilename() {
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename("test-file.pdf")
                .build();

        assertEquals("attachment", disposition.getType());
        assertEquals("test-file.pdf", disposition.getFilename());
        assertNull(disposition.getName());
        assertEquals("attachment; filename=\"test-file.pdf\"", disposition.toString());
    }

    @Test
    void testInlineWithFilename() {
        ContentDisposition disposition = ContentDisposition.inline()
                .filename("image.png")
                .build();

        assertEquals("inline", disposition.getType());
        assertEquals("image.png", disposition.getFilename());
        assertEquals("inline; filename=\"image.png\"", disposition.toString());
    }

    @Test
    void testFormDataWithNameAndFilename() {
        ContentDisposition disposition = ContentDisposition.formData()
                .name("file")
                .filename("document.pdf")
                .build();

        assertEquals("form-data", disposition.getType());
        assertEquals("file", disposition.getName());
        assertEquals("document.pdf", disposition.getFilename());
        assertEquals("form-data; name=\"file\"; filename=\"document.pdf\"", disposition.toString());
    }

    @Test
    void testFormDataWithNameOnly() {
        ContentDisposition disposition = ContentDisposition.formData()
                .name("data")
                .build();

        assertEquals("form-data", disposition.getType());
        assertEquals("data", disposition.getName());
        assertNull(disposition.getFilename());
        assertEquals("form-data; name=\"data\"", disposition.toString());
    }

    @Test
    void testAttachmentWithoutFilename() {
        ContentDisposition disposition = ContentDisposition.attachment().build();

        assertEquals("attachment", disposition.getType());
        assertNull(disposition.getFilename());
        assertNull(disposition.getName());
        assertEquals("attachment", disposition.toString());
    }

    @Test
    void testFilenameWithQuotes() {
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename("file\"with\"quotes.txt")
                .build();

        assertEquals("attachment; filename=\"file\\\"with\\\"quotes.txt\"", disposition.toString());
    }

    @Test
    void testFilenameWithBackslashes() {
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename("path\\to\\file.txt")
                .build();

        assertEquals("attachment; filename=\"path\\\\to\\\\file.txt\"", disposition.toString());
    }

    @Test
    void testHttpHeadersSetContentType() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);

        assertEquals(MediaType.IMAGE_PNG, headers.getFirst(HttpHeaders.CONTENT_TYPE));
        assertEquals(MediaType.IMAGE_PNG, headers.getContentType());
    }

    @Test
    void testHttpHeadersGetContentTypeWhenNotSet() {
        HttpHeaders headers = new HttpHeaders();
        assertNull(headers.getContentType());
    }

    @Test
    void testHttpHeadersSetContentLength() {
        HttpHeaders headers = new HttpHeaders();
        byte[] expectedContent = new byte[1024];
        headers.setContentLength(expectedContent.length);

        assertEquals("1024", headers.getFirst(HttpHeaders.CONTENT_LENGTH));
        assertEquals("1024", headers.getContentLength());
    }

    @Test
    void testHttpHeadersGetContentLengthWhenNotSet() {
        HttpHeaders headers = new HttpHeaders();
        assertNull(headers.getContentLength());
    }

    @Test
    void testHttpHeadersSetContentDisposition() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(
                ContentDisposition.attachment().filename("test-file.pdf").build()
        );

        assertEquals("attachment; filename=\"test-file.pdf\"",
                headers.getFirst(HttpHeaders.CONTENT_DISPOSITION));
        assertEquals("attachment; filename=\"test-file.pdf\"",
                headers.getContentDisposition());
    }

    @Test
    void testHttpHeadersGetContentDispositionWhenNotSet() {
        HttpHeaders headers = new HttpHeaders();
        assertNull(headers.getContentDisposition());
    }

    @Test
    void testCompleteExample1() {
        // Example from user request
        HttpHeaders headers = new HttpHeaders();
        byte[] expectedContent = new byte[1024];
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(expectedContent.length);

        assertEquals(MediaType.IMAGE_PNG, headers.getFirst(HttpHeaders.CONTENT_TYPE));
        assertEquals("1024", headers.getFirst(HttpHeaders.CONTENT_LENGTH));
    }

    @Test
    void testCompleteExample2() {
        // Example from user request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(
                ContentDisposition.attachment().filename("test-file.pdf").build()
        );

        assertEquals(MediaType.APPLICATION_OCTET_STREAM, headers.getFirst(HttpHeaders.CONTENT_TYPE));
        assertEquals("attachment; filename=\"test-file.pdf\"",
                headers.getFirst(HttpHeaders.CONTENT_DISPOSITION));
    }
}
