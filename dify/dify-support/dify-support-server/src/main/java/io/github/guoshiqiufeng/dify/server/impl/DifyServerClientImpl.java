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
package io.github.guoshiqiufeng.dify.server.impl;

import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.server.DifyServer;
import io.github.guoshiqiufeng.dify.server.client.DifyServerClient;
import io.github.guoshiqiufeng.dify.server.dto.request.AppsRequest;
import io.github.guoshiqiufeng.dify.server.dto.request.ChatConversationsRequest;
import io.github.guoshiqiufeng.dify.server.dto.response.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/9 10:42
 */
@Slf4j
public class DifyServerClientImpl implements DifyServer {

    private final DifyServerClient difyServerClient;

    public DifyServerClientImpl(DifyServerClient difyServerClient) {
        this.difyServerClient = difyServerClient;
    }

    @Override
    public List<AppsResponse> apps(String mode, String name) {
        return difyServerClient.apps(mode, name);
    }

    @Override
    public AppsResponseResult apps(AppsRequest appsRequest) {
        return difyServerClient.apps(appsRequest);
    }

    @Override
    public AppsResponse app(String appId) {
        return difyServerClient.app(appId);
    }

    @Override
    public List<ApiKeyResponse> getAppApiKey(String appId) {
        return difyServerClient.getAppApiKey(appId);
    }

    @Override
    public List<ApiKeyResponse> initAppApiKey(String appId) {
        return difyServerClient.initAppApiKey(appId);
    }

    @Override
    public List<DatasetApiKeyResponse> getDatasetApiKey() {
        return difyServerClient.getDatasetApiKey();
    }

    @Override
    public List<DatasetApiKeyResponse> initDatasetApiKey() {
        return difyServerClient.initDatasetApiKey();
    }

    @Override
    public DifyPageResult<ChatConversationResponse> chatConversations(ChatConversationsRequest request) {
        return difyServerClient.chatConversations(request);
    }

    @Override
    public List<DailyConversationsResponse> dailyConversations(String appId, LocalDateTime start, LocalDateTime end) {
        return difyServerClient.dailyConversations(appId, start, end);
    }

    @Override
    public List<DailyEndUsersResponse> dailyEndUsers(String appId, LocalDateTime start, LocalDateTime end) {
        return difyServerClient.dailyEndUsers(appId, start, end);
    }

    @Override
    public List<AverageSessionInteractionsResponse> averageSessionInteractions(String appId, LocalDateTime start, LocalDateTime end) {
        return difyServerClient.averageSessionInteractions(appId, start, end);
    }

    @Override
    public List<TokensPerSecondResponse> tokensPerSecond(String appId, LocalDateTime start, LocalDateTime end) {
        return difyServerClient.tokensPerSecond(appId, start, end);
    }
}
