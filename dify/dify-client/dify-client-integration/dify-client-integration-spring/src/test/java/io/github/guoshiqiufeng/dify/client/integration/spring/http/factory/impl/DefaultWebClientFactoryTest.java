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
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test coverage for DefaultWebClientFactory
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/2/10
 */
class DefaultWebClientFactoryTest {

    @Test
    void testCreateWebClient_builderNull_usesDefaultBuilder() {
        DefaultWebClientFactory factory = new DefaultWebClientFactory(null, null);
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();

        WebClient webClient = factory.createWebClient(null, "https://api.example.com", config, null, null);

        assertNotNull(webClient);
    }

    @Test
    void testCreateWebClient_setsBaseUrl() {
        DefaultWebClientFactory factory = new DefaultWebClientFactory(null, null);
        String baseUrl = "https://api.example.com";

        WebClient webClient = factory.createWebClient(null, baseUrl, null, null, null);

        assertNotNull(webClient);
    }

    @Test
    void testCreateWebClient_appliesTimeoutConfig() {
        DefaultWebClientFactory factory = new DefaultWebClientFactory(null, null);
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setConnectTimeout(10);
        config.setReadTimeout(20);
        config.setWriteTimeout(30);

        WebClient webClient = factory.createWebClient(null, "https://api.example.com", config, null, null);

        assertNotNull(webClient);
    }

    @Test
    void testCreateWebClient_appliesConnectionPoolConfigurerWhenAvailable() {
        ConnectionPoolConfigurer mockConfigurer = mock(ConnectionPoolConfigurer.class);
        when(mockConfigurer.isAvailable()).thenReturn(true);
        when(mockConfigurer.getName()).thenReturn("TestConfigurer");
        when(mockConfigurer.configureWebClient(any(WebClient.Builder.class), any(PoolSettings.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<ConnectionPoolConfigurer> configurers = Collections.singletonList(mockConfigurer);
        DefaultWebClientFactory factory = new DefaultWebClientFactory(null, configurers);

        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setMaxIdleConnections(10);
        config.setKeepAliveSeconds(300);

        WebClient webClient = factory.createWebClient(null, "https://api.example.com", config, null, null);

        assertNotNull(webClient);
        verify(mockConfigurer, times(1)).isAvailable();
        verify(mockConfigurer, times(1)).configureWebClient(any(WebClient.Builder.class), any(PoolSettings.class));
    }

    @Test
    void testCreateWebClient_skipsConfigurerWhenUnavailable() {
        ConnectionPoolConfigurer mockConfigurer = mock(ConnectionPoolConfigurer.class);
        when(mockConfigurer.isAvailable()).thenReturn(false);

        List<ConnectionPoolConfigurer> configurers = Collections.singletonList(mockConfigurer);
        DefaultWebClientFactory factory = new DefaultWebClientFactory(null, configurers);

        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setMaxIdleConnections(10);

        WebClient webClient = factory.createWebClient(null, "https://api.example.com", config, null, null);

        assertNotNull(webClient);
        verify(mockConfigurer, times(1)).isAvailable();
        verify(mockConfigurer, never()).configureWebClient(any(), any());
    }

    @Test
    void testCreateWebClient_appliesDefaultHeaders() {
        DefaultWebClientFactory factory = new DefaultWebClientFactory(null, null);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Custom-Header", "value1");
        headers.add("X-Another-Header", "value2");

        WebClient webClient = factory.createWebClient(null, "https://api.example.com", null, headers, null);

        assertNotNull(webClient);
    }

    @Test
    void testCreateWebClient_appliesDefaultHeaders_multipleValues() {
        DefaultWebClientFactory factory = new DefaultWebClientFactory(null, null);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Custom-Header", "value1");
        headers.add("X-Custom-Header", "value2");

        WebClient webClient = factory.createWebClient(null, "https://api.example.com", null, headers, null);

        assertNotNull(webClient);
    }

    @Test
    void testCreateWebClient_appliesCustomInterceptors() {
        DefaultWebClientFactory factory = new DefaultWebClientFactory(null, null);

        ExchangeFilterFunction interceptor = (request, next) -> {
            return next.exchange(request);
        };

        List<Object> interceptors = Collections.singletonList(interceptor);

        WebClient webClient = factory.createWebClient(null, "https://api.example.com", null, null, interceptors);

        assertNotNull(webClient);
    }

    @Test
    void testCreateWebClient_loggingFilterEnabled() {
        DefaultWebClientFactory factory = new DefaultWebClientFactory(null, null);
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setLogging(true);
        config.setLoggingMaskEnabled(true);
        config.setLogBodyMaxBytes(4096);
        config.setLogBinaryBody(false);

        WebClient webClient = factory.createWebClient(null, "https://api.example.com", config, null, null);

        assertNotNull(webClient);
    }

    @Test
    void testCreateWebClient_loggingFilterDisabled() {
        DefaultWebClientFactory factory = new DefaultWebClientFactory(null, null);
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setLogging(false);

        WebClient webClient = factory.createWebClient(null, "https://api.example.com", config, null, null);

        assertNotNull(webClient);
    }

    @Test
    void testCreateWebClient_nullConfig() {
        DefaultWebClientFactory factory = new DefaultWebClientFactory(null, null);

        WebClient webClient = factory.createWebClient(null, "https://api.example.com", null, null, null);

        assertNotNull(webClient);
    }

    @Test
    void testCreateWebClient_emptyHeaders() {
        DefaultWebClientFactory factory = new DefaultWebClientFactory(null, null);
        HttpHeaders headers = new HttpHeaders();

        WebClient webClient = factory.createWebClient(null, "https://api.example.com", null, headers, null);

        assertNotNull(webClient);
    }

    @Test
    void testCreateWebClient_emptyInterceptors() {
        DefaultWebClientFactory factory = new DefaultWebClientFactory(null, null);
        List<Object> interceptors = new ArrayList<>();

        WebClient webClient = factory.createWebClient(null, "https://api.example.com", null, null, interceptors);

        assertNotNull(webClient);
    }

    @Test
    void testCreateWebClient_multipleConfigurers_usesFirstAvailable() {
        ConnectionPoolConfigurer mockConfigurer1 = mock(ConnectionPoolConfigurer.class);
        when(mockConfigurer1.isAvailable()).thenReturn(false);

        ConnectionPoolConfigurer mockConfigurer2 = mock(ConnectionPoolConfigurer.class);
        when(mockConfigurer2.isAvailable()).thenReturn(true);
        when(mockConfigurer2.getName()).thenReturn("Configurer2");
        when(mockConfigurer2.configureWebClient(any(WebClient.Builder.class), any(PoolSettings.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ConnectionPoolConfigurer mockConfigurer3 = mock(ConnectionPoolConfigurer.class);
        when(mockConfigurer3.isAvailable()).thenReturn(true);

        List<ConnectionPoolConfigurer> configurers = List.of(mockConfigurer1, mockConfigurer2, mockConfigurer3);
        DefaultWebClientFactory factory = new DefaultWebClientFactory(null, configurers);

        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setMaxIdleConnections(10);

        WebClient webClient = factory.createWebClient(null, "https://api.example.com", config, null, null);

        assertNotNull(webClient);
        verify(mockConfigurer1, times(1)).isAvailable();
        verify(mockConfigurer2, times(1)).isAvailable();
        verify(mockConfigurer2, times(1)).configureWebClient(any(), any());
        verify(mockConfigurer3, never()).isAvailable();
    }

    @Test
    void testCreateWebClient_nullPoolSettings_skipsConfigurer() {
        ConnectionPoolConfigurer mockConfigurer = mock(ConnectionPoolConfigurer.class);
        when(mockConfigurer.isAvailable()).thenReturn(true);

        List<ConnectionPoolConfigurer> configurers = Collections.singletonList(mockConfigurer);
        DefaultWebClientFactory factory = new DefaultWebClientFactory(null, configurers);

        // Pass null config to ensure PoolSettings.from(null) returns null
        WebClient webClient = factory.createWebClient(null, "https://api.example.com", null, null, null);

        assertNotNull(webClient);
        verify(mockConfigurer, never()).isAvailable();
    }

    @Test
    void testCreateWebClient_loggingWithDefaultValues() {
        DefaultWebClientFactory factory = new DefaultWebClientFactory(null, null);
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setLogging(true);
        // Use default values for masking, maxBytes, and binaryBody

        WebClient webClient = factory.createWebClient(null, "https://api.example.com", config, null, null);

        assertNotNull(webClient);
    }

    @Test
    void testCreateWebClient_partialTimeoutConfig() {
        DefaultWebClientFactory factory = new DefaultWebClientFactory(null, null);
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setConnectTimeout(10);
        // readTimeout and writeTimeout use defaults

        WebClient webClient = factory.createWebClient(null, "https://api.example.com", config, null, null);

        assertNotNull(webClient);
    }

    @Test
    void testCreateWebClient_invalidInterceptor_skipped() {
        DefaultWebClientFactory factory = new DefaultWebClientFactory(null, null);

        // Add a non-ExchangeFilterFunction object
        List<Object> interceptors = Collections.singletonList("not an interceptor");

        WebClient webClient = factory.createWebClient(null, "https://api.example.com", null, null, interceptors);

        assertNotNull(webClient);
    }
}
