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
public class DifyChatStatusChecker extends AbstractClientStatusChecker {

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

    @Override
    protected String[] methodsToCheck() {
        return new String[]{
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
    }

    /**
     * Check all DifyChat APIs
     *
     * @param apiKey API key for authentication
     * @return ClientStatusReport
     */
    public ClientStatusReport checkAllApis(String apiKey) {
        return checkAllApisInternal(apiKey);
    }
}
