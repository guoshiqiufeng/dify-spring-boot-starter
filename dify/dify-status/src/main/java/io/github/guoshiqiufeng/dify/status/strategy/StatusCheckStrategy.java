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
package io.github.guoshiqiufeng.dify.status.strategy;

import io.github.guoshiqiufeng.dify.status.dto.ApiStatusResult;

/**
 * Status check strategy interface
 *
 * @author yanghq
 * @version 1.7.0
 * @since 2025/12/19 14:00
 */
public interface StatusCheckStrategy {
    /**
     * Check the status of a specific API method
     *
     * @param methodName The name of the method to check
     * @param apiKey     The API key for authentication
     * @return ApiStatusResult containing the check result
     */
    ApiStatusResult checkStatus(String methodName, String apiKey);

    /**
     * Get the client name this strategy is for
     *
     * @return Client name
     */
    String getClientName();
}
