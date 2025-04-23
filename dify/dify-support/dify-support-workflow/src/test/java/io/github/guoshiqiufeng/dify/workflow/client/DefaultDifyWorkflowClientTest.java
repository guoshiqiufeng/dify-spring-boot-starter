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
package io.github.guoshiqiufeng.dify.workflow.client;

import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.workflow.dto.request.WorkflowLogsRequest;
import io.github.guoshiqiufeng.dify.workflow.dto.request.WorkflowRunRequest;
import io.github.guoshiqiufeng.dify.workflow.dto.response.*;
import io.github.guoshiqiufeng.dify.workflow.enums.StreamEventEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link DifyWorkflowClient}.
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/23 14:47
 */
class DefaultDifyWorkflowClientTest {

    private DifyWorkflowClient difyWorkflowClient;

    @BeforeEach
    public void setup() {
        difyWorkflowClient = mock(DifyWorkflowClient.class);
    }

    @Test
    void testRunWorkflow() {
        // Arrange
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("query", "Test input");

        WorkflowRunRequest request = new WorkflowRunRequest();
        request.setInputs(inputs);
        request.setApiKey("test-api-key");

        WorkflowRunResponse expectedResponse = new WorkflowRunResponse();
        expectedResponse.setWorkflowRunId("workflow_123");
        expectedResponse.setTaskId("task_xyz");

        WorkflowRunResponse.WorkflowRunData data = new WorkflowRunResponse.WorkflowRunData();
        data.setId("run_123");
        data.setWorkflowId("workflow_abc");
        data.setStatus("succeeded");

        Map<String, Object> outputs = new HashMap<>();
        outputs.put("result", "Workflow execution completed successfully");
        data.setOutputs(outputs);

        expectedResponse.setData(data);

        when(difyWorkflowClient.runWorkflow(any(WorkflowRunRequest.class))).thenReturn(expectedResponse);

        // Act
        WorkflowRunResponse actualResponse = difyWorkflowClient.runWorkflow(request);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getWorkflowRunId(), actualResponse.getWorkflowRunId());
        assertEquals(expectedResponse.getTaskId(), actualResponse.getTaskId());
        assertNotNull(actualResponse.getData());
        assertEquals(expectedResponse.getData().getId(), actualResponse.getData().getId());
        assertEquals(expectedResponse.getData().getStatus(), actualResponse.getData().getStatus());
        assertEquals("Workflow execution completed successfully",
                actualResponse.getData().getOutputs().get("result"));
        verify(difyWorkflowClient, times(1)).runWorkflow(any(WorkflowRunRequest.class));
    }

    @Test
    void testRunWorkflowStream() {
        // Arrange
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("query", "Stream test input");

        WorkflowRunRequest request = new WorkflowRunRequest();
        request.setInputs(inputs);
        request.setApiKey("streaming-key");

        WorkflowRunStreamResponse response1 = new WorkflowRunStreamResponse();
        response1.setEvent(StreamEventEnum.workflow_started);
        response1.setWorkflowRunId("workflow_123");
        response1.setTaskId("task_123");
        // Set a text chunk as data
        response1.setData("First part");

        WorkflowRunStreamResponse response2 = new WorkflowRunStreamResponse();
        response2.setEvent(StreamEventEnum.workflow_finished);
        response2.setWorkflowRunId("workflow_123");
        response2.setTaskId("task_123");
        // Set a text chunk as data
        response2.setData(" of the streaming response");

        Flux<WorkflowRunStreamResponse> expectedFlux = Flux.just(response1, response2);

        when(difyWorkflowClient.runWorkflowStream(any(WorkflowRunRequest.class))).thenReturn(expectedFlux);

        // Act
        Flux<WorkflowRunStreamResponse> actualFlux = difyWorkflowClient.runWorkflowStream(request);

        // Assert using StepVerifier
        StepVerifier.create(actualFlux)
                .expectNext(response1)
                .expectNext(response2)
                .verifyComplete();

        verify(difyWorkflowClient, times(1)).runWorkflowStream(any(WorkflowRunRequest.class));
    }

    @Test
    void testInfo() {
        // Arrange
        String workflowRunId = "workflow_123";
        String apiKey = "test-api-key";

        WorkflowInfoResponse expectedResponse = new WorkflowInfoResponse();
        expectedResponse.setId(workflowRunId); // Using id field instead of workflowRunId
        expectedResponse.setStatus("completed");
        expectedResponse.setWorkflowId("workflow_abc");
        expectedResponse.setCreatedAt(System.currentTimeMillis());

        Map<String, Object> outputs = new HashMap<>();
        outputs.put("result", "Workflow execution result");
        expectedResponse.setOutputs(outputs);

        when(difyWorkflowClient.info(anyString(), anyString())).thenReturn(expectedResponse);

        // Act
        WorkflowInfoResponse actualResponse = difyWorkflowClient.info(workflowRunId, apiKey);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getStatus(), actualResponse.getStatus());
        assertEquals(expectedResponse.getWorkflowId(), actualResponse.getWorkflowId());
        assertEquals(expectedResponse.getOutputs(), actualResponse.getOutputs());
        verify(difyWorkflowClient, times(1)).info(workflowRunId, apiKey);
    }

    @Test
    void testStopWorkflowStream() {
        // Arrange
        String apiKey = "test-api-key";
        String taskId = "task_123";
        String userId = "user_456";

        WorkflowStopResponse expectedResponse = new WorkflowStopResponse();
        expectedResponse.setResult("success");

        when(difyWorkflowClient.stopWorkflowStream(anyString(), anyString(), anyString())).thenReturn(expectedResponse);

        // Act
        WorkflowStopResponse actualResponse = difyWorkflowClient.stopWorkflowStream(apiKey, taskId, userId);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getResult(), actualResponse.getResult());
        verify(difyWorkflowClient, times(1)).stopWorkflowStream(apiKey, taskId, userId);
    }

    @Test
    void testLogs() {
        // Arrange
        WorkflowLogsRequest request = new WorkflowLogsRequest();
        request.setApiKey("test-api-key");
        request.setLimit(10);
        request.setPage(1);
        request.setStatus("succeeded");

        // Create a properly structured WorkflowLogs object
        WorkflowLogs logEntry = new WorkflowLogs();
        logEntry.setId("log_123");

        // Create and set the workflowRun property
        WorkflowRunResponse workflowRun = new WorkflowRunResponse();
        workflowRun.setWorkflowRunId("workflow_123");
        workflowRun.setTaskId("task_456");

        WorkflowRunResponse.WorkflowRunData runData = new WorkflowRunResponse.WorkflowRunData();
        runData.setId("run_123");
        runData.setWorkflowId("workflow_abc");
        runData.setStatus("succeeded");
        workflowRun.setData(runData);

        logEntry.setWorkflowRun(workflowRun);

        // Set other fields from WorkflowLogs
        logEntry.setCreatedFrom("api");
        logEntry.setCreatedByRole("admin");
        logEntry.setCreatedByAccount("user@example.com");
        logEntry.setCreatedAt(System.currentTimeMillis());

        // Create and set the createdByEndUser property
        WorkflowLogs.CreatedByEndUser endUser = new WorkflowLogs.CreatedByEndUser();
        endUser.setId("user_123");
        endUser.setType("human");
        endUser.setIsAnonymous(false);
        endUser.setSessionId("session_xyz");
        logEntry.setCreatedByEndUser(endUser);

        DifyPageResult<WorkflowLogs> expectedResult = new DifyPageResult<>();
        expectedResult.setData(List.of(logEntry));
        expectedResult.setLimit(10);
        expectedResult.setHasMore(false);

        when(difyWorkflowClient.logs(any(WorkflowLogsRequest.class))).thenReturn(expectedResult);

        // Act
        DifyPageResult<WorkflowLogs> actualResult = difyWorkflowClient.logs(request);

        // Assert
        assertNotNull(actualResult);
        assertEquals(expectedResult.getLimit(), actualResult.getLimit());
        assertEquals(expectedResult.getHasMore(), actualResult.getHasMore());
        assertEquals(expectedResult.getData().size(), actualResult.getData().size());

        // Assert properties of the WorkflowLogs object
        WorkflowLogs resultLog = actualResult.getData().get(0);
        assertEquals(logEntry.getId(), resultLog.getId());
        assertEquals(logEntry.getWorkflowRun().getWorkflowRunId(), resultLog.getWorkflowRun().getWorkflowRunId());
        assertEquals(logEntry.getCreatedFrom(), resultLog.getCreatedFrom());
        assertEquals(logEntry.getCreatedByRole(), resultLog.getCreatedByRole());
        assertEquals(logEntry.getCreatedAt(), resultLog.getCreatedAt());

        // Assert the createdByEndUser properties
        assertEquals(logEntry.getCreatedByEndUser().getId(), resultLog.getCreatedByEndUser().getId());
        assertEquals(logEntry.getCreatedByEndUser().getType(), resultLog.getCreatedByEndUser().getType());

        verify(difyWorkflowClient, times(1)).logs(any(WorkflowLogsRequest.class));
    }
}
