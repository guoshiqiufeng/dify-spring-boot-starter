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
package io.github.guoshiqiufeng.dify.client.integration.spring.http.pool;

/**
 * Factory interface for creating WebClient ConnectionProvider.
 * Allows users to customize connection pool implementation for WebClient.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026-02-06
 */
public interface WebClientConnectionProviderFactory {

    /**
     * Build a reactor-netty ConnectionProvider with the given settings.
     *
     * @param settings the pool settings
     * @param poolName the name of the connection pool
     * @return ConnectionProvider instance (reactor.netty.resources.ConnectionProvider)
     */
    Object buildConnectionProvider(PoolSettings settings, String poolName);
}
