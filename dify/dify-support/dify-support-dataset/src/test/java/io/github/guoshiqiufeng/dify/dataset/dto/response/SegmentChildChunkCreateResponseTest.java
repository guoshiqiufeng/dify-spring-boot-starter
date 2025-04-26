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
package io.github.guoshiqiufeng.dify.dataset.dto.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试 SegmentChildChunkCreateResponse 类的功能
 *
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/11 09:34
 */
public class SegmentChildChunkCreateResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("测试 SegmentChildChunkCreateResponse 基本属性的设置和获取")
    public void testBasicProperties() {
        // 创建测试对象
        SegmentChildChunkCreateResponse response = new SegmentChildChunkCreateResponse();

        // 创建子块响应对象
        SegmentChildChunkResponse chunkResponse = new SegmentChildChunkResponse();
        chunkResponse.setId("chunk-123");
        chunkResponse.setSegmentId("segment-456");
        chunkResponse.setContent("这是一个测试文本块");
        chunkResponse.setStatus("pending");

        // 设置属性
        response.setData(chunkResponse);

        // 验证属性
        assertNotNull(response.getData());
        assertEquals("chunk-123", response.getData().getId());
        assertEquals("segment-456", response.getData().getSegmentId());
        assertEquals("这是一个测试文本块", response.getData().getContent());
        assertEquals("pending", response.getData().getStatus());
    }

    @Test
    @DisplayName("测试 SegmentChildChunkCreateResponse 的JSON序列化和反序列化")
    public void testJsonSerializationDeserialization() throws JsonProcessingException {
        // 创建测试对象
        SegmentChildChunkCreateResponse response = new SegmentChildChunkCreateResponse();

        // 创建子块响应对象
        SegmentChildChunkResponse chunkResponse = new SegmentChildChunkResponse();
        chunkResponse.setId("chunk-123");
        chunkResponse.setSegmentId("segment-456");
        chunkResponse.setContent("这是一个测试文本块");
        chunkResponse.setStatus("pending");

        // 设置属性
        response.setData(chunkResponse);

        // 序列化为JSON
        String json = objectMapper.writeValueAsString(response);

        // 验证JSON包含预期字段
        assertTrue(json.contains("\"data\":"));
        assertTrue(json.contains("\"id\":\"chunk-123\""));
        assertTrue(json.contains("\"segment_id\":\"segment-456\""));
        assertTrue(json.contains("\"content\":\"这是一个测试文本块\""));
        assertTrue(json.contains("\"status\":\"pending\""));

        // 从JSON反序列化
        SegmentChildChunkCreateResponse deserializedResponse = objectMapper.readValue(json, SegmentChildChunkCreateResponse.class);

        // 验证反序列化对象
        assertNotNull(deserializedResponse.getData());
        assertEquals("chunk-123", deserializedResponse.getData().getId());
        assertEquals("segment-456", deserializedResponse.getData().getSegmentId());
        assertEquals("这是一个测试文本块", deserializedResponse.getData().getContent());
        assertEquals("pending", deserializedResponse.getData().getStatus());
    }

    @Test
    @DisplayName("测试 SegmentChildChunkCreateResponse 使用下划线命名法的JSON反序列化")
    public void testSnakeCaseJsonDeserialization() throws JsonProcessingException {
        // 使用下划线命名法的JSON
        String snakeCaseJson = "{\n" +
                "  \"data\": {\n" +
                "    \"id\": \"chunk-123\",\n" +
                "    \"segment_id\": \"segment-456\",\n" +
                "    \"content\": \"这是一个测试文本块\",\n" +
                "    \"word_count\": 100,\n" +
                "    \"tokens\": 150,\n" +
                "    \"status\": \"pending\"\n" +
                "  }\n" +
                "}";

        // 从下划线命名法JSON反序列化
        SegmentChildChunkCreateResponse deserializedResponse = objectMapper.readValue(snakeCaseJson, SegmentChildChunkCreateResponse.class);

        // 验证下划线命名法字段是否正确映射
        assertNotNull(deserializedResponse.getData());
        assertEquals("chunk-123", deserializedResponse.getData().getId());
        assertEquals("segment-456", deserializedResponse.getData().getSegmentId());  // 验证segment_id映射
        assertEquals("这是一个测试文本块", deserializedResponse.getData().getContent());
        assertEquals(Integer.valueOf(100), deserializedResponse.getData().getWordCount());  // 验证word_count映射
        assertEquals(Integer.valueOf(150), deserializedResponse.getData().getTokens());
        assertEquals("pending", deserializedResponse.getData().getStatus());
    }

    @Test
    @DisplayName("测试 SegmentChildChunkCreateResponse 的相等性和哈希码")
    public void testEqualsAndHashCode() {
        // 创建子块响应对象1
        SegmentChildChunkResponse chunkResponse1 = new SegmentChildChunkResponse();
        chunkResponse1.setId("chunk-123");
        chunkResponse1.setSegmentId("segment-456");

        // 创建子块响应对象2（与对象1相同内容）
        SegmentChildChunkResponse chunkResponse2 = new SegmentChildChunkResponse();
        chunkResponse2.setId("chunk-123");
        chunkResponse2.setSegmentId("segment-456");

        // 创建两个相同内容的对象
        SegmentChildChunkCreateResponse response1 = new SegmentChildChunkCreateResponse();
        response1.setData(chunkResponse1);

        SegmentChildChunkCreateResponse response2 = new SegmentChildChunkCreateResponse();
        response2.setData(chunkResponse2);

        // 测试相等性
        assertEquals(response1, response2);
        assertEquals(response2, response1);

        // 测试哈希码
        assertEquals(response1.hashCode(), response2.hashCode());

        // 创建子块响应对象3（与对象1、2不同内容）
        SegmentChildChunkResponse chunkResponse3 = new SegmentChildChunkResponse();
        chunkResponse3.setId("chunk-789");  // 不同ID
        chunkResponse3.setSegmentId("segment-456");

        // 创建不同内容的对象
        SegmentChildChunkCreateResponse response3 = new SegmentChildChunkCreateResponse();
        response3.setData(chunkResponse3);

        // 测试不相等性
        assertNotEquals(response1, response3);
        assertNotEquals(response3, response1);
    }

    @Test
    @DisplayName("测试 SegmentChildChunkCreateResponse 的默认值")
    public void testDefaultValues() {
        // 创建新对象验证默认值
        SegmentChildChunkCreateResponse response = new SegmentChildChunkCreateResponse();

        // 验证默认值
        assertNull(response.getData());
    }

    @Test
    @DisplayName("测试 SegmentChildChunkCreateResponse 的toString方法")
    public void testToString() {
        // 创建测试对象
        SegmentChildChunkCreateResponse response = new SegmentChildChunkCreateResponse();

        // 创建子块响应对象
        SegmentChildChunkResponse chunkResponse = new SegmentChildChunkResponse();
        chunkResponse.setId("chunk-123");
        chunkResponse.setSegmentId("segment-456");

        // 设置属性
        response.setData(chunkResponse);

        // 获取toString结果
        String toStringResult = response.toString();

        // 验证toString包含重要字段
        assertTrue(toStringResult.contains("data="));
        assertTrue(toStringResult.contains("id=chunk-123"));
        assertTrue(toStringResult.contains("segmentId=segment-456"));
    }
} 