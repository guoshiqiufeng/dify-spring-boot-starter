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
//package io.github.guoshiqiufeng.dify.chat.client;
//
//import io.github.guoshiqiufeng.dify.chat.dto.request.*;
//import io.github.guoshiqiufeng.dify.chat.dto.response.*;
//import io.github.guoshiqiufeng.dify.chat.dto.response.parameter.Enabled;
//import io.github.guoshiqiufeng.dify.chat.enums.AnnotationReplyActionEnum;
//import io.github.guoshiqiufeng.dify.chat.enums.IconTypeEnum;
//import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
//import io.github.guoshiqiufeng.dify.core.pojo.response.MessagesResponseVO;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.http.*;
//import org.springframework.web.multipart.MultipartFile;
//import reactor.core.publisher.Flux;
//import reactor.test.StepVerifier;
//
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//
///**
// * Tests for {@link DifyChatClient}.
// *
// * @author yanghq
// * @version 0.10.0
// * @since 2025/4/23 14:12
// */
//class DifyChatClientTest {
//
//
//    private static DifyChatClient difyChatClient;
//
//    @BeforeAll
//    public static void setup() {
//        difyChatClient = Mockito.mock(DifyChatClient.class);
//    }
//
//    @Test
//    void testChat() {
//        // Arrange
//        ChatMessageSendRequest request = new ChatMessageSendRequest();
//        request.setContent("Hello, how are you?");
//        request.setApiKey("x");
//
//        ChatMessageSendResponse expectedResponse = new ChatMessageSendResponse();
//        expectedResponse.setId("msg_123");
//        expectedResponse.setAnswer("I am doing well, thank you!");
//
//        when(difyChatClient.chat(any(ChatMessageSendRequest.class))).thenReturn(expectedResponse);
//
//        // Act
//        ChatMessageSendResponse actualResponse = difyChatClient.chat(request);
//
//        // Assert
//        assertNotNull(actualResponse);
//        assertEquals(expectedResponse.getId(), actualResponse.getId());
//        assertEquals(expectedResponse.getAnswer(), actualResponse.getAnswer());
//        verify(difyChatClient, times(1)).chat(any(ChatMessageSendRequest.class));
//    }
//
//    @Test
//    void testStreamingChat() {
//        // Arrange
//        ChatMessageSendRequest request = new ChatMessageSendRequest();
//        request.setContent("Tell me about AI");
//        request.setApiKey("streaming");
//
//        ChatMessageSendCompletionResponse response1 = new ChatMessageSendCompletionResponse();
//        response1.setEvent("message");
//        response1.setId("msg_123");
//        response1.setAnswer("Artificial");
//
//        ChatMessageSendCompletionResponse response2 = new ChatMessageSendCompletionResponse();
//        response2.setEvent("message");
//        response2.setId("msg_123");
//        response2.setAnswer(" Intelligence");
//
//        Flux<ChatMessageSendCompletionResponse> expectedFlux = Flux.just(response1, response2);
//
//        when(difyChatClient.streamingChat(any(ChatMessageSendRequest.class))).thenReturn(expectedFlux);
//
//        // Act
//        Flux<ChatMessageSendCompletionResponse> actualFlux = difyChatClient.streamingChat(request);
//
//        // Assert using StepVerifier
//        StepVerifier.create(actualFlux)
//                .expectNext(response1)
//                .expectNext(response2)
//                .verifyComplete();
//
//        verify(difyChatClient, times(1)).streamingChat(any(ChatMessageSendRequest.class));
//    }
//
//    @Test
//    void testStopMessagesStream() {
//        // Arrange
//        String apiKey = "test-api-key";
//        String taskId = "task_123";
//        String userId = "user_456";
//
//        doNothing().when(difyChatClient).stopMessagesStream(anyString(), anyString(), anyString());
//
//        // Act
//        difyChatClient.stopMessagesStream(apiKey, taskId, userId);
//
//        // Assert
//        verify(difyChatClient, times(1)).stopMessagesStream(apiKey, taskId, userId);
//    }
//
//    @Test
//    void testMessageFeedback() {
//        // Arrange
//        MessageFeedbackRequest request = new MessageFeedbackRequest();
//        request.setMessageId("msg_123");
//        request.setRating(MessageFeedbackRequest.Rating.LIKE);
//
//        MessageFeedbackResponse expectedResponse = new MessageFeedbackResponse();
//        expectedResponse.setResult("success");
//
//        when(difyChatClient.messageFeedback(any(MessageFeedbackRequest.class))).thenReturn(expectedResponse);
//
//        // Act
//        MessageFeedbackResponse actualResponse = difyChatClient.messageFeedback(request);
//
//        // Assert
//        assertNotNull(actualResponse);
//        assertEquals(expectedResponse.getResult(), actualResponse.getResult());
//        verify(difyChatClient, times(1)).messageFeedback(any(MessageFeedbackRequest.class));
//    }
//
//    @Test
//    void testConversations() {
//        // Arrange
//        MessageConversationsRequest request = new MessageConversationsRequest();
//        request.setLimit(10);
//
//        MessageConversationsResponse conversation = new MessageConversationsResponse();
//        conversation.setId("conv_123");
//        conversation.setName("Test Conversation");
//
//        DifyPageResult<MessageConversationsResponse> expectedResult = new DifyPageResult<>();
//        expectedResult.setData(List.of(conversation));
//        expectedResult.setLimit(10);
//        expectedResult.setHasMore(false);
//
//        when(difyChatClient.conversations(any(MessageConversationsRequest.class))).thenReturn(expectedResult);
//
//        // Act
//        DifyPageResult<MessageConversationsResponse> actualResult = difyChatClient.conversations(request);
//
//        // Assert
//        assertNotNull(actualResult);
//        assertEquals(expectedResult.getLimit(), actualResult.getLimit());
//        assertEquals(expectedResult.getHasMore(), actualResult.getHasMore());
//        assertEquals(expectedResult.getData().size(), actualResult.getData().size());
//        assertEquals(expectedResult.getData().get(0).getId(), actualResult.getData().get(0).getId());
//        verify(difyChatClient, times(1)).conversations(any(MessageConversationsRequest.class));
//    }
//
//    @Test
//    void testMessages() {
//        // Arrange
//        MessagesRequest request = new MessagesRequest();
//        request.setConversationId("conv_123");
//        request.setLimit(20);
//
//        MessagesResponseVO message = new MessagesResponseVO();
//        message.setId("msg_456");
//        message.setAnswer("This is a test message");
//
//        DifyPageResult<MessagesResponseVO> expectedResult = new DifyPageResult<>();
//        expectedResult.setData(List.of(message));
//        expectedResult.setLimit(20);
//        expectedResult.setHasMore(false);
//
//        when(difyChatClient.messages(any(MessagesRequest.class))).thenReturn(expectedResult);
//
//        // Act
//        DifyPageResult<MessagesResponseVO> actualResult = difyChatClient.messages(request);
//
//        // Assert
//        assertNotNull(actualResult);
//        assertEquals(expectedResult.getLimit(), actualResult.getLimit());
//        assertEquals(expectedResult.getData().size(), actualResult.getData().size());
//        assertEquals(expectedResult.getData().get(0).getId(), actualResult.getData().get(0).getId());
//        verify(difyChatClient, times(1)).messages(any(MessagesRequest.class));
//    }
//
//    @Test
//    void testMessagesSuggested() {
//        // Arrange
//        String messageId = "msg_123";
//        String apiKey = "test-api-key";
//        String userId = "user_456";
//
//        List<String> expectedSuggestions = Arrays.asList(
//                "Tell me more about this topic",
//                "Can you provide examples?",
//                "How does this work in practice?"
//        );
//
//        when(difyChatClient.messagesSuggested(anyString(), anyString(), anyString())).thenReturn(expectedSuggestions);
//
//        // Act
//        List<String> actualSuggestions = difyChatClient.messagesSuggested(messageId, apiKey, userId);
//
//        // Assert
//        assertNotNull(actualSuggestions);
//        assertEquals(expectedSuggestions.size(), actualSuggestions.size());
//        for (int i = 0; i < expectedSuggestions.size(); i++) {
//            assertEquals(expectedSuggestions.get(i), actualSuggestions.get(i));
//        }
//        verify(difyChatClient, times(1)).messagesSuggested(messageId, apiKey, userId);
//    }
//
//    @Test
//    void testDeleteConversation() {
//        // Arrange
//        String conversationId = "conv_123";
//        String apiKey = "test-api-key";
//        String userId = "user_456";
//
//        doNothing().when(difyChatClient).deleteConversation(anyString(), anyString(), anyString());
//
//        // Act
//        difyChatClient.deleteConversation(conversationId, apiKey, userId);
//
//        // Assert
//        verify(difyChatClient, times(1)).deleteConversation(conversationId, apiKey, userId);
//    }
//
//    @Test
//    void testTextToAudio() {
//        // Arrange
//        TextToAudioRequest request = new TextToAudioRequest();
//        request.setText("Convert this text to speech");
//
//        byte[] mockAudioData = "audio data".getBytes();
//        ResponseEntity<byte[]> expectedResponse = ResponseEntity.ok(mockAudioData);
//
//        when(difyChatClient.textToAudio(any(TextToAudioRequest.class))).thenReturn(expectedResponse);
//
//        // Act
//        ResponseEntity<byte[]> actualResponse = difyChatClient.textToAudio(request);
//
//        // Assert
//        assertNotNull(actualResponse);
//        assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
//        assertArrayEquals(expectedResponse.getBody(), actualResponse.getBody());
//        verify(difyChatClient, times(1)).textToAudio(any(TextToAudioRequest.class));
//    }
//
//    @Test
//    void testAudioToText() {
//        // Arrange
//        AudioToTextRequest request = new AudioToTextRequest();
//        // Set up request properties
//
//        DifyTextVO expectedResponse = new DifyTextVO();
//        expectedResponse.setText("Transcribed text from audio");
//
//        when(difyChatClient.audioToText(any(AudioToTextRequest.class))).thenReturn(expectedResponse);
//
//        // Act
//        DifyTextVO actualResponse = difyChatClient.audioToText(request);
//
//        // Assert
//        assertNotNull(actualResponse);
//        assertEquals(expectedResponse.getText(), actualResponse.getText());
//        verify(difyChatClient, times(1)).audioToText(any(AudioToTextRequest.class));
//    }
//
//    @Test
//    void testRenameConversation() {
//        // Arrange
//        RenameConversationRequest request = new RenameConversationRequest();
//        request.setConversationId("conv_123");
//        request.setName("New Conversation Name");
//
//        MessageConversationsResponse expectedResponse = new MessageConversationsResponse();
//        expectedResponse.setId("conv_123");
//        expectedResponse.setName("New Conversation Name");
//
//        when(difyChatClient.renameConversation(any(RenameConversationRequest.class))).thenReturn(expectedResponse);
//
//        // Act
//        MessageConversationsResponse actualResponse = difyChatClient.renameConversation(request);
//
//        // Assert
//        assertNotNull(actualResponse);
//        assertEquals(expectedResponse.getId(), actualResponse.getId());
//        assertEquals(expectedResponse.getName(), actualResponse.getName());
//        verify(difyChatClient, times(1)).renameConversation(any(RenameConversationRequest.class));
//    }
//
//    @Test
//    void testParameters() {
//        // Arrange
//        String apiKey = "test-api-key";
//
//        AppParametersResponseVO expectedResponse = new AppParametersResponseVO();
//        expectedResponse.setOpeningStatement("This is a openingStatement");
//        expectedResponse.setSpeechToText(new Enabled(false));
//
//        when(difyChatClient.parameters(anyString())).thenReturn(expectedResponse);
//
//        // Act
//        AppParametersResponseVO actualResponse = difyChatClient.parameters(apiKey);
//
//        // Assert
//        assertNotNull(actualResponse);
//        assertEquals(expectedResponse.getOpeningStatement(), actualResponse.getOpeningStatement());
//        assertEquals(expectedResponse.getSpeechToText(), actualResponse.getSpeechToText());
//        verify(difyChatClient, times(1)).parameters(apiKey);
//    }
//
//    @Test
//    void testFileUpload() {
//        // Arrange
//        String apiKey = "test-api-key";
//
//        FileUploadRequest request = new FileUploadRequest();
//        request.setFile(mock(MultipartFile.class));
//        request.setUserId("0524");
//        request.setApiKey(apiKey);
//
//        FileUploadResponse expectedResponse = new FileUploadResponse();
//        expectedResponse.setId("72fa9618-8f89-4a37-9b33-7e1178a24a67");
//        expectedResponse.setName("example.png");
//
//        when(difyChatClient.fileUpload(any(FileUploadRequest.class))).thenReturn(expectedResponse);
//
//        // Act
//        FileUploadResponse actualResponse = difyChatClient.fileUpload(request);
//
//        // Assert
//        assertNotNull(actualResponse);
//        assertEquals(expectedResponse.getId(), actualResponse.getId());
//        assertEquals(expectedResponse.getName(), actualResponse.getName());
//        verify(difyChatClient, times(1)).fileUpload(request);
//    }
//
//    @Test
//    void testFilePreview() {
//        // Arrange
//        String apiKey = "test-api-key";
//        String fileId = "test-file-id-123";
//
//        FilePreviewRequest request = new FilePreviewRequest(fileId);
//        request.setApiKey(apiKey);
//        request.setUserId("test-user-id");
//        request.setAsAttachment(false);
//
//        byte[] expectedContent = "file content".getBytes();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.IMAGE_PNG);
//        headers.setContentLength(expectedContent.length);
//
//        ResponseEntity<byte[]> expectedResponse = new ResponseEntity<>(expectedContent, headers, HttpStatus.OK);
//
//        when(difyChatClient.filePreview(any(FilePreviewRequest.class))).thenReturn(expectedResponse);
//
//        // Act
//        ResponseEntity<byte[]> actualResponse = difyChatClient.filePreview(request);
//
//        // Assert
//        assertNotNull(actualResponse);
//        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
//        assertArrayEquals(expectedContent, actualResponse.getBody());
//        assertEquals(MediaType.IMAGE_PNG, actualResponse.getHeaders().getContentType());
//        verify(difyChatClient, times(1)).filePreview(request);
//    }
//
//    @Test
//    void testFilePreviewAsAttachment() {
//        // Arrange
//        String apiKey = "test-api-key";
//        String fileId = "test-file-id-456";
//
//        FilePreviewRequest request = new FilePreviewRequest(fileId, true, apiKey, "test-user-id");
//
//        byte[] expectedContent = "attachment content".getBytes();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//        headers.setContentDisposition(ContentDisposition.attachment().filename("test-file.pdf").build());
//
//        ResponseEntity<byte[]> expectedResponse = new ResponseEntity<>(expectedContent, headers, HttpStatus.OK);
//
//        when(difyChatClient.filePreview(any(FilePreviewRequest.class))).thenReturn(expectedResponse);
//
//        // Act
//        ResponseEntity<byte[]> actualResponse = difyChatClient.filePreview(request);
//
//        // Assert
//        assertNotNull(actualResponse);
//        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
//        assertArrayEquals(expectedContent, actualResponse.getBody());
//        assertEquals(MediaType.APPLICATION_OCTET_STREAM, actualResponse.getHeaders().getContentType());
//        assertNotNull(actualResponse.getHeaders().getContentDisposition());
//        verify(difyChatClient, times(1)).filePreview(request);
//    }
//
//    @Test
//    void testInfo() {
//        // Arrange
//        String apiKey = "test-api-key";
//
//        AppInfoResponse expectedResponse = new AppInfoResponse();
//        expectedResponse.setName("My App");
//        expectedResponse.setDescription("This is my app.");
//
//        when(difyChatClient.info(any())).thenReturn(expectedResponse);
//
//        // Act
//        AppInfoResponse actualResponse = difyChatClient.info(apiKey);
//
//        // Assert
//        assertNotNull(actualResponse);
//        assertEquals(expectedResponse.getName(), actualResponse.getName());
//        assertEquals(expectedResponse.getDescription(), actualResponse.getDescription());
//        verify(difyChatClient, times(1)).info(apiKey);
//    }
//
//    @Test
//    void testMeta() {
//        // Arrange
//        String apiKey = "test-api-key";
//
//        AppMetaResponse expectedResponse = new AppMetaResponse();
//        Map<String, Object> maps = new HashMap<>(1);
//        maps.put("tools", "tools icon url");
//        maps.put("api", Map.of("background", "#252525"));
//        expectedResponse.setToolIcons(maps);
//
//        when(difyChatClient.meta(any())).thenReturn(expectedResponse);
//
//        // Act
//        AppMetaResponse actualResponse = difyChatClient.meta(apiKey);
//
//        // Assert
//        assertNotNull(actualResponse);
//        assertEquals(expectedResponse.getToolIcons(), actualResponse.getToolIcons());
//        verify(difyChatClient, times(1)).meta(apiKey);
//    }
//
//    @Test
//    void testPageAppAnnotation() {
//        // Arrange
//        AppAnnotationPageRequest request = new AppAnnotationPageRequest();
//        request.setApiKey("test-api-key");
//        request.setUserId("user-123");
//        request.setPage(1);
//        request.setLimit(10);
//
//        List<AppAnnotationResponse> annotations = new ArrayList<>();
//        AppAnnotationResponse annotation1 = new AppAnnotationResponse();
//        annotation1.setId("anno-1");
//        annotation1.setQuestion("What is AI?");
//        annotation1.setAnswer("Artificial Intelligence is...");
//        annotation1.setHitCount(5);
//        annotation1.setCreatedAt(1619712000000L);
//
//        AppAnnotationResponse annotation2 = new AppAnnotationResponse();
//        annotation2.setId("anno-2");
//        annotation2.setQuestion("How does machine learning work?");
//        annotation2.setAnswer("Machine learning works by...");
//        annotation2.setHitCount(3);
//        annotation2.setCreatedAt(1619712001000L);
//
//        annotations.add(annotation1);
//        annotations.add(annotation2);
//
//        DifyPageResult<AppAnnotationResponse> expectedResult = new DifyPageResult<>();
//        expectedResult.setData(annotations);
//        expectedResult.setHasMore(false);
//        expectedResult.setLimit(10);
//        expectedResult.setPage(1);
//
//        when(difyChatClient.pageAppAnnotation(any(AppAnnotationPageRequest.class))).thenReturn(expectedResult);
//
//        // Act
//        DifyPageResult<AppAnnotationResponse> actualResult = difyChatClient.pageAppAnnotation(request);
//
//        // Assert
//        assertNotNull(actualResult);
//        assertEquals(expectedResult.getLimit(), actualResult.getLimit());
//        assertEquals(expectedResult.getPage(), actualResult.getPage());
//        assertEquals(expectedResult.getHasMore(), actualResult.getHasMore());
//        assertEquals(expectedResult.getData().size(), actualResult.getData().size());
//        assertEquals(expectedResult.getData().get(0).getId(), actualResult.getData().get(0).getId());
//        assertEquals(expectedResult.getData().get(0).getQuestion(), actualResult.getData().get(0).getQuestion());
//        assertEquals(expectedResult.getData().get(0).getAnswer(), actualResult.getData().get(0).getAnswer());
//        assertEquals(expectedResult.getData().get(0).getHitCount(), actualResult.getData().get(0).getHitCount());
//        assertEquals(expectedResult.getData().get(0).getCreatedAt(), actualResult.getData().get(0).getCreatedAt());
//        verify(difyChatClient, times(1)).pageAppAnnotation(any(AppAnnotationPageRequest.class));
//    }
//
//    @Test
//    void testCreateAppAnnotation() {
//        // Arrange
//        AppAnnotationCreateRequest request = new AppAnnotationCreateRequest();
//        request.setApiKey("test-api-key");
//        request.setUserId("user-123");
//        request.setQuestion("What is natural language processing?");
//        request.setAnswer("Natural language processing (NLP) is a field of AI that focuses on...");
//
//        AppAnnotationResponse expectedResponse = new AppAnnotationResponse();
//        expectedResponse.setId("anno-new");
//        expectedResponse.setQuestion(request.getQuestion());
//        expectedResponse.setAnswer(request.getAnswer());
//        expectedResponse.setHitCount(0);
//        expectedResponse.setCreatedAt(System.currentTimeMillis());
//
//        when(difyChatClient.createAppAnnotation(any(AppAnnotationCreateRequest.class))).thenReturn(expectedResponse);
//
//        // Act
//        AppAnnotationResponse actualResponse = difyChatClient.createAppAnnotation(request);
//
//        // Assert
//        assertNotNull(actualResponse);
//        assertEquals(expectedResponse.getId(), actualResponse.getId());
//        assertEquals(expectedResponse.getQuestion(), actualResponse.getQuestion());
//        assertEquals(expectedResponse.getAnswer(), actualResponse.getAnswer());
//        assertEquals(expectedResponse.getHitCount(), actualResponse.getHitCount());
//        assertEquals(expectedResponse.getCreatedAt(), actualResponse.getCreatedAt());
//        verify(difyChatClient, times(1)).createAppAnnotation(any(AppAnnotationCreateRequest.class));
//    }
//
//    @Test
//    void testUpdateAppAnnotation() {
//        // Arrange
//        AppAnnotationUpdateRequest request = new AppAnnotationUpdateRequest();
//        request.setApiKey("test-api-key");
//        request.setUserId("user-123");
//        request.setAnnotationId("anno-1");
//        request.setQuestion("What is AI? (Updated)");
//        request.setAnswer("Artificial Intelligence is a field that... (Updated)");
//
//        AppAnnotationResponse expectedResponse = new AppAnnotationResponse();
//        expectedResponse.setId(request.getAnnotationId());
//        expectedResponse.setQuestion(request.getQuestion());
//        expectedResponse.setAnswer(request.getAnswer());
//        expectedResponse.setHitCount(6);
//        expectedResponse.setCreatedAt(1619712000000L);
//
//        when(difyChatClient.updateAppAnnotation(any(AppAnnotationUpdateRequest.class))).thenReturn(expectedResponse);
//
//        // Act
//        AppAnnotationResponse actualResponse = difyChatClient.updateAppAnnotation(request);
//
//        // Assert
//        assertNotNull(actualResponse);
//        assertEquals(expectedResponse.getId(), actualResponse.getId());
//        assertEquals(expectedResponse.getQuestion(), actualResponse.getQuestion());
//        assertEquals(expectedResponse.getAnswer(), actualResponse.getAnswer());
//        assertEquals(expectedResponse.getHitCount(), actualResponse.getHitCount());
//        assertEquals(expectedResponse.getCreatedAt(), actualResponse.getCreatedAt());
//        verify(difyChatClient, times(1)).updateAppAnnotation(any(AppAnnotationUpdateRequest.class));
//    }
//
//    @Test
//    void testDeleteAppAnnotation() {
//        // Arrange
//        String annotationId = "anno-2";
//        String apiKey = "test-api-key";
//
//        difyChatClient.deleteAppAnnotation(annotationId, apiKey);
//
//        // Assert
//        verify(difyChatClient, times(1)).deleteAppAnnotation(annotationId, apiKey);
//    }
//
//
//    @Test
//    void testAnnotationReply() {
//        // Arrange
//        AppAnnotationReplyRequest request = new AppAnnotationReplyRequest();
//        request.setApiKey("test-api-key");
//        request.setUserId("user-123");
//        request.setAction(AnnotationReplyActionEnum.enable);
//        request.setEmbeddingProviderName("openai");
//        request.setEmbeddingModelName("text-embedding-ada-002");
//        request.setScoreThreshold(0.8f);
//
//        AppAnnotationReplyResponse expectedResponse = new AppAnnotationReplyResponse();
//        expectedResponse.setJobId("b15c8f68-1cf4-4877-bf21-ed7cf2011802");
//        expectedResponse.setJobStatus("waiting");
//
//        when(difyChatClient.annotationReply(any(AppAnnotationReplyRequest.class))).thenReturn(expectedResponse);
//
//        // Act
//        AppAnnotationReplyResponse actualResponse = difyChatClient.annotationReply(request);
//
//        // Assert
//        assertNotNull(actualResponse);
//        assertEquals(expectedResponse.getJobId(), actualResponse.getJobId());
//        assertEquals(expectedResponse.getJobStatus(), actualResponse.getJobStatus());
//
//        verify(difyChatClient, times(1)).annotationReply(any(AppAnnotationReplyRequest.class));
//    }
//
//    @Test
//    void testQueryAnnotationReply() {
//        // Arrange
//        AppAnnotationReplyQueryRequest request = new AppAnnotationReplyQueryRequest();
//        request.setApiKey("test-api-key");
//        request.setUserId("user-123");
//        request.setAction(AnnotationReplyActionEnum.enable);
//        request.setJobId("job-123");
//
//        AppAnnotationReplyResponse expectedResponse = new AppAnnotationReplyResponse();
//        expectedResponse.setJobId("b15c8f68-1cf4-4877-bf21-ed7cf2011802");
//        expectedResponse.setJobStatus("waiting");
//        expectedResponse.setErrorMsg("");
//
//        when(difyChatClient.queryAnnotationReply(any(AppAnnotationReplyQueryRequest.class))).thenReturn(expectedResponse);
//
//        // Act
//        AppAnnotationReplyResponse actualResponse = difyChatClient.queryAnnotationReply(request);
//
//        // Assert
//        assertNotNull(actualResponse);
//        assertEquals(expectedResponse.getJobId(), actualResponse.getJobId());
//        assertEquals(expectedResponse.getJobStatus(), actualResponse.getJobStatus());
//
//        verify(difyChatClient, times(1)).queryAnnotationReply(any(AppAnnotationReplyQueryRequest.class));
//    }
//
//    @Test
//    void testSite() {
//        // Arrange
//        String apiKey = "test-api-key";
//
//        AppSiteResponse expectedResponse = new AppSiteResponse();
//        expectedResponse.setTitle("Test App");
//        expectedResponse.setChatColorTheme("#FF5733");
//        expectedResponse.setChatColorThemeInverted(true);
//        expectedResponse.setIconType(IconTypeEnum.emoji);
//        expectedResponse.setIcon("🤖");
//        expectedResponse.setIconBackground("#FFFFFF");
//        expectedResponse.setIconUrl("https://example.com/icon.png");
//        expectedResponse.setDescription("Test description");
//        expectedResponse.setCopyright("© 2024 Test Company");
//        expectedResponse.setPrivacyPolicy("https://example.com/privacy");
//        expectedResponse.setCustomDisclaimer("Test disclaimer");
//        expectedResponse.setDefaultLanguage("en-US");
//        expectedResponse.setShowWorkflowSteps(true);
//        expectedResponse.setUseIconAsAnswerIcon(false);
//
//        when(difyChatClient.site(anyString())).thenReturn(expectedResponse);
//
//        // Act
//        AppSiteResponse actualResponse = difyChatClient.site(apiKey);
//
//        // Assert
//        assertNotNull(actualResponse);
//        assertEquals(expectedResponse.getTitle(), actualResponse.getTitle());
//        assertEquals(expectedResponse.getChatColorTheme(), actualResponse.getChatColorTheme());
//        assertEquals(expectedResponse.getChatColorThemeInverted(), actualResponse.getChatColorThemeInverted());
//        assertEquals(expectedResponse.getIconType(), actualResponse.getIconType());
//        assertEquals(expectedResponse.getIcon(), actualResponse.getIcon());
//        assertEquals(expectedResponse.getIconBackground(), actualResponse.getIconBackground());
//        assertEquals(expectedResponse.getIconUrl(), actualResponse.getIconUrl());
//        assertEquals(expectedResponse.getDescription(), actualResponse.getDescription());
//        assertEquals(expectedResponse.getCopyright(), actualResponse.getCopyright());
//        assertEquals(expectedResponse.getPrivacyPolicy(), actualResponse.getPrivacyPolicy());
//        assertEquals(expectedResponse.getCustomDisclaimer(), actualResponse.getCustomDisclaimer());
//        assertEquals(expectedResponse.getDefaultLanguage(), actualResponse.getDefaultLanguage());
//        assertEquals(expectedResponse.getShowWorkflowSteps(), actualResponse.getShowWorkflowSteps());
//        assertEquals(expectedResponse.getUseIconAsAnswerIcon(), actualResponse.getUseIconAsAnswerIcon());
//        verify(difyChatClient, times(1)).site(apiKey);
//    }
//
//    @Test
//    void testFeedbacks() {
//        // Arrange
//        AppFeedbackPageRequest request = new AppFeedbackPageRequest();
//        request.setApiKey("test-api-key");
//        request.setUserId("user-123");
//        request.setPage(1);
//        request.setLimit(20);
//
//        DifyPageResult<AppFeedbackResponse> expectedResponse = new DifyPageResult<>();
//        expectedResponse.setData(new ArrayList<>());
//
//        AppFeedbackResponse feedback = new AppFeedbackResponse();
//        feedback.setId("8c0fbed8-e2f9-49ff-9f0e-15a35bdd0e25");
//        feedback.setAppId("f252d396-fe48-450e-94ec-e184218e7346");
//        feedback.setConversationId("2397604b-9deb-430e-b285-4726e51fd62d");
//        feedback.setMessageId("709c0b0f-0a96-4a4e-91a4-ec0889937b11");
//        feedback.setRating("like");
//        feedback.setContent("message feedback information-3");
//        feedback.setFromSource("user");
//        feedback.setFromEndUserId("74286412-9a1a-42c1-929c-01edb1d381d5");
//        feedback.setFromAccountId(null);
//
//        expectedResponse.getData().add(feedback);
//
//        when(difyChatClient.feedbacks(any(AppFeedbackPageRequest.class))).thenReturn(expectedResponse);
//
//        // Act
//        DifyPageResult<AppFeedbackResponse> actualResponse = difyChatClient.feedbacks(request);
//
//        // Assert
//        assertNotNull(actualResponse);
//        assertNotNull(actualResponse.getData());
//        assertEquals(1, actualResponse.getData().size());
//
//        AppFeedbackResponse actualFeedback = actualResponse.getData().get(0);
//        assertEquals(feedback.getId(), actualFeedback.getId());
//        assertEquals(feedback.getAppId(), actualFeedback.getAppId());
//        assertEquals(feedback.getConversationId(), actualFeedback.getConversationId());
//        assertEquals(feedback.getMessageId(), actualFeedback.getMessageId());
//        assertEquals(feedback.getRating(), actualFeedback.getRating());
//        assertEquals(feedback.getContent(), actualFeedback.getContent());
//        assertEquals(feedback.getFromSource(), actualFeedback.getFromSource());
//        assertEquals(feedback.getFromEndUserId(), actualFeedback.getFromEndUserId());
//        assertEquals(feedback.getFromAccountId(), actualFeedback.getFromAccountId());
//
//        verify(difyChatClient, times(1)).feedbacks(any(AppFeedbackPageRequest.class));
//    }
//
//    @Test
//    void testConversationVariables() {
//        // Arrange
//        ConversationVariableRequest request = new ConversationVariableRequest();
//        request.setApiKey("test-api-key");
//        request.setUserId("user-123");
//        request.setConversationId("conv-123456");
//        request.setVariableName("customer_name");
//
//        DifyPageResult<ConversationVariableResponse> expectedResponse = new DifyPageResult<>();
//        expectedResponse.setData(new ArrayList<>());
//
//        ConversationVariableResponse variable1 = new ConversationVariableResponse();
//        variable1.setId("variable-uuid-1");
//        variable1.setName("customer_name");
//        variable1.setValueType("string");
//        variable1.setValue("John Doe");
//        variable1.setDescription("客户名称（从对话中提取）");
//        variable1.setCreatedAt(1650000000000L);
//        variable1.setUpdatedAt(1650000000000L);
//
//        ConversationVariableResponse variable2 = new ConversationVariableResponse();
//        variable2.setId("variable-uuid-2");
//        variable2.setName("order_details");
//        variable2.setValueType("json");
//        variable2.setValue("{\"product\":\"Widget\",\"quantity\":5,\"price\":19.99}");
//        variable2.setDescription("客户的订单详情");
//        variable2.setCreatedAt(1650000000000L);
//        variable2.setUpdatedAt(1650000000000L);
//
//        expectedResponse.getData().add(variable1);
//        expectedResponse.getData().add(variable2);
//
//        when(difyChatClient.conversationVariables(any(ConversationVariableRequest.class))).thenReturn(expectedResponse);
//
//        // Act
//        DifyPageResult<ConversationVariableResponse> actualResponse = difyChatClient.conversationVariables(request);
//
//        // Assert
//        assertNotNull(actualResponse);
//        assertNotNull(actualResponse.getData());
//        assertEquals(2, actualResponse.getData().size());
//
//        ConversationVariableResponse actualVariable1 = actualResponse.getData().get(0);
//        assertEquals(variable1.getId(), actualVariable1.getId());
//        assertEquals(variable1.getName(), actualVariable1.getName());
//        assertEquals(variable1.getValueType(), actualVariable1.getValueType());
//        assertEquals(variable1.getValue(), actualVariable1.getValue());
//        assertEquals(variable1.getDescription(), actualVariable1.getDescription());
//        assertEquals(variable1.getCreatedAt(), actualVariable1.getCreatedAt());
//        assertEquals(variable1.getUpdatedAt(), actualVariable1.getUpdatedAt());
//
//        ConversationVariableResponse actualVariable2 = actualResponse.getData().get(1);
//        assertEquals(variable2.getId(), actualVariable2.getId());
//        assertEquals(variable2.getName(), actualVariable2.getName());
//        assertEquals(variable2.getValueType(), actualVariable2.getValueType());
//        assertEquals(variable2.getValue(), actualVariable2.getValue());
//        assertEquals(variable2.getDescription(), actualVariable2.getDescription());
//        assertEquals(variable2.getCreatedAt(), actualVariable2.getCreatedAt());
//        assertEquals(variable2.getUpdatedAt(), actualVariable2.getUpdatedAt());
//
//        verify(difyChatClient, times(1)).conversationVariables(any(ConversationVariableRequest.class));
//    }
//
//    @Test
//    void testUpdateConversationVariable() {
//        // Arrange
//        UpdateConversationVariableRequest request = new UpdateConversationVariableRequest();
//        request.setApiKey("test-api-key");
//        request.setUserId("user-123");
//        request.setConversationId("conv-123456");
//        request.setVariableId("variable-uuid-1");
//        request.setValue("Updated Value");
//
//        ConversationVariableResponse expectedResponse = new ConversationVariableResponse();
//        expectedResponse.setId("variable-uuid-1");
//        expectedResponse.setName("customer_name");
//        expectedResponse.setValueType("string");
//        expectedResponse.setValue("Updated Value");
//        expectedResponse.setDescription("客户名称（从对话中提取）");
//        expectedResponse.setCreatedAt(1650000000000L);
//        expectedResponse.setUpdatedAt(1650000001000L);
//
//        when(difyChatClient.updateConversationVariable(any(UpdateConversationVariableRequest.class))).thenReturn(expectedResponse);
//
//        // Act
//        ConversationVariableResponse actualResponse = difyChatClient.updateConversationVariable(request);
//
//        // Assert
//        assertNotNull(actualResponse);
//        assertEquals(expectedResponse.getId(), actualResponse.getId());
//        assertEquals(expectedResponse.getName(), actualResponse.getName());
//        assertEquals(expectedResponse.getValueType(), actualResponse.getValueType());
//        assertEquals(expectedResponse.getValue(), actualResponse.getValue());
//        assertEquals(expectedResponse.getDescription(), actualResponse.getDescription());
//        assertEquals(expectedResponse.getCreatedAt(), actualResponse.getCreatedAt());
//        assertEquals(expectedResponse.getUpdatedAt(), actualResponse.getUpdatedAt());
//
//        verify(difyChatClient, times(1)).updateConversationVariable(any(UpdateConversationVariableRequest.class));
//    }
//}
