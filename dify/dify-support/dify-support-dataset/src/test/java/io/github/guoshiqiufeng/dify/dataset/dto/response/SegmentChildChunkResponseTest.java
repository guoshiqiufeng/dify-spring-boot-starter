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
 * 测试 SegmentChildChunkResponse 类的功能
 *
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/11 09:46
 */
public class SegmentChildChunkResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("测试 SegmentChildChunkResponse 基本属性的设置和获取")
    public void testBasicProperties() {
        // 创建测试对象
        SegmentChildChunkResponse response = new SegmentChildChunkResponse();

        // 设置属性
        response.setId("chunk-123");
        response.setSegmentId("segment-456");
        response.setContent("这是一个文本块内容");
        response.setWordCount(100);
        response.setTokens(150);
        response.setIndexNodeId("node-789");
        response.setIndexNodeHash("abcdef1234567890");
        response.setStatus("completed");
        response.setCreatedBy("user1");
        response.setCreatedAt(1618123456789L);
        response.setIndexingAt(1618123556789L);
        response.setCompletedAt(1618123656789L);
        response.setError(null);
        response.setStoppedAt(null);

        // 验证属性
        assertEquals("chunk-123", response.getId());
        assertEquals("segment-456", response.getSegmentId());
        assertEquals("这是一个文本块内容", response.getContent());
        assertEquals(Integer.valueOf(100), response.getWordCount());
        assertEquals(Integer.valueOf(150), response.getTokens());
        assertEquals("node-789", response.getIndexNodeId());
        assertEquals("abcdef1234567890", response.getIndexNodeHash());
        assertEquals("completed", response.getStatus());
        assertEquals("user1", response.getCreatedBy());
        assertEquals(Long.valueOf(1618123456789L), response.getCreatedAt());
        assertEquals(Long.valueOf(1618123556789L), response.getIndexingAt());
        assertEquals(Long.valueOf(1618123656789L), response.getCompletedAt());
        assertNull(response.getError());
        assertNull(response.getStoppedAt());
    }

    @Test
    @DisplayName("测试 SegmentChildChunkResponse 的JSON序列化和反序列化")
    public void testJsonSerializationDeserialization() throws JsonProcessingException {
        // 创建测试对象
        SegmentChildChunkResponse response = new SegmentChildChunkResponse();
        response.setId("chunk-123");
        response.setSegmentId("segment-456");
        response.setContent("这是一个文本块内容");
        response.setWordCount(100);
        response.setTokens(150);
        response.setStatus("completed");

        // 序列化为JSON
        String json = objectMapper.writeValueAsString(response);

        // 验证JSON包含预期字段，使用驼峰命名
        assertTrue(json.contains("\"id\":\"chunk-123\""));
        assertTrue(json.contains("\"segment_id\":\"segment-456\""));
        assertTrue(json.contains("\"content\":\"这是一个文本块内容\""));
        assertTrue(json.contains("\"word_count\":100"));
        assertTrue(json.contains("\"tokens\":150"));
        assertTrue(json.contains("\"status\":\"completed\""));

        // 从JSON反序列化
        SegmentChildChunkResponse deserializedResponse = objectMapper.readValue(json, SegmentChildChunkResponse.class);

        // 验证反序列化对象
        assertEquals("chunk-123", deserializedResponse.getId());
        assertEquals("segment-456", deserializedResponse.getSegmentId());
        assertEquals("这是一个文本块内容", deserializedResponse.getContent());
        assertEquals(Integer.valueOf(100), deserializedResponse.getWordCount());
        assertEquals(Integer.valueOf(150), deserializedResponse.getTokens());
        assertEquals("completed", deserializedResponse.getStatus());
    }

    @Test
    @DisplayName("测试 SegmentChildChunkResponse 使用下划线命名法的JSON反序列化")
    public void testSnakeCaseJsonDeserialization() throws JsonProcessingException {
        // 使用下划线命名法的JSON
        String snakeCaseJson = "{\n" +
                "  \"id\": \"chunk-123\",\n" +
                "  \"segment_id\": \"segment-456\",\n" +
                "  \"content\": \"这是一个文本块内容\",\n" +
                "  \"word_count\": 100,\n" +
                "  \"tokens\": 150,\n" +
                "  \"index_node_id\": \"node-789\",\n" +
                "  \"index_node_hash\": \"abcdef1234567890\",\n" +
                "  \"status\": \"completed\",\n" +
                "  \"created_by\": \"user1\",\n" +
                "  \"created_at\": 1618123456789,\n" +
                "  \"indexing_at\": 1618123556789,\n" +
                "  \"completed_at\": 1618123656789\n" +
                "}";

        // 从下划线命名法JSON反序列化
        SegmentChildChunkResponse deserializedResponse = objectMapper.readValue(snakeCaseJson, SegmentChildChunkResponse.class);

        // 验证下划线命名法字段是否正确映射
        assertEquals("chunk-123", deserializedResponse.getId());
        assertEquals("segment-456", deserializedResponse.getSegmentId());  // 验证segment_id映射
        assertEquals("这是一个文本块内容", deserializedResponse.getContent());
        assertEquals(Integer.valueOf(100), deserializedResponse.getWordCount());  // 验证word_count映射
        assertEquals(Integer.valueOf(150), deserializedResponse.getTokens());
        assertEquals("node-789", deserializedResponse.getIndexNodeId());  // 验证index_node_id映射
        assertEquals("abcdef1234567890", deserializedResponse.getIndexNodeHash());  // 验证index_node_hash映射
        assertEquals("completed", deserializedResponse.getStatus());
        assertEquals("user1", deserializedResponse.getCreatedBy());  // 验证created_by映射
        assertEquals(Long.valueOf(1618123456789L), deserializedResponse.getCreatedAt());  // 验证created_at映射
        assertEquals(Long.valueOf(1618123556789L), deserializedResponse.getIndexingAt());  // 验证indexing_at映射
        assertEquals(Long.valueOf(1618123656789L), deserializedResponse.getCompletedAt());  // 验证completed_at映射
    }

    @Test
    @DisplayName("测试 SegmentChildChunkResponse 的相等性和哈希码")
    public void testEqualsAndHashCode() {
        // 创建两个相同内容的对象
        SegmentChildChunkResponse response1 = new SegmentChildChunkResponse();
        response1.setId("chunk-123");
        response1.setSegmentId("segment-456");
        response1.setContent("这是一个文本块内容");
        response1.setStatus("completed");

        SegmentChildChunkResponse response2 = new SegmentChildChunkResponse();
        response2.setId("chunk-123");
        response2.setSegmentId("segment-456");
        response2.setContent("这是一个文本块内容");
        response2.setStatus("completed");

        // 测试相等性
        assertEquals(response1, response2);
        assertEquals(response2, response1);

        // 测试哈希码
        assertEquals(response1.hashCode(), response2.hashCode());

        // 创建不同内容的对象
        SegmentChildChunkResponse response3 = new SegmentChildChunkResponse();
        response3.setId("chunk-789");  // 不同ID
        response3.setSegmentId("segment-456");
        response3.setContent("这是一个文本块内容");
        response3.setStatus("completed");

        // 测试不相等性
        assertNotEquals(response1, response3);
        assertNotEquals(response3, response1);
    }

    @Test
    @DisplayName("测试 SegmentChildChunkResponse 的默认值")
    public void testDefaultValues() {
        // 创建新对象验证默认值
        SegmentChildChunkResponse response = new SegmentChildChunkResponse();

        // 验证默认值
        assertNull(response.getId());
        assertNull(response.getSegmentId());
        assertNull(response.getContent());
        assertNull(response.getWordCount());
        assertNull(response.getTokens());
        assertNull(response.getIndexNodeId());
        assertNull(response.getIndexNodeHash());
        assertNull(response.getStatus());
        assertNull(response.getCreatedBy());
        assertNull(response.getCreatedAt());
        assertNull(response.getIndexingAt());
        assertNull(response.getCompletedAt());
        assertNull(response.getError());
        assertNull(response.getStoppedAt());
    }

    @Test
    @DisplayName("测试 SegmentChildChunkResponse 的不同状态")
    public void testDifferentStatuses() throws JsonProcessingException {
        // 测试不同状态的块
        String[] statuses = {"pending", "processing", "completed", "error", "stopped"};

        for (String status : statuses) {
            // 创建不同状态的块
            SegmentChildChunkResponse response = new SegmentChildChunkResponse();
            response.setId("chunk-" + status);
            response.setStatus(status);

            if ("error".equals(status)) {
                response.setError("处理出错：索引失败");
            } else if ("stopped".equals(status)) {
                response.setStoppedAt(1618123756789L);
            } else if ("completed".equals(status)) {
                response.setCompletedAt(1618123656789L);
            }

            // 序列化为JSON
            String json = objectMapper.writeValueAsString(response);

            // 从JSON反序列化
            SegmentChildChunkResponse deserializedResponse = objectMapper.readValue(json, SegmentChildChunkResponse.class);

            // 验证状态正确保留
            assertEquals(status, deserializedResponse.getStatus());
            assertEquals("chunk-" + status, deserializedResponse.getId());

            if ("error".equals(status)) {
                assertEquals("处理出错：索引失败", deserializedResponse.getError());
            } else if ("stopped".equals(status)) {
                assertEquals(Long.valueOf(1618123756789L), deserializedResponse.getStoppedAt());
            } else if ("completed".equals(status)) {
                assertEquals(Long.valueOf(1618123656789L), deserializedResponse.getCompletedAt());
            }
        }
    }

    @Test
    @DisplayName("测试 SegmentChildChunkResponse 的toString方法")
    public void testToString() {
        // 创建测试对象
        SegmentChildChunkResponse response = new SegmentChildChunkResponse();
        response.setId("chunk-123");
        response.setSegmentId("segment-456");
        response.setContent("这是一个文本块内容");
        response.setStatus("completed");

        // 获取toString结果
        String toStringResult = response.toString();

        // 验证toString包含重要字段
        assertTrue(toStringResult.contains("id=chunk-123"));
        assertTrue(toStringResult.contains("segmentId=segment-456"));
        assertTrue(toStringResult.contains("content=这是一个文本块内容"));
        assertTrue(toStringResult.contains("status=completed"));
    }
} 