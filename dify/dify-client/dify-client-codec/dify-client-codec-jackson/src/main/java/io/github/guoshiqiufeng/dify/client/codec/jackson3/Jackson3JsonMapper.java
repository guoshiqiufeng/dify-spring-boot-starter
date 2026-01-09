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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.guoshiqiufeng.dify.client.core.codec.JsonDeserialize;
import io.github.guoshiqiufeng.dify.client.core.codec.JsonDeserializer;
import io.github.guoshiqiufeng.dify.client.core.codec.JsonException;
import io.github.guoshiqiufeng.dify.client.core.http.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.lang.reflect.Type;

/**
 * Jackson 3 JSON 实现
 * <p>
 * 基于 Jackson 3.x JsonMapper 实现序列化接口
 * 支持 Jackson 注解如 @JsonProperty, @JsonAlias 等
 * 适用于 Spring Boot 4 和需要 Jackson 3 支持的模块
 * </p>
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025-12-31
 */
public class Jackson3JsonMapper implements io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper {

    private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();
    private static final JsonMapper JSON_MAPPER_IGNORE_NULL = JsonMapper.builder()
            .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL))
            .changeDefaultPropertyInclusion(incl -> incl.withContentInclusion(JsonInclude.Include.NON_NULL))
            .build();
    private static final Jackson3JsonMapper INSTANCE = new Jackson3JsonMapper();

    public static Jackson3JsonMapper getInstance() {
        return INSTANCE;
    }

    /**
     * Get the underlying JsonMapper instance for advanced configuration
     *
     * @return JsonMapper instance
     */
    public static JsonMapper getJsonMapper() {
        return JSON_MAPPER;
    }

    @Override
    public String toJson(Object object) throws JsonException {
        try {
            return JSON_MAPPER.writeValueAsString(object);
        } catch (Exception e) {
            throw new JsonException("Failed to serialize object to JSON", e);
        }
    }

    @Override
    public String toJsonIgnoreNull(Object object) throws JsonException {
        try {
            return JSON_MAPPER_IGNORE_NULL.writeValueAsString(object);
        } catch (Exception e) {
            throw new JsonException("Failed to serialize object to JSON", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T fromJson(String json, Class<T> clazz) throws JsonException {
        try {
            // Check if the class has @JsonDeserialize annotation
            JsonDeserialize annotation = clazz.getAnnotation(JsonDeserialize.class);

            if (annotation != null) {
                // Use custom deserializer
                Class<? extends JsonDeserializer<?>> deserializerClass = annotation.using();
                JsonDeserializer<T> deserializer = (JsonDeserializer<T>) deserializerClass.getDeclaredConstructor().newInstance();

                io.github.guoshiqiufeng.dify.client.core.codec.JsonNode node = parseTree(json);
                return deserializer.deserialize(node, this);
            }

            return JSON_MAPPER.readValue(json, clazz);
        } catch (JsonException e) {
            throw e;
        } catch (Exception e) {
            throw new JsonException("Failed to deserialize JSON to " + clazz.getName(), e);
        }
    }

    @Override
    public <T> T fromJson(String json, TypeReference<T> typeReference) throws JsonException {
        try {
            Type type = typeReference.getType();
            tools.jackson.core.type.TypeReference<T> jacksonTypeRef =
                    new tools.jackson.core.type.TypeReference<T>() {
                        @Override
                        public Type getType() {
                            return type;
                        }
                    };
            return JSON_MAPPER.readValue(json, jacksonTypeRef);
        } catch (Exception e) {
            throw new JsonException("Failed to deserialize JSON to " + typeReference.getType(), e);
        }
    }

    @Override
    public io.github.guoshiqiufeng.dify.client.core.codec.JsonNode parseTree(String json) throws JsonException {
        try {
            JsonNode node = JSON_MAPPER.readTree(json);
            return new Jackson3JsonNode(node);
        } catch (Exception e) {
            throw new JsonException("Failed to parse JSON string", e);
        }
    }

    @Override
    public <T> T treeToValue(io.github.guoshiqiufeng.dify.client.core.codec.JsonNode node, Class<T> clazz)
            throws JsonException {
        try {
            if (node == null || node.isNull()) {
                return null;
            }
            JsonNode jacksonNode = (JsonNode) node.unwrap();
            return JSON_MAPPER.treeToValue(jacksonNode, clazz);
        } catch (Exception e) {
            throw new JsonException("Failed to convert tree to " + clazz.getName(), e);
        }
    }

    @Override
    public io.github.guoshiqiufeng.dify.client.core.codec.JsonNode valueToTree(Object object) throws JsonException {
        try {
            if (object == null) {
                return new Jackson3JsonNode(JSON_MAPPER.nullNode());
            }
            // Use Jackson's optimized valueToTree method - no string conversion needed
            JsonNode node = JSON_MAPPER.valueToTree(object);
            return new Jackson3JsonNode(node);
        } catch (Exception e) {
            throw new JsonException("Failed to convert value to tree", e);
        }
    }
}
