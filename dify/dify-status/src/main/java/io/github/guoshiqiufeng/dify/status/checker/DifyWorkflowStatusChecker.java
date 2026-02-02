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
public class DifyWorkflowStatusChecker extends AbstractClientStatusChecker {

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

    @Override
    protected String[] methodsToCheck() {
        return new String[]{"logs"};
    }

    /**
     * Check all DifyWorkflow APIs
     *
     * @param apiKey API key for authentication
     * @return ClientStatusReport
     */
    public ClientStatusReport checkAllApis(String apiKey) {
        return checkAllApisInternal(apiKey);
    }
}
