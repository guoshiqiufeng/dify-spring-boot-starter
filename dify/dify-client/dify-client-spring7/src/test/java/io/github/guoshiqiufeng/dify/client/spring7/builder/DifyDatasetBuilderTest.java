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
package io.github.guoshiqiufeng.dify.client.spring7.builder;

import io.github.guoshiqiufeng.dify.client.spring7.dataset.DifyDatasetDefaultClient;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.dataset.DifyDataset;
import io.github.guoshiqiufeng.dify.dataset.client.DifyDatasetClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for {@link DifyDatasetBuilder}.
 *
 * @author yanghq
 * @version 0.9.0
 * @since 2025/4/18 10:32
 */
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DifyDatasetBuilderTest {

    @Test
    @Order(1)
    @DisplayName("Test create with default client")
    public void testCreateWithDefaultClient() {
        DifyDataset difyDataset = DifyDatasetBuilder.create(
                DifyDatasetBuilder.DifyDatasetClientBuilder
                        .create());

        assertNotNull(difyDataset, "DifyDataset should not be null");
    }

    @Test
    @Order(2)
    @DisplayName("Test create with default client by baseUrl")
    public void testCreateWithDefaultClientByBaseUrl() {
        DifyDataset difyDataset = DifyDatasetBuilder.create(
                DifyDatasetBuilder.DifyDatasetClientBuilder
                        .create("https://custom-dify-api.example.com"));

        assertNotNull(difyDataset, "DifyDataset should not be null");
    }

    @Test
    @Order(3)
    @DisplayName("Test create with builder - empty build")
    public void testCreateWithEmptyBuilder() {
        DifyDatasetClient client = DifyDatasetBuilder.DifyDatasetClientBuilder
                .builder()
                .build();

        assertNotNull(client, "DifyDatasetClient should not be null");
        assertInstanceOf(DifyDatasetDefaultClient.class, client, "Client should be instance of DifyDatasetDefaultClient");
    }

    @Test
    @Order(4)
    @DisplayName("Test create with builder - set baseUrl")
    public void testCreateWithBaseUrl() {
        String baseUrl = "https://custom-dify-api.example.com";

        DifyDatasetClient client = DifyDatasetBuilder.DifyDatasetClientBuilder
                .builder()
                .baseUrl(baseUrl)
                .build();

        assertNotNull(client, "DifyDatasetClient should not be null");
    }

    @Test
    @Order(5)
    @DisplayName("Test create with builder - set clientConfig")
    public void testCreateWithClientConfig() {
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        clientConfig.setSkipNull(false);

        DifyDatasetClient client = DifyDatasetBuilder.DifyDatasetClientBuilder
                .builder()
                .clientConfig(clientConfig)
                .build();

        assertNotNull(client, "DifyDatasetClient should not be null");
    }

    @Test
    @Order(6)
    @DisplayName("Test create with builder - set restClientBuilder")
    public void testCreateWithRestClientBuilder() {
        RestClient.Builder restClientBuilder = RestClient.builder();

        DifyDatasetClient client = DifyDatasetBuilder.DifyDatasetClientBuilder
                .builder()
                .restClientBuilder(restClientBuilder)
                .build();

        assertNotNull(client, "DifyDatasetClient should not be null");
    }

    @Test
    @Order(7)
    @DisplayName("Test create with builder - set webClientBuilder")
    public void testCreateWithWebClientBuilder() {
        WebClient.Builder webClientBuilder = WebClient.builder();

        DifyDatasetClient client = DifyDatasetBuilder.DifyDatasetClientBuilder
                .builder()
                .webClientBuilder(webClientBuilder)
                .build();

        assertNotNull(client, "DifyDatasetClient should not be null");
    }

    @Test
    @Order(8)
    @DisplayName("Test create with builder - set all parameters")
    public void testCreateWithAllParameters() {
        String baseUrl = "https://custom-dify-api.example.com";
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        clientConfig.setSkipNull(false);
        RestClient.Builder restClientBuilder = RestClient.builder();
        WebClient.Builder webClientBuilder = WebClient.builder();

        DifyDatasetClient client = DifyDatasetBuilder.DifyDatasetClientBuilder
                .builder()
                .baseUrl(baseUrl)
                .clientConfig(clientConfig)
                .restClientBuilder(restClientBuilder)
                .webClientBuilder(webClientBuilder)
                .build();

        assertNotNull(client, "DifyDatasetClient should not be null");
    }

    @Test
    @Order(9)
    @DisplayName("Test create DifyDataset with full builder chain")
    public void testCreateDifyDatasetWithFullBuilderChain() {
        DifyDataset difyDataset = DifyDatasetBuilder.create(
                DifyDatasetBuilder.DifyDatasetClientBuilder
                        .builder()
                        .baseUrl("https://custom-dify-api.example.com")
                        .clientConfig(new DifyProperties.ClientConfig())
                        .restClientBuilder(RestClient.builder())
                        .webClientBuilder(WebClient.builder())
                        .build());

        assertNotNull(difyDataset, "DifyDataset should not be null");
    }

    @Test
    @Order(10)
    @DisplayName("Test Customized Tokens")
    public void testCustomizedTokens() {
        String apiKey = "aa";
        DifyDataset difyDataset = DifyDatasetBuilder.create(
                DifyDatasetBuilder.DifyDatasetClientBuilder
                        .builder()
                        .baseUrl("https://custom-dify-api.example.com")
                        .clientConfig(new DifyProperties.ClientConfig())
                        .restClientBuilder(RestClient.builder().defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey))
                        .webClientBuilder(WebClient.builder().defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey))
                        .build());

        assertNotNull(difyDataset, "DifyDataset should not be null");
    }

    @Test
    @Order(11)
    @DisplayName("Test Customized Tokens By ApiKey")
    public void testCustomizedTokensApiKey() {
        String apiKey = "aa";
        DifyDataset difyDataset = DifyDatasetBuilder.create(
                DifyDatasetBuilder.DifyDatasetClientBuilder
                        .builder()
                        .baseUrl("https://custom-dify-api.example.com")
                        .apiKey(apiKey)
                        .clientConfig(new DifyProperties.ClientConfig())
                        .restClientBuilder(RestClient.builder())
                        .webClientBuilder(WebClient.builder())
                        .build());

        assertNotNull(difyDataset, "DifyDataset should not be null");
    }
}
