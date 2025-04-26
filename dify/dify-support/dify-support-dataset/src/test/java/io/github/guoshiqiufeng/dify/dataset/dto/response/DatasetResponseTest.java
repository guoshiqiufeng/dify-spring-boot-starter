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
import io.github.guoshiqiufeng.dify.dataset.enums.IndexingTechniqueEnum;
import io.github.guoshiqiufeng.dify.dataset.enums.PermissionEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试 DatasetResponse 类的功能
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/26 13:15
 */
public class DatasetResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("测试 DatasetResponse 基本属性的设置和获取")
    public void testBasicProperties() {
        // 创建测试对象
        DatasetResponse response = new DatasetResponse();

        // 设置基本属性
        String id = "dataset-123";
        String name = "测试数据集";
        String description = "这是一个测试数据集";
        PermissionEnum permission = PermissionEnum.ONLY_ME;
        String dataSourceType = "file";
        IndexingTechniqueEnum indexingTechnique = IndexingTechniqueEnum.HIGH_QUALITY;
        Integer appCount = 5;
        Integer documentCount = 10;
        Integer wordCount = 1000;
        String createdBy = "user-789";
        Long createdAt = 1615379400000L;
        String updatedBy = "user-789";
        Long updatedAt = 1615379450000L;

        // 设置属性
        response.setId(id);
        response.setName(name);
        response.setDescription(description);
        response.setPermission(permission);
        response.setDataSourceType(dataSourceType);
        response.setIndexingTechnique(indexingTechnique);
        response.setAppCount(appCount);
        response.setDocumentCount(documentCount);
        response.setWordCount(wordCount);
        response.setCreatedBy(createdBy);
        response.setCreatedAt(createdAt);
        response.setUpdatedBy(updatedBy);
        response.setUpdatedAt(updatedAt);

        // 验证属性
        assertEquals(id, response.getId());
        assertEquals(name, response.getName());
        assertEquals(description, response.getDescription());
        assertEquals(permission, response.getPermission());
        assertEquals(dataSourceType, response.getDataSourceType());
        assertEquals(indexingTechnique, response.getIndexingTechnique());
        assertEquals(appCount, response.getAppCount());
        assertEquals(documentCount, response.getDocumentCount());
        assertEquals(wordCount, response.getWordCount());
        assertEquals(createdBy, response.getCreatedBy());
        assertEquals(createdAt, response.getCreatedAt());
        assertEquals(updatedBy, response.getUpdatedBy());
        assertEquals(updatedAt, response.getUpdatedAt());
    }

    @Test
    @DisplayName("测试 DatasetResponse 的JSON序列化和反序列化")
    public void testJsonSerializationDeserialization() throws JsonProcessingException {
        // 创建测试对象
        DatasetResponse response = new DatasetResponse();
        response.setId("dataset-123");
        response.setName("测试数据集");
        response.setDescription("这是一个测试数据集");
        response.setPermission(PermissionEnum.ONLY_ME);
        response.setDataSourceType("file");
        response.setIndexingTechnique(IndexingTechniqueEnum.HIGH_QUALITY);
        response.setAppCount(5);
        response.setDocumentCount(10);
        response.setWordCount(1000);
        response.setCreatedBy("user-789");
        response.setCreatedAt(1615379400000L);

        // 序列化为JSON
        String json = objectMapper.writeValueAsString(response);

        // 验证JSON包含预期字段
        assertTrue(json.contains("\"id\":\"dataset-123\""));
        assertTrue(json.contains("\"name\":\"测试数据集\""));
        assertTrue(json.contains("\"description\":\"这是一个测试数据集\""));
        assertTrue(json.contains("\"permission\":\"only_me\""));
        assertTrue(json.contains("\"dataSourceType\":\"file\""));
        assertTrue(json.contains("\"indexingTechnique\":\"high_quality\""));
        assertTrue(json.contains("\"appCount\":5"));
        assertTrue(json.contains("\"documentCount\":10"));
        assertTrue(json.contains("\"wordCount\":1000"));
        assertTrue(json.contains("\"createdBy\":\"user-789\""));
        assertTrue(json.contains("\"createdAt\":1615379400000"));

        // 从JSON反序列化
        DatasetResponse deserializedResponse = objectMapper.readValue(json, DatasetResponse.class);

        // 验证反序列化对象
        assertEquals("dataset-123", deserializedResponse.getId());
        assertEquals("测试数据集", deserializedResponse.getName());
        assertEquals("这是一个测试数据集", deserializedResponse.getDescription());
        assertEquals(PermissionEnum.ONLY_ME, deserializedResponse.getPermission());
        assertEquals("file", deserializedResponse.getDataSourceType());
        assertEquals(IndexingTechniqueEnum.HIGH_QUALITY, deserializedResponse.getIndexingTechnique());
        assertEquals(Integer.valueOf(5), deserializedResponse.getAppCount());
        assertEquals(Integer.valueOf(10), deserializedResponse.getDocumentCount());
        assertEquals(Integer.valueOf(1000), deserializedResponse.getWordCount());
        assertEquals("user-789", deserializedResponse.getCreatedBy());
        assertEquals(Long.valueOf(1615379400000L), deserializedResponse.getCreatedAt());
    }

    @Test
    @DisplayName("测试 DatasetResponse 使用蛇形命名法的JSON反序列化")
    public void testSnakeCaseJsonDeserialization() throws JsonProcessingException {
        // 使用蛇形命名法的JSON
        String snakeCaseJson = "{\n" +
                "  \"id\": \"dataset-123\",\n" +
                "  \"name\": \"测试数据集\",\n" +
                "  \"description\": \"这是一个测试数据集\",\n" +
                "  \"permission\": \"only_me\",\n" +
                "  \"data_source_type\": \"file\",\n" +
                "  \"indexing_technique\": \"high_quality\",\n" +
                "  \"app_count\": 5,\n" +
                "  \"document_count\": 10,\n" +
                "  \"word_count\": 1000,\n" +
                "  \"created_by\": \"user-789\",\n" +
                "  \"created_at\": 1615379400000,\n" +
                "  \"updated_by\": \"user-789\",\n" +
                "  \"updated_at\": 1615379450000\n" +
                "}";

        // 从蛇形命名法JSON反序列化
        DatasetResponse deserializedResponse = objectMapper.readValue(snakeCaseJson, DatasetResponse.class);

        // 验证蛇形命名法字段是否正确映射
        assertEquals("dataset-123", deserializedResponse.getId());
        assertEquals("测试数据集", deserializedResponse.getName());
        assertEquals("这是一个测试数据集", deserializedResponse.getDescription());
        assertEquals(PermissionEnum.ONLY_ME, deserializedResponse.getPermission());
        assertEquals("file", deserializedResponse.getDataSourceType());
        assertEquals(IndexingTechniqueEnum.HIGH_QUALITY, deserializedResponse.getIndexingTechnique());
        assertEquals(Integer.valueOf(5), deserializedResponse.getAppCount());
        assertEquals(Integer.valueOf(10), deserializedResponse.getDocumentCount());
        assertEquals(Integer.valueOf(1000), deserializedResponse.getWordCount());
        assertEquals("user-789", deserializedResponse.getCreatedBy());
        assertEquals(Long.valueOf(1615379400000L), deserializedResponse.getCreatedAt());
        assertEquals("user-789", deserializedResponse.getUpdatedBy());
        assertEquals(Long.valueOf(1615379450000L), deserializedResponse.getUpdatedAt());
    }

    @Test
    @DisplayName("测试 DatasetResponse 的相等性和哈希码")
    public void testEqualsAndHashCode() {
        // 创建两个相同内容的对象
        DatasetResponse response1 = new DatasetResponse();
        response1.setId("dataset-123");
        response1.setName("测试数据集");
        response1.setPermission(PermissionEnum.ONLY_ME);

        DatasetResponse response2 = new DatasetResponse();
        response2.setId("dataset-123");
        response2.setName("测试数据集");
        response2.setPermission(PermissionEnum.ONLY_ME);

        // 测试相等性
        assertEquals(response1, response2);
        assertEquals(response2, response1);

        // 测试哈希码
        assertEquals(response1.hashCode(), response2.hashCode());

        // 创建不同内容的对象
        DatasetResponse response3 = new DatasetResponse();
        response3.setId("dataset-456"); // 不同ID
        response3.setName("测试数据集");
        response3.setPermission(PermissionEnum.ONLY_ME);

        // 测试不相等性
        assertNotEquals(response1, response3);
        assertNotEquals(response3, response1);
    }

    @Test
    @DisplayName("测试 DatasetResponse 的默认值")
    public void testDefaultValues() {
        // 创建新对象验证默认值
        DatasetResponse response = new DatasetResponse();

        // 所有字段默认值应为null
        assertNull(response.getId());
        assertNull(response.getName());
        assertNull(response.getDescription());
        assertNull(response.getPermission());
        assertNull(response.getDataSourceType());
        assertNull(response.getIndexingTechnique());
        assertNull(response.getAppCount());
        assertNull(response.getDocumentCount());
        assertNull(response.getWordCount());
        assertNull(response.getCreatedBy());
        assertNull(response.getCreatedAt());
        assertNull(response.getUpdatedBy());
        assertNull(response.getUpdatedAt());
    }

    @Test
    @DisplayName("测试 DatasetResponse 的枚举值处理")
    public void testEnumValues() throws JsonProcessingException {
        // 创建带有枚举值的对象
        DatasetResponse response = new DatasetResponse();
        response.setPermission(PermissionEnum.ONLY_ME);
        response.setIndexingTechnique(IndexingTechniqueEnum.HIGH_QUALITY);

        // 序列化为JSON
        String json = objectMapper.writeValueAsString(response);

        // 验证JSON包含枚举名称
        assertTrue(json.contains("\"permission\":\"only_me\""));
        assertTrue(json.contains("\"indexingTechnique\":\"high_quality\""));

        // 从JSON反序列化
        DatasetResponse deserializedResponse = objectMapper.readValue(json, DatasetResponse.class);

        // 验证枚举值
        assertEquals(PermissionEnum.ONLY_ME, deserializedResponse.getPermission());
        assertEquals(IndexingTechniqueEnum.HIGH_QUALITY, deserializedResponse.getIndexingTechnique());
    }

    @Test
    @DisplayName("测试 DatasetResponse 的toString方法")
    public void testToString() {
        // 创建测试对象
        DatasetResponse response = new DatasetResponse();
        response.setId("dataset-123");
        response.setName("测试数据集");
        response.setPermission(PermissionEnum.ONLY_ME);

        // 获取toString结果
        String toStringResult = response.toString();

        // 验证toString包含重要字段
        assertTrue(toStringResult.contains("id=dataset-123"));
        assertTrue(toStringResult.contains("name=测试数据集"));
        assertTrue(toStringResult.contains("permission=ONLY_ME"));
    }
} 