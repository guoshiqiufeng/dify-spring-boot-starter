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
package io.github.guoshiqiufeng.dify.springboot.autoconfigure;

import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link DifyPropertiesAutoConfiguration}.
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/23 16:10
 */
public class DifyPropertiesAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    DifyPropertiesAutoConfiguration.class));

    @Test
    void difyPropertiesAreConfigured() {
        this.contextRunner
                .withPropertyValues("dify.url=https://api.dify.ai")
                .run(context -> {
                    DifyProperties difyProperties = context.getBean(DifyProperties.class);
                    DifyConnectionDetails connectionProperties = context.getBean(DifyConnectionDetails.class);

                    assertThat(difyProperties.getUrl()).isEqualTo("https://api.dify.ai");
                    assertThat(connectionProperties.getUrl()).isEqualTo("https://api.dify.ai");
                });

    }

    @Test
    void customDifyPropertiesAreRespected() {
        this.contextRunner
                .withUserConfiguration(CustomDifyPropertiesConfiguration.class)
                .run((context) -> {
                    assertThat(context).hasSingleBean(DifyProperties.class);
                    assertThat(context.getBean(DifyProperties.class)).isNotInstanceOf(DifyConnectionDetails.class);
                });
    }

    @Test
    void customConnectionDetailsAreRespected() {
        this.contextRunner
                .withUserConfiguration(CustomConnectionDetailsConfiguration.class)
                .run((context) -> {
                    assertThat(context).hasSingleBean(DifyConnectionDetails.class);
                    assertThat(context.getBean(CustomConnectionDetailsConfiguration.class)).isNotInstanceOf(DifyConnectionDetails.class);
                });
    }

    @Configuration
    static class CustomDifyPropertiesConfiguration {
        @Bean
        DifyProperties customDifyProperties() {
            return mock(DifyProperties.class);
        }
    }

    @Configuration
    static class CustomConnectionDetailsConfiguration {
        @Bean
        DifyConnectionDetails customDifyConnectionDetails() {
            return mock(DifyConnectionDetails.class);
        }
    }
}
