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
package io.github.guoshiqiufeng.dify.client.spring5.chat;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import io.github.guoshiqiufeng.dify.chat.client.DifyChatClient;
import io.github.guoshiqiufeng.dify.chat.constant.ChatUriConstant;
import io.github.guoshiqiufeng.dify.chat.dto.request.*;
import io.github.guoshiqiufeng.dify.chat.dto.response.*;
import io.github.guoshiqiufeng.dify.chat.exception.DiftChatException;
import io.github.guoshiqiufeng.dify.chat.exception.DiftChatExceptionEnum;
import io.github.guoshiqiufeng.dify.client.spring5.base.BaseDifyDefaultClient;
import io.github.guoshiqiufeng.dify.client.spring5.utils.DatasetHeaderUtils;
import io.github.guoshiqiufeng.dify.client.spring5.utils.MultipartInputStreamFileResource;
import io.github.guoshiqiufeng.dify.client.spring5.utils.WebClientUtil;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.core.enums.ResponseModeEnum;
import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.core.pojo.DifyResult;
import io.github.guoshiqiufeng.dify.core.pojo.request.ChatMessageVO;
import io.github.guoshiqiufeng.dify.core.pojo.response.MessagesResponseVO;
import io.github.guoshiqiufeng.dify.dataset.constant.DatasetUriConstant;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/4/16 10:58
 */
public class DifyChatDefaultClient extends BaseDifyDefaultClient implements DifyChatClient {

    public DifyChatDefaultClient() {
        super();
    }

    public DifyChatDefaultClient(String baseUrl) {
        super(baseUrl);
    }

    public DifyChatDefaultClient(String baseUrl, DifyProperties.ClientConfig clientConfig, WebClient.Builder webClientBuilder) {
        super(baseUrl, clientConfig, webClientBuilder);
    }

    @Override
    public ChatMessageSendResponse chat(ChatMessageSendRequest chatRequest) {
        Assert.notNull(chatRequest, REQUEST_BODY_NULL_ERROR);

        ChatMessageVO chatMessage = builderChatMessage(ResponseModeEnum.blocking, chatRequest);

        return this.webClient.post()
                .uri(ChatUriConstant.V1_CHAT_MESSAGES_URI)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + chatRequest.getApiKey())
                .bodyValue(chatMessage)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(ChatMessageSendResponse.class).block();
    }

    @Override
    public Flux<ChatMessageSendCompletionResponse> streamingChat(ChatMessageSendRequest chatRequest) {
        Assert.notNull(chatRequest, REQUEST_BODY_NULL_ERROR);

        ChatMessageVO chatMessage = builderChatMessage(ResponseModeEnum.streaming, chatRequest);

        return this.webClient.post()
                .uri(ChatUriConstant.V1_CHAT_MESSAGES_URI)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + chatRequest.getApiKey())
                .body(Mono.just(chatMessage), ChatMessageVO.class)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToFlux(ChatMessageSendCompletionResponse.class);
    }

    @Override
    public void stopMessagesStream(String apiKey, String taskId, String userId) {
        Assert.notNull(apiKey, "apiKey must not be null");
        Assert.notNull(taskId, "taskId must not be null");

        Map<String, Object> params = new HashMap<>(2);
        params.put("user", userId);

        this.webClient.post()
                .uri(ChatUriConstant.V1_CHAT_MESSAGES_URI + "/{taskId}/stop", taskId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .bodyValue(params)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(Void.class)
                .block();
    }

    @Override
    public MessageFeedbackResponse messageFeedback(MessageFeedbackRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);

        Map<String, Object> values = new HashMap<>(3);
        values.put("rating", request.getRating() != null ? request.getRating().getKey() : null);
        values.put("user", request.getUserId());
        values.put("content", request.getContent() == null ? "" : request.getContent());

        return this.webClient.post()
                .uri(ChatUriConstant.V1_MESSAGES_URI + "/{messageId}/feedbacks", request.getMessageId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + request.getApiKey())
                .bodyValue(values)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(new ParameterizedTypeReference<MessageFeedbackResponse>() {
                }).block();
    }

    @Override
    public DifyPageResult<MessageConversationsResponse> conversations(MessageConversationsRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        if (StrUtil.isEmpty(request.getSortBy())) {
            request.setSortBy("-updated_at");
        }
        if (request.getLimit() == null) {
            request.setLimit(20);
        }

        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(ChatUriConstant.V1_CONVERSATIONS_URI)
                        .queryParam("sort_by", request.getSortBy())
                        .queryParam("limit", request.getLimit())
                        .queryParamIfPresent("user", Optional.ofNullable(request.getUserId()).filter(m -> !m.isEmpty()))
                        .queryParamIfPresent("last_id", Optional.ofNullable(request.getLastId()).filter(m -> !m.isEmpty()))
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + request.getApiKey())
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(new ParameterizedTypeReference<DifyPageResult<MessageConversationsResponse>>() {
                }).block();
    }

    @Override
    public DifyPageResult<MessagesResponseVO> messages(MessagesRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        if (request.getLimit() == null) {
            request.setLimit(20);
        }

        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(ChatUriConstant.V1_MESSAGES_URI)
                        .queryParam("conversation_id", request.getConversationId())
                        .queryParam("limit", request.getLimit())
                        // 条件参数：status（为空时忽略）
                        .queryParamIfPresent("first_id", Optional.ofNullable(request.getFirstId()).filter(m -> !m.isEmpty()))
                        // 条件参数：keyword（为空时忽略）
                        .queryParamIfPresent("user", Optional.ofNullable(request.getUserId()).filter(m -> !m.isEmpty()))
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + request.getApiKey())
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(new ParameterizedTypeReference<DifyPageResult<MessagesResponseVO>>() {
                }).block();

    }


    @Override
    public List<String> messagesSuggested(String messageId, String apiKey, String userId) {
        DifyResult<List<String>> body = this.webClient.get()
                .uri(ChatUriConstant.V1_MESSAGES_URI + "/{messageId}/suggested?user={user}",
                        messageId,
                        userId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(new ParameterizedTypeReference<DifyResult<List<String>>>() {
                }).block();
        return body == null ? new ArrayList<>(0) : body.getData();
    }

    @Override
    public void deleteConversation(String conversationId, String apiKey, String userId) {
        Map<String, Object> params = new HashMap<>(1);
        params.put("user", userId);

        this.webClient.method(HttpMethod.DELETE)
                .uri(ChatUriConstant.V1_CONVERSATIONS_URI + "/{conversationId}", conversationId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(params)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(Void.class).block();
    }

    @Override
    public ResponseEntity<byte[]> textToAudio(TextToAudioRequest request) {
        Map<String, String> requestBody = new HashMap<>(3);
        requestBody.put("user", request.getUserId());
        requestBody.put("text", request.getText());
        requestBody.put("message_id", request.getMessageId());
        return this.webClient.post()
                .uri(ChatUriConstant.V1_TEXT_TO_AUDIO_URI)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + request.getApiKey())
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(new ParameterizedTypeReference<ResponseEntity<byte[]>>() {
                }).block();
    }

    @Override
    public DifyTextVO audioToText(AudioToTextRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        MultipartFile file = request.getFile();
        BodyInserters.MultipartInserter fromMultipartData = BodyInserters.fromMultipartData(
                new LinkedMultiValueMap<String, MultipartInputStreamFileResource>() {{
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
        return this.webClient.post()
                .uri(ChatUriConstant.V1_AUDIO_TO_TEXT_URI)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + request.getApiKey())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(fromMultipartData)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(DifyTextVO.class).block();
    }

    @Override
    public MessageConversationsResponse renameConversation(RenameConversationRequest renameConversationRequest) {
        if (renameConversationRequest.getAutoGenerate() == null) {
            renameConversationRequest.setAutoGenerate(false);
        }
        Map<String, Object> values = new HashMap<>(3);
        values.put("name", renameConversationRequest.getName() == null ? "" : renameConversationRequest.getName());
        values.put("auto_generate", renameConversationRequest.getAutoGenerate());
        values.put("user", renameConversationRequest.getUserId());

        return this.webClient.post()
                .uri(ChatUriConstant.V1_CONVERSATIONS_URI + "/{conversationId}/name", renameConversationRequest.getConversationId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + renameConversationRequest.getApiKey())
                .bodyValue(values)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(new ParameterizedTypeReference<MessageConversationsResponse>() {
                }).block();
    }

    @Override
    public AppParametersResponseVO parameters(String apiKey) {
        return this.webClient.get()
                .uri(ChatUriConstant.V1_PARAMETERS_URI)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(new ParameterizedTypeReference<AppParametersResponseVO>() {
                }).block();
    }

    @Override
    public AppSiteResponse site(String apiKey) {
        Assert.notNull(apiKey, "apiKey must not be null");
        return this.webClient.get()
                .uri(ChatUriConstant.V1_SITE_URI)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(new ParameterizedTypeReference<AppSiteResponse>() {
                }).block();
    }

    @Override
    public FileUploadResponse fileUpload(FileUploadRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        Assert.notNull(request.getFile(), "file must not be null");
        Assert.notNull(request.getUserId(), "userId must not be null");
        Assert.notNull(request.getApiKey(), "apiKey must not be null");

        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        try {
            // Get file content and type
            byte[] fileContent = request.getFile().getBytes();
            String contentType = request.getFile().getContentType();
            contentType = (StrUtil.isEmpty(contentType)) ? MediaType.TEXT_PLAIN_VALUE : contentType;

            // Add file part
            builder.part("file", fileContent)
                    .header("Content-Disposition",
                            "form-data; name=\"file\"; filename=\"" + request.getFile().getOriginalFilename() + "\"")
                    .header("Content-Type", contentType);
            request.setFile(null);

            builder.part("user", request.getUserId());
        } catch (IOException e) {
            throw new DiftChatException(DiftChatExceptionEnum.DIFY_DATA_PARSING_FAILURE);
        }

        return webClient.post()
                .uri(DatasetUriConstant.V1_FILES_UPLOAD)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request.getApiKey()).accept(h))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(builder.build())
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(FileUploadResponse.class).block();
    }

    @Override
    public AppInfoResponse info(String apiKey) {
        Assert.notNull(apiKey, "apiKey must not be null");
        return webClient.get()
                .uri(DatasetUriConstant.V1_INFO)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(apiKey).accept(h))
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(AppInfoResponse.class).block();
    }

    @Override
    public AppMetaResponse meta(String apiKey) {
        Assert.notNull(apiKey, "apiKey must not be null");
        return webClient.get()
                .uri(DatasetUriConstant.V1_META)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(apiKey).accept(h))
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(AppMetaResponse.class).block();
    }

    @Override
    public DifyPageResult<AppAnnotationResponse> pageAppAnnotation(AppAnnotationPageRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return webClient.get()
                .uri(DatasetUriConstant.V1_APPS_ANNOTATIONS + "?page={page}&limit={limit}", request.getPage(), request.getLimit())
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request.getApiKey()).accept(h))
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(new ParameterizedTypeReference<DifyPageResult<AppAnnotationResponse>>() {
                }).block();
    }

    @Override
    public AppAnnotationResponse createAppAnnotation(AppAnnotationCreateRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return webClient.post()
                .uri(DatasetUriConstant.V1_APPS_ANNOTATIONS)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request.getApiKey()).accept(h))
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(AppAnnotationResponse.class).block();
    }

    @Override
    public AppAnnotationResponse updateAppAnnotation(AppAnnotationUpdateRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return webClient.put()
                .uri(DatasetUriConstant.V1_APPS_ANNOTATIONS + "/{annotation_id}", request.getAnnotationId())
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request.getApiKey()).accept(h))
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(AppAnnotationResponse.class).block();
    }

    @Override
    public void deleteAppAnnotation(String annotationId, String apiKey) {
        Assert.notNull(annotationId, "annotationId must not be null");
        Assert.notNull(apiKey, "apiKey must not be null");
        webClient.delete()
                .uri(DatasetUriConstant.V1_APPS_ANNOTATIONS + "/{annotation_id}", annotationId)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(apiKey).accept(h))
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(Void.class)
                .block();
    }

    @Override
    public AppAnnotationReplyResponse annotationReply(AppAnnotationReplyRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return webClient.post()
                .uri(DatasetUriConstant.V1_APPS_ANNOTATIONS_REPLY + "/{action}", request.getAction())
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request.getApiKey()).accept(h))
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(AppAnnotationReplyResponse.class).block();
    }

    @Override
    public AppAnnotationReplyResponse queryAnnotationReply(AppAnnotationReplyQueryRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return webClient.get()
                .uri(DatasetUriConstant.V1_APPS_ANNOTATIONS_REPLY + "/{action}/status/{job_id}",
                        request.getAction(), request.getJobId())
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request.getApiKey()).accept(h))
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(AppAnnotationReplyResponse.class).block();
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
            }).collect(Collectors.toList());
            chatMessage.setFiles(BeanUtil.copyToList(files, ChatMessageVO.ChatMessageFile.class));
        }
        chatMessage.setInputs(sendRequest.getInputs() == null ? new HashMap<>(0) : sendRequest.getInputs());
        return chatMessage;
    }
}
