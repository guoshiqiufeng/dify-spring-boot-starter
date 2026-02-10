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
package io.github.guoshiqiufeng.dify.client.integration.spring.http.factory;

import io.github.guoshiqiufeng.dify.client.integration.spring.http.pool.PoolSettings;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Strategy interface for configuring connection pools.
 * Different implementations for different HTTP client libraries.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/2/8
 */
public interface ConnectionPoolConfigurer {

    /**
     * Configure connection pool for WebClient.
     *
     * @param builder      WebClient.Builder
     * @param poolSettings pool settings
     * @return configured builder
     */
    WebClient.Builder configureWebClient(WebClient.Builder builder, PoolSettings poolSettings);

    /**
     * Configure connection pool for RestClient.
     *
     * @param builder      RestClient.Builder (as Object)
     * @param poolSettings pool settings
     * @return configured builder
     */
    Object configureRestClient(Object builder, PoolSettings poolSettings);

    /**
     * Get the name of this configurer (e.g., "ReactorNetty", "ApacheHC5").
     *
     * @return configurer name
     */
    String getName();

    /**
     * Check if this configurer is available in the current classpath.
     *
     * @return true if available
     */
    boolean isAvailable();
}
