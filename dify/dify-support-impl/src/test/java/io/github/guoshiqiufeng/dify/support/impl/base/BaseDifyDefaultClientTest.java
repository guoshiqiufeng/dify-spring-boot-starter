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
package io.github.guoshiqiufeng.dify.support.impl.base;

import io.github.guoshiqiufeng.dify.client.core.http.HttpClientFactory;
import io.github.guoshiqiufeng.dify.client.core.http.ResponseErrorHandler;
import io.github.guoshiqiufeng.dify.client.core.response.HttpResponse;
import io.github.guoshiqiufeng.dify.client.core.web.client.HttpClient;
import io.github.guoshiqiufeng.dify.core.config.DifyProperties;
import io.github.guoshiqiufeng.dify.core.exception.DifyClientException;
import lombok.experimental.UtilityClass;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for BaseDifyDefaultClient
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/01/13
 */
@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class BaseDifyDefaultClientTest {

    @Test
    void testConstructorWithHttpClient() {
        // Arrange
        HttpClient mockHttpClient = mock(HttpClient.class);

        // Act
        BaseDifyDefaultClient client = new BaseDifyDefaultClient(mockHttpClient);

        // Assert
        assertNotNull(client);
        assertNotNull(client.httpClient);
        assertNotNull(client.responseErrorHandler);
    }

    @Test
    void testConstructorWithHttpClientAndErrorHandler() {
        // Arrange
        HttpClient mockHttpClient = mock(HttpClient.class);
        ResponseErrorHandler mockErrorHandler = mock(ResponseErrorHandler.class);

        // Act
        BaseDifyDefaultClient client = new BaseDifyDefaultClient(mockHttpClient, mockErrorHandler);

        // Assert
        assertNotNull(client);
        assertNotNull(client.httpClient);
        assertNotNull(client.responseErrorHandler);
        assertEquals(mockErrorHandler, client.responseErrorHandler);
    }

    @Test
    void testConstructorWithHttpClientFactory() {
        // Arrange
        String baseUrl = "https://api.dify.ai";
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        HttpClientFactory mockFactory = mock(HttpClientFactory.class);
        HttpClient mockHttpClient = mock(HttpClient.class);

        when(mockFactory.createClient(anyString(), any(DifyProperties.ClientConfig.class)))
                .thenReturn(mockHttpClient);

        // Act
        BaseDifyDefaultClient client = new BaseDifyDefaultClient(baseUrl, clientConfig, mockFactory);

        // Assert
        assertNotNull(client);
        assertNotNull(client.httpClient);
        assertNotNull(client.responseErrorHandler);
    }

    @Test
    void testConstructorWithAllParameters() {
        // Arrange
        String baseUrl = "https://api.dify.ai";
        DifyProperties.ClientConfig clientConfig = new DifyProperties.ClientConfig();
        HttpClientFactory mockFactory = mock(HttpClientFactory.class);
        HttpClient mockHttpClient = mock(HttpClient.class);
        ResponseErrorHandler mockErrorHandler = mock(ResponseErrorHandler.class);

        when(mockFactory.createClient(anyString(), any(DifyProperties.ClientConfig.class)))
                .thenReturn(mockHttpClient);

        // Act
        BaseDifyDefaultClient client = new BaseDifyDefaultClient(baseUrl, clientConfig, mockFactory, mockErrorHandler);

        // Assert
        assertNotNull(client);
        assertNotNull(client.httpClient);
        assertNotNull(client.responseErrorHandler);
        assertEquals(mockErrorHandler, client.responseErrorHandler);
    }

    @Test
    void testErrorHandlerStatusPredicate_Success() {
        // Arrange
        HttpClient mockHttpClient = mock(HttpClient.class);
        BaseDifyDefaultClient client = new BaseDifyDefaultClient(mockHttpClient);
        Predicate<Integer> statusPredicate = client.responseErrorHandler.getStatusPredicate();

        // Act & Assert
        assertFalse(statusPredicate.test(200)); // Success
        assertFalse(statusPredicate.test(201)); // Created
        assertFalse(statusPredicate.test(204)); // No Content
        assertFalse(statusPredicate.test(299)); // Last 2xx
    }

    @Test
    void testErrorHandlerStatusPredicate_ClientError() {
        // Arrange
        HttpClient mockHttpClient = mock(HttpClient.class);
        BaseDifyDefaultClient client = new BaseDifyDefaultClient(mockHttpClient);
        Predicate<Integer> statusPredicate = client.responseErrorHandler.getStatusPredicate();

        // Act & Assert
        assertTrue(statusPredicate.test(400)); // Bad Request
        assertTrue(statusPredicate.test(401)); // Unauthorized
        assertTrue(statusPredicate.test(404)); // Not Found
        assertTrue(statusPredicate.test(499)); // Last 4xx
    }

    @Test
    void testErrorHandlerStatusPredicate_ServerError() {
        // Arrange
        HttpClient mockHttpClient = mock(HttpClient.class);
        BaseDifyDefaultClient client = new BaseDifyDefaultClient(mockHttpClient);
        Predicate<Integer> statusPredicate = client.responseErrorHandler.getStatusPredicate();

        // Act & Assert
        assertTrue(statusPredicate.test(500)); // Internal Server Error
        assertTrue(statusPredicate.test(502)); // Bad Gateway
        assertTrue(statusPredicate.test(503)); // Service Unavailable
    }

    @Test
    void testErrorHandlerStatusPredicate_InformationalAndRedirect() {
        // Arrange
        HttpClient mockHttpClient = mock(HttpClient.class);
        BaseDifyDefaultClient client = new BaseDifyDefaultClient(mockHttpClient);
        Predicate<Integer> statusPredicate = client.responseErrorHandler.getStatusPredicate();

        // Act & Assert
        assertTrue(statusPredicate.test(100)); // Continue
        assertTrue(statusPredicate.test(199)); // Last 1xx
        assertTrue(statusPredicate.test(300)); // Multiple Choices
        assertTrue(statusPredicate.test(301)); // Moved Permanently
        assertTrue(statusPredicate.test(302)); // Found
    }

    @Test
    void testErrorHandler_401Unauthorized() {
        // Arrange
        HttpClient mockHttpClient = mock(HttpClient.class);
        BaseDifyDefaultClient client = new BaseDifyDefaultClient(mockHttpClient);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);

        when(mockResponse.getStatusCode()).thenReturn(401);
        when(mockResponse.getBody()).thenReturn("Unauthorized");

        // Act & Assert
        DifyClientException exception = assertThrows(DifyClientException.class, () -> {
            client.responseErrorHandler.handle(mockResponse);
        });

        assertNotNull(exception);
    }

    @Test
    void testErrorHandler_404NotFound() {
        // Arrange
        HttpClient mockHttpClient = mock(HttpClient.class);
        BaseDifyDefaultClient client = new BaseDifyDefaultClient(mockHttpClient);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);

        when(mockResponse.getStatusCode()).thenReturn(404);
        when(mockResponse.getBody()).thenReturn("Not Found");

        // Act & Assert
        DifyClientException exception = assertThrows(DifyClientException.class, () -> {
            client.responseErrorHandler.handle(mockResponse);
        });

        assertNotNull(exception);
    }

    @Test
    void testErrorHandler_GenericError() {
        // Arrange
        HttpClient mockHttpClient = mock(HttpClient.class);
        BaseDifyDefaultClient client = new BaseDifyDefaultClient(mockHttpClient);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);

        when(mockResponse.getStatusCode()).thenReturn(500);
        when(mockResponse.getBody()).thenReturn("Internal Server Error");

        // Act & Assert
        DifyClientException exception = assertThrows(DifyClientException.class, () -> {
            client.responseErrorHandler.handle(mockResponse);
        });

        assertNotNull(exception);
        assertEquals(500, exception.getCode());
    }

    @Test
    void testErrorHandler_WithNullBody() {
        // Arrange
        HttpClient mockHttpClient = mock(HttpClient.class);
        BaseDifyDefaultClient client = new BaseDifyDefaultClient(mockHttpClient);
        HttpResponse<?> mockResponse = mock(HttpResponse.class);

        when(mockResponse.getStatusCode()).thenReturn(500);
        when(mockResponse.getBody()).thenReturn(null);

        // Act & Assert
        DifyClientException exception = assertThrows(DifyClientException.class, () -> {
            client.responseErrorHandler.handle(mockResponse);
        });

        assertNotNull(exception);
        assertEquals(500, exception.getCode());
    }

    @Test
    void testErrorHandler_WithMessageBody() {
        // Arrange
        HttpClient mockHttpClient = mock(HttpClient.class);
        BaseDifyDefaultClient client = new BaseDifyDefaultClient(mockHttpClient);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);

        when(mockResponse.getStatusCode()).thenReturn(400);
        when(mockResponse.getBody()).thenReturn("Bad Request: Invalid parameters");

        // Act & Assert
        DifyClientException exception = assertThrows(DifyClientException.class, () -> {
            client.responseErrorHandler.handle(mockResponse);
        });

        assertNotNull(exception);
        assertEquals(400, exception.getCode());
    }

    @Test
    void testErrorHandler_403Forbidden() {
        // Arrange
        HttpClient mockHttpClient = mock(HttpClient.class);
        BaseDifyDefaultClient client = new BaseDifyDefaultClient(mockHttpClient);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);

        when(mockResponse.getStatusCode()).thenReturn(403);
        when(mockResponse.getBody()).thenReturn("Forbidden");

        // Act & Assert
        DifyClientException exception = assertThrows(DifyClientException.class, () -> {
            client.responseErrorHandler.handle(mockResponse);
        });

        assertNotNull(exception);
        assertEquals(403, exception.getCode());
        assertEquals("Forbidden", exception.getMsg());
    }

    @Test
    void testErrorHandler_429RateLimitExceeded() {
        // Arrange
        HttpClient mockHttpClient = mock(HttpClient.class);
        BaseDifyDefaultClient client = new BaseDifyDefaultClient(mockHttpClient);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);

        when(mockResponse.getStatusCode()).thenReturn(429);
        when(mockResponse.getBody()).thenReturn("Rate limit exceeded");

        // Act & Assert
        DifyClientException exception = assertThrows(DifyClientException.class, () -> {
            client.responseErrorHandler.handle(mockResponse);
        });

        assertNotNull(exception);
    }

    @Test
    void testErrorHandler_503ServiceUnavailable() {
        // Arrange
        HttpClient mockHttpClient = mock(HttpClient.class);
        BaseDifyDefaultClient client = new BaseDifyDefaultClient(mockHttpClient);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);

        when(mockResponse.getStatusCode()).thenReturn(503);
        when(mockResponse.getBody()).thenReturn("Service Unavailable");

        // Act & Assert
        DifyClientException exception = assertThrows(DifyClientException.class, () -> {
            client.responseErrorHandler.handle(mockResponse);
        });

        assertNotNull(exception);
    }
}
