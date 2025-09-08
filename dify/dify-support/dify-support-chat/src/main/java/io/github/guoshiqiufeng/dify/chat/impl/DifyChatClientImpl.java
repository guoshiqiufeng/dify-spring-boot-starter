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
import io.github.guoshiqiufeng.dify.chat.pipeline.DifyChatPipelineUtils;
import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.core.pojo.response.MessagesResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;

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
        return difyChatClient.streamingChat(sendRequest)
                .doOnNext(DifyChatPipelineUtils::processChat);
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
    public ResponseEntity<byte[]> textToAudio(TextToAudioRequest request) {
        return difyChatClient.textToAudio(request);
    }

    @Override
    public DifyTextVO audioToText(AudioToTextRequest request) {
        return difyChatClient.audioToText(request);
    }

    @Override
    public FileUploadResponse fileUpload(FileUploadRequest request) {
        return difyChatClient.fileUpload(request);
    }

    @Override
    public ResponseEntity<byte[]> filePreview(FilePreviewRequest request) {
        return difyChatClient.filePreview(request);
    }

    @Override
    public AppInfoResponse info(String apiKey) {
        return difyChatClient.info(apiKey);
    }

    @Override
    public AppMetaResponse meta(String apiKey) {
        return difyChatClient.meta(apiKey);
    }

    @Override
    public AppSiteResponse site(String apikey) {
        return difyChatClient.site(apikey);
    }

    @Override
    public DifyPageResult<AppAnnotationResponse> pageAppAnnotation(AppAnnotationPageRequest request) {
        return difyChatClient.pageAppAnnotation(request);
    }

    @Override
    public AppAnnotationResponse createAppAnnotation(AppAnnotationCreateRequest request) {
        return difyChatClient.createAppAnnotation(request);
    }

    @Override
    public AppAnnotationResponse updateAppAnnotation(AppAnnotationUpdateRequest request) {
        return difyChatClient.updateAppAnnotation(request);
    }

    @Override
    public void deleteAppAnnotation(String annotationId, String apiKey) {
        difyChatClient.deleteAppAnnotation(annotationId, apiKey);
    }

    @Override
    public AppAnnotationReplyResponse annotationReply(AppAnnotationReplyRequest request) {
        return difyChatClient.annotationReply(request);
    }

    @Override
    public AppAnnotationReplyResponse queryAnnotationReply(AppAnnotationReplyQueryRequest request) {
        return difyChatClient.queryAnnotationReply(request);
    }

    @Override
    public DifyPageResult<AppFeedbackResponse> feedbacks(AppFeedbackPageRequest request) {
        return difyChatClient.feedbacks(request);
    }

    @Override
    public DifyPageResult<ConversationVariableResponse> conversationVariables(ConversationVariableRequest request) {
        return difyChatClient.conversationVariables(request);
    }

    @Override
    public ConversationVariableResponse updateConversationVariable(UpdateConversationVariableRequest request) {
        return difyChatClient.updateConversationVariable(request);
    }
}
