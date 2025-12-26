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
package io.github.guoshiqiufeng.dify.client.core.response;

import io.github.guoshiqiufeng.dify.client.core.http.HttpHeaders;
import lombok.Getter;

import java.util.*;

/**
 * HTTP response wrapper that encapsulates status code, headers, and body.
 * This is a framework-agnostic alternative to Spring's ResponseEntity.
 *
 * @param <T> the type of the response body
 * @author yanghq
 * @version 2.0.0
 * @since 2025-12-26
 */
public class HttpResponse<T> {

    /**
     * -- GETTER --
     *  Get the HTTP status code.
     *
     * @return status code
     */
    @Getter
    private final int statusCode;
    private final HttpHeaders headers;
    /**
     * -- GETTER --
     *  Get the response body.
     *
     * @return response body
     */
    @Getter
    private final T body;

    /**
     * Constructor for HttpResponse.
     *
     * @param statusCode HTTP status code
     * @param headers    HTTP headers
     * @param body       response body
     */
    public HttpResponse(int statusCode, Map<String, List<String>> headers, T body) {
        this.statusCode = statusCode;
        this.headers = new HttpHeaders(headers);
        this.body = body;
    }

    /**
     * Constructor for HttpResponse with HttpHeaders.
     *
     * @param statusCode HTTP status code
     * @param headers    HTTP headers
     * @param body       response body
     */
    public HttpResponse(int statusCode, HttpHeaders headers, T body) {
        this.statusCode = statusCode;
        this.headers = headers != null ? headers : new HttpHeaders();
        this.body = body;
    }

    /**
     * Get all HTTP headers.
     *
     * @return HttpHeaders instance
     */
    public HttpHeaders getHeaders() {
        return headers;
    }

    /**
     * Get a specific header value.
     *
     * @param name header name (case-insensitive)
     * @return list of header values, or empty list if not found
     */
    public List<String> getHeader(String name) {
        return headers.getOrEmpty(name);
    }

    /**
     * Get a specific header value or empty list if not found.
     * This is an alias for getHeader() for better API compatibility.
     *
     * @param name header name (case-insensitive)
     * @return list of header values, or empty list if not found
     */
    public List<String> getOrEmpty(String name) {
        return headers.getOrEmpty(name);
    }

    /**
     * Get the first value of a specific header.
     *
     * @param name header name (case-insensitive)
     * @return first header value, or null if not found
     */
    public String getFirstHeader(String name) {
        return headers.getFirst(name);
    }

    /**
     * Check if the response is successful (2xx status code).
     *
     * @return true if status code is 2xx
     */
    public boolean isSuccessful() {
        return statusCode >= 200 && statusCode < 300;
    }

    /**
     * Create a builder for HttpResponse.
     *
     * @param <T> the type of the response body
     * @return a new builder instance
     */
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    /**
     * Builder for HttpResponse.
     *
     * @param <T> the type of the response body
     */
    public static class Builder<T> {
        private int statusCode = 200;
        private HttpHeaders headers = new HttpHeaders();
        private T body;

        /**
         * Set the status code.
         *
         * @param statusCode HTTP status code
         * @return this builder
         */
        public Builder<T> statusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        /**
         * Set all headers.
         *
         * @param headers map of headers
         * @return this builder
         */
        public Builder<T> headers(Map<String, List<String>> headers) {
            this.headers = new HttpHeaders(headers);
            return this;
        }

        /**
         * Set all headers.
         *
         * @param headers HttpHeaders instance
         * @return this builder
         */
        public Builder<T> headers(HttpHeaders headers) {
            this.headers = headers;
            return this;
        }

        /**
         * Add a header.
         *
         * @param name  header name
         * @param value header value
         * @return this builder
         */
        public Builder<T> header(String name, String value) {
            this.headers.add(name, value);
            return this;
        }

        /**
         * Add multiple values for a header.
         *
         * @param name   header name
         * @param values header values
         * @return this builder
         */
        public Builder<T> header(String name, List<String> values) {
            this.headers.addAll(name, values);
            return this;
        }

        /**
         * Set the response body.
         *
         * @param body response body
         * @return this builder
         */
        public Builder<T> body(T body) {
            this.body = body;
            return this;
        }

        /**
         * Build the HttpResponse.
         *
         * @return a new HttpResponse instance
         */
        public HttpResponse<T> build() {
            return new HttpResponse<>(statusCode, headers, body);
        }
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "statusCode=" + statusCode +
                ", headers=" + headers +
                ", body=" + body +
                '}';
    }
}
