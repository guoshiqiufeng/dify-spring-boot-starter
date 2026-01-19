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

/**
 * Custom JSON deserializer interface
 * <p>
 * Implement this interface to provide custom deserialization logic.
 * Used in conjunction with {@link JsonDeserialize} annotation.
 * </p>
 *
 * @param <T> the type of object to deserialize
 * @author yanghq
 * @version 2.0.0
 * @since 2026/01/09
 */
public interface JsonDeserializer<T> {

    /**
     * Deserialize a JSON node to an object
     *
     * @param root       the JSON node to deserialize
     * @param jsonMapper the JSON mapper for additional operations
     * @return the deserialized object
     * @throws JsonException if deserialization fails
     */
    T deserialize(JsonNode root, JsonMapper jsonMapper) throws JsonException;
}
