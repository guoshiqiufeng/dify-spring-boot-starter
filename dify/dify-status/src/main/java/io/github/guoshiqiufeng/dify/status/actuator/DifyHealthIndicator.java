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
package io.github.guoshiqiufeng.dify.status.actuator;

import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.status.dto.AggregatedStatusReport;
import io.github.guoshiqiufeng.dify.status.enums.ApiStatus;
import io.github.guoshiqiufeng.dify.status.service.DifyStatusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

/**
 * Dify health indicator for Spring Boot Actuator
 *
 * @author yanghq
 * @version 1.7.0
 * @since 2025/12/19 14:00
 */
@Slf4j
public class DifyHealthIndicator implements HealthIndicator {

    private final DifyStatusService statusService;
    private final DifyProperties.StatusConfig statusConfig;

    public DifyHealthIndicator(DifyStatusService statusService, DifyProperties.StatusConfig statusConfig) {
        this.statusService = statusService;
        this.statusConfig = statusConfig;
    }

    @Override
    public Health health() {
        try {
            AggregatedStatusReport report;
            if(statusConfig.getHealthIndicatorInitByServer()) {
                report = statusService.checkAllClientsStatusByServer();
            } else {
                report = statusService.checkAllClientsStatus(statusConfig);
            }

            if (report.getOverallStatus() == ApiStatus.NORMAL) {
                return Health.up()
                        .withDetail("totalApis", report.getTotalApis())
                        .withDetail("healthyApis", report.getHealthyApis())
                        .withDetail("unhealthyApis", report.getUnhealthyApis())
                        .withDetail("clientSummary", report.getClientSummary())
                        .withDetail("reportTime", report.getReportTime())
                        .withDetail("clientReports", report.getClientReports())
                        .build();
            } else {
                Health.Builder healthBuilder;
                if(report.getHealthyApis() > 0) {
                    healthBuilder = Health.up();
                } else {
                    healthBuilder = Health.down();
                }
                return healthBuilder
                        .withDetail("overallStatus", report.getOverallStatus())
                        .withDetail("totalApis", report.getTotalApis())
                        .withDetail("healthyApis", report.getHealthyApis())
                        .withDetail("unhealthyApis", report.getUnhealthyApis())
                        .withDetail("clientSummary", report.getClientSummary())
                        .withDetail("reportTime", report.getReportTime())
                        .withDetail("clientReports", report.getClientReports())
                        .build();
            }
        } catch (Exception e) {
            log.error("Error checking Dify health", e);
            return Health.down()
                    .withException(e)
                    .build();
        }
    }
}
