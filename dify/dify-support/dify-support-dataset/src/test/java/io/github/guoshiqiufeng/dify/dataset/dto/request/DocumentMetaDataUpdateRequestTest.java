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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试 DocumentMetaDataUpdateRequest 类的功能
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/26 10:43
 */
public class DocumentMetaDataUpdateRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("测试 DocumentMetaDataUpdateRequest 基本属性的设置和获取")
    public void testBasicProperties() {
        // 创建测试对象
        DocumentMetaDataUpdateRequest request = new DocumentMetaDataUpdateRequest();

        // 设置属性
        request.setApiKey("test-api-key");
        request.setDatasetId("dataset-123");

        List<DocumentMetaDataUpdateRequest.OperationData> operationDataList = new ArrayList<>();
        DocumentMetaDataUpdateRequest.OperationData operationData = new DocumentMetaDataUpdateRequest.OperationData();
        operationData.setDocumentId("doc-456");
        operationData.setMetadataList(Arrays.asList(
                MetaData.builder().id("meta-1").type("text").name("title").value("Sample Title").build(),
                MetaData.builder().id("meta-2").type("number").name("priority").value("5").build()
        ));
        operationDataList.add(operationData);
        request.setOperationData(operationDataList);

        // 验证属性
        assertEquals("test-api-key", request.getApiKey());
        assertEquals("dataset-123", request.getDatasetId());
        assertNotNull(request.getOperationData());
        assertEquals(1, request.getOperationData().size());

        DocumentMetaDataUpdateRequest.OperationData retrievedOpData = request.getOperationData().get(0);
        assertEquals("doc-456", retrievedOpData.getDocumentId());
        assertEquals(2, retrievedOpData.getMetadataList().size());
        assertEquals("meta-1", retrievedOpData.getMetadataList().get(0).getId());
        assertEquals("text", retrievedOpData.getMetadataList().get(0).getType());
        assertEquals("title", retrievedOpData.getMetadataList().get(0).getName());
        assertEquals("Sample Title", retrievedOpData.getMetadataList().get(0).getValue());
        assertEquals("meta-2", retrievedOpData.getMetadataList().get(1).getId());
        assertEquals("number", retrievedOpData.getMetadataList().get(1).getType());
        assertEquals("priority", retrievedOpData.getMetadataList().get(1).getName());
        assertEquals("5", retrievedOpData.getMetadataList().get(1).getValue());
    }

    @Test
    @DisplayName("测试 DocumentMetaDataUpdateRequest 的JSON序列化和反序列化")
    public void testJsonSerializationDeserialization() throws JsonProcessingException {
        // 创建测试对象
        DocumentMetaDataUpdateRequest request = new DocumentMetaDataUpdateRequest();
        request.setApiKey("test-api-key");
        request.setDatasetId("dataset-123");

        DocumentMetaDataUpdateRequest.OperationData operationData = new DocumentMetaDataUpdateRequest.OperationData();
        operationData.setDocumentId("doc-456");
        operationData.setMetadataList(Arrays.asList(
                MetaData.builder().id("meta-1").type("text").name("title").value("Sample Title").build()
        ));
        request.setOperationData(Arrays.asList(operationData));

        // 序列化为JSON
        String json = objectMapper.writeValueAsString(request);

        // 验证JSON包含预期字段
        assertTrue(json.contains("\"apiKey\":\"test-api-key\""));
        assertTrue(json.contains("\"datasetId\":\"dataset-123\""));
        assertTrue(json.contains("\"operation_data\""));
        assertTrue(json.contains("\"document_id\":\"doc-456\""));
        assertTrue(json.contains("\"metadata_list\""));
        assertTrue(json.contains("\"id\":\"meta-1\""));
        assertTrue(json.contains("\"type\":\"text\""));
        assertTrue(json.contains("\"name\":\"title\""));
        assertTrue(json.contains("\"value\":\"Sample Title\""));

        // 从JSON反序列化
        DocumentMetaDataUpdateRequest deserializedRequest = objectMapper.readValue(json, DocumentMetaDataUpdateRequest.class);

        // 验证反序列化对象
        assertEquals("test-api-key", deserializedRequest.getApiKey());
        assertEquals("dataset-123", deserializedRequest.getDatasetId());
        assertNotNull(deserializedRequest.getOperationData());
        assertEquals(1, deserializedRequest.getOperationData().size());

        DocumentMetaDataUpdateRequest.OperationData deserializedOpData = deserializedRequest.getOperationData().get(0);
        assertEquals("doc-456", deserializedOpData.getDocumentId());
        assertEquals(1, deserializedOpData.getMetadataList().size());
        assertEquals("meta-1", deserializedOpData.getMetadataList().get(0).getId());
        assertEquals("text", deserializedOpData.getMetadataList().get(0).getType());
        assertEquals("title", deserializedOpData.getMetadataList().get(0).getName());
        assertEquals("Sample Title", deserializedOpData.getMetadataList().get(0).getValue());
    }

    @Test
    @DisplayName("测试 JSON 别名属性 - 驼峰命名风格")
    public void testJsonAliasCamelCase() throws JsonProcessingException {
        // 使用驼峰命名风格的JSON
        String camelCaseJson = "{" +
                "\"apiKey\":\"test-api-key\"," +
                "\"datasetId\":\"dataset-123\"," +
                "\"operationData\":[{" +
                "\"documentId\":\"doc-456\"," +
                "\"metadataList\":[{" +
                "\"id\":\"meta-1\"," +
                "\"type\":\"text\"," +
                "\"name\":\"title\"," +
                "\"value\":\"Sample Title\"" +
                "}]" +
                "}]" +
                "}";

        // 从JSON反序列化
        DocumentMetaDataUpdateRequest deserializedRequest = objectMapper.readValue(camelCaseJson, DocumentMetaDataUpdateRequest.class);

        // 验证反序列化对象
        assertEquals("test-api-key", deserializedRequest.getApiKey());
        assertEquals("dataset-123", deserializedRequest.getDatasetId());
        assertNotNull(deserializedRequest.getOperationData());
        assertEquals(1, deserializedRequest.getOperationData().size());

        DocumentMetaDataUpdateRequest.OperationData deserializedOpData = deserializedRequest.getOperationData().get(0);
        assertEquals("doc-456", deserializedOpData.getDocumentId());
        assertEquals(1, deserializedOpData.getMetadataList().size());
        assertEquals("meta-1", deserializedOpData.getMetadataList().get(0).getId());
        assertEquals("text", deserializedOpData.getMetadataList().get(0).getType());
        assertEquals("title", deserializedOpData.getMetadataList().get(0).getName());
        assertEquals("Sample Title", deserializedOpData.getMetadataList().get(0).getValue());
    }

    @Test
    @DisplayName("测试 JSON 属性名 - 下划线命名风格")
    public void testJsonPropertySnakeCase() throws JsonProcessingException {
        // 使用下划线命名风格的JSON
        String snakeCaseJson = "{" +
                "\"apiKey\":\"test-api-key\"," +
                "\"datasetId\":\"dataset-123\"," +
                "\"operation_data\":[{" +
                "\"document_id\":\"doc-456\"," +
                "\"metadata_list\":[{" +
                "\"id\":\"meta-1\"," +
                "\"type\":\"text\"," +
                "\"name\":\"title\"," +
                "\"value\":\"Sample Title\"" +
                "}]" +
                "}]" +
                "}";

        // 从JSON反序列化
        DocumentMetaDataUpdateRequest deserializedRequest = objectMapper.readValue(snakeCaseJson, DocumentMetaDataUpdateRequest.class);

        // 验证反序列化对象
        assertEquals("test-api-key", deserializedRequest.getApiKey());
        assertEquals("dataset-123", deserializedRequest.getDatasetId());
        assertNotNull(deserializedRequest.getOperationData());
        assertEquals(1, deserializedRequest.getOperationData().size());

        DocumentMetaDataUpdateRequest.OperationData deserializedOpData = deserializedRequest.getOperationData().get(0);
        assertEquals("doc-456", deserializedOpData.getDocumentId());
        assertEquals(1, deserializedOpData.getMetadataList().size());
        assertEquals("meta-1", deserializedOpData.getMetadataList().get(0).getId());
        assertEquals("text", deserializedOpData.getMetadataList().get(0).getType());
        assertEquals("title", deserializedOpData.getMetadataList().get(0).getName());
        assertEquals("Sample Title", deserializedOpData.getMetadataList().get(0).getValue());
    }

    @Test
    @DisplayName("测试 DocumentMetaDataUpdateRequest 的多个操作数据")
    public void testMultipleOperationData() {
        // 创建测试对象
        DocumentMetaDataUpdateRequest request = new DocumentMetaDataUpdateRequest();
        request.setApiKey("test-api-key");
        request.setDatasetId("dataset-123");

        List<DocumentMetaDataUpdateRequest.OperationData> operationDataList = new ArrayList<>();

        // 第一个操作数据
        DocumentMetaDataUpdateRequest.OperationData opData1 = new DocumentMetaDataUpdateRequest.OperationData();
        opData1.setDocumentId("doc-456");
        opData1.setMetadataList(Arrays.asList(
                MetaData.builder().id("meta-1").type("text").name("title").value("Document 1").build()
        ));
        operationDataList.add(opData1);

        // 第二个操作数据
        DocumentMetaDataUpdateRequest.OperationData opData2 = new DocumentMetaDataUpdateRequest.OperationData();
        opData2.setDocumentId("doc-789");
        opData2.setMetadataList(Arrays.asList(
                MetaData.builder().id("meta-2").type("category").name("category").value("Technical").build(),
                MetaData.builder().id("meta-3").type("boolean").name("published").value("true").build()
        ));
        operationDataList.add(opData2);

        request.setOperationData(operationDataList);

        // 验证属性
        assertEquals(2, request.getOperationData().size());

        // 验证第一个操作数据
        DocumentMetaDataUpdateRequest.OperationData retrievedOpData1 = request.getOperationData().get(0);
        assertEquals("doc-456", retrievedOpData1.getDocumentId());
        assertEquals(1, retrievedOpData1.getMetadataList().size());
        assertEquals("meta-1", retrievedOpData1.getMetadataList().get(0).getId());
        assertEquals("Document 1", retrievedOpData1.getMetadataList().get(0).getValue());

        // 验证第二个操作数据
        DocumentMetaDataUpdateRequest.OperationData retrievedOpData2 = request.getOperationData().get(1);
        assertEquals("doc-789", retrievedOpData2.getDocumentId());
        assertEquals(2, retrievedOpData2.getMetadataList().size());
        assertEquals("meta-2", retrievedOpData2.getMetadataList().get(0).getId());
        assertEquals("category", retrievedOpData2.getMetadataList().get(0).getName());
        assertEquals("Technical", retrievedOpData2.getMetadataList().get(0).getValue());
        assertEquals("meta-3", retrievedOpData2.getMetadataList().get(1).getId());
        assertEquals("boolean", retrievedOpData2.getMetadataList().get(1).getType());
        assertEquals("published", retrievedOpData2.getMetadataList().get(1).getName());
        assertEquals("true", retrievedOpData2.getMetadataList().get(1).getValue());
    }

    @Test
    @DisplayName("测试 OperationData 内部类的相等性")
    public void testOperationDataEquality() {
        // 创建两个相同内容的操作数据对象
        DocumentMetaDataUpdateRequest.OperationData opData1 = new DocumentMetaDataUpdateRequest.OperationData();
        opData1.setDocumentId("doc-123");
        opData1.setMetadataList(Arrays.asList(
                MetaData.builder().id("meta-1").type("text").name("title").value("Test Document").build()
        ));

        DocumentMetaDataUpdateRequest.OperationData opData2 = new DocumentMetaDataUpdateRequest.OperationData();
        opData2.setDocumentId("doc-123");
        opData2.setMetadataList(Arrays.asList(
                MetaData.builder().id("meta-1").type("text").name("title").value("Test Document").build()
        ));

        // 创建不同内容的操作数据对象
        DocumentMetaDataUpdateRequest.OperationData opData3 = new DocumentMetaDataUpdateRequest.OperationData();
        opData3.setDocumentId("doc-456");  // 不同的ID
        opData3.setMetadataList(Arrays.asList(
                MetaData.builder().id("meta-1").type("text").name("title").value("Test Document").build()
        ));

        // 测试相等性
        assertEquals(opData1, opData2);
        assertEquals(opData2, opData1);
        assertNotEquals(opData1, opData3);
        assertNotEquals(opData3, opData1);

        // 测试哈希码
        assertEquals(opData1.hashCode(), opData2.hashCode());
    }

    @Test
    @DisplayName("测试 DocumentMetaDataUpdateRequest 的默认值")
    public void testDefaultValues() {
        // 创建新对象验证默认值
        DocumentMetaDataUpdateRequest request = new DocumentMetaDataUpdateRequest();

        // 验证默认值
        assertNull(request.getApiKey());
        assertNull(request.getDatasetId());
        assertNull(request.getOperationData());

        // 创建内部类对象验证默认值
        DocumentMetaDataUpdateRequest.OperationData operationData = new DocumentMetaDataUpdateRequest.OperationData();

        // 验证内部类默认值
        assertNull(operationData.getDocumentId());
        assertNull(operationData.getMetadataList());
    }
}
