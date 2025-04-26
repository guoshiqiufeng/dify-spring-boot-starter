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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试 SegmentParam 类的功能
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/26 14:29
 */
public class SegmentParamTest {

    @Test
    @DisplayName("测试 SegmentParam 基本属性的设置和获取")
    public void testBasicProperties() {
        // 创建测试对象
        SegmentParam segmentParam = new SegmentParam();

        // 测试内容属性
        String content = "测试分段内容";
        segmentParam.setContent(content);
        assertEquals(content, segmentParam.getContent());

        // 测试答案属性
        String answer = "测试答案";
        segmentParam.setAnswer(answer);
        assertEquals(answer, segmentParam.getAnswer());

        // 测试关键词属性
        List<String> keywords = Arrays.asList("关键词1", "关键词2", "关键词3");
        segmentParam.setKeywords(keywords);
        assertEquals(keywords, segmentParam.getKeywords());
        assertEquals(3, segmentParam.getKeywords().size());
    }

    @Test
    @DisplayName("测试 SegmentParam 链式API")
    public void testChainedAPI() {
        // 测试链式API
        String content = "链式API测试内容";
        String answer = "链式API测试答案";
        List<String> keywords = Arrays.asList("链式", "API", "测试");

        SegmentParam segmentParam = new SegmentParam()
                .setContent(content)
                .setAnswer(answer)
                .setKeywords(keywords);

        assertEquals(content, segmentParam.getContent());
        assertEquals(answer, segmentParam.getAnswer());
        assertEquals(keywords, segmentParam.getKeywords());
    }

    @Test
    @DisplayName("测试 SegmentParam 的序列化和反序列化")
    public void testSerializationDeserialization() throws JsonProcessingException {
        // 创建对象进行测试
        SegmentParam originalParam = new SegmentParam()
                .setContent("序列化测试内容")
                .setAnswer("序列化测试答案")
                .setKeywords(Arrays.asList("序列化", "测试"));

        // 序列化为JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(originalParam);

        // 确保JSON包含所有字段
        assertTrue(json.contains("content"));
        assertTrue(json.contains("序列化测试内容"));
        assertTrue(json.contains("answer"));
        assertTrue(json.contains("序列化测试答案"));
        assertTrue(json.contains("keywords"));
        assertTrue(json.contains("序列化"));
        assertTrue(json.contains("测试"));

        // 从JSON反序列化
        SegmentParam deserializedParam = objectMapper.readValue(json, SegmentParam.class);

        // 验证反序列化的对象
        assertEquals(originalParam.getContent(), deserializedParam.getContent());
        assertEquals(originalParam.getAnswer(), deserializedParam.getAnswer());
        assertEquals(originalParam.getKeywords(), deserializedParam.getKeywords());
    }

    @Test
    @DisplayName("测试 SegmentParam 的相等性和哈希码")
    public void testEqualsAndHashCode() {
        // 创建两个相同内容的对象
        SegmentParam param1 = new SegmentParam()
                .setContent("相同内容")
                .setAnswer("相同答案")
                .setKeywords(Arrays.asList("相同", "关键词"));

        SegmentParam param2 = new SegmentParam()
                .setContent("相同内容")
                .setAnswer("相同答案")
                .setKeywords(Arrays.asList("相同", "关键词"));

        // 测试相等性
        assertEquals(param1, param2);
        assertEquals(param2, param1);

        // 测试哈希码
        assertEquals(param1.hashCode(), param2.hashCode());

        // 创建不同内容的对象
        SegmentParam param3 = new SegmentParam()
                .setContent("不同内容")
                .setAnswer("相同答案")
                .setKeywords(Arrays.asList("相同", "关键词"));

        // 测试不相等性
        assertNotEquals(param1, param3);
        assertNotEquals(param3, param1);

        // 测试与null和其他类型比较
        assertNotEquals(param1, null);
        assertNotEquals(param1, "字符串");
    }

    @Test
    @DisplayName("测试 SegmentParam 的默认值")
    public void testDefaultValues() {
        // 创建新对象验证默认值
        SegmentParam segmentParam = new SegmentParam();

        // 默认属性应为null
        assertNull(segmentParam.getContent());
        assertNull(segmentParam.getAnswer());
        assertNull(segmentParam.getKeywords());
    }

    @Test
    @DisplayName("测试 SegmentParam 对空值的处理")
    public void testNullValues() {
        // 创建对象并设置非空值
        SegmentParam segmentParam = new SegmentParam()
                .setContent("内容")
                .setAnswer("答案")
                .setKeywords(List.of("关键词"));

        // 设置null值
        segmentParam.setContent(null);
        segmentParam.setAnswer(null);
        segmentParam.setKeywords(null);

        // 验证是否正确设置为null
        assertNull(segmentParam.getContent());
        assertNull(segmentParam.getAnswer());
        assertNull(segmentParam.getKeywords());
    }

    @Test
    @DisplayName("测试 SegmentParam 的关键词列表修改")
    public void testKeywordsListModification() {
        // 创建对象及其关键词列表
        SegmentParam segmentParam = new SegmentParam();
        List<String> keywords = Arrays.asList("关键词1", "关键词2");
        segmentParam.setKeywords(keywords);

        // 验证关键词列表是否正确设置
        assertEquals(2, segmentParam.getKeywords().size());
        assertTrue(segmentParam.getKeywords().contains("关键词1"));
        assertTrue(segmentParam.getKeywords().contains("关键词2"));
    }
} 