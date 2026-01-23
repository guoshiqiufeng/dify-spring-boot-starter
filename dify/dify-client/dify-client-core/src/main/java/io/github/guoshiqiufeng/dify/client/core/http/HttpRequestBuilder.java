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
package io.github.guoshiqiufeng.dify.client.core.http;

import io.github.guoshiqiufeng.dify.client.core.response.ResponseEntity;
import io.github.guoshiqiufeng.dify.client.core.util.MultiValueMap;
import io.github.guoshiqiufeng.dify.client.core.web.client.ResponseSpec;
import io.github.guoshiqiufeng.dify.client.core.web.util.UriBuilder;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.function.Consumer;

/**
 * HTTP request builder with fluent API for constructing and executing HTTP requests.
 * This interface provides a framework-agnostic way to build HTTP requests.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025-12-26
 */
public interface HttpRequestBuilder {

    /**
     * Set the request URI path.
     *
     * @param uri the URI path (relative to base URL)
     * @return this builder
     */
    HttpRequestBuilder uri(String uri);

    /**
     * Set the request URI with path variables.
     *
     * @param uri       the URI path with placeholders (e.g., "/users/{id}")
     * @param uriParams path variable values
     * @return this builder
     */
    HttpRequestBuilder uri(String uri, Object... uriParams);

    /**
     * Build the request URI using a UriBuilder consumer.
     * This allows for fluent construction of URIs with conditional query parameters.
     *
     * @param uriBuilderConsumer a function that configures the UriBuilder
     * @return this builder
     */
    HttpRequestBuilder uri(Consumer<UriBuilder> uriBuilderConsumer);

    /**
     * Manipulate request headers with the given consumer.
     * The headers provided to the consumer are mutable.
     *
     * @param headersConsumer a function that consumes the headers
     * @return this builder
     */
    HttpRequestBuilder headers(Consumer<HttpHeaders> headersConsumer);

    /**
     * Manipulate request cookies with the given consumer.
     * The cookies provided to the consumer are mutable.
     *
     * @param cookiesConsumer a function that consumes the cookies
     * @return this builder
     */
    HttpRequestBuilder cookies(Consumer<MultiValueMap<String, String>> cookiesConsumer);

    /**
     * Add a header to the request.
     *
     * @param name  header name
     * @param value header value
     * @return this builder
     */
    HttpRequestBuilder header(String name, String value);

    /**
     * Add multiple headers to the request.
     *
     * @param headers map of header names to values
     * @return this builder
     */
    HttpRequestBuilder headers(Map<String, String> headers);

    /**
     * Set the Content-Type header for the request.
     *
     * @param contentType the content type (e.g., "application/json")
     * @return this builder
     */
    default HttpRequestBuilder contentType(String contentType) {
        return header("Content-Type", contentType);
    }

    /**
     * Set the Bearer Auth header for the request.
     *
     * @param token token
     * @return this builder
     */
    default HttpRequestBuilder setBearerAuth(String token) {
        if (token == null || token.isEmpty()) {
            return this;
        }
        return header(HttpHeaders.AUTHORIZATION, HttpHeaders.AUTHORIZATION_BEARER_KEY + token);
    }

    /**
     * Add a query parameter to the request.
     *
     * @param name  parameter name
     * @param value parameter value
     * @return this builder
     */
    HttpRequestBuilder queryParam(String name, String value);

    /**
     * Add multiple query parameters to the request.
     *
     * @param params map of parameter names to values
     * @return this builder
     */
    HttpRequestBuilder queryParams(Map<String, String> params);

    /**
     * Set the request body (will be serialized to JSON).
     *
     * @param body the request body object
     * @return this builder
     */
    HttpRequestBuilder body(Object body);

    /**
     * Set multipart form data for file uploads.
     *
     * @param formData map of form field names to values
     * @return this builder
     */
    HttpRequestBuilder multipart(Map<String, Object> formData);

    /**
     * Execute the request and return the response body.
     *
     * @param responseType the expected response type
     * @param <T>          the response type
     * @return the response body
     */
    <T> T execute(Class<T> responseType);

    /**
     * Execute the request and return the response body with generic type support.
     *
     * @param typeReference the type reference for generic types
     * @param <T>           the response type
     * @return the response body
     */
    <T> T execute(TypeReference<T> typeReference);

    /**
     * Execute the request and return the full HTTP response.
     *
     * @param responseType the expected response body type
     * @param <T>          the response body type
     * @return the HTTP response with status, headers, and body
     */
    <T> ResponseEntity<T> executeForResponse(Class<T> responseType);

    /**
     * Execute the request and return the full HTTP response with generic type support.
     *
     * @param typeReference the type reference for generic types
     * @param <T>           the response body type
     * @return the HTTP response with status, headers, and body
     */
    <T> ResponseEntity<T> executeForResponse(TypeReference<T> typeReference);

    /**
     * Execute the request as a stream (for Server-Sent Events or streaming responses).
     *
     * @param responseType the expected item type in the stream
     * @param <T>          the item type
     * @return a Flux that emits stream items
     */
    <T> Flux<T> stream(Class<T> responseType);

    /**
     * Execute the request without expecting a response body.
     *
     * @return the HTTP status code
     */
    int executeForStatus();

    /**
     * Proceed to the response handling stage.
     * This method returns a ResponseSpec that allows for fluent configuration
     * of response handling, including error handlers and response extraction.
     *
     * @return a ResponseSpec for handling the response
     */
    ResponseSpec retrieve();
}
