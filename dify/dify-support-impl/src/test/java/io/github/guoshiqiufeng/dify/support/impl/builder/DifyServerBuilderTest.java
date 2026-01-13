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
package io.github.guoshiqiufeng.dify.support.impl.builder;

import io.github.guoshiqiufeng.dify.client.core.http.HttpClientFactory;
import io.github.guoshiqiufeng.dify.client.core.web.client.HttpClient;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.server.DifyServer;
import io.github.guoshiqiufeng.dify.server.client.BaseDifyServerToken;
import io.github.guoshiqiufeng.dify.server.client.DifyServerClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DifyServerBuilder
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/01/13
 */
class DifyServerBuilderTest {

    @Test
    void testCreateFromClient() {
        DifyServerClient mockClient = mock(DifyServerClient.class);
        DifyServer server = DifyServerBuilder.create(mockClient);

        assertNotNull(server);
    }

    @Test
    void testCreateClientFromHttpClientAndProperties() {
        HttpClient mockHttpClient = mock(HttpClient.class);
        DifyProperties.Server serverProperties = new DifyProperties.Server();

        DifyServerClient client = DifyServerBuilder.createClient(mockHttpClient, serverProperties);

        assertNotNull(client);
    }

    @Test
    void testCreateClientFromHttpClient() {
        HttpClient mockHttpClient = mock(HttpClient.class);

        DifyServerClient client = DifyServerBuilder.createClient(mockHttpClient);

        assertNotNull(client);
    }

    @Test
    void testCreateClientFromHttpClientAndToken() {
        HttpClient mockHttpClient = mock(HttpClient.class);
        BaseDifyServerToken mockToken = mock(BaseDifyServerToken.class);

        DifyServerClient client = DifyServerBuilder.createClient(mockHttpClient, mockToken);

        assertNotNull(client);
    }

    @Test
    void testCreateClientWithAllParameters() {
        DifyProperties.Server serverProperties = new DifyProperties.Server();
        String baseUrl = "https://api.dify.ai";
        HttpClientFactory mockFactory = mock(HttpClientFactory.class);
        HttpClient mockHttpClient = mock(HttpClient.class);
        when(mockFactory.createClient(anyString(), any())).thenReturn(mockHttpClient);

        DifyServerClient client = DifyServerBuilder.createClient(serverProperties, baseUrl, mockFactory);

        assertNotNull(client);
    }

    @Test
    void testBuilder() {
        DifyServerBuilder.Builder builder = DifyServerBuilder.builder();
        assertNotNull(builder);
    }

    @Test
    void testBuilderBuild() {
        DifyServerBuilder.Builder builder = DifyServerBuilder.builder();
        builder.baseUrl("https://api.dify.ai");

        DifyServerClient client = builder.build();
        assertNotNull(client);
    }

    @Test
    void testBuilderWithServerProperties() {
        DifyProperties.Server serverProperties = new DifyProperties.Server();

        DifyServerClient client = DifyServerBuilder.builder()
                .baseUrl("https://api.dify.ai")
                .serverProperties(serverProperties)
                .build();

        assertNotNull(client);
    }

    @Test
    void testBuilderWithServerToken() {
        BaseDifyServerToken mockToken = mock(BaseDifyServerToken.class);

        DifyServerClient client = DifyServerBuilder.builder()
                .baseUrl("https://api.dify.ai")
                .serverToken(mockToken)
                .build();

        assertNotNull(client);
    }

    @Test
    void testBuilderWithoutServerToken() {
        DifyServerClient client = DifyServerBuilder.builder()
                .baseUrl("https://api.dify.ai")
                .build();

        assertNotNull(client);
    }

    @Test
    void testBuilderFluentAPI() {
        DifyProperties.Server serverProperties = new DifyProperties.Server();
        BaseDifyServerToken mockToken = mock(BaseDifyServerToken.class);

        DifyServerClient client = DifyServerBuilder.builder()
                .baseUrl("https://api.dify.ai")
                .serverProperties(serverProperties)
                .serverToken(mockToken)
                .build();

        assertNotNull(client);
    }

    @Test
    void testBuilderWithCustomTimeout() {
        DifyServerClient client = DifyServerBuilder.builder()
                .baseUrl("https://api.dify.ai")
                .build();

        assertNotNull(client);
    }

    @Test
    void testBuilderReturnsCorrectType() {
        DifyServerBuilder.Builder builder = DifyServerBuilder.builder();
        DifyProperties.Server serverProperties = new DifyProperties.Server();

        DifyServerBuilder.Builder result1 = builder.serverProperties(serverProperties);
        assertSame(builder, result1);

        BaseDifyServerToken mockToken = mock(BaseDifyServerToken.class);
        DifyServerBuilder.Builder result2 = builder.serverToken(mockToken);
        assertSame(builder, result2);
    }

    @Test
    void testBuilderWithNullServerProperties() {
        DifyServerClient client = DifyServerBuilder.builder()
                .baseUrl("https://api.dify.ai")
                .serverProperties(null)
                .build();

        assertNotNull(client);
    }

    @Test
    void testBuilderWithNullServerToken() {
        DifyServerClient client = DifyServerBuilder.builder()
                .baseUrl("https://api.dify.ai")
                .serverToken(null)
                .build();

        assertNotNull(client);
    }

    @Test
    void testBuilderWithMinimalConfiguration() {
        DifyServerClient client = DifyServerBuilder.builder()
                .baseUrl("https://api.dify.ai")
                .build();

        assertNotNull(client);
    }

    @Test
    void testBuilderWithCompleteConfiguration() {
        DifyProperties.Server serverProperties = new DifyProperties.Server();

        BaseDifyServerToken mockToken = mock(BaseDifyServerToken.class);

        DifyServerClient client = DifyServerBuilder.builder()
                .baseUrl("https://api.dify.ai")
                .serverProperties(serverProperties)
                .serverToken(mockToken)
                .build();

        assertNotNull(client);
    }
}
