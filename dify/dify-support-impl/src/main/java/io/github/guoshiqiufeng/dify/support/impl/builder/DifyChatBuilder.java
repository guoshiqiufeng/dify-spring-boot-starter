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
package io.github.guoshiqiufeng.dify.support.impl.builder;

import io.github.guoshiqiufeng.dify.chat.DifyChat;
import io.github.guoshiqiufeng.dify.chat.client.DifyChatClient;
import io.github.guoshiqiufeng.dify.chat.impl.DifyChatClientImpl;
import io.github.guoshiqiufeng.dify.client.core.web.client.HttpClient;
import io.github.guoshiqiufeng.dify.support.impl.chat.DifyChatDefaultClient;

/**
 * Builder for creating DifyChat and DifyChatClient instances
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025/12/26
 */
public final class DifyChatBuilder {

    private DifyChatBuilder() {
    }

    /**
     * Create a DifyChat instance from a DifyChatClient
     *
     * @param difyChatClient the chat client
     * @return the DifyChat instance
     */
    public static DifyChat create(DifyChatClient difyChatClient) {
        return new DifyChatClientImpl(difyChatClient);
    }

    /**
     * Create a DifyChatClient with the given HTTP client
     *
     * @param httpClient the HTTP client
     * @return the DifyChatClient
     */
    public static DifyChatClient createClient(HttpClient httpClient) {
        return new DifyChatDefaultClient(httpClient);
    }

    /**
     * Create a new builder instance for DifyChatClient
     *
     * @return the builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for DifyChatClient
     */
    public static final class Builder extends BaseDifyBuilder<Builder> {
        /**
         * Build the DifyChatClient
         *
         * @return the DifyChatClient
         */
        public DifyChatClient build() {
            initDefaults();
            HttpClient httpClient = createHttpClient();
            return new DifyChatDefaultClient(httpClient);
        }
    }
}
