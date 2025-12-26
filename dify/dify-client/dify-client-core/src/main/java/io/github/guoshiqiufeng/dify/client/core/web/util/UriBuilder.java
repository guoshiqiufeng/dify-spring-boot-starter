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
package io.github.guoshiqiufeng.dify.client.core.web.util;

import java.util.Optional;

/**
 * URI builder with fluent API for constructing URIs with path and query parameters.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025-12-29
 */
public interface UriBuilder {

    /**
     * Set the URI path.
     *
     * @param path the URI path (relative to base URL)
     * @return this builder
     */
    UriBuilder path(String path);

    /**
     * Add a query parameter to the URI.
     *
     * @param name  parameter name
     * @param value parameter value
     * @return this builder
     */
    UriBuilder queryParam(String name, Object value);

    /**
     * Add a query parameter only if the Optional value is present.
     *
     * @param name  parameter name
     * @param value optional parameter value
     * @return this builder
     */
    UriBuilder queryParamIfPresent(String name, Optional<?> value);

    /**
     * Build the URI string with path and query parameters.
     *
     * @return the constructed URI string
     */
    String build();

    /**
     * Build the URI string with path variables and query parameters.
     * Path variables are replaced in the order they appear in the path template.
     * For example, path "/users/{userId}/posts/{postId}" with uriVariables ["123", "456"]
     * will result in "/users/123/posts/456".
     *
     * @param uriVariables the values to replace path variables (in order)
     * @return the constructed URI string
     */
    String build(Object... uriVariables);
}
