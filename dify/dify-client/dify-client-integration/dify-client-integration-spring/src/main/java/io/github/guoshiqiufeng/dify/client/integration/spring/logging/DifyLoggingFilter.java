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
package io.github.guoshiqiufeng.dify.client.integration.spring.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
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
    private static final DataBufferFactory DATA_BUFFER_FACTORY = new DefaultDataBufferFactory();

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
                        return logResponseWithBody(requestId, response);
                    } else {
                        REQUEST_TIME_CACHE.remove(requestId);
                        return Mono.just(response);
                    }
                });
    }

    private void logRequest(String requestId, ClientRequest request) {
        if (log.isDebugEnabled()) {
            log.debug("logRequest，requestId：{}，url：{}，method：{}，headers：{}, cookies: {}",
                    requestId, request.url(), request.method(), request.headers(), request.cookies());
        }
    }

    private Mono<ClientResponse> logResponseWithBody(String requestId, ClientResponse response) {
        long executionTime = System.currentTimeMillis() - REQUEST_TIME_CACHE.getOrDefault(requestId, 0L);
        REQUEST_TIME_CACHE.remove(requestId);

        // Extract body as string
        return response.bodyToMono(String.class)
                .defaultIfEmpty("")
                .flatMap(body -> {
                    log.debug("logResponse，requestId：{}，status：{}，headers：{}，executionTime：{}ms，body：{}",
                            requestId, response.statusCode(), response.headers().asHttpHeaders(), executionTime, body);

                    // Recreate the response with the body we just read
                    ClientResponse newResponse = ClientResponse.create(response.statusCode())
                            .headers(headers -> headers.addAll(response.headers().asHttpHeaders()))
                            .cookies(cookies -> cookies.addAll(response.cookies()))
                            .body(Flux.just(body).map(s -> {
                                byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
                                DataBuffer buffer = DATA_BUFFER_FACTORY.allocateBuffer(bytes.length);
                                buffer.write(bytes);
                                return buffer;
                            }))
                            .build();

                    return Mono.just(newResponse);
                });
    }
}
