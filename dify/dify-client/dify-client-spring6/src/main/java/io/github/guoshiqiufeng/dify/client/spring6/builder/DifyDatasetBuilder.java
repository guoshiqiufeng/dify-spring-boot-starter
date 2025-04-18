package io.github.guoshiqiufeng.dify.client.spring6.builder;

import io.github.guoshiqiufeng.dify.client.spring6.dataset.DifyDatasetDefaultClient;
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
                return new DifyDatasetDefaultClient(baseUrl, clientConfig, restClientBuilder, webClientBuilder);
            }
        }
    }
}
