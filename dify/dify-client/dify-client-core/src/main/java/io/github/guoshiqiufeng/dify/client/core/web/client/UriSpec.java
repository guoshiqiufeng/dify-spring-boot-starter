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

import io.github.guoshiqiufeng.dify.client.core.web.util.UriBuilder;

import java.util.Map;
import java.util.function.Function;

/**
 * Contract for specifying the URI for a request.
 * Inspired by Spring RestClient's UriSpec interface.
 *
 * @param <S> a self reference to the spec type
 * @author yanghq
 * @version 2.0.0
 * @since 2025-12-30
 */
public interface UriSpec<S extends RequestHeadersSpec<S>> {

    /**
     * Specify the URI for the request using a URI template and URI variables.
     * <p>For example:
     * <pre class="code">
     * client.get()
     *     .uri("/users/{id}", userId)
     *     .retrieve()
     *     .body(User.class);
     * </pre>
     *
     * @param uri          the URI path with placeholders (e.g., "/users/{id}")
     * @param uriVariables path variable values
     * @return the next stage of the request specification
     */
    S uri(String uri, Object... uriVariables);

    /**
     * Specify the URI for the request using a URI template and a map of URI variables.
     * <p>For example:
     * <pre class="code">
     * Map&lt;String, Object&gt; vars = Map.of("id", userId);
     * client.get()
     *     .uri("/users/{id}", vars)
     *     .retrieve()
     *     .body(User.class);
     * </pre>
     *
     * @param uri          the URI path with placeholders
     * @param uriVariables map of variable names to values
     * @return the next stage of the request specification
     */
    S uri(String uri, Map<String, ?> uriVariables);

    /**
     * Specify the URI starting with a URI template and finishing off with a
     * {@link UriBuilder} created from the template.
     * <p>This is useful for building URIs with conditional query parameters:
     * <pre class="code">
     * client.get()
     *     .uri("/users/{id}", userId, uriBuilder -&gt; {
     *         if (includeDetails) {
     *             uriBuilder.queryParam("details", "true");
     *         }
     *         return uriBuilder.build();
     *     })
     *     .retrieve()
     *     .body(User.class);
     * </pre>
     *
     * @param uri         the URI path with placeholders
     * @param uriVariables path variable values
     * @param uriFunction function to further customize the URI
     * @return the next stage of the request specification
     */
    S uri(String uri, Object[] uriVariables, Function<UriBuilder, String> uriFunction);

    /**
     * Specify the URI by through a {@link UriBuilder}.
     * <p>This is the most flexible option for building URIs:
     * <pre class="code">
     * client.get()
     *     .uri(uriBuilder -&gt; uriBuilder
     *         .path("/users/{id}")
     *         .queryParam("details", "true")
     *         .build(userId))
     *     .retrieve()
     *     .body(User.class);
     * </pre>
     *
     * @param uriFunction function to build the URI
     * @return the next stage of the request specification
     */
    S uri(Function<UriBuilder, String> uriFunction);
}
