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
package io.github.guoshiqiufeng.dify.server.client;

import io.github.guoshiqiufeng.dify.server.dto.request.AppsRequest;
import io.github.guoshiqiufeng.dify.server.dto.response.ApiKeyResponse;
import io.github.guoshiqiufeng.dify.server.dto.response.AppsResponse;
import io.github.guoshiqiufeng.dify.server.dto.response.AppsResponseResult;
import io.github.guoshiqiufeng.dify.server.dto.response.DatasetApiKeyResponse;
import io.github.guoshiqiufeng.dify.server.dto.response.LoginResponse;

import java.util.List;

/**
 * Dify Server Client Interface
 * Provides methods to interact with Dify's server API for managing applications,
 * API keys, authentication, and related operations.
 *
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/9 10:14
 */
public interface DifyServerClient {

    /**
     * Retrieves a list of applications based on mode and name filter
     *
     * @param mode The application mode to filter by (e.g., "completion", "chat")
     * @param name The application name to search for
     * @return List of application responses matching the criteria
     */
    List<AppsResponse> apps(String mode, String name);

    /**
     * Retrieves paginated list of applications based on request parameters
     *
     * @param appsRequest Application query request containing pagination parameters and filter conditions
     *                    Should include mode, name, isCreatedByMe and other filtering conditions, as well as page, limit for pagination
     * @return Paginated application list result encapsulated in {@link AppsResponseResult} object
     *         Contains current page data, total count, page number, page limit and other pagination information
     * @since 1.5.0
     */
    AppsResponseResult apps(AppsRequest appsRequest);

    /**
     * Retrieves detailed information about a specific application
     *
     * @param appId The ID of the application to retrieve
     * @return The application response containing detailed information
     */
    AppsResponse app(String appId);

    /**
     * Retrieves existing API keys for a specific application
     *
     * @param appId The ID of the application to get API keys for
     * @return List of API key responses associated with the application
     */
    List<ApiKeyResponse> getAppApiKey(String appId);

    /**
     * Initializes or regenerates API keys for a specific application
     *
     * @param appId The ID of the application to initialize API keys for
     * @return List of newly generated API key responses
     */
    List<ApiKeyResponse> initAppApiKey(String appId);

    /**
     * Retrieves existing API keys for datasets
     *
     * @return List of dataset API key responses
     */
    List<DatasetApiKeyResponse> getDatasetApiKey();

    /**
     * Initializes or regenerates API keys for datasets
     *
     * @return List of newly generated dataset API key responses
     */
    List<DatasetApiKeyResponse> initDatasetApiKey();

    /**
     * Authenticates with the Dify server and returns login credentials
     *
     * @return Login response containing access token and refresh token
     */
    LoginResponse login();

    /**
     * Refreshes an authentication token to maintain session validity
     *
     * @param refreshToken The refresh token used to obtain a new access token
     * @return Login response containing new access token and refresh token
     */
    LoginResponse refreshToken(String refreshToken);
}
