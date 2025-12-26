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
package io.github.guoshiqiufeng.dify.boot.application;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yanghq
 */
@Slf4j
public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    private final Map<String, Long> REQUEST_TIME_CACHE = new HashMap<>();

    @Override
    public @NotNull ClientHttpResponse intercept(@NotNull HttpRequest request, byte @NotNull [] body, ClientHttpRequestExecution execution) throws IOException {
        String randomId = IdUtil.fastUUID();
        logRequest(request, body, randomId);
        ClientHttpResponse response = execution.execute(request, body);
        logResponse(response, randomId);
        return response;
    }

    private void logRequest(HttpRequest request, byte[] body, String randomId) throws IOException {
        REQUEST_TIME_CACHE.put(randomId, System.currentTimeMillis());
        log.debug("【http】send request，requestID：{}，request url：{}，request method：{}，request headers：{}，request body：{}", randomId, request.getURI(), request.getMethod(), request.getHeaders(), new String(body, "UTF-8"));
    }

    private void logResponse(ClientHttpResponse response, String randomId) throws IOException {
        log.debug("【http】receive response，requestID：{}，response status：{}，response time：{}ms", randomId, response.getStatusCode(), System.currentTimeMillis() - REQUEST_TIME_CACHE.getOrDefault(randomId, 0L));
        REQUEST_TIME_CACHE.remove(randomId);
    }
}

