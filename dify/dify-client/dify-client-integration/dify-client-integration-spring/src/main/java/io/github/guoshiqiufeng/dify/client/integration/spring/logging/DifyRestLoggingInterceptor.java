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
    private final int logBodyMaxBytes;
    private final boolean logBinaryBody;

    /**
     * Constructor with default masking enabled
     */
    public DifyRestLoggingInterceptor() {
        this(true, 10240, false);
    }

    /**
     * Constructor with configurable masking
     *
     * @param maskingEnabled whether to enable log masking
     */
    public DifyRestLoggingInterceptor(boolean maskingEnabled) {
        this(maskingEnabled, 10240, false);
    }

    /**
     * Constructor with full configuration
     *
     * @param maskingEnabled  whether to enable log masking
     * @param logBodyMaxBytes maximum bytes to log for body (0 = unlimited)
     */
    public DifyRestLoggingInterceptor(boolean maskingEnabled, int logBodyMaxBytes) {
        this(maskingEnabled, logBodyMaxBytes, false);
    }

    /**
     * Constructor with full configuration including binary body logging
     *
     * @param maskingEnabled  whether to enable log masking
     * @param logBodyMaxBytes maximum bytes to log for body (0 = unlimited)
     * @param logBinaryBody   whether to log binary body content (default: false, only log metadata)
     */
    public DifyRestLoggingInterceptor(boolean maskingEnabled, int logBodyMaxBytes, boolean logBinaryBody) {
        this.maskingEnabled = maskingEnabled;
        this.logBodyMaxBytes = logBodyMaxBytes;
        this.logBinaryBody = logBinaryBody;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        String requestId = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();
        REQUEST_TIME_CACHE.put(requestId, startTime);

        try {
            // Log request
            logRequest(requestId, request, body);

            // Execute request
            ClientHttpResponse response = execution.execute(request, body);

            // Log response and get cached body (or null for SSE)
            byte[] cachedBody = logResponse(requestId, response);

            // If cachedBody is null (SSE case), return original response without wrapping
            if (cachedBody == null) {
                return response;
            }

            // Otherwise wrap with buffered proxy
            return createBufferedResponse(response, cachedBody);
        } catch (Exception e) {
            // Ensure cache cleanup on error path
            REQUEST_TIME_CACHE.remove(requestId);
            throw e;
        }
    }

    private void logRequest(String requestId, HttpRequest request, byte[] body) {
        if (log.isDebugEnabled()) {
            String bodyContent = "";
            if (body != null && body.length > 0) {
                // Apply logBodyMaxBytes limit at byte level (already in bytes)
                if (logBodyMaxBytes > 0 && body.length > logBodyMaxBytes) {
                    // Truncate at byte boundary
                    bodyContent = new String(body, 0, logBodyMaxBytes, StandardCharsets.UTF_8) + "... (truncated)";
                } else {
                    bodyContent = new String(body, StandardCharsets.UTF_8);
                }
            }

            if (maskingEnabled) {
                // Convert HttpHeaders to Map for masking
                Map<String, List<String>> headersMap = new HashMap<>();
                request.getHeaders().forEach(headersMap::put);

                // Mask sensitive headers
                Map<String, List<String>> maskedHeaders = LogMaskingUtils.maskHeaders(headersMap);

                // Mask sensitive body content
                String maskedBody = LogMaskingUtils.maskBody(bodyContent);

                // Mask sensitive URL parameters using shared utility
                String maskedUrl = LogMaskingUtils.maskUrl(request.getURI().toString());

                log.debug("logRequest | requestId: {} | url: {} | method: {} | headers: {} | body: {}",
                        requestId, maskedUrl, request.getMethod(), maskedHeaders, maskedBody);
            } else {
                log.debug("logRequest | requestId: {} | url: {} | method: {} | headers: {} | body: {}",
                        requestId, request.getURI(), request.getMethod(), request.getHeaders(), bodyContent);
            }
        }
    }

    private byte[] logResponse(String requestId, ClientHttpResponse response) throws IOException {
        // Always remove from cache to prevent memory leak
        Long startTime = REQUEST_TIME_CACHE.remove(requestId);

        // Check if SSE response first - before any body reading
        String contentType = response.getHeaders().getContentType() != null
                ? response.getHeaders().getContentType().toString()
                : "";
        if (contentType.contains("text/event-stream")) {
            if (log.isDebugEnabled()) {
                log.debug("logResponse | requestId: {} | SSE response detected, skipping body logging to preserve stream", requestId);
            }
            // Return null to signal "don't wrap, use original response"
            return null;
        }

        // Check if binary content type
        boolean isBinary = isBinaryContentType(contentType);
        if (isBinary && !logBinaryBody) {
            if (log.isDebugEnabled()) {
                log.debug("logResponse | requestId: {} | Binary content detected ({}), skipping body logging (logBinaryBody=false)",
                        requestId, contentType);
            }
            // Return null to signal "don't wrap, use original response"
            return null;
        }

        // If debug logging is disabled, don't buffer body - return null to use original response
        if (!log.isDebugEnabled()) {
            return null;
        }

        // Check content length and skip buffering if too large or unknown
        long contentLength = response.getHeaders().getContentLength();

        // Skip buffering if content-length is unknown
        if (contentLength == -1) {
            log.debug("logResponse | requestId: {} | Response content-length unknown, skipping body buffering for safety",
                    requestId);
            return null; // Use original response
        }

        if (logBodyMaxBytes > 0 && contentLength > logBodyMaxBytes) {
            log.debug("logResponse | requestId: {} | Response body too large ({}bytes > {}bytes), skipping body buffering",
                    requestId, contentLength, logBodyMaxBytes);
            // Don't buffer large responses - return null to use original response
            return null;
        }

        // Safe to buffer
        byte[] body = StreamUtils.copyToByteArray(response.getBody());

        if (startTime != null) {
            long executionTime = System.currentTimeMillis() - startTime;

            // Apply max bytes truncation at byte level (already in bytes)
            String bodyContent = "";
            if (body.length > 0) {
                if (logBodyMaxBytes > 0 && body.length > logBodyMaxBytes) {
                    // Truncate at byte boundary
                    bodyContent = new String(body, 0, logBodyMaxBytes, StandardCharsets.UTF_8) + "... (truncated)";
                } else {
                    bodyContent = new String(body, StandardCharsets.UTF_8);
                }
            }

            // Use reflection to get status code to support both Spring 5 (HttpStatus) and Spring 6+ (HttpStatusCode)
            Object statusCode = getStatusCodeSafely(response);

            if (maskingEnabled) {
                // Convert HttpHeaders to Map for masking
                Map<String, List<String>> headersMap = new HashMap<>();
                response.getHeaders().forEach(headersMap::put);

                // Mask sensitive headers
                Map<String, List<String>> maskedHeaders = LogMaskingUtils.maskHeaders(headersMap);

                // Mask sensitive body content
                String maskedBody = LogMaskingUtils.maskBody(bodyContent);

                log.debug("logResponse | requestId: {} | status: {} | headers: {} | executionTime: {}ms | body: {}",
                        requestId, statusCode, maskedHeaders, executionTime, maskedBody);
            } else {
                log.debug("logResponse | requestId: {} | status: {} | headers: {} | executionTime: {}ms | body: {}",
                        requestId, statusCode, response.getHeaders(), executionTime, bodyContent);
            }
        }

        return body;
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
