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
import io.github.guoshiqiufeng.dify.client.integration.spring.http.pool.RestClientHttpClientFactory;
import io.github.guoshiqiufeng.dify.client.integration.spring.version.SpringVersionDetector;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DefaultRestClientFactory
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/2/24
 */
@ExtendWith(MockitoExtension.class)
class DefaultRestClientFactoryTest {

    @Mock
    private RestClientHttpClientFactory httpClientFactory;

    @Mock
    private ConnectionPoolConfigurer configurer;

    private DefaultRestClientFactory factory;
    private DifyProperties.ClientConfig clientConfig;

    @BeforeEach
    void setUp() {
        List<ConnectionPoolConfigurer> configurers = new ArrayList<>();
        configurers.add(configurer);
        factory = new DefaultRestClientFactory(httpClientFactory, configurers);
        clientConfig = new DifyProperties.ClientConfig();
    }

    @Test
    void testIsRestClientAvailable() {
        // Act
        boolean available = factory.isRestClientAvailable();

        // Assert
        assertEquals(SpringVersionDetector.hasRestClient(), available);
    }

    @Test
    @EnabledIf("isRestClientAvailable")
    void testCreateRestClientWithNullBuilder() {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();

        // Act
        Object restClient = factory.createRestClient(null, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(restClient);
    }

    @Test
    @EnabledIf("isRestClientAvailable")
    void testCreateRestClientWithCustomBuilder() throws Exception {
        // Arrange
        Class<?> restClientClass = Class.forName("org.springframework.web.client.RestClient");
        Object builder = restClientClass.getMethod("builder").invoke(null);
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();

        // Act
        Object restClient = factory.createRestClient(builder, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(restClient);
    }

    @Test
    @EnabledIf("isRestClientAvailable")
    void testCreateRestClientWithNullConfig() throws Exception {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();

        // Act
        Object restClient = factory.createRestClient(null, baseUrl, null, headers, interceptors);

        // Assert
        assertNotNull(restClient);
    }

    @Test
    @EnabledIf("isRestClientAvailable")
    void testCreateRestClientWithEmptyHeaders() {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();

        // Act
        Object restClient = factory.createRestClient(null, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(restClient);
    }

    @Test
    @EnabledIf("isRestClientAvailable")
    void testCreateRestClientWithNullHeaders() {
        // Arrange
        String baseUrl = "http://example.com";
        List<Object> interceptors = new ArrayList<>();

        // Act
        Object restClient = factory.createRestClient(null, baseUrl, clientConfig, null, interceptors);

        // Assert
        assertNotNull(restClient);
    }

    @Test
    @EnabledIf("isRestClientAvailable")
    void testCreateRestClientWithCustomHeaders() {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Custom-Header", "test-value");
        headers.add("Authorization", "Bearer token");
        List<Object> interceptors = new ArrayList<>();

        // Act
        Object restClient = factory.createRestClient(null, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(restClient);
    }

    @Test
    @EnabledIf("isRestClientAvailable")
    void testCreateRestClientWithLoggingEnabled() {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();
        clientConfig.setLogging(true);

        // Act
        Object restClient = factory.createRestClient(null, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(restClient);
    }

    @Test
    @EnabledIf("isRestClientAvailable")
    void testCreateRestClientWithLoggingEnabledAndMaskingDisabled() {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();
        clientConfig.setLogging(true);
        clientConfig.setLoggingMaskEnabled(false);

        // Act
        Object restClient = factory.createRestClient(null, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(restClient);
    }

    @Test
    @EnabledIf("isRestClientAvailable")
    void testCreateRestClientWithLoggingEnabledAndCustomLogBodyMaxBytes() {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();
        clientConfig.setLogging(true);
        clientConfig.setLogBodyMaxBytes(5120);

        // Act
        Object restClient = factory.createRestClient(null, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(restClient);
    }

    @Test
    @EnabledIf("isRestClientAvailable")
    void testCreateRestClientWithLoggingEnabledAndLogBinaryBodyEnabled() {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();
        clientConfig.setLogging(true);
        clientConfig.setLogBinaryBody(true);

        // Act
        Object restClient = factory.createRestClient(null, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(restClient);
    }

    @Test
    @EnabledIf("isRestClientAvailable")
    void testCreateRestClientWithEmptyInterceptors() {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();

        // Act
        Object restClient = factory.createRestClient(null, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(restClient);
    }

    @Test
    @EnabledIf("isRestClientAvailable")
    void testCreateRestClientWithNullInterceptors() {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();

        // Act
        Object restClient = factory.createRestClient(null, baseUrl, clientConfig, headers, null);

        // Assert
        assertNotNull(restClient);
    }

    @Test
    @EnabledIf("isRestClientAvailable")
    void testCreateRestClientWithConnectionPoolConfig() {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();
        clientConfig.setMaxIdleConnections(50);
        clientConfig.setKeepAliveSeconds(60);

        when(configurer.isAvailable()).thenReturn(true);
        when(configurer.getName()).thenReturn("TestConfigurer");
        when(configurer.configureRestClient(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Object restClient = factory.createRestClient(null, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(restClient);
        verify(configurer, atLeastOnce()).isAvailable();
    }

    @Test
    @EnabledIf("isRestClientAvailable")
    void testCreateRestClientWithHttpClientFactory() {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();
        clientConfig.setMaxIdleConnections(50);
        clientConfig.setKeepAliveSeconds(60);

        when(httpClientFactory.buildHttpClient(any())).thenReturn(null);

        // Act
        Object restClient = factory.createRestClient(null, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(restClient);
        verify(httpClientFactory).buildHttpClient(any());
    }

    @Test
    @EnabledIf("isRestClientAvailable")
    void testCreateRestClientWithHttpClientFactoryThrowsException() {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();
        clientConfig.setMaxIdleConnections(50);

        when(httpClientFactory.buildHttpClient(any())).thenThrow(new RuntimeException("Test exception"));

        // Act
        Object restClient = factory.createRestClient(null, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(restClient);
    }

    @Test
    @EnabledIf("isRestClientAvailable")
    void testCreateRestClientWithConfigurerNotAvailable() {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();
        clientConfig.setMaxIdleConnections(50);

        when(configurer.isAvailable()).thenReturn(false);

        // Act
        Object restClient = factory.createRestClient(null, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(restClient);
        verify(configurer).isAvailable();
        verify(configurer, never()).configureRestClient(any(), any());
    }

    @Test
    @EnabledIf("isRestClientAvailable")
    void testCreateRestClientWithNullConfigurers() {
        // Arrange
        DefaultRestClientFactory factoryWithNullConfigurers =
                new DefaultRestClientFactory(httpClientFactory, null);
        String baseUrl = "http://example.com";
        HttpHeaders headers = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();
        clientConfig.setMaxIdleConnections(50);

        // Act
        Object restClient = factoryWithNullConfigurers.createRestClient(
                null, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(restClient);
    }

    @Test
    @EnabledIf("isRestClientAvailable")
    void testCreateRestClientWithEmptyBaseUrl() {
        // Arrange
        String baseUrl = "";
        HttpHeaders headers = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();

        // Act
        Object restClient = factory.createRestClient(null, baseUrl, clientConfig, headers, interceptors);

        // Assert
        assertNotNull(restClient);
    }

    static boolean isRestClientAvailable() {
        return SpringVersionDetector.hasRestClient();
    }
}
