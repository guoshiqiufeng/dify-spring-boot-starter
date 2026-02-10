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
import io.github.guoshiqiufeng.dify.client.integration.spring.http.factory.RestClientFactory;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.factory.WebClientFactory;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.pool.PoolSettings;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.pool.RestClientHttpClientFactory;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.pool.WebClientConnectionProviderFactory;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.util.RestClientConfigurer;
import io.github.guoshiqiufeng.dify.client.integration.spring.logging.DifyLoggingControl;
import io.github.guoshiqiufeng.dify.client.integration.spring.logging.DifyLoggingFilter;
import io.github.guoshiqiufeng.dify.client.integration.spring.logging.DifyRestLoggingInterceptor;
import io.github.guoshiqiufeng.dify.client.integration.spring.version.SpringVersionDetector;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
     * WebClient for SSE requests with custom timeout configuration.
     * Separate from main webClient to avoid applying SSE timeout to regular requests.
     */
    @Getter
    private final WebClient sseWebClient;
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
    private final WebClientConnectionProviderFactory webClientConnectionProviderFactory;
    private final RestClientHttpClientFactory restClientHttpClientFactory;
    private final WebClientFactory webClientFactory;
    private final RestClientFactory restClientFactory;

    /**
     * Constructor with base URL and client configuration.
     *
     * @param baseUrl      the base URL for all requests
     * @param clientConfig the client configuration
     * @param jsonMapper   the JSON mapper
     */
    public SpringHttpClient(String baseUrl, DifyProperties.ClientConfig clientConfig, JsonMapper jsonMapper) {
        this(baseUrl, clientConfig, null, null, jsonMapper, new HttpHeaders(), new ArrayList<>(), null, null, null, null);
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
        this(baseUrl, clientConfig, webClientBuilder, restClientBuilder, jsonMapper, new HttpHeaders(), new ArrayList<>(), null, null, null, null);
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
        this(baseUrl, clientConfig, webClientBuilder, restClientBuilder, jsonMapper, defaultHeaders, new ArrayList<>(), null, null, null, null);
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
        this(baseUrl, clientConfig, webClientBuilder, restClientBuilder, jsonMapper, defaultHeaders, interceptors, null, null, null, null);
    }

    /**
     * Constructor with custom WebClient builder, default headers, interceptors, and connection pool factories.
     *
     * @param baseUrl                              the base URL for all requests
     * @param clientConfig                         the client configuration
     * @param webClientBuilder                     custom WebClient.Builder (optional)
     * @param restClientBuilder                    custom RestClient.Builder (optional, Spring 6+ only)
     * @param jsonMapper                           the JSON mapper
     * @param defaultHeaders                       default headers to add to all requests
     * @param interceptors                         list of interceptors
     * @param webClientConnectionProviderFactory   factory for creating WebClient ConnectionProvider (optional)
     * @param restClientHttpClientFactory          factory for creating RestClient HttpClient (optional)
     */
    public SpringHttpClient(String baseUrl, DifyProperties.ClientConfig clientConfig,
                            WebClient.Builder webClientBuilder, Object restClientBuilder,
                            JsonMapper jsonMapper, HttpHeaders defaultHeaders, List<Object> interceptors,
                            WebClientConnectionProviderFactory webClientConnectionProviderFactory,
                            RestClientHttpClientFactory restClientHttpClientFactory) {
        this(baseUrl, clientConfig, webClientBuilder, restClientBuilder, jsonMapper, defaultHeaders, interceptors,
                webClientConnectionProviderFactory, restClientHttpClientFactory, null, null);
    }

    /**
     * Main constructor with all parameters including factory classes.
     *
     * @param baseUrl                              the base URL for all requests
     * @param clientConfig                         the client configuration
     * @param webClientBuilder                     custom WebClient.Builder (optional)
     * @param restClientBuilder                    custom RestClient.Builder (optional, Spring 6+ only)
     * @param jsonMapper                           the JSON mapper
     * @param defaultHeaders                       default headers to add to all requests
     * @param interceptors                         list of interceptors
     * @param webClientConnectionProviderFactory   factory for creating WebClient ConnectionProvider (optional)
     * @param restClientHttpClientFactory          factory for creating RestClient HttpClient (optional)
     * @param webClientFactory                     factory for creating WebClient (optional)
     * @param restClientFactory                    factory for creating RestClient (optional)
     */
    public SpringHttpClient(String baseUrl, DifyProperties.ClientConfig clientConfig,
                            WebClient.Builder webClientBuilder, Object restClientBuilder,
                            JsonMapper jsonMapper, HttpHeaders defaultHeaders, List<Object> interceptors,
                            WebClientConnectionProviderFactory webClientConnectionProviderFactory,
                            RestClientHttpClientFactory restClientHttpClientFactory,
                            WebClientFactory webClientFactory,
                            RestClientFactory restClientFactory) {
        this.baseUrl = baseUrl;
        this.clientConfig = clientConfig;
        this.jsonMapper = jsonMapper;
        this.defaultHeaders = defaultHeaders != null ? defaultHeaders : new HttpHeaders();
        this.interceptors = interceptors != null ? interceptors : new ArrayList<>();
        this.webClientConnectionProviderFactory = webClientConnectionProviderFactory;
        this.restClientHttpClientFactory = restClientHttpClientFactory;
        this.webClientFactory = webClientFactory;
        this.restClientFactory = restClientFactory;

        // Create WebClient (available in all Spring versions)
        this.webClient = createWebClient(webClientBuilder, false);

        // Create separate SSE WebClient if sseReadTimeout is configured
        Integer sseReadTimeout = (clientConfig != null) ? clientConfig.getSseReadTimeout() : null;
        if (sseReadTimeout != null && sseReadTimeout >= 0) {
            log.debug("Creating separate SSE WebClient with timeout={}s", sseReadTimeout);
            this.sseWebClient = createWebClient(webClientBuilder, true);
        } else {
            // Use same client for SSE if no special timeout configured
            this.sseWebClient = this.webClient;
        }

        // Create RestClient if available (Spring 6+)
        this.restClient = createRestClient(restClientBuilder);
    }

    /**
     * Create WebClient with configuration.
     *
     * @param builder custom builder (optional)
     * @param forSSE  true if creating WebClient for SSE requests with special timeout
     * @return configured WebClient
     */
    private WebClient createWebClient(WebClient.Builder builder, boolean forSSE) {
        // Use factory if available
        if (webClientFactory != null) {
            if (forSSE) {
                log.debug("Using WebClientFactory to create SSE WebClient");
                return webClientFactory.createWebClientForSSE(builder, baseUrl, clientConfig, defaultHeaders, interceptors);
            } else {
                log.debug("Using WebClientFactory to create WebClient");
                return webClientFactory.createWebClient(builder, baseUrl, clientConfig, defaultHeaders, interceptors);
            }
        }

        // Legacy logic: create WebClient directly
        boolean hasCustomBuilder = (builder != null);
        if (builder == null) {
            builder = WebClient.builder();
        }

        // Warn if custom builder is provided - connection pool config may override custom connector
        if (hasCustomBuilder && clientConfig != null &&
            (clientConfig.getMaxIdleConnections() != null || clientConfig.getKeepAliveSeconds() != null)) {
            log.warn("Custom WebClient.Builder provided with connection pool configuration. " +
                    "The connection pool settings will override any custom clientConnector (TLS/proxy/tracing). " +
                    "To preserve custom connector, use WebClientFactory or disable connection pool configuration.");
        }

        // Configure timeouts and connection pool using reactor-netty HttpClient
        try {
            Class<?> httpClientClass = Class.forName("reactor.netty.http.client.HttpClient");

            // Check if user explicitly configured connection pool settings
            boolean hasPoolConfig = clientConfig != null && (
                clientConfig.getMaxIdleConnections() != null ||
                clientConfig.getKeepAliveSeconds() != null ||
                clientConfig.getMaxRequests() != null ||
                clientConfig.getMaxRequestsPerHost() != null
            );

            // Build PoolSettings from clientConfig (only if user configured pool settings)
            PoolSettings poolSettings = hasPoolConfig ? PoolSettings.from(clientConfig) : null;

            // Create ConnectionProvider if factory is available and user configured pool settings
            Object connectionProvider = null;
            if (hasPoolConfig && webClientConnectionProviderFactory != null && poolSettings != null) {
                connectionProvider = webClientConnectionProviderFactory.buildConnectionProvider(
                        poolSettings, "dify-webclient-pool");
                if (connectionProvider != null) {
                    log.debug("Using custom WebClient ConnectionProvider with user-configured pool settings");
                }
            }

            // Create HttpClient with or without ConnectionProvider
            Object httpClient;
            if (connectionProvider != null) {
                // HttpClient.create(connectionProvider)
                httpClient = httpClientClass.getMethod("create", Class.forName("reactor.netty.resources.ConnectionProvider"))
                        .invoke(null, connectionProvider);
            } else {
                // HttpClient.create() - use Reactor Netty defaults (maxConnections=500)
                httpClient = httpClientClass.getMethod("create").invoke(null);
                if (!hasPoolConfig) {
                    log.debug("Using default Reactor Netty connection pool settings (no user configuration)");
                }
            }

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

            // Calculate effective response timeout
            // For SSE client: use sseReadTimeout if configured
            // For regular client: use callTimeout > max(read, write)
            long effectiveTimeout;
            if (forSSE) {
                // SSE client: use sseReadTimeout (0 = no timeout, >0 = specific timeout)
                Integer sseReadTimeout = (clientConfig != null) ? clientConfig.getSseReadTimeout() : null;
                effectiveTimeout = (sseReadTimeout != null && sseReadTimeout >= 0) ? sseReadTimeout : 0;
                log.debug("SSE WebClient using sseReadTimeout={}s for streaming requests", effectiveTimeout);
            } else if (poolSettings != null && poolSettings.getCallTimeoutSeconds() > 0) {
                // Regular client: if callTimeout is specified, use min(callTimeout, max(read, write))
                effectiveTimeout = Math.min(poolSettings.getCallTimeoutSeconds(), Math.max(readTimeout, writeTimeout));
                log.debug("WebClient using callTimeout {}s (min of callTimeout and max(read={}, write={}))",
                        effectiveTimeout, readTimeout, writeTimeout);
            } else {
                // Regular client: use max(read, write)
                effectiveTimeout = Math.max(readTimeout, writeTimeout);
                log.debug("WebClient using max(read={}, write={})={}s", readTimeout, writeTimeout, effectiveTimeout);
            }

            // Apply timeout: 0 means no timeout (skip setting responseTimeout)
            // Note: Duration.ZERO may be interpreted as "immediate timeout" by some implementations
            if (effectiveTimeout > 0) {
                Object responseTimeoutDuration = durationClass.getMethod("ofSeconds", long.class)
                        .invoke(null, effectiveTimeout);
                httpClient = httpClientClass.getMethod("responseTimeout", durationClass)
                        .invoke(httpClient, responseTimeoutDuration);
                log.debug("WebClient response timeout set to {}s", effectiveTimeout);
            } else {
                // Don't set responseTimeout to allow unlimited duration for SSE streams
                log.debug("WebClient response timeout disabled (not set) for long-running streams");
            }

            // Apply HttpClient to WebClient
            Class<?> reactorClientHttpConnectorClass = Class.forName("org.springframework.http.client.reactive.ReactorClientHttpConnector");
            Object connector = reactorClientHttpConnectorClass.getConstructor(httpClientClass).newInstance(httpClient);
            builder = builder.clientConnector((org.springframework.http.client.reactive.ClientHttpConnector) connector);
        } catch (Exception e) {
            // Check if user explicitly configured connection pool settings
            boolean hasPoolConfig = clientConfig != null &&
                (clientConfig.getMaxIdleConnections() != null ||
                 clientConfig.getKeepAliveSeconds() != null ||
                 clientConfig.getMaxRequests() != null ||
                 clientConfig.getMaxRequestsPerHost() != null);

            if (hasPoolConfig) {
                // User configured pool but it failed - log warning
                log.warn("Failed to configure WebClient connection pool. Connection pool settings will be ignored. " +
                        "Reason: {}. Ensure reactor-netty dependency is available. Falling back to default WebClient.",
                        e.getMessage());
            } else {
                // No pool config - just debug
                log.debug("WebClient connection pool not configured: {}", e.getMessage());
            }
        }

        builder = builder.baseUrl(baseUrl)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json");

        // Add default headers if configured
        if (defaultHeaders != null && !defaultHeaders.isEmpty()) {
            WebClient.Builder finalBuilder = builder;
            defaultHeaders.forEach((key, values) -> {
                if (values != null && !values.isEmpty()) {
                    finalBuilder.defaultHeader(key, values.get(0));
                }
            });
        }
        if (clientConfig != null && clientConfig.getLogging()) {
            boolean maskingEnabled = clientConfig.getLoggingMaskEnabled() != null
                    ? clientConfig.getLoggingMaskEnabled() : true;
            int logBodyMaxBytes = clientConfig.getLogBodyMaxBytes() != null
                    ? clientConfig.getLogBodyMaxBytes() : 10240;
            boolean logBinaryBody = clientConfig.getLogBinaryBody() != null
                    ? clientConfig.getLogBinaryBody() : false;
            // Create a new filter instance for each client to support different configurations
            DifyLoggingFilter filter = DifyLoggingControl.createFilter(maskingEnabled, logBodyMaxBytes, logBinaryBody);
            builder.filter(filter);
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
     * Check if user has already set a custom requestFactory in RestClient.Builder.
     * Uses reflection to access the internal requestFactory field.
     *
     * @param builder the RestClient.Builder instance
     * @return true if user has set a custom requestFactory, false otherwise
     */
    private boolean hasUserRequestFactory(Object builder) {
        try {
            Field field = builder.getClass().getDeclaredField("requestFactory");
            field.setAccessible(true);
            return field.get(builder) != null;
        } catch (NoSuchFieldException e) {
            // Field name changed in newer Spring version, assume user has customized to be safe
            log.debug("Cannot detect requestFactory field (Spring API may have changed), assuming user customization exists");
            // Default to respecting user configuration
            return true;
        } catch (IllegalAccessException e) {
            // Reflection access denied (strong encapsulation in Java 9+)
            log.warn("Failed to access requestFactory field due to module restrictions. " +
                    "Connection pool configuration may be skipped. " +
                    "Consider adding JVM flag: --add-opens java.base/java.lang.reflect=ALL-UNNAMED");
            // Default to respecting user configuration to avoid breaking changes
            return true;
        } catch (Exception e) {
            // Other reflection errors, assume user has customized to be safe
            log.debug("Failed to check user requestFactory: {}", e.getMessage());
            // Default to respecting user configuration
            return true;
        }
    }

    /**
     * Create RestClient if available (Spring 6+).
     *
     * @param builder custom builder (optional)
     * @return configured RestClient, or null if not available
     */
    @SuppressWarnings("unchecked")
    private Object createRestClient(Object builder) {
        // Use factory if available
        if (restClientFactory != null) {
            log.debug("Using RestClientFactory to create RestClient");
            return restClientFactory.createRestClient(builder, baseUrl, clientConfig, defaultHeaders, interceptors);
        }

        // Legacy logic: create RestClient directly
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

            // Check if user has already set a custom requestFactory (user customization takes precedence)
            boolean hasUserRequestFactory = hasUserRequestFactory(restClientBuilder);

            if (hasUserRequestFactory) {
                log.debug("User has provided custom requestFactory for RestClient, skipping default connection pool configuration");
            } else {
                // Check if user explicitly configured connection pool settings
                boolean hasPoolConfig = clientConfig != null && (
                    clientConfig.getMaxIdleConnections() != null ||
                    clientConfig.getKeepAliveSeconds() != null ||
                    clientConfig.getMaxRequests() != null ||
                    clientConfig.getMaxRequestsPerHost() != null
                );

                if (hasPoolConfig) {
                    // Build PoolSettings from clientConfig (only if user configured pool settings)
                    PoolSettings poolSettings = PoolSettings.from(clientConfig);

                    // Try to use Apache HttpClient 5 with connection pool if factory is available
                    Object httpClient = null;
                    if (restClientHttpClientFactory != null) {
                        httpClient = restClientHttpClientFactory.buildHttpClient(poolSettings);
                        if (httpClient != null) {
                            log.debug("Using custom RestClient HttpClient with user-configured connection pool");
                        }
                    }

                    // Configure timeouts using ClientHttpRequestFactory
                    try {
                        restClientBuilder = RestClientConfigurer.applyRequestFactory(
                                builderClass, restClientBuilder, httpClient, poolSettings);
                    } catch (Exception e) {
                        // User configured pool but it failed - log warning
                        log.warn("Failed to configure RestClient connection pool. Connection pool settings will be ignored. " +
                                "Reason: {}. Ensure Apache HttpClient 5 dependency is available. Falling back to default client.",
                                e.getMessage());
                    }
                } else {
                    log.debug("Using default RestClient configuration (no user-configured connection pool settings)");
                }
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
                    List<String> values = entry.getValue();
                    if (values != null && !values.isEmpty()) {
                        restClientBuilder = builderClass.getMethod("defaultHeader", String.class, String[].class)
                                .invoke(restClientBuilder, key, new String[]{values.get(0)});
                    }
                }
            }

            if (clientConfig != null && clientConfig.getLogging()) {
                boolean maskingEnabled = clientConfig.getLoggingMaskEnabled() != null
                        ? clientConfig.getLoggingMaskEnabled() : true;
                int logBodyMaxBytes = clientConfig.getLogBodyMaxBytes() != null
                        ? clientConfig.getLogBodyMaxBytes() : 10240;
                // Create a new interceptor instance for each client to support different configurations
                DifyRestLoggingInterceptor interceptor = DifyLoggingControl.createInterceptor(maskingEnabled, logBodyMaxBytes);

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
        } catch (ClassNotFoundException e) {
            log.debug("RestClient not available (Spring 6+ required): {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.warn("Failed to create RestClient: {}", e.getMessage(), e);
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
    public RequestBodyUriSpec method(io.github.guoshiqiufeng.dify.client.core.http.HttpMethod httpMethod) {
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
