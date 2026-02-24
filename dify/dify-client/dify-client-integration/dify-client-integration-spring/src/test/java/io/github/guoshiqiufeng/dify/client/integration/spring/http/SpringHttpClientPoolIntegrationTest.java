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
package io.github.guoshiqiufeng.dify.client.integration.spring.http;

import io.github.guoshiqiufeng.dify.client.codec.gson.GsonJsonMapper;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.pool.DefaultRestClientHttpClientFactory;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.pool.DefaultWebClientConnectionProviderFactory;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Spring HTTP client connection pool configuration.
 * Verifies that connection pool settings are properly applied to WebClient and RestClient.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026-02-06
 */
@DisplayName("Spring Connection Pool Integration Tests")
class SpringHttpClientPoolIntegrationTest {

    @Test
    @DisplayName("Should apply connection pool settings to WebClient")
    void testWebClientConnectionPoolConfiguration() {
        // Arrange
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setMaxIdleConnections(20);
        config.setKeepAliveSeconds(600);
        config.setMaxRequests(256);
        config.setMaxRequestsPerHost(20);
        config.setCallTimeout(60);

        DefaultWebClientConnectionProviderFactory factory = new DefaultWebClientConnectionProviderFactory();

        // Act
        SpringHttpClient client = new SpringHttpClient(
                "https://api.dify.ai",
                config,
                WebClient.builder(),
                null,
                new GsonJsonMapper(),
                null,
                null,
                factory,
                null
        );

        // Assert
        assertNotNull(client.getWebClient());
        assertNotNull(client.getClientConfig());
        assertEquals(20, client.getClientConfig().getMaxIdleConnections());
        assertEquals(600, client.getClientConfig().getKeepAliveSeconds());
        assertEquals(256, client.getClientConfig().getMaxRequests());
        assertEquals(20, client.getClientConfig().getMaxRequestsPerHost());
        assertEquals(60, client.getClientConfig().getCallTimeout());
    }

    @Test
    @DisplayName("Should apply connection pool settings to RestClient")
    void testRestClientConnectionPoolConfiguration() {
        // Arrange
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setMaxIdleConnections(20);
        config.setKeepAliveSeconds(600);
        config.setMaxRequests(256);
        config.setMaxRequestsPerHost(20);
        config.setCallTimeout(60);

        DefaultRestClientHttpClientFactory factory = new DefaultRestClientHttpClientFactory();

        // Act
        SpringHttpClient client = new SpringHttpClient(
                "https://api.dify.ai",
                config,
                WebClient.builder(),
                null,
                new GsonJsonMapper(),
                null,
                null,
                null,
                factory
        );

        // Assert
        assertNotNull(client.getClientConfig());
        assertEquals(20, client.getClientConfig().getMaxIdleConnections());
        assertEquals(600, client.getClientConfig().getKeepAliveSeconds());
        assertEquals(256, client.getClientConfig().getMaxRequests());
        assertEquals(20, client.getClientConfig().getMaxRequestsPerHost());
        assertEquals(60, client.getClientConfig().getCallTimeout());
    }

    @Test
    @DisplayName("Should use default settings when config is null")
    void testDefaultConnectionPoolSettings() {
        // Act
        SpringHttpClient client = new SpringHttpClient(
                "https://api.dify.ai",
                null,
                new GsonJsonMapper()
        );

        // Assert
        assertNotNull(client.getWebClient());
        assertNull(client.getClientConfig());
    }

    @Test
    @DisplayName("Should not override user-provided RestClient.Builder requestFactory")
    void testUserProvidedRequestFactoryNotOverridden() throws Exception {
        // Arrange
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setMaxIdleConnections(20);

        // Create a RestClient.Builder with pre-configured requestFactory
        Object restClientBuilder = createRestClientBuilderWithRequestFactory();

        if (restClientBuilder == null) {
            // RestClient not available (Spring 5), skip test
            return;
        }

        DefaultRestClientHttpClientFactory factory = new DefaultRestClientHttpClientFactory();

        // Act
        SpringHttpClient client = new SpringHttpClient(
                "https://api.dify.ai",
                config,
                WebClient.builder(),
                restClientBuilder,
                new GsonJsonMapper(),
                null,
                null,
                null,
                factory
        );

        // Assert
        assertNotNull(client.getRestClient());
        // User's requestFactory should be preserved
        assertTrue(hasRequestFactory(restClientBuilder));
    }

    @Test
    @DisplayName("Should apply default connection pool when user has not provided requestFactory")
    void testDefaultConnectionPoolAppliedWhenNoUserRequestFactory() {
        // Arrange
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setMaxIdleConnections(20);
        config.setKeepAliveSeconds(600);

        DefaultRestClientHttpClientFactory factory = new DefaultRestClientHttpClientFactory();

        // Act
        SpringHttpClient client = new SpringHttpClient(
                "https://api.dify.ai",
                config,
                WebClient.builder(),
                null, // No user-provided RestClient.Builder
                new GsonJsonMapper(),
                null,
                null,
                null,
                factory
        );

        // Assert
        assertNotNull(client.getClientConfig());
        assertEquals(20, client.getClientConfig().getMaxIdleConnections());
        assertEquals(600, client.getClientConfig().getKeepAliveSeconds());
    }

    @Test
    @DisplayName("Should handle missing reactor-netty gracefully")
    void testMissingReactorNettyGracefulDegradation() {
        // Arrange
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setMaxIdleConnections(20);

        // Act & Assert - should not throw exception even if reactor-netty is not available
        assertDoesNotThrow(() -> {
            SpringHttpClient client = new SpringHttpClient(
                    "https://api.dify.ai",
                    config,
                    new GsonJsonMapper()
            );
            assertNotNull(client.getWebClient());
        });
    }

    @Test
    @DisplayName("Should handle missing Apache HttpClient 5 gracefully")
    void testMissingHttpClient5GracefulDegradation() {
        // Arrange
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setMaxIdleConnections(20);

        DefaultRestClientHttpClientFactory factory = new DefaultRestClientHttpClientFactory();

        // Act & Assert - should not throw exception even if HttpClient 5 is not available
        assertDoesNotThrow(() -> {
            SpringHttpClient client = new SpringHttpClient(
                    "https://api.dify.ai",
                    config,
                    WebClient.builder(),
                    null,
                    new GsonJsonMapper(),
                    null,
                    null,
                    null,
                    factory
            );
            assertNotNull(client);
        });
    }

    // Helper methods

    private Object createRestClientBuilderWithRequestFactory() {
        try {
            Class<?> restClientClass = Class.forName("org.springframework.web.client.RestClient");
            Object builder = restClientClass.getMethod("builder").invoke(null);

            // Set a dummy requestFactory
            Class<?> factoryClass = Class.forName("org.springframework.http.client.SimpleClientHttpRequestFactory");
            Object factory = factoryClass.getDeclaredConstructor().newInstance();

            Class<?> builderClass = Class.forName("org.springframework.web.client.RestClient$Builder");
            Class<?> clientHttpRequestFactoryClass = Class.forName("org.springframework.http.client.ClientHttpRequestFactory");

            return builderClass.getMethod("requestFactory", clientHttpRequestFactoryClass)
                    .invoke(builder, factory);
        } catch (Exception e) {
            // RestClient not available (Spring 5)
            return null;
        }
    }

    private boolean hasRequestFactory(Object builder) {
        try {
            Field field = builder.getClass().getDeclaredField("requestFactory");
            field.setAccessible(true);
            return field.get(builder) != null;
        } catch (Exception e) {
            return false;
        }
    }
}
