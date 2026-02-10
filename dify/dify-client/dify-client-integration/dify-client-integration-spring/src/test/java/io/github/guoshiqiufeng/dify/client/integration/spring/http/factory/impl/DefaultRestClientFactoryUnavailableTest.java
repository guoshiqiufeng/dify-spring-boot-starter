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
import io.github.guoshiqiufeng.dify.client.integration.spring.http.pool.RestClientHttpClientFactory;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test coverage for DefaultRestClientFactory when RestClient is unavailable (Spring 5)
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/2/10
 */
class DefaultRestClientFactoryUnavailableTest {

    /**
     * Test subclass that simulates RestClient unavailability
     */
    private static class TestDefaultRestClientFactory extends DefaultRestClientFactory {
        public TestDefaultRestClientFactory(RestClientHttpClientFactory httpClientFactory) {
            super(httpClientFactory, Collections.emptyList());
        }

        @Override
        public boolean isRestClientAvailable() {
            return false;
        }
    }

    @Test
    void testCreateRestClient_returnsNullWhenUnavailable() {
        RestClientHttpClientFactory mockFactory = null;
        TestDefaultRestClientFactory factory = new TestDefaultRestClientFactory(mockFactory);

        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        HttpHeaders headers = new HttpHeaders();

        Object result = factory.createRestClient(null, "https://api.example.com", config, headers, null);

        assertNull(result, "Should return null when RestClient is not available");
    }

    @Test
    void testCreateRestClient_returnsNullWhenUnavailable_withBuilder() {
        RestClientHttpClientFactory mockFactory = null;
        TestDefaultRestClientFactory factory = new TestDefaultRestClientFactory(mockFactory);

        Object builder = new Object(); // Simulated builder
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();

        Object result = factory.createRestClient(builder, "https://api.example.com", config, null, null);

        assertNull(result, "Should return null when RestClient is not available, even with builder");
    }

    @Test
    void testCreateRestClient_returnsNullWhenUnavailable_nullConfig() {
        RestClientHttpClientFactory mockFactory = null;
        TestDefaultRestClientFactory factory = new TestDefaultRestClientFactory(mockFactory);

        Object result = factory.createRestClient(null, "https://api.example.com", null, null, null);

        assertNull(result, "Should return null when RestClient is not available");
    }

    @Test
    void testCreateRestClient_returnsNullWhenUnavailable_withAllParameters() {
        RestClientHttpClientFactory mockFactory = null;
        TestDefaultRestClientFactory factory = new TestDefaultRestClientFactory(mockFactory);

        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setMaxIdleConnections(10);
        config.setLogging(true);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Custom-Header", "value");

        Object result = factory.createRestClient(
                null,
                "https://api.example.com",
                config,
                headers,
                Collections.emptyList()
        );

        assertNull(result, "Should return null when RestClient is not available, regardless of parameters");
    }
}
