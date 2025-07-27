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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试 TagBindingRequest 类的功能
 *
 * @author yanghq
 * @version 1.3.0
 * @since 2025/7/27
 */
public class TagBindingRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("测试 TagBindingRequest 基本属性的设置和获取")
    public void testBasicProperties() {
        // 创建测试对象
        TagBindingRequest request = new TagBindingRequest();

        // 设置基本属性
        String apiKey = "sk-12345678";
        List<String> tagIds = List.of("tag-123", "tag-456");
        String targetId = "dataset-789";

        // 设置属性
        request.setApiKey(apiKey);
        request.setTagIds(tagIds);
        request.setTargetId(targetId);

        // 验证属性
        assertEquals(apiKey, request.getApiKey());
        assertEquals(tagIds, request.getTagIds());
        assertEquals(targetId, request.getTargetId());
    }

    @Test
    @DisplayName("测试 TagBindingRequest 的JSON序列化和反序列化")
    public void testJsonSerializationDeserialization() throws JsonProcessingException {
        // 创建测试对象
        TagBindingRequest request = new TagBindingRequest();
        request.setApiKey("sk-12345678");
        request.setTagIds(List.of("tag-123", "tag-456"));
        request.setTargetId("dataset-789");

        // 序列化为JSON
        String json = objectMapper.writeValueAsString(request);

        // 验证JSON包含预期字段
        assertTrue(json.contains("\"apiKey\":\"sk-12345678\""));
        assertTrue(json.contains("\"targetId\":\"dataset-789\""));
        assertTrue(json.contains("\"target_id\":\"dataset-789\""));
        assertTrue(json.contains("\"tagIds\":[\"tag-123\",\"tag-456\"]"));
        assertTrue(json.contains("\"tag_ids\":[\"tag-123\",\"tag-456\"]"));

        // 从JSON反序列化
        TagBindingRequest deserializedRequest = objectMapper.readValue(json, TagBindingRequest.class);

        // 验证反序列化对象
        assertEquals("sk-12345678", deserializedRequest.getApiKey());
        assertEquals("dataset-789", deserializedRequest.getTargetId());
        assertEquals(List.of("tag-123", "tag-456"), deserializedRequest.getTagIds());
    }

    @Test
    @DisplayName("测试 TagBindingRequest 使用蛇形命名法的JSON反序列化")
    public void testSnakeCaseJsonDeserialization() throws JsonProcessingException {
        // 使用蛇形命名法的JSON
        String snakeCaseJson = "{\n" +
                "  \"apiKey\": \"sk-12345678\",\n" +
                "  \"tag_ids\": [\"tag-123\", \"tag-456\"],\n" +
                "  \"target_id\": \"dataset-789\"\n" +
                "}";

        // 从蛇形命名法JSON反序列化
        TagBindingRequest deserializedRequest = objectMapper.readValue(snakeCaseJson, TagBindingRequest.class);

        // 验证蛇形命名法字段是否正确映射
        assertEquals("sk-12345678", deserializedRequest.getApiKey());
        assertEquals("dataset-789", deserializedRequest.getTargetId());
        assertEquals(List.of("tag-123", "tag-456"), deserializedRequest.getTagIds());
    }

    @Test
    @DisplayName("测试 TagBindingRequest 的相等性和哈希码")
    public void testEqualsAndHashCode() {
        // 创建两个相同内容的对象
        TagBindingRequest request1 = new TagBindingRequest();
        request1.setApiKey("sk-12345678");
        request1.setTagIds(List.of("tag-123", "tag-456"));
        request1.setTargetId("dataset-789");

        TagBindingRequest request2 = new TagBindingRequest();
        request2.setApiKey("sk-12345678");
        request2.setTagIds(List.of("tag-123", "tag-456"));
        request2.setTargetId("dataset-789");

        // 测试相等性
        assertEquals(request1, request2);
        assertEquals(request2, request1);

        // 测试哈希码
        assertEquals(request1.hashCode(), request2.hashCode());

        // 创建不同内容的对象
        TagBindingRequest request3 = new TagBindingRequest();
        request3.setApiKey("sk-12345678");
        request3.setTagIds(List.of("tag-123", "tag-456"));
        request3.setTargetId("dataset-999"); // 不同targetId

        // 测试不相等性
        assertNotEquals(request1, request3);
        assertNotEquals(request3, request1);
    }

    @Test
    @DisplayName("测试 TagBindingRequest 的默认值")
    public void testDefaultValues() {
        // 创建新对象验证默认值
        TagBindingRequest request = new TagBindingRequest();

        // 所有字段默认值应为null
        assertNull(request.getApiKey());
        assertNull(request.getTagIds());
        assertNull(request.getTargetId());
    }

    @Test
    @DisplayName("测试 TagBindingRequest 的toString方法")
    public void testToString() {
        // 创建测试对象
        TagBindingRequest request = new TagBindingRequest();
        request.setApiKey("sk-12345678");
        request.setTagIds(List.of("tag-123", "tag-456"));
        request.setTargetId("dataset-789");

        // 获取toString结果
        String toStringResult = request.toString();

        // 验证toString包含重要字段
        assertTrue(toStringResult.contains("apiKey=sk-12345678"));
        assertTrue(toStringResult.contains("tagIds=[tag-123, tag-456]"));
        assertTrue(toStringResult.contains("targetId=dataset-789"));
    }
}
