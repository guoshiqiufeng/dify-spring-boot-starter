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
package io.github.guoshiqiufeng.dify.workflow.client;

import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.workflow.dto.request.WorkflowLogsRequest;
import io.github.guoshiqiufeng.dify.workflow.dto.request.WorkflowRunRequest;
import io.github.guoshiqiufeng.dify.workflow.dto.response.*;
import reactor.core.publisher.Flux;

/**
 * Dify Workflow Client Interface
 * Provides methods to interact with Dify's workflow API for running workflows,
 * managing workflow execution, retrieving workflow information, and accessing logs.
 *
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/8 16:26
 */
public interface DifyWorkflowClient {

    /**
     * Runs a workflow synchronously and returns the result
     *
     * @param request The workflow run request containing workflow parameters and inputs
     * @return The workflow run response with execution results
     */
    WorkflowRunResponse runWorkflow(WorkflowRunRequest request);

    /**
     * Runs a workflow and returns the results as a reactive stream
     * Used for streaming responses during workflow execution
     *
     * @param request The workflow run request containing workflow parameters and inputs
     * @return A Flux stream of workflow run responses
     */
    Flux<WorkflowRunStreamResponse> runWorkflowStream(WorkflowRunRequest request);

    /**
     * Retrieves information about a specific workflow run
     *
     * @param workflowRunId The ID of the workflow run to retrieve information for
     * @param apiKey        The API key for authentication and authorization to the Dify API
     * @return The workflow information response
     */
    WorkflowInfoResponse info(String workflowRunId, String apiKey);

    /**
     * Stops an ongoing workflow stream execution
     *
     * @param apiKey The API key for authentication and authorization to the Dify API
     * @param taskId The ID of the streaming task to stop
     * @param userId The ID of the user who initiated the workflow
     * @return The workflow stop response
     */
    WorkflowStopResponse stopWorkflowStream(String apiKey, String taskId, String userId);

    /**
     * Retrieves execution logs for a workflow
     *
     * @param request The workflow logs request containing parameters for fetching logs
     * @return Paginated result containing workflow log data
     */
    DifyPageResult<WorkflowLogs> logs(WorkflowLogsRequest request);

}
