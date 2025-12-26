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

/**
 * Factory for creating HTTP client instances.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025-12-26
 */
public interface HttpClientFactory {

    /**
     * Create an HTTP client with the specified base URL and configuration.
     *
     * @param baseUrl      the base URL for all requests
     * @param clientConfig the client configuration
     * @return a new HTTP client instance
     */
    HttpClient createClient(String baseUrl, DifyProperties.ClientConfig clientConfig);

    /**
     * Create an HTTP client with the specified base URL and default configuration.
     *
     * @param baseUrl the base URL for all requests
     * @return a new HTTP client instance
     */
    default HttpClient createClient(String baseUrl) {
        return createClient(baseUrl, new DifyProperties.ClientConfig());
    }

    /**
     * Add a default header that will be included in all requests made by clients created from this factory.
     * This method returns a new factory instance with the header configured.
     *
     * @param key   the header name
     * @param value the header value
     * @return a new HttpClientFactory instance with the default header configured
     */
    HttpClientFactory defaultHeader(String key, String value);

    /**
     * Add an interceptor that will be applied to all requests made by clients created from this factory.
     * This method returns a new factory instance with the interceptor configured.
     * <p>
     * The interceptor type depends on the underlying HTTP client implementation:
     * <ul>
     *   <li>For OkHttp-based clients: {@code okhttp3.Interceptor}</li>
     *   <li>For Spring WebClient: {@code org.springframework.web.reactive.function.client.ExchangeFilterFunction}</li>
     *   <li>For Spring RestClient: {@code org.springframework.http.client.ClientHttpRequestInterceptor}</li>
     * </ul>
     *
     * @param interceptor the interceptor to add (type depends on implementation)
     * @return a new HttpClientFactory instance with the interceptor configured
     */
    HttpClientFactory interceptor(Object interceptor);
}
