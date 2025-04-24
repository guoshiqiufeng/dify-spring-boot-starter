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
import io.github.guoshiqiufeng.dify.chat.dto.response.AppParametersResponseVO;
import io.github.guoshiqiufeng.dify.chat.dto.response.ChatMessageSendResponse;
import io.github.guoshiqiufeng.dify.chat.dto.response.FileUploadResponse;
import io.github.guoshiqiufeng.dify.chat.dto.response.MessageConversationsResponse;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.core.enums.ResponseModeEnum;
import io.github.guoshiqiufeng.dify.core.pojo.request.ChatMessageVO;
import io.github.guoshiqiufeng.dify.dataset.constant.DatasetUriConstant;
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

    @Test
    @DisplayName("Test chat method with valid request")
    public void testChat() {
        // Create mock objects
        RestClient.RequestBodyUriSpec requestBodyUriSpec = Mockito.mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec requestBodySpec = Mockito.mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec responseSpec = Mockito.mock(RestClient.ResponseSpec.class);
        RestClient.Builder mockRestClientBuilder = Mockito.mock(RestClient.Builder.class);
        RestClient mockRestClient = Mockito.mock(RestClient.class);
        WebClient.Builder mockWebClientBuilder = Mockito.mock(WebClient.Builder.class);
        WebClient mockWebClient = Mockito.mock(WebClient.class);

        // Setup mock behavior
        when(mockRestClientBuilder.baseUrl(anyString())).thenReturn(mockRestClientBuilder);
        when(mockRestClientBuilder.defaultHeaders(any())).thenReturn(mockRestClientBuilder);
        when(mockRestClientBuilder.build()).thenReturn(mockRestClient);

        when(mockWebClientBuilder.baseUrl(anyString())).thenReturn(mockWebClientBuilder);
        when(mockWebClientBuilder.defaultHeaders(any())).thenReturn(mockWebClientBuilder);
        when(mockWebClientBuilder.build()).thenReturn(mockWebClient);

        doReturn(requestBodyUriSpec).when(mockRestClient).post();
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        doReturn(requestBodySpec).when(requestBodySpec).header(eq(HttpHeaders.AUTHORIZATION), anyString());
        doReturn(requestBodySpec).when(requestBodySpec).body(any(ChatMessageVO.class));
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any())).thenReturn(responseSpec);

        // Mock the response
        ChatMessageSendResponse mockResponse = new ChatMessageSendResponse();
        mockResponse.setId("chat-123");
        mockResponse.setCreatedAt(1713841200L); // 2025-04-21T10:00:00Z 的时间戳
        mockResponse.setAnswer("Hello, this is a test response.");
        when(responseSpec.body(ChatMessageSendResponse.class)).thenReturn(mockResponse);

        // Create the client with mocked dependencies
        String baseUrl = "https://api.dify.ai";
        DifyChatDefaultClient client = new DifyChatDefaultClient(baseUrl, null, mockRestClientBuilder, mockWebClientBuilder);

        // Create a chat request
        ChatMessageSendRequest request = new ChatMessageSendRequest();
        request.setApiKey("test-api-key");
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
        verify(requestBodySpec).header(HttpHeaders.AUTHORIZATION, "Bearer test-api-key");

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
        // Create mock objects
        RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec<?> requestHeadersSpec = Mockito.mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = Mockito.mock(RestClient.ResponseSpec.class);
        RestClient.Builder mockRestClientBuilder = Mockito.mock(RestClient.Builder.class);
        RestClient mockRestClient = Mockito.mock(RestClient.class);
        WebClient.Builder mockWebClientBuilder = Mockito.mock(WebClient.Builder.class);
        WebClient mockWebClient = Mockito.mock(WebClient.class);

        // Setup mock behavior
        when(mockRestClientBuilder.baseUrl(anyString())).thenReturn(mockRestClientBuilder);
        when(mockRestClientBuilder.defaultHeaders(any())).thenReturn(mockRestClientBuilder);
        when(mockRestClientBuilder.build()).thenReturn(mockRestClient);

        when(mockWebClientBuilder.baseUrl(anyString())).thenReturn(mockWebClientBuilder);
        when(mockWebClientBuilder.defaultHeaders(any())).thenReturn(mockWebClientBuilder);
        when(mockWebClientBuilder.build()).thenReturn(mockWebClient);

        // Mock the get() method of RestClient using doReturn to avoid type parameter issues
        doReturn(requestHeadersUriSpec).when(mockRestClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(anyString());
        doReturn(requestHeadersSpec).when(requestHeadersSpec).header(eq(HttpHeaders.AUTHORIZATION), anyString());
        doReturn(responseSpec).when(requestHeadersSpec).retrieve();
        doReturn(responseSpec).when(responseSpec).onStatus(any());

        // Mock the response
        AppParametersResponseVO mockResponse = new AppParametersResponseVO();
        mockResponse.setOpeningStatement("Welcome to Dify!");
        List<String> suggestedQuestions = new ArrayList<>();
        suggestedQuestions.add("What is Dify?");
        suggestedQuestions.add("How can I use Dify?");
        mockResponse.setSuggestedQuestions(suggestedQuestions);

        // Use the appropriate type reference matcher
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(mockResponse);

        // Create the client with mocked dependencies
        String baseUrl = "https://api.dify.ai";
        DifyChatDefaultClient client = new DifyChatDefaultClient(baseUrl, null, mockRestClientBuilder, mockWebClientBuilder);

        // Call the method to test
        AppParametersResponseVO response = client.parameters("test-api-key");

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
        verify(requestHeadersSpec).header(HttpHeaders.AUTHORIZATION, "Bearer test-api-key");
    }

    @Test
    @DisplayName("Test renameConversation method")
    public void testRenameConversation() {
        // Create mock objects
        RestClient.RequestBodyUriSpec requestBodyUriSpec = Mockito.mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec requestBodySpec = Mockito.mock(RestClient.RequestBodySpec.class);
        RestClient.RequestHeadersSpec<?> requestHeadersSpec = Mockito.mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = Mockito.mock(RestClient.ResponseSpec.class);
        RestClient.Builder mockRestClientBuilder = Mockito.mock(RestClient.Builder.class);
        RestClient mockRestClient = Mockito.mock(RestClient.class);
        WebClient.Builder mockWebClientBuilder = Mockito.mock(WebClient.Builder.class);
        WebClient mockWebClient = Mockito.mock(WebClient.class);

        // Setup mock behavior
        when(mockRestClientBuilder.baseUrl(anyString())).thenReturn(mockRestClientBuilder);
        when(mockRestClientBuilder.defaultHeaders(any())).thenReturn(mockRestClientBuilder);
        when(mockRestClientBuilder.build()).thenReturn(mockRestClient);

        when(mockWebClientBuilder.baseUrl(anyString())).thenReturn(mockWebClientBuilder);
        when(mockWebClientBuilder.defaultHeaders(any())).thenReturn(mockWebClientBuilder);
        when(mockWebClientBuilder.build()).thenReturn(mockWebClient);

        // 使用 doReturn 语法避免类型不匹配问题
        doReturn(requestBodyUriSpec).when(mockRestClient).post();
        when(requestBodyUriSpec.uri(anyString(), anyString())).thenReturn(requestBodySpec);
        doReturn(requestBodySpec).when(requestBodySpec).header(eq(HttpHeaders.AUTHORIZATION), anyString());
        // 使用 doReturn 语法避免类型不匹配问题
        doReturn(requestBodySpec).when(requestBodySpec).body(any(Map.class));
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any())).thenReturn(responseSpec);

        // Mock the response
        MessageConversationsResponse mockResponse = new MessageConversationsResponse();
        mockResponse.setId("conv-123");
        mockResponse.setName("New Conversation Name");

        // Use the appropriate type reference matcher
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(mockResponse);

        // Create the client with mocked dependencies
        String baseUrl = "https://api.dify.ai";
        DifyChatDefaultClient client = new DifyChatDefaultClient(baseUrl, null, mockRestClientBuilder, mockWebClientBuilder);

        // Create a rename request
        RenameConversationRequest request = new RenameConversationRequest();
        request.setApiKey("test-api-key");
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
        verify(requestBodySpec).header(HttpHeaders.AUTHORIZATION, "Bearer test-api-key");

        // Capture and verify the request body
        ArgumentCaptor<Map<String, Object>> bodyCaptor = ArgumentCaptor.forClass(Map.class);
        verify(requestBodySpec).body(bodyCaptor.capture());

        Map<String, Object> capturedBody = bodyCaptor.getValue();
        assertEquals("New Conversation Name", capturedBody.get("name"));
        assertEquals(false, capturedBody.get("auto_generate"));
        assertEquals("user-123", capturedBody.get("user"));
    }

    @Test
    public void testFileUpload() {
        // Create mock objects with more specific names to avoid confusion
        RestClient mockClient = mock(RestClient.class);
        RestClient.RequestBodyUriSpec mockUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec mockBodySpec = mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec mockResponseSpec = mock(RestClient.ResponseSpec.class);

        // Create builder that returns our mocked client
        RestClient.Builder mockClientBuilder = mock(RestClient.Builder.class);
        when(mockClientBuilder.baseUrl(anyString())).thenReturn(mockClientBuilder);
        when(mockClientBuilder.defaultHeaders(any())).thenReturn(mockClientBuilder);
        when(mockClientBuilder.defaultCookies(any())).thenReturn(mockClientBuilder);
        when(mockClientBuilder.build()).thenReturn(mockClient);

        // Test data setup
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

        // Setup mock chain - THE KEY FIX: Use Mockito.any() instead of any() to ensure proper type matching
        when(mockClient.post()).thenReturn(mockUriSpec);
        when(mockUriSpec.uri(eq(DatasetUriConstant.V1_FILES_UPLOAD))).thenReturn(mockBodySpec);
        when(mockBodySpec.header(eq(HttpHeaders.AUTHORIZATION), anyString())).thenReturn(mockBodySpec);
        when(mockBodySpec.headers(any())).thenReturn(mockBodySpec);
        when(mockBodySpec.contentType(eq(MediaType.MULTIPART_FORM_DATA))).thenReturn(mockBodySpec);

        // Multiple body() mocks to catch different argument types
        when(mockBodySpec.body(Mockito.any())).thenReturn(mockBodySpec);
        when(mockBodySpec.body(Mockito.any(Object.class))).thenReturn(mockBodySpec);
        when(mockBodySpec.body(Mockito.any(Map.class))).thenReturn(mockBodySpec);

        // Make sure retrieve returns the response spec
        when(mockBodySpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.onStatus(any())).thenReturn(mockResponseSpec);
        when(mockResponseSpec.body(eq(FileUploadResponse.class))).thenReturn(expectedResponse);

        // Create the client instance with our mocks
        DifyChatDefaultClient client = new DifyChatDefaultClient(
                "https://api.dify.ai",
                new DifyProperties.ClientConfig(),
                mockClientBuilder,
                WebClient.builder()
        );

        // Execute the method under test
        FileUploadResponse actualResponse = client.fileUpload(request);

        // Verify response
        assertNotNull(actualResponse);
        assertEquals(fileId, actualResponse.getId());
        assertEquals(fileName, actualResponse.getName());
        assertEquals(fileContent.length(), actualResponse.getSize());
        assertEquals("text/plain", actualResponse.getMimeType());

        // Verify basic interactions
        verify(mockClient).post();
        verify(mockUriSpec).uri(DatasetUriConstant.V1_FILES_UPLOAD);
        verify(mockBodySpec).contentType(MediaType.MULTIPART_FORM_DATA);

        // Capture the actual body to help diagnose issues
        ArgumentCaptor<Object> bodyCaptor = ArgumentCaptor.forClass(Object.class);
        verify(mockBodySpec).body(bodyCaptor.capture());
        // Debug output of the actual body type would be helpful for future debugging
        // System.out.println("Body class: " + bodyCaptor.getValue().getClass().getName());

        verify(mockResponseSpec).body(FileUploadResponse.class);
    }
}
