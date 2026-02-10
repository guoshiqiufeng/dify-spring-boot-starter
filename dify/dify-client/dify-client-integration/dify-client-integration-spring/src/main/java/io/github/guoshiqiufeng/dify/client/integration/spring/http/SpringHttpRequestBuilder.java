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
import io.github.guoshiqiufeng.dify.client.core.http.*;
import io.github.guoshiqiufeng.dify.client.core.response.ResponseEntity;
import io.github.guoshiqiufeng.dify.client.core.util.LinkedMultiValueMap;
import io.github.guoshiqiufeng.dify.client.core.util.MultiValueMap;
import io.github.guoshiqiufeng.dify.client.core.web.client.ResponseSpec;
import io.github.guoshiqiufeng.dify.client.core.web.util.DefaultUriBuilder;
import io.github.guoshiqiufeng.dify.client.core.web.util.UriBuilder;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Spring-based implementation of HttpRequestBuilder.
 * Uses RestClient for synchronous requests (Spring 6+) or WebClient (all versions).
 * Delegates execution to specialized executors following Single Responsibility Principle.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025-12-26
 */
public class SpringHttpRequestBuilder implements HttpRequestBuilder {

    private final SpringHttpClient client;
    private final String method;
    private final JsonMapper jsonMapper;
    private final RestClientExecutor restClientExecutor;
    private final WebClientExecutor webClientExecutor;

    private URI uri;
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> cookies = new HashMap<>();
    private final Map<String, String> queryParams = new HashMap<>();
    private Object body;
    private Map<String, Object> multipartData;

    /**
     * Constructor.
     *
     * @param client         the Spring HTTP client
     * @param method         the HTTP method
     * @param jsonMapper     the JSON mapper
     * @param defaultHeaders default headers to add to all requests
     */
    public SpringHttpRequestBuilder(SpringHttpClient client, String method, JsonMapper jsonMapper, HttpHeaders defaultHeaders) {
        this.client = client;
        this.method = method;
        this.jsonMapper = jsonMapper;

        // Add default headers if not empty
        if (defaultHeaders != null && !defaultHeaders.isEmpty()) {
            defaultHeaders.forEach((key, values) -> {
                if (values != null && !values.isEmpty()) {
                    this.headers.put(key, values.get(0));
                }
            });
        }

        Boolean skipNull = client.getClientConfig() != null ? client.getClientConfig().getSkipNull() : true;
        // Initialize executors
        this.restClientExecutor = client.hasRestClient()
                ? new RestClientExecutor(client.getRestClient(), jsonMapper, skipNull)
                : null;
        this.webClientExecutor = new WebClientExecutor(client.getWebClient(), client.getSseWebClient(), jsonMapper, skipNull);
    }

    @Override
    public HttpRequestBuilder uri(String uri) {
        this.uri = new DefaultUriBuilder().path(uri).build();
        return this;
    }

    @Override
    public HttpRequestBuilder uri(String uri, Object... uriParams) {
        this.uri = new DefaultUriBuilder().path(uri).build(uriParams);
        return this;
    }

    @Override
    public HttpRequestBuilder uri(Consumer<UriBuilder> uriBuilderConsumer) {
        DefaultUriBuilder uriBuilder = new DefaultUriBuilder();
        uriBuilderConsumer.accept(uriBuilder);
        this.uri = uriBuilder.build();
        return this;
    }

    @Override
    public HttpRequestBuilder header(String name, String value) {
        this.headers.put(name, value);
        return this;
    }

    @Override
    public HttpRequestBuilder headers(Map<String, String> headers) {
        this.headers.putAll(headers);
        return this;
    }

    @Override
    public HttpRequestBuilder headers(Consumer<HttpHeaders> headersConsumer) {
        HttpHeaders httpHeaders = new HttpHeaders();
        headersConsumer.accept(httpHeaders);
        // Convert HttpHeaders to single value map (using first value)
        httpHeaders.forEach((key, values) -> {
            if (values != null && !values.isEmpty()) {
                this.headers.put(key, values.get(0));
            }
        });
        return this;
    }

    @Override
    public HttpRequestBuilder cookies(Consumer<MultiValueMap<String, String>> cookiesConsumer) {
        MultiValueMap<String, String> httpCookies = new LinkedMultiValueMap<>();
        cookiesConsumer.accept(httpCookies);
        // Convert HttpHeaders to single value map (using first value)
        httpCookies.forEach((key, values) -> {
            if (values != null && !values.isEmpty()) {
                this.cookies.put(key, values.get(0));
            }
        });
        return this;
    }

    @Override
    public HttpRequestBuilder queryParam(String name, String value) {
        this.queryParams.put(name, value);
        return this;
    }

    @Override
    public HttpRequestBuilder queryParams(Map<String, String> params) {
        this.queryParams.putAll(params);
        return this;
    }

    @Override
    public HttpRequestBuilder body(Object body) {
        this.body = body;
        return this;
    }

    @Override
    public HttpRequestBuilder multipart(Map<String, Object> formData) {
        this.multipartData = formData;
        return this;
    }

    @Override
    public <T> T execute(Class<T> responseType) {
        if (restClientExecutor != null) {
            return restClientExecutor.execute(method, uri, headers, cookies, body, responseType);
        } else {
            return webClientExecutor.execute(method, uri, headers, cookies, queryParams, body, responseType);
        }
    }

    @Override
    public <T> T execute(TypeReference<T> typeReference) {
        if (restClientExecutor != null) {
            return restClientExecutor.execute(method, uri, headers, cookies, body, typeReference);
        } else {
            return webClientExecutor.execute(method, uri, headers, cookies, queryParams, body, typeReference);
        }
    }

    @Override
    public <T> ResponseEntity<T> executeForResponse(Class<T> responseType) {
        T body = execute(responseType);
        // Note: We lose status code and headers information here
        // This is a limitation of the simplified approach
        return ResponseEntity.<T>builder()
                .statusCode(200)
                .body(body)
                .build();
    }

    @Override
    public <T> ResponseEntity<T> executeForResponse(TypeReference<T> typeReference) {
        T body = execute(typeReference);
        return ResponseEntity.<T>builder()
                .statusCode(200)
                .body(body)
                .build();
    }

    @Override
    public <T> Flux<T> stream(Class<T> responseType) {
        return webClientExecutor.executeStream(method, uri, headers, cookies, queryParams, body, responseType);
    }

    @Override
    public int executeForStatus() {
        execute(Void.class);
        return 200;
    }

    @Override
    public ResponseSpec retrieve() {
        return new SpringResponseSpec();
    }

    /**
     * Spring implementation of ResponseSpec.
     * Delegates execution to executors and handles error processing.
     */
    private class SpringResponseSpec implements ResponseSpec {

        private final List<ResponseErrorHandler> errorHandlers = new ArrayList<>();

        @Override
        public ResponseSpec onStatus(ResponseErrorHandler errorHandler) {
            this.errorHandlers.add(errorHandler);
            return this;
        }

        @Override
        public <T> T body(Class<T> responseType) {
            ResponseEntity<T> response = toEntity(responseType);
            handleErrors(response);
            return response.getBody();
        }

        @Override
        public <T> T body(TypeReference<T> typeReference) {
            ResponseEntity<T> response = toEntity(typeReference);
            handleErrors(response);
            return response.getBody();
        }

        @Override
        public <T> ResponseEntity<T> toEntity(Class<T> responseType) {
            ResponseEntity<T> response;
            if (restClientExecutor != null) {
                response = restClientExecutor.executeForEntity(method, uri, headers, cookies, body, responseType);
            } else {
                response = webClientExecutor.executeForEntity(method, uri, headers, cookies, queryParams, body, responseType);
            }
            // Handle errors before returning the response
            handleErrors(response);
            return response;
        }

        @Override
        public <T> ResponseEntity<T> toEntity(TypeReference<T> typeReference) {
            ResponseEntity<T> response;
            if (restClientExecutor != null) {
                response = restClientExecutor.executeForEntity(method, uri, headers, cookies, body, typeReference);
            } else {
                response = webClientExecutor.executeForEntity(method, uri, headers, cookies, queryParams, body, typeReference);
            }
            // Handle errors before returning the response
            handleErrors(response);
            return response;
        }

        @Override
        public ResponseEntity<Void> toBodilessEntity() {
            ResponseEntity<Void> response = toEntity(Void.class);
            handleErrors(response);
            return response;
        }

        @Override
        public <T> Flux<T> bodyToFlux(Class<T> responseType) {
            return webClientExecutor.executeStream(method, uri, headers, cookies, queryParams, body, responseType);
        }

        @Override
        public <T> Flux<T> bodyToFlux(TypeReference<T> typeReference) {
            return webClientExecutor.executeStream(method, uri, headers, cookies, queryParams, body, typeReference);
        }

        /**
         * Handle errors using registered error handlers.
         *
         * @param response the HTTP response
         */
        private void handleErrors(ResponseEntity<?> response) {
            for (ResponseErrorHandler handler : errorHandlers) {
                if (handler.getStatusPredicate().test(response.getStatusCode())) {
                    try {
                        handler.handle(response);
                    } catch (Exception e) {
                        if (e instanceof RuntimeException) {
                            throw (RuntimeException) e;
                        }
                        throw new HttpClientException("Error handler failed: " + e.getMessage(), e);
                    }
                }
            }
        }
    }
}
