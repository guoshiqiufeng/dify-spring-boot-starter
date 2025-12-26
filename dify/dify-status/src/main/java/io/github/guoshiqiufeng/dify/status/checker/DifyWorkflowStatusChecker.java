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
package io.github.guoshiqiufeng.dify.status.checker;

import io.github.guoshiqiufeng.dify.status.dto.ApiStatusResult;
import io.github.guoshiqiufeng.dify.status.dto.ClientStatusReport;
import io.github.guoshiqiufeng.dify.status.enums.ApiStatus;
import io.github.guoshiqiufeng.dify.status.strategy.AbstractStatusCheckStrategy;
import io.github.guoshiqiufeng.dify.workflow.DifyWorkflow;
import io.github.guoshiqiufeng.dify.workflow.dto.request.WorkflowLogsRequest;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DifyWorkflow status checker
 *
 * @author yanghq
 * @version 1.7.0
 * @since 2025/12/19 14:00
 */
@Slf4j
public class DifyWorkflowStatusChecker extends AbstractStatusCheckStrategy {

    private final DifyWorkflow difyWorkflow;

    public DifyWorkflowStatusChecker(DifyWorkflow difyWorkflow) {
        this.difyWorkflow = difyWorkflow;
    }

    @Override
    public String getClientName() {
        return "DifyWorkflow";
    }

    @Override
    public ApiStatusResult checkStatus(String methodName, String apiKey) {
        switch (methodName) {
            case "logs":
                return executeCheck(methodName, "/logs",
                        () -> {
                            WorkflowLogsRequest request = new WorkflowLogsRequest();
                            request.setApiKey(apiKey);
                            request.setLimit(1);
                            difyWorkflow.logs(request);
                        });
            default:
                throw new IllegalArgumentException("Unknown method: " + methodName);
        }
    }

    /**
     * Check all DifyWorkflow APIs
     *
     * @param apiKey API key for authentication
     * @return ClientStatusReport
     */
    public ClientStatusReport checkAllApis(String apiKey) {
        List<ApiStatusResult> results = new ArrayList<>();

        // Read-only methods that are safe to check
        String[] methodsToCheck = {
                "logs"
        };

        for (String method : methodsToCheck) {
            try {
                results.add(checkStatus(method, apiKey));
            } catch (Exception e) {
                log.error("Error checking method {}: {}", method, e.getMessage());
                // Add error result
                results.add(ApiStatusResult.builder()
                        .methodName(method)
                        .status(ApiStatus.UNKNOWN_ERROR)
                        .errorMessage(e.getMessage())
                        .checkTime(LocalDateTime.now())
                        .build());
            }
        }

        return buildClientReport(results);
    }

    /**
     * Build client status report from results
     *
     * @param results List of API status results
     * @return ClientStatusReport
     */
    private ClientStatusReport buildClientReport(List<ApiStatusResult> results) {
        int totalApis = results.size();
        int normalApis = 0;
        int errorApis = 0;
        Map<ApiStatus, Integer> statusSummary = new HashMap<>();

        for (ApiStatusResult result : results) {
            ApiStatus status = result.getStatus();
            statusSummary.put(status, statusSummary.getOrDefault(status, 0) + 1);

            if (status == ApiStatus.NORMAL) {
                normalApis++;
            } else {
                errorApis++;
            }
        }

        // Determine overall status
        ApiStatus overallStatus;
        if (normalApis == totalApis) {
            overallStatus = ApiStatus.NORMAL;
        } else if (normalApis == 0) {
            overallStatus = ApiStatus.SERVER_ERROR;
        } else {
            // Partially working
            overallStatus = ApiStatus.CLIENT_ERROR;
        }

        return ClientStatusReport.builder()
                .clientName(getClientName())
                .overallStatus(overallStatus)
                .apiStatuses(results)
                .totalApis(totalApis)
                .normalApis(normalApis)
                .errorApis(errorApis)
                .reportTime(LocalDateTime.now())
                .statusSummary(statusSummary)
                .build();
    }
}
