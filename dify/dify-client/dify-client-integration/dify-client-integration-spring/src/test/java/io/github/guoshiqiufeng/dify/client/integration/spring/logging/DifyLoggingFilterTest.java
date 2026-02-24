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

    // ========== Branch coverage tests for content-length and binary content ==========

    @Test
    void testFilterWithBinaryContentAndLogBinaryBodyEnabled() {
        // Arrange - Test binary content with logBinaryBody=true
        DifyLoggingFilter binaryFilter = new DifyLoggingFilter(true, 10240, true);

        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/api"))
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .header("Content-Type", "application/octet-stream")
                .header("Content-Length", "1024")
                .body("binary data placeholder")
                .build();

        when(exchangeFunction.exchange(any(ClientRequest.class))).thenReturn(Mono.just(mockResponse));

        // Act
        Mono<ClientResponse> result = binaryFilter.filter(request, exchangeFunction);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void testFilterWithBinaryContentAndLogBinaryBodyDisabled() {
        // Arrange - Test binary content with logBinaryBody=false (default)
        DifyLoggingFilter binaryFilter = new DifyLoggingFilter(true, 10240, false);

        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/api"))
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .header("Content-Type", "application/octet-stream")
                .header("Content-Length", "2048")
                .body("binary data placeholder")
                .build();

        when(exchangeFunction.exchange(any(ClientRequest.class))).thenReturn(Mono.just(mockResponse));

        // Act
        Mono<ClientResponse> result = binaryFilter.filter(request, exchangeFunction);

        // Assert - should skip body logging for binary content
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void testFilterWithUnknownContentLength() {
        // Arrange - Test with unknown content-length (-1)
        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/api"))
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .header("Content-Type", "application/json")
                // No Content-Length header = -1
                .body("{\"data\":\"test\"}")
                .build();

        when(exchangeFunction.exchange(any(ClientRequest.class))).thenReturn(Mono.just(mockResponse));

        // Act
        Mono<ClientResponse> result = loggingFilter.filter(request, exchangeFunction);

        // Assert - should handle unknown content-length
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void testFilterWithContentLengthExceedsLimit() {
        // Arrange - Test with content-length > logBodyMaxBytes
        DifyLoggingFilter limitedFilter = new DifyLoggingFilter(true, 100); // 100 bytes limit

        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/api"))
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .header("Content-Type", "application/json")
                .header("Content-Length", "10240") // 10KB > 100 bytes
                .body("{\"data\":\"large response body\"}")
                .build();

        when(exchangeFunction.exchange(any(ClientRequest.class))).thenReturn(Mono.just(mockResponse));

        // Act
        Mono<ClientResponse> result = limitedFilter.filter(request, exchangeFunction);

        // Assert - should skip body logging when exceeds limit
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void testFilterWithContentLengthWithinLimit() {
        // Arrange - Test with content-length <= logBodyMaxBytes
        DifyLoggingFilter limitedFilter = new DifyLoggingFilter(true, 10240); // 10KB limit

        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/api"))
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .header("Content-Type", "application/json")
                .header("Content-Length", "50")
                .body("{\"data\":\"small response\"}")
                .build();

        when(exchangeFunction.exchange(any(ClientRequest.class))).thenReturn(Mono.just(mockResponse));

        // Act
        Mono<ClientResponse> result = limitedFilter.filter(request, exchangeFunction);

        // Assert - should log body when within limit
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void testFilterWithUnlimitedBodyLogging() {
        // Arrange - Test with logBodyMaxBytes=0 (unlimited)
        DifyLoggingFilter unlimitedFilter = new DifyLoggingFilter(true, 0);

        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/api"))
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .header("Content-Type", "application/json")
                .header("Content-Length", "100000") // Large content
                .body("{\"data\":\"very large response body\"}")
                .build();

        when(exchangeFunction.exchange(any(ClientRequest.class))).thenReturn(Mono.just(mockResponse));

        // Act
        Mono<ClientResponse> result = unlimitedFilter.filter(request, exchangeFunction);

        // Assert - should log body regardless of size
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void testFilterWithExchangeError() {
        // Arrange - Test error/cancel path (doFinally cleanup)
        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/api"))
                .build();

        when(exchangeFunction.exchange(any(ClientRequest.class)))
                .thenReturn(Mono.error(new RuntimeException("Network error")));

        // Act
        Mono<ClientResponse> result = loggingFilter.filter(request, exchangeFunction);

        // Assert - should handle error and cleanup
        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void testFilterWithImageContentType() {
        // Arrange - Test with image content type (binary)
        DifyLoggingFilter binaryFilter = new DifyLoggingFilter(true, 10240, false);

        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/image.png"))
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .header("Content-Type", "image/png")
                .header("Content-Length", "5000")
                .body("image binary data")
                .build();

        when(exchangeFunction.exchange(any(ClientRequest.class))).thenReturn(Mono.just(mockResponse));

        // Act
        Mono<ClientResponse> result = binaryFilter.filter(request, exchangeFunction);

        // Assert - should skip binary body logging
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void testFilterWithVideoContentType() {
        // Arrange - Test with video content type (binary)
        DifyLoggingFilter binaryFilter = new DifyLoggingFilter(true, 10240, true);

        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/video.mp4"))
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .header("Content-Type", "video/mp4")
                .header("Content-Length", "8000")
                .body("video binary data")
                .build();

        when(exchangeFunction.exchange(any(ClientRequest.class))).thenReturn(Mono.just(mockResponse));

        // Act
        Mono<ClientResponse> result = binaryFilter.filter(request, exchangeFunction);

        // Assert - should log binary body when enabled
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    // ========== Tests for log.isDebugEnabled()=false branch ==========

    @Test
    void testFilterWithDebugDisabled() {
        // Arrange - Temporarily disable DEBUG logging to test the else branch
        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger)
            org.slf4j.LoggerFactory.getLogger(DifyLoggingFilter.class);
        ch.qos.logback.classic.Level originalLevel = logger.getLevel();

        try {
            // Set to INFO to disable debug
            logger.setLevel(ch.qos.logback.classic.Level.INFO);

            ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/api"))
                    .build();

            ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                    .body("{\"data\":\"test\"}")
                    .build();

            when(exchangeFunction.exchange(any(ClientRequest.class))).thenReturn(Mono.just(mockResponse));

            // Act
            Mono<ClientResponse> result = loggingFilter.filter(request, exchangeFunction);

            // Assert - should skip logging when debug is disabled
            StepVerifier.create(result)
                    .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                    .verifyComplete();
        } finally {
            // Restore original level
            logger.setLevel(originalLevel);
        }
    }

    @Test
    void testFilterWithDebugDisabledAndMaskingEnabled() {
        // Arrange - Test debug disabled with masking enabled
        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger)
            org.slf4j.LoggerFactory.getLogger(DifyLoggingFilter.class);
        ch.qos.logback.classic.Level originalLevel = logger.getLevel();

        try {
            logger.setLevel(ch.qos.logback.classic.Level.INFO);

            DifyLoggingFilter maskingFilter = new DifyLoggingFilter(true);

            ClientRequest request = ClientRequest.create(HttpMethod.GET,
                    URI.create("http://example.com/api?api_key=secret"))
                    .header("Authorization", "Bearer token")
                    .build();

            ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                    .body("{\"password\":\"secret\"}")
                    .build();

            when(exchangeFunction.exchange(any(ClientRequest.class))).thenReturn(Mono.just(mockResponse));

            // Act
            Mono<ClientResponse> result = maskingFilter.filter(request, exchangeFunction);

            // Assert - should skip all logging when debug is disabled
            StepVerifier.create(result)
                    .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                    .verifyComplete();
        } finally {
            logger.setLevel(originalLevel);
        }
    }

    @Test
    void testFilterWithDebugDisabledAndError() {
        // Arrange - Test debug disabled with error response
        ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger)
            org.slf4j.LoggerFactory.getLogger(DifyLoggingFilter.class);
        ch.qos.logback.classic.Level originalLevel = logger.getLevel();

        try {
            logger.setLevel(ch.qos.logback.classic.Level.INFO);

            ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/api"))
                    .build();

            when(exchangeFunction.exchange(any(ClientRequest.class)))
                    .thenReturn(Mono.error(new RuntimeException("Network error")));

            // Act
            Mono<ClientResponse> result = loggingFilter.filter(request, exchangeFunction);

            // Assert - should handle error and cleanup cache
            StepVerifier.create(result)
                    .expectError(RuntimeException.class)
                    .verify();
        } finally {
            logger.setLevel(originalLevel);
        }
    }

    // ========== Tests for additional content-type and size branches ==========

    @Test
    void testFilterWithApplicationStreamContentType() {
        // Arrange - Test with application/stream+json (contains "stream")
        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/stream"))
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .header("Content-Type", "application/stream+json")
                .body("stream data")
                .build();

        when(exchangeFunction.exchange(any(ClientRequest.class))).thenReturn(Mono.just(mockResponse));

        // Act
        Mono<ClientResponse> result = loggingFilter.filter(request, exchangeFunction);

        // Assert - should treat as streaming and skip body logging
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void testFilterWithOctetStreamContentType() {
        // Arrange - Test with application/octet-stream (binary)
        DifyLoggingFilter binaryFilter = new DifyLoggingFilter(true, 10240, false);

        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/file"))
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .header("Content-Type", "application/octet-stream")
                .header("Content-Length", "3000")
                .body("binary data")
                .build();

        when(exchangeFunction.exchange(any(ClientRequest.class))).thenReturn(Mono.just(mockResponse));

        // Act
        Mono<ClientResponse> result = binaryFilter.filter(request, exchangeFunction);

        // Assert - should skip binary body when logBinaryBody=false
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void testFilterWithPdfContentType() {
        // Arrange - Test with application/pdf (binary)
        DifyLoggingFilter binaryFilter = new DifyLoggingFilter(true, 10240, true);

        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/doc.pdf"))
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .header("Content-Type", "application/pdf")
                .header("Content-Length", "5000")
                .body("pdf binary data")
                .build();

        when(exchangeFunction.exchange(any(ClientRequest.class))).thenReturn(Mono.just(mockResponse));

        // Act
        Mono<ClientResponse> result = binaryFilter.filter(request, exchangeFunction);

        // Assert - should log binary body when logBinaryBody=true
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void testFilterWithZeroContentLength() {
        // Arrange - Test with content-length=0
        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/api"))
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .header("Content-Type", "application/json")
                .header("Content-Length", "0")
                .body("")
                .build();

        when(exchangeFunction.exchange(any(ClientRequest.class))).thenReturn(Mono.just(mockResponse));

        // Act
        Mono<ClientResponse> result = loggingFilter.filter(request, exchangeFunction);

        // Assert - should handle zero content-length
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void testFilterWithVeryLargeContentLength() {
        // Arrange - Test with very large content-length
        DifyLoggingFilter limitedFilter = new DifyLoggingFilter(true, 1024); // 1KB limit

        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/api"))
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .header("Content-Type", "application/json")
                .header("Content-Length", "1048576") // 1MB
                .body("{\"data\":\"large\"}")
                .build();

        when(exchangeFunction.exchange(any(ClientRequest.class))).thenReturn(Mono.just(mockResponse));

        // Act
        Mono<ClientResponse> result = limitedFilter.filter(request, exchangeFunction);

        // Assert - should skip body logging when exceeds limit
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void testFilterWithMaskingDisabledAndSensitiveData() {
        // Arrange - Test masking disabled with sensitive data
        DifyLoggingFilter noMaskFilter = new DifyLoggingFilter(false);

        ClientRequest request = ClientRequest.create(HttpMethod.POST,
                URI.create("http://example.com/api?password=secret123"))
                .header("Authorization", "Bearer token123")
                .header("Cookie", "session=abc")
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .body("{\"api_key\":\"key123\"}")
                .build();

        when(exchangeFunction.exchange(any(ClientRequest.class))).thenReturn(Mono.just(mockResponse));

        // Act
        Mono<ClientResponse> result = noMaskFilter.filter(request, exchangeFunction);

        // Assert - should log without masking
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    // ========== Tests for various content types (isBinaryContentType coverage) ==========

    @Test
    void testFilterWithTextPlainContentType() {
        // Arrange - Test text/plain (text/* should be logged)
        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/api"))
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .header("Content-Type", "text/plain")
                .body("plain text response")
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
    void testFilterWithTextHtmlContentType() {
        // Arrange - Test text/html
        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/api"))
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .header("Content-Type", "text/html; charset=utf-8")
                .body("<html><body>Hello</body></html>")
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
    void testFilterWithApplicationXmlContentType() {
        // Arrange - Test application/xml
        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/api"))
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .header("Content-Type", "application/xml")
                .body("<root><data>value</data></root>")
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
    void testFilterWithApplicationFormUrlencodedContentType() {
        // Arrange - Test application/x-www-form-urlencoded
        ClientRequest request = ClientRequest.create(HttpMethod.POST, URI.create("http://example.com/api"))
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body("key1=value1&key2=value2")
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
    void testFilterWithApplicationJavascriptContentType() {
        // Arrange - Test application/javascript
        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/api"))
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .header("Content-Type", "application/javascript")
                .body("function test() { return true; }")
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
    void testFilterWithApplicationXhtmlXmlContentType() {
        // Arrange - Test application/xhtml+xml
        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/api"))
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .header("Content-Type", "application/xhtml+xml")
                .body("<?xml version=\"1.0\"?><html></html>")
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
    void testFilterWithJsonSuffixContentType() {
        // Arrange - Test +json suffix (e.g., application/vnd.api+json)
        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/api"))
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .header("Content-Type", "application/vnd.api+json")
                .body("{\"data\":\"value\"}")
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
    void testFilterWithXmlSuffixContentType() {
        // Arrange - Test +xml suffix (e.g., application/atom+xml)
        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/api"))
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .header("Content-Type", "application/atom+xml")
                .body("<?xml version=\"1.0\"?><feed></feed>")
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
    void testFilterWithAudioContentType() {
        // Arrange - Test audio/* (binary)
        DifyLoggingFilter binaryFilter = new DifyLoggingFilter(true, 10240, false);

        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/api"))
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .header("Content-Type", "audio/mpeg")
                .body("binary audio data")
                .build();

        when(exchangeFunction.exchange(any(ClientRequest.class))).thenReturn(Mono.just(mockResponse));

        // Act
        Mono<ClientResponse> result = binaryFilter.filter(request, exchangeFunction);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void testFilterWithApplicationZipContentType() {
        // Arrange - Test application/zip (binary)
        DifyLoggingFilter binaryFilter = new DifyLoggingFilter(true, 10240, false);

        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/api"))
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .header("Content-Type", "application/zip")
                .body("binary zip data")
                .build();

        when(exchangeFunction.exchange(any(ClientRequest.class))).thenReturn(Mono.just(mockResponse));

        // Act
        Mono<ClientResponse> result = binaryFilter.filter(request, exchangeFunction);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void testFilterWithApplicationGzipContentType() {
        // Arrange - Test application/gzip (binary)
        DifyLoggingFilter binaryFilter = new DifyLoggingFilter(true, 10240, false);

        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/api"))
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .header("Content-Type", "application/gzip")
                .body("binary gzip data")
                .build();

        when(exchangeFunction.exchange(any(ClientRequest.class))).thenReturn(Mono.just(mockResponse));

        // Act
        Mono<ClientResponse> result = binaryFilter.filter(request, exchangeFunction);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void testFilterWithMultipartContentType() {
        // Arrange - Test multipart/* (binary)
        DifyLoggingFilter binaryFilter = new DifyLoggingFilter(true, 10240, false);

        ClientRequest request = ClientRequest.create(HttpMethod.POST, URI.create("http://example.com/api"))
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .header("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundary")
                .body("multipart data")
                .build();

        when(exchangeFunction.exchange(any(ClientRequest.class))).thenReturn(Mono.just(mockResponse));

        // Act
        Mono<ClientResponse> result = binaryFilter.filter(request, exchangeFunction);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void testFilterWithUnknownApplicationContentType() {
        // Arrange - Test unknown application/* type (should default to binary)
        DifyLoggingFilter binaryFilter = new DifyLoggingFilter(true, 10240, false);

        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/api"))
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .header("Content-Type", "application/unknown-type")
                .body("unknown data")
                .build();

        when(exchangeFunction.exchange(any(ClientRequest.class))).thenReturn(Mono.just(mockResponse));

        // Act
        Mono<ClientResponse> result = binaryFilter.filter(request, exchangeFunction);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void testFilterWithNullContentType() {
        // Arrange - Test null content-type (should assume text)
        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/api"))
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                // No Content-Type header
                .body("response without content type")
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
    void testFilterWithEmptyContentType() {
        // Arrange - Test empty content-type (should assume text)
        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://example.com/api"))
                .build();

        ClientResponse mockResponse = ClientResponse.create(HttpStatus.OK, ExchangeStrategies.withDefaults())
                .header("Content-Type", "")
                .body("response with empty content type")
                .build();

        when(exchangeFunction.exchange(any(ClientRequest.class))).thenReturn(Mono.just(mockResponse));

        // Act
        Mono<ClientResponse> result = loggingFilter.filter(request, exchangeFunction);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                .verifyComplete();
    }
}
