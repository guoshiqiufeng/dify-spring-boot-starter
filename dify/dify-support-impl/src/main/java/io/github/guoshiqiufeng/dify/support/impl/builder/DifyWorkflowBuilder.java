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
import io.github.guoshiqiufeng.dify.support.impl.workflow.DifyWorkflowDefaultClient;
import io.github.guoshiqiufeng.dify.workflow.DifyWorkflow;
import io.github.guoshiqiufeng.dify.workflow.client.DifyWorkflowClient;
import io.github.guoshiqiufeng.dify.workflow.impl.DifyWorkflowClientImpl;

/**
 * Builder for creating DifyWorkflow and DifyWorkflowClient instances
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025/12/26
 */
public class DifyWorkflowBuilder {

    private DifyWorkflowBuilder() {
    }

    /**
     * Create a DifyWorkflow instance from a DifyWorkflowClient
     *
     * @param difyWorkflowClient the workflow client
     * @return the DifyWorkflow instance
     */
    public static DifyWorkflow create(DifyWorkflowClient difyWorkflowClient) {
        return new DifyWorkflowClientImpl(difyWorkflowClient);
    }

    /**
     * Builder for DifyWorkflowClient
     */
    public static class DifyWorkflowClientBuilder {
        private DifyWorkflowClientBuilder() {
        }

        /**
         * Create a DifyWorkflowClient with the given HTTP client
         *
         * @param httpClient the HTTP client
         * @return the DifyWorkflowClient
         */
        public static DifyWorkflowClient create(HttpClient httpClient) {
            return new DifyWorkflowDefaultClient(httpClient);
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
         * Builder class for DifyWorkflowClient
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
             * Build the DifyWorkflowClient
             *
             * @return the DifyWorkflowClient
             */
            public DifyWorkflowClient build() {
                initDefaults();

                HttpClient httpClient = createHttpClient();

                // If apiKey is provided, we could wrap the HttpClient to add default headers
                // For now, we'll just create the client and let users pass apiKey in requests
                return new DifyWorkflowDefaultClient(httpClient);
            }
        }
    }
}
