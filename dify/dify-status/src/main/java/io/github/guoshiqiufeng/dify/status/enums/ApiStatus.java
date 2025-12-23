/*
 * Copyright (c) 2025-2025, fubluesky (fubluesky@foxmail.com)
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
package io.github.guoshiqiufeng.dify.status.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * API status enum
 *
 * @author yanghq
 * @version 1.7.0
 * @since 2025/12/19 14:00
 */
@AllArgsConstructor
@Getter
public enum ApiStatus {

    /**
     * API is functioning normally
     */
    NORMAL("normal", "API is functioning normally"),

    /**
     * Resource not found (404)
     */
    NOT_FOUND_404("not_found_404", "Resource not found"),

    /**
     * Unauthorized access (401)
     */
    UNAUTHORIZED_401("unauthorized_401", "Unauthorized access"),

    /**
     * API call timeout
     */
    TIMEOUT("timeout", "Request timeout"),

    /**
     * Network error or connection failure
     */
    NETWORK_ERROR("network_error", "Network connection error"),

    /**
     * Server error (5xx)
     */
    SERVER_ERROR("server_error", "Server error"),

    /**
     * Client error (4xx, excluding 401 and 404)
     */
    CLIENT_ERROR("client_error", "Client error"),

    /**
     * Unknown error
     */
    UNKNOWN_ERROR("unknown_error", "Unknown error"),

    /**
     * API not configured or disabled
     */
    NOT_CONFIGURED("not_configured", "API not configured");

    @JsonValue
    private final String code;
    private final String description;

    @JsonCreator
    public static ApiStatus codeOf(String code) {
        for (ApiStatus value : ApiStatus.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return UNKNOWN_ERROR;
    }
}
