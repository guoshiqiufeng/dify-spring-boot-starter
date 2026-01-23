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

import io.github.guoshiqiufeng.dify.client.core.http.HttpMethod;

/**
 * HTTP client interface for making HTTP requests.
 * This interface provides a framework-agnostic way to create HTTP request builders.
 * <p>Inspired by Spring RestClient's design, this interface provides a fluent API
 * for building and executing HTTP requests with proper type safety.
 *
 * <p>Example usage:
 * <pre class="code">
 * // GET request
 * User user = httpClient.get()
 *     .uri("/users/{id}", userId)
 *     .header("Authorization", "Bearer token")
 *     .retrieve()
 *     .body(User.class);
 *
 * // POST request
 * User created = httpClient.post()
 *     .uri("/users")
 *     .contentType("application/json")
 *     .body(newUser)
 *     .retrieve()
 *     .body(User.class);
 * </pre>
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025-12-26
 */
public interface HttpClient {

    /**
     * Start building an HTTP GET request.
     * <p>GET requests typically do not have a request body, so this method
     * returns a {@link RequestHeadersUriSpec} that allows specifying URI and headers.
     *
     * @return a spec for specifying the target URL and headers
     */
    RequestHeadersUriSpec<?> get();

    /**
     * Start building an HTTP POST request.
     * <p>POST requests typically have a request body, so this method
     * returns a {@link RequestBodyUriSpec} that allows specifying URI, headers, and body.
     *
     * @return a spec for specifying the target URL, headers, and body
     */
    RequestBodyUriSpec post();

    /**
     * Start building an HTTP PUT request.
     * <p>PUT requests typically have a request body, so this method
     * returns a {@link RequestBodyUriSpec} that allows specifying URI, headers, and body.
     *
     * @return a spec for specifying the target URL, headers, and body
     */
    RequestBodyUriSpec put();

    /**
     * Start building an HTTP DELETE request.
     * <p>DELETE requests typically do not have a request body, so this method
     * returns a {@link RequestHeadersUriSpec} that allows specifying URI and headers.
     *
     * @return a spec for specifying the target URL and headers
     */
    RequestHeadersUriSpec<?> delete();

    /**
     * Start building an HTTP PATCH request.
     * <p>PATCH requests typically have a request body, so this method
     * returns a {@link RequestBodyUriSpec} that allows specifying URI, headers, and body.
     *
     * @return a spec for specifying the target URL, headers, and body
     */
    RequestBodyUriSpec patch();

    /**
     * Start building an HTTP HEAD request.
     * <p>HEAD requests do not have a request body, so this method
     * returns a {@link RequestHeadersUriSpec} that allows specifying URI and headers.
     *
     * @return a spec for specifying the target URL and headers
     */
    RequestHeadersUriSpec<?> head();

    /**
     * Start building an HTTP OPTIONS request.
     * <p>OPTIONS requests typically do not have a request body, so this method
     * returns a {@link RequestHeadersUriSpec} that allows specifying URI and headers.
     *
     * @return a spec for specifying the target URL and headers
     */
    RequestHeadersUriSpec<?> options();

    /**
     * Start building a request for the specified HTTP method.
     * <p>This is a generic method that can be used for any HTTP method.
     * It returns a {@link RequestBodyUriSpec} to support methods that may have a body.
     *
     * @param httpMethod the HTTP method to use
     * @return a spec for specifying the target URL, headers, and optionally body
     */
    RequestBodyUriSpec method(HttpMethod httpMethod);
}
