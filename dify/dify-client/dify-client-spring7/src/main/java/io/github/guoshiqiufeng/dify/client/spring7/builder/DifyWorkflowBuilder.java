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
package io.github.guoshiqiufeng.dify.client.spring7.builder;

import io.github.guoshiqiufeng.dify.client.spring7.workflow.DifyWorkflowDefaultClient;
import io.github.guoshiqiufeng.dify.workflow.DifyWorkflow;
import io.github.guoshiqiufeng.dify.workflow.client.DifyWorkflowClient;
import io.github.guoshiqiufeng.dify.workflow.impl.DifyWorkflowClientImpl;

/**
 * @author yanghq
 * @version 0.9.0
 * @since 2025/4/18 09:53
 */
public class DifyWorkflowBuilder {
    private DifyWorkflowBuilder() {
    }

    public static DifyWorkflow create(DifyWorkflowClient difyWorkflowClient) {
        return new DifyWorkflowClientImpl(difyWorkflowClient);
    }

    /**
     * Builder for DifyWorkflowClient
     */
    public static class DifyWorkflowClientBuilder {
        private DifyWorkflowClientBuilder() {
        }

        public static DifyWorkflowClient create() {
            return new DifyWorkflowDefaultClient();
        }

        public static DifyWorkflowClient create(String baseUrl) {
            return new DifyWorkflowDefaultClient(baseUrl);
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder extends BaseDifyBuilder<Builder> {

            /**
             * Build the DifyWorkflowClient
             *
             * @return the DifyWorkflowClient
             */
            public DifyWorkflowClient build() {
                initDefaults();
                return new DifyWorkflowDefaultClient(baseUrl, clientConfig, restClientBuilder, webClientBuilder);
            }
        }
    }
}
