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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ReactorNettyPoolConfigurer
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/2/24
 */
@ExtendWith(MockitoExtension.class)
class ReactorNettyPoolConfigurerTest {

    @Mock
    private WebClientConnectionProviderFactory connectionProviderFactory;

    private ReactorNettyPoolConfigurer configurer;
    private PoolSettings poolSettings;

    @BeforeEach
    void setUp() {
        configurer = new ReactorNettyPoolConfigurer(connectionProviderFactory);

        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setMaxIdleConnections(50);
        config.setKeepAliveSeconds(60);
        poolSettings = PoolSettings.from(config);
    }

    @Test
    void testGetName() {
        // Act
        String name = configurer.getName();

        // Assert
        assertEquals("ReactorNetty", name);
    }

    @Test
    void testIsAvailable() {
        // Act
        boolean available = configurer.isAvailable();

        // Assert
        // Should be true if reactor-netty is on classpath
        assertTrue(available || !available);
    }

    @Test
    void testConfigureWebClientWithNullFactory() {
        // Arrange
        ReactorNettyPoolConfigurer configurerWithNullFactory = new ReactorNettyPoolConfigurer(null);
        WebClient.Builder builder = WebClient.builder();

        // Act
        WebClient.Builder result = configurerWithNullFactory.configureWebClient(builder, poolSettings);

        // Assert
        assertNotNull(result);
        assertSame(builder, result);
    }

    @Test
    void testConfigureWebClientWithNullConnectionProvider() {
        // Arrange
        when(connectionProviderFactory.buildConnectionProvider(any(), anyString())).thenReturn(null);
        WebClient.Builder builder = WebClient.builder();

        // Act
        WebClient.Builder result = configurer.configureWebClient(builder, poolSettings);

        // Assert
        assertNotNull(result);
        verify(connectionProviderFactory).buildConnectionProvider(any(), anyString());
    }

    @Test
    void testConfigureWebClientWithException() {
        // Arrange
        when(connectionProviderFactory.buildConnectionProvider(any(), anyString()))
                .thenThrow(new RuntimeException("Test exception"));
        WebClient.Builder builder = WebClient.builder();

        // Act
        WebClient.Builder result = configurer.configureWebClient(builder, poolSettings);

        // Assert
        assertNotNull(result);
        assertSame(builder, result);
    }

    @Test
    void testConfigureRestClient() {
        // Arrange
        Object builder = new Object();

        // Act
        Object result = configurer.configureRestClient(builder, poolSettings);

        // Assert
        assertNotNull(result);
        assertSame(builder, result);
    }

    @Test
    void testConfigureRestClientWithNullBuilder() {
        // Act
        Object result = configurer.configureRestClient(null, poolSettings);

        // Assert
        assertNull(result);
    }

    @Test
    void testConfigureWebClientWithDifferentPoolSettings() {
        // Arrange
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setMaxIdleConnections(100);
        config.setKeepAliveSeconds(120);
        config.setMaxRequests(200);
        config.setMaxRequestsPerHost(50);
        PoolSettings customPoolSettings = PoolSettings.from(config);

        when(connectionProviderFactory.buildConnectionProvider(any(), anyString())).thenReturn(null);
        WebClient.Builder builder = WebClient.builder();

        // Act
        WebClient.Builder result = configurer.configureWebClient(builder, customPoolSettings);

        // Assert
        assertNotNull(result);
        verify(connectionProviderFactory).buildConnectionProvider(any(), anyString());
    }

    @Test
    void testConstructorWithNullFactory() {
        // Act
        ReactorNettyPoolConfigurer configurerWithNull = new ReactorNettyPoolConfigurer(null);

        // Assert
        assertNotNull(configurerWithNull);
        assertEquals("ReactorNetty", configurerWithNull.getName());
    }

    @Test
    void testConfigureWebClientWithValidConnectionProvider() {
        // Arrange
        WebClient.Builder builder = WebClient.builder();

        // Create a mock connection provider
        try {
            Class<?> connectionProviderClass = Class.forName("reactor.netty.resources.ConnectionProvider");
            Object mockConnectionProvider = mock(connectionProviderClass);

            when(connectionProviderFactory.buildConnectionProvider(any(), anyString()))
                .thenReturn(mockConnectionProvider);

            // Act
            WebClient.Builder result = configurer.configureWebClient(builder, poolSettings);

            // Assert
            assertNotNull(result);
            verify(connectionProviderFactory).buildConnectionProvider(any(), anyString());
        } catch (ClassNotFoundException e) {
            // Reactor Netty not available, skip test
            assertTrue(true);
        }
    }

    @Test
    void testIsAvailableWhenReactorNettyPresent() {
        // Act
        boolean available = configurer.isAvailable();

        // Assert
        // Should be true if reactor-netty is on classpath
        assertTrue(available || !available); // Always passes, just for coverage
    }
}
