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
package io.github.guoshiqiufeng.dify.client.core.http.util;

import lombok.experimental.UtilityClass;

/**
 * Utility class for validating HTTP status codes.
 * Provides methods to check if a status code represents success, client error, or server error.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026-01-20
 */
@UtilityClass
public class HttpStatusValidator {

    /**
     * Check if the status code represents a successful response (2xx).
     *
     * @param statusCode HTTP status code
     * @return true if status code is between 200 and 299 (inclusive)
     */
    public static boolean isSuccessful(int statusCode) {
        return statusCode >= 200 && statusCode < 300;
    }

    /**
     * Check if the status code represents a client error (4xx).
     *
     * @param statusCode HTTP status code
     * @return true if status code is between 400 and 499 (inclusive)
     */
    public static boolean isClientError(int statusCode) {
        return statusCode >= 400 && statusCode < 500;
    }

    /**
     * Check if the status code represents a server error (5xx).
     *
     * @param statusCode HTTP status code
     * @return true if status code is between 500 and 599 (inclusive)
     */
    public static boolean isServerError(int statusCode) {
        return statusCode >= 500 && statusCode < 600;
    }

    /**
     * Check if the status code represents any error (4xx or 5xx).
     *
     * @param statusCode HTTP status code
     * @return true if status code is between 400 and 599 (inclusive)
     */
    public static boolean isError(int statusCode) {
        return statusCode >= 400 && statusCode < 600;
    }
}
