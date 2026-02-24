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
package io.github.guoshiqiufeng.dify.client.integration.spring.http.factory;

import io.github.guoshiqiufeng.dify.client.core.http.HttpHeaders;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test coverage for WebClientFactory default method
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/2/10
 */
class WebClientFactoryTest {

    @Test
    void testCreateWebClientForSSE_delegatesToCreateWebClient() {
        AtomicInteger createWebClientCallCount = new AtomicInteger(0);

        WebClientFactory factory = new WebClientFactory() {
            @Override
            public WebClient createWebClient(WebClient.Builder builder, String baseUrl,
                                            DifyProperties.ClientConfig clientConfig,
                                            HttpHeaders defaultHeaders, List<Object> interceptors) {
                createWebClientCallCount.incrementAndGet();
                return WebClient.builder().baseUrl(baseUrl).build();
            }
        };

        String baseUrl = "https://api.example.com";
        WebClient webClient = factory.createWebClientForSSE(null, baseUrl, null, null, null);

        assertNotNull(webClient);
        assertEquals(1, createWebClientCallCount.get(), "createWebClient should be called once");
    }

    @Test
    void testCreateWebClientForSSE_canBeOverridden() {
        WebClientFactory factory = new WebClientFactory() {
            @Override
            public WebClient createWebClient(WebClient.Builder builder, String baseUrl,
                                            DifyProperties.ClientConfig clientConfig,
                                            HttpHeaders defaultHeaders, List<Object> interceptors) {
                return WebClient.builder().baseUrl(baseUrl).build();
            }

            @Override
            public WebClient createWebClientForSSE(WebClient.Builder builder, String baseUrl,
                                                  DifyProperties.ClientConfig clientConfig,
                                                  HttpHeaders defaultHeaders, List<Object> interceptors) {
                // Custom SSE implementation
                return WebClient.builder().baseUrl(baseUrl + "/sse").build();
            }
        };

        String baseUrl = "https://api.example.com";
        WebClient sseClient = factory.createWebClientForSSE(null, baseUrl, null, null, null);

        assertNotNull(sseClient);
    }

    @Test
    void testCreateWebClientForSSE_passesParametersCorrectly() {
        WebClientFactory factory = new WebClientFactory() {
            @Override
            public WebClient createWebClient(WebClient.Builder builder, String baseUrl,
                                            DifyProperties.ClientConfig clientConfig,
                                            HttpHeaders defaultHeaders, List<Object> interceptors) {
                assertNull(builder, "Builder should be null");
                assertEquals("https://api.example.com", baseUrl, "Base URL should match");
                assertNotNull(clientConfig, "Config should not be null");
                assertNotNull(defaultHeaders, "Headers should not be null");
                assertNotNull(interceptors, "Interceptors should not be null");
                return WebClient.builder().baseUrl(baseUrl).build();
            }
        };

        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        HttpHeaders headers = new HttpHeaders();
        List<Object> interceptors = List.of();

        WebClient webClient = factory.createWebClientForSSE(null, "https://api.example.com",
                config, headers, interceptors);

        assertNotNull(webClient);
    }
}
