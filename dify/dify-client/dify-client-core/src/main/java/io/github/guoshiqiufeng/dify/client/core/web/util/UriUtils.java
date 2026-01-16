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
package io.github.guoshiqiufeng.dify.client.core.web.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * Utility class for URI manipulation and encoding.
 * Provides common methods for URI variable replacement and URL encoding.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025-12-31
 */
public final class UriUtils {

    /**
     * Pattern for matching URI variables in the format {variableName}.
     */
    public static final Pattern URI_VARIABLE_PATTERN = Pattern.compile("\\{([^}]+)}");

    /**
     * Regex pattern for URI variable placeholders used in replaceFirst operations.
     */
    private static final String URI_PLACEHOLDER_REGEX = "\\{[^}]+\\}";

    private UriUtils() {
        // Utility class, no instantiation
    }

    /**
     * Replace URI path variables with provided values in order.
     * Placeholders are in the format {variableName}.
     * Variables are replaced in the order they appear in the path.
     *
     * @param path         the path with placeholders (e.g., "/users/{id}/posts/{postId}")
     * @param uriVariables the values to replace placeholders with
     * @return the path with placeholders replaced
     * @throws IllegalArgumentException if path is null or not enough variables provided to expand all placeholders
     */
    public static String replacePlaceholders(String path, Object... uriVariables) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }

        if (uriVariables == null || uriVariables.length == 0) {
            variablePath(path);
            return path;
        }

        String result = path;
        for (Object param : uriVariables) {
            if (param != null) {
                result = result.replaceFirst(URI_PLACEHOLDER_REGEX, String.valueOf(param));
            }
        }

        // After replacement, check if there are still placeholders left
        variablePath(result);

        return result;
    }

    public static void variablePath(String path) {
        // Check if there are any placeholders that need to be replaced
        if (hasPlaceholders(path)) {
            // Extract the first placeholder name
            java.util.regex.Matcher matcher = URI_VARIABLE_PATTERN.matcher(path);
            if (matcher.find()) {
                String placeholderName = matcher.group(1);
                throw new IllegalArgumentException("Not enough variable values available to expand '" + placeholderName + "'");
            }
        }
    }

    /**
     * URL encode a query parameter value using UTF-8 encoding.
     * Uses StandardCharsets.UTF_8 to avoid UnsupportedEncodingException.
     *
     * @param value the value to encode
     * @return the URL-encoded value
     * @throws IllegalArgumentException if value is null
     */
    public static String encodeQueryParam(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        } catch (java.io.UnsupportedEncodingException e) {
            // This should never happen as UTF-8 is always supported
            throw new RuntimeException("UTF-8 encoding not supported", e);
        }
    }

    /**
     * URL encode a query parameter value, returning empty string if value is null.
     *
     * @param value the value to encode (may be null)
     * @return the URL-encoded value, or empty string if value is null
     */
    public static String encodeQueryParamOrEmpty(String value) {
        if (value == null) {
            return "";
        }
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        } catch (java.io.UnsupportedEncodingException e) {
            // This should never happen as UTF-8 is always supported
            throw new RuntimeException("UTF-8 encoding not supported", e);
        }
    }

    /**
     * Check if a string contains URI variable placeholders.
     *
     * @param path the path to check
     * @return true if the path contains at least one placeholder
     */
    public static boolean hasPlaceholders(String path) {
        return path != null && URI_VARIABLE_PATTERN.matcher(path).find();
    }

    /**
     * Count the number of URI variable placeholders in a path.
     *
     * @param path the path to check
     * @return the number of placeholders found
     */
    public static int countPlaceholders(String path) {
        if (path == null) {
            return 0;
        }

        int count = 0;
        java.util.regex.Matcher matcher = URI_VARIABLE_PATTERN.matcher(path);
        while (matcher.find()) {
            count++;
        }
        return count;
    }
}
