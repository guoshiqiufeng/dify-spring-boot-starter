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
package io.github.guoshiqiufeng.dify.workflow;

import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.workflow.dto.request.WorkflowLogsRequest;
import io.github.guoshiqiufeng.dify.workflow.dto.request.WorkflowRunRequest;
import io.github.guoshiqiufeng.dify.workflow.dto.response.WorkflowLogs;
import io.github.guoshiqiufeng.dify.workflow.dto.response.WorkflowRunResponse;
import io.github.guoshiqiufeng.dify.workflow.dto.response.WorkflowStopResponse;
import reactor.core.publisher.Flux;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/11 14:22
 */
public interface DifyWorkflow {

    /**
     * 运行工作流
     *
     * @param request 工作流运行请求对象，包含输入参数和配置
     * @return WorkflowRunResponse 工作流运行响应对象，包含执行结果和状态信息
     */
    WorkflowRunResponse runWorkflow(WorkflowRunRequest request);

    /**
     * 运行工作流并获取实时数据流
     *
     * @param request 工作流运行请求对象，包含输入参数和配置
     * @return Flux<WorkflowRunResponse> 工作流执行过程的实时响应流，用于持续获取执行状态
     */
    Flux<WorkflowRunResponse> runWorkflowStream(WorkflowRunRequest request);

    /**
     * 获取工作流运行详情
     *
     * @param workflowRunId 工作流运行实例ID，用于标识具体执行任务
     * @param apiKey        API密钥，用于身份验证
     * @return WorkflowRunResponse 工作流运行的详细信息，包含状态、输入输出等数据
     */
    WorkflowRunResponse info(String workflowRunId, String apiKey);

    /**
     * 停止工作流流式响应
     *
     * @param apiKey API密钥，用于身份验证
     * @param taskId 任务ID，标识需要停止的流式响应任务
     * @param userId 用户ID，标识操作者
     * @return WorkflowStopResponse 停止操作的响应结果，包含停止状态和相关信息
     */
    WorkflowStopResponse stopWorkflowStream(String apiKey, String taskId, String userId);

    /**
     * 查询工作流日志
     *
     * @param request 日志查询请求对象，包含分页、排序和过滤条件
     * @return DifyPageResult<WorkflowLogs> 分页结果，包含工作流执行日志列表
     */
    DifyPageResult<WorkflowLogs> logs(WorkflowLogsRequest request);
}
