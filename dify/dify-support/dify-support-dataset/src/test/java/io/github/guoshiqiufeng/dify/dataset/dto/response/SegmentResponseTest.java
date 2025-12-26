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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试 SegmentResponse 类的功能
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/26 16:29
 */
public class SegmentResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("测试 SegmentResponse 基本属性的设置和获取")
    public void testBasicProperties() {
        // 创建测试对象
        SegmentResponse response = new SegmentResponse();

        // 创建数据列表
        List<SegmentData> dataList = new ArrayList<>();
        SegmentData data1 = new SegmentData();
        data1.setId("segment-1");
        data1.setContent("内容1");

        SegmentData data2 = new SegmentData();
        data2.setId("segment-2");
        data2.setContent("内容2");

        dataList.add(data1);
        dataList.add(data2);

        // 设置属性
        response.setData(dataList);
        response.setDocForm("qa");

        // 验证属性
        assertEquals(dataList, response.getData());
        assertEquals(2, response.getData().size());
        assertEquals("segment-1", response.getData().get(0).getId());
        assertEquals("内容1", response.getData().get(0).getContent());
        assertEquals("segment-2", response.getData().get(1).getId());
        assertEquals("内容2", response.getData().get(1).getContent());
        assertEquals("qa", response.getDocForm());
    }

    @Test
    @DisplayName("测试 SegmentResponse 的JSON序列化和反序列化")
    public void testJsonSerializationDeserialization() throws JsonProcessingException {
        // 创建测试对象和数据
        SegmentResponse response = new SegmentResponse();

        List<SegmentData> dataList = new ArrayList<>();
        SegmentData data = new SegmentData();
        data.setId("segment-123");
        data.setContent("测试内容");
        dataList.add(data);

        response.setData(dataList);
        response.setDocForm("qa");

        // 序列化为JSON
        String json = objectMapper.writeValueAsString(response);

        // 验证JSON包含预期字段
        assertTrue(json.contains("\"data\":"));
        assertTrue(json.contains("\"id\":\"segment-123\""));
        assertTrue(json.contains("\"content\":\"测试内容\""));
        assertTrue(json.contains("\"docForm\":\"qa\""));

        // 从JSON反序列化
        SegmentResponse deserializedResponse = objectMapper.readValue(json, SegmentResponse.class);

        // In 验证反序列化对象
        assertNotNull(deserializedResponse.getData());
        assertEquals(1, deserializedResponse.getData().size());
        assertEquals("segment-123", deserializedResponse.getData().get(0).getId());
        assertEquals("测试内容", deserializedResponse.getData().get(0).getContent());
        assertEquals("qa", deserializedResponse.getDocForm());
    }

    @Test
    @DisplayName("测试 SegmentResponse 使用蛇形命名法的JSON反序列化")
    public void testSnakeCaseJsonDeserialization() throws JsonProcessingException {
        // 使用蛇形命名法的JSON
        String snakeCaseJson = "{\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"id\": \"segment-123\",\n" +
                "      \"content\": \"测试内容\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"doc_form\": \"qa\"\n" +
                "}";

        // 从蛇形命名法JSON反序列化
        SegmentResponse deserializedResponse = objectMapper.readValue(snakeCaseJson, SegmentResponse.class);

        // 验证是否正确映射
        assertNotNull(deserializedResponse.getData());
        assertEquals(1, deserializedResponse.getData().size());
        assertEquals("segment-123", deserializedResponse.getData().get(0).getId());
        assertEquals("测试内容", deserializedResponse.getData().get(0).getContent());
        assertEquals("qa", deserializedResponse.getDocForm());
    }

    @Test
    @DisplayName("测试 SegmentResponse 的相等性和哈希码")
    public void testEqualsAndHashCode() {
        // 创建两个相同内容的对象
        SegmentResponse response1 = new SegmentResponse();
        List<SegmentData> dataList1 = new ArrayList<>();
        SegmentData data1 = new SegmentData();
        data1.setId("segment-1");
        dataList1.add(data1);
        response1.setData(dataList1);
        response1.setDocForm("qa");

        SegmentResponse response2 = new SegmentResponse();
        List<SegmentData> dataList2 = new ArrayList<>();
        SegmentData data2 = new SegmentData();
        data2.setId("segment-1");
        dataList2.add(data2);
        response2.setData(dataList2);
        response2.setDocForm("qa");

        // 测试相等性
        assertEquals(response1, response2);
        assertEquals(response2, response1);

        // 测试哈希码
        assertEquals(response1.hashCode(), response2.hashCode());

        // 创建不同内容的对象
        SegmentResponse response3 = new SegmentResponse();
        response3.setDocForm("different");

        // 测试不相等性
        assertNotEquals(response1, response3);
        assertNotEquals(response3, response1);
    }

    @Test
    @DisplayName("测试 SegmentResponse 的默认值")
    public void testDefaultValues() {
        // 创建新对象验证默认值
        SegmentResponse response = new SegmentResponse();

        // 默认应为null
        assertNull(response.getData());
        assertNull(response.getDocForm());
    }

    @Test
    @DisplayName("测试 SegmentResponse 的空列表")
    public void testEmptyDataList() throws JsonProcessingException {
        // 创建带有空列表的对象
        SegmentResponse response = new SegmentResponse();
        response.setData(new ArrayList<>());
        response.setDocForm("qa");

        // 序列化为JSON
        String json = objectMapper.writeValueAsString(response);

        // 验证JSON
        assertTrue(json.contains("\"data\":[]"));

        // 从JSON反序列化
        SegmentResponse deserializedResponse = objectMapper.readValue(json, SegmentResponse.class);

        // 验证反序列化后的对象
        assertNotNull(deserializedResponse.getData());
        assertTrue(deserializedResponse.getData().isEmpty());
        assertEquals("qa", deserializedResponse.getDocForm());
    }

    @Test
    @DisplayName("测试 SegmentResponse 的toString方法")
    public void testToString() {
        // 创建测试对象
        SegmentResponse response = new SegmentResponse();

        List<SegmentData> dataList = new ArrayList<>();
        SegmentData data = new SegmentData();
        data.setId("segment-123");
        dataList.add(data);

        response.setData(dataList);
        response.setDocForm("qa");

        // 获取toString结果
        String toStringResult = response.toString();

        // 验证toString包含所有重要字段
        assertTrue(toStringResult.contains("data="));
        assertTrue(toStringResult.contains("segment-123"));
        assertTrue(toStringResult.contains("docForm=qa"));
    }
} 