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
package io.github.guoshiqiufeng.dify.support.impl.workflow;

import io.github.guoshiqiufeng.dify.chat.dto.request.ChatMessageSendRequest;
import io.github.guoshiqiufeng.dify.client.core.http.HttpClientFactory;
import io.github.guoshiqiufeng.dify.client.core.http.TypeReference;
import io.github.guoshiqiufeng.dify.client.core.web.util.UriBuilder;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.core.pojo.request.ChatMessageVO;
import io.github.guoshiqiufeng.dify.support.impl.BaseClientTest;
import io.github.guoshiqiufeng.dify.support.impl.dto.workflow.WorkflowRunStreamResponseDto;
import io.github.guoshiqiufeng.dify.workflow.constant.WorkflowConstant;
import io.github.guoshiqiufeng.dify.workflow.dto.request.WorkflowLogsRequest;
import io.github.guoshiqiufeng.dify.workflow.dto.request.WorkflowRunRequest;
import io.github.guoshiqiufeng.dify.workflow.dto.response.*;
import io.github.guoshiqiufeng.dify.workflow.enums.StreamEventEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.util.*;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link DifyWorkflowDefaultClient}.
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/25 09:57
 */
@SuppressWarnings("unchecked")
public class DifyWorkflowDefaultClientTest extends BaseClientTest {

    private DifyWorkflowDefaultClient client;

    @BeforeEach
    public void setup() {
        super.setup();
        // Create real client with mocked HttpClient
        client = new DifyWorkflowDefaultClient(httpClientMock);
    }

    @Test
    public void testConstructor() {
        HttpClientFactory httpClientFactory = mock(HttpClientFactory.class);
        new DifyWorkflowDefaultClient("http://127.0.0.1", new DifyProperties.ClientConfig(), httpClientFactory);
    }

    @Test
    public void testRunWorkflow() {
        // Prepare test data
        String apiKey = "test-api-key";
        String userId = "test-user-id";

        WorkflowRunRequest request = new WorkflowRunRequest();
        request.setApiKey(apiKey);
        request.setUserId(userId);

        Map<String, Object> inputs = new HashMap<>();
        inputs.put("question", "What is the weather today?");
        request.setInputs(inputs);

        // Create expected response
        WorkflowRunResponse expectedResponse = new WorkflowRunResponse();
        expectedResponse.setWorkflowRunId("workflow-run-123456");

        // Mock the response
        when(responseSpecMock.body(WorkflowRunResponse.class)).thenReturn(expectedResponse);

        // Execute the method
        WorkflowRunResponse actualResponse = client.runWorkflow(request);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getWorkflowRunId(), actualResponse.getWorkflowRunId());

        // Verify WebClient interactions
        verify(httpClientMock).post();
        verify(requestBodyUriSpecMock).uri(WorkflowConstant.WORKFLOW_RUN_URL);
        verify(requestBodySpecMock).header(eq("AUTHORIZATION"), eq("Bearer " + apiKey));
        verify(requestBodySpecMock).body(any(ChatMessageVO.class));
        verify(responseSpecMock).body(WorkflowRunResponse.class);
    }

    @Test
    public void testRunWorkflowAndFile() {
        // Prepare test data
        String apiKey = "test-api-key";
        String userId = "test-user-id";

        WorkflowRunRequest request = new WorkflowRunRequest();
        request.setApiKey(apiKey);
        request.setUserId(userId);
        WorkflowRunRequest.WorkflowFile workflowFile = new WorkflowRunRequest.WorkflowFile();
        workflowFile.setType(null);
        workflowFile.setTransferMethod(null);
        workflowFile.setUrl("https://file.com");
        WorkflowRunRequest.WorkflowFile workflowFile2 = new WorkflowRunRequest.WorkflowFile();
        workflowFile2.setType("image");
        workflowFile2.setTransferMethod("remote_url");
        workflowFile2.setUrl("https://file.com");
        request.setFiles(List.of(workflowFile));
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("question", "What is the weather today?");
        request.setInputs(inputs);

        // Create expected response
        WorkflowRunResponse expectedResponse = new WorkflowRunResponse();
        expectedResponse.setWorkflowRunId("workflow-run-123456");

        // Mock the response
        when(responseSpecMock.body(WorkflowRunResponse.class)).thenReturn(expectedResponse);

        // Execute the method
        WorkflowRunResponse actualResponse = client.runWorkflow(request);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getWorkflowRunId(), actualResponse.getWorkflowRunId());

        // Verify WebClient interactions
        verify(httpClientMock).post();
        verify(requestBodyUriSpecMock).uri(WorkflowConstant.WORKFLOW_RUN_URL);
        verify(requestBodySpecMock).header(eq("AUTHORIZATION"), eq("Bearer " + apiKey));
        verify(requestBodySpecMock).body(any(ChatMessageVO.class));
        verify(responseSpecMock).body(WorkflowRunResponse.class);

        WorkflowRunRequest emptyRequest = new WorkflowRunRequest();
        emptyRequest.setApiKey(apiKey);
        emptyRequest.setUserId(userId);
        emptyRequest.setFiles(null);
        emptyRequest.setInputs(null);
        client.runWorkflow(emptyRequest);
    }

    @Test
    public void testRunWorkflowStream() {
        // Prepare test data
        String apiKey = "test-api-key";
        String userId = "test-user-id";

        WorkflowRunRequest request = new WorkflowRunRequest();
        request.setApiKey(apiKey);
        request.setUserId(userId);

        Map<String, Object> inputs = new HashMap<>();
        inputs.put("question", "What is the weather today?");
        request.setInputs(inputs);

        WorkflowRunRequest.WorkflowFile file = new WorkflowRunRequest.WorkflowFile();
        file.setType(null);
        file.setTransferMethod(null);
        file.setUrl("https://file.com");
        WorkflowRunRequest.WorkflowFile file2 = new WorkflowRunRequest.WorkflowFile();
        file2.setTransferMethod("remote_url");
        file2.setUrl("https://file.com");
        file2.setType("image");
        request.setFiles(List.of(file, file2));

        // Create expected responses for stream
        WorkflowRunStreamResponse response1 = new WorkflowRunStreamResponse();
        response1.setEvent(StreamEventEnum.workflow_started);
        response1.setWorkflowRunId("workflow-run-123456");

        WorkflowRunStreamResponse response2 = new WorkflowRunStreamResponse();
        response2.setEvent(StreamEventEnum.node_started);
        response2.setTaskId("task-123456");
        Map<String, Object> data2 = new HashMap<>();
        data2.put("node_id", "node-1");
        data2.put("message", "Processing your request...");
        response2.setData(data2);

        WorkflowRunStreamResponse response3 = new WorkflowRunStreamResponse();
        response3.setEvent(StreamEventEnum.text_chunk);
        response3.setTaskId("task-123456");
        Map<String, Object> data3 = new HashMap<>();
        data3.put("text", "The weather today is sunny.");
        response3.setData(data3);

        WorkflowRunStreamResponseDto dto1 = new WorkflowRunStreamResponseDto(response1);
        WorkflowRunStreamResponseDto dto2 = new WorkflowRunStreamResponseDto(response2);
        WorkflowRunStreamResponseDto dto3 = new WorkflowRunStreamResponseDto(response3);
        WorkflowRunStreamResponseDto dto4 = new WorkflowRunStreamResponseDto(null);

        List<WorkflowRunStreamResponseDto> responses = List.of(dto1, dto2, dto3, dto4);

        // Mock the response
        when(responseSpecMock.bodyToFlux(WorkflowRunStreamResponseDto.class)).thenReturn(Flux.fromIterable(responses));

        // Execute the method
        Flux<WorkflowRunStreamResponse> actualResponseFlux = client.runWorkflowStream(request);
        List<WorkflowRunStreamResponse> actualResponses = actualResponseFlux.collectList().block();

        // Verify the result
        assertNotNull(actualResponses);
        assertEquals(3, actualResponses.size());
        assertEquals(response1.getEvent(), actualResponses.get(0).getEvent());
        assertEquals(response1.getWorkflowRunId(), actualResponses.get(0).getWorkflowRunId());
        assertEquals(response3.getData(), actualResponses.get(2).getData());

        // Verify WebClient interactions
        verify(httpClientMock).post();
        verify(requestBodyUriSpecMock).uri(WorkflowConstant.WORKFLOW_RUN_URL);
        verify(requestBodySpecMock).header(eq("AUTHORIZATION"), eq("Bearer " + apiKey));
        verify(requestBodySpecMock).body(any(ChatMessageVO.class));
    }

    @Test
    public void testInfo() {
        // Prepare test data
        String apiKey = "test-api-key";
        String workflowRunId = "workflow-run-123456";

        // Create expected response
        WorkflowInfoResponse expectedResponse = new WorkflowInfoResponse();
        expectedResponse.setId(workflowRunId);
        expectedResponse.setWorkflowId("workflow-123");
        expectedResponse.setStatus("succeeded");

        // Set complex fields
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("question", "What is the weather today?");
        expectedResponse.setInputs(inputs);

        Map<String, Object> outputs = new HashMap<>();
        outputs.put("answer", "The weather today is sunny.");
        expectedResponse.setOutputs(outputs);

        expectedResponse.setElapsedTime(1.5f);
        expectedResponse.setTotalTokens(150);
        expectedResponse.setTotalSteps(3);
        expectedResponse.setCreatedAt(1745546400000L);
        expectedResponse.setFinishedAt(1745546410000L);

        // Mock the response
        when(responseSpecMock.body(WorkflowInfoResponse.class)).thenReturn(expectedResponse);

        // Execute the method
        WorkflowInfoResponse actualResponse = client.info(workflowRunId, apiKey);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getWorkflowId(), actualResponse.getWorkflowId());
        assertEquals(expectedResponse.getStatus(), actualResponse.getStatus());
        assertEquals(expectedResponse.getInputs(), actualResponse.getInputs());
        assertEquals(expectedResponse.getOutputs(), actualResponse.getOutputs());
        assertEquals(expectedResponse.getElapsedTime(), actualResponse.getElapsedTime());
        assertEquals(expectedResponse.getTotalTokens(), actualResponse.getTotalTokens());
        assertEquals(expectedResponse.getCreatedAt(), actualResponse.getCreatedAt());
        assertEquals(expectedResponse.getFinishedAt(), actualResponse.getFinishedAt());

        // Verify WebClient interactions
        verify(httpClientMock).get();
        verify(requestHeadersUriSpecMock).uri(eq(WorkflowConstant.WORKFLOW_RUN_URL + "/{workflowRunId}"), eq(workflowRunId));
        verify(requestHeadersSpecMock).header(eq("AUTHORIZATION"), eq("Bearer " + apiKey));
        verify(responseSpecMock).body(WorkflowInfoResponse.class);
    }

    @Test
    public void testStopWorkflowStream() {
        // Prepare test data
        String apiKey = "test-api-key";
        String taskId = "task-123456";
        String userId = "test-user-id";

        // Create expected response
        WorkflowStopResponse expectedResponse = new WorkflowStopResponse();
        expectedResponse.setResult("success");

        // Mock the response
        when(responseSpecMock.body(WorkflowStopResponse.class)).thenReturn(expectedResponse);

        // Execute the method
        WorkflowStopResponse actualResponse = client.stopWorkflowStream(apiKey, taskId, userId);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getResult(), actualResponse.getResult());

        // Verify WebClient interactions
        verify(httpClientMock).post();
        verify(requestBodyUriSpecMock).uri(eq(WorkflowConstant.WORKFLOW_TASKS_URL + "/{taskId}/stop"), eq(taskId));
        verify(requestBodySpecMock).header(eq("AUTHORIZATION"), eq("Bearer " + apiKey));
        verify(requestBodySpecMock).body(any(HashMap.class));
        verify(responseSpecMock).body(WorkflowStopResponse.class);
    }

    @Test
    public void testLogs() {
        // Prepare test data
        String apiKey = "test-api-key";
        int page = 1;
        int limit = 10;
        String keyword = "error";
        String status = "completed";

        WorkflowLogsRequest request = new WorkflowLogsRequest();
        request.setApiKey(apiKey);
        request.setPage(page);
        request.setLimit(limit);
        request.setKeyword(keyword);
        request.setStatus(status);

        // We need to mock the UriBuilder for the queryParam functionality
        UriBuilder uriBuilderMock = mock(UriBuilder.class);
        URI uriMock = mock(URI.class);
        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenAnswer(invocation -> {
            Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);

            when(uriBuilderMock.path(anyString())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("page"), anyInt())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParam(eq("limit"), anyInt())).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("status"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.queryParamIfPresent(eq("keyword"), any(Optional.class))).thenReturn(uriBuilderMock);
            when(uriBuilderMock.build()).thenReturn(uriMock);

            uriFunction.apply(uriBuilderMock);
            return requestHeadersSpecMock;
        });

        // Create expected response
        DifyPageResult<WorkflowLogs> expectedResponse = new DifyPageResult<>();
        List<WorkflowLogs> logs = new ArrayList<>();

        WorkflowLogs log1 = new WorkflowLogs();
        log1.setId("log-123456");

        // Create workflow run for the log
        WorkflowRunResponse workflowRun1 = new WorkflowRunResponse();
        workflowRun1.setWorkflowRunId("workflow-run-123456");
        log1.setWorkflowRun(workflowRun1);

        log1.setCreatedFrom("api");
        log1.setCreatedByRole("user");
        log1.setCreatedByAccount("test@example.com");

        // Create end user info
        WorkflowLogs.CreatedByEndUser endUser1 = new WorkflowLogs.CreatedByEndUser();
        endUser1.setId("user-123456");
        endUser1.setType("user");
        endUser1.setIsAnonymous(false);
        endUser1.setSessionId("session-123456");
        log1.setCreatedByEndUser(endUser1);

        log1.setCreatedAt(1745546400000L);
        logs.add(log1);

        WorkflowLogs log2 = new WorkflowLogs();
        log2.setId("log-789012");

        // Create workflow run for the second log
        WorkflowRunResponse workflowRun2 = new WorkflowRunResponse();
        workflowRun2.setWorkflowRunId("workflow-run-789012");
        log2.setWorkflowRun(workflowRun2);

        log2.setCreatedFrom("web");
        log2.setCreatedByRole("admin");
        log2.setCreatedByAccount("admin@example.com");
        log2.setCreatedAt(1745546500000L);
        logs.add(log2);

        expectedResponse.setData(logs);
        expectedResponse.setTotal(2);
        expectedResponse.setPage(page);
        expectedResponse.setLimit(limit);

        // Mock the response
        when(responseSpecMock.body(any(TypeReference.class))).thenReturn(expectedResponse);

        // Execute the method
        DifyPageResult<WorkflowLogs> actualResponse = client.logs(request);

        // Verify the result
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getTotal(), actualResponse.getTotal());
        assertEquals(expectedResponse.getPage(), actualResponse.getPage());
        assertEquals(expectedResponse.getLimit(), actualResponse.getLimit());
        assertEquals(expectedResponse.getData().size(), actualResponse.getData().size());

        // Verify first log details
        assertEquals(log1.getId(), actualResponse.getData().get(0).getId());
        assertEquals(log1.getWorkflowRun().getWorkflowRunId(), actualResponse.getData().get(0).getWorkflowRun().getWorkflowRunId());
        assertEquals(log1.getCreatedFrom(), actualResponse.getData().get(0).getCreatedFrom());
        assertEquals(log1.getCreatedByRole(), actualResponse.getData().get(0).getCreatedByRole());
        assertEquals(log1.getCreatedByAccount(), actualResponse.getData().get(0).getCreatedByAccount());
        assertEquals(log1.getCreatedAt(), actualResponse.getData().get(0).getCreatedAt());

        // Verify CreatedByEndUser details
        assertEquals(log1.getCreatedByEndUser().getId(), actualResponse.getData().get(0).getCreatedByEndUser().getId());
        assertEquals(log1.getCreatedByEndUser().getType(), actualResponse.getData().get(0).getCreatedByEndUser().getType());
        assertEquals(log1.getCreatedByEndUser().getIsAnonymous(), actualResponse.getData().get(0).getCreatedByEndUser().getIsAnonymous());

        // Verify second log basic details
        assertEquals(log2.getId(), actualResponse.getData().get(1).getId());
        assertEquals(log2.getWorkflowRun().getWorkflowRunId(), actualResponse.getData().get(1).getWorkflowRun().getWorkflowRunId());

        // Verify WebClient interactions
        verify(httpClientMock).get();
        verify(requestHeadersUriSpecMock).uri(any(Function.class));
        verify(requestHeadersSpecMock).header(eq("AUTHORIZATION"), eq("Bearer " + apiKey));
        verify(responseSpecMock).body(any(TypeReference.class));

        WorkflowLogsRequest defaultRequest = new WorkflowLogsRequest();
        defaultRequest.setApiKey("wf-api-key-123");
        defaultRequest.setPage(null);
        defaultRequest.setLimit(null);
        defaultRequest.setKeyword("");
        defaultRequest.setStatus("");
        client.logs(defaultRequest);
    }
}
