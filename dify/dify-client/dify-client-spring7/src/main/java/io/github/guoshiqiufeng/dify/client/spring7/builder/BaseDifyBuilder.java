/*
 * Copyright (c) 2025-2025, fubluesky (fubluesky@foxmail.com)
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
package io.github.guoshiqiufeng.dify.client.spring7.builder;

import io.github.guoshiqiufeng.dify.core.client.BaseDifyClient;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Base builder class with common builder functionality
 *
 * @author yanghq
 * @version 0.9.0
 * @since 2025/4/18 10:20
 */
public abstract class BaseDifyBuilder<T extends BaseDifyBuilder<T>> {

    protected String baseUrl;
    protected DifyProperties.ClientConfig clientConfig;
    protected RestClient.Builder restClientBuilder;
    protected WebClient.Builder webClientBuilder;

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
     * Set the RestClient builder
     *
     * @param restClientBuilder the RestClient builder
     * @return the builder
     */
    @SuppressWarnings("unchecked")
    public T restClientBuilder(RestClient.Builder restClientBuilder) {
        this.restClientBuilder = restClientBuilder;
        return (T) this;
    }

    /**
     * Set the WebClient builder
     *
     * @param webClientBuilder the WebClient builder
     * @return the builder
     */
    @SuppressWarnings("unchecked")
    public T webClientBuilder(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
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
        if (restClientBuilder == null) {
            restClientBuilder = RestClient.builder();
        }
        if (webClientBuilder == null) {
            webClientBuilder = WebClient.builder();
        }
    }
}
