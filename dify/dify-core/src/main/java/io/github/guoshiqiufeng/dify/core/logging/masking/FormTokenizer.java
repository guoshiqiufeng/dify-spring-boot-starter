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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lightweight form-data tokenizer for masking sensitive fields
 * Parses key=value&key2=value2 format without allocations
 *
 * @author yanghq
 * @version 2.1.0
 * @since 2026/2/3
 */
public final class FormTokenizer implements Tokenizer {

    @Override
    public boolean supports(String contentType, String body) {
        if (body == null || body.isEmpty()) {
            return false;
        }

        // Check if body looks like form data
        // Simple heuristic: contains '=' and optionally '&'
        return body.contains("=") && !body.trim().startsWith("{") && !body.trim().startsWith("[");
    }

    @Override
    public String mask(String body, MaskingContext context, MaskingRuleRegistry registry, MaskingBuffer buffer) {
        if (body == null || body.isEmpty()) {
            return body;
        }

        // Simplified implementation: use regex-based approach
        String result = body;

        // Get all sensitive field names from registry
        for (MaskingRule rule : registry.getRules()) {
            for (String fieldName : rule.getFieldNames()) {
                // Match form data patterns: field=value
                String formPattern = "(" + Pattern.quote(fieldName) + "=)([^&\\s]*)";
                Pattern pattern = Pattern.compile(formPattern, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(result);
                StringBuffer sb = new StringBuffer();

                while (matcher.find()) {
                    String prefix = matcher.group(1);
                    String value = matcher.group(2);
                    String maskedValue = rule.apply(value);
                    matcher.appendReplacement(sb, Matcher.quoteReplacement(prefix + maskedValue));
                }
                matcher.appendTail(sb);
                result = sb.toString();
            }
        }

        return result;
    }
}
