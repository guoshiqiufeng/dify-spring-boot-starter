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
import reactor.core.publisher.Flux;

/**
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/8 16:26
 */
public interface DifyWorkflowClient {

    WorkflowRunResponse runWorkflow(WorkflowRunRequest request);

    Flux<WorkflowRunStreamResponse> runWorkflowStream(WorkflowRunRequest request);

    WorkflowInfoResponse info(String workflowRunId, String apiKey);

    WorkflowStopResponse stopWorkflowStream(String apiKey, String taskId, String userId);

    DifyPageResult<WorkflowLogs> logs(WorkflowLogsRequest request);

}
