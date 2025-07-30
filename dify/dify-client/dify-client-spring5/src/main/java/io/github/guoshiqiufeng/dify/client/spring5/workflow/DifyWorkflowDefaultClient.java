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
package io.github.guoshiqiufeng.dify.client.spring5.workflow;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import io.github.guoshiqiufeng.dify.client.spring5.base.BaseDifyDefaultClient;
import io.github.guoshiqiufeng.dify.client.spring5.utils.WebClientUtil;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.core.enums.ResponseModeEnum;
import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.core.pojo.request.ChatMessageVO;
import io.github.guoshiqiufeng.dify.workflow.client.DifyWorkflowClient;
import io.github.guoshiqiufeng.dify.workflow.constant.WorkflowConstant;
import io.github.guoshiqiufeng.dify.workflow.dto.request.WorkflowLogsRequest;
import io.github.guoshiqiufeng.dify.workflow.dto.request.WorkflowRunRequest;
import io.github.guoshiqiufeng.dify.workflow.dto.response.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/8 16:26
 */
@Slf4j
public class DifyWorkflowDefaultClient extends BaseDifyDefaultClient implements DifyWorkflowClient {

    public DifyWorkflowDefaultClient() {
        super();
    }

    public DifyWorkflowDefaultClient(String baseUrl) {
        super(baseUrl);
    }

    public DifyWorkflowDefaultClient(String baseUrl, DifyProperties.ClientConfig clientConfig, WebClient.Builder webClientBuilder) {
        super(baseUrl, clientConfig, webClientBuilder);
    }


    @Override
    public WorkflowRunResponse runWorkflow(WorkflowRunRequest request) {
        ChatMessageVO chatMessage = builderChatMessage(ResponseModeEnum.blocking, request);
        return webClient.post()
                .uri(WorkflowConstant.WORKFLOW_RUN_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + request.getApiKey())
                .bodyValue(chatMessage)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(WorkflowRunResponse.class)
                .block();
    }


    @Override
    public Flux<WorkflowRunStreamResponse> runWorkflowStream(WorkflowRunRequest request) {
        ChatMessageVO chatMessage = builderChatMessage(ResponseModeEnum.streaming, request);

        return webClient.post()
                .uri(WorkflowConstant.WORKFLOW_RUN_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + request.getApiKey())
                .bodyValue(chatMessage)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToFlux(WorkflowRunStreamResponse.class);
    }


    @Override
    public WorkflowInfoResponse info(String workflowRunId, String apiKey) {
        return webClient.get()
                .uri(WorkflowConstant.WORKFLOW_RUN_URL + "/{workflowRunId}", workflowRunId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(WorkflowInfoResponse.class)
                .block();
    }


    @Override
    public WorkflowStopResponse stopWorkflowStream(String apiKey, String taskId, String userId) {
        HashMap<String, Object> hashMap = new HashMap<>(1);
        hashMap.put("user", userId);
        return webClient.post()
                .uri(WorkflowConstant.WORKFLOW_TASKS_URL + "/{taskId}/stop", taskId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .bodyValue(hashMap)
                .retrieve()
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(WorkflowStopResponse.class)
                .block();
    }


    @Override
    public DifyPageResult<WorkflowLogs> logs(WorkflowLogsRequest request) {
        if (request.getPage() == null) {
            request.setPage(1);
        }
        if (request.getLimit() == null) {
            request.setLimit(20);
        }
        return webClient.get()
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
                .onStatus(HttpStatus::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(new ParameterizedTypeReference<DifyPageResult<WorkflowLogs>>() {
                })
                .block();
    }

    private ChatMessageVO builderChatMessage(ResponseModeEnum responseMode, WorkflowRunRequest request) {
        ChatMessageVO chatMessage = new ChatMessageVO();
        chatMessage.setResponseMode(responseMode);
        chatMessage.setUser(request.getUserId());

        List<WorkflowRunRequest.WorkflowFile> files = request.getFiles();
        if (!CollectionUtils.isEmpty(files)) {
            files = files.stream().peek(f -> {
                if (StrUtil.isEmpty(f.getType())) {
                    f.setType("image");
                }
                if (StrUtil.isEmpty(f.getTransferMethod())) {
                    f.setTransferMethod("remote_url");
                }
            }).collect(Collectors.toList());
            chatMessage.setFiles(BeanUtil.copyToList(files, ChatMessageVO.ChatMessageFile.class));
        }
        chatMessage.setInputs(request.getInputs() == null ? new HashMap<>(0) : request.getInputs());

        return chatMessage;
    }

}
