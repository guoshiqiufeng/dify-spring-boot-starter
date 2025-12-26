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
import io.github.guoshiqiufeng.dify.support.impl.dataset.DifyDatasetDefaultClient;
import io.github.guoshiqiufeng.dify.dataset.DifyDataset;
import io.github.guoshiqiufeng.dify.dataset.client.DifyDatasetClient;
import io.github.guoshiqiufeng.dify.dataset.impl.DifyDatasetClientImpl;

/**
 * Builder for creating DifyDataset and DifyDatasetClient instances
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025/12/26
 */
public class DifyDatasetBuilder {

    private DifyDatasetBuilder() {
    }

    /**
     * Create a DifyDataset instance from a DifyDatasetClient
     *
     * @param difyDatasetClient the dataset client
     * @return the DifyDataset instance
     */
    public static DifyDataset create(DifyDatasetClient difyDatasetClient) {
        return new DifyDatasetClientImpl(difyDatasetClient);
    }

    /**
     * Builder for DifyDatasetClient
     */
    public static class DifyDatasetClientBuilder {
        private DifyDatasetClientBuilder() {
        }

        /**
         * Create a DifyDatasetClient with the given HTTP client
         *
         * @param httpClient the HTTP client
         * @return the DifyDatasetClient
         */
        public static DifyDatasetClient create(HttpClient httpClient) {
            return new DifyDatasetDefaultClient(httpClient);
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
         * Builder class for DifyDatasetClient
         */
        public static class Builder extends BaseDifyBuilder<Builder> {
            private String apiKey;

            /**
             * Set the API key for authentication
             *
             * @param apiKey the API key
             * @return the builder
             */
            public Builder apiKey(String apiKey) {
                this.apiKey = apiKey;
                return this;
            }

            /**
             * Build the DifyDatasetClient
             *
             * @return the DifyDatasetClient
             */
            public DifyDatasetClient build() {
                initDefaults();

                HttpClient httpClient = createHttpClient();

                // If apiKey is provided, we could wrap the HttpClient to add default headers
                // For now, we'll just create the client and let users pass apiKey in requests
                return new DifyDatasetDefaultClient(httpClient);
            }
        }
    }
}
