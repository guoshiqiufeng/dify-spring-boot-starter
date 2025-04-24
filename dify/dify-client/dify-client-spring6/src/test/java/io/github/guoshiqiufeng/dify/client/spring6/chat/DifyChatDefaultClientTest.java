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

import io.github.guoshiqiufeng.dify.chat.dto.request.ChatMessageSendRequest;
import io.github.guoshiqiufeng.dify.chat.dto.request.FileUploadRequest;
import io.github.guoshiqiufeng.dify.chat.dto.request.RenameConversationRequest;
import io.github.guoshiqiufeng.dify.chat.dto.response.*;
import io.github.guoshiqiufeng.dify.core.enums.ResponseModeEnum;
import io.github.guoshiqiufeng.dify.core.pojo.request.ChatMessageVO;
import io.github.guoshiqiufeng.dify.dataset.constant.DatasetUriConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/4/21 14:45
 */
@DisplayName("DifyChatDefaultClient Tests")
public class DifyChatDefaultClientTest {

    private static final String BASE_URL = "https://api.dify.ai";
    private static final String TEST_API_KEY = "test-api-key";

    // Common mock objects
    private RestClient.Builder mockRestClientBuilder;
    private RestClient mockRestClient;
    private WebClient.Builder mockWebClientBuilder;
    private WebClient mockWebClient;
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;
    private RestClient.RequestBodySpec requestBodySpec;
    private RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;
    private RestClient.RequestHeadersSpec<?> requestHeadersSpec;
    private RestClient.ResponseSpec responseSpec;

    // Client under test
    private DifyChatDefaultClient client;

    @BeforeEach
    public void setup() {
        // Initialize all mock objects
        mockRestClientBuilder = Mockito.mock(RestClient.Builder.class);
        mockRestClient = Mockito.mock(RestClient.class);
        mockWebClientBuilder = Mockito.mock(WebClient.Builder.class);
        mockWebClient = Mockito.mock(WebClient.class);
        requestBodyUriSpec = Mockito.mock(RestClient.RequestBodyUriSpec.class);
        requestBodySpec = Mockito.mock(RestClient.RequestBodySpec.class);
        requestHeadersUriSpec = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        requestHeadersSpec = Mockito.mock(RestClient.RequestHeadersSpec.class);
        responseSpec = Mockito.mock(RestClient.ResponseSpec.class);

        // Setup common mock behavior
        when(mockRestClientBuilder.baseUrl(anyString())).thenReturn(mockRestClientBuilder);
        when(mockRestClientBuilder.defaultHeaders(any())).thenReturn(mockRestClientBuilder);
        when(mockRestClientBuilder.build()).thenReturn(mockRestClient);

        when(mockWebClientBuilder.baseUrl(anyString())).thenReturn(mockWebClientBuilder);
        when(mockWebClientBuilder.defaultHeaders(any())).thenReturn(mockWebClientBuilder);
        when(mockWebClientBuilder.build()).thenReturn(mockWebClient);

        when(responseSpec.onStatus(any())).thenReturn(responseSpec);

        // Initialize the client with mocked dependencies
        client = new DifyChatDefaultClient(BASE_URL, null, mockRestClientBuilder, mockWebClientBuilder);
    }

    // Helper method to setup mocks for POST requests
    private void setupPostRequestMocks(String uri) {
        doReturn(requestBodyUriSpec).when(mockRestClient).post();
        when(requestBodyUriSpec.uri(uri)).thenReturn(requestBodySpec);
        when(requestBodySpec.header(eq(HttpHeaders.AUTHORIZATION), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
    }

    // Helper method to setup mocks for GET requests
    private void setupGetRequestMocks(String uri) {
        doReturn(requestHeadersUriSpec).when(mockRestClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(anyString());
        doReturn(requestHeadersSpec).when(requestHeadersSpec).header(eq(HttpHeaders.AUTHORIZATION), anyString());
        doReturn(requestHeadersSpec).when(requestHeadersSpec).headers(any());
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    // Helper method to setup mocks for parameterized URI POST requests
    private void setupPostRequestMocksWithParam(String uri, String paramValue) {
        doReturn(requestBodyUriSpec).when(mockRestClient).post();
        when(requestBodyUriSpec.uri(eq(uri), eq(paramValue))).thenReturn(requestBodySpec);
        when(requestBodySpec.header(eq(HttpHeaders.AUTHORIZATION), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    @DisplayName("Test chat method with valid request")
    public void testChat() {
        // Setup specific mock behavior
        setupPostRequestMocks("/v1/chat-messages");
        when(requestBodySpec.body(any(ChatMessageVO.class))).thenReturn(requestBodySpec);

        // Mock the response
        ChatMessageSendResponse mockResponse = new ChatMessageSendResponse();
        mockResponse.setId("chat-123");
        mockResponse.setCreatedAt(1713841200L); // 2025-04-21T10:00:00Z 的时间戳
        mockResponse.setAnswer("Hello, this is a test response.");
        when(responseSpec.body(ChatMessageSendResponse.class)).thenReturn(mockResponse);

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
        verify(requestBodyUriSpec).uri("/v1/chat-messages");
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
    @DisplayName("Test parameters method")
    public void testParameters() {
        // Setup specific mock behavior
        setupGetRequestMocks("/v1/parameters");

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
        verify(mockRestClient).get();
        verify(requestHeadersUriSpec).uri("/v1/parameters");
        verify(requestHeadersSpec).header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_API_KEY);
    }

    @Test
    @DisplayName("Test renameConversation method")
    public void testRenameConversation() {
        // Setup specific mock behavior
        setupPostRequestMocksWithParam("/v1/conversations/{conversationId}/name", "conv-123");
        when(requestBodySpec.body(any(Map.class))).thenReturn(requestBodySpec);

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
        request.setAutoGenerate(false);

        // Call the method to test
        MessageConversationsResponse response = client.renameConversation(request);

        // Verify the response
        assertNotNull(response);
        assertEquals("conv-123", response.getId());
        assertEquals("New Conversation Name", response.getName());

        // Verify interactions with mocks
        verify(requestBodyUriSpec).uri("/v1/conversations/{conversationId}/name", "conv-123");
        verify(requestBodySpec).header(HttpHeaders.AUTHORIZATION, "Bearer " + TEST_API_KEY);

        // Capture and verify the request body
        ArgumentCaptor<Map<String, Object>> bodyCaptor = ArgumentCaptor.forClass(Map.class);
        verify(requestBodySpec).body(bodyCaptor.capture());

        Map<String, Object> capturedBody = bodyCaptor.getValue();
        assertEquals("New Conversation Name", capturedBody.get("name"));
        assertEquals(false, capturedBody.get("auto_generate"));
        assertEquals("user-123", capturedBody.get("user"));
    }

    @Test
    @DisplayName("Test file upload method")
    public void testFileUpload() {
        // Setup specific mock behavior for file upload
        doReturn(requestBodyUriSpec).when(mockRestClient).post();
        when(requestBodyUriSpec.uri(eq(DatasetUriConstant.V1_FILES_UPLOAD))).thenReturn(requestBodySpec);
        when(requestBodySpec.header(eq(HttpHeaders.AUTHORIZATION), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(eq(MediaType.MULTIPART_FORM_DATA))).thenReturn(requestBodySpec);
        when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);

        // Add more specific mocks for various body method overloads
        when(requestBodySpec.body(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(Object.class))).thenReturn(requestBodySpec);
        doReturn(requestBodySpec).when(requestBodySpec).body(any(Map.class));
        doReturn(requestBodySpec).when(requestBodySpec).body(any(MultipartFile.class));

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
        verify(mockRestClient).post();
        verify(requestBodyUriSpec).uri(DatasetUriConstant.V1_FILES_UPLOAD);
        verify(requestBodySpec).contentType(MediaType.MULTIPART_FORM_DATA);
        // Body verification is tricky with multiple overloads, so we'll skip it
        verify(responseSpec).body(FileUploadResponse.class);
    }

    @Test
    @DisplayName("Test info")
    public void testInfo() {
        // Setup specific mock behavior
        setupGetRequestMocks("/v1/info");

        // Mock the response
        AppInfoResponse mockResponse = new AppInfoResponse();
        mockResponse.setName("My App");
        mockResponse.setDescription("This is my app.");

        // Use the matching method signature rather than ParameterizedTypeReference
        when(responseSpec.body(eq(AppInfoResponse.class))).thenReturn(mockResponse);

        // Call the method to test
        AppInfoResponse response = client.info(TEST_API_KEY);

        // Verify the result
        assertEquals(mockResponse.getName(), response.getName());
        assertEquals(mockResponse.getDescription(), response.getDescription());

        // Verify interactions with mocks
        verify(mockRestClient).get();
        verify(requestHeadersUriSpec).uri(DatasetUriConstant.V1_INFO);
    }

    @Test
    @DisplayName("Test meta")
    public void testMeta() {
        // Setup specific mock behavior
        setupGetRequestMocks("/v1/meta");

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
        verify(mockRestClient).get();
        verify(requestHeadersUriSpec).uri(DatasetUriConstant.V1_META);
    }
}
