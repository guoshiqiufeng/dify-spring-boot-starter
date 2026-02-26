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
package io.github.guoshiqiufeng.dify.client.integration.spring.http;

import io.github.guoshiqiufeng.dify.client.core.http.HttpClientException;
import io.github.guoshiqiufeng.dify.client.core.http.ResponseErrorHandler;
import io.github.guoshiqiufeng.dify.client.core.response.ResponseEntity;

import java.util.List;

/**
 * Utility class for handling HTTP response errors.
 * Provides shared error handling logic for both streaming and non-streaming requests.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026-02-25
 */
final class ResponseErrorHandlerUtils {

    private ResponseErrorHandlerUtils() {
    }

    /**
     * Handle errors using registered error handlers.
     *
     * @param errorHandlers list of error handlers to apply
     * @param response      the HTTP response
     */
    static void handleErrors(List<ResponseErrorHandler> errorHandlers, ResponseEntity<?> response) {
        for (ResponseErrorHandler handler : errorHandlers) {
            if (handler.getStatusPredicate().test(response.getStatusCode())) {
                try {
                    handler.handle(response);
                } catch (Exception e) {
                    if (e instanceof RuntimeException) {
                        throw (RuntimeException) e;
                    }
                    throw new HttpClientException("Error handler failed: " + e.getMessage(), e);
                }
            }
        }
    }
}
