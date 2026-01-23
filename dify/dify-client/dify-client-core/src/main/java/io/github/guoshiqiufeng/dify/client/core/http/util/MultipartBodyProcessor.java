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
package io.github.guoshiqiufeng.dify.client.core.http.util;

import lombok.experimental.UtilityClass;

import java.util.Map;

/**
 * Utility class for processing multipart request bodies.
 * Provides methods for detecting multipart content and extracting metadata.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026-01-20
 */
@UtilityClass
public class MultipartBodyProcessor {

    /**
     * Enum representing different types of multipart parts.
     */
    public enum PartType {
        /**
         * Binary file data
         */
        FILE,
        /**
         * String value
         */
        STRING,
        /**
         * Numeric value
         */
        NUMBER,
        /**
         * Boolean value
         */
        BOOLEAN,
        /**
         * Complex object (needs JSON serialization)
         */
        OBJECT
    }

    /**
     * Check if the content type indicates a multipart request.
     *
     * @param contentType the Content-Type header value
     * @return true if the content type is multipart/form-data
     */
    public static boolean isMultipartRequest(String contentType) {
        return contentType != null && contentType.toLowerCase().contains("multipart/form-data");
    }

    /**
     * Extract filename from Content-Disposition header.
     * Parses headers like: form-data; name="file"; filename="test.txt"
     *
     * @param contentDisposition Content-Disposition header value
     * @return filename, or "file" if not found
     */
    public static String extractFilename(String contentDisposition) {
        if (contentDisposition == null) {
            return "file";
        }

        // Parse: form-data; name="file"; filename="test.txt"
        int filenameIndex = contentDisposition.indexOf("filename=\"");
        if (filenameIndex != -1) {
            int start = filenameIndex + 10; // length of "filename=\""
            int end = contentDisposition.indexOf("\"", start);
            if (end != -1) {
                return contentDisposition.substring(start, end);
            }
        }

        return "file";
    }

    /**
     * Check if a body map contains multipart body parts.
     * Inspects the first value to determine if it's a MultipartBodyBuilder.Part.
     *
     * @param bodyMap the body map to check
     * @return true if the map contains multipart parts
     */
    public static boolean isMultipartBodyMap(Map<?, ?> bodyMap) {
        if (bodyMap == null || bodyMap.isEmpty()) {
            return false;
        }

        Object firstValue = bodyMap.values().iterator().next();
        return firstValue != null &&
                firstValue.getClass().getName().contains("MultipartBodyBuilder$Part");
    }

    /**
     * Determine the type of a multipart part value.
     * Categorizes the value to determine how it should be processed.
     *
     * @param value the part value to categorize
     * @return the PartType enum value
     */
    public static PartType determinePartType(Object value) {
        if (value == null) {
            return PartType.OBJECT;
        }

        if (value instanceof byte[]) {
            return PartType.FILE;
        }

        if (value instanceof String) {
            return PartType.STRING;
        }

        if (value instanceof Number) {
            return PartType.NUMBER;
        }

        if (value instanceof Boolean) {
            return PartType.BOOLEAN;
        }

        return PartType.OBJECT;
    }

    /**
     * Check if a value should be serialized as JSON.
     * Returns true for complex objects that need JSON serialization.
     *
     * @param value the value to check
     * @return true if the value should be serialized as JSON
     */
    public static boolean shouldSerializeAsJson(Object value) {
        return determinePartType(value) == PartType.OBJECT;
    }
}
