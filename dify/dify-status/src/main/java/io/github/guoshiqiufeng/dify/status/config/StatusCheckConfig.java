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
package io.github.guoshiqiufeng.dify.status.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * Status check configuration
 *
 * @author yanghq
 * @version 1.7.0
 * @since 2025/12/19 14:00
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusCheckConfig {
    /**
     * API key for authentication (default for all clients if specific keys not set)
     */
    private String apiKey;

    /**
     * Chat API key (for DifyChat client)
     */
    private List<String> chatApiKey;

    /**
     * Dataset API key (for DifyDataset client)
     */
    private String datasetApiKey;

    /**
     * Workflow API key (for DifyWorkflow client)
     */
    private List<String> workflowApiKey;

    /**
     * Clients to check (null = all)
     */
    private Set<String> clientsToCheck;

    /**
     * Specific methods to check per client
     */
    private Map<String, List<String>> methodsToCheck;

    /**
     * Timeout for each check in milliseconds
     */
    private Long timeoutMs;

    /**
     * Whether to run checks in parallel
     */
    @Builder.Default
    private Boolean parallel = true;

    /**
     * Whether to use cached results
     */
    @Builder.Default
    private Boolean useCache = true;

    /**
     * Cache TTL in seconds
     */
    @Builder.Default
    private Long cacheTtlSeconds = 60L;

    /**
     * Get the API key for chat client
     *
     * @return chat API key or default API key
     */
    public List<String> getChatApiKey() {
        return chatApiKey != null ? chatApiKey : new ArrayList<>(Collections.singleton(apiKey));
    }

    /**
     * Get the API key for dataset client
     *
     * @return dataset API key or default API key
     */
    public String getDatasetApiKey() {
        return datasetApiKey != null ? datasetApiKey : apiKey;
    }

    /**
     * Get the API key for workflow client
     *
     * @return workflow API key or default API key
     */
    public List<String> getWorkflowApiKey() {
        return workflowApiKey != null ? workflowApiKey : new ArrayList<>(Collections.singleton(apiKey));
    }
}
