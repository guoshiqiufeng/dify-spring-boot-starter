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
 * Client status report
 *
 * @author yanghq
 * @version 1.7.0
 * @since 2025/12/19 14:00
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientStatusReport {
    /**
     * Client name (e.g., "DifyChat", "DifyDataset")
     */
    private String clientName;

    /**
     * Overall status of the client
     */
    private ApiStatus overallStatus;

    /**
     * List of individual API status results
     */
    private List<ApiStatusResult> apiStatuses;

    /**
     * Total number of APIs checked
     */
    private Integer totalApis;

    /**
     * Number of APIs with NORMAL status
     */
    private Integer normalApis;

    /**
     * Number of APIs with errors
     */
    private Integer errorApis;

    /**
     * Report generation time
     */
    private LocalDateTime reportTime;

    /**
     * Summary statistics by status
     */
    private Map<ApiStatus, Integer> statusSummary;
}
