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
import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.core.pojo.response.MessagesResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import javax.annotation.Resource;
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

    //@Test
    //@Order(6)
    //@DisplayName("Test get messages")
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
    @Order(12)
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
}
