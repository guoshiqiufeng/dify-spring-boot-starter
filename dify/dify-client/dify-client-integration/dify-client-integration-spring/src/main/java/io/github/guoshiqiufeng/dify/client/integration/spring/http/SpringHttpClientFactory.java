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
import io.github.guoshiqiufeng.dify.client.core.http.HttpClientFactory;
import io.github.guoshiqiufeng.dify.client.core.http.HttpHeaders;
import io.github.guoshiqiufeng.dify.client.core.web.client.HttpClient;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.pool.RestClientHttpClientFactory;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.pool.WebClientConnectionProviderFactory;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory for creating Spring HTTP client instances.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025-12-26
 */
public class SpringHttpClientFactory implements HttpClientFactory {

    private final WebClient.Builder webClientBuilder;

    private final Object restClientBuilder;

    private final JsonMapper jsonMapper;

    private final HttpHeaders defaultHeaders;

    private final List<Object> interceptors;

    private final WebClientConnectionProviderFactory webClientConnectionProviderFactory;

    private final RestClientHttpClientFactory restClientHttpClientFactory;

    public SpringHttpClientFactory(JsonMapper jsonMapper) {
        this(WebClient.builder(), null, jsonMapper, new HttpHeaders(), new ArrayList<>(), null, null);
    }

    public SpringHttpClientFactory(WebClient.Builder webClientBuilder, JsonMapper jsonMapper) {
        this(webClientBuilder, null, jsonMapper, new HttpHeaders(), new ArrayList<>(), null, null);
    }

    public SpringHttpClientFactory(WebClient.Builder webClientBuilder, Object restClientBuilder, JsonMapper jsonMapper) {
        this(webClientBuilder, restClientBuilder, jsonMapper, new HttpHeaders(), new ArrayList<>(), null, null);
    }

    /**
     * Constructor with custom connection pool factories.
     *
     * @param webClientBuilder                     custom WebClient.Builder
     * @param restClientBuilder                    custom RestClient.Builder (Spring 6+ only)
     * @param jsonMapper                           JSON mapper
     * @param webClientConnectionProviderFactory   factory for creating WebClient ConnectionProvider (optional)
     * @param restClientHttpClientFactory          factory for creating RestClient HttpClient (optional)
     */
    public SpringHttpClientFactory(WebClient.Builder webClientBuilder, Object restClientBuilder, JsonMapper jsonMapper,
                                    WebClientConnectionProviderFactory webClientConnectionProviderFactory,
                                    RestClientHttpClientFactory restClientHttpClientFactory) {
        this(webClientBuilder, restClientBuilder, jsonMapper, new HttpHeaders(), new ArrayList<>(),
                webClientConnectionProviderFactory, restClientHttpClientFactory);
    }

    private SpringHttpClientFactory(WebClient.Builder webClientBuilder, Object restClientBuilder, JsonMapper jsonMapper,
                                    HttpHeaders defaultHeaders, List<Object> interceptors,
                                    WebClientConnectionProviderFactory webClientConnectionProviderFactory,
                                    RestClientHttpClientFactory restClientHttpClientFactory) {
        this.webClientBuilder = webClientBuilder;
        this.restClientBuilder = restClientBuilder;
        this.jsonMapper = jsonMapper;
        this.defaultHeaders = defaultHeaders;
        this.interceptors = interceptors;
        this.webClientConnectionProviderFactory = webClientConnectionProviderFactory;
        this.restClientHttpClientFactory = restClientHttpClientFactory;
    }

    @Override
    public HttpClient createClient(String baseUrl, DifyProperties.ClientConfig clientConfig) {
        WebClient.Builder builder = webClientBuilder.clone();

        return new SpringHttpClient(baseUrl, clientConfig, builder, restClientBuilder, jsonMapper, defaultHeaders, interceptors,
                webClientConnectionProviderFactory, restClientHttpClientFactory);
    }

    @Override
    public HttpClientFactory defaultHeader(String key, String value) {
        HttpHeaders newHeaders = new HttpHeaders(this.defaultHeaders);
        newHeaders.add(key, value);
        return new SpringHttpClientFactory(webClientBuilder, restClientBuilder, jsonMapper, newHeaders, interceptors,
                webClientConnectionProviderFactory, restClientHttpClientFactory);
    }

    @Override
    public HttpClientFactory interceptor(Object interceptor) {
        List<Object> newInterceptors = new ArrayList<>(this.interceptors);
        newInterceptors.add(interceptor);
        return new SpringHttpClientFactory(webClientBuilder, restClientBuilder, jsonMapper, defaultHeaders, newInterceptors,
                webClientConnectionProviderFactory, restClientHttpClientFactory);
    }
}
