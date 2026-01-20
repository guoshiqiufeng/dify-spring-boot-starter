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
package io.github.guoshiqiufeng.dify.springboot.autoconfigure;

import io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.SpringHttpClientFactory;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.server.client.DifyServerClient;
import io.github.guoshiqiufeng.dify.springboot.common.autoconfigure.AbstractDifyServerAutoConfiguration;
import io.github.guoshiqiufeng.dify.springboot.common.autoconfigure.DifyCodecAutoConfiguration;
import io.github.guoshiqiufeng.dify.springboot.common.autoconfigure.DifyServerRedisTokenAutoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Spring Boot 3 Server 自动配置
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/3/18 16:17
 */
@Slf4j
@Configuration
@AutoConfigureAfter({DifyServerRedisTokenAutoConfiguration.class, DifyCodecAutoConfiguration.class})
@ConditionalOnClass({DifyServerClient.class})
public class DifyServerAutoConfiguration extends AbstractDifyServerAutoConfiguration {

    private final ObjectProvider<WebClient.Builder> webClientBuilderProvider;
    private final ObjectProvider<RestClient.Builder> restClientBuilderProvider;

    public DifyServerAutoConfiguration(
            ObjectProvider<WebClient.Builder> webClientBuilderProvider,
            ObjectProvider<RestClient.Builder> restClientBuilderProvider) {
        this.webClientBuilderProvider = webClientBuilderProvider;
        this.restClientBuilderProvider = restClientBuilderProvider;
    }

    @Override
    protected SpringHttpClientFactory createHttpClientFactory(DifyProperties properties, JsonMapper jsonMapper) {
        return new SpringHttpClientFactory(
                webClientBuilderProvider.getIfAvailable(WebClient::builder),
                restClientBuilderProvider.getIfAvailable(RestClient::builder),
                jsonMapper);
    }
}
