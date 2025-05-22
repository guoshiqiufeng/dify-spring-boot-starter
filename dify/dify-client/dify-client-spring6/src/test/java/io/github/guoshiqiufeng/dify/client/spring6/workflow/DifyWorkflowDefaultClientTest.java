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
package io.github.guoshiqiufeng.dify.client.spring6.workflow;

import io.github.guoshiqiufeng.dify.client.spring6.BaseClientTest;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.core.enums.ResponseModeEnum;
import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.core.pojo.request.ChatMessageVO;
import io.github.guoshiqiufeng.dify.workflow.constant.WorkflowConstant;
import io.github.guoshiqiufeng.dify.workflow.dto.request.WorkflowLogsRequest;
import io.github.guoshiqiufeng.dify.workflow.dto.request.WorkflowRunRequest;
import io.github.guoshiqiufeng.dify.workflow.dto.response.*;
import io.github.guoshiqiufeng.dify.workflow.enums.StreamEventEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.util.*;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link DifyWorkflowDefaultClient}.
 *
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/21 16:10
 */
@SuppressWarnings("unchecked")
@DisplayName("DifyWorkflowDefaultClient Tests")
public class DifyWorkflowDefaultClientTest extends BaseClientTest {
    private static final String BASE_URL = "https://api.dify.ai";

    private DifyWorkflowDefaultClient client;

    @BeforeEach
    public void setup() {
        super.setup();
        client = new DifyWorkflowDefaultClient(BASE_URL, new DifyProperties.ClientConfig(), restClientMock.getRestClientBuilder(), webClientMock.getWebClientBuilder());
    }

    @Test
    @DisplayName("Test runWorkflow method with valid request")
    public void testRunWorkflow() {
        RestClient.RequestBodySpec requestBodySpec = restClientMock.getRequestBodySpec();
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestBodyUriSpec requestBodyUriSpec = restClientMock.getRequestBodyUriSpec();

        // Mock the workflow response
        WorkflowRunResponse mockResponse = new WorkflowRunResponse();
        mockResponse.setTaskId("task-123");
        mockResponse.setWorkflowRunId("workflow-run-123");

        when(responseSpec.body(WorkflowRunResponse.class)).thenReturn(mockResponse);

        // Create a workflow run request
        WorkflowRunRequest request = new WorkflowRunRequest();
        request.setApiKey("wf-api-key-123");
        request.setUserId("user-123");
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("question", "How does this workflow work?");
        request.setInputs(inputs);

        // Call the method to test
        WorkflowRunResponse response = client.runWorkflow(request);

        // Verify the response
        assertNotNull(response);
        assertEquals("task-123", response.getTaskId());
        assertEquals("workflow-run-123", response.getWorkflowRunId());


        // Verify interactions with mocks
        verify(requestBodyUriSpec).uri(WorkflowConstant.WORKFLOW_RUN_URL);
        verify(requestBodySpec).header(HttpHeaders.AUTHORIZATION, "Bearer wf-api-key-123");

        // Capture and verify the request body
        ArgumentCaptor<ChatMessageVO> bodyCaptor = ArgumentCaptor.forClass(ChatMessageVO.class);
        verify(requestBodySpec).body(bodyCaptor.capture());

        ChatMessageVO capturedBody = bodyCaptor.getValue();
        assertEquals(ResponseModeEnum.blocking, capturedBody.getResponseMode());
        assertEquals("user-123", capturedBody.getUser());
        assertEquals("How does this workflow work?", capturedBody.getInputs().get("question"));
    }

    @Test
    @DisplayName("Test runWorkflowStream method with valid request")
    public void testRunWorkflowStream() {
        WebClient.RequestBodySpec requestBodySpec = webClientMock.getRequestBodySpec();
        WebClient.ResponseSpec responseSpec = webClientMock.getResponseSpec();
        WebClient.RequestBodyUriSpec requestBodyUriSpec = webClientMock.getRequestBodyUriSpec();

        // Mock the stream response
        WorkflowRunStreamResponse mockStreamResponse1 = new WorkflowRunStreamResponse();
        mockStreamResponse1.setTaskId("task-123");
        mockStreamResponse1.setEvent(StreamEventEnum.workflow_started);

        WorkflowRunStreamResponse mockStreamResponse2 = new WorkflowRunStreamResponse();
        mockStreamResponse2.setTaskId("task-124");
        mockStreamResponse2.setEvent(StreamEventEnum.workflow_finished);

        WorkflowRunStreamResponse mockStreamResponse3 = new WorkflowRunStreamResponse();
        mockStreamResponse3.setTaskId("task-125");
        mockStreamResponse3.setEvent(StreamEventEnum.text_chunk);
        mockStreamResponse3.setData(Map.of("text", "\n\n", "from_variable_selector", "[]"));

        Flux<WorkflowRunStreamResponse> mockFlux = Flux.just(mockStreamResponse1, mockStreamResponse2, mockStreamResponse3);
        doReturn(mockFlux).when(responseSpec).bodyToFlux(WorkflowRunStreamResponse.class);

        // Create a workflow run request
        WorkflowRunRequest request = new WorkflowRunRequest();
        request.setApiKey("wf-api-key-123");
        request.setUserId("user-123");
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("question", "How does this workflow work?");
        request.setInputs(inputs);

        // Add a file to the request
        List<WorkflowRunRequest.WorkflowFile> files = new ArrayList<>();
        WorkflowRunRequest.WorkflowFile file = new WorkflowRunRequest.WorkflowFile();
        file.setType(null);
        file.setTransferMethod(null);
        file.setUrl("https://example.com/image.jpg");
        files.add(file);
        request.setFiles(files);

        // Call the method to test
        Flux<WorkflowRunStreamResponse> responseFlux = client.runWorkflowStream(request);

        // Collect the response items
        List<WorkflowRunStreamResponse> responses = responseFlux.collectList().block();

        // Verify the response
        assertNotNull(responses);
        assertEquals(3, responses.size());
        assertEquals("task-123", responses.get(0).getTaskId());
        assertEquals(StreamEventEnum.workflow_started, responses.get(0).getEvent());
        assertEquals("task-124", responses.get(1).getTaskId());
        assertEquals(StreamEventEnum.workflow_finished, responses.get(1).getEvent());

        // Verify interactions with mocks
        verify(requestBodyUriSpec).uri(WorkflowConstant.WORKFLOW_RUN_URL);
        verify(requestBodySpec).header(HttpHeaders.AUTHORIZATION, "Bearer wf-api-key-123");

        // Capture and verify the request body
        ArgumentCaptor<ChatMessageVO> bodyCaptor = ArgumentCaptor.forClass(ChatMessageVO.class);
        verify(requestBodySpec).bodyValue(bodyCaptor.capture());

        ChatMessageVO capturedBody = bodyCaptor.getValue();
        assertEquals(ResponseModeEnum.streaming, capturedBody.getResponseMode());
        assertEquals("user-123", capturedBody.getUser());
        assertEquals("How does this workflow work?", capturedBody.getInputs().get("question"));
        assertNotNull(capturedBody.getFiles());
        assertEquals(1, capturedBody.getFiles().size());
        assertEquals("https://example.com/image.jpg", capturedBody.getFiles().get(0).getUrl());
        assertEquals("image", capturedBody.getFiles().get(0).getType());
        assertEquals("remote_url", capturedBody.getFiles().get(0).getTransferMethod());

        WorkflowRunRequest emptyRequest = new WorkflowRunRequest();
        emptyRequest.setApiKey("wf-api-key-123");
        emptyRequest.setUserId("user-123");
        emptyRequest.setFiles(null);
        emptyRequest.setInputs(null);
        client.runWorkflow(emptyRequest);
    }

    @Test
    @DisplayName("Test info method with valid workflowRunId")
    public void testInfo() {
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = restClientMock.getRequestHeadersUriSpec();
        RestClient.RequestHeadersSpec<?> requestHeadersSpec = restClientMock.getRequestHeadersSpec();

        // Mock the info response
        WorkflowInfoResponse mockResponse = new WorkflowInfoResponse();
        mockResponse.setId("workflow-run-123");
        mockResponse.setStatus("completed");
        when(responseSpec.body(WorkflowInfoResponse.class)).thenReturn(mockResponse);

        // Call the method to test
        String workflowRunId = "workflow-run-123";
        String apiKey = "wf-api-key-123";
        WorkflowInfoResponse response = client.info(workflowRunId, apiKey);

        // Verify the response
        assertNotNull(response);
        assertEquals("workflow-run-123", response.getId());
        assertEquals("completed", response.getStatus());

        // Verify interactions with mocks
        verify(requestHeadersUriSpec).uri(WorkflowConstant.WORKFLOW_RUN_URL + "/{workflowRunId}", workflowRunId);
        verify(requestHeadersSpec).header(HttpHeaders.AUTHORIZATION, "Bearer wf-api-key-123");
    }

    @Test
    @DisplayName("Test stopWorkflowStream method with valid parameters")
    public void testStopWorkflowStream() {
        RestClient.RequestBodySpec requestBodySpec = restClientMock.getRequestBodySpec();
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestBodyUriSpec requestBodyUriSpec = restClientMock.getRequestBodyUriSpec();

        // Mock the stop response
        WorkflowStopResponse mockResponse = new WorkflowStopResponse();
        mockResponse.setResult("success");
        when(responseSpec.body(WorkflowStopResponse.class)).thenReturn(mockResponse);

        // Call the method to test
        String apiKey = "wf-api-key-123";
        String taskId = "task-123";
        String userId = "user-123";
        WorkflowStopResponse response = client.stopWorkflowStream(apiKey, taskId, userId);

        // Verify the response
        assertNotNull(response);
        assertEquals("success", response.getResult());

        // Verify interactions with mocks
        verify(requestBodyUriSpec).uri(WorkflowConstant.WORKFLOW_TASKS_URL + "/{taskId}/stop", taskId);
        verify(requestBodySpec).header(HttpHeaders.AUTHORIZATION, "Bearer wf-api-key-123");

        // Capture and verify the request body
        ArgumentCaptor<Map<String, String>> bodyCaptor = ArgumentCaptor.forClass(Map.class);
        verify(requestBodySpec).body(bodyCaptor.capture());

        Map<String, String> capturedBody = bodyCaptor.getValue();
        assertEquals("user-123", capturedBody.get("user"));
    }

    @Test
    @DisplayName("Test logs method with valid request")
    public void testLogs() {
        RestClient.ResponseSpec responseSpec = restClientMock.getResponseSpec();
        RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = restClientMock.getRequestHeadersUriSpec();
        RestClient.RequestHeadersSpec<?> requestHeadersSpec = restClientMock.getRequestHeadersSpec();

        // Mock the logs response
        DifyPageResult<WorkflowLogs> mockResponse = new DifyPageResult<>();
        mockResponse.setLimit(20);
        mockResponse.setPage(1);
        mockResponse.setTotal(2);

        List<WorkflowLogs> logs = new ArrayList<>();
        WorkflowLogs log1 = new WorkflowLogs();
        log1.setId("log-1");
        logs.add(log1);

        WorkflowLogs log2 = new WorkflowLogs();
        log2.setId("log-2");
        logs.add(log2);

        mockResponse.setData(logs);
        doReturn(mockResponse).when(responseSpec).body(any(ParameterizedTypeReference.class));

        // Create logs request
        WorkflowLogsRequest request = new WorkflowLogsRequest();
        request.setApiKey("wf-api-key-123");
        request.setPage(1);
        request.setLimit(20);
        request.setStatus("all");
        request.setKeyword("test");

        // We need to mock the UriBuilder for the queryParam functionality
        UriBuilder uriBuilderMock = mock(UriBuilder.class);
        URI uriMock = mock(URI.class);

        when(requestHeadersUriSpec.uri(any(Function.class))).thenAnswer(invocation -> {
            Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);

            when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("page"), anyInt())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("limit"), anyInt())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("status"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("keyword"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.build()).thenReturn(uriMock);

            uriFunction.apply(uriBuilderMock);
            return requestHeadersSpec;
        });

        // Call the method to test
        DifyPageResult<WorkflowLogs> response = client.logs(request);

        // Verify the response
        assertNotNull(response);
        assertEquals(1, response.getPage());
        assertEquals(20, response.getLimit());
        assertEquals(2, response.getTotal());
        assertEquals(2, response.getData().size());
        assertEquals("log-1", response.getData().get(0).getId());
        assertEquals("log-2", response.getData().get(1).getId());

        // Verify interactions with mocks
        verify(requestHeadersUriSpec).uri(any(Function.class));
        verify(requestHeadersSpec).header(HttpHeaders.AUTHORIZATION, "Bearer wf-api-key-123");

        WorkflowLogsRequest defaultRequest = new WorkflowLogsRequest();
        defaultRequest.setApiKey("wf-api-key-123");
        defaultRequest.setPage(null);
        defaultRequest.setLimit(null);
        client.logs(defaultRequest);
    }
}
