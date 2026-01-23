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
package io.github.guoshiqiufeng.dify.client.core.web.client;

import io.github.guoshiqiufeng.dify.client.core.http.ResponseErrorHandler;
import io.github.guoshiqiufeng.dify.client.core.http.TypeReference;
import io.github.guoshiqiufeng.dify.client.core.response.ResponseEntity;
import reactor.core.publisher.Flux;

/**
 * Response specification for handling HTTP responses with fluent API.
 * This interface provides a framework-agnostic way to handle HTTP responses.
 * <p>Inspired by Spring RestClient's ResponseSpec interface.
 *
 * <p>Example usage:
 * <pre class="code">
 * // Extract body directly
 * User user = httpClient.get()
 *     .uri("/users/{id}", userId)
 *     .retrieve()
 *     .body(User.class);
 *
 * // Get full response entity
 * HttpResponse&lt;User&gt; response = httpClient.get()
 *     .uri("/users/{id}", userId)
 *     .retrieve()
 *     .toEntity(User.class);
 *
 * // Handle errors
 * User user = httpClient.get()
 *     .uri("/users/{id}", userId)
 *     .retrieve()
 *     .onStatus(errorHandler)
 *     .body(User.class);
 * </pre>
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025-12-29
 */
public interface ResponseSpec {

    /**
     * Register a custom error handler for specific status codes.
     * The error handler will be invoked when the response status matches the predicate.
     * <p>By default, if there are no matching status handlers, responses with
     * status codes &gt;= 400 will throw an exception.
     * <p>Example:
     * <pre class="code">
     * User user = httpClient.get()
     *     .uri("/users/{id}", userId)
     *     .retrieve()
     *     .onStatus(errorHandler)
     *     .body(User.class);
     * </pre>
     *
     * @param errorHandler the error handler to apply
     * @return this ResponseSpec for method chaining
     */
    ResponseSpec onStatus(ResponseErrorHandler errorHandler);

    /**
     * Extract the response body as an object of the given type.
     * <p>Example:
     * <pre class="code">
     * User user = httpClient.get()
     *     .uri("/users/{id}", userId)
     *     .retrieve()
     *     .body(User.class);
     * </pre>
     *
     * @param responseType the expected response type
     * @param <T>          the response type
     * @return the response body, or {@code null} if no response body was available
     */
    <T> T body(Class<T> responseType);

    /**
     * Extract the response body with generic type support.
     * <p>Example:
     * <pre class="code">
     * List&lt;User&gt; users = httpClient.get()
     *     .uri("/users")
     *     .retrieve()
     *     .body(new TypeReference&lt;List&lt;User&gt;&gt;() {});
     * </pre>
     *
     * @param typeReference the type reference for generic types
     * @param <T>           the response type
     * @return the response body, or {@code null} if no response body was available
     */
    <T> T body(TypeReference<T> typeReference);

    /**
     * Return an {@code HttpResponse} with the body decoded to an Object of
     * the given type.
     * <p>This method provides access to the full HTTP response including
     * status code, headers, and body.
     * <p>Example:
     * <pre class="code">
     * HttpResponse&lt;User&gt; response = httpClient.get()
     *     .uri("/users/{id}", userId)
     *     .retrieve()
     *     .toEntity(User.class);
     *
     * int statusCode = response.getStatusCode();
     * Map&lt;String, List&lt;String&gt;&gt; headers = response.getHeaders();
     * User user = response.getBody();
     * </pre>
     *
     * @param responseType the expected response body type
     * @param <T>          the response body type
     * @return the HTTP response with status, headers, and body
     */
    <T> ResponseEntity<T> toEntity(Class<T> responseType);

    /**
     * Return an {@code HttpResponse} with the body decoded to an Object of
     * the given type with generic type support.
     * <p>Example:
     * <pre class="code">
     * HttpResponse&lt;List&lt;User&gt;&gt; response = httpClient.get()
     *     .uri("/users")
     *     .retrieve()
     *     .toEntity(new TypeReference&lt;List&lt;User&gt;&gt;() {});
     * </pre>
     *
     * @param typeReference the type reference for generic types
     * @param <T>           the response body type
     * @return the HTTP response with status, headers, and body
     */
    <T> ResponseEntity<T> toEntity(TypeReference<T> typeReference);

    /**
     * Return an {@code HttpResponse} without a body.
     * This is useful for DELETE, PUT, or POST requests where you only care about
     * the status code and headers, not the response body.
     * <p>Example:
     * <pre class="code">
     * HttpResponse&lt;Void&gt; response = httpClient.delete()
     *     .uri("/users/{id}", userId)
     *     .retrieve()
     *     .toBodilessEntity();
     *
     * if (response.getStatusCode() == 204) {
     *     // User deleted successfully
     * }
     * </pre>
     *
     * @return the HTTP response with status and headers, but no body
     */
    ResponseEntity<Void> toBodilessEntity();

    /**
     * Extract the response body as a Flux stream.
     * This is useful for streaming responses like Server-Sent Events.
     * <p>Example:
     * <pre class="code">
     * Flux&lt;Event&gt; events = httpClient.get()
     *     .uri("/events")
     *     .retrieve()
     *     .bodyToFlux(Event.class);
     *
     * events.subscribe(event -&gt; {
     *     // Process each event
     * });
     * </pre>
     *
     * @param responseType the expected response type for each stream item
     * @param <T>          the response type
     * @return a Flux that emits stream items
     */
    <T> Flux<T> bodyToFlux(Class<T> responseType);

    /**
     * Extract the response body as a Flux stream with generic type support.
     * <p>Example:
     * <pre class="code">
     * Flux&lt;Message&lt;String&gt;&gt; messages = httpClient.get()
     *     .uri("/messages")
     *     .retrieve()
     *     .bodyToFlux(new TypeReference&lt;Message&lt;String&gt;&gt;() {});
     * </pre>
     *
     * @param typeReference the type reference for generic types
     * @param <T>           the response type
     * @return a Flux that emits stream items
     */
    <T> Flux<T> bodyToFlux(TypeReference<T> typeReference);
}
