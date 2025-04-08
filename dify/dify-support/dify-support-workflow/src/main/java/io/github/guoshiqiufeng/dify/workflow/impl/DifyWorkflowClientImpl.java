package io.github.guoshiqiufeng.dify.workflow.impl;

import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.workflow.DifyWorkflow;
import io.github.guoshiqiufeng.dify.workflow.client.DifyWorkflowClient;
import io.github.guoshiqiufeng.dify.workflow.dto.request.WorkflowLogsRequest;
import io.github.guoshiqiufeng.dify.workflow.dto.request.WorkflowRunRequest;
import io.github.guoshiqiufeng.dify.workflow.dto.response.*;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/4/8 16:50
 */
@Slf4j
public class DifyWorkflowClientImpl implements DifyWorkflow {

    private final DifyWorkflowClient difyWorkflowClient;

    public DifyWorkflowClientImpl(DifyWorkflowClient difyWorkflowClient) {
        this.difyWorkflowClient = difyWorkflowClient;
    }

    @Override
    public WorkflowRunResponse runWorkflow(WorkflowRunRequest request) {
        return difyWorkflowClient.runWorkflow(request);
    }

    @Override
    public Flux<WorkflowRunStreamResponse> runWorkflowStream(WorkflowRunRequest request) {
        return difyWorkflowClient.runWorkflowStream(request);
    }

    @Override
    public WorkflowInfoResponse info(String workflowRunId, String apiKey) {
        return difyWorkflowClient.info(workflowRunId, apiKey);
    }

    @Override
    public WorkflowStopResponse stopWorkflowStream(String apiKey, String taskId, String userId) {
        return difyWorkflowClient.stopWorkflowStream(apiKey, taskId, userId);
    }

    @Override
    public DifyPageResult<WorkflowLogs> logs(WorkflowLogsRequest request) {
        return difyWorkflowClient.logs(request);
    }
}
