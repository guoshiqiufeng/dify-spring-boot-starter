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
package io.github.guoshiqiufeng.dify.client.integration.okhttp.http;

import io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper;
import io.github.guoshiqiufeng.dify.client.core.web.client.HttpClient;
import io.github.guoshiqiufeng.dify.client.core.web.client.RequestBodyUriSpec;
import io.github.guoshiqiufeng.dify.client.core.web.client.RequestHeadersUriSpec;
import io.github.guoshiqiufeng.dify.client.integration.okhttp.interceptor.LoggingInterceptor;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

/**
 * Java HTTP client implementation using OkHttp.
 * This implementation is framework-agnostic and works with Java 8+.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025-12-26
 */
public class JavaHttpClient implements HttpClient {

    private final OkHttpClient okHttpClient;
    private final String baseUrl;
    private final JsonMapper jsonMapper;

    /**
     * Constructor with base URL and client configuration.
     *
     * @param baseUrl      the base URL for all requests
     * @param clientConfig the client configuration
     * @param jsonMapper   the JSON mapper
     */
    public JavaHttpClient(String baseUrl, DifyProperties.ClientConfig clientConfig, JsonMapper jsonMapper) {
        this.baseUrl = baseUrl;
        this.jsonMapper = jsonMapper;
        this.okHttpClient = createOkHttpClient(clientConfig);
    }

    /**
     * Constructor with base URL and default configuration.
     *
     * @param baseUrl    the base URL for all requests
     * @param jsonMapper the JSON mapper
     */
    public JavaHttpClient(String baseUrl, JsonMapper jsonMapper) {
        this(baseUrl, new DifyProperties.ClientConfig(), jsonMapper);
    }

    /**
     * Create OkHttpClient with configuration.
     *
     * @param clientConfig the client configuration
     * @return configured OkHttpClient
     */
    private OkHttpClient createOkHttpClient(DifyProperties.ClientConfig clientConfig) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        // Set default timeouts (30 seconds)
        builder.connectTimeout(30, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);
        builder.writeTimeout(30, TimeUnit.SECONDS);

        // Add logging interceptor if enabled
        if (clientConfig != null && clientConfig.getLogging() != null && clientConfig.getLogging()) {
            builder.addInterceptor(new LoggingInterceptor());
        }

        return builder.build();
    }

    @Override
    public RequestHeadersUriSpec<?> get() {
        return new io.github.guoshiqiufeng.dify.client.core.http.DefaultRequestHeadersUriSpec(
                new io.github.guoshiqiufeng.dify.client.integration.okhttp.http.OkHttpRequestBuilder(okHttpClient, baseUrl, jsonMapper, "GET"));
    }

    @Override
    public RequestBodyUriSpec post() {
        return new io.github.guoshiqiufeng.dify.client.core.http.DefaultRequestBodyUriSpec(
                new io.github.guoshiqiufeng.dify.client.integration.okhttp.http.OkHttpRequestBuilder(okHttpClient, baseUrl, jsonMapper, "POST"));
    }

    @Override
    public RequestBodyUriSpec put() {
        return new io.github.guoshiqiufeng.dify.client.core.http.DefaultRequestBodyUriSpec(
                new io.github.guoshiqiufeng.dify.client.integration.okhttp.http.OkHttpRequestBuilder(okHttpClient, baseUrl, jsonMapper, "PUT"));
    }

    @Override
    public RequestHeadersUriSpec<?> delete() {
        return new io.github.guoshiqiufeng.dify.client.core.http.DefaultRequestHeadersUriSpec(
                new io.github.guoshiqiufeng.dify.client.integration.okhttp.http.OkHttpRequestBuilder(okHttpClient, baseUrl, jsonMapper, "DELETE"));
    }

    @Override
    public RequestBodyUriSpec patch() {
        return new io.github.guoshiqiufeng.dify.client.core.http.DefaultRequestBodyUriSpec(
                new io.github.guoshiqiufeng.dify.client.integration.okhttp.http.OkHttpRequestBuilder(okHttpClient, baseUrl, jsonMapper, "PATCH"));
    }

    @Override
    public RequestHeadersUriSpec<?> head() {
        return new io.github.guoshiqiufeng.dify.client.core.http.DefaultRequestHeadersUriSpec(
                new io.github.guoshiqiufeng.dify.client.integration.okhttp.http.OkHttpRequestBuilder(okHttpClient, baseUrl, jsonMapper, "HEAD"));
    }

    @Override
    public RequestHeadersUriSpec<?> options() {
        return new io.github.guoshiqiufeng.dify.client.core.http.DefaultRequestHeadersUriSpec(
                new io.github.guoshiqiufeng.dify.client.integration.okhttp.http.OkHttpRequestBuilder(okHttpClient, baseUrl, jsonMapper, "OPTIONS"));
    }

    @Override
    public RequestBodyUriSpec method(io.github.guoshiqiufeng.dify.client.core.enums.HttpMethod httpMethod) {
        return new io.github.guoshiqiufeng.dify.client.core.http.DefaultRequestBodyUriSpec(
                new io.github.guoshiqiufeng.dify.client.integration.okhttp.http.OkHttpRequestBuilder(okHttpClient, baseUrl, jsonMapper, httpMethod.name()));
    }
}
