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
package io.github.guoshiqiufeng.dify.client.core.codec.util;

import io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper;
import lombok.experimental.UtilityClass;

/**
 * Utility class for JSON serialization with skipNull support.
 * Provides centralized logic for serializing objects to JSON with optional null value skipping.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026-01-20
 */
@UtilityClass
public class JsonSerializationHelper {

    /**
     * Serialize an object to JSON string.
     * If skipNull is true, null values will be omitted from the output.
     *
     * @param body     the object to serialize
     * @param mapper   the JSON mapper to use
     * @param skipNull whether to skip null values
     * @return JSON string representation
     */
    public static String serialize(Object body, JsonMapper mapper, boolean skipNull) {
        if (body == null) {
            return null;
        }
        return skipNull ? mapper.toJsonIgnoreNull(body) : mapper.toJson(body);
    }

    /**
     * Serialize a multipart field value to JSON string.
     * This is specifically for multipart form data where complex objects need to be serialized.
     *
     * @param fieldName the name of the field (for error messages)
     * @param value     the value to serialize
     * @param mapper    the JSON mapper to use
     * @param skipNull  whether to skip null values
     * @return JSON string representation
     * @throws RuntimeException if serialization fails
     */
    public static String serializeMultipartField(String fieldName, Object value, JsonMapper mapper, boolean skipNull) {
        try {
            return serialize(value, mapper, skipNull);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize multipart field to JSON: " + fieldName, e);
        }
    }
}
