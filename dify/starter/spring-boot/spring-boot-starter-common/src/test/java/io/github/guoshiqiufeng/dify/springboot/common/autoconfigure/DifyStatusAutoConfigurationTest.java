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
package io.github.guoshiqiufeng.dify.springboot.common.autoconfigure;

import io.github.guoshiqiufeng.dify.chat.DifyChat;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.dataset.DifyDataset;
import io.github.guoshiqiufeng.dify.server.DifyServer;
import io.github.guoshiqiufeng.dify.status.actuator.DifyHealthIndicator;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Test for DifyStatusAutoConfiguration
 *
 * @author yanghq
 * @version 1.7.0
 * @since 2025/12/19 14:00
 */
class DifyStatusAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(DifyStatusAutoConfiguration.class));

    @Test
    void shouldCreateDifyChatStatusCheckerWhenDifyChatExists() {
        contextRunner
                .withBean(DifyChat.class, () -> mock(DifyChat.class))
                .run(context -> {
                    assertThat(context).hasSingleBean(DifyChatStatusChecker.class);
                    assertThat(context.getBean(DifyChatStatusChecker.class)).isNotNull();
                });
    }

    @Test
    void shouldNotCreateDifyChatStatusCheckerWhenDifyChatNotExists() {
        contextRunner.run(context -> {
            assertThat(context).doesNotHaveBean(DifyChatStatusChecker.class);
        });
    }

    @Test
    void shouldCreateDifyDatasetStatusCheckerWhenDifyDatasetExists() {
        contextRunner
                .withBean(DifyDataset.class, () -> mock(DifyDataset.class))
                .run(context -> {
                    assertThat(context).hasSingleBean(DifyDatasetStatusChecker.class);
                    assertThat(context.getBean(DifyDatasetStatusChecker.class)).isNotNull();
                });
    }

    @Test
    void shouldNotCreateDifyDatasetStatusCheckerWhenDifyDatasetNotExists() {
        contextRunner.run(context -> {
            assertThat(context).doesNotHaveBean(DifyDatasetStatusChecker.class);
        });
    }

    @Test
    void shouldCreateDifyServerStatusCheckerWhenDifyServerExists() {
        contextRunner
                .withBean(DifyServer.class, () -> mock(DifyServer.class))
                .run(context -> {
                    assertThat(context).hasSingleBean(DifyServerStatusChecker.class);
                    assertThat(context.getBean(DifyServerStatusChecker.class)).isNotNull();
                });
    }

    @Test
    void shouldNotCreateDifyServerStatusCheckerWhenDifyServerNotExists() {
        contextRunner.run(context -> {
            assertThat(context).doesNotHaveBean(DifyServerStatusChecker.class);
        });
    }

    @Test
    void shouldCreateDifyWorkflowStatusCheckerWhenDifyWorkflowExists() {
        contextRunner
                .withBean(DifyWorkflow.class, () -> mock(DifyWorkflow.class))
                .run(context -> {
                    assertThat(context).hasSingleBean(DifyWorkflowStatusChecker.class);
                    assertThat(context.getBean(DifyWorkflowStatusChecker.class)).isNotNull();
                });
    }

    @Test
    void shouldNotCreateDifyWorkflowStatusCheckerWhenDifyWorkflowNotExists() {
        contextRunner.run(context -> {
            assertThat(context).doesNotHaveBean(DifyWorkflowStatusChecker.class);
        });
    }

    @Test
    void shouldCreateDefaultStatusCacheService() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(StatusCacheService.class);
            assertThat(context.getBean(StatusCacheService.class))
                    .isInstanceOf(InMemoryStatusCacheService.class);
        });
    }

    @Test
    void shouldNotCreateStatusCacheServiceWhenCustomBeanExists() {
        StatusCacheService customService = mock(StatusCacheService.class);
        contextRunner
                .withBean("customStatusCacheService", StatusCacheService.class, () -> customService)
                .run(context -> {
                    assertThat(context).hasSingleBean(StatusCacheService.class);
                    assertThat(context.getBean(StatusCacheService.class)).isSameAs(customService);
                });
    }

    @Test
    void shouldCreateDifyStatusService() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(DifyStatusService.class);
            assertThat(context.getBean(DifyStatusService.class))
                    .isInstanceOf(DifyStatusServiceImpl.class);
        });
    }

    @Test
    void shouldCreateDifyStatusServiceWithAllCheckers() {
        contextRunner
                .withBean(DifyChat.class, () -> mock(DifyChat.class))
                .withBean(DifyDataset.class, () -> mock(DifyDataset.class))
                .withBean(DifyServer.class, () -> mock(DifyServer.class))
                .withBean(DifyWorkflow.class, () -> mock(DifyWorkflow.class))
                .run(context -> {
                    assertThat(context).hasSingleBean(DifyStatusService.class);
                    assertThat(context).hasSingleBean(DifyChatStatusChecker.class);
                    assertThat(context).hasSingleBean(DifyDatasetStatusChecker.class);
                    assertThat(context).hasSingleBean(DifyServerStatusChecker.class);
                    assertThat(context).hasSingleBean(DifyWorkflowStatusChecker.class);
                });
    }

    @Test
    void shouldNotCreateDifyStatusServiceWhenCustomBeanExists() {
        DifyStatusService customService = mock(DifyStatusService.class);
        contextRunner
                .withBean("customDifyStatusService", DifyStatusService.class, () -> customService)
                .run(context -> {
                    assertThat(context).hasSingleBean(DifyStatusService.class);
                    assertThat(context.getBean(DifyStatusService.class)).isSameAs(customService);
                });
    }

    @Test
    void shouldCreateDifyHealthIndicatorWhenEnabled() {
        DifyProperties properties = new DifyProperties();
        contextRunner
                .withBean(DifyStatusService.class, () -> mock(DifyStatusService.class))
                .withBean(DifyProperties.class, () -> properties)
                .withPropertyValues("dify.status.health-indicator-enabled=true")
                .run(context -> {
                    assertThat(context).hasSingleBean(DifyHealthIndicator.class);
                    assertThat(context.getBean(DifyHealthIndicator.class)).isNotNull();
                });
    }

    @Test
    void shouldNotCreateDifyHealthIndicatorWhenDisabled() {
        DifyProperties properties = new DifyProperties();
        contextRunner
                .withBean(DifyStatusService.class, () -> mock(DifyStatusService.class))
                .withBean(DifyProperties.class, () -> properties)
                .withPropertyValues("dify.status.health-indicator-enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(DifyHealthIndicator.class);
                });
    }

    @Test
    void shouldNotCreateDifyHealthIndicatorWhenPropertyNotSet() {
        DifyProperties properties = new DifyProperties();
        contextRunner
                .withBean(DifyStatusService.class, () -> mock(DifyStatusService.class))
                .withBean(DifyProperties.class, () -> properties)
                .run(context -> {
                    assertThat(context).doesNotHaveBean(DifyHealthIndicator.class);
                });
    }

    @Test
    void shouldNotCreateDifyHealthIndicatorWhenCustomBeanExists() {
        DifyProperties properties = new DifyProperties();
        DifyHealthIndicator customIndicator = mock(DifyHealthIndicator.class);
        contextRunner
                .withBean(DifyStatusService.class, () -> mock(DifyStatusService.class))
                .withBean(DifyProperties.class, () -> properties)
                .withBean("customDifyHealthIndicator", DifyHealthIndicator.class, () -> customIndicator)
                .withPropertyValues("dify.status.health-indicator-enabled=true")
                .run(context -> {
                    assertThat(context).hasSingleBean(DifyHealthIndicator.class);
                    assertThat(context.getBean(DifyHealthIndicator.class)).isSameAs(customIndicator);
                });
    }
}
