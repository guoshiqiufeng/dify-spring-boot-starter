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

import io.github.guoshiqiufeng.dify.client.integration.spring.http.pool.PoolSettings;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.pool.WebClientConnectionProviderFactory;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Test coverage for ReactorNettyPoolConfigurer
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/2/10
 */
class ReactorNettyPoolConfigurerTest {

    @Test
    void testIsAvailable_trueWhenReactorNettyPresent() {
        WebClientConnectionProviderFactory mockFactory = mock(WebClientConnectionProviderFactory.class);
        ReactorNettyPoolConfigurer configurer = new ReactorNettyPoolConfigurer(mockFactory);

        // This test assumes reactor-netty is on the classpath (which it should be for Spring WebFlux)
        boolean available = configurer.isAvailable();

        assertTrue(available, "ReactorNetty should be available in test environment");
    }

    @Test
    void testGetName_returnsReactorNetty() {
        WebClientConnectionProviderFactory mockFactory = mock(WebClientConnectionProviderFactory.class);
        ReactorNettyPoolConfigurer configurer = new ReactorNettyPoolConfigurer(mockFactory);

        assertEquals("ReactorNetty", configurer.getName());
    }

    @Test
    void testConfigureWebClient_withConnectionProvider() {
        WebClientConnectionProviderFactory mockFactory = mock(WebClientConnectionProviderFactory.class);

        // Mock the connection provider (we can't create a real one without reactor-netty)
        Object mockConnectionProvider = new Object();
        when(mockFactory.buildConnectionProvider(any(PoolSettings.class), anyString()))
                .thenReturn(mockConnectionProvider);

        ReactorNettyPoolConfigurer configurer = new ReactorNettyPoolConfigurer(mockFactory);
        WebClient.Builder builder = WebClient.builder();
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setMaxIdleConnections(10);
        config.setKeepAliveSeconds(300);
        PoolSettings poolSettings = PoolSettings.from(config);

        WebClient.Builder result = configurer.configureWebClient(builder, poolSettings);

        assertNotNull(result);
        verify(mockFactory, times(1)).buildConnectionProvider(eq(poolSettings), eq("dify-webclient-pool"));
    }

    @Test
    void testConfigureWebClient_nullFactory_returnsSameBuilder() {
        ReactorNettyPoolConfigurer configurer = new ReactorNettyPoolConfigurer(null);
        WebClient.Builder builder = WebClient.builder();
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setMaxIdleConnections(10);
        PoolSettings poolSettings = PoolSettings.from(config);

        WebClient.Builder result = configurer.configureWebClient(builder, poolSettings);

        assertNotNull(result);
        assertSame(builder, result);
    }

    @Test
    void testConfigureWebClient_factoryReturnsNull_returnsSameBuilder() {
        WebClientConnectionProviderFactory mockFactory = mock(WebClientConnectionProviderFactory.class);
        when(mockFactory.buildConnectionProvider(any(PoolSettings.class), anyString()))
                .thenReturn(null);

        ReactorNettyPoolConfigurer configurer = new ReactorNettyPoolConfigurer(mockFactory);
        WebClient.Builder builder = WebClient.builder();
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setMaxIdleConnections(10);
        PoolSettings poolSettings = PoolSettings.from(config);

        WebClient.Builder result = configurer.configureWebClient(builder, poolSettings);

        assertNotNull(result);
        verify(mockFactory, times(1)).buildConnectionProvider(any(PoolSettings.class), anyString());
    }

    @Test
    void testConfigureWebClient_factoryThrowsException_returnsSameBuilder() {
        WebClientConnectionProviderFactory mockFactory = mock(WebClientConnectionProviderFactory.class);
        when(mockFactory.buildConnectionProvider(any(PoolSettings.class), anyString()))
                .thenThrow(new RuntimeException("Test exception"));

        ReactorNettyPoolConfigurer configurer = new ReactorNettyPoolConfigurer(mockFactory);
        WebClient.Builder builder = WebClient.builder();
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setMaxIdleConnections(10);
        PoolSettings poolSettings = PoolSettings.from(config);

        WebClient.Builder result = configurer.configureWebClient(builder, poolSettings);

        assertNotNull(result);
        verify(mockFactory, times(1)).buildConnectionProvider(any(PoolSettings.class), anyString());
    }

    @Test
    void testConfigureRestClient_noop() {
        WebClientConnectionProviderFactory mockFactory = mock(WebClientConnectionProviderFactory.class);
        ReactorNettyPoolConfigurer configurer = new ReactorNettyPoolConfigurer(mockFactory);

        Object builder = new Object();
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setMaxIdleConnections(10);
        PoolSettings poolSettings = PoolSettings.from(config);

        Object result = configurer.configureRestClient(builder, poolSettings);

        assertSame(builder, result, "configureRestClient should return the same builder");
        verifyNoInteractions(mockFactory);
    }

    @Test
    void testConfigureWebClient_withNullPoolSettings() {
        WebClientConnectionProviderFactory mockFactory = mock(WebClientConnectionProviderFactory.class);
        when(mockFactory.buildConnectionProvider(any(), anyString()))
                .thenReturn(new Object());

        ReactorNettyPoolConfigurer configurer = new ReactorNettyPoolConfigurer(mockFactory);
        WebClient.Builder builder = WebClient.builder();

        WebClient.Builder result = configurer.configureWebClient(builder, null);

        assertNotNull(result);
        verify(mockFactory, times(1)).buildConnectionProvider(eq(null), eq("dify-webclient-pool"));
    }

    @Test
    void testConfigureWebClient_usesCorrectPoolName() {
        WebClientConnectionProviderFactory mockFactory = mock(WebClientConnectionProviderFactory.class);
        when(mockFactory.buildConnectionProvider(any(PoolSettings.class), anyString()))
                .thenReturn(new Object());

        ReactorNettyPoolConfigurer configurer = new ReactorNettyPoolConfigurer(mockFactory);
        WebClient.Builder builder = WebClient.builder();
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setMaxIdleConnections(10);
        PoolSettings poolSettings = PoolSettings.from(config);

        configurer.configureWebClient(builder, poolSettings);

        verify(mockFactory, times(1)).buildConnectionProvider(any(PoolSettings.class), eq("dify-webclient-pool"));
    }
}
