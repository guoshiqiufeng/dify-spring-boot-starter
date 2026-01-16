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
public final class DifyWorkflowBuilder {

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
     * Create a DifyWorkflowClient with the given HTTP client
     *
     * @param httpClient the HTTP client
     * @return the DifyWorkflowClient
     */
    public static DifyWorkflowClient createClient(HttpClient httpClient) {
        return new DifyWorkflowDefaultClient(httpClient);
    }

    /**
     * Create a new builder instance for DifyWorkflowClient
     *
     * @return the builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for DifyWorkflowClient
     */
    public static final class Builder extends BaseDifyBuilder<Builder> {
        /**
         * Build the DifyWorkflowClient
         *
         * @return the DifyWorkflowClient
         */
        public DifyWorkflowClient build() {
            initDefaults();
            HttpClient httpClient = createHttpClient();
            return new DifyWorkflowDefaultClient(httpClient);
        }
    }
}
