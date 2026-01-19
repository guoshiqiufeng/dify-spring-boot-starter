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

import io.github.guoshiqiufeng.dify.client.core.codec.exception.JsonException;
import io.github.guoshiqiufeng.dify.client.core.http.TypeReference;

/**
 * JSON 序列化与反序列化统一接口
 * <p>
 * 用于解耦具体的 JSON 库实现(Jackson 2.x/3.x, Gson, Fastjson 等)
 * 各模块可根据依赖情况选择不同的实现
 * </p>
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025/12/30
 */
public interface JsonMapper {

    /**
     * 将对象序列化为 JSON 字符串
     *
     * @param object 要序列化的对象
     * @return JSON 字符串
     * @throws JsonException 序列化失败时抛出
     */
    String toJson(Object object) throws JsonException;

    /**
     * 将对象序列化为 JSON 字符串,忽略值为 null 的字段
     *
     * @param object 要序列化的对象
     * @return JSON 字符串(不包含 null 值字段)
     * @throws JsonException 序列化失败时抛出
     */
    String toJsonIgnoreNull(Object object) throws JsonException;

    /**
     * 将 JSON 字符串反序列化为指定类型的对象
     *
     * @param json  JSON 字符串
     * @param clazz 目标类型
     * @param <T>   目标类型泛型
     * @return 反序列化后的对象
     * @throws JsonException 反序列化失败时抛出
     */
    <T> T fromJson(String json, Class<T> clazz) throws JsonException;

    /**
     * 将 JSON 字符串反序列化为泛型类型的对象
     * <p>
     * 用于处理泛型类型,如 {@code List<User>}, {@code Map<String, Object>} 等
     * </p>
     *
     * @param json          JSON 字符串
     * @param typeReference 泛型类型引用
     * @param <T>           目标类型泛型
     * @return 反序列化后的对象
     * @throws JsonException 反序列化失败时抛出
     */
    <T> T fromJson(String json, TypeReference<T> typeReference) throws JsonException;

    /**
     * 解析 JSON 字符串为抽象的 JSON 树节点
     * <p>
     * 用于自定义反序列化器中的动态解析
     * </p>
     *
     * @param json JSON 字符串
     * @return JSON 树节点
     * @throws JsonException 解析失败时抛出
     */
    JsonNode parseTree(String json) throws JsonException;

    /**
     * 将 JSON 节点转换为指定类型的对象
     *
     * @param node  JSON 节点
     * @param clazz 目标类型
     * @param <T>   目标类型泛型
     * @return 转换后的对象
     * @throws JsonException 转换失败时抛出
     */
    <T> T treeToValue(JsonNode node, Class<T> clazz) throws JsonException;

    /**
     * 将对象转换为 JSON 节点
     *
     * @param object 要转换的对象
     * @return JSON 节点
     * @throws JsonException 转换失败时抛出
     */
    JsonNode valueToTree(Object object) throws JsonException;
}
