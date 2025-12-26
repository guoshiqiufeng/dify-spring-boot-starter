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

import io.github.guoshiqiufeng.dify.client.core.http.HttpClientException;
import io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper;
import io.github.guoshiqiufeng.dify.client.core.web.util.DefaultUriBuilder;
import io.github.guoshiqiufeng.dify.client.core.http.HttpHeaders;
import io.github.guoshiqiufeng.dify.client.core.http.HttpRequestBuilder;
import io.github.guoshiqiufeng.dify.client.core.http.ResponseErrorHandler;
import io.github.guoshiqiufeng.dify.client.core.map.LinkedMultiValueMap;
import io.github.guoshiqiufeng.dify.client.core.map.MultiValueMap;
import io.github.guoshiqiufeng.dify.client.core.web.client.ResponseSpec;
import io.github.guoshiqiufeng.dify.client.core.http.TypeReference;
import io.github.guoshiqiufeng.dify.client.core.web.util.UriBuilder;
import io.github.guoshiqiufeng.dify.client.core.response.HttpResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    private String uri;
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
        this.webClientExecutor = new WebClientExecutor(client.getWebClient(), jsonMapper, skipNull);
    }

    @Override
    public HttpRequestBuilder uri(String uri) {
        this.uri = uri;
        return this;
    }

    @Override
    public HttpRequestBuilder uri(String uri, Object... uriParams) {
        this.uri = replaceUriVariables(uri, uriParams);
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
    public <T> HttpResponse<T> executeForResponse(Class<T> responseType) {
        T body = execute(responseType);
        // Note: We lose status code and headers information here
        // This is a limitation of the simplified approach
        return HttpResponse.<T>builder()
                .statusCode(200)
                .body(body)
                .build();
    }

    @Override
    public <T> HttpResponse<T> executeForResponse(TypeReference<T> typeReference) {
        T body = execute(typeReference);
        return HttpResponse.<T>builder()
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
            HttpResponse<T> response = toEntity(responseType);
            handleErrors(response);
            return response.getBody();
        }

        @Override
        public <T> T body(TypeReference<T> typeReference) {
            HttpResponse<T> response = toEntity(typeReference);
            handleErrors(response);
            return response.getBody();
        }

        @Override
        public <T> HttpResponse<T> toEntity(Class<T> responseType) {
            HttpResponse<T> response;
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
        public <T> HttpResponse<T> toEntity(TypeReference<T> typeReference) {
            HttpResponse<T> response;
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
        public HttpResponse<Void> toBodilessEntity() {
            HttpResponse<Void> response = toEntity(Void.class);
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
        private void handleErrors(HttpResponse<?> response) {
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

    /**
     * Replace URI path variables with values.
     * Only replaces placeholders in the path portion, not in query parameters.
     * Query parameters with placeholders are extracted and matched with remaining uriParams.
     *
     * @param uri       URI template (e.g., "/users/{id}?page={page}&limit={limit}")
     * @param uriParams parameter values for both path and query variables
     * @return URI with replaced path variables
     */
    private String replaceUriVariables(String uri, Object... uriParams) {
        // Split URI into path and query parts
        int queryIndex = uri.indexOf('?');
        if (queryIndex == -1) {
            // No query parameters, replace all placeholders in path
            String result = uri;
            for (Object param : uriParams) {
                result = result.replaceFirst("\\{[^}]+\\}", String.valueOf(param));
            }
            return result;
        }

        // Has query parameters - handle path and query separately
        String pathPart = uri.substring(0, queryIndex);
        String queryPart = uri.substring(queryIndex + 1);

        // Count placeholders in path
        int pathPlaceholderCount = 0;
        String tempPath = pathPart;
        while (tempPath.contains("{") && tempPath.contains("}")) {
            int start = tempPath.indexOf("{");
            int end = tempPath.indexOf("}", start);
            if (end > start) {
                pathPlaceholderCount++;
                tempPath = tempPath.substring(end + 1);
            } else {
                break;
            }
        }

        // Replace path variables
        String resultPath = pathPart;
        int paramIndex = 0;
        for (int i = 0; i < pathPlaceholderCount && paramIndex < uriParams.length; i++, paramIndex++) {
            resultPath = resultPath.replaceFirst("\\{[^}]+\\}", String.valueOf(uriParams[paramIndex]));
        }

        // Parse query parameters
        String[] queryPairs = queryPart.split("&");
        for (String pair : queryPairs) {
            int eqIndex = pair.indexOf('=');
            if (eqIndex > 0) {
                String key = pair.substring(0, eqIndex);
                String value = pair.substring(eqIndex + 1);

                // Check if value is a placeholder
                if (value.startsWith("{") && value.endsWith("}")) {
                    // Match with remaining uriParams
                    if (paramIndex < uriParams.length) {
                        Object paramValue = uriParams[paramIndex++];
                        // Only add non-null values to queryParams
                        if (paramValue != null) {
                            this.queryParams.put(key, String.valueOf(paramValue));
                        }
                    }
                } else {
                    // Static query parameter value
                    this.queryParams.put(key, value);
                }
            }
        }

        // Return only the path portion
        return resultPath;
    }
}
