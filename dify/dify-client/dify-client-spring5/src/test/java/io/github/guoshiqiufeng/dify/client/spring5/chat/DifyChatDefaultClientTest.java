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

import io.github.guoshiqiufeng.dify.chat.dto.request.FileUploadRequest;
import io.github.guoshiqiufeng.dify.chat.dto.response.AppInfoResponse;
import io.github.guoshiqiufeng.dify.chat.dto.response.AppMetaResponse;
import io.github.guoshiqiufeng.dify.chat.dto.response.FileUploadResponse;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.dataset.constant.DatasetUriConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link DifyChatDefaultClient}.
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/24 14:29
 */
public class DifyChatDefaultClientTest {

    private DifyChatDefaultClient difyChatDefaultClient;

    @Mock
    private WebClient webClientMock;

    @Mock
    private WebClient.Builder webClientBuilderMock;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpecMock;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpecMock;

    @Mock
    private WebClient.RequestBodySpec requestBodySpecMock;

    @Mock
    private WebClient.ResponseSpec responseSpecMock;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Setup WebClient.Builder behavior for proper chaining
        when(webClientBuilderMock.baseUrl(anyString())).thenReturn(webClientBuilderMock);
        when(webClientBuilderMock.defaultHeaders(any())).thenReturn(webClientBuilderMock);
        when(webClientBuilderMock.defaultCookies(any())).thenReturn(webClientBuilderMock);
        when(webClientBuilderMock.build()).thenReturn(webClientMock);

        // Setup WebClient mock behavior chain
        when(webClientMock.post()).thenReturn(requestBodyUriSpecMock);
        when(requestBodyUriSpecMock.uri(anyString())).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.headers(any())).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.contentType(any())).thenReturn(requestBodySpecMock);
        when(requestBodySpecMock.bodyValue(any())).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.onStatus(any(), any())).thenReturn(responseSpecMock);

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
        verify(webClientMock).post();
        verify(requestBodyUriSpecMock).uri(DatasetUriConstant.V1_INFO);

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
        verify(webClientMock).post();
        verify(requestBodyUriSpecMock).uri(DatasetUriConstant.V1_META);

        // Verify response handling
        verify(responseSpecMock).bodyToMono(AppMetaResponse.class);
    }
}
