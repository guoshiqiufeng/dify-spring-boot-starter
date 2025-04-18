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
package io.github.guoshiqiufeng.dify.client.spring5.builder;

import io.github.guoshiqiufeng.dify.chat.DifyChat;
import io.github.guoshiqiufeng.dify.chat.client.DifyChatClient;
import io.github.guoshiqiufeng.dify.client.spring5.chat.DifyChatDefaultClient;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author yanghq
 * @version 0.9.0
 * @since 2025/4/18 10:29
 */
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DifyChatBuilderTest {

    @Test
    @Order(1)
    @DisplayName("Test create with default client")
    public void testCreateWithDefaultClient() {
        DifyChat difyChat = DifyChatBuilder.create(
                DifyChatBuilder.DifyChatClientBuilder
                        .create());

        assertNotNull(difyChat, "DifyChat should not be null");
    }

    @Test
    @Order(2)
    @DisplayName("Test create with builder - empty build")
    public void testCreateWithEmptyBuilder() {
        DifyChatClient client = DifyChatBuilder.DifyChatClientBuilder
                .builder()
                .build();

        assertNotNull(client, "DifyChatClient should not be null");
        assertInstanceOf(DifyChatDefaultClient.class, client, "Client should be instance of DifyChatDefaultClient");
    }

    @Test
    @Order(3)
    @DisplayName("Test create with default client by baseUrl")
    public void testCreateWithDefaultClientByBaseUrl() {
        DifyChat difyChat = DifyChatBuilder.create(
                DifyChatBuilder.DifyChatClientBuilder
                        .create("https://custom-dify-api.example.com"));

        assertNotNull(difyChat, "DifyChat should not be null");
    }

    @Test
    @Order(10)
    @DisplayName("Test create with builder - set baseUrl")
    public void testCreateWithBaseUrl() {
        String baseUrl = "https://custom-dify-api.example.com";

        DifyChatClient client = DifyChatBuilder.DifyChatClientBuilder
                .builder()
                .baseUrl(baseUrl)
                .build();

        assertNotNull(client, "DifyChatClient should not be null");
    }

    @Test
    @Order(11)
    @DisplayName("Test create with builder - set clientConfig")
    public void testCreateWithClientConfig() {
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        clientConfig.setSkipNull(false);

        DifyChatClient client = DifyChatBuilder.DifyChatClientBuilder
                .builder()
                .clientConfig(clientConfig)
                .build();

        assertNotNull(client, "DifyChatClient should not be null");
    }

    @Test
    @Order(13)
    @DisplayName("Test create with builder - set webClientBuilder")
    public void testCreateWithWebClientBuilder() {
        WebClient.Builder webClientBuilder = WebClient.builder();

        DifyChatClient client = DifyChatBuilder.DifyChatClientBuilder
                .builder()
                .webClientBuilder(webClientBuilder)
                .build();

        assertNotNull(client, "DifyChatClient should not be null");
    }

    @Test
    @Order(14)
    @DisplayName("Test create with builder - set all parameters")
    public void testCreateWithAllParameters() {
        String baseUrl = "https://custom-dify-api.example.com";
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        clientConfig.setSkipNull(false);
        WebClient.Builder webClientBuilder = WebClient.builder();

        DifyChatClient client = DifyChatBuilder.DifyChatClientBuilder
                .builder()
                .baseUrl(baseUrl)
                .clientConfig(clientConfig)
                .webClientBuilder(webClientBuilder)
                .build();

        assertNotNull(client, "DifyChatClient should not be null");
    }

    @Test
    @Order(15)
    @DisplayName("Test create DifyChat with full builder chain")
    public void testCreateDifyChatWithFullBuilderChain() {
        DifyChat difyChat = DifyChatBuilder.create(
                DifyChatBuilder.DifyChatClientBuilder
                        .builder()
                        .baseUrl("https://custom-dify-api.example.com")
                        .clientConfig(new DifyProperties.ClientConfig())
                        .webClientBuilder(WebClient.builder())
                        .build());

        assertNotNull(difyChat, "DifyChat should not be null");
    }
}
