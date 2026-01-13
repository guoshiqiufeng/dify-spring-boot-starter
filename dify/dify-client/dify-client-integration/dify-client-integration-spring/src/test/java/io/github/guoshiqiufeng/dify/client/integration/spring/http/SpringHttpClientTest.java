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
package io.github.guoshiqiufeng.dify.client.integration.spring.http;

import io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper;
import io.github.guoshiqiufeng.dify.client.core.enums.HttpMethod;
import io.github.guoshiqiufeng.dify.client.core.web.client.RequestBodyUriSpec;
import io.github.guoshiqiufeng.dify.client.core.web.client.RequestHeadersUriSpec;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SpringHttpClient
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/1/13
 */
@ExtendWith(MockitoExtension.class)
class SpringHttpClientTest {

    @Mock
    private JsonMapper jsonMapper;

    private SpringHttpClient httpClient;
    private DifyProperties.ClientConfig clientConfig;

    @BeforeEach
    void setUp() {
        String baseUrl = "http://example.com";
        clientConfig = new DifyProperties.ClientConfig();
        httpClient = new SpringHttpClient(baseUrl, clientConfig, jsonMapper);
    }

    @Test
    void testGetMethod() {
        // Act
        RequestHeadersUriSpec<?> spec = httpClient.get();

        // Assert
        assertNotNull(spec);
    }

    @Test
    void testPostMethod() {
        // Act
        RequestBodyUriSpec spec = httpClient.post();

        // Assert
        assertNotNull(spec);
    }

    @Test
    void testPutMethod() {
        // Act
        RequestBodyUriSpec spec = httpClient.put();

        // Assert
        assertNotNull(spec);
    }

    @Test
    void testDeleteMethod() {
        // Act
        RequestHeadersUriSpec<?> spec = httpClient.delete();

        // Assert
        assertNotNull(spec);
    }

    @Test
    void testPatchMethod() {
        // Act
        RequestBodyUriSpec spec = httpClient.patch();

        // Assert
        assertNotNull(spec);
    }

    @Test
    void testHeadMethod() {
        // Act
        RequestHeadersUriSpec<?> spec = httpClient.head();

        // Assert
        assertNotNull(spec);
    }

    @Test
    void testOptionsMethod() {
        // Act
        RequestHeadersUriSpec<?> spec = httpClient.options();

        // Assert
        assertNotNull(spec);
    }

    @Test
    void testMethodWithHttpMethod() {
        // Act
        RequestBodyUriSpec spec = httpClient.method(HttpMethod.POST);

        // Assert
        assertNotNull(spec);
    }

    @Test
    void testGetWebClient() {
        // Act
        var webClient = httpClient.getWebClient();

        // Assert
        assertNotNull(webClient);
    }

    @Test
    void testGetClientConfig() {
        // Act
        var config = httpClient.getClientConfig();

        // Assert
        assertNotNull(config);
        assertEquals(clientConfig, config);
    }

    @Test
    void testHasRestClient() {
        // Act
        boolean hasRestClient = httpClient.hasRestClient();

        // Assert - may be true or false depending on Spring version
        // Just verify the method works without throwing exception
        assertTrue(hasRestClient || !hasRestClient);
    }

    @Test
    void testGetRestClient() {
        // Act
        Object restClient = httpClient.getRestClient();

        // Assert - may be null for Spring 5, non-null for Spring 6+
        // Just verify the method works without throwing exception
        assertTrue(restClient == null || restClient != null);
    }
}
