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
package io.github.guoshiqiufeng.dify.support.impl.builder;

import io.github.guoshiqiufeng.dify.client.core.web.client.HttpClient;
import io.github.guoshiqiufeng.dify.client.core.http.HttpClientFactory;
import io.github.guoshiqiufeng.dify.core.client.BaseDifyClient;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;

/**
 * Base builder class with common builder functionality for framework-agnostic clients
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025/12/26
 */
public abstract class BaseDifyBuilder<T extends BaseDifyBuilder<T>> {

    protected String baseUrl;
    protected DifyProperties.ClientConfig clientConfig;
    protected HttpClientFactory httpClientFactory;

    /**
     * Set the base URL for the Dify API
     *
     * @param baseUrl the base URL
     * @return the builder
     */
    @SuppressWarnings("unchecked")
    public T baseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return (T) this;
    }

    /**
     * Set the client configuration
     *
     * @param clientConfig the client configuration
     * @return the builder
     */
    @SuppressWarnings("unchecked")
    public T clientConfig(DifyProperties.ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
        return (T) this;
    }

    /**
     * Set the HTTP client factory
     *
     * @param httpClientFactory the HTTP client factory
     * @return the builder
     */
    @SuppressWarnings("unchecked")
    public T httpClientFactory(HttpClientFactory httpClientFactory) {
        this.httpClientFactory = httpClientFactory;
        return (T) this;
    }

    /**
     * Initialize default values for common properties
     */
    protected void initDefaults() {
        if (baseUrl == null) {
            baseUrl = BaseDifyClient.DEFAULT_BASE_URL;
        }
        if (clientConfig == null) {
            clientConfig = new DifyProperties.ClientConfig();
        }
    }

    /**
     * Create HTTP client using the configured factory or throw exception if not set
     *
     * @return the HTTP client
     * @throws IllegalStateException if httpClientFactory is not set
     */
    protected HttpClient createHttpClient() {
        if (httpClientFactory == null) {
            throw new IllegalStateException("HttpClientFactory must be set before building the client");
        }
        return httpClientFactory.createClient(baseUrl, clientConfig);
    }
}
