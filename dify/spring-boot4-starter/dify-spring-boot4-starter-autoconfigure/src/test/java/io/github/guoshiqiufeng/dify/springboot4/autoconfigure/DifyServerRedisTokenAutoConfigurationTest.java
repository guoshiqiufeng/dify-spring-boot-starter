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

import io.github.guoshiqiufeng.dify.server.client.BaseDifyServerToken;
import io.github.guoshiqiufeng.dify.server.client.DifyServerClient;
import io.github.guoshiqiufeng.dify.server.client.DifyServerTokenDefault;
import io.github.guoshiqiufeng.dify.server.client.DifyServerTokenRedis;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link DifyServerRedisTokenAutoConfiguration}.
 *
 * @author yanghq
 * @version 0.10.0
 * @since 2025/4/23 16:24
 */
public class DifyServerRedisTokenAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    DataRedisAutoConfiguration.class,
                    DifyServerRedisTokenAutoConfiguration.class));

    @Test
    void autoconfigurationActivatesWithRedisAndServerClasses() {
        this.contextRunner
                .withPropertyValues("spring.data.redis.host=localhost")
                .run((context) -> {
                    assertThat(context).hasBean("redisTemplate");
                    assertThat(context).hasBean("stringRedisTemplate");
                    assertThat(context).hasSingleBean(BaseDifyServerToken.class);
                    assertThat(context.getBean(BaseDifyServerToken.class)).isInstanceOf(DifyServerTokenRedis.class);
                });
    }

    @Test
    void autoconfigurationIsDisabledWhenServerClassIsNotPresent() {
        this.contextRunner
                .withClassLoader(new FilteredClassLoader(DifyServerClient.class))
                .run((context) -> {
                    assertThat(context).doesNotHaveBean(BaseDifyServerToken.class);
                });
    }

    @Test
    void autoconfigurationIsDisabledWhenRedisTemplateIsNotPresent() {
        ApplicationContextRunner localContextRunner = new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(
                        DifyServerRedisTokenAutoConfiguration.class));

        localContextRunner.run((context) -> {
            assertThat(context).doesNotHaveBean(BaseDifyServerToken.class);
        });
    }

    @Test
    void customServerTokenIsRespected() {
        this.contextRunner
                .withUserConfiguration(CustomDifyServerTokenConfiguration.class)
                .run((context) -> {
                    assertThat(context).hasSingleBean(BaseDifyServerToken.class);
                    assertThat(context.getBean(BaseDifyServerToken.class)).isNotInstanceOf(DifyServerTokenRedis.class);
                    assertThat(context.getBean(BaseDifyServerToken.class)).isNotInstanceOf(DifyServerTokenDefault.class);
                });
    }

    @Configuration
    static class CustomDifyServerTokenConfiguration {
        @Bean
        BaseDifyServerToken customDifyServerToken() {
            return mock(BaseDifyServerToken.class);
        }
    }
}
