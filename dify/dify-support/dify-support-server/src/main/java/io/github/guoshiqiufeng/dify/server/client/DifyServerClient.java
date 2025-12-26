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
package io.github.guoshiqiufeng.dify.server.client;

import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.server.dto.request.AppsRequest;
import io.github.guoshiqiufeng.dify.server.dto.request.ChatConversationsRequest;
import io.github.guoshiqiufeng.dify.server.dto.response.*;

import java.time.LocalDateTime;
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
     * Contains current page data, total count, page number, page limit and other pagination information
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
     * Deletes an API key for a specific application
     *
     * @param appId The ID of the application to delete API key for
     * @param apiKeyId The ID of the API key to delete
     */
    void deleteAppApiKey(String appId, String apiKeyId);

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
     * Deletes a dataset API key
     *
     * @param apiKeyId The ID of the dataset API key to delete
     */
    void deleteDatasetApiKey(String apiKeyId);

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

    /**
     * Retrieves paginated chat conversations for a specific application
     *
     * @param request Chat conversations query request containing pagination parameters and filter conditions
     *                Should include appId, date range, annotation status, and other filtering conditions, as well as page, limit for pagination
     * @return Paginated chat conversations result encapsulated in {@link DifyPageResult} object
     * Contains current page data, total count, page number, page limit and other pagination information
     */
    DifyPageResult<ChatConversationResponse> chatConversations(ChatConversationsRequest request);

    /**
     * Retrieves daily conversation statistics for a specific application
     *
     * @param appId The ID of the application to get statistics for
     * @param start Start time in format "yyyy-MM-dd HH:mm"
     * @param end   End time in format "yyyy-MM-dd HH:mm"
     * @return List of daily conversation statistics, each encapsulated in {@link DailyConversationsResponse} object
     */
    List<DailyConversationsResponse> dailyConversations(String appId, LocalDateTime start, LocalDateTime end);

    /**
     * Retrieves daily end users statistics for a specific application
     *
     * @param appId The ID of the application to get statistics for
     * @param start Start time in format "yyyy-MM-dd HH:mm"
     * @param end   End time in format "yyyy-MM-dd HH:mm"
     * @return List of daily end users statistics, each encapsulated in {@link DailyEndUsersResponse} object
     */
    List<DailyEndUsersResponse> dailyEndUsers(String appId, LocalDateTime start, LocalDateTime end);

    /**
     * Retrieves average session interactions statistics for a specific application
     *
     * @param appId The ID of the application to get statistics for
     * @param start Start time in format "yyyy-MM-dd HH:mm"
     * @param end   End time in format "yyyy-MM-dd HH:mm"
     * @return List of average session interactions statistics, each encapsulated in {@link AverageSessionInteractionsResponse} object
     */
    List<AverageSessionInteractionsResponse> averageSessionInteractions(String appId, LocalDateTime start, LocalDateTime end);

    /**
     * Retrieves tokens per second statistics for a specific application
     *
     * @param appId The ID of the application to get statistics for
     * @param start Start time in format "yyyy-MM-dd HH:mm"
     * @param end   End time in format "yyyy-MM-dd HH:mm"
     * @return List of tokens per second statistics, each encapsulated in {@link TokensPerSecondResponse} object
     */
    List<TokensPerSecondResponse> tokensPerSecond(String appId, LocalDateTime start, LocalDateTime end);

    /**
     * Retrieves user satisfaction rate statistics for a specific application
     *
     * @param appId The ID of the application to get statistics for
     * @param start Start time in format "yyyy-MM-dd HH:mm"
     * @param end   End time in format "yyyy-MM-dd HH:mm"
     * @return List of user satisfaction rate statistics, each encapsulated in {@link UserSatisfactionRateResponse} object
     */
    List<UserSatisfactionRateResponse> userSatisfactionRate(String appId, LocalDateTime start, LocalDateTime end);

    /**
     * Retrieves token costs statistics for a specific application
     *
     * @param appId The ID of the application to get statistics for
     * @param start Start time in format "yyyy-MM-dd HH:mm"
     * @param end   End time in format "yyyy-MM-dd HH:mm"
     * @return List of token costs statistics, each encapsulated in {@link TokenCostsResponse} object
     */
    List<TokenCostsResponse> tokenCosts(String appId, LocalDateTime start, LocalDateTime end);

    /**
     * Retrieves daily messages statistics for a specific application
     *
     * @param appId The ID of the application to get statistics for
     * @param start Start time in format "yyyy-MM-dd HH:mm"
     * @param end   End time in format "yyyy-MM-dd HH:mm"
     * @return List of daily messages statistics, each encapsulated in {@link DailyMessagesResponse} object
     */
    List<DailyMessagesResponse> dailyMessages(String appId, LocalDateTime start, LocalDateTime end);
}
