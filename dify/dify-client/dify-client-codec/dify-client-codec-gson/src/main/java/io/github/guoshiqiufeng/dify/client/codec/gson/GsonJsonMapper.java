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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import io.github.guoshiqiufeng.dify.client.core.codec.*;
import io.github.guoshiqiufeng.dify.client.core.http.TypeReference;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

/**
 * Gson JSON 实现
 * <p>
 * 基于 Google Gson 实现序列化接口
 * 支持 Jackson 注解如 @JsonProperty, @JsonAlias
 * 适用于需要使用 Gson 但项目中已有 Jackson 注解的场景
 * </p>
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025/12/30
 */
public class GsonJsonMapper implements JsonMapper {

    private static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapterFactory(new JacksonAnnotationTypeAdapterFactory())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .create();

    private static final Gson GSON_IGNORE_NULL = new GsonBuilder()
            .registerTypeAdapterFactory(new JacksonAnnotationTypeAdapterFactory())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .create();

    private static final GsonJsonMapper INSTANCE = new GsonJsonMapper();

    public static GsonJsonMapper getInstance() {
        return INSTANCE;
    }

    /**
     * Get the underlying Gson instance for advanced configuration
     *
     * @return Gson instance
     */
    public static Gson getGson() {
        return GSON;
    }

    @Override
    public String toJson(Object object) throws JsonException {
        try {
            return GSON.toJson(object);
        } catch (Exception e) {
            throw new JsonException("Failed to serialize object to JSON", e);
        }
    }

    @Override
    public String toJsonIgnoreNull(Object object) throws JsonException {
        try {
            return GSON_IGNORE_NULL.toJson(object);
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

            return GSON.fromJson(json, clazz);
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
            return GSON.fromJson(json, type);
        } catch (Exception e) {
            throw new JsonException("Failed to deserialize JSON to " + typeReference.getType(), e);
        }
    }

    @Override
    public JsonNode parseTree(String json) throws JsonException {
        try {
            com.google.gson.JsonElement element = JsonParser.parseString(json);
            return new GsonJsonNode(element);
        } catch (Exception e) {
            throw new JsonException("Failed to parse JSON string", e);
        }
    }

    @Override
    public <T> T treeToValue(JsonNode node, Class<T> clazz) throws JsonException {
        try {
            if (node == null || node.isNull()) {
                return null;
            }
            com.google.gson.JsonElement element = (com.google.gson.JsonElement) node.unwrap();
            return GSON.fromJson(element, clazz);
        } catch (Exception e) {
            throw new JsonException("Failed to convert tree to " + clazz.getName(), e);
        }
    }

    @Override
    public JsonNode valueToTree(Object object) throws JsonException {
        try {
            if (object == null) {
                return new GsonJsonNode(com.google.gson.JsonNull.INSTANCE);
            }
            com.google.gson.JsonElement element = GSON.toJsonTree(object);
            return new GsonJsonNode(element);
        } catch (Exception e) {
            throw new JsonException("Failed to convert value to tree", e);
        }
    }
}
