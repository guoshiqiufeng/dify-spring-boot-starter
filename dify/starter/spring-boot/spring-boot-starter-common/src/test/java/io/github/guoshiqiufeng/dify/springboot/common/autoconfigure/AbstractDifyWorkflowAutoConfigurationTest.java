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

import io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.SpringHttpClientFactory;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.workflow.DifyWorkflow;
import io.github.guoshiqiufeng.dify.workflow.client.DifyWorkflowClient;
import io.github.guoshiqiufeng.dify.workflow.impl.DifyWorkflowClientImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Test for AbstractDifyWorkflowAutoConfiguration
 */
class AbstractDifyWorkflowAutoConfigurationTest {

    @Configuration
    static class TestWorkflowConfiguration extends AbstractDifyWorkflowAutoConfiguration {
        @Override
        protected SpringHttpClientFactory createHttpClientFactory(DifyProperties properties, JsonMapper jsonMapper) {
            return mock(SpringHttpClientFactory.class);
        }

        @Bean
        public DifyProperties difyProperties() {
            DifyProperties properties = new DifyProperties();
            properties.setUrl("https://api.dify.ai");
            return properties;
        }

        @Bean
        public JsonMapper jsonMapper() {
            return mock(JsonMapper.class);
        }
    }

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(TestWorkflowConfiguration.class));

    @Test
    void shouldCreateDifyWorkflowClientBean() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(DifyWorkflowClient.class);
        });
    }

    @Test
    void shouldCreateDifyWorkflowHandlerBean() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(DifyWorkflow.class);
            assertThat(context.getBean(DifyWorkflow.class))
                    .isInstanceOf(DifyWorkflowClientImpl.class);
        });
    }
}
