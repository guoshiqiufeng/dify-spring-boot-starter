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
package io.github.guoshiqiufeng.dify.client.core.http.util;

import io.github.guoshiqiufeng.dify.core.utils.MultipartBodyBuilder;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MultipartBodyProcessor
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026-01-20
 */
class MultipartBodyProcessorTest {

    // Tests for isMultipartRequest
    @Test
    void testIsMultipartRequestWithValidContentType() {
        assertTrue(MultipartBodyProcessor.isMultipartRequest("multipart/form-data"));
        assertTrue(MultipartBodyProcessor.isMultipartRequest("multipart/form-data; boundary=----WebKitFormBoundary"));
    }

    @Test
    void testIsMultipartRequestWithMixedCase() {
        assertTrue(MultipartBodyProcessor.isMultipartRequest("Multipart/Form-Data"));
        assertTrue(MultipartBodyProcessor.isMultipartRequest("MULTIPART/FORM-DATA"));
        assertTrue(MultipartBodyProcessor.isMultipartRequest("MuLtIpArT/fOrM-dAtA"));
    }

    @Test
    void testIsMultipartRequestWithNonMultipartContentType() {
        assertFalse(MultipartBodyProcessor.isMultipartRequest("application/json"));
        assertFalse(MultipartBodyProcessor.isMultipartRequest("text/plain"));
        assertFalse(MultipartBodyProcessor.isMultipartRequest("application/x-www-form-urlencoded"));
    }

    @Test
    void testIsMultipartRequestWithNull() {
        assertFalse(MultipartBodyProcessor.isMultipartRequest(null));
    }

    @Test
    void testIsMultipartRequestWithEmptyString() {
        assertFalse(MultipartBodyProcessor.isMultipartRequest(""));
    }

    @Test
    void testIsMultipartRequestWithPartialMatch() {
        assertFalse(MultipartBodyProcessor.isMultipartRequest("multipart"));
        assertFalse(MultipartBodyProcessor.isMultipartRequest("form-data"));
    }

    // Tests for extractFilename
    @Test
    void testExtractFilenameWithValidHeader() {
        String header = "form-data; name=\"file\"; filename=\"test.txt\"";
        assertEquals("test.txt", MultipartBodyProcessor.extractFilename(header));
    }

    @Test
    void testExtractFilenameWithComplexFilename() {
        String header = "form-data; name=\"upload\"; filename=\"my-document.pdf\"";
        assertEquals("my-document.pdf", MultipartBodyProcessor.extractFilename(header));
    }

    @Test
    void testExtractFilenameWithSpacesInFilename() {
        String header = "form-data; name=\"file\"; filename=\"my file.txt\"";
        assertEquals("my file.txt", MultipartBodyProcessor.extractFilename(header));
    }

    @Test
    void testExtractFilenameWithoutFilename() {
        String header = "form-data; name=\"field\"";
        assertEquals("file", MultipartBodyProcessor.extractFilename(header));
    }

    @Test
    void testExtractFilenameWithNull() {
        assertEquals("file", MultipartBodyProcessor.extractFilename(null));
    }

    @Test
    void testExtractFilenameWithEmptyString() {
        assertEquals("file", MultipartBodyProcessor.extractFilename(""));
    }

    @Test
    void testExtractFilenameWithMalformedHeader() {
        String header = "form-data; name=\"file\"; filename=";
        assertEquals("file", MultipartBodyProcessor.extractFilename(header));
    }

    @Test
    void testExtractFilenameWithUnclosedQuote() {
        String header = "form-data; name=\"file\"; filename=\"test.txt";
        assertEquals("file", MultipartBodyProcessor.extractFilename(header));
    }

    @Test
    void testExtractFilenameWithEmptyFilename() {
        String header = "form-data; name=\"file\"; filename=\"\"";
        assertEquals("", MultipartBodyProcessor.extractFilename(header));
    }

    @Test
    void testExtractFilenameWithPathInFilename() {
        String header = "form-data; name=\"file\"; filename=\"/path/to/file.txt\"";
        assertEquals("/path/to/file.txt", MultipartBodyProcessor.extractFilename(header));
    }

    @Test
    void testExtractFilenameWithSpecialCharacters() {
        String header = "form-data; name=\"file\"; filename=\"file@#$.txt\"";
        assertEquals("file@#$.txt", MultipartBodyProcessor.extractFilename(header));
    }

    @Test
    void testExtractFilenameWithUnicodeCharacters() {
        String header = "form-data; name=\"file\"; filename=\"文件.txt\"";
        assertEquals("文件.txt", MultipartBodyProcessor.extractFilename(header));
    }

    // Tests for isMultipartBodyMap
    @Test
    void testIsMultipartBodyMapWithNull() {
        assertFalse(MultipartBodyProcessor.isMultipartBodyMap(null));
    }

    @Test
    void testIsMultipartBodyMapWithEmptyMap() {
        Map<String, Object> map = new HashMap<>();
        assertFalse(MultipartBodyProcessor.isMultipartBodyMap(map));
    }

    @Test
    void testIsMultipartBodyMapWithNonMultipartValues() {
        Map<String, Object> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", 123);
        assertFalse(MultipartBodyProcessor.isMultipartBodyMap(map));
    }

    @Test
    void testIsMultipartBodyMapWithNullValue() {
        Map<String, Object> map = new HashMap<>();
        map.put("key", null);
        assertFalse(MultipartBodyProcessor.isMultipartBodyMap(map));
    }

    @Test
    void testIsMultipartBodyMapWithMultipartPart() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", "test content");
        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        assertTrue(MultipartBodyProcessor.isMultipartBodyMap(parts));
    }

    // Tests for determinePartType
    @Test
    void testDeterminePartTypeWithNull() {
        assertEquals(MultipartBodyProcessor.PartType.OBJECT, MultipartBodyProcessor.determinePartType(null));
    }

    @Test
    void testDeterminePartTypeWithByteArray() {
        byte[] data = new byte[]{1, 2, 3};
        assertEquals(MultipartBodyProcessor.PartType.FILE, MultipartBodyProcessor.determinePartType(data));
    }

    @Test
    void testDeterminePartTypeWithEmptyByteArray() {
        byte[] data = new byte[0];
        assertEquals(MultipartBodyProcessor.PartType.FILE, MultipartBodyProcessor.determinePartType(data));
    }

    @Test
    void testDeterminePartTypeWithString() {
        assertEquals(MultipartBodyProcessor.PartType.STRING, MultipartBodyProcessor.determinePartType("test"));
        assertEquals(MultipartBodyProcessor.PartType.STRING, MultipartBodyProcessor.determinePartType(""));
    }

    @Test
    void testDeterminePartTypeWithNumber() {
        assertEquals(MultipartBodyProcessor.PartType.NUMBER, MultipartBodyProcessor.determinePartType(123));
        assertEquals(MultipartBodyProcessor.PartType.NUMBER, MultipartBodyProcessor.determinePartType(123L));
        assertEquals(MultipartBodyProcessor.PartType.NUMBER, MultipartBodyProcessor.determinePartType(123.45));
        assertEquals(MultipartBodyProcessor.PartType.NUMBER, MultipartBodyProcessor.determinePartType(123.45f));
    }

    @Test
    void testDeterminePartTypeWithBoolean() {
        assertEquals(MultipartBodyProcessor.PartType.BOOLEAN, MultipartBodyProcessor.determinePartType(true));
        assertEquals(MultipartBodyProcessor.PartType.BOOLEAN, MultipartBodyProcessor.determinePartType(false));
    }

    @Test
    void testDeterminePartTypeWithObject() {
        assertEquals(MultipartBodyProcessor.PartType.OBJECT, MultipartBodyProcessor.determinePartType(new Object()));
        assertEquals(MultipartBodyProcessor.PartType.OBJECT, MultipartBodyProcessor.determinePartType(new HashMap<>()));
        assertEquals(MultipartBodyProcessor.PartType.OBJECT, MultipartBodyProcessor.determinePartType(new int[]{1, 2, 3}));
    }

    @Test
    void testDeterminePartTypeWithCustomObject() {
        class CustomObject {
            String field;
        }
        assertEquals(MultipartBodyProcessor.PartType.OBJECT, MultipartBodyProcessor.determinePartType(new CustomObject()));
    }

    // Tests for shouldSerializeAsJson
    @Test
    void testShouldSerializeAsJsonWithNull() {
        assertTrue(MultipartBodyProcessor.shouldSerializeAsJson(null));
    }

    @Test
    void testShouldSerializeAsJsonWithByteArray() {
        assertFalse(MultipartBodyProcessor.shouldSerializeAsJson(new byte[]{1, 2, 3}));
    }

    @Test
    void testShouldSerializeAsJsonWithString() {
        assertFalse(MultipartBodyProcessor.shouldSerializeAsJson("test"));
    }

    @Test
    void testShouldSerializeAsJsonWithNumber() {
        assertFalse(MultipartBodyProcessor.shouldSerializeAsJson(123));
        assertFalse(MultipartBodyProcessor.shouldSerializeAsJson(123.45));
    }

    @Test
    void testShouldSerializeAsJsonWithBoolean() {
        assertFalse(MultipartBodyProcessor.shouldSerializeAsJson(true));
        assertFalse(MultipartBodyProcessor.shouldSerializeAsJson(false));
    }

    @Test
    void testShouldSerializeAsJsonWithObject() {
        assertTrue(MultipartBodyProcessor.shouldSerializeAsJson(new Object()));
        assertTrue(MultipartBodyProcessor.shouldSerializeAsJson(new HashMap<>()));
    }

    // Tests for PartType enum
    @Test
    void testPartTypeEnumValues() {
        assertEquals(5, MultipartBodyProcessor.PartType.values().length);
        assertNotNull(MultipartBodyProcessor.PartType.valueOf("FILE"));
        assertNotNull(MultipartBodyProcessor.PartType.valueOf("STRING"));
        assertNotNull(MultipartBodyProcessor.PartType.valueOf("NUMBER"));
        assertNotNull(MultipartBodyProcessor.PartType.valueOf("BOOLEAN"));
        assertNotNull(MultipartBodyProcessor.PartType.valueOf("OBJECT"));
    }

    @Test
    void testPartTypeEnumOrdering() {
        MultipartBodyProcessor.PartType[] values = MultipartBodyProcessor.PartType.values();
        assertEquals(MultipartBodyProcessor.PartType.FILE, values[0]);
        assertEquals(MultipartBodyProcessor.PartType.STRING, values[1]);
        assertEquals(MultipartBodyProcessor.PartType.NUMBER, values[2]);
        assertEquals(MultipartBodyProcessor.PartType.BOOLEAN, values[3]);
        assertEquals(MultipartBodyProcessor.PartType.OBJECT, values[4]);
    }

    // Additional edge case tests
    @Test
    void testIsMultipartRequestWithWhitespace() {
        assertTrue(MultipartBodyProcessor.isMultipartRequest("  multipart/form-data  "));
        assertTrue(MultipartBodyProcessor.isMultipartRequest("\tmultipart/form-data\n"));
    }

    @Test
    void testExtractFilenameWithMultipleFilenameAttributes() {
        String header = "form-data; name=\"file\"; filename=\"first.txt\"; filename=\"second.txt\"";
        assertEquals("first.txt", MultipartBodyProcessor.extractFilename(header));
    }

    @Test
    void testDeterminePartTypeWithZeroNumber() {
        assertEquals(MultipartBodyProcessor.PartType.NUMBER, MultipartBodyProcessor.determinePartType(0));
        assertEquals(MultipartBodyProcessor.PartType.NUMBER, MultipartBodyProcessor.determinePartType(0.0));
    }

    @Test
    void testDeterminePartTypeWithNegativeNumber() {
        assertEquals(MultipartBodyProcessor.PartType.NUMBER, MultipartBodyProcessor.determinePartType(-123));
        assertEquals(MultipartBodyProcessor.PartType.NUMBER, MultipartBodyProcessor.determinePartType(-123.45));
    }

    @Test
    void testIsMultipartRequestWithBoundaryParameter() {
        assertTrue(MultipartBodyProcessor.isMultipartRequest("multipart/form-data; boundary=----WebKitFormBoundaryABC123"));
        assertTrue(MultipartBodyProcessor.isMultipartRequest("multipart/form-data;boundary=simple"));
    }

    @Test
    void testExtractFilenameWithSingleQuoteInFilename() {
        String header = "form-data; name=\"file\"; filename=\"test's file.txt\"";
        assertEquals("test's file.txt", MultipartBodyProcessor.extractFilename(header));
    }

    @Test
    void testDeterminePartTypeWithBoxedPrimitives() {
        assertEquals(MultipartBodyProcessor.PartType.NUMBER, MultipartBodyProcessor.determinePartType(Integer.valueOf(123)));
        assertEquals(MultipartBodyProcessor.PartType.NUMBER, MultipartBodyProcessor.determinePartType(Long.valueOf(123L)));
        assertEquals(MultipartBodyProcessor.PartType.NUMBER, MultipartBodyProcessor.determinePartType(Double.valueOf(123.45)));
        assertEquals(MultipartBodyProcessor.PartType.NUMBER, MultipartBodyProcessor.determinePartType(Float.valueOf(123.45f)));
        assertEquals(MultipartBodyProcessor.PartType.BOOLEAN, MultipartBodyProcessor.determinePartType(Boolean.TRUE));
        assertEquals(MultipartBodyProcessor.PartType.BOOLEAN, MultipartBodyProcessor.determinePartType(Boolean.FALSE));
    }
}
