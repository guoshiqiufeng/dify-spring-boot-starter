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

import io.github.guoshiqiufeng.dify.server.DifyServer;
import io.github.guoshiqiufeng.dify.server.dto.request.AppsRequest;
import io.github.guoshiqiufeng.dify.status.dto.ApiStatusResult;
import io.github.guoshiqiufeng.dify.status.dto.ClientStatusReport;
import io.github.guoshiqiufeng.dify.status.enums.ApiStatus;
import io.github.guoshiqiufeng.dify.status.strategy.AbstractStatusCheckStrategy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DifyServer status checker
 *
 * @author yanghq
 * @version 1.7.0
 * @since 2025/12/19 14:00
 */
@Getter
@Slf4j
public class DifyServerStatusChecker extends AbstractClientStatusChecker {

    private final DifyServer difyServer;

    public DifyServerStatusChecker(DifyServer difyServer) {
        this.difyServer = difyServer;
    }

    @Override
    public String getClientName() {
        return "DifyServer";
    }

    @Override
    public ApiStatusResult checkStatus(String methodName, String apiKey) {
        switch (methodName) {
            case "apps":
                return executeCheck(methodName, "/apps",
                        () -> {
                            AppsRequest request = new AppsRequest();
                            request.setLimit(1);
                            difyServer.apps(request);
                        });
            case "getDatasetApiKey":
                return executeCheck(methodName, "/datasets/api-keys",
                        difyServer::getDatasetApiKey);
            default:
                throw new IllegalArgumentException("Unknown method: " + methodName);
        }
    }

    @Override
    protected String[] methodsToCheck() {
        return new String[]{
                "apps",
                "getDatasetApiKey"
        };
    }

    /**
     * Check all DifyServer APIs
     *
     * @return ClientStatusReport
     */
    public ClientStatusReport checkAllApis() {
        return checkAllApisInternal(null);
    }
}
