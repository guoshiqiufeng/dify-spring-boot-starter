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
 * Token for lightweight tokenizer
 *
 * @author yanghq
 * @version 2.1.0
 * @since 2026/2/3
 */
public final class Token {
    private final TokenType type;
    private final int start;
    private final int end;
    private final String value;

    public Token(TokenType type, int start, int end, String value) {
        this.type = type;
        this.start = start;
        this.end = end;
        this.value = value;
    }

    public TokenType getType() {
        return type;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", start=" + start +
                ", end=" + end +
                ", value='" + value + '\'' +
                '}';
    }
}
