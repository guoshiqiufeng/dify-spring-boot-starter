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

import java.util.List;
import java.util.Map;

/**
 * Masking strategy interface
 *
 * @author yanghq
 * @version 2.1.0
 * @since 2026/2/3
 */
public interface MaskingStrategy {

    /**
     * Mask sensitive headers
     *
     * @param headers headers
     * @param context masking context
     * @return masked headers
     */
    Map<String, List<String>> maskHeaders(Map<String, List<String>> headers, MaskingContext context);

    /**
     * Mask sensitive body content
     *
     * @param body body content
     * @param context masking context
     * @return masked body
     */
    String maskBody(String body, MaskingContext context);

    /**
     * Mask single value
     *
     * @param fieldName field name
     * @param value value
     * @param context masking context
     * @return masked value
     */
    String maskValue(String fieldName, String value, MaskingContext context);
}
