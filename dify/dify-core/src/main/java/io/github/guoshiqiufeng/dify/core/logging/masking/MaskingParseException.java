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
 * Exception thrown when a tokenizer fails to parse the input.
 * This indicates a real error (e.g., malformed JSON) and should be logged.
 *
 * @author yanghq
 * @version 2.1.0
 * @since 2026/2/8
 */
public class MaskingParseException extends RuntimeException {

    public MaskingParseException() {
        super();
    }

    public MaskingParseException(String message) {
        super(message);
    }

    public MaskingParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
