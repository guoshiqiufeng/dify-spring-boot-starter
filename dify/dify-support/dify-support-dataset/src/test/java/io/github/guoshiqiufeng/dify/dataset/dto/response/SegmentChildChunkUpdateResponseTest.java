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
package io.github.guoshiqiufeng.dify.dataset.dto.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试 SegmentChildChunkUpdateResponse 类的功能
 *
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/11 09:59
 */
public class SegmentChildChunkUpdateResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("测试 SegmentChildChunkUpdateResponse 基本属性的设置和获取")
    public void testBasicProperties() {
        // 创建测试对象
        SegmentChildChunkUpdateResponse response = new SegmentChildChunkUpdateResponse();

        // 创建子块响应对象
        SegmentChildChunkResponse chunkResponse = new SegmentChildChunkResponse();
        chunkResponse.setId("chunk-123");
        chunkResponse.setSegmentId("segment-456");
        chunkResponse.setContent("这是更新后的文本块内容");
        chunkResponse.setStatus("completed");

        // 设置属性
        response.setData(chunkResponse);

        // 验证属性
        assertNotNull(response.getData());
        assertEquals("chunk-123", response.getData().getId());
        assertEquals("segment-456", response.getData().getSegmentId());
        assertEquals("这是更新后的文本块内容", response.getData().getContent());
        assertEquals("completed", response.getData().getStatus());
    }

    @Test
    @DisplayName("测试 SegmentChildChunkUpdateResponse 的JSON序列化和反序列化")
    public void testJsonSerializationDeserialization() throws JsonProcessingException {
        // 创建测试对象
        SegmentChildChunkUpdateResponse response = new SegmentChildChunkUpdateResponse();

        // 创建子块响应对象
        SegmentChildChunkResponse chunkResponse = new SegmentChildChunkResponse();
        chunkResponse.setId("chunk-123");
        chunkResponse.setSegmentId("segment-456");
        chunkResponse.setContent("这是更新后的文本块内容");
        chunkResponse.setStatus("completed");
        chunkResponse.setCompletedAt(1618123656789L);

        // 设置属性
        response.setData(chunkResponse);

        // 序列化为JSON
        String json = objectMapper.writeValueAsString(response);

        // 验证JSON包含预期字段
        assertTrue(json.contains("\"data\":"));
        assertTrue(json.contains("\"id\":\"chunk-123\""));
        assertTrue(json.contains("\"segment_id\":\"segment-456\""));
        assertTrue(json.contains("\"content\":\"这是更新后的文本块内容\""));
        assertTrue(json.contains("\"status\":\"completed\""));
        assertTrue(json.contains("\"completed_at\":1618123656789"));

        // 从JSON反序列化
        SegmentChildChunkUpdateResponse deserializedResponse = objectMapper.readValue(json, SegmentChildChunkUpdateResponse.class);

        // 验证反序列化对象
        assertNotNull(deserializedResponse.getData());
        assertEquals("chunk-123", deserializedResponse.getData().getId());
        assertEquals("segment-456", deserializedResponse.getData().getSegmentId());
        assertEquals("这是更新后的文本块内容", deserializedResponse.getData().getContent());
        assertEquals("completed", deserializedResponse.getData().getStatus());
        assertEquals(Long.valueOf(1618123656789L), deserializedResponse.getData().getCompletedAt());
    }

    @Test
    @DisplayName("测试 SegmentChildChunkUpdateResponse 使用下划线命名法的JSON反序列化")
    public void testSnakeCaseJsonDeserialization() throws JsonProcessingException {
        // 使用下划线命名法的JSON
        String snakeCaseJson = "{\n" +
                "  \"data\": {\n" +
                "    \"id\": \"chunk-123\",\n" +
                "    \"segment_id\": \"segment-456\",\n" +
                "    \"content\": \"这是更新后的文本块内容\",\n" +
                "    \"word_count\": 120,\n" +
                "    \"tokens\": 180,\n" +
                "    \"status\": \"completed\",\n" +
                "    \"completed_at\": 1618123656789\n" +
                "  }\n" +
                "}";

        // 从下划线命名法JSON反序列化
        SegmentChildChunkUpdateResponse deserializedResponse = objectMapper.readValue(snakeCaseJson, SegmentChildChunkUpdateResponse.class);

        // 验证下划线命名法字段是否正确映射
        assertNotNull(deserializedResponse.getData());
        assertEquals("chunk-123", deserializedResponse.getData().getId());
        assertEquals("segment-456", deserializedResponse.getData().getSegmentId());  // 验证segment_id映射
        assertEquals("这是更新后的文本块内容", deserializedResponse.getData().getContent());
        assertEquals(Integer.valueOf(120), deserializedResponse.getData().getWordCount());  // 验证word_count映射
        assertEquals(Integer.valueOf(180), deserializedResponse.getData().getTokens());
        assertEquals("completed", deserializedResponse.getData().getStatus());
        assertEquals(Long.valueOf(1618123656789L), deserializedResponse.getData().getCompletedAt());  // 验证completed_at映射
    }

    @Test
    @DisplayName("测试 SegmentChildChunkUpdateResponse 的相等性和哈希码")
    public void testEqualsAndHashCode() {
        // 创建子块响应对象1
        SegmentChildChunkResponse chunkResponse1 = new SegmentChildChunkResponse();
        chunkResponse1.setId("chunk-123");
        chunkResponse1.setSegmentId("segment-456");
        chunkResponse1.setContent("这是更新后的文本块内容");

        // 创建子块响应对象2（与对象1相同内容）
        SegmentChildChunkResponse chunkResponse2 = new SegmentChildChunkResponse();
        chunkResponse2.setId("chunk-123");
        chunkResponse2.setSegmentId("segment-456");
        chunkResponse2.setContent("这是更新后的文本块内容");

        // 创建两个相同内容的对象
        SegmentChildChunkUpdateResponse response1 = new SegmentChildChunkUpdateResponse();
        response1.setData(chunkResponse1);

        SegmentChildChunkUpdateResponse response2 = new SegmentChildChunkUpdateResponse();
        response2.setData(chunkResponse2);

        // 测试相等性
        assertEquals(response1, response2);
        assertEquals(response2, response1);

        // 测试哈希码
        assertEquals(response1.hashCode(), response2.hashCode());

        // 创建子块响应对象3（与对象1、2不同内容）
        SegmentChildChunkResponse chunkResponse3 = new SegmentChildChunkResponse();
        chunkResponse3.setId("chunk-123");
        chunkResponse3.setSegmentId("segment-456");
        chunkResponse3.setContent("这是不同的内容");  // 不同内容

        // 创建不同内容的对象
        SegmentChildChunkUpdateResponse response3 = new SegmentChildChunkUpdateResponse();
        response3.setData(chunkResponse3);

        // 测试不相等性
        assertNotEquals(response1, response3);
        assertNotEquals(response3, response1);
    }

    @Test
    @DisplayName("测试 SegmentChildChunkUpdateResponse 的默认值")
    public void testDefaultValues() {
        // 创建新对象验证默认值
        SegmentChildChunkUpdateResponse response = new SegmentChildChunkUpdateResponse();

        // 验证默认值
        assertNull(response.getData());
    }

    @Test
    @DisplayName("测试 SegmentChildChunkUpdateResponse 更新后的状态")
    public void testUpdatedStatus() throws JsonProcessingException {
        // 测试不同更新状态
        String[] updatedStatuses = {"processing", "completed", "error"};

        for (String status : updatedStatuses) {
            // 创建测试对象
            SegmentChildChunkUpdateResponse response = new SegmentChildChunkUpdateResponse();

            // 创建子块响应对象
            SegmentChildChunkResponse chunkResponse = new SegmentChildChunkResponse();
            chunkResponse.setId("chunk-123");
            chunkResponse.setSegmentId("segment-456");
            chunkResponse.setStatus(status);

            if ("error".equals(status)) {
                chunkResponse.setError("更新时出错");
            } else if ("completed".equals(status)) {
                chunkResponse.setCompletedAt(1618123656789L);
            }

            // 设置属性
            response.setData(chunkResponse);

            // 序列化为JSON
            String json = objectMapper.writeValueAsString(response);

            // 从JSON反序列化
            SegmentChildChunkUpdateResponse deserializedResponse = objectMapper.readValue(json, SegmentChildChunkUpdateResponse.class);

            // 验证状态正确保留
            assertEquals(status, deserializedResponse.getData().getStatus());

            if ("error".equals(status)) {
                assertEquals("更新时出错", deserializedResponse.getData().getError());
            } else if ("completed".equals(status)) {
                assertEquals(Long.valueOf(1618123656789L), deserializedResponse.getData().getCompletedAt());
            }
        }
    }

    @Test
    @DisplayName("测试 SegmentChildChunkUpdateResponse 的toString方法")
    public void testToString() {
        // 创建测试对象
        SegmentChildChunkUpdateResponse response = new SegmentChildChunkUpdateResponse();

        // 创建子块响应对象
        SegmentChildChunkResponse chunkResponse = new SegmentChildChunkResponse();
        chunkResponse.setId("chunk-123");
        chunkResponse.setSegmentId("segment-456");
        chunkResponse.setContent("这是更新后的文本块内容");

        // 设置属性
        response.setData(chunkResponse);

        // 获取toString结果
        String toStringResult = response.toString();

        // 验证toString包含重要字段
        assertTrue(toStringResult.contains("data="));
        assertTrue(toStringResult.contains("id=chunk-123"));
        assertTrue(toStringResult.contains("segmentId=segment-456"));
        assertTrue(toStringResult.contains("content=这是更新后的文本块内容"));
    }
} 