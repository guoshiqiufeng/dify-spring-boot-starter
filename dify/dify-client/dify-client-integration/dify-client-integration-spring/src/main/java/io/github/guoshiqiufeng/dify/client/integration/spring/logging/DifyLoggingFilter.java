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

import io.github.guoshiqiufeng.dify.client.integration.spring.http.util.HttpHeaderConverter;
import io.github.guoshiqiufeng.dify.client.integration.spring.util.ClientResponseUtils;
import io.github.guoshiqiufeng.dify.core.utils.LogMaskingUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.MimeType;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A logging filter for Dify client requests and responses.
 * This filter logs the request and response details including headers, body, and timing.
 *
 * @author yanghq
 * @version 0.11.0
 * @since 2025/4/29 15:30
 */
@Slf4j
public class DifyLoggingFilter implements ExchangeFilterFunction {

    private static final ConcurrentMap<String, Long> REQUEST_TIME_CACHE = new ConcurrentHashMap<>();

    private final boolean maskingEnabled;
    private final int logBodyMaxBytes;
    private final boolean logBinaryBody;

    /**
     * Constructor with default masking enabled
     */
    public DifyLoggingFilter() {
        this(true, 10240, false); // Default 10KB, don't log binary body
    }

    /**
     * Constructor with configurable masking
     *
     * @param maskingEnabled whether to enable log masking
     */
    public DifyLoggingFilter(boolean maskingEnabled) {
        this(maskingEnabled, 10240, false); // Default 10KB, don't log binary body
    }

    /**
     * Constructor with full configuration
     *
     * @param maskingEnabled  whether to enable log masking
     * @param logBodyMaxBytes maximum bytes to log for body (0 = unlimited)
     */
    public DifyLoggingFilter(boolean maskingEnabled, int logBodyMaxBytes) {
        this(maskingEnabled, logBodyMaxBytes, false);
    }

    /**
     * Constructor with full configuration including binary body logging
     *
     * @param maskingEnabled  whether to enable log masking
     * @param logBodyMaxBytes maximum bytes to log for body (0 = unlimited)
     * @param logBinaryBody   whether to log binary body content (default: false, only log metadata)
     */
    public DifyLoggingFilter(boolean maskingEnabled, int logBodyMaxBytes, boolean logBinaryBody) {
        this.maskingEnabled = maskingEnabled;
        this.logBodyMaxBytes = logBodyMaxBytes;
        this.logBinaryBody = logBinaryBody;
    }

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        String requestId = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();
        REQUEST_TIME_CACHE.put(requestId, startTime);

        // Log request
        logRequest(requestId, request);

        return next.exchange(request)
                .flatMap(response -> {
                    if (log.isDebugEnabled()) {
                        // Check if this is a streaming response (SSE)
                        String contentType = response.headers().contentType()
                                .map(MimeType::toString)
                                .orElse("");

                        if (contentType.contains("text/event-stream") || contentType.contains("stream")) {
                            // For streaming responses, just log headers without reading the body
                            logResponseHeadersOnly(requestId, response);
                            return Mono.just(response);
                        } else {
                            // For non-streaming responses, log with body
                            return logResponseWithBody(requestId, response);
                        }
                    } else {
                        REQUEST_TIME_CACHE.remove(requestId);
                        return Mono.just(response);
                    }
                })
                .doFinally(signalType -> {
                    // Ensure cache cleanup on error/cancel path
                    REQUEST_TIME_CACHE.remove(requestId);
                });
    }

    private void logRequest(String requestId, ClientRequest request) {
        if (log.isDebugEnabled()) {
            if (maskingEnabled) {
                // Convert HttpHeaders to Map for masking
                Map<String, List<String>> headersMap = new HashMap<>(HttpHeaderConverter.fromSpringHeaders(request.headers()));

                // Mask sensitive headers
                Map<String, List<String>> maskedHeaders = LogMaskingUtils.maskHeaders(headersMap);

                // Mask URL parameters using shared utility
                String maskedUrl = LogMaskingUtils.maskUrl(request.url().toString());

                log.debug("logRequest | requestId: {} | url: {} | method: {} | headers: {} | cookies: ***MASKED***",
                        requestId, maskedUrl, request.method(), maskedHeaders);
            } else {
                log.debug("logRequest | requestId: {} | url: {} | method: {} | headers: {} | cookies: {}",
                        requestId, request.url(), request.method(), request.headers(), request.cookies());
            }
        }
    }

    private void logResponseHeadersOnly(String requestId, ClientResponse response) {
        long executionTime = System.currentTimeMillis() - REQUEST_TIME_CACHE.getOrDefault(requestId, 0L);
        REQUEST_TIME_CACHE.remove(requestId);

        if (log.isDebugEnabled()) {
            if (maskingEnabled) {
                // Convert HttpHeaders to Map for masking
                Map<String, List<String>> headersMap = new HashMap<>(HttpHeaderConverter.fromSpringHeaders(response.headers().asHttpHeaders()));

                // Mask sensitive headers
                Map<String, List<String>> maskedHeaders = LogMaskingUtils.maskHeaders(headersMap);

                log.debug("logResponse (streaming) | requestId: {} | status: {} | headers: {} | executionTime: {}ms",
                        requestId, ClientResponseUtils.getStatusCodeValue(response), maskedHeaders, executionTime);
            } else {
                log.debug("logResponse (streaming) | requestId: {} | status: {} | headers: {} | executionTime: {}ms",
                        requestId, ClientResponseUtils.getStatusCodeValue(response), response.headers().asHttpHeaders(), executionTime);
            }
        }

    }

    private Mono<ClientResponse> logResponseWithBody(String requestId, ClientResponse response) {
        long executionTime = System.currentTimeMillis() - REQUEST_TIME_CACHE.getOrDefault(requestId, 0L);
        REQUEST_TIME_CACHE.remove(requestId);

        // Check if SSE response
        String contentType = response.headers().contentType()
                .map(MimeType::toString)
                .orElse("");

        if (contentType.contains("text/event-stream")) {
            if (log.isDebugEnabled()) {
                log.debug("【Dify】Response: {} {} (SSE stream, body logging skipped)",
                        ClientResponseUtils.getStatusCodeValue(response), requestId);
            }
            // Don't buffer SSE responses - return original response
            return Mono.just(response);
        }

        // Only buffer body if debug logging is enabled
        if (!log.isDebugEnabled()) {
            return Mono.just(response);
        }

        // Check content-length before buffering
        long contentLength = response.headers().contentLength().orElse(-1L);

        // Check if content type is binary
        boolean isBinary = isBinaryContentType(contentType);
        if (isBinary && !logBinaryBody) {
            log.debug("logResponse | requestId: {} | Binary content detected ({}), logging metadata only (logBinaryBody=false)",
                    requestId, contentType);
            logResponseHeadersOnly(requestId, response, executionTime);
            return Mono.just(response);
        }

        // Skip buffering if content-length is unknown to prevent OOM with chunked/streaming responses
        if (contentLength == -1) {
            log.debug("logResponse | requestId: {} | Response content-length unknown, skipping body logging for safety",
                    requestId);
            logResponseHeadersOnly(requestId, response, executionTime);
            return Mono.just(response);
        }

        if (logBodyMaxBytes > 0 && contentLength > logBodyMaxBytes) {
            log.debug("logResponse | requestId: {} | Response body too large ({}bytes > {}bytes), skipping body logging",
                    requestId, contentLength, logBodyMaxBytes);
            logResponseHeadersOnly(requestId, response, executionTime);
            return Mono.just(response);
        }

        // Safe to buffer - content-length is known and within limit
        return response.bodyToMono(String.class)
                .defaultIfEmpty("")
                .flatMap(body -> {
                    // Apply logBodyMaxBytes limit at byte level (not character level)
                    String truncatedBody = body;
                    if (logBodyMaxBytes > 0) {
                        byte[] bodyBytes = body.getBytes(java.nio.charset.StandardCharsets.UTF_8);
                        if (bodyBytes.length > logBodyMaxBytes) {
                            // Truncate at byte boundary
                            byte[] truncated = new byte[logBodyMaxBytes];
                            System.arraycopy(bodyBytes, 0, truncated, 0, logBodyMaxBytes);
                            truncatedBody = new String(truncated, java.nio.charset.StandardCharsets.UTF_8) + "... (truncated)";
                        }
                    }

                    if (maskingEnabled) {
                        // Convert HttpHeaders to Map for masking
                        Map<String, List<String>> headersMap = new HashMap<>(HttpHeaderConverter.fromSpringHeaders(response.headers().asHttpHeaders()));

                        // Mask sensitive headers
                        Map<String, List<String>> maskedHeaders = LogMaskingUtils.maskHeaders(headersMap);

                        // Mask sensitive body content
                        String maskedBody = LogMaskingUtils.maskBody(truncatedBody);

                        log.debug("logResponse | requestId: {} | status: {} | headers: {} | executionTime: {}ms | body: {}",
                                requestId, ClientResponseUtils.getStatusCodeValue(response), maskedHeaders, executionTime, maskedBody);
                    } else {
                        log.debug("logResponse | requestId: {} | status: {} | headers: {} | executionTime: {}ms | body: {}",
                                requestId, ClientResponseUtils.getStatusCodeValue(response), response.headers().asHttpHeaders(), executionTime, truncatedBody);
                    }

                    return Mono.just(ClientResponseUtils.createClientResponse(response, body));
                });
    }

    /**
     * Log response headers only (when body is too large or unavailable).
     */
    private void logResponseHeadersOnly(String requestId, ClientResponse response, long executionTime) {
        if (maskingEnabled) {
            Map<String, List<String>> headersMap = new HashMap<>(HttpHeaderConverter.fromSpringHeaders(
                    response.headers().asHttpHeaders()));
            Map<String, List<String>> maskedHeaders = LogMaskingUtils.maskHeaders(headersMap);
            log.debug("logResponse | requestId: {} | status: {} | headers: {} | executionTime: {}ms | body: <not logged>",
                    requestId, ClientResponseUtils.getStatusCodeValue(response), maskedHeaders, executionTime);
        } else {
            log.debug("logResponse | requestId: {} | status: {} | headers: {} | executionTime: {}ms | body: <not logged>",
                    requestId, ClientResponseUtils.getStatusCodeValue(response),
                    response.headers().asHttpHeaders(), executionTime);
        }
    }

    /**
     * Check if the content type is binary (non-text).
     * Returns true for images, videos, audio, application/octet-stream, etc.
     * Returns false for text/*, application/json, application/xml, etc.
     *
     * @param contentType the content type string
     * @return true if binary content
     */
    private boolean isBinaryContentType(String contentType) {
        if (contentType == null || contentType.isEmpty()) {
            return false; // Assume text if no content type
        }

        String lowerContentType = contentType.toLowerCase();

        // Text types (safe to log)
        if (lowerContentType.startsWith("text/") ||
            lowerContentType.contains("application/json") ||
            lowerContentType.contains("application/xml") ||
            lowerContentType.contains("application/x-www-form-urlencoded") ||
            lowerContentType.contains("application/javascript") ||
            lowerContentType.contains("application/xhtml+xml") ||
            lowerContentType.contains("+json") ||
            lowerContentType.contains("+xml")) {
            return false;
        }

        // Binary types (should not log body)
        if (lowerContentType.startsWith("image/") ||
            lowerContentType.startsWith("video/") ||
            lowerContentType.startsWith("audio/") ||
            lowerContentType.contains("application/octet-stream") ||
            lowerContentType.contains("application/pdf") ||
            lowerContentType.contains("application/zip") ||
            lowerContentType.contains("application/gzip") ||
            lowerContentType.contains("multipart/")) {
            return true;
        }

        // Default: assume binary for unknown application/* types
        if (lowerContentType.startsWith("application/")) {
            return true;
        }

        return false;
    }

}
