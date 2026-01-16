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

import io.github.guoshiqiufeng.dify.chat.DifyChat;
import io.github.guoshiqiufeng.dify.chat.client.DifyChatClient;
import io.github.guoshiqiufeng.dify.client.core.http.HttpClientFactory;
import io.github.guoshiqiufeng.dify.client.core.web.client.HttpClient;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for DifyChatBuilder
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/01/06
 */
@ExtendWith(MockitoExtension.class)
class DifyChatBuilderTest {

    @Test
    void testCreateFromClient() {
        // Arrange
        DifyChatClient mockClient = mock(DifyChatClient.class);

        // Act
        DifyChat difyChat = DifyChatBuilder.create(mockClient);

        // Assert
        assertNotNull(difyChat);
    }

    @Test
    void testCreateClient() {
        // Arrange
        HttpClient mockHttpClient = mock(HttpClient.class);

        // Act
        DifyChatClient client = DifyChatBuilder.createClient(mockHttpClient);

        // Assert
        assertNotNull(client);
    }

    @Test
    void testBuilder() {
        // Act
        DifyChatBuilder.Builder builder = DifyChatBuilder.builder();

        // Assert
        assertNotNull(builder);
    }

    @Test
    void testBuilderBuild() {
        // Arrange
        HttpClientFactory mockFactory = mock(HttpClientFactory.class);
        HttpClient mockHttpClient = mock(HttpClient.class);
        when(mockFactory.createClient(anyString(), any(DifyProperties.ClientConfig.class)))
                .thenReturn(mockHttpClient);

        // Act
        DifyChatClient client = DifyChatBuilder.builder()
                .httpClientFactory(mockFactory)
                .build();

        // Assert
        assertNotNull(client);
    }

    @Test
    void testBuilderWithBaseUrl() {
        // Arrange
        HttpClientFactory mockFactory = mock(HttpClientFactory.class);
        HttpClient mockHttpClient = mock(HttpClient.class);
        when(mockFactory.createClient(anyString(), any(DifyProperties.ClientConfig.class)))
                .thenReturn(mockHttpClient);

        // Act
        DifyChatClient client = DifyChatBuilder.builder()
                .baseUrl("https://api.dify.ai")
                .httpClientFactory(mockFactory)
                .build();

        // Assert
        assertNotNull(client);
    }

    @Test
    void testBuilderWithClientConfig() {
        // Arrange
        HttpClientFactory mockFactory = mock(HttpClientFactory.class);
        HttpClient mockHttpClient = mock(HttpClient.class);
        when(mockFactory.createClient(anyString(), any(DifyProperties.ClientConfig.class)))
                .thenReturn(mockHttpClient);

        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();

        // Act
        DifyChatClient client = DifyChatBuilder.builder()
                .clientConfig(config)
                .httpClientFactory(mockFactory)
                .build();

        // Assert
        assertNotNull(client);
    }

    @Test
    void testBuilderWithAllParameters() {
        // Arrange
        HttpClientFactory mockFactory = mock(HttpClientFactory.class);
        HttpClient mockHttpClient = mock(HttpClient.class);
        when(mockFactory.createClient(anyString(), any(DifyProperties.ClientConfig.class)))
                .thenReturn(mockHttpClient);

        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();

        // Act
        DifyChatClient client = DifyChatBuilder.builder()
                .baseUrl("https://api.dify.ai")
                .clientConfig(config)
                .httpClientFactory(mockFactory)
                .build();

        // Assert
        assertNotNull(client);
    }

    @Test
    void testBuilderWithoutHttpClientFactory() {
        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            DifyChatBuilder.builder().build();
        });
    }

    @Test
    void testBuilderChaining() {
        // Arrange
        HttpClientFactory mockFactory = mock(HttpClientFactory.class);
        HttpClient mockHttpClient = mock(HttpClient.class);

        // Act
        DifyChatBuilder.Builder builder = DifyChatBuilder.builder();
        DifyChatBuilder.Builder result1 = builder.baseUrl("https://api.dify.ai");
        DifyChatBuilder.Builder result2 = result1.clientConfig(new DifyProperties.ClientConfig());
        DifyChatBuilder.Builder result3 = result2.httpClientFactory(mockFactory);

        // Assert
        assertSame(builder, result1);
        assertSame(builder, result2);
        assertSame(builder, result3);
    }

    @Test
    void testUtilityClassCannotBeInstantiated() throws Exception {
        // Act & Assert
        java.lang.reflect.Constructor<DifyChatBuilder> constructor =
                DifyChatBuilder.class.getDeclaredConstructor();
        assertTrue(java.lang.reflect.Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }
}
