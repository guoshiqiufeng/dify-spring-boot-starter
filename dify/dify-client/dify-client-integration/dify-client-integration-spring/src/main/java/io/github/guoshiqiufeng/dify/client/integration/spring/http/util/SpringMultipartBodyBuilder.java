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
package io.github.guoshiqiufeng.dify.client.integration.spring.http.util;

import io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper;
import io.github.guoshiqiufeng.dify.client.core.http.HttpClientException;
import io.github.guoshiqiufeng.dify.client.core.http.util.MultipartBodyProcessor;
import lombok.experimental.UtilityClass;
import org.springframework.util.LinkedMultiValueMap;

import java.util.Map;

/**
 * Utility class for building Spring multipart request bodies.
 * Handles conversion from MultipartBodyBuilder.Part to Spring's multipart format.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026-01-20
 */
@UtilityClass
public class SpringMultipartBodyBuilder {

    /**
     * Build Spring multipart body from MultipartBodyBuilder.Part map.
     * Handles byte[] to ByteArrayResource conversion and JSON serialization.
     *
     * @param bodyMap    map of multipart parts
     * @param jsonMapper JSON mapper for serialization
     * @param skipNull   whether to skip null values in JSON serialization
     * @return Spring LinkedMultiValueMap for multipart body
     */
    public static LinkedMultiValueMap<String, Object> buildMultipartBody(
            Map<String, io.github.guoshiqiufeng.dify.core.utils.MultipartBodyBuilder.Part> bodyMap,
            JsonMapper jsonMapper,
            Boolean skipNull) {

        LinkedMultiValueMap<String, Object> multipartData = new LinkedMultiValueMap<>();

        for (Map.Entry<String, io.github.guoshiqiufeng.dify.core.utils.MultipartBodyBuilder.Part> entry : bodyMap.entrySet()) {
            io.github.guoshiqiufeng.dify.core.utils.MultipartBodyBuilder.Part part = entry.getValue();
            Object partValue = part.getValue();

            // Convert byte[] to Spring's ByteArrayResource for file uploads
            if (partValue instanceof byte[]) {
                byte[] bytes = (byte[]) partValue;
                String filename = MultipartBodyProcessor.extractFilename(part.getHeader("Content-Disposition"));

                org.springframework.core.io.ByteArrayResource resource =
                    new org.springframework.core.io.ByteArrayResource(bytes) {
                        @Override
                        public String getFilename() {
                            return filename;
                        }
                    };

                multipartData.add(entry.getKey(), resource);
            } else if (partValue instanceof String || partValue instanceof Number || partValue instanceof Boolean) {
                // For simple types, add directly
                multipartData.add(entry.getKey(), partValue);
            } else {
                // For complex objects, serialize to JSON
                try {
                    String jsonValue = (skipNull != null && skipNull) ?
                        jsonMapper.toJsonIgnoreNull(partValue) :
                        jsonMapper.toJson(partValue);
                    multipartData.add(entry.getKey(), jsonValue);
                } catch (Exception e) {
                    throw new HttpClientException("Failed to serialize multipart field to JSON: " + entry.getKey(), e);
                }
            }
        }

        return multipartData;
    }
}
