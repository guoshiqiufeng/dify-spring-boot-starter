/*
 * Copyright (c) 2025-2025, fubluesky (fubluesky@foxmail.com)
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

import io.github.guoshiqiufeng.dify.client.core.web.client.HttpClient;
import io.github.guoshiqiufeng.dify.client.core.http.HttpClientFactory;
import io.github.guoshiqiufeng.dify.support.impl.server.DifyServerDefaultClient;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.server.DifyServer;
import io.github.guoshiqiufeng.dify.server.client.BaseDifyServerToken;
import io.github.guoshiqiufeng.dify.server.client.DifyServerClient;
import io.github.guoshiqiufeng.dify.server.client.DifyServerTokenDefault;
import io.github.guoshiqiufeng.dify.server.impl.DifyServerClientImpl;

/**
 * Builder for creating DifyServer and DifyServerClient instances
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025/12/26
 */
public class DifyServerBuilder {

    private DifyServerBuilder() {
    }

    /**
     * Create a DifyServer instance from a DifyServerClient
     *
     * @param difyServerClient the server client
     * @return the DifyServer instance
     */
    public static DifyServer create(DifyServerClient difyServerClient) {
        return new DifyServerClientImpl(difyServerClient);
    }

    /**
     * Builder for DifyServerClient
     */
    public static class DifyServerClientBuilder {
        private DifyServerClientBuilder() {
        }

        public static DifyServerClient create(HttpClient httpClient, DifyProperties.Server difyServerProperties) {
            return new DifyServerDefaultClient(httpClient, difyServerProperties);
        }

        public static DifyServerClient create(DifyProperties.Server difyServerProperties, String baseUrl, HttpClientFactory httpClientFactory) {
            return new DifyServerDefaultClient(difyServerProperties, new DifyServerTokenDefault(),  baseUrl,
                    new DifyProperties.ClientConfig(), httpClientFactory);
        }

        /**
         * Create a DifyServerClient with the given HTTP client
         *
         * @param httpClient the HTTP client
         * @return the DifyServerClient
         */
        public static DifyServerClient create(HttpClient httpClient) {
            return new DifyServerDefaultClient(httpClient, null);
        }

        /**
         * Create a DifyServerClient with the given HTTP client and token manager
         *
         * @param httpClient      the HTTP client
         * @param difyServerToken the server token manager
         * @return the DifyServerClient
         */
        public static DifyServerClient create(HttpClient httpClient, BaseDifyServerToken difyServerToken) {
            return new DifyServerDefaultClient(httpClient, null, difyServerToken);
        }

        /**
         * Create a new builder instance
         *
         * @return the builder
         */
        public static Builder builder() {
            return new Builder();
        }

        /**
         * Builder class for DifyServerClient
         */
        public static class Builder extends BaseDifyBuilder<Builder> {
            private DifyProperties.Server difyServerProperties;
            private BaseDifyServerToken difyServerToken;

            /**
             * Set server properties
             *
             * @param difyServerProperties the server properties
             * @return the builder
             */
            public Builder serverProperties(DifyProperties.Server difyServerProperties) {
                this.difyServerProperties = difyServerProperties;
                return this;
            }

            /**
             * Set server token manager
             *
             * @param difyServerToken the server token manager
             * @return the builder
             */
            public Builder serverToken(BaseDifyServerToken difyServerToken) {
                this.difyServerToken = difyServerToken;
                return this;
            }

            /**
             * Build the DifyServerClient
             *
             * @return the DifyServerClient
             */
            public DifyServerClient build() {
                initDefaults();

                if (difyServerToken == null) {
                    difyServerToken = new DifyServerTokenDefault();
                }

                HttpClient httpClient = createHttpClient();
                return new DifyServerDefaultClient(httpClient, difyServerProperties, difyServerToken);
            }
        }
    }
}
