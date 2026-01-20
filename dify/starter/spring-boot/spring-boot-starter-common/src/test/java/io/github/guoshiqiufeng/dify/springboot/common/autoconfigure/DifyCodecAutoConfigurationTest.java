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
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for DifyCodecAutoConfiguration
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025-12-30
 */
class DifyCodecAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(DifyCodecAutoConfiguration.class));

    @Test
    void shouldCreateJsonMapperBean() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(JsonMapper.class);
            assertThat(context.getBean(JsonMapper.class))
                    .isInstanceOf(JacksonJsonMapper.class);
        });
    }

    @Test
    void shouldNotCreateJsonMapperWhenBeanExists() {
        JsonMapper customMapper = JacksonJsonMapper.getInstance();
        contextRunner
                .withBean("customJsonMapper", JsonMapper.class, () -> customMapper)
                .run(context -> {
                    assertThat(context).hasSingleBean(JsonMapper.class);
                    assertThat(context.getBean(JsonMapper.class)).isSameAs(customMapper);
                });
    }
}
