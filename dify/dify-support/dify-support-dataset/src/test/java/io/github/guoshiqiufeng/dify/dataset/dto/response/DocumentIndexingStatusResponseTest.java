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
 * 测试 DocumentIndexingStatusResponse 类的功能
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/26 14:02
 */
public class DocumentIndexingStatusResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("测试 DocumentIndexingStatusResponse 基本属性的设置和获取")
    public void testBasicProperties() {
        // 创建测试对象
        DocumentIndexingStatusResponse response = new DocumentIndexingStatusResponse();

        // 创建处理状态列表
        List<DocumentIndexingStatusResponse.ProcessingStatus> statusList = new ArrayList<>();

        // 创建第一个状态
        DocumentIndexingStatusResponse.ProcessingStatus status1 = new DocumentIndexingStatusResponse.ProcessingStatus();
        status1.setId("doc-123");
        status1.setIndexingStatus("processing");
        status1.setProcessingStartedAt(1615379400000L);
        status1.setParsingCompletedAt(1615379410000L);
        status1.setCleaningCompletedAt(1615379420000L);
        status1.setSplittingCompletedAt(1615379430000L);
        status1.setCompletedAt(null);  // 未完成
        status1.setPausedAt(null);
        status1.setError(null);
        status1.setStoppedAt(null);
        status1.setCompletedSegments(80);
        status1.setTotalSegments(100);

        // 创建第二个状态
        DocumentIndexingStatusResponse.ProcessingStatus status2 = new DocumentIndexingStatusResponse.ProcessingStatus();
        status2.setId("doc-456");
        status2.setIndexingStatus("completed");
        status2.setProcessingStartedAt(1615379500000L);
        status2.setParsingCompletedAt(1615379510000L);
        status2.setCleaningCompletedAt(1615379520000L);
        status2.setSplittingCompletedAt(1615379530000L);
        status2.setCompletedAt(1615379540000L);
        status2.setPausedAt(null);
        status2.setError(null);
        status2.setStoppedAt(null);
        status2.setCompletedSegments(50);
        status2.setTotalSegments(50);

        statusList.add(status1);
        statusList.add(status2);

        // 设置响应数据
        response.setData(statusList);

        // 验证属性
        assertEquals(statusList, response.getData());
        assertEquals(2, response.getData().size());

        // 验证第一个状态
        DocumentIndexingStatusResponse.ProcessingStatus retrievedStatus1 = response.getData().get(0);
        assertEquals("doc-123", retrievedStatus1.getId());
        assertEquals("processing", retrievedStatus1.getIndexingStatus());
        assertEquals(Long.valueOf(1615379400000L), retrievedStatus1.getProcessingStartedAt());
        assertEquals(Long.valueOf(1615379410000L), retrievedStatus1.getParsingCompletedAt());
        assertEquals(Long.valueOf(1615379420000L), retrievedStatus1.getCleaningCompletedAt());
        assertEquals(Long.valueOf(1615379430000L), retrievedStatus1.getSplittingCompletedAt());
        assertNull(retrievedStatus1.getCompletedAt());
        assertNull(retrievedStatus1.getPausedAt());
        assertNull(retrievedStatus1.getError());
        assertNull(retrievedStatus1.getStoppedAt());
        assertEquals(Integer.valueOf(80), retrievedStatus1.getCompletedSegments());
        assertEquals(Integer.valueOf(100), retrievedStatus1.getTotalSegments());

        // 验证第二个状态
        DocumentIndexingStatusResponse.ProcessingStatus retrievedStatus2 = response.getData().get(1);
        assertEquals("doc-456", retrievedStatus2.getId());
        assertEquals("completed", retrievedStatus2.getIndexingStatus());
        assertEquals(Long.valueOf(1615379500000L), retrievedStatus2.getProcessingStartedAt());
        assertEquals(Long.valueOf(1615379510000L), retrievedStatus2.getParsingCompletedAt());
        assertEquals(Long.valueOf(1615379520000L), retrievedStatus2.getCleaningCompletedAt());
        assertEquals(Long.valueOf(1615379530000L), retrievedStatus2.getSplittingCompletedAt());
        assertEquals(Long.valueOf(1615379540000L), retrievedStatus2.getCompletedAt());
        assertNull(retrievedStatus2.getPausedAt());
        assertNull(retrievedStatus2.getError());
        assertNull(retrievedStatus2.getStoppedAt());
        assertEquals(Integer.valueOf(50), retrievedStatus2.getCompletedSegments());
        assertEquals(Integer.valueOf(50), retrievedStatus2.getTotalSegments());
    }

    @Test
    @DisplayName("测试 DocumentIndexingStatusResponse 的JSON序列化和反序列化")
    public void testJsonSerializationDeserialization() throws JsonProcessingException {
        // 创建测试对象
        DocumentIndexingStatusResponse response = new DocumentIndexingStatusResponse();

        // 创建处理状态列表
        List<DocumentIndexingStatusResponse.ProcessingStatus> statusList = new ArrayList<>();

        // 创建状态对象
        DocumentIndexingStatusResponse.ProcessingStatus status = new DocumentIndexingStatusResponse.ProcessingStatus();
        status.setId("doc-123");
        status.setIndexingStatus("processing");
        status.setProcessingStartedAt(1615379400000L);
        status.setCompletedSegments(80);
        status.setTotalSegments(100);

        statusList.add(status);
        response.setData(statusList);

        // 序列化为JSON
        String json = objectMapper.writeValueAsString(response);

        // 验证JSON包含预期字段
        assertTrue(json.contains("\"data\":"));
        assertTrue(json.contains("\"id\":\"doc-123\""));
        assertTrue(json.contains("\"indexingStatus\":\"processing\""));
        assertTrue(json.contains("\"processingStartedAt\":1615379400000"));
        assertTrue(json.contains("\"completedSegments\":80"));
        assertTrue(json.contains("\"totalSegments\":100"));

        // 从JSON反序列化
        DocumentIndexingStatusResponse deserializedResponse = objectMapper.readValue(json, DocumentIndexingStatusResponse.class);

        // 验证反序列化对象
        assertNotNull(deserializedResponse.getData());
        assertEquals(1, deserializedResponse.getData().size());

        DocumentIndexingStatusResponse.ProcessingStatus deserializedStatus = deserializedResponse.getData().get(0);
        assertEquals("doc-123", deserializedStatus.getId());
        assertEquals("processing", deserializedStatus.getIndexingStatus());
        assertEquals(Long.valueOf(1615379400000L), deserializedStatus.getProcessingStartedAt());
        assertEquals(Integer.valueOf(80), deserializedStatus.getCompletedSegments());
        assertEquals(Integer.valueOf(100), deserializedStatus.getTotalSegments());
    }

    @Test
    @DisplayName("测试 DocumentIndexingStatusResponse 使用蛇形命名法的JSON反序列化")
    public void testSnakeCaseJsonDeserialization() throws JsonProcessingException {
        // 使用蛇形命名法的JSON
        String snakeCaseJson = "{\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"id\": \"doc-123\",\n" +
                "      \"indexing_status\": \"processing\",\n" +
                "      \"processing_started_at\": 1615379400000,\n" +
                "      \"parsing_completed_at\": 1615379410000,\n" +
                "      \"cleaning_completed_at\": 1615379420000,\n" +
                "      \"splitting_completed_at\": 1615379430000,\n" +
                "      \"completed_segments\": 80,\n" +
                "      \"total_segments\": 100\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"doc-456\",\n" +
                "      \"indexing_status\": \"completed\",\n" +
                "      \"processing_started_at\": 1615379500000,\n" +
                "      \"completed_at\": 1615379540000,\n" +
                "      \"error\": null,\n" +
                "      \"completed_segments\": 50,\n" +
                "      \"total_segments\": 50\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        // 从蛇形命名法JSON反序列化
        DocumentIndexingStatusResponse deserializedResponse = objectMapper.readValue(snakeCaseJson, DocumentIndexingStatusResponse.class);

        // 验证蛇形命名法字段是否正确映射
        assertNotNull(deserializedResponse.getData());
        assertEquals(2, deserializedResponse.getData().size());

        // 验证第一个状态
        DocumentIndexingStatusResponse.ProcessingStatus status1 = deserializedResponse.getData().get(0);
        assertEquals("doc-123", status1.getId());
        assertEquals("processing", status1.getIndexingStatus());  // 验证indexing_status映射
        assertEquals(Long.valueOf(1615379400000L), status1.getProcessingStartedAt());  // 验证processing_started_at映射
        assertEquals(Long.valueOf(1615379410000L), status1.getParsingCompletedAt());  // 验证parsing_completed_at映射
        assertEquals(Long.valueOf(1615379420000L), status1.getCleaningCompletedAt());  // 验证cleaning_completed_at映射
        assertEquals(Long.valueOf(1615379430000L), status1.getSplittingCompletedAt());  // 验证splitting_completed_at映射
        assertEquals(Integer.valueOf(80), status1.getCompletedSegments());  // 验证completed_segments映射
        assertEquals(Integer.valueOf(100), status1.getTotalSegments());  // 验证total_segments映射

        // 验证第二个状态
        DocumentIndexingStatusResponse.ProcessingStatus status2 = deserializedResponse.getData().get(1);
        assertEquals("doc-456", status2.getId());
        assertEquals("completed", status2.getIndexingStatus());
        assertEquals(Long.valueOf(1615379500000L), status2.getProcessingStartedAt());
        assertEquals(Long.valueOf(1615379540000L), status2.getCompletedAt());  // 验证completed_at映射
        assertNull(status2.getError());
        assertEquals(Integer.valueOf(50), status2.getCompletedSegments());
        assertEquals(Integer.valueOf(50), status2.getTotalSegments());
    }

    @Test
    @DisplayName("测试 DocumentIndexingStatusResponse.ProcessingStatus 的相等性和哈希码")
    public void testProcessingStatusEqualsAndHashCode() {
        // 创建两个相同内容的状态对象
        DocumentIndexingStatusResponse.ProcessingStatus status1 = new DocumentIndexingStatusResponse.ProcessingStatus();
        status1.setId("doc-123");
        status1.setIndexingStatus("processing");
        status1.setCompletedSegments(80);
        status1.setTotalSegments(100);

        DocumentIndexingStatusResponse.ProcessingStatus status2 = new DocumentIndexingStatusResponse.ProcessingStatus();
        status2.setId("doc-123");
        status2.setIndexingStatus("processing");
        status2.setCompletedSegments(80);
        status2.setTotalSegments(100);

        // 测试相等性
        assertEquals(status1, status2);
        assertEquals(status2, status1);

        // 测试哈希码
        assertEquals(status1.hashCode(), status2.hashCode());

        // 创建不同内容的状态对象
        DocumentIndexingStatusResponse.ProcessingStatus status3 = new DocumentIndexingStatusResponse.ProcessingStatus();
        status3.setId("doc-456");  // 不同ID
        status3.setIndexingStatus("processing");
        status3.setCompletedSegments(80);
        status3.setTotalSegments(100);

        // 测试不相等性
        assertNotEquals(status1, status3);
        assertNotEquals(status3, status1);
    }

    @Test
    @DisplayName("测试 DocumentIndexingStatusResponse 的默认值")
    public void testDefaultValues() {
        // 创建新对象验证默认值
        DocumentIndexingStatusResponse response = new DocumentIndexingStatusResponse();

        // 验证主类默认值
        assertNull(response.getData());

        // 验证内部类默认值
        DocumentIndexingStatusResponse.ProcessingStatus status = new DocumentIndexingStatusResponse.ProcessingStatus();
        assertNull(status.getId());
        assertNull(status.getIndexingStatus());
        assertNull(status.getProcessingStartedAt());
        assertNull(status.getParsingCompletedAt());
        assertNull(status.getCleaningCompletedAt());
        assertNull(status.getSplittingCompletedAt());
        assertNull(status.getCompletedAt());
        assertNull(status.getPausedAt());
        assertNull(status.getError());
        assertNull(status.getStoppedAt());
        assertNull(status.getCompletedSegments());
        assertNull(status.getTotalSegments());
    }

    @Test
    @DisplayName("测试 DocumentIndexingStatusResponse 的空数据列表")
    public void testEmptyDataList() throws JsonProcessingException {
        // 创建带有空数据列表的响应
        DocumentIndexingStatusResponse response = new DocumentIndexingStatusResponse();
        response.setData(new ArrayList<>());

        // 序列化为JSON
        String json = objectMapper.writeValueAsString(response);

        // 验证JSON
        assertTrue(json.contains("\"data\":[]"));

        // 从JSON反序列化
        DocumentIndexingStatusResponse deserializedResponse = objectMapper.readValue(json, DocumentIndexingStatusResponse.class);

        // 验证反序列化后的对象
        assertNotNull(deserializedResponse.getData());
        assertTrue(deserializedResponse.getData().isEmpty());
    }

    @Test
    @DisplayName("测试 DocumentIndexingStatusResponse 的错误状态")
    public void testErrorStatus() throws JsonProcessingException {
        // 使用包含错误信息的JSON
        String errorJson = "{\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"id\": \"doc-123\",\n" +
                "      \"indexing_status\": \"error\",\n" +
                "      \"processing_started_at\": 1615379400000,\n" +
                "      \"error\": \"文档解析失败：无效的文件格式\",\n" +
                "      \"completed_segments\": 0,\n" +
                "      \"total_segments\": 100\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        // 从JSON反序列化
        DocumentIndexingStatusResponse deserializedResponse = objectMapper.readValue(errorJson, DocumentIndexingStatusResponse.class);

        // 验证错误状态
        assertNotNull(deserializedResponse.getData());
        assertEquals(1, deserializedResponse.getData().size());

        DocumentIndexingStatusResponse.ProcessingStatus errorStatus = deserializedResponse.getData().get(0);
        assertEquals("doc-123", errorStatus.getId());
        assertEquals("error", errorStatus.getIndexingStatus());
        assertEquals("文档解析失败：无效的文件格式", errorStatus.getError());
        assertEquals(Integer.valueOf(0), errorStatus.getCompletedSegments());
        assertEquals(Integer.valueOf(100), errorStatus.getTotalSegments());
    }

    @Test
    @DisplayName("测试 DocumentIndexingStatusResponse 的toString方法")
    public void testToString() {
        // 创建测试对象
        DocumentIndexingStatusResponse response = new DocumentIndexingStatusResponse();

        List<DocumentIndexingStatusResponse.ProcessingStatus> statusList = new ArrayList<>();
        DocumentIndexingStatusResponse.ProcessingStatus status = new DocumentIndexingStatusResponse.ProcessingStatus();
        status.setId("doc-123");
        status.setIndexingStatus("processing");
        statusList.add(status);

        response.setData(statusList);

        // 获取toString结果
        String toStringResult = response.toString();

        // 验证toString包含重要字段
        assertTrue(toStringResult.contains("data="));
        assertTrue(toStringResult.contains("id=doc-123"));
        assertTrue(toStringResult.contains("indexingStatus=processing"));
    }
} 