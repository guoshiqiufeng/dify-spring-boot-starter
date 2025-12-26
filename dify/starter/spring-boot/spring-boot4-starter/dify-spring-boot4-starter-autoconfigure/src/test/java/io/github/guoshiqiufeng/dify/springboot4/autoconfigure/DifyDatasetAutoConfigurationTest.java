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

import io.github.guoshiqiufeng.dify.dataset.DifyDataset;
import io.github.guoshiqiufeng.dify.dataset.client.DifyDatasetClient;
import io.github.guoshiqiufeng.dify.dataset.impl.DifyDatasetClientImpl;
import io.github.guoshiqiufeng.dify.support.impl.dataset.DifyDatasetDefaultClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link DifyDatasetAutoConfiguration}.
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/23 16:12
 */
public class DifyDatasetAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    DifyPropertiesAutoConfiguration.class,
                    DifyCodecAutoConfiguration.class,
                    DifyDatasetAutoConfiguration.class));

    @Test
    void autoconfigurationActivatesWithDatasetClassOnClasspath() {
        this.contextRunner
                .withPropertyValues(
                        "dify.url=https://api.dify.ai",
                        "dify.dataset.api-key=test-api-key")
                .run((context) -> {
                    assertThat(context).hasSingleBean(DifyDatasetClient.class);
                    assertThat(context).hasSingleBean(DifyDataset.class);
                    assertThat(context.getBean(DifyDatasetClient.class)).isInstanceOf(DifyDatasetDefaultClient.class);
                    assertThat(context.getBean(DifyDataset.class)).isInstanceOf(DifyDatasetClientImpl.class);
                });
    }

    @Test
    void autoconfigurationIsDisabledWhenDatasetClassIsNotPresent() {
        this.contextRunner
                .withClassLoader(new FilteredClassLoader(DifyDatasetClient.class))
                .run((context) -> {
                    assertThat(context).doesNotHaveBean(DifyDatasetClient.class);
                    assertThat(context).doesNotHaveBean(DifyDataset.class);
                });
    }

    @Test
    void defaultClientBuildersAreUsedWhenNoneAreProvided() {
        this.contextRunner
                .withPropertyValues(
                        "dify.url=https://api.dify.ai",
                        "dify.dataset.api-key=test-api-key")
                .run((context) -> {
                    assertThat(context).hasSingleBean(DifyDatasetClient.class);
                    assertThat(context.getBean(DifyDatasetClient.class)).isInstanceOf(DifyDatasetDefaultClient.class);
                });
    }

    @Test
    void customDatasetClientIsRespected() {
        this.contextRunner
                .withUserConfiguration(CustomDifyDatasetClientConfiguration.class)
                .run((context) -> {
                    assertThat(context).hasSingleBean(DifyDatasetClient.class);
                    assertThat(context.getBean(DifyDatasetClient.class)).isNotInstanceOf(DifyDatasetDefaultClient.class);
                });
    }

    @Test
    void customDatasetHandlerIsRespected() {
        this.contextRunner
                .withUserConfiguration(CustomDifyDatasetHandlerConfiguration.class)
                .run((context) -> {
                    assertThat(context).hasSingleBean(DifyDataset.class);
                    assertThat(context.getBean(DifyDataset.class)).isNotInstanceOf(DifyDatasetClientImpl.class);
                });
    }

    @Configuration
    static class CustomDifyDatasetClientConfiguration {
        @Bean
        DifyDatasetClient customDifyDatasetClient() {
            return mock(DifyDatasetClient.class);
        }
    }

    @Configuration
    static class CustomDifyDatasetHandlerConfiguration {
        @Bean
        DifyDataset customDifyDatasetHandler() {
            return mock(DifyDataset.class);
        }
    }
}
