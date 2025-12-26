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
package io.github.guoshiqiufeng.dify.client.codec.jackson3;

import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Jackson 3 JsonNode wrapper implementation
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025-12-31
 */
public class Jackson3JsonNode implements io.github.guoshiqiufeng.dify.client.core.codec.JsonNode {

    private final JsonNode node;

    public Jackson3JsonNode(JsonNode node) {
        this.node = node;
    }

    @Override
    public Object unwrap() {
        return node;
    }

    @Override
    public boolean isNull() {
        return node == null || node.isNull();
    }

    @Override
    public boolean isTextual() {
        return node != null && node.isString();
    }

    @Override
    public boolean isNumber() {
        return node != null && node.isNumber();
    }

    @Override
    public boolean isBoolean() {
        return node != null && node.isBoolean();
    }

    @Override
    public boolean isObject() {
        return node != null && node.isObject();
    }

    @Override
    public boolean isArray() {
        return node != null && node.isArray();
    }

    @Override
    public boolean has(String fieldName) {
        return node != null && node.has(fieldName);
    }

    @Override
    public String asText() {
        if (node == null) {
            return null;
        }
        if (node.isString()) {
            return node.stringValue();
        }
        return node.toString();
    }

    @Override
    public int asInt() {
        return node != null ? node.asInt() : 0;
    }

    @Override
    public long asLong() {
        return node != null ? node.asLong() : 0L;
    }

    @Override
    public double asDouble() {
        return node != null ? node.asDouble() : 0.0;
    }

    @Override
    public boolean asBoolean() {
        return node != null && node.asBoolean();
    }

    @Override
    public io.github.guoshiqiufeng.dify.client.core.codec.JsonNode get(String fieldName) {
        if (node == null) {
            return null;
        }
        JsonNode child = node.get(fieldName);
        return child != null ? new Jackson3JsonNode(child) : null;
    }

    @Override
    public Iterator<io.github.guoshiqiufeng.dify.client.core.codec.JsonNode> elements() {
        if (node == null || !node.isArray()) {
            return Collections.emptyIterator();
        }
        List<io.github.guoshiqiufeng.dify.client.core.codec.JsonNode> elements = new ArrayList<>();
        for (JsonNode element : node) {
            elements.add(new Jackson3JsonNode(element));
        }
        return elements.iterator();
    }

    @Override
    public Iterator<String> fieldNames() {
        if (node == null || !node.isObject()) {
            return Collections.emptyIterator();
        }
        return node.propertyNames().iterator();
    }
}
