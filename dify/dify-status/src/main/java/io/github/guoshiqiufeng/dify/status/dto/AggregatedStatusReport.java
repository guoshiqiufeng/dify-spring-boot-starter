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
package io.github.guoshiqiufeng.dify.status.dto;

import io.github.guoshiqiufeng.dify.status.enums.ApiStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Aggregated status report
 *
 * @author yanghq
 * @version 1.7.0
 * @since 2025/12/19 14:00
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AggregatedStatusReport {
    /**
     * Overall system status
     */
    private ApiStatus overallStatus;

    /**
     * Status reports for each client
     */
    private List<ClientStatusReport> clientReports;

    /**
     * Total number of APIs across all clients
     */
    private Integer totalApis;

    /**
     * Number of healthy APIs
     */
    private Integer healthyApis;

    /**
     * Number of unhealthy APIs
     */
    private Integer unhealthyApis;

    /**
     * Report generation time
     */
    private LocalDateTime reportTime;

    /**
     * Summary by client
     */
    private Map<String, ApiStatus> clientSummary;
}
