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
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class JavaHttpClient implements HttpClient {

    @Getter
    private final OkHttpClient okHttpClient;
    @Getter
    private final OkHttpClient sseOkHttpClient;  // Separate client for SSE requests
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
        OkHttpClient[] clients = createOkHttpClients(clientConfig, builder, new HttpHeaders(), new ArrayList<>());
        this.okHttpClient = clients[0];
        this.sseOkHttpClient = clients[1];
    }

    public JavaHttpClient(String baseUrl, DifyProperties.ClientConfig clientConfig, OkHttpClient.Builder builder, JsonMapper jsonMapper, HttpHeaders defaultHeaders) {
        this.baseUrl = baseUrl;
        this.jsonMapper = jsonMapper;
        this.builder = builder;
        this.defaultHeaders = defaultHeaders;
        this.skipNull = clientConfig != null ? clientConfig.getSkipNull() : true;
        OkHttpClient[] clients = createOkHttpClients(clientConfig, builder, defaultHeaders, new ArrayList<>());
        this.okHttpClient = clients[0];
        this.sseOkHttpClient = clients[1];
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
        OkHttpClient[] clients = createOkHttpClients(clientConfig, builder, defaultHeaders, interceptors);
        this.okHttpClient = clients[0];
        this.sseOkHttpClient = clients[1];
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
     * Create OkHttpClient instances with configuration.
     * Creates separate clients for regular and SSE requests if sseReadTimeout differs.
     * Creates a new Builder instance to avoid shared state and race conditions.
     *
     * @param clientConfig   the client configuration
     * @param defaultHeaders the default headers to add to all requests
     * @param interceptors   the list of custom interceptors
     * @return array of [regularClient, sseClient] - both may be the same instance if no SSE timeout configured
     */
    private OkHttpClient[] createOkHttpClients(DifyProperties.ClientConfig clientConfig, OkHttpClient.Builder builder,
                                               HttpHeaders defaultHeaders,
                                               List<Interceptor> interceptors) {
        // Preserve user's custom builder configuration while avoiding shared mutable state
        // If a builder is provided, create a new builder from the existing client to preserve all configurations
        if (builder != null) {
            builder = builder.build().newBuilder();
        } else {
            builder = new OkHttpClient.Builder();
        }

        // Set timeouts from configuration (default 30 seconds)
        int connectTimeout = (clientConfig != null && clientConfig.getConnectTimeout() != null)
                ? clientConfig.getConnectTimeout() : 30;
        int readTimeout = (clientConfig != null && clientConfig.getReadTimeout() != null)
                ? clientConfig.getReadTimeout() : 30;
        int writeTimeout = (clientConfig != null && clientConfig.getWriteTimeout() != null)
                ? clientConfig.getWriteTimeout() : 30;

        // Check if SSE read timeout is configured and differs from regular read timeout
        Integer sseReadTimeout = (clientConfig != null && clientConfig.getSseReadTimeout() != null)
                ? clientConfig.getSseReadTimeout() : null;

        boolean needsSeparateSseClient = false;
        if (sseReadTimeout != null) {
            // Validate: must be >= 0 (0 = no timeout, >0 = timeout in seconds)
            if (sseReadTimeout < 0) {
                // Invalid value, log warning and ignore
                log.warn("【Dify】Invalid sseReadTimeout value: {}. Must be >= 0. Using default readTimeout instead.", sseReadTimeout);
                sseReadTimeout = null;
            } else if (sseReadTimeout != readTimeout) {
                // Valid and different from regular timeout - need separate client
                needsSeparateSseClient = true;
            }
        }

        // Build regular client with readTimeout
        OkHttpClient regularClient = buildOkHttpClient(builder, connectTimeout, readTimeout, writeTimeout,
                clientConfig, defaultHeaders, interceptors);

        // Build SSE client if needed
        OkHttpClient sseClient;
        if (needsSeparateSseClient) {
            // Create a new builder from the regular client to preserve all custom configurations
            // (TLS, proxy, auth, custom interceptors) while applying different timeout
            OkHttpClient.Builder sseBuilder = regularClient.newBuilder();
            // Only override the read timeout for SSE
            sseBuilder.readTimeout(sseReadTimeout, TimeUnit.SECONDS);
            sseClient = sseBuilder.build();
        } else {
            // Use same client for both
            sseClient = regularClient;
        }

        return new OkHttpClient[]{regularClient, sseClient};
    }

    /**
     * Build a single OkHttpClient with the specified timeouts and configuration.
     */
    private OkHttpClient buildOkHttpClient(OkHttpClient.Builder builder, int connectTimeout, int readTimeout,
                                           int writeTimeout, DifyProperties.ClientConfig clientConfig,
                                           HttpHeaders defaultHeaders, List<Interceptor> interceptors) {

        builder.connectTimeout(connectTimeout, TimeUnit.SECONDS);
        builder.readTimeout(readTimeout, TimeUnit.SECONDS);
        builder.writeTimeout(writeTimeout, TimeUnit.SECONDS);

        // Configure connection pool
        if (clientConfig != null) {
            // Validate and apply connection pool settings with boundary checks
            // Strategy: Use default value for invalid parameters (< 1)
            // This matches the Spring implementation for consistency
            int maxIdleConnections = clientConfig.getMaxIdleConnections() != null
                    ? clientConfig.getMaxIdleConnections() : 5;
            if (maxIdleConnections < 1) {
                log.warn("Invalid maxIdleConnections value: {}, using default value 5", maxIdleConnections);
                maxIdleConnections = 5;
            }

            int keepAliveSeconds = clientConfig.getKeepAliveSeconds() != null
                    ? clientConfig.getKeepAliveSeconds() : 300;
            if (keepAliveSeconds < 1) {
                log.warn("Invalid keepAliveSeconds value: {}, using default value 300", keepAliveSeconds);
                keepAliveSeconds = 300;
            }

            okhttp3.ConnectionPool pool = new okhttp3.ConnectionPool(
                    maxIdleConnections, keepAliveSeconds, TimeUnit.SECONDS);
            builder.connectionPool(pool);

            // Configure dispatcher with boundary checks
            int maxRequests = clientConfig.getMaxRequests() != null
                    ? clientConfig.getMaxRequests() : 64;
            if (maxRequests < 1) {
                log.warn("Invalid maxRequests value: {}, using default value 64", maxRequests);
                maxRequests = 64;
            }

            int maxRequestsPerHost = clientConfig.getMaxRequestsPerHost() != null
                    ? clientConfig.getMaxRequestsPerHost() : 5;
            if (maxRequestsPerHost < 1) {
                log.warn("Invalid maxRequestsPerHost value: {}, using default value 5", maxRequestsPerHost);
                maxRequestsPerHost = 5;
            }

            okhttp3.Dispatcher dispatcher = new okhttp3.Dispatcher();
            dispatcher.setMaxRequests(maxRequests);
            dispatcher.setMaxRequestsPerHost(maxRequestsPerHost);
            builder.dispatcher(dispatcher);

            // Configure call timeout if specified
            // Note: callTimeout can be 0 (no limit) or positive value
            Integer callTimeout = clientConfig.getCallTimeout();
            if (callTimeout != null && callTimeout > 0) {
                builder.callTimeout(callTimeout, TimeUnit.SECONDS);
            }
        }

        // Add logging interceptor if enabled
        if (clientConfig != null && clientConfig.getLogging() != null && clientConfig.getLogging()) {
            boolean maskingEnabled = clientConfig.getLoggingMaskEnabled() != null
                    ? clientConfig.getLoggingMaskEnabled() : true;
            Integer logBodyMaxBytes = clientConfig.getLogBodyMaxBytes() != null
                    ? clientConfig.getLogBodyMaxBytes() : 4096;
            Boolean logBinaryBody = clientConfig.getLogBinaryBody() != null
                    ? clientConfig.getLogBinaryBody() : false;
            builder.addInterceptor(new LoggingInterceptor(maskingEnabled, logBodyMaxBytes, logBinaryBody));
        }

        // Add custom interceptors
        if (interceptors != null && !interceptors.isEmpty()) {
            for (Interceptor interceptor : interceptors) {
                builder.addInterceptor(interceptor);
            }
        }

        builder.addInterceptor(chain -> {
            okhttp3.Request.Builder requestBuilder = chain.request().newBuilder();
            requestBuilder.addHeader("Content-Type", "application/json");
            requestBuilder.addHeader("Accept", "application/json");
            return chain.proceed(requestBuilder.build());
        });

        // Add default headers interceptor if configured
        if (defaultHeaders != null && !defaultHeaders.isEmpty()) {
            builder.addInterceptor(chain -> {
                okhttp3.Request.Builder requestBuilder = chain.request().newBuilder();
                defaultHeaders.forEach((key, values) -> {
                    if (!values.isEmpty()) {
                        values.forEach(value -> {
                            if (StrUtil.isNotEmpty(value)) {
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
    public RequestBodyUriSpec method(io.github.guoshiqiufeng.dify.client.core.http.HttpMethod httpMethod) {
        return new io.github.guoshiqiufeng.dify.client.core.http.DefaultRequestBodyUriSpec(
                new io.github.guoshiqiufeng.dify.client.integration.okhttp.http.OkHttpRequestBuilder(this, jsonMapper, httpMethod.name()));
    }
}
