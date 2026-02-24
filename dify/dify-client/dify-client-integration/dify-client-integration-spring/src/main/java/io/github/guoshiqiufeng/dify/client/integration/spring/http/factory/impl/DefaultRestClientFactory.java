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
import io.github.guoshiqiufeng.dify.client.integration.spring.http.factory.RestClientFactory;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.pool.PoolSettings;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.pool.RestClientHttpClientFactory;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.util.RestClientConfigurer;
import io.github.guoshiqiufeng.dify.client.integration.spring.logging.DifyLoggingControl;
import io.github.guoshiqiufeng.dify.client.integration.spring.logging.DifyRestLoggingInterceptor;
import io.github.guoshiqiufeng.dify.client.integration.spring.version.SpringVersionDetector;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Default implementation of RestClientFactory.
 * Creates RestClient (Spring 6+) with proper configuration.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/2/8
 */
@Slf4j
public class DefaultRestClientFactory implements RestClientFactory {

    private final RestClientHttpClientFactory httpClientFactory;
    private final List<ConnectionPoolConfigurer> configurers;

    public DefaultRestClientFactory(
            RestClientHttpClientFactory httpClientFactory,
            List<ConnectionPoolConfigurer> configurers) {
        this.httpClientFactory = httpClientFactory;
        this.configurers = configurers;
    }

    @Override
    public Object createRestClient(
            Object builder,
            String baseUrl,
            DifyProperties.ClientConfig clientConfig,
            HttpHeaders defaultHeaders,
            List<Object> interceptors) {

        if (!isRestClientAvailable()) {
            log.warn("RestClient is not available in Spring 5, returning null");
            return null;
        }

        try {
            // Get RestClient.Builder class
            Class<?> builderClass = Class.forName("org.springframework.web.client.RestClient$Builder");

            // Create builder if not provided
            if (builder == null) {
                Class<?> restClientClass = Class.forName("org.springframework.web.client.RestClient");
                Method createMethod = restClientClass.getMethod("builder");
                builder = createMethod.invoke(null);
            }

            // Set base URL
            Object restClientBuilder = builderClass.getMethod("baseUrl", String.class)
                    .invoke(builder, baseUrl);

            // Check if user has already configured a custom requestFactory
            boolean hasCustomRequestFactory = hasUserRequestFactory(builder);
            if (hasCustomRequestFactory) {
                log.warn("Custom requestFactory detected on RestClient.Builder. " +
                        "Connection pool configuration will be skipped to preserve user settings. " +
                        "If you want to use connection pool, create a new builder without requestFactory.");
            }

            // Configure connection pool using available configurer
            if (clientConfig != null && !hasCustomRequestFactory) {
                PoolSettings poolSettings = PoolSettings.from(clientConfig);
                if (poolSettings != null) {
                    // Apply HttpClient from factory if available
                    if (httpClientFactory != null) {
                        try {
                            Object httpClient = httpClientFactory.buildHttpClient(poolSettings);
                            if (httpClient != null) {
                                // Use RestClientConfigurer to apply HttpClient
                                restClientBuilder = RestClientConfigurer.applyRequestFactory(
                                        builderClass, restClientBuilder, httpClient, poolSettings);
                                log.debug("Applied pooled HttpClient to RestClient");
                            }
                        } catch (Exception e) {
                            log.warn("Failed to apply HttpClient pool to RestClient, using default: {}",
                                    e.getMessage());
                        }
                    }

                    // Fallback to configurers if httpClientFactory didn't apply
                    if (configurers != null) {
                        for (ConnectionPoolConfigurer configurer : configurers) {
                            if (configurer.isAvailable()) {
                                restClientBuilder = configurer.configureRestClient(restClientBuilder, poolSettings);
                                log.debug("Configured RestClient connection pool using: {}", configurer.getName());
                                break;
                            }
                        }
                    }
                }
            }

            // Add default headers
            if (defaultHeaders != null && !defaultHeaders.isEmpty()) {
                for (String key : defaultHeaders.keySet()) {
                    List<String> values = defaultHeaders.get(key);
                    if (values != null && !values.isEmpty()) {
                        restClientBuilder = builderClass.getMethod("defaultHeader", String.class, String[].class)
                                .invoke(restClientBuilder, key, values.toArray(new String[0]));
                    }
                }
            }

            // Add logging interceptor
            if (clientConfig != null && clientConfig.getLogging()) {
                boolean maskingEnabled = clientConfig.getLoggingMaskEnabled() != null
                        ? clientConfig.getLoggingMaskEnabled() : true;
                int logBodyMaxBytes = clientConfig.getLogBodyMaxBytes() != null
                        ? clientConfig.getLogBodyMaxBytes() : 10240;
                boolean logBinaryBody = clientConfig.getLogBinaryBody() != null
                        ? clientConfig.getLogBinaryBody() : false;
                DifyRestLoggingInterceptor interceptor = DifyLoggingControl.createInterceptor(maskingEnabled, logBodyMaxBytes, logBinaryBody);

                try {
                    Class<?> clientHttpRequestInterceptorClass = Class.forName(
                            "org.springframework.http.client.ClientHttpRequestInterceptor");
                    if (clientHttpRequestInterceptorClass.isInstance(interceptor)) {
                        restClientBuilder = builderClass.getMethod("requestInterceptor", clientHttpRequestInterceptorClass)
                                .invoke(restClientBuilder, interceptor);
                    }
                } catch (ClassNotFoundException e) {
                    log.warn("ClientHttpRequestInterceptor not available");
                }
            }

            // Add custom interceptors
            if (interceptors != null && !interceptors.isEmpty()) {
                for (Object interceptor : interceptors) {
                    try {
                        Class<?> clientHttpRequestInterceptorClass = Class.forName(
                                "org.springframework.http.client.ClientHttpRequestInterceptor");
                        if (clientHttpRequestInterceptorClass.isInstance(interceptor)) {
                            restClientBuilder = builderClass.getMethod("requestInterceptor", clientHttpRequestInterceptorClass)
                                    .invoke(restClientBuilder, interceptor);
                        }
                    } catch (ClassNotFoundException e) {
                        log.warn("ClientHttpRequestInterceptor not available, skipping interceptor");
                    }
                }
            }

            // Build RestClient
            return builderClass.getMethod("build").invoke(restClientBuilder);

        } catch (Exception e) {
            log.error("Failed to create RestClient", e);
            return null;
        }
    }

    @Override
    public boolean isRestClientAvailable() {
        return SpringVersionDetector.hasRestClient();
    }

    /**
     * Check if the RestClient.Builder has a custom requestFactory configured.
     * This helps avoid overwriting user's custom HTTP client configuration.
     *
     * @param builder the RestClient.Builder instance
     * @return true if a custom requestFactory is detected
     */
    private boolean hasUserRequestFactory(Object builder) {
        try {
            // Try to access the requestFactory field via reflection
            // RestClient.Builder stores requestFactory in a private field
            Field requestFactoryField = getFactoryField(builder);

            if (requestFactoryField != null) {
                requestFactoryField.setAccessible(true);
                Object requestFactory = requestFactoryField.get(builder);
                return requestFactory != null;
            }

            return false;
        } catch (Exception e) {
            // Conservative approach: assume user has custom factory when reflection fails
            // This prevents accidentally overwriting user's TLS/proxy/interceptor configuration
            log.warn("Unable to detect custom requestFactory via reflection (field name may have changed or access restricted). " +
                    "Assuming custom factory exists to preserve user configuration. Error: {}", e.getMessage());
            return true;
        }
    }

    @Nullable
    private static Field getFactoryField(Object builder) {
        Class<?> builderClass = builder.getClass();
        Field requestFactoryField = null;

        // Search for requestFactory field in the class hierarchy
        Class<?> currentClass = builderClass;
        while (currentClass != null && requestFactoryField == null) {
            try {
                requestFactoryField = currentClass.getDeclaredField("requestFactory");
            } catch (NoSuchFieldException e) {
                currentClass = currentClass.getSuperclass();
            }
        }
        return requestFactoryField;
    }
}
