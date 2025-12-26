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
package io.github.guoshiqiufeng.dify.springboot4.autoconfigure;

import io.github.guoshiqiufeng.dify.chat.DifyChat;
import io.github.guoshiqiufeng.dify.chat.client.DifyChatClient;
import io.github.guoshiqiufeng.dify.chat.impl.DifyChatClientImpl;
import io.github.guoshiqiufeng.dify.client.spring7.chat.DifyChatDefaultClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link DifyChatAutoConfiguration}.
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/23 16:17
 */
public class DifyChatAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    DifyPropertiesAutoConfiguration.class,
                    DifyChatAutoConfiguration.class));

    @Test
    void autoconfigurationActivatesWithChatClassOnClasspath() {
        this.contextRunner
                .withPropertyValues("dify.url=https://api.dify.ai")
                .run((context) -> {
                    assertThat(context).hasSingleBean(DifyChatClient.class);
                    assertThat(context).hasSingleBean(DifyChat.class);
                    assertThat(context.getBean(DifyChatClient.class)).isInstanceOf(DifyChatDefaultClient.class);
                    assertThat(context.getBean(DifyChat.class)).isInstanceOf(DifyChatClientImpl.class);
                });
    }

    @Test
    void autoconfigurationIsDisabledWhenChatClassIsNotPresent() {
        this.contextRunner
                .withClassLoader(new FilteredClassLoader(DifyChatClient.class))
                .run((context) -> {
                    assertThat(context).doesNotHaveBean(DifyChatClient.class);
                    assertThat(context).doesNotHaveBean(DifyChat.class);
                });
    }

    @Test
    void defaultClientBuildersAreUsedWhenNoneAreProvided() {
        this.contextRunner
                .withPropertyValues("dify.url=https://api.dify.ai")
                .run((context) -> {
                    assertThat(context).hasSingleBean(DifyChatClient.class);
                    assertThat(context.getBean(DifyChatClient.class)).isInstanceOf(DifyChatDefaultClient.class);
                });
    }

    @Test
    void customChatClientIsRespected() {
        this.contextRunner
                .withUserConfiguration(CustomDifyChatClientConfiguration.class)
                .run((context) -> {
                    assertThat(context).hasSingleBean(DifyChatClient.class);
                    assertThat(context.getBean(DifyChatClient.class)).isNotInstanceOf(DifyChatDefaultClient.class);
                });
    }

    @Test
    void customChatHandlerIsRespected() {
        this.contextRunner
                .withUserConfiguration(CustomDifyChatHandlerConfiguration.class)
                .run((context) -> {
                    assertThat(context).hasSingleBean(DifyChat.class);
                    assertThat(context.getBean(DifyChat.class)).isNotInstanceOf(DifyChatClientImpl.class);
                });
    }

    @Configuration
    static class CustomDifyChatClientConfiguration {
        @Bean
        DifyChatClient customDifyChatClient() {
            return mock(DifyChatClient.class);
        }
    }

    @Configuration
    static class CustomDifyChatHandlerConfiguration {
        @Bean
        DifyChat customDifyChatHandler() {
            return mock(DifyChat.class);
        }
    }
}
