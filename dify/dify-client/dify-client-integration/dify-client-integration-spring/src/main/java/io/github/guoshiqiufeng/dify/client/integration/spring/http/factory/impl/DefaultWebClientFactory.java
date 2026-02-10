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

import io.github.guoshiqiufeng.dify.client.core.http.HttpHeaders;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.factory.ConnectionPoolConfigurer;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.factory.WebClientFactory;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.pool.PoolSettings;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.pool.WebClientConnectionProviderFactory;
import io.github.guoshiqiufeng.dify.client.integration.spring.logging.DifyLoggingControl;
import io.github.guoshiqiufeng.dify.client.integration.spring.logging.DifyLoggingFilter;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;

/**
 * Default implementation of WebClientFactory.
 * Creates WebClient with proper configuration including connection pool, timeouts, and logging.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/2/8
 */
@Slf4j
public class DefaultWebClientFactory implements WebClientFactory {

    private final WebClientConnectionProviderFactory connectionProviderFactory;
    private final List<ConnectionPoolConfigurer> configurers;

    public DefaultWebClientFactory(
            WebClientConnectionProviderFactory connectionProviderFactory,
            List<ConnectionPoolConfigurer> configurers) {
        this.connectionProviderFactory = connectionProviderFactory;
        this.configurers = configurers;
    }

    @Override
    public WebClient createWebClient(
            WebClient.Builder builder,
            String baseUrl,
            DifyProperties.ClientConfig clientConfig,
            HttpHeaders defaultHeaders,
            List<Object> interceptors) {

        if (builder == null) {
            builder = WebClient.builder();
        }

        // Set base URL
        builder.baseUrl(baseUrl);

        // Configure timeouts
        if (clientConfig != null) {
            Integer connectTimeout = clientConfig.getConnectTimeout();
            Integer readTimeout = clientConfig.getReadTimeout();
            Integer writeTimeout = clientConfig.getWriteTimeout();

            if (connectTimeout != null || readTimeout != null || writeTimeout != null) {
                // Use default values if not specified
                int finalConnectTimeout = connectTimeout != null ? connectTimeout : 30;
                int finalReadTimeout = readTimeout != null ? readTimeout : 30;
                int finalWriteTimeout = writeTimeout != null ? writeTimeout : 30;

                // Configure HTTP client with timeouts using reflection to avoid compile-time dependency
                try {
                    Class<?> httpClientClass = Class.forName("reactor.netty.http.client.HttpClient");
                    Class<?> channelOptionClass = Class.forName("io.netty.channel.ChannelOption");

                    Object httpClient = httpClientClass.getMethod("create").invoke(null);

                    // Set connect timeout using ChannelOption.CONNECT_TIMEOUT_MILLIS
                    Object connectTimeoutOption = channelOptionClass.getField("CONNECT_TIMEOUT_MILLIS").get(null);
                    httpClient = httpClientClass.getMethod("option", channelOptionClass, Object.class)
                            .invoke(httpClient, connectTimeoutOption, finalConnectTimeout * 1000);

                    // Set response timeout (read timeout)
                    httpClient = httpClientClass.getMethod("responseTimeout", Duration.class)
                            .invoke(httpClient, Duration.ofSeconds(finalReadTimeout));

                    // Note: reactor-netty doesn't have a separate write timeout configuration
                    // Write operations are covered by responseTimeout
                    if (finalWriteTimeout != finalReadTimeout) {
                        log.debug("WebClient: writeTimeout={}s is noted but reactor-netty uses responseTimeout={}s for both read and write",
                                finalWriteTimeout, finalReadTimeout);
                    }

                    // Create ReactorClientHttpConnector using reflection
                    Class<?> connectorClass = org.springframework.http.client.reactive.ReactorClientHttpConnector.class;
                    Object connector = connectorClass.getConstructor(httpClientClass).newInstance(httpClient);

                    builder = builder.clientConnector(
                            (org.springframework.http.client.reactive.ReactorClientHttpConnector) connector
                    );

                    log.debug("Configured WebClient timeouts: connect={}s, read={}s", finalConnectTimeout, finalReadTimeout);
                } catch (Exception e) {
                    log.warn("Failed to configure timeouts, using defaults: {}", e.getMessage());
                }
            }

            // Configure connection pool using available configurer
            PoolSettings poolSettings = PoolSettings.from(clientConfig);
            if (poolSettings != null && configurers != null) {
                for (ConnectionPoolConfigurer configurer : configurers) {
                    if (configurer.isAvailable()) {
                        builder = configurer.configureWebClient(builder, poolSettings);
                        log.debug("Configured WebClient connection pool using: {}", configurer.getName());
                        break;
                    }
                }
            }
        }

        // Add default headers
        if (defaultHeaders != null && !defaultHeaders.isEmpty()) {
            WebClient.Builder finalBuilder = builder;
            defaultHeaders.forEach((key, values) -> {
                if (values != null && !values.isEmpty()) {
                    // Apply all values, not just the first
                    finalBuilder.defaultHeader(key, values.toArray(new String[0]));
                }
            });
        }

        // Add logging filter
        if (clientConfig != null && clientConfig.getLogging()) {
            boolean maskingEnabled = clientConfig.getLoggingMaskEnabled() != null
                    ? clientConfig.getLoggingMaskEnabled() : true;
            int logBodyMaxBytes = clientConfig.getLogBodyMaxBytes() != null
                    ? clientConfig.getLogBodyMaxBytes() : 10240;
            boolean logBinaryBody = clientConfig.getLogBinaryBody() != null
                    ? clientConfig.getLogBinaryBody() : false;
            DifyLoggingFilter filter = DifyLoggingControl.createFilter(maskingEnabled, logBodyMaxBytes, logBinaryBody);
            builder.filter(filter);
        }

        // Add custom interceptors
        if (interceptors != null && !interceptors.isEmpty()) {
            for (Object interceptor : interceptors) {
                try {
                    Class<?> exchangeFilterFunctionClass = Class.forName(
                            "org.springframework.web.reactive.function.client.ExchangeFilterFunction");
                    if (exchangeFilterFunctionClass.isInstance(interceptor)) {
                        builder = builder.filter((org.springframework.web.reactive.function.client.ExchangeFilterFunction) interceptor);
                    }
                } catch (ClassNotFoundException e) {
                    log.warn("ExchangeFilterFunction not available, skipping interceptor");
                }
            }
        }

        return builder.build();
    }
}
