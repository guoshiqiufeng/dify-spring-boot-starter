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

import io.github.guoshiqiufeng.dify.chat.constant.ChatUriConstant;
import io.github.guoshiqiufeng.dify.chat.dto.request.*;
import io.github.guoshiqiufeng.dify.chat.dto.response.*;
import io.github.guoshiqiufeng.dify.client.spring5.BaseClientTest;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.core.pojo.DifyResult;
import io.github.guoshiqiufeng.dify.core.pojo.request.ChatMessageVO;
import io.github.guoshiqiufeng.dify.core.pojo.response.MessagesResponseVO;
import io.github.guoshiqiufeng.dify.dataset.constant.DatasetUriConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link DifyChatDefaultClient}.
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/24 14:29
 */
@SuppressWarnings("unchecked")
public class DifyChatDefaultClientTest extends BaseClientTest {

    private DifyChatDefaultClient difyChatDefaultClient;

    @BeforeEach
    public void setup() {
        super.setup();
        // Create real client with mocked WebClient
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        difyChatDefaultClient = new DifyChatDefaultClient("https://api.dify.ai", clientConfig, webClientBuilderMock);
    }

    @Test
    public void testFileUpload() {
        // Prepare test data
        String apiKey = "test-api-key";
        String userId = "test-user-id";
        String fileName = "test-file.txt";
        String fileContent = "Test file content";
        String fileId = "file-123456";

        // Create mock file
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn(fileName);
        when(mockFile.getContentType()).thenReturn("text/plain");
        try {
            when(mockFile.getBytes()).thenReturn(fileContent.getBytes());
            when(mockFile.getInputStream()).thenReturn(new java.io.ByteArrayInputStream(fileContent.getBytes()));
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }

        // Create request
        FileUploadRequest request = new FileUploadRequest();
        request.setApiKey(apiKey);
        request.setUserId(userId);
        request.setFile(mockFile);

        // Create expected response
        FileUploadResponse expectedResponse = new FileUploadResponse();
        expectedResponse.setId(fileId);
        expectedResponse.setName(fileName);
        expectedResponse.setSize(fileContent.length());
        expectedResponse.setMimeType("text/plain");

        // Set up the response mock to return our expected response
        when(responseSpecMock.bodyToMono(FileUploadResponse.class)).thenReturn(Mono.just(expectedResponse));

        // Execute the method
        FileUploadResponse actualResponse = difyChatDefaultClient.fileUpload(request);

        // Verify the result
        assertEquals(fileId, actualResponse.getId());
        assertEquals(fileName, actualResponse.getName());
        assertEquals(fileContent.length(), actualResponse.getSize());
        assertEquals("text/plain", actualResponse.getMimeType());

        // Verify WebClient interactions
        verify(webClientMock).post();
        verify(requestBodyUriSpecMock).uri(DatasetUriConstant.V1_FILES_UPLOAD);

        // Verify content type is set correctly
        verify(requestBodySpecMock).contentType(MediaType.MULTIPART_FORM_DATA);

        // Verify body is set
        verify(requestBodySpecMock).bodyValue(any());

        // Verify response handling
        verify(responseSpecMock).bodyToMono(FileUploadResponse.class);
    }

    @Test
    public void testInfo() {
        String apiKey = "test-api-key";

        // Create expected response
        AppInfoResponse expectedResponse = new AppInfoResponse();
        expectedResponse.setName("My App");
        expectedResponse.setDescription("This is my app.");

        // Set up the response mock to return our expected response
        when(responseSpecMock.bodyToMono(AppInfoResponse.class)).thenReturn(Mono.just(expectedResponse));

        // Execute the method
        AppInfoResponse actualResponse = difyChatDefaultClient.info(apiKey);

        // Verify the result
        assertEquals(expectedResponse.getName(), actualResponse.getName());
        assertEquals(expectedResponse.getDescription(), actualResponse.getDescription());

        // Verify WebClient interactions
        verify(webClientMock).get();
        verify(requestHeadersUriSpecMock).uri(DatasetUriConstant.V1_INFO);

        // Verify response handling
        verify(responseSpecMock).bodyToMono(AppInfoResponse.class);
    }

    @Test
    public void testMeta() {
        String apiKey = "test-api-key";

        // Create expected response
        AppMetaResponse expectedResponse = new AppMetaResponse();
        Map<String, Object> maps = new HashMap<>(1);
        maps.put("tools", "tools icon url");
        maps.put("api", Map.of("background", "#252525"));
        expectedResponse.setToolIcons(maps);

        // Set up the response mock to return our expected response
        when(responseSpecMock.bodyToMono(AppMetaResponse.class)).thenReturn(Mono.just(expectedResponse));

        // Execute the method
        AppMetaResponse actualResponse = difyChatDefaultClient.meta(apiKey);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getToolIcons(), actualResponse.getToolIcons());

        // Verify WebClient interactions
        verify(webClientMock).get();
        verify(requestHeadersUriSpecMock).uri(DatasetUriConstant.V1_META);

        // Verify response handling
        verify(responseSpecMock).bodyToMono(AppMetaResponse.class);
    }

    @Test
    public void testChat() {
        // Prepare test data
        String apiKey = "test-api-key";
        String userId = "test-user-id";
        String content = "Hello, how are you?";
        String conversationId = "conv-123456";

        // Create request
        ChatMessageSendRequest request = new ChatMessageSendRequest();
        request.setApiKey(apiKey);
        request.setUserId(userId);
        request.setContent(content);
        request.setConversationId(conversationId);
        request.setInputs(new HashMap<>());

        // Create expected response
        ChatMessageSendResponse expectedResponse = new ChatMessageSendResponse();
        expectedResponse.setId("msg-123456");
        expectedResponse.setConversationId(conversationId);
        expectedResponse.setAnswer("I'm doing well, thank you for asking!");

        // Mock response
        when(responseSpecMock.bodyToMono(ChatMessageSendResponse.class))
                .thenReturn(Mono.just(expectedResponse));

        // Execute the method
        ChatMessageSendResponse actualResponse = difyChatDefaultClient.chat(request);

        // Verify results
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getConversationId(), actualResponse.getConversationId());
        assertEquals(expectedResponse.getAnswer(), actualResponse.getAnswer());

        // Verify WebClient interactions
        verify(webClientMock).post();
        verify(requestBodyUriSpecMock).uri(ChatUriConstant.V1_CHAT_MESSAGES_URI);
        verify(requestBodySpecMock).bodyValue(any(ChatMessageVO.class));
        verify(responseSpecMock).bodyToMono(ChatMessageSendResponse.class);
    }

    @Test
    public void testChatAndFile() {
        // Prepare test data
        String apiKey = "test-api-key";
        String userId = "test-user-id";
        String content = "Hello, how are you?";
        String conversationId = "conv-123456";

        // Create request
        ChatMessageSendRequest request = new ChatMessageSendRequest();
        request.setApiKey(apiKey);
        request.setUserId(userId);
        request.setContent(content);
        request.setConversationId(conversationId);
        request.setInputs(new HashMap<>());
        ChatMessageSendRequest.ChatMessageFile workflowFile = new ChatMessageSendRequest.ChatMessageFile();
        workflowFile.setUrl("https://file.com");
        workflowFile.setType("image");
        request.setFiles(List.of(workflowFile));
        // Create expected response
        ChatMessageSendResponse expectedResponse = new ChatMessageSendResponse();
        expectedResponse.setId("msg-123456");
        expectedResponse.setConversationId(conversationId);
        expectedResponse.setAnswer("I'm doing well, thank you for asking!");

        // Mock response
        when(responseSpecMock.bodyToMono(ChatMessageSendResponse.class))
                .thenReturn(Mono.just(expectedResponse));

        // Execute the method
        ChatMessageSendResponse actualResponse = difyChatDefaultClient.chat(request);

        // Verify results
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getConversationId(), actualResponse.getConversationId());
        assertEquals(expectedResponse.getAnswer(), actualResponse.getAnswer());

        // Verify WebClient interactions
        verify(webClientMock).post();
        verify(requestBodyUriSpecMock).uri(ChatUriConstant.V1_CHAT_MESSAGES_URI);
        verify(requestBodySpecMock).bodyValue(any(ChatMessageVO.class));
        verify(responseSpecMock).bodyToMono(ChatMessageSendResponse.class);
    }

    @Test
    public void testConversations() {
        // Prepare test data
        String apiKey = "test-api-key";
        String userId = "test-user-id";
        String sortBy = "-updated_at";
        Integer limit = 20;

        // Create request
        MessageConversationsRequest request = new MessageConversationsRequest();
        request.setApiKey(apiKey);
        request.setUserId(userId);
        request.setSortBy(sortBy);
        request.setLimit(limit);

        // Create expected response
        DifyPageResult<MessageConversationsResponse> expectedResponse = new DifyPageResult<>();
        List<MessageConversationsResponse> items = new ArrayList<>();
        MessageConversationsResponse item = new MessageConversationsResponse();
        item.setId("conv-123456");
        item.setName("Test Conversation");
        items.add(item);
        expectedResponse.setData(items);
        expectedResponse.setHasMore(false);

        // Mock response
        when(responseSpecMock.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedResponse));

        // Execute the method
        DifyPageResult<MessageConversationsResponse> actualResponse = difyChatDefaultClient.conversations(request);

        // Verify results
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getData().size(), actualResponse.getData().size());
        assertEquals(expectedResponse.getData().get(0).getId(), actualResponse.getData().get(0).getId());
        assertEquals(expectedResponse.getData().get(0).getName(), actualResponse.getData().get(0).getName());

        // Verify WebClient interactions - updated to match actual implementation
        verify(webClientMock).get();
        verify(requestHeadersUriSpecMock).uri(any(Function.class));
        verify(requestHeadersSpecMock).header(eq(HttpHeaders.AUTHORIZATION), eq("Bearer " + apiKey));
        verify(requestHeadersSpecMock).retrieve();
        verify(responseSpecMock).bodyToMono(any(ParameterizedTypeReference.class));
    }

    @Test
    public void testMessages() {
        // Prepare test data
        String apiKey = "test-api-key";
        String userId = "test-user-id";
        String conversationId = "conv-123456";
        Integer limit = 20;

        // Create request
        MessagesRequest request = new MessagesRequest();
        request.setApiKey(apiKey);
        request.setUserId(userId);
        request.setConversationId(conversationId);
        request.setLimit(limit);

        // Create expected response
        DifyPageResult<MessagesResponseVO> expectedResponse = new DifyPageResult<>();
        List<MessagesResponseVO> items = new ArrayList<>();
        MessagesResponseVO item = new MessagesResponseVO();
        item.setId("msg-123456");
        item.setConversationId(conversationId);
        item.setAnswer("This is a test message");
        items.add(item);
        expectedResponse.setData(items);
        expectedResponse.setHasMore(false);

        // Mock response
        when(responseSpecMock.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedResponse));

        // Execute the method
        DifyPageResult<MessagesResponseVO> actualResponse = difyChatDefaultClient.messages(request);

        // Verify results
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getData().size(), actualResponse.getData().size());
        assertEquals(expectedResponse.getData().get(0).getId(), actualResponse.getData().get(0).getId());
        assertEquals(expectedResponse.getData().get(0).getConversationId(), actualResponse.getData().get(0).getConversationId());
        assertEquals(expectedResponse.getData().get(0).getAnswer(), actualResponse.getData().get(0).getAnswer());

        // Verify WebClient interactions
        verify(webClientMock).get();
        verify(responseSpecMock).bodyToMono(any(ParameterizedTypeReference.class));
    }

    @Test
    public void testParameters() {
        // Prepare test data
        String apiKey = "test-api-key";

        // Create expected response
        AppParametersResponseVO expectedResponse = new AppParametersResponseVO();
        expectedResponse.setOpeningStatement("You are a helpful assistant");

        // Mock response
        when(responseSpecMock.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedResponse));

        // Execute the method
        AppParametersResponseVO actualResponse = difyChatDefaultClient.parameters(apiKey);

        // Verify results
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getOpeningStatement(), actualResponse.getOpeningStatement());

        // Verify WebClient interactions
        verify(webClientMock).get();
        verify(requestHeadersUriSpecMock).uri(ChatUriConstant.V1_PARAMETERS_URI);
        verify(responseSpecMock).bodyToMono(any(ParameterizedTypeReference.class));
    }

    @Test
    public void testRenameConversation() {
        // Prepare test data
        String apiKey = "test-api-key";
        String userId = "test-user-id";
        String conversationId = "conv-123456";
        String newName = "Renamed Conversation";
        boolean autoGenerate = false;

        // Create request
        RenameConversationRequest request = new RenameConversationRequest();
        request.setApiKey(apiKey);
        request.setUserId(userId);
        request.setConversationId(conversationId);
        request.setName(newName);
        request.setAutoGenerate(autoGenerate);

        // Create expected response
        MessageConversationsResponse expectedResponse = new MessageConversationsResponse();
        expectedResponse.setId(conversationId);
        expectedResponse.setName(newName);

        // Mock response
        when(responseSpecMock.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedResponse));

        // Execute the method
        MessageConversationsResponse actualResponse = difyChatDefaultClient.renameConversation(request);

        // Verify results
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getName(), actualResponse.getName());

        // Verify WebClient interactions
        verify(webClientMock).post();
        verify(requestBodyUriSpecMock).uri(ChatUriConstant.V1_CONVERSATIONS_URI + "/{conversationId}/name", conversationId);
        verify(requestBodySpecMock).bodyValue(any(Map.class));
        verify(responseSpecMock).bodyToMono(any(ParameterizedTypeReference.class));
    }

    @Test
    public void testMessagesSuggested() {
        // Prepare test data
        String apiKey = "test-api-key";
        String userId = "test-user-id";
        String messageId = "msg-123456";

        // Create expected response
        DifyResult<List<String>> expectedResponse = new DifyResult<>();
        List<String> suggestions = new ArrayList<>();
        suggestions.add("Tell me more about this");
        suggestions.add("What are the alternatives?");
        expectedResponse.setData(suggestions);

        // Mock response
        when(responseSpecMock.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedResponse));

        // Execute the method
        List<String> actualResponse = difyChatDefaultClient.messagesSuggested(messageId, apiKey, userId);

        // Verify results
        assertNotNull(actualResponse);
        assertEquals(2, actualResponse.size());
        assertEquals("Tell me more about this", actualResponse.get(0));
        assertEquals("What are the alternatives?", actualResponse.get(1));

        // Verify WebClient interactions
        verify(webClientMock).get();
        verify(requestHeadersUriSpecMock).uri(
                ChatUriConstant.V1_MESSAGES_URI + "/{messageId}/suggested?user={user}",
                messageId,
                userId
        );
        verify(responseSpecMock).bodyToMono(any(ParameterizedTypeReference.class));
    }

    @Test
    public void testDeleteConversation() {
        // Prepare test data
        String apiKey = "test-api-key";
        String userId = "test-user-id";
        String conversationId = "conv-123456";

        // Execute the method
        difyChatDefaultClient.deleteConversation(conversationId, apiKey, userId);

        // Verify WebClient interactions
        verify(webClientMock).method(HttpMethod.DELETE);
        verify(requestBodyUriSpecMock).uri(ChatUriConstant.V1_CONVERSATIONS_URI + "/{conversationId}", conversationId);
        verify(requestBodySpecMock).contentType(MediaType.APPLICATION_JSON);
        verify(requestBodySpecMock).bodyValue(any(Map.class));
    }

    @Test
    public void testTextToAudio() {
        // Prepare test data
        String apiKey = "test-api-key";
        String userId = "test-user-id";
        String messageId = "msg-123456";
        String text = "This is text to be converted to audio";

        // Create request
        TextToAudioRequest request = new TextToAudioRequest();
        request.setApiKey(apiKey);
        request.setUserId(userId);
        request.setMessageId(messageId);
        request.setText(text);

        // Create expected response
        byte[] audioData = "mock audio data".getBytes();
        ResponseEntity<byte[]> expectedResponse = ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(audioData);

        // Mock response
        when(responseSpecMock.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedResponse));

        // Execute the method
        ResponseEntity<byte[]> actualResponse = difyChatDefaultClient.textToAudio(request);

        // Verify results
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
        assertArrayEquals(expectedResponse.getBody(), actualResponse.getBody());

        // Verify WebClient interactions
        verify(webClientMock).post();
        verify(requestBodyUriSpecMock).uri(ChatUriConstant.V1_TEXT_TO_AUDIO_URI);
        verify(requestBodySpecMock).bodyValue(any(Map.class));
        verify(responseSpecMock).bodyToMono(any(ParameterizedTypeReference.class));
    }

    @Test
    public void testMessageFeedback() {
        // Prepare test data
        String apiKey = "test-api-key";
        String userId = "test-user-id";
        String messageId = "msg-123456";
        MessageFeedbackRequest.Rating rating = MessageFeedbackRequest.Rating.LIKE;
        String content = "This was helpful";

        // Create request
        MessageFeedbackRequest request = new MessageFeedbackRequest();
        request.setApiKey(apiKey);
        request.setUserId(userId);
        request.setMessageId(messageId);
        request.setRating(rating);
        request.setContent(content);

        // Create expected response
        MessageFeedbackResponse expectedResponse = new MessageFeedbackResponse();
        expectedResponse.setResult("success");

        // Mock response
        when(responseSpecMock.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedResponse));

        // Execute the method
        MessageFeedbackResponse actualResponse = difyChatDefaultClient.messageFeedback(request);

        // Verify results
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getResult(), actualResponse.getResult());

        // Verify WebClient interactions
        verify(webClientMock).post();
        verify(requestBodyUriSpecMock).uri(ChatUriConstant.V1_MESSAGES_URI + "/{messageId}/feedbacks", messageId);
        verify(requestBodySpecMock).bodyValue(any(Map.class));
        verify(responseSpecMock).bodyToMono(any(ParameterizedTypeReference.class));
    }

    @Test
    public void testAudioToText() {
        // Prepare test data
        String apiKey = "test-api-key";
        String fileName = "audio.mp3";
        String audioContent = "mock audio data";

        // Create mock file
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn(fileName);
        when(mockFile.getContentType()).thenReturn("audio/mpeg");
        try {
            when(mockFile.getBytes()).thenReturn(audioContent.getBytes());
            when(mockFile.getInputStream()).thenReturn(new java.io.ByteArrayInputStream(audioContent.getBytes()));
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }

        // Create request
        AudioToTextRequest request = new AudioToTextRequest();
        request.setApiKey(apiKey);
        request.setFile(mockFile);

        // Create expected response
        DifyTextVO expectedResponse = new DifyTextVO();
        expectedResponse.setText("Transcribed text from audio");

        // Mock response
        when(responseSpecMock.bodyToMono(DifyTextVO.class)).thenReturn(Mono.just(expectedResponse));

        // Execute the method
        DifyTextVO actualResponse = difyChatDefaultClient.audioToText(request);

        // Verify results
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getText(), actualResponse.getText());

        // Verify WebClient interactions
        verify(webClientMock).post();
        verify(requestBodyUriSpecMock).uri(ChatUriConstant.V1_AUDIO_TO_TEXT_URI);
        verify(requestBodySpecMock).contentType(MediaType.MULTIPART_FORM_DATA);
        verify(requestBodySpecMock).bodyValue(any());
        verify(responseSpecMock).bodyToMono(DifyTextVO.class);
    }

    @Test
    public void testPageAppAnnotation() {
        // Prepare test data
        String apiKey = "test-api-key";
        int page = 1;
        int limit = 20;

        // Create request
        AppAnnotationPageRequest request = new AppAnnotationPageRequest();
        request.setApiKey(apiKey);
        request.setPage(page);
        request.setLimit(limit);

        // Create expected response
        DifyPageResult<AppAnnotationResponse> expectedResponse = new DifyPageResult<>();
        List<AppAnnotationResponse> items = new ArrayList<>();
        AppAnnotationResponse item = new AppAnnotationResponse();
        item.setId("anno-1");
        item.setQuestion("What is Dify?");
        item.setAnswer("Dify is an LLM application development platform.");
        item.setHitCount(10);
        item.setCreatedAt(1715086123456L);
        items.add(item);
        expectedResponse.setData(items);
        expectedResponse.setHasMore(false);

        // Mock response
        when(responseSpecMock.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(expectedResponse));

        // Execute the method
        DifyPageResult<AppAnnotationResponse> actualResponse = difyChatDefaultClient.pageAppAnnotation(request);

        // Verify results
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getData().size(), actualResponse.getData().size());
        assertEquals(expectedResponse.getData().get(0).getId(), actualResponse.getData().get(0).getId());
        assertEquals(expectedResponse.getData().get(0).getQuestion(), actualResponse.getData().get(0).getQuestion());
        assertEquals(expectedResponse.getData().get(0).getAnswer(), actualResponse.getData().get(0).getAnswer());
        assertEquals(expectedResponse.getData().get(0).getHitCount(), actualResponse.getData().get(0).getHitCount());
        assertEquals(expectedResponse.getData().get(0).getCreatedAt(), actualResponse.getData().get(0).getCreatedAt());

        // Verify WebClient interactions
        verify(webClientMock).get();
        verify(requestHeadersUriSpecMock).uri(
                DatasetUriConstant.V1_APPS_ANNOTATIONS + "?page={page}&limit={limit}",
                page,
                limit
        );
        verify(responseSpecMock).bodyToMono(any(ParameterizedTypeReference.class));
    }

    @Test
    public void testCreateAppAnnotation() {
        // Prepare test data
        String apiKey = "test-api-key";
        String question = "What is LLM?";
        String answer = "LLM stands for Large Language Model.";

        // Create request
        AppAnnotationCreateRequest request = new AppAnnotationCreateRequest();
        request.setApiKey(apiKey);
        request.setQuestion(question);
        request.setAnswer(answer);

        // Create expected response
        AppAnnotationResponse expectedResponse = new AppAnnotationResponse();
        expectedResponse.setId("anno-new");
        expectedResponse.setQuestion(question);
        expectedResponse.setAnswer(answer);
        expectedResponse.setHitCount(0);
        expectedResponse.setCreatedAt(System.currentTimeMillis());

        // Mock response
        when(responseSpecMock.bodyToMono(AppAnnotationResponse.class))
                .thenReturn(Mono.just(expectedResponse));

        // Execute the method
        AppAnnotationResponse actualResponse = difyChatDefaultClient.createAppAnnotation(request);

        // Verify results
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getQuestion(), actualResponse.getQuestion());
        assertEquals(expectedResponse.getAnswer(), actualResponse.getAnswer());
        assertEquals(expectedResponse.getHitCount(), actualResponse.getHitCount());
        assertEquals(expectedResponse.getCreatedAt(), actualResponse.getCreatedAt());

        // Verify WebClient interactions
        verify(webClientMock).post();
        verify(requestBodyUriSpecMock).uri(DatasetUriConstant.V1_APPS_ANNOTATIONS);
        verify(requestBodySpecMock).bodyValue(request);
        verify(responseSpecMock).bodyToMono(AppAnnotationResponse.class);
    }

    @Test
    public void testUpdateAppAnnotation() {
        // Prepare test data
        String apiKey = "test-api-key";
        String annotationId = "anno-1";
        String question = "What is an API key?";
        String answer = "An API key is a unique identifier used to authenticate requests to an API.";

        // Create request
        AppAnnotationUpdateRequest request = new AppAnnotationUpdateRequest();
        request.setApiKey(apiKey);
        request.setAnnotationId(annotationId);
        request.setQuestion(question);
        request.setAnswer(answer);

        // Create expected response
        AppAnnotationResponse expectedResponse = new AppAnnotationResponse();
        expectedResponse.setId(annotationId);
        expectedResponse.setQuestion(question);
        expectedResponse.setAnswer(answer);
        expectedResponse.setHitCount(5);
        expectedResponse.setCreatedAt(1715086123456L);

        // Mock response
        when(responseSpecMock.bodyToMono(AppAnnotationResponse.class))
                .thenReturn(Mono.just(expectedResponse));

        // Execute the method
        AppAnnotationResponse actualResponse = difyChatDefaultClient.updateAppAnnotation(request);

        // Verify results
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getQuestion(), actualResponse.getQuestion());
        assertEquals(expectedResponse.getAnswer(), actualResponse.getAnswer());
        assertEquals(expectedResponse.getHitCount(), actualResponse.getHitCount());
        assertEquals(expectedResponse.getCreatedAt(), actualResponse.getCreatedAt());

        // Verify WebClient interactions
        verify(webClientMock).post();
        verify(requestBodyUriSpecMock).uri(
                DatasetUriConstant.V1_APPS_ANNOTATIONS + "/{annotation_id}",
                annotationId
        );
        verify(requestBodySpecMock).bodyValue(request);
        verify(responseSpecMock).bodyToMono(AppAnnotationResponse.class);
    }

    @Test
    public void testDeleteAppAnnotation() {
        // Prepare test data
        String apiKey = "test-api-key";
        String annotationId = "anno-1";

        // Create expected response
        AppAnnotationDeleteResponse expectedResponse = new AppAnnotationDeleteResponse();
        expectedResponse.setResult("success");

        // Mock response
        when(responseSpecMock.bodyToMono(AppAnnotationDeleteResponse.class))
                .thenReturn(Mono.just(expectedResponse));

        // Execute the method
        AppAnnotationDeleteResponse actualResponse = difyChatDefaultClient.deleteAppAnnotation(annotationId, apiKey);

        // Verify results
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getResult(), actualResponse.getResult());

        // Verify WebClient interactions
        verify(webClientMock).delete();
        verify(requestHeadersUriSpecMock).uri(
                DatasetUriConstant.V1_APPS_ANNOTATIONS + "/{annotation_id}",
                annotationId
        );
        verify(responseSpecMock).bodyToMono(AppAnnotationDeleteResponse.class);
    }
}
