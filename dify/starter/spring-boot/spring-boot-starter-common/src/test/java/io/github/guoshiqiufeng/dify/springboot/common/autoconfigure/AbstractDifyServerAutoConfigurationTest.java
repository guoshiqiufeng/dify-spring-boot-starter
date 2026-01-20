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

import io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.SpringHttpClientFactory;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.server.DifyServer;
import io.github.guoshiqiufeng.dify.server.client.BaseDifyServerToken;
import io.github.guoshiqiufeng.dify.server.client.DifyServerClient;
import io.github.guoshiqiufeng.dify.server.client.DifyServerTokenDefault;
import io.github.guoshiqiufeng.dify.server.impl.DifyServerClientImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Test for AbstractDifyServerAutoConfiguration
 */
class AbstractDifyServerAutoConfigurationTest {

    @Configuration
    static class TestServerConfiguration extends AbstractDifyServerAutoConfiguration {
        @Override
        protected SpringHttpClientFactory createHttpClientFactory(DifyProperties properties, JsonMapper jsonMapper) {
            return mock(SpringHttpClientFactory.class);
        }

        @Bean
        public DifyProperties difyProperties() {
            DifyProperties properties = new DifyProperties();
            properties.setUrl("https://api.dify.ai");
            DifyProperties.Server server = new DifyProperties.Server();
            server.setEmail("test");
            server.setPassword("test");
            properties.setServer(server);
            return properties;
        }

        @Bean
        public JsonMapper jsonMapper() {
            return mock(JsonMapper.class);
        }
    }

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(TestServerConfiguration.class));

    @Test
    void shouldCreateDifyServerTokenBean() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(BaseDifyServerToken.class);
            assertThat(context.getBean(BaseDifyServerToken.class))
                    .isInstanceOf(DifyServerTokenDefault.class);
        });
    }

    @Test
    void shouldCreateDifyServerClientBean() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(DifyServerClient.class);
        });
    }

    @Test
    void shouldCreateDifyServerHandlerBean() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(DifyServer.class);
            assertThat(context.getBean(DifyServer.class))
                    .isInstanceOf(DifyServerClientImpl.class);
        });
    }
}
