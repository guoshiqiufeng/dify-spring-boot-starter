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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试 MetaDataListResponse 类的功能
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/26 10:58
 */
public class MetaDataListResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("测试 MetaDataListResponse 基本属性的设置和获取")
    public void testBasicProperties() {
        // 创建测试对象
        MetaDataListResponse response = new MetaDataListResponse();

        // 设置属性
        response.setBuiltInFieldEnabled(true);

        // 创建文档元数据列表
        List<MetaDataListResponse.DocMetadata> docMetadataList = new ArrayList<>();

        // 创建文档元数据1
        MetaDataListResponse.DocMetadata docMetadata1 = new MetaDataListResponse.DocMetadata();
        docMetadata1.setId("meta-123");
        docMetadata1.setType("text");
        docMetadata1.setName("文本字段");
        docMetadata1.setUserCount(10);

        // 创建文档元数据2
        MetaDataListResponse.DocMetadata docMetadata2 = new MetaDataListResponse.DocMetadata();
        docMetadata2.setId("meta-456");
        docMetadata2.setType("number");
        docMetadata2.setName("数值字段");
        docMetadata2.setUserCount(5);

        docMetadataList.add(docMetadata1);
        docMetadataList.add(docMetadata2);

        // 设置文档元数据列表
        response.setDocMetadata(docMetadataList);

        // 验证属性
        assertEquals(true, response.getBuiltInFieldEnabled());
        assertEquals(docMetadataList, response.getDocMetadata());
        assertEquals(2, response.getDocMetadata().size());

        // 验证第一个文档元数据
        MetaDataListResponse.DocMetadata retrievedMetadata1 = response.getDocMetadata().get(0);
        assertEquals("meta-123", retrievedMetadata1.getId());
        assertEquals("text", retrievedMetadata1.getType());
        assertEquals("文本字段", retrievedMetadata1.getName());
        assertEquals(Integer.valueOf(10), retrievedMetadata1.getUserCount());

        // 验证第二个文档元数据
        MetaDataListResponse.DocMetadata retrievedMetadata2 = response.getDocMetadata().get(1);
        assertEquals("meta-456", retrievedMetadata2.getId());
        assertEquals("number", retrievedMetadata2.getType());
        assertEquals("数值字段", retrievedMetadata2.getName());
        assertEquals(Integer.valueOf(5), retrievedMetadata2.getUserCount());
    }

    @Test
    @DisplayName("测试 MetaDataListResponse 的JSON序列化和反序列化")
    public void testJsonSerializationDeserialization() throws JsonProcessingException {
        // 创建测试对象
        MetaDataListResponse response = new MetaDataListResponse();
        response.setBuiltInFieldEnabled(true);

        List<MetaDataListResponse.DocMetadata> docMetadataList = new ArrayList<>();
        MetaDataListResponse.DocMetadata docMetadata = new MetaDataListResponse.DocMetadata();
        docMetadata.setId("meta-123");
        docMetadata.setType("text");
        docMetadata.setName("文本字段");
        docMetadata.setUserCount(10);
        docMetadataList.add(docMetadata);

        response.setDocMetadata(docMetadataList);

        // 序列化为JSON
        String json = objectMapper.writeValueAsString(response);

        // 验证JSON包含预期字段
        assertTrue(json.contains("\"builtInFieldEnabled\":true"));
        assertTrue(json.contains("\"docMetadata\":"));
        assertTrue(json.contains("\"id\":\"meta-123\""));
        assertTrue(json.contains("\"type\":\"text\""));
        assertTrue(json.contains("\"name\":\"文本字段\""));
        assertTrue(json.contains("\"userCount\":10"));

        // 从JSON反序列化
        MetaDataListResponse deserializedResponse = objectMapper.readValue(json, MetaDataListResponse.class);

        // 验证反序列化对象
        assertEquals(Boolean.TRUE, deserializedResponse.getBuiltInFieldEnabled());
        assertNotNull(deserializedResponse.getDocMetadata());
        assertEquals(1, deserializedResponse.getDocMetadata().size());

        MetaDataListResponse.DocMetadata deserializedMetadata = deserializedResponse.getDocMetadata().get(0);
        assertEquals("meta-123", deserializedMetadata.getId());
        assertEquals("text", deserializedMetadata.getType());
        assertEquals("文本字段", deserializedMetadata.getName());
        assertEquals(Integer.valueOf(10), deserializedMetadata.getUserCount());
    }

    @Test
    @DisplayName("测试 MetaDataListResponse 使用蛇形命名法的JSON反序列化")
    public void testSnakeCaseJsonDeserialization() throws JsonProcessingException {
        // 使用蛇形命名法的JSON
        String snakeCaseJson = "{\n" +
                "  \"built_in_field_name\": true,\n" +
                "  \"doc_metadata\": [\n" +
                "    {\n" +
                "      \"id\": \"meta-123\",\n" +
                "      \"type\": \"text\",\n" +
                "      \"name\": \"文本字段\",\n" +
                "      \"use_count\": 10\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"meta-456\",\n" +
                "      \"type\": \"number\",\n" +
                "      \"name\": \"数值字段\",\n" +
                "      \"use_count\": 5\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        // 从蛇形命名法JSON反序列化
        MetaDataListResponse deserializedResponse = objectMapper.readValue(snakeCaseJson, MetaDataListResponse.class);

        // 验证蛇形命名法字段是否正确映射
        assertEquals(Boolean.TRUE, deserializedResponse.getBuiltInFieldEnabled());  // 验证built_in_field_name映射
        assertNotNull(deserializedResponse.getDocMetadata());  // 验证doc_metadata映射
        assertEquals(2, deserializedResponse.getDocMetadata().size());

        // 验证第一个元数据
        MetaDataListResponse.DocMetadata metadata1 = deserializedResponse.getDocMetadata().get(0);
        assertEquals("meta-123", metadata1.getId());
        assertEquals("text", metadata1.getType());
        assertEquals("文本字段", metadata1.getName());
        assertEquals(Integer.valueOf(10), metadata1.getUserCount());  // 验证use_count映射

        // 验证第二个元数据
        MetaDataListResponse.DocMetadata metadata2 = deserializedResponse.getDocMetadata().get(1);
        assertEquals("meta-456", metadata2.getId());
        assertEquals("number", metadata2.getType());
        assertEquals("数值字段", metadata2.getName());
        assertEquals(Integer.valueOf(5), metadata2.getUserCount());
    }

    @Test
    @DisplayName("测试 DocMetadata 的相等性和哈希码")
    public void testDocMetadataEqualsAndHashCode() {
        // 创建两个相同内容的对象
        MetaDataListResponse.DocMetadata metadata1 = new MetaDataListResponse.DocMetadata();
        metadata1.setId("meta-123");
        metadata1.setType("text");
        metadata1.setName("文本字段");
        metadata1.setUserCount(10);

        MetaDataListResponse.DocMetadata metadata2 = new MetaDataListResponse.DocMetadata();
        metadata2.setId("meta-123");
        metadata2.setType("text");
        metadata2.setName("文本字段");
        metadata2.setUserCount(10);

        // 测试相等性
        assertEquals(metadata1, metadata2);
        assertEquals(metadata2, metadata1);

        // 测试哈希码
        assertEquals(metadata1.hashCode(), metadata2.hashCode());

        // 创建不同内容的对象
        MetaDataListResponse.DocMetadata metadata3 = new MetaDataListResponse.DocMetadata();
        metadata3.setId("meta-456");  // 不同ID
        metadata3.setType("text");
        metadata3.setName("文本字段");
        metadata3.setUserCount(10);

        // 测试不相等性
        assertNotEquals(metadata1, metadata3);
        assertNotEquals(metadata3, metadata1);
    }

    @Test
    @DisplayName("测试 MetaDataListResponse 的默认值")
    public void testDefaultValues() {
        // 创建新对象验证默认值
        MetaDataListResponse response = new MetaDataListResponse();

        // 验证默认值
        assertNull(response.getBuiltInFieldEnabled());
        assertNull(response.getDocMetadata());

        // 验证内部类默认值
        MetaDataListResponse.DocMetadata metadata = new MetaDataListResponse.DocMetadata();
        assertNull(metadata.getId());
        assertNull(metadata.getType());
        assertNull(metadata.getName());
        assertNull(metadata.getUserCount());
    }

    @Test
    @DisplayName("测试 MetaDataListResponse 的空元数据列表")
    public void testEmptyMetadataList() throws JsonProcessingException {
        // 创建带有空元数据列表的响应
        MetaDataListResponse response = new MetaDataListResponse();
        response.setBuiltInFieldEnabled(false);
        response.setDocMetadata(new ArrayList<>());

        // 序列化为JSON
        String json = objectMapper.writeValueAsString(response);

        // 验证JSON
        assertTrue(json.contains("\"builtInFieldEnabled\":false"));
        assertTrue(json.contains("\"docMetadata\":[]"));

        // 从JSON反序列化
        MetaDataListResponse deserializedResponse = objectMapper.readValue(json, MetaDataListResponse.class);

        // 验证反序列化后的对象
        assertEquals(Boolean.FALSE, deserializedResponse.getBuiltInFieldEnabled());
        assertNotNull(deserializedResponse.getDocMetadata());
        assertTrue(deserializedResponse.getDocMetadata().isEmpty());
    }

    @Test
    @DisplayName("测试 MetaDataListResponse 的toString方法")
    public void testToString() {
        // 创建测试对象
        MetaDataListResponse response = new MetaDataListResponse();
        response.setBuiltInFieldEnabled(true);

        List<MetaDataListResponse.DocMetadata> docMetadataList = new ArrayList<>();
        MetaDataListResponse.DocMetadata docMetadata = new MetaDataListResponse.DocMetadata();
        docMetadata.setId("meta-123");
        docMetadata.setType("text");
        docMetadata.setName("文本字段");
        docMetadataList.add(docMetadata);

        response.setDocMetadata(docMetadataList);

        // 获取toString结果
        String toStringResult = response.toString();

        // 验证toString包含重要字段
        assertTrue(toStringResult.contains("builtInFieldEnabled=true"));
        assertTrue(toStringResult.contains("docMetadata="));
        assertTrue(toStringResult.contains("id=meta-123"));
        assertTrue(toStringResult.contains("type=text"));
        assertTrue(toStringResult.contains("name=文本字段"));
    }
} 