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
package io.github.guoshiqiufeng.dify.client.spring6.base;

import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author yanghq
 * @version 0.8.0
 * @since 2025/4/21 14:40
 */
public class BaseDifyDefaultClientTest {

    @Test
    @DisplayName("Test BaseDifyDefaultClient constructor with default parameters")
    public void testConstructorWithDefaults() {
        // Create a concrete subclass for testing since BaseDifyDefaultClient is abstract
        BaseDifyDefaultClient client = new BaseDifyDefaultClient() {
            // No need to implement any methods as we are just testing the constructor
        };

        // Verify that the client was created successfully
        assertNotNull(client);
        assertNotNull(client.responseErrorHandler);
        assertNotNull(client.restClient);
        assertNotNull(client.webClient);
    }

    @Test
    @DisplayName("Test BaseDifyDefaultClient constructor with custom baseUrl")
    public void testConstructorWithCustomBaseUrl() {
        String customBaseUrl = "https://custom-api.dify.ai";

        // Create a concrete subclass with a custom baseUrl
        BaseDifyDefaultClient client = new BaseDifyDefaultClient(customBaseUrl) {
            // No need to implement any methods
        };

        // Verify that the client was created successfully
        assertNotNull(client);
        assertNotNull(client.responseErrorHandler);
        assertNotNull(client.restClient);
        assertNotNull(client.webClient);
    }

    @Test
    @DisplayName("Test BaseDifyDefaultClient constructor with custom client config")
    public void testConstructorWithCustomClientConfig() {
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        clientConfig.setSkipNull(true);

        // Create a concrete subclass with a custom client config
        BaseDifyDefaultClient client = new BaseDifyDefaultClient(clientConfig) {
            // No need to implement any methods
        };

        // Verify that the client was created successfully
        assertNotNull(client);
        assertNotNull(client.responseErrorHandler);
        assertNotNull(client.restClient);
        assertNotNull(client.webClient);
    }

    @Test
    @DisplayName("Test BaseDifyDefaultClient constructor with custom baseUrl and client config")
    public void testConstructorWithCustomBaseUrlAndClientConfig() {
        String customBaseUrl = "https://custom-api.dify.ai";
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        clientConfig.setSkipNull(true);

        // Create a concrete subclass with a custom baseUrl and client config
        BaseDifyDefaultClient client = new BaseDifyDefaultClient(customBaseUrl, clientConfig) {
            // No need to implement any methods
        };

        // Verify that the client was created successfully
        assertNotNull(client);
        assertNotNull(client.responseErrorHandler);
        assertNotNull(client.restClient);
        assertNotNull(client.webClient);
    }

    @Test
    @DisplayName("Test BaseDifyDefaultClient constructor with all custom parameters")
    public void testConstructorWithAllCustomParameters() {
        String customBaseUrl = "https://custom-api.dify.ai";
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        clientConfig.setSkipNull(true);

        // Create mocks
        RestClient.Builder mockRestClientBuilder = Mockito.mock(RestClient.Builder.class);
        WebClient.Builder mockWebClientBuilder = Mockito.mock(WebClient.Builder.class);
        RestClient mockRestClient = Mockito.mock(RestClient.class);
        WebClient mockWebClient = Mockito.mock(WebClient.class);

        // Set up behavior
        when(mockRestClientBuilder.baseUrl(anyString())).thenReturn(mockRestClientBuilder);
        when(mockRestClientBuilder.defaultHeaders(any(Consumer.class))).thenReturn(mockRestClientBuilder);
        when(mockRestClientBuilder.build()).thenReturn(mockRestClient);

        when(mockWebClientBuilder.baseUrl(anyString())).thenReturn(mockWebClientBuilder);
        when(mockWebClientBuilder.defaultHeaders(any(Consumer.class))).thenReturn(mockWebClientBuilder);
        when(mockWebClientBuilder.build()).thenReturn(mockWebClient);

        // Create a concrete subclass with all custom parameters
        BaseDifyDefaultClient client = new BaseDifyDefaultClient(customBaseUrl, clientConfig,
                mockRestClientBuilder, mockWebClientBuilder) {
            // No need to implement any methods
        };

        // Verify that the client was created successfully
        assertNotNull(client);
        assertNotNull(client.responseErrorHandler);
        assertEquals(mockRestClient, client.restClient);
        assertEquals(mockWebClient, client.webClient);

        // Verify interactions with mocks
        verify(mockRestClientBuilder).baseUrl(customBaseUrl);
        verify(mockRestClientBuilder).defaultHeaders(any(Consumer.class));
        verify(mockRestClientBuilder).build();

        verify(mockWebClientBuilder).baseUrl(customBaseUrl);
        verify(mockWebClientBuilder).defaultHeaders(any(Consumer.class));
        verify(mockWebClientBuilder).build();
    }

    @Test
    @DisplayName("Test DifyResponseErrorHandler hasError method with error response")
    public void testResponseErrorHandlerHasErrorWithErrorResponse() throws IOException {
        // Create a mock ClientHttpResponse
        ClientHttpResponse mockResponse = Mockito.mock(ClientHttpResponse.class);
        when(mockResponse.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);

        // Create a concrete subclass to access the DifyResponseErrorHandler
        BaseDifyDefaultClient client = new BaseDifyDefaultClient() {
            // No need to implement any methods
        };

        // Check if hasError returns true for an error response
        assertTrue(client.responseErrorHandler.hasError(mockResponse));
    }

    @Test
    @DisplayName("Test DifyResponseErrorHandler hasError method with success response")
    public void testResponseErrorHandlerHasErrorWithSuccessResponse() throws IOException {
        // Create a mock ClientHttpResponse
        ClientHttpResponse mockResponse = Mockito.mock(ClientHttpResponse.class);
        when(mockResponse.getStatusCode()).thenReturn(HttpStatus.OK);

        // Create a concrete subclass to access the DifyResponseErrorHandler
        BaseDifyDefaultClient client = new BaseDifyDefaultClient() {
            // No need to implement any methods
        };

        // Check if hasError returns false for a success response
        assertFalse(client.responseErrorHandler.hasError(mockResponse));
    }

    @Test
    @DisplayName("Test DifyResponseErrorHandler handleError method with error response")
    public void testResponseErrorHandlerHandleErrorWithErrorResponse() throws IOException {
        // Create a mock ClientHttpResponse
        ClientHttpResponse mockResponse = Mockito.mock(ClientHttpResponse.class);
        when(mockResponse.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(mockResponse.getStatusText()).thenReturn("Bad Request");
        when(mockResponse.getBody()).thenReturn(new ByteArrayInputStream("Error message".getBytes()));

        // Create a concrete subclass to access the DifyResponseErrorHandler
        BaseDifyDefaultClient client = new BaseDifyDefaultClient() {
            // No need to implement any methods
        };

        // Test that handleError throws a RuntimeException for an error response
        URI uri = URI.create("https://api.dify.ai/endpoint");
        HttpMethod method = HttpMethod.POST;
        Exception exception = assertThrows(RuntimeException.class, () ->
                client.responseErrorHandler.handleError(uri, method, mockResponse)
        );

        // Verify that the exception message contains the status code, text, and error message
        String expectedMessageContent = "[400] Bad Request - Error message";
        assertTrue(exception.getMessage().contains(expectedMessageContent));
    }
}
