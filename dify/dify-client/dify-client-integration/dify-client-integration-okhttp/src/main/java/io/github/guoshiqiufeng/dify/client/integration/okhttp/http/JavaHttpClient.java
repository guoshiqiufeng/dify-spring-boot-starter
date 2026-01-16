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
package io.github.guoshiqiufeng.dify.client.integration.okhttp.http;

import io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper;
import io.github.guoshiqiufeng.dify.client.core.http.HttpHeaders;
import io.github.guoshiqiufeng.dify.client.core.web.client.HttpClient;
import io.github.guoshiqiufeng.dify.client.core.web.client.RequestBodyUriSpec;
import io.github.guoshiqiufeng.dify.client.core.web.client.RequestHeadersUriSpec;
import io.github.guoshiqiufeng.dify.client.integration.okhttp.logging.LoggingInterceptor;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.core.utils.StrUtil;
import lombok.Getter;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

import java.util.ArrayList;
import java.util.List;
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

    @Getter
    private final OkHttpClient okHttpClient;
    @Getter
    private final String baseUrl;
    private final HttpHeaders defaultHeaders;
    private final OkHttpClient.Builder builder;
    private final JsonMapper jsonMapper;
    @Getter
    private final Boolean skipNull;

    /**
     * Constructor with base URL and client configuration.
     *
     * @param baseUrl      the base URL for all requests
     * @param clientConfig the client configuration
     * @param jsonMapper   the JSON mapper
     */
    public JavaHttpClient(String baseUrl, DifyProperties.ClientConfig clientConfig, OkHttpClient.Builder builder, JsonMapper jsonMapper) {
        this.baseUrl = baseUrl;
        this.jsonMapper = jsonMapper;
        this.builder = builder;
        this.defaultHeaders = new HttpHeaders();
        this.skipNull = clientConfig != null ? clientConfig.getSkipNull() : true;
        this.okHttpClient = createOkHttpClient(clientConfig, builder, new HttpHeaders(), new ArrayList<>());
    }

    public JavaHttpClient(String baseUrl, DifyProperties.ClientConfig clientConfig, OkHttpClient.Builder builder, JsonMapper jsonMapper, HttpHeaders defaultHeaders) {
        this.baseUrl = baseUrl;
        this.jsonMapper = jsonMapper;
        this.builder = builder;
        this.defaultHeaders = defaultHeaders;
        this.skipNull = clientConfig != null ? clientConfig.getSkipNull() : true;
        this.okHttpClient = createOkHttpClient(clientConfig, builder, defaultHeaders, new ArrayList<>());
    }

    /**
     * Constructor with base URL, client configuration, default headers, and interceptors.
     *
     * @param baseUrl        the base URL for all requests
     * @param clientConfig   the client configuration
     * @param jsonMapper     the JSON mapper
     * @param defaultHeaders the default headers to add to all requests
     * @param interceptors   the list of custom interceptors
     */
    public JavaHttpClient(String baseUrl, DifyProperties.ClientConfig clientConfig, OkHttpClient.Builder builder, JsonMapper jsonMapper,
                          HttpHeaders defaultHeaders, List<Interceptor> interceptors) {
        this.baseUrl = baseUrl;
        this.jsonMapper = jsonMapper;
        this.builder = builder;
        this.defaultHeaders = defaultHeaders;
        this.skipNull = clientConfig != null ? clientConfig.getSkipNull() : true;
        this.okHttpClient = createOkHttpClient(clientConfig, builder, defaultHeaders, interceptors);
    }

    /**
     * Constructor with base URL and default configuration.
     *
     * @param baseUrl    the base URL for all requests
     * @param jsonMapper the JSON mapper
     */
    public JavaHttpClient(String baseUrl, JsonMapper jsonMapper) {
        this(baseUrl, new DifyProperties.ClientConfig(), new OkHttpClient.Builder(), jsonMapper);
    }

    public JavaHttpClient(String baseUrl, DifyProperties.ClientConfig clientConfig, JsonMapper jsonMapper) {
        this(baseUrl, clientConfig, new OkHttpClient.Builder(), jsonMapper);
    }

    public JavaHttpClient(String baseUrl, DifyProperties.ClientConfig clientConfig, JsonMapper jsonMapper,
                          HttpHeaders defaultHeaders, List<Interceptor> interceptors) {
        this(baseUrl, clientConfig, new OkHttpClient.Builder(), jsonMapper, defaultHeaders, interceptors);
    }

    /**
     * Create OkHttpClient with configuration.
     *
     * @param clientConfig   the client configuration
     * @param defaultHeaders the default headers to add to all requests
     * @param interceptors   the list of custom interceptors
     * @return configured OkHttpClient
     */
    private OkHttpClient createOkHttpClient(DifyProperties.ClientConfig clientConfig, OkHttpClient.Builder builder,
                                            HttpHeaders defaultHeaders,
                                            List<Interceptor> interceptors) {
        if (builder == null) {
            builder = new OkHttpClient.Builder();
        }

        // Set timeouts from configuration (default 30 seconds)
        int connectTimeout = (clientConfig != null && clientConfig.getConnectTimeout() != null)
                ? clientConfig.getConnectTimeout() : 30;
        int readTimeout = (clientConfig != null && clientConfig.getReadTimeout() != null)
                ? clientConfig.getReadTimeout() : 30;
        int writeTimeout = (clientConfig != null && clientConfig.getWriteTimeout() != null)
                ? clientConfig.getWriteTimeout() : 30;

        builder.connectTimeout(connectTimeout, TimeUnit.SECONDS);
        builder.readTimeout(readTimeout, TimeUnit.SECONDS);
        builder.writeTimeout(writeTimeout, TimeUnit.SECONDS);

        // Add logging interceptor if enabled
        if (clientConfig != null && clientConfig.getLogging() != null && clientConfig.getLogging()) {
            builder.addInterceptor(new LoggingInterceptor());
        }

        // Add custom interceptors
        if (interceptors != null && !interceptors.isEmpty()) {
            for (Interceptor interceptor : interceptors) {
                builder.addInterceptor(interceptor);
            }
        }

        // Add default headers interceptor if configured
        if (defaultHeaders != null && !defaultHeaders.isEmpty()) {
            builder.addInterceptor(chain -> {
                okhttp3.Request.Builder requestBuilder = chain.request().newBuilder();
                defaultHeaders.forEach((key, values) -> {
                    if (!values.isEmpty()) {
                        values.forEach(value -> {
                            if(StrUtil.isNotEmpty(value)) {
                                requestBuilder.addHeader(key, value);
                            }
                        });
                    }
                });
                return chain.proceed(requestBuilder.build());
            });
        }

        return builder.build();
    }

    @Override
    public RequestHeadersUriSpec<?> get() {
        return new io.github.guoshiqiufeng.dify.client.core.http.DefaultRequestHeadersUriSpec(
                new io.github.guoshiqiufeng.dify.client.integration.okhttp.http.OkHttpRequestBuilder(this, jsonMapper, "GET"));
    }

    @Override
    public RequestBodyUriSpec post() {
        return new io.github.guoshiqiufeng.dify.client.core.http.DefaultRequestBodyUriSpec(
                new io.github.guoshiqiufeng.dify.client.integration.okhttp.http.OkHttpRequestBuilder(this, jsonMapper, "POST"));
    }

    @Override
    public RequestBodyUriSpec put() {
        return new io.github.guoshiqiufeng.dify.client.core.http.DefaultRequestBodyUriSpec(
                new io.github.guoshiqiufeng.dify.client.integration.okhttp.http.OkHttpRequestBuilder(this, jsonMapper, "PUT"));
    }

    @Override
    public RequestHeadersUriSpec<?> delete() {
        return new io.github.guoshiqiufeng.dify.client.core.http.DefaultRequestHeadersUriSpec(
                new io.github.guoshiqiufeng.dify.client.integration.okhttp.http.OkHttpRequestBuilder(this, jsonMapper, "DELETE"));
    }

    @Override
    public RequestBodyUriSpec patch() {
        return new io.github.guoshiqiufeng.dify.client.core.http.DefaultRequestBodyUriSpec(
                new io.github.guoshiqiufeng.dify.client.integration.okhttp.http.OkHttpRequestBuilder(this, jsonMapper, "PATCH"));
    }

    @Override
    public RequestHeadersUriSpec<?> head() {
        return new io.github.guoshiqiufeng.dify.client.core.http.DefaultRequestHeadersUriSpec(
                new io.github.guoshiqiufeng.dify.client.integration.okhttp.http.OkHttpRequestBuilder(this, jsonMapper, "HEAD"));
    }

    @Override
    public RequestHeadersUriSpec<?> options() {
        return new io.github.guoshiqiufeng.dify.client.core.http.DefaultRequestHeadersUriSpec(
                new io.github.guoshiqiufeng.dify.client.integration.okhttp.http.OkHttpRequestBuilder(this, jsonMapper, "OPTIONS"));
    }

    @Override
    public RequestBodyUriSpec method(io.github.guoshiqiufeng.dify.client.core.enums.HttpMethod httpMethod) {
        return new io.github.guoshiqiufeng.dify.client.core.http.DefaultRequestBodyUriSpec(
                new io.github.guoshiqiufeng.dify.client.integration.okhttp.http.OkHttpRequestBuilder(this, jsonMapper, httpMethod.name()));
    }
}
