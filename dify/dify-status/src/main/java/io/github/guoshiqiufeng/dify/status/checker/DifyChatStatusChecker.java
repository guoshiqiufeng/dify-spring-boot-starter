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
package io.github.guoshiqiufeng.dify.status.checker;

import io.github.guoshiqiufeng.dify.chat.DifyChat;
import io.github.guoshiqiufeng.dify.chat.dto.request.*;
import io.github.guoshiqiufeng.dify.status.dto.ApiStatusResult;
import io.github.guoshiqiufeng.dify.status.dto.ClientStatusReport;
import io.github.guoshiqiufeng.dify.status.enums.ApiStatus;
import io.github.guoshiqiufeng.dify.status.strategy.AbstractStatusCheckStrategy;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DifyChat status checker
 *
 * @author yanghq
 * @version 1.7.0
 * @since 2025/12/19 14:00
 */
@Slf4j
public class DifyChatStatusChecker extends AbstractStatusCheckStrategy {

    private final DifyChat difyChat;

    public DifyChatStatusChecker(DifyChat difyChat) {
        this.difyChat = difyChat;
    }

    @Override
    public String getClientName() {
        return "DifyChat";
    }

    @Override
    public ApiStatusResult checkStatus(String methodName, String apiKey) {
        switch (methodName) {
            case "parameters":
                return executeCheck(methodName, "/parameters",
                        () -> difyChat.parameters(apiKey));
            case "info":
                return executeCheck(methodName, "/info",
                        () -> difyChat.info(apiKey));
            case "meta":
                return executeCheck(methodName, "/meta",
                        () -> difyChat.meta(apiKey));
            case "site":
                return executeCheck(methodName, "/site",
                        () -> difyChat.site(apiKey));
            case "conversations":
                return executeCheck(methodName, "/conversations",
                        () -> {
                            MessageConversationsRequest request = new MessageConversationsRequest();
                            request.setApiKey(apiKey);
                            request.setLimit(1);
                            difyChat.conversations(request);
                        });
            case "messages":
                return executeCheck(methodName, "/messages",
                        () -> {
                            MessagesRequest request = new MessagesRequest();
                            request.setApiKey(apiKey);
                            request.setLimit(1);
                            difyChat.messages(request);
                        });
            case "feedbacks":
                return executeCheck(methodName, "/feedbacks",
                        () -> {
                            AppFeedbackPageRequest request = new AppFeedbackPageRequest();
                            request.setApiKey(apiKey);
                            request.setLimit(1);
                            difyChat.feedbacks(request);
                        });
            case "pageAppAnnotation":
                return executeCheck(methodName, "/annotations",
                        () -> {
                            AppAnnotationPageRequest request = new AppAnnotationPageRequest();
                            request.setApiKey(apiKey);
                            request.setLimit(1);
                            difyChat.pageAppAnnotation(request);
                        });
            case "conversationVariables":
                return executeCheck(methodName, "/conversation-variables",
                        () -> {
                            ConversationVariableRequest request = new ConversationVariableRequest();
                            request.setApiKey(apiKey);
                            difyChat.conversationVariables(request);
                        });
            default:
                throw new IllegalArgumentException("Unknown method: " + methodName);
        }
    }

    /**
     * Check all DifyChat APIs
     *
     * @param apiKey API key for authentication
     * @return ClientStatusReport
     */
    public ClientStatusReport checkAllApis(String apiKey) {
        List<ApiStatusResult> results = new ArrayList<>();

        // Read-only methods that are safe to check
        String[] methodsToCheck = {
                "parameters",
                "info",
                "meta",
                "site",
                "conversations",
                "messages",
                "feedbacks",
                "pageAppAnnotation",
                "conversationVariables"
        };

        for (String method : methodsToCheck) {
            try {
                results.add(checkStatus(method, apiKey));
            } catch (Exception e) {
                log.error("Error checking method {}: {}", method, e.getMessage());
                // Add error result
                results.add(ApiStatusResult.builder()
                        .methodName(method)
                        .status(ApiStatus.UNKNOWN_ERROR)
                        .errorMessage(e.getMessage())
                        .checkTime(LocalDateTime.now())
                        .build());
            }
        }

        return buildClientReport(results);
    }

    /**
     * Build client status report from results
     *
     * @param results List of API status results
     * @return ClientStatusReport
     */
    private ClientStatusReport buildClientReport(List<ApiStatusResult> results) {
        int totalApis = results.size();
        int normalApis = 0;
        int errorApis = 0;
        Map<ApiStatus, Integer> statusSummary = new HashMap<>();

        for (ApiStatusResult result : results) {
            ApiStatus status = result.getStatus();
            statusSummary.put(status, statusSummary.getOrDefault(status, 0) + 1);

            if (status == ApiStatus.NORMAL) {
                normalApis++;
            } else {
                errorApis++;
            }
        }

        // Determine overall status
        ApiStatus overallStatus;
        if (normalApis == totalApis) {
            overallStatus = ApiStatus.NORMAL;
        } else if (normalApis == 0) {
            overallStatus = ApiStatus.SERVER_ERROR;
        } else {
            // Partially working
            overallStatus = ApiStatus.CLIENT_ERROR;
        }

        return ClientStatusReport.builder()
                .clientName(getClientName())
                .overallStatus(overallStatus)
                .apiStatuses(results)
                .totalApis(totalApis)
                .normalApis(normalApis)
                .errorApis(errorApis)
                .reportTime(LocalDateTime.now())
                .statusSummary(statusSummary)
                .build();
    }
}
