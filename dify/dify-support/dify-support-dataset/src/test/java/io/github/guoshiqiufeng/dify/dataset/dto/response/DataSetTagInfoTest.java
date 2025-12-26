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
 * 测试 DataSetTagInfo 类的功能
 *
 * @author yanghq
 * @version 1.3.0
 * @since 2025/7/27
 */
public class DataSetTagInfoTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("测试 DataSetTagInfo 基本属性的设置和获取")
    public void testBasicProperties() {
        // 创建测试对象
        DataSetTagInfo info = new DataSetTagInfo();

        // 设置基本属性
        String id = "tag-123";
        String name = "测试标签";

        // 设置属性
        info.setId(id);
        info.setName(name);

        // 验证属性
        assertEquals(id, info.getId());
        assertEquals(name, info.getName());
    }

    @Test
    @DisplayName("测试 DataSetTagInfo 的JSON序列化和反序列化")
    public void testJsonSerializationDeserialization() throws JsonProcessingException {
        // 创建测试对象
        DataSetTagInfo info = new DataSetTagInfo();
        info.setId("tag-123");
        info.setName("测试标签");

        // 序列化为JSON
        String json = objectMapper.writeValueAsString(info);

        // 验证JSON包含预期字段
        assertTrue(json.contains("\"id\":\"tag-123\""));
        assertTrue(json.contains("\"name\":\"测试标签\""));

        // 从JSON反序列化
        DataSetTagInfo deserializedInfo = objectMapper.readValue(json, DataSetTagInfo.class);

        // 验证反序列化对象
        assertEquals("tag-123", deserializedInfo.getId());
        assertEquals("测试标签", deserializedInfo.getName());
    }

    @Test
    @DisplayName("测试 DataSetTagInfo 的相等性和哈希码")
    public void testEqualsAndHashCode() {
        // 创建两个相同内容的对象
        DataSetTagInfo info1 = new DataSetTagInfo();
        info1.setId("tag-123");
        info1.setName("测试标签");

        DataSetTagInfo info2 = new DataSetTagInfo();
        info2.setId("tag-123");
        info2.setName("测试标签");

        // 测试相等性
        assertEquals(info1, info2);
        assertEquals(info2, info1);

        // 测试哈希码
        assertEquals(info1.hashCode(), info2.hashCode());

        // 创建不同内容的对象
        DataSetTagInfo info3 = new DataSetTagInfo();
        info3.setId("tag-123");
        info3.setName("不同标签"); // 不同name

        // 测试不相等性
        assertNotEquals(info1, info3);
        assertNotEquals(info3, info1);
    }

    @Test
    @DisplayName("测试 DataSetTagInfo 的默认值")
    public void testDefaultValues() {
        // 创建新对象验证默认值
        DataSetTagInfo info = new DataSetTagInfo();

        // 所有字段默认值应为null
        assertNull(info.getId());
        assertNull(info.getName());
    }

    @Test
    @DisplayName("测试 DataSetTagInfo 的toString方法")
    public void testToString() {
        // 创建测试对象
        DataSetTagInfo info = new DataSetTagInfo();
        info.setId("tag-123");
        info.setName("测试标签");

        // 获取toString结果
        String toStringResult = info.toString();

        // 验证toString包含重要字段
        assertTrue(toStringResult.contains("id=tag-123"));
        assertTrue(toStringResult.contains("name=测试标签"));
    }
}
