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

import io.github.guoshiqiufeng.dify.dataset.DifyDataset;
import io.github.guoshiqiufeng.dify.dataset.dto.request.DatasetPageRequest;
import io.github.guoshiqiufeng.dify.status.dto.ApiStatusResult;
import io.github.guoshiqiufeng.dify.status.dto.ClientStatusReport;
import io.github.guoshiqiufeng.dify.status.enums.ApiStatus;
import io.github.guoshiqiufeng.dify.status.strategy.AbstractStatusCheckStrategy;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DifyDataset status checker
 *
 * @author yanghq
 * @version 1.7.0
 * @since 2025/12/19 14:00
 */
@Slf4j
public class DifyDatasetStatusChecker extends AbstractClientStatusChecker {

    private final DifyDataset difyDataset;

    public DifyDatasetStatusChecker(DifyDataset difyDataset) {
        this.difyDataset = difyDataset;
    }

    @Override
    public String getClientName() {
        return "DifyDataset";
    }

    @Override
    public ApiStatusResult checkStatus(String methodName, String apiKey) {
        switch (methodName) {
            case "page":
                return executeCheck(methodName, "/datasets",
                        () -> {
                            DatasetPageRequest request = new DatasetPageRequest();
                            request.setApiKey(apiKey);
                            request.setLimit(1);
                            difyDataset.page(request);
                        });
            case "listTextEmbedding":
                return executeCheck(methodName, "/text-embeddings",
                        () -> difyDataset.listTextEmbedding(apiKey));
            case "listRerank":
                return executeCheck(methodName, "/rerank",
                        () -> difyDataset.listRerank(apiKey));
            case "listTag":
                return executeCheck(methodName, "/tags",
                        () -> difyDataset.listTag(apiKey));
            default:
                throw new IllegalArgumentException("Unknown method: " + methodName);
        }
    }

    @Override
    protected String[] methodsToCheck() {
        return new String[]{
                "page",
                "listTextEmbedding",
                "listRerank",
                "listTag"
        };
    }

    /**
     * Check all DifyDataset APIs
     *
     * @param apiKey API key for authentication
     * @return ClientStatusReport
     */
    public ClientStatusReport checkAllApis(String apiKey) {
        return checkAllApisInternal(apiKey);
    }
}
