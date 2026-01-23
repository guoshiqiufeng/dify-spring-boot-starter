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
package io.github.guoshiqiufeng.dify.springboot.common.autoconfigure;

import io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper;
import io.github.guoshiqiufeng.dify.client.core.web.client.HttpClient;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.SpringHttpClientFactory;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.server.DifyServer;
import io.github.guoshiqiufeng.dify.server.client.BaseDifyServerToken;
import io.github.guoshiqiufeng.dify.server.client.DifyServerClient;
import io.github.guoshiqiufeng.dify.server.client.DifyServerTokenDefault;
import io.github.guoshiqiufeng.dify.server.impl.DifyServerClientImpl;
import io.github.guoshiqiufeng.dify.support.impl.server.DifyServerDefaultClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * Dify Server 自动配置抽象基类
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/3/18 16:17
 */
@Slf4j
@ConditionalOnClass({DifyServerClient.class})
public abstract class AbstractDifyServerAutoConfiguration {

    protected abstract SpringHttpClientFactory createHttpClientFactory(
            DifyProperties properties,
            JsonMapper jsonMapper);

    @Bean
    @ConditionalOnMissingBean(BaseDifyServerToken.class)
    public BaseDifyServerToken difyServerToken() {
        if(log.isDebugEnabled()) {
            log.debug("Redis token storage not available, using default in-memory token storage. " +
                    "For production environments, consider configuring Redis for distributed token management.");
        }
        return new DifyServerTokenDefault();
    }

    @Bean
    @ConditionalOnMissingBean(DifyServerClient.class)
    public DifyServerClient difyServerClient(DifyProperties properties,
                                             BaseDifyServerToken difyServerToken,
                                             JsonMapper jsonMapper) {
        SpringHttpClientFactory httpClientFactory = createHttpClientFactory(properties, jsonMapper);
        HttpClient httpClient = httpClientFactory.createClient(properties.getUrl(), properties.getClientConfig());
        return new DifyServerDefaultClient(httpClient, properties.getServer(), difyServerToken);
    }

    @Bean
    @ConditionalOnMissingBean({DifyServer.class})
    public DifyServerClientImpl difyServerHandler(DifyServerClient difyServerClient) {
        return new DifyServerClientImpl(difyServerClient);
    }
}
