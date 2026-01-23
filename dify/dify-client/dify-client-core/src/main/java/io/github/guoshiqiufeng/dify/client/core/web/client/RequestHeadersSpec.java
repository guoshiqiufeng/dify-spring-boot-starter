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

import io.github.guoshiqiufeng.dify.client.core.http.HttpHeaders;
import io.github.guoshiqiufeng.dify.client.core.util.MultiValueMap;

import java.util.function.Consumer;

/**
 * Contract for specifying request headers leading up to the exchange.
 * Inspired by Spring RestClient's RequestHeadersSpec interface.
 *
 * @param <S> a self reference to the spec type
 * @author yanghq
 * @version 2.0.0
 * @since 2025-12-30
 */
public interface RequestHeadersSpec<S extends RequestHeadersSpec<S>> {

    /**
     * Add the given, single header value under the given name.
     * <p>For example:
     * <pre class="code">
     * client.get()
     *     .uri("/users")
     *     .header("Authorization", "Bearer token")
     *     .retrieve()
     *     .body(User[].class);
     * </pre>
     *
     * @param headerName   the header name
     * @param headerValues the header value(s)
     * @return this builder
     */
    S header(String headerName, String... headerValues);

    /**
     * Provides access to every header declared so far with the possibility
     * to add, replace, or remove values.
     * <p>For example:
     * <pre class="code">
     * client.get()
     *     .uri("/users")
     *     .headers(headers -&gt; {
     *         headers.add("Authorization", "Bearer token");
     *         headers.add("Accept", "application/json");
     *     })
     *     .retrieve()
     *     .body(User[].class);
     * </pre>
     *
     * @param headersConsumer the consumer to provide access to
     * @return this builder
     */
    S headers(Consumer<HttpHeaders> headersConsumer);

    /**
     * Add a cookie with the given name and value.
     * <p>For example:
     * <pre class="code">
     * client.get()
     *     .uri("/users")
     *     .cookie("sessionId", "abc123")
     *     .retrieve()
     *     .body(User[].class);
     * </pre>
     *
     * @param name  the cookie name
     * @param value the cookie value
     * @return this builder
     */
    S cookie(String name, String value);

    /**
     * Provides access to every cookie declared so far with the possibility
     * to add, replace, or remove values.
     * <p>For example:
     * <pre class="code">
     * client.get()
     *     .uri("/users")
     *     .cookies(cookies -&gt; {
     *         cookies.add("sessionId", "abc123");
     *         cookies.add("userId", "user456");
     *     })
     *     .retrieve()
     *     .body(User[].class);
     * </pre>
     *
     * @param cookiesConsumer the consumer to provide access to
     * @return this builder
     */
    S cookies(Consumer<MultiValueMap<String, String>> cookiesConsumer);

    /**
     * Set the Content-Type header for the request.
     * <p>For example:
     * <pre class="code">
     * client.post()
     *     .uri("/users")
     *     .contentType("application/json")
     *     .body(user)
     *     .retrieve()
     *     .body(User.class);
     * </pre>
     *
     * @param contentType the content type (e.g., "application/json")
     * @return this builder
     */
    S contentType(String contentType);

    /**
     * Enter the retrieve workflow and use the returned {@link ResponseSpec}
     * to select from a number of built-in options to extract the response.
     * <p>For example:
     * <pre class="code">
     * User user = client.get()
     *     .uri("/users/{id}", userId)
     *     .retrieve()
     *     .body(User.class);
     * </pre>
     * <p>Or to get the full response entity:
     * <pre class="code">
     * HttpResponse&lt;User&gt; response = client.get()
     *     .uri("/users/{id}", userId)
     *     .retrieve()
     *     .toEntity(User.class);
     * </pre>
     * <p>Note that this method does not actually execute the request until you
     * call one of the methods on the returned {@link ResponseSpec}.
     *
     * @return {@code ResponseSpec} to specify how to decode the body
     */
    ResponseSpec retrieve();
}
