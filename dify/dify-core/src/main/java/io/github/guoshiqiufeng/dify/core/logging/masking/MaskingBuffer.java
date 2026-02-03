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
 * Reusable buffer for masking operations to avoid GC pressure
 *
 * @author yanghq
 * @version 2.1.0
 * @since 2026/2/3
 */
public final class MaskingBuffer {

    private static final ThreadLocal<MaskingBuffer> THREAD_LOCAL = ThreadLocal.withInitial(MaskingBuffer::new);

    private final StringBuilder builder;

    private MaskingBuffer() {
        this.builder = new StringBuilder(2048);
    }

    /**
     * Get thread-local masking buffer
     *
     * @return masking buffer
     */
    public static MaskingBuffer get() {
        MaskingBuffer buffer = THREAD_LOCAL.get();
        buffer.clear();
        return buffer;
    }

    /**
     * Clear the buffer
     */
    public void clear() {
        builder.setLength(0);
    }

    /**
     * Append string
     *
     * @param str string
     * @return this buffer
     */
    public MaskingBuffer append(String str) {
        builder.append(str);
        return this;
    }

    /**
     * Append char
     *
     * @param ch char
     * @return this buffer
     */
    public MaskingBuffer append(char ch) {
        builder.append(ch);
        return this;
    }

    /**
     * Append substring
     *
     * @param str string
     * @param start start index
     * @param end end index
     * @return this buffer
     */
    public MaskingBuffer append(String str, int start, int end) {
        builder.append(str, start, end);
        return this;
    }

    /**
     * Get the result string
     *
     * @return result string
     */
    public String toString() {
        return builder.toString();
    }

    /**
     * Get current length
     *
     * @return length
     */
    public int length() {
        return builder.length();
    }
}
