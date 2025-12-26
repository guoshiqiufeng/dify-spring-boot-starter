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
package io.github.guoshiqiufeng.dify.server;

import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.server.dto.request.AppsRequest;
import io.github.guoshiqiufeng.dify.server.dto.request.ChatConversationsRequest;
import io.github.guoshiqiufeng.dify.server.dto.response.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Dify服务接口，提供与Dify平台交互的方法
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/3/4 15:05
 */
public interface DifyServer {

    /**
     * 获取所有应用列表
     *
     * @param mode 模式 chat\agent-chat\completion\advanced-chat\workflow
     * @param name 应用名称，用于过滤应用列表（可选，传入空字符串时表示不过滤）
     * @return 返回符合条件的应用列表，每个应用封装为 {@link AppsResponse} 对象
     * @throws IllegalArgumentException 如果传入的参数不符合预期格式或范围
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
     * 根据应用ID获取单个应用的详细信息
     *
     * @param appId 应用的唯一标识符，不能为空
     * @return 返回封装了应用详细信息的 {@link AppsResponse} 对象，如果未找到应用则返回null
     * @throws NullPointerException 如果 appId 为 null
     */
    AppsResponse app(String appId);

    /**
     * 根据应用ID获取该应用的所有API Key列表
     *
     * @param appId 应用的唯一标识符，不能为空
     * @return 返回封装了API Key信息的列表，每个API Key封装为 {@link ApiKeyResponse} 对象
     * @throws NullPointerException 如果 id 为 null
     */
    List<ApiKeyResponse> getAppApiKey(String appId);

    /**
     * 初始化应用的API Key
     * 如果应用尚未创建API Key，此方法将创建并返回新的API Key
     *
     * @param appId 应用的唯一标识符，不能为空
     * @return 返回初始化后的API Key列表，每个API Key封装为 {@link ApiKeyResponse} 对象
     * @throws NullPointerException 如果 id 为 null
     */
    List<ApiKeyResponse> initAppApiKey(String appId);

    /**
     * 删除指定应用的API Key
     *
     * @param appId 应用的唯一标识符，不能为空
     * @param apiKeyId API Key的唯一标识符，不能为空
     * @throws NullPointerException 如果 appId 或 apiKeyId 为 null
     */
    void deleteAppApiKey(String appId, String apiKeyId);

    /**
     * 获取知识库的API Key列表
     * 用于访问Dify平台上的知识库资源
     *
     * @return 返回知识库API Key列表，每个API Key封装为 {@link DatasetApiKeyResponse} 对象，
     * 如果未找到则返回null
     */
    List<DatasetApiKeyResponse> getDatasetApiKey();

    /**
     * 初始化知识库的API Key
     * 如果知识库尚未创建API Key，此方法将创建并返回新的API Key
     *
     * @return 返回初始化后的知识库API Key列表，每个API Key封装为 {@link DatasetApiKeyResponse} 对象，
     * 如果初始化失败则返回空列表
     */
    List<DatasetApiKeyResponse> initDatasetApiKey();

    /**
     * 删除指定知识库的API Key
     *
     * @param apiKeyId API Key的唯一标识符，不能为空
     * @throws NullPointerException 如果 apiKeyId 为 null
     */
    void deleteDatasetApiKey(String apiKeyId);

    /**
     * 获取应用的聊天会话列表
     *
     * @param request 聊天会话查询请求，包含分页参数和过滤条件
     * @return 分页结果，包含会话列表信息
     */
    DifyPageResult<ChatConversationResponse> chatConversations(ChatConversationsRequest request);

    /**
     * 获取应用的每日对话统计
     *
     * @param appId 应用的唯一标识符，不能为空
     * @param start 开始时间，格式为 "yyyy-MM-dd HH:mm"
     * @param end   结束时间，格式为 "yyyy-MM-dd HH:mm"
     * @return 返回每日对话统计列表，每个统计封装为 {@link DailyConversationsResponse} 对象
     * @throws IllegalArgumentException 如果传入的参数不符合预期格式或范围
     */
    List<DailyConversationsResponse> dailyConversations(String appId, LocalDateTime start, LocalDateTime end);

    /**
     * 获取应用的每日终端用户统计
     *
     * @param appId 应用的唯一标识符，不能为空
     * @param start 开始时间，格式为 "yyyy-MM-dd HH:mm"
     * @param end   结束时间，格式为 "yyyy-MM-dd HH:mm"
     * @return 返回每日终端用户统计列表，每个统计封装为 {@link DailyEndUsersResponse} 对象
     * @throws IllegalArgumentException 如果传入的参数不符合预期格式或范围
     */
    List<DailyEndUsersResponse> dailyEndUsers(String appId, LocalDateTime start, LocalDateTime end);

    /**
     * 获取应用的平均会话交互统计
     *
     * @param appId 应用的唯一标识符，不能为空
     * @param start 开始时间，格式为 "yyyy-MM-dd HH:mm"
     * @param end   结束时间，格式为 "yyyy-MM-dd HH:mm"
     * @return 返回平均会话交互统计列表，每个统计封装为 {@link AverageSessionInteractionsResponse} 对象
     * @throws IllegalArgumentException 如果传入的参数不符合预期格式或范围
     */
    List<AverageSessionInteractionsResponse> averageSessionInteractions(String appId, LocalDateTime start, LocalDateTime end);

    /**
     * 获取应用的每秒令牌统计
     *
     * @param appId 应用的唯一标识符，不能为空
     * @param start 开始时间，格式为 "yyyy-MM-dd HH:mm"
     * @param end   结束时间，格式为 "yyyy-MM-dd HH:mm"
     * @return 返回每秒令牌统计列表，每个统计封装为 {@link TokensPerSecondResponse} 对象
     * @throws IllegalArgumentException 如果传入的参数不符合预期格式或范围
     */
    List<TokensPerSecondResponse> tokensPerSecond(String appId, LocalDateTime start, LocalDateTime end);

    /**
     * 获取应用的用户满意度率统计
     *
     * @param appId 应用的唯一标识符，不能为空
     * @param start 开始时间،格式为 "yyyy-MM-dd HH:mm"
     * @param end   结束时间，格式为 "yyyy-MM-dd HH:mm"
     * @return 返回用户满意度率统计列表，每个统计封装为 {@link UserSatisfactionRateResponse} 对象
     * @throws IllegalArgumentException 如果传入的参数不符合预期格式或范围
     */
    List<UserSatisfactionRateResponse> userSatisfactionRate(String appId, LocalDateTime start, LocalDateTime end);

    /**
     * 获取应用的令牌费用统计
     *
     * @param appId 应用的唯一标识符，不能为空
     * @param start 开始时间，格式为 "yyyy-MM-dd HH:mm"
     * @param end   结束时间，格式为 "yyyy-MM-dd HH:mm"
     * @return 返回令牌费用统计列表，每个统计封装为 {@link TokenCostsResponse} 对象
     * @throws IllegalArgumentException 如果传入的参数不符合预期格式或范围
     */
    List<TokenCostsResponse> tokenCosts(String appId, LocalDateTime start, LocalDateTime end);

    /**
     * 获取应用的每日消息统计
     *
     * @param appId 应用的唯一标识符，不能为空
     * @param start 开始时间，格式为 "yyyy-MM-dd HH:mm"
     * @param end   结束时间，格式为 "yyyy-MM-dd HH:mm"
     * @return 返回每日消息统计列表，每个统计封装为 {@link DailyMessagesResponse} 对象
     * @throws IllegalArgumentException 如果传入的参数不符合预期格式或范围
     */
    List<DailyMessagesResponse> dailyMessages(String appId, LocalDateTime start, LocalDateTime end);
}
