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
package io.github.guoshiqiufeng.dify.core.utils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Log masking utility for sensitive information protection
 *
 * @author yanghq
 * @version 2.1.0
 * @since 2026/2/2
 */
public final class LogMaskingUtils {

    private static final String MASK_VALUE = "***MASKED***";
    private static final int DEFAULT_MAX_BODY_LENGTH = 1000;

    /**
     * Default sensitive header names (case-insensitive)
     */
    private static final Set<String> DEFAULT_SENSITIVE_HEADERS = new HashSet<>(Arrays.asList(
            "authorization",
            "api-key",
            "x-api-key",
            "apikey",
            "token",
            "x-auth-token",
            "cookie",
            "set-cookie"
    ));

    /**
     * Default sensitive body field names (case-insensitive)
     */
    private static final Set<String> DEFAULT_SENSITIVE_FIELDS = new HashSet<>(Arrays.asList(
            "password",
            "apikey",
            "api_key",
            "apiKey",  // Support both camelCase and snake_case
            "token",
            "secret",
            "credential",
            "authorization"
    ));

    private LogMaskingUtils() {
        // Utility class
    }

    /**
     * Mask sensitive headers using default sensitive header names
     *
     * @param headers Original headers
     * @return Masked headers
     */
    public static Map<String, List<String>> maskHeaders(Map<String, List<String>> headers) {
        return maskHeaders(headers, DEFAULT_SENSITIVE_HEADERS);
    }

    /**
     * Mask sensitive headers
     *
     * @param headers          Original headers
     * @param sensitiveHeaders Set of sensitive header names (case-insensitive)
     * @return Masked headers
     */
    public static Map<String, List<String>> maskHeaders(Map<String, List<String>> headers,
                                                         Set<String> sensitiveHeaders) {
        if (headers == null || headers.isEmpty()) {
            return headers;
        }

        if (sensitiveHeaders == null || sensitiveHeaders.isEmpty()) {
            return headers;
        }

        // Convert sensitive headers to lowercase for case-insensitive comparison
        Set<String> lowerCaseSensitiveHeaders = sensitiveHeaders.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        return headers.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            String headerName = entry.getKey();
                            if (headerName != null && lowerCaseSensitiveHeaders.contains(headerName.toLowerCase())) {
                                return Collections.singletonList(MASK_VALUE);
                            }
                            return entry.getValue();
                        }
                ));
    }

    /**
     * Mask sensitive body content using default sensitive field names
     *
     * @param body Original body content
     * @return Masked body content
     */
    public static String maskBody(String body) {
        return maskBody(body, DEFAULT_SENSITIVE_FIELDS, DEFAULT_MAX_BODY_LENGTH);
    }

    /**
     * Mask sensitive body content
     *
     * @param body            Original body content
     * @param sensitiveFields Set of sensitive field names (case-insensitive)
     * @param maxLength       Maximum length of body to return (0 = unlimited)
     * @return Masked body content
     */
    public static String maskBody(String body, Set<String> sensitiveFields, int maxLength) {
        if (body == null || body.isEmpty()) {
            return body;
        }

        String maskedBody = body;

        // Mask sensitive fields if specified
        if (sensitiveFields != null && !sensitiveFields.isEmpty()) {
            for (String field : sensitiveFields) {
                // Match JSON field patterns: "field":"value" or "field": "value" or "field" : "value"
                // Use [\s\n\r]* to match any whitespace including newlines
                // Use capturing group to preserve original field name case
                String jsonPattern = "(\"" + field + "\"[\\s\\n\\r]*:[\\s\\n\\r]*)\"[^\"]*\"";
                maskedBody = maskedBody.replaceAll("(?i)" + jsonPattern, "$1\"" + MASK_VALUE + "\"");

                // Match form data patterns: field=value
                // Use capturing group to preserve original field name case
                String formPattern = "(" + field + "=)[^&\\s]*";
                maskedBody = maskedBody.replaceAll("(?i)" + formPattern, "$1" + MASK_VALUE);
            }
        }

        // Truncate if exceeds max length
        if (maxLength > 0 && maskedBody.length() > maxLength) {
            maskedBody = maskedBody.substring(0, maxLength) + "... (truncated)";
        }

        return maskedBody;
    }

    /**
     * Mask single header value
     *
     * @param headerName  Header name
     * @param headerValue Header value
     * @return Masked value if sensitive, original value otherwise, or null if either parameter is null
     */
    public static String maskHeaderValue(String headerName, String headerValue) {
        if (headerName == null || headerValue == null) {
            return headerValue;
        }

        if (DEFAULT_SENSITIVE_HEADERS.contains(headerName.toLowerCase())) {
            return MASK_VALUE;
        }

        return headerValue;
    }

    /**
     * Check if a header name is sensitive
     *
     * @param headerName Header name to check
     * @return true if sensitive, false otherwise
     */
    public static boolean isSensitiveHeader(String headerName) {
        if (headerName == null) {
            return false;
        }
        return DEFAULT_SENSITIVE_HEADERS.contains(headerName.toLowerCase());
    }

    /**
     * Get default sensitive header names
     *
     * @return Unmodifiable set of default sensitive header names
     */
    public static Set<String> getDefaultSensitiveHeaders() {
        return Collections.unmodifiableSet(DEFAULT_SENSITIVE_HEADERS);
    }

    /**
     * Get default sensitive field names
     *
     * @return Unmodifiable set of default sensitive field names
     */
    public static Set<String> getDefaultSensitiveFields() {
        return Collections.unmodifiableSet(DEFAULT_SENSITIVE_FIELDS);
    }
}
