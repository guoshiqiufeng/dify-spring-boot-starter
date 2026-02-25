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
import io.github.guoshiqiufeng.dify.client.integration.spring.http.pool.WebClientConnectionProviderFactory;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DefaultWebClientFactory
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/2/24
 */
@ExtendWith(MockitoExtension.class)
class DefaultWebClientFactoryTest {

    @Mock
    private WebClientConnectionProviderFactory connectionProviderFactory;

    @Mock
    private ConnectionPoolConfigurer configurer;

    private DefaultWebClientFactory factory;
    private DifyProperties.ClientConfig clientConfig;

    @BeforeEach
    void setUp() {
        List<ConnectionPoolConfigurer> configurers = new ArrayList<>();
        configurers.add(configurer);
        factory = new DefaultWebClientFactory(connectionProviderFactory, configurers);
        clientConfig = new DifyProperties.ClientConfig();
    }

    @Test
    void testCreateWebClientWithNullBuilder() {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();

        // Act
        WebClient webClient = factory.createWebClient(null, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(webClient);
    }

    @Test
    void testCreateWebClientWithCustomBuilder() {
        // Arrange
        WebClient.Builder builder = WebClient.builder();
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();

        // Act
        WebClient webClient = factory.createWebClient(builder, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(webClient);
    }

    @Test
    void testCreateWebClientWithNullConfig() {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();

        // Act
        WebClient webClient = factory.createWebClient(null, baseUrl, null, headers, interceptors);

        // Assert
        assertNotNull(webClient);
    }

    @Test
    void testCreateWebClientWithTimeouts() {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();
        clientConfig.setConnectTimeout(10);
        clientConfig.setReadTimeout(20);
        clientConfig.setWriteTimeout(30);

        // Act
        WebClient webClient = factory.createWebClient(null, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(webClient);
    }

    @Test
    void testCreateWebClientWithPartialTimeouts() {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();
        clientConfig.setConnectTimeout(10);

        // Act
        WebClient webClient = factory.createWebClient(null, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(webClient);
    }

    @Test
    void testCreateWebClientWithDifferentWriteTimeout() {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();
        clientConfig.setConnectTimeout(10);
        clientConfig.setReadTimeout(20);
        clientConfig.setWriteTimeout(40);

        // Act
        WebClient webClient = factory.createWebClient(null, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(webClient);
    }

    @Test
    void testCreateWebClientWithHeaders() {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Custom-Header", "value1");
        headers.add("Authorization", "Bearer token");
        List<Object> interceptors = new ArrayList<>();

        // Act
        WebClient webClient = factory.createWebClient(null, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(webClient);
    }

    @Test
    void testCreateWebClientWithMultiValueHeaders() {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Custom-Header", "value1");
        headers.add("X-Custom-Header", "value2");
        List<Object> interceptors = new ArrayList<>();

        // Act
        WebClient webClient = factory.createWebClient(null, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(webClient);
    }

    @Test
    void testCreateWebClientWithEmptyHeaders() {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();

        // Act
        WebClient webClient = factory.createWebClient(null, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(webClient);
    }

    @Test
    void testCreateWebClientWithNullHeaders() {
        // Arrange
        String baseUrl = "http://example.com";
        List<Object> interceptors = new ArrayList<>();

        // Act
        WebClient webClient = factory.createWebClient(null, baseUrl, clientConfig, null, interceptors);

        // Assert
        assertNotNull(webClient);
    }

    @Test
    void testCreateWebClientWithLogging() {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();
        clientConfig.setLogging(true);

        // Act
        WebClient webClient = factory.createWebClient(null, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(webClient);
    }

    @Test
    void testCreateWebClientWithLoggingAndMaskingDisabled() {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();
        clientConfig.setLogging(true);
        clientConfig.setLoggingMaskEnabled(false);

        // Act
        WebClient webClient = factory.createWebClient(null, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(webClient);
    }

    @Test
    void testCreateWebClientWithLoggingAndCustomLogBodyMaxBytes() {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();
        clientConfig.setLogging(true);
        clientConfig.setLogBodyMaxBytes(5120);

        // Act
        WebClient webClient = factory.createWebClient(null, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(webClient);
    }

    @Test
    void testCreateWebClientWithLoggingAndLogBinaryBodyEnabled() {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();
        clientConfig.setLogging(true);
        clientConfig.setLogBinaryBody(true);

        // Act
        WebClient webClient = factory.createWebClient(null, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(webClient);
    }

    @Test
    void testCreateWebClientWithInterceptors() {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();
        interceptors.add(new Object());

        // Act
        WebClient webClient = factory.createWebClient(null, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(webClient);
    }

    @Test
    void testCreateWebClientWithNullInterceptors() {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();

        // Act
        WebClient webClient = factory.createWebClient(null, baseUrl, clientConfig, headers, null);

        // Assert
        assertNotNull(webClient);
    }

    @Test
    void testCreateWebClientWithConnectionPool() {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();
        clientConfig.setMaxIdleConnections(50);
        clientConfig.setKeepAliveSeconds(60);

        when(configurer.isAvailable()).thenReturn(true);
        when(configurer.getName()).thenReturn("TestConfigurer");
        when(configurer.configureWebClient(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        WebClient webClient = factory.createWebClient(null, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(webClient);
        verify(configurer).isAvailable();
        verify(configurer).configureWebClient(any(), any());
    }

    @Test
    void testCreateWebClientWithConfigurerNotAvailable() {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();
        clientConfig.setMaxIdleConnections(50);

        when(configurer.isAvailable()).thenReturn(false);

        // Act
        WebClient webClient = factory.createWebClient(null, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(webClient);
        verify(configurer).isAvailable();
        verify(configurer, never()).configureWebClient(any(), any());
    }

    @Test
    void testCreateWebClientWithNullConfigurers() {
        // Arrange
        DefaultWebClientFactory factoryWithNullConfigurers =
                new DefaultWebClientFactory(connectionProviderFactory, null);
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();
        clientConfig.setMaxIdleConnections(50);

        // Act
        WebClient webClient = factoryWithNullConfigurers.createWebClient(
                null, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(webClient);
    }

    @Test
    void testCreateWebClientWithEmptyBaseUrl() {
        // Arrange
        String baseUrl = "";
        HttpHeaders headers = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();

        // Act
        WebClient webClient = factory.createWebClient(null, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(webClient);
    }

    @Test
    void testCreateWebClientWithAllFeatures() {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Custom-Header", "value1");
        List<Object> interceptors = new ArrayList<>();
        clientConfig.setConnectTimeout(10);
        clientConfig.setReadTimeout(20);
        clientConfig.setWriteTimeout(30);
        clientConfig.setMaxIdleConnections(50);
        clientConfig.setKeepAliveSeconds(60);
        clientConfig.setLogging(true);
        clientConfig.setLoggingMaskEnabled(false);
        clientConfig.setLogBodyMaxBytes(5120);

        when(configurer.isAvailable()).thenReturn(true);
        when(configurer.getName()).thenReturn("TestConfigurer");
        when(configurer.configureWebClient(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        WebClient webClient = factory.createWebClient(null, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(webClient);
    }

    @Test
    void testCreateWebClientWithOnlyConnectTimeout() {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();
        clientConfig.setConnectTimeout(15);
        // readTimeout and writeTimeout are null

        // Act
        WebClient webClient = factory.createWebClient(null, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(webClient);
    }

    @Test
    void testCreateWebClientWithOnlyReadTimeout() {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();
        clientConfig.setReadTimeout(25);
        // connectTimeout and writeTimeout are null

        // Act
        WebClient webClient = factory.createWebClient(null, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(webClient);
    }

    @Test
    void testCreateWebClientWithOnlyWriteTimeout() {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();
        clientConfig.setWriteTimeout(35);
        // connectTimeout and readTimeout are null

        // Act
        WebClient webClient = factory.createWebClient(null, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(webClient);
    }

    @Test
    void testCreateWebClientWithHeadersContainingNullValues() {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        headers.put("X-Null-Header", null);
        headers.add("X-Valid-Header", "value");
        List<Object> interceptors = new ArrayList<>();

        // Act
        WebClient webClient = factory.createWebClient(null, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(webClient);
    }

    @Test
    void testCreateWebClientWithHeadersContainingEmptyValues() {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        headers.put("X-Empty-Header", new ArrayList<>());
        headers.add("X-Valid-Header", "value");
        List<Object> interceptors = new ArrayList<>();

        // Act
        WebClient webClient = factory.createWebClient(null, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(webClient);
    }

    @Test
    void testCreateWebClientWithExchangeFilterFunctionInterceptor() {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();

        // Create a real ExchangeFilterFunction
        org.springframework.web.reactive.function.client.ExchangeFilterFunction filter =
            (request, next) -> next.exchange(request);
        interceptors.add(filter);

        // Act
        WebClient webClient = factory.createWebClient(null, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(webClient);
    }

    @Test
    void testCreateWebClientWithNullPoolSettings() {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();
        // clientConfig with no pool settings will result in null PoolSettings

        // Act
        WebClient webClient = factory.createWebClient(null, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(webClient);
    }

    @Test
    void testCreateWebClientWithConnectAndReadTimeout() {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();
        clientConfig.setConnectTimeout(10);
        clientConfig.setReadTimeout(20);
        // writeTimeout is null

        // Act
        WebClient webClient = factory.createWebClient(null, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(webClient);
    }

    @Test
    void testCreateWebClientWithConnectAndWriteTimeout() {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();
        clientConfig.setConnectTimeout(10);
        clientConfig.setWriteTimeout(30);
        // readTimeout is null

        // Act
        WebClient webClient = factory.createWebClient(null, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(webClient);
    }

    @Test
    void testCreateWebClientWithReadAndWriteTimeout() {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();
        clientConfig.setReadTimeout(20);
        clientConfig.setWriteTimeout(30);
        // connectTimeout is null

        // Act
        WebClient webClient = factory.createWebClient(null, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(webClient);
    }
}
