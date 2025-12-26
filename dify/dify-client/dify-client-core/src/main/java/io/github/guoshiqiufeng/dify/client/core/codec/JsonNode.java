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
package io.github.guoshiqiufeng.dify.client.core.codec;

import java.util.Iterator;

/**
 * JSON 节点抽象接口
 * <p>
 * 用于统一不同 JSON 库的树节点 API
 * 支持 Jackson JsonNode, Gson JsonElement 等
 * </p>
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025/12/30
 */
public interface JsonNode {

    /**
     * 获取指定字段的子节点
     *
     * @param fieldName 字段名
     * @return 子节点,不存在返回 null
     */
    JsonNode get(String fieldName);

    /**
     * 判断节点是否为 null 或不存在
     *
     * @return true 表示节点为 null 或不存在
     */
    boolean isNull();

    /**
     * 判断节点是否为字符串类型
     *
     * @return true 表示节点为字符串类型
     */
    boolean isTextual();

    /**
     * 判断节点是否为数字类型
     *
     * @return true 表示节点为数字类型
     */
    boolean isNumber();

    /**
     * 判断节点是否为布尔类型
     *
     * @return true 表示节点为布尔类型
     */
    boolean isBoolean();

    /**
     * 判断节点是否为数组类型
     *
     * @return true 表示节点为数组类型
     */
    boolean isArray();

    /**
     * 判断节点是否为对象类型
     *
     * @return true 表示节点为对象类型
     */
    boolean isObject();

    /**
     * 判断节点是否包含指定字段
     *
     * @param fieldName 字段名
     * @return true 表示包含该字段
     */
    boolean has(String fieldName);

    /**
     * 获取节点的字符串值
     *
     * @return 字符串值
     */
    String asText();

    /**
     * 获取节点的整数值
     *
     * @return 整数值
     */
    int asInt();

    /**
     * 获取节点的长整数值
     *
     * @return 长整数值
     */
    long asLong();

    /**
     * 获取节点的浮点数值
     *
     * @return 浮点数值
     */
    double asDouble();

    /**
     * 获取节点的布尔值
     *
     * @return 布尔值
     */
    boolean asBoolean();

    /**
     * 如果节点是数组,返回数组元素迭代器
     *
     * @return 数组元素迭代器
     */
    Iterator<JsonNode> elements();

    /**
     * 如果节点是对象,返回字段名迭代器
     *
     * @return 字段名迭代器
     */
    Iterator<String> fieldNames();

    /**
     * 获取原始的底层节点对象
     * <p>
     * 用于需要访问底层 API 的场景
     * </p>
     *
     * @return 底层节点对象 (Jackson JsonNode, Gson JsonElement 等)
     */
    Object unwrap();
}
