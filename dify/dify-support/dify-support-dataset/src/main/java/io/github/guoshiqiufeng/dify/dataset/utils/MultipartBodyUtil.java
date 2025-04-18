/*
 * Copyright (c) 2025-2025, fubluesky (fubluesky@foxmail.com)
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
package io.github.guoshiqiufeng.dify.dataset.utils;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.guoshiqiufeng.dify.dataset.dto.request.file.FileOperation;
import io.github.guoshiqiufeng.dify.dataset.exception.DiftDatasetException;
import io.github.guoshiqiufeng.dify.dataset.exception.DiftDatasetExceptionEnum;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Utility class for building multipart body requests
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/4/17 17:41
 */
public class MultipartBodyUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private MultipartBodyUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Creates a MultipartBodyBuilder with file and request data
     *
     * @param file    The file to upload
     * @param request The request object containing metadata
     * @return Configured MultipartBodyBuilder instance
     * @throws DiftDatasetException if file processing fails
     */
    public static MultipartBodyBuilder getMultipartBodyBuilder(MultipartFile file, FileOperation request) {
        if (file == null) {
            throw new DiftDatasetException(DiftDatasetExceptionEnum.DIFY_DATA_PARSING_FAILURE);
        }

        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        try {
            // Get file content and type
            byte[] fileContent = file.getBytes();
            String contentType = file.getContentType();
            contentType = (StrUtil.isEmpty(contentType)) ? MediaType.TEXT_PLAIN_VALUE : contentType;

            // Add file part
            builder.part("file", fileContent)
                    .header("Content-Disposition",
                            "form-data; name=\"file\"; filename=\"" + file.getOriginalFilename() + "\"")
                    .header("Content-Type", contentType);
            request.setFile(null);
            // Add JSON data part
            builder.part("data", toJson(request))
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE);

            return builder;
        } catch (IOException e) {
            throw new DiftDatasetException(DiftDatasetExceptionEnum.DIFY_DATA_PARSING_FAILURE);
        }
    }

    /**
     * Converts an object to JSON string
     *
     * @param object The object to convert
     * @return JSON string representation
     * @throws DiftDatasetException if JSON conversion fails
     */
    private static String toJson(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new DiftDatasetException(DiftDatasetExceptionEnum.DIFY_DATA_PARSING_FAILURE);
        }
    }
}
