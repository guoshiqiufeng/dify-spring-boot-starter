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
import io.github.guoshiqiufeng.dify.client.core.codec.util.JsonSerializationHelper;
import io.github.guoshiqiufeng.dify.client.core.http.HttpClientException;
import io.github.guoshiqiufeng.dify.client.core.http.TypeReference;
import io.github.guoshiqiufeng.dify.client.core.http.util.HttpStatusValidator;
import io.github.guoshiqiufeng.dify.client.core.http.util.MultipartBodyProcessor;
import io.github.guoshiqiufeng.dify.client.core.http.util.RequestParameterProcessor;
import io.github.guoshiqiufeng.dify.client.core.response.HttpResponse;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.util.HttpHeaderConverter;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.util.SpringMultipartBodyBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;

/**
 * Executor for Spring WebClient.
 * Handles request execution using WebClient API.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025-12-31
 */
@Slf4j
class WebClientExecutor {

    private final WebClient webClient;
    private final JsonMapper jsonMapper;
    private final ResponseConverter responseConverter;
    private final Boolean skipNull;

    /**
     * Constructor.
     *
     * @param webClient  WebClient instance
     * @param jsonMapper JSON mapper
     */
    WebClientExecutor(WebClient webClient, JsonMapper jsonMapper) {
        this(webClient, jsonMapper, true);
    }

    /**
     * Constructor.
     *
     * @param webClient  WebClient instance
     * @param jsonMapper JSON mapper
     */
    WebClientExecutor(WebClient webClient, JsonMapper jsonMapper, Boolean skipNull) {
        this.webClient = webClient;
        this.jsonMapper = jsonMapper;
        this.skipNull = skipNull;
        this.responseConverter = new ResponseConverter(jsonMapper);
    }

    /**
     * Execute request and return body.
     *
     * @param method       HTTP method
     * @param uri          request URI
     * @param headers      request headers
     * @param cookies      request cookies
     * @param queryParams  query parameters
     * @param body         request body
     * @param responseType response type
     * @param <T>          response type
     * @return response body
     */
    <T> T execute(String method, URI uri, Map<String, String> headers,
                  Map<String, String> cookies, Map<String, String> queryParams,
                  Object body, Class<T> responseType) {
        WebClient.RequestBodySpec requestSpec = buildRequest(method, uri, headers, cookies, queryParams, body);

        return requestSpec
                .retrieve()
                .bodyToMono(String.class)
                .map(responseBody -> responseConverter.deserialize(responseBody, responseType))
                .block();
    }

    /**
     * Execute request with TypeReference and return body.
     *
     * @param method        HTTP method
     * @param uri           request URI
     * @param headers       request headers
     * @param cookies       request cookies
     * @param queryParams   query parameters
     * @param body          request body
     * @param typeReference type reference
     * @param <T>           response type
     * @return response body
     */
    <T> T execute(String method, URI uri, Map<String, String> headers,
                  Map<String, String> cookies, Map<String, String> queryParams,
                  Object body, TypeReference<T> typeReference) {
        WebClient.RequestBodySpec requestSpec = buildRequest(method, uri, headers, cookies, queryParams, body);

        return requestSpec
                .retrieve()
                .bodyToMono(String.class)
                .map(responseBody -> responseConverter.deserialize(responseBody, typeReference))
                .block();
    }

    /**
     * Execute request and return full HttpResponse.
     *
     * @param method       HTTP method
     * @param uri          request URI
     * @param headers      request headers
     * @param cookies      request cookies
     * @param queryParams  query parameters
     * @param body         request body
     * @param responseType response type
     * @param <T>          response type
     * @return HttpResponse with status, headers, and body
     */
    <T> HttpResponse<T> executeForEntity(String method, URI uri, Map<String, String> headers,
                                         Map<String, String> cookies, Map<String, String> queryParams,
                                         Object body, Class<T> responseType) {
        WebClient.RequestBodySpec requestSpec = buildRequest(method, uri, headers, cookies, queryParams, body);

        try {
            Mono<ResponseEntity<String>> responseMono = requestSpec
                    .retrieve()
                    .toEntity(String.class);

            ResponseEntity<String> responseEntity = responseMono.block();

            if (responseEntity == null) {
                throw new HttpClientException("Response entity is null");
            }

            // For success responses (2xx), deserialize normally
            int statusCode = responseEntity.getStatusCode().value();
            if (HttpStatusValidator.isSuccessful(statusCode)) {
                return responseConverter.convert(responseEntity, responseType);
            } else {
                // For error responses, return raw error message as body
                // The error handler will receive this and can process it
                @SuppressWarnings("unchecked")
                T errorBody = (T) responseEntity.getBody();
                return HttpResponse.<T>builder()
                        .statusCode(statusCode)
                        .headers(HttpHeaderConverter.fromSpringHeaders(responseEntity.getHeaders()))
                        .body(errorBody)
                        .build();
            }
        } catch (WebClientResponseException e) {
            // Handle HTTP error responses (4xx, 5xx) thrown by WebClient
            int statusCode = e.getStatusCode().value();
            String errorBody = e.getResponseBodyAsString();

            log.debug("WebClient error response: status={}, body={}", statusCode, errorBody);

            // Return error response without throwing exception
            // Let the upper layer handleErrors() process it
            @SuppressWarnings("unchecked")
            T typedErrorBody = (T) errorBody;
            return HttpResponse.<T>builder()
                    .statusCode(statusCode)
                    .headers(HttpHeaderConverter.fromSpringHeaders(e.getHeaders()))
                    .body(typedErrorBody)
                    .build();
        }
    }

    /**
     * Execute request with TypeReference and return full HttpResponse.
     *
     * @param method        HTTP method
     * @param uri           request URI
     * @param headers       request headers
     * @param cookies       request cookies
     * @param queryParams   query parameters
     * @param body          request body
     * @param typeReference type reference
     * @param <T>           response type
     * @return HttpResponse with status, headers, and body
     */
    <T> HttpResponse<T> executeForEntity(String method, URI uri, Map<String, String> headers,
                                         Map<String, String> cookies, Map<String, String> queryParams,
                                         Object body, TypeReference<T> typeReference) {
        WebClient.RequestBodySpec requestSpec = buildRequest(method, uri, headers, cookies, queryParams, body);

        try {
            Mono<ResponseEntity<String>> responseMono = requestSpec
                    .retrieve()
                    .toEntity(String.class);

            ResponseEntity<String> responseEntity = responseMono.block();

            if (responseEntity == null) {
                throw new HttpClientException("Response entity is null");
            }

            // For success responses (2xx), deserialize normally
            int statusCode = responseEntity.getStatusCode().value();
            if (HttpStatusValidator.isSuccessful(statusCode)) {
                return responseConverter.convert(responseEntity, typeReference);
            } else {
                // For error responses, return raw error message as body
                // The error handler will receive this and can process it
                @SuppressWarnings("unchecked")
                T errorBody = (T) responseEntity.getBody();
                return HttpResponse.<T>builder()
                        .statusCode(statusCode)
                        .headers(HttpHeaderConverter.fromSpringHeaders(responseEntity.getHeaders()))
                        .body(errorBody)
                        .build();
            }
        } catch (WebClientResponseException e) {
            // Handle HTTP error responses (4xx, 5xx) thrown by WebClient
            int statusCode = e.getStatusCode().value();
            String errorBody = e.getResponseBodyAsString();

            log.debug("WebClient error response: status={}, body={}", statusCode, errorBody);

            // Return error response without throwing exception
            // Let the upper layer handleErrors() process it
            @SuppressWarnings("unchecked")
            T typedErrorBody = (T) errorBody;
            return HttpResponse.<T>builder()
                    .statusCode(statusCode)
                    .headers(HttpHeaderConverter.fromSpringHeaders(e.getHeaders()))
                    .body(typedErrorBody)
                    .build();
        }
    }

    /**
     * Execute streaming request.
     *
     * @param method       HTTP method
     * @param uri          request URI
     * @param headers      request headers
     * @param cookies      request cookies
     * @param queryParams  query parameters
     * @param body         request body
     * @param responseType response type
     * @param <T>          response type
     * @return Flux of response items
     */
    <T> Flux<T> executeStream(String method, URI uri, Map<String, String> headers,
                              Map<String, String> cookies, Map<String, String> queryParams,
                              Object body, Class<T> responseType) {
        WebClient.RequestBodySpec requestSpec = buildRequest(method, uri, headers, cookies, queryParams, body);

        // For SSE streaming, use Spring's ServerSentEvent support
        ParameterizedTypeReference<org.springframework.http.codec.ServerSentEvent<String>> sseType =
                new ParameterizedTypeReference<org.springframework.http.codec.ServerSentEvent<String>>() {
                };

        return requestSpec
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(sseType)
                .doOnNext(sse -> log.debug("Received SSE event: id={}, event={}, data={}", sse.id(), sse.event(), sse.data()))
                .doOnComplete(() -> log.debug("SSE stream completed"))
                .doOnError(e -> log.error("SSE stream error", e))
                .mapNotNull(sse -> sse.data())
                .doOnNext(data -> log.debug("Extracted data: {}", data))
                .filter(data -> data != null && !data.isEmpty() && isCompleteJson(data))
                .doOnNext(json -> log.debug("Filtered JSON: {}", json))
                .mapNotNull(json -> {
                    try {
                        return responseConverter.deserialize(json, responseType);
                    } catch (Exception e) {
                        log.warn("Failed to parse SSE event: {}", json, e);
                        return null;
                    }
                });
    }

    /**
     * Execute streaming request with TypeReference.
     *
     * @param method        HTTP method
     * @param uri           request URI
     * @param headers       request headers
     * @param cookies       request cookies
     * @param queryParams   query parameters
     * @param body          request body
     * @param typeReference type reference
     * @param <T>           response type
     * @return Flux of response items
     */
    <T> Flux<T> executeStream(String method, URI uri, Map<String, String> headers,
                              Map<String, String> cookies, Map<String, String> queryParams,
                              Object body, TypeReference<T> typeReference) {
        WebClient.RequestBodySpec requestSpec = buildRequest(method, uri, headers, cookies, queryParams, body);

        // For SSE streaming, use Spring's ServerSentEvent support
        ParameterizedTypeReference<org.springframework.http.codec.ServerSentEvent<String>> sseType =
                new ParameterizedTypeReference<org.springframework.http.codec.ServerSentEvent<String>>() {
                };

        return requestSpec
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(sseType)
                .mapNotNull(ServerSentEvent::data)
                .filter(data -> data != null && !data.isEmpty() && isCompleteJson(data))
                .mapNotNull(json -> {
                    try {
                        return responseConverter.deserialize(json, typeReference);
                    } catch (Exception e) {
                        log.warn("Failed to parse SSE event: {}", json, e);
                        return null;
                    }
                });
    }

    /**
     * Check if a string is complete JSON (simple heuristic).
     *
     * @param data JSON string
     * @return true if appears to be complete JSON
     */
    private boolean isCompleteJson(String data) {
        if (data == null || data.isEmpty()) {
            return false;
        }

        data = data.trim();

        // Check for complete JSON object
        if (data.startsWith("{") && data.endsWith("}")) {
            return true;
        }

        // Check for complete JSON array
        if (data.startsWith("[") && data.endsWith("]")) {
            return true;
        }

        // Check for JSON primitives
        if (data.startsWith("\"") && data.endsWith("\"")) {
            return true;
        }

        if ("true".equals(data) || "false".equals(data) || "null".equals(data)) {
            return true;
        }

        // Check for numbers
        try {
            Double.parseDouble(data);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Build WebClient request.
     *
     * @param method      HTTP method
     * @param uri         request URI
     * @param headers     request headers
     * @param cookies     request cookies
     * @param queryParams query parameters
     * @param body        request body
     * @return WebClient.RequestBodySpec
     */
    WebClient.RequestBodySpec buildRequest(String method, URI uri, Map<String, String> headers,
                                           Map<String, String> cookies, Map<String, String> queryParams,
                                           Object body) {
        // Start request
        WebClient.RequestBodyUriSpec requestSpec = webClient.method(HttpMethod.valueOf(method));

        // Set URI - use uriBuilder to respect baseUrl and avoid double encoding
        WebClient.RequestBodySpec bodySpec;
        if (uri != null) {
            // Extract path and query from URI
            String path = uri.getPath();
            String query = uri.getQuery();

            // Use uriBuilder to properly handle baseUrl concatenation
            bodySpec = requestSpec.uri(uriBuilder -> {
                uriBuilder.path(path != null ? path : "");
                if (query != null && !query.isEmpty()) {
                    // Parse and add query parameters from URI
                    String[] pairs = query.split("&");
                    for (String pair : pairs) {
                        int idx = pair.indexOf("=");
                        if (idx > 0) {
                            String key = pair.substring(0, idx);
                            String value = pair.substring(idx + 1);
                            uriBuilder.queryParam(key, value);
                        } else {
                            uriBuilder.queryParam(pair, "");
                        }
                    }
                }
                return uriBuilder.build();
            });
        } else {
            bodySpec = requestSpec.uri("");
        }

        if (!cookies.isEmpty()) {
            String cookieHeader = RequestParameterProcessor.buildCookieHeader(cookies);
            headers.put("Cookie", cookieHeader);
        }

        // Set headers
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            bodySpec.header(entry.getKey(), entry.getValue());
        }

        // Set cookies
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            bodySpec.cookie(entry.getKey(), entry.getValue());
        }

        // Set body - handle multipart or JSON based on Content-Type header
        if (body != null) {
            try {
                // Check if Content-Type is multipart/form-data
                String contentType = headers.get("Content-Type");
                boolean isMultipart = MultipartBodyProcessor.isMultipartRequest(contentType);

                if (isMultipart && body instanceof Map) {
                    Map<?, ?> bodyMap = (Map<?, ?>) body;
                    // Check if it's a multipart body by inspecting the first value
                    if (MultipartBodyProcessor.isMultipartBodyMap(bodyMap)) {
                        // Handle multipart data
                        @SuppressWarnings("unchecked")
                        Map<String, io.github.guoshiqiufeng.dify.core.utils.MultipartBodyBuilder.Part> parts =
                            (Map<String, io.github.guoshiqiufeng.dify.core.utils.MultipartBodyBuilder.Part>) bodyMap;

                        org.springframework.util.LinkedMultiValueMap<String, Object> multipartData =
                            SpringMultipartBodyBuilder.buildMultipartBody(parts, jsonMapper, skipNull);

                        bodySpec.body(org.springframework.web.reactive.function.BodyInserters.fromMultipartData(multipartData));
                        return bodySpec;
                    }
                }

                // Default: serialize to JSON
                bodySpec.contentType(MediaType.APPLICATION_JSON);
                bodySpec.bodyValue(JsonSerializationHelper.serialize(body, jsonMapper, skipNull));
            } catch (Exception e) {
                throw new HttpClientException("Failed to serialize request body", e);
            }
        }

        return bodySpec;
    }
}
