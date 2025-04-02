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
package io.github.guoshiqiufeng.dify.workflow.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.guoshiqiufeng.dify.core.enums.ResponseModeEnum;
import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.core.pojo.request.ChatMessageVO;
import io.github.guoshiqiufeng.dify.workflow.DifyWorkflow;
import io.github.guoshiqiufeng.dify.workflow.constant.WorkflowConstant;
import io.github.guoshiqiufeng.dify.workflow.dto.request.WorkflowLogsRequest;
import io.github.guoshiqiufeng.dify.workflow.dto.request.WorkflowRunRequest;
import io.github.guoshiqiufeng.dify.workflow.dto.response.*;
import io.github.guoshiqiufeng.dify.workflow.exception.DifyWorkflowException;
import io.github.guoshiqiufeng.dify.workflow.exception.DifyWorkflowExceptionEnum;
import io.github.guoshiqiufeng.dify.workflow.utils.WebClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/11 14:51
 */
@Slf4j
public class DifyWorkflowDefaultImpl implements DifyWorkflow {

    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    public DifyWorkflowDefaultImpl(ObjectMapper objectMapper, WebClient webClient) {
        this.objectMapper = objectMapper;
        this.webClient = webClient;
    }


    @Override
    public WorkflowRunResponse runWorkflow(WorkflowRunRequest request) {
        // 请求地址 url + /v1/chat-messages
        String url = WorkflowConstant.WORKFLOW_RUN_URL;

        // 请求体
        String body = builderRunBody(ResponseModeEnum.blocking, request);
        // 使用 WebClient 发送 POST 请求

        return webClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + request.getApiKey())
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(WorkflowRunResponse.class)
                .block();
    }

    @Override
    public Flux<WorkflowRunStreamResponse> runWorkflowStream(WorkflowRunRequest request) {
        // 请求地址 url + /v1/chat-messages 请求方式 POST , stream 流
        String url = WorkflowConstant.WORKFLOW_RUN_URL;

        String body = builderRunBody(ResponseModeEnum.streaming, request);

        // 使用 WebClient 发送 POST 请求

        return webClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + request.getApiKey())
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, WebClientUtil::exceptionFunction)
                .bodyToFlux(WorkflowRunStreamResponse.class)
                .doOnError(e -> log.error("Error while workflow runWorkflow stream: {}", e.getMessage()));
    }

    @Override
    public WorkflowInfoResponse info(String workflowRunId, String apiKey) {
        String url = WorkflowConstant.WORKFLOW_RUN_URL + "/{workflowRunId}";

        // 使用 WebClient 发送 GET 请求

        return webClient.get()
                .uri(url, workflowRunId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .retrieve()
                .onStatus(HttpStatusCode::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(String.class)
                // .doOnNext(System.out::println)
                .flatMap(responseBody -> {
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        WorkflowInfoResponse workflowInfoResponse = objectMapper.readValue(responseBody, WorkflowInfoResponse.class);
                        return Mono.just(workflowInfoResponse);
                    } catch (Exception e) {
                        return Mono.error(new RuntimeException("Failed to deserialize response", e));
                    }
                })
                .block();  // 转为同步调用
    }

    @Override
    public WorkflowStopResponse stopWorkflowStream(String apiKey, String taskId, String userId) {
        String url = WorkflowConstant.WORKFLOW_TASKS_URL + "/{taskId}/stop";
        String body = "";
        try {
            body = objectMapper.writeValueAsString(Map.of("user", userId));
        } catch (JsonProcessingException e) {
            throw new DifyWorkflowException(DifyWorkflowExceptionEnum.DIFY_DATA_PARSING_FAILURE);
        }

        // 使用 WebClient 发送 POST 请求

        return webClient.post()
                .uri(url, taskId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(WorkflowStopResponse.class)
                .block();
    }

    @Override
    public DifyPageResult<WorkflowLogs> logs(WorkflowLogsRequest request) {
        String url = WorkflowConstant.WORKFLOW_LOGS_URL;

        if (request.getPage() == null) {
            request.setPage(1);
        }
        if (request.getLimit() == null) {
            request.setLimit(20);
        }

        // 使用 WebClient 发送 GET 请求

        String uri = url + "?page={page}&limit={limit}";
        if (StrUtil.isNotEmpty(request.getStatus())) {
            uri += "&status={}";
            uri = StrUtil.format(uri, request.getStatus());
        }
        if (StrUtil.isNotEmpty(request.getKeyword())) {
            uri += "&keyword={}";
            uri = StrUtil.format(uri, request.getKeyword());
        }
        return webClient.get()
                .uri(uri, request.getPage(), request.getLimit())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + request.getApiKey())
                .retrieve()
                .onStatus(HttpStatusCode::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(new ParameterizedTypeReference<DifyPageResult<WorkflowLogs>>() {
                })
                .block();  // 转为同步调用

    }

    private String builderRunBody(ResponseModeEnum responseMode, WorkflowRunRequest request) {
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
            }).toList();
            chatMessage.setFiles(BeanUtil.copyToList(files, ChatMessageVO.ChatMessageFile.class));
        }
        chatMessage.setInputs(request.getInputs() == null ? Map.of() : request.getInputs());

        String body = null;
        try {
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            body = objectMapper.writeValueAsString(chatMessage);
        } catch (JsonProcessingException e) {
            throw new DifyWorkflowException(DifyWorkflowExceptionEnum.DIFY_DATA_PARSING_FAILURE);
        }
        return body;
    }


}
