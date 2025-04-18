package io.github.guoshiqiufeng.dify.client.spring5.builder;

import io.github.guoshiqiufeng.dify.client.spring5.workflow.DifyWorkflowDefaultClient;
import io.github.guoshiqiufeng.dify.workflow.DifyWorkflow;
import io.github.guoshiqiufeng.dify.workflow.client.DifyWorkflowClient;
import io.github.guoshiqiufeng.dify.workflow.impl.DifyWorkflowClientImpl;

/**
 * @author yanghq
 * @version 0.9.0
 * @since 2025/4/18 09:53
 */
public class DifyWorkflowBuilder {

    public static DifyWorkflow create(DifyWorkflowClient difyWorkflowClient) {
        return new DifyWorkflowClientImpl(difyWorkflowClient);
    }

    /**
     * Builder for DifyWorkflowClient
     */
    public static class DifyWorkflowClientBuilder {
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
                return new DifyWorkflowDefaultClient(baseUrl, clientConfig, webClientBuilder);
            }
        }
    }
}
