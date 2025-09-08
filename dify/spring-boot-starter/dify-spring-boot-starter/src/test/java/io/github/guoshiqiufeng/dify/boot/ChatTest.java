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
package io.github.guoshiqiufeng.dify.boot;

import io.github.guoshiqiufeng.dify.boot.base.BaseChatContainerTest;
import io.github.guoshiqiufeng.dify.chat.DifyChat;
import io.github.guoshiqiufeng.dify.chat.dto.request.*;
import io.github.guoshiqiufeng.dify.chat.dto.response.*;
import io.github.guoshiqiufeng.dify.chat.dto.response.parameter.Enabled;
import io.github.guoshiqiufeng.dify.chat.enums.AnnotationReplyActionEnum;
import io.github.guoshiqiufeng.dify.client.spring6.builder.DifyDatasetBuilder;
import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.core.pojo.response.MessagesResponseVO;
import io.github.guoshiqiufeng.dify.dataset.DifyDataset;
import io.github.guoshiqiufeng.dify.dataset.dto.response.TextEmbeddingListResponse;
import io.github.guoshiqiufeng.dify.dataset.dto.response.textembedding.TextEmbedding;
import io.github.guoshiqiufeng.dify.server.dto.response.DatasetApiKeyResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/4/7 14:47
 */
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ChatTest extends BaseChatContainerTest {

    @Resource
    private DifyChat difyChat;

    private static final String userId = "1504";
    private static String conversationId;
    private static String messageId;
    private static String taskId;
    private static String annotationId;
    private static String replyJobId;

    private static Boolean suggestedEnable;

    @Test
    @Order(1)
    @DisplayName("Test chat send")
    public void testChatSend() {
        ChatMessageSendRequest chatMessageSendRequest = new ChatMessageSendRequest();
        chatMessageSendRequest.setContent("hi");
        chatMessageSendRequest.setApiKey(apiKey);
        chatMessageSendRequest.setUserId(userId);
        ChatMessageSendResponse sendResponse = difyChat.send(chatMessageSendRequest);
        assertNotNull(sendResponse);

        // Store conversation and message IDs for subsequent tests
        if (sendResponse.getConversationId() != null) {
            conversationId = sendResponse.getConversationId();
        }
        if (sendResponse.getId() != null) {
            messageId = sendResponse.getId();
        }
    }

    @Test
    @Order(2)
    @DisplayName("Test chat message stream")
    public void testChatMessageStream() {
        ChatMessageSendRequest chatMessageSendRequest = new ChatMessageSendRequest();
        chatMessageSendRequest.setContent("tell me more");
        chatMessageSendRequest.setApiKey(apiKey);
        chatMessageSendRequest.setUserId(userId);
        chatMessageSendRequest.setConversationId(conversationId);

        Flux<ChatMessageSendCompletionResponse> responseFlux = difyChat.sendChatMessageStream(chatMessageSendRequest);

        StepVerifier.create(responseFlux.take(1))
                .assertNext(response -> {
                    assertNotNull(response);
                    taskId = response.getTaskId();
                })
                .verifyComplete();
    }

    @Test
    @Order(3)
    @DisplayName("Test stop message stream")
    public void testStopMessagesStream() {
        // This test might not actually verify the behavior due to the nature of streaming
        difyChat.stopMessagesStream(apiKey, taskId, userId);
    }

    @Test
    @Order(4)
    @DisplayName("Test message feedback")
    public void testMessageFeedback() {
        MessageFeedbackRequest request = new MessageFeedbackRequest();
        request.setApiKey(apiKey);
        request.setUserId(userId);
        request.setMessageId(messageId);
        request.setRating(MessageFeedbackRequest.Rating.LIKE);

        MessageFeedbackResponse response = difyChat.messageFeedback(request);
        assertNotNull(response);
    }

    @Test
    @Order(5)
    @DisplayName("Test feedbacks")
    public void testFeedbacks() {
        AppFeedbackPageRequest request = new AppFeedbackPageRequest();
        request.setApiKey(apiKey);

        DifyPageResult<AppFeedbackResponse> response = difyChat.feedbacks(request);
        assertNotNull(response);
        assertNotNull(response.getData());
        assertNotNull(response.getData().getFirst());
    }

    @Test
    @Order(6)
    @DisplayName("Test get conversations")
    public void testGetConversations() {
        MessageConversationsRequest request = new MessageConversationsRequest();
        request.setApiKey(apiKey);
        request.setUserId(userId);
        request.setLastId(null);
        request.setLimit(10);

        DifyPageResult<MessageConversationsResponse> result = difyChat.conversations(request);
        assertNotNull(result);
        assertFalse(result.getData().isEmpty(), "Should have at least one conversation");
    }

    @Test
    @Order(7)
    @DisplayName("Test get messages")
    public void testGetMessages() {
        MessagesRequest request = new MessagesRequest();
        request.setApiKey(apiKey);
        request.setUserId(userId);
        request.setConversationId(conversationId);
        request.setFirstId(null);
        request.setLimit(10);

        DifyPageResult<MessagesResponseVO> result = difyChat.messages(request);
        assertNotNull(result);
        assertFalse(result.getData().isEmpty(), "Should have at least one message");
    }

    @Test
    @Order(8)
    @DisplayName("Test rename conversation")
    public void testRenameConversation() {
        RenameConversationRequest request = new RenameConversationRequest();
        request.setApiKey(apiKey);
        request.setUserId(userId);
        request.setConversationId(conversationId);
        request.setName("Renamed Test Conversation");

        MessageConversationsResponse response = difyChat.renameConversation(request);
        assertNotNull(response);
    }

    @Test
    @Order(9)
    @DisplayName("Test get app parameters")
    public void testGetParameters() {
        AppParametersResponseVO response = difyChat.parameters(apiKey);
        assertNotNull(response);
        suggestedEnable = Optional.ofNullable(response.getSuggestedQuestionsAfterAnswer())
                .orElse(new Enabled(false))
                .getEnabled();
    }

    @Test
    @Order(10)
    @DisplayName("Test get suggested messages")
    public void testGetSuggestedMessages() {
        if (!suggestedEnable) {
            return;
        }
        List<String> suggestions = difyChat.messagesSuggested(messageId, apiKey, userId);
        assertNotNull(suggestions);
    }

    @Test
    @Order(11)
    @DisplayName("Test get site")
    public void testGetSite() {
        AppSiteResponse site = difyChat.site(apiKey);
        assertNotNull(site);
    }

    @Test
    @Order(12)
    @DisplayName("Test conversationVariables")
    public void testConversationVariables() {
        ConversationVariableRequest conversationVariableRequest = new ConversationVariableRequest();
        conversationVariableRequest.setConversationId(conversationId);
        conversationVariableRequest.setApiKey(apiKey);
        conversationVariableRequest.setUserId(userId);
        DifyPageResult<ConversationVariableResponse> conversationVariableResponseDifyPageResult = difyChat.conversationVariables(conversationVariableRequest);
    }

    @Test
    @Order(15)
    @DisplayName("Test delete conversation")
    public void testDeleteConversation() {
        difyChat.deleteConversation(conversationId, apiKey, userId);
    }

    @Test
    @Order(20)
    @DisplayName("Test file upload")
    public void testFileUpload() throws IOException {
        MultipartFile testFile = DatasetTest.createTestFile("chat.txt", MediaType.TEXT_PLAIN_VALUE);
        FileUploadRequest request = new FileUploadRequest(testFile);
        request.setApiKey(apiKey);
        request.setUserId(userId);
        difyChat.fileUpload(request);
    }

    @Test
    @Order(21)
    @DisplayName("Test info")
    public void testInfo() {
        AppInfoResponse info = difyChat.info(apiKey);
        assertNotNull(info);
    }

    @Test
    @Order(22)
    @DisplayName("Test meta")
    public void testMeta() {
        AppMetaResponse meta = difyChat.meta(apiKey);
    }

    @Test
    @Order(23)
    @DisplayName("Test create app annotation")
    public void testCreateAppAnnotation() {
        AppAnnotationCreateRequest request = new AppAnnotationCreateRequest();
        request.setApiKey(apiKey);
        request.setUserId(userId);
        request.setQuestion("Test annotation question");
        request.setAnswer("Test annotation answer");

        AppAnnotationResponse response = difyChat.createAppAnnotation(request);
        assertNotNull(response);
        assertNotNull(response.getId());
        annotationId = response.getId();
    }

    @Test
    @Order(24)
    @DisplayName("Test page app annotation")
    public void testPageAppAnnotation() {
        AppAnnotationPageRequest request = new AppAnnotationPageRequest();
        request.setApiKey(apiKey);
        request.setUserId(userId);
        request.setPage(1);
        request.setLimit(10);

        DifyPageResult<AppAnnotationResponse> result = difyChat.pageAppAnnotation(request);
        assertNotNull(result);
        assertFalse(result.getData().isEmpty(), "Should have at least one annotation");
    }

    @Test
    @Order(25)
    @DisplayName("Test update app annotation")
    public void testUpdateAppAnnotation() {
        AppAnnotationUpdateRequest request = new AppAnnotationUpdateRequest();
        request.setApiKey(apiKey);
        request.setUserId(userId);
        request.setAnnotationId(annotationId);
        request.setQuestion("Updated annotation question");
        request.setAnswer("Updated annotation answer");

        AppAnnotationResponse response = difyChat.updateAppAnnotation(request);
        assertNotNull(response);
    }

    @Test
    @Order(26)
    @DisplayName("Test delete app annotation")
    public void testDeleteAppAnnotation() {
        difyChat.deleteAppAnnotation(annotationId, apiKey);
    }

    @Test
    @Order(27)
    @DisplayName("Test annotation reply")
    public void testAnnotationReply() {
        List<DatasetApiKeyResponse> datasetApiKeys = difyServer.getDatasetApiKey();
        if (CollectionUtils.isEmpty(datasetApiKeys)) {
            datasetApiKeys = difyServer.initDatasetApiKey();
        }
        String token = datasetApiKeys.getFirst().getToken();
        DifyDataset difyDataset = DifyDatasetBuilder.create(DifyDatasetBuilder.DifyDatasetClientBuilder.builder()
                .baseUrl(difyProperties.getUrl())
                .restClientBuilder(RestClient.builder().defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .build()
        );
        TextEmbeddingListResponse textEmbeddingListResponse = difyDataset.listTextEmbedding();
        assertNotNull(textEmbeddingListResponse);
        TextEmbedding textEmbedding = textEmbeddingListResponse.getData().getFirst();


        // Test the annotation reply
        AppAnnotationReplyRequest request = new AppAnnotationReplyRequest();
        request.setApiKey(apiKey);
        request.setUserId(userId);
        request.setAction(AnnotationReplyActionEnum.enable);
        request.setEmbeddingProviderName(textEmbedding.getProvider());
        request.setEmbeddingModelName(textEmbedding.getModels().getFirst().getModel());
        request.setScoreThreshold(0.8f);

        AppAnnotationReplyResponse response = difyChat.annotationReply(request);
        assertNotNull(response);
        assertNotNull(response.getJobId());
        replyJobId = response.getJobId();
    }

    @Test
    @Order(28)
    @DisplayName("Test query annotation reply")
    public void testQueryAnnotationReply() {
        if (replyJobId == null) {
            return;
        }

        AppAnnotationReplyQueryRequest request = new AppAnnotationReplyQueryRequest();
        request.setApiKey(apiKey);
        request.setUserId(userId);
        request.setAction(AnnotationReplyActionEnum.enable);
        request.setJobId(replyJobId);

        AppAnnotationReplyResponse response = difyChat.queryAnnotationReply(request);
        assertNotNull(response);
        assertNotNull(response.getJobStatus());
    }
}
