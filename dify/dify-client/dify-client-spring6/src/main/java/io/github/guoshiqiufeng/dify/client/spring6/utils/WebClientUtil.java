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
package io.github.guoshiqiufeng.dify.client.spring6.utils;

import io.github.guoshiqiufeng.dify.core.exception.DiftClientExceptionEnum;
import io.github.guoshiqiufeng.dify.core.exception.DifyClientException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/3/7 13:47
 */
@Slf4j
@UtilityClass
public class WebClientUtil {

    public Mono<? extends Throwable> exceptionFunction(ClientResponse clientResponse) {
        return clientResponse.bodyToMono(String.class)
                .flatMap(errorBody -> {
                    String url = clientResponse.request().getURI().getPath();
                    String method = clientResponse.request().getMethod().name();
                    int statusCode = clientResponse.statusCode().value();

                    log.warn("URI: {}, Method: {}, Status: [{}] {}", url, method, statusCode, errorBody);
                    return switch (statusCode) {
                        case 401 -> throw new DifyClientException(DiftClientExceptionEnum.UNAUTHORIZED);
                        case 404 -> throw new DifyClientException(DiftClientExceptionEnum.NOT_FOUND);
                        default -> Mono.error(new RuntimeException(String.format("[%d] %s", statusCode, errorBody)));
                    };
                });
    }

}
