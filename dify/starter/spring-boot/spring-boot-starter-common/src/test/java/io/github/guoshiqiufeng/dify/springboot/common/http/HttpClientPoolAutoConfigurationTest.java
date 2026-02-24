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
package io.github.guoshiqiufeng.dify.springboot.common.http;

import io.github.guoshiqiufeng.dify.client.integration.spring.http.pool.RestClientHttpClientFactory;
import io.github.guoshiqiufeng.dify.client.integration.spring.http.pool.WebClientConnectionProviderFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for HttpClientPoolAutoConfiguration.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026-02-06
 */
@DisplayName("HttpClientPoolAutoConfiguration Tests")
class HttpClientPoolAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(HttpClientPoolAutoConfiguration.class));

    @Test
    @DisplayName("Should create default WebClientConnectionProviderFactory when not provided")
    void testDefaultWebClientConnectionProviderFactory() {
        this.contextRunner.run(context -> {
            assertThat(context).hasSingleBean(WebClientConnectionProviderFactory.class);
            assertThat(context.getBean(WebClientConnectionProviderFactory.class))
                    .isNotNull();
        });
    }

    @Test
    @DisplayName("Should create default RestClientHttpClientFactory when not provided")
    void testDefaultRestClientHttpClientFactory() {
        this.contextRunner.run(context -> {
            assertThat(context).hasSingleBean(RestClientHttpClientFactory.class);
            assertThat(context.getBean(RestClientHttpClientFactory.class))
                    .isNotNull();
        });
    }

    @Test
    @DisplayName("Should use custom WebClientConnectionProviderFactory when provided")
    void testCustomWebClientConnectionProviderFactory() {
        this.contextRunner
                .withUserConfiguration(CustomWebClientFactoryConfig.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(WebClientConnectionProviderFactory.class);
                    assertThat(context.getBean(WebClientConnectionProviderFactory.class))
                            .isInstanceOf(CustomWebClientConnectionProviderFactory.class);
                });
    }

    @Test
    @DisplayName("Should use custom RestClientHttpClientFactory when provided")
    void testCustomRestClientHttpClientFactory() {
        this.contextRunner
                .withUserConfiguration(CustomRestClientFactoryConfig.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(RestClientHttpClientFactory.class);
                    assertThat(context.getBean(RestClientHttpClientFactory.class))
                            .isInstanceOf(CustomRestClientHttpClientFactory.class);
                });
    }

    @Configuration
    static class CustomWebClientFactoryConfig {
        @Bean
        public WebClientConnectionProviderFactory customWebClientConnectionProviderFactory() {
            return new CustomWebClientConnectionProviderFactory();
        }
    }

    @Configuration
    static class CustomRestClientFactoryConfig {
        @Bean
        public RestClientHttpClientFactory customRestClientHttpClientFactory() {
            return new CustomRestClientHttpClientFactory();
        }
    }

    static class CustomWebClientConnectionProviderFactory implements WebClientConnectionProviderFactory {
        @Override
        public Object buildConnectionProvider(io.github.guoshiqiufeng.dify.client.integration.spring.http.pool.PoolSettings settings, String poolName) {
            return null; // Custom implementation
        }
    }

    static class CustomRestClientHttpClientFactory implements RestClientHttpClientFactory {
        @Override
        public Object buildHttpClient(io.github.guoshiqiufeng.dify.client.integration.spring.http.pool.PoolSettings settings) {
            return null; // Custom implementation
        }
    }
}
