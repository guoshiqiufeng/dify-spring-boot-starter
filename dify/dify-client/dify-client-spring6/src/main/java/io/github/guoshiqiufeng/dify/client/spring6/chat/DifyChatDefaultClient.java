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
package io.github.guoshiqiufeng.dify.client.spring6.chat;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import io.github.guoshiqiufeng.dify.chat.client.DifyChatClient;
import io.github.guoshiqiufeng.dify.chat.constant.ChatUriConstant;
import io.github.guoshiqiufeng.dify.chat.dto.request.*;
import io.github.guoshiqiufeng.dify.chat.dto.response.*;
import io.github.guoshiqiufeng.dify.chat.exception.DiftChatException;
import io.github.guoshiqiufeng.dify.chat.exception.DiftChatExceptionEnum;
import io.github.guoshiqiufeng.dify.client.spring6.base.BaseDifyDefaultClient;
import io.github.guoshiqiufeng.dify.client.spring6.utils.DatasetHeaderUtils;
import io.github.guoshiqiufeng.dify.client.spring6.utils.MultipartInputStreamFileResource;
import io.github.guoshiqiufeng.dify.client.spring6.utils.WebClientUtil;
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
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    public DifyChatDefaultClient(String baseUrl, DifyProperties.ClientConfig clientConfig, RestClient.Builder restClientBuilder, WebClient.Builder webClientBuilder) {
        super(baseUrl, clientConfig, restClientBuilder, webClientBuilder);
    }

    @Override
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

    @Override
    public Flux<ChatMessageSendCompletionResponse> streamingChat(ChatMessageSendRequest chatRequest) {
        Assert.notNull(chatRequest, REQUEST_BODY_NULL_ERROR);

        ChatMessageVO chatMessage = builderChatMessage(ResponseModeEnum.streaming, chatRequest);

        return this.webClient.post()
                .uri(ChatUriConstant.V1_CHAT_MESSAGES_URI)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + chatRequest.getApiKey())
                .body(Mono.just(chatMessage), ChatMessageVO.class)
                .retrieve()
                .onStatus(HttpStatusCode::isError, WebClientUtil::exceptionFunction)
                .bodyToFlux(ChatMessageSendCompletionResponse.class);
    }

    @Override
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

    @Override
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

    @Override
    public DifyPageResult<MessageConversationsResponse> conversations(MessageConversationsRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        if (StrUtil.isEmpty(request.getSortBy())) {
            request.setSortBy("-updated_at");
        }
        if (request.getLimit() == null) {
            request.setLimit(20);
        }

        return this.restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(ChatUriConstant.V1_CONVERSATIONS_URI)
                        .queryParam("sort_by", request.getSortBy())
                        .queryParam("limit", request.getLimit())
                        .queryParamIfPresent("user", Optional.ofNullable(request.getUserId()).filter(m -> !m.isEmpty()))
                        .queryParamIfPresent("last_id", Optional.ofNullable(request.getLastId()).filter(m -> !m.isEmpty()))
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + request.getApiKey())
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(new ParameterizedTypeReference<DifyPageResult<MessageConversationsResponse>>() {
                });
    }

    @Override
    public DifyPageResult<MessagesResponseVO> messages(MessagesRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        if (request.getLimit() == null) {
            request.setLimit(20);
        }

        return this.restClient.get()
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
                .onStatus(responseErrorHandler)
                .body(new ParameterizedTypeReference<DifyPageResult<MessagesResponseVO>>() {
                });

    }


    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
    public AppParametersResponseVO parameters(String apiKey) {
        return this.restClient.get()
                .uri(ChatUriConstant.V1_PARAMETERS_URI)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(new ParameterizedTypeReference<AppParametersResponseVO>() {
                });
    }

    @Override
    public AppSiteResponse site(String apiKey) {
        Assert.notNull(apiKey, "apiKey must not be null");
        return this.restClient.get()
                .uri(ChatUriConstant.V1_SITE_URI)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(new ParameterizedTypeReference<AppSiteResponse>() {
                });
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

        return restClient.post()
                .uri(DatasetUriConstant.V1_FILES_UPLOAD)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request.getApiKey()).accept(h))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(builder.build())
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(FileUploadResponse.class);
    }

    @Override
    public ResponseEntity<byte[]> filePreview(FilePreviewRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        Assert.notNull(request.getFileId(), "fileId must not be null");
        Assert.notNull(request.getApiKey(), "apiKey must not be null");

        // Build the URI with path variable and optional query parameter
        return this.restClient.get()
                .uri(uriBuilder -> {
                    UriBuilder builder = uriBuilder.path(ChatUriConstant.V1_FILES_PREVIEW_URI);
                    if (request.getAsAttachment() != null && request.getAsAttachment()) {
                        builder.queryParam("as_attachment", "true");
                    }
                    return builder.build(request.getFileId());
                })
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + request.getApiKey())
                .retrieve()
                .onStatus(responseErrorHandler)
                .toEntity(byte[].class);
    }

    @Override
    public AppInfoResponse info(String apiKey) {
        Assert.notNull(apiKey, "apiKey must not be null");
        return restClient.get()
                .uri(DatasetUriConstant.V1_INFO)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(apiKey).accept(h))
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(AppInfoResponse.class);
    }

    @Override
    public AppMetaResponse meta(String apiKey) {
        Assert.notNull(apiKey, "apiKey must not be null");
        return restClient.get()
                .uri(DatasetUriConstant.V1_META)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(apiKey).accept(h))
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(AppMetaResponse.class);
    }

    @Override
    public DifyPageResult<AppAnnotationResponse> pageAppAnnotation(AppAnnotationPageRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return restClient.get()
                .uri(DatasetUriConstant.V1_APPS_ANNOTATIONS + "?page={page}&limit={limit}", request.getPage(), request.getLimit())
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request.getApiKey()).accept(h))
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(new ParameterizedTypeReference<DifyPageResult<AppAnnotationResponse>>() {
                });
    }

    @Override
    public AppAnnotationResponse createAppAnnotation(AppAnnotationCreateRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return restClient.post()
                .uri(DatasetUriConstant.V1_APPS_ANNOTATIONS)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request.getApiKey()).accept(h))
                .body(request)
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(AppAnnotationResponse.class);
    }

    @Override
    public AppAnnotationResponse updateAppAnnotation(AppAnnotationUpdateRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return restClient.put()
                .uri(DatasetUriConstant.V1_APPS_ANNOTATIONS + "/{annotation_id}", request.getAnnotationId())
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request.getApiKey()).accept(h))
                .body(request)
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(AppAnnotationResponse.class);
    }

    @Override
    public void deleteAppAnnotation(String annotationId, String apiKey) {
        Assert.notNull(annotationId, "annotationId must not be null");
        Assert.notNull(apiKey, "apiKey must not be null");
        restClient.delete()
                .uri(DatasetUriConstant.V1_APPS_ANNOTATIONS + "/{annotation_id}", annotationId)
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(apiKey).accept(h))
                .retrieve()
                .onStatus(responseErrorHandler).body(Void.class);
    }

    @Override
    public AppAnnotationReplyResponse annotationReply(AppAnnotationReplyRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return restClient.post()
                .uri(DatasetUriConstant.V1_APPS_ANNOTATIONS_REPLY + "/{action}", request.getAction())
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request.getApiKey()).accept(h))
                .body(request)
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(AppAnnotationReplyResponse.class);
    }

    @Override
    public AppAnnotationReplyResponse queryAnnotationReply(AppAnnotationReplyQueryRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return restClient.get()
                .uri(DatasetUriConstant.V1_APPS_ANNOTATIONS_REPLY + "/{action}/status/{job_id}",
                        request.getAction(), request.getJobId())
                .headers(h -> DatasetHeaderUtils.getHttpHeadersConsumer(request.getApiKey()).accept(h))
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(AppAnnotationReplyResponse.class);
    }

    @Override
    public DifyPageResult<AppFeedbackResponse> feedbacks(AppFeedbackPageRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(ChatUriConstant.V1_APPS_FEEDBACKS)
                        .queryParam("page", request.getPage())
                        .queryParam("limit", request.getLimit())
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + request.getApiKey())
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(new ParameterizedTypeReference<DifyPageResult<AppFeedbackResponse>>() {
                });
    }

    @Override
    public DifyPageResult<ConversationVariableResponse> conversationVariables(ConversationVariableRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        Assert.notNull(request.getConversationId(), "conversationId must not be null");

        return restClient.get()
                .uri(uriBuilder -> {
                    UriBuilder builder = uriBuilder.path(ChatUriConstant.V1_CONVERSATIONS_VARIABLES_URI);
                    if (request.getVariableName() != null && !request.getVariableName().isEmpty()) {
                        builder.queryParam("variable_name", request.getVariableName());
                    }
                    builder.queryParam("user", request.getUserId());
                    return builder.build(request.getConversationId());
                })
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + request.getApiKey())
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(new ParameterizedTypeReference<DifyPageResult<ConversationVariableResponse>>() {
                });
    }

    @Override
    public ConversationVariableResponse updateConversationVariable(UpdateConversationVariableRequest request) {
        Assert.notNull(request, REQUEST_BODY_NULL_ERROR);
        Assert.notNull(request.getConversationId(), "conversationId must not be null");
        Assert.notNull(request.getVariableId(), "variableId must not be null");
        Assert.notNull(request.getValue(), "value must not be null");

        Map<String, Object> values = new HashMap<>(2);
        values.put("value", request.getValue());
        values.put("user", request.getUserId());

        return restClient.put()
                .uri(ChatUriConstant.V1_CONVERSATIONS_VARIABLES_UPDATE_URI, request.getConversationId(), request.getVariableId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + request.getApiKey())
                .contentType(MediaType.APPLICATION_JSON)
                .body(values)
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(ConversationVariableResponse.class);
    }

    private ChatMessageVO builderChatMessage(ResponseModeEnum responseMode, ChatMessageSendRequest sendRequest) {
        ChatMessageVO chatMessage = new ChatMessageVO();
        chatMessage.setResponseMode(responseMode);
        chatMessage.setUser(sendRequest.getUserId());
        chatMessage.setQuery(sendRequest.getContent());
        chatMessage.setConversationId(sendRequest.getConversationId());
        chatMessage.setAutoGenerateName(sendRequest.getAutoGenerateName());
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
