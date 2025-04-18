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

import io.github.guoshiqiufeng.dify.client.spring5.server.DifyServerDefaultClient;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.server.DifyServer;
import io.github.guoshiqiufeng.dify.server.client.BaseDifyServerToken;
import io.github.guoshiqiufeng.dify.server.client.DifyServerClient;
import io.github.guoshiqiufeng.dify.server.client.DifyServerTokenDefault;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author yanghq
 * @version 0.9.0
 * @since 2025/4/18 10:49
 */
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DifyServerBuilderTest {

    @Test
    @Order(1)
    @DisplayName("Test create with properties")
    public void testCreateWithProperties() {
        DifyProperties.Server serverProperties = new DifyProperties.Server();

        DifyServer difyServer = DifyServerBuilder.create(
                DifyServerBuilder.DifyServerClientBuilder
                        .create(serverProperties));

        assertNotNull(difyServer, "DifyServer should not be null");
    }

    @Test
    @Order(2)
    @DisplayName("Test create with properties and baseUrl")
    public void testCreateWithPropertiesAndBaseUrl() {
        DifyProperties.Server serverProperties = new DifyProperties.Server();

        DifyServer difyServer = DifyServerBuilder.create(
                DifyServerBuilder.DifyServerClientBuilder
                        .create(serverProperties, "https://custom-dify-api.example.com"));

        assertNotNull(difyServer, "DifyServer should not be null");
    }

    @Test
    @Order(3)
    @DisplayName("Test create with builder - empty build")
    public void testCreateWithEmptyBuilder() {
        DifyServerClient client = DifyServerBuilder.DifyServerClientBuilder
                .builder()
                .build();

        assertNotNull(client, "DifyServerClient should not be null");
        assertInstanceOf(DifyServerDefaultClient.class, client, "Client should be instance of DifyServerDefaultClient");
    }

    @Test
    @Order(4)
    @DisplayName("Test create with builder - set baseUrl")
    public void testCreateWithBaseUrl() {
        String baseUrl = "https://custom-dify-api.example.com";

        DifyServerClient client = DifyServerBuilder.DifyServerClientBuilder
                .builder()
                .baseUrl(baseUrl)
                .build();

        assertNotNull(client, "DifyServerClient should not be null");
    }

    @Test
    @Order(5)
    @DisplayName("Test create with builder - set serverProperties")
    public void testCreateWithServerProperties() {
        DifyServerClient client = DifyServerBuilder.DifyServerClientBuilder
                .builder()
                .serverProperties(new DifyProperties.Server("admin@admin.com", "admin123456"))
                .build();

        assertNotNull(client, "DifyServerClient should not be null");
    }

    @Test
    @Order(6)
    @DisplayName("Test create with builder - set serverToken")
    public void testCreateWithServerToken() {
        BaseDifyServerToken serverToken = new DifyServerTokenDefault();

        DifyServerClient client = DifyServerBuilder.DifyServerClientBuilder
                .builder()
                .serverToken(serverToken)
                .build();

        assertNotNull(client, "DifyServerClient should not be null");
    }

    @Test
    @Order(7)
    @DisplayName("Test create with builder - set clientConfig")
    public void testCreateWithClientConfig() {
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        clientConfig.setSkipNull(false);

        DifyServerClient client = DifyServerBuilder.DifyServerClientBuilder
                .builder()
                .clientConfig(clientConfig)
                .build();

        assertNotNull(client, "DifyServerClient should not be null");
    }

    @Test
    @Order(9)
    @DisplayName("Test create with builder - set webClientBuilder")
    public void testCreateWithWebClientBuilder() {
        WebClient.Builder webClientBuilder = WebClient.builder();

        DifyServerClient client = DifyServerBuilder.DifyServerClientBuilder
                .builder()
                .webClientBuilder(webClientBuilder)
                .build();

        assertNotNull(client, "DifyServerClient should not be null");
    }

    @Test
    @Order(10)
    @DisplayName("Test create with builder - set all parameters")
    public void testCreateWithAllParameters() {
        String baseUrl = "https://custom-dify-api.example.com";

        BaseDifyServerToken serverToken = new DifyServerTokenDefault();
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        clientConfig.setSkipNull(false);
        WebClient.Builder webClientBuilder = WebClient.builder();

        DifyServerClient client = DifyServerBuilder.DifyServerClientBuilder
                .builder()
                .baseUrl(baseUrl)
                .serverProperties(new DifyProperties.Server("admin@admin.com", "admin123456"))
                .serverToken(serverToken)
                .clientConfig(clientConfig)
                .webClientBuilder(webClientBuilder)
                .build();

        assertNotNull(client, "DifyServerClient should not be null");
    }

    @Test
    @Order(11)
    @DisplayName("Test create DifyServer with full builder chain")
    public void testCreateDifyServerWithFullBuilderChain() {

        DifyServer difyServer = DifyServerBuilder.create(
                DifyServerBuilder.DifyServerClientBuilder
                        .builder()
                        .baseUrl("https://custom-dify-api.example.com")
                        .serverProperties(new DifyProperties.Server("admin@admin.com", "admin123456"))
                        .serverToken(new DifyServerTokenDefault())
                        .clientConfig(new DifyProperties.ClientConfig())
                        .webClientBuilder(WebClient.builder())
                        .build());

        assertNotNull(difyServer, "DifyServer should not be null");
    }
}
