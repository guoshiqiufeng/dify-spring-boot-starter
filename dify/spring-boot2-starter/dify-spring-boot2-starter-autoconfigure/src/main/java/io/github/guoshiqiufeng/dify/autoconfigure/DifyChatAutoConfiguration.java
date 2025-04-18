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
package io.github.guoshiqiufeng.dify.autoconfigure;

import io.github.guoshiqiufeng.dify.chat.DifyChat;
import io.github.guoshiqiufeng.dify.chat.client.DifyChatClient;
import io.github.guoshiqiufeng.dify.chat.impl.DifyChatClientImpl;
import io.github.guoshiqiufeng.dify.client.spring5.chat.DifyChatDefaultClient;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author yanghq
 * @version 0.9.0
 * @since 2025/3/18 16:17
 */
@Slf4j
@Configuration
@ConditionalOnClass({DifyChatClient.class})
public class DifyChatAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(DifyChatClient.class)
    public DifyChatClient difyChatClient(DifyProperties properties,
                                         ObjectProvider<WebClient.Builder> webClientBuilderProvider) {
        return new DifyChatDefaultClient(properties.getUrl(),
                properties.getClientConfig(),
                webClientBuilderProvider.getIfAvailable(WebClient::builder));
    }

    @Bean
    @ConditionalOnMissingBean({DifyChat.class})
    public DifyChatClientImpl difyChatHandler(DifyChatClient difyChatClient) {
        return new DifyChatClientImpl(difyChatClient);
    }

}
