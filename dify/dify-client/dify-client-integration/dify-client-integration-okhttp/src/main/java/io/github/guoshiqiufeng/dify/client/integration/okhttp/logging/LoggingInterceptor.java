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
package io.github.guoshiqiufeng.dify.client.integration.okhttp.logging;

import io.github.guoshiqiufeng.dify.core.utils.LogMaskingUtils;
import okhttp3.*;
import okio.Buffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * OkHttp interceptor for logging HTTP requests and responses.
 *
 * @author yanghq
 * @version 2.0.0
 * @since 2025-12-26
 */
public class LoggingInterceptor implements Interceptor {

    private static final Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);
    private static final Charset UTF8 = StandardCharsets.UTF_8;

    private final boolean maskingEnabled;
    private final int logBodyMaxBytes;
    private final boolean logBinaryBody;

    /**
     * Constructor with default masking enabled
     */
    public LoggingInterceptor() {
        this(true, 4096, false);
    }

    /**
     * Constructor with configurable masking
     *
     * @param maskingEnabled whether to enable log masking
     */
    public LoggingInterceptor(boolean maskingEnabled) {
        this(maskingEnabled, 4096, false);
    }

    /**
     * Constructor with full configuration
     *
     * @param maskingEnabled  whether to enable log masking
     * @param logBodyMaxBytes maximum bytes to log for body (0 = unlimited)
     * @param logBinaryBody   whether to log binary response bodies
     */
    public LoggingInterceptor(boolean maskingEnabled, int logBodyMaxBytes, boolean logBinaryBody) {
        this.maskingEnabled = maskingEnabled;
        this.logBodyMaxBytes = logBodyMaxBytes;
        this.logBinaryBody = logBinaryBody;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        // Generate unique request ID for tracing
        String requestId = UUID.randomUUID().toString();

        // Log request
        logRequest(request, requestId);

        // Execute request
        long startTime = System.currentTimeMillis();
        Response response = chain.proceed(request);
        long duration = System.currentTimeMillis() - startTime;

        // Log response and potentially recreate with new body
        Response newResponse = logResponse(response, duration, requestId);

        return newResponse != null ? newResponse : response;
    }

    /**
     * Log HTTP request details.
     *
     * @param request the HTTP request
     * @param requestId unique request ID for tracing
     */
    private void logRequest(Request request, String requestId) {
        if (!log.isDebugEnabled()) {
            return;
        }

        try {
            // Mask URL to prevent sensitive query parameters from being logged
            String url = request.url().toString();
            if (maskingEnabled) {
                url = LogMaskingUtils.maskUrl(url);
            }
            log.debug("【Dify】HTTP Request | requestId: {} | {} {}", requestId, request.method(), url);

            // Mask headers if enabled
            if (maskingEnabled) {
                Map<String, List<String>> headersMap = new HashMap<>();
                request.headers().toMultimap().forEach(headersMap::put);
                Map<String, List<String>> maskedHeaders = LogMaskingUtils.maskHeaders(headersMap);
                log.debug("【Dify】Request Headers | requestId: {} | {}", requestId, maskedHeaders);
            } else {
                log.debug("【Dify】Request Headers | requestId: {} | {}", requestId, request.headers());
            }

            RequestBody requestBody = request.body();
            if (requestBody != null) {
                // Check if request body is one-shot or duplex (non-repeatable)
                // These types of bodies cannot be read multiple times
                try {
                    // Try to check if body is one-shot (OkHttp 4.x+)
                    java.lang.reflect.Method isOneShotMethod = requestBody.getClass().getMethod("isOneShot");
                    Boolean isOneShot = (Boolean) isOneShotMethod.invoke(requestBody);
                    if (Boolean.TRUE.equals(isOneShot)) {
                        log.debug("【Dify】Request body | requestId: {} | one-shot body detected, skipping logging to preserve stream", requestId);
                        return;
                    }
                } catch (NoSuchMethodException e) {
                    // isOneShot() not available in older OkHttp versions, continue
                } catch (Exception e) {
                    log.debug("【Dify】Failed to check isOneShot: {}", e.getMessage());
                }

                try {
                    // Try to check if body is duplex (OkHttp 4.x+)
                    java.lang.reflect.Method isDuplexMethod = requestBody.getClass().getMethod("isDuplex");
                    Boolean isDuplex = (Boolean) isDuplexMethod.invoke(requestBody);
                    if (Boolean.TRUE.equals(isDuplex)) {
                        log.debug("【Dify】Request body | requestId: {} | duplex body detected, skipping logging to preserve stream", requestId);
                        return;
                    }
                } catch (NoSuchMethodException e) {
                    // isDuplex() not available in older OkHttp versions, continue
                } catch (Exception e) {
                    log.debug("【Dify】Failed to check isDuplex: {}", e.getMessage());
                }

                // Check content length before buffering
                long contentLength = requestBody.contentLength();

                // Skip buffering if content-length is unknown or too large
                if (contentLength == -1) {
                    log.debug("【Dify】Request body | requestId: {} | content-length unknown, skipping body logging for safety", requestId);
                } else if (logBodyMaxBytes > 0 && contentLength > logBodyMaxBytes) {
                    log.debug("【Dify】Request body | requestId: {} | too large ({}bytes > {}bytes), skipping body logging",
                            requestId, contentLength, logBodyMaxBytes);
                } else {
                    // Safe to buffer
                    Buffer buffer = new Buffer();
                    requestBody.writeTo(buffer);
                    Charset charset = UTF8;
                    MediaType contentType = requestBody.contentType();
                    if (contentType != null) {
                        Charset mediaCharset = contentType.charset(UTF8);
                        if (mediaCharset != null) {
                            charset = mediaCharset;
                        }
                    }

                    // Optimize: Check buffer size before reading to avoid redundant String->byte[] conversion
                    String logBody;
                    if (logBodyMaxBytes > 0 && buffer.size() > logBodyMaxBytes) {
                        // Truncate at byte boundary directly from buffer
                        byte[] truncated = new byte[logBodyMaxBytes];
                        buffer.read(truncated);
                        logBody = new String(truncated, charset) + "... (truncated)";
                    } else {
                        // Read full content
                        logBody = buffer.readString(charset);
                    }

                    // Mask body if enabled
                    if (maskingEnabled) {
                        String maskedBody = LogMaskingUtils.maskBody(logBody);
                        log.debug("【Dify】Request Body | requestId: {} | {}", requestId, maskedBody);
                    } else {
                        log.debug("【Dify】Request Body | requestId: {} | {}", requestId, logBody);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("【Dify】Failed to log request", e);
        }
    }

    /**
     * Log HTTP response details.
     *
     * @param response the HTTP response
     * @param duration request duration in milliseconds
     * @param requestId unique request ID for tracing
     * @return new response with recreated body if body was consumed, null otherwise
     */
    private Response logResponse(Response response, long duration, String requestId) {
        if (!log.isDebugEnabled()) {
            return null;
        }

        try {
            log.debug("【Dify】HTTP Response | requestId: {} | {} {} ({}ms)", requestId, response.code(), response.message(), duration);

            // Mask headers if enabled
            if (maskingEnabled) {
                Map<String, List<String>> headersMap = new HashMap<>();
                response.headers().toMultimap().forEach(headersMap::put);
                Map<String, List<String>> maskedHeaders = LogMaskingUtils.maskHeaders(headersMap);
                log.debug("【Dify】Response Headers | requestId: {} | {}", requestId, maskedHeaders);
            } else {
                log.debug("【Dify】Response Headers | requestId: {} | {}", requestId, response.headers());
            }

            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                MediaType contentType = responseBody.contentType();

                // Check if SSE response - skip body logging to avoid blocking stream
                if (isSseResponse(contentType)) {
                    log.debug("【Dify】SSE response detected, skipping body logging to avoid blocking stream");
                    return null;
                }

                // Check content length and skip if unknown or too large
                long contentLength = responseBody.contentLength();

                // Skip buffering if content-length is unknown
                if (contentLength == -1) {
                    log.debug("【Dify】Response content-length unknown, skipping body logging for safety");
                    return null;
                }

                if (logBodyMaxBytes > 0 && contentLength > logBodyMaxBytes) {
                    log.debug("【Dify】Response body too large ({}bytes > {}bytes), skipping body logging",
                            contentLength, logBodyMaxBytes);
                    return null;
                }

                // Handle binary content
                if (contentType != null && !isTextContentType(contentType)) {
                    if (!logBinaryBody) {
                        log.debug("【Dify】Binary response detected ({}), skipping body logging", contentType);
                        return null;
                    }

                    // Check size before loading binary content into memory
                    if (contentLength > 0 && logBodyMaxBytes > 0 && contentLength > logBodyMaxBytes) {
                        String binaryInfo = String.format(
                            "Binary content: %d bytes (exceeds limit %d bytes), Content-Type: %s",
                            contentLength, logBodyMaxBytes, contentType);
                        log.debug("【Dify】Response Body (Binary): {}", binaryInfo);
                        // Return original response without consuming body
                        return null;
                    }

                    // Safe to load - either small or no limit
                    byte[] bodyBytes = responseBody.bytes();
                    String binaryInfo = String.format("Binary content: %d bytes, Content-Type: %s",
                        bodyBytes.length, contentType);
                    log.debug("【Dify】Response Body (Binary): {}", binaryInfo);

                    // Recreate response body for downstream consumption
                    ResponseBody newBody = ResponseBody.create(bodyBytes, contentType);
                    return response.newBuilder().body(newBody).build();
                }

                // Handle text content
                if (contentType != null && isTextContentType(contentType)) {
                    String bodyString = responseBody.string();

                    // Optimize: Check string byte length before conversion to avoid redundant allocation
                    String logBody;
                    Charset charset = contentType.charset(UTF8);
                    if (charset == null) {
                        charset = UTF8;
                    }

                    if (logBodyMaxBytes > 0) {
                        byte[] bodyBytes = bodyString.getBytes(charset);
                        if (bodyBytes.length > logBodyMaxBytes) {
                            // Truncate at byte boundary
                            byte[] truncated = new byte[logBodyMaxBytes];
                            System.arraycopy(bodyBytes, 0, truncated, 0, logBodyMaxBytes);
                            logBody = new String(truncated, charset) + "... (truncated)";
                        } else {
                            logBody = bodyString;
                        }
                    } else {
                        logBody = bodyString;
                    }

                    // Mask body if enabled
                    if (maskingEnabled) {
                        String maskedBody = LogMaskingUtils.maskBody(logBody);
                        log.debug("【Dify】Response Body | requestId: {} | {}", requestId, maskedBody);
                    } else {
                        log.debug("【Dify】Response Body | requestId: {} | {}", requestId, logBody);
                    }

                    // Recreate response body for downstream consumption (use original body, not masked)
                    ResponseBody newBody = ResponseBody.create(bodyString, contentType);
                    return response.newBuilder().body(newBody).build();
                }
            }
        } catch (Exception e) {
            log.warn("【Dify】Failed to log response", e);
        }
        return null;
    }

    /**
     * Check if response is Server-Sent Events (SSE).
     *
     * @param contentType the media type
     * @return true if SSE response
     */
    private boolean isSseResponse(MediaType contentType) {
        if (contentType == null) {
            return false;
        }
        // Check for text/event-stream
        return "text".equals(contentType.type()) && "event-stream".equals(contentType.subtype());
    }

    /**
     * Check if content type is text-based.
     *
     * @param contentType the media type
     * @return true if text-based
     */
    private boolean isTextContentType(MediaType contentType) {
        String type = contentType.type();
        String subtype = contentType.subtype();
        return "text".equals(type) ||
                "json".equals(subtype) ||
                "xml".equals(subtype) ||
                "html".equals(subtype) ||
                "x-www-form-urlencoded".equals(subtype);
    }
}
