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

import io.github.guoshiqiufeng.dify.client.spring7.server.DifyServerDefaultClient;
import io.github.guoshiqiufeng.dify.server.DifyServer;
import io.github.guoshiqiufeng.dify.server.client.BaseDifyServerToken;
import io.github.guoshiqiufeng.dify.server.client.DifyServerClient;
import io.github.guoshiqiufeng.dify.server.client.DifyServerTokenDefault;
import io.github.guoshiqiufeng.dify.server.impl.DifyServerClientImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link DifyServerAutoConfiguration}.
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/23 16:25
 */
public class DifyServerAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    DifyPropertiesAutoConfiguration.class,
                    DifyServerAutoConfiguration.class));

    @Test
    void autoconfigurationActivatesWithServerClassOnClasspath() {
        this.contextRunner
                .withPropertyValues("dify.url=https://api.dify.ai")
                .run((context) -> {
                    assertThat(context).hasSingleBean(DifyServerClient.class);
                    assertThat(context).hasSingleBean(DifyServer.class);
                    assertThat(context).hasSingleBean(BaseDifyServerToken.class);
                    assertThat(context.getBean(DifyServerClient.class)).isInstanceOf(DifyServerDefaultClient.class);
                    assertThat(context.getBean(DifyServer.class)).isInstanceOf(DifyServerClientImpl.class);
                    assertThat(context.getBean(BaseDifyServerToken.class)).isInstanceOf(DifyServerTokenDefault.class);
                });
    }

    @Test
    void autoconfigurationIsDisabledWhenServerClassIsNotPresent() {
        this.contextRunner
                .withClassLoader(new FilteredClassLoader(DifyServerClient.class))
                .run((context) -> {
                    assertThat(context).doesNotHaveBean(DifyServerClient.class);
                    assertThat(context).doesNotHaveBean(DifyServer.class);
                    assertThat(context).doesNotHaveBean(BaseDifyServerToken.class);
                });
    }

    @Test
    void defaultClientBuildersAreUsedWhenNoneAreProvided() {
        this.contextRunner
                .withPropertyValues("dify.url=https://api.dify.ai")
                .run((context) -> {
                    assertThat(context).hasSingleBean(DifyServerClient.class);
                    assertThat(context.getBean(DifyServerClient.class)).isInstanceOf(DifyServerDefaultClient.class);
                });
    }

    @Test
    void customServerClientIsRespected() {
        this.contextRunner
                .withUserConfiguration(CustomDifyServerClientConfiguration.class)
                .run((context) -> {
                    assertThat(context).hasSingleBean(DifyServerClient.class);
                    assertThat(context.getBean(DifyServerClient.class)).isNotInstanceOf(DifyServerDefaultClient.class);
                });
    }

    @Test
    void customServerHandlerIsRespected() {
        this.contextRunner
                .withUserConfiguration(CustomDifyServerHandlerConfiguration.class)
                .run((context) -> {
                    assertThat(context).hasSingleBean(DifyServer.class);
                    assertThat(context.getBean(DifyServer.class)).isNotInstanceOf(DifyServerClientImpl.class);
                });
    }

    @Test
    void customServerTokenIsRespected() {
        this.contextRunner
                .withUserConfiguration(CustomDifyServerTokenConfiguration.class)
                .run((context) -> {
                    assertThat(context).hasSingleBean(BaseDifyServerToken.class);
                    assertThat(context.getBean(BaseDifyServerToken.class)).isNotInstanceOf(DifyServerTokenDefault.class);
                });
    }

    @Configuration
    static class CustomDifyServerClientConfiguration {
        @Bean
        DifyServerClient customDifyServerClient() {
            return mock(DifyServerClient.class);
        }
    }

    @Configuration
    static class CustomDifyServerHandlerConfiguration {
        @Bean
        DifyServer customDifyServerHandler() {
            return mock(DifyServer.class);
        }
    }

    @Configuration
    static class CustomDifyServerTokenConfiguration {
        @Bean
        BaseDifyServerToken customBaseDifyServerToken() {
            return mock(BaseDifyServerToken.class);
        }
    }
}
