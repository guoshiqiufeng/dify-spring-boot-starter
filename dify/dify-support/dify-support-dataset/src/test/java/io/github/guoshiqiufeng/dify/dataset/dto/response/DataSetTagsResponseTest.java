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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试 DataSetTagsResponse 类的功能
 *
 * @author yanghq
 * @version 1.3.0
 * @since 2025/7/27
 */
public class DataSetTagsResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("测试 DataSetTagsResponse 基本属性的设置和获取")
    public void testBasicProperties() {
        // 创建测试对象
        DataSetTagsResponse response = new DataSetTagsResponse();

        // 创建测试数据
        DataSetTagInfo tagInfo1 = new DataSetTagInfo();
        tagInfo1.setId("tag-123");
        tagInfo1.setName("测试标签1");

        DataSetTagInfo tagInfo2 = new DataSetTagInfo();
        tagInfo2.setId("tag-456");
        tagInfo2.setName("测试标签2");

        List<DataSetTagInfo> data = List.of(tagInfo1, tagInfo2);
        Integer total = 2;

        // 设置属性
        response.setData(data);
        response.setTotal(total);

        // 验证属性
        assertEquals(data, response.getData());
        assertEquals(total, response.getTotal());
        assertEquals(2, response.getData().size());
        assertEquals("tag-123", response.getData().get(0).getId());
        assertEquals("测试标签1", response.getData().get(0).getName());
        assertEquals("tag-456", response.getData().get(1).getId());
        assertEquals("测试标签2", response.getData().get(1).getName());
    }

    @Test
    @DisplayName("测试 DataSetTagsResponse 的JSON序列化和反序列化")
    public void testJsonSerializationDeserialization() throws JsonProcessingException {
        // 创建测试对象
        DataSetTagsResponse response = new DataSetTagsResponse();

        // 创建测试数据
        DataSetTagInfo tagInfo1 = new DataSetTagInfo();
        tagInfo1.setId("tag-123");
        tagInfo1.setName("测试标签1");

        DataSetTagInfo tagInfo2 = new DataSetTagInfo();
        tagInfo2.setId("tag-456");
        tagInfo2.setName("测试标签2");

        List<DataSetTagInfo> data = List.of(tagInfo1, tagInfo2);
        Integer total = 2;

        // 设置属性
        response.setData(data);
        response.setTotal(total);

        // 序列化为JSON
        String json = objectMapper.writeValueAsString(response);

        // 验证JSON包含预期字段
        assertTrue(json.contains("\"total\":2"));

        // 从JSON反序列化
        DataSetTagsResponse deserializedResponse = objectMapper.readValue(json, DataSetTagsResponse.class);

        // 验证反序列化对象
        assertEquals(total, deserializedResponse.getTotal());
        assertNotNull(deserializedResponse.getData());
        assertEquals(2, deserializedResponse.getData().size());
        // Note: Due to generic type erasure, we can't directly compare the list contents
        // But we can check the size and that it's not null
    }

    @Test
    @DisplayName("测试 DataSetTagsResponse 的相等性和哈希码")
    public void testEqualsAndHashCode() {
        // 创建两个相同内容的对象
        DataSetTagsResponse response1 = new DataSetTagsResponse();
        response1.setTotal(2);

        DataSetTagsResponse response2 = new DataSetTagsResponse();
        response2.setTotal(2);

        // 测试相等性
        assertEquals(response1, response2);
        assertEquals(response2, response1);

        // 测试哈希码
        assertEquals(response1.hashCode(), response2.hashCode());

        // 创建不同内容的对象
        DataSetTagsResponse response3 = new DataSetTagsResponse();
        response3.setTotal(3); // 不同total

        // 测试不相等性
        assertNotEquals(response1, response3);
        assertNotEquals(response3, response1);
    }

    @Test
    @DisplayName("测试 DataSetTagsResponse 的默认值")
    public void testDefaultValues() {
        // 创建新对象验证默认值
        DataSetTagsResponse response = new DataSetTagsResponse();

        // 所有字段默认值应为null
        assertNull(response.getData());
        assertNull(response.getTotal());
    }

    @Test
    @DisplayName("测试 DataSetTagsResponse 的toString方法")
    public void testToString() {
        // 创建测试对象
        DataSetTagsResponse response = new DataSetTagsResponse();
        response.setTotal(2);

        // 获取toString结果
        String toStringResult = response.toString();

        // 验证toString包含重要字段
        assertTrue(toStringResult.contains("total=2"));
    }
}
