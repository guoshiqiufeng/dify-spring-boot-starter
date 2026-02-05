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
package io.github.guoshiqiufeng.dify.client.integration.okhttp.http;

import io.github.guoshiqiufeng.dify.client.core.codec.JsonMapper;
import io.github.guoshiqiufeng.dify.client.core.http.HttpHeaders;
import io.github.guoshiqiufeng.dify.client.core.http.HttpMethod;
import io.github.guoshiqiufeng.dify.client.core.web.client.RequestBodyUriSpec;
import io.github.guoshiqiufeng.dify.client.core.web.client.RequestHeadersUriSpec;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JavaHttpClient
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/1/13
 */
@ExtendWith(MockitoExtension.class)
class JavaHttpClientTest {

    @Mock
    private JsonMapper jsonMapper;

    private JavaHttpClient httpClient;
    private DifyProperties.ClientConfig clientConfig;

    @BeforeEach
    void setUp() {
        String baseUrl = "http://example.com";
        clientConfig = new DifyProperties.ClientConfig();
        httpClient = new JavaHttpClient(baseUrl, clientConfig, jsonMapper);
    }

    @Test
    void testForInit() {
        String baseUrl = "http://example.com";
        new JavaHttpClient(baseUrl, jsonMapper);
        new JavaHttpClient(baseUrl, new DifyProperties.ClientConfig(), jsonMapper);
        new JavaHttpClient(baseUrl, null, jsonMapper);
        DifyProperties.ClientConfig nullConfig = new DifyProperties.ClientConfig();
        nullConfig.setConnectTimeout(null);
        nullConfig.setReadTimeout(null);
        nullConfig.setWriteTimeout(null);
        nullConfig.setSkipNull(false);
        nullConfig.setLogging(null);
        new JavaHttpClient(baseUrl, nullConfig, jsonMapper);
        DifyProperties.ClientConfig clientConfig1 = new DifyProperties.ClientConfig();
        nullConfig.setLogging(false);
        new JavaHttpClient(baseUrl, clientConfig1, new OkHttpClient.Builder(),
                jsonMapper);
        new JavaHttpClient(baseUrl, new DifyProperties.ClientConfig(), new OkHttpClient.Builder(),
                jsonMapper, new HttpHeaders());
        new JavaHttpClient(baseUrl, new DifyProperties.ClientConfig(), new OkHttpClient.Builder(),
                jsonMapper, null);
        new JavaHttpClient(baseUrl, new DifyProperties.ClientConfig(), new OkHttpClient.Builder(),
                jsonMapper, null, null);
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
    void testGetOkHttpClient() {
        // Act
        var okHttpClient = httpClient.getOkHttpClient();

        // Assert
        assertNotNull(okHttpClient);
    }

    @Test
    void testGetBaseUrl() {
        // Act
        String baseUrl = httpClient.getBaseUrl();

        // Assert
        assertNotNull(baseUrl);
        assertEquals("http://example.com", baseUrl);
    }

    @Test
    void testGetSkipNull() {
        // Act
        Boolean skipNull = httpClient.getSkipNull();

        // Assert
        assertNotNull(skipNull);
        assertTrue(skipNull);
    }

    @Test
    void testConstructorWithDefaultHeaders() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer token");
        headers.add("X-Custom-Header", "value");

        // Act
        JavaHttpClient client = new JavaHttpClient("http://example.com", clientConfig,
                new OkHttpClient.Builder(), jsonMapper, headers);

        // Assert
        assertNotNull(client);
        assertNotNull(client.getOkHttpClient());
        assertEquals("http://example.com", client.getBaseUrl());
    }

    @Test
    void testConstructorWithInterceptors() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        List<Interceptor> interceptors = new ArrayList<>();
        interceptors.add(chain -> chain.proceed(chain.request()));

        // Act
        JavaHttpClient client = new JavaHttpClient("http://example.com", clientConfig,
                new OkHttpClient.Builder(), jsonMapper, headers, interceptors);

        // Assert
        assertNotNull(client);
        assertNotNull(client.getOkHttpClient());
    }

    @Test
    void testConstructorWithNullBuilder() {
        // Act
        JavaHttpClient client = new JavaHttpClient("http://example.com", clientConfig,
                null, jsonMapper, null, null);

        // Assert
        assertNotNull(client);
        assertNotNull(client.getOkHttpClient());
    }

    @Test
    void testConstructorWithEmptyHeaders() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();

        // Act
        JavaHttpClient client = new JavaHttpClient("http://example.com", clientConfig,
                new OkHttpClient.Builder(), jsonMapper, headers);

        // Assert
        assertNotNull(client);
        assertNotNull(client.getOkHttpClient());
    }

    @Test
    void testConstructorWithEmptyInterceptors() {
        // Arrange
        List<Interceptor> interceptors = new ArrayList<>();

        // Act
        JavaHttpClient client = new JavaHttpClient("http://example.com", clientConfig,
                jsonMapper, new HttpHeaders(), interceptors);

        // Assert
        assertNotNull(client);
        assertNotNull(client.getOkHttpClient());
    }

    @Test
    void testConstructorWithCustomTimeouts() {
        // Arrange
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setConnectTimeout(60);
        config.setReadTimeout(90);
        config.setWriteTimeout(120);

        // Act
        JavaHttpClient client = new JavaHttpClient("http://example.com", config, jsonMapper);

        // Assert
        assertNotNull(client);
        OkHttpClient okHttpClient = client.getOkHttpClient();
        assertEquals(60, okHttpClient.connectTimeoutMillis() / 1000);
        assertEquals(90, okHttpClient.readTimeoutMillis() / 1000);
        assertEquals(120, okHttpClient.writeTimeoutMillis() / 1000);
    }

    @Test
    void testConstructorWithLoggingEnabled() {
        // Arrange
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setLogging(true);

        // Act
        JavaHttpClient client = new JavaHttpClient("http://example.com", config, jsonMapper);

        // Assert
        assertNotNull(client);
        assertNotNull(client.getOkHttpClient());
        assertTrue(client.getOkHttpClient().interceptors().size() > 0);
    }

    @Test
    void testConstructorWithLoggingDisabled() {
        // Arrange
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setLogging(false);

        // Act
        JavaHttpClient client = new JavaHttpClient("http://example.com", config, jsonMapper);

        // Assert
        assertNotNull(client);
        assertNotNull(client.getOkHttpClient());
    }

    @Test
    void testConstructorWithSkipNullFalse() {
        // Arrange
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setSkipNull(false);

        // Act
        JavaHttpClient client = new JavaHttpClient("http://example.com", config, jsonMapper);

        // Assert
        assertNotNull(client);
        assertFalse(client.getSkipNull());
    }

    @Test
    void testConstructorWithSkipNullTrue() {
        // Arrange
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setSkipNull(true);

        // Act
        JavaHttpClient client = new JavaHttpClient("http://example.com", config, jsonMapper);

        // Assert
        assertNotNull(client);
        assertTrue(client.getSkipNull());
    }

    @Test
    void testConstructorWithNullClientConfig() {
        // Act
        JavaHttpClient client = new JavaHttpClient("http://example.com", null, jsonMapper);

        // Assert
        assertNotNull(client);
        assertTrue(client.getSkipNull()); // Default should be true
    }

    @Test
    void testMethodWithAllHttpMethods() {
        // Test all HTTP methods
        assertNotNull(httpClient.method(HttpMethod.GET));
        assertNotNull(httpClient.method(HttpMethod.POST));
        assertNotNull(httpClient.method(HttpMethod.PUT));
        assertNotNull(httpClient.method(HttpMethod.DELETE));
        assertNotNull(httpClient.method(HttpMethod.PATCH));
        assertNotNull(httpClient.method(HttpMethod.HEAD));
        assertNotNull(httpClient.method(HttpMethod.OPTIONS));
    }

    @Test
    void testConstructorWithMultipleInterceptors() {
        // Arrange
        List<Interceptor> interceptors = new ArrayList<>();
        interceptors.add(chain -> chain.proceed(chain.request()));
        interceptors.add(chain -> chain.proceed(chain.request()));
        interceptors.add(chain -> chain.proceed(chain.request()));

        // Act
        JavaHttpClient client = new JavaHttpClient("http://example.com", clientConfig,
                jsonMapper, new HttpHeaders(), interceptors);

        // Assert
        assertNotNull(client);
        assertNotNull(client.getOkHttpClient());
    }

    @Test
    void testConstructorWithHeadersContainingMultipleValues() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        headers.add("Accept", "text/plain");
        headers.add("X-Custom", "value1");
        headers.add("X-Custom", "value2");

        // Act
        JavaHttpClient client = new JavaHttpClient("http://example.com", clientConfig,
                new OkHttpClient.Builder(), jsonMapper, headers);

        // Assert
        assertNotNull(client);
        assertNotNull(client.getOkHttpClient());
    }

    @Test
    void testConstructorWithEmptyStringHeader() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Empty", "");
        headers.add("X-Valid", "value");

        // Act
        JavaHttpClient client = new JavaHttpClient("http://example.com", clientConfig,
                new OkHttpClient.Builder(), jsonMapper, headers);

        // Assert
        assertNotNull(client);
        assertNotNull(client.getOkHttpClient());
    }

    @Test
    void testConstructorWithDifferentBaseUrls() {
        // Test with different base URL formats
        assertNotNull(new JavaHttpClient("http://example.com", jsonMapper));
        assertNotNull(new JavaHttpClient("https://example.com", jsonMapper));
        assertNotNull(new JavaHttpClient("http://example.com:8080", jsonMapper));
        assertNotNull(new JavaHttpClient("https://api.example.com/v1", jsonMapper));
    }

    @Test
    void testOkHttpClientConfiguration() {
        // Arrange
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setConnectTimeout(45);
        config.setReadTimeout(60);
        config.setWriteTimeout(75);

        // Act
        JavaHttpClient client = new JavaHttpClient("http://example.com", config, jsonMapper);
        OkHttpClient okHttpClient = client.getOkHttpClient();

        // Assert
        assertNotNull(okHttpClient);
        assertTrue(okHttpClient.connectTimeoutMillis() > 0);
        assertTrue(okHttpClient.readTimeoutMillis() > 0);
        assertTrue(okHttpClient.writeTimeoutMillis() > 0);
    }

    @Test
    void testDefaultTimeoutsWhenConfigIsNull() {
        // Act
        JavaHttpClient client = new JavaHttpClient("http://example.com", null, jsonMapper);
        OkHttpClient okHttpClient = client.getOkHttpClient();

        // Assert
        assertNotNull(okHttpClient);
        assertEquals(30, okHttpClient.connectTimeoutMillis() / 1000);
        assertEquals(30, okHttpClient.readTimeoutMillis() / 1000);
        assertEquals(30, okHttpClient.writeTimeoutMillis() / 1000);
    }

    @Test
    void testInterceptorsAreApplied() {
        // Arrange
        List<Interceptor> interceptors = new ArrayList<>();
        final boolean[] interceptorCalled = {false};
        interceptors.add(chain -> {
            interceptorCalled[0] = true;
            return chain.proceed(chain.request());
        });

        // Act
        JavaHttpClient client = new JavaHttpClient("http://example.com", clientConfig,
                jsonMapper, new HttpHeaders(), interceptors);

        // Assert
        assertNotNull(client);
        assertNotNull(client.getOkHttpClient());
        // Note: Interceptor won't be called until an actual request is made
    }

    @Test
    void testConstructorWithAllParameters() {
        // Arrange
        String baseUrl = "https://api.example.com";
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setConnectTimeout(30);
        config.setReadTimeout(60);
        config.setWriteTimeout(90);
        config.setLogging(true);
        config.setSkipNull(false);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer token");

        List<Interceptor> interceptors = new ArrayList<>();
        interceptors.add(chain -> chain.proceed(chain.request()));

        // Act
        JavaHttpClient client = new JavaHttpClient(baseUrl, config, builder,
                jsonMapper, headers, interceptors);

        // Assert
        assertNotNull(client);
        assertEquals(baseUrl, client.getBaseUrl());
        assertFalse(client.getSkipNull());
        assertNotNull(client.getOkHttpClient());
    }

    @Test
    void testConstructorWithLoggingEnabledAndMaskingExplicitlyTrue() {
        // Arrange - Test explicit masking enabled
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setLogging(true);
        config.setLoggingMaskEnabled(true);

        // Act
        JavaHttpClient client = new JavaHttpClient("http://example.com", config, jsonMapper);

        // Assert
        assertNotNull(client);
        assertNotNull(client.getOkHttpClient());
        assertTrue(client.getOkHttpClient().interceptors().size() > 0);
    }

    @Test
    void testConstructorWithLoggingEnabledAndMaskingExplicitlyFalse() {
        // Arrange - Test explicit masking disabled
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setLogging(true);
        config.setLoggingMaskEnabled(false);

        // Act
        JavaHttpClient client = new JavaHttpClient("http://example.com", config, jsonMapper);

        // Assert
        assertNotNull(client);
        assertNotNull(client.getOkHttpClient());
        assertTrue(client.getOkHttpClient().interceptors().size() > 0);
    }

    @Test
    void testConstructorWithLoggingEnabledAndMaskingNull() {
        // Arrange - Test masking null (should default to true)
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setLogging(true);
        config.setLoggingMaskEnabled(null);

        // Act
        JavaHttpClient client = new JavaHttpClient("http://example.com", config, jsonMapper);

        // Assert
        assertNotNull(client);
        assertNotNull(client.getOkHttpClient());
        assertTrue(client.getOkHttpClient().interceptors().size() > 0);
    }

    @Test
    void testConnectionPoolConfiguration() {
        // Arrange - Test connection pool configuration
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setMaxIdleConnections(10);
        config.setKeepAliveSeconds(600);
        config.setMaxRequests(128);
        config.setMaxRequestsPerHost(10);
        config.setCallTimeout(60);

        // Act
        JavaHttpClient client = new JavaHttpClient("http://example.com", config, jsonMapper);

        // Assert
        assertNotNull(client);
        assertNotNull(client.getOkHttpClient());
        assertNotNull(client.getOkHttpClient().connectionPool());
        assertNotNull(client.getOkHttpClient().dispatcher());
        assertEquals(60000, client.getOkHttpClient().callTimeoutMillis()); // 60 seconds in milliseconds
    }

    @Test
    void testConnectionPoolWithNullConfig() {
        // Arrange - Test with null config (should use defaults)
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setMaxIdleConnections(null);
        config.setKeepAliveSeconds(null);
        config.setMaxRequests(null);
        config.setMaxRequestsPerHost(null);
        config.setCallTimeout(null);

        // Act
        JavaHttpClient client = new JavaHttpClient("http://example.com", config, jsonMapper);

        // Assert
        assertNotNull(client);
        assertNotNull(client.getOkHttpClient());
        assertNotNull(client.getOkHttpClient().connectionPool());
        assertNotNull(client.getOkHttpClient().dispatcher());
    }

    @Test
    void testConnectionPoolWithZeroCallTimeout() {
        // Arrange - Test with call timeout = 0 (should not set timeout)
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setCallTimeout(0);

        // Act
        JavaHttpClient client = new JavaHttpClient("http://example.com", config, jsonMapper);

        // Assert
        assertNotNull(client);
        assertNotNull(client.getOkHttpClient());
        assertEquals(0, client.getOkHttpClient().callTimeoutMillis());
    }

    @Test
    void testLoggingInterceptorWithNewParameters() {
        // Arrange - Test logging interceptor with new parameters
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setLogging(true);
        config.setLoggingMaskEnabled(true);
        config.setLogBodyMaxBytes(8192);
        config.setLogBinaryBody(false);

        // Act
        JavaHttpClient client = new JavaHttpClient("http://example.com", config, jsonMapper);

        // Assert
        assertNotNull(client);
        assertNotNull(client.getOkHttpClient());
        assertTrue(client.getOkHttpClient().interceptors().size() > 0);
    }

    @Test
    void testLoggingInterceptorWithNullParameters() {
        // Arrange - Test logging interceptor with null parameters (should use defaults)
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setLogging(true);
        config.setLoggingMaskEnabled(null);
        config.setLogBodyMaxBytes(null);
        config.setLogBinaryBody(null);

        // Act
        JavaHttpClient client = new JavaHttpClient("http://example.com", config, jsonMapper);

        // Assert
        assertNotNull(client);
        assertNotNull(client.getOkHttpClient());
        assertTrue(client.getOkHttpClient().interceptors().size() > 0);
    }

    @Test
    void testHighConcurrencyConfiguration() {
        // Arrange - Test high concurrency configuration
        DifyProperties.ClientConfig config = new DifyProperties.ClientConfig();
        config.setMaxIdleConnections(20);
        config.setKeepAliveSeconds(300);
        config.setMaxRequests(256);
        config.setMaxRequestsPerHost(20);
        config.setCallTimeout(60);

        // Act
        JavaHttpClient client = new JavaHttpClient("http://example.com", config, jsonMapper);

        // Assert
        assertNotNull(client);
        assertNotNull(client.getOkHttpClient());
        assertNotNull(client.getOkHttpClient().connectionPool());
        assertNotNull(client.getOkHttpClient().dispatcher());
    }
}
