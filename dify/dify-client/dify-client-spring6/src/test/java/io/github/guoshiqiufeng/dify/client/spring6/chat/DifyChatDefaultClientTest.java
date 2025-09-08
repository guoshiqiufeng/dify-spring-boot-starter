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

import io.github.guoshiqiufeng.dify.chat.constant.ChatUriConstant;
import io.github.guoshiqiufeng.dify.chat.dto.request.*;
import io.github.guoshiqiufeng.dify.chat.dto.response.*;
import io.github.guoshiqiufeng.dify.chat.enums.AnnotationReplyActionEnum;
import io.github.guoshiqiufeng.dify.chat.enums.IconTypeEnum;
import io.github.guoshiqiufeng.dify.client.spring6.BaseClientTest;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.core.enums.ResponseModeEnum;
import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.core.pojo.DifyResult;
import io.github.guoshiqiufeng.dify.core.pojo.request.ChatMessageVO;
import io.github.guoshiqiufeng.dify.core.pojo.response.MessagesResponseVO;
import io.github.guoshiqiufeng.dify.dataset.constant.DatasetUriConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.*;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link DifyChatDefaultClient}.
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/4/21 14:45
 */
@SuppressWarnings("unchecked")
@DisplayName("DifyChatDefaultClient Tests")
public class DifyChatDefaultClientTest extends BaseClientTest {

    private static final String BASE_URL = "https://api.dify.ai";
    private static final String TEST_API_KEY = "test-api-key";

    private DifyChatDefaultClient client;

    @BeforeEach
    public void setup() {
        super.setup();
        client = new DifyChatDefaultClient(BASE_URL, new DifyProperties.ClientConfig(), restClientMock.getRestClientBuilder(), webClientMock.getWebClientBuilder());
    }

    @Test
    @DisplayName("Test chat method with valid request")
    public void testChat() {
        RestClient.RequestBodySpec requestBodySpec = restClientMock.getRequestBodySpec();
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestBodyUriSpec requestBodyUriSpec = restClientMock.getRequestBodyUriSpec();

        // Mock the response
        ChatMessageSendResponse mockResponse = new ChatMessageSendResponse();
        mockResponse.setId("chat-123");
        mockResponse.setCreatedAt(1713841200L); // 2025-04-21T10:00:00Z 的时间戳
        mockResponse.setAnswer("Hello, this is a test response.");

        when(responseSpec.body(eq(ChatMessageSendResponse.class))).thenReturn(mockResponse);

        // Create a chat request
        ChatMessageSendRequest request = new ChatMessageSendRequest();
        request.setApiKey(TEST_API_KEY);
        request.setUserId("user-123");
        request.setContent("Hello, Dify!");
        request.setConversationId("conv-123");

        // Call the method to test
        ChatMessageSendResponse response = client.chat(request);

        // Verify the response
        assertNotNull(response);
        assertEquals("chat-123", response.getId());
        assertEquals(1713841200L, response.getCreatedAt());
        assertEquals("Hello, this is a test response.", response.getAnswer());

        // Verify interactions with mocks
        verify(requestBodyUriSpec).uri(ChatUriConstant.V1_CHAT_MESSAGES_URI);
        verify(requestBodySpec).header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_API_KEY);

        // Capture and verify the request body
        ArgumentCaptor<ChatMessageVO> bodyCaptor = ArgumentCaptor.forClass(ChatMessageVO.class);
        verify(requestBodySpec).body(bodyCaptor.capture());

        ChatMessageVO capturedBody = bodyCaptor.getValue();
        assertEquals("Hello, Dify!", capturedBody.getQuery());
        assertEquals("user-123", capturedBody.getUser());
        assertEquals("conv-123", capturedBody.getConversationId());
        assertEquals(ResponseModeEnum.blocking, capturedBody.getResponseMode());
    }

    @Test
    @DisplayName("Test streamingChat method with valid request")
    public void testStreamingChat() {
        WebClient.RequestBodySpec requestBodySpec = webClientMock.getRequestBodySpec();
        WebClient.ResponseSpec responseSpec = webClientMock.getResponseSpec();
        WebClient.RequestBodyUriSpec requestBodyUriSpec = webClientMock.getRequestBodyUriSpec();

        // Mock the response
        ChatMessageSendCompletionResponse response1 = new ChatMessageSendCompletionResponse();
        response1.setEvent("message");
        response1.setId("chat-123");
        response1.setCreatedAt(1713841200L);
        response1.setAnswer("Hello, ");

        ChatMessageSendCompletionResponse response2 = new ChatMessageSendCompletionResponse();
        response2.setEvent("message");
        response2.setId("chat-123");
        response2.setCreatedAt(1713841200L);
        response2.setAnswer("this is a test response.");

        Flux<ChatMessageSendCompletionResponse> mockFlux = Flux.just(response1, response2);

        when(responseSpec.bodyToFlux(ChatMessageSendCompletionResponse.class)).thenReturn(mockFlux);

        // Create a chat request
        ChatMessageSendRequest request = new ChatMessageSendRequest();
        request.setApiKey(TEST_API_KEY);
        request.setUserId("user-123");
        request.setContent("Hello, Dify!");
        request.setConversationId("conv-123");
        request.setInputs(Map.of("key", "value"));
        request.setFiles(List.of(new ChatMessageSendRequest.ChatMessageFile()));
        // Call the method to test
        Flux<ChatMessageSendCompletionResponse> responseFlux = client.streamingChat(request);

        // Verify the response using StepVerifier
        assertNotNull(responseFlux);
        StepVerifier.create(responseFlux)
                .expectNext(response1)
                .expectNext(response2)
                .verifyComplete();

        // Verify interactions with mocks
        verify(requestBodyUriSpec).uri(ChatUriConstant.V1_CHAT_MESSAGES_URI);
        verify(requestBodySpec).header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_API_KEY);

        // Verify the body method was called with the correct argument types
        verify(requestBodySpec).body(any(Mono.class), eq(ChatMessageVO.class));
    }

    @Test
    @DisplayName("Test stopMessagesStream method")
    public void testStopMessagesStream() {
        RestClient restClient = restClientMock.getRestClient();
        RestClient.RequestBodySpec requestBodySpec = restClientMock.getRequestBodySpec();
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestBodyUriSpec requestBodyUriSpec = restClientMock.getRequestBodyUriSpec();

        // Mock the response for toBodilessEntity
        when(responseSpec.toBodilessEntity()).thenReturn(ResponseEntity.ok().build());

        // Define test parameters
        String apiKey = TEST_API_KEY;
        String taskId = "task-123";
        String userId = "user-123";

        // Call the method to test
        client.stopMessagesStream(apiKey, taskId, userId);

        // Verify interactions with mocks
        verify(restClient).post();
        verify(requestBodyUriSpec).uri(ChatUriConstant.V1_CHAT_MESSAGES_URI + "/{taskId}/stop", taskId);
        verify(requestBodySpec).header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);

        // Capture and verify the request body
        ArgumentCaptor<Map<String, Object>> bodyCaptor = ArgumentCaptor.forClass(Map.class);
        verify(requestBodySpec).body(bodyCaptor.capture());

        Map<String, Object> capturedBody = bodyCaptor.getValue();
        assertEquals(userId, capturedBody.get("user"));
    }

    @Test
    @DisplayName("Test parameters method")
    public void testParameters() {
        RestClient restClient = restClientMock.getRestClient();
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = restClientMock.getRequestHeadersUriSpec();
        RestClient.RequestHeadersSpec<?> requestHeadersSpec = restClientMock.getRequestHeadersSpec();

        // Mock the response
        AppParametersResponseVO mockResponse = new AppParametersResponseVO();
        mockResponse.setOpeningStatement("Welcome to Dify!");
        List<String> suggestedQuestions = new ArrayList<>();
        suggestedQuestions.add("What is Dify?");
        suggestedQuestions.add("How can I use Dify?");
        mockResponse.setSuggestedQuestions(suggestedQuestions);

        // Use the appropriate type reference matcher
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(mockResponse);

        // Call the method to test
        AppParametersResponseVO response = client.parameters(TEST_API_KEY);

        // Verify the response
        assertNotNull(response);
        assertEquals("Welcome to Dify!", response.getOpeningStatement());
        assertNotNull(response.getSuggestedQuestions());
        assertEquals(2, response.getSuggestedQuestions().size());
        assertEquals("What is Dify?", response.getSuggestedQuestions().get(0));
        assertEquals("How can I use Dify?", response.getSuggestedQuestions().get(1));

        // Verify interactions with mocks
        verify(restClient).get();
        verify(requestHeadersUriSpec).uri("/v1/parameters");
        verify(requestHeadersSpec).header(eq(HttpHeaders.AUTHORIZATION), eq("Bearer " + TEST_API_KEY));
    }

    @Test
    @DisplayName("Test renameConversation method")
    public void testRenameConversation() {
        RestClient restClient = restClientMock.getRestClient();
        RestClient.RequestBodySpec requestBodySpec = restClientMock.getRequestBodySpec();
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestBodyUriSpec requestBodyUriSpec = restClientMock.getRequestBodyUriSpec();

        // Mock the response
        MessageConversationsResponse mockResponse = new MessageConversationsResponse();
        mockResponse.setId("conv-123");
        mockResponse.setName("New Conversation Name");

        // Use the appropriate type reference matcher
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(mockResponse);

        // Create a rename request
        RenameConversationRequest request = new RenameConversationRequest();
        request.setApiKey(TEST_API_KEY);
        request.setConversationId("conv-123");
        request.setName("New Conversation Name");
        request.setUserId("user-123");
        request.setAutoGenerate(null);

        // Call the method to test
        MessageConversationsResponse response = client.renameConversation(request);

        // Verify the response
        assertNotNull(response);
        assertEquals("conv-123", response.getId());
        assertEquals("New Conversation Name", response.getName());

        // Verify interactions with mocks
        verify(restClient).post();
        verify(requestBodyUriSpec).uri("/v1/conversations/{conversationId}/name", "conv-123");
        verify(requestBodySpec).header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_API_KEY);

        // Capture and verify the request body
        ArgumentCaptor<Map<String, Object>> bodyCaptor = ArgumentCaptor.forClass(Map.class);
        verify(requestBodySpec).body(bodyCaptor.capture());

        Map<String, Object> capturedBody = bodyCaptor.getValue();
        assertEquals("New Conversation Name", capturedBody.get("name"));
        assertEquals(false, capturedBody.get("auto_generate"));
        assertEquals("user-123", capturedBody.get("user"));

        RenameConversationRequest defaultRequest = new RenameConversationRequest();
        request.setApiKey(TEST_API_KEY);
        request.setUserId("user-123");
        request.setConversationId("conv-123");
        defaultRequest.setName(null);
        defaultRequest.setAutoGenerate(false);
        client.renameConversation(defaultRequest);
    }

    @Test
    @DisplayName("Test file upload method")
    public void testFileUpload() {
        RestClient restClient = restClientMock.getRestClient();
        RestClient.RequestBodySpec requestBodySpec = restClientMock.getRequestBodySpec();
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestBodyUriSpec requestBodyUriSpec = restClientMock.getRequestBodyUriSpec();

        // Test data setup
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
        request.setApiKey(TEST_API_KEY);
        request.setUserId(userId);
        request.setFile(mockFile);

        // Create expected response
        FileUploadResponse expectedResponse = new FileUploadResponse();
        expectedResponse.setId(fileId);
        expectedResponse.setName(fileName);
        expectedResponse.setSize(fileContent.length());
        expectedResponse.setMimeType("text/plain");

        // Mock the response
        when(responseSpec.body(eq(FileUploadResponse.class))).thenReturn(expectedResponse);

        // Execute the method under test
        FileUploadResponse actualResponse = client.fileUpload(request);

        // Verify response
        assertNotNull(actualResponse);
        assertEquals(fileId, actualResponse.getId());
        assertEquals(fileName, actualResponse.getName());
        assertEquals(fileContent.length(), actualResponse.getSize());
        assertEquals("text/plain", actualResponse.getMimeType());

        // Verify basic interactions
        verify(restClient).post();
        verify(requestBodyUriSpec).uri(DatasetUriConstant.V1_FILES_UPLOAD);
        verify(requestBodySpec).contentType(MediaType.MULTIPART_FORM_DATA);
        // Body verification is tricky with multiple overloads, so we'll skip it
        verify(responseSpec).body(FileUploadResponse.class);
    }

    @Test
    @DisplayName("Test info")
    public void testInfo() {
        RestClient restClient = restClientMock.getRestClient();
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = restClientMock.getRequestHeadersUriSpec();
        RestClient.RequestHeadersSpec<?> requestHeadersSpec = restClientMock.getRequestHeadersSpec();

        // Mock the response
        AppInfoResponse mockResponse = new AppInfoResponse();
        mockResponse.setName("My App");
        mockResponse.setDescription("This is my app.");

        // Use the matching method signature rather than ParameterizedTypeReference
        when(responseSpec.body(eq(AppInfoResponse.class))).thenReturn(mockResponse);

        // Call the method to test
        AppInfoResponse response = client.info(TEST_API_KEY);

        // Verify the result
        assertNotNull(response);
        assertEquals(mockResponse.getName(), response.getName());
        assertEquals(mockResponse.getDescription(), response.getDescription());

        // Verify interactions with mocks
        verify(restClient).get();
        verify(requestHeadersUriSpec).uri(DatasetUriConstant.V1_INFO);
    }

    @Test
    @DisplayName("Test meta")
    public void testMeta() {
        RestClient restClient = restClientMock.getRestClient();
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = restClientMock.getRequestHeadersUriSpec();

        // Mock the response
        AppMetaResponse mockResponse = new AppMetaResponse();
        Map<String, Object> maps = new HashMap<>(1);
        maps.put("tools", "tools icon url");
        maps.put("api", Map.of("background", "#252525"));
        mockResponse.setToolIcons(maps);

        // Use the matching method signature rather than ParameterizedTypeReference
        when(responseSpec.body(eq(AppMetaResponse.class))).thenReturn(mockResponse);

        // Call the method to test
        AppMetaResponse response = client.meta(TEST_API_KEY);

        // Verify the result
        assertNotNull(response);
        assertEquals(mockResponse.getToolIcons(), response.getToolIcons());

        // Verify interactions with mocks
        verify(restClient).get();
        verify(requestHeadersUriSpec).uri(DatasetUriConstant.V1_META);
    }

    @Test
    public void testChatAndFile() {
        RestClient restClient = restClientMock.getRestClient();
        RestClient.RequestBodySpec requestBodySpec = restClientMock.getRequestBodySpec();
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestBodyUriSpec requestBodyUriSpec = restClientMock.getRequestBodyUriSpec();
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
        request.setInputs(null);
        ChatMessageSendRequest.ChatMessageFile file1 = new ChatMessageSendRequest.ChatMessageFile();
        file1.setUrl("https://file.com");
        file1.setType("image");
        ChatMessageSendRequest.ChatMessageFile file2 = new ChatMessageSendRequest.ChatMessageFile();
        file2.setType(null);
        file2.setTransferMethod(null);
        file2.setUrl("https://file.com");
        request.setFiles(List.of(file1, file2));
        // Create expected response
        ChatMessageSendResponse expectedResponse = new ChatMessageSendResponse();
        expectedResponse.setId("msg-123456");
        expectedResponse.setConversationId(conversationId);
        expectedResponse.setAnswer("I'm doing well, thank you for asking!");

        // Mock response
        when(responseSpec.body(ChatMessageSendResponse.class)).thenReturn(expectedResponse);

        // Execute the method
        ChatMessageSendResponse actualResponse = client.chat(request);

        // Verify results
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getConversationId(), actualResponse.getConversationId());
        assertEquals(expectedResponse.getAnswer(), actualResponse.getAnswer());

        // Verify WebClient interactions
        verify(restClient).post();
        verify(requestBodyUriSpec).uri(ChatUriConstant.V1_CHAT_MESSAGES_URI);
        verify(requestBodySpec).body(any(ChatMessageVO.class));
        verify(responseSpec).body(ChatMessageSendResponse.class);
    }

    @Test
    public void testConversations() {
        RestClient restClient = restClientMock.getRestClient();
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestHeadersSpec<?> requestHeadersSpec = restClientMock.getRequestHeadersSpec();
        RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = restClientMock.getRequestHeadersUriSpec();

        UriBuilder uriBuilderMock = mock(UriBuilder.class);
        URI uriMock = mock(URI.class);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenAnswer(invocation -> {
            Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);

            when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("sort_by"), anyString())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("limit"), anyInt())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("user"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("last_id"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.build()).thenReturn(uriMock);

            uriFunction.apply(uriBuilderMock);
            return requestHeadersSpec;
        });

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
        request.setLastId("1");

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
        when(responseSpec.body(any(ParameterizedTypeReference.class)))
                .thenReturn(expectedResponse);

        // Execute the method
        DifyPageResult<MessageConversationsResponse> actualResponse = client.conversations(request);

        // Verify results
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getData().size(), actualResponse.getData().size());
        assertEquals(expectedResponse.getData().get(0).getId(), actualResponse.getData().get(0).getId());
        assertEquals(expectedResponse.getData().get(0).getName(), actualResponse.getData().get(0).getName());

        // Verify WebClient interactions
        verify(restClient).get();
        verify(responseSpec).body(any(ParameterizedTypeReference.class));

        MessageConversationsRequest defaultRequest = new MessageConversationsRequest();
        defaultRequest.setApiKey(apiKey);
        defaultRequest.setUserId("");
        defaultRequest.setSortBy(null);
        defaultRequest.setLimit(null);
        defaultRequest.setLastId("");
        client.conversations(defaultRequest);
    }

    @Test
    public void testMessages() {
        RestClient restClient = restClientMock.getRestClient();
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestHeadersSpec<?> requestHeadersSpec = restClientMock.getRequestHeadersSpec();
        RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = restClientMock.getRequestHeadersUriSpec();

        UriBuilder uriBuilderMock = mock(UriBuilder.class);
        URI uriMock = mock(URI.class);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenAnswer(invocation -> {
            Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);

            when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("conversation_id"), anyString())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("limit"), anyInt())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("user"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("first_id"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.build()).thenReturn(uriMock);

            uriFunction.apply(uriBuilderMock);
            return requestHeadersSpec;
        });

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
        request.setFirstId("1");
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
        when(responseSpec.body(any(ParameterizedTypeReference.class)))
                .thenReturn(expectedResponse);

        // Execute the method
        DifyPageResult<MessagesResponseVO> actualResponse = client.messages(request);

        // Verify results
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getData().size(), actualResponse.getData().size());
        assertEquals(expectedResponse.getData().get(0).getId(), actualResponse.getData().get(0).getId());
        assertEquals(expectedResponse.getData().get(0).getConversationId(), actualResponse.getData().get(0).getConversationId());
        assertEquals(expectedResponse.getData().get(0).getAnswer(), actualResponse.getData().get(0).getAnswer());

        // Verify WebClient interactions
        verify(restClient).get();
        verify(responseSpec).body(any(ParameterizedTypeReference.class));

        MessagesRequest defaultRequest = new MessagesRequest();
        defaultRequest.setApiKey(apiKey);
        defaultRequest.setUserId("");
        defaultRequest.setConversationId(conversationId);
        defaultRequest.setLimit(null);
        defaultRequest.setFirstId("");
        client.messages(defaultRequest);
    }

    @Test
    public void testMessagesSuggested() {
        RestClient restClient = restClientMock.getRestClient();
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = restClientMock.getRequestHeadersUriSpec();
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
        when(responseSpec.body(any(ParameterizedTypeReference.class)))
                .thenReturn(expectedResponse);

        // Execute the method
        List<String> actualResponse = client.messagesSuggested(messageId, apiKey, userId);

        // Verify results
        assertNotNull(actualResponse);
        assertEquals(2, actualResponse.size());
        assertEquals("Tell me more about this", actualResponse.get(0));
        assertEquals("What are the alternatives?", actualResponse.get(1));

        // Verify WebClient interactions
        verify(restClient).get();
        verify(requestHeadersUriSpec).uri(
                ChatUriConstant.V1_MESSAGES_URI + "/{messageId}/suggested?user={user}",
                messageId,
                userId
        );
        verify(responseSpec).body(any(ParameterizedTypeReference.class));
    }

    @Test
    public void testMessagesSuggestedForNull() {
        RestClient restClient = restClientMock.getRestClient();
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = restClientMock.getRequestHeadersUriSpec();
        // Prepare test data
        String apiKey = "test-api-key";
        String userId = "test-user-id";
        String messageId = "msg-123456";

        // Mock response
        when(responseSpec.body(any(ParameterizedTypeReference.class)))
                .thenReturn(null);

        // Execute the method
        List<String> actualResponse = client.messagesSuggested(messageId, apiKey, userId);

        // Verify results
        assertNotNull(actualResponse);

        // Verify WebClient interactions
        verify(restClient).get();
        verify(requestHeadersUriSpec).uri(
                ChatUriConstant.V1_MESSAGES_URI + "/{messageId}/suggested?user={user}",
                messageId,
                userId
        );
        verify(responseSpec).body(any(ParameterizedTypeReference.class));
    }

    @Test
    public void testDeleteConversation() {
        RestClient restClient = restClientMock.getRestClient();
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestBodyUriSpec requestBodyUriSpec = restClientMock.getRequestBodyUriSpec();
        RestClient.RequestBodySpec requestBodySpec = restClientMock.getRequestBodySpec();
        // Prepare test data
        String apiKey = "test-api-key";
        String userId = "test-user-id";
        String conversationId = "conv-123456";

        // Execute the method
        client.deleteConversation(conversationId, apiKey, userId);

        // Verify WebClient interactions
        verify(restClient).method(HttpMethod.DELETE);
        verify(requestBodyUriSpec).uri(ChatUriConstant.V1_CONVERSATIONS_URI + "/{conversationId}", conversationId);
        verify(requestBodySpec).contentType(MediaType.APPLICATION_JSON);
        verify(requestBodySpec).body(any(Map.class));
    }

    @Test
    public void testTextToAudio() {
        RestClient restClient = restClientMock.getRestClient();
        RestClient.RequestBodySpec requestBodySpec = restClientMock.getRequestBodySpec();
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestBodyUriSpec requestBodyUriSpec = restClientMock.getRequestBodyUriSpec();
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
        when(responseSpec.body(any(ParameterizedTypeReference.class)))
                .thenReturn(expectedResponse);

        // Execute the method
        ResponseEntity<byte[]> actualResponse = client.textToAudio(request);

        // Verify results
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
        assertArrayEquals(expectedResponse.getBody(), actualResponse.getBody());

        // Verify WebClient interactions
        verify(restClient).post();
        verify(requestBodyUriSpec).uri(ChatUriConstant.V1_TEXT_TO_AUDIO_URI);
        verify(requestBodySpec).body(any(Map.class));
        verify(responseSpec).body(any(ParameterizedTypeReference.class));
    }

    @Test
    public void testMessageFeedback() {
        RestClient restClient = restClientMock.getRestClient();
        RestClient.RequestBodySpec requestBodySpec = restClientMock.getRequestBodySpec();
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestBodyUriSpec requestBodyUriSpec = restClientMock.getRequestBodyUriSpec();

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
        when(responseSpec.body(any(ParameterizedTypeReference.class)))
                .thenReturn(expectedResponse);

        // Execute the method
        MessageFeedbackResponse actualResponse = client.messageFeedback(request);

        // Verify results
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getResult(), actualResponse.getResult());

        // Verify WebClient interactions
        verify(restClient).post();
        verify(requestBodyUriSpec).uri(ChatUriConstant.V1_MESSAGES_URI + "/{messageId}/feedbacks", messageId);
        verify(requestBodySpec).body(any(Map.class));
        verify(responseSpec).body(any(ParameterizedTypeReference.class));

        // Create request
        MessageFeedbackRequest defaultRequest = new MessageFeedbackRequest();
        request.setApiKey(apiKey);
        request.setUserId(userId);
        request.setMessageId(messageId);
        client.messageFeedback(defaultRequest);
    }

    @Test
    public void testAudioToText() {
        RestClient restClient = restClientMock.getRestClient();
        RestClient.RequestBodySpec requestBodySpec = restClientMock.getRequestBodySpec();
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestBodyUriSpec requestBodyUriSpec = restClientMock.getRequestBodyUriSpec();
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
        when(responseSpec.body(DifyTextVO.class)).thenReturn(expectedResponse);

        // Execute the method
        DifyTextVO actualResponse = client.audioToText(request);

        // Verify results
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getText(), actualResponse.getText());

        // Verify WebClient interactions
        verify(restClient).post();
        verify(requestBodyUriSpec).uri(ChatUriConstant.V1_AUDIO_TO_TEXT_URI);
        verify(requestBodySpec).contentType(MediaType.MULTIPART_FORM_DATA);
        verify(responseSpec).body(DifyTextVO.class);
    }

    @Test
    @DisplayName("Test page app annotations")
    public void testPageAppAnnotation() {
        RestClient restClient = restClientMock.getRestClient();
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = restClientMock.getRequestHeadersUriSpec();

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
        when(responseSpec.body(any(ParameterizedTypeReference.class)))
                .thenReturn(expectedResponse);

        // Execute the method
        DifyPageResult<AppAnnotationResponse> actualResponse = client.pageAppAnnotation(request);

        // Verify results
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getData().size(), actualResponse.getData().size());
        assertEquals(expectedResponse.getData().get(0).getId(), actualResponse.getData().get(0).getId());
        assertEquals(expectedResponse.getData().get(0).getQuestion(), actualResponse.getData().get(0).getQuestion());
        assertEquals(expectedResponse.getData().get(0).getAnswer(), actualResponse.getData().get(0).getAnswer());
        assertEquals(expectedResponse.getData().get(0).getHitCount(), actualResponse.getData().get(0).getHitCount());
        assertEquals(expectedResponse.getData().get(0).getCreatedAt(), actualResponse.getData().get(0).getCreatedAt());

        // Verify WebClient interactions
        verify(restClient).get();
        verify(requestHeadersUriSpec).uri(
                DatasetUriConstant.V1_APPS_ANNOTATIONS + "?page={page}&limit={limit}",
                page,
                limit
        );
        verify(responseSpec).body(any(ParameterizedTypeReference.class));
    }

    @Test
    @DisplayName("Test create app annotation")
    public void testCreateAppAnnotation() {
        RestClient restClient = restClientMock.getRestClient();
        RestClient.RequestBodySpec requestBodySpec = restClientMock.getRequestBodySpec();
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestBodyUriSpec requestBodyUriSpec = restClientMock.getRequestBodyUriSpec();

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
        when(responseSpec.body(AppAnnotationResponse.class))
                .thenReturn(expectedResponse);

        // Execute the method
        AppAnnotationResponse actualResponse = client.createAppAnnotation(request);

        // Verify results
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getQuestion(), actualResponse.getQuestion());
        assertEquals(expectedResponse.getAnswer(), actualResponse.getAnswer());
        assertEquals(expectedResponse.getHitCount(), actualResponse.getHitCount());
        assertEquals(expectedResponse.getCreatedAt(), actualResponse.getCreatedAt());

        // Verify WebClient interactions
        verify(restClient).post();
        verify(requestBodyUriSpec).uri(DatasetUriConstant.V1_APPS_ANNOTATIONS);
        verify(requestBodySpec).body(request);
        verify(responseSpec).body(AppAnnotationResponse.class);
    }

    @Test
    @DisplayName("Test update app annotation")
    public void testUpdateAppAnnotation() {
        RestClient restClient = restClientMock.getRestClient();
        RestClient.RequestBodySpec requestBodySpec = restClientMock.getRequestBodySpec();
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestBodyUriSpec requestBodyUriSpec = restClientMock.getRequestBodyUriSpec();

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
        when(responseSpec.body(AppAnnotationResponse.class))
                .thenReturn(expectedResponse);

        // Execute the method
        AppAnnotationResponse actualResponse = client.updateAppAnnotation(request);

        // Verify results
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getQuestion(), actualResponse.getQuestion());
        assertEquals(expectedResponse.getAnswer(), actualResponse.getAnswer());
        assertEquals(expectedResponse.getHitCount(), actualResponse.getHitCount());
        assertEquals(expectedResponse.getCreatedAt(), actualResponse.getCreatedAt());

        // Verify WebClient interactions
        verify(restClient).put();
        verify(requestBodyUriSpec).uri(
                DatasetUriConstant.V1_APPS_ANNOTATIONS + "/{annotation_id}",
                annotationId
        );
        verify(requestBodySpec).body(request);
        verify(responseSpec).body(AppAnnotationResponse.class);
    }

    @Test
    @DisplayName("Test delete app annotation")
    public void testDeleteAppAnnotation() {
        RestClient restClient = restClientMock.getRestClient();
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = restClientMock.getRequestHeadersUriSpec();

        // Prepare test data
        String apiKey = "test-api-key";
        String annotationId = "anno-1";

        // Execute the method
        client.deleteAppAnnotation(annotationId, apiKey);

        // Verify WebClient interactions
        verify(restClient).delete();
        verify(requestHeadersUriSpec).uri(
                DatasetUriConstant.V1_APPS_ANNOTATIONS + "/{annotation_id}",
                annotationId
        );
    }

    @Test
    @DisplayName("Test annotation reply")
    public void testAnnotationReply() {
        RestClient restClient = restClientMock.getRestClient();
        RestClient.RequestBodySpec requestBodySpec = restClientMock.getRequestBodySpec();
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestBodyUriSpec requestBodyUriSpec = restClientMock.getRequestBodyUriSpec();

        // Prepare test data
        String apiKey = "test-api-key";
        AnnotationReplyActionEnum action = AnnotationReplyActionEnum.enable;
        String embeddingModelProvider = "openai";
        String embeddingModel = "text-embedding-3-small";
        Float scoreThreshold = 0.75f;

        // Create request
        AppAnnotationReplyRequest request = new AppAnnotationReplyRequest();
        request.setApiKey(apiKey);
        request.setAction(action);
        request.setEmbeddingProviderName(embeddingModelProvider);
        request.setEmbeddingModelName(embeddingModel);
        request.setScoreThreshold(scoreThreshold);

        // Create expected response
        AppAnnotationReplyResponse expectedResponse = new AppAnnotationReplyResponse();
        // Assuming jobId field exists in the response based on the query method
        expectedResponse.setJobId("b15c8f68-1cf4-4877-bf21-ed7cf2011802");
        expectedResponse.setJobStatus("waiting");

        // Mock response
        when(responseSpec.body(AppAnnotationReplyResponse.class))
                .thenReturn(expectedResponse);

        // Execute the method
        AppAnnotationReplyResponse actualResponse = client.annotationReply(request);

        // Verify results
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getJobId(), actualResponse.getJobId());
        assertEquals(expectedResponse.getJobStatus(), actualResponse.getJobStatus());

        // Verify WebClient interactions
        verify(restClient).post();
        verify(requestBodyUriSpec).uri(
                DatasetUriConstant.V1_APPS_ANNOTATIONS_REPLY + "/{action}",
                action
        );
        verify(requestBodySpec).body(request);
        verify(responseSpec).body(AppAnnotationReplyResponse.class);
    }

    @Test
    @DisplayName("Test query annotation reply status")
    public void testQueryAnnotationReply() {
        RestClient restClient = restClientMock.getRestClient();
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = restClientMock.getRequestHeadersUriSpec();

        // Prepare test data
        String apiKey = "test-api-key";
        AnnotationReplyActionEnum action = AnnotationReplyActionEnum.enable;
        String jobId = "job-123456";

        // Create request
        AppAnnotationReplyQueryRequest request = new AppAnnotationReplyQueryRequest();
        request.setApiKey(apiKey);
        request.setAction(action);
        request.setJobId(jobId);

        // Create expected response
        AppAnnotationReplyResponse expectedResponse = new AppAnnotationReplyResponse();
        expectedResponse.setJobId("b15c8f68-1cf4-4877-bf21-ed7cf2011802");
        expectedResponse.setJobStatus("waiting");
        expectedResponse.setErrorMsg("");

        // Mock response
        when(responseSpec.body(AppAnnotationReplyResponse.class))
                .thenReturn(expectedResponse);

        // Execute the method
        AppAnnotationReplyResponse actualResponse = client.queryAnnotationReply(request);

        // Verify results
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getJobId(), actualResponse.getJobId());
        assertEquals(expectedResponse.getJobStatus(), actualResponse.getJobStatus());

        // Verify WebClient interactions
        verify(restClient).get();
        verify(requestHeadersUriSpec).uri(
                DatasetUriConstant.V1_APPS_ANNOTATIONS_REPLY + "/{action}/status/{job_id}",
                action,
                jobId
        );
        verify(responseSpec).body(AppAnnotationReplyResponse.class);
    }

    @Test
    @DisplayName("Test site method")
    public void testSite() {
        RestClient restClient = restClientMock.getRestClient();
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = restClientMock.getRequestHeadersUriSpec();
        RestClient.RequestHeadersSpec<?> requestHeadersSpec = restClientMock.getRequestHeadersSpec();

        // Create expected response
        AppSiteResponse expectedResponse = new AppSiteResponse();
        expectedResponse.setTitle("Test App");
        expectedResponse.setChatColorTheme("#FF5733");
        expectedResponse.setChatColorThemeInverted(true);
        expectedResponse.setIconType(IconTypeEnum.emoji);
        expectedResponse.setIcon("🤖");
        expectedResponse.setIconBackground("#FFFFFF");
        expectedResponse.setIconUrl("https://example.com/icon.png");
        expectedResponse.setDescription("Test description");
        expectedResponse.setCopyright("© 2024 Test Company");
        expectedResponse.setPrivacyPolicy("https://example.com/privacy");
        expectedResponse.setCustomDisclaimer("Test disclaimer");
        expectedResponse.setDefaultLanguage("en-US");
        expectedResponse.setShowWorkflowSteps(true);
        expectedResponse.setUseIconAsAnswerIcon(false);

        // Mock the response
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(expectedResponse);

        // Call the method to test
        AppSiteResponse actualResponse = client.site(TEST_API_KEY);

        // Verify the response
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getTitle(), actualResponse.getTitle());
        assertEquals(expectedResponse.getChatColorTheme(), actualResponse.getChatColorTheme());
        assertEquals(expectedResponse.getChatColorThemeInverted(), actualResponse.getChatColorThemeInverted());
        assertEquals(expectedResponse.getIconType(), actualResponse.getIconType());
        assertEquals(expectedResponse.getIcon(), actualResponse.getIcon());
        assertEquals(expectedResponse.getIconBackground(), actualResponse.getIconBackground());
        assertEquals(expectedResponse.getIconUrl(), actualResponse.getIconUrl());
        assertEquals(expectedResponse.getDescription(), actualResponse.getDescription());
        assertEquals(expectedResponse.getCopyright(), actualResponse.getCopyright());
        assertEquals(expectedResponse.getPrivacyPolicy(), actualResponse.getPrivacyPolicy());
        assertEquals(expectedResponse.getCustomDisclaimer(), actualResponse.getCustomDisclaimer());
        assertEquals(expectedResponse.getDefaultLanguage(), actualResponse.getDefaultLanguage());
        assertEquals(expectedResponse.getShowWorkflowSteps(), actualResponse.getShowWorkflowSteps());
        assertEquals(expectedResponse.getUseIconAsAnswerIcon(), actualResponse.getUseIconAsAnswerIcon());

        // Verify interactions with mocks
        verify(restClient).get();
        verify(requestHeadersUriSpec).uri(ChatUriConstant.V1_SITE_URI);
        verify(requestHeadersSpec).header(eq(HttpHeaders.AUTHORIZATION), eq("Bearer " + TEST_API_KEY));
        verify(responseSpec).body(any(ParameterizedTypeReference.class));
    }

    @Test
    @DisplayName("Test filePreview method with valid request")
    public void testFilePreview() {
        RestClient restClient = restClientMock.getRestClient();
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = restClientMock.getRequestHeadersUriSpec();
        RestClient.RequestHeadersSpec<?> requestHeadersSpec = restClientMock.getRequestHeadersSpec();

        // Prepare test data
        String apiKey = TEST_API_KEY;
        String fileId = "file-123456";
        Boolean asAttachment = true;

        // Create request
        FilePreviewRequest request = new FilePreviewRequest();
        request.setApiKey(apiKey);
        request.setFileId(fileId);
        request.setAsAttachment(asAttachment);

        // Create expected response
        byte[] fileContent = "mock file content".getBytes();
        ResponseEntity<byte[]> expectedResponse = ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header("Content-Disposition", "attachment; filename=\"test.pdf\"")
                .body(fileContent);

        // Set up the URI builder mock
        UriBuilder uriBuilderMock = mock(UriBuilder.class);
        URI uriMock = mock(URI.class);

        when(requestHeadersUriSpec.uri(any(Function.class))).thenAnswer(invocation -> {
            Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);

            when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("as_attachment"), eq("true"))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.build(fileId)).thenReturn(uriMock);

            uriFunction.apply(uriBuilderMock);
            return requestHeadersSpec;
        });

        // Mock response
        when(responseSpec.toEntity(byte[].class)).thenReturn(expectedResponse);

        // Execute the method
        ResponseEntity<byte[]> actualResponse = client.filePreview(request);

        // Verify results
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
        assertEquals(expectedResponse.getHeaders().getContentType(), actualResponse.getHeaders().getContentType());
        assertArrayEquals(expectedResponse.getBody(), actualResponse.getBody());

        // Verify interactions with mocks
        verify(restClient).get();
        verify(requestHeadersUriSpec).uri(any(Function.class));
        verify(requestHeadersSpec).header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);
        verify(responseSpec).toEntity(byte[].class);

        // Test with asAttachment = false
        FilePreviewRequest request2 = new FilePreviewRequest();
        request2.setApiKey(apiKey);
        request2.setFileId(fileId);
        request2.setAsAttachment(false);

        when(requestHeadersUriSpec.uri(any(Function.class))).thenAnswer(invocation -> {
            Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);

            when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.build(fileId)).thenReturn(uriMock);

            uriFunction.apply(uriBuilderMock);
            return requestHeadersSpec;
        });

        client.filePreview(request2);

        // Verify the second call
        verify(restClient, times(2)).get();
    }

    @Test
    @DisplayName("Test feedbacks method with valid request")
    public void testFeedbacks() {
        RestClient restClient = restClientMock.getRestClient();
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = restClientMock.getRequestHeadersUriSpec();
        RestClient.RequestHeadersSpec<?> requestHeadersSpec = restClientMock.getRequestHeadersSpec();

        // Prepare test data
        String apiKey = TEST_API_KEY;
        int page = 1;
        int limit = 20;

        // Create request
        AppFeedbackPageRequest request = new AppFeedbackPageRequest();
        request.setApiKey(apiKey);
        request.setPage(page);
        request.setLimit(limit);

        // Create expected response
        DifyPageResult<AppFeedbackResponse> expectedResponse = new DifyPageResult<>();
        expectedResponse.setData(new ArrayList<>());

        AppFeedbackResponse feedback = new AppFeedbackResponse();
        feedback.setId("8c0fbed8-e2f9-49ff-9f0e-15a35bdd0e25");
        feedback.setAppId("f252d396-fe48-450e-94ec-e184218e7346");
        feedback.setConversationId("2397604b-9deb-430e-b285-4726e51fd62d");
        feedback.setMessageId("709c0b0f-0a96-4a4e-91a4-ec0889937b11");
        feedback.setRating("like");
        feedback.setContent("message feedback information-3");
        feedback.setFromSource("user");
        feedback.setFromEndUserId("74286412-9a1a-42c1-929c-01edb1d381d5");
        feedback.setFromAccountId(null);

        expectedResponse.getData().add(feedback);

        // Set up the URI builder mock
        UriBuilder uriBuilderMock = mock(UriBuilder.class);
        URI uriMock = mock(URI.class);

        when(requestHeadersUriSpec.uri(any(Function.class))).thenAnswer(invocation -> {
            Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);

            when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("page"), eq(page))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("limit"), eq(limit))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.build()).thenReturn(uriMock);

            uriFunction.apply(uriBuilderMock);
            return requestHeadersSpec;
        });

        // Mock response
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(expectedResponse);

        // Execute the method
        DifyPageResult<AppFeedbackResponse> actualResponse = client.feedbacks(request);

        // Verify results
        assertNotNull(actualResponse);
        assertNotNull(actualResponse.getData());
        assertEquals(1, actualResponse.getData().size());

        AppFeedbackResponse actualFeedback = actualResponse.getData().get(0);
        assertEquals(feedback.getId(), actualFeedback.getId());
        assertEquals(feedback.getAppId(), actualFeedback.getAppId());
        assertEquals(feedback.getConversationId(), actualFeedback.getConversationId());
        assertEquals(feedback.getMessageId(), actualFeedback.getMessageId());
        assertEquals(feedback.getRating(), actualFeedback.getRating());
        assertEquals(feedback.getContent(), actualFeedback.getContent());
        assertEquals(feedback.getFromSource(), actualFeedback.getFromSource());
        assertEquals(feedback.getFromEndUserId(), actualFeedback.getFromEndUserId());
        assertEquals(feedback.getFromAccountId(), actualFeedback.getFromAccountId());

        // Verify interactions with mocks
        verify(restClient).get();
        verify(requestHeadersUriSpec).uri(any(Function.class));
        verify(requestHeadersSpec).header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);
        verify(responseSpec).body(any(ParameterizedTypeReference.class));
    }

    @Test
    @DisplayName("Test conversationVariables method with valid request")
    public void testConversationVariables() {
        RestClient restClient = restClientMock.getRestClient();
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = restClientMock.getRequestHeadersUriSpec();
        RestClient.RequestHeadersSpec<?> requestHeadersSpec = restClientMock.getRequestHeadersSpec();

        // Prepare test data
        String apiKey = TEST_API_KEY;
        String userId = "user-123";
        String conversationId = "conv-123456";
        String variableName = "customer_name";

        // Create request
        ConversationVariableRequest request = new ConversationVariableRequest();
        request.setApiKey(apiKey);
        request.setUserId(userId);
        request.setConversationId(conversationId);
        request.setVariableName(variableName);

        // Create expected response
        DifyPageResult<ConversationVariableResponse> expectedResponse = new DifyPageResult<>();
        expectedResponse.setData(new ArrayList<>());

        ConversationVariableResponse variable1 = new ConversationVariableResponse();
        variable1.setId("variable-uuid-1");
        variable1.setName("customer_name");
        variable1.setValueType("string");
        variable1.setValue("John Doe");
        variable1.setDescription("客户名称（从对话中提取）");
        variable1.setCreatedAt(1650000000000L);
        variable1.setUpdatedAt(1650000000000L);

        ConversationVariableResponse variable2 = new ConversationVariableResponse();
        variable2.setId("variable-uuid-2");
        variable2.setName("order_details");
        variable2.setValueType("json");
        variable2.setValue("{\"product\":\"Widget\",\"quantity\":5,\"price\":19.99}");
        variable2.setDescription("客户的订单详情");
        variable2.setCreatedAt(1650000000000L);
        variable2.setUpdatedAt(1650000000000L);

        expectedResponse.getData().add(variable1);
        expectedResponse.getData().add(variable2);

        // Set up the URI builder mock
        UriBuilder uriBuilderMock = mock(UriBuilder.class);
        URI uriMock = mock(URI.class);

        when(requestHeadersUriSpec.uri(any(Function.class))).thenAnswer(invocation -> {
            Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);

            when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("variable_name"), eq(variableName))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("user"), eq(userId))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.build(eq(conversationId))).thenReturn(uriMock);

            uriFunction.apply(uriBuilderMock);
            return requestHeadersSpec;
        });

        // Mock response
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(expectedResponse);

        // Execute the method
        DifyPageResult<ConversationVariableResponse> actualResponse = client.conversationVariables(request);

        // Verify results
        assertNotNull(actualResponse);
        assertNotNull(actualResponse.getData());
        assertEquals(2, actualResponse.getData().size());

        ConversationVariableResponse actualVariable1 = actualResponse.getData().get(0);
        assertEquals(variable1.getId(), actualVariable1.getId());
        assertEquals(variable1.getName(), actualVariable1.getName());
        assertEquals(variable1.getValueType(), actualVariable1.getValueType());
        assertEquals(variable1.getValue(), actualVariable1.getValue());
        assertEquals(variable1.getDescription(), actualVariable1.getDescription());
        assertEquals(variable1.getCreatedAt(), actualVariable1.getCreatedAt());
        assertEquals(variable1.getUpdatedAt(), actualVariable1.getUpdatedAt());

        ConversationVariableResponse actualVariable2 = actualResponse.getData().get(1);
        assertEquals(variable2.getId(), actualVariable2.getId());
        assertEquals(variable2.getName(), actualVariable2.getName());
        assertEquals(variable2.getValueType(), actualVariable2.getValueType());
        assertEquals(variable2.getValue(), actualVariable2.getValue());
        assertEquals(variable2.getDescription(), actualVariable2.getDescription());
        assertEquals(variable2.getCreatedAt(), actualVariable2.getCreatedAt());
        assertEquals(variable2.getUpdatedAt(), actualVariable2.getUpdatedAt());

        // Verify interactions with mocks
        verify(restClient).get();
        verify(requestHeadersUriSpec).uri(any(Function.class));
        verify(requestHeadersSpec).header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);
        verify(responseSpec).body(any(ParameterizedTypeReference.class));

        // Test without variable name
        ConversationVariableRequest request2 = new ConversationVariableRequest();
        request2.setApiKey(apiKey);
        request2.setUserId(userId);
        request2.setConversationId(conversationId);
        request2.setVariableName(null);

        when(requestHeadersUriSpec.uri(any(Function.class))).thenAnswer(invocation -> {
            Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);

            when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("user"), eq(userId))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.build(eq(conversationId))).thenReturn(uriMock);

            uriFunction.apply(uriBuilderMock);
            return requestHeadersSpec;
        });

        client.conversationVariables(request2);
    }

    @Test
    @DisplayName("Test updateConversationVariable method with valid request")
    public void testUpdateConversationVariable() {
        RestClient restClient = restClientMock.getRestClient();
        RestClient.RequestBodySpec requestBodySpec = restClientMock.getRequestBodySpec();
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestBodyUriSpec requestBodyUriSpec = restClientMock.getRequestBodyUriSpec();

        // Prepare test data
        String apiKey = TEST_API_KEY;
        String userId = "user-123";
        String conversationId = "conv-123456";
        String variableId = "variable-uuid-1";
        String newValue = "Updated Value";

        // Create request
        UpdateConversationVariableRequest request = new UpdateConversationVariableRequest();
        request.setApiKey(apiKey);
        request.setUserId(userId);
        request.setConversationId(conversationId);
        request.setVariableId(variableId);
        request.setValue(newValue);

        // Create expected response
        ConversationVariableResponse expectedResponse = new ConversationVariableResponse();
        expectedResponse.setId(variableId);
        expectedResponse.setName("customer_name");
        expectedResponse.setValueType("string");
        expectedResponse.setValue(newValue);
        expectedResponse.setDescription("客户名称（从对话中提取）");
        expectedResponse.setCreatedAt(1650000000000L);
        expectedResponse.setUpdatedAt(1650000001000L);

        // Mock response
        when(responseSpec.body(eq(ConversationVariableResponse.class))).thenReturn(expectedResponse);

        // Execute the method
        ConversationVariableResponse actualResponse = client.updateConversationVariable(request);

        // Verify results
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getName(), actualResponse.getName());
        assertEquals(expectedResponse.getValueType(), actualResponse.getValueType());
        assertEquals(expectedResponse.getValue(), actualResponse.getValue());
        assertEquals(expectedResponse.getDescription(), actualResponse.getDescription());
        assertEquals(expectedResponse.getCreatedAt(), actualResponse.getCreatedAt());
        assertEquals(expectedResponse.getUpdatedAt(), actualResponse.getUpdatedAt());

        // Verify interactions with mocks
        verify(restClient).put();
        verify(requestBodyUriSpec).uri(
                ChatUriConstant.V1_CONVERSATIONS_VARIABLES_UPDATE_URI,
                conversationId,
                variableId
        );
        verify(requestBodySpec).header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);
        verify(requestBodySpec).contentType(MediaType.APPLICATION_JSON);

        // Capture and verify the request body
        ArgumentCaptor<Map<String, Object>> bodyCaptor = ArgumentCaptor.forClass(Map.class);
        verify(requestBodySpec).body(bodyCaptor.capture());

        Map<String, Object> capturedBody = bodyCaptor.getValue();
        assertEquals(newValue, capturedBody.get("value"));
        assertEquals(userId, capturedBody.get("user"));
    }
}
