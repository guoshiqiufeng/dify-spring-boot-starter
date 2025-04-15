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
package io.github.guoshiqiufeng.dify.chat.impl;

import io.github.guoshiqiufeng.dify.chat.DifyChat;
import io.github.guoshiqiufeng.dify.chat.client.DifyChatClient;
import io.github.guoshiqiufeng.dify.chat.dto.request.*;
import io.github.guoshiqiufeng.dify.chat.dto.response.*;
import io.github.guoshiqiufeng.dify.chat.exception.DiftChatException;
import io.github.guoshiqiufeng.dify.chat.exception.DiftChatExceptionEnum;
import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.core.pojo.response.MessagesResponseVO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.List;

/**
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/7 14:27
 */
@Slf4j
public class DifyChatClientImpl implements DifyChat {

    private final DifyChatClient difyChatClient;

    public DifyChatClientImpl(DifyChatClient difyChatClient) {
        this.difyChatClient = difyChatClient;
    }

    @Override
    public ChatMessageSendResponse send(ChatMessageSendRequest sendRequest) {
        return difyChatClient.chat(sendRequest);
    }

    @Override
    public Flux<ChatMessageSendCompletionResponse> sendChatMessageStream(ChatMessageSendRequest sendRequest) {
        return difyChatClient.streamingChat(sendRequest);
    }

    @Override
    public void stopMessagesStream(String apiKey, String taskId, String userId) {
        difyChatClient.stopMessagesStream(apiKey, taskId, userId);
    }

    @Override
    public MessageFeedbackResponse messageFeedback(MessageFeedbackRequest messageFeedbackRequest) {
        return difyChatClient.messageFeedback(messageFeedbackRequest);
    }

    @Override
    public DifyPageResult<MessageConversationsResponse> conversations(MessageConversationsRequest request) {
        return difyChatClient.conversations(request);
    }

    @Override
    public DifyPageResult<MessagesResponseVO> messages(MessagesRequest request) {
        return difyChatClient.messages(request);
    }

    @Override
    public List<String> messagesSuggested(String messageId, String apiKey, String userId) {
        return difyChatClient.messagesSuggested(messageId, apiKey, userId);
    }

    @Override
    public void deleteConversation(String conversationId, String apiKey, String userId) {
        difyChatClient.deleteConversation(conversationId, apiKey, userId);
    }

    @Override
    public MessageConversationsResponse renameConversation(RenameConversationRequest renameConversationRequest) {
        return difyChatClient.renameConversation(renameConversationRequest);
    }

    @Override
    public AppParametersResponseVO parameters(String apiKey) {
        return difyChatClient.parameters(apiKey);
    }

    @Override
    public void textToAudio(TextToAudioRequest request, HttpServletResponse response) {
        try {
            ResponseEntity<byte[]> responseEntity = difyChatClient.textToAudio(request);

            String type = responseEntity.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
            response.setContentType(type != null ? type : "audio/mpeg");

            String contentDisposition = responseEntity.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION);
            if (contentDisposition != null) {
                response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);
            } else {
                response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=audio.mp3");
            }

            if (responseEntity.getBody() != null) {
                response.getOutputStream().write(responseEntity.getBody());
                response.getOutputStream().flush();
            }

        } catch (IOException | WebClientResponseException e) {
            log.error("textToAudio error: {}", e.getMessage());
            throw new DiftChatException(DiftChatExceptionEnum.DIFY_API_ERROR);
        }
    }

    @Override
    public DifyTextVO audioToText(AudioToTextRequest request) {
        return difyChatClient.audioToText(request);
    }
}
