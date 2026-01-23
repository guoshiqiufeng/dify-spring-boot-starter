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
package io.github.guoshiqiufeng.dify.client.integration.okhttp.http.util;

import io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper;
import io.github.guoshiqiufeng.dify.client.core.http.HttpClientException;
import io.github.guoshiqiufeng.dify.core.utils.MultipartBodyBuilder;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OkHttpMultipartBodyBuilder
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026-01-20
 */
@ExtendWith(MockitoExtension.class)
class OkHttpMultipartBodyBuilderTest {

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

        RequestBody body = OkHttpMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);

        assertNotNull(body);
        assertTrue(body instanceof MultipartBody);
        MultipartBody multipartBody = (MultipartBody) body;
        assertEquals(1, multipartBody.size());
    }

    @Test
    void testBuildMultipartBodyWithMultipleStringValues() {
        builder.part("name", "John Doe");
        builder.part("email", "john@example.com");
        builder.part("age", "30");
        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        RequestBody body = OkHttpMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);

        assertNotNull(body);
        assertTrue(body instanceof MultipartBody);
        MultipartBody multipartBody = (MultipartBody) body;
        assertEquals(3, multipartBody.size());
    }

    @Test
    void testBuildMultipartBodyWithNumberValue() {
        builder.part("count", 123);
        builder.part("price", 99.99);
        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        RequestBody body = OkHttpMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);

        assertNotNull(body);
        assertTrue(body instanceof MultipartBody);
        MultipartBody multipartBody = (MultipartBody) body;
        assertEquals(2, multipartBody.size());
    }

    @Test
    void testBuildMultipartBodyWithBooleanValue() {
        builder.part("active", true);
        builder.part("verified", false);
        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        RequestBody body = OkHttpMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);

        assertNotNull(body);
        assertTrue(body instanceof MultipartBody);
        MultipartBody multipartBody = (MultipartBody) body;
        assertEquals(2, multipartBody.size());
    }

    @Test
    void testBuildMultipartBodyWithByteArrayValue() {
        byte[] fileData = "test file content".getBytes();
        builder.part("file", fileData)
                .header("Content-Disposition", "form-data; name=\"file\"; filename=\"test.txt\"")
                .header("Content-Type", "text/plain");
        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        RequestBody body = OkHttpMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);

        assertNotNull(body);
        assertTrue(body instanceof MultipartBody);
        MultipartBody multipartBody = (MultipartBody) body;
        assertEquals(1, multipartBody.size());
    }

    @Test
    void testBuildMultipartBodyWithByteArrayWithoutContentType() {
        byte[] fileData = "test file content".getBytes();
        builder.part("file", fileData)
                .header("Content-Disposition", "form-data; name=\"file\"; filename=\"test.bin\"");
        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        RequestBody body = OkHttpMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);

        assertNotNull(body);
        assertTrue(body instanceof MultipartBody);
        MultipartBody multipartBody = (MultipartBody) body;
        assertEquals(1, multipartBody.size());
    }

    @Test
    void testBuildMultipartBodyWithByteArrayWithoutFilename() {
        byte[] fileData = "test file content".getBytes();
        builder.part("file", fileData);
        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        RequestBody body = OkHttpMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);

        assertNotNull(body);
        assertTrue(body instanceof MultipartBody);
        MultipartBody multipartBody = (MultipartBody) body;
        assertEquals(1, multipartBody.size());
    }

    @Test
    void testBuildMultipartBodyWithComplexObject() {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", "John");
        userData.put("age", 30);

        when(jsonMapper.toJson(any())).thenReturn("{\"name\":\"John\",\"age\":30}");

        builder.part("user", userData);
        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        RequestBody body = OkHttpMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);

        assertNotNull(body);
        assertTrue(body instanceof MultipartBody);
        MultipartBody multipartBody = (MultipartBody) body;
        assertEquals(1, multipartBody.size());
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

        RequestBody body = OkHttpMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, true);

        assertNotNull(body);
        assertTrue(body instanceof MultipartBody);
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

        RequestBody body = OkHttpMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);

        assertNotNull(body);
        assertTrue(body instanceof MultipartBody);
        MultipartBody multipartBody = (MultipartBody) body;
        assertEquals(5, multipartBody.size());
    }

    @Test
    void testBuildMultipartBodyWithJsonSerializationError() {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", "John");

        when(jsonMapper.toJson(any())).thenThrow(new RuntimeException("Serialization failed"));

        builder.part("user", userData);
        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        HttpClientException exception = assertThrows(HttpClientException.class, () -> {
            OkHttpMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);
        });

        assertTrue(exception.getMessage().contains("Failed to serialize multipart field to JSON"));
        assertTrue(exception.getMessage().contains("user"));
    }

    @Test
    void testBuildMultipartBodyWithEmptyParts() {
        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        assertThrows(IllegalStateException.class, () -> {
            OkHttpMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);
        });
    }

    @Test
    void testBuildMultipartBodyWithEmptyByteArray() {
        byte[] emptyData = new byte[0];
        builder.part("file", emptyData)
                .header("Content-Disposition", "form-data; name=\"file\"; filename=\"empty.txt\"");
        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        RequestBody body = OkHttpMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);

        assertNotNull(body);
        assertInstanceOf(MultipartBody.class, body);
        MultipartBody multipartBody = (MultipartBody) body;
        assertEquals(1, multipartBody.size());
    }

    @Test
    void testBuildMultipartBodyWithEmptyString() {
        builder.part("field", "");
        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        RequestBody body = OkHttpMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);

        assertNotNull(body);
        assertTrue(body instanceof MultipartBody);
        MultipartBody multipartBody = (MultipartBody) body;
        assertEquals(1, multipartBody.size());
    }

    @Test
    void testBuildMultipartBodyWithZeroNumber() {
        builder.part("count", 0);
        builder.part("price", 0.0);
        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        RequestBody body = OkHttpMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);

        assertNotNull(body);
        assertTrue(body instanceof MultipartBody);
        MultipartBody multipartBody = (MultipartBody) body;
        assertEquals(2, multipartBody.size());
    }

    @Test
    void testBuildMultipartBodyWithNegativeNumber() {
        builder.part("temperature", -10);
        builder.part("balance", -99.99);
        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        RequestBody body = OkHttpMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);

        assertNotNull(body);
        assertTrue(body instanceof MultipartBody);
        MultipartBody multipartBody = (MultipartBody) body;
        assertEquals(2, multipartBody.size());
    }

    @Test
    void testBuildMultipartBodyWithLongNumber() {
        builder.part("id", 9223372036854775807L);
        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        RequestBody body = OkHttpMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);

        assertNotNull(body);
        assertTrue(body instanceof MultipartBody);
        MultipartBody multipartBody = (MultipartBody) body;
        assertEquals(1, multipartBody.size());
    }

    @Test
    void testBuildMultipartBodyWithSpecialCharactersInString() {
        builder.part("text", "Hello \"World\" & <tag>");
        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        RequestBody body = OkHttpMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);

        assertNotNull(body);
        assertTrue(body instanceof MultipartBody);
        MultipartBody multipartBody = (MultipartBody) body;
        assertEquals(1, multipartBody.size());
    }

    @Test
    void testBuildMultipartBodyWithUnicodeString() {
        builder.part("name", "用户名");
        builder.part("message", "こんにちは");
        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        RequestBody body = OkHttpMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);

        assertNotNull(body);
        assertTrue(body instanceof MultipartBody);
        MultipartBody multipartBody = (MultipartBody) body;
        assertEquals(2, multipartBody.size());
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

        RequestBody body = OkHttpMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);

        assertNotNull(body);
        assertTrue(body instanceof MultipartBody);
        MultipartBody multipartBody = (MultipartBody) body;
        assertEquals(2, multipartBody.size());
    }

    @Test
    void testBuildMultipartBodyWithCustomContentType() {
        byte[] imageData = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
        builder.part("image", imageData)
                .header("Content-Disposition", "form-data; name=\"image\"; filename=\"photo.jpg\"")
                .header("Content-Type", "image/jpeg");

        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        RequestBody body = OkHttpMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);

        assertNotNull(body);
        assertTrue(body instanceof MultipartBody);
        MultipartBody multipartBody = (MultipartBody) body;
        assertEquals(1, multipartBody.size());
    }

    @Test
    void testBuildMultipartBodyVerifyMultipartFormType() {
        builder.part("field", "value");
        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        RequestBody body = OkHttpMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);

        assertNotNull(body);
        assertTrue(body instanceof MultipartBody);
        MultipartBody multipartBody = (MultipartBody) body;
        assertEquals(MultipartBody.FORM, multipartBody.type());
    }

    @Test
    void testBuildMultipartBodyWithNullJsonResult() {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", "John");

        when(jsonMapper.toJson(any())).thenReturn(null);

        builder.part("user", userData);
        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        assertThrows(HttpClientException.class, () -> {
            OkHttpMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);
        });
    }

    @Test
    void testBuildMultipartBodyWithComplexFilename() {
        byte[] fileData = "content".getBytes();
        builder.part("file", fileData)
                .header("Content-Disposition", "form-data; name=\"file\"; filename=\"my document (1).pdf\"");

        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        RequestBody body = OkHttpMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);

        assertNotNull(body);
        assertInstanceOf(MultipartBody.class, body);
        MultipartBody multipartBody = (MultipartBody) body;
        assertEquals(1, multipartBody.size());
    }

    @Test
    void testBuildMultipartBodyWithBoxedPrimitives() {
        builder.part("intValue", Integer.valueOf(123));
        builder.part("longValue", Long.valueOf(456L));
        builder.part("doubleValue", Double.valueOf(78.9));
        builder.part("floatValue", Float.valueOf(12.3f));
        builder.part("boolValue", Boolean.TRUE);

        Map<String, MultipartBodyBuilder.Part> parts = builder.build();

        RequestBody body = OkHttpMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, false);

        assertNotNull(body);
        assertTrue(body instanceof MultipartBody);
        MultipartBody multipartBody = (MultipartBody) body;
        assertEquals(5, multipartBody.size());
    }
}
