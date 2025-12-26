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
package io.github.guoshiqiufeng.dify.status.service.impl;

import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.server.dto.request.AppsRequest;
import io.github.guoshiqiufeng.dify.server.dto.response.ApiKeyResponse;
import io.github.guoshiqiufeng.dify.server.dto.response.AppsResponse;
import io.github.guoshiqiufeng.dify.server.dto.response.AppsResponseResult;
import io.github.guoshiqiufeng.dify.server.dto.response.DatasetApiKeyResponse;
import io.github.guoshiqiufeng.dify.status.checker.DifyChatStatusChecker;
import io.github.guoshiqiufeng.dify.status.checker.DifyDatasetStatusChecker;
import io.github.guoshiqiufeng.dify.status.checker.DifyServerStatusChecker;
import io.github.guoshiqiufeng.dify.status.checker.DifyWorkflowStatusChecker;
import io.github.guoshiqiufeng.dify.status.config.StatusCheckConfig;
import io.github.guoshiqiufeng.dify.status.dto.AggregatedStatusReport;
import io.github.guoshiqiufeng.dify.status.dto.ClientStatusReport;
import io.github.guoshiqiufeng.dify.status.enums.ApiStatus;
import io.github.guoshiqiufeng.dify.status.service.DifyStatusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Dify status service implementation
 *
 * @author yanghq
 * @version 1.7.0
 * @since 2025/12/19 14:00
 */
@Slf4j
public class DifyStatusServiceImpl implements DifyStatusService {

    private final DifyChatStatusChecker chatChecker;
    private final DifyDatasetStatusChecker datasetChecker;
    private final DifyServerStatusChecker serverChecker;
    private final DifyWorkflowStatusChecker workflowChecker;
    private final ExecutorService executorService;

    public DifyStatusServiceImpl(
            DifyChatStatusChecker chatChecker,
            DifyDatasetStatusChecker datasetChecker,
            DifyServerStatusChecker serverChecker,
            DifyWorkflowStatusChecker workflowChecker) {
        this(chatChecker, datasetChecker, serverChecker, workflowChecker, null);
    }

    public DifyStatusServiceImpl(
            DifyChatStatusChecker chatChecker,
            DifyDatasetStatusChecker datasetChecker,
            DifyServerStatusChecker serverChecker,
            DifyWorkflowStatusChecker workflowChecker,
            ExecutorService executorService) {
        this.chatChecker = chatChecker;
        this.datasetChecker = datasetChecker;
        this.serverChecker = serverChecker;
        this.workflowChecker = workflowChecker;
        this.executorService = executorService != null ? executorService : Executors.newFixedThreadPool(4);
    }

    @Override
    public ClientStatusReport checkClientStatus(String clientName, String apiKey) {
        switch (clientName.toLowerCase()) {
            case "difychat":
                return chatChecker.checkAllApis(apiKey);
            case "difydataset":
                return datasetChecker.checkAllApis(apiKey);
            case "difyserver":
                return serverChecker.checkAllApis();
            case "difyworkflow":
                return workflowChecker.checkAllApis(apiKey);
            default:
                throw new IllegalArgumentException("Unknown client: " + clientName);
        }
    }

    @Override
    public AggregatedStatusReport checkAllClientsStatus(DifyProperties.StatusConfig statusConfig) {
        StatusCheckConfig config = StatusCheckConfig.builder()
                .apiKey(statusConfig.getApiKey())
                .datasetApiKey(statusConfig.getDatasetApiKey())
                .chatApiKey(statusConfig.getChatApiKey())
                .workflowApiKey(statusConfig.getWorkflowApiKey())
                .parallel(true)
                .build();
        return checkStatus(config);
    }

    @Override
    public AggregatedStatusReport checkAllClientsStatusByServer() {
        AppsRequest request = new AppsRequest();
        request.setLimit(1);
        AppsResponseResult apps = serverChecker.getDifyServer().apps(request);
        List<AppsResponse> data = apps.getData();

        List<String> chatApiKey = new ArrayList<>();
        List<String> workflowApiKey = new ArrayList<>();
        data.forEach(appsResponse -> {
            String appId = appsResponse.getId();
            List<ApiKeyResponse> appApiKeys = serverChecker.getDifyServer().getAppApiKey(appId);
            if (ObjectUtils.isEmpty(appApiKeys)) {
                appApiKeys = serverChecker.getDifyServer().initAppApiKey(appId);
            }
            if ("workflow".equals(appsResponse.getMode())) {
                workflowApiKey.add(appApiKeys.get(0).getToken());
            } else {
                chatApiKey.add(appApiKeys.get(0).getToken());
            }
        });
        List<DatasetApiKeyResponse> datasetApiKeys = serverChecker.getDifyServer().getDatasetApiKey();
        if (ObjectUtils.isEmpty(datasetApiKeys)) {
            datasetApiKeys = serverChecker.getDifyServer().initDatasetApiKey();
        }
        StatusCheckConfig config = StatusCheckConfig.builder()
                .datasetApiKey(datasetApiKeys.get(0).getToken())
                .chatApiKey(chatApiKey)
                .workflowApiKey(workflowApiKey)
                .parallel(true)
                .build();
        return checkStatus(config);
    }

    @Override
    public AggregatedStatusReport checkStatus(StatusCheckConfig config) {
        List<ClientStatusReport> clientReports = new ArrayList<>();

        if (config.getParallel() != null && config.getParallel()) {
            // Parallel execution
            List<CompletableFuture<ClientStatusReport>> futures = new ArrayList<>();

            if (chatChecker != null) {
                for (String apikey : config.getChatApiKey()) {
                    futures.add(CompletableFuture.supplyAsync(
                            () -> chatChecker.checkAllApis(apikey), executorService));
                }
            }
            if (datasetChecker != null) {
                futures.add(CompletableFuture.supplyAsync(
                        () -> datasetChecker.checkAllApis(config.getDatasetApiKey()), executorService));
            }
            if (serverChecker != null) {
                futures.add(CompletableFuture.supplyAsync(
                        serverChecker::checkAllApis, executorService));
            }
            if (workflowChecker != null) {
                for (String apikey : config.getWorkflowApiKey()) {
                    futures.add(CompletableFuture.supplyAsync(
                            () -> workflowChecker.checkAllApis(apikey), executorService));
                }
            }
            clientReports = futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());
        } else {
            // Sequential execution
            if (chatChecker != null) {
                for (String apikey : config.getChatApiKey()) {
                    clientReports.add(chatChecker.checkAllApis(apikey));
                }
            }
            if (datasetChecker != null) {
                clientReports.add(datasetChecker.checkAllApis(config.getDatasetApiKey()));
            }
            if (serverChecker != null) {
                clientReports.add(serverChecker.checkAllApis());
            }
            if (workflowChecker != null) {
                for (String apikey : config.getWorkflowApiKey()) {
                    clientReports.add(workflowChecker.checkAllApis(apikey));
                }
            }
        }

        return aggregateReports(clientReports);
    }

    /**
     * Aggregate client reports into overall status report
     *
     * @param clientReports List of client status reports
     * @return AggregatedStatusReport
     */
    private AggregatedStatusReport aggregateReports(List<ClientStatusReport> clientReports) {
        int totalApis = clientReports.stream()
                .mapToInt(ClientStatusReport::getTotalApis)
                .sum();

        int healthyApis = clientReports.stream()
                .mapToInt(ClientStatusReport::getNormalApis)
                .sum();

        ApiStatus overallStatus = determineOverallStatus(clientReports);

        Map<String, ApiStatus> clientSummary = clientReports.stream()
                .collect(Collectors.toMap(
                        ClientStatusReport::getClientName,
                        ClientStatusReport::getOverallStatus
                ));

        return AggregatedStatusReport.builder()
                .overallStatus(overallStatus)
                .clientReports(clientReports)
                .totalApis(totalApis)
                .healthyApis(healthyApis)
                .unhealthyApis(totalApis - healthyApis)
                .reportTime(LocalDateTime.now())
                .clientSummary(clientSummary)
                .build();
    }

    /**
     * Determine overall status based on client reports
     *
     * @param reports List of client status reports
     * @return ApiStatus
     */
    private ApiStatus determineOverallStatus(List<ClientStatusReport> reports) {
        long normalClients = reports.stream()
                .filter(r -> r.getOverallStatus() == ApiStatus.NORMAL)
                .count();

        if (normalClients == reports.size()) {
            // All clients are normal
            return ApiStatus.NORMAL;
        } else if (normalClients == 0) {
            // All clients have errors
            return ApiStatus.SERVER_ERROR;
        } else {
            // Some clients are working, some are not
            return ApiStatus.CLIENT_ERROR;
        }
    }

    /**
     * Shutdown the executor service
     */
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
