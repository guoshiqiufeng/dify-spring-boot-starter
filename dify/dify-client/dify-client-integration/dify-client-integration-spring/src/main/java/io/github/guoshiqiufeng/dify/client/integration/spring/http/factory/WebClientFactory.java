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
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

/**
 * Factory for creating WebClient instances with proper configuration.
 * Separates WebClient creation logic from SpringHttpClient.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/2/8
 */
public interface WebClientFactory {

    /**
     * Create a configured WebClient instance.
     *
     * @param builder        WebClient.Builder (can be null for default)
     * @param baseUrl        base URL for requests
     * @param clientConfig   client configuration
     * @param defaultHeaders default headers to add
     * @param interceptors   list of interceptors
     * @return configured WebClient
     */
    WebClient createWebClient(
            WebClient.Builder builder,
            String baseUrl,
            DifyProperties.ClientConfig clientConfig,
            HttpHeaders defaultHeaders,
            List<Object> interceptors
    );

    /**
     * Create a configured WebClient instance for SSE (Server-Sent Events) requests.
     * This allows custom factories to apply SSE-specific timeout configurations.
     *
     * @param builder        WebClient.Builder (can be null for default)
     * @param baseUrl        base URL for requests
     * @param clientConfig   client configuration
     * @param defaultHeaders default headers to add
     * @param interceptors   list of interceptors
     * @return configured WebClient for SSE requests
     */
    default WebClient createWebClientForSSE(
            WebClient.Builder builder,
            String baseUrl,
            DifyProperties.ClientConfig clientConfig,
            HttpHeaders defaultHeaders,
            List<Object> interceptors
    ) {
        // Default implementation: delegate to regular createWebClient
        // Implementations should override this to apply SSE-specific timeout settings
        return createWebClient(builder, baseUrl, clientConfig, defaultHeaders, interceptors);
    }
}
