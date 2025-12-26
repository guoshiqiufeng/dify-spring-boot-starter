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
package io.github.guoshiqiufeng.dify.springboot4.autoconfigure;

import io.github.guoshiqiufeng.dify.chat.DifyChat;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.dataset.DifyDataset;
import io.github.guoshiqiufeng.dify.server.DifyServer;
import io.github.guoshiqiufeng.dify.springboot4.autoconfigure.actuator.DifyHealthIndicator;
import io.github.guoshiqiufeng.dify.status.cache.StatusCacheService;
import io.github.guoshiqiufeng.dify.status.cache.impl.InMemoryStatusCacheService;
import io.github.guoshiqiufeng.dify.status.checker.DifyChatStatusChecker;
import io.github.guoshiqiufeng.dify.status.checker.DifyDatasetStatusChecker;
import io.github.guoshiqiufeng.dify.status.checker.DifyServerStatusChecker;
import io.github.guoshiqiufeng.dify.status.checker.DifyWorkflowStatusChecker;
import io.github.guoshiqiufeng.dify.status.service.DifyStatusService;
import io.github.guoshiqiufeng.dify.status.service.impl.DifyStatusServiceImpl;
import io.github.guoshiqiufeng.dify.workflow.DifyWorkflow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Dify status auto-configuration
 *
 * @author yanghq
 * @version 1.7.0
 * @since 2025/12/19 14:00
 */
@Slf4j
@Configuration
@ConditionalOnClass(value = {DifyStatusService.class, DifyHealthIndicator.class})
public class DifyStatusAutoConfiguration {

    @Bean
    @ConditionalOnBean({DifyChat.class})
    @ConditionalOnMissingBean(DifyChatStatusChecker.class)
    public DifyChatStatusChecker difyChatStatusChecker(DifyChat difyChat) {
        return new DifyChatStatusChecker(difyChat);
    }

    @Bean
    @ConditionalOnBean({DifyDataset.class})
    @ConditionalOnMissingBean(DifyDatasetStatusChecker.class)
    public DifyDatasetStatusChecker difyDatasetStatusChecker(DifyDataset difyDataset) {
        return new DifyDatasetStatusChecker(difyDataset);
    }

    @Bean
    @ConditionalOnBean({DifyServer.class})
    @ConditionalOnMissingBean(DifyServerStatusChecker.class)
    public DifyServerStatusChecker difyServerStatusChecker(DifyServer difyServer) {
        return new DifyServerStatusChecker(difyServer);
    }

    @Bean
    @ConditionalOnBean({DifyWorkflow.class})
    @ConditionalOnMissingBean(DifyWorkflowStatusChecker.class)
    public DifyWorkflowStatusChecker difyWorkflowStatusChecker(DifyWorkflow difyWorkflow) {
        return new DifyWorkflowStatusChecker(difyWorkflow);
    }

    @Bean
    @ConditionalOnMissingBean(StatusCacheService.class)
    public StatusCacheService statusCacheService() {
        return new InMemoryStatusCacheService();
    }

    @Bean
    @ConditionalOnMissingBean(DifyStatusService.class)
    public DifyStatusService difyStatusService(
            ObjectProvider<DifyChatStatusChecker> chatChecker,
            ObjectProvider<DifyDatasetStatusChecker> datasetChecker,
            ObjectProvider<DifyServerStatusChecker> serverChecker,
            ObjectProvider<DifyWorkflowStatusChecker> workflowChecker) {
        return new DifyStatusServiceImpl(chatChecker.getIfAvailable(),
                datasetChecker.getIfAvailable(), serverChecker.getIfAvailable(), workflowChecker.getIfAvailable());
    }

    @Bean
    @ConditionalOnMissingBean(DifyHealthIndicator.class)
    @ConditionalOnProperty(name = "dify.status.health-indicator-enabled", havingValue = "true")
    @ConditionalOnClass(name = "org.springframework.boot.actuate.health.HealthIndicator")
    public DifyHealthIndicator difyHealthIndicator(
            DifyStatusService statusService,
            DifyProperties properties) {
        return new DifyHealthIndicator(statusService, properties.getStatus());
    }
}
