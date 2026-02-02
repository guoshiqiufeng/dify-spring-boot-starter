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

import io.github.guoshiqiufeng.dify.core.utils.LogMaskingUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * A logging interceptor for RestClient requests and responses.
 * This interceptor logs the request and response details including headers, body, and timing.
 * Compatible with both Spring 5 and Spring 6+.
 *
 * @author yanghq
 * @version 0.11.0
 * @since 2025/4/29 16:30
 */
@Slf4j
public class DifyRestLoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final ConcurrentMap<String, Long> REQUEST_TIME_CACHE = new ConcurrentHashMap<>();

    private final boolean maskingEnabled;

    /**
     * Constructor with default masking enabled
     */
    public DifyRestLoggingInterceptor() {
        this(true);
    }

    /**
     * Constructor with configurable masking
     *
     * @param maskingEnabled whether to enable log masking
     */
    public DifyRestLoggingInterceptor(boolean maskingEnabled) {
        this.maskingEnabled = maskingEnabled;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        String requestId = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();
        REQUEST_TIME_CACHE.put(requestId, startTime);

        // Log request
        logRequest(requestId, request, body);

        // Execute request
        ClientHttpResponse response = execution.execute(request, body);

        // Log response
        byte[] cachedBody = logResponse(requestId, response);

        return createBufferedResponse(response, cachedBody);
    }

    private void logRequest(String requestId, HttpRequest request, byte[] body) {
        if (log.isDebugEnabled()) {
            String bodyContent = body != null && body.length > 0 ? new String(body, StandardCharsets.UTF_8) : "";

            if (maskingEnabled) {
                // Mask sensitive headers
                Map<String, List<String>> maskedHeaders = LogMaskingUtils.maskHeaders(
                    request.getHeaders().entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                );

                // Mask sensitive body content
                String maskedBody = LogMaskingUtils.maskBody(bodyContent);

                log.debug("logRequest，requestId：{}，url：{}，method：{}，headers：{}，body：{}",
                        requestId, request.getURI(), request.getMethod(), maskedHeaders, maskedBody);
            } else {
                log.debug("logRequest，requestId：{}，url：{}，method：{}，headers：{}，body：{}",
                        requestId, request.getURI(), request.getMethod(), request.getHeaders(), bodyContent);
            }
        }
    }

    private byte[] logResponse(String requestId, ClientHttpResponse response) throws IOException {
        byte[] body = StreamUtils.copyToByteArray(response.getBody());

        // Always remove from cache to prevent memory leak
        Long startTime = REQUEST_TIME_CACHE.remove(requestId);

        if (log.isDebugEnabled() && startTime != null) {
            long executionTime = System.currentTimeMillis() - startTime;

            String bodyContent = body.length > 0 ? new String(body, StandardCharsets.UTF_8) : "";
            // Use reflection to get status code to support both Spring 5 (HttpStatus) and Spring 6+ (HttpStatusCode)
            Object statusCode = getStatusCodeSafely(response);

            if (maskingEnabled) {
                // Mask sensitive headers
                Map<String, List<String>> maskedHeaders = LogMaskingUtils.maskHeaders(
                    response.getHeaders().entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                );

                // Mask sensitive body content
                String maskedBody = LogMaskingUtils.maskBody(bodyContent);

                log.debug("logResponse，requestId：{}，status：{}，headers：{}，executionTime：{}ms，body：{}",
                        requestId, statusCode, maskedHeaders, executionTime, maskedBody);
            } else {
                log.debug("logResponse，requestId：{}，status：{}，headers：{}，executionTime：{}ms，body：{}",
                        requestId, statusCode, response.getHeaders(), executionTime, bodyContent);
            }
        }

        return body;
    }

    /**
     * Safely get status code from response to support both Spring 5 and Spring 6+.
     * In Spring 5, getStatusCode() returns HttpStatus.
     * In Spring 6+, getStatusCode() returns HttpStatusCode.
     *
     * @param response the client http response
     * @return the status code as an object
     */
    private Object getStatusCodeSafely(ClientHttpResponse response) {
        try {
            Method method = ClientHttpResponse.class.getMethod("getStatusCode");
            return method.invoke(response);
        } catch (Exception e) {
            log.warn("Failed to get status code from response", e);
            return "UNKNOWN";
        }
    }

    /**
     * Create a buffered response using dynamic proxy to support both Spring 5 and Spring 6+.
     * This approach avoids compile-time dependencies on HttpStatusCode (Spring 6+) or HttpStatus (Spring 5).
     *
     * @param originalResponse the original response
     * @param cachedBody       the cached response body
     * @return a buffered response
     */
    private ClientHttpResponse createBufferedResponse(ClientHttpResponse originalResponse, byte[] cachedBody) {
        return (ClientHttpResponse) Proxy.newProxyInstance(
                ClientHttpResponse.class.getClassLoader(),
                new Class<?>[]{ClientHttpResponse.class},
                new BufferedResponseInvocationHandler(originalResponse, cachedBody)
        );
    }

    /**
     * InvocationHandler for buffered response that delegates all method calls to the original response,
     * except for getBody() which returns the cached body.
     */
    static class BufferedResponseInvocationHandler implements InvocationHandler {

        private final ClientHttpResponse originalResponse;
        private final byte[] cachedBody;

        public BufferedResponseInvocationHandler(ClientHttpResponse originalResponse, byte[] cachedBody) {
            this.originalResponse = originalResponse;
            this.cachedBody = cachedBody;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // Override getBody() to return cached body
            if ("getBody".equals(method.getName())) {
                return new ByteArrayInputStream(cachedBody);
            }

            // Delegate all other methods to the original response
            return method.invoke(originalResponse, args);
        }
    }
}
