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
import io.github.guoshiqiufeng.dify.client.integration.spring.version.SpringVersionDetector;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RestClientConfigurer with Apache HttpClient
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/2/24
 */
class RestClientConfigurerApacheTest {

    @Test
    @EnabledIf("isRestClientAndApacheAvailable")
    void testApplyRequestFactoryWithApacheHttpClient() throws Exception {
        // Arrange
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setConnectTimeout(10);
        config.setReadTimeout(20);
        PoolSettings poolSettings = PoolSettings.from(config);

        Class<?> restClientClass = Class.forName("org.springframework.web.client.RestClient");
        Class<?> builderClass = Class.forName("org.springframework.web.client.RestClient$Builder");
        Object restClientBuilder = restClientClass.getMethod("builder").invoke(null);

        // Create Apache HttpClient 5 instance
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        // Act
        Object result = RestClientConfigurer.applyRequestFactory(
                builderClass, restClientBuilder, httpClient, poolSettings);

        // Assert
        assertNotNull(result);

        // Cleanup
        httpClient.close();
    }

    @Test
    @EnabledIf("isRestClientAndApacheAvailable")
    void testApplyRequestFactoryWithApacheHttpClientCustomSettings() throws Exception {
        // Arrange
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setConnectTimeout(5);
        config.setReadTimeout(15);
        config.setWriteTimeout(25);
        PoolSettings poolSettings = PoolSettings.from(config);

        Class<?> restClientClass = Class.forName("org.springframework.web.client.RestClient");
        Class<?> builderClass = Class.forName("org.springframework.web.client.RestClient$Builder");
        Object restClientBuilder = restClientClass.getMethod("builder").invoke(null);

        // Create Apache HttpClient 5 with custom configuration
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        // Act
        Object result = RestClientConfigurer.applyRequestFactory(
                builderClass, restClientBuilder, httpClient, poolSettings);

        // Assert
        assertNotNull(result);

        // Cleanup
        httpClient.close();
    }

    @Test
    @EnabledIf("isRestClientAndApacheAvailable")
    void testApplyRequestFactoryWithApacheHttpClientMultipleTimes() throws Exception {
        // Test that Apache HttpClient can be configured multiple times
        // Arrange
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setConnectTimeout(10);
        config.setReadTimeout(20);
        PoolSettings poolSettings = PoolSettings.from(config);

        Class<?> restClientClass = Class.forName("org.springframework.web.client.RestClient");
        Class<?> builderClass = Class.forName("org.springframework.web.client.RestClient$Builder");

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        try {
            // Act - call multiple times
            for (int i = 0; i < 3; i++) {
                Object restClientBuilder = restClientClass.getMethod("builder").invoke(null);
                Object result = RestClientConfigurer.applyRequestFactory(
                        builderClass, restClientBuilder, httpClient, poolSettings);

                // Assert
                assertNotNull(result);
            }
        } finally {
            // Cleanup
            httpClient.close();
        }
    }

    @Test
    @EnabledIf("isRestClientAndApacheAvailable")
    void testApplyRequestFactoryWithApacheHttpClientDifferentTimeouts() throws Exception {
        // Test with various timeout configurations
        // Arrange
        Class<?> restClientClass = Class.forName("org.springframework.web.client.RestClient");
        Class<?> builderClass = Class.forName("org.springframework.web.client.RestClient$Builder");

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        try {
            // Test case 1: Short timeouts
            DifyProperties.ClientConfig config1 = new DifyProperties.ClientConfig();
            config1.setConnectTimeout(1);
            config1.setReadTimeout(2);
            PoolSettings poolSettings1 = PoolSettings.from(config1);

            Object restClientBuilder1 = restClientClass.getMethod("builder").invoke(null);
            Object result1 = RestClientConfigurer.applyRequestFactory(
                    builderClass, restClientBuilder1, httpClient, poolSettings1);
            assertNotNull(result1);

            // Test case 2: Long timeouts
            DifyProperties.ClientConfig config2 = new DifyProperties.ClientConfig();
            config2.setConnectTimeout(60);
            config2.setReadTimeout(120);
            PoolSettings poolSettings2 = PoolSettings.from(config2);

            Object restClientBuilder2 = restClientClass.getMethod("builder").invoke(null);
            Object result2 = RestClientConfigurer.applyRequestFactory(
                    builderClass, restClientBuilder2, httpClient, poolSettings2);
            assertNotNull(result2);

            // Test case 3: Zero timeouts
            DifyProperties.ClientConfig config3 = new DifyProperties.ClientConfig();
            config3.setConnectTimeout(0);
            config3.setReadTimeout(0);
            PoolSettings poolSettings3 = PoolSettings.from(config3);

            Object restClientBuilder3 = restClientClass.getMethod("builder").invoke(null);
            Object result3 = RestClientConfigurer.applyRequestFactory(
                    builderClass, restClientBuilder3, httpClient, poolSettings3);
            assertNotNull(result3);
        } finally {
            // Cleanup
            httpClient.close();
        }
    }

    @Test
    @EnabledIf("isRestClientAndApacheAvailable")
    void testApplyRequestFactoryWithApacheHttpClientDefaultBuilder() throws Exception {
        // Test with default HttpClient builder
        // Arrange
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setConnectTimeout(30);
        config.setReadTimeout(30);
        PoolSettings poolSettings = PoolSettings.from(config);

        Class<?> restClientClass = Class.forName("org.springframework.web.client.RestClient");
        Class<?> builderClass = Class.forName("org.springframework.web.client.RestClient$Builder");
        Object restClientBuilder = restClientClass.getMethod("builder").invoke(null);

        // Create default Apache HttpClient
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        // Act
        Object result = RestClientConfigurer.applyRequestFactory(
                builderClass, restClientBuilder, httpClient, poolSettings);

        // Assert
        assertNotNull(result);

        // Cleanup
        httpClient.close();
    }

    static boolean isRestClientAndApacheAvailable() {
        try {
            // Check if RestClient is available
            if (!SpringVersionDetector.hasRestClient()) {
                return false;
            }

            // Check if Apache HttpClient 5 is available
            Class.forName("org.apache.hc.client5.http.impl.classic.CloseableHttpClient");
            Class.forName("org.apache.hc.client5.http.impl.classic.HttpClientBuilder");
            Class.forName("org.springframework.http.client.HttpComponentsClientHttpRequestFactory");

            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
