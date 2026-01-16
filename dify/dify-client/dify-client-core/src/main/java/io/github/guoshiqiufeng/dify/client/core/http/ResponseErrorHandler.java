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

import io.github.guoshiqiufeng.dify.client.core.response.HttpResponse;

import java.io.IOException;
import java.net.URI;
import java.util.function.Predicate;

/**
 * Error handler for HTTP responses.
 * Allows custom error handling based on status codes.
 * Supports both simple and Spring-style error handling.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025-12-29
 */
public interface ResponseErrorHandler {

    /**
     * Get the status code predicate that determines when this handler should be invoked.
     *
     * @return predicate for status codes
     */
    Predicate<Integer> getStatusPredicate();

    /**
     * Handle the error response.
     *
     * @param response the HTTP response
     * @throws Exception if error handling fails
     */
    void handle(HttpResponse<?> response) throws Exception;

    /**
     * Create an error handler for 4xx status codes.
     *
     * @param handler the handler function
     * @return a new ResponseErrorHandler
     */
    static ResponseErrorHandler on4xxStatus(ErrorHandler handler) {
        return new ResponseErrorHandler() {
            @Override
            public Predicate<Integer> getStatusPredicate() {
                return status -> status >= 400 && status < 500;
            }

            @Override
            public void handle(HttpResponse<?> response) throws Exception {
                handler.handle(response);
            }
        };
    }

    /**
     * Create an error handler for 5xx status codes.
     *
     * @param handler the handler function
     * @return a new ResponseErrorHandler
     */
    static ResponseErrorHandler on5xxStatus(ErrorHandler handler) {
        return new ResponseErrorHandler() {
            @Override
            public Predicate<Integer> getStatusPredicate() {
                return status -> status >= 500 && status < 600;
            }

            @Override
            public void handle(HttpResponse<?> response) throws Exception {
                handler.handle(response);
            }
        };
    }

    /**
     * Create an error handler for specific status codes.
     *
     * @param statusPredicate predicate to match status codes
     * @param handler         the handler function
     * @return a new ResponseErrorHandler
     */
    static ResponseErrorHandler onStatus(Predicate<Integer> statusPredicate, ErrorHandler handler) {
        return new ResponseErrorHandler() {
            @Override
            public Predicate<Integer> getStatusPredicate() {
                return statusPredicate;
            }

            @Override
            public void handle(HttpResponse<?> response) throws Exception {
                handler.handle(response);
            }
        };
    }

    /**
     * Functional interface for error handling.
     */
    @FunctionalInterface
    interface ErrorHandler {
        /**
         * Handle the error response.
         *
         * @param response the HTTP response
         * @throws Exception if error handling fails
         */
        void handle(HttpResponse<?> response) throws Exception;
    }
}
