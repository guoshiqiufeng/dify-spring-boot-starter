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
package io.github.guoshiqiufeng.dify.client.integration.okhttp.http;

import io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper;
import io.github.guoshiqiufeng.dify.client.core.http.HttpClientFactory;
import io.github.guoshiqiufeng.dify.client.core.web.client.HttpClient;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for JavaHttpClientFactory
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/1/13
 */
@ExtendWith(MockitoExtension.class)
class JavaHttpClientFactoryTest {

    @Mock
    private JsonMapper jsonMapper;

    private JavaHttpClientFactory factory;

    @BeforeEach
    void setUp() {
        factory = new JavaHttpClientFactory(jsonMapper);
    }

    @Test
    void testForInit() {
        new JavaHttpClientFactory(jsonMapper);
        new JavaHttpClientFactory(new OkHttpClient.Builder(), jsonMapper);
        new JavaHttpClientFactory(null, jsonMapper);
    }


    @Test
    void testCreateClientWithBaseUrl() {
        // Arrange
        String baseUrl = "http://example.com";
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();

        // Act
        HttpClient client = factory.createClient(baseUrl, clientConfig);

        // Assert
        assertNotNull(client);
        assertInstanceOf(JavaHttpClient.class, client);
    }

    @Test
    void testDefaultHeaderReturnsNewFactory() {
        // Act
        HttpClientFactory newFactory = factory.defaultHeader("X-Custom-Header", "custom-value");

        // Assert
        assertNotNull(newFactory);
        assertInstanceOf(JavaHttpClientFactory.class, newFactory);
        assertNotSame(factory, newFactory, "Should return a new factory instance");
    }

    @Test
    void testDefaultHeaderCreatesClientWithHeader() {
        // Arrange
        HttpClientFactory factoryWithHeader = factory.defaultHeader("Authorization", "Bearer token");
        String baseUrl = "http://example.com";
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();

        // Act
        HttpClient client = factoryWithHeader.createClient(baseUrl, clientConfig);

        // Assert
        assertNotNull(client);
        assertInstanceOf(JavaHttpClient.class, client);
    }

    @Test
    void testInterceptorReturnsNewFactory() {
        // Arrange
        Interceptor mockInterceptor = mock(Interceptor.class);

        // Act
        HttpClientFactory newFactory = factory.interceptor(mockInterceptor);

        // Assert
        assertNotNull(newFactory);
        assertInstanceOf(JavaHttpClientFactory.class, newFactory);
        assertNotSame(factory, newFactory, "Should return a new factory instance");
    }

    @Test
    void testInterceptorWithInvalidTypeThrowsException() {
        // Arrange
        Object invalidInterceptor = new Object();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> factory.interceptor(invalidInterceptor)
        );
        assertEquals("Interceptor must be an instance of okhttp3.Interceptor", exception.getMessage());
    }

    @Test
    void testInterceptorCreatesClientWithInterceptor() {
        // Arrange
        Interceptor mockInterceptor = mock(Interceptor.class);
        HttpClientFactory factoryWithInterceptor = factory.interceptor(mockInterceptor);
        String baseUrl = "http://example.com";
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();

        // Act
        HttpClient client = factoryWithInterceptor.createClient(baseUrl, clientConfig);

        // Assert
        assertNotNull(client);
        assertInstanceOf(JavaHttpClient.class, client);
    }

    @Test
    void testChainedConfiguration() {
        // Arrange
        String baseUrl = "http://example.com";
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        Interceptor mockInterceptor = mock(Interceptor.class);

        // Act - chain multiple configurations
        HttpClientFactory configuredFactory = factory
                .defaultHeader("X-Header-1", "value1")
                .defaultHeader("X-Header-2", "value2")
                .interceptor(mockInterceptor);

        HttpClient client = configuredFactory.createClient(baseUrl, clientConfig);

        // Assert
        assertNotNull(client);
        assertInstanceOf(JavaHttpClient.class, client);
    }

    @Test
    void testCreateMultipleClients() {
        // Arrange
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();

        // Act - create multiple clients with different base URLs
        HttpClient client1 = factory.createClient("http://api1.example.com", clientConfig);
        HttpClient client2 = factory.createClient("http://api2.example.com", clientConfig);

        // Assert
        assertNotNull(client1);
        assertNotNull(client2);
        assertNotSame(client1, client2, "Should create different client instances");
    }
}
