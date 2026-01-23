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
import io.github.guoshiqiufeng.dify.workflow.DifyWorkflow;
import io.github.guoshiqiufeng.dify.workflow.client.DifyWorkflowClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DifyWorkflowBuilder
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/01/13
 */
class DifyWorkflowBuilderTest {

    @Test
    void testCreateFromClient() {
        DifyWorkflowClient mockClient = mock(DifyWorkflowClient.class);
        DifyWorkflow workflow = DifyWorkflowBuilder.create(mockClient);

        assertNotNull(workflow);
    }

    @Test
    void testCreateClientFromHttpClient() {
        HttpClient mockHttpClient = mock(HttpClient.class);
        DifyWorkflowClient client = DifyWorkflowBuilder.createClient(mockHttpClient);

        assertNotNull(client);
    }

    @Test
    void testBuilder() {
        DifyWorkflowBuilder.Builder builder = DifyWorkflowBuilder.builder();
        assertNotNull(builder);
    }

    @Test
    void testBuilderBuild() {
        HttpClientFactory mockFactory = mock(HttpClientFactory.class);
        HttpClient mockHttpClient = mock(HttpClient.class);
        when(mockFactory.createClient(anyString(), any())).thenReturn(mockHttpClient);

        DifyWorkflowBuilder.Builder builder = DifyWorkflowBuilder.builder();
        builder.baseUrl("https://api.dify.ai");
        builder.httpClientFactory(mockFactory);

        DifyWorkflowClient client = builder.build();
        assertNotNull(client);
    }

    @Test
    void testBuilderWithCustomTimeout() {
        HttpClientFactory mockFactory = mock(HttpClientFactory.class);
        HttpClient mockHttpClient = mock(HttpClient.class);
        when(mockFactory.createClient(anyString(), any())).thenReturn(mockHttpClient);

        DifyWorkflowBuilder.Builder builder = DifyWorkflowBuilder.builder();
        builder.baseUrl("https://api.dify.ai");
        builder.httpClientFactory(mockFactory);

        DifyWorkflowClient client = builder.build();
        assertNotNull(client);
    }

    @Test
    void testBuilderFluentAPI() {
        HttpClientFactory mockFactory = mock(HttpClientFactory.class);
        HttpClient mockHttpClient = mock(HttpClient.class);
        when(mockFactory.createClient(anyString(), any())).thenReturn(mockHttpClient);

        DifyWorkflowClient client = DifyWorkflowBuilder.builder()
                .baseUrl("https://api.dify.ai")
                .httpClientFactory(mockFactory)
                .build();

        assertNotNull(client);
    }

    @Test
    void testBuilderWithMinimalConfiguration() {
        HttpClientFactory mockFactory = mock(HttpClientFactory.class);
        HttpClient mockHttpClient = mock(HttpClient.class);
        when(mockFactory.createClient(anyString(), any())).thenReturn(mockHttpClient);

        DifyWorkflowClient client = DifyWorkflowBuilder.builder()
                .baseUrl("https://api.dify.ai")
                .httpClientFactory(mockFactory)
                .build();

        assertNotNull(client);
    }
}
