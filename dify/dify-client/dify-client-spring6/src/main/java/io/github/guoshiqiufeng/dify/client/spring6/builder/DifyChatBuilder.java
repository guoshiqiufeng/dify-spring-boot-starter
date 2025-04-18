package io.github.guoshiqiufeng.dify.client.spring6.builder;

import io.github.guoshiqiufeng.dify.chat.DifyChat;
import io.github.guoshiqiufeng.dify.chat.client.DifyChatClient;
import io.github.guoshiqiufeng.dify.chat.impl.DifyChatClientImpl;
import io.github.guoshiqiufeng.dify.client.spring6.chat.DifyChatDefaultClient;

/**
 * @author yanghq
 * @version 0.9.0
 * @since 2025/4/18 09:53
 */
public class DifyChatBuilder {

    public static DifyChat create(DifyChatClient difyChatClient) {
        return new DifyChatClientImpl(difyChatClient);
    }

    /**
     * Builder for DifyChatClient
     */
    public static class DifyChatClientBuilder {
        public static DifyChatClient create() {
            return new DifyChatDefaultClient();
        }

        public static DifyChatClient create(String baseUrl) {
            return new DifyChatDefaultClient(baseUrl);
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder extends BaseDifyBuilder<Builder> {
            /**
             * Build the DifyChatClient
             *
             * @return the DifyChatClient
             */
            public DifyChatClient build() {
                initDefaults();
                return new DifyChatDefaultClient(baseUrl, clientConfig, restClientBuilder, webClientBuilder);
            }
        }
    }
}
