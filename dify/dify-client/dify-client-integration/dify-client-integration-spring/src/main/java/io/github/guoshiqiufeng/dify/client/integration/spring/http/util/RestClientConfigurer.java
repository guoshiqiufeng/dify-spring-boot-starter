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
package io.github.guoshiqiufeng.dify.client.integration.spring.http.util;

import io.github.guoshiqiufeng.dify.client.integration.spring.http.pool.PoolSettings;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for configuring RestClient with connection pool and timeouts.
 * Extracts common logic to avoid code duplication between SpringHttpClient and DefaultRestClientFactory.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026-02-09
 */
@Slf4j
public final class RestClientConfigurer {

    private RestClientConfigurer() {
        // Utility class
    }

    /**
     * Apply HttpClient-based request factory to RestClient builder.
     * Supports Apache HttpClient 5 (with connection pool) or JDK HttpClient (fallback).
     *
     * @param builderClass    RestClient.Builder class
     * @param restClientBuilder RestClient.Builder instance
     * @param httpClient      Apache HttpClient 5 instance (nullable)
     * @param poolSettings    Pool settings for timeout configuration
     * @return Updated RestClient.Builder instance
     * @throws Exception if reflection fails
     */
    public static Object applyRequestFactory(Class<?> builderClass, Object restClientBuilder,
                                             Object httpClient, PoolSettings poolSettings) throws Exception {
        // Get timeout values from configuration (default 30 seconds)
        int connectTimeout = poolSettings.getConnectTimeoutSeconds();
        int readTimeout = poolSettings.getReadTimeoutSeconds();

        if (httpClient != null) {
            // Use Apache HttpClient 5 with HttpComponentsClientHttpRequestFactory
            return applyApacheHttpClient(builderClass, restClientBuilder, httpClient);
        } else {
            // Fall back to JDK HttpClient or SimpleClientHttpRequestFactory
            return applyJdkHttpClient(builderClass, restClientBuilder, connectTimeout, readTimeout);
        }
    }

    /**
     * Apply Apache HttpClient 5 to RestClient builder.
     */
    private static Object applyApacheHttpClient(Class<?> builderClass, Object restClientBuilder,
                                                Object httpClient) throws Exception {
        try {
            Class<?> httpComponentsFactoryClass = Class.forName(
                    "org.springframework.http.client.HttpComponentsClientHttpRequestFactory");
            Class<?> closeableHttpClientClass = Class.forName(
                    "org.apache.hc.client5.http.impl.classic.CloseableHttpClient");

            Object factory = httpComponentsFactoryClass.getConstructor(closeableHttpClientClass)
                    .newInstance(httpClient);

            // Set the request factory
            Class<?> clientHttpRequestFactoryClass = Class.forName(
                    "org.springframework.http.client.ClientHttpRequestFactory");
            Object updatedBuilder = builderClass.getMethod("requestFactory", clientHttpRequestFactoryClass)
                    .invoke(restClientBuilder, factory);

            log.debug("RestClient configured with Apache HttpClient 5 connection pool");
            return updatedBuilder;
        } catch (ClassNotFoundException e) {
            log.warn("HttpComponentsClientHttpRequestFactory not available, falling back to JDK client");
            throw e; // Let caller handle fallback
        }
    }

    /**
     * Apply JDK HttpClient or SimpleClientHttpRequestFactory to RestClient builder.
     */
    private static Object applyJdkHttpClient(Class<?> builderClass, Object restClientBuilder,
                                            int connectTimeout, int readTimeout) throws Exception {
        try {
            // Try JdkClientHttpRequestFactory first (Spring 6+)
            return applyJdkClientHttpRequestFactory(builderClass, restClientBuilder, connectTimeout, readTimeout);
        } catch (ClassNotFoundException e) {
            // Fall back to SimpleClientHttpRequestFactory (Spring 5)
            log.debug("JdkClientHttpRequestFactory not available, using SimpleClientHttpRequestFactory");
            return applySimpleClientHttpRequestFactory(builderClass, restClientBuilder, connectTimeout, readTimeout);
        }
    }

    /**
     * Apply JdkClientHttpRequestFactory (Spring 6+).
     */
    private static Object applyJdkClientHttpRequestFactory(Class<?> builderClass, Object restClientBuilder,
                                                           int connectTimeout, int readTimeout) throws Exception {
        // Create Duration objects for timeouts
        Class<?> durationClass = Class.forName("java.time.Duration");
        Object connectDuration = durationClass.getMethod("ofSeconds", long.class)
                .invoke(null, (long) connectTimeout);
        Object readDuration = durationClass.getMethod("ofSeconds", long.class)
                .invoke(null, (long) readTimeout);

        // Create HttpClient with connect timeout
        Class<?> jdkHttpClientClass = Class.forName("java.net.http.HttpClient");
        Class<?> httpClientBuilderClass = Class.forName("java.net.http.HttpClient$Builder");
        Object httpClientBuilder = jdkHttpClientClass.getMethod("newBuilder").invoke(null);
        httpClientBuilder = httpClientBuilderClass.getMethod("connectTimeout", durationClass)
                .invoke(httpClientBuilder, connectDuration);
        Object jdkHttpClient = httpClientBuilderClass.getMethod("build").invoke(httpClientBuilder);

        // Create JdkClientHttpRequestFactory with HttpClient
        Class<?> jdkFactoryClass = Class.forName(
                "org.springframework.http.client.JdkClientHttpRequestFactory");
        Object factory = jdkFactoryClass.getConstructor(jdkHttpClientClass)
                .newInstance(jdkHttpClient);

        // Set read timeout
        jdkFactoryClass.getMethod("setReadTimeout", durationClass).invoke(factory, readDuration);

        // Set the request factory
        Class<?> clientHttpRequestFactoryClass = Class.forName(
                "org.springframework.http.client.ClientHttpRequestFactory");
        return builderClass.getMethod("requestFactory", clientHttpRequestFactoryClass)
                .invoke(restClientBuilder, factory);
    }

    /**
     * Apply SimpleClientHttpRequestFactory (Spring 5 fallback).
     */
    private static Object applySimpleClientHttpRequestFactory(Class<?> builderClass, Object restClientBuilder,
                                                              int connectTimeout, int readTimeout) throws Exception {
        Class<?> factoryClass = Class.forName(
                "org.springframework.http.client.SimpleClientHttpRequestFactory");
        Object factory = factoryClass.getDeclaredConstructor().newInstance();
        factoryClass.getMethod("setConnectTimeout", int.class)
                .invoke(factory, connectTimeout * 1000);
        factoryClass.getMethod("setReadTimeout", int.class)
                .invoke(factory, readTimeout * 1000);

        // Set the request factory
        Class<?> clientHttpRequestFactoryClass = Class.forName(
                "org.springframework.http.client.ClientHttpRequestFactory");
        return builderClass.getMethod("requestFactory", clientHttpRequestFactoryClass)
                .invoke(restClientBuilder, factory);
    }
}
