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
package io.github.guoshiqiufeng.dify.springboot2.autoconfigure;

import io.github.guoshiqiufeng.dify.chat.client.DifyChatClient;
import io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.SpringHttpClientFactory;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.springboot.common.autoconfigure.AbstractDifyChatAutoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Spring Boot 2 Chat 自动配置
 * 继承公共基类，提供 Spring Boot 2 特定的 HttpClientFactory 实现（仅支持 WebClient）
 *
 * @author yanghq
 * @version 0.9.0
 * @since 2025/3/18 16:17
 */
@Slf4j
@Configuration
@ConditionalOnClass({DifyChatClient.class})
public class DifyChatAutoConfiguration extends AbstractDifyChatAutoConfiguration {

    private final ObjectProvider<WebClient.Builder> webClientBuilderProvider;

    public DifyChatAutoConfiguration(ObjectProvider<WebClient.Builder> webClientBuilderProvider) {
        this.webClientBuilderProvider = webClientBuilderProvider;
    }

    @Override
    protected SpringHttpClientFactory createHttpClientFactory(DifyProperties properties, JsonMapper jsonMapper) {
        // Spring Boot 2 不支持 RestClient，只使用 WebClient
        return new SpringHttpClientFactory(
                webClientBuilderProvider.getIfAvailable(WebClient::builder),
                null,
                jsonMapper);
    }
}
