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

import io.github.guoshiqiufeng.dify.server.client.BaseDifyServerToken;
import io.github.guoshiqiufeng.dify.server.client.DifyServerTokenDefault;
import io.github.guoshiqiufeng.dify.server.client.DifyServerTokenRedis;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Test for DifyServerRedisTokenAutoConfiguration
 *
 * @author yanghq
 * @version 1.0
 * @since 2025/4/17 09:30
 */
class DifyServerRedisTokenAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(
                    DifyServerRedisTokenAutoConfiguration.class
            ));

    @Test
    @SuppressWarnings("unchecked")
    void shouldCreateRedisTokenWhenRedisTemplateExists() {
        RedisTemplate<String, String> redisTemplate = mock(RedisTemplate.class);

        contextRunner
                .withBean("redisTemplate", RedisTemplate.class, () -> redisTemplate)
                .run(context -> {
                    assertThat(context).hasSingleBean(BaseDifyServerToken.class);
                    assertThat(context.getBean(BaseDifyServerToken.class))
                            .isInstanceOf(DifyServerTokenRedis.class);
                });
    }

    @Test
    void shouldNotCreateTokenWhenNoRedisTemplate() {
        contextRunner.run(context -> {
            assertThat(context).doesNotHaveBean(BaseDifyServerToken.class);
        });
    }

    @Test
    void shouldCreateDefaultTokenWhenRedisTemplateProviderReturnsNull() {
        DifyServerRedisTokenAutoConfiguration configuration = new DifyServerRedisTokenAutoConfiguration();
        ObjectProvider<RedisTemplate<String, String>> provider = new ObjectProvider<RedisTemplate<String, String>>() {
            @Override
            public RedisTemplate<String, String> getObject() {
                return null;
            }

            @Override
            public RedisTemplate<String, String> getObject(Object... args) {
                return null;
            }

            @Override
            public RedisTemplate<String, String> getIfAvailable() {
                return null;
            }

            @Override
            public RedisTemplate<String, String> getIfUnique() {
                return null;
            }
        };

        BaseDifyServerToken token = configuration.difyServerToken(provider);
        assertThat(token).isInstanceOf(DifyServerTokenDefault.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldNotCreateTokenWhenCustomTokenExists() {
        BaseDifyServerToken customToken = mock(BaseDifyServerToken.class);
        RedisTemplate<String, String> redisTemplate = mock(RedisTemplate.class);

        contextRunner
                .withBean("redisTemplate", RedisTemplate.class, () -> redisTemplate)
                .withBean("customToken", BaseDifyServerToken.class, () -> customToken)
                .run(context -> {
                    assertThat(context).hasSingleBean(BaseDifyServerToken.class);
                    assertThat(context.getBean(BaseDifyServerToken.class)).isSameAs(customToken);
                });
    }
}
