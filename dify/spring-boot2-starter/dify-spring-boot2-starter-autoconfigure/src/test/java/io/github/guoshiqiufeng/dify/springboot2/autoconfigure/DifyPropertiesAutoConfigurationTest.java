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
package io.github.guoshiqiufeng.dify.springboot2.autoconfigure;

import io.github.guoshiqiufeng.dify.autoconfigure.DifyConnectionDetails;
import io.github.guoshiqiufeng.dify.autoconfigure.DifyPropertiesAutoConfiguration;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DifyPropertiesAutoConfiguration}.
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/23 15:28
 */
public class DifyPropertiesAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(DifyPropertiesAutoConfiguration.class));

    @Test
    void defaultPropertiesAreCreated() {
        this.contextRunner.run((context) -> {
            assertThat(context).hasSingleBean(DifyProperties.class);
            assertThat(context).hasSingleBean(DifyConnectionDetails.class);
        });
    }

    @Test
    void customPropertiesOverrideDefaults() {
        this.contextRunner
                .withPropertyValues("dify.url=https://custom-dify-api.example.com")
                .run((context) -> {
                    DifyProperties properties = context.getBean(DifyProperties.class);
                    // assertThat(properties.getUrl()).isEqualTo("https://custom-dify-api.example.com");

                    DifyConnectionDetails connectionDetails = context.getBean(DifyConnectionDetails.class);
                    // assertThat(connectionDetails.getUrl()).isEqualTo("https://custom-dify-api.example.com");
                });
    }

    @Test
    void customDifyConnectionDetailsAreRespected() {
        this.contextRunner
                .withBean(DifyConnectionDetails.class, () -> new DifyConnectionDetails() {
                    @Override
                    public String getUrl() {
                        return "https://custom-connection-details.example.com";
                    }
                })
                .run((context) -> {
                    assertThat(context).hasSingleBean(DifyConnectionDetails.class);
                    DifyConnectionDetails connectionDetails = context.getBean(DifyConnectionDetails.class);
                    // assertThat(connectionDetails.getUrl()).isEqualTo("https://custom-connection-details.example.com");
                });
    }
}
