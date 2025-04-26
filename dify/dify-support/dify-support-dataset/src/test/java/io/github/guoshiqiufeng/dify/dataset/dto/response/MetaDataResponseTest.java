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
 * 测试 MetaDataResponse 类的功能
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/26 10:43
 */
public class MetaDataResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("测试 MetaDataResponse 基本属性的设置和获取")
    public void testBasicProperties() {
        // 创建测试对象
        MetaDataResponse response = new MetaDataResponse();

        // 设置属性
        response.setId("meta-123");
        response.setType("text");
        response.setName("文本分类");

        // 验证属性
        assertEquals("meta-123", response.getId());
        assertEquals("text", response.getType());
        assertEquals("文本分类", response.getName());
    }

    @Test
    @DisplayName("测试 MetaDataResponse 的JSON序列化和反序列化")
    public void testJsonSerializationDeserialization() throws JsonProcessingException {
        // 创建测试对象
        MetaDataResponse response = new MetaDataResponse();
        response.setId("meta-123");
        response.setType("text");
        response.setName("文本分类");

        // 序列化为JSON
        String json = objectMapper.writeValueAsString(response);

        // 验证JSON包含预期字段
        assertTrue(json.contains("\"id\":\"meta-123\""));
        assertTrue(json.contains("\"type\":\"text\""));
        assertTrue(json.contains("\"name\":\"文本分类\""));

        // 从JSON反序列化
        MetaDataResponse deserializedResponse = objectMapper.readValue(json, MetaDataResponse.class);

        // 验证反序列化对象
        assertEquals("meta-123", deserializedResponse.getId());
        assertEquals("text", deserializedResponse.getType());
        assertEquals("文本分类", deserializedResponse.getName());
    }

    @Test
    @DisplayName("测试 MetaDataResponse 的相等性和哈希码")
    public void testEqualsAndHashCode() {
        // 创建两个相同内容的对象
        MetaDataResponse response1 = new MetaDataResponse();
        response1.setId("meta-123");
        response1.setType("text");
        response1.setName("文本分类");

        MetaDataResponse response2 = new MetaDataResponse();
        response2.setId("meta-123");
        response2.setType("text");
        response2.setName("文本分类");

        // 测试相等性
        assertEquals(response1, response2);
        assertEquals(response2, response1);

        // 测试哈希码
        assertEquals(response1.hashCode(), response2.hashCode());

        // 创建不同内容的对象
        MetaDataResponse response3 = new MetaDataResponse();
        response3.setId("meta-456");  // 不同ID
        response3.setType("text");
        response3.setName("文本分类");

        // 测试不相等性
        assertNotEquals(response1, response3);
        assertNotEquals(response3, response1);
    }

    @Test
    @DisplayName("测试 MetaDataResponse 的默认值")
    public void testDefaultValues() {
        // 创建新对象验证默认值
        MetaDataResponse response = new MetaDataResponse();

        // 验证默认值
        assertNull(response.getId());
        assertNull(response.getType());
        assertNull(response.getName());
    }

    @Test
    @DisplayName("测试 MetaDataResponse 不同类型的元数据")
    public void testDifferentMetadataTypes() throws JsonProcessingException {
        // 测试不同类型的元数据
        String[] types = {"text", "number", "date", "boolean", "category"};

        for (String type : types) {
            // 创建不同类型的元数据
            MetaDataResponse response = new MetaDataResponse();
            response.setId("meta-" + type);
            response.setType(type);
            response.setName(type + "字段");

            // 序列化为JSON
            String json = objectMapper.writeValueAsString(response);

            // 从JSON反序列化
            MetaDataResponse deserializedResponse = objectMapper.readValue(json, MetaDataResponse.class);

            // 验证类型正确保留
            assertEquals(type, deserializedResponse.getType());
            assertEquals("meta-" + type, deserializedResponse.getId());
            assertEquals(type + "字段", deserializedResponse.getName());
        }
    }

    @Test
    @DisplayName("测试 MetaDataResponse 的toString方法")
    public void testToString() {
        // 创建测试对象
        MetaDataResponse response = new MetaDataResponse();
        response.setId("meta-123");
        response.setType("text");
        response.setName("文本分类");

        // 获取toString结果
        String toStringResult = response.toString();

        // 验证toString包含重要字段
        assertTrue(toStringResult.contains("id=meta-123"));
        assertTrue(toStringResult.contains("type=text"));
        assertTrue(toStringResult.contains("name=文本分类"));
    }
} 