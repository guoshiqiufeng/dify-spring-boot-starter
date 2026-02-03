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

/**
 * Tokenizer interface for lightweight parsing
 *
 * @author yanghq
 * @version 2.1.0
 * @since 2026/2/3
 */
public interface Tokenizer {

    /**
     * Check if this tokenizer supports the given content
     *
     * @param contentType content type (optional)
     * @param body body content
     * @return true if supports
     */
    boolean supports(String contentType, String body);

    /**
     * Mask sensitive fields in the body
     *
     * @param body body content
     * @param context masking context
     * @param registry rule registry
     * @param buffer reusable buffer
     * @return masked body
     */
    String mask(String body, MaskingContext context, MaskingRuleRegistry registry, MaskingBuffer buffer);
}
