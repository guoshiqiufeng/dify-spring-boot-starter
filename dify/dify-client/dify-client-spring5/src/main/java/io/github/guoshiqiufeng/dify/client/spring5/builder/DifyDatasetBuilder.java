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
package io.github.guoshiqiufeng.dify.client.spring5.builder;

import io.github.guoshiqiufeng.dify.client.spring5.dataset.DifyDatasetDefaultClient;
import io.github.guoshiqiufeng.dify.dataset.DifyDataset;
import io.github.guoshiqiufeng.dify.dataset.client.DifyDatasetClient;
import io.github.guoshiqiufeng.dify.dataset.impl.DifyDatasetClientImpl;

/**
 * @author yanghq
 * @version 0.9.0
 * @since 2025/4/18 09:53
 */
public class DifyDatasetBuilder {

    public static DifyDataset create(DifyDatasetClient difyDatasetClient) {
        return new DifyDatasetClientImpl(difyDatasetClient);
    }

    /**
     * Builder for DifyDatasetClient
     */
    public static class DifyDatasetClientBuilder {
        public static DifyDatasetClient create() {
            return new DifyDatasetDefaultClient();
        }

        public static DifyDatasetClient create(String baseUrl) {
            return new DifyDatasetDefaultClient(baseUrl);
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder extends BaseDifyBuilder<Builder> {

            /**
             * Build the DifyDatasetClient
             *
             * @return the DifyDatasetClient
             */
            public DifyDatasetClient build() {
                initDefaults();
                return new DifyDatasetDefaultClient(baseUrl, clientConfig, webClientBuilder);
            }
        }
    }
}
