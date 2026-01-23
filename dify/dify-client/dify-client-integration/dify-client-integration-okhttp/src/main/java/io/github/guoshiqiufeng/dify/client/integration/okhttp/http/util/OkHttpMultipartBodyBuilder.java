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
package io.github.guoshiqiufeng.dify.client.integration.okhttp.http.util;

import io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper;
import io.github.guoshiqiufeng.dify.client.core.codec.util.JsonSerializationHelper;
import io.github.guoshiqiufeng.dify.client.core.http.HttpClientException;
import io.github.guoshiqiufeng.dify.client.core.http.util.MultipartBodyProcessor;
import lombok.experimental.UtilityClass;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import java.util.Map;

/**
 * Utility class for building OkHttp multipart request bodies.
 * Handles conversion of multipart parts to OkHttp RequestBody format.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026-01-20
 */
@UtilityClass
public class OkHttpMultipartBodyBuilder {

    /**
     * Build OkHttp multipart body from MultipartBodyBuilder.Part map.
     * Handles different part types: files (byte[]), strings, numbers, booleans, and complex objects.
     *
     * @param parts    map of part names to Part objects
     * @param mapper   JSON mapper for serializing complex objects
     * @param skipNull whether to skip null values when serializing to JSON
     * @return OkHttp RequestBody containing the multipart data
     */
    public static RequestBody buildMultipartBody(Map<String, io.github.guoshiqiufeng.dify.core.utils.MultipartBodyBuilder.Part> parts,
                                                 JsonMapper mapper, boolean skipNull) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        for (Map.Entry<String, io.github.guoshiqiufeng.dify.core.utils.MultipartBodyBuilder.Part> entry : parts.entrySet()) {
            io.github.guoshiqiufeng.dify.core.utils.MultipartBodyBuilder.Part part = entry.getValue();
            Object partValue = part.getValue();

            if (partValue instanceof byte[]) {
                // Handle file upload
                byte[] bytes = (byte[]) partValue;
                String filename = MultipartBodyProcessor.extractFilename(part.getHeader("Content-Disposition"));
                String partContentType = part.getHeader("Content-Type");
                MediaType mediaType = partContentType != null ?
                        MediaType.parse(partContentType) : MediaType.parse("application/octet-stream");

                builder.addFormDataPart(entry.getKey(), filename,
                        RequestBody.create(bytes, mediaType));
            } else if (partValue instanceof String) {
                builder.addFormDataPart(entry.getKey(), (String) partValue);
            } else if (partValue instanceof Number || partValue instanceof Boolean) {
                builder.addFormDataPart(entry.getKey(), String.valueOf(partValue));
            } else {
                // For complex objects, serialize to JSON
                try {
                    String json = JsonSerializationHelper.serialize(partValue, mapper, skipNull);
                    builder.addFormDataPart(entry.getKey(), json);
                } catch (Exception e) {
                    throw new HttpClientException("Failed to serialize multipart field to JSON: " + entry.getKey(), e);
                }
            }
        }

        return builder.build();
    }
}
