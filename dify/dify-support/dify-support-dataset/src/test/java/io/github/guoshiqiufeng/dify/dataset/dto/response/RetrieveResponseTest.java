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
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试 RetrieveResponse 类的功能
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/26 17:42
 */
public class RetrieveResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("测试 RetrieveResponse 基本属性的设置和获取")
    public void testBasicProperties() {
        // 创建测试对象
        RetrieveResponse response = new RetrieveResponse();

        // 创建查询对象
        RetrieveResponse.RetrieveQuery query = new RetrieveResponse.RetrieveQuery();
        query.setContent("测试查询内容");

        // 创建记录列表
        List<RetrieveResponse.RetrieveRecord> records = new ArrayList<>();

        // 创建第一条记录
        RetrieveResponse.RetrieveRecord record1 = new RetrieveResponse.RetrieveRecord();
        record1.setScore(0.95f);

        // 创建位置信息
        RetrieveResponse.TsnePosition position1 = new RetrieveResponse.TsnePosition();
        position1.setX(1.0f);
        position1.setY(2.0f);
        record1.setTsnePosition(position1);

        // 创建分段信息
        RetrieveResponse.Segment segment1 = new RetrieveResponse.Segment();
        segment1.setId("segment-123");
        segment1.setDocumentId("doc-456");
        segment1.setContent("测试分段内容1");
        segment1.setAnswer("测试答案1");
        segment1.setKeywords(Arrays.asList("关键词1", "关键词2"));

        // 创建文档信息
        RetrieveResponse.Document document1 = new RetrieveResponse.Document();
        document1.setId("doc-456");
        document1.setDataSourceType("file");
        document1.setName("测试文档1");
        document1.setDocType("txt");

        segment1.setDocument(document1);
        record1.setSegment(segment1);

        records.add(record1);

        // 设置属性
        response.setQuery(query);
        response.setRecords(records);

        // 验证属性
        assertEquals(query, response.getQuery());
        assertEquals("测试查询内容", response.getQuery().getContent());

        assertEquals(records, response.getRecords());
        assertEquals(1, response.getRecords().size());

        RetrieveResponse.RetrieveRecord retrievedRecord = response.getRecords().get(0);
        assertEquals(0.95f, retrievedRecord.getScore());
        assertEquals(1.0f, retrievedRecord.getTsnePosition().getX());
        assertEquals(2.0f, retrievedRecord.getTsnePosition().getY());

        RetrieveResponse.Segment retrievedSegment = retrievedRecord.getSegment();
        assertEquals("segment-123", retrievedSegment.getId());
        assertEquals("doc-456", retrievedSegment.getDocumentId());
        assertEquals("测试分段内容1", retrievedSegment.getContent());
        assertEquals("测试答案1", retrievedSegment.getAnswer());
        assertEquals(2, retrievedSegment.getKeywords().size());
        assertEquals("关键词1", retrievedSegment.getKeywords().get(0));

        RetrieveResponse.Document retrievedDocument = retrievedSegment.getDocument();
        assertEquals("doc-456", retrievedDocument.getId());
        assertEquals("file", retrievedDocument.getDataSourceType());
        assertEquals("测试文档1", retrievedDocument.getName());
        assertEquals("txt", retrievedDocument.getDocType());
    }

    @Test
    @DisplayName("测试 RetrieveResponse 的JSON序列化和反序列化")
    public void testJsonSerializationDeserialization() throws JsonProcessingException {
        // 创建测试对象
        RetrieveResponse response = new RetrieveResponse();

        // 创建查询对象
        RetrieveResponse.RetrieveQuery query = new RetrieveResponse.RetrieveQuery();
        query.setContent("测试查询内容");
        response.setQuery(query);

        // 创建记录
        List<RetrieveResponse.RetrieveRecord> records = new ArrayList<>();
        RetrieveResponse.RetrieveRecord record = new RetrieveResponse.RetrieveRecord();
        record.setScore(0.95f);

        // 创建位置信息
        RetrieveResponse.TsnePosition position = new RetrieveResponse.TsnePosition();
        position.setX(1.0f);
        position.setY(2.0f);
        record.setTsnePosition(position);

        // 创建分段
        RetrieveResponse.Segment segment = new RetrieveResponse.Segment();
        segment.setId("segment-123");
        segment.setContent("测试内容");

        // 创建文档
        RetrieveResponse.Document document = new RetrieveResponse.Document();
        document.setId("doc-456");
        document.setName("测试文档");

        segment.setDocument(document);
        record.setSegment(segment);
        records.add(record);
        response.setRecords(records);

        // 序列化为JSON
        String json = objectMapper.writeValueAsString(response);

        // 验证JSON包含预期字段
        assertTrue(json.contains("\"query\":"));
        assertTrue(json.contains("\"content\":\"测试查询内容\""));
        assertTrue(json.contains("\"records\":"));
        assertTrue(json.contains("\"score\":0.95"));
        assertTrue(json.contains("\"x\":1.0"));
        assertTrue(json.contains("\"y\":2.0"));
        assertTrue(json.contains("\"id\":\"segment-123\""));
        assertTrue(json.contains("\"content\":\"测试内容\""));
        assertTrue(json.contains("\"id\":\"doc-456\""));
        assertTrue(json.contains("\"name\":\"测试文档\""));

        // 从JSON反序列化
        RetrieveResponse deserializedResponse = objectMapper.readValue(json, RetrieveResponse.class);

        // 验证反序列化对象
        assertNotNull(deserializedResponse.getQuery());
        assertEquals("测试查询内容", deserializedResponse.getQuery().getContent());

        assertNotNull(deserializedResponse.getRecords());
        assertEquals(1, deserializedResponse.getRecords().size());

        RetrieveResponse.RetrieveRecord deserializedRecord = deserializedResponse.getRecords().get(0);
        assertEquals(0.95f, deserializedRecord.getScore());
        assertEquals(1.0f, deserializedRecord.getTsnePosition().getX());
        assertEquals(2.0f, deserializedRecord.getTsnePosition().getY());

        RetrieveResponse.Segment deserializedSegment = deserializedRecord.getSegment();
        assertEquals("segment-123", deserializedSegment.getId());
        assertEquals("测试内容", deserializedSegment.getContent());

        RetrieveResponse.Document deserializedDocument = deserializedSegment.getDocument();
        assertEquals("doc-456", deserializedDocument.getId());
        assertEquals("测试文档", deserializedDocument.getName());
    }

    @Test
    @DisplayName("测试 RetrieveResponse 使用蛇形命名法的JSON反序列化")
    public void testSnakeCaseJsonDeserialization() throws JsonProcessingException {
        // 使用蛇形命名法的JSON
        String snakeCaseJson = "{\n" +
                "  \"query\": {\n" +
                "    \"content\": \"测试查询内容\"\n" +
                "  },\n" +
                "  \"records\": [\n" +
                "    {\n" +
                "      \"segment\": {\n" +
                "        \"id\": \"segment-123\",\n" +
                "        \"document_id\": \"doc-456\",\n" +
                "        \"content\": \"测试分段内容\",\n" +
                "        \"word_count\": 100,\n" +
                "        \"hit_count\": 5,\n" +
                "        \"document\": {\n" +
                "          \"id\": \"doc-456\",\n" +
                "          \"data_source_type\": \"file\",\n" +
                "          \"name\": \"测试文档\",\n" +
                "          \"doc_type\": \"txt\"\n" +
                "        }\n" +
                "      },\n" +
                "      \"score\": 0.95,\n" +
                "      \"tsne_position\": {\n" +
                "        \"x\": 1.0,\n" +
                "        \"y\": 2.0\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        // 从蛇形命名法JSON反序列化
        RetrieveResponse deserializedResponse = objectMapper.readValue(snakeCaseJson, RetrieveResponse.class);

        // 验证蛇形命名法字段是否正确映射
        assertNotNull(deserializedResponse.getQuery());
        assertEquals("测试查询内容", deserializedResponse.getQuery().getContent());

        assertNotNull(deserializedResponse.getRecords());
        assertEquals(1, deserializedResponse.getRecords().size());

        RetrieveResponse.RetrieveRecord deserializedRecord = deserializedResponse.getRecords().get(0);
        assertEquals(0.95f, deserializedRecord.getScore());

        // 验证使用@JsonAlias注解的蛇形命名法字段
        assertEquals(1.0f, deserializedRecord.getTsnePosition().getX());
        assertEquals(2.0f, deserializedRecord.getTsnePosition().getY());

        RetrieveResponse.Segment deserializedSegment = deserializedRecord.getSegment();
        assertEquals("segment-123", deserializedSegment.getId());
        assertEquals("doc-456", deserializedSegment.getDocumentId());  // 验证document_id映射
        assertEquals("测试分段内容", deserializedSegment.getContent());
        assertEquals(Integer.valueOf(100), deserializedSegment.getWordCount());  // 验证word_count映射
        assertEquals(Integer.valueOf(5), deserializedSegment.getHitCount());  // 验证hit_count映射

        RetrieveResponse.Document deserializedDocument = deserializedSegment.getDocument();
        assertEquals("doc-456", deserializedDocument.getId());
        assertEquals("file", deserializedDocument.getDataSourceType());  // 验证data_source_type映射
        assertEquals("测试文档", deserializedDocument.getName());
        assertEquals("txt", deserializedDocument.getDocType());  // 验证doc_type映射
    }

    @Test
    @DisplayName("测试 RetrieveResponse 内部类的相等性和哈希码")
    public void testInnerClassesEqualsAndHashCode() {
        // 测试查询类的相等性
        RetrieveResponse.RetrieveQuery query1 = new RetrieveResponse.RetrieveQuery();
        query1.setContent("测试内容");

        RetrieveResponse.RetrieveQuery query2 = new RetrieveResponse.RetrieveQuery();
        query2.setContent("测试内容");

        assertEquals(query1, query2);
        assertEquals(query1.hashCode(), query2.hashCode());

        // 测试位置类的相等性
        RetrieveResponse.TsnePosition position1 = new RetrieveResponse.TsnePosition();
        position1.setX(1.0f);
        position1.setY(2.0f);

        RetrieveResponse.TsnePosition position2 = new RetrieveResponse.TsnePosition();
        position2.setX(1.0f);
        position2.setY(2.0f);

        assertEquals(position1, position2);
        assertEquals(position1.hashCode(), position2.hashCode());

        // 测试文档类的相等性
        RetrieveResponse.Document doc1 = new RetrieveResponse.Document();
        doc1.setId("doc-123");
        doc1.setName("测试文档");

        RetrieveResponse.Document doc2 = new RetrieveResponse.Document();
        doc2.setId("doc-123");
        doc2.setName("测试文档");

        assertEquals(doc1, doc2);
        assertEquals(doc1.hashCode(), doc2.hashCode());
    }

    @Test
    @DisplayName("测试 RetrieveResponse 的默认值")
    public void testDefaultValues() {
        // 创建新对象验证默认值
        RetrieveResponse response = new RetrieveResponse();

        // 主类默认值为null
        assertNull(response.getQuery());
        assertNull(response.getRecords());

        // 内部类默认值
        RetrieveResponse.RetrieveQuery query = new RetrieveResponse.RetrieveQuery();
        assertNull(query.getContent());

        RetrieveResponse.RetrieveRecord record = new RetrieveResponse.RetrieveRecord();
        assertNull(record.getSegment());
        assertNull(record.getScore());
        assertNull(record.getTsnePosition());

        RetrieveResponse.TsnePosition position = new RetrieveResponse.TsnePosition();
        assertNull(position.getX());
        assertNull(position.getY());

        RetrieveResponse.Segment segment = new RetrieveResponse.Segment();
        assertNull(segment.getId());
        assertNull(segment.getDocumentId());
        assertNull(segment.getContent());
        assertNull(segment.getAnswer());
        assertNull(segment.getWordCount());
        assertNull(segment.getTokens());
        assertNull(segment.getKeywords());
        assertNull(segment.getDocument());

        RetrieveResponse.Document document = new RetrieveResponse.Document();
        assertNull(document.getId());
        assertNull(document.getDataSourceType());
        assertNull(document.getName());
        assertNull(document.getDocType());
    }

    @Test
    @DisplayName("测试 RetrieveResponse 的空记录列表")
    public void testEmptyRecordsList() throws JsonProcessingException {
        // 创建带有空记录列表的响应
        RetrieveResponse response = new RetrieveResponse();
        RetrieveResponse.RetrieveQuery query = new RetrieveResponse.RetrieveQuery();
        query.setContent("测试查询");
        response.setQuery(query);
        response.setRecords(new ArrayList<>());

        // 序列化为JSON
        String json = objectMapper.writeValueAsString(response);

        // 验证JSON
        assertTrue(json.contains("\"query\":"));
        assertTrue(json.contains("\"content\":\"测试查询\""));
        assertTrue(json.contains("\"records\":[]"));

        // 从JSON反序列化
        RetrieveResponse deserializedResponse = objectMapper.readValue(json, RetrieveResponse.class);

        // 验证反序列化后的对象
        assertNotNull(deserializedResponse.getQuery());
        assertEquals("测试查询", deserializedResponse.getQuery().getContent());
        assertNotNull(deserializedResponse.getRecords());
        assertTrue(deserializedResponse.getRecords().isEmpty());
    }

    @Test
    @DisplayName("测试 RetrieveResponse 的toString方法")
    public void testToString() {
        // 创建测试对象
        RetrieveResponse response = new RetrieveResponse();

        RetrieveResponse.RetrieveQuery query = new RetrieveResponse.RetrieveQuery();
        query.setContent("测试查询");
        response.setQuery(query);

        // 获取toString结果
        String toStringResult = response.toString();

        // 验证toString包含重要字段
        assertTrue(toStringResult.contains("query="));
        assertTrue(toStringResult.contains("content=测试查询"));
    }
} 