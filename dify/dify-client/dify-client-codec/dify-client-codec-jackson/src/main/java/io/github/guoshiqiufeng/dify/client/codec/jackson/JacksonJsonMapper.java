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
package io.github.guoshiqiufeng.dify.client.codec.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.guoshiqiufeng.dify.client.core.codec.JsonDeserialize;
import io.github.guoshiqiufeng.dify.client.core.codec.JsonDeserializer;
import io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper;
import io.github.guoshiqiufeng.dify.client.core.codec.exception.JsonException;
import io.github.guoshiqiufeng.dify.client.core.http.TypeReference;

import java.lang.reflect.Type;

/**
 * Jackson JSON 实现
 * <p>
 * 基于 Jackson ObjectMapper 实现序列化接口
 * 支持 Jackson 注解如 @JsonProperty, @JsonAlias 等
 * 适用于需要 Jackson 注解支持的模块
 * </p>
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025-12-31
 */
public class JacksonJsonMapper implements JsonMapper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .registerModule(new JavaTimeModule());
    private static final ObjectMapper OBJECT_MAPPER_IGNORE_NULL = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .registerModule(new JavaTimeModule());
    private static final JacksonJsonMapper INSTANCE = new JacksonJsonMapper();

    public static JacksonJsonMapper getInstance() {
        return INSTANCE;
    }

    /**
     * Get the underlying ObjectMapper instance for advanced configuration
     *
     * @return ObjectMapper instance
     */
    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    @Override
    public String toJson(Object object) throws JsonException {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (Exception e) {
            throw new JsonException("Failed to serialize object to JSON", e);
        }
    }

    @Override
    public String toJsonIgnoreNull(Object object) throws JsonException {
        try {
            return OBJECT_MAPPER_IGNORE_NULL.writeValueAsString(object);
        } catch (Exception e) {
            throw new JsonException("Failed to serialize object to JSON (ignoring null)", e);
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

            return OBJECT_MAPPER.readValue(json, clazz);
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
            com.fasterxml.jackson.core.type.TypeReference<T> jacksonTypeRef =
                    new com.fasterxml.jackson.core.type.TypeReference<T>() {
                        @Override
                        public Type getType() {
                            return type;
                        }
                    };
            return OBJECT_MAPPER.readValue(json, jacksonTypeRef);
        } catch (Exception e) {
            throw new JsonException("Failed to deserialize JSON to " + typeReference.getType(), e);
        }
    }

    @Override
    public io.github.guoshiqiufeng.dify.client.core.codec.JsonNode parseTree(String json) throws JsonException {
        try {
            JsonNode node = OBJECT_MAPPER.readTree(json);
            return new JacksonJsonNode(node);
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
            return OBJECT_MAPPER.treeToValue(jacksonNode, clazz);
        } catch (Exception e) {
            throw new JsonException("Failed to convert tree to " + clazz.getName(), e);
        }
    }

    @Override
    public io.github.guoshiqiufeng.dify.client.core.codec.JsonNode valueToTree(Object object) throws JsonException {
        try {
            if (object == null) {
                return new JacksonJsonNode(OBJECT_MAPPER.nullNode());
            }
            // Use Jackson's optimized valueToTree method - no string conversion needed
            JsonNode node = OBJECT_MAPPER.valueToTree(object);
            return new JacksonJsonNode(node);
        } catch (Exception e) {
            throw new JsonException("Failed to convert value to tree", e);
        }
    }
}
