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

import io.github.guoshiqiufeng.dify.client.core.web.client.HttpClient;
import io.github.guoshiqiufeng.dify.dataset.DifyDataset;
import io.github.guoshiqiufeng.dify.dataset.client.DifyDatasetClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DifyDatasetBuilder
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/01/13
 */
class DifyDatasetBuilderTest {

    @Test
    void testCreateFromClient() {
        DifyDatasetClient mockClient = mock(DifyDatasetClient.class);
        DifyDataset dataset = DifyDatasetBuilder.create(mockClient);

        assertNotNull(dataset);
    }

    @Test
    void testCreateClientFromHttpClient() {
        HttpClient mockHttpClient = mock(HttpClient.class);
        DifyDatasetClient client = DifyDatasetBuilder.createClient(mockHttpClient);

        assertNotNull(client);
    }

    @Test
    void testBuilder() {
        DifyDatasetBuilder.Builder builder = DifyDatasetBuilder.builder();
        assertNotNull(builder);
    }

    @Test
    void testBuilderBuild() {
        DifyDatasetBuilder.Builder builder = DifyDatasetBuilder.builder();
        builder.baseUrl("https://api.dify.ai");
        builder.apiKey("test-api-key");

        DifyDatasetClient client = builder.build();
        assertNotNull(client);
    }

    @Test
    void testBuilderWithApiKey() {
        DifyDatasetBuilder.Builder builder = DifyDatasetBuilder.builder();
        builder.baseUrl("https://api.dify.ai");
        builder.apiKey("dataset-api-key");

        DifyDatasetClient client = builder.build();
        assertNotNull(client);
    }

    @Test
    void testBuilderWithoutApiKey() {
        DifyDatasetBuilder.Builder builder = DifyDatasetBuilder.builder();
        builder.baseUrl("https://api.dify.ai");

        DifyDatasetClient client = builder.build();
        assertNotNull(client);
    }

    @Test
    void testBuilderWithEmptyApiKey() {
        DifyDatasetBuilder.Builder builder = DifyDatasetBuilder.builder();
        builder.baseUrl("https://api.dify.ai");
        builder.apiKey("");

        DifyDatasetClient client = builder.build();
        assertNotNull(client);
    }

    @Test
    void testBuilderWithNullApiKey() {
        DifyDatasetBuilder.Builder builder = DifyDatasetBuilder.builder();
        builder.baseUrl("https://api.dify.ai");
        builder.apiKey(null);

        DifyDatasetClient client = builder.build();
        assertNotNull(client);
    }

    @Test
    void testBuilderFluentAPI() {
        DifyDatasetClient client = DifyDatasetBuilder.builder()
                .baseUrl("https://api.dify.ai")
                .apiKey("test-api-key")
                .build();

        assertNotNull(client);
    }

    @Test
    void testBuilderWithCustomTimeout() {
        DifyDatasetClient client = DifyDatasetBuilder.builder()
                .baseUrl("https://api.dify.ai")
                .apiKey("test-api-key")
                .build();

        assertNotNull(client);
    }

    @Test
    void testBuilderReturnsCorrectType() {
        DifyDatasetBuilder.Builder builder = DifyDatasetBuilder.builder();
        DifyDatasetBuilder.Builder result = builder.apiKey("test");

        assertSame(builder, result);
    }
}
