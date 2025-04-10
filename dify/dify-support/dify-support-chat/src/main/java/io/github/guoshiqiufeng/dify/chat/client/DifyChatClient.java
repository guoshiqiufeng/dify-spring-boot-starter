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
package io.github.guoshiqiufeng.dify.chat.client;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import io.github.guoshiqiufeng.dify.chat.constant.ChatUriConstant;
import io.github.guoshiqiufeng.dify.chat.dto.request.*;
import io.github.guoshiqiufeng.dify.chat.dto.response.*;
import io.github.guoshiqiufeng.dify.chat.utils.MultipartInputStreamFileResource;
import io.github.guoshiqiufeng.dify.chat.utils.WebClientUtil;
import io.github.guoshiqiufeng.dify.core.client.BaseDifyClient;
import io.github.guoshiqiufeng.dify.core.enums.ResponseModeEnum;
import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.core.pojo.DifyResult;
import io.github.guoshiqiufeng.dify.core.pojo.request.ChatMessageVO;
import io.github.guoshiqiufeng.dify.core.pojo.response.MessagesResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/7 13:20
 */
@Slf4j
public class DifyChatClient extends BaseDifyClient {

    public DifyChatClient() {
        super();
    }

    public DifyChatClient(String baseUrl) {
        super(baseUrl);
    }

    public DifyChatClient(String baseUrl, RestClient.Builder restClientBuilder, WebClient.Builder webClientBuilder) {
        super(baseUrl, restClientBuilder, webClientBuilder);
    }

    public ChatMessageSendResponse chat(ChatMessageSendRequest chatRequest) {
        Assert.notNull(chatRequest, REQUEST_BODY_NULL_ERROR);

        ChatMessageVO chatMessage = builderChatMessage(ResponseModeEnum.blocking, chatRequest);

        return this.restClient.post()
                .uri(ChatUriConstant.V1_CHAT_MESSAGES_URI)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + chatRequest.getApiKey())
                .body(chatMessage)
                .retrieve()
                .onStatus(this.responseErrorHandler)
                .body(ChatMessageSendResponse.class);
    }

    public Flux<ChatMessageSendResponse> streamingChat(ChatMessageSendRequest chatRequest) {
        Assert.notNull(chatRequest, REQUEST_BODY_NULL_ERROR);

        ChatMessageVO chatMessage = builderChatMessage(ResponseModeEnum.streaming, chatRequest);

        return this.webClient.post()
                .uri(ChatUriConstant.V1_CHAT_MESSAGES_URI)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + chatRequest.getApiKey())
                .body(Mono.just(chatMessage), ChatMessageVO.class)
                .retrieve()
                .onStatus(HttpStatusCode::isError, WebClientUtil::exceptionFunction)
                .bodyToFlux(ChatMessageSendResponse.class);
    }

    public void stopMessagesStream(String apiKey, String taskId, String userId) {
        Assert.notNull(apiKey, "apiKey must not be null");
        Assert.notNull(taskId, "taskId must not be null");

        Map<String, Object> params = new HashMap<>(2);
        params.put("user", userId);

        this.restClient.post()
                .uri(ChatUriConstant.V1_CHAT_MESSAGES_URI + "/{taskId}/stop", taskId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .body(params)
                .retrieve()
                .onStatus(responseErrorHandler)
                .toBodilessEntity();
    }

    public MessageFeedbackResponse messageFeedback(MessageFeedbackRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);

        Map<String, Object> values = new HashMap<>(3);
        values.put("rating", request.getRating() != null ? request.getRating().getKey() : null);
        values.put("user", request.getUserId());
        values.put("content", request.getContent() == null ? "" : request.getContent());

        return this.restClient.post()
                .uri(ChatUriConstant.V1_MESSAGES_URI + "/{messageId}/feedbacks", request.getMessageId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + request.getApiKey())
                .body(values)
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(new ParameterizedTypeReference<MessageFeedbackResponse>() {
                });
    }

    public DifyPageResult<MessageConversationsResponse> conversations(MessageConversationsRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        if (StrUtil.isEmpty(request.getSortBy())) {
            request.setSortBy("-updated_at");
        }
        if (request.getLimit() == null) {
            request.setLimit(20);
        }

        return this.restClient.get()
                .uri(ChatUriConstant.V1_CONVERSATIONS_URI + "?user={userId}&last_id={lastId}&limit={limit}&sort_by={sortBy}",
                        request.getUserId(), request.getLastId(), request.getLimit(), request.getSortBy())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + request.getApiKey())
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(new ParameterizedTypeReference<DifyPageResult<MessageConversationsResponse>>() {
                });
    }

    public DifyPageResult<MessagesResponseVO> messages(MessagesRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        if (request.getLimit() == null) {
            request.setLimit(20);
        }

        return this.restClient.get()
                .uri(ChatUriConstant.V1_MESSAGES_URI + "?conversation_id={conversationId}&user={user}&first_id={firstId}&limit={limit}",
                        request.getConversationId(),
                        request.getUserId(),
                        request.getFirstId() == null ? "" : request.getFirstId(),
                        request.getLimit())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + request.getApiKey())
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(new ParameterizedTypeReference<DifyPageResult<MessagesResponseVO>>() {
                });

    }


    public List<String> messagesSuggested(String messageId, String apiKey, String userId) {
        DifyResult<List<String>> body = this.restClient.get()
                .uri(ChatUriConstant.V1_MESSAGES_URI + "/{messageId}/suggested?user={user}",
                        messageId,
                        userId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(new ParameterizedTypeReference<DifyResult<List<String>>>() {
                });
        return body == null ? List.of() : body.getData();
    }

    public void deleteConversation(String conversationId, String apiKey, String userId) {
        Map<String, Object> params = new HashMap<>(1);
        params.put("user", userId);

        this.restClient.method(HttpMethod.DELETE)
                .uri(ChatUriConstant.V1_CONVERSATIONS_URI + "/{conversationId}", conversationId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(params)
                .retrieve()
                .onStatus(responseErrorHandler)
                .toBodilessEntity();
    }

    public ResponseEntity<byte[]> textToAudio(TextToAudioRequest request) {
        Map<String, String> requestBody = new HashMap<>(3);
        requestBody.put("user", request.getUserId());
        requestBody.put("text", request.getText());
        requestBody.put("message_id", request.getMessageId());
        return this.restClient.post()
                .uri(ChatUriConstant.V1_TEXT_TO_AUDIO_URI)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + request.getApiKey())
                .body(requestBody)
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(new ParameterizedTypeReference<ResponseEntity<byte[]>>() {
                });
    }

    public DifyTextVO audioToText(AudioToTextRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        MultipartFile file = request.getFile();
        BodyInserters.MultipartInserter fromMultipartData = BodyInserters.fromMultipartData(
                new LinkedMultiValueMap<>() {{
                    try {
                        add("file", new MultipartInputStreamFileResource(
                                file.getInputStream(),
                                file.getOriginalFilename()
                        ));
                    } catch (IOException e) {
                        throw new RuntimeException("file getInputStream error", e);
                    }
                }}
        );
        return this.restClient.post()
                .uri(ChatUriConstant.V1_AUDIO_TO_TEXT_URI)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + request.getApiKey())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(fromMultipartData)
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(DifyTextVO.class);
    }

    public MessageConversationsResponse renameConversation(RenameConversationRequest renameConversationRequest) {
        if (renameConversationRequest.getAutoGenerate() == null) {
            renameConversationRequest.setAutoGenerate(false);
        }
        Map<String, Object> values = new HashMap<>(3);
        values.put("name", renameConversationRequest.getName() == null ? "" : renameConversationRequest.getName());
        values.put("auto_generate", renameConversationRequest.getAutoGenerate());
        values.put("user", renameConversationRequest.getUserId());

        return this.restClient.post()
                .uri(ChatUriConstant.V1_CONVERSATIONS_URI + "/{conversationId}/name", renameConversationRequest.getConversationId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + renameConversationRequest.getApiKey())
                .body(values)
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(new ParameterizedTypeReference<MessageConversationsResponse>() {
                });
    }

    public AppParametersResponseVO parameters(String apiKey) {
        return this.restClient.get()
                .uri(ChatUriConstant.V1_PARAMETERS_URI)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(new ParameterizedTypeReference<AppParametersResponseVO>() {
                });
    }

    private ChatMessageVO builderChatMessage(ResponseModeEnum responseMode, ChatMessageSendRequest sendRequest) {
        ChatMessageVO chatMessage = new ChatMessageVO();
        chatMessage.setResponseMode(responseMode);
        chatMessage.setUser(sendRequest.getUserId());
        chatMessage.setQuery(sendRequest.getContent());
        chatMessage.setConversationId(sendRequest.getConversationId());
        List<ChatMessageSendRequest.ChatMessageFile> files = sendRequest.getFiles();
        if (!CollectionUtils.isEmpty(files)) {
            files = files.stream().peek(f -> {
                if (StrUtil.isEmpty(f.getType())) {
                    f.setType("image");
                }
                if (StrUtil.isEmpty(f.getTransferMethod())) {
                    f.setTransferMethod("remote_url");
                }
            }).toList();
            chatMessage.setFiles(BeanUtil.copyToList(files, ChatMessageVO.ChatMessageFile.class));
        }
        chatMessage.setInputs(sendRequest.getInputs() == null ? Map.of() : sendRequest.getInputs());
        return chatMessage;
    }


}
