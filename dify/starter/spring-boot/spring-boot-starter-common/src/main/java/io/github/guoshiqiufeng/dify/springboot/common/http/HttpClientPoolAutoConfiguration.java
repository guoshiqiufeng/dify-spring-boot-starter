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
package io.github.guoshiqiufeng.dify.springboot.common.http;

import io.github.guoshiqiufeng.dify.client.integration.spring.http.pool.DefaultRestClientHttpClientFactory;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.pool.DefaultWebClientConnectionProviderFactory;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.pool.RestClientHttpClientFactory;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.pool.WebClientConnectionProviderFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Auto-configuration for HTTP client connection pools.
 * Provides default factory beans for WebClient and RestClient connection pool configuration.
 * Users can override these beans to provide custom connection pool implementations.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026-02-06
 */
@Slf4j
@Configuration
@ConditionalOnClass(name = "io.github.guoshiqiufeng.dify.client.integration.spring.http.SpringHttpClient")
public class HttpClientPoolAutoConfiguration {

    /**
     * Provide default WebClient ConnectionProvider factory if user hasn't defined one.
     * This factory creates reactor-netty ConnectionProvider with settings from DifyProperties.ClientConfig.
     *
     * @return default WebClientConnectionProviderFactory
     */
    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean(WebClientConnectionProviderFactory.class)
    @ConditionalOnClass(name = "reactor.netty.resources.ConnectionProvider")
    public WebClientConnectionProviderFactory defaultWebClientConnectionProviderFactory() {
        log.debug("Creating default WebClientConnectionProviderFactory");
        return new DefaultWebClientConnectionProviderFactory();
    }

    /**
     * Provide default RestClient HttpClient factory if user hasn't defined one.
     * This factory creates Apache HttpClient 5 with connection pool settings from DifyProperties.ClientConfig.
     *
     * @return default RestClientHttpClientFactory
     */
    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean(RestClientHttpClientFactory.class)
    @ConditionalOnClass(name = "org.apache.hc.client5.http.impl.classic.CloseableHttpClient")
    public RestClientHttpClientFactory defaultRestClientHttpClientFactory() {
        log.debug("Creating default RestClientHttpClientFactory");
        return new DefaultRestClientHttpClientFactory();
    }
}
