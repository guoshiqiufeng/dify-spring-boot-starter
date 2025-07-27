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
package io.github.guoshiqiufeng.dify.dataset.dto.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试 TagCreateRequest 类的功能
 *
 * @author yanghq
 * @version 1.3.0
 * @since 2025/7/27
 */
public class TagCreateRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("测试 TagCreateRequest 基本属性的设置和获取")
    public void testBasicProperties() {
        // 创建测试对象
        TagCreateRequest request = new TagCreateRequest();

        // 设置基本属性
        String apiKey = "sk-12345678";
        String name = "测试标签";

        // 设置属性
        request.setApiKey(apiKey);
        request.setName(name);

        // 验证属性
        assertEquals(apiKey, request.getApiKey());
        assertEquals(name, request.getName());
    }

    @Test
    @DisplayName("测试 TagCreateRequest 的JSON序列化和反序列化")
    public void testJsonSerializationDeserialization() throws JsonProcessingException {
        // 创建测试对象
        TagCreateRequest request = new TagCreateRequest();
        request.setApiKey("sk-12345678");
        request.setName("测试标签");

        // 序列化为JSON
        String json = objectMapper.writeValueAsString(request);

        // 验证JSON包含预期字段
        assertTrue(json.contains("\"apiKey\":\"sk-12345678\""));
        assertTrue(json.contains("\"name\":\"测试标签\""));

        // 从JSON反序列化
        TagCreateRequest deserializedRequest = objectMapper.readValue(json, TagCreateRequest.class);

        // 验证反序列化对象
        assertEquals("sk-12345678", deserializedRequest.getApiKey());
        assertEquals("测试标签", deserializedRequest.getName());
    }

    @Test
    @DisplayName("测试 TagCreateRequest 的相等性和哈希码")
    public void testEqualsAndHashCode() {
        // 创建两个相同内容的对象
        TagCreateRequest request1 = new TagCreateRequest();
        request1.setApiKey("sk-12345678");
        request1.setName("测试标签");

        TagCreateRequest request2 = new TagCreateRequest();
        request2.setApiKey("sk-12345678");
        request2.setName("测试标签");

        // 测试相等性
        assertEquals(request1, request2);
        assertEquals(request2, request1);

        // 测试哈希码
        assertEquals(request1.hashCode(), request2.hashCode());

        // 创建不同内容的对象
        TagCreateRequest request3 = new TagCreateRequest();
        request3.setApiKey("sk-12345678");
        request3.setName("不同标签"); // 不同名称

        // 测试不相等性
        assertNotEquals(request1, request3);
        assertNotEquals(request3, request1);
    }

    @Test
    @DisplayName("测试 TagCreateRequest 的默认值")
    public void testDefaultValues() {
        // 创建新对象验证默认值
        TagCreateRequest request = new TagCreateRequest();

        // 所有字段默认值应为null
        assertNull(request.getApiKey());
        assertNull(request.getName());
    }

    @Test
    @DisplayName("测试 TagCreateRequest 的toString方法")
    public void testToString() {
        // 创建测试对象
        TagCreateRequest request = new TagCreateRequest();
        request.setApiKey("sk-12345678");
        request.setName("测试标签");

        // 获取toString结果
        String toStringResult = request.toString();

        // 验证toString包含重要字段
        assertTrue(toStringResult.contains("name=测试标签"));
    }
}
