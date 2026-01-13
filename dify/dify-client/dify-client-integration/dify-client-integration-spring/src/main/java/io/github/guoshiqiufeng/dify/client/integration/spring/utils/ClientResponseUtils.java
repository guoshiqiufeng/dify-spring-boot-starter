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
package io.github.guoshiqiufeng.dify.client.integration.spring.utils;

import lombok.experimental.UtilityClass;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import reactor.core.publisher.Flux;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

/**
 * @author yanghq
 * @version 1.0
 * @since 2026/1/12 09:48
 */
@UtilityClass
public class ClientResponseUtils {
    private static final DataBufferFactory DATA_BUFFER_FACTORY = new DefaultDataBufferFactory();

    public int getStatusCodeValue(ClientResponse clientResponse) {
        try {
            Method getStatusCodeMethod = ClientResponse.class.getMethod("statusCode");
            Object statusCode = getStatusCodeMethod.invoke(clientResponse);
            Method valueMethod = statusCode.getClass().getMethod("value");
            return (int) valueMethod.invoke(statusCode);
        } catch (Exception e) {
            return clientResponse.statusCode().value();
        }
    }

    public ClientResponse createClientResponse(ClientResponse response, String body) {
        try {
            // Try Spring 6+ approach: ClientResponse.create(HttpStatusCode, ExchangeStrategies)
            Method statusCodeMethod = ClientResponse.class.getMethod("statusCode");
            Object statusCode = statusCodeMethod.invoke(response);

            // Use HttpStatusCode interface for Spring 6+
            Class<?> httpStatusCodeClass = Class.forName("org.springframework.http.HttpStatusCode");
            Method createMethod = ClientResponse.class.getMethod("create", httpStatusCodeClass, ExchangeStrategies.class);
            ClientResponse.Builder builder = (ClientResponse.Builder) createMethod.invoke(null, statusCode, ExchangeStrategies.withDefaults());

            return builder
                    .headers(headers -> headers.addAll(response.headers().asHttpHeaders()))
                    .cookies(cookies -> cookies.addAll(response.cookies()))
                    .body(Flux.just(body).map(s -> {
                        byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
                        DataBuffer buffer = DATA_BUFFER_FACTORY.allocateBuffer(bytes.length);
                        buffer.write(bytes);
                        return buffer;
                    }))
                    .build();
        } catch (Exception e) {
            // Fallback for Spring 5: ClientResponse.create(HttpStatus)
            try {
                Method statusCodeMethod = ClientResponse.class.getMethod("statusCode");
                Object statusCode = statusCodeMethod.invoke(response);

                Method createMethod = ClientResponse.class.getMethod("create", statusCode.getClass());
                ClientResponse.Builder builder = (ClientResponse.Builder) createMethod.invoke(null, statusCode);

                return builder
                        .headers(headers -> headers.addAll(response.headers().asHttpHeaders()))
                        .cookies(cookies -> cookies.addAll(response.cookies()))
                        .body(Flux.just(body).map(s -> {
                            byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
                            DataBuffer buffer = DATA_BUFFER_FACTORY.allocateBuffer(bytes.length);
                            buffer.write(bytes);
                            return buffer;
                        }))
                        .build();
            } catch (Exception ex) {
                throw new RuntimeException("Failed to create ClientResponse", ex);
            }
        }
    }
}
