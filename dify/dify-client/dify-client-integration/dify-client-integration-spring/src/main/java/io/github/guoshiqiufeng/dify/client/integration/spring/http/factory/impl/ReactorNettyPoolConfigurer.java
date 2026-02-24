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
package io.github.guoshiqiufeng.dify.client.integration.spring.http.factory.impl;

import io.github.guoshiqiufeng.dify.client.integration.spring.http.factory.ConnectionPoolConfigurer;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.pool.PoolSettings;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.pool.WebClientConnectionProviderFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Reactor Netty connection pool configurer.
 * Uses WebClientConnectionProviderFactory to configure connection pool.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/2/8
 */
@Slf4j
public class ReactorNettyPoolConfigurer implements ConnectionPoolConfigurer {

    private final WebClientConnectionProviderFactory connectionProviderFactory;

    public ReactorNettyPoolConfigurer(WebClientConnectionProviderFactory connectionProviderFactory) {
        this.connectionProviderFactory = connectionProviderFactory;
    }

    @Override
    public WebClient.Builder configureWebClient(WebClient.Builder builder, PoolSettings poolSettings) {
        if (connectionProviderFactory != null) {
            try {
                // Use buildConnectionProvider instead of createConnectionProvider
                Object connectionProvider = connectionProviderFactory.buildConnectionProvider(
                        poolSettings, "dify-webclient-pool");
                if (connectionProvider != null) {
                    // Use reflection to avoid compile-time dependency on reactor-netty
                    Class<?> httpClientClass = Class.forName("reactor.netty.http.client.HttpClient");
                    Class<?> connectionProviderClass = Class.forName("reactor.netty.resources.ConnectionProvider");

                    Object httpClient = httpClientClass.getMethod("create").invoke(null);
                    httpClient = httpClientClass.getMethod("connectionProvider", connectionProviderClass)
                            .invoke(httpClient, connectionProvider);

                    // Create ReactorClientHttpConnector using reflection
                    Class<?> connectorClass = org.springframework.http.client.reactive.ReactorClientHttpConnector.class;
                    Object connector = connectorClass.getConstructor(httpClientClass).newInstance(httpClient);

                    builder = builder.clientConnector(
                            (org.springframework.http.client.reactive.ReactorClientHttpConnector) connector
                    );
                    log.debug("Configured WebClient with Reactor Netty connection pool");
                }
            } catch (Exception e) {
                log.warn("Failed to configure Reactor Netty connection pool", e);
            }
        }
        return builder;
    }

    @Override
    public Object configureRestClient(Object builder, PoolSettings poolSettings) {
        // RestClient uses different HTTP client configuration
        // Delegate to RestClientHttpClientFactory
        log.debug("RestClient connection pool configuration handled by RestClientHttpClientFactory");
        return builder;
    }

    @Override
    public String getName() {
        return "ReactorNetty";
    }

    @Override
    public boolean isAvailable() {
        try {
            Class.forName("reactor.netty.http.client.HttpClient");
            Class.forName("reactor.netty.resources.ConnectionProvider");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
