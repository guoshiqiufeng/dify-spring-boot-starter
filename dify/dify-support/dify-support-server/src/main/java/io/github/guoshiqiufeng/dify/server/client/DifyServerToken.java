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
package io.github.guoshiqiufeng.dify.server.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

/**
 * @author yanghq
 * @version 1.0
 * @since 2025/4/9 13:28
 */
@Slf4j
public abstract class DifyServerToken {

    /**
     * 最大重试次数
     */
    protected static final int MAX_RETRY_ATTEMPTS = 3;

    /**
     * @param headers
     */
    abstract void addAuthorizationHeader(HttpHeaders headers, DifyServerClient difyServerClient);

    <T> T executeWithRetry(RequestSupplier<T> supplier, DifyServerClient difyServerClient) {
        int retryCount = 0;
        while (retryCount < MAX_RETRY_ATTEMPTS) {
            try {
                return supplier.get();
            } catch (Exception e) {
                if (e.getMessage() != null && e.getMessage().contains("[401]") && retryCount < MAX_RETRY_ATTEMPTS - 1) {
                    log.warn("Token invalid, attempting to refresh token. Retry count: {}", retryCount + 1);
                    refreshOrObtainNewToken(difyServerClient);
                    retryCount++;
                } else {
                    throw e;
                }
            }
        }
        throw new RuntimeException("Max retry attempts reached");
    }

    abstract void refreshOrObtainNewToken(DifyServerClient difyServerClient);

}
