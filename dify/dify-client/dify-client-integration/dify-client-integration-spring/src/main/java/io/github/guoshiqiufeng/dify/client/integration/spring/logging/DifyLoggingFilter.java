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

import io.github.guoshiqiufeng.dify.client.integration.spring.util.ClientResponseUtils;
import io.github.guoshiqiufeng.dify.core.utils.LogMaskingUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import java.util.*;
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

    /**
     * Constructor with default masking enabled
     */
    public DifyLoggingFilter() {
        this(true);
    }

    /**
     * Constructor with configurable masking
     *
     * @param maskingEnabled whether to enable log masking
     */
    public DifyLoggingFilter(boolean maskingEnabled) {
        this.maskingEnabled = maskingEnabled;
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
                                .map(mediaType -> mediaType.toString())
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
                });
    }

    private void logRequest(String requestId, ClientRequest request) {
        if (log.isDebugEnabled()) {
            if (maskingEnabled && !request.headers().getClass().getName().contains("ReadOnlyHttpHeaders")) {
                // Convert HttpHeaders to Map for masking
                Map<String, List<String>> headersMap = new HashMap<>(request.headers());

                // Mask sensitive headers
                Map<String, List<String>> maskedHeaders = LogMaskingUtils.maskHeaders(headersMap);

                log.debug("logRequest，requestId：{}，url：{}，method：{}，headers：{}, cookies: {}",
                        requestId, request.url(), request.method(), maskedHeaders, request.cookies());
            } else {
                log.debug("logRequest，requestId：{}，url：{}，method：{}，headers：{}, cookies: {}",
                        requestId, request.url(), request.method(), request.headers(), request.cookies());
            }
        }
    }

    private void logResponseHeadersOnly(String requestId, ClientResponse response) {
        long executionTime = System.currentTimeMillis() - REQUEST_TIME_CACHE.getOrDefault(requestId, 0L);
        REQUEST_TIME_CACHE.remove(requestId);

        if(log.isDebugEnabled()) {
            log.debug("logResponse (streaming)，requestId：{}，status：{}，headers：{}，executionTime：{}ms",
                    requestId, ClientResponseUtils.getStatusCodeValue(response), response.headers().asHttpHeaders(), executionTime);
        }

    }

    private Mono<ClientResponse> logResponseWithBody(String requestId, ClientResponse response) {
        long executionTime = System.currentTimeMillis() - REQUEST_TIME_CACHE.getOrDefault(requestId, 0L);
        REQUEST_TIME_CACHE.remove(requestId);

        // Extract body as string
        return response.bodyToMono(String.class)
                .defaultIfEmpty("")
                .flatMap(body -> {
                    if (maskingEnabled) {
                        // Convert HttpHeaders to Map for masking
                        Map<String, List<String>> headersMap = new HashMap<>(response.headers().asHttpHeaders());

                        // Mask sensitive headers
                        Map<String, List<String>> maskedHeaders = LogMaskingUtils.maskHeaders(headersMap);

                        // Mask sensitive body content
                        String maskedBody = LogMaskingUtils.maskBody(body);

                        log.debug("logResponse，requestId：{}，status：{}，headers：{}，executionTime：{}ms，body：{}",
                                requestId, ClientResponseUtils.getStatusCodeValue(response), maskedHeaders, executionTime, maskedBody);
                    } else {
                        log.debug("logResponse，requestId：{}，status：{}，headers：{}，executionTime：{}ms，body：{}",
                                requestId, ClientResponseUtils.getStatusCodeValue(response), response.headers().asHttpHeaders(), executionTime, body);
                    }

                    return Mono.just(ClientResponseUtils.createClientResponse(response, body));
                });
    }


}
