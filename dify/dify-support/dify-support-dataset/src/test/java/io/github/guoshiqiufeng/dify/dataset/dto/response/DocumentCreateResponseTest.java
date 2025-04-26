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

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试 DocumentCreateResponse 类的功能
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/26 15:27
 */
public class DocumentCreateResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("测试 DocumentCreateResponse 基本属性的设置和获取")
    public void testBasicProperties() {
        // 创建测试对象
        DocumentCreateResponse response = new DocumentCreateResponse();

        // 创建文档信息对象
        DocumentInfo documentInfo = new DocumentInfo();
        documentInfo.setId("doc-123");
        documentInfo.setName("测试文档");
        documentInfo.setIndexingStatus("pending");

        // 设置属性
        response.setDocument(documentInfo);
        response.setBatch("batch-456");

        // 验证属性
        assertNotNull(response.getDocument());
        assertEquals("doc-123", response.getDocument().getId());
        assertEquals("测试文档", response.getDocument().getName());
        assertEquals("pending", response.getDocument().getIndexingStatus());
        assertEquals("batch-456", response.getBatch());
    }

    @Test
    @DisplayName("测试 DocumentCreateResponse 的JSON序列化和反序列化")
    public void testJsonSerializationDeserialization() throws JsonProcessingException {
        // 创建测试对象
        DocumentCreateResponse response = new DocumentCreateResponse();

        // 创建文档信息对象
        DocumentInfo documentInfo = new DocumentInfo();
        documentInfo.setId("doc-123");
        documentInfo.setName("测试文档");
        documentInfo.setIndexingStatus("pending");

        // 设置属性
        response.setDocument(documentInfo);
        response.setBatch("batch-456");

        // 序列化为JSON
        String json = objectMapper.writeValueAsString(response);

        // 验证JSON包含预期字段
        assertTrue(json.contains("\"document\":"));
        assertTrue(json.contains("\"id\":\"doc-123\""));
        assertTrue(json.contains("\"name\":\"测试文档\""));
        assertTrue(json.contains("\"indexingStatus\":\"pending\""));
        assertTrue(json.contains("\"batch\":\"batch-456\""));

        // 从JSON反序列化
        DocumentCreateResponse deserializedResponse = objectMapper.readValue(json, DocumentCreateResponse.class);

        // 验证反序列化对象
        assertNotNull(deserializedResponse.getDocument());
        assertEquals("doc-123", deserializedResponse.getDocument().getId());
        assertEquals("测试文档", deserializedResponse.getDocument().getName());
        assertEquals("pending", deserializedResponse.getDocument().getIndexingStatus());
        assertEquals("batch-456", deserializedResponse.getBatch());
    }

    @Test
    @DisplayName("测试 DocumentCreateResponse 使用下划线命名法的JSON反序列化")
    public void testSnakeCaseJsonDeserialization() throws JsonProcessingException {
        // 使用下划线命名法的JSON
        String snakeCaseJson = "{\n" +
                "  \"document\": {\n" +
                "    \"id\": \"doc-123\",\n" +
                "    \"name\": \"测试文档\",\n" +
                "    \"indexing_status\": \"pending\",\n" +
                "    \"created_by\": \"user1\",\n" +
                "    \"created_at\": 1615379400000\n" +
                "  },\n" +
                "  \"batch\": \"batch-456\"\n" +
                "}";

        // 从下划线命名法JSON反序列化
        DocumentCreateResponse deserializedResponse = objectMapper.readValue(snakeCaseJson, DocumentCreateResponse.class);

        // 验证下划线命名法字段是否正确映射
        assertNotNull(deserializedResponse.getDocument());
        assertEquals("doc-123", deserializedResponse.getDocument().getId());
        assertEquals("测试文档", deserializedResponse.getDocument().getName());
        assertEquals("pending", deserializedResponse.getDocument().getIndexingStatus());  // 验证indexing_status映射
        assertEquals("user1", deserializedResponse.getDocument().getCreatedBy());  // 验证created_by映射
        assertEquals(Long.valueOf(1615379400000L), deserializedResponse.getDocument().getCreatedAt());  // 验证created_at映射
        assertEquals("batch-456", deserializedResponse.getBatch());
    }

    @Test
    @DisplayName("测试 DocumentCreateResponse 的复杂文档信息")
    public void testComplexDocumentInfo() throws JsonProcessingException {
        // 创建测试对象
        DocumentCreateResponse response = new DocumentCreateResponse();

        // 创建复杂文档信息对象
        DocumentInfo documentInfo = new DocumentInfo();
        documentInfo.setId("doc-123");
        documentInfo.setPosition(1);
        documentInfo.setDataSourceType("file");
        Map<String, Object> dataSourceInfo = new HashMap<>();
        dataSourceInfo.put("filename", "test.pdf");
        dataSourceInfo.put("size", 1024);
        documentInfo.setDataSourceInfo(dataSourceInfo);
        documentInfo.setName("测试文档");
        documentInfo.setCreatedFrom("upload");
        documentInfo.setCreatedBy("user1");
        documentInfo.setCreatedAt(1615379400000L);
        documentInfo.setTokens(1500);
        documentInfo.setIndexingStatus("pending");

        // 设置属性
        response.setDocument(documentInfo);
        response.setBatch("batch-456");

        // 序列化为JSON
        String json = objectMapper.writeValueAsString(response);

        // 从JSON反序列化
        DocumentCreateResponse deserializedResponse = objectMapper.readValue(json, DocumentCreateResponse.class);

        // 验证复杂文档信息是否正确保留
        assertNotNull(deserializedResponse.getDocument());
        assertEquals("doc-123", deserializedResponse.getDocument().getId());
        assertEquals(Integer.valueOf(1), deserializedResponse.getDocument().getPosition());
        assertEquals("file", deserializedResponse.getDocument().getDataSourceType());
        assertNotNull(deserializedResponse.getDocument().getDataSourceInfo());
        assertEquals("test.pdf", deserializedResponse.getDocument().getDataSourceInfo().get("filename"));
        assertEquals(1024, deserializedResponse.getDocument().getDataSourceInfo().get("size"));
        assertEquals("测试文档", deserializedResponse.getDocument().getName());
        assertEquals("upload", deserializedResponse.getDocument().getCreatedFrom());
        assertEquals("user1", deserializedResponse.getDocument().getCreatedBy());
        assertEquals(Long.valueOf(1615379400000L), deserializedResponse.getDocument().getCreatedAt());
        assertEquals(Integer.valueOf(1500), deserializedResponse.getDocument().getTokens());
        assertEquals("pending", deserializedResponse.getDocument().getIndexingStatus());
        assertEquals("batch-456", deserializedResponse.getBatch());
    }

    @Test
    @DisplayName("测试 DocumentCreateResponse 的相等性和哈希码")
    public void testEqualsAndHashCode() {
        // 创建文档信息对象1
        DocumentInfo documentInfo1 = new DocumentInfo();
        documentInfo1.setId("doc-123");
        documentInfo1.setName("测试文档");

        // 创建文档信息对象2（与对象1相同内容）
        DocumentInfo documentInfo2 = new DocumentInfo();
        documentInfo2.setId("doc-123");
        documentInfo2.setName("测试文档");

        // 创建两个相同内容的响应对象
        DocumentCreateResponse response1 = new DocumentCreateResponse();
        response1.setDocument(documentInfo1);
        response1.setBatch("batch-456");

        DocumentCreateResponse response2 = new DocumentCreateResponse();
        response2.setDocument(documentInfo2);
        response2.setBatch("batch-456");

        // 测试相等性
        assertEquals(response1, response2);
        assertEquals(response2, response1);

        // 测试哈希码
        assertEquals(response1.hashCode(), response2.hashCode());

        // 创建不同内容的响应对象
        DocumentCreateResponse response3 = new DocumentCreateResponse();
        response3.setDocument(documentInfo1);
        response3.setBatch("batch-789");  // 不同批次

        // 测试不相等性
        assertNotEquals(response1, response3);
        assertNotEquals(response3, response1);
    }

    @Test
    @DisplayName("测试 DocumentCreateResponse 的默认值")
    public void testDefaultValues() {
        // 创建新对象验证默认值
        DocumentCreateResponse response = new DocumentCreateResponse();

        // 验证默认值
        assertNull(response.getDocument());
        assertNull(response.getBatch());
    }

    @Test
    @DisplayName("测试 DocumentCreateResponse 的toString方法")
    public void testToString() {
        // 创建测试对象
        DocumentCreateResponse response = new DocumentCreateResponse();

        // 创建文档信息对象
        DocumentInfo documentInfo = new DocumentInfo();
        documentInfo.setId("doc-123");
        documentInfo.setName("测试文档");

        // 设置属性
        response.setDocument(documentInfo);
        response.setBatch("batch-456");

        // 获取toString结果
        String toStringResult = response.toString();

        // 验证toString包含重要字段
        assertTrue(toStringResult.contains("document="));
        assertTrue(toStringResult.contains("id=doc-123"));
        assertTrue(toStringResult.contains("name=测试文档"));
        assertTrue(toStringResult.contains("batch=batch-456"));
    }
} 