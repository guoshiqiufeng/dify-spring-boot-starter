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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Executor for Spring WebClient.
 * Handles request execution using WebClient API.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025-12-31
 */
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

        Mono<ResponseEntity<String>> responseMono = requestSpec
                .retrieve()
                .toEntity(String.class);

        ResponseEntity<String> responseEntity = responseMono.block();

        if (responseEntity == null) {
            throw new HttpClientException("Response entity is null");
        }

        return responseConverter.convert(responseEntity, responseType);
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

        Mono<ResponseEntity<String>> responseMono = requestSpec
                .retrieve()
                .toEntity(String.class);

        ResponseEntity<String> responseEntity = responseMono.block();

        if (responseEntity == null) {
            throw new HttpClientException("Response entity is null");
        }

        return responseConverter.convert(responseEntity, typeReference);
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

        return requestSpec
                .retrieve()
                .bodyToFlux(responseType);
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
        ParameterizedTypeReference<T> springTypeRef = ParameterizedTypeReference.forType(typeReference.getType());

        return requestSpec
                .retrieve()
                .bodyToFlux(springTypeRef);
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
