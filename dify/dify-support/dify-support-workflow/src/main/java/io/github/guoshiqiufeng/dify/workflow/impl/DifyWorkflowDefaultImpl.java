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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.guoshiqiufeng.dify.core.config.DifyServerProperties;
import io.github.guoshiqiufeng.dify.core.enums.ResponseModeEnum;
import io.github.guoshiqiufeng.dify.core.pojo.DifyPageResult;
import io.github.guoshiqiufeng.dify.core.pojo.request.ChatMessageVO;
import io.github.guoshiqiufeng.dify.workflow.DifyWorkflow;
import io.github.guoshiqiufeng.dify.workflow.constant.WorkflowConstant;
import io.github.guoshiqiufeng.dify.workflow.dto.request.WorkflowLogsRequest;
import io.github.guoshiqiufeng.dify.workflow.dto.request.WorkflowRunRequest;
import io.github.guoshiqiufeng.dify.workflow.dto.response.WorkflowLogs;
import io.github.guoshiqiufeng.dify.workflow.dto.response.WorkflowRunResponse;
import io.github.guoshiqiufeng.dify.workflow.dto.response.WorkflowStopResponse;
import io.github.guoshiqiufeng.dify.workflow.exception.DiftWorkflowExceptionEnum;
import io.github.guoshiqiufeng.dify.workflow.exception.DifyWorkflowException;
import io.github.guoshiqiufeng.dify.workflow.utils.WebClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/11 14:51
 */
@Slf4j
public class DifyWorkflowDefaultImpl implements DifyWorkflow {

    private final DifyServerProperties difyServerProperties;
    private final ObjectMapper objectMapper;

    public DifyWorkflowDefaultImpl(DifyServerProperties difyServerProperties, ObjectMapper objectMapper) {
        this.difyServerProperties = difyServerProperties;
        this.objectMapper = objectMapper;
    }


    @Override
    public WorkflowRunResponse runWorkflow(WorkflowRunRequest request) {
        // 请求地址 url + /v1/chat-messages
        String url = difyServerProperties.getUrl() + WorkflowConstant.WORKFLOW_RUN_URL;

        // 请求体
        String body = builderRunBody(ResponseModeEnum.blocking, request);
        // 使用 WebClient 发送 POST 请求
        WebClient webClient = getWebClient(request.getApiKey());

        return webClient.post()
                .uri(url)
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(WorkflowRunResponse.class)
                .block();
    }

    @Override
    public Flux<WorkflowRunResponse> runWorkflowStream(WorkflowRunRequest request) {
        // 请求地址 url + /v1/chat-messages 请求方式 POST , stream 流
        String url = difyServerProperties.getUrl() + WorkflowConstant.WORKFLOW_RUN_URL;

        String body = builderRunBody(ResponseModeEnum.streaming, request);

        // 使用 WebClient 发送 POST 请求
        WebClient webClient = getWebClient(request.getApiKey());

        return webClient.post()
                .uri(url)
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, WebClientUtil::exceptionFunction)
                .bodyToFlux(WorkflowRunResponse.class)
                .doOnError(e -> log.error("Error while workflow runWorkflow stream: {}", e.getMessage()));
    }

    @Override
    public WorkflowRunResponse info(String workflowRunId, String apiKey) {
        String url = difyServerProperties.getUrl() + WorkflowConstant.WORKFLOW_RUN_URL + "/{}";
        url = StrUtil.format(url, workflowRunId);

        // 使用 WebClient 发送 GET 请求
        WebClient webClient = getWebClient(apiKey);
        return webClient.get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(new ParameterizedTypeReference<WorkflowRunResponse>() {
                })
                .block();  // 转为同步调用
    }

    @Override
    public WorkflowStopResponse stopWorkflowStream(String apiKey, String taskId, String userId) {
        String url = difyServerProperties.getUrl() + WorkflowConstant.WORKFLOW_TASKS_URL + "/{}/stop";
        String body = "";
        try {
            body = objectMapper.writeValueAsString(Map.of("user", userId));
        } catch (JsonProcessingException e) {
            throw new DifyWorkflowException(DiftWorkflowExceptionEnum.DIFY_DATA_PARSING_FAILURE);
        }

        // 使用 WebClient 发送 POST 请求
        WebClient webClient = getWebClient(apiKey);

        return webClient.post()
                .uri(url)
                .bodyValue(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, WebClientUtil::exceptionFunction)
                .bodyToMono(WorkflowStopResponse.class)
                .block();
    }

    @Override
    public DifyPageResult<WorkflowLogs> logs(WorkflowLogsRequest request) {
        String url = difyServerProperties.getUrl() + WorkflowConstant.WORKFLOW_LOGS_URL;

        if (request.getPage() == null) {
            request.setPage(1);
        }
        if (request.getLimit() == null) {
            request.setLimit(20);
        }

        // 使用 WebClient 发送 GET 请求
        WebClient webClient = getWebClient(request.getApiKey());

        return webClient.get()
                .uri(url + "?page={page}&limit={limit}&status={status}&keyword={keyword}",
                        request.getPage(),
                        request.getLimit(),
                        request.getStatus(),
                        request.getKeyword())
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
                f.setType("image");
                f.setTransferMethod("remote_url");
            }).toList();
            chatMessage.setFiles(BeanUtil.copyToList(files, ChatMessageVO.ChatMessageFile.class));
        }
        chatMessage.setInputs(request.getInputs());

        String body = null;
        try {
            body = objectMapper.writeValueAsString(chatMessage);
        } catch (JsonProcessingException e) {
            throw new DifyWorkflowException(DiftWorkflowExceptionEnum.DIFY_DATA_PARSING_FAILURE);
        }
        return body;
    }


    /**
     * 获取WebClient
     *
     * @param apiKey API密钥，用于身份验证
     * @return WebClient
     */
    private static WebClient getWebClient(String apiKey) {
        return WebClient.builder()
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }


}
