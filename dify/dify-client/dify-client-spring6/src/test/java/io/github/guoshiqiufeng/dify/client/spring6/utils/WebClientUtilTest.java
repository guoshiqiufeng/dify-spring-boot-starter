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
package io.github.guoshiqiufeng.dify.client.spring6.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.net.URISyntaxException;

import static org.mockito.Mockito.when;

/**
 * Tests for {@link WebClientUtil}.
 *
 * @author yanghq
 * @version 0.9.0
 * @since 2025/4/21 11:55
 */
public class WebClientUtilTest {

    @Test
    @DisplayName("Test exceptionFunction with 400 Bad Request")
    public void testExceptionFunctionWithBadRequest() throws URISyntaxException {
        // Create a mock ClientResponse and HttpRequest
        ClientResponse clientResponse = Mockito.mock(ClientResponse.class);
        HttpRequest httpRequest = Mockito.mock(HttpRequest.class);

        // Configure mocks
        when(clientResponse.statusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(clientResponse.bodyToMono(String.class)).thenReturn(Mono.just("Bad Request Error"));
        when(clientResponse.request()).thenReturn(httpRequest);
        when(httpRequest.getURI()).thenReturn(new URI("http://test.com/api/test"));
        when(httpRequest.getMethod()).thenReturn(HttpMethod.GET);

        // Call the exceptionFunction
        Mono<? extends Throwable> result = WebClientUtil.exceptionFunction(clientResponse);

        // Verify the result - the exceptionFunction returns Mono.error() which triggers onError
        StepVerifier.create(result.flatMap(Mono::error))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().contains("400") &&
                                throwable.getMessage().contains("Bad Request Error"))
                .verify();
    }

    @Test
    @DisplayName("Test exceptionFunction with 401")
    public void testExceptionFunctionWithUnauthorized() throws URISyntaxException {
        // Create a mock ClientResponse and HttpRequest
        ClientResponse clientResponse = Mockito.mock(ClientResponse.class);
        HttpRequest httpRequest = Mockito.mock(HttpRequest.class);

        // Configure mocks
        when(clientResponse.statusCode()).thenReturn(HttpStatus.UNAUTHORIZED);
        when(clientResponse.bodyToMono(String.class)).thenReturn(Mono.just("unauthorized"));
        when(clientResponse.request()).thenReturn(httpRequest);
        when(httpRequest.getURI()).thenReturn(new URI("http://test.com/api/test"));
        when(httpRequest.getMethod()).thenReturn(HttpMethod.GET);

        // Call the exceptionFunction
        Mono<? extends Throwable> result = WebClientUtil.exceptionFunction(clientResponse);

        // Verify the result - the exceptionFunction returns Mono.error() which triggers onError
        StepVerifier.create(result.flatMap(Mono::error))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().contains("Access token is invalid"))
                .verify();
    }

    @Test
    @DisplayName("Test exceptionFunction with 404 Not Found")
    public void testExceptionFunctionWithNotFound() throws URISyntaxException {
        // Create a mock ClientResponse and HttpRequest
        ClientResponse clientResponse = Mockito.mock(ClientResponse.class);
        HttpRequest httpRequest = Mockito.mock(HttpRequest.class);

        // Configure mocks
        when(clientResponse.statusCode()).thenReturn(HttpStatus.NOT_FOUND);
        when(clientResponse.bodyToMono(String.class)).thenReturn(Mono.just("Resource Not Found"));
        when(clientResponse.request()).thenReturn(httpRequest);
        when(httpRequest.getURI()).thenReturn(new URI("http://test.com/api/test"));
        when(httpRequest.getMethod()).thenReturn(HttpMethod.GET);

        // Call the exceptionFunction
        Mono<? extends Throwable> result = WebClientUtil.exceptionFunction(clientResponse);

        // Verify the result - the exceptionFunction returns Mono.error() which triggers onError
        StepVerifier.create(result.flatMap(Mono::error))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().contains("Not Found"))
                .verify();
    }

    @Test
    @DisplayName("Test exceptionFunction with 500 Internal Server Error")
    public void testExceptionFunctionWithInternalServerError() throws URISyntaxException {
        // Create a mock ClientResponse and HttpRequest
        ClientResponse clientResponse = Mockito.mock(ClientResponse.class);
        HttpRequest httpRequest = Mockito.mock(HttpRequest.class);

        // Configure mocks
        when(clientResponse.statusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
        when(clientResponse.bodyToMono(String.class)).thenReturn(Mono.just("Internal Server Error"));
        when(clientResponse.request()).thenReturn(httpRequest);
        when(httpRequest.getURI()).thenReturn(new URI("http://test.com/api/test"));
        when(httpRequest.getMethod()).thenReturn(HttpMethod.GET);

        // Call the exceptionFunction
        Mono<? extends Throwable> result = WebClientUtil.exceptionFunction(clientResponse);

        // Verify the result - the exceptionFunction returns Mono.error() which triggers onError
        StepVerifier.create(result.flatMap(Mono::error))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().contains("500") &&
                                throwable.getMessage().contains("Internal Server Error"))
                .verify();
    }

    @Test
    @DisplayName("Test exceptionFunction with empty error body")
    public void testExceptionFunctionWithEmptyErrorBody() throws URISyntaxException {
        // Create a mock ClientResponse and HttpRequest
        ClientResponse clientResponse = Mockito.mock(ClientResponse.class);
        HttpRequest httpRequest = Mockito.mock(HttpRequest.class);

        // Configure mocks
        when(clientResponse.statusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(clientResponse.bodyToMono(String.class)).thenReturn(Mono.just(""));
        when(clientResponse.request()).thenReturn(httpRequest);
        when(httpRequest.getURI()).thenReturn(new URI("http://test.com/api/test"));
        when(httpRequest.getMethod()).thenReturn(HttpMethod.GET);

        // Call the exceptionFunction
        Mono<? extends Throwable> result = WebClientUtil.exceptionFunction(clientResponse);

        // Verify the result - the exceptionFunction returns Mono.error() which triggers onError
        StepVerifier.create(result.flatMap(Mono::error))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().contains("400"))
                .verify();
    }
}
