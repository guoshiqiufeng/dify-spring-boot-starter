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
package io.github.guoshiqiufeng.dify.client.integration.spring.file;

import io.github.guoshiqiufeng.dify.core.pojo.DifyFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Converter utility for converting Spring MultipartFile to DifyFile.
 * This class provides integration between Spring's file upload handling
 * and Dify's framework-independent file representation.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025/12/31
 */
public class DifyFileConverter {

    /**
     * Private constructor to prevent instantiation
     */
    private DifyFileConverter() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Convert Spring MultipartFile to DifyFile.
     * This method extracts the file content, filename, and content type
     * from the MultipartFile and creates a DifyFile instance.
     *
     * @param multipartFile Spring MultipartFile to convert
     * @return DifyFile instance
     * @throws IllegalArgumentException if multipartFile is null or empty
     */
    public static DifyFile from(MultipartFile multipartFile) {
        if (multipartFile == null) {
            throw new IllegalArgumentException("MultipartFile cannot be null");
        }
        if (multipartFile.isEmpty()) {
            throw new IllegalArgumentException("MultipartFile cannot be empty");
        }

        String filename = multipartFile.getOriginalFilename();
        String contentType = multipartFile.getContentType();
        byte[] content = null;
        try {
            content = multipartFile.getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new DifyFile(filename, contentType, content);
    }

    /**
     * Convert Spring MultipartFile to DifyFile.
     * Alias method for better API fluency.
     *
     * @param multipartFile Spring MultipartFile to convert
     * @return DifyFile instance
     * @throws IllegalArgumentException if multipartFile is null or empty
     */
    public static DifyFile convert(MultipartFile multipartFile) {
        return from(multipartFile);
    }
}
