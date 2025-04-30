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
package io.github.guoshiqiufeng.dify.client.spring6.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A logging interceptor for RestClient requests and responses.
 * This interceptor logs the request and response details including headers, body, and timing.
 *
 * @author yanghq
 * @version 0.11.0
 * @since 2025/4/29 16:30
 */
@Slf4j
public class DifyRestLoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final ConcurrentMap<String, Long> REQUEST_TIME_CACHE = new ConcurrentHashMap<>();

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

        return new BufferedClientHttpResponse(response, cachedBody);
    }

    private void logRequest(String requestId, HttpRequest request, byte[] body) {
        if (log.isDebugEnabled()) {
            String bodyContent = body != null && body.length > 0 ? new String(body, StandardCharsets.UTF_8) : "";
            log.debug("logRequest，requestId：{}，url：{}，method：{}，headers：{}，body：{}",
                    requestId, request.getURI(), request.getMethod(), request.getHeaders(), bodyContent);
        }
    }

    private byte[] logResponse(String requestId, ClientHttpResponse response) throws IOException {
        byte[] body = StreamUtils.copyToByteArray(response.getBody());

        if (log.isDebugEnabled()) {
            long executionTime = System.currentTimeMillis() - REQUEST_TIME_CACHE.getOrDefault(requestId, 0L);
            REQUEST_TIME_CACHE.remove(requestId);

            String bodyContent = body.length > 0 ? new String(body, StandardCharsets.UTF_8) : "";
            log.debug("logResponse，requestId：{}，status：{}，headers：{}，执行时间：{}ms，body：{}",
                    requestId, response.getStatusCode(), response.getHeaders(), executionTime, bodyContent);
        }

        return body;
    }

    @SuppressWarnings("unchecked")
    static class BufferedClientHttpResponse implements ClientHttpResponse {

        private final ClientHttpResponse originalResponse;
        private final byte[] cachedBody;

        public BufferedClientHttpResponse(ClientHttpResponse originalResponse, byte[] cachedBody) {
            this.originalResponse = originalResponse;
            this.cachedBody = cachedBody;
        }

        @Override
        public InputStream getBody() {
            return new ByteArrayInputStream(cachedBody);
        }

        @Override
        public HttpStatusCode getStatusCode() throws IOException {
            return originalResponse.getStatusCode();
        }

        @Override
        public String getStatusText() throws IOException {
            return originalResponse.getStatusText();
        }

        @Override
        public void close() {
            originalResponse.close(); // 关闭原始响应资源
        }

        @Override
        public HttpHeaders getHeaders() {
            return originalResponse.getHeaders();
        }
    }
}
