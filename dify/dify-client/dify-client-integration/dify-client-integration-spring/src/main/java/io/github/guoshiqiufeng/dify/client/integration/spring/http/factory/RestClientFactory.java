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

import java.util.List;

/**
 * Factory for creating RestClient instances (Spring 6+ only).
 * Separates RestClient creation logic from SpringHttpClient.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/2/8
 */
public interface RestClientFactory {

    /**
     * Create a configured RestClient instance.
     *
     * @param builder        RestClient.Builder (can be null for default)
     * @param baseUrl        base URL for requests
     * @param clientConfig   client configuration
     * @param defaultHeaders default headers to add
     * @param interceptors   list of interceptors
     * @return configured RestClient (as Object for compatibility)
     */
    Object createRestClient(
            Object builder,
            String baseUrl,
            DifyProperties.ClientConfig clientConfig,
            HttpHeaders defaultHeaders,
            List<Object> interceptors
    );

    /**
     * Check if RestClient is available in the current Spring version.
     *
     * @return true if RestClient is available (Spring 6+)
     */
    boolean isRestClientAvailable();
}
