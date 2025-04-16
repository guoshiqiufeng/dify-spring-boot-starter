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
/// *
// * Copyright (c) 2025-2025, fubluesky (fubluesky@foxmail.com)
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package io.github.guoshiqiufeng.dify.chat.impl;
//
//import cn.hutool.core.bean.BeanUtil;
//import cn.hutool.core.util.StrUtil;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import io.github.guoshiqiufeng.dify.chat.DifyChat;
//import io.github.guoshiqiufeng.dify.chat.constant.ChatUriConstant;
//import io.github.guoshiqiufeng.dify.chat.dto.request.*;
//import io.github.guoshiqiufeng.dify.chat.dto.response.*;
//import io.github.guoshiqiufeng.dify.chat.exception.DiftChatException;
//import io.github.guoshiqiufeng.dify.chat.exception.DiftChatExceptionEnum;
//import io.github.guoshiqiufeng.dify.chat.utils.MultipartInputStreamFileResource;
//import io.github.guoshiqiufeng.dify.chat.utils.WebClientUtil;
//import io.github.guoshiqiufeng.dify.core.enums.ResponseModeEnum;
//import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
//import io.github.guoshiqiufeng.dify.core.pojo.DifyResult;
//import io.github.guoshiqiufeng.dify.core.pojo.request.ChatMessageVO;
//import io.github.guoshiqiufeng.dify.core.pojo.response.MessagesResponseVO;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatusCode;
//import org.springframework.http.MediaType;
//import org.springframework.util.CollectionUtils;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.reactive.function.BodyInserters;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.web.reactive.function.client.WebClientResponseException;
//import reactor.core.publisher.Flux;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
/// **
// * 默认实现
// *
// * @author yanghq
// * @version 1.0
// * @since 2025/3/4 10:21
// */
//@Slf4j
//@Deprecated(since = "0.8.0")
//public class DifyChatDefaultImpl implements DifyChat {
//
//    private final ObjectMapper objectMapper;
//    private final WebClient webClient;
//
//    public DifyChatDefaultImpl(ObjectMapper objectMapper, WebClient webClient) {
//        this.objectMapper = objectMapper;
//        this.webClient = webClient;
//    }
//
//    /**
//     * 发送消息
//     */
//    @Override
//    public ChatMessageSendResponse send(ChatMessageSendRequest sendRequest) {
//        // 请求地址 url + /v1/chat-messages
//        String url = ChatUriConstant.V1_CHAT_MESSAGES_URI;
//
//        // 请求体
//        String body = builderChatMessageBody(ResponseModeEnum.blocking, sendRequest);
//        // 使用 WebClient 发送 POST 请求
//
//        return webClient.post()
//                .uri(url)
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + sendRequest.getApiKey())
//                .bodyValue(body)
//                .retrieve()
//                .onStatus(HttpStatusCode::isError, WebClientUtil::exceptionFunction)
//                .bodyToMono(ChatMessageSendResponse.class)
//                .block();
//    }
//
//    private String builderChatMessageBody(ResponseModeEnum responseMode, ChatMessageSendRequest sendRequest) {
//        ChatMessageVO chatMessage = new ChatMessageVO();
//        chatMessage.setResponseMode(responseMode);
//        chatMessage.setUser(sendRequest.getUserId());
//        chatMessage.setQuery(sendRequest.getContent());
//        chatMessage.setConversationId(sendRequest.getConversationId());
//        List<ChatMessageSendRequest.ChatMessageFile> files = sendRequest.getFiles();
//        if (!CollectionUtils.isEmpty(files)) {
//            files = files.stream().peek(f -> {
//                if (StrUtil.isEmpty(f.getType())) {
//                    f.setType("image");
//                }
//                if (StrUtil.isEmpty(f.getTransferMethod())) {
//                    f.setTransferMethod("remote_url");
//                }
//            }).toList();
//            chatMessage.setFiles(BeanUtil.copyToList(files, ChatMessageVO.ChatMessageFile.class));
//        }
//        chatMessage.setInputs(sendRequest.getInputs() == null ? Map.of() : sendRequest.getInputs());
//
//        String body = null;
//        try {
//            body = objectMapper.writeValueAsString(chatMessage);
//        } catch (JsonProcessingException e) {
//            throw new DiftChatException(DiftChatExceptionEnum.DIFY_DATA_PARSING_FAILURE);
//        }
//        return body;
//    }
//
//    /**
//     * 发送消息获取消息流
//     */
//    @Override
//    public Flux<ChatMessageSendCompletionResponse> sendChatMessageStream(ChatMessageSendRequest sendRequest) {
//        // 请求地址 url + /v1/chat-messages 请求方式 POST , stream 流
//        String url = ChatUriConstant.V1_CHAT_MESSAGES_URI;
//
//        String body = builderChatMessageBody(ResponseModeEnum.streaming, sendRequest);
//
//        // 使用 WebClient 发送 POST 请求
//
//        return webClient.post()
//                .uri(url)
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + sendRequest.getApiKey())
//                .bodyValue(body)
//                .retrieve()
//                .onStatus(HttpStatusCode::isError, WebClientUtil::exceptionFunction)
//                .bodyToFlux(ChatMessageSendCompletionResponse.class)
//                .doOnError(e -> log.error("Error while sending chat message: {}", e.getMessage()));
//    }
//
//    @Override
//    public void stopMessagesStream(String apiKey, String taskId, String userId) {
//        String url = ChatUriConstant.V1_CHAT_MESSAGES_URI + "/{taskId}/stop";
//
//        // 使用 WebClient 发送 POST 请求
//
//        Map<String, Object> params = new HashMap<>(2);
//        params.put("user", userId);
//
//        try {
//            String body = objectMapper.writeValueAsString(params);
//
//            webClient.post()
//                    .uri(url, taskId)
//                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
//                    .bodyValue(body)
//                    .retrieve()
//                    .onStatus(HttpStatusCode::isError, WebClientUtil::exceptionFunction)
//                    .toBodilessEntity()  // 如果不需要响应体
//                    .block();  // 转为同步调用
//
//        } catch (WebClientResponseException e) {
//            log.error("Error while stop chat message: {}", e.getMessage());
//            throw new DiftChatException(DiftChatExceptionEnum.DIFY_API_ERROR);
//        } catch (JsonProcessingException e) {
//            log.error("Error while serializing request body: {}", e.getMessage());
//            throw new DiftChatException(DiftChatExceptionEnum.DIFY_DATA_PARSING_FAILURE);
//        }
//    }
//
//    @Override
//    public MessageFeedbackResponse messageFeedback(MessageFeedbackRequest request) {
//        String url = ChatUriConstant.V1_MESSAGES_URI + "/{messageId}/feedbacks";
//
//        try {
//            // 使用 WebClient 发送 GET 请求
//
//            Map<String, Object> values = new HashMap<>(3);
//            values.put("rating", request.getRating() != null ? request.getRating().getKey() : null);
//            values.put("user", request.getUserId());
//            values.put("content", request.getContent() == null ? "" : request.getContent());
//
//            return webClient.post()
//                    .uri(url, request.getMessageId())
//                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + request.getApiKey())
//                    .bodyValue(objectMapper.writeValueAsString(values))
//                    .retrieve()
//                    .onStatus(HttpStatusCode::isError, WebClientUtil::exceptionFunction)
//                    .bodyToMono(new ParameterizedTypeReference<MessageFeedbackResponse>() {
//                    })
//                    .block();  // 转为同步调用
//
//        } catch (JsonProcessingException | WebClientResponseException e) {
//            log.error("Error while messageFeedback: {}", e.getMessage());
//            throw new DiftChatException(DiftChatExceptionEnum.DIFY_API_ERROR);
//        }
//    }
//
//    /**
//     * 获取会话列表
//     */
//    @Override
//    public DifyPageResult<MessageConversationsResponse> conversations(MessageConversationsRequest request) {
//        String url = ChatUriConstant.V1_CONVERSATIONS_URI;
//
//        if (StrUtil.isEmpty(request.getSortBy())) {
//            request.setSortBy("-updated_at");
//        }
//        if (request.getLimit() == null) {
//            request.setLimit(20);
//        }
//
//        try {
//            // 使用 WebClient 发送 GET 请求
//
//            String uri = url + "?user={}";
//            uri = StrUtil.format(uri, request.getUserId());
//
//            if (StrUtil.isNotEmpty(request.getLastId())) {
//                uri += "&last_id={}";
//                uri = StrUtil.format(uri, request.getLastId());
//            }
//
//            uri += "&limit={}&sort_by={}";
//            uri = StrUtil.format(uri, request.getLimit(), request.getSortBy());
//            return webClient.get()
//                    .uri(uri)
//                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + request.getApiKey())
//                    .retrieve()
//                    .onStatus(HttpStatusCode::isError, WebClientUtil::exceptionFunction)
//                    .bodyToMono(new ParameterizedTypeReference<DifyPageResult<MessageConversationsResponse>>() {
//                    })
//                    .block();  // 转为同步调用
//
//        } catch (WebClientResponseException e) {
//            log.error("Error while getting conversations: {}", e.getMessage());
//            throw new DiftChatException(DiftChatExceptionEnum.DIFY_API_ERROR);
//        }
//    }
//
//    /**
//     * 获取消息列表
//     */
//    @Override
//    public DifyPageResult<MessagesResponseVO> messages(MessagesRequest request) {
//        String url = ChatUriConstant.V1_MESSAGES_URI;
//
//        if (request.getLimit() == null) {
//            request.setLimit(20);
//        }
//
//        try {
//            // 使用 WebClient 发送 GET 请求
//
//            return webClient.get()
//                    .uri(url + "?conversation_id={conversationId}&user={user}&first_id={lastId}&limit={limit}",
//                            request.getConversationId(),
//                            request.getUserId(),
//                            request.getFirstId(),
//                            request.getLimit())
//                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + request.getApiKey())
//                    .retrieve()
//                    .onStatus(HttpStatusCode::isError, WebClientUtil::exceptionFunction)
//                    .bodyToMono(new ParameterizedTypeReference<DifyPageResult<MessagesResponseVO>>() {
//                    })
//                    .block();  // 转为同步调用
//
//        } catch (WebClientResponseException e) {
//            log.error("messages error: {}", e.getMessage());
//            throw new DiftChatException(DiftChatExceptionEnum.DIFY_API_ERROR);
//        }
//    }
//
//    /**
//     * 获取消息建议
//     */
//    @Override
//    public List<String> messagesSuggested(String messageId, String apiKey, String userId) {
//        String url = ChatUriConstant.V1_MESSAGES_URI + "/{messageId}/suggested";
//
//        try {
//            // 使用 WebClient 发送 GET 请求
//
//            DifyResult<List<String>> difyResult = webClient.get()
//                    .uri(url + "?user={user}", messageId,
//                            userId)
//                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
//                    .retrieve()
//                    .onStatus(HttpStatusCode::isError, WebClientUtil::exceptionFunction)
//                    .bodyToMono(new ParameterizedTypeReference<DifyResult<List<String>>>() {
//                    })
//                    .onErrorResume(e -> {
//                        log.error("Error while getting conversations: {}", e.getMessage());
//                        return null;
//                    })
//                    .block();// 转为同步调用
//            return difyResult != null ? difyResult.getData() : List.of();
//
//        } catch (WebClientResponseException e) {
//            log.error("messagesSuggested error: {}", e.getMessage());
//            throw new DiftChatException(DiftChatExceptionEnum.DIFY_API_ERROR);
//        }
//    }
//
//    /**
//     * 删除会话
//     */
//    @Override
//    public void deleteConversation(String conversationId, String apiKey, String userId) {
//        String url = ChatUriConstant.V1_CONVERSATIONS_URI + "/{conversationId}";
//
//        // 使用 WebClient 发送 Delete 请求
//
//        Map<String, Object> params = new HashMap<>(1);
//        params.put("user", userId);
//
//        try {
//            String body = objectMapper.writeValueAsString(params);
//
//            webClient.method(HttpMethod.DELETE)
//                    .uri(url, conversationId)
//                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .bodyValue(body)
//                    .retrieve()
//                    .toBodilessEntity()  // 如果不需要响应体
//                    .block();  // 转为同步调用
//
//        } catch (WebClientResponseException | JsonProcessingException e) {
//            log.error("deleteConversation error: {}", e.getMessage());
//            throw new DiftChatException(DiftChatExceptionEnum.DELETE_ERROR);
//        }
//    }
//
//    @Override
//    public MessageConversationsResponse renameConversation(RenameConversationRequest renameConversationRequest) {
//        String url = ChatUriConstant.V1_CONVERSATIONS_URI + "/{conversationId}/name";
//        if (renameConversationRequest.getAutoGenerate() == null) {
//            renameConversationRequest.setAutoGenerate(false);
//        }
//
//        try {
//            // 使用 WebClient 发送 GET 请求
//
//            Map<String, Object> values = new HashMap<>(3);
//            values.put("name", renameConversationRequest.getName() == null ? "" : renameConversationRequest.getName());
//            values.put("auto_generate", renameConversationRequest.getAutoGenerate());
//            values.put("user", renameConversationRequest.getUserId());
//
//            return webClient.post()
//                    .uri(url, renameConversationRequest.getConversationId())
//                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + renameConversationRequest.getApiKey())
//                    .bodyValue(objectMapper.writeValueAsString(values))
//                    .retrieve()
//                    .onStatus(HttpStatusCode::isError, WebClientUtil::exceptionFunction)
//                    .bodyToMono(new ParameterizedTypeReference<MessageConversationsResponse>() {
//                    })
//                    .block();  // 转为同步调用
//
//        } catch (JsonProcessingException | WebClientResponseException e) {
//            log.error("Error while rename conversation: {}", e.getMessage());
//            throw new DiftChatException(DiftChatExceptionEnum.DIFY_API_ERROR);
//        }
//    }
//
//    /**
//     * 获取应用参数
//     */
//    @Override
//    public AppParametersResponseVO parameters(String apiKey) {
//        String url = ChatUriConstant.V1_PARAMETERS_URI;
//
//        try {
//            // 使用 WebClient 发送 GET 请求
//
//            // 转为同步调用
//            return webClient.get()
//                    .uri(url)
//                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
//                    .retrieve()
//                    .onStatus(HttpStatusCode::isError, WebClientUtil::exceptionFunction)
//                    .bodyToMono(new ParameterizedTypeReference<AppParametersResponseVO>() {
//                    })
//                    .onErrorResume(e -> {
//                        log.error("Error while getting conversations: {}", e.getMessage());
//                        return null;
//                    })
//                    .block();
//
//        } catch (WebClientResponseException e) {
//            log.error("parameters error: {}", e.getMessage());
//            throw new DiftChatException(DiftChatExceptionEnum.DIFY_API_ERROR);
//        }
//
//    }
//
//    @Override
//    public void textToAudio(TextToAudioRequest request, HttpServletResponse response) {
//        String url = ChatUriConstant.V1_TEXT_TO_AUDIO_URI;
//
//        try {
//            // 使用 WebClient 发送 POST 请求
//
//            // 构建请求体
//            Map<String, String> requestBody = new HashMap<>(3);
//            requestBody.put("user", request.getUserId());
//            requestBody.put("text", request.getText());
//            requestBody.put("message_id", request.getMessageId());
//
//            // 获取响应，包括headers和body
//            WebClient.ResponseSpec responseSpec = webClient.post()
//                    .uri(url)
//                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + request.getApiKey())
//                    .bodyValue(requestBody)
//                    .retrieve()
//                    .onStatus(HttpStatusCode::isError, WebClientUtil::exceptionFunction);
//
//            // 获取响应头和数据
//            byte[] audioData = responseSpec.toEntity(byte[].class)
//                    .mapNotNull(responseEntity -> {
//                        // 设置 Content-Type
//                        String type = responseEntity.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
//                        response.setContentType(type != null ? type : "audio/mpeg");
//
//                        // 获取并设置 Content-Disposition
//                        String contentDisposition = responseEntity.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION);
//                        if (contentDisposition != null) {
//                            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);
//                        } else {
//                            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=audio.mp3");
//                        }
//
//                        return responseEntity.getBody();
//                    })
//                    .block();
//
//            if (audioData != null) {
//                response.getOutputStream().write(audioData);
//                response.getOutputStream().flush();
//            }
//
//        } catch (IOException | WebClientResponseException e) {
//            log.error("textToAudio error: {}", e.getMessage());
//            throw new DiftChatException(DiftChatExceptionEnum.DIFY_API_ERROR);
//        }
//    }
//
//    @Override
//    public DifyTextVO audioToText(AudioToTextRequest request) {
//        String url = ChatUriConstant.V1_AUDIO_TO_TEXT_URI;
//
//        try {
//            // 使用 WebClient 发送 POST 请求
//            // 构建 MultipartData 请求
//            MultipartFile file = request.getFile();
//
//            return webClient.post()
//                    .uri(url)
//                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + request.getApiKey())
//                    .contentType(MediaType.MULTIPART_FORM_DATA)
//                    .body(BodyInserters.fromMultipartData(
//                            new LinkedMultiValueMap<>() {{
//                                add("file", new MultipartInputStreamFileResource(
//                                        file.getInputStream(),
//                                        file.getOriginalFilename()
//                                ));
//                            }}
//                    ))
//                    .retrieve()
//                    .onStatus(HttpStatusCode::isError, WebClientUtil::exceptionFunction)
//                    .bodyToMono(DifyTextVO.class)
//                    .block();
//
//        } catch (IOException | WebClientResponseException e) {
//            log.error("audioToText error: {}", e.getMessage());
//            throw new DiftChatException(DiftChatExceptionEnum.DIFY_API_ERROR);
//        }
//    }
//
//
//}
