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

import io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper;
import io.github.guoshiqiufeng.dify.client.codec.jackson.JacksonJsonMapper;
import io.github.guoshiqiufeng.dify.springboot.common.autoconfigure.DifyCodecAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link DifyCodecAutoConfiguration}.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025-12-30
 */
public class DifyCodecAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(DifyCodecAutoConfiguration.class));

    @Test
    void autoconfigurationActivatesWithJsonMapperClassOnClasspath() {
        this.contextRunner
                .run((context) -> {
                    assertThat(context).hasSingleBean(JsonMapper.class);
                    assertThat(context.getBean(JsonMapper.class)).isInstanceOf(JacksonJsonMapper.class);
                });
    }

    @Test
    void autoconfigurationIsDisabledWhenJsonMapperClassIsNotPresent() {
        this.contextRunner
                .withClassLoader(new FilteredClassLoader(JsonMapper.class))
                .run((context) -> {
                    assertThat(context).doesNotHaveBean(JsonMapper.class);
                });
    }

    @Test
    void autoconfigurationIsDisabledWhenJacksonJsonMapperClassIsNotPresent() {
        this.contextRunner
                .withClassLoader(new FilteredClassLoader(JacksonJsonMapper.class))
                .run((context) -> {
                    assertThat(context).doesNotHaveBean(JsonMapper.class);
                });
    }

    @Test
    void defaultJsonMapperIsJacksonJsonMapper() {
        this.contextRunner
                .run((context) -> {
                    assertThat(context).hasSingleBean(JsonMapper.class);
                    JsonMapper jsonMapper = context.getBean(JsonMapper.class);
                    assertThat(jsonMapper).isInstanceOf(JacksonJsonMapper.class);
                    assertThat(jsonMapper).isSameAs(JacksonJsonMapper.getInstance());
                });
    }

    @Test
    void customJsonMapperIsRespected() {
        this.contextRunner
                .withUserConfiguration(CustomJsonMapperConfiguration.class)
                .run((context) -> {
                    assertThat(context).hasSingleBean(JsonMapper.class);
                    assertThat(context.getBean(JsonMapper.class)).isNotInstanceOf(JacksonJsonMapper.class);
                });
    }

    @Configuration
    static class CustomJsonMapperConfiguration {
        @Bean
        JsonMapper customJsonMapper() {
            return mock(JsonMapper.class);
        }
    }
}
