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
package io.github.guoshiqiufeng.dify.client.codec.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.guoshiqiufeng.dify.client.core.codec.JsonNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Gson JSON 节点适配器
 * <p>
 * 将 Gson 的 JsonElement 适配为统一的 JsonNode 接口
 * </p>
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025/12/30
 */
public class GsonJsonNode implements JsonNode {

    private final JsonElement element;

    public GsonJsonNode(JsonElement element) {
        this.element = element;
    }

    @Override
    public JsonNode get(String fieldName) {
        if (element != null && element.isJsonObject()) {
            JsonElement value = element.getAsJsonObject().get(fieldName);
            return value != null ? new GsonJsonNode(value) : null;
        }
        return null;
    }

    @Override
    public boolean isNull() {
        return element == null || element.isJsonNull();
    }

    @Override
    public boolean isTextual() {
        return element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isString();
    }

    @Override
    public boolean isNumber() {
        return element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber();
    }

    @Override
    public boolean isBoolean() {
        return element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean();
    }

    @Override
    public boolean isArray() {
        return element != null && element.isJsonArray();
    }

    @Override
    public boolean isObject() {
        return element != null && element.isJsonObject();
    }

    @Override
    public boolean has(String fieldName) {
        if (element != null && element.isJsonObject()) {
            return element.getAsJsonObject().has(fieldName);
        }
        return false;
    }

    @Override
    public String asText() {
        if (element == null || element.isJsonNull()) {
            return null;
        }
        if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isString()) {
                return primitive.getAsString();
            }
        }
        return element.toString();
    }

    @Override
    public int asInt() {
        if (element != null && element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isNumber()) {
                return primitive.getAsInt();
            }
            if (primitive.isString()) {
                return Integer.parseInt(primitive.getAsString());
            }
        }
        return 0;
    }

    @Override
    public long asLong() {
        if (element != null && element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isNumber()) {
                return primitive.getAsLong();
            }
            if (primitive.isString()) {
                return Long.parseLong(primitive.getAsString());
            }
        }
        return 0L;
    }

    @Override
    public double asDouble() {
        if (element != null && element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isNumber()) {
                return primitive.getAsDouble();
            }
            if (primitive.isString()) {
                return Double.parseDouble(primitive.getAsString());
            }
        }
        return 0.0;
    }

    @Override
    public boolean asBoolean() {
        if (element != null && element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isBoolean()) {
                return primitive.getAsBoolean();
            }
            if (primitive.isString()) {
                return Boolean.parseBoolean(primitive.getAsString());
            }
        }
        return false;
    }

    @Override
    public Iterator<JsonNode> elements() {
        if (element != null && element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            List<JsonNode> nodes = new ArrayList<>(array.size());
            for (JsonElement elem : array) {
                nodes.add(new GsonJsonNode(elem));
            }
            return nodes.iterator();
        }
        return Collections.emptyIterator();
    }

    @Override
    public Iterator<String> fieldNames() {
        if (element != null && element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            Set<String> keys = object.keySet();
            return keys.iterator();
        }
        return Collections.emptyIterator();
    }

    @Override
    public Object unwrap() {
        return element;
    }
}
