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
package io.github.guoshiqiufeng.dify.core.logging.masking;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Masking engine implementation
 *
 * @author yanghq
 * @version 2.1.0
 * @since 2026/2/3
 */
public final class MaskingEngine implements MaskingStrategy {

    private static final String MASK_VALUE = "***MASKED***";
    private static final MaskingEngine DEFAULT_INSTANCE = new MaskingEngine(MaskingConfig.createDefault());

    private final MaskingContext context;
    private final List<Tokenizer> tokenizers;

    private MaskingEngine(MaskingConfig config) {
        this.context = new MaskingContext(config);
        this.tokenizers = new ArrayList<>();
        // Register tokenizers in order of preference
        this.tokenizers.add(new JsonTokenizer());
        this.tokenizers.add(new FormTokenizer());
    }

    /**
     * Get default masking engine instance
     *
     * @return default instance
     */
    public static MaskingEngine getDefault() {
        return DEFAULT_INSTANCE;
    }

    /**
     * Create masking engine with custom config
     *
     * @param config masking config
     * @return masking engine
     */
    public static MaskingEngine of(MaskingConfig config) {
        return new MaskingEngine(config);
    }

    @Override
    public Map<String, List<String>> maskHeaders(Map<String, List<String>> headers, MaskingContext context) {
        if (headers == null || headers.isEmpty()) {
            return headers;
        }

        if (!context.isEnabled()) {
            return headers;
        }

        MaskingRuleRegistry registry = context.getRegistry();

        return headers.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            String headerName = entry.getKey();
                            if (headerName != null && registry.isSensitive(headerName)) {
                                return Collections.singletonList(MASK_VALUE);
                            }
                            return entry.getValue();
                        }
                ));
    }

    @Override
    public String maskBody(String body, MaskingContext context) {
        if (body == null || body.isEmpty()) {
            return body;
        }

        if (!context.isEnabled()) {
            return body;
        }

        // Try each tokenizer
        MaskingBuffer buffer = MaskingBuffer.get();
        for (Tokenizer tokenizer : tokenizers) {
            if (tokenizer.supports(null, body)) {
                try {
                    String masked = tokenizer.mask(body, context, context.getRegistry(), buffer);

                    // Apply max length truncation
                    int maxLength = context.getMaxBodyLength();
                    if (maxLength > 0 && masked.length() > maxLength) {
                        return masked.substring(0, maxLength) + "... (truncated)";
                    }

                    return masked;
                } catch (Exception e) {
                    // Tokenizer failed, try next one
                }
            }
        }

        // Fallback: return original body with truncation
        int maxLength = context.getMaxBodyLength();
        if (maxLength > 0 && body.length() > maxLength) {
            return body.substring(0, maxLength) + "... (truncated)";
        }

        return body;
    }

    @Override
    public String maskValue(String fieldName, String value, MaskingContext context) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        if (!context.isEnabled()) {
            return value;
        }

        MaskingRule rule = context.getRegistry().findRule(fieldName);
        if (rule == null) {
            return value;
        }

        return rule.apply(value);
    }

    /**
     * Mask headers with default context
     *
     * @param headers headers
     * @return masked headers
     */
    public Map<String, List<String>> maskHeaders(Map<String, List<String>> headers) {
        return maskHeaders(headers, context);
    }

    /**
     * Mask body with default context
     *
     * @param body body
     * @return masked body
     */
    public String maskBody(String body) {
        return maskBody(body, context);
    }

    /**
     * Mask value with default context
     *
     * @param fieldName field name
     * @param value value
     * @return masked value
     */
    public String maskValue(String fieldName, String value) {
        return maskValue(fieldName, value, context);
    }
}
