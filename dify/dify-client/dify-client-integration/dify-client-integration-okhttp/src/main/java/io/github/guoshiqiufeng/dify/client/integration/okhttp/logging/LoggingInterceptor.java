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

import okhttp3.*;
import okio.Buffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

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

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        // Log request
        logRequest(request);

        // Execute request
        long startTime = System.currentTimeMillis();
        Response response = chain.proceed(request);
        long duration = System.currentTimeMillis() - startTime;

        // Log response and potentially recreate with new body
        Response newResponse = logResponse(response, duration);

        return newResponse != null ? newResponse : response;
    }

    /**
     * Log HTTP request details.
     *
     * @param request the HTTP request
     */
    private void logRequest(Request request) {
        if (!log.isDebugEnabled()) {
            return;
        }

        try {
            log.debug("【Dify】HTTP Request: {} {}", request.method(), request.url());
            log.debug("【Dify】Request Headers: {}", request.headers());

            RequestBody requestBody = request.body();
            if (requestBody != null) {
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
                log.debug("【Dify】Request Body: {}", buffer.readString(charset));
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
     * @return new response with recreated body if body was consumed, null otherwise
     */
    private Response logResponse(Response response, long duration) {
        if (!log.isDebugEnabled()) {
            return null;
        }

        try {
            log.debug("【Dify】HTTP Response: {} {} ({}ms)", response.code(), response.message(), duration);
            log.debug("【Dify】Response Headers: {}", response.headers());

            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                MediaType contentType = responseBody.contentType();
                if (contentType != null && isTextContentType(contentType)) {
                    String bodyString = responseBody.string();
                    log.debug("【Dify】Response Body: {}", bodyString);

                    // Recreate response body for downstream consumption
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
