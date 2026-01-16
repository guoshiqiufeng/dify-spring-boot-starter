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

import io.github.guoshiqiufeng.dify.core.bean.BeanUtils;
import io.github.guoshiqiufeng.dify.core.utils.CollUtil;
import io.github.guoshiqiufeng.dify.core.utils.StrUtil;
import io.github.guoshiqiufeng.dify.client.core.http.HttpHeaders;
import io.github.guoshiqiufeng.dify.client.core.web.client.HttpClient;
import io.github.guoshiqiufeng.dify.client.core.http.HttpClientFactory;
import io.github.guoshiqiufeng.dify.client.core.http.TypeReference;
import io.github.guoshiqiufeng.dify.support.impl.base.BaseDifyDefaultClient;
import io.github.guoshiqiufeng.dify.support.impl.dto.workflow.WorkflowRunStreamResponseDto;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.core.enums.ResponseModeEnum;
import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.core.pojo.request.ChatMessageVO;
import io.github.guoshiqiufeng.dify.workflow.client.DifyWorkflowClient;
import io.github.guoshiqiufeng.dify.workflow.constant.WorkflowConstant;
import io.github.guoshiqiufeng.dify.workflow.dto.request.WorkflowLogsRequest;
import io.github.guoshiqiufeng.dify.workflow.dto.request.WorkflowRunRequest;
import io.github.guoshiqiufeng.dify.workflow.dto.response.*;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author yanghq
 * @version 2.0.0
 * @since 2025/12/30 14:17
 */
public class DifyWorkflowDefaultClient extends BaseDifyDefaultClient implements DifyWorkflowClient {

    public DifyWorkflowDefaultClient(HttpClient httpClient) {
        super(httpClient);
    }

    public DifyWorkflowDefaultClient(String baseUrl, DifyProperties.ClientConfig clientConfig,
                                     HttpClientFactory httpClientFactory) {
        super(baseUrl, clientConfig, httpClientFactory);
    }


    @Override
    public WorkflowRunResponse runWorkflow(WorkflowRunRequest request) {
        ChatMessageVO chatMessage = builderChatMessage(ResponseModeEnum.blocking, request);
        return httpClient.post()
                .uri(WorkflowConstant.WORKFLOW_RUN_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + request.getApiKey())
                .body(chatMessage)
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(WorkflowRunResponse.class);
    }


    @Override
    public Flux<WorkflowRunStreamResponse> runWorkflowStream(WorkflowRunRequest request) {
        ChatMessageVO chatMessage = builderChatMessage(ResponseModeEnum.streaming, request);

        return httpClient.post()
                .uri(WorkflowConstant.WORKFLOW_RUN_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + request.getApiKey())
                .body(chatMessage)
                .retrieve()
                .onStatus(responseErrorHandler)
                .bodyToFlux(WorkflowRunStreamResponseDto.class)
                .mapNotNull(dto -> {
                    if (dto.getData() == null) {
                        return null;
                    }
                    return dto.getData();
                });
    }


    @Override
    public WorkflowInfoResponse info(String workflowRunId, String apiKey) {
        return httpClient.get()
                .uri(WorkflowConstant.WORKFLOW_RUN_URL + "/{workflowRunId}", workflowRunId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(WorkflowInfoResponse.class);
    }


    @Override
    public WorkflowStopResponse stopWorkflowStream(String apiKey, String taskId, String userId) {
        Map<String, String> body = new java.util.HashMap<>();
        body.put("user", userId);
        return httpClient.post()
                .uri(WorkflowConstant.WORKFLOW_TASKS_URL + "/{taskId}/stop", taskId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .body(body)
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(WorkflowStopResponse.class);
    }


    @Override
    public DifyPageResult<WorkflowLogs> logs(WorkflowLogsRequest request) {
        if (request.getPage() == null) {
            request.setPage(1);
        }
        if (request.getLimit() == null) {
            request.setLimit(20);
        }
        return httpClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(WorkflowConstant.WORKFLOW_LOGS_URL)
                        .queryParam("page", request.getPage())
                        .queryParam("limit", request.getLimit())
                        .queryParamIfPresent("status", Optional.ofNullable(request.getStatus()).filter(m -> !m.isEmpty()))
                        .queryParamIfPresent("keyword", Optional.ofNullable(request.getKeyword()).filter(m -> !m.isEmpty()))
                        .build()
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + request.getApiKey())
                .retrieve()
                .onStatus(responseErrorHandler)
                .body(new TypeReference<DifyPageResult<WorkflowLogs>>() {
                });  // 转为同步调用

    }

    private ChatMessageVO builderChatMessage(ResponseModeEnum responseMode, WorkflowRunRequest request) {
        ChatMessageVO chatMessage = new ChatMessageVO();
        chatMessage.setResponseMode(responseMode);
        chatMessage.setUser(request.getUserId());

        List<WorkflowRunRequest.WorkflowFile> files = request.getFiles();
        if (!CollUtil.isEmpty(files)) {
            files = files.stream().peek(f -> {
                if (StrUtil.isEmpty(f.getType())) {
                    f.setType("image");
                }
                if (StrUtil.isEmpty(f.getTransferMethod())) {
                    f.setTransferMethod("remote_url");
                }
            }).collect(Collectors.toList());
            chatMessage.setFiles(BeanUtils.copyToList(files, ChatMessageVO.ChatMessageFile.class));
        }
        HashMap<String, Object> inputs = new HashMap<>(1);
        chatMessage.setInputs(request.getInputs() == null ? inputs : request.getInputs());

        return chatMessage;
    }

}
