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
 * 测试 TagUpdateRequest 类的功能
 *
 * @author yanghq
 * @version 1.3.0
 * @since 2025/7/27
 */
public class TagUpdateRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("测试 TagUpdateRequest 基本属性的设置和获取")
    public void testBasicProperties() {
        // 创建测试对象
        TagUpdateRequest request = new TagUpdateRequest();

        // 设置基本属性
        String apiKey = "sk-12345678";
        String name = "更新标签";
        String tagId = "tag-123";

        // 设置属性
        request.setApiKey(apiKey);
        request.setName(name);
        request.setTagId(tagId);

        // 验证属性
        assertEquals(apiKey, request.getApiKey());
        assertEquals(name, request.getName());
        assertEquals(tagId, request.getTagId());
    }

    @Test
    @DisplayName("测试 TagUpdateRequest 的JSON序列化和反序列化")
    public void testJsonSerializationDeserialization() throws JsonProcessingException {
        // 创建测试对象
        TagUpdateRequest request = new TagUpdateRequest();
        request.setApiKey("sk-12345678");
        request.setName("更新标签");
        request.setTagId("tag-123");

        // 序列化为JSON
        String json = objectMapper.writeValueAsString(request);

        // 验证JSON包含预期字段
        assertTrue(json.contains("\"apiKey\":\"sk-12345678\""));
        assertTrue(json.contains("\"name\":\"更新标签\""));
        assertTrue(json.contains("\"tagId\":\"tag-123\""));
        assertTrue(json.contains("\"tag_id\":\"tag-123\""));

        // 从JSON反序列化
        TagUpdateRequest deserializedRequest = objectMapper.readValue(json, TagUpdateRequest.class);

        // 验证反序列化对象
        assertEquals("sk-12345678", deserializedRequest.getApiKey());
        assertEquals("更新标签", deserializedRequest.getName());
        assertEquals("tag-123", deserializedRequest.getTagId());
    }

    @Test
    @DisplayName("测试 TagUpdateRequest 使用蛇形命名法的JSON反序列化")
    public void testSnakeCaseJsonDeserialization() throws JsonProcessingException {
        // 使用蛇形命名法的JSON
        String snakeCaseJson = "{\n" +
                "  \"apiKey\": \"sk-12345678\",\n" +
                "  \"name\": \"更新标签\",\n" +
                "  \"tag_id\": \"tag-123\"\n" +
                "}";

        // 从蛇形命名法JSON反序列化
        TagUpdateRequest deserializedRequest = objectMapper.readValue(snakeCaseJson, TagUpdateRequest.class);

        // 验证蛇形命名法字段是否正确映射
        assertEquals("sk-12345678", deserializedRequest.getApiKey());
        assertEquals("更新标签", deserializedRequest.getName());
        assertEquals("tag-123", deserializedRequest.getTagId());
    }

    @Test
    @DisplayName("测试 TagUpdateRequest 的相等性和哈希码")
    public void testEqualsAndHashCode() {
        // 创建两个相同内容的对象
        TagUpdateRequest request1 = new TagUpdateRequest();
        request1.setApiKey("sk-12345678");
        request1.setName("更新标签");
        request1.setTagId("tag-123");

        TagUpdateRequest request2 = new TagUpdateRequest();
        request2.setApiKey("sk-12345678");
        request2.setName("更新标签");
        request2.setTagId("tag-123");

        // 测试相等性
        assertEquals(request1, request2);
        assertEquals(request2, request1);

        // 测试哈希码
        assertEquals(request1.hashCode(), request2.hashCode());

        // 创建不同内容的对象
        TagUpdateRequest request3 = new TagUpdateRequest();
        request3.setApiKey("sk-12345678");
        request3.setName("更新标签");
        request3.setTagId("tag-456"); // 不同tagId

        // 测试不相等性
        assertNotEquals(request1, request3);
        assertNotEquals(request3, request1);
    }

    @Test
    @DisplayName("测试 TagUpdateRequest 的默认值")
    public void testDefaultValues() {
        // 创建新对象验证默认值
        TagUpdateRequest request = new TagUpdateRequest();

        // 所有字段默认值应为null
        assertNull(request.getApiKey());
        assertNull(request.getName());
        assertNull(request.getTagId());
    }

    @Test
    @DisplayName("测试 TagUpdateRequest 的toString方法")
    public void testToString() {
        // 创建测试对象
        TagUpdateRequest request = new TagUpdateRequest();
        request.setApiKey("sk-12345678");
        request.setName("更新标签");
        request.setTagId("tag-123");

        // 获取toString结果
        String toStringResult = request.toString();

        // 验证toString包含重要字段
        assertTrue(toStringResult.contains("apiKey=sk-12345678"));
        assertTrue(toStringResult.contains("name=更新标签"));
        assertTrue(toStringResult.contains("tagId=tag-123"));
    }
}
