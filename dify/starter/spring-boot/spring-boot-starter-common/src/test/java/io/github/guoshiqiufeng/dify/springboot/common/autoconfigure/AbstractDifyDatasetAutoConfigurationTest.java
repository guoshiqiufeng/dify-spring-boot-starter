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

import io.github.guoshiqiufeng.dify.client.codec.jackson.JacksonJsonMapper;
import io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper;
import io.github.guoshiqiufeng.dify.client.core.http.HttpClientFactory;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.SpringHttpClientFactory;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.dataset.DifyDataset;
import io.github.guoshiqiufeng.dify.dataset.client.DifyDatasetClient;
import io.github.guoshiqiufeng.dify.dataset.impl.DifyDatasetClientImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for AbstractDifyDatasetAutoConfiguration
 */
class AbstractDifyDatasetAutoConfigurationTest {

    @Configuration
    static class TestDatasetConfiguration extends AbstractDifyDatasetAutoConfiguration {
        @Override
        protected HttpClientFactory createHttpClientFactory(DifyProperties properties, JsonMapper jsonMapper) {
            return new SpringHttpClientFactory(jsonMapper);
        }

        @Bean
        public DifyProperties difyProperties() {
            DifyProperties properties = new DifyProperties();
            properties.setUrl("https://api.dify.ai");
            DifyProperties.Dataset dataset = new DifyProperties.Dataset();
            dataset.setApiKey("test-key");
            properties.setDataset(dataset);
            return properties;
        }

        @Bean
        public JsonMapper jsonMapper() {
            return JacksonJsonMapper.getInstance();
        }
    }

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(TestDatasetConfiguration.class));

    @Test
    void shouldCreateDifyDatasetClientBean() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(DifyDatasetClient.class);
        });
    }

    @Test
    void shouldCreateDifyDatasetBean() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(DifyDataset.class);
            assertThat(context.getBean(DifyDataset.class))
                    .isInstanceOf(DifyDatasetClientImpl.class);
        });
    }
}
