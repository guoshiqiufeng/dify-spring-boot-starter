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
package io.github.guoshiqiufeng.dify.client.core.http;

import io.github.guoshiqiufeng.dify.client.core.web.client.HttpClient;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for HttpClientFactory default methods
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/01/16
 */
class HttpClientFactoryTest {

    private HttpClientFactory factory;

    @BeforeEach
    void setUp() {
        // Create a mock that delegates to real default methods
        factory = mock(HttpClientFactory.class, CALLS_REAL_METHODS);
    }

    @Test
    void testCreateClientWithBaseUrlOnly() {
        String baseUrl = "https://api.example.com";
        HttpClient mockClient = mock(HttpClient.class);

        when(factory.createClient(eq(baseUrl), any(DifyProperties.ClientConfig.class)))
            .thenReturn(mockClient);

        HttpClient result = factory.createClient(baseUrl);

        assertNotNull(result);
        assertSame(mockClient, result);
        verify(factory).createClient(eq(baseUrl), any(DifyProperties.ClientConfig.class));
    }

    @Test
    void testCreateClientWithBaseUrlCreatesDefaultConfig() {
        String baseUrl = "https://api.example.com";
        HttpClient mockClient = mock(HttpClient.class);

        when(factory.createClient(eq(baseUrl), any(DifyProperties.ClientConfig.class)))
            .thenReturn(mockClient);

        factory.createClient(baseUrl);

        // Verify that a ClientConfig was created and passed
        verify(factory).createClient(eq(baseUrl), argThat(config ->
            config != null && config instanceof DifyProperties.ClientConfig
        ));
    }

    @Test
    void testCreateClientWithDifferentBaseUrls() {
        HttpClient mockClient1 = mock(HttpClient.class);
        HttpClient mockClient2 = mock(HttpClient.class);

        when(factory.createClient(eq("https://api1.example.com"), any(DifyProperties.ClientConfig.class)))
            .thenReturn(mockClient1);
        when(factory.createClient(eq("https://api2.example.com"), any(DifyProperties.ClientConfig.class)))
            .thenReturn(mockClient2);

        HttpClient result1 = factory.createClient("https://api1.example.com");
        HttpClient result2 = factory.createClient("https://api2.example.com");

        assertNotNull(result1);
        assertNotNull(result2);
        assertNotSame(result1, result2);
    }

    @Test
    void testCreateClientWithLocalhost() {
        String baseUrl = "http://localhost:8080";
        HttpClient mockClient = mock(HttpClient.class);

        when(factory.createClient(eq(baseUrl), any(DifyProperties.ClientConfig.class)))
            .thenReturn(mockClient);

        HttpClient result = factory.createClient(baseUrl);

        assertNotNull(result);
        verify(factory).createClient(eq(baseUrl), any(DifyProperties.ClientConfig.class));
    }
}
