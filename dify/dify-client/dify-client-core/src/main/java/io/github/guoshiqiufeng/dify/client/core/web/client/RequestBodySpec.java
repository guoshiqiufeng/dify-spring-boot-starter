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

import io.github.guoshiqiufeng.dify.client.core.http.TypeReference;

/**
 * Contract for specifying request headers and body leading up to the exchange.
 * Inspired by Spring RestClient's RequestBodySpec interface.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025-12-30
 */
public interface RequestBodySpec extends RequestHeadersSpec<RequestBodySpec> {

    /**
     * Set the length of the body in bytes, as specified by the
     * {@code Content-Length} header.
     * <p>For example:
     * <pre class="code">
     * client.post()
     *     .uri("/users")
     *     .contentLength(1024)
     *     .body(data)
     *     .retrieve()
     *     .body(User.class);
     * </pre>
     *
     * @param contentLength the content length
     * @return this builder
     */
    RequestBodySpec contentLength(long contentLength);

    /**
     * Set the body of the request to the given {@code Object}.
     * <p>For example:
     * <pre class="code">
     * User user = new User("John", "Doe");
     * User created = client.post()
     *     .uri("/users")
     *     .contentType("application/json")
     *     .body(user)
     *     .retrieve()
     *     .body(User.class);
     * </pre>
     *
     * @param body the body of the request
     * @return this builder
     */
    RequestBodySpec body(Object body);

    /**
     * Set the body of the request to the given {@code Object}.
     * The parameter {@code bodyType} is used to capture the generic type.
     * <p>For example:
     * <pre class="code">
     * List&lt;User&gt; users = Arrays.asList(user1, user2);
     * List&lt;User&gt; created = client.post()
     *     .uri("/users/batch")
     *     .contentType("application/json")
     *     .body(users, new TypeReference&lt;List&lt;User&gt;&gt;() {})
     *     .retrieve()
     *     .body(new TypeReference&lt;List&lt;User&gt;&gt;() {});
     * </pre>
     *
     * @param body     the body of the request
     * @param bodyType the type of the body, used to capture the generic type
     * @param <T>      the body type
     * @return this builder
     */
    <T> RequestBodySpec body(T body, TypeReference<T> bodyType);
}
