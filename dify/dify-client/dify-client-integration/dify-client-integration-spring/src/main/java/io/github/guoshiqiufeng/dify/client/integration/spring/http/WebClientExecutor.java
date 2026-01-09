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
import io.github.guoshiqiufeng.dify.client.core.http.HttpClientException;
import io.github.guoshiqiufeng.dify.client.core.http.TypeReference;
import io.github.guoshiqiufeng.dify.client.core.response.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;

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

    /**
     * Constructor.
     *
     * @param webClient  WebClient instance
     * @param jsonMapper JSON mapper
     */
    WebClientExecutor(WebClient webClient, JsonMapper jsonMapper) {
        this.webClient = webClient;
        this.jsonMapper = jsonMapper;
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
    <T> T execute(String method, String uri, Map<String, String> headers,
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
    <T> T execute(String method, String uri, Map<String, String> headers,
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
    <T> HttpResponse<T> executeForEntity(String method, String uri, Map<String, String> headers,
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
            if (statusCode >= 200 && statusCode < 300) {
                return responseConverter.convert(responseEntity, responseType);
            } else {
                // For error responses, return raw error message as body
                // The error handler will receive this and can process it
                @SuppressWarnings("unchecked")
                T errorBody = (T) responseEntity.getBody();
                return HttpResponse.<T>builder()
                        .statusCode(statusCode)
                        .headers(convertHeaders(responseEntity.getHeaders()))
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
                    .headers(convertHeaders(e.getHeaders()))
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
    <T> HttpResponse<T> executeForEntity(String method, String uri, Map<String, String> headers,
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
            if (statusCode >= 200 && statusCode < 300) {
                return responseConverter.convert(responseEntity, typeReference);
            } else {
                // For error responses, return raw error message as body
                // The error handler will receive this and can process it
                @SuppressWarnings("unchecked")
                T errorBody = (T) responseEntity.getBody();
                return HttpResponse.<T>builder()
                        .statusCode(statusCode)
                        .headers(convertHeaders(responseEntity.getHeaders()))
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
                    .headers(convertHeaders(e.getHeaders()))
                    .body(typedErrorBody)
                    .build();
        }
    }

    /**
     * Convert Spring HttpHeaders to our HttpHeaders format.
     */
    private io.github.guoshiqiufeng.dify.client.core.http.HttpHeaders convertHeaders(org.springframework.http.HttpHeaders springHeaders) {
        io.github.guoshiqiufeng.dify.client.core.http.HttpHeaders headers = new io.github.guoshiqiufeng.dify.client.core.http.HttpHeaders();
        if (springHeaders != null) {
            springHeaders.forEach((key, values) -> {
                for (String value : values) {
                    headers.add(key, value);
                }
            });
        }
        return headers;
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
    <T> Flux<T> executeStream(String method, String uri, Map<String, String> headers,
                              Map<String, String> cookies, Map<String, String> queryParams,
                              Object body, Class<T> responseType) {
        WebClient.RequestBodySpec requestSpec = buildRequest(method, uri, headers, cookies, queryParams, body);

        // For SSE streaming, use Spring's ServerSentEvent support
        ParameterizedTypeReference<org.springframework.http.codec.ServerSentEvent<String>> sseType =
                new ParameterizedTypeReference<org.springframework.http.codec.ServerSentEvent<String>>() {};

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
    <T> Flux<T> executeStream(String method, String uri, Map<String, String> headers,
                              Map<String, String> cookies, Map<String, String> queryParams,
                              Object body, TypeReference<T> typeReference) {
        WebClient.RequestBodySpec requestSpec = buildRequest(method, uri, headers, cookies, queryParams, body);

        // For SSE streaming, use Spring's ServerSentEvent support
        ParameterizedTypeReference<org.springframework.http.codec.ServerSentEvent<String>> sseType =
                new ParameterizedTypeReference<org.springframework.http.codec.ServerSentEvent<String>>() {};

        return requestSpec
                .accept(MediaType.TEXT_EVENT_STREAM)
                .retrieve()
                .bodyToFlux(sseType)
                .mapNotNull(sse -> sse.data())
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
    WebClient.RequestBodySpec buildRequest(String method, String uri, Map<String, String> headers,
                                           Map<String, String> cookies, Map<String, String> queryParams,
                                           Object body) {
        // Start request
        WebClient.RequestBodyUriSpec requestSpec = webClient.method(HttpMethod.valueOf(method));

        // Set URI
        WebClient.RequestBodySpec bodySpec;
        if (uri != null) {
            if (queryParams.isEmpty()) {
                bodySpec = requestSpec.uri(uri);
            } else {
                bodySpec = requestSpec.uri(uriBuilder -> {
                    uriBuilder.path(uri);
                    for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                        uriBuilder.queryParam(entry.getKey(), entry.getValue());
                    }
                    return uriBuilder.build();
                });
            }
        } else {
            bodySpec = requestSpec.uri("");
        }

        if (!cookies.isEmpty()) {
            String cookieHeader = cookies.entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining("; "));
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

        // Set body - serialize to JSON string using JsonMapper
        if (body != null) {
            try {
                String jsonBody = jsonMapper.toJson(body);
                bodySpec.contentType(MediaType.APPLICATION_JSON);
                bodySpec.bodyValue(jsonBody);
            } catch (Exception e) {
                throw new HttpClientException("Failed to serialize request body", e);
            }
        }

        return bodySpec;
    }
}
