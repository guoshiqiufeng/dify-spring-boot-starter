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
package io.github.guoshiqiufeng.dify.client.integration.spring.http;

import io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper;
import io.github.guoshiqiufeng.dify.client.core.web.client.HttpClient;
import io.github.guoshiqiufeng.dify.client.core.web.client.RequestBodyUriSpec;
import io.github.guoshiqiufeng.dify.client.core.web.client.RequestHeadersUriSpec;
import io.github.guoshiqiufeng.dify.client.integration.spring.version.SpringVersionDetector;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import lombok.Getter;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Spring-based HTTP client implementation.
 * Supports Spring 5, 6, and 7 through runtime version detection.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025-12-26
 */
public class SpringHttpClient implements HttpClient {

    private final String baseUrl;
    /**
     * -- GETTER --
     * Get the client configuration.
     *
     * @return client configuration
     */
    @Getter
    private final DifyProperties.ClientConfig clientConfig;
    /**
     * -- GETTER --
     * Get the WebClient instance.
     *
     * @return WebClient
     */
    @Getter
    private final WebClient webClient;
    /**
     * RestClient for Spring 6+, null for Spring 5
     * -- GETTER --
     * Get the RestClient instance (Spring 6+ only).
     *
     * @return RestClient, or null if not available
     */
    @Getter
    private final Object restClient;
    private final JsonMapper jsonMapper;

    /**
     * Constructor with base URL and client configuration.
     *
     * @param baseUrl      the base URL for all requests
     * @param clientConfig the client configuration
     * @param jsonMapper   the JSON mapper
     */
    public SpringHttpClient(String baseUrl, DifyProperties.ClientConfig clientConfig, JsonMapper jsonMapper) {
        this(baseUrl, clientConfig, null, null, jsonMapper);
    }

    /**
     * Constructor with custom WebClient builder.
     *
     * @param baseUrl           the base URL for all requests
     * @param clientConfig      the client configuration
     * @param webClientBuilder  custom WebClient.Builder (optional)
     * @param restClientBuilder custom RestClient.Builder (optional, Spring 6+ only)
     * @param jsonMapper        the JSON mapper
     */
    public SpringHttpClient(String baseUrl, DifyProperties.ClientConfig clientConfig,
                            WebClient.Builder webClientBuilder, Object restClientBuilder, JsonMapper jsonMapper) {
        this.baseUrl = baseUrl;
        this.clientConfig = clientConfig;
        this.jsonMapper = jsonMapper;

        // Create WebClient (available in all Spring versions)
        this.webClient = createWebClient(webClientBuilder);

        // Create RestClient if available (Spring 6+)
        this.restClient = createRestClient(restClientBuilder);
    }

    /**
     * Create WebClient with configuration.
     *
     * @param builder custom builder (optional)
     * @return configured WebClient
     */
    private WebClient createWebClient(WebClient.Builder builder) {
        if (builder == null) {
            builder = WebClient.builder();
        }

        return builder
                .baseUrl(baseUrl)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .build();
    }

    /**
     * Create RestClient if available (Spring 6+).
     *
     * @param builder custom builder (optional)
     * @return configured RestClient, or null if not available
     */
    private Object createRestClient(Object builder) {
        if (!SpringVersionDetector.hasRestClient()) {
            return null;
        }

        try {
            // Use reflection to create RestClient
            Class<?> restClientClass = Class.forName("org.springframework.web.client.RestClient");
            Class<?> builderClass = Class.forName("org.springframework.web.client.RestClient$Builder");

            Object restClientBuilder;
            if (builder != null) {
                restClientBuilder = builder;
            } else {
                // Call RestClient.builder()
                restClientBuilder = restClientClass.getMethod("builder").invoke(null);
            }

            // Call builder.baseUrl(baseUrl)
            restClientBuilder = builderClass.getMethod("baseUrl", String.class)
                    .invoke(restClientBuilder, baseUrl);

            // Call builder.defaultHeader("Content-Type", "application/json")
            restClientBuilder = builderClass.getMethod("defaultHeader", String.class, String[].class)
                    .invoke(restClientBuilder, "Content-Type", new String[]{"application/json"});

            // Call builder.defaultHeader("Accept", "application/json")
            restClientBuilder = builderClass.getMethod("defaultHeader", String.class, String[].class)
                    .invoke(restClientBuilder, "Accept", new String[]{"application/json"});

            // Call builder.build()
            return builderClass.getMethod("build").invoke(restClientBuilder);
        } catch (Exception e) {
            // If reflection fails, log and return null
            return null;
        }
    }

    @Override
    public RequestHeadersUriSpec<?> get() {
        return new io.github.guoshiqiufeng.dify.client.core.http.DefaultRequestHeadersUriSpec(
                new SpringHttpRequestBuilder(this, "GET", jsonMapper));
    }

    @Override
    public RequestBodyUriSpec post() {
        return new io.github.guoshiqiufeng.dify.client.core.http.DefaultRequestBodyUriSpec(
                new SpringHttpRequestBuilder(this, "POST", jsonMapper));
    }

    @Override
    public RequestBodyUriSpec put() {
        return new io.github.guoshiqiufeng.dify.client.core.http.DefaultRequestBodyUriSpec(
                new SpringHttpRequestBuilder(this, "PUT", jsonMapper));
    }

    @Override
    public RequestHeadersUriSpec<?> delete() {
        return new io.github.guoshiqiufeng.dify.client.core.http.DefaultRequestHeadersUriSpec(
                new SpringHttpRequestBuilder(this, "DELETE", jsonMapper));
    }

    @Override
    public RequestBodyUriSpec patch() {
        return new io.github.guoshiqiufeng.dify.client.core.http.DefaultRequestBodyUriSpec(
                new SpringHttpRequestBuilder(this, "PATCH", jsonMapper));
    }

    @Override
    public RequestHeadersUriSpec<?> head() {
        return new io.github.guoshiqiufeng.dify.client.core.http.DefaultRequestHeadersUriSpec(
                new SpringHttpRequestBuilder(this, "HEAD", jsonMapper));
    }

    @Override
    public RequestHeadersUriSpec<?> options() {
        return new io.github.guoshiqiufeng.dify.client.core.http.DefaultRequestHeadersUriSpec(
                new SpringHttpRequestBuilder(this, "OPTIONS", jsonMapper));
    }

    @Override
    public RequestBodyUriSpec method(io.github.guoshiqiufeng.dify.client.core.enums.HttpMethod httpMethod) {
        return new io.github.guoshiqiufeng.dify.client.core.http.DefaultRequestBodyUriSpec(
                new SpringHttpRequestBuilder(this, httpMethod.name(), jsonMapper));
    }

    /**
     * Check if RestClient is available.
     *
     * @return true if RestClient is available
     */
    public boolean hasRestClient() {
        return restClient != null;
    }
}
