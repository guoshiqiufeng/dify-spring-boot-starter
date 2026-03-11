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
import io.github.guoshiqiufeng.dify.chat.dto.response.MessageConversationsResponse;
import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
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
    private final ThreadLocal<ConversationPrefetchContext> conversationPrefetchContext = new ThreadLocal<>();

    private static final String ENDPOINT_CONVERSATION_VARIABLES = "/conversation-variables";

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
                ConversationPrefetchContext messagesContext = conversationPrefetchContext.get();
                if (messagesContext == null || messagesContext.getConversationId() == null) {
                    String reason = (messagesContext != null) ? messagesContext.getSkipReason() : "No prefetch context";
                    return buildSkippedResult("messages", "/messages", reason);
                }
                return executeCheck(methodName, "/messages",
                        () -> {
                            MessagesRequest request = new MessagesRequest();
                            request.setApiKey(apiKey);
                            request.setConversationId(messagesContext.getConversationId());
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
                ConversationPrefetchContext context = conversationPrefetchContext.get();
                if (context == null || context.getConversationId() == null) {
                    String reason = (context != null) ? context.getSkipReason() : "No prefetch context";
                    return buildSkippedResult("conversationVariables", ENDPOINT_CONVERSATION_VARIABLES, reason);
                }
                return executeCheck(methodName, "/conversation-variables",
                        () -> {
                            ConversationVariableRequest request = new ConversationVariableRequest();
                            request.setApiKey(apiKey);
                            request.setConversationId(context.getConversationId());
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
        try {
            // Prefetch conversation for conversationVariables check
            prefetchConversation(apiKey);
            return checkAllApisInternal(apiKey);
        } finally {
            // Clean up ThreadLocal to prevent memory leak
            conversationPrefetchContext.remove();
        }
    }

    /**
     * Prefetch a conversation ID for conversationVariables check
     *
     * @param apiKey API key for authentication
     */
    private void prefetchConversation(String apiKey) {
        ConversationPrefetchContext context = new ConversationPrefetchContext();

        try {
            MessageConversationsRequest request = new MessageConversationsRequest();
            request.setApiKey(apiKey);
            request.setLimit(1);

            DifyPageResult<MessageConversationsResponse> result = difyChat.conversations(request);

            if (result != null && result.getData() != null && !result.getData().isEmpty()) {
                context.setConversationId(result.getData().get(0).getId());
                log.debug("Prefetched conversation ID: {}", context.getConversationId());
            } else {
                context.setSkipReason("No conversations available for health check");
                log.debug("No conversations available, conversationVariables check will be skipped");
            }
        } catch (Exception e) {
            context.setSkipReason("Failed to prefetch conversations: " + e.getMessage());
            log.debug("Failed to prefetch conversations: {}", e.getMessage());
        }

        conversationPrefetchContext.set(context);
    }

    /**
     * Build a skipped result for an API check
     *
     * @param methodName Method name
     * @param endpoint   Endpoint path
     * @param reason     Skip reason
     * @return ApiStatusResult with SKIPPED status
     */
    private ApiStatusResult buildSkippedResult(String methodName, String endpoint, String reason) {
        return ApiStatusResult.builder()
                .methodName(methodName)
                .endpoint(endpoint)
                .status(ApiStatus.SKIPPED)
                .errorMessage(reason)
                .httpStatusCode(0)
                .responseTimeMs(0L)
                .checkTime(LocalDateTime.now())
                .build();
    }

    /**
     * Context for conversation prefetch
     */
    private static final class ConversationPrefetchContext {
        private String conversationId;
        private String skipReason;

        public String getConversationId() {
            return conversationId;
        }

        public void setConversationId(String conversationId) {
            this.conversationId = conversationId;
        }

        public String getSkipReason() {
            return skipReason;
        }

        public void setSkipReason(String skipReason) {
            this.skipReason = skipReason;
        }
    }
}
