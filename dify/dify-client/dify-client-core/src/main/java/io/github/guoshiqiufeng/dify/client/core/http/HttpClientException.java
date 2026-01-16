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

/**
 * Exception thrown when an HTTP request fails.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025-12-26
 */
public class HttpClientException extends RuntimeException {

    private final int statusCode;
    private final String responseBody;

    /**
     * Constructor with message.
     *
     * @param message error message
     */
    public HttpClientException(String message) {
        super(message);
        this.statusCode = -1;
        this.responseBody = null;
    }

    /**
     * Constructor with message and cause.
     *
     * @param message error message
     * @param cause   the cause
     */
    public HttpClientException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = -1;
        this.responseBody = null;
    }

    /**
     * Constructor with status code and response body.
     *
     * @param statusCode   HTTP status code
     * @param responseBody response body
     */
    public HttpClientException(int statusCode, String responseBody) {
        super("HTTP request failed with status " + statusCode + ": " + responseBody);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    /**
     * Constructor with status code, response body, and message.
     *
     * @param message      error message
     * @param statusCode   HTTP status code
     * @param responseBody response body
     */
    public HttpClientException(String message, int statusCode, String responseBody) {
        super(message);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    /**
     * Get the HTTP status code.
     *
     * @return status code, or -1 if not available
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Get the response body.
     *
     * @return response body, or null if not available
     */
    public String getResponseBody() {
        return responseBody;
    }
}
