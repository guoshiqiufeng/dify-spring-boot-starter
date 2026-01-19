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

import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.springboot.common.autoconfigure.DifyCodecAutoConfiguration;
import io.github.guoshiqiufeng.dify.springboot.common.autoconfigure.DifyPropertiesAutoConfiguration;
import io.github.guoshiqiufeng.dify.support.impl.workflow.DifyWorkflowDefaultClient;
import io.github.guoshiqiufeng.dify.workflow.DifyWorkflow;
import io.github.guoshiqiufeng.dify.workflow.client.DifyWorkflowClient;
import io.github.guoshiqiufeng.dify.workflow.impl.DifyWorkflowClientImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link DifyWorkflowAutoConfiguration}.
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/23 15:37
 */
public class DifyWorkflowAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    DifyPropertiesAutoConfiguration.class,
                    DifyCodecAutoConfiguration.class,
                    DifyWorkflowAutoConfiguration.class));

    @Test
    void autoconfigurationActivatesWithWorkflowClassOnClasspath() {
        this.contextRunner
                .withPropertyValues("dify.url=https://api.dify.ai")
                .run((context) -> {
                    assertThat(context).hasSingleBean(DifyWorkflowClient.class);
                    assertThat(context).hasSingleBean(DifyWorkflow.class);
                    assertThat(context.getBean(DifyWorkflowClient.class)).isInstanceOf(DifyWorkflowDefaultClient.class);
                    assertThat(context.getBean(DifyWorkflow.class)).isInstanceOf(DifyWorkflowClientImpl.class);
                });
    }

    @Test
    void autoconfigurationIsDisabledWhenWorkflowClassIsNotPresent() {
        this.contextRunner
                .withClassLoader(new FilteredClassLoader(DifyWorkflowClient.class))
                .run((context) -> {
                    assertThat(context).doesNotHaveBean(DifyWorkflowClient.class);
                    assertThat(context).doesNotHaveBean(DifyWorkflow.class);
                });
    }

    @Test
    void defaultWebClientBuilderIsUsedWhenNoneIsProvided() {
        this.contextRunner
                .withPropertyValues("dify.url=https://api.dify.ai")
                .run((context) -> {
                    assertThat(context).hasSingleBean(DifyWorkflowClient.class);
                    assertThat(context.getBean(DifyWorkflowClient.class)).isInstanceOf(DifyWorkflowDefaultClient.class);
                });
    }

    @Test
    void customWorkflowClientIsRespected() {
        this.contextRunner
                .withUserConfiguration(CustomDifyWorkflowClientConfiguration.class)
                .run((context) -> {
                    assertThat(context).hasSingleBean(DifyWorkflowClient.class);
                    assertThat(context.getBean(DifyWorkflowClient.class)).isNotInstanceOf(DifyWorkflowDefaultClient.class);
                });
    }

    @Test
    void customWorkflowHandlerIsRespected() {
        this.contextRunner
                .withUserConfiguration(CustomDifyWorkflowHandlerConfiguration.class)
                .run((context) -> {
                    assertThat(context).hasSingleBean(DifyWorkflow.class);
                    assertThat(context.getBean(DifyWorkflow.class)).isNotInstanceOf(DifyWorkflowClientImpl.class);
                });
    }

    @Configuration
    static class CustomDifyWorkflowClientConfiguration {
        @Bean
        DifyWorkflowClient customDifyWorkflowClient() {
            return mock(DifyWorkflowClient.class);
        }
    }

    @Configuration
    static class CustomDifyWorkflowHandlerConfiguration {
        @Bean
        DifyWorkflow customDifyWorkflowClient() {
            return mock(DifyWorkflow.class);
        }
    }
}
