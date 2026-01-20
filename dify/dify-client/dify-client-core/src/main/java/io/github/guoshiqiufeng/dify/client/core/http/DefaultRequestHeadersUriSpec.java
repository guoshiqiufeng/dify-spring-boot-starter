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

import io.github.guoshiqiufeng.dify.client.core.map.MultiValueMap;
import io.github.guoshiqiufeng.dify.client.core.web.client.RequestHeadersUriSpec;
import io.github.guoshiqiufeng.dify.client.core.web.client.ResponseSpec;
import io.github.guoshiqiufeng.dify.client.core.web.util.UriBuilder;
import io.github.guoshiqiufeng.dify.client.core.web.util.UriUtil;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;

/**
 * Default implementation of RequestHeadersUriSpec that delegates to HttpRequestBuilder.
 * This class acts as an adapter between the new fluent API and the existing HttpRequestBuilder.
 * <p>This adapter provides a Spring RestClient-like API while maintaining compatibility
 * with the existing HttpRequestBuilder implementation.
 * <p>This implementation is typically used for HTTP methods that don't have a request body,
 * such as GET, HEAD, DELETE, and OPTIONS.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025-12-30
 */
public class DefaultRequestHeadersUriSpec implements RequestHeadersUriSpec<DefaultRequestHeadersUriSpec> {

    private final HttpRequestBuilder requestBuilder;

    /**
     * Create a new DefaultRequestHeadersUriSpec.
     *
     * @param requestBuilder the underlying request builder to delegate to
     */
    public DefaultRequestHeadersUriSpec(HttpRequestBuilder requestBuilder) {
        if (requestBuilder == null) {
            throw new IllegalArgumentException("HttpRequestBuilder must not be null");
        }
        this.requestBuilder = requestBuilder;
    }

    @Override
    public DefaultRequestHeadersUriSpec uri(URI uri) {
        if (uri == null) {
            throw new IllegalArgumentException("URI must not be null");
        }
        requestBuilder.uri(uri.toString());
        return this;
    }

    @Override
    public DefaultRequestHeadersUriSpec uri(String uri, Object... uriVariables) {
        if (uri == null) {
            throw new IllegalArgumentException("URI must not be null");
        }
        requestBuilder.uri(uri, uriVariables);
        return this;
    }

    @Override
    public DefaultRequestHeadersUriSpec uri(String uri, Map<String, ?> uriVariables) {
        if (uri == null) {
            throw new IllegalArgumentException("URI must not be null");
        }
        if (uriVariables == null || uriVariables.isEmpty()) {
            requestBuilder.uri(uri);
            return this;
        }

        // Extract variable names from URI template and build array in order
        List<Object> orderedValues = new ArrayList<>();
        Matcher matcher = UriUtil.URI_VARIABLE_PATTERN.matcher(uri);

        while (matcher.find()) {
            String variableName = matcher.group(1);
            Object value = uriVariables.get(variableName);
            if (value == null) {
                throw new IllegalArgumentException(
                    "URI variable '" + variableName + "' has no value in the provided map");
            }
            orderedValues.add(value);
        }

        requestBuilder.uri(uri, orderedValues.toArray());
        return this;
    }

    @Override
    public DefaultRequestHeadersUriSpec uri(String uri, Object[] uriVariables, Function<UriBuilder, URI> uriFunction) {
        if (uri == null) {
            throw new IllegalArgumentException("URI must not be null");
        }
        if (uriFunction == null) {
            throw new IllegalArgumentException("URI function must not be null");
        }

        requestBuilder.uri(uriBuilder -> {
            uriBuilder.path(uri);
            if (uriVariables != null && uriVariables.length > 0) {
                uriFunction.apply(uriBuilder);
                uriBuilder.build(uriVariables);
            } else {
                uriFunction.apply(uriBuilder);
            }
        });

        return this;
    }

    @Override
    public DefaultRequestHeadersUriSpec uri(Function<UriBuilder, URI> uriFunction) {
        if (uriFunction == null) {
            throw new IllegalArgumentException("URI function must not be null");
        }

        // Apply the function and let it build the complete URI
        requestBuilder.uri(uriFunction::apply);
        return this;
    }

    @Override
    public DefaultRequestHeadersUriSpec header(String headerName, String... headerValues) {
        if (headerName == null || headerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Header name must not be null or empty");
        }
        if (headerValues != null && headerValues.length > 0) {
            // Add all header values
            for (String headerValue : headerValues) {
                if (headerValue != null) {
                    requestBuilder.header(headerName, headerValue);
                }
            }
        }
        return this;
    }

    @Override
    public DefaultRequestHeadersUriSpec headers(Consumer<HttpHeaders> headersConsumer) {
        if (headersConsumer == null) {
            throw new IllegalArgumentException("Headers consumer must not be null");
        }
        requestBuilder.headers(headersConsumer);
        return this;
    }

    @Override
    public DefaultRequestHeadersUriSpec cookie(String name, String value) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Cookie name must not be null or empty");
        }
        if (value != null) {
            requestBuilder.cookies(cookies -> cookies.add(name, value));
        }
        return this;
    }

    @Override
    public DefaultRequestHeadersUriSpec cookies(Consumer<MultiValueMap<String, String>> cookiesConsumer) {
        if (cookiesConsumer == null) {
            throw new IllegalArgumentException("Cookies consumer must not be null");
        }
        requestBuilder.cookies(cookiesConsumer);
        return this;
    }

    @Override
    public DefaultRequestHeadersUriSpec contentType(String contentType) {
        if (contentType == null || contentType.trim().isEmpty()) {
            throw new IllegalArgumentException("Content type must not be null or empty");
        }
        requestBuilder.contentType(contentType);
        return this;
    }

    @Override
    public ResponseSpec retrieve() {
        return requestBuilder.retrieve();
    }

    /**
     * Get the underlying HttpRequestBuilder.
     * This method is package-private for testing purposes.
     *
     * @return the underlying request builder
     */
    HttpRequestBuilder getRequestBuilder() {
        return requestBuilder;
    }
}
