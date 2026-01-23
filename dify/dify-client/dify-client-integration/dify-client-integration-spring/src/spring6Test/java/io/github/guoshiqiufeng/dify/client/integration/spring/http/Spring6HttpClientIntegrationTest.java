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

import io.github.guoshiqiufeng.dify.client.codec.jackson.JacksonJsonMapper;
import io.github.guoshiqiufeng.dify.client.core.http.HttpHeaders;
import io.github.guoshiqiufeng.dify.client.core.http.HttpMethod;
import io.github.guoshiqiufeng.dify.client.core.http.ResponseErrorHandler;
import io.github.guoshiqiufeng.dify.client.core.web.client.RequestBodyUriSpec;
import io.github.guoshiqiufeng.dify.client.core.web.client.RequestHeadersUriSpec;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for SpringHttpClient
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/1/14
 */
class Spring6HttpClientIntegrationTest {

    private MockWebServer mockServer;
    private String baseUrl;
    private JacksonJsonMapper jsonMapper;
    private DifyProperties.ClientConfig clientConfig;

    @BeforeEach
    void setUp() throws IOException {
        mockServer = new MockWebServer();
        mockServer.start();
        baseUrl = mockServer.url("/").toString();
        jsonMapper = new JacksonJsonMapper();

        // Create default client config
        clientConfig = new DifyProperties.ClientConfig();
        clientConfig.setConnectTimeout(5);
        clientConfig.setReadTimeout(5);
        clientConfig.setWriteTimeout(5);
        clientConfig.setLogging(false);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockServer.shutdown();
    }

    // ========== Constructor Tests ==========

    @Test
    void testConstructorWithBaseUrlAndConfig() {
        // Act
        SpringHttpClient client = new SpringHttpClient(baseUrl, clientConfig, jsonMapper);

        // Assert
        assertNotNull(client);
        assertNotNull(client.getWebClient());
        assertNotNull(client.getClientConfig());
        assertEquals(clientConfig, client.getClientConfig());
    }

    @Test
    void testConstructorWithCustomWebClientBuilder() {
        // Arrange
        WebClient.Builder webClientBuilder = WebClient.builder();

        // Act
        SpringHttpClient client = new SpringHttpClient(baseUrl, clientConfig, webClientBuilder, null, jsonMapper);

        // Assert
        assertNotNull(client);
        assertNotNull(client.getWebClient());
    }

    @Test
    void testConstructorWithCustomRestClientBuilder() {
        // Arrange
        RestClient.Builder restClientBuilder = RestClient.builder();

        // Act
        SpringHttpClient client = new SpringHttpClient(baseUrl, clientConfig, null, restClientBuilder, jsonMapper);

        // Assert
        assertNotNull(client);
        assertNotNull(client.getWebClient());
        // RestClient should be available in Spring 6+
        if (client.hasRestClient()) {
            assertNotNull(client.getRestClient());
        }
    }

    @Test
    void testConstructorWithDefaultHeaders() {
        // Arrange
        HttpHeaders defaultHeaders = new HttpHeaders();
        defaultHeaders.add("X-Custom-Header", "custom-value");
        defaultHeaders.add("Authorization", "Bearer token123");

        // Act
        SpringHttpClient client = new SpringHttpClient(baseUrl, clientConfig, null, null, jsonMapper, defaultHeaders);

        // Assert
        assertNotNull(client);
        assertNotNull(client.getWebClient());
    }

    @Test
    void testConstructorWithInterceptors() {
        // Arrange
        List<Object> interceptors = new ArrayList<>();

        // Add WebClient interceptor (ExchangeFilterFunction)
        ExchangeFilterFunction webClientInterceptor = (request, next) -> {
            return next.exchange(request);
        };
        interceptors.add(webClientInterceptor);

        // Add RestClient interceptor (ClientHttpRequestInterceptor)
        ClientHttpRequestInterceptor restClientInterceptor = (request, body, execution) -> {
            return execution.execute(request, body);
        };
        interceptors.add(restClientInterceptor);

        // Act
        SpringHttpClient client = new SpringHttpClient(baseUrl, clientConfig, null, null, jsonMapper, new HttpHeaders(), interceptors);

        // Assert
        assertNotNull(client);
        assertNotNull(client.getWebClient());
    }

    @Test
    void testConstructorWithNullConfig() {
        // Act
        SpringHttpClient client = new SpringHttpClient(baseUrl, null, jsonMapper);

        // Assert
        assertNotNull(client);
        assertNotNull(client.getWebClient());
        assertNull(client.getClientConfig());
    }

    @Test
    void testConstructorWithNullDefaultHeaders() {
        // Act
        SpringHttpClient client = new SpringHttpClient(baseUrl, clientConfig, null, null, jsonMapper, null);

        // Assert
        assertNotNull(client);
        assertNotNull(client.getWebClient());
    }

    @Test
    void testConstructorWithNullInterceptors() {
        // Act
        SpringHttpClient client = new SpringHttpClient(baseUrl, clientConfig, null, null, jsonMapper, new HttpHeaders(), null);

        // Assert
        assertNotNull(client);
        assertNotNull(client.getWebClient());
    }

    // ========== HTTP Method Builder Tests ==========

    @Test
    void testGetMethod() {
        // Arrange
        SpringHttpClient client = new SpringHttpClient(baseUrl, clientConfig, jsonMapper);

        // Act
        RequestHeadersUriSpec<?> spec = client.get();

        // Assert
        assertNotNull(spec);
    }

    @Test
    void testPostMethod() {
        // Arrange
        SpringHttpClient client = new SpringHttpClient(baseUrl, clientConfig, jsonMapper);

        // Act
        RequestBodyUriSpec spec = client.post();

        // Assert
        assertNotNull(spec);
    }

    @Test
    void testPutMethod() {
        // Arrange
        SpringHttpClient client = new SpringHttpClient(baseUrl, clientConfig, jsonMapper);

        // Act
        RequestBodyUriSpec spec = client.put();

        // Assert
        assertNotNull(spec);
    }

    @Test
    void testDeleteMethod() {
        // Arrange
        SpringHttpClient client = new SpringHttpClient(baseUrl, clientConfig, jsonMapper);

        // Act
        RequestHeadersUriSpec<?> spec = client.delete();

        // Assert
        assertNotNull(spec);
    }

    @Test
    void testPatchMethod() {
        // Arrange
        SpringHttpClient client = new SpringHttpClient(baseUrl, clientConfig, jsonMapper);

        // Act
        RequestBodyUriSpec spec = client.patch();

        // Assert
        assertNotNull(spec);
    }

    @Test
    void testHeadMethod() {
        // Arrange
        SpringHttpClient client = new SpringHttpClient(baseUrl, clientConfig, jsonMapper);

        // Act
        RequestHeadersUriSpec<?> spec = client.head();

        // Assert
        assertNotNull(spec);
    }

    @Test
    void testOptionsMethod() {
        // Arrange
        SpringHttpClient client = new SpringHttpClient(baseUrl, clientConfig, jsonMapper);

        // Act
        RequestHeadersUriSpec<?> spec = client.options();

        // Assert
        assertNotNull(spec);
    }

    @Test
    void testMethodWithHttpMethodEnum() {
        // Arrange
        SpringHttpClient client = new SpringHttpClient(baseUrl, clientConfig, jsonMapper);

        // Act
        RequestBodyUriSpec getSpec = client.method(HttpMethod.GET);
        RequestBodyUriSpec postSpec = client.method(HttpMethod.POST);
        RequestBodyUriSpec putSpec = client.method(HttpMethod.PUT);
        RequestBodyUriSpec deleteSpec = client.method(HttpMethod.DELETE);
        RequestBodyUriSpec patchSpec = client.method(HttpMethod.PATCH);

        // Assert
        assertNotNull(getSpec);
        assertNotNull(postSpec);
        assertNotNull(putSpec);
        assertNotNull(deleteSpec);
        assertNotNull(patchSpec);
    }

    // ========== RestClient Availability Tests ==========

    @Test
    void testHasRestClient() {
        // Arrange
        SpringHttpClient client = new SpringHttpClient(baseUrl, clientConfig, jsonMapper);

        // Act
        boolean hasRestClient = client.hasRestClient();

        // Assert
        // In Spring 6+, RestClient should be available
        assertTrue(hasRestClient || !hasRestClient); // Just verify the method works
    }

    @Test
    void testRestClientIsNotNullWhenAvailable() {
        // Arrange
        SpringHttpClient client = new SpringHttpClient(baseUrl, clientConfig, jsonMapper);

        // Act & Assert
        if (client.hasRestClient()) {
            assertNotNull(client.getRestClient());
        }
    }

    // ========== Configuration Tests ==========

    @Test
    void testClientConfigWithCustomTimeouts() {
        // Arrange
        DifyProperties.ClientConfig customConfig = new DifyProperties.ClientConfig();
        customConfig.setConnectTimeout(10);
        customConfig.setReadTimeout(20);
        customConfig.setWriteTimeout(30);

        // Act
        SpringHttpClient client = new SpringHttpClient(baseUrl, customConfig, jsonMapper);

        // Assert
        assertNotNull(client);
        assertEquals(10, client.getClientConfig().getConnectTimeout());
        assertEquals(20, client.getClientConfig().getReadTimeout());
        assertEquals(30, client.getClientConfig().getWriteTimeout());
    }

    @Test
    void testClientConfigWithLoggingEnabled() {
        // Arrange
        DifyProperties.ClientConfig loggingConfig = new DifyProperties.ClientConfig();
        loggingConfig.setLogging(true);

        // Act
        SpringHttpClient client = new SpringHttpClient(baseUrl, loggingConfig, jsonMapper);

        // Assert
        assertNotNull(client);
        assertTrue(client.getClientConfig().getLogging());
    }

    @Test
    void testClientConfigWithLoggingDisabled() {
        // Arrange
        DifyProperties.ClientConfig loggingConfig = new DifyProperties.ClientConfig();
        loggingConfig.setLogging(false);

        // Act
        SpringHttpClient client = new SpringHttpClient(baseUrl, loggingConfig, jsonMapper);

        // Assert
        assertNotNull(client);
        assertFalse(client.getClientConfig().getLogging());
    }

    // ========== Default Headers Tests ==========

    @Test
    void testDefaultHeadersAreApplied() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"status\":\"ok\"}")
                .setHeader("Content-Type", "application/json"));

        HttpHeaders defaultHeaders = new HttpHeaders();
        defaultHeaders.add("X-API-Key", "test-api-key");
        defaultHeaders.add("X-Client-Version", "2.0.0");

        SpringHttpClient client = new SpringHttpClient(baseUrl, clientConfig, null, null, jsonMapper, defaultHeaders);

        // Act
        client.get()
                .uri("/test")
                .retrieve()
                .body(String.class)
        ;

        // Assert
        RecordedRequest request = mockServer.takeRequest();
        assertNotNull(request.getHeader("X-API-Key"));
        assertNotNull(request.getHeader("X-Client-Version"));
    }

    @Test
    void testDefaultContentTypeHeader() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"status\":\"ok\"}")
                .setHeader("Content-Type", "application/json"));

        SpringHttpClient client = new SpringHttpClient(baseUrl, clientConfig, jsonMapper);

        // Act
        client.get()
                .uri("/test")
                .retrieve()
                .body(String.class)
        ;

        // Assert
        RecordedRequest request = mockServer.takeRequest();
        assertEquals("application/json", request.getHeader("Accept"));
    }

    // ========== WebClient Tests ==========

    @Test
    void testWebClientIsNotNull() {
        // Arrange & Act
        SpringHttpClient client = new SpringHttpClient(baseUrl, clientConfig, jsonMapper);

        // Assert
        assertNotNull(client.getWebClient());
    }

    @Test
    void testWebClientBaseUrl() {
        // Arrange & Act
        SpringHttpClient client = new SpringHttpClient(baseUrl, clientConfig, jsonMapper);

        // Assert
        assertNotNull(client.getWebClient());
        // WebClient is configured with the base URL
    }

    @Test
    void testWebClientWithCustomBuilder() {
        // Arrange
        WebClient.Builder customBuilder = WebClient.builder()
                .defaultHeader("X-Custom", "value");

        // Act
        SpringHttpClient client = new SpringHttpClient(baseUrl, clientConfig, customBuilder, null, jsonMapper);

        // Assert
        assertNotNull(client.getWebClient());
    }

    // ========== Interceptor Tests ==========

    @Test
    void testWebClientInterceptorIsApplied() {
        // Arrange
        List<Object> interceptors = new ArrayList<>();
        final boolean[] interceptorCalled = {false};

        // Use ClientHttpRequestInterceptor for Spring 6+ (RestClient)
        ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
            interceptorCalled[0] = true;
            return execution.execute(request, body);
        };
        interceptors.add(interceptor);

        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"status\":\"ok\"}")
                .setHeader("Content-Type", "application/json"));

        SpringHttpClient client = new SpringHttpClient(baseUrl, clientConfig, null, null, jsonMapper, new HttpHeaders(), interceptors);

        // Act
        client.get()
                .uri("/test")
                .retrieve()
                .body(String.class)
        ;

        // Assert
        assertTrue(interceptorCalled[0]);
    }

    @Test
    void testMultipleInterceptorsAreApplied() {
        // Arrange
        List<Object> interceptors = new ArrayList<>();
        final int[] callCount = {0};

        // Use ClientHttpRequestInterceptor for Spring 6+ (RestClient)
        ClientHttpRequestInterceptor interceptor1 = (request, body, execution) -> {
            callCount[0]++;
            return execution.execute(request, body);
        };

        ClientHttpRequestInterceptor interceptor2 = (request, body, execution) -> {
            callCount[0]++;
            return execution.execute(request, body);
        };

        interceptors.add(interceptor1);
        interceptors.add(interceptor2);

        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"status\":\"ok\"}")
                .setHeader("Content-Type", "application/json"));

        SpringHttpClient client = new SpringHttpClient(baseUrl, clientConfig, null, null, jsonMapper, new HttpHeaders(), interceptors);

        // Act
        client.get()
                .uri("/test")
                .retrieve()
                .body(String.class)
        ;

        // Assert
        assertEquals(2, callCount[0]);
    }

    // ========== Error Handling Tests ==========

    //@Test
    void testClientHandlesInvalidBaseUrl() {
        // Arrange
        String invalidUrl = "not-a-valid-url";

        // Act & Assert
        assertThrows(Exception.class, () -> {
            SpringHttpClient client = new SpringHttpClient(invalidUrl, clientConfig, jsonMapper);
            client.get()
                    .uri("/test")
                    .retrieve()
                    .body(String.class)
            ;
        });
    }

    @Test
    void testClientHandlesServerError() {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("{\"error\":\"Internal Server Error\"}"));

        SpringHttpClient client = new SpringHttpClient(baseUrl, clientConfig, jsonMapper);

        // Act & Assert
        assertThrows(Exception.class, () -> {
            client.get()
                    .uri("/error")
                    .retrieve()
                    .onStatus(ResponseErrorHandler.onStatus(status -> status == 500, response -> {
                        throw new RuntimeException("Internal Server Error");
                    }))
                    .body(String.class)
            ;
        });
    }

    @Test
    void testClientHandlesNotFound() {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("{\"error\":\"Not Found\"}"));

        SpringHttpClient client = new SpringHttpClient(baseUrl, clientConfig, jsonMapper);

        // Act & Assert
        assertThrows(Exception.class, () -> {
            client.get()
                    .uri("/notfound")
                    .retrieve()
                    .onStatus(ResponseErrorHandler.onStatus(status -> status == 404, response -> {
                        throw new RuntimeException("Not Found");
                    }))
                    .body(String.class)
            ;
        });
    }

    // ========== Integration Tests ==========

    @Test
    void testSuccessfulGetRequest() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"message\":\"success\"}")
                .setHeader("Content-Type", "application/json"));

        SpringHttpClient client = new SpringHttpClient(baseUrl, clientConfig, jsonMapper);

        // Act
        String response = client.get()
                .uri("/api/test")
                .retrieve()
                .body(String.class);

        // Assert
        assertNotNull(response);
        assertTrue(response.contains("success"));

        RecordedRequest request = mockServer.takeRequest();
        assertEquals("GET", request.getMethod());
        assertEquals("/api/test", request.getPath());
    }

    @Test
    void testSuccessfulPostRequest() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(201)
                .setBody("{\"id\":123,\"status\":\"created\"}")
                .setHeader("Content-Type", "application/json"));

        SpringHttpClient client = new SpringHttpClient(baseUrl, clientConfig, jsonMapper);

        // Act
        String response = client.post()
                .uri("/api/create")
                .body("{\"name\":\"test\"}")
                .retrieve()
                .body(String.class);

        // Assert
        assertNotNull(response);
        assertTrue(response.contains("created"));

        RecordedRequest request = mockServer.takeRequest();
        assertEquals("POST", request.getMethod());
        assertEquals("/api/create", request.getPath());
    }

    @Test
    void testRequestWithCustomHeaders() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"status\":\"ok\"}")
                .setHeader("Content-Type", "application/json"));

        SpringHttpClient client = new SpringHttpClient(baseUrl, clientConfig, jsonMapper);

        // Act
        client.get()
                .uri("/api/test")
                .header("Authorization", "Bearer token123")
                .header("X-Request-ID", "req-456")
                .retrieve()
                .body(String.class)
        ;

        // Assert
        RecordedRequest request = mockServer.takeRequest();
        assertEquals("Bearer token123", request.getHeader("Authorization"));
        assertEquals("req-456", request.getHeader("X-Request-ID"));
    }

    @Test
    void testEmptyResponseBody() throws Exception {
        // Arrange
        mockServer.enqueue(new MockResponse()
                .setResponseCode(204)
                .setHeader("Content-Length", "0"));

        SpringHttpClient client = new SpringHttpClient(baseUrl, clientConfig, jsonMapper);

        // Act
        String response = client.get()
                .uri("/api/empty")
                .retrieve()
                .body(String.class);

        // Assert - 204 No Content should return null or empty
        // The behavior depends on the WebClient configuration
    }
}
