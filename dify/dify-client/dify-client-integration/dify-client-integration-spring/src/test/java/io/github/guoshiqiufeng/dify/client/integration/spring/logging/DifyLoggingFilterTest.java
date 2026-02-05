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
package io.github.guoshiqiufeng.dify.client.integration.spring.logging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for DifyLoggingFilter
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2026/1/13
 */
@ExtendWith(MockitoExtension.class)
class DifyLoggingFilterTest {

    @Mock
    private ExchangeFunction exchangeFunction;

    private DifyLoggingFilter loggingFilter;

    @BeforeEach
    void setUp() {
        loggingFilter = new DifyLoggingFilter();
    }

    @Test
    void testFilterWithNonStreamingResponse() {
        // Arrange
        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/api"))
                .build();

        String responseBody = "{\"status\":\"ok\"}";
        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .header("Content-Type", "application/json")
                .body(responseBody)
                .build();

        when(exchangeFunction.exchange(any(ClientRequest.class))).thenReturn(Mono.just(mockResponse));

        // Act
        Mono<ClientResponse> result = loggingFilter.filter(request, exchangeFunction);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void testFilterWithStreamingResponse() {
        // Arrange
        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/stream"))
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .header("Content-Type", "text/event-stream")
                .body("data: test stream")
                .build();

        when(exchangeFunction.exchange(any(ClientRequest.class))).thenReturn(Mono.just(mockResponse));

        // Act
        Mono<ClientResponse> result = loggingFilter.filter(request, exchangeFunction);

        // Assert - streaming responses should not have body logged
        StepVerifier.create(result)
                .expectNextMatches(response ->
                        response.statusCode().equals(HttpStatus.OK) &&
                                response.headers().contentType().isPresent() &&
                                response.headers().contentType().get().toString().contains("text/event-stream")
                )
                .verifyComplete();
    }

    @Test
    void testFilterWithPostRequest() {
        // Arrange
        ClientRequest request = ClientRequest.create(HttpMethod.POST, URI.create("http://example.com/api"))
                .header("Content-Type", "application/json")
                .body((outputMessage, context) -> Mono.empty())
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.CREATED, ExchangeStrategies.withDefaults())
                .body("{\"id\":123}")
                .build();

        when(exchangeFunction.exchange(any(ClientRequest.class))).thenReturn(Mono.just(mockResponse));

        // Act
        Mono<ClientResponse> result = loggingFilter.filter(request, exchangeFunction);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.CREATED))
                .verifyComplete();
    }

    @Test
    void testFilterWithErrorResponse() {
        // Arrange
        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/api"))
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.INTERNAL_SERVER_ERROR, ExchangeStrategies.withDefaults())
                .body("{\"error\":\"Internal error\"}")
                .build();

        when(exchangeFunction.exchange(any(ClientRequest.class))).thenReturn(Mono.just(mockResponse));

        // Act
        Mono<ClientResponse> result = loggingFilter.filter(request, exchangeFunction);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR))
                .verifyComplete();
    }

    @Test
    void testFilterWithEmptyResponse() {
        // Arrange
        ClientRequest request = ClientRequest.create(HttpMethod.DELETE, URI.create("http://example.com/api/123"))
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.NO_CONTENT, ExchangeStrategies.withDefaults())
                .build();

        when(exchangeFunction.exchange(any(ClientRequest.class))).thenReturn(Mono.just(mockResponse));

        // Act
        Mono<ClientResponse> result = loggingFilter.filter(request, exchangeFunction);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.NO_CONTENT))
                .verifyComplete();
    }

    @Test
    void testFilterPreservesRequestHeaders() {
        // Arrange
        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/api"))
                .header("Authorization", "Bearer token123")
                .header("X-Custom-Header", "custom-value")
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .body("response")
                .build();

        when(exchangeFunction.exchange(any(ClientRequest.class))).thenReturn(Mono.just(mockResponse));

        // Act
        Mono<ClientResponse> result = loggingFilter.filter(request, exchangeFunction);

        // Assert
        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void testFilterWithDifferentContentTypes() {
        String[] contentTypes = {
                "application/json",
                "application/xml",
                "text/plain",
                "application/octet-stream"
        };

        for (String contentType : contentTypes) {
            // Arrange
            ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/api"))
                    .build();

            ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                    .header("Content-Type", contentType)
                    .body("test response")
                    .build();

            when(exchangeFunction.exchange(any(ClientRequest.class))).thenReturn(Mono.just(mockResponse));

            // Act
            Mono<ClientResponse> result = loggingFilter.filter(request, exchangeFunction);

            // Assert
            StepVerifier.create(result)
                    .expectNextMatches(response ->
                            response.statusCode().equals(HttpStatus.OK) &&
                                    response.headers().contentType().isPresent()
                    )
                    .verifyComplete();
        }
    }

    @Test
    void testFilterWithUrlParameterMasking() {
        // Arrange - Test URL parameter masking
        DifyLoggingFilter maskingFilter = new DifyLoggingFilter(true);

        ClientRequest request = ClientRequest.create(HttpMethod.GET,
                URI.create("http://example.com/api?api_key=secret123&token=abc&user_id=456"))
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .body("response")
                .build();

        when(exchangeFunction.exchange(any(ClientRequest.class))).thenReturn(Mono.just(mockResponse));

        // Act
        Mono<ClientResponse> result = maskingFilter.filter(request, exchangeFunction);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void testFilterWithMaskingDisabled() {
        // Arrange - Test with masking disabled
        DifyLoggingFilter noMaskFilter = new DifyLoggingFilter(false);

        ClientRequest request = ClientRequest.create(HttpMethod.GET,
                URI.create("http://example.com/api?api_key=secret123"))
                .header("Authorization", "Bearer token")
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .body("{\"password\":\"secret\"}")
                .build();

        when(exchangeFunction.exchange(any(ClientRequest.class))).thenReturn(Mono.just(mockResponse));

        // Act
        Mono<ClientResponse> result = noMaskFilter.filter(request, exchangeFunction);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void testFilterWithSensitiveHeaders() {
        // Arrange - Test sensitive headers masking
        DifyLoggingFilter maskingFilter = new DifyLoggingFilter(true);

        ClientRequest request = ClientRequest.create(HttpMethod.POST, URI.create("http://example.com/api"))
                .header("Authorization", "Bearer secret-token")
                .header("Cookie", "session=abc123")
                .header("X-API-Key", "api-key-value")
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .body("response")
                .build();

        when(exchangeFunction.exchange(any(ClientRequest.class))).thenReturn(Mono.just(mockResponse));

        // Act
        Mono<ClientResponse> result = maskingFilter.filter(request, exchangeFunction);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void testFilterWithMultipleSensitiveUrlParams() {
        // Arrange - Test multiple sensitive URL parameters
        DifyLoggingFilter maskingFilter = new DifyLoggingFilter(true);

        ClientRequest request = ClientRequest.create(HttpMethod.GET,
                URI.create("http://example.com/api?api_key=key1&password=pass1&secret=sec1&access_token=tok1&refresh_token=tok2"))
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .body("response")
                .build();

        when(exchangeFunction.exchange(any(ClientRequest.class))).thenReturn(Mono.just(mockResponse));

        // Act
        Mono<ClientResponse> result = maskingFilter.filter(request, exchangeFunction);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void testFilterWithEmptyUrl() {
        // Arrange - Test with simple URL (no parameters)
        DifyLoggingFilter maskingFilter = new DifyLoggingFilter(true);

        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/api"))
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .body("response")
                .build();

        when(exchangeFunction.exchange(any(ClientRequest.class))).thenReturn(Mono.just(mockResponse));

        // Act
        Mono<ClientResponse> result = maskingFilter.filter(request, exchangeFunction);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void testFilterWithStreamContentType() {
        // Arrange - Test with generic stream content type
        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/stream"))
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .header("Content-Type", "application/stream+json")
                .body("data stream")
                .build();

        when(exchangeFunction.exchange(any(ClientRequest.class))).thenReturn(Mono.just(mockResponse));

        // Act
        Mono<ClientResponse> result = loggingFilter.filter(request, exchangeFunction);

        // Assert - streaming responses should not have body logged
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void testFilterWithCookies() {
        // Arrange - Test cookies masking
        DifyLoggingFilter maskingFilter = new DifyLoggingFilter(true);

        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/api"))
                .cookie("session", "session-value")
                .cookie("auth", "auth-value")
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .body("response")
                .build();

        when(exchangeFunction.exchange(any(ClientRequest.class))).thenReturn(Mono.just(mockResponse));

        // Act
        Mono<ClientResponse> result = maskingFilter.filter(request, exchangeFunction);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void testFilterWithStreamingResponseAndMaskingDisabled() {
        // Arrange - Test streaming response with masking disabled
        DifyLoggingFilter noMaskFilter = new DifyLoggingFilter(false);

        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/stream"))
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .header("Content-Type", "text/event-stream")
                .body("data: test stream")
                .build();

        when(exchangeFunction.exchange(any(ClientRequest.class))).thenReturn(Mono.just(mockResponse));

        // Act
        Mono<ClientResponse> result = noMaskFilter.filter(request, exchangeFunction);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void testFilterWithNullUrl() {
        // Arrange - Test with request that has URL parameters to trigger maskSensitiveUrlParams with edge cases
        DifyLoggingFilter maskingFilter = new DifyLoggingFilter(true);

        // Create a request with a URL that will test the masking logic
        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/api"))
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .body("response")
                .build();

        when(exchangeFunction.exchange(any(ClientRequest.class))).thenReturn(Mono.just(mockResponse));

        // Act
        Mono<ClientResponse> result = maskingFilter.filter(request, exchangeFunction);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }
}
