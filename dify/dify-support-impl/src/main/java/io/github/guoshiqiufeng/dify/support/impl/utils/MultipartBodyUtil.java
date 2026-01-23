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
package io.github.guoshiqiufeng.dify.support.impl.utils;

import io.github.guoshiqiufeng.dify.client.core.http.MediaType;
import io.github.guoshiqiufeng.dify.core.pojo.DifyFile;
import io.github.guoshiqiufeng.dify.core.utils.MultipartBodyBuilder;
import io.github.guoshiqiufeng.dify.core.utils.StrUtil;
import io.github.guoshiqiufeng.dify.dataset.dto.request.file.FileOperation;
import io.github.guoshiqiufeng.dify.dataset.exception.DiftDatasetException;
import io.github.guoshiqiufeng.dify.dataset.exception.DiftDatasetExceptionEnum;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Utility class for building multipart body requests
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025/12/31 09:41
 */
public class MultipartBodyUtil {

    private MultipartBodyUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Creates a MultipartBodyBuilder with file and request data
     *
     * @param file The file to upload
     * @return Configured MultipartBodyBuilder instance
     * @throws DiftDatasetException if file processing fails
     */
    public static MultipartBodyBuilder getMultipartBodyBuilder(DifyFile file) {
        if (file == null) {
            throw new DiftDatasetException(DiftDatasetExceptionEnum.DIFY_DATA_PARSING_FAILURE);
        }

        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        // Get file content and type
        byte[] fileContent = file.getContent();
        String contentType = file.getContentType();
        contentType = (StrUtil.isEmpty(contentType)) ? MediaType.TEXT_PLAIN : contentType;

        // Add file part
        builder.part("file", fileContent)
                .header("Content-Disposition",
                        "form-data; name=\"file\"; filename=\"" + file.getFilename() + "\"")
                .header("Content-Type", contentType);

        return builder;
    }

    public static MultipartBodyBuilder getMultipartBodyBuilderForAudio(DifyFile file) {
        if (file == null) {
            throw new DiftDatasetException(DiftDatasetExceptionEnum.DIFY_DATA_PARSING_FAILURE);
        }

        Set<String> supportedAudioTypes = new HashSet<>();
        supportedAudioTypes.add("audio/mp3");
        supportedAudioTypes.add("audio/m4a");
        supportedAudioTypes.add("audio/wav");
        supportedAudioTypes.add("audio/amr");
        supportedAudioTypes.add("audio/mpga");

        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        // Get file content and type
        byte[] fileContent = file.getContent();
        String contentType = Optional.ofNullable(file.getContentType())
                .filter(supportedAudioTypes::contains)
                .orElse("audio/mp3");

        // Add file part
        builder.part("file", fileContent)
                .header("Content-Disposition",
                        "form-data; name=\"file\"; filename=\"" + file.getFilename() + "\"")
                .header("Content-Type", contentType);

        return builder;
    }

    public static MultipartBodyBuilder getMultipartBodyBuilderByUser(DifyFile file, String userId) {
        MultipartBodyBuilder builder = getMultipartBodyBuilder(file);
        builder.part("user", userId);
        return builder;
    }

    /**
     * Creates a MultipartBodyBuilder with file and request data
     *
     * @param file    The file to upload
     * @param request The request object containing metadata
     * @return Configured MultipartBodyBuilder instance
     * @throws DiftDatasetException if file processing fails
     */
    public static MultipartBodyBuilder getMultipartBodyBuilder(DifyFile file, FileOperation request) {
        MultipartBodyBuilder builder = getMultipartBodyBuilder(file);
        request.setFile(null);
        builder.part("data", request)
                .header("Content-Type", MediaType.APPLICATION_JSON);

        return builder;
    }
}
