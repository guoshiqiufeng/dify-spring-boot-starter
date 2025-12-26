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

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.*;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Gson TypeAdapterFactory that adds support for Jackson annotations
 * <p>
 * Supports @JsonProperty and @JsonAlias annotations:
 * - @JsonProperty: Specifies the JSON field name for serialization/deserialization
 * - @JsonAlias: Specifies alternative names for deserialization (read-only)
 * </p>
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025-12-31
 */
public class JacksonAnnotationTypeAdapterFactory implements TypeAdapterFactory {

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<? super T> rawType = type.getRawType();

        // Only process custom classes, skip primitives and standard library classes
        if (rawType.isPrimitive() || rawType.isArray() || rawType.isEnum() ||
                rawType.getName().startsWith("java.") || rawType.getName().startsWith("javax.")) {
            return null;
        }

        // Build field mapping information
        Map<String, FieldInfo> serializationMap = new HashMap<>();
        Map<String, FieldInfo> deserializationMap = new HashMap<>();

        buildFieldMappings(rawType, serializationMap, deserializationMap);

        // If no Jackson annotations found, use default adapter
        if (serializationMap.isEmpty() && deserializationMap.isEmpty()) {
            return null;
        }

        TypeAdapter<T> defaultAdapter = gson.getDelegateAdapter(this, type);
        return new JacksonAnnotationTypeAdapter<>(defaultAdapter, serializationMap, deserializationMap);
    }

    /**
     * Build field mapping information from class hierarchy
     */
    private void buildFieldMappings(Class<?> clazz, Map<String, FieldInfo> serializationMap,
                                    Map<String, FieldInfo> deserializationMap) {
        if (clazz == null || clazz == Object.class) {
            return;
        }

        // Process parent class first
        buildFieldMappings(clazz.getSuperclass(), serializationMap, deserializationMap);

        // Process current class fields
        for (Field field : clazz.getDeclaredFields()) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) ||
                    java.lang.reflect.Modifier.isTransient(field.getModifiers())) {
                continue;
            }

            String fieldName = field.getName();
            JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
            JsonAlias jsonAlias = field.getAnnotation(JsonAlias.class);

            // Handle @JsonProperty
            if (jsonProperty != null && !jsonProperty.value().isEmpty()) {
                String jsonName = jsonProperty.value();
                FieldInfo fieldInfo = new FieldInfo(fieldName, jsonName);

                // For serialization: field name -> JSON name
                serializationMap.put(fieldName, fieldInfo);

                // For deserialization: JSON name -> field name
                deserializationMap.put(jsonName, fieldInfo);
            }

            // Handle @JsonAlias (only for deserialization)
            if (jsonAlias != null) {
                for (String alias : jsonAlias.value()) {
                    FieldInfo fieldInfo = new FieldInfo(fieldName, alias);
                    deserializationMap.put(alias, fieldInfo);
                }
            }
        }
    }

    /**
     * Field mapping information
     */
    private static class FieldInfo {
        final String fieldName;
        final String jsonName;

        FieldInfo(String fieldName, String jsonName) {
            this.fieldName = fieldName;
            this.jsonName = jsonName;
        }
    }

    /**
     * Custom TypeAdapter that handles Jackson annotation mapping
     */
    private static class JacksonAnnotationTypeAdapter<T> extends TypeAdapter<T> {
        private final TypeAdapter<T> delegate;
        private final Map<String, FieldInfo> serializationMap;
        private final Map<String, FieldInfo> deserializationMap;

        JacksonAnnotationTypeAdapter(TypeAdapter<T> delegate,
                                     Map<String, FieldInfo> serializationMap,
                                     Map<String, FieldInfo> deserializationMap) {
            this.delegate = delegate;
            this.serializationMap = serializationMap;
            this.deserializationMap = deserializationMap;
        }

        @Override
        public void write(JsonWriter out, T value) throws IOException {
            if (serializationMap.isEmpty()) {
                delegate.write(out, value);
                return;
            }

            // Serialize to JsonElement first, then transform field names
            JsonElement element = delegate.toJsonTree(value);

            if (element.isJsonObject()) {
                JsonObject transformed = transformForSerialization(element.getAsJsonObject());
                Streams.write(transformed, out);
            } else {
                Streams.write(element, out);
            }
        }

        @Override
        public T read(JsonReader in) throws IOException {
            if (deserializationMap.isEmpty()) {
                return delegate.read(in);
            }

            // Read JSON into JsonElement, then transform field names
            JsonElement element = Streams.parse(in);

            if (element.isJsonObject()) {
                JsonObject transformed = transformForDeserialization(element.getAsJsonObject());
                return delegate.fromJsonTree(transformed);
            } else {
                return delegate.fromJsonTree(element);
            }
        }

        /**
         * Transform field names for serialization (field name -> JSON name)
         */
        private JsonObject transformForSerialization(JsonObject original) {
            JsonObject transformed = new JsonObject();

            for (Map.Entry<String, JsonElement> entry : original.entrySet()) {
                String fieldName = entry.getKey();
                JsonElement value = entry.getValue();

                FieldInfo fieldInfo = serializationMap.get(fieldName);
                String outputName = fieldInfo != null ? fieldInfo.jsonName : fieldName;

                transformed.add(outputName, value);
            }

            return transformed;
        }

        /**
         * Transform field names for deserialization (JSON name -> field name)
         */
        private JsonObject transformForDeserialization(JsonObject original) {
            JsonObject transformed = new JsonObject();

            for (Map.Entry<String, JsonElement> entry : original.entrySet()) {
                String jsonName = entry.getKey();
                JsonElement value = entry.getValue();

                FieldInfo fieldInfo = deserializationMap.get(jsonName);
                String fieldName = fieldInfo != null ? fieldInfo.fieldName : jsonName;

                // Avoid duplicate field assignments (prefer explicit mapping)
                if (!transformed.has(fieldName)) {
                    transformed.add(fieldName, value);
                }
            }

            return transformed;
        }
    }
}
