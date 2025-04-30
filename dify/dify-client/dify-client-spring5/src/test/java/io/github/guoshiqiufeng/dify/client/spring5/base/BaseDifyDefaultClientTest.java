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
package io.github.guoshiqiufeng.dify.client.spring5.base;

import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link BaseDifyDefaultClient}.
 *
 * @author yanghq
 * @version 0.9.0
 * @since 2025/4/21 12:00
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BaseDifyDefaultClientTest {

    // Concrete implementation for testing
    static class TestBaseDifyDefaultClient extends BaseDifyDefaultClient {
        public TestBaseDifyDefaultClient() {
            super();
        }

        public TestBaseDifyDefaultClient(String baseUrl) {
            super(baseUrl);
        }

        public TestBaseDifyDefaultClient(DifyProperties.ClientConfig clientConfig) {
            super(clientConfig);
        }

        public TestBaseDifyDefaultClient(String baseUrl, DifyProperties.ClientConfig clientConfig) {
            super(baseUrl, clientConfig);
        }

        public TestBaseDifyDefaultClient(String baseUrl, DifyProperties.ClientConfig clientConfig, WebClient.Builder webClientBuilder) {
            super(baseUrl, clientConfig, webClientBuilder);
        }

        public TestBaseDifyDefaultClient(String baseUrl, DifyProperties.ClientConfig clientConfig, WebClient.Builder webClientBuilder, ResponseErrorHandler responseErrorHandler) {
            super(baseUrl, clientConfig, webClientBuilder, responseErrorHandler);
        }

        // Expose protected fields for testing
        public ResponseErrorHandler getResponseErrorHandler() {
            return responseErrorHandler;
        }

    }

    @Test
    @DisplayName("Test default constructor")
    public void testDefaultConstructor() {
        TestBaseDifyDefaultClient client = new TestBaseDifyDefaultClient();

        assertNotNull(client.getResponseErrorHandler(), "ResponseErrorHandler should not be null");
        assertNotNull(client.getWebClient(), "WebClient should not be null");
    }

    @Test
    @DisplayName("Test constructor with baseUrl")
    public void testConstructorWithBaseUrl() {
        String baseUrl = "https://custom-dify-api.example.com";
        TestBaseDifyDefaultClient client = new TestBaseDifyDefaultClient(baseUrl);

        assertNotNull(client.getResponseErrorHandler(), "ResponseErrorHandler should not be null");
        assertNotNull(client.getWebClient(), "WebClient should not be null");
    }

    @Test
    @DisplayName("Test constructor with null clientConfig")
    public void testConstructorWithNullClientConfig() {
        TestBaseDifyDefaultClient client = new TestBaseDifyDefaultClient("", null);

        assertNotNull(client.getResponseErrorHandler(), "ResponseErrorHandler should not be null");
        assertNotNull(client.getWebClient(), "WebClient should not be null");
    }

    @Test
    @DisplayName("Test constructor with clientConfig")
    public void testConstructorWithClientConfig() {
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        clientConfig.setSkipNull(false);

        TestBaseDifyDefaultClient client = new TestBaseDifyDefaultClient(clientConfig);

        assertNotNull(client.getResponseErrorHandler(), "ResponseErrorHandler should not be null");
        assertNotNull(client.getWebClient(), "WebClient should not be null");
    }

    @Test
    @DisplayName("Test constructor with baseUrl and clientConfig")
    public void testConstructorWithBaseUrlAndClientConfig() {
        String baseUrl = "https://custom-dify-api.example.com";
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        clientConfig.setSkipNull(false);

        TestBaseDifyDefaultClient client = new TestBaseDifyDefaultClient(baseUrl, clientConfig);

        assertNotNull(client.getResponseErrorHandler(), "ResponseErrorHandler should not be null");
        assertNotNull(client.getWebClient(), "WebClient should not be null");
    }

    @Test
    @DisplayName("Test constructor with baseUrl, clientConfig, and webClientBuilder")
    public void testConstructorWithBaseUrlClientConfigAndWebClientBuilder() {
        String baseUrl = "https://custom-dify-api.example.com";
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        clientConfig.setSkipNull(false);
        WebClient.Builder webClientBuilder = WebClient.builder();

        TestBaseDifyDefaultClient client = new TestBaseDifyDefaultClient(baseUrl, clientConfig, webClientBuilder);

        assertNotNull(client.getResponseErrorHandler(), "ResponseErrorHandler should not be null");
        assertNotNull(client.getWebClient(), "WebClient should not be null");
    }

    @Test
    @DisplayName("Test constructor with all parameters")
    public void testConstructorWithAllParameters() {
        String baseUrl = "https://custom-dify-api.example.com";
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        clientConfig.setSkipNull(false);
        WebClient.Builder webClientBuilder = WebClient.builder();
        ResponseErrorHandler responseErrorHandler = mock(ResponseErrorHandler.class);

        TestBaseDifyDefaultClient client = new TestBaseDifyDefaultClient(baseUrl, clientConfig, webClientBuilder, responseErrorHandler);

        assertNotNull(client.getResponseErrorHandler(), "ResponseErrorHandler should not be null");
        assertNotNull(client.getWebClient(), "WebClient should not be null");
        assertSame(responseErrorHandler, client.getResponseErrorHandler(), "ResponseErrorHandler should be the same instance");
    }

    @Test
    @DisplayName("Test DifyResponseErrorHandler hasError method")
    public void testDifyResponseErrorHandlerHasError() throws IOException {
        // Create a mock ClientHttpResponse
        ClientHttpResponse response = mock(ClientHttpResponse.class);

        // Test with error status
        when(response.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);

        // Create a client to get the response error handler
        TestBaseDifyDefaultClient client = new TestBaseDifyDefaultClient();
        ResponseErrorHandler errorHandler = client.getResponseErrorHandler();

        // Verify hasError returns true for error status
        assertTrue(errorHandler.hasError(response), "hasError should return true for error status");

        // Test with success status
        when(response.getStatusCode()).thenReturn(HttpStatus.OK);

        // Verify hasError returns false for success status
        assertFalse(errorHandler.hasError(response), "hasError should return false for success status");
    }

    @Test
    @DisplayName("Test DifyResponseErrorHandler handleError method")
    public void testDifyResponseErrorHandlerHandleError() throws IOException {
        // Create a mock ClientHttpResponse
        ClientHttpResponse response = mock(ClientHttpResponse.class);
        when(response.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(response.getStatusText()).thenReturn("Bad Request");
        when(response.getBody()).thenReturn(new ByteArrayInputStream("Error message".getBytes(StandardCharsets.UTF_8)));

        // Create a client to get the response error handler
        TestBaseDifyDefaultClient client = new TestBaseDifyDefaultClient();
        ResponseErrorHandler errorHandler = client.getResponseErrorHandler();

        // Verify handleError(ClientHttpResponse) doesn't throw an exception
        assertDoesNotThrow(() -> errorHandler.handleError(response),
                "handleError(ClientHttpResponse) should not throw an exception");

        // Verify handleError(URI, HttpMethod, ClientHttpResponse) throws a RuntimeException
        URI uri = URI.create("https://example.com/api");
        HttpMethod method = HttpMethod.POST;

        Exception exception = assertThrows(RuntimeException.class,
                () -> errorHandler.handleError(uri, method, response),
                "handleError(URI, HttpMethod, ClientHttpResponse) should throw a RuntimeException");

        // Verify the exception message contains the status code, status text, and error message
        String exceptionMessage = exception.getMessage();
        assertTrue(exceptionMessage.contains("400"), "Exception message should contain status code");
        assertTrue(exceptionMessage.contains("Bad Request"), "Exception message should contain status text");
        assertTrue(exceptionMessage.contains("Error message"), "Exception message should contain error message");
    }
}
