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
package io.github.guoshiqiufeng.dify.client.integration.spring.http;

import io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper;
import io.github.guoshiqiufeng.dify.client.core.http.HttpHeaders;
import io.github.guoshiqiufeng.dify.client.core.web.client.HttpClient;
import io.github.guoshiqiufeng.dify.client.core.web.client.RequestBodyUriSpec;
import io.github.guoshiqiufeng.dify.client.core.web.client.RequestHeadersUriSpec;
import io.github.guoshiqiufeng.dify.client.integration.spring.logging.DifyLoggingControl;
import io.github.guoshiqiufeng.dify.client.integration.spring.logging.DifyLoggingFilter;
import io.github.guoshiqiufeng.dify.client.integration.spring.logging.DifyRestLoggingInterceptor;
import io.github.guoshiqiufeng.dify.client.integration.spring.version.SpringVersionDetector;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.core.exception.DiftClientExceptionEnum;
import io.github.guoshiqiufeng.dify.core.exception.DifyClientException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Spring-based HTTP client implementation.
 * Supports Spring 5, 6, and 7 through runtime version detection.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025-12-26
 */
@Slf4j
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
    private final HttpHeaders defaultHeaders;
    private final List<Object> interceptors;

    /**
     * Constructor with base URL and client configuration.
     *
     * @param baseUrl      the base URL for all requests
     * @param clientConfig the client configuration
     * @param jsonMapper   the JSON mapper
     */
    public SpringHttpClient(String baseUrl, DifyProperties.ClientConfig clientConfig, JsonMapper jsonMapper) {
        this(baseUrl, clientConfig, null, null, jsonMapper, new HttpHeaders(), new ArrayList<>());
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
        this(baseUrl, clientConfig, webClientBuilder, restClientBuilder, jsonMapper, new HttpHeaders(), new ArrayList<>());
    }

    /**
     * Constructor with custom WebClient builder and default headers.
     *
     * @param baseUrl           the base URL for all requests
     * @param clientConfig      the client configuration
     * @param webClientBuilder  custom WebClient.Builder (optional)
     * @param restClientBuilder custom RestClient.Builder (optional, Spring 6+ only)
     * @param jsonMapper        the JSON mapper
     * @param defaultHeaders    default headers to add to all requests
     */
    public SpringHttpClient(String baseUrl, DifyProperties.ClientConfig clientConfig,
                            WebClient.Builder webClientBuilder, Object restClientBuilder,
                            JsonMapper jsonMapper, HttpHeaders defaultHeaders) {
        this(baseUrl, clientConfig, webClientBuilder, restClientBuilder, jsonMapper, defaultHeaders, new ArrayList<>());
    }

    /**
     * Constructor with custom WebClient builder, default headers, and interceptors.
     *
     * @param baseUrl           the base URL for all requests
     * @param clientConfig      the client configuration
     * @param webClientBuilder  custom WebClient.Builder (optional)
     * @param restClientBuilder custom RestClient.Builder (optional, Spring 6+ only)
     * @param jsonMapper        the JSON mapper
     * @param defaultHeaders    default headers to add to all requests
     * @param interceptors      list of interceptors (ExchangeFilterFunction for WebClient, ClientHttpRequestInterceptor for RestClient)
     */
    public SpringHttpClient(String baseUrl, DifyProperties.ClientConfig clientConfig,
                            WebClient.Builder webClientBuilder, Object restClientBuilder,
                            JsonMapper jsonMapper, HttpHeaders defaultHeaders, List<Object> interceptors) {
        this.baseUrl = baseUrl;
        this.clientConfig = clientConfig;
        this.jsonMapper = jsonMapper;
        this.defaultHeaders = defaultHeaders != null ? defaultHeaders : new HttpHeaders();
        this.interceptors = interceptors != null ? interceptors : new ArrayList<>();

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

        // Configure timeouts using reactor-netty HttpClient
        try {
            Class<?> httpClientClass = Class.forName("reactor.netty.http.client.HttpClient");
            Object httpClient = httpClientClass.getMethod("create").invoke(null);

            // Get timeout values from configuration (default 30 seconds)
            int connectTimeout = (clientConfig != null && clientConfig.getConnectTimeout() != null)
                    ? clientConfig.getConnectTimeout() : 30;
            int readTimeout = (clientConfig != null && clientConfig.getReadTimeout() != null)
                    ? clientConfig.getReadTimeout() : 30;
            int writeTimeout = (clientConfig != null && clientConfig.getWriteTimeout() != null)
                    ? clientConfig.getWriteTimeout() : 30;

            // Set connection timeout
            Class<?> durationClass = Class.forName("java.time.Duration");
            Object connectDuration = durationClass.getMethod("ofSeconds", long.class).invoke(null, (long) connectTimeout);
            httpClient = httpClientClass.getMethod("option", Class.forName("io.netty.channel.ChannelOption"), Object.class)
                    .invoke(httpClient,
                            Class.forName("io.netty.channel.ChannelOption").getField("CONNECT_TIMEOUT_MILLIS").get(null),
                            connectTimeout * 1000);

            // Set read/write timeout
            Object readWriteDuration = durationClass.getMethod("ofSeconds", long.class).invoke(null, (long) Math.max(readTimeout, writeTimeout));
            httpClient = httpClientClass.getMethod("responseTimeout", durationClass).invoke(httpClient, readWriteDuration);

            // Apply HttpClient to WebClient
            Class<?> reactorClientHttpConnectorClass = Class.forName("org.springframework.http.client.reactive.ReactorClientHttpConnector");
            Object connector = reactorClientHttpConnectorClass.getConstructor(httpClientClass).newInstance(httpClient);
            builder = builder.clientConnector((org.springframework.http.client.reactive.ClientHttpConnector) connector);
        } catch (Exception e) {
            // If reactor-netty is not available or configuration fails, continue without timeout configuration
            // The default WebClient will be used
        }

        builder = builder.baseUrl(baseUrl)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json");

        // Add default headers if configured
        if (defaultHeaders != null && !defaultHeaders.isEmpty()) {
            WebClient.Builder finalBuilder = builder;
            defaultHeaders.forEach((key, values) -> {
                finalBuilder.defaultHeader(key, String.valueOf(values));
            });
        }
        if (clientConfig != null && clientConfig.getLogging()) {
            DifyLoggingControl loggingControl = DifyLoggingControl.getInstance();
            DifyLoggingFilter filter = loggingControl.getAndMarkFilter();
            if (filter != null) {
                builder.filter(filter);
            }
        }
        // Add interceptors (ExchangeFilterFunction for WebClient)
        if (interceptors != null && !interceptors.isEmpty()) {
            for (Object interceptor : interceptors) {
                try {
                    // Check if it's an ExchangeFilterFunction
                    Class<?> exchangeFilterFunctionClass = Class.forName("org.springframework.web.reactive.function.client.ExchangeFilterFunction");
                    if (exchangeFilterFunctionClass.isInstance(interceptor)) {
                        builder = builder.filter((org.springframework.web.reactive.function.client.ExchangeFilterFunction) interceptor);
                    }
                } catch (ClassNotFoundException e) {
                    // Ignore if ExchangeFilterFunction is not available
                }
            }
        }

        return builder.build();
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

            // Configure timeouts using ClientHttpRequestFactory
            try {
                // Get timeout values from configuration (default 30 seconds)
                int connectTimeout = (clientConfig != null && clientConfig.getConnectTimeout() != null)
                        ? clientConfig.getConnectTimeout() : 30;
                int readTimeout = (clientConfig != null && clientConfig.getReadTimeout() != null)
                        ? clientConfig.getReadTimeout() : 30;

                // Try to use JdkClientHttpRequestFactory (Spring 6.1+) which properly handles error response bodies
                try {
                    Class<?> jdkFactoryClass = Class.forName("org.springframework.http.client.JdkClientHttpRequestFactory");
                    Object factory = jdkFactoryClass.getDeclaredConstructor().newInstance();

                    // Set timeouts
                    jdkFactoryClass.getMethod("setConnectTimeout", int.class).invoke(factory, connectTimeout * 1000);
                    jdkFactoryClass.getMethod("setReadTimeout", int.class).invoke(factory, readTimeout * 1000);

                    // Set the request factory
                    Class<?> clientHttpRequestFactoryClass = Class.forName("org.springframework.http.client.ClientHttpRequestFactory");
                    restClientBuilder = builderClass.getMethod("requestFactory", clientHttpRequestFactoryClass)
                            .invoke(restClientBuilder, factory);
                } catch (ClassNotFoundException e) {
                    // JdkClientHttpRequestFactory not available, fall back to SimpleClientHttpRequestFactory
                    // Note: SimpleClientHttpRequestFactory may not properly read error response bodies
                    Class<?> factoryClass = Class.forName("org.springframework.http.client.SimpleClientHttpRequestFactory");
                    Object factory = factoryClass.getDeclaredConstructor().newInstance();
                    factoryClass.getMethod("setConnectTimeout", int.class).invoke(factory, connectTimeout * 1000);
                    factoryClass.getMethod("setReadTimeout", int.class).invoke(factory, readTimeout * 1000);

                    // Set the request factory
                    Class<?> clientHttpRequestFactoryClass = Class.forName("org.springframework.http.client.ClientHttpRequestFactory");
                    restClientBuilder = builderClass.getMethod("requestFactory", clientHttpRequestFactoryClass)
                            .invoke(restClientBuilder, factory);
                }
            } catch (Exception e) {
                // If timeout configuration fails, continue without it
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

            // Add default headers if configured
            if (defaultHeaders != null && !defaultHeaders.isEmpty()) {
                for (Map.Entry<String, List<String>> entry : defaultHeaders.entrySet()) {
                    String key = entry.getKey();
                    Object values = entry.getValue();
                    restClientBuilder = builderClass.getMethod("defaultHeader", String.class, String[].class)
                            .invoke(restClientBuilder, key, new String[]{String.valueOf(values)});
                }
            }

            if (clientConfig != null && clientConfig.getLogging()) {
                DifyLoggingControl loggingControl = DifyLoggingControl.getInstance();

                DifyRestLoggingInterceptor interceptor = loggingControl.getAndMarkInterceptor();
                if (interceptor != null) {
                    try {
                        // Check if it's a ClientHttpRequestInterceptor
                        Class<?> clientHttpRequestInterceptorClass = Class.forName("org.springframework.http.client.ClientHttpRequestInterceptor");
                        if (clientHttpRequestInterceptorClass.isInstance(interceptor)) {
                            restClientBuilder = builderClass.getMethod("requestInterceptor", clientHttpRequestInterceptorClass)
                                    .invoke(restClientBuilder, interceptor);
                        }
                    } catch (ClassNotFoundException e) {
                        // Ignore if ClientHttpRequestInterceptor is not available
                    }
                }

            }

            // Add interceptors (ClientHttpRequestInterceptor for RestClient)
            if (interceptors != null && !interceptors.isEmpty()) {
                for (Object interceptor : interceptors) {
                    try {
                        // Check if it's a ClientHttpRequestInterceptor
                        Class<?> clientHttpRequestInterceptorClass = Class.forName("org.springframework.http.client.ClientHttpRequestInterceptor");
                        if (clientHttpRequestInterceptorClass.isInstance(interceptor)) {
                            restClientBuilder = builderClass.getMethod("requestInterceptor", clientHttpRequestInterceptorClass)
                                    .invoke(restClientBuilder, interceptor);
                        }
                    } catch (ClassNotFoundException e) {
                        // Ignore if ClientHttpRequestInterceptor is not available
                    }
                }
            }

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
                new SpringHttpRequestBuilder(this, "GET", jsonMapper, defaultHeaders));
    }

    @Override
    public RequestBodyUriSpec post() {
        return new io.github.guoshiqiufeng.dify.client.core.http.DefaultRequestBodyUriSpec(
                new SpringHttpRequestBuilder(this, "POST", jsonMapper, defaultHeaders));
    }

    @Override
    public RequestBodyUriSpec put() {
        return new io.github.guoshiqiufeng.dify.client.core.http.DefaultRequestBodyUriSpec(
                new SpringHttpRequestBuilder(this, "PUT", jsonMapper, defaultHeaders));
    }

    @Override
    public RequestHeadersUriSpec<?> delete() {
        return new io.github.guoshiqiufeng.dify.client.core.http.DefaultRequestHeadersUriSpec(
                new SpringHttpRequestBuilder(this, "DELETE", jsonMapper, defaultHeaders));
    }

    @Override
    public RequestBodyUriSpec patch() {
        return new io.github.guoshiqiufeng.dify.client.core.http.DefaultRequestBodyUriSpec(
                new SpringHttpRequestBuilder(this, "PATCH", jsonMapper, defaultHeaders));
    }

    @Override
    public RequestHeadersUriSpec<?> head() {
        return new io.github.guoshiqiufeng.dify.client.core.http.DefaultRequestHeadersUriSpec(
                new SpringHttpRequestBuilder(this, "HEAD", jsonMapper, defaultHeaders));
    }

    @Override
    public RequestHeadersUriSpec<?> options() {
        return new io.github.guoshiqiufeng.dify.client.core.http.DefaultRequestHeadersUriSpec(
                new SpringHttpRequestBuilder(this, "OPTIONS", jsonMapper, defaultHeaders));
    }

    @Override
    public RequestBodyUriSpec method(io.github.guoshiqiufeng.dify.client.core.enums.HttpMethod httpMethod) {
        return new io.github.guoshiqiufeng.dify.client.core.http.DefaultRequestBodyUriSpec(
                new SpringHttpRequestBuilder(this, httpMethod.name(), jsonMapper, defaultHeaders));
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
