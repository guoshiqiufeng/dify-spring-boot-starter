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
package io.github.guoshiqiufeng.dify.status.service;

import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.status.config.StatusCheckConfig;
import io.github.guoshiqiufeng.dify.status.dto.AggregatedStatusReport;
import io.github.guoshiqiufeng.dify.status.dto.ClientStatusReport;

/**
 * Dify status service interface
 *
 * @author yanghq
 * @version 1.7.0
 * @since 2025/12/19 14:00
 */
public interface DifyStatusService {
    /**
     * Check status of a specific client
     *
     * @param clientName Client name (DifyChat, DifyDataset, DifyServer, DifyWorkflow)
     * @param apiKey     API key for authentication
     * @return ClientStatusReport
     */
    ClientStatusReport checkClientStatus(String clientName, String apiKey);

    /**
     * Check status of all clients
     *
     * @param statusConfig statusConfig
     * @return AggregatedStatusReport
     */
    AggregatedStatusReport checkAllClientsStatus(DifyProperties.StatusConfig statusConfig);

    /**
     * Check status of all clients by server account
     *
     * @return AggregatedStatusReport
     */
    AggregatedStatusReport checkAllClientsStatusByServer();

    /**
     * Check status with custom configuration
     *
     * @param config Status check configuration
     * @return AggregatedStatusReport
     */
    AggregatedStatusReport checkStatus(StatusCheckConfig config);
}
