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
package io.github.guoshiqiufeng.dify.springboot2.autoconfigure;

import io.github.guoshiqiufeng.dify.chat.DifyChat;
import io.github.guoshiqiufeng.dify.dataset.DifyDataset;
import io.github.guoshiqiufeng.dify.server.DifyServer;
import io.github.guoshiqiufeng.dify.springboot.common.autoconfigure.DifyCodecAutoConfiguration;
import io.github.guoshiqiufeng.dify.springboot.common.autoconfigure.DifyPropertiesAutoConfiguration;
import io.github.guoshiqiufeng.dify.springboot.common.autoconfigure.DifyStatusAutoConfiguration;
import io.github.guoshiqiufeng.dify.status.cache.StatusCacheService;
import io.github.guoshiqiufeng.dify.status.cache.impl.InMemoryStatusCacheService;
import io.github.guoshiqiufeng.dify.status.checker.DifyChatStatusChecker;
import io.github.guoshiqiufeng.dify.status.checker.DifyDatasetStatusChecker;
import io.github.guoshiqiufeng.dify.status.checker.DifyServerStatusChecker;
import io.github.guoshiqiufeng.dify.status.checker.DifyWorkflowStatusChecker;
import io.github.guoshiqiufeng.dify.status.service.DifyStatusService;
import io.github.guoshiqiufeng.dify.status.service.impl.DifyStatusServiceImpl;
import io.github.guoshiqiufeng.dify.workflow.DifyWorkflow;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link DifyStatusAutoConfiguration}.
 *
 * @author yanghq
 * @version 1.7.0
 * @since 2025/12/19 14:00
 */
public class DifyStatusAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    DifyPropertiesAutoConfiguration.class,
                    DifyCodecAutoConfiguration.class,
                    DifyStatusAutoConfiguration.class));

    @Test
    void statusCacheServiceIsCreatedByDefault() {
        this.contextRunner
                .run((context) -> {
                    assertThat(context).hasSingleBean(StatusCacheService.class);
                    assertThat(context.getBean(StatusCacheService.class))
                            .isInstanceOf(InMemoryStatusCacheService.class);
                });
    }

    @Test
    void statusServiceIsCreatedByDefault() {
        this.contextRunner
                .run((context) -> {
                    assertThat(context).hasSingleBean(DifyStatusService.class);
                    assertThat(context.getBean(DifyStatusService.class))
                            .isInstanceOf(DifyStatusServiceImpl.class);
                });
    }

    @Test
    void chatStatusCheckerIsCreatedWhenDifyChatBeanExists() {
        this.contextRunner
                .withUserConfiguration(DifyChatConfiguration.class)
                .run((context) -> {
                    assertThat(context).hasSingleBean(DifyChatStatusChecker.class);
                });
    }

    @Test
    void chatStatusCheckerIsNotCreatedWhenDifyChatBeanIsMissing() {
        this.contextRunner
                .run((context) -> {
                    assertThat(context).doesNotHaveBean(DifyChatStatusChecker.class);
                });
    }

    @Test
    void datasetStatusCheckerIsCreatedWhenDifyDatasetBeanExists() {
        this.contextRunner
                .withUserConfiguration(DifyDatasetConfiguration.class)
                .run((context) -> {
                    assertThat(context).hasSingleBean(DifyDatasetStatusChecker.class);
                });
    }

    @Test
    void datasetStatusCheckerIsNotCreatedWhenDifyDatasetBeanIsMissing() {
        this.contextRunner
                .run((context) -> {
                    assertThat(context).doesNotHaveBean(DifyDatasetStatusChecker.class);
                });
    }

    @Test
    void serverStatusCheckerIsCreatedWhenDifyServerBeanExists() {
        this.contextRunner
                .withUserConfiguration(DifyServerConfiguration.class)
                .run((context) -> {
                    assertThat(context).hasSingleBean(DifyServerStatusChecker.class);
                });
    }

    @Test
    void serverStatusCheckerIsNotCreatedWhenDifyServerBeanIsMissing() {
        this.contextRunner
                .run((context) -> {
                    assertThat(context).doesNotHaveBean(DifyServerStatusChecker.class);
                });
    }

    @Test
    void workflowStatusCheckerIsCreatedWhenDifyWorkflowBeanExists() {
        this.contextRunner
                .withUserConfiguration(DifyWorkflowConfiguration.class)
                .run((context) -> {
                    assertThat(context).hasSingleBean(DifyWorkflowStatusChecker.class);
                });
    }

    @Test
    void workflowStatusCheckerIsNotCreatedWhenDifyWorkflowBeanIsMissing() {
        this.contextRunner
                .run((context) -> {
                    assertThat(context).doesNotHaveBean(DifyWorkflowStatusChecker.class);
                });
    }

    @Test
    void customStatusCacheServiceIsRespected() {
        this.contextRunner
                .withUserConfiguration(CustomStatusCacheServiceConfiguration.class)
                .run((context) -> {
                    assertThat(context).hasSingleBean(StatusCacheService.class);
                    assertThat(context.getBean(StatusCacheService.class))
                            .isNotInstanceOf(InMemoryStatusCacheService.class);
                });
    }

    @Test
    void customStatusServiceIsRespected() {
        this.contextRunner
                .withUserConfiguration(CustomStatusServiceConfiguration.class)
                .run((context) -> {
                    assertThat(context).hasSingleBean(DifyStatusService.class);
                    assertThat(context.getBean(DifyStatusService.class))
                            .isNotInstanceOf(DifyStatusServiceImpl.class);
                });
    }

    @Test
    void customChatStatusCheckerIsRespected() {
        this.contextRunner
                .withUserConfiguration(DifyChatConfiguration.class, CustomChatStatusCheckerConfiguration.class)
                .run((context) -> {
                    assertThat(context).hasSingleBean(DifyChatStatusChecker.class);
                });
    }

    @Configuration
    static class DifyChatConfiguration {
        @Bean
        DifyChat difyChat() {
            return mock(DifyChat.class);
        }
    }

    @Configuration
    static class DifyDatasetConfiguration {
        @Bean
        DifyDataset difyDataset() {
            return mock(DifyDataset.class);
        }
    }

    @Configuration
    static class DifyServerConfiguration {
        @Bean
        DifyServer difyServer() {
            return mock(DifyServer.class);
        }
    }

    @Configuration
    static class DifyWorkflowConfiguration {
        @Bean
        DifyWorkflow difyWorkflow() {
            return mock(DifyWorkflow.class);
        }
    }

    @Configuration
    static class CustomStatusCacheServiceConfiguration {
        @Bean
        StatusCacheService customStatusCacheService() {
            return mock(StatusCacheService.class);
        }
    }

    @Configuration
    static class CustomStatusServiceConfiguration {
        @Bean
        DifyStatusService customStatusService() {
            return mock(DifyStatusService.class);
        }
    }

    @Configuration
    static class CustomChatStatusCheckerConfiguration {
        @Bean
        DifyChatStatusChecker customChatStatusChecker(DifyChat difyChat) {
            return mock(DifyChatStatusChecker.class);
        }
    }
}
