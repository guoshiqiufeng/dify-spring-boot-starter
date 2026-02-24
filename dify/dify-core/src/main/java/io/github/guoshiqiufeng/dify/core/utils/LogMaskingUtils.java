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

import io.github.guoshiqiufeng.dify.core.logging.masking.MaskingConfig;
import io.github.guoshiqiufeng.dify.core.logging.masking.MaskingEngine;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Log masking utility for sensitive information protection
 * <p>
 * This class provides backward-compatible API while delegating to the new MaskingEngine.
 * The new engine supports partial masking (e.g., phone numbers showing first 3 and last 4 digits)
 * and uses lightweight tokenizers for better performance.
 * </p>
 *
 * @author yanghq
 * @version 2.1.0
 * @since 2026/2/2
 */
@Slf4j
@UtilityClass
public final class LogMaskingUtils {

    private static final String MASK_VALUE = "***MASKED***";

    /**
     * Default masking engine instance
     */
    private static final MaskingEngine DEFAULT_ENGINE = MaskingEngine.getDefault();

    /**
     * Mask sensitive headers using default sensitive header names
     * <p>
     * This method now delegates to MaskingEngine for better performance.
     * </p>
     *
     * @param headers Original headers
     * @return Masked headers
     */
    public static Map<String, List<String>> maskHeaders(Map<String, List<String>> headers) {
        return DEFAULT_ENGINE.maskHeaders(headers);
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
     * <p>
     * This method now delegates to MaskingEngine which uses lightweight tokenizers
     * for better performance and supports partial masking (e.g., phone numbers).
     * </p>
     *
     * @param body Original body content
     * @return Masked body content
     */
    public static String maskBody(String body) {
        return DEFAULT_ENGINE.maskBody(body);
    }

    /**
     * Create a custom masking engine with specific configuration
     * <p>
     * This allows advanced users to customize masking behavior, including:
     * <ul>
     *   <li>Custom sensitive field rules</li>
     *   <li>Partial masking patterns (e.g., phone numbers, ID cards)</li>
     *   <li>Maximum body length</li>
     * </ul>
     * </p>
     *
     * @param config masking configuration
     * @return custom masking engine
     * @since 2.1.0
     */
    public static MaskingEngine createEngine(MaskingConfig config) {
        return MaskingEngine.of(config);
    }

    /**
     * Get the default masking engine
     *
     * @return default masking engine
     * @since 2.1.0
     */
    public static MaskingEngine getDefaultEngine() {
        return DEFAULT_ENGINE;
    }

    /**
     * Mask sensitive query parameters in URL.
     * Supports case-insensitive matching and URL-encoded parameters.
     * <p>
     * This method masks common sensitive parameters including:
     * <ul>
     *   <li>API keys: api_key, apiKey, api-key</li>
     *   <li>Tokens: token, access_token, accessToken, refresh_token, refreshToken, bearer_token, bearerToken, session_token, sessionToken</li>
     *   <li>Credentials: password, secret, authorization, auth</li>
     * </ul>
     * </p>
     *
     * @param url the URL to mask
     * @return masked URL with sensitive parameters replaced with ***
     * @since 2.1.0
     */
    public static String maskUrl(String url) {
        if (url == null || !url.contains("?")) {
            return url;
        }

        try {
            int queryStart = url.indexOf('?');
            String baseUrl = url.substring(0, queryStart);
            String queryString = url.substring(queryStart + 1);

            // Comprehensive pattern covering all variants (case-insensitive)
            // Matches: api_key, apiKey, api-key, token, password, secret, authorization, auth,
            // access_token, accessToken, access-token, refresh_token, refreshToken, refresh-token,
            // bearer_token, bearerToken, bearer-token, session_token, sessionToken, session-token
            String maskedQuery = queryString.replaceAll(
                    "(?i)(api[_-]?key|token|password|secret|authorization|auth|access[_-]?token|refresh[_-]?token|bearer[_-]?token|session[_-]?token)=([^&]*)",
                    "$1=***"
            );

            return baseUrl + "?" + maskedQuery;
        } catch (Exception e) {
            // SECURITY: Don't return original URL - strip query params
            int queryStart = url.indexOf('?');
            String baseUrl = url.substring(0, queryStart);
            log.warn("Failed to mask URL query parameters, returning base URL only");
            return baseUrl + "?***MASKED***";
        }
    }
}
