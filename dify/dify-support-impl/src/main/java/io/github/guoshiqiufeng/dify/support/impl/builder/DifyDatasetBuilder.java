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

import io.github.guoshiqiufeng.dify.client.core.http.HttpHeaders;
import io.github.guoshiqiufeng.dify.client.core.web.client.HttpClient;
import io.github.guoshiqiufeng.dify.core.utils.StrUtil;
import io.github.guoshiqiufeng.dify.dataset.DifyDataset;
import io.github.guoshiqiufeng.dify.dataset.client.DifyDatasetClient;
import io.github.guoshiqiufeng.dify.dataset.impl.DifyDatasetClientImpl;
import io.github.guoshiqiufeng.dify.support.impl.dataset.DifyDatasetDefaultClient;

/**
 * Builder for creating DifyDataset and DifyDatasetClient instances
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025/12/26
 */
public final class DifyDatasetBuilder {

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
     * Create a DifyDatasetClient with the given HTTP client
     *
     * @param httpClient the HTTP client
     * @return the DifyDatasetClient
     */
    public static DifyDatasetClient createClient(HttpClient httpClient) {
        return new DifyDatasetDefaultClient(httpClient);
    }

    /**
     * Create a new builder instance for DifyDatasetClient
     *
     * @return the builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for DifyDatasetClient
     */
    public static final class Builder extends BaseDifyBuilder<Builder> {

        protected String apiKey;

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
            if (StrUtil.isNotEmpty(apiKey)) {
                httpClientFactory = httpClientFactory.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);
            }
            HttpClient httpClient = createHttpClient();
            return new DifyDatasetDefaultClient(httpClient);
        }
    }
}
