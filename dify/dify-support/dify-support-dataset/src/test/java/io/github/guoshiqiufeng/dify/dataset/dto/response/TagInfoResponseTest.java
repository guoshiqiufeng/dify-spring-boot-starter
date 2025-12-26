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
 * 测试 TagInfoResponse 类的功能
 *
 * @author yanghq
 * @version 1.3.0
 * @since 2025/7/27
 */
public class TagInfoResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("测试 TagInfoResponse 基本属性的设置和获取")
    public void testBasicProperties() {
        // 创建测试对象
        TagInfoResponse response = new TagInfoResponse();

        // 设置基本属性
        String id = "tag-123";
        String name = "测试标签";
        String type = "custom";
        Integer bindingCount = 5;

        // 设置属性
        response.setId(id);
        response.setName(name);
        response.setType(type);
        response.setBindingCount(bindingCount);

        // 验证属性
        assertEquals(id, response.getId());
        assertEquals(name, response.getName());
        assertEquals(type, response.getType());
        assertEquals(bindingCount, response.getBindingCount());
    }

    @Test
    @DisplayName("测试 TagInfoResponse 的JSON序列化和反序列化")
    public void testJsonSerializationDeserialization() throws JsonProcessingException {
        // 创建测试对象
        TagInfoResponse response = new TagInfoResponse();
        response.setId("tag-123");
        response.setName("测试标签");
        response.setType("custom");
        response.setBindingCount(5);

        // 序列化为JSON
        String json = objectMapper.writeValueAsString(response);

        // 验证JSON包含预期字段
        assertTrue(json.contains("\"id\":\"tag-123\""));
        assertTrue(json.contains("\"name\":\"测试标签\""));
        assertTrue(json.contains("\"type\":\"custom\""));
        assertTrue(json.contains("\"bindingCount\":5"));

        // 从JSON反序列化
        TagInfoResponse deserializedResponse = objectMapper.readValue(json, TagInfoResponse.class);

        // 验证反序列化对象
        assertEquals("tag-123", deserializedResponse.getId());
        assertEquals("测试标签", deserializedResponse.getName());
        assertEquals("custom", deserializedResponse.getType());
        assertEquals(Integer.valueOf(5), deserializedResponse.getBindingCount());
    }

    @Test
    @DisplayName("测试 TagInfoResponse 使用蛇形命名法的JSON反序列化")
    public void testSnakeCaseJsonDeserialization() throws JsonProcessingException {
        // 使用蛇形命名法的JSON
        String snakeCaseJson = "{\n" +
                "  \"id\": \"tag-123\",\n" +
                "  \"name\": \"测试标签\",\n" +
                "  \"type\": \"custom\",\n" +
                "  \"binding_count\": 5\n" +
                "}";

        // 从蛇形命名法JSON反序列化
        TagInfoResponse deserializedResponse = objectMapper.readValue(snakeCaseJson, TagInfoResponse.class);

        // 验证蛇形命名法字段是否正确映射
        assertEquals("tag-123", deserializedResponse.getId());
        assertEquals("测试标签", deserializedResponse.getName());
        assertEquals("custom", deserializedResponse.getType());
        assertEquals(Integer.valueOf(5), deserializedResponse.getBindingCount());
    }

    @Test
    @DisplayName("测试 TagInfoResponse 的相等性和哈希码")
    public void testEqualsAndHashCode() {
        // 创建两个相同内容的对象
        TagInfoResponse response1 = new TagInfoResponse();
        response1.setId("tag-123");
        response1.setName("测试标签");
        response1.setType("custom");
        response1.setBindingCount(5);

        TagInfoResponse response2 = new TagInfoResponse();
        response2.setId("tag-123");
        response2.setName("测试标签");
        response2.setType("custom");
        response2.setBindingCount(5);

        // 测试相等性
        assertEquals(response1, response2);
        assertEquals(response2, response1);

        // 测试哈希码
        assertEquals(response1.hashCode(), response2.hashCode());

        // 创建不同内容的对象
        TagInfoResponse response3 = new TagInfoResponse();
        response3.setId("tag-123");
        response3.setName("测试标签");
        response3.setType("custom");
        response3.setBindingCount(10); // 不同bindingCount

        // 测试不相等性
        assertNotEquals(response1, response3);
        assertNotEquals(response3, response1);
    }

    @Test
    @DisplayName("测试 TagInfoResponse 的默认值")
    public void testDefaultValues() {
        // 创建新对象验证默认值
        TagInfoResponse response = new TagInfoResponse();

        // 所有字段默认值应为null
        assertNull(response.getId());
        assertNull(response.getName());
        assertNull(response.getType());
        assertNull(response.getBindingCount());
    }

    @Test
    @DisplayName("测试 TagInfoResponse 的toString方法")
    public void testToString() {
        // 创建测试对象
        TagInfoResponse response = new TagInfoResponse();
        response.setId("tag-123");
        response.setName("测试标签");
        response.setType("custom");
        response.setBindingCount(5);

        // 获取toString结果
        String toStringResult = response.toString();

        // 验证toString包含重要字段
        assertTrue(toStringResult.contains("id=tag-123"));
        assertTrue(toStringResult.contains("name=测试标签"));
        assertTrue(toStringResult.contains("type=custom"));
        assertTrue(toStringResult.contains("bindingCount=5"));
    }
}
