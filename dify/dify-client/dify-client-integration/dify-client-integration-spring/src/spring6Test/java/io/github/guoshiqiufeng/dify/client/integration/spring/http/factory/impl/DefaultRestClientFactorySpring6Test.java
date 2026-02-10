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
import io.github.guoshiqiufeng.dify.client.integration.spring.http.pool.PoolSettings;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.pool.RestClientHttpClientFactory;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Spring 6 specific tests for DefaultRestClientFactory
 * Tests the main path that requires RestClient (Spring 6+)
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/2/10
 */
@ExtendWith(MockitoExtension.class)
class DefaultRestClientFactorySpring6Test {

    @Mock
    private RestClientHttpClientFactory httpClientFactory;

    @Mock
    private ConnectionPoolConfigurer configurer;

    private DefaultRestClientFactory factory;

    @BeforeEach
    void setUp() {
        List<ConnectionPoolConfigurer> configurers = new ArrayList<>();
        configurers.add(configurer);
        factory = new DefaultRestClientFactory(httpClientFactory, configurers);
    }

    @Test
    void testIsRestClientAvailable() {
        // Act
        boolean available = factory.isRestClientAvailable();

        // Assert
        assertTrue(available, "RestClient should be available in Spring 6");
    }

    @Test
    void testCreateRestClientWithNullBuilder() {
        // Arrange
        String baseUrl = "http://example.com";
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        HttpHeaders defaultHeaders = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();

        // Act
        Object restClient = factory.createRestClient(null, baseUrl, clientConfig, defaultHeaders, interceptors);

        // Assert
        assertNotNull(restClient, "Should create RestClient when builder is null");
        assertInstanceOf(RestClient.class, restClient);
    }

    @Test
    void testCreateRestClientWithProvidedBuilder() {
        // Arrange
        RestClient.Builder builder = RestClient.builder();
        String baseUrl = "http://example.com";
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        HttpHeaders defaultHeaders = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();

        // Act
        Object restClient = factory.createRestClient(builder, baseUrl, clientConfig, defaultHeaders, interceptors);

        // Assert
        assertNotNull(restClient, "Should create RestClient with provided builder");
        assertInstanceOf(RestClient.class, restClient);
    }

    @Test
    void testCreateRestClientWithDefaultHeaders() {
        // Arrange
        String baseUrl = "http://example.com";
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        HttpHeaders defaultHeaders = new HttpHeaders();
        defaultHeaders.add("X-API-Key", "test-key");
        defaultHeaders.add("User-Agent", "Dify-Client/1.0");
        List<Object> interceptors = new ArrayList<>();

        // Act
        Object restClient = factory.createRestClient(null, baseUrl, clientConfig, defaultHeaders, interceptors);

        // Assert
        assertNotNull(restClient, "Should create RestClient with default headers");
        assertInstanceOf(RestClient.class, restClient);
    }

    @Test
    void testCreateRestClientWithLoggingEnabled() {
        // Arrange
        String baseUrl = "http://example.com";
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        clientConfig.setLogging(true);
        clientConfig.setLoggingMaskEnabled(true);
        clientConfig.setLogBodyMaxBytes(8192);
        clientConfig.setLogBinaryBody(false);
        HttpHeaders defaultHeaders = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();

        // Act
        Object restClient = factory.createRestClient(null, baseUrl, clientConfig, defaultHeaders, interceptors);

        // Assert
        assertNotNull(restClient, "Should create RestClient with logging interceptor");
        assertInstanceOf(RestClient.class, restClient);
    }

    @Test
    void testCreateRestClientWithLoggingEnabledAndDefaultConfig() {
        // Arrange
        String baseUrl = "http://example.com";
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        clientConfig.setLogging(true);
        // loggingMaskEnabled, logBodyMaxBytes, logBinaryBody are null (use defaults)
        HttpHeaders defaultHeaders = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();

        // Act
        Object restClient = factory.createRestClient(null, baseUrl, clientConfig, defaultHeaders, interceptors);

        // Assert
        assertNotNull(restClient, "Should create RestClient with default logging config");
        assertInstanceOf(RestClient.class, restClient);
    }

    @Test
    void testCreateRestClientWithCustomInterceptors() {
        // Arrange
        String baseUrl = "http://example.com";
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        HttpHeaders defaultHeaders = new HttpHeaders();

        List<Object> interceptors = new ArrayList<>();
        ClientHttpRequestInterceptor customInterceptor = (request, body, execution) -> execution.execute(request, body);
        interceptors.add(customInterceptor);
        interceptors.add("not-an-interceptor"); // Should be skipped

        // Act
        Object restClient = factory.createRestClient(null, baseUrl, clientConfig, defaultHeaders, interceptors);

        // Assert
        assertNotNull(restClient, "Should create RestClient with custom interceptors");
        assertInstanceOf(RestClient.class, restClient);
    }

    @Test
    void testCreateRestClientWithConnectionPoolConfig() {
        // Arrange
        String baseUrl = "http://example.com";
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        clientConfig.setMaxIdleConnections(50);
        clientConfig.setMaxRequestsPerHost(10);
        clientConfig.setConnectTimeout(5);
        clientConfig.setReadTimeout(30);
        HttpHeaders defaultHeaders = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();

        when(configurer.isAvailable()).thenReturn(true);
        when(configurer.getName()).thenReturn("TestConfigurer");
        when(configurer.configureRestClient(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Object restClient = factory.createRestClient(null, baseUrl, clientConfig, defaultHeaders, interceptors);

        // Assert
        assertNotNull(restClient, "Should create RestClient with connection pool config");
        assertInstanceOf(RestClient.class, restClient);
        verify(configurer, times(1)).configureRestClient(any(), any());
    }

    @Test
    void testCreateRestClientWithHttpClientFactory() throws Exception {
        // Arrange
        String baseUrl = "http://example.com";
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        clientConfig.setMaxIdleConnections(50);
        HttpHeaders defaultHeaders = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();

        // Skip this test if HttpClient is not available (optional dependency)
        try {
            Class.forName("org.apache.hc.client5.http.impl.classic.CloseableHttpClient");
        } catch (ClassNotFoundException e) {
            // HttpClient not available, skip test
            return;
        }

        // Mock httpClientFactory to return a mock HttpClient (use Object to avoid dependency)
        Object mockHttpClient = new Object(); // Simplified mock
        when(httpClientFactory.buildHttpClient(any(PoolSettings.class))).thenReturn(mockHttpClient);

        // Act
        Object restClient = factory.createRestClient(null, baseUrl, clientConfig, defaultHeaders, interceptors);

        // Assert
        assertNotNull(restClient, "Should create RestClient with HttpClient from factory");
        assertInstanceOf(RestClient.class, restClient);
        verify(httpClientFactory, times(1)).buildHttpClient(any(PoolSettings.class));
    }

    @Test
    void testCreateRestClientWithHttpClientFactoryThrowsException() throws Exception {
        // Arrange
        String baseUrl = "http://example.com";
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        clientConfig.setMaxIdleConnections(50);
        HttpHeaders defaultHeaders = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();

        when(httpClientFactory.buildHttpClient(any(PoolSettings.class)))
                .thenThrow(new RuntimeException("HttpClient creation failed"));
        when(configurer.isAvailable()).thenReturn(true);
        when(configurer.configureRestClient(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Object restClient = factory.createRestClient(null, baseUrl, clientConfig, defaultHeaders, interceptors);

        // Assert - should fallback to configurer
        assertNotNull(restClient, "Should create RestClient even when HttpClient factory fails");
        assertInstanceOf(RestClient.class, restClient);
        verify(configurer, times(1)).configureRestClient(any(), any());
    }

    @Test
    void testCreateRestClientWithConfigurerNotAvailable() {
        // Arrange
        String baseUrl = "http://example.com";
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        clientConfig.setMaxIdleConnections(50);
        HttpHeaders defaultHeaders = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();

        when(configurer.isAvailable()).thenReturn(false);

        // Act
        Object restClient = factory.createRestClient(null, baseUrl, clientConfig, defaultHeaders, interceptors);

        // Assert
        assertNotNull(restClient, "Should create RestClient even when configurer is not available");
        assertInstanceOf(RestClient.class, restClient);
        verify(configurer, never()).configureRestClient(any(), any());
    }

    @Test
    void testCreateRestClientWithNullClientConfig() {
        // Arrange
        String baseUrl = "http://example.com";
        HttpHeaders defaultHeaders = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();

        // Act
        Object restClient = factory.createRestClient(null, baseUrl, null, defaultHeaders, interceptors);

        // Assert
        assertNotNull(restClient, "Should create RestClient with null client config");
        assertInstanceOf(RestClient.class, restClient);
    }

    @Test
    void testCreateRestClientWithNullDefaultHeaders() {
        // Arrange
        String baseUrl = "http://example.com";
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        List<Object> interceptors = new ArrayList<>();

        // Act
        Object restClient = factory.createRestClient(null, baseUrl, clientConfig, null, interceptors);

        // Assert
        assertNotNull(restClient, "Should create RestClient with null default headers");
        assertInstanceOf(RestClient.class, restClient);
    }

    @Test
    void testCreateRestClientWithEmptyDefaultHeaders() {
        // Arrange
        String baseUrl = "http://example.com";
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        HttpHeaders defaultHeaders = new HttpHeaders(); // Empty
        List<Object> interceptors = new ArrayList<>();

        // Act
        Object restClient = factory.createRestClient(null, baseUrl, clientConfig, defaultHeaders, interceptors);

        // Assert
        assertNotNull(restClient, "Should create RestClient with empty default headers");
        assertInstanceOf(RestClient.class, restClient);
    }

    @Test
    void testCreateRestClientWithNullInterceptors() {
        // Arrange
        String baseUrl = "http://example.com";
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        HttpHeaders defaultHeaders = new HttpHeaders();

        // Act
        Object restClient = factory.createRestClient(null, baseUrl, clientConfig, defaultHeaders, null);

        // Assert
        assertNotNull(restClient, "Should create RestClient with null interceptors");
        assertInstanceOf(RestClient.class, restClient);
    }

    @Test
    void testCreateRestClientWithEmptyInterceptors() {
        // Arrange
        String baseUrl = "http://example.com";
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        HttpHeaders defaultHeaders = new HttpHeaders();
        List<Object> interceptors = Collections.emptyList();

        // Act
        Object restClient = factory.createRestClient(null, baseUrl, clientConfig, defaultHeaders, interceptors);

        // Assert
        assertNotNull(restClient, "Should create RestClient with empty interceptors");
        assertInstanceOf(RestClient.class, restClient);
    }

    @Test
    void testCreateRestClientWithMultipleHeaderValues() {
        // Arrange
        String baseUrl = "http://example.com";
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        HttpHeaders defaultHeaders = new HttpHeaders();
        defaultHeaders.add("Accept", "application/json");
        defaultHeaders.add("Accept", "application/xml");
        List<Object> interceptors = new ArrayList<>();

        // Act
        Object restClient = factory.createRestClient(null, baseUrl, clientConfig, defaultHeaders, interceptors);

        // Assert
        assertNotNull(restClient, "Should create RestClient with multiple header values");
        assertInstanceOf(RestClient.class, restClient);
    }

    @Test
    void testCreateRestClientWithEmptyConfigurersList() {
        // Arrange
        DefaultRestClientFactory factoryWithEmptyConfigurers = new DefaultRestClientFactory(
                httpClientFactory,
                Collections.emptyList()
        );
        String baseUrl = "http://example.com";
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        clientConfig.setMaxIdleConnections(50);
        HttpHeaders defaultHeaders = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();

        // Act
        Object restClient = factoryWithEmptyConfigurers.createRestClient(null, baseUrl, clientConfig, defaultHeaders, interceptors);

        // Assert
        assertNotNull(restClient, "Should create RestClient with empty configurers list");
        assertInstanceOf(RestClient.class, restClient);
    }

    @Test
    void testCreateRestClientWithNullConfigurers() {
        // Arrange
        DefaultRestClientFactory factoryWithNullConfigurers = new DefaultRestClientFactory(
                httpClientFactory,
                null
        );
        String baseUrl = "http://example.com";
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        clientConfig.setMaxIdleConnections(50);
        HttpHeaders defaultHeaders = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();

        // Act
        Object restClient = factoryWithNullConfigurers.createRestClient(null, baseUrl, clientConfig, defaultHeaders, interceptors);

        // Assert
        assertNotNull(restClient, "Should create RestClient with null configurers");
        assertInstanceOf(RestClient.class, restClient);
    }

    @Test
    void testCreateRestClientWithNullHttpClientFactory() {
        // Arrange
        DefaultRestClientFactory factoryWithNullHttpClientFactory = new DefaultRestClientFactory(
                null,
                Collections.singletonList(configurer)
        );
        String baseUrl = "http://example.com";
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        clientConfig.setMaxIdleConnections(50);
        HttpHeaders defaultHeaders = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();

        when(configurer.isAvailable()).thenReturn(true);
        when(configurer.configureRestClient(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Object restClient = factoryWithNullHttpClientFactory.createRestClient(null, baseUrl, clientConfig, defaultHeaders, interceptors);

        // Assert
        assertNotNull(restClient, "Should create RestClient with null HttpClient factory");
        assertInstanceOf(RestClient.class, restClient);
        verify(configurer, times(1)).configureRestClient(any(), any());
    }

    @Test
    void testCreateRestClientWithLoggingDisabled() {
        // Arrange
        String baseUrl = "http://example.com";
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        clientConfig.setLogging(false);
        HttpHeaders defaultHeaders = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();

        // Act
        Object restClient = factory.createRestClient(null, baseUrl, clientConfig, defaultHeaders, interceptors);

        // Assert
        assertNotNull(restClient, "Should create RestClient without logging interceptor");
        assertInstanceOf(RestClient.class, restClient);
    }

    @Test
    void testCreateRestClientWithBinaryBodyLoggingEnabled() {
        // Arrange
        String baseUrl = "http://example.com";
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        clientConfig.setLogging(true);
        clientConfig.setLoggingMaskEnabled(false);
        clientConfig.setLogBodyMaxBytes(0); // Unlimited
        clientConfig.setLogBinaryBody(true);
        HttpHeaders defaultHeaders = new HttpHeaders();
        List<Object> interceptors = new ArrayList<>();

        // Act
        Object restClient = factory.createRestClient(null, baseUrl, clientConfig, defaultHeaders, interceptors);

        // Assert
        assertNotNull(restClient, "Should create RestClient with binary body logging enabled");
        assertInstanceOf(RestClient.class, restClient);
    }
}
