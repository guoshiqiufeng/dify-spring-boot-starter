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
package io.github.guoshiqiufeng.dify.client.integration.spring.http;

import io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper;
import io.github.guoshiqiufeng.dify.client.core.web.client.HttpClient;
import io.github.guoshiqiufeng.dify.client.core.http.HttpClientFactory;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Factory for creating Spring HTTP client instances.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025-12-26
 */
public class SpringHttpClientFactory implements HttpClientFactory {

    private final WebClient.Builder webClientBuilder;

    private final Object restClientBuilder;

    private final JsonMapper jsonMapper;

    public SpringHttpClientFactory(WebClient.Builder webClientBuilder, Object restClientBuilder, JsonMapper jsonMapper) {
        this.webClientBuilder = webClientBuilder;
        this.restClientBuilder = restClientBuilder;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public HttpClient createClient(String baseUrl, DifyProperties.ClientConfig clientConfig) {
        return new SpringHttpClient(baseUrl, clientConfig, webClientBuilder, restClientBuilder, jsonMapper);
    }
}
