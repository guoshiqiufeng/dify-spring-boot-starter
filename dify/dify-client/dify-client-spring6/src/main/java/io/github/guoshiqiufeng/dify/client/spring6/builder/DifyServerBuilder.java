package io.github.guoshiqiufeng.dify.client.spring6.builder;

import io.github.guoshiqiufeng.dify.client.spring6.server.DifyServerDefaultClient;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.server.DifyServer;
import io.github.guoshiqiufeng.dify.server.client.DifyServerClient;
import io.github.guoshiqiufeng.dify.server.client.DifyServerToken;
import io.github.guoshiqiufeng.dify.server.client.DifyServerTokenDefault;
import io.github.guoshiqiufeng.dify.server.impl.DifyServerClientImpl;

/**
 * @author yanghq
 * @version 0.9.0
 * @since 2025/4/18 09:53
 */
public class DifyServerBuilder {

    public static DifyServer create(DifyServerClient difyServerClient) {
        return new DifyServerClientImpl(difyServerClient);
    }

    /**
     * Builder for DifyServerClient
     */
    public static class DifyServerClientBuilder {
        public static DifyServerClient create(DifyProperties.Server difyServerProperties) {
            return new DifyServerDefaultClient(difyServerProperties);
        }

        public static DifyServerClient create(DifyProperties.Server difyServerProperties, String baseUrl) {
            return new DifyServerDefaultClient(difyServerProperties, new DifyServerTokenDefault(), baseUrl);
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder extends BaseDifyBuilder<Builder> {
            private DifyProperties.Server difyServerProperties;
            private DifyServerToken difyServerToken;

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
             * Set server token
             *
             * @param difyServerToken the server token
             * @return the builder
             */
            public Builder serverToken(DifyServerToken difyServerToken) {
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

                if (difyServerProperties == null) {
                    difyServerProperties = new DifyProperties.Server();
                }
                if (difyServerToken == null) {
                    difyServerToken = new DifyServerTokenDefault();
                }

                return new DifyServerDefaultClient(difyServerProperties,
                        difyServerToken, baseUrl, clientConfig, restClientBuilder, webClientBuilder);
            }
        }
    }
}
