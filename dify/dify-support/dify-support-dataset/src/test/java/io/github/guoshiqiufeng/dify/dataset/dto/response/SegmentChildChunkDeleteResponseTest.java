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
 * 测试 SegmentChildChunkDeleteResponse 类的功能
 *
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/11 09:55
 */
public class SegmentChildChunkDeleteResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("测试 SegmentChildChunkDeleteResponse 基本属性的设置和获取")
    public void testBasicProperties() {
        // 创建测试对象
        SegmentChildChunkDeleteResponse response = new SegmentChildChunkDeleteResponse();

        // 设置属性
        response.setResult("success");

        // 验证属性
        assertEquals("success", response.getResult());
    }

    @Test
    @DisplayName("测试 SegmentChildChunkDeleteResponse 的JSON序列化和反序列化")
    public void testJsonSerializationDeserialization() throws JsonProcessingException {
        // 创建测试对象
        SegmentChildChunkDeleteResponse response = new SegmentChildChunkDeleteResponse();
        response.setResult("success");

        // 序列化为JSON
        String json = objectMapper.writeValueAsString(response);

        // 验证JSON包含预期字段
        assertTrue(json.contains("\"result\":\"success\""));

        // 从JSON反序列化
        SegmentChildChunkDeleteResponse deserializedResponse = objectMapper.readValue(json, SegmentChildChunkDeleteResponse.class);

        // 验证反序列化对象
        assertEquals("success", deserializedResponse.getResult());
    }

    @Test
    @DisplayName("测试 SegmentChildChunkDeleteResponse 的相等性和哈希码")
    public void testEqualsAndHashCode() {
        // 创建两个相同内容的对象
        SegmentChildChunkDeleteResponse response1 = new SegmentChildChunkDeleteResponse();
        response1.setResult("success");

        SegmentChildChunkDeleteResponse response2 = new SegmentChildChunkDeleteResponse();
        response2.setResult("success");

        // 测试相等性
        assertEquals(response1, response2);
        assertEquals(response2, response1);

        // 测试哈希码
        assertEquals(response1.hashCode(), response2.hashCode());

        // 创建不同内容的对象
        SegmentChildChunkDeleteResponse response3 = new SegmentChildChunkDeleteResponse();
        response3.setResult("failed");  // 不同结果

        // 测试不相等性
        assertNotEquals(response1, response3);
        assertNotEquals(response3, response1);
    }

    @Test
    @DisplayName("测试 SegmentChildChunkDeleteResponse 的默认值")
    public void testDefaultValues() {
        // 创建新对象验证默认值
        SegmentChildChunkDeleteResponse response = new SegmentChildChunkDeleteResponse();

        // 验证默认值
        assertNull(response.getResult());
    }

    @Test
    @DisplayName("测试 SegmentChildChunkDeleteResponse 的不同结果状态")
    public void testDifferentResults() throws JsonProcessingException {
        // 测试不同结果状态
        String[] results = {"success", "failed", "not_found"};

        for (String result : results) {
            // 创建带有不同结果的响应
            SegmentChildChunkDeleteResponse response = new SegmentChildChunkDeleteResponse();
            response.setResult(result);

            // 序列化为JSON
            String json = objectMapper.writeValueAsString(response);

            // 从JSON反序列化
            SegmentChildChunkDeleteResponse deserializedResponse = objectMapper.readValue(json, SegmentChildChunkDeleteResponse.class);

            // 验证结果正确保留
            assertEquals(result, deserializedResponse.getResult());
        }
    }

    @Test
    @DisplayName("测试 SegmentChildChunkDeleteResponse 的toString方法")
    public void testToString() {
        // 创建测试对象
        SegmentChildChunkDeleteResponse response = new SegmentChildChunkDeleteResponse();
        response.setResult("success");

        // 获取toString结果
        String toStringResult = response.toString();

        // 验证toString包含重要字段
        assertTrue(toStringResult.contains("result=success"));
    }
} 