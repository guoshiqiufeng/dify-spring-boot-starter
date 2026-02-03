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
 * Lightweight JSON tokenizer for masking sensitive fields
 * Uses state machine to scan JSON without creating objects for better performance
 *
 * @author yanghq
 * @version 2.1.0
 * @since 2026/2/3
 */
public final class JsonTokenizer implements Tokenizer {

    private enum State {
        START,
        IN_OBJECT,
        IN_ARRAY,
        FIELD_NAME,
        AFTER_COLON,
        STRING_VALUE,
        OTHER_VALUE,
        AFTER_VALUE
    }

    @Override
    public boolean supports(String contentType, String body) {
        if (body == null || body.isEmpty()) {
            return false;
        }

        // Check if body looks like JSON
        String trimmed = body.trim();
        return (trimmed.startsWith("{") && trimmed.endsWith("}"))
                || (trimmed.startsWith("[") && trimmed.endsWith("]"));
    }

    @Override
    public String mask(String body, MaskingContext context, MaskingRuleRegistry registry, MaskingBuffer buffer) {
        if (body == null || body.isEmpty()) {
            return body;
        }

        char[] chars = body.toCharArray();
        int length = chars.length;
        int i = 0;

        State state = State.START;
        String currentFieldName = null;
        int depth = 0;

        while (i < length) {
            char ch = chars[i];

            switch (state) {
                case START:
                    if (ch == '{') {
                        buffer.append(ch);
                        state = State.IN_OBJECT;
                        depth++;
                    } else if (ch == '[') {
                        buffer.append(ch);
                        state = State.IN_ARRAY;
                        depth++;
                    } else if (!Character.isWhitespace(ch)) {
                        // Not valid JSON, fallback to original
                        return body;
                    } else {
                        buffer.append(ch);
                    }
                    i++;
                    break;

                case IN_OBJECT:
                    if (ch == '"') {
                        // Start of field name
                        int fieldEnd = findStringEnd(chars, i + 1);
                        if (fieldEnd > i + 1) {
                            currentFieldName = new String(chars, i + 1, fieldEnd - i - 1);
                            buffer.append('"').append(currentFieldName).append('"');
                            i = fieldEnd + 1;
                            state = State.FIELD_NAME;
                        } else {
                            buffer.append(ch);
                            i++;
                        }
                    } else if (ch == '{') {
                        buffer.append(ch);
                        depth++;
                        i++;
                    } else if (ch == '[') {
                        buffer.append(ch);
                        state = State.IN_ARRAY;
                        depth++;
                        i++;
                    } else if (ch == '}') {
                        buffer.append(ch);
                        depth--;
                        state = depth > 0 ? State.AFTER_VALUE : State.START;
                        i++;
                    } else {
                        buffer.append(ch);
                        i++;
                    }
                    break;

                case IN_ARRAY:
                    if (ch == '"') {
                        // String value in array (not a field name)
                        int stringEnd = findStringEnd(chars, i + 1);
                        if (stringEnd > i + 1) {
                            buffer.append('"');
                            buffer.append(body, i + 1, stringEnd);
                            buffer.append('"');
                            i = stringEnd + 1;
                            state = State.AFTER_VALUE;
                        } else {
                            buffer.append(ch);
                            i++;
                        }
                    } else if (ch == '{') {
                        buffer.append(ch);
                        state = State.IN_OBJECT;
                        depth++;
                        i++;
                    } else if (ch == '[') {
                        buffer.append(ch);
                        depth++;
                        i++;
                    } else if (ch == ']') {
                        buffer.append(ch);
                        depth--;
                        state = depth > 0 ? State.AFTER_VALUE : State.START;
                        i++;
                    } else {
                        buffer.append(ch);
                        i++;
                    }
                    break;

                case FIELD_NAME:
                    if (ch == ':') {
                        buffer.append(ch);
                        state = State.AFTER_COLON;
                    } else {
                        buffer.append(ch);
                    }
                    i++;
                    break;

                case AFTER_COLON:
                    if (ch == '"') {
                        // String value - check if field is sensitive
                        int stringEnd = findStringEnd(chars, i + 1);
                        if (stringEnd > i + 1) {
                            String value = new String(chars, i + 1, stringEnd - i - 1);

                            if (currentFieldName != null && registry.isSensitive(currentFieldName)) {
                                MaskingRule rule = registry.findRule(currentFieldName);
                                String maskedValue = rule != null ? rule.apply(value) : "***MASKED***";
                                buffer.append('"').append(maskedValue).append('"');
                            } else {
                                buffer.append('"').append(value).append('"');
                            }

                            i = stringEnd + 1;
                            state = State.AFTER_VALUE;
                            currentFieldName = null;
                        } else {
                            buffer.append(ch);
                            i++;
                        }
                    } else if (ch == '{') {
                        buffer.append(ch);
                        state = State.IN_OBJECT;
                        currentFieldName = null;
                        depth++;
                        i++;
                    } else if (ch == '[') {
                        buffer.append(ch);
                        state = State.IN_ARRAY;
                        currentFieldName = null;
                        depth++;
                        i++;
                    } else if (!Character.isWhitespace(ch)) {
                        // Other value (number, boolean, null)
                        state = State.OTHER_VALUE;
                    } else {
                        buffer.append(ch);
                        i++;
                    }
                    break;

                case OTHER_VALUE:
                    // Find end of value
                    int valueEnd = i;
                    while (valueEnd < length && !isValueTerminator(chars[valueEnd])) {
                        valueEnd++;
                    }
                    buffer.append(body, i, valueEnd);
                    i = valueEnd;
                    state = State.AFTER_VALUE;
                    currentFieldName = null;
                    break;

                case AFTER_VALUE:
                    if (ch == ',') {
                        buffer.append(ch);
                        // Determine next state based on depth
                        state = State.IN_OBJECT; // Simplified, could be IN_ARRAY
                    } else if (ch == '}') {
                        buffer.append(ch);
                        depth--;
                        state = depth > 0 ? State.AFTER_VALUE : State.START;
                    } else if (ch == ']') {
                        buffer.append(ch);
                        depth--;
                        state = depth > 0 ? State.AFTER_VALUE : State.START;
                    } else {
                        buffer.append(ch);
                    }
                    i++;
                    break;

                default:
                    buffer.append(ch);
                    i++;
            }
        }

        return buffer.toString();
    }

    /**
     * Find the end of a JSON string (closing quote)
     * Handles escaped quotes
     */
    private int findStringEnd(char[] chars, int start) {
        int i = start;
        while (i < chars.length) {
            char ch = chars[i];
            if (ch == '"') {
                // Check if escaped
                int backslashCount = 0;
                int j = i - 1;
                while (j >= start && chars[j] == '\\') {
                    backslashCount++;
                    j--;
                }
                // If even number of backslashes, quote is not escaped
                if (backslashCount % 2 == 0) {
                    return i;
                }
            }
            i++;
        }
        return -1; // No closing quote found
    }

    /**
     * Check if character is a value terminator
     */
    private boolean isValueTerminator(char ch) {
        return ch == ',' || ch == '}' || ch == ']' || Character.isWhitespace(ch);
    }
}
