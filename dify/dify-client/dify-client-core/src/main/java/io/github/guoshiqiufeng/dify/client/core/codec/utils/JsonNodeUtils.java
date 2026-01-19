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
package io.github.guoshiqiufeng.dify.client.core.codec.utils;

import io.github.guoshiqiufeng.dify.client.core.codec.JsonNode;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yanghq
 * @version 1.0
 * @since 2026/1/20 09:45
 */
@UtilityClass
public class JsonNodeUtils {

    /**
     * 将 JsonNode 转换为 Map
     */
    public static Map<String, Object> convertToMap(JsonNode node) {
        Map<String, Object> map = new LinkedHashMap<>();
        if (node == null || !node.isObject()) {
            return map;
        }

        java.util.Iterator<String> fieldNames = node.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            JsonNode value = node.get(fieldName);
            map.put(fieldName, convertToObject(value));
        }
        return map;
    }

    /**
     * 将 JsonNode 转换为 List<String>
     */
    public static List<String> convertToStringList(JsonNode node) {
        List<String> list = new ArrayList<>();
        if (node == null || !node.isArray()) {
            return list;
        }

        java.util.Iterator<JsonNode> elements = node.elements();
        while (elements.hasNext()) {
            JsonNode element = elements.next();
            list.add(element.asText());
        }
        return list;
    }

    /**
     * 将 JsonNode 转换为 Java 对象
     */
    public static Object convertToObject(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        if (node.isTextual()) {
            return node.asText();
        }
        if (node.isNumber()) {
            return node.asDouble();
        }
        if (node.isBoolean()) {
            return node.asBoolean();
        }
        if (node.isArray()) {
            java.util.List<Object> list = new java.util.ArrayList<>();
            java.util.Iterator<JsonNode> elements = node.elements();
            while (elements.hasNext()) {
                list.add(convertToObject(elements.next()));
            }
            return list;
        }
        if (node.isObject()) {
            return convertToMap(node);
        }
        return node.asText();
    }
}
