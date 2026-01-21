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
package io.github.guoshiqiufeng.dify.client.integration.spring.http.util;

import io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper;
import io.github.guoshiqiufeng.dify.client.core.http.HttpClientException;
import io.github.guoshiqiufeng.dify.core.utils.MultipartBodyBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.LinkedMultiValueMap;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SpringMultipartBodyBuilder
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026-01-20
 */
@ExtendWith(MockitoExtension.class)
class SpringMultipartBodyBuilderTest {

    @Mock
    private JsonMapper jsonMapper;

    private MultipartBodyBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new MultipartBodyBuilder();
    }

    @Test
    void testBuildMultipartBodyWithStringValue() {
        builder.part("name", "John Doe");
        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        LinkedMultiValueMap<String, Object> result = SpringMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey("name"));
        assertEquals("John Doe", result.getFirst("name"));
    }

    @Test
    void testBuildMultipartBodyWithMultipleStringValues() {
        builder.part("name", "John Doe");
        builder.part("email", "john@example.com");
        builder.part("city", "New York");
        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        LinkedMultiValueMap<String, Object> result = SpringMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("John Doe", result.getFirst("name"));
        assertEquals("john@example.com", result.getFirst("email"));
        assertEquals("New York", result.getFirst("city"));
    }

    @Test
    void testBuildMultipartBodyWithNumberValue() {
        builder.part("age", 30);
        builder.part("price", 99.99);
        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        LinkedMultiValueMap<String, Object> result = SpringMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(30, result.getFirst("age"));
        assertEquals(99.99, result.getFirst("price"));
    }

    @Test
    void testBuildMultipartBodyWithBooleanValue() {
        builder.part("active", true);
        builder.part("verified", false);
        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        LinkedMultiValueMap<String, Object> result = SpringMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(true, result.getFirst("active"));
        assertEquals(false, result.getFirst("verified"));
    }

    @Test
    void testBuildMultipartBodyWithByteArrayValue() {
        byte[] fileData = "test file content".getBytes();
        builder.part("file", fileData)
                .header("Content-Disposition", "form-data; name=\"file\"; filename=\"test.txt\"");
        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        LinkedMultiValueMap<String, Object> result = SpringMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);

        assertNotNull(result);
        assertEquals(1, result.size());
        Object resource = result.getFirst("file");
        assertTrue(resource instanceof ByteArrayResource);
        assertEquals("test.txt", ((ByteArrayResource) resource).getFilename());
    }

    @Test
    void testBuildMultipartBodyWithByteArrayWithoutFilename() {
        byte[] fileData = "test content".getBytes();
        builder.part("file", fileData);
        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        LinkedMultiValueMap<String, Object> result = SpringMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);

        assertNotNull(result);
        assertEquals(1, result.size());
        Object resource = result.getFirst("file");
        assertTrue(resource instanceof ByteArrayResource);
        assertEquals("file", ((ByteArrayResource) resource).getFilename());
    }

    @Test
    void testBuildMultipartBodyWithComplexObject() {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", "John");
        userData.put("age", 30);

        when(jsonMapper.toJson(any())).thenReturn("{\"name\":\"John\",\"age\":30}");

        builder.part("user", userData);
        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        LinkedMultiValueMap<String, Object> result = SpringMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("{\"name\":\"John\",\"age\":30}", result.getFirst("user"));
        verify(jsonMapper).toJson(userData);
    }

    @Test
    void testBuildMultipartBodyWithComplexObjectSkipNull() {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", "John");
        userData.put("age", null);

        when(jsonMapper.toJsonIgnoreNull(any())).thenReturn("{\"name\":\"John\"}");

        builder.part("user", userData);
        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        LinkedMultiValueMap<String, Object> result = SpringMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, true);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("{\"name\":\"John\"}", result.getFirst("user"));
        verify(jsonMapper).toJsonIgnoreNull(userData);
        verify(jsonMapper, never()).toJson(any());
    }

    @Test
    void testBuildMultipartBodyWithMixedTypes() {
        byte[] fileData = "file content".getBytes();

        when(jsonMapper.toJson(any())).thenReturn("{\"key\":\"value\"}");

        builder.part("name", "John");
        builder.part("age", 30);
        builder.part("active", true);
        builder.part("file", fileData)
                .header("Content-Disposition", "form-data; name=\"file\"; filename=\"test.txt\"");
        builder.part("metadata", new HashMap<String, String>());

        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        LinkedMultiValueMap<String, Object> result = SpringMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);

        assertNotNull(result);
        assertEquals(5, result.size());
        assertEquals("John", result.getFirst("name"));
        assertEquals(30, result.getFirst("age"));
        assertEquals(true, result.getFirst("active"));
        assertTrue(result.getFirst("file") instanceof ByteArrayResource);
        assertEquals("{\"key\":\"value\"}", result.getFirst("metadata"));
    }

    @Test
    void testBuildMultipartBodyWithJsonSerializationError() {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", "John");

        when(jsonMapper.toJson(any())).thenThrow(new RuntimeException("Serialization failed"));

        builder.part("user", userData);
        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        HttpClientException exception = assertThrows(HttpClientException.class, () -> {
            SpringMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);
        });

        assertTrue(exception.getMessage().contains("Failed to serialize multipart field to JSON"));
        assertTrue(exception.getMessage().contains("user"));
    }

    @Test
    void testBuildMultipartBodyWithEmptyParts() {
        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        LinkedMultiValueMap<String, Object> result = SpringMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testBuildMultipartBodyWithEmptyByteArray() {
        byte[] emptyData = new byte[0];
        builder.part("file", emptyData)
                .header("Content-Disposition", "form-data; name=\"file\"; filename=\"empty.txt\"");
        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        LinkedMultiValueMap<String, Object> result = SpringMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);

        assertNotNull(result);
        assertEquals(1, result.size());
        Object resource = result.getFirst("file");
        assertTrue(resource instanceof ByteArrayResource);
        assertEquals("empty.txt", ((ByteArrayResource) resource).getFilename());
    }

    @Test
    void testBuildMultipartBodyWithEmptyString() {
        builder.part("field", "");
        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        LinkedMultiValueMap<String, Object> result = SpringMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("", result.getFirst("field"));
    }

    @Test
    void testBuildMultipartBodyWithZeroNumber() {
        builder.part("count", 0);
        builder.part("price", 0.0);
        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        LinkedMultiValueMap<String, Object> result = SpringMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(0, result.getFirst("count"));
        assertEquals(0.0, result.getFirst("price"));
    }

    @Test
    void testBuildMultipartBodyWithNegativeNumber() {
        builder.part("temperature", -10);
        builder.part("balance", -99.99);
        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        LinkedMultiValueMap<String, Object> result = SpringMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(-10, result.getFirst("temperature"));
        assertEquals(-99.99, result.getFirst("balance"));
    }

    @Test
    void testBuildMultipartBodyWithLongNumber() {
        builder.part("id", 9223372036854775807L);
        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        LinkedMultiValueMap<String, Object> result = SpringMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(9223372036854775807L, result.getFirst("id"));
    }

    @Test
    void testBuildMultipartBodyWithSpecialCharactersInString() {
        builder.part("text", "Hello \"World\" & <tag>");
        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        LinkedMultiValueMap<String, Object> result = SpringMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Hello \"World\" & <tag>", result.getFirst("text"));
    }

    @Test
    void testBuildMultipartBodyWithUnicodeString() {
        builder.part("name", "用户名");
        builder.part("message", "こんにちは");
        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        LinkedMultiValueMap<String, Object> result = SpringMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("用户名", result.getFirst("name"));
        assertEquals("こんにちは", result.getFirst("message"));
    }

    @Test
    void testBuildMultipartBodyWithMultipleFiles() {
        byte[] file1 = "content1".getBytes();
        byte[] file2 = "content2".getBytes();

        builder.part("file1", file1)
                .header("Content-Disposition", "form-data; name=\"file1\"; filename=\"file1.txt\"");
        builder.part("file2", file2)
                .header("Content-Disposition", "form-data; name=\"file2\"; filename=\"file2.txt\"");

        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        LinkedMultiValueMap<String, Object> result = SpringMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.getFirst("file1") instanceof ByteArrayResource);
        assertTrue(result.getFirst("file2") instanceof ByteArrayResource);
        assertEquals("file1.txt", ((ByteArrayResource) result.getFirst("file1")).getFilename());
        assertEquals("file2.txt", ((ByteArrayResource) result.getFirst("file2")).getFilename());
    }

    @Test
    void testBuildMultipartBodyWithNullSkipNull() {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", "John");

        when(jsonMapper.toJson(any())).thenReturn("{\"name\":\"John\"}");

        builder.part("user", userData);
        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        LinkedMultiValueMap<String, Object> result = SpringMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, null);

        assertNotNull(result);
        verify(jsonMapper).toJson(userData);
    }

    @Test
    void testBuildMultipartBodyWithBoxedPrimitives() {
        builder.part("intValue", Integer.valueOf(123));
        builder.part("longValue", Long.valueOf(456L));
        builder.part("doubleValue", Double.valueOf(78.9));
        builder.part("floatValue", Float.valueOf(12.3f));
        builder.part("boolValue", Boolean.TRUE);

        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        LinkedMultiValueMap<String, Object> result = SpringMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);

        assertNotNull(result);
        assertEquals(5, result.size());
        assertEquals(123, result.getFirst("intValue"));
        assertEquals(456L, result.getFirst("longValue"));
        assertEquals(78.9, result.getFirst("doubleValue"));
        assertEquals(12.3f, result.getFirst("floatValue"));
        assertEquals(Boolean.TRUE, result.getFirst("boolValue"));
    }

    @Test
    void testBuildMultipartBodyWithComplexFilename() {
        byte[] fileData = "content".getBytes();
        builder.part("file", fileData)
                .header("Content-Disposition", "form-data; name=\"file\"; filename=\"my document (1).pdf\"");

        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        LinkedMultiValueMap<String, Object> result = SpringMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);

        assertNotNull(result);
        assertEquals(1, result.size());
        Object resource = result.getFirst("file");
        assertTrue(resource instanceof ByteArrayResource);
        assertEquals("my document (1).pdf", ((ByteArrayResource) resource).getFilename());
    }
}
